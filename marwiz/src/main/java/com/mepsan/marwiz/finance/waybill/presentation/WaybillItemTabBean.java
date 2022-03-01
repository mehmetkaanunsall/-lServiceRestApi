/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 06.04.2017 11:33:48
 */
package com.mepsan.marwiz.finance.waybill.presentation;

import com.google.gson.JsonArray;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillItemService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class WaybillItemTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{waybillItemService}") // session
    public IWaybillItemService waybillItemService;

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    public StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{waybillProcessBean}")
    private WaybillProcessBean waybillProcessBean;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    @ManagedProperty(value = "#{stockAlternativeUnitService}")
    private IStockAlternativeUnitService stockAlternativeUnitService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Waybill selectedWaybill;
    private List<WaybillItem> listOfObjects;
    private WaybillItem selectedObject, deleteItem;
    private CheckDelete checkDelete;
    private int processType;
    private String deleteControlMessage = "";
    private String deleteControlMessage1 = "";
    private String deleteControlMessage2 = "";
    private String relatedRecord = "";
    private BigDecimal productAmount;
    private String productBarcode;
    private List<WaybillItem> listOfProduct;
    private List<StockUnitConnection> listOfUnit;
    private String fileNames;
    private String fileName;
    private boolean isOpenSaveBtn, isOpenCancelBtn, isOpenTransferBtn, isOpenErrorData;
    private UploadedFile uploadedFile;
    private List<WaybillItem> excelStockList;
    private List<WaybillItem> tempStockList;
    private List<WaybillItem> tempProductList;
    private List<WaybillItem> sampleList;
    private WaybillItem waybillItem;
    private List<ErrorItem> errorList;
    private BigDecimal oldQuantity;
    private boolean isMinStockLevel;
    private boolean isMaxStockLevel;

    private boolean isCreateWaybillFromOrder;
    private boolean isOrderConnection;

    private List<Warehouse> listOfFuelWarehouse;
    private boolean isListFuelWarehouse;

    private String bookType;

    private boolean isPurchaseMinStockLevel;
    private boolean isSalesMaxStockLevel;

    public List<StockUnitConnection> getListOfUnit() {
        return listOfUnit;
    }

    public void setListOfUnit(List<StockUnitConnection> listOfUnit) {
        this.listOfUnit = listOfUnit;
    }

    public String getRelatedRecord() {
        return relatedRecord;
    }

    public void setStockAlternativeUnitService(IStockAlternativeUnitService stockAlternativeUnitService) {
        this.stockAlternativeUnitService = stockAlternativeUnitService;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setRelatedRecord(String relatedRecord) {
        this.relatedRecord = relatedRecord;
    }

    public String getDeleteControlMessage() {
        return deleteControlMessage;
    }

    public void setDeleteControlMessage(String deleteControlMessage) {
        this.deleteControlMessage = deleteControlMessage;
    }

    public String getDeleteControlMessage1() {
        return deleteControlMessage1;
    }

    public void setDeleteControlMessage1(String deleteControlMessage1) {
        this.deleteControlMessage1 = deleteControlMessage1;
    }

    public String getDeleteControlMessage2() {
        return deleteControlMessage2;
    }

    public void setDeleteControlMessage2(String deleteControlMessage2) {
        this.deleteControlMessage2 = deleteControlMessage2;
    }

    public CheckDelete getCheckDelete() {
        return checkDelete;
    }

    public void setCheckDelete(CheckDelete checkDelete) {
        this.checkDelete = checkDelete;
    }

    public void setWaybillProcessBean(WaybillProcessBean waybillProcessBean) {
        this.waybillProcessBean = waybillProcessBean;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWaybillItemService(IWaybillItemService waybillItemService) {
        this.waybillItemService = waybillItemService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public Waybill getSelectedWaybill() {
        return selectedWaybill;
    }

    public void setSelectedWaybill(Waybill selectedWaybill) {
        this.selectedWaybill = selectedWaybill;
    }

    public List<WaybillItem> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<WaybillItem> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public WaybillItem getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WaybillItem selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public BigDecimal getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(BigDecimal productAmount) {
        this.productAmount = productAmount;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public List<WaybillItem> getListOfProduct() {
        return listOfProduct;
    }

    public void setListOfProduct(List<WaybillItem> listOfProduct) {
        this.listOfProduct = listOfProduct;
    }

    public WaybillItem getDeleteItem() {
        return deleteItem;
    }

    public void setDeleteItem(WaybillItem deleteItem) {
        this.deleteItem = deleteItem;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIsOpenSaveBtn() {
        return isOpenSaveBtn;
    }

    public void setIsOpenSaveBtn(boolean isOpenSaveBtn) {
        this.isOpenSaveBtn = isOpenSaveBtn;
    }

    public boolean isIsOpenCancelBtn() {
        return isOpenCancelBtn;
    }

    public void setIsOpenCancelBtn(boolean isOpenCancelBtn) {
        this.isOpenCancelBtn = isOpenCancelBtn;
    }

    public boolean isIsOpenTransferBtn() {
        return isOpenTransferBtn;
    }

    public void setIsOpenTransferBtn(boolean isOpenTransferBtn) {
        this.isOpenTransferBtn = isOpenTransferBtn;
    }

    public boolean isIsOpenErrorData() {
        return isOpenErrorData;
    }

    public void setIsOpenErrorData(boolean isOpenErrorData) {
        this.isOpenErrorData = isOpenErrorData;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<WaybillItem> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<WaybillItem> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public List<WaybillItem> getTempStockList() {
        return tempStockList;
    }

    public void setTempStockList(List<WaybillItem> tempStockList) {
        this.tempStockList = tempStockList;
    }

    public List<WaybillItem> getTempProductList() {
        return tempProductList;
    }

    public void setTempProductList(List<WaybillItem> tempProductList) {
        this.tempProductList = tempProductList;
    }

    public List<WaybillItem> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<WaybillItem> sampleList) {
        this.sampleList = sampleList;
    }

    public WaybillItem getWaybillItem() {
        return waybillItem;
    }

    public void setWaybillItem(WaybillItem waybillItem) {
        this.waybillItem = waybillItem;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public BigDecimal getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(BigDecimal oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public boolean isIsMinStockLevel() {
        return isMinStockLevel;
    }

    public void setIsMinStockLevel(boolean isMinStockLevel) {
        this.isMinStockLevel = isMinStockLevel;
    }

    public boolean isIsMaxStockLevel() {
        return isMaxStockLevel;
    }

    public void setIsMaxStockLevel(boolean isMaxStockLevel) {
        this.isMaxStockLevel = isMaxStockLevel;
    }

    public boolean isIsCreateWaybillFromOrder() {
        return isCreateWaybillFromOrder;
    }

    public void setIsCreateWaybillFromOrder(boolean isCreateWaybillFromOrder) {
        this.isCreateWaybillFromOrder = isCreateWaybillFromOrder;
    }

    public boolean isIsOrderConnection() {
        return isOrderConnection;
    }

    public void setIsOrderConnection(boolean isOrderConnection) {
        this.isOrderConnection = isOrderConnection;
    }

    public List<Warehouse> getListOfFuelWarehouse() {
        return listOfFuelWarehouse;
    }

    public void setListOfFuelWarehouse(List<Warehouse> listOfFuelWarehouse) {
        this.listOfFuelWarehouse = listOfFuelWarehouse;
    }

    public boolean isIsListFuelWarehouse() {
        return isListFuelWarehouse;
    }

    public void setIsListFuelWarehouse(boolean isListFuelWarehouse) {
        this.isListFuelWarehouse = isListFuelWarehouse;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public boolean isIsPurchaseMinStockLevel() {
        return isPurchaseMinStockLevel;
    }

    public void setIsPurchaseMinStockLevel(boolean isPurchaseMinStockLevel) {
        this.isPurchaseMinStockLevel = isPurchaseMinStockLevel;
    }

    public boolean isIsSalesMaxStockLevel() {
        return isSalesMaxStockLevel;
    }

    public void setIsSalesMaxStockLevel(boolean isSalesMaxStockLevel) {
        this.isSalesMaxStockLevel = isSalesMaxStockLevel;
    }

    @PostConstruct
    public void init() {
        System.out.println("------------WaybillItemBean");
        selectedWaybill = waybillProcessBean.getSelectedObject();
        listOfFuelWarehouse = new ArrayList<>();
        isListFuelWarehouse = false;
        if (selectedWaybill.getId() > 0) {
            listOfObjects = waybillItemService.listWaybillItem(selectedWaybill);
        } else {
            listOfObjects = new ArrayList<>();
        }

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Waybill) {
                    isCreateWaybillFromOrder = false;
                }

                if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {
                    isCreateWaybillFromOrder = true;
                }

                if (((ArrayList) sessionBean.parameter).get(i) instanceof JsonArray) {//siparişten irsaliye oluşturulacak ise JsonArray olarak sipariş itemları gelir.
                    listOfObjects = new ArrayList<>();
                    if (isCreateWaybillFromOrder) {

                        JsonArray jArray = (JsonArray) ((ArrayList) sessionBean.parameter).get(i);
                        for (int j = 0; j < jArray.size(); j++) {

                            boolean isThere = false;
                            for (WaybillItem listOfObject : listOfObjects) {
                                if (listOfObject.getStock().getId() == jArray.get(j).getAsJsonObject().get("stock_id").getAsInt()) {
                                    isThere = true;
                                    listOfObject.setControlQuantity(listOfObject.getControlQuantity().add(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal()));
                                    listOfObject.setOrderIds(listOfObject.getOrderIds() + "," + jArray.get(j).getAsJsonObject().get("order_id").getAsString());
                                    listOfObject.setOrderItemIds(listOfObject.getOrderItemIds() + "," + jArray.get(j).getAsJsonObject().get("id").getAsString());
                                    listOfObject.setOrderItemQuantitys(listOfObject.getOrderItemQuantitys() + "," + jArray.get(j).getAsJsonObject().get("quantity").getAsString());
                                }
                            }

                            if (!isThere) {
                                WaybillItem waybillItem = new WaybillItem();
                                waybillItem.setId(jArray.get(j).getAsJsonObject().get("id").getAsInt());//gereksiz datatable da eşsiz olsun diye atadık.
                                waybillItem.getStock().setId(jArray.get(j).getAsJsonObject().get("stock_id").getAsInt());
                                waybillItem.getStock().setName(jArray.get(j).getAsJsonObject().get("stock_name").getAsString());
                                waybillItem.getStock().setBarcode(jArray.get(j).getAsJsonObject().get("stock_barcode").getAsString());
                                waybillItem.getStock().setCenterProductCode(jArray.get(j).getAsJsonObject().get("stock_centerproductcode").getAsString());
                                waybillItem.getStock().getUnit().setId(jArray.get(j).getAsJsonObject().get("unit_id").getAsInt());
                                waybillItem.getStock().getUnit().setSortName(jArray.get(j).getAsJsonObject().get("unit_sortname").getAsString());
                                waybillItem.getStock().getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unitrounding").getAsInt());
                                waybillItem.getStock().getUnit().setName(jArray.get(j).getAsJsonObject().get("unit_name").getAsString());
                                waybillItem.setControlQuantity(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal());
                                waybillItem.setDescription("");
                                waybillItem.setOrderIds(jArray.get(j).getAsJsonObject().get("order_id").getAsString());
                                waybillItem.setOrderItemIds(jArray.get(j).getAsJsonObject().get("id").getAsString());//waybillitem id bilgisi
                                waybillItem.setOrderItemQuantitys(jArray.get(j).getAsJsonObject().get("quantity").getAsString());
                                waybillItem.getStock().getStockInfo().setCurrentSalePrice(jArray.get(j).getAsJsonObject().get("currenctsaleprice").getAsBigDecimal());
                                waybillItem.getStock().getStockInfo().getCurrentSaleCurrency().setId(jArray.get(j).getAsJsonObject().get("currenctsalecurrency").getAsInt());
                                waybillItem.getStock().getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unitrounding").getAsInt());

                                waybillItem.getWaybill().getBranchSetting().getBranch().setId(selectedWaybill.getBranchSetting().getBranch().getId());
                                waybillItem.getWaybill().getBranchSetting().setIsCentralIntegration(selectedWaybill.getBranchSetting().isIsCentralIntegration());
                                waybillItem.getWaybill().getBranchSetting().setIsInvoiceStockSalePriceList(selectedWaybill.getBranchSetting().isIsInvoiceStockSalePriceList());
                                waybillItem.getWaybill().getBranchSetting().getBranch().getCurrency().setId(selectedWaybill.getBranchSetting().getBranch().getCurrency().getId());
                                waybillItem.getWaybill().setIsPurchase(true);

                                //Ürün işlemlerinde sipariş kalan miktarı alanını formatlamak için
                                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                                numberFormat.setMaximumFractionDigits(waybillItem.getStock().getUnit().getUnitRounding());
                                numberFormat.setMinimumFractionDigits(waybillItem.getStock().getUnit().getUnitRounding());
                                numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

                                DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
                                decimalFormatSymbols.setCurrencySymbol("");
                                ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

                                waybillItem.setOrderRemainingQuantity(numberFormat.format(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal()));

                                listOfObjects.add(waybillItem);
                            }
                        }

                        waybillProcessBean.setIsCreateWaybill(false);

                        NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                        formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
                        DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
                        decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
                        decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
                        decimalFormatSymbolsUnit.setCurrencySymbol("");
                        ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

                        for (WaybillItem waybillItem1 : listOfObjects) {

                            waybillItem1.setFirstOrderIds(waybillItem1.getOrderIds());
                            waybillItem1.setFirstOrderItemIds(waybillItem1.getOrderItemIds());
                            waybillItem1.setFirstOrderItemQuantitys(waybillItem1.getOrderItemQuantitys());

                            formatterUnit.setMaximumFractionDigits(waybillItem1.getStock().getUnit().getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(waybillItem1.getStock().getUnit().getUnitRounding());
                            waybillItem1.setOrderRemainingQuantity(formatterUnit.format(waybillItem1.getControlQuantity()));

                        }

                        waybillProcessBean.getListOfItemForOrder().clear();
                        waybillProcessBean.getListOfItemForOrder().addAll(listOfObjects);

                    }

                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{15, 16, 17}, 0));
        isMinStockLevel = false;
        isMaxStockLevel = false;
        oldQuantity = BigDecimal.ZERO;

    }

    public void createDialog(int type) {
        isMinStockLevel = false;
        isMaxStockLevel = false;
        processType = type;
        listOfUnit = new ArrayList<>();
        isOrderConnection = false;

        if (processType == 1) {
            oldQuantity = BigDecimal.ZERO;
            selectedObject = new WaybillItem();
            bringStockType();
        } else {
            if (selectedWaybill.isIsFuel()) {
                listOfFuelWarehouse.add(selectedObject.getWarehouse());
            }

            oldQuantity = selectedObject.getQuantity();//Hızlı stok eklemede stok seviyesi kontrolünde kullanılır
            listOfUnit.add(0, new StockUnitConnection(selectedObject.getStock(), selectedObject.getStock().getUnit(), 1));
            if (selectedObject.getOrderItemIds() != null && !"".equals(selectedObject.getOrderItemIds())) {
                isOrderConnection = true;
            }

        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_waybillitem').show();");
    }

    public void save() {
        if (sessionBean.isPeriodClosed(selectedWaybill.getWaybillDate())) {

//            int isFuelWarehouseItem = 1;
//            int isFuelWarehouse = 1;
//            if (selectedWaybill.getWarehouseIdList() != null && !selectedWaybill.getWarehouseIdList().equals("")) {
//                isFuelWarehouse = invoiceService.controlAutomationWarehouse(selectedWaybill.getWarehouseIdList());
//                if (isFuelWarehouse == 1) {
//                    isFuelWarehouseItem = invoiceService.controlTankItemAvailable(selectedWaybill.getWarehouseIdList(), selectedObject.getStock());
//                }
//            }
//
//            if (isFuelWarehouseItem == 1) {
            int result = 0;
            int sCount = 1;

            for (WaybillItem item : listOfObjects) {
                if (item.getStock().getId() == selectedObject.getStock().getId()) {
                    sCount++;
                }
            }
            selectedObject.setStockCount(sCount);

            selectedObject.setWaybill(selectedWaybill);

            if (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel()) {  //Stok kartında "Stok eksi bakiyeye düşebilir mi" seçili değil ise
                stockLevelControl(processType, selectedObject);
            }
            if (((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && selectedObject.getStock().getStockInfo().getMaxStockLevel() != null) { // Satın alma işlemlerinde stok max seviyesi üzerinde alım yapılamaması için kontrol edilir
                maxStockLevelControl(processType, selectedObject);
            }

            if (selectedObject.getQuantity() != null && selectedObject.getQuantity().doubleValue() <= 0) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        sessionBean.loc.getString("warning"),
                        sessionBean.loc.getString("quantitycannnotbezero"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            } else if (selectedObject.getControlQuantity() != null && selectedObject.getQuantity().compareTo(selectedObject.getControlQuantity()) == 1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannotbegreaterthanorderamount") + " " + sessionBean.loc.getString("orderquantity") + ":" + selectedObject.getControlQuantity());
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");

            } else {
                if (isCreateWaybillFromOrder) {
                    selectedObject.setStockCount(1);
                    if (processType == 1) {
                        listOfObjects.add(selectedObject);
                    }

                    RequestContext.getCurrentInstance().execute("PF('dlg_waybillitem').hide()");
                    //tüm ürünlerin para birimi seçildi ise yani tüm alanlar dolduruldu ise json stringi oluştur.
                    //tüm ürünlerin tüm alanları doldurulmadı ise fatura oluşturmasına izin verme
                    waybillProcessBean.setIsCreateWaybill(true);
                    for (WaybillItem waybillItem1 : listOfObjects) {
                        if (waybillItem1.getOrderItemIds() != null && !waybillItem1.getOrderItemIds().equals("")) {
                            if (waybillItem1.getQuantity() == null || waybillItem1.getQuantity().compareTo(BigDecimal.ZERO) <= 0 || waybillItem1.getQuantity().compareTo(waybillItem1.getControlQuantity()) == 1) {
                                waybillProcessBean.setIsCreateWaybill(false);
                                break;
                            }
                        }
                    }
                    if (selectedObject.getOrderItemIds() != null && !selectedObject.getOrderItemIds().equals("")) {
                        controlOrderQuantity();
                    }

                    for (Iterator<WaybillItem> iterator = waybillProcessBean.getListOfItemForOrder().iterator(); iterator.hasNext();) {
                        WaybillItem next = iterator.next();
                        if (next.getStock().getId() == selectedObject.getStock().getId()) {
                            iterator.remove();
                            break;
                        }
                    }
                    waybillProcessBean.getListOfItemForOrder().add(selectedObject);
                    RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab");
                    RequestContext.getCurrentInstance().update("frmItemsProcess:pgrItemsProcess");
                } else {

                    if (isOrderConnection) {
                        if (selectedObject.getOrderItemIds() != null && !selectedObject.getOrderItemIds().equals("")) {
                            controlOrderQuantityForUpdate();
                        }
                    }
                    if (selectedWaybill.isIsFuel()) {
                        selectedWaybill.getListOfWarehouse().clear();
                    }
                    if (processType == 1) {

                        if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {

                            result = waybillItemService.create(selectedObject);
                            if (result > 0) {
                                listOfObjects = waybillItemService.listWaybillItem(selectedWaybill);
                            }
                        }
                    } else if (processType == 2) {

                        if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                            result = waybillItemService.update(selectedObject);
                        }

                    }

                    if (result != 0) {

                        if (result > 0) {

                            if (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel() && (!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 21) || (selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) {
                                selectedObject.getStock().setAvailableQuantity(selectedObject.getStock().getAvailableQuantity().subtract(selectedObject.getQuantity().subtract(oldQuantity)));
                            } else if (((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && selectedObject.getStock().getStockInfo().getMaxStockLevel() != null) {
                                if (selectedObject.getQuantity().compareTo(oldQuantity) == -1) {
                                    selectedObject.getStock().getStockInfo().setBalance(selectedObject.getStock().getStockInfo().getBalance().subtract(selectedObject.getQuantity().subtract(oldQuantity)));
                                } else {
                                    selectedObject.getStock().getStockInfo().setBalance(selectedObject.getStock().getStockInfo().getBalance().add(selectedObject.getQuantity().subtract(oldQuantity)));

                                }
                            }
                            RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab");
                            RequestContext.getCurrentInstance().execute("PF('dlg_waybillitem').hide();");
                            sessionBean.createUpdateMessage(result);
                        } else if (result == -101) {//faturaya aktarılmış miktardan küçük olamaz
                            FacesMessage message = new FacesMessage();
                            message.setSummary(sessionBean.loc.getString("warning"));
                            message.setDetail(sessionBean.loc.getString("cannotbeenteredsmallerthantheamounttransferredtotheinvoice"));
                            message.setSeverity(FacesMessage.SEVERITY_WARN);
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext context = RequestContext.getCurrentInstance();
                            context.update("grwProcessMessage");
                        } else {
                            sessionBean.createUpdateMessage(result);
                        }
                    }

                }
            }
//            } else {
//                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
//                        sessionBean.loc.getString("warning"),
//                        sessionBean.loc.getString("differentstockcannotbeenteredintank"));
//                FacesContext.getCurrentInstance().addMessage(null, message);
//                RequestContext.getCurrentInstance().update("grwProcessMessage");
//            }

        }
    }

    public void controlOrderQuantity() {
        if (selectedObject.getQuantity() != null) {

            BigDecimal quant = selectedObject.getQuantity();
            String[] quantitys = selectedObject.getFirstOrderItemQuantitys().split(",");
            String[] orderitemids = selectedObject.getFirstOrderItemIds().split(",");
            String[] orderids = selectedObject.getFirstOrderIds().split(",");

            selectedObject.setOrderIds("");
            selectedObject.setOrderItemIds("");
            selectedObject.setOrderItemQuantitys("");

            for (int i = 0; i < quantitys.length; i++) {
                if (quant.compareTo(BigDecimal.ZERO) > 0) {

                    if (quant.compareTo(new BigDecimal(quantitys[i])) >= 0) {
                        selectedObject.setOrderIds(selectedObject.getOrderIds() + orderids[i] + ",");
                        selectedObject.setOrderItemIds(selectedObject.getOrderItemIds() + orderitemids[i] + ",");
                        selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys() + quantitys[i] + ",");
                        quant = quant.subtract(new BigDecimal(quantitys[i]));

                    } else {
                        selectedObject.setOrderIds(selectedObject.getOrderIds() + orderids[i] + ",");
                        selectedObject.setOrderItemIds(selectedObject.getOrderItemIds() + orderitemids[i] + ",");
                        selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys() + quant.toString() + ",");
                        break;
                    }
                }
            }

            if (!selectedObject.getOrderIds().equals("")) {
                selectedObject.setOrderIds(selectedObject.getOrderIds().substring(0, selectedObject.getOrderIds().length() - 1));
            }

            if (!selectedObject.getOrderItemIds().equals("")) {
                selectedObject.setOrderItemIds(selectedObject.getOrderItemIds().substring(0, selectedObject.getOrderItemIds().length() - 1));
            }

            if (!selectedObject.getOrderItemQuantitys().equals("")) {
                selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys().substring(0, selectedObject.getOrderItemQuantitys().length() - 1));
            }

            System.out.println("selectedObject getOrderIds" + selectedObject.getOrderIds());
            System.out.println("selectedObject getOrderItemIds" + selectedObject.getOrderItemIds());
            System.out.println("selectedObject getOrderItemQuantitys" + selectedObject.getOrderItemQuantitys());

        }
    }

    public void controlOrderQuantityForUpdate() {
        if (selectedObject.getQuantity() != null) {

            BigDecimal quant = selectedObject.getQuantity();
            String[] quantitys = selectedObject.getFirstOrderItemQuantitys().split(",");
            String[] orderitemids = selectedObject.getFirstOrderItemIds().split(",");

            selectedObject.setOrderItemIds("");
            selectedObject.setOrderItemQuantitys("");

            for (int i = 0; i < quantitys.length; i++) {

                if (quant.compareTo(new BigDecimal(quantitys[i])) >= 0) {
                    selectedObject.setOrderItemIds(selectedObject.getOrderItemIds() + orderitemids[i] + ",");
                    selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys() + quantitys[i] + ",");
                    quant = quant.subtract(new BigDecimal(quantitys[i]));

                } else {
                    selectedObject.setOrderItemIds(selectedObject.getOrderItemIds() + orderitemids[i] + ",");
                    selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys() + quant.toString() + ",");
                    quant = BigDecimal.ZERO;
                }

            }

            if (!selectedObject.getOrderItemIds().equals("")) {
                selectedObject.setOrderItemIds(selectedObject.getOrderItemIds().substring(0, selectedObject.getOrderItemIds().length() - 1));
            }

            if (!selectedObject.getOrderItemQuantitys().equals("")) {
                selectedObject.setOrderItemQuantitys(selectedObject.getOrderItemQuantitys().substring(0, selectedObject.getOrderItemQuantitys().length() - 1));
            }

            System.out.println("selectedObject getOrderItemIds" + selectedObject.getOrderItemIds());
            System.out.println("selectedObject getOrderItemQuantitys" + selectedObject.getOrderItemQuantitys());

        }
    }

    /**
     * kitaptan stok secıldıgınde calısır
     *
     * @throws IOException
     */
    public void updateAllInformation() throws IOException {
        boolean isThere = false;

        for (WaybillItem s : listOfObjects) {
            if (stockBookFilterBean.getSelectedData().getId() == s.getStock().getId()) {//seçilen ürün listede varsa
                if (processType == 1 || (processType == 2 && selectedObject.getId() != s.getId())) {//ekleme ise veya güncellenen itemdan başka item da aynı ürün varsa
                    isThere = true;
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.loc.getString("warning"));
                    message.setDetail(sessionBean.loc.getString("thestockyouwanttoaddisavailable"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.update("grwProcessMessage");
                    break;
                }
            }
        }

        if (!isThere) {

            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            if (selectedWaybill.getType().getId() == 21 && selectedWaybill.isIsFuel()) {
                listOfFuelWarehouse = waybillItemService.findFuelStockWarehouse(selectedObject, selectedWaybill);

                if (!listOfFuelWarehouse.isEmpty()) {
                    if (listOfFuelWarehouse.size() == 1) {
                        selectedObject.setWarehouse(listOfFuelWarehouse.get(0));
                        selectedObject.getStock().getStockInfo().setMaxStockLevel(listOfFuelWarehouse.get(0).getStock().getStockInfo().getMaxStockLevel());
                        selectedObject.getStock().getStockInfo().setBalance(listOfFuelWarehouse.get(0).getStock().getStockInfo().getBalance());
                        selectedObject.getStock().setAvailableQuantity(listOfFuelWarehouse.get(0).getStock().getAvailableQuantity());
                        selectedObject.getStock().getStockInfo().setIsMinusStockLevel(listOfFuelWarehouse.get(0).getStock().getStockInfo().isIsMinusStockLevel());
                        isListFuelWarehouse = false;
                    } else {
                        isListFuelWarehouse = true;
                    }
                }

            }

            isMaxStockLevel = false;
            isMinStockLevel = false;

            //seçilen stokun alternatif birimlerini çek
            listOfUnit = stockAlternativeUnitService.findStockUnitConnection(selectedObject.getStock(), selectedWaybill.getBranchSetting());

            RequestContext.getCurrentInstance().update("frmItemsProcess");
        }
    }

    public void bringUnit() {
        for (StockUnitConnection unitCon : listOfUnit) {
            if (selectedObject.getStock().getUnit().getId() == unitCon.getUnit().getId()) {
                selectedObject.getStock().getUnit().setSortName(unitCon.getUnit().getSortName());
                selectedObject.getStock().getUnit().setUnitRounding(unitCon.getUnit().getUnitRounding());
            }
        }
    }

    //Satınalma irsaliyesinde stoktaki stok eksi bakiyeye düşebilir mi paremetresine göre stok bakiyesinin eksiye düşmesini engellemek amacıyla kontrol yapar.
    public void stockPurchaseLevelControl(WaybillItem obj) {
        if (obj.getStock().getAvailableQuantity() != null && obj.getQuantity() != null) {
            if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity()) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isPurchaseMinStockLevel = true;
            } else {
                isPurchaseMinStockLevel = false;
            }
        } else {
            isPurchaseMinStockLevel = false;
        }

    }

    //Satış irsaliyesinde stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde satış irsaliyesinin silinmesi engellemek için çalışır.
    public void salesMaxStockLevelControl(WaybillItem obj) {

        if (obj.getStock().getStockInfo().getMaxStockLevel() != null && obj.getQuantity() != null) {
            BigDecimal salesAmount = BigDecimal.ZERO;
            salesAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
            if (salesAmount.compareTo(obj.getQuantity()) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nodeletionispossibleabovethemaximumstocklevel"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isSalesMaxStockLevel = true;
            } else {
                isSalesMaxStockLevel = false;
            }
        } else {
            isSalesMaxStockLevel = false;
        }

    }

    public void testBeforeDelete() {
        isSalesMaxStockLevel = true;
        isPurchaseMinStockLevel = true;

        if (((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (selectedWaybill.getType().getId() == 22 && !selectedWaybill.isIsPurchase())) && (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel())) { // Stok kartında ürün eksiye düşebilir mi seçili değilse
            stockPurchaseLevelControl(selectedObject);
        }

        if (((!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && (selectedObject.getStock().getStockInfo().getMaxStockLevel() != null)) {
            salesMaxStockLevelControl(selectedObject);
        }
        if ((((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (selectedWaybill.getType().getId() == 22 && !selectedWaybill.isIsPurchase())) && (selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isPurchaseMinStockLevel)) || (((!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && (selectedObject.getStock().getStockInfo().getMaxStockLevel() == null || !isSalesMaxStockLevel)) || isCreateWaybillFromOrder) {
            deleteControlMessage = "";
            deleteControlMessage1 = "";
            deleteControlMessage2 = "";

            checkDelete = waybillItemService.testBeforeDelete(selectedObject);

            if (checkDelete != null) {
                switch (checkDelete.getR_response()) {
                    case -101://faturaya bağlı ise silme uyarı ver
                        deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                        deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeletetheinvoiceitem");
                        deleteControlMessage2 = sessionBean.getLoc().getString("documentnumber");
                        relatedRecord = checkDelete.getR_recordno();
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                        break;
                    default:
                        //Sil
                        RequestContext.getCurrentInstance().update("frmItemsProcess:dlgDelete");
                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                        break;
                }
            }
        }
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_waybillitem').hide();");
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("goToRelatedRecord();");

    }

    public void goToRelatedRecord() {

        List<Object> list = new ArrayList<>();
        switch (checkDelete.getR_response()) {
            case -101:
                Invoice invoice = new Invoice();
                invoice.setId(checkDelete.getR_record_id());
                invoice = invoiceService.findInvoice(invoice);
                list.add(invoice);
                list.add(selectedWaybill);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            default:
                break;
        }
    }

    public void delete() {

        if (isCreateWaybillFromOrder) {
            listOfObjects.remove(selectedObject);
            for (Iterator<WaybillItem> iterator = waybillProcessBean.getListOfItemForOrder().iterator(); iterator.hasNext();) {
                WaybillItem next = iterator.next();
                if (next.getStock().getId() == selectedObject.getStock().getId()) {
                    iterator.remove();
                    break;
                }
            }
            RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab:dtbItems");
            RequestContext.getCurrentInstance().execute("PF('dlg_waybillitem').hide();");
            sessionBean.createUpdateMessage(1);

        } else {
            int result = 0;
            result = waybillItemService.delete(selectedObject);
            if (result > 0) {
                listOfObjects.remove(selectedObject);
                RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab:dtbItems");
            }
            RequestContext.getCurrentInstance().execute("PF('dlg_waybillitem').hide();");
            sessionBean.createUpdateMessage(result);
        }

    }

    public void openQuickAdd() {
        productBarcode = "";
        productAmount = BigDecimal.ONE;
        listOfProduct = new ArrayList<>();
        deleteItem = new WaybillItem();

        RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').show()");
        RequestContext.getCurrentInstance().update("frmQuickAddProduct");
    }

    /**
     * Bu metot hızlı ekleme dialogunda girişi yapılan stoğu kontrol eder.Varsa
     * ekler , yoksa mesaj bilgisi döndürür.
     */
    public void saveData() {
        if (sessionBean.isPeriodClosed(selectedWaybill.getWaybillDate())) {
            WaybillItem selectedWaybillItem = new WaybillItem();
            if (productAmount != null && productBarcode != null) {
                if (productAmount.compareTo(BigDecimal.ZERO) > 0) {
                    boolean isThere = false;
                    boolean isThereItemList = false;
                    for (WaybillItem listItem : listOfProduct) {
                        if (listItem.getStock().getBarcode().equals(productBarcode)) {
                            isThere = true;
                            break;
                        }
                    }

                    for (WaybillItem listItem : listOfObjects) {
                        if (listItem.getStock().getBarcode().equals(productBarcode)) {
                            isThereItemList = true;
                            break;
                        }
                    }
                    if (!isThere && !isThereItemList) {
                        selectedWaybillItem = waybillItemService.findStock(productBarcode, selectedWaybill, false);

                        if (selectedWaybillItem.getStock().getId() <= 0) {
                            selectedWaybillItem = waybillItemService.findStock(productBarcode, selectedWaybill, true);

                            if (selectedWaybillItem.getQuantity() != null && selectedWaybillItem.getQuantity().doubleValue() > 0) {// alternatif barkod karşılığ ile miktar çarpılır
                                productAmount = productAmount.multiply(selectedWaybillItem.getQuantity());
                            }

                            for (WaybillItem listItem : listOfProduct) {
                                if (listItem.getStock().getId() == selectedWaybillItem.getStock().getId()) {
                                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    isThere = true;
                                    break;
                                }
                            }

                            for (WaybillItem listItem : listOfObjects) {
                                if (listItem.getStock().getId() == selectedWaybillItem.getStock().getId()) {
                                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("producthasalreadybeenaddedtotherelevantinwaybill"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    isThereItemList = true;
                                    break;
                                }
                            }

                        }
                        if (isThere || isThereItemList) {
                            productAmount = BigDecimal.ONE;
                            productBarcode = "";
                            selectedWaybillItem = new WaybillItem();
                            RequestContext.getCurrentInstance().update("frmQuickAddProduct");
                            return;
                        }

                        if (selectedWaybillItem.getStock().getId() > 0) {

                            if ((selectedWaybill.isIsPurchase() && !selectedWaybillItem.getStock().getStockInfo().isIsDelist()) || !selectedWaybill.isIsPurchase()) {

                                int isFuelWarehouseItem = 1;
                                int isFuelWarehouse = 1;
                                if (selectedWaybill.getWarehouseIdList() != null && !selectedWaybill.getWarehouseIdList().equals("")) {
                                    isFuelWarehouse = invoiceService.controlAutomationWarehouse(selectedWaybill.getWarehouseIdList());
                                    if (isFuelWarehouse == 1) {
                                        isFuelWarehouseItem = invoiceService.controlTankItemAvailable(selectedWaybill.getWarehouseIdList(), selectedWaybillItem.getStock());
                                    }
                                }

                                if (isFuelWarehouseItem == 1) {
                                    if (!selectedWaybillItem.getStock().getStockInfo().isIsMinusStockLevel()) {
                                        stockLevelControl(3, selectedWaybillItem);
                                    }
                                    if (((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && selectedWaybillItem.getStock().getStockInfo().getMaxStockLevel() != null) {
                                        maxStockLevelControl(3, selectedWaybillItem);
                                    }
                                    if ((selectedWaybillItem.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                                        selectedWaybillItem.setQuantity(productAmount);
                                        selectedWaybillItem.setStockCount(1);
                                        listOfProduct.add(selectedWaybillItem);
                                    }
                                } else {
                                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            sessionBean.loc.getString("warning"),
                                            sessionBean.loc.getString("differentstockcannotbeenteredintank"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                }

                            } else {

                                //Stock Delist
                                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("theproductcannotbeaddedbecauseitisdelisted"));
                                FacesContext.getCurrentInstance().addMessage(null, message);
                                RequestContext.getCurrentInstance().update("grwProcessMessage");

                            }

                        } else {
                            //Stock Bolunamadı
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("stockinformationnotfound"));
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }
                    } else if (isThere && !isThereItemList) {
                        //Barkodlar Aynı
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else if (!isThere && isThereItemList) {
                        // Ürün İrsaliyeye Daha Önce Eklenmiş
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("producthasalreadybeenaddedtotherelevantinwaybill"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {
                    ///Miktar Hatalı
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterquantityinformation"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else { // Barkod veya Miktar Boş Olamaz
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasecheckthebarcodeandquantityinformation"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
            productAmount = BigDecimal.ONE;
            productBarcode = "";
            RequestContext.getCurrentInstance().update("frmQuickAddProduct");
        }
    }

    /**
     * Bu metot hızlı ekleme diyaloğunda eklenen stoğun değiştirilme durumunda
     * çalışır.
     *
     * @param waybillItem
     */
    public void updateData(WaybillItem waybillItem) {
        WaybillItem updateItem = new WaybillItem();
        int oldStockId = waybillItem.getStock().getId();
        if (waybillItem.getStock().getBarcode() != null || waybillItem.getQuantity() != null) {

            if (waybillItem.getQuantity().compareTo(BigDecimal.ZERO) > 0) {

                updateItem = waybillItemService.findStock(waybillItem.getStock().getBarcode(), selectedWaybill, false);
                if (updateItem.getStock().getId() <= 0) {
                    updateItem = waybillItemService.findStock(waybillItem.getStock().getBarcode(), selectedWaybill, true);

                    if (updateItem.getQuantity() != null && updateItem.getQuantity().doubleValue() > 0) {// alternatif barkod karşılığ ile miktar çarpılır
                        waybillItem.setQuantity(waybillItem.getQuantity().multiply(updateItem.getQuantity()));
                    }
                }

                for (Iterator<WaybillItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
                    WaybillItem next = iterator.next();
                    if (next.getStock().getId() == oldStockId) {
                        iterator.remove();
                        break;
                    }
                }

                if (updateItem.getStock().getId() > 0) {
                    boolean isThere = false;
                    boolean isThereItemList = false;
                    for (WaybillItem allItem : listOfProduct) {
                        if (allItem.getStock().getId() == updateItem.getStock().getId()) {
                            isThere = true;
                            break;
                        }
                    }

                    for (WaybillItem listItem : listOfObjects) { // ürün irsaliyeye eklenmiş mi 
                        if (listItem.getStock().getId() == updateItem.getStock().getId()) {
                            isThereItemList = true;
                            break;
                        }
                    }

                    if (!isThere && !isThereItemList) {
                        if (!updateItem.getStock().getStockInfo().isIsMinusStockLevel()) {
                            oldQuantity = BigDecimal.ZERO;
                            updateItem.setQuantity(waybillItem.getQuantity());
                            updateItem.getStock().getStockInfo().setMaxStockLevel(waybillItem.getStock().getStockInfo().getMaxStockLevel());
                            updateItem.getStock().getStockInfo().setBalance(waybillItem.getStock().getStockInfo().getBalance());

                            stockLevelControl(2, updateItem);
                        }

                        if (((selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() != 22) || (!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) && updateItem.getStock().getStockInfo().getMaxStockLevel() != null) {
                            maxStockLevelControl(2, updateItem);

                        }

                        if ((updateItem.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                            updateItem.setQuantity(waybillItem.getQuantity());
                            updateItem.setStockCount(1);
                            listOfProduct.add(updateItem);

                            sessionBean.createUpdateMessage(1);
                        }

                    } else if (isThere && !isThereItemList) {
                        //Barkodlar Aynı
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else if (!isThere && isThereItemList) {
                        // Ürün İrsaliyeye Daha Önce Eklenmiş
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("producthasalreadybeenaddedtotherelevantinwaybill"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }

                } else {
                    //Stock Bolunamadı
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("stockinformationnotfound"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else {
                // 0 giremez
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterquantityinformation"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            for (Iterator<WaybillItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
                WaybillItem next = iterator.next();
                if (next.getStock().getId() == oldStockId) {
                    iterator.remove();
                    break;
                }
            }
        }
        RequestContext.getCurrentInstance().update("frmQuickAddProduct");

    }

    public void createWayBillProductItem() {
        if (listOfProduct.size() > 0) {
            int result = waybillItemService.createAll(listOfProduct, selectedWaybill);
            if (result > 0) {
                RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab:dtbItems");
                listOfObjects = waybillItemService.listWaybillItem(selectedWaybill);
                RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').hide()");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("stockinformationnotfound"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    /**
     * Bu metot irsaliye ürününü listeden siler.
     *
     * @param waybillItem
     */
    public void deleteRecord(WaybillItem waybillItem) {
        for (Iterator<WaybillItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
            WaybillItem next = iterator.next();
            if (next.getStock().getId() == waybillItem.getStock().getId()) {
                iterator.remove();
            }
        }
        RequestContext.getCurrentInstance().update("frmQuickAddProduct");
    }

    /**
     * Bu metot dosya yükleme dialogu açıldığı zaman verileri sıfırlamak için
     * kullanılır.
     */
    public void resetUpload() {
        clearProducts();
        isOpenTransferBtn = true;
        isOpenCancelBtn = true;
        isOpenSaveBtn = false;
        fileNames = "";
        uploadedFile = null;
    }

    public void clear() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvWaybill:form:pgrFileUpload");
    }

    public void clearProducts() {
        waybillItem = new WaybillItem();
        excelStockList = new ArrayList<>();
        tempProductList = new ArrayList<>();
        tempStockList = new ArrayList<>();

    }

    /**
     * Bu metot excel dosyası yüklemek için dialogu açar
     */
    public void showUploadDialog() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvWaybill:frmtoolbar");
        context.update("tbvWaybill:form:pgrFileUpload");

        sampleList = new ArrayList<>();

        sampleList = waybillItemService.createSampleList();

        context.execute("PF('dlg_stockfileupload').show();");
        context.update("tbvWaybill:dlgStockFileUpload");
    }

    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        resetUpload(); // uploaddan önce tüm kayıtları sıfırlar

        uploadedFile = event.getFile();
        fileName = uploadedFile.getFileName();
        String s = new String(fileName.getBytes(Charset.defaultCharset()), "UTF-8"); // gelen türkçe karakterli excel dosyasının adını utf8 formatında düzenler.
        String substringData = "";
        if (s.length() > 20) { // eğer gelen fileName değeri 20 den büyük ise substring yapılır.
            substringData = s.substring(0, 20);
        } else {
            substringData = s;
        }
        fileNames = substringData.toLowerCase();

        isOpenTransferBtn = false;
        isOpenCancelBtn = false;

        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

    }

    /**
     * Bu metot aktar butonuna basınca çalışır.Excelden okuduğu verileri
     * istenilen formda geriye döndürür.
     */
    public void convertUploadData() throws IOException {
        clearProducts();
        RequestContext context = RequestContext.getCurrentInstance();

        excelStockList = waybillItemService.processUploadFile(uploadedFile.getInputstream(), selectedWaybill);

        tempProductList.addAll(excelStockList);
        tempStockList.addAll(excelStockList);

        int count = 0;
        for (WaybillItem obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getExcelDataType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isOpenSaveBtn = true;
        }
        isOpenErrorData = false;
        context.execute("PF('dlg_productView').show();");
        context.update("tbvWaybill:btnSave");
        context.update("tbvWaybill:frmProductView");
        context.update("tbvWaybill:frmtoolbar");
        context.update("tbvWaybill:form:pgrFileUpload");
        context.update("tbvWaybill:frmProductView:dtbProductView");

        isOpenCancelBtn = false;
    }

    /**
     * Bu metot hatalı kayıtları göstermek/ gizlemek durumunda çalışır.Listeyi
     * günceller
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<WaybillItem> iterator = tempStockList.iterator(); iterator.hasNext();) {
                WaybillItem value = iterator.next();
                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }
            excelStockList.clear();
            excelStockList.addAll(tempStockList);
        } else {
            excelStockList.clear();
            excelStockList.addAll(tempProductList);
        }
        context.update("tbvWaybill:frmProductView:dtbProductView");
    }

    public void saveItem() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();

        for (WaybillItem stock : tempProductList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }

        String resultJson = waybillItemService.jsonArrayForExcelUpload(selectedWaybill, excelStockList);

        excelStockList.clear();
        excelStockList.addAll(tempProductList);

        if (resultJson.isEmpty() || resultJson.equals("[]")) {
            listOfObjects.clear();
            listOfObjects = waybillItemService.listWaybillItem(selectedWaybill);
            RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab:dtbItems");
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");
            context.execute("PF('dlg_stockfileupload').hide();");

        } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
            JSONArray jsonArr = new JSONArray(resultJson);
            for (int m = 0; m < jsonArr.length(); m++) {
                ErrorItem item = new ErrorItem();
                String jsonBarcode = jsonArr.getJSONObject(m).getString("barcode");
                int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorCode");
                item.setBarcode(jsonBarcode);
                item.setErrorCode(jsonErrorCode);
                switch (item.getErrorCode()) {
                    case -1:
                        item.setErrorString(sessionBean.getLoc().getString("stockinformationnotfound")); // sessionBean.getLoc().getString("")
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("unsuccesfuloperation"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("stockcannotdroptonegativebalance"));
                        break;
                    case -4:
                        item.setErrorString(sessionBean.getLoc().getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                    case -5:
                        item.setErrorString(sessionBean.getLoc().getString("differentstockcannotbeenteredintank"));
                    case -6:
                        item.setErrorString(sessionBean.getLoc().getString("theproductcannotbeaddedbecauseitisdelisted"));
                    case -7:
                        item.setErrorString(sessionBean.getLoc().getString("sincetheroundingvalueoftheproductsunitis0enteranondecimalvalueinthequantityfield"));
                    default:
                        break;
                }
                errorList.add(item);
            }
            listOfObjects.clear();
            listOfObjects = waybillItemService.listWaybillItem(selectedWaybill);
            RequestContext.getCurrentInstance().update("tbvWaybill:frmWaybillItemsTab:dtbItems");
            context.update("grwProcessMessage");
            context.execute("PF('dlg_productView').hide();");
            context.execute("PF('dlg_productErrorView').show();");
            context.update("tbvWaybill:frmProductErrorView:dtbProductErrorView");
        }
    }

    //Stoktaki "Stok Eksi Bakiyeye Düşebilir Mi?" işaretli değilse eksi bakiye kontrolü için çalışır
    public void stockLevelControl(int processType, WaybillItem obj) {
        if (processType == 1 || processType == 3) {
            BigDecimal quantity = new BigDecimal(BigInteger.ZERO);

            if (processType == 1) {
                quantity = selectedObject.getQuantity();
            } else if (processType == 3) {
                quantity = productAmount;
            }
            if ((!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 21) || (selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) {
                if (obj.getStock().getAvailableQuantity().compareTo(quantity) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMinStockLevel = true;
                } else {
                    isMinStockLevel = false;
                }
            }

        } else if (processType == 2) {

            if ((!selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 21) || (selectedWaybill.isIsPurchase() && selectedWaybill.getType().getId() == 22)) {

                if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMinStockLevel = true;
                    selectedObject.setQuantity(oldQuantity);
                } else {
                    isMinStockLevel = false;
                }
            }
        }
    }

    // Stokta max seviye tanımlanmışsa satın alma işlemlerinde max seviyenin üzerine çıkılmaması için çalışır
    public void maxStockLevelControl(int processType, WaybillItem obj) {

        if (processType == 1 || processType == 3) {

            BigDecimal quantity = new BigDecimal(BigInteger.ZERO);
            if (processType == 1) {
                quantity = selectedObject.getQuantity();
            } else if (processType == 3) {
                quantity = productAmount;
            }

            BigDecimal purchaseAmount = BigDecimal.ZERO;
            purchaseAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
            if (purchaseAmount.compareTo(quantity) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isMaxStockLevel = true;
            } else {
                isMaxStockLevel = false;

            }

        } else if (processType == 2) {
            BigDecimal prchAmount = BigDecimal.ZERO;
            prchAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());

            if (prchAmount.compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isMaxStockLevel = true;
                selectedObject.setQuantity(oldQuantity);
            } else {
                isMaxStockLevel = false;
            }
        }
    }

    public void changeFuelWarehouse() {
        for (Warehouse wh : listOfFuelWarehouse) {
            if (wh.getId() == selectedObject.getWarehouse().getId()) {

                selectedObject.getStock().getStockInfo().setMaxStockLevel(wh.getStock().getStockInfo().getMaxStockLevel());
                selectedObject.getStock().getStockInfo().setBalance(wh.getStock().getStockInfo().getBalance());
                selectedObject.getStock().setAvailableQuantity(wh.getStock().getAvailableQuantity());
                selectedObject.getStock().getStockInfo().setIsMinusStockLevel(wh.getStock().getStockInfo().isIsMinusStockLevel());

            }

        }

    }

    public void bringStockType() {
        if (isCreateWaybillFromOrder) {
            if (selectedObject.getStock().isIsService()) {
                bookType = "serviceCreateWaybillFromOrder";
            } else {
                bookType = "stockCreateWaybillFromOrder";
            }
        } else {
            if (selectedObject.getStock().isIsService()) {
                bookType = "waybillservice";
            } else {
                bookType = "waybillstock";
            }
        }

    }

}
