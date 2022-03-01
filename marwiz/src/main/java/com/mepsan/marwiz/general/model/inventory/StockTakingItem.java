/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 09:17:24
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class StockTakingItem extends WotLogging {

    private int id;
    private StockTaking stockTaking;
    private Date processDate;
    private Stock stock;
    private BigDecimal systemQuantity;
    private BigDecimal realQuantity;
    private BigDecimal entryQuantity;
    private BigDecimal exitQuantity;
    private BigDecimal currentQuantity;
    private BigDecimal price;
    private Currency currency;
    private int excelDataType; // excelden stok aktarımında kaydın hatalı olup olmadığı bilgisini döndürür.
    private boolean openUpdate;
    private BigDecimal currentPurchasePrice;
    private BigDecimal currentSalePrice;
    private Currency currentPurchaseCurrency;
    private Currency currentSaleCurrency;

    public StockTakingItem() {
        this.stockTaking = new StockTaking();
        this.stock = new Stock();
        this.currency=new Currency();
        this.currentSaleCurrency=new Currency();
        this.currentPurchaseCurrency=new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(BigDecimal systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public BigDecimal getRealQuantity() {
        return realQuantity;
    }

    public void setRealQuantity(BigDecimal realQuantity) {
        this.realQuantity = realQuantity;
    }

    public BigDecimal getEntryQuantity() {
        return entryQuantity;
    }

    public void setEntryQuantity(BigDecimal entryQuantity) {
        this.entryQuantity = entryQuantity;
    }

    public BigDecimal getExitQuantity() {
        return exitQuantity;
    }

    public void setExitQuantity(BigDecimal exitQuantity) {
        this.exitQuantity = exitQuantity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public boolean isOpenUpdate() {
        return openUpdate;
    }

    public void setOpenUpdate(boolean openUpdate) {
        this.openUpdate = openUpdate;
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getCurrentPurchasePrice() {
        return currentPurchasePrice;
    }

    public void setCurrentPurchasePrice(BigDecimal currentPurchasePrice) {
        this.currentPurchasePrice = currentPurchasePrice;
    }

    public BigDecimal getCurrentSalePrice() {
        return currentSalePrice;
    }

    public void setCurrentSalePrice(BigDecimal currentSalePrice) {
        this.currentSalePrice = currentSalePrice;
    }

    public Currency getCurrentPurchaseCurrency() {
        return currentPurchaseCurrency;
    }

    public void setCurrentPurchaseCurrency(Currency currentPurchaseCurrency) {
        this.currentPurchaseCurrency = currentPurchaseCurrency;
    }

    public Currency getCurrentSaleCurrency() {
        return currentSaleCurrency;
    }

    public void setCurrentSaleCurrency(Currency currentSaleCurrency) {
        this.currentSaleCurrency = currentSaleCurrency;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getStock().getId();
    }
}
