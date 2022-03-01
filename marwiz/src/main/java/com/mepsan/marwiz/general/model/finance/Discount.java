/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 13:42:15
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.system.Status;
import java.util.Date;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class Discount extends WotLogging {

    private int id;
    private Status status;

    private String name;
    private Date beginDate;
    private Date endDate;
    private String description;
    private boolean isAllCustomer;
    private boolean isInvoice;
    private boolean isAllBranch;
    private boolean isRetailCustomer;
    private int centercampaign_id;

    public Discount() {
        status = new Status();
    }

    public int getCentercampaign_id() {
        return centercampaign_id;
    }

    public void setCentercampaign_id(int centercampaign_id) {
        this.centercampaign_id = centercampaign_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsAllCustomer() {
        return isAllCustomer;
    }

    public void setIsAllCustomer(boolean isAllCustomer) {
        this.isAllCustomer = isAllCustomer;
    }

    public boolean isIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public boolean isIsAllBranch() {
        return isAllBranch;
    }

    public void setIsAllBranch(boolean isAllBranch) {
        this.isAllBranch = isAllBranch;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    public boolean isIsRetailCustomer() {
        return isRetailCustomer;
    }

    public void setIsRetailCustomer(boolean isRetailCustomer) {
        this.isRetailCustomer = isRetailCustomer;
    }

}
