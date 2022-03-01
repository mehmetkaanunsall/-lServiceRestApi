/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:55:16 PM
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class RefineryStockPrice extends WotLogging {

    private int id;
    private int refineryId;
    private Stock stock;
    private Currency currency;
    private BigDecimal price;
    private int rowId;

    public RefineryStockPrice() {
        this.stock = new Stock();
        this.currency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRefineryId() {
        return refineryId;
    }

    public void setRefineryId(int refineryId) {
        this.refineryId = refineryId;
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

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    @Override
    public String toString() {
        return this.stock.getName();
    }

    @Override
    public int hashCode() {
        return this.getRowId();
    }

}
