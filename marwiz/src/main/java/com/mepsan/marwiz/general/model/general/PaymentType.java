/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

/**
 *
 * @author m.duzoylum
 */
public class PaymentType extends WotLogging {

    private int id;
    private String entegrationcode;
    private String entegrationname;

    private BigDecimal totalMoney; // Sap mutabakat işlemleri sayfasında tora satışlarını ödeme tipine göre göstermek için tutuldu.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntegrationcode() {
        return entegrationcode;
    }

    public void setEntegrationcode(String entegrationcode) {
        this.entegrationcode = entegrationcode;
    }

    public String getEntegrationname() {
        return entegrationname;
    }

    public void setEntegrationname(String entegrationname) {
        this.entegrationname = entegrationname;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

}
