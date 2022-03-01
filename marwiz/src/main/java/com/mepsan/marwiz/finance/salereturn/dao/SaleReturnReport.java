/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2018 09:38:12
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import com.mepsan.marwiz.general.model.finance.CreditPayment;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import java.math.BigDecimal;

public class SaleReturnReport extends SalePayment {

    private boolean isCreditPayment;
    private BigDecimal totalCreditPayment;
    private SaleItem saleItem;
    private CreditPayment creditPayment;
    private boolean isUsedStock;//Servis ürünlerinin kullanılıp kullanılmadığını gösterir

    public SaleReturnReport() {
        this.saleItem = new SaleItem();
        this.creditPayment = new CreditPayment();
    }

    public SaleItem getSaleItem() {
        return saleItem;
    }

    public void setSaleItem(SaleItem saleItem) {
        this.saleItem = saleItem;
    }

    public boolean isIsCreditPayment() {
        return isCreditPayment;
    }

    public void setIsCreditPayment(boolean isCreditPayment) {
        this.isCreditPayment = isCreditPayment;
    }

    public BigDecimal getTotalCreditPayment() {
        return totalCreditPayment;
    }

    public void setTotalCreditPayment(BigDecimal totalCreditPayment) {
        this.totalCreditPayment = totalCreditPayment;
    }

    public CreditPayment getCreditPayment() {
        return creditPayment;
    }

    public void setCreditPayment(CreditPayment creditPayment) {
        this.creditPayment = creditPayment;
    }

    public boolean isIsUsedStock() {
        return isUsedStock;
    }

    public void setIsUsedStock(boolean isUsedStock) {
        this.isUsedStock = isUsedStock;
    }

}
