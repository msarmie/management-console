/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class DuracloudInstanceConverter
    extends BaseDomainConverter
    implements DomainConverter<DuracloudInstance> {

    public DuracloudInstanceConverter() {
        log = LoggerFactory.getLogger(DuracloudInstanceConverter.class);
    }

    protected static final String IMAGE_ID_ATT = "IMAGE_ID";
    public static final String ACCOUNT_ID_ATT = "ACCOUNT_ID";
    protected static final String HOST_NAME_ATT = "HOST_NAME";
    protected static final String PROVIDER_INSTANCE_ID_ATT =
        "PROVIDER_INSTANCE_ID";
    protected static final String INSTANCE_INITIALIZED_ATT =
        "INSTANCE_INITIALIZED";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(DuracloudInstance instance) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(instance.getCounter() + 1);
        atts.add(new ReplaceableAttribute(
            IMAGE_ID_ATT,
            asString(instance.getImageId()),
            true));
        atts.add(new ReplaceableAttribute(
            ACCOUNT_ID_ATT,
            asString(instance.getAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            HOST_NAME_ATT,
            instance.getHostName(),
            true));
        atts.add(new ReplaceableAttribute(
            PROVIDER_INSTANCE_ID_ATT,
            instance.getProviderInstanceId(),
            true));
        atts.add(new ReplaceableAttribute(
            INSTANCE_INITIALIZED_ATT,
            String.valueOf(instance.isInitialized()),
            true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public DuracloudInstance fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        int imageId = -1;
        int accountId = -1;
        String hostName = null;
        String providerInstanceId = null;
        boolean initialized = false;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (IMAGE_ID_ATT.equals(name)) {
                imageId = idFromString(value, "Image", "Instance", id);

            } else if (ACCOUNT_ID_ATT.equals(name)) {
                accountId = idFromString(value, "Account", "Instance", id);

            } else if (HOST_NAME_ATT.equals(name)) {
                hostName = value;

            } else if (PROVIDER_INSTANCE_ID_ATT.equals(name)) {
                providerInstanceId = value;

            } else if (INSTANCE_INITIALIZED_ATT.equals(name)) {
                initialized = Boolean.valueOf(value);
                
            } else {
                StringBuilder msg = new StringBuilder("Unexpected name: ");
                msg.append(name);
                msg.append(" in domain: ");
                msg.append(getDomain());
                msg.append(" [with id]: ");
                msg.append(id);
                log.info(msg.toString());
            }
        }

        return new DuracloudInstance(id,
                                     imageId,
                                     accountId,
                                     hostName,
                                     providerInstanceId,
                                     initialized,
                                     counter);
    }

}
