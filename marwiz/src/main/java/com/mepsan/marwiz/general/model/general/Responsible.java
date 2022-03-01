/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 02:54:09
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Responsible extends WotLogging implements Serializable {

    private int id;
    private Account account;
    private String name;
    private String surname;
    private String identityNo;
    private Date birthdate;
    private boolean gender;
    private String position;
    private BankBranch bankBranch;

    private List<Address<Responsible>> listOfAddresses;
    private List<Phone<Responsible>> listOfPhones;
    private List<Internet<Responsible>> listOfinternet;

    public Responsible() {
        this.account = new Account();
        this.listOfAddresses = new ArrayList<>();
        this.listOfAddresses = new ArrayList<>();
        this.listOfAddresses = new ArrayList<>();
        this.bankBranch = new BankBranch();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Address<Responsible>> getListOfAddresses() {
        return listOfAddresses;
    }

    public void setListOfAddresses(List<Address<Responsible>> listOfAddresses) {
        this.listOfAddresses = listOfAddresses;
    }

    public List<Phone<Responsible>> getListOfPhones() {
        return listOfPhones;
    }

    public void setListOfPhones(List<Phone<Responsible>> listOfPhones) {
        this.listOfPhones = listOfPhones;
    }

    public List<Internet<Responsible>> getListOfinternet() {
        return listOfinternet;
    }

    public void setListOfinternet(List<Internet<Responsible>> listOfinternet) {
        this.listOfinternet = listOfinternet;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
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
