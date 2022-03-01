/**
 *
 *
 *
 * @author Esra Çabuk
 *
 * @date 18.07.2018 08:58:42
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class StockAlternativeBarcode extends WotLogging {

    private int id;
    private Stock stock;
    private String barcode;
    private BigDecimal quantity;
    private boolean isOtherBranch;

    public StockAlternativeBarcode() {

        this.stock = new Stock();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isIsOtherBranch() {
        return isOtherBranch;
    }

    public void setIsOtherBranch(boolean isOtherBranch) {
        this.isOtherBranch = isOtherBranch;
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
