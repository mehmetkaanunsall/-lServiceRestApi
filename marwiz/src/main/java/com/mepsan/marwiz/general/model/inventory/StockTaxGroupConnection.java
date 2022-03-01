/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * Created on 04.11.2016 08:19:04
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class StockTaxGroupConnection extends WotLogging {

    private int id;
    private Stock stock;
    private TaxGroup taxGroup;
    private boolean isPurchase;

    public StockTaxGroupConnection() {
        this.stock = new Stock();
        this.taxGroup = new TaxGroup();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
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
