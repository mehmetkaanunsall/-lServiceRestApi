/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 09:00:32
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Size;

public class Safe extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    @Size(max = 30)
    private String code;
    private Currency currency;
    private Status status;
    private BigDecimal balance;
    private int shiftmovementsafe_id;

    private Date reportDate; // kasa tutanağğı yazdırmak için gerekli olan tarih
    private BigDecimal reportBalance;
    private boolean isMposMovement;
    private Branch branch;

    public Safe() {
        this.currency = new Currency();
        this.status = new Status();
        this.branch = new Branch();
    }

    public Safe(int id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getShiftmovementsafe_id() {
        return shiftmovementsafe_id;
    }

    public void setShiftmovementsafe_id(int shiftmovementsafe_id) {
        this.shiftmovementsafe_id = shiftmovementsafe_id;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getReportBalance() {
        return reportBalance;
    }

    public void setReportBalance(BigDecimal reportBalance) {
        this.reportBalance = reportBalance;
    }

    public boolean isIsMposMovement() {
        return isMposMovement;
    }

    public void setIsMposMovement(boolean isMposMovement) {
        this.isMposMovement = isMposMovement;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
