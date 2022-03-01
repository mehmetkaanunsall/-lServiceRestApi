/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 08:50:08
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockTaking extends WotLogging {

    private int id;
    private Branch branch;
    private String name;
    private Warehouse warehouse;
    private WarehouseReceipt warehouseReceipt;
    private Date beginDate;
    private Date endDate;
    private Status status;
    private String description;
    private Account takingEmployee;
    private Account approvalEmployee;
    private PriceList priceList;
    private boolean isTaxIncluded;
    private String realTakingQuantity;
    private String realTakingPrice;
    private String systemTakingQuantity;
    private String systemTakingPrice;
    private String diffQuantity;
    private String diffPrice;
    private BigDecimal differencePrice;
    private Currency currency;
    private boolean isControl;
    private boolean isRetrospective;
    
    private String categories; 
    private List<Categorization> listOfCategorization;

    private String jsonItems;

    public StockTaking() {
        this.branch = new Branch();
        this.warehouse = new Warehouse();
        this.warehouseReceipt = new WarehouseReceipt();
        this.status = new Status();
        this.takingEmployee = new Account();
        this.approvalEmployee = new Account();
        this.priceList = new PriceList();
        this.currency=new Currency();
        this.listOfCategorization=new ArrayList<>();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public WarehouseReceipt getWarehouseReceipt() {
        return warehouseReceipt;
    }

    public void setWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        this.warehouseReceipt = warehouseReceipt;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJsonItems() {
        return jsonItems;
    }

    public void setJsonItems(String jsonItems) {
        this.jsonItems = jsonItems;
    }

    public Account getTakingEmployee() {
        return takingEmployee;
    }

    public void setTakingEmployee(Account takingEmployee) {
        this.takingEmployee = takingEmployee;
    }

    public Account getApprovalEmployee() {
        return approvalEmployee;
    }

    public void setApprovalEmployee(Account approvalEmploye) {
        this.approvalEmployee = approvalEmploye;
    }

    public boolean isIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public String getRealTakingQuantity() {
        return realTakingQuantity;
    }

    public void setRealTakingQuantity(String realTakingQuantity) {
        this.realTakingQuantity = realTakingQuantity;
    }

    public String getRealTakingPrice() {
        return realTakingPrice;
    }

    public void setRealTakingPrice(String realTakingPrice) {
        this.realTakingPrice = realTakingPrice;
    }

    public String getSystemTakingQuantity() {
        return systemTakingQuantity;
    }

    public void setSystemTakingQuantity(String systemTakingQuantity) {
        this.systemTakingQuantity = systemTakingQuantity;
    }

    public String getSystemTakingPrice() {
        return systemTakingPrice;
    }

    public void setSystemTakingPrice(String systemTakingPrice) {
        this.systemTakingPrice = systemTakingPrice;
    }

    public String getDiffQuantity() {
        return diffQuantity;
    }

    public void setDiffQuantity(String diffQuantity) {
        this.diffQuantity = diffQuantity;
    }

    public String getDiffPrice() {
        return diffPrice;
    }

    public void setDiffPrice(String diffPrice) {
        this.diffPrice = diffPrice;
    }

    public BigDecimal getDifferencePrice() {
        return differencePrice;
    }

    public void setDifferencePrice(BigDecimal differencePrice) {
        this.differencePrice = differencePrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public boolean isIsControl() {
        return isControl;
    }

    public void setIsControl(boolean isControl) {
        this.isControl = isControl;
    }

    public boolean isIsRetrospective() {
        return isRetrospective;
    }

    public void setIsRetrospective(boolean isRetrospective) {
        this.isRetrospective = isRetrospective;
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
