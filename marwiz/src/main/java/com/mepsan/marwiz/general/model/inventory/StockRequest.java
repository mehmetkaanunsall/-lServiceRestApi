/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.04.2018 04:41:04
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Size;

public class StockRequest {

    private int id;
    private Date processDate;
    @Size(max = 50)
    private String barcode;
    @Size(max = 100)
    private String name;
    private Unit unit;
    @Size(max = 80)
    private String brand;
    private boolean isService;
    private String description;
    private int approval;
    private Date approvalDate;
    private BigDecimal price;
    private Currency currency;
    private TaxGroup saleTaxGroup;
    private TaxGroup purchaseTaxGroup;
    private Country country;
    private int approvalCenterStockId;
    private Stock approvalStock;
    private String code;
    private BigDecimal weight;
    private Unit weightUnit;

    private boolean isCentralIntegration; //veritabanında yok kontrol için/

    public StockRequest() {
        this.unit = new Unit();
        this.currency = new Currency();
        this.saleTaxGroup = new TaxGroup();
        this.purchaseTaxGroup = new TaxGroup();
        this.country = new Country();
        this.approvalStock = new Stock();
        this.weightUnit = new Unit();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
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

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isIsCentralIntegration() {
        return isCentralIntegration;
    }

    public void setIsCentralIntegration(boolean isCentralIntegration) {
        this.isCentralIntegration = isCentralIntegration;
    }

    public TaxGroup getSaleTaxGroup() {
        return saleTaxGroup;
    }

    public void setSaleTaxGroup(TaxGroup saleTaxGroup) {
        this.saleTaxGroup = saleTaxGroup;
    }

    public TaxGroup getPurchaseTaxGroup() {
        return purchaseTaxGroup;
    }

    public void setPurchaseTaxGroup(TaxGroup purchaseTaxGroup) {
        this.purchaseTaxGroup = purchaseTaxGroup;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getApprovalCenterStockId() {
        return approvalCenterStockId;
    }

    public void setApprovalCenterStockId(int approvalCenterStockId) {
        this.approvalCenterStockId = approvalCenterStockId;
    }

    public Stock getApprovalStock() {
        return approvalStock;
    }

    public void setApprovalStock(Stock approvalStock) {
        this.approvalStock = approvalStock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Unit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(Unit weightUnit) {
        this.weightUnit = weightUnit;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
