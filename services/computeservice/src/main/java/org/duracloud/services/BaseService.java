package org.duracloud.services;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Bill Branan
 * Date: Oct 21, 2009
 */
public abstract class BaseService implements ComputeService {

    private String serviceId;

    private ServiceStatus serviceStatus;

    public void start() throws Exception {
        setServiceStatus(ServiceStatus.STARTED);
    }

    public void stop() throws Exception {
        setServiceStatus(ServiceStatus.STOPPED);
    }

    public Map<String, String> getServiceProps() {
        Map<String, String> props = new HashMap<String, String>();
        props.put("serviceId", getServiceId());
        return props;
    }

    public String describe() throws Exception {
        StringBuilder serviceDescription = new StringBuilder();
        serviceDescription.append("; Service ID: ");
        serviceDescription.append(serviceId);
        serviceDescription.append("; Service class: ");
        serviceDescription.append(getClass().getName());
        serviceDescription.append("; Service status: ");
        serviceDescription.append(serviceStatus);
        return serviceDescription.toString();
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}
