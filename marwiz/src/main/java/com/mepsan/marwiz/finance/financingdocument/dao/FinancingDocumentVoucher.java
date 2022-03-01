/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.10.2018 12:21:06
 */
package com.mepsan.marwiz.finance.financingdocument.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.UserData;

public class FinancingDocumentVoucher extends FinancingDocument {

    private ChequeBill chequeBill;
    private BankAccount bankAccount;
    private UserData recipientPerson;
    private UserData deliveryPerson;
    private Branch branch;

    public FinancingDocumentVoucher() {
        chequeBill = new ChequeBill();
        bankAccount = new BankAccount();
        recipientPerson = new UserData();
        deliveryPerson = new UserData();
        this.branch = new Branch();
    }

    public ChequeBill getChequeBill() {
        return chequeBill;
    }

    public void setChequeBill(ChequeBill chequeBill) {
        this.chequeBill = chequeBill;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public UserData getRecipientPerson() {
        return recipientPerson;
    }

    public void setRecipientPerson(UserData recipientPerson) {
        this.recipientPerson = recipientPerson;
    }

    public UserData getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(UserData deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}
