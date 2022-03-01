/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asli.can
 */
package com.mepsan.marwiz.general.model.general;

public class BranchIntegration {

    private int id;
    private Branch branch;
    private int integrationtype;
    private String name;
    private String description;
    private String host1;
    private String host2;
    private String username1;
    private String username2;
    private int integrationtimeout;
    private String parameter1;
    private int timeout1;
    private int timeout2;
    private String password1;
    private String password2;
    private String parameter2;
    private String parameter3;
    private String parameter4;
    private String parameter5;

    public BranchIntegration() {
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost1() {
        return host1;
    }

    public void setHost1(String host1) {
        this.host1 = host1;
    }

    public String getHost2() {
        return host2;
    }

    public void setHost2(String host2) {
        this.host2 = host2;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public int getIntegrationtimeout() {
        return integrationtimeout;
    }

    public void setIntegrationtimeout(int integrationtimeout) {
        this.integrationtimeout = integrationtimeout;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public int getTimeout1() {
        return timeout1;
    }

    public void setTimeout1(int timeout1) {
        this.timeout1 = timeout1;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public int getTimeout2() {
        return timeout2;
    }

    public void setTimeout2(int timeout2) {
        this.timeout2 = timeout2;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameter4() {
        return parameter4;
    }

    public void setParameter4(String parameter4) {
        this.parameter4 = parameter4;
    }

    public String getParameter5() {
        return parameter5;
    }

    public void setParameter5(String parameter5) {
        this.parameter5 = parameter5;
    }

    public int getIntegrationtype() {
        return integrationtype;
    }

    public void setIntegrationtype(int integrationtype) {
        this.integrationtype = integrationtype;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
