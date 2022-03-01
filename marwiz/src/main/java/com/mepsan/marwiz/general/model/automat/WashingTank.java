/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:47:13 AM
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.inventory.Stock;
import java.math.BigDecimal;

public class WashingTank {

    private int id;
    private WashingMachicne washingMachicne;
    private Stock stock;
    private String tankNo;
    private BigDecimal capacity;
    private BigDecimal balance;
    private BigDecimal minCapacity;

    public WashingTank() {
        this.washingMachicne = new WashingMachicne();
        this.stock = new Stock();
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

    public String getTankNo() {
        return tankNo;
    }

    public void setTankNo(String tankNo) {
        this.tankNo = tankNo;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(BigDecimal minCapacity) {
        this.minCapacity = minCapacity;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getTankNo();
    }
}
