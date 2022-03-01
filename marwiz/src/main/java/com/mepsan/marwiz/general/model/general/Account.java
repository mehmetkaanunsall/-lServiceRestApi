/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2018 03:18:34
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.io.Serializable;
import java.math.BigDecimal;

public class Account extends WotLogging implements Serializable {

    private int id;
    private Boolean isPerson;
    private String name;
    private String title;
    private String code;
    private String taxNo;
    private String taxOffice;
    private Status status;
    private Type type;
    private BigDecimal balance;
    private BigDecimal creditlimit;
    private String phone;
    private String email;
    private String address;
    private City city;
    private County county;
    private Country country;
    private boolean isEmployee;
    private int maxExpiryCount;
    private int taxpayertype_id;
    private String onlyAccountName;
    private BigDecimal transferBalance;
    private int centeraccount_id;
    private String tagInfo;
    private String description;
    private Integer dueDay;
    private int paymenttype_id;
    private int tagQuantity;

    public Account() {
        this.type = new Type();
        this.status = new Status();
        this.city = new City();
        this.country = new Country();
        this.county = new County();

    }

    public boolean isIsEmployee() {
        return isEmployee;
    }

    public void setIsEmployee(boolean isEmployee) {
        this.isEmployee = isEmployee;
    }

    public Account(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getIsPerson() {
        return isPerson;
    }

    public void setIsPerson(Boolean isPerson) {
        this.isPerson = isPerson;
    }

    public String getName() {
        if (isEmployee) {
            if (name != null || title != null) {
                return name + " " + title;
            }
            return null;
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public BigDecimal getCreditlimit() {
        return creditlimit;
    }

    public void setCreditlimit(BigDecimal creditlimit) {
        this.creditlimit = creditlimit;
    }

    public String getOnlyAccountName() {
        return onlyAccountName;
    }

    public void setOnlyAccountName(String onlyAccountName) {
        this.onlyAccountName = onlyAccountName;
    }

    public BigDecimal getTransferBalance() {
        return transferBalance;
    }

    public void setTransferBalance(BigDecimal transferBalance) {
        this.transferBalance = transferBalance;
    }

    public int getMaxExpiryCount() {
        return maxExpiryCount;
    }

    public void setMaxExpiryCount(int maxExpiryCount) {
        this.maxExpiryCount = maxExpiryCount;
    }

    public int getCenteraccount_id() {
        return centeraccount_id;
    }

    public void setCenteraccount_id(int centeraccount_id) {
        this.centeraccount_id = centeraccount_id;
    }

    public int getTaxpayertype_id() {
        return taxpayertype_id;
    }

    public void setTaxpayertype_id(int taxpayertype_id) {
        this.taxpayertype_id = taxpayertype_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(String tagInfo) {
        this.tagInfo = tagInfo;
    }

    public Integer getDueDay() {
        return dueDay;
    }

    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
    }

    public int getPaymenttype_id() {
        return paymenttype_id;
    }

    public void setPaymenttype_id(int paymenttype_id) {
        this.paymenttype_id = paymenttype_id;
    }

    public int getTagQuantity() {
        return tagQuantity;
    }

    public void setTagQuantity(int tagQuantity) {
        this.tagQuantity = tagQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account other = (Account) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
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
