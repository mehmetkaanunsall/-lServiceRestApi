/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.07.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.wot;

public class ApplicationList {

    private int id;
    private String statusJson;
    private String typeJson;
    private String currencyJson;
    private String langJson;
    private String parameters;
    private String quartzJobJson;
    private String branchShiftPayment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusJson() {
        return statusJson;
    }

    public void setStatusJson(String statusJson) {
        this.statusJson = statusJson;
    }

    public String getTypeJson() {
        return typeJson;
    }

    public void setTypeJson(String typeJson) {
        this.typeJson = typeJson;
    }

    public String getCurrencyJson() {
        return currencyJson;
    }

    public void setCurrencyJson(String currencyJson) {
        this.currencyJson = currencyJson;
    }

    public String getLangJson() {
        return langJson;
    }

    public void setLangJson(String langJson) {
        this.langJson = langJson;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getQuartzJobJson() {
        return quartzJobJson;
    }

    public void setQuartzJobJson(String quartzJobJson) {
        this.quartzJobJson = quartzJobJson;
    }

    public String getBranchShiftPayment() {
        return branchShiftPayment;
    }

    public void setBranchShiftPayment(String branchShiftPayment) {
        this.branchShiftPayment = branchShiftPayment;
    }

}
