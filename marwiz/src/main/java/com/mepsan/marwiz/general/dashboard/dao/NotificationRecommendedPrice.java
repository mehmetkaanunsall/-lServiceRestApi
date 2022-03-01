/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 11:42:53
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import java.math.BigDecimal;

public class NotificationRecommendedPrice {

    private int centerstock_id;
    private String stockname;
    private String processdate;
    private double price;
    private int currency_id;
    private boolean is_updateprice;
    private String barcode;
    private BigDecimal weight;
    private int weightunit_id;
    private int sequence;
    private String tag;//Stock üretim yeri değişikliğinde yeni üretim yeri bilgisi 

    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname;
    }

    public String getProcessdate() {
        return processdate;
    }

    public void setProcessdate(String processdate) {
        this.processdate = processdate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(int currency_id) {
        this.currency_id = currency_id;
    }

    public boolean isIs_updateprice() {
        return is_updateprice;
    }

    public void setIs_updateprice(boolean is_updateprice) {
        this.is_updateprice = is_updateprice;
    }

    public int getCenterstock_id() {
        return centerstock_id;
    }

    public void setCenterstock_id(int centerstock_id) {
        this.centerstock_id = centerstock_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getWeightunit_id() {
        return weightunit_id;
    }

    public void setWeightunit_id(int weightunit_id) {
        this.weightunit_id = weightunit_id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    

}
