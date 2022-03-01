/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class WaybillItem extends WotLogging {

    private int id;
    private Waybill waybill;
    private Stock stock;
    private BigDecimal quantity;
    private String description;
    private BigDecimal remainingQuantity;
    private int stockCount;
    private String jsonItems;

    private int excelDataType;
    private TaxGroup taxGroup;
    private PriceListItem priceListItem;

    private String orderItemIds;
    private String orderItemQuantitys;
    private String orderIds;

    private String firstOrderItemIds;
    private String firstOrderItemQuantitys;
    private String firstOrderIds;

    private BigDecimal controlQuantity;// siparişten irsaliye oluştura basılırsa irsaliye miktarının sipariş miktarından büyük olmasını engellemek için ko ntrol için yazılmıştır.

    private Warehouse warehouse;

    private String orderRemainingQuantity;

    public WaybillItem(int id) {
        this.id = id;
    }

    public WaybillItem() {
        this.stock = new Stock();
        this.waybill = new Waybill();
        this.taxGroup = new TaxGroup();
        this.priceListItem = new PriceListItem();
        this.warehouse = new Warehouse();
    }

    public String getJsonItems() {
        return jsonItems;
    }

    public void setJsonItems(String jsonItems) {
        this.jsonItems = jsonItems;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public PriceListItem getPriceListItem() {
        return priceListItem;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }

    public void setPriceListItem(PriceListItem priceListItem) {
        this.priceListItem = priceListItem;
    }

    public String getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderItemIds(String orderItemIds) {
        this.orderItemIds = orderItemIds;
    }

    public String getOrderItemQuantitys() {
        return orderItemQuantitys;
    }

    public void setOrderItemQuantitys(String orderItemQuantitys) {
        this.orderItemQuantitys = orderItemQuantitys;
    }

    public BigDecimal getControlQuantity() {
        return controlQuantity;
    }

    public void setControlQuantity(BigDecimal controlQuantity) {
        this.controlQuantity = controlQuantity;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }

    public String getFirstOrderItemIds() {
        return firstOrderItemIds;
    }

    public void setFirstOrderItemIds(String firstOrderItemIds) {
        this.firstOrderItemIds = firstOrderItemIds;
    }

    public String getFirstOrderItemQuantitys() {
        return firstOrderItemQuantitys;
    }

    public void setFirstOrderItemQuantitys(String firstOrderItemQuantitys) {
        this.firstOrderItemQuantitys = firstOrderItemQuantitys;
    }

    public String getFirstOrderIds() {
        return firstOrderIds;
    }

    public void setFirstOrderIds(String firstOrderIds) {
        this.firstOrderIds = firstOrderIds;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getOrderRemainingQuantity() {
        return orderRemainingQuantity;
    }

    public void setOrderRemainingQuantity(String orderRemainingQuantity) {
        this.orderRemainingQuantity = orderRemainingQuantity;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
