/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:34:43
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class PriceListItem extends WotLogging {

    private int id;
    private PriceList priceList;
    private Stock stock;
    private Currency currency;
    private BigDecimal price;
    private boolean is_taxIncluded;
    private int type; // fiyat listesine excelden eklenen kayıtların hatalı olup olmadığı bilgisi bu alanda tutulur. Type=-1 hatalı 
    private BigDecimal profitRate; // bu alan faturalarda karlılık oranı hespalamak için tutulmuştur.
    private String alternativeBarcodes;
    private BigDecimal priceWithTax; // Etiket yazdırırken KDV dahil fiyatının basılması amacıyla tutuldu.
    private int logPrintTagId;
    private BigDecimal printTagQuantity;
    private int tagQuantity; // Paremetrik olarak kaç adet etiket yazdıracağını belirlemek amacıyla tutuldu.
    private Waybill waybill;
    private Categorization categorization;

    /**
     * Etiket Yazdırma İşleminde Son Fiyat Değerini Almak İçin Oluşturuldu
     */
    private Date processDate;
    private BigDecimal lastPrice;

    public PriceListItem() {
        this.priceList = new PriceList();
        this.stock = new Stock();
        this.currency = new Currency();
        this.waybill = new Waybill();
        this.categorization = new Categorization();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isIs_taxIncluded() {
        return is_taxIncluded;
    }

    public void setIs_taxIncluded(boolean is_taxIncluded) {
        this.is_taxIncluded = is_taxIncluded;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
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
        final PriceListItem other = (PriceListItem) obj;
        if (!Objects.equals(this.stock.getId(), other.stock.getId())) {
            return false;
        }
        return true;
    }

    public int getLogPrintTagId() {
        return logPrintTagId;
    }

    public void setLogPrintTagId(int logPrintTagId) {
        this.logPrintTagId = logPrintTagId;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public BigDecimal getPrintTagQuantity() {
        return printTagQuantity;
    }

    public void setPrintTagQuantity(BigDecimal printTagQuantity) {
        this.printTagQuantity = printTagQuantity;
    }

    public String getAlternativeBarcodes() {
        return alternativeBarcodes;
    }

    public void setAlternativeBarcodes(String alternativeBarcodes) {
        this.alternativeBarcodes = alternativeBarcodes;
    }

    public BigDecimal getPriceWithTax() {
        return priceWithTax;
    }

    public void setPriceWithTax(BigDecimal priceWithTax) {
        this.priceWithTax = priceWithTax;
    }

    public int getTagQuantity() {
        return tagQuantity;
    }

    public void setTagQuantity(int tagQuantity) {
        this.tagQuantity = tagQuantity;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
