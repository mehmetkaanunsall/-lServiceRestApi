package com.mepsan.marwiz.general.model.general;

/**
 *
 * @author samet.dag
 */
public class AccountInfo {

    private int id;
    private Account account;
    private String fuelintegrationcode;
    private String accountingintegrationcode;

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

    public String getFuelintegrationcode() {
        return fuelintegrationcode;
    }

    public void setFuelintegrationcode(String fuelintegrationcode) {
        this.fuelintegrationcode = fuelintegrationcode;
    }

    public String getAccountingintegrationcode() {
        return accountingintegrationcode;
    }

    public void setAccountingintegrationcode(String accountingintegrationcode) {
        this.accountingintegrationcode = accountingintegrationcode;
    }

}
