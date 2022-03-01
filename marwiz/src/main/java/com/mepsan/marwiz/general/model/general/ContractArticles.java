/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:13:46 PM
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class ContractArticles extends WotLogging {

    private int id;
    private Stock stock;
    private int articltType;
    private BigDecimal warehouseCost;
    private BigDecimal branchProfitRate;
    private BigDecimal rate1;
    private BigDecimal volume1;
    private BigDecimal rate2;
    private BigDecimal volume2;
    private BigDecimal rate3;
    private BigDecimal volume3;
    private BigDecimal rate4;
    private BigDecimal volume4;
    private BigDecimal rate5;
    private BigDecimal volume5;

    public ContractArticles() {
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

    public int getArticltType() {
        return articltType;
    }

    public void setArticltType(int articltType) {
        this.articltType = articltType;
    }

    public BigDecimal getWarehouseCost() {
        return warehouseCost;
    }

    public void setWarehouseCost(BigDecimal warehouseCost) {
        this.warehouseCost = warehouseCost;
    }

    public BigDecimal getBranchProfitRate() {
        return branchProfitRate;
    }

    public void setBranchProfitRate(BigDecimal branchProfitRate) {
        this.branchProfitRate = branchProfitRate;
    }

    public BigDecimal getRate1() {
        return rate1;
    }

    public void setRate1(BigDecimal rate1) {
        this.rate1 = rate1;
    }

    public BigDecimal getVolume1() {
        return volume1;
    }

    public void setVolume1(BigDecimal volume1) {
        this.volume1 = volume1;
    }

    public BigDecimal getRate2() {
        return rate2;
    }

    public void setRate2(BigDecimal rate2) {
        this.rate2 = rate2;
    }

    public BigDecimal getVolume2() {
        return volume2;
    }

    public void setVolume2(BigDecimal volume2) {
        this.volume2 = volume2;
    }

    public BigDecimal getRate3() {
        return rate3;
    }

    public void setRate3(BigDecimal rate3) {
        this.rate3 = rate3;
    }

    public BigDecimal getVolume3() {
        return volume3;
    }

    public void setVolume3(BigDecimal volume3) {
        this.volume3 = volume3;
    }

    public BigDecimal getRate4() {
        return rate4;
    }

    public void setRate4(BigDecimal rate4) {
        this.rate4 = rate4;
    }

    public BigDecimal getVolume4() {
        return volume4;
    }

    public void setVolume4(BigDecimal volume4) {
        this.volume4 = volume4;
    }

    public BigDecimal getRate5() {
        return rate5;
    }

    public void setRate5(BigDecimal rate5) {
        this.rate5 = rate5;
    }

    public BigDecimal getVolume5() {
        return volume5;
    }

    public void setVolume5(BigDecimal volume5) {
        this.volume5 = volume5;
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
