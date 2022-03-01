/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:21:09 PM
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class AccountCard extends WotLogging {

    private int id;
    private Account account;
    private Status status;
    private String rfNo;

    public AccountCard() {
        this.status = new Status();
        this.account = new Account();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRfNo() {
        return rfNo;
    }

    public void setRfNo(String rfNo) {
        this.rfNo = rfNo;
    }

    @Override
    public String toString() {
        return this.getRfNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
