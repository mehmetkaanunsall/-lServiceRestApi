/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */    
package com.mepsan.marwiz.general.model.automation;
     
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Account;

/**
 *
 * @author asli.can
 */
public class FuelCardType {

    private int id;
    private String name;
    private int typeNo;    
    private FuelSaleType saleType;
    private Account account;    
    private BankAccount bankacount;

    public FuelCardType() {
        this.saleType = new FuelSaleType();
        this.account = new Account();
        this.bankacount = new BankAccount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(int typeNo) {
        this.typeNo = typeNo;
    }

    public FuelSaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(FuelSaleType saleType) {
        this.saleType = saleType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BankAccount getBankacount() {
        return bankacount;
    }

    public void setBankacount(BankAccount bankacount) {
        this.bankacount = bankacount;
    }




    
    
}
