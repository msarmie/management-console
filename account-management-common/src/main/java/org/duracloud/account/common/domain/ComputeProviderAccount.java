/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.duracloud.computeprovider.domain.ComputeProviderType;

/**
 * @author: Bill Branan
 * Date: 3/24/11
 */
public class ComputeProviderAccount extends ProviderAccount {

    /**
     * The type of compute provider - meaning the organization acting as the
     * provider of compute services.
     */
    private ComputeProviderType providerType;

    /**
     * The IP address to which the instance host for this compute account
     * will be attached
     */
    private String elasticIp;

    /**
     * The grouping of firewall paramters which will be applied to the
     * instance managed by this compute account
     */
    private String securityGroup;

    /**
     * The key pair which will be used to directly access the server instance
     * managed by this compute account
     */
    private String keypair;

    public ComputeProviderAccount(int id,
                                  ComputeProviderType providerType,
                                  String username,
                                  String password,
                                  String elasticIp,
                                  String securityGroup,
                                  String keypair) {
        this(id, providerType, username, password,
             elasticIp, securityGroup, keypair, 0);
    }

    public ComputeProviderAccount(int id,
                                  ComputeProviderType providerType,
                                  String username,
                                  String password,
                                  String elasticIp,
                                  String securityGroup,
                                  String keypair,
                                  int counter) {
        this.id = id;
        this.providerType = providerType;
        this.username = username;
        this.password = password;
        this.elasticIp = elasticIp;
        this.securityGroup = securityGroup;
        this.keypair = keypair;
        this.counter = counter;
    }

    public ComputeProviderType getProviderType() {
        return providerType;
    }

    public String getElasticIp() {
        return elasticIp;
    }

    public String getSecurityGroup() {
        return securityGroup;
    }

    public String getKeypair() {
        return keypair;
    }

    /*
     * Generated by IntelliJ
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComputeProviderAccount that = (ComputeProviderAccount) o;

        if (elasticIp != null ? !elasticIp.equals(that.elasticIp) :
            that.elasticIp != null) {
            return false;
        }
        if (keypair != null ? !keypair.equals(that.keypair) :
            that.keypair != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) :
            that.password != null) {
            return false;
        }
        if (providerType != null ? !providerType.equals(that.providerType) :
            that.providerType != null) {
            return false;
        }
        if (securityGroup != null ? !securityGroup.equals(that.securityGroup) :
            that.securityGroup != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) :
            that.username != null) {
            return false;
        }

        return true;
    }

    /*
     * Generated by IntelliJ
     */
    @Override
    public int hashCode() {
        int result = providerType != null ? providerType.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (elasticIp != null ? elasticIp.hashCode() : 0);
        result = 31 * result +
            (securityGroup != null ? securityGroup.hashCode() : 0);
        result = 31 * result + (keypair != null ? keypair.hashCode() : 0);
        return result;
    }
    
}
