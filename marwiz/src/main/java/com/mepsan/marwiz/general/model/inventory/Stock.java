/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 10:43:47
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class Stock extends WotLogging {

    private int id;
    private String barcode;
    private String code;
    private String centerProductCode;
    private String name;
    private Status status;
    private Unit unit;
    private Brand brand;
    private boolean isService;
    private String description;
    private int centerstock_id;
    private String category;
    private StockInfo stockInfo;
    private Country country;

    private PriceListItem purchasePriceListItem;
    private PriceListItem salePriceListItem;

    private Account supplier;
    private String supplierProductCode;
    private CentralSupplier centralSupplier;
    private String centralSupplierProductCode;
    private Boolean isServiceTemp;

    private StockEInvoiceUnitCon stockEInvoiceUnitCon;


    /*---------------tabloda yok stok sayfası için----------------*/
    private BigDecimal availableQuantity;
    private BigDecimal otherQuantity;
    private BigDecimal profitPercentage;
    private BigDecimal saleKdv;
    private BigDecimal purchaseKdv;

    private String alternativeBarcodes;

    private int excelDataType; // excelden stok aktarımında kaydın hatalı olup olmadığı bilgisini döndürür.
    private BigDecimal alternativeQuantity;
    private BigDecimal taxRate;
    private BigDecimal boxQuantity;

    private int stockType_id;
    private String categoryName;
    private Boolean is_get;

    public Stock() {
        this.status = new Status();
        this.brand = new Brand();
        this.unit = new Unit();
        this.stockInfo = new StockInfo();
        this.country = new Country();
        this.supplier = new Account();
        this.centralSupplier = new CentralSupplier();
        this.stockEInvoiceUnitCon = new StockEInvoiceUnitCon();
    }

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Stock(int id) {
        this.id = id;
    }

    public int getCenterstock_id() {
        return centerstock_id;
    }

    public void setCenterstock_id(int centerstock_id) {
        this.centerstock_id = centerstock_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public boolean isIsService() {
        return isService;
    }

    public void setIsService(boolean isService) {
        this.isService = isService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCenterProductCode() {
        return centerProductCode;
    }

    public void setCenterProductCode(String centerProductCode) {
        this.centerProductCode = centerProductCode;
    }

    public BigDecimal getSaleKdv() {
        return saleKdv;
    }

    public void setSaleKdv(BigDecimal saleKdv) {
        this.saleKdv = saleKdv;
    }

    public BigDecimal getPurchaseKdv() {
        return purchaseKdv;
    }

    public void setPurchaseKdv(BigDecimal purchaseKdv) {
        this.purchaseKdv = purchaseKdv;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public StockInfo getStockInfo() {
        return stockInfo;
    }

    public void setStockInfo(StockInfo stockInfo) {
        this.stockInfo = stockInfo;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public PriceListItem getPurchasePriceListItem() {
        return purchasePriceListItem;
    }

    public void setPurchasePriceListItem(PriceListItem purchasePriceListItem) {
        this.purchasePriceListItem = purchasePriceListItem;
    }

    public PriceListItem getSalePriceListItem() {
        return salePriceListItem;
    }

    public void setSalePriceListItem(PriceListItem salePriceListItem) {
        this.salePriceListItem = salePriceListItem;
    }

    public Account getSupplier() {
        return supplier;
    }

    public void setSupplier(Account supplier) {
        this.supplier = supplier;
    }

    public String getSupplierProductCode() {
        return supplierProductCode;
    }

    public CentralSupplier getCentralSupplier() {
        return centralSupplier;
    }

    public void setCentralSupplier(CentralSupplier centralSupplier) {
        this.centralSupplier = centralSupplier;
    }

    public String getCentralSupplierProductCode() {
        return centralSupplierProductCode;
    }

    public void setCentralSupplierProductCode(String centralSupplierProductCode) {
        this.centralSupplierProductCode = centralSupplierProductCode;
    }

    public void setSupplierProductCode(String supplierProductCode) {
        this.supplierProductCode = supplierProductCode;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(BigDecimal profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

    public BigDecimal getOtherQuantity() {
        return otherQuantity;
    }

    public void setOtherQuantity(BigDecimal otherQuantity) {
        this.otherQuantity = otherQuantity;
    }

    public String getAlternativeBarcodes() {
        return alternativeBarcodes;
    }

    public void setAlternativeBarcodes(String alternativeBarcodes) {
        this.alternativeBarcodes = alternativeBarcodes;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public BigDecimal getAlternativeQuantity() {
        return alternativeQuantity;
    }

    public void setAlternativeQuantity(BigDecimal alternativeQuantity) {
        this.alternativeQuantity = alternativeQuantity;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(BigDecimal boxQuantity) {
        this.boxQuantity = boxQuantity;
    }

    public int getStockType_id() {
        return stockType_id;
    }

    public void setStockType_id(int stockType_id) {
        this.stockType_id = stockType_id;
    }

    public Boolean getIs_get() {
        return is_get;
    }

    public void setIs_get(Boolean is_get) {
        this.is_get = is_get;
    }

    public StockEInvoiceUnitCon getStockEInvoiceUnitCon() {
        return stockEInvoiceUnitCon;
    }

    public void setStockEInvoiceUnitCon(StockEInvoiceUnitCon stockEInvoiceUnitCon) {
        this.stockEInvoiceUnitCon = stockEInvoiceUnitCon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getIsServiceTemp() {
        return isServiceTemp;
    }

    public void setIsServiceTemp(Boolean isServiceTemp) {
        this.isServiceTemp = isServiceTemp;
    }

    @Override
    public int hashCode() {
        return this.getId();
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
        final Stock other = (Stock) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
