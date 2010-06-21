/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.webflow.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

public class AddContentItemFlowHandler
        extends AbstractFlowHandler {

    private static final String SUCCESS_OUTCOME = "success";

    private static final String CONTENT_ITEM = "contentItem";

    private static final String SPACE = "space";

    private static Logger log = LoggerFactory.getLogger(AddContentItemFlowHandler.class);

    private transient ContentStoreProvider contentStoreProvider;

    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    private ContentStore getContentStore() throws ContentStoreException {
        return contentStoreProvider.getContentStore();
    }

    private Space getSpace(String spaceId) throws Exception {
        Space space = new Space();
        org.duracloud.domain.Space cloudSpace =
                getContentStore().getSpace(spaceId, null, 0, null);
        SpaceUtil.populateSpace(space, cloudSpace);
        return space;
    }

    @Override
    public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
        MutableAttributeMap map = super.createExecutionInputMap(request);
        try {
            if (map == null) {
                map = new LocalAttributeMap();
            }

            NavigationUtils.setReturnTo(request, map);

            String spaceId = request.getParameter("spaceId");
            Space space = getSpace(spaceId);
            map.put(SPACE, space);
        } catch (Exception ex) {
            log.error("Error creating execution map", ex);
        }
        return map;
    }

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String returnTo = NavigationUtils.getReturnTo(outcome);
        Space space = (Space) outcome.getOutput().get(SPACE);
        ContentItem contentItem =
                (ContentItem) outcome.getOutput().get(CONTENT_ITEM);

        String outcomeUrl = null;

        if (outcome.getId().equals(SUCCESS_OUTCOME)) {
            
            outcomeUrl =
                    MessageFormat
                            .format("contextRelative:/content.htm?spaceId={0}&contentId={1}",
                                    space.getSpaceId(),
                                    urlEncode(contentItem.getContentId()));
            outcomeUrl =
                    MessageUtils
                            .appendRedirectMessage(outcomeUrl,
                                                   MessageUtils
                                                           .createMessage("Successfully added content."),
                                                   request);

        } else if (returnTo == null) {
            outcomeUrl =
                    MessageFormat
                            .format("contextRelative:/contents.htm?spaceId={0}",
                                    space.getSpaceId());
        } else {
            outcomeUrl = returnTo;
        }

        return outcomeUrl;
    }

    private Object urlEncode(String value) {
        try{
            return URLEncoder.encode(value,CharEncoding.UTF_8);
            
        }catch(UnsupportedEncodingException ex){
            throw new RuntimeException("this should never happen");
        }

    }

}
