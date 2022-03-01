/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:25:19 PM
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;

public class ExpenseUnitPrice {

    private int id;
    private WashingMachicne washingMachicne;
    private Stock stock;
    private BigDecimal unitPrice;
    private Currency currency;

    public ExpenseUnitPrice() {
        this.washingMachicne = new WashingMachicne();
        this.stock = new Stock();
        this.currency = new Currency();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WashingMachicne getWashingMachicne() {
        return washingMachicne;
    }

    public void setWashingMachicne(WashingMachicne washingMachicne) {
        this.washingMachicne = washingMachicne;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return this.stock.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
