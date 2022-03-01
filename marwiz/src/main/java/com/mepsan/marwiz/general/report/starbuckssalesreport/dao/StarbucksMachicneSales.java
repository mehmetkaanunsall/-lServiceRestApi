/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:10:16 PM
 */
package com.mepsan.marwiz.general.report.starbuckssalesreport.dao;

import java.io.Serializable;

public class StarbucksMachicneSales implements Serializable{

    private int transaction_id;
    private int machine_id;
    private int datetime;
    private int transaction_dt;
    private int registered_dt;
    private double quantity;
    private double price;
    private double vat;
    private int stock_id;
    private int selection;
    private String payment_method;
    private String longTime;
    private String stockName;

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(int machine_id) {
        this.machine_id = machine_id;
    }

    public int getDatetime() {
        return datetime;
    }

    public void setDatetime(int datetime) {
        this.datetime = datetime;
    }

    public int getTransaction_dt() {
        return transaction_dt;
    }

    public void setTransaction_dt(int transaction_dt) {
        this.transaction_dt = transaction_dt;
    }

    public int getRegistered_dt() {
        return registered_dt;
    }

    public void setRegistered_dt(int registered_dt) {
        this.registered_dt = registered_dt;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getLongTime() {
        return longTime;
    }

    public void setLongTime(String longTime) {
        this.longTime = longTime;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    
    

}
