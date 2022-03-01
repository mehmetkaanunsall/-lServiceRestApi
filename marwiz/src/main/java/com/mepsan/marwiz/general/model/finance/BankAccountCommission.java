/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.08.2020 04:13:39
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class BankAccountCommission extends WotLogging {

    private int id;
    private FinancingDocument financingDocument;
    private FinancingDocument commissionFinancingDocument;
    private BigDecimal totalMoney;
    private BigDecimal commissionRate;
    private BigDecimal commissionMoney;

    public BankAccountCommission() {
        this.financingDocument = new FinancingDocument();
        this.commissionFinancingDocument = new FinancingDocument();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public FinancingDocument getCommissionFinancingDocument() {
        return commissionFinancingDocument;
    }

    public void setCommissionFinancingDocument(FinancingDocument commissionFinancingDocument) {
        this.commissionFinancingDocument = commissionFinancingDocument;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionMoney() {
        return commissionMoney;
    }

    public void setCommissionMoney(BigDecimal commissionMoney) {
        this.commissionMoney = commissionMoney;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
