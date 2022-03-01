/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 15:23:58
 */
package com.mepsan.marwiz.system.sapintegration.business;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;

public class SapIntegration {

    private FinancingDocument financingDocument;
    private Safe safe;
    private BankAccount bankAccount;
    private String branchCode;
    private int branchId;
    private boolean isSend;
    private String response;

    public SapIntegration() {
        this.financingDocument = new FinancingDocument();
        this.safe = new Safe();
        this.bankAccount = new BankAccount();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

}
