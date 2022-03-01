/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author esra.cabuk
 */
public class Order extends WotLogging {

    private int id;
    private BranchSetting branchSetting;
    private Account account;
    private DocumentNumber dNumber;
    private String documentSerial;
    private String documentNumber;
    private Date orderDate;
    private Status status;
    private Type type;
    private int typeNo;
    private boolean isInvoice;
    private boolean isCheckInvoice;
    private String documentNo;
    private BigDecimal remainingQuantity;//ürün kalan miktarı

    private String jsonItems;

    public Order() {
        this.branchSetting = new BranchSetting();
        this.account = new Account();
        this.dNumber = new DocumentNumber();
        this.status = new Status();
        this.type = new Type();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public DocumentNumber getdNumber() {
        return dNumber;
    }

    public void setdNumber(DocumentNumber dNumber) {
        this.dNumber = dNumber;
    }

    public String getDocumentSerial() {
        return documentSerial;
    }

    public void setDocumentSerial(String documentSerial) {
        this.documentSerial = documentSerial;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(int typeNo) {
        this.typeNo = typeNo;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public boolean isIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    public boolean isIsCheckInvoice() {
        return isCheckInvoice;
    }

    public void setIsCheckInvoice(boolean isCheckInvoice) {
        this.isCheckInvoice = isCheckInvoice;
    }

    public String getJsonItems() {
        return jsonItems;
    }

    public void setJsonItems(String jsonItems) {
        this.jsonItems = jsonItems;
    }

    public String getDocumentNo() {
        if (documentSerial != null || documentNumber != null) {
            return documentSerial + "" + documentNumber;
        } else {
            return null;
        }
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    @Override
    public String toString() {
        if (this.documentSerial != null) {
            return this.documentSerial + this.getDocumentNumber();
        } else {
            return this.getDocumentNumber();
        }

    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
