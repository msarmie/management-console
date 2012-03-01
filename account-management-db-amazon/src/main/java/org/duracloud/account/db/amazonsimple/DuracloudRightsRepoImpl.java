/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.USER_ID_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class DuracloudRightsRepoImpl extends BaseDuracloudRepoImpl implements DuracloudRightsRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_RIGHTS";
    private static final int WILDCARD_ACCT_ID = 0;

    private final DomainConverter<AccountRights> converter;

    public DuracloudRightsRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudRightsRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                 String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudUserRepoImpl.class);

        this.converter = new DuracloudRightsConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public AccountRights findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(AccountRights item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

    /**
     * If the arg userId is for a root user, all accounts are returned.
     * @param userId of user
     * @return accountRights set
     */
    @Override
    public Set<AccountRights> findByUserId(int userId) {
        List<Item> items;
        try {
            items = findItemsByAttribute(USER_ID_ATT, String.valueOf(userId));
        } catch(DBNotFoundException e) {
            items = new ArrayList<Item>();
        }

        Set<AccountRights> rights = getAccountRightsFromItems(items);
        if (isRootRights(rights)) {
            Set<Role> rootRoles = Role.ROLE_ROOT.getRoleHierarchy();
            rights = getAllAccountRights(userId, rootRoles);
        }
        return rights;
    }

    private boolean isRootRights(Set<AccountRights> rights) {
        for (AccountRights r : rights) {
            if (r.getRoles().contains(Role.ROLE_ROOT)) {
                return true;
            }
        }
        return false;
    }

    private Set<AccountRights> getAllAccountRights(int userId,
                                                   Set<Role> rootRoles) {
        Set<AccountRights> rights = new HashSet<AccountRights>();

        List<Item> all;
        try {
            all = findAllItems();
        } catch(DBNotFoundException e) {
            all = new ArrayList<Item>();
        }

        Set<AccountRights> allRights = getAccountRightsFromItems(all);
        for (AccountRights r : allRights) {
            rights.add(new AccountRights(r.getId(),
                                         r.getAccountId(),
                                         userId,
                                         rootRoles,
                                         r.getCounter()));
        }
        return rights;
    }

    /**
     * All root users will be added to the result set.
     * @param accountId of account
     * @return accountRights set
     * @throws DBNotFoundException
     */
    @Override
    public Set<AccountRights> findByAccountId(int accountId) {
        Set<AccountRights> userAccountRights = doFindByAccountId(accountId);
        Set<AccountRights> rootAccountRights = getRootAccountRights(accountId);

        // Add user rights to root rights list
        for(AccountRights userRight : userAccountRights) {
            if(!inRootRights(userRight, rootAccountRights)) {
                rootAccountRights.add(userRight);
            }
        }

        return rootAccountRights;
    }

    /**
     * Root users will not be added to the result set.
     * @param accountId of account
     * @return accountRights set
     * @throws DBNotFoundException
     */
    @Override
    public Set<AccountRights> findByAccountIdSkipRoot(int accountId) {
        return doFindByAccountId(accountId);
    }

    private Set<AccountRights> doFindByAccountId(int accountId) {
        List<Item> items;
        try {
            items = findItemsByAttribute(ACCOUNT_ID_ATT,
                                         String.valueOf(accountId));
        } catch(DBNotFoundException e) {
            items = new ArrayList<Item>();
        }

        return getAccountRightsFromItems(items);
    }

    private boolean inRootRights(AccountRights userRight,
                                 Set<AccountRights> rootRights) {
        for(AccountRights rootRight : rootRights) {
            if(userRight.getUserId() == rootRight.getUserId() &&
               userRight.getAccountId() == rootRight.getAccountId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns AccountRights for each root user and sets the
     * rights.acctId to the arg accountId.
     * @param accountId
     * @return
     */
    private Set<AccountRights> getRootAccountRights(int accountId) {
        List<Item> items = null;
        try {
            items = findItemsByAttribute(ACCOUNT_ID_ATT, String.valueOf(
                WILDCARD_ACCT_ID));

        } catch (Exception e) {
            // do nothing.
        }

        Set<AccountRights> rootAccountRights = new HashSet<AccountRights>();
        if (null == items) {
            return rootAccountRights;
        }

        Set<AccountRights> rights = getAccountRightsFromItems(items);
        for (AccountRights r : rights) {
            if (r.getRoles().contains(Role.ROLE_ROOT)) {
                rootAccountRights.add(new AccountRights(r.getId(),
                                                        accountId,
                                                        r.getUserId(),
                                                        r.getRoles(),
                                                        r.getCounter()));
            } else {
                log.warn("Unexpected accountRights: " + r);
            }
        }

        return rootAccountRights;
    }

    /**
     * This method returns the accountRights for the arg accountId and userId.
     * If no such item is found, and the arg userId belongs to the root user,
     * then an accountRights for the root user is returned with the
     * rights.acctId set to the arg accountId.
     *
     * @param accountId of account
     * @param userId of user
     * @return
     * @throws DBNotFoundException
     */
    @Override
    public AccountRights findByAccountIdAndUserId(int accountId,
                                                  int userId) throws DBNotFoundException {
        Map attributes = new HashMap<String, String>();
        attributes.put(ACCOUNT_ID_ATT, String.valueOf(accountId));
        attributes.put(USER_ID_ATT, String.valueOf(userId));

        AccountRights rootRights = null;
        Item item = null;
        try {
            // find item.
            item = findItemByAttributes(attributes);

        } catch (DBNotFoundException e) {
            // item not found, does account exist?
            Set<AccountRights> rights = doFindByAccountId(accountId);
            if(rights.size() > 0) { // account exists
                for (AccountRights root : getRootAccountRights(accountId)) {
                    // was the target user a root user?
                    if (root.getUserId() == userId) {
                        rootRights = new AccountRights(root.getId(),
                                                       accountId,
                                                       root.getUserId(),
                                                       root.getRoles(),
                                                       root.getCounter());
                    }
                }
            }

            // item not found and not root.
            if (null == rootRights) {
                throw e;
            }
        }

        if (null != item) {
            List<Attribute> atts = item.getAttributes();
            return converter.fromAttributes(atts, idFromString(item.getName()));

        } else {
            return rootRights;
        }
    }

    /**
     * This method returns the accountRights for the arg accountId and userId.
     * If no such item is found, and the arg userId belongs to the root user,
     * then an accountRights for the root user is returned with the
     * rights.acctId set to the arg accountId.
     * If the user is root then those rights will be returned no matter what.
     *
     * @param accountId of account
     * @param userId of user
     * @return
     * @throws DBNotFoundException
     */
    @Override
    public AccountRights findAccountRightsForUser(int accountId,
                                                  int userId) throws DBNotFoundException {
        AccountRights accountRights = null;

        List<Item> items =
            findItemsByAttribute(USER_ID_ATT, String.valueOf(userId));

        Set<AccountRights> rights = getAccountRightsFromItems(items);
        for (AccountRights r : rights) {
            // Is user root?
            if (r.getRoles().contains(Role.ROLE_ROOT)) {
                accountRights = new AccountRights(r.getId(),
                                                  accountId,
                                                  r.getUserId(),
                                                  r.getRoles(),
                                                  r.getCounter());
                break;
            } else if(r.getAccountId() == accountId) {
                accountRights = r;
            }
        }

        return accountRights;
    }

    private Set<AccountRights> getAccountRightsFromItems(List<Item> items) {
        Set<AccountRights> set = new HashSet<AccountRights>();
        for(Item item : items) {
            List<Attribute> atts = item.getAttributes();
            set.add(converter.fromAttributes(atts,
                                             idFromString(item.getName())));
        }
        return set;
    }

}
