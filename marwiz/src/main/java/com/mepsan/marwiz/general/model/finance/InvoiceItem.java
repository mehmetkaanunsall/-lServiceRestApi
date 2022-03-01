/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2017 14:02:29
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class InvoiceItem extends WotLogging implements Cloneable {

    private int id;
    private Invoice invoice;
    private boolean isService;
    private Stock stock;
    private Unit unit;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalPrice;
    private BigDecimal taxRate;
    private BigDecimal totalTax;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal totalMoney;
    private String description;
    private boolean isDiscountRate;
    private boolean isDiscountRate2;
    private BigDecimal discountRate2;
    private BigDecimal discountPrice2;

    private int stockCount;//bir faturada aynı üründen kaç adet olduğu bilgisi
    private String waybillItemIds;//ürün irsaliyeden aktarıldı ise irsaliye item id bilgileri
    private String waybillItemQuantity;
    private String waybillItemQuantitys;//ürün birden fazla irsaliyeye bağlı ise hangi irsaliyeye kaçar adet bağlandığını tutuyor.
    private String jsonItems;
    private boolean isTaxIncluded;
    private PriceListItem priceListItem;
    private int itemProcessType; // faturalara hızlı ürün eklerken objenin insert mi update mi olacağını belirler.
    private boolean isCanSaveItem;
    private BigDecimal recommendedPrice;//fatura iskontosu değiştiğinde önerilen satış fiyatını tutmak için
    private BigDecimal oldProfitPercentage;//faturada ürün bazlı karlılık dğeişimi için eski kar oranı
    private BigDecimal newProfitPercentage;//faturada ürün bazlı karlılık dğeişimi için yeni kar oranı
    private BigDecimal invoiceDiscountPrice; //fatura bazlı iskonto girildiği zaman ürüne yansıyan iskonto tutarını gösteren değer 

    private int excelDataType;
    private BigDecimal fuelPrice;
    private int rowId;
    private int processType;
    private BigDecimal recommendedSalesPrice;
    private BigDecimal profitRate;
    private boolean isThereMandatoryPrice;
    private BigDecimal lastPurchasePrice;
    private BigDecimal excelUnitPrice;
    private boolean excelIsTaxInclued;

    private BigDecimal profitPrice;
    private BigDecimal profitPercentage;
    private BigDecimal oldPrice;
    private BigDecimal oldDiscountRate;
    private BigDecimal olddiscountPrice;
    private boolean isFree;

    private BigDecimal oldQuantity;//Hızlı eklemede stok bakiyesinin eksiye düşmesini engellemek için miktar karşılaştırmasında kullanılır

    private InvoiceItem priceDifferentInvoiceItem;
    private BigDecimal priceDifferentTotalMoney;

    private BigDecimal controlQuantity;// irsaliyeden veya siparişten faturalaştıra basılırsa fatura miktarının irsaliye veya sipariş miktarından büyük olmasını engellemek için ko ntrol için yazılmıştır.

    private boolean isNotCalcTotalPrice;

    private String orderItemIds;
    private String orderItemQuantitys;
    private String orderIds;

    private String firstOrderItemIds;
    private String firstOrderItemQuantitys;
    private String firstOrderIds;

    private BigDecimal newUnitPrice;
    private BigDecimal oldUnitPrice;
    private Warehouse warehouse;

    private TaxGroup taxGroup;
    private int isFuelWarehouse;
    private int isFuelWarehouseItem;
    private BigDecimal excelUnitPriceTaxExcluded;
    private String remainingQuantity;
    private BigDecimal redemption;

    public InvoiceItem(int id) {
        this.id = id;
    }

    public InvoiceItem() {
        this.invoice = new Invoice();
        this.stock = new Stock();
        this.currency = new Currency();
        this.unit = new Unit();
        this.warehouse = new Warehouse();
        this.taxGroup = new TaxGroup();

    }

    public InvoiceItem(BigDecimal totalTax, BigDecimal taxRate, Currency currency) {
        this.taxRate = taxRate;
        this.currency = currency;
        this.totalTax = totalTax;
    }

    public InvoiceItem(BigDecimal totalTax, BigDecimal taxRate) {
        this.taxRate = taxRate;
        this.totalTax = totalTax;
    }

    public boolean isIsDiscountRate() {
        return isDiscountRate;
    }

    public BigDecimal getOldProfitPercentage() {
        return oldProfitPercentage;
    }

    public BigDecimal getLastPurchasePrice() {
        return lastPurchasePrice;
    }

    public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
        this.lastPurchasePrice = lastPurchasePrice;
    }

    public BigDecimal getOldDiscountRate() {
        return oldDiscountRate;
    }

    public void setOldDiscountRate(BigDecimal oldDiscountRate) {
        this.oldDiscountRate = oldDiscountRate;
    }

    public BigDecimal getOlddiscountPrice() {
        return olddiscountPrice;
    }

    public void setOlddiscountPrice(BigDecimal olddiscountPrice) {
        this.olddiscountPrice = olddiscountPrice;
    }

    public void setOldProfitPercentage(BigDecimal oldProfitPercentage) {
        this.oldProfitPercentage = oldProfitPercentage;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewProfitPercentage() {
        return newProfitPercentage;
    }

    public void setNewProfitPercentage(BigDecimal newProfitPercentage) {
        this.newProfitPercentage = newProfitPercentage;
    }

    public void setIsDiscountRate(boolean isDiscountRate) {
        this.isDiscountRate = isDiscountRate;
    }

    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public String getWaybillItemQuantitys() {
        return waybillItemQuantitys;
    }

    public void setWaybillItemQuantitys(String waybillItemQuantitys) {
        this.waybillItemQuantitys = waybillItemQuantitys;
    }

    public String getJsonItems() {
        return jsonItems;
    }

    public void setJsonItems(String jsonItems) {
        this.jsonItems = jsonItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public boolean isIsService() {
        return isService;
    }

    public void setIsService(boolean isService) {
        this.isService = isService;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getInvoiceDiscountPrice() {
        return invoiceDiscountPrice;
    }

    public void setInvoiceDiscountPrice(BigDecimal invoiceDiscountPrice) {
        this.invoiceDiscountPrice = invoiceDiscountPrice;
    }

    public String getWaybillItemIds() {
        return waybillItemIds;
    }

    public String getWaybillItemQuantity() {
        return waybillItemQuantity;
    }

    public void setWaybillItemQuantity(String waybillItemQuantity) {
        this.waybillItemQuantity = waybillItemQuantity;
    }

    public void setWaybillItemIds(String waybillItemIds) {
        this.waybillItemIds = waybillItemIds;
    }

    public PriceListItem getPriceListItem() {
        return priceListItem;
    }

    public void setPriceListItem(PriceListItem priceListItem) {
        this.priceListItem = priceListItem;
    }

    public int getItemProcessType() {
        return itemProcessType;
    }

    public void setItemProcessType(int itemProcessType) {
        this.itemProcessType = itemProcessType;
    }

    public boolean isIsCanSaveItem() {
        return isCanSaveItem;
    }

    public void setIsCanSaveItem(boolean isCanSaveItem) {
        this.isCanSaveItem = isCanSaveItem;
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

    public BigDecimal getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(BigDecimal fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public BigDecimal getRecommendedSalesPrice() {
        return recommendedSalesPrice;
    }

    public void setRecommendedSalesPrice(BigDecimal recommendedSalesPrice) {
        this.recommendedSalesPrice = recommendedSalesPrice;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public boolean isIsThereMandatoryPrice() {
        return isThereMandatoryPrice;
    }

    public void setIsThereMandatoryPrice(boolean isThereMandatoryPrice) {
        this.isThereMandatoryPrice = isThereMandatoryPrice;
    }

    public BigDecimal getExcelUnitPrice() {
        return excelUnitPrice;
    }

    public void setExcelUnitPrice(BigDecimal excelUnitPrice) {
        this.excelUnitPrice = excelUnitPrice;
    }

    public boolean isExcelIsTaxInclued() {
        return excelIsTaxInclued;
    }

    public void setExcelIsTaxInclued(boolean excelIsTaxInclued) {
        this.excelIsTaxInclued = excelIsTaxInclued;
    }

    public BigDecimal getProfitPrice() {
        return profitPrice;
    }

    public void setProfitPrice(BigDecimal profitPrice) {
        this.profitPrice = profitPrice;
    }

    public BigDecimal getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(BigDecimal profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public BigDecimal getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(BigDecimal oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public boolean isIsDiscountRate2() {
        return isDiscountRate2;
    }

    public void setIsDiscountRate2(boolean isDiscountRate2) {
        this.isDiscountRate2 = isDiscountRate2;
    }

    public BigDecimal getDiscountRate2() {
        return discountRate2;
    }

    public void setDiscountRate2(BigDecimal discountRate2) {
        this.discountRate2 = discountRate2;
    }

    public BigDecimal getDiscountPrice2() {
        return discountPrice2;
    }

    public void setDiscountPrice2(BigDecimal discountPrice2) {
        this.discountPrice2 = discountPrice2;
    }

    public boolean isIsFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public InvoiceItem getPriceDifferentInvoiceItem() {
        return priceDifferentInvoiceItem;
    }

    public void setPriceDifferentInvoiceItem(InvoiceItem priceDifferentInvoiceItem) {
        this.priceDifferentInvoiceItem = priceDifferentInvoiceItem;
    }

    public BigDecimal getPriceDifferentTotalMoney() {
        return priceDifferentTotalMoney;
    }

    public void setPriceDifferentTotalMoney(BigDecimal priceDifferentTotalMoney) {
        this.priceDifferentTotalMoney = priceDifferentTotalMoney;
    }

    public boolean isIsNotCalcTotalPrice() {
        return isNotCalcTotalPrice;
    }

    public void setIsNotCalcTotalPrice(boolean isNotCalcTotalPrice) {
        this.isNotCalcTotalPrice = isNotCalcTotalPrice;
    }

    public BigDecimal getControlQuantity() {
        return controlQuantity;
    }

    public void setControlQuantity(BigDecimal controlQuantity) {
        this.controlQuantity = controlQuantity;
    }

    public String getOrderItemQuantitys() {
        return orderItemQuantitys;
    }

    public void setOrderItemQuantitys(String orderItemQuantitys) {
        this.orderItemQuantitys = orderItemQuantitys;
    }

    public String getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderItemIds(String orderItemIds) {
        this.orderItemIds = orderItemIds;
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

    public BigDecimal getNewUnitPrice() {
        return newUnitPrice;
    }

    public void setNewUnitPrice(BigDecimal newUnitPrice) {
        this.newUnitPrice = newUnitPrice;
    }

    public BigDecimal getOldUnitPrice() {
        return oldUnitPrice;
    }

    public void setOldUnitPrice(BigDecimal oldUnitPrice) {
        this.oldUnitPrice = oldUnitPrice;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }

    public int getIsFuelWarehouse() {
        return isFuelWarehouse;
    }

    public void setIsFuelWarehouse(int isFuelWarehouse) {
        this.isFuelWarehouse = isFuelWarehouse;
    }

    public int getIsFuelWarehouseItem() {
        return isFuelWarehouseItem;
    }

    public void setIsFuelWarehouseItem(int isFuelWarehouseItem) {
        this.isFuelWarehouseItem = isFuelWarehouseItem;
    }

    public BigDecimal getExcelUnitPriceTaxExcluded() {
        return excelUnitPriceTaxExcluded;
    }

    public void setExcelUnitPriceTaxExcluded(BigDecimal excelUnitPriceTaxExcluded) {
        this.excelUnitPriceTaxExcluded = excelUnitPriceTaxExcluded;
    }

    public String getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    public BigDecimal getRedemption() {
        return redemption;
    }

    public void setRedemption(BigDecimal redemption) {
        this.redemption = redemption;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
