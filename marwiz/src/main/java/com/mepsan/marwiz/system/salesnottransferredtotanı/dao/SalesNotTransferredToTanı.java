/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.dao;

import java.math.BigDecimal;

/**
 *
 * @author sinem.arslan
 */
public class SalesNotTransferredToTanı {

    private BigDecimal sentSalesCount;
    private BigDecimal unsentSalesCount;

    public BigDecimal getSentSalesCount() {
        return sentSalesCount;
    }

    public void setSentSalesCount(BigDecimal sentSalesCount) {
        this.sentSalesCount = sentSalesCount;
    }

    public BigDecimal getUnsentSalesCount() {
        return unsentSalesCount;
    }

    public void setUnsentSalesCount(BigDecimal unsentSalesCount) {
        this.unsentSalesCount = unsentSalesCount;
    }

}
