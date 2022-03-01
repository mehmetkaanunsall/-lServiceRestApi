/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.01.2019 15:07:31
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import java.math.BigDecimal;

public class ResponseSalesReturn {

    private int receiptId;
    private String receiptNo;
    private BigDecimal balance;
    private int saleId;

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

}
