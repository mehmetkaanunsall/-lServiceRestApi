/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Waybill extends WotLogging {

    private int id;
    private boolean isPurchase;
    private Account account;
    private String documentNumber;
    private Date waybillDate;
    private Date dispatchDate;
    private String dispatchAddress;
    private String description;
    private Type type;
    private Status status;
    private String deliveryPerson;
    private Warehouse warehouse;
    private DocumentNumber dNumber;
    private String documentSerial;

    private String warehouseIdList;
    private String warehouseNameList;
    private List<Warehouse> listOfWarehouse;
    private String jsonWarehouses;

    private boolean isInvoice;//irsaliye faturaya bağlı ise true gelir.
    private boolean isWaybillInvoice;
    private BranchSetting branchSetting;

    private boolean sapLogİsSend;//İrsaliye Sap ye başarılı olarak gönderilmiş mi

    private boolean isOrderConnection; //sipariş ile bağlantısı var ise true olur.
    private String orderIds;
    private boolean isFuel;

    public Waybill() {
        this.account = new Account();
        this.type = new Type();
        this.status = new Status();
        this.warehouse = new Warehouse();
        this.dNumber = new DocumentNumber();
        this.listOfWarehouse = new ArrayList<>();
        this.branchSetting = new BranchSetting();
    }

    public String getDocumentSerial() {
        return documentSerial;
    }

    public void setDocumentSerial(String documentSerial) {
        this.documentSerial = documentSerial;
    }

    public DocumentNumber getdNumber() {
        return dNumber;
    }

    public void setdNumber(DocumentNumber dNumber) {
        this.dNumber = dNumber;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public boolean isIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(boolean isInvoice) {
        this.isInvoice = isInvoice;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Waybill(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getWaybillDate() {
        return waybillDate;
    }

    public void setWaybillDate(Date waybillDate) {
        this.waybillDate = waybillDate;
    }

    public Date getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getDispatchAddress() {
        return dispatchAddress;
    }

    public void setDispatchAddress(String dispatchAddress) {
        this.dispatchAddress = dispatchAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(String deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public String getWarehouseIdList() {
        return warehouseIdList;
    }

    public void setWarehouseIdList(String warehouseIdList) {
        this.warehouseIdList = warehouseIdList;
    }

    public String getWarehouseNameList() {
        return warehouseNameList;
    }

    public void setWarehouseNameList(String warehouseNameList) {
        this.warehouseNameList = warehouseNameList;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public String getJsonWarehouses() {
        return jsonWarehouses;
    }

    public void setJsonWarehouses(String jsonWarehouses) {
        this.jsonWarehouses = jsonWarehouses;
    }

    public boolean isIsWaybillInvoice() {
        return isWaybillInvoice;
    }

    public void setIsWaybillInvoice(boolean isWaybillInvoice) {
        this.isWaybillInvoice = isWaybillInvoice;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public boolean isSapLogİsSend() {
        return sapLogİsSend;
    }

    public void setSapLogİsSend(boolean sapLogİsSend) {
        this.sapLogİsSend = sapLogİsSend;
    }

    public boolean isIsOrderConnection() {
        return isOrderConnection;
    }

    public void setIsOrderConnection(boolean isOrderConnection) {
        this.isOrderConnection = isOrderConnection;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }

    public boolean isIsFuel() {
        return isFuel;
    }

    public void setIsFuel(boolean isFuel) {
        this.isFuel = isFuel;
    }

    @Override
    public String toString() {
        return this.getDocumentNumber();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
