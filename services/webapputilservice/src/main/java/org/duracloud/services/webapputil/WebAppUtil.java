package org.duracloud.services.webapputil;

import java.net.URL;
import java.io.InputStream;

/**
 * This interface abstracts the ability to (un)deploy a webapp to/from an
 * application server.
 *
 * @author Andrew Woods
 *         Date: Dec 7, 2009
 */
public interface WebAppUtil {

    /**
     * This method deploys the arg war to a new application server under the
     * arg context.
     *
     * @param context
     * @param war
     * @return URL of running webapp
     */
    public URL deploy(String context, InputStream war);

    /**
     * This method undeploys the webapp currently running at the arg url and
     * destroys the application server that was hosting it.
     *
     * @param url of webapp to undeploy
     */
    public void unDeploy(URL url);
}
