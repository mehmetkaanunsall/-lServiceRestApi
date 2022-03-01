/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 11:53:30
 */
package com.mepsan.marwiz.general.model.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ECommerceStock {

    private Stock stock;
    private int typeId;
    private String HepsiburadaSku;
    private String MerchantSku;
    private BigDecimal Price;
    private BigDecimal AvailableStock;
    private BigDecimal marwizAvailableStock;
    private BigDecimal marwizPrice;
    private int DispatchTime;
    private String CargoCompany1;
    private String CargoCompany2;
    private String CargoCompany3;
    private int MaximumPurchasableQuantity;
    private boolean IsSalable;
    private boolean isSalableHepsiburada;
    private List<Stock> stockList;

    public ECommerceStock() {
        this.stock = new Stock();
        this.stockList = new ArrayList<>();
    }

    public int getDispatchTime() {
        return DispatchTime;
    }

    public void setDispatchTime(int DispatchTime) {
        this.DispatchTime = DispatchTime;
    }

    public String getCargoCompany1() {
        return CargoCompany1;
    }

    public void setCargoCompany1(String CargoCompany1) {
        this.CargoCompany1 = CargoCompany1;
    }

    public String getCargoCompany2() {
        return CargoCompany2;
    }

    public void setCargoCompany2(String CargoCompany2) {
        this.CargoCompany2 = CargoCompany2;
    }

    public String getCargoCompany3() {
        return CargoCompany3;
    }

    public void setCargoCompany3(String CargoCompany3) {
        this.CargoCompany3 = CargoCompany3;
    }

    public int getMaximumPurchasableQuantity() {
        return MaximumPurchasableQuantity;
    }

    public void setMaximumPurchasableQuantity(int MaximumPurchasableQuantity) {
        this.MaximumPurchasableQuantity = MaximumPurchasableQuantity;
    }

    public boolean isIsSalable() {
        return IsSalable;
    }

    public void setIsSalable(boolean IsSalable) {
        this.IsSalable = IsSalable;
    }

    public boolean isIsSalableHepsiburada() {
        return isSalableHepsiburada;
    }

    public void setIsSalableHepsiburada(boolean isSalableHepsiburada) {
        this.isSalableHepsiburada = isSalableHepsiburada;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getHepsiburadaSku() {
        return HepsiburadaSku;
    }

    public void setHepsiburadaSku(String HepsiburadaSku) {
        this.HepsiburadaSku = HepsiburadaSku;
    }

    public String getMerchantSku() {
        return MerchantSku;
    }

    public void setMerchantSku(String MerchantSku) {
        this.MerchantSku = MerchantSku;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal Price) {
        this.Price = Price;
    }

    public BigDecimal getAvailableStock() {
        return AvailableStock;
    }

    public void setAvailableStock(BigDecimal AvailableStock) {
        this.AvailableStock = AvailableStock;
    }

    public BigDecimal getMarwizAvailableStock() {
        return marwizAvailableStock;
    }

    public void setMarwizAvailableStock(BigDecimal marwizAvailableStock) {
        this.marwizAvailableStock = marwizAvailableStock;
    }

    public BigDecimal getMarwizPrice() {
        return marwizPrice;
    }

    public void setMarwizPrice(BigDecimal marwizPrice) {
        this.marwizPrice = marwizPrice;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public String toString() {
        return this.getHepsiburadaSku();
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }
}
