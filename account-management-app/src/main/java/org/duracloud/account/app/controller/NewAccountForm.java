/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.UniqueSubdomainConstraint;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class NewAccountForm {
    @NotBlank(message = "You must specify an organization.")
    private String orgName;

    private String department;

    @NotBlank(message = "You must specify an account name.")
    private String acctName;

    @UniqueSubdomainConstraint
    @Length(min = 3, max = 25, message = "Subdomain must be between 3 and 25 characters.")
    private String subdomain;

    private List<StorageProviderType> storageProviders = new ArrayList<StorageProviderType>(0);

    @NotBlank(message = "You must specify a service plan.")
    private ServicePlan servicePlan = ServicePlan.PROFESSIONAL;

    private AccountType accountType;

    private Integer accountClusterId;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public List<StorageProviderType> getStorageProviders() {
        return storageProviders;
    }

    public void setStorageProviders(List<StorageProviderType> storageProviders) {
        this.storageProviders = storageProviders;
    }

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Integer getAccountClusterId() {
        return accountClusterId;
    }

    public void setAccountClusterId(Integer accountClusterId) {
        this.accountClusterId = accountClusterId;
    }

}
