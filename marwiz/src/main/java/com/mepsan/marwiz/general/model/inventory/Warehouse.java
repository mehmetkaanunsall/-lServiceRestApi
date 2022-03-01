/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 01:52:33
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Objects;

public class Warehouse extends WotLogging {

    private int id;
    private String name;
    private String code;
    private String description;
    private Status status;
    private boolean isFuel;
    private BigDecimal minCapacity;
    private BigDecimal capacity;
    private BigDecimal concentrationRate;
    private boolean isAutomat;

    //////Tank Sayfasinda İstenilen Alanlar
    private BigDecimal purchaseTotalPrice;
    private BigDecimal purchaseTotalMoney;
    private BigDecimal purchaseTotalLiter;
    private BigDecimal salesTotalLiter;
    private BigDecimal salesTotalPrice;
    private BigDecimal salesTotalMoney;
    private BigDecimal purchaseUnitPriceWithTax;
    private BigDecimal purchaseUnitPriceWithoutTax;
    private BigDecimal salesUnitPriceWithTax;
    private BigDecimal salesUnitPriceWithoutTax;
    private BigDecimal availableStockWithoutSalesPrice;
    private BigDecimal availableStockWithSalesPrice;
    private BigDecimal availableStockWithoutPurchasePrice;
    private BigDecimal availableStockWithPurchasePrice;
    private BigDecimal lastQuantity;
    private Stock stock;//WareHouseItem Constructor İçerisinde Warehouse new Olduğu İçin Stok Burada Kullanıldı.
    private Branch branch;

    public Warehouse() {
        this.status = new Status();
        this.stock = new Stock();
        this.branch = new Branch();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isIsFuel() {
        return isFuel;
    }

    public void setIsFuel(boolean isFuel) {
        this.isFuel = isFuel;
    }

    public BigDecimal getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(BigDecimal minCapacity) {
        this.minCapacity = minCapacity;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getConcentrationRate() {
        return concentrationRate;
    }

    public void setConcentrationRate(BigDecimal concentrationRate) {
        this.concentrationRate = concentrationRate;
    }

    public BigDecimal getPurchaseTotalPrice() {
        return purchaseTotalPrice;
    }

    public void setPurchaseTotalPrice(BigDecimal purchaseTotalPrice) {
        this.purchaseTotalPrice = purchaseTotalPrice;
    }

    public BigDecimal getPurchaseTotalMoney() {
        return purchaseTotalMoney;
    }

    public void setPurchaseTotalMoney(BigDecimal purchaseTotalMoney) {
        this.purchaseTotalMoney = purchaseTotalMoney;
    }

    public BigDecimal getPurchaseTotalLiter() {
        return purchaseTotalLiter;
    }

    public void setPurchaseTotalLiter(BigDecimal purchaseTotalLiter) {
        this.purchaseTotalLiter = purchaseTotalLiter;
    }

    public BigDecimal getSalesTotalLiter() {
        return salesTotalLiter;
    }

    public void setSalesTotalLiter(BigDecimal salesTotalLiter) {
        this.salesTotalLiter = salesTotalLiter;
    }

    public BigDecimal getSalesTotalPrice() {
        return salesTotalPrice;
    }

    public void setSalesTotalPrice(BigDecimal salesTotalPrice) {
        this.salesTotalPrice = salesTotalPrice;
    }

    public BigDecimal getSalesTotalMoney() {
        return salesTotalMoney;
    }

    public void setSalesTotalMoney(BigDecimal salesTotalMoney) {
        this.salesTotalMoney = salesTotalMoney;
    }

    public BigDecimal getPurchaseUnitPriceWithTax() {
        return purchaseUnitPriceWithTax;
    }

    public void setPurchaseUnitPriceWithTax(BigDecimal purchaseUnitPriceWithTax) {
        this.purchaseUnitPriceWithTax = purchaseUnitPriceWithTax;
    }

    public BigDecimal getPurchaseUnitPriceWithoutTax() {
        return purchaseUnitPriceWithoutTax;
    }

    public void setPurchaseUnitPriceWithoutTax(BigDecimal purchaseUnitPriceWithoutTax) {
        this.purchaseUnitPriceWithoutTax = purchaseUnitPriceWithoutTax;
    }

    public BigDecimal getLastQuantity() {
        return lastQuantity;
    }

    public void setLastQuantity(BigDecimal lastQuantity) {
        this.lastQuantity = lastQuantity;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getSalesUnitPriceWithTax() {
        return salesUnitPriceWithTax;
    }

    public void setSalesUnitPriceWithTax(BigDecimal salesUnitPriceWithTax) {
        this.salesUnitPriceWithTax = salesUnitPriceWithTax;
    }

    public BigDecimal getSalesUnitPriceWithoutTax() {
        return salesUnitPriceWithoutTax;
    }

    public void setSalesUnitPriceWithoutTax(BigDecimal salesUnitPriceWithoutTax) {
        this.salesUnitPriceWithoutTax = salesUnitPriceWithoutTax;
    }

    public BigDecimal getAvailableStockWithoutSalesPrice() {
        return availableStockWithoutSalesPrice;
    }

    public void setAvailableStockWithoutSalesPrice(BigDecimal availableStockWithoutSalesPrice) {
        this.availableStockWithoutSalesPrice = availableStockWithoutSalesPrice;
    }

    public BigDecimal getAvailableStockWithSalesPrice() {
        return availableStockWithSalesPrice;
    }

    public void setAvailableStockWithSalesPrice(BigDecimal availableStockWithSalesPrice) {
        this.availableStockWithSalesPrice = availableStockWithSalesPrice;
    }

    public BigDecimal getAvailableStockWithoutPurchasePrice() {
        return availableStockWithoutPurchasePrice;
    }

    public void setAvailableStockWithoutPurchasePrice(BigDecimal availableStockWithoutPurchasePrice) {
        this.availableStockWithoutPurchasePrice = availableStockWithoutPurchasePrice;
    }

    public BigDecimal getAvailableStockWithPurchasePrice() {
        return availableStockWithPurchasePrice;
    }

    public void setAvailableStockWithPurchasePrice(BigDecimal availableStockWithPurchasePrice) {
        this.availableStockWithPurchasePrice = availableStockWithPurchasePrice;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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
        final Warehouse other = (Warehouse) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    public boolean isIsAutomat() {
        return isAutomat;
    }

    public void setIsAutomat(boolean isAutomat) {
        this.isAutomat = isAutomat;
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
