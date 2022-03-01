/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   13.01.2017 01:32:54
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.google.gson.JsonArray;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceItemService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillItemService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.contractarticles.business.IContractArticleService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.refinerypurchase.business.IRefineryPurchaseService;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IncomingInvoicesItem;
import com.mepsan.marwiz.system.einvoiceintegration.presentation.IncomingEInvoicesBean;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class InvoiceItemTabBean extends GeneralDefinitionBean<InvoiceItem> {

    @ManagedProperty(value = "#{invoiceItemService}")
    public IInvoiceItemService invoiceItemService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    public StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{taxGroupService}")
    public ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceProcessBean}")
    public InvoiceProcessBean invoiceProcessBean;

    @ManagedProperty(value = "#{priceListItemService}")
    public IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{waybillItemService}")
    public IWaybillItemService waybillItemService;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{stockAlternativeUnitService}")
    private IStockAlternativeUnitService stockAlternativeUnitService;

    @ManagedProperty(value = "#{contractArticleService}")
    public IContractArticleService contractArticleService;

    @ManagedProperty(value = "#{refineryPurchaseService}")
    public IRefineryPurchaseService refineryPurchaseService;

    @ManagedProperty(value = "#{incomingEInvoicesBean}")
    public IncomingEInvoicesBean incomingEInvoicesBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Invoice selectedInvoice;
    private int processType;
    private String taxRates, exchange;
    private BigDecimal totalDiscount, totalPrice;
    private TaxGroup taxGroup;
    private TaxGroup selectedStockTaxGroup;
    private boolean isCreateInvFromWaybill;//irsaliyeden fatura oluşturma mı ? true : evet
    private boolean isCreateInvFromOrder;
    private String bookType;
    private List<WaybillItem> listWaybillItem;
    private PriceListItem priceList;
    private WaybillItem selectedWaybillItem;
    private BranchSetting branchSetting;
    private PriceListItem stockPriceList;
    private List<PriceListItem> listOfPriceListItem;
    private boolean isCreateInvFromCustomerAgreement, isThereCurrentPurchasePrice;//Mutabakat sayfasından oluşmuş faturalar için
    private List<InvoiceItem> listOfProduct;
    private InvoiceItem selectedStock, stockInformation;
    private Stock purchaseControlStock;
    private BigDecimal currentProfitRate, stockRecomendedPrice, currentPurchasePrice;
    private boolean isPriceListItemSave, isControlPurchase, isSpeedAdd;
    private List<StockUnitConnection> listOfUnit;
    private CustomerAgreements customerAgreements;
    private CreditReport credit;
    private int sCount = 0;
    private Stock stockItem;
    private String fileNames;
    private String fileName;
    private boolean isOpenSaveBtn, isOpenCancelBtn, isOpenTransferBtn, isOpenErrorData;
    private UploadedFile uploadedFile;
    private List<InvoiceItem> excelStockList;
    private List<InvoiceItem> tempStockList;
    private List<InvoiceItem> tempProductList;
    private List<InvoiceItem> sampleList;
    private InvoiceItem selectedObj;
    private List<ErrorItem> errorList;
    private List<IncomingInvoicesItem> listItem;
    private BigDecimal oldQuantity;
    private boolean isMinStockLevel;
    private boolean isMaxStockLevel;
    private List<InvoiceItem> listOfTaxs;
    List<CheckDelete> controlDeleteList;
    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    private int response;
    private int relatedRecordId;
    private boolean isUpdate;
    private List<InvoiceItem> listOfItemForWaitedInvoice;
    private List<WaybillItem> tempListOfWaybillItem;

    private boolean isOrderConnection;
    private List<Warehouse> listOfFuelWarehouse;
    private boolean isListFuelWarehouse;
    private boolean isPurchaseMinStockLevel;
    private boolean isSalesMaxStockLevel;

    private List<InvoiceItem> excelList;
    private List<InvoiceItem> listOfSelectedItems;
    private boolean isAll;

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setIncomingEInvoicesBean(IncomingEInvoicesBean incomingEInvoicesBean) {
        this.incomingEInvoicesBean = incomingEInvoicesBean;
    }

    public List<IncomingInvoicesItem> getListItem() {
        return listItem;
    }

    public void setListItem(List<IncomingInvoicesItem> listItem) {
        this.listItem = listItem;
    }

    public WaybillItem getSelectedWaybillItem() {
        return selectedWaybillItem;
    }

    public List<StockUnitConnection> getListOfUnit() {
        return listOfUnit;
    }

    public void setListOfUnit(List<StockUnitConnection> listOfUnit) {
        this.listOfUnit = listOfUnit;
    }

    public void setStockAlternativeUnitService(IStockAlternativeUnitService stockAlternativeUnitService) {
        this.stockAlternativeUnitService = stockAlternativeUnitService;
    }

    public void setContractArticleService(IContractArticleService contractArticleService) {
        this.contractArticleService = contractArticleService;
    }

    public void setRefineryPurchaseService(IRefineryPurchaseService refineryPurchaseService) {
        this.refineryPurchaseService = refineryPurchaseService;
    }

    public BigDecimal getCurrentPurchasePrice() {
        return currentPurchasePrice;
    }

    public void setCurrentPurchasePrice(BigDecimal currentPurchasePrice) {
        this.currentPurchasePrice = currentPurchasePrice;
    }

    public void setSelectedWaybillItem(WaybillItem selectedWaybillItem) {
        this.selectedWaybillItem = selectedWaybillItem;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public boolean isIsOpenTransferBtn() {
        return isOpenTransferBtn;
    }

    public void setIsOpenTransferBtn(boolean isOpenTransferBtn) {
        this.isOpenTransferBtn = isOpenTransferBtn;
    }

    public void setWaybillItemService(IWaybillItemService waybillItemService) {
        this.waybillItemService = waybillItemService;
    }

    public List<WaybillItem> getListWaybillItem() {
        return listWaybillItem;
    }

    public void setListWaybillItem(List<WaybillItem> listWaybillItem) {
        this.listWaybillItem = listWaybillItem;
    }

    public String getBookType() {
        return bookType;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }

    public void setInvoiceProcessBean(InvoiceProcessBean invoiceProcessBean) {
        this.invoiceProcessBean = invoiceProcessBean;
    }

    public String getTaxRates() {
        return taxRates;
    }

    public void setTaxRates(String taxRates) {
        this.taxRates = taxRates;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public boolean isIsSpeedAdd() {
        return isSpeedAdd;
    }

    public void setIsSpeedAdd(boolean isSpeedAdd) {
        this.isSpeedAdd = isSpeedAdd;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setInvoiceItemService(IInvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(Invoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public boolean isIsCreateInvFromWaybill() {
        return isCreateInvFromWaybill;
    }

    public void setIsCreateInvFromWaybill(boolean isCreateInvFromWaybill) {
        this.isCreateInvFromWaybill = isCreateInvFromWaybill;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public List<PriceListItem> getListOfPriceListItem() {
        return listOfPriceListItem;
    }

    public void setListOfPriceListItem(List<PriceListItem> listOfPriceListItem) {
        this.listOfPriceListItem = listOfPriceListItem;
    }

    public boolean isIsCreateInvFromCustomerAgreement() {
        return isCreateInvFromCustomerAgreement;
    }

    public void setIsCreateInvFromCustomerAgreement(boolean isCreateInvFromCustomerAgreement) {
        this.isCreateInvFromCustomerAgreement = isCreateInvFromCustomerAgreement;
    }

    public InvoiceItem getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(InvoiceItem selectedStock) {
        this.selectedStock = selectedStock;
    }

    public List<InvoiceItem> getListOfProduct() {
        return listOfProduct;
    }

    public void setListOfProduct(List<InvoiceItem> listOfProduct) {
        this.listOfProduct = listOfProduct;
    }

    public TaxGroup getSelectedStockTaxGroup() {
        return selectedStockTaxGroup;
    }

    public void setSelectedStockTaxGroup(TaxGroup selectedStockTaxGroup) {
        this.selectedStockTaxGroup = selectedStockTaxGroup;
    }

    public PriceListItem getStockPriceList() {
        return stockPriceList;
    }

    public void setStockPriceList(PriceListItem stockPriceList) {
        this.stockPriceList = stockPriceList;
    }

    public Stock getPurchaseControlStock() {
        return purchaseControlStock;
    }

    public void setPurchaseControlStock(Stock purchaseControlStock) {
        this.purchaseControlStock = purchaseControlStock;
    }

    public BigDecimal getCurrentProfitRate() {
        return currentProfitRate;
    }

    public void setCurrentProfitRate(BigDecimal currentProfitRate) {
        this.currentProfitRate = currentProfitRate;
    }

    public boolean isIsControlPurchase() {
        return isControlPurchase;
    }

    public void setIsControlPurchase(boolean isControlPurchase) {
        this.isControlPurchase = isControlPurchase;
    }

    public BigDecimal getStockRecomendedPrice() {
        return stockRecomendedPrice;
    }

    public void setStockRecomendedPrice(BigDecimal stockRecomendedPrice) {
        this.stockRecomendedPrice = stockRecomendedPrice;
    }

    public boolean isIsThereCurrentPurchasePrice() {
        return isThereCurrentPurchasePrice;
    }

    public void setIsThereCurrentPurchasePrice(boolean isThereCurrentPurchasePrice) {
        this.isThereCurrentPurchasePrice = isThereCurrentPurchasePrice;
    }

    public boolean isIsPriceListItemSave() {
        return isPriceListItemSave;
    }

    public void setIsPriceListItemSave(boolean isPriceListItemSave) {
        this.isPriceListItemSave = isPriceListItemSave;
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

    public List<InvoiceItem> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<InvoiceItem> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public List<InvoiceItem> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<InvoiceItem> sampleList) {
        this.sampleList = sampleList;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public InvoiceItem getSelectedObj() {
        return selectedObj;
    }

    public void setSelectedObj(InvoiceItem selectedObj) {
        this.selectedObj = selectedObj;
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

    public List<InvoiceItem> getListOfTaxs() {
        return listOfTaxs;
    }

    public void setListOfTaxs(List<InvoiceItem> listOfTaxs) {
        this.listOfTaxs = listOfTaxs;
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

    public String getRelatedRecord() {
        return relatedRecord;
    }

    public void setRelatedRecord(String relatedRecord) {
        this.relatedRecord = relatedRecord;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getRelatedRecordId() {
        return relatedRecordId;
    }

    public void setRelatedRecordId(int relatedRecordId) {
        this.relatedRecordId = relatedRecordId;
    }

    public boolean isIsCreateInvFromOrder() {
        return isCreateInvFromOrder;
    }

    public void setIsCreateInvFromOrder(boolean isCreateInvFromOrder) {
        this.isCreateInvFromOrder = isCreateInvFromOrder;
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

    public List<InvoiceItem> getExcelList() {
        return excelList;
    }

    public void setExcelList(List<InvoiceItem> excelList) {
        this.excelList = excelList;
    }

    public List<InvoiceItem> getListOfSelectedItems() {
        return listOfSelectedItems;
    }

    public void setListOfSelectedItems(List<InvoiceItem> listOfSelectedItems) {
        this.listOfSelectedItems = listOfSelectedItems;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("--------------InvoiceItemTabBean");
        isMinStockLevel = false;
        isMaxStockLevel = false;
        oldQuantity = BigDecimal.ZERO;
        controlDeleteList = new ArrayList<>();
        listOfObjects = new ArrayList<>();
        listOfItemForWaitedInvoice = new ArrayList<>();
        listWaybillItem = new ArrayList<>();
        tempListOfWaybillItem = new ArrayList<>();

        listOfSelectedItems = new ArrayList<>();
        isAll = false;

        selectedInvoice = invoiceProcessBean.getSelectedObject();
        isCreateInvFromCustomerAgreement = true;
        branchSetting = sessionBean.getLastBranchSetting();
        listOfFuelWarehouse = new ArrayList<>();
        isListFuelWarehouse = false;
        listItem = new ArrayList<>();
        listOfUnit = new ArrayList<>();
        if (selectedInvoice.getType().getId() != 26) {
            if (selectedInvoice.getId() > 0) {
                if (selectedInvoice.isIsWait()) {
                    jsonToListForWaitedInvoice();
                } else {
                    listOfObjects = findall();
                }
                if (selectedInvoice.isIsPeriodInvoice()) {//Mutabakat sayfasından oluşmuş faturalar için
                    isCreateInvFromCustomerAgreement = false;
                }
                for (InvoiceItem item : listOfObjects) {
                    item.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                    item.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                    item.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                    item.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                }
                bringInvoiceDiscount();
                calcInvoicePrice();
                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
            } else {
                listOfObjects = new ArrayList<>();
            }

            if (sessionBean.parameter instanceof ArrayList) {

                for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {

                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Invoice) {//Fatura ise
                        isCreateInvFromWaybill = false;
                        isCreateInvFromOrder = false;
                    }

                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Waybill) {
                        isCreateInvFromWaybill = true;
                    }

                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {
                        isCreateInvFromOrder = true;
                    }

                    if (((ArrayList) sessionBean.parameter).get(i) instanceof JsonArray) {//irsaliyeden fatura oluşturulacak ise JsonArray olarak irsaliye itemları gelir.
                        listOfObjects = new ArrayList<>();
                        if (isCreateInvFromWaybill) {
                            JsonArray jArray = (JsonArray) ((ArrayList) sessionBean.parameter).get(i);
                            for (int j = 0; j < jArray.size(); j++) {
                                InvoiceItem invi = new InvoiceItem();

                                invi.setId(jArray.get(j).getAsJsonObject().get("id").getAsInt());//gereksiz datatable da eşsiz olsun diye atadık.
                                invi.getStock().setId(jArray.get(j).getAsJsonObject().get("stock_id").getAsInt());
                                invi.getStock().setName(jArray.get(j).getAsJsonObject().get("stock_name").getAsString());
                                invi.getStock().setBarcode(jArray.get(j).getAsJsonObject().get("stock_barcode").getAsString());
                                invi.getUnit().setId(jArray.get(j).getAsJsonObject().get("unit_id").getAsInt());
                                invi.getUnit().setSortName(jArray.get(j).getAsJsonObject().get("unit_sortname").getAsString());
                                invi.getUnit().setName(jArray.get(j).getAsJsonObject().get("unit_name").getAsString());
                                invi.getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unit_rounding").getAsInt());
                                invi.getStock().getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unit_rounding").getAsInt());
                                invi.setQuantity(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal());
                                invi.setControlQuantity(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal());
                                invi.setDescription(jArray.get(j).getAsJsonObject().get("description").getAsString());
                                invi.setWaybillItemIds(jArray.get(j).getAsJsonObject().get("id").getAsString());//waybillitem id bilgisi
                                invi.getStock().getStockInfo().setCurrentSalePrice(jArray.get(j).getAsJsonObject().get("currenctsaleprice").getAsBigDecimal());
                                invi.getStock().getStockInfo().getCurrentSaleCurrency().setId(jArray.get(j).getAsJsonObject().get("currenctsalecurrency").getAsInt());
                                // invi.setTaxRate(BigDecimal.ZERO);
                                invi.setTotalTax(BigDecimal.ZERO);
                                //.setUnitPrice(BigDecimal.ZERO);
                                invi.setProfitPercentage(BigDecimal.ZERO);
                                invi.setProfitPrice(BigDecimal.ZERO);
                                // invi.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                                if (!jArray.get(j).getAsJsonObject().get("taxrate").isJsonNull()) {
                                    invi.setTaxRate(jArray.get(j).getAsJsonObject().get("taxrate").getAsBigDecimal());
                                }

                                invi.setUnitPrice(jArray.get(j).getAsJsonObject().get("pricelistprice").getAsBigDecimal());
                                invi.getCurrency().setId(jArray.get(j).getAsJsonObject().get("pricelistcurrency").getAsInt());
                                invi.getCurrency().setTag(jArray.get(j).getAsJsonObject().get("pricelistcurrencyname").getAsString());
                                invi.setIsTaxIncluded(jArray.get(j).getAsJsonObject().get("pricelisttaxincluded").getAsBoolean());
                                invi.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                                invi.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                                invi.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                                invi.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                                invi.getInvoice().setIsPurchase(selectedInvoice.isIsPurchase());
                                invi.getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unit_rounding").getAsInt());
                                invi.getStock().getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unit_rounding").getAsInt());

                                if (selectedInvoice.isIsFuel()) {

                                    invi.getWarehouse().setId(jArray.get(j).getAsJsonObject().get("warehouse_id").getAsInt());
                                    invi.getWarehouse().setName(jArray.get(j).getAsJsonObject().get("warehouse_name").getAsString());
                                }

                                invi = invoiceItemService.calculater(invi, 1);
                                listOfObjects.add(invi);
                            }

                            invoiceProcessBean.setIsCreateInv(true);
                            for (InvoiceItem invi : listOfObjects) {
                                if (invi.getCurrency().getId() == 0) {
                                    invoiceProcessBean.setIsCreateInv(false);
                                    break;
                                } else {
                                    invi.setExchangeRate(exchangeService.bringExchangeRate(invi.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                                }
                            }
                            calcInvoicePrice();
                            invoiceProcessBean.getListOfItemForWaybill().clear();
                            invoiceProcessBean.getListOfItemForWaybill().addAll(listOfObjects);
                        } else if (isCreateInvFromOrder) {

                            JsonArray jArray = (JsonArray) ((ArrayList) sessionBean.parameter).get(i);
                            for (int j = 0; j < jArray.size(); j++) {

                                boolean isThere = false;
                                for (InvoiceItem listOfObject : listOfObjects) {
                                    if (listOfObject.getStock().getId() == jArray.get(j).getAsJsonObject().get("stock_id").getAsInt()) {
                                        isThere = true;
                                        listOfObject.setControlQuantity(listOfObject.getControlQuantity().add(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal()));
                                        if (jArray.get(j).getAsJsonObject().get("id").getAsString() != null && !jArray.get(j).getAsJsonObject().get("id").getAsString().equals("")) {
                                            listOfObject.setOrderItemIds(listOfObject.getOrderItemIds() + "," + jArray.get(j).getAsJsonObject().get("id").getAsString());
                                            listOfObject.setOrderItemQuantitys(listOfObject.getOrderItemQuantitys() + "," + jArray.get(j).getAsJsonObject().get("quantity").getAsString());
                                        }
                                        if (jArray.get(j).getAsJsonObject().get("order_id").getAsString() != null && !jArray.get(j).getAsJsonObject().get("order_id").getAsString().equals("")) {
                                            listOfObject.setOrderIds(listOfObject.getOrderIds() + "," + jArray.get(j).getAsJsonObject().get("order_id").getAsString());
                                        }
//                                        listOfObject.setRemainingQuantity(listOfObject.getRemainingQuantity().add(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal()));                             
                                    }
                                }

                                if (!isThere) {
                                    InvoiceItem invi = new InvoiceItem();
                                    invi.setId(jArray.get(j).getAsJsonObject().get("id").getAsInt());//gereksiz datatable da eşsiz olsun diye atadık.
                                    invi.getStock().setId(jArray.get(j).getAsJsonObject().get("stock_id").getAsInt());
                                    invi.getStock().setName(jArray.get(j).getAsJsonObject().get("stock_name").getAsString());
                                    invi.getStock().setBarcode(jArray.get(j).getAsJsonObject().get("stock_barcode").getAsString());
                                    invi.getStock().setCenterProductCode(jArray.get(j).getAsJsonObject().get("stock_centerproductcode").getAsString());
                                    invi.getUnit().setId(jArray.get(j).getAsJsonObject().get("unit_id").getAsInt());
                                    invi.getUnit().setSortName(jArray.get(j).getAsJsonObject().get("unit_sortname").getAsString());
                                    invi.getUnit().setName(jArray.get(j).getAsJsonObject().get("unit_name").getAsString());
                                    invi.getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unitrounding").getAsInt());
                                    invi.setControlQuantity(jArray.get(j).getAsJsonObject().get("remainingquantity").getAsBigDecimal());
                                    invi.setDescription("");
                                    invi.setOrderIds(jArray.get(j).getAsJsonObject().get("order_id").getAsString());
                                    invi.setOrderItemIds(jArray.get(j).getAsJsonObject().get("id").getAsString());//waybillitem id bilgisi
                                    invi.setOrderItemQuantitys(jArray.get(j).getAsJsonObject().get("quantity").getAsString());
                                    invi.getStock().getStockInfo().setCurrentSalePrice(jArray.get(j).getAsJsonObject().get("currenctsaleprice").getAsBigDecimal());
                                    invi.getStock().getStockInfo().getCurrentSaleCurrency().setId(jArray.get(j).getAsJsonObject().get("currenctsalecurrency").getAsInt());
                                    // invi.setTaxRate(BigDecimal.ZERO);
                                    invi.setTotalTax(BigDecimal.ZERO);
                                    //.setUnitPrice(BigDecimal.ZERO);
                                    invi.setProfitPercentage(BigDecimal.ZERO);
                                    invi.setProfitPrice(BigDecimal.ZERO);
                                    // invi.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

                                    invi.setTaxRate(jArray.get(j).getAsJsonObject().get("taxrate").getAsBigDecimal());
                                    invi.setUnitPrice(jArray.get(j).getAsJsonObject().get("pricelistprice").getAsBigDecimal());
                                    invi.getCurrency().setId(jArray.get(j).getAsJsonObject().get("pricelistcurrency").getAsInt());
                                    invi.getCurrency().setTag(jArray.get(j).getAsJsonObject().get("pricelistcurrencyname").getAsString());
                                    invi.setIsTaxIncluded(true);
                                    invi.getUnit().setUnitRounding(jArray.get(j).getAsJsonObject().get("unitrounding").getAsInt());
                                    invi.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                                    invi.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                                    invi.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                                    invi.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                                    invi.getInvoice().setIsPurchase(true);
                                    if (invi.getCurrency().getId() > 0) {
                                        invi.setExchangeRate(exchangeService.bringExchangeRate(invi.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                                    }

                                    listOfObjects.add(invi);
                                }
                            }

                            invoiceProcessBean.setIsCreateInv(false);

                            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
                            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
                            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
                            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
                            decimalFormatSymbolsUnit.setCurrencySymbol("");
                            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

                            for (InvoiceItem invoiceItem1 : listOfObjects) {

                                invoiceItem1.setFirstOrderIds(invoiceItem1.getOrderIds());
                                invoiceItem1.setFirstOrderItemIds(invoiceItem1.getOrderItemIds());
                                invoiceItem1.setFirstOrderItemQuantitys(invoiceItem1.getOrderItemQuantitys());

                                formatterUnit.setMaximumFractionDigits(invoiceItem1.getUnit().getUnitRounding());
                                formatterUnit.setMinimumFractionDigits(invoiceItem1.getUnit().getUnitRounding());
                                invoiceItem1.setRemainingQuantity(formatterUnit.format(invoiceItem1.getControlQuantity()));

                            }

                            invoiceProcessBean.getListOfItemForOrder().clear();
                            invoiceProcessBean.getListOfItemForOrder().addAll(listOfObjects);

                        }
                    } else if (((ArrayList) sessionBean.parameter).get(i) instanceof CustomerAgreements) {
                        customerAgreements = (CustomerAgreements) ((ArrayList) sessionBean.parameter).get(i);
                        listOfObjects = invoiceItemService.findAllSaleItemForCredit(customerAgreements, "");
                        for (InvoiceItem item : listOfObjects) {
                            item.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                            item.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                            item.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                            item.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                            if (item.getUnitPrice() != null && item.getTaxRate() != null) {
                                item.setUnitPrice(item.getUnitPrice().divide((BigDecimal.valueOf(1).add(item.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN));
                                invoiceItemService.calculateProfit(item, 1);
                            }
                        }
                        invoiceProcessBean.getListOfItemForAgreement().addAll(listOfObjects);
                        bringInvoiceDiscount();
                        calcInvoicePrice();
                    } else if (((ArrayList) sessionBean.parameter).get(i) instanceof CreditReport && selectedInvoice.getId() == 0) { // kredi sayfasından geldi ise itemleri eklemek için
                        credit = (CreditReport) ((ArrayList) sessionBean.parameter).get(i);
                        customerAgreements = new CustomerAgreements();

                        customerAgreements.setBeginDate(credit.getBeginDate());
                        customerAgreements.setEndDate(credit.getEndDate());
                        customerAgreements.setAccount(credit.getAccount());
                        customerAgreements.getBranchSetting().getBranch().setId(credit.getBranchSetting().getBranch().getId());
                        customerAgreements.getBranchSetting().setIsCentralIntegration(credit.getBranchSetting().isIsCentralIntegration());
                        customerAgreements.getBranchSetting().setIsInvoiceStockSalePriceList(credit.getBranchSetting().isIsInvoiceStockSalePriceList());
                        customerAgreements.getBranchSetting().getBranch().getCurrency().setId(credit.getBranchSetting().getBranch().getCurrency().getId());

                        listOfObjects = invoiceItemService.findAllSaleItemForCredit(customerAgreements, "AND crdt.id =" + credit.getId());
                        for (InvoiceItem item : listOfObjects) {
                            item.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                            item.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                            item.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                            item.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                            if (item.getUnitPrice() != null && item.getTaxRate() != null) {
                                item.setUnitPrice(item.getUnitPrice().divide((BigDecimal.valueOf(1).add(item.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN));
                                invoiceItemService.calculateProfit(item, 1);
                            }
                        }

                        invoiceProcessBean.getListOfItemForAgreement().addAll(listOfObjects);
                        bringInvoiceDiscount();
                        calcInvoicePrice();
                    }
                }
            }

            for (InvoiceItem listOfObject : listOfObjects) {
                listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
            }

            if (selectedInvoice.isIsWait() && (selectedInvoice.getType().getId() == 23 || selectedInvoice.getType().getId() == 27)) {
                tempListOfWaybillItem = waybillItemService.listWaybillItemForInvoice(selectedInvoice);
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{5, 6, 7, 324, 143}, 0));
    }

    public void update() {

        processType = 2;//update
        if ((selectedInvoice.getType().getId() == 59 || (selectedInvoice.getType().getId() == 23 && isCreateInvFromWaybill) || selectedInvoice.getType().getId() == 23) && selectedInvoice.isIsFuel() && selectedObject.getWarehouse() != null) {
            listOfFuelWarehouse.add(selectedObject.getWarehouse());
        }
        isMinStockLevel = false;
        isMaxStockLevel = false;
        isOrderConnection = false;
        if (selectedObject.getOrderItemIds() != null && !"".equals(selectedObject.getOrderItemIds())) {
            isOrderConnection = true;
        }
        oldQuantity = selectedObject.getQuantity();
        isSpeedAdd = false;

        selectedObject.setInvoice(selectedInvoice);
        listOfUnit.clear();
        StockUnitConnection suc = new StockUnitConnection();
        suc.setStock(selectedObject.getStock());
        suc.setUnit(selectedObject.getUnit());
        listOfUnit.add(0, suc);
        selectedObject.setIsCanSaveItem(true);
        controlDeleteList.clear();
        if (selectedObject.getUnit() != null && selectedObject.getUnit().getId() > 0) {
            selectedObject.getStock().setUnit(selectedObject.getUnit());
        }
        if (isCreateInvFromWaybill || isCreateInvFromOrder) {
            stockBookFilterBean.setSelectedData(selectedObject.getStock());
            updateAllInformation();
            isUpdate = true;
        } else {

            controlDeleteList = invoiceItemService.testBeforeDelete(selectedObject);
            if (!controlDeleteList.isEmpty()) {
                response = controlDeleteList.get(0).getR_response();
                relatedRecordId = controlDeleteList.get(0).getR_record_id();
                if (response == -101) {//Bağlı kayıt var fiyat farkı
                    isUpdate = false;
                } else {//Bağlı kayıt yok direk güncelle
                    isUpdate = true;
                }
            } else {
                isUpdate = true;
            }

        }

        calculaterIsNotCalcTotalPrice();

        RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').show()");
        RequestContext.getCurrentInstance().update("dlgStockProcess");

    }

    @Override
    public void create() {
        isMinStockLevel = false;
        isMaxStockLevel = false;
        isSpeedAdd = false;
        isUpdate = true;
        processType = 1;//create 
        exchange = "";
        oldQuantity = BigDecimal.ZERO;
        selectedObject = new InvoiceItem();
        selectedObject.setInvoice(selectedInvoice);
        selectedObject.setCurrency(sessionBean.getUser().getLastBranch().getCurrency());
        listOfUnit.clear();
        if (selectedInvoice.getType().getId() == 59) {//irsaliyeli fatura ise direk ürün ekleyebilecek
            selectedObject.setTaxRate(BigDecimal.ZERO);
            selectedObject.setTotalTax(BigDecimal.ZERO);

            bringCurrency();
            bringStockType();
            RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').show()");
            RequestContext.getCurrentInstance().update("dlgStockProcess");
        } else {//irsaliye fatura dışında var olan irsaliyelerden ürün aktaracak
            listWaybillItem = waybillItemService.listWaybillItemForInvoice(selectedInvoice);
            if (selectedInvoice.isIsWait()) {
                for (Iterator<WaybillItem> iterator = listWaybillItem.iterator(); iterator.hasNext();) {
                    WaybillItem value = iterator.next();
                    for (InvoiceItem ii : listOfObjects) {
                        if (ii.getStock().getId() == value.getStock().getId()) {
                            value.setRemainingQuantity(value.getRemainingQuantity().subtract(ii.getQuantity()));
                        }
                    }
                    if (value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == 0 || value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == -1) {
                        iterator.remove();
                    }
                }
            }
            RequestContext.getCurrentInstance().execute("PF('dlg_waybillstock').show()");
            RequestContext.getCurrentInstance().update("frmInvoiceWaybillStock:dtbWaybillItem");
        }

        selectedObject.setIsCanSaveItem(true);
    }

    /**
     * Bu metot satın alma faturası için fiyat kontrolü sağlar.Eğer satın alma
     * kısıtı eklenmiş ise , tarih ve fiyat kontrolü yaparak kullanıcıya uyarı
     * gösterir.
     *
     * @param obj
     * @return
     */
    public boolean controlPurchaseConstraint(InvoiceItem obj) {
        boolean isControl = true;
        RequestContext context = RequestContext.getCurrentInstance();
        isThereCurrentPurchasePrice = false;
        isPriceListItemSave = false;
        PriceListItem stockPrice = null;
        if (!selectedObject.isIsService()) {
            //vergisiz tutarı sıfırdan farklı ise kontrole gir
            if (selectedInvoice.isIsPurchase() && obj.getTotalPrice().compareTo(BigDecimal.ZERO) != 0) {

                currentProfitRate = BigDecimal.ZERO;
                currentPurchasePrice = BigDecimal.ZERO;
                BigDecimal currentSalePrice = BigDecimal.ZERO;

                //vergisiz iskonto uygulanmış birim fiyat.
                currentPurchasePrice = obj.getTotalMoney().divide(obj.getQuantity(), 4, RoundingMode.HALF_EVEN);

                Date nowDate = new Date();
                Date purchaseConstraintDate = obj.getStock().getStockInfo().getPurchaseControlDate();

                if (purchaseConstraintDate == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    purchaseConstraintDate = calendar.getTime();
                }
                purchaseControlStock = stockService.findStockLastPrice(obj.getStock().getId(), selectedInvoice.getBranchSetting());

                stockPrice = priceListItemService.findStockPrice(obj.getStock(), false, selectedInvoice.getBranchSetting().getBranch());// stoğun bsğlı olduğu şubedeki varsılan satış fiyat listesindeki price biligsini getirir.

                // Eğer son alış fiyatı varsa kar oranını bul 
                if (purchaseControlStock.getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) >= 0) {

                    purchaseControlStock.getStockInfo().setCurrentPurchasePrice(purchaseControlStock.getStockInfo().getCurrentPurchasePrice().multiply((BigDecimal.valueOf(1).add((purchaseControlStock.getPurchaseKdv().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))))));

                    isThereCurrentPurchasePrice = true;
                    BigDecimal bd = exchangeService.bringExchangeRate(purchaseControlStock.getStockInfo().getCurrentSaleCurrency(), purchaseControlStock.getStockInfo().getCurrentPurchaseCurrency(), sessionBean.getUser());
                    currentSalePrice = purchaseControlStock.getStockInfo().getCurrentSalePrice().multiply(bd);

                    if (purchaseControlStock.getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) != 0) {
                        currentProfitRate = ((currentSalePrice.subtract(purchaseControlStock.getStockInfo().getCurrentPurchasePrice())).divide(purchaseControlStock.getStockInfo().getCurrentPurchasePrice(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    }

                    BigDecimal profitability = BigDecimal.ZERO;
                    if (purchaseControlStock.getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) != 0) {
                        profitability = ((currentSalePrice.subtract(currentPurchasePrice)).divide(currentPurchasePrice, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    }

                    //Son alış-son satış karlılığı ile şimdiki alış-son satış karlılığı arasındaki farkın mutlak değeri
                    //karlılık toleransından büyük ise dialog aç.
                    if (nowDate.after(purchaseConstraintDate) && profitability.subtract(currentProfitRate).abs().doubleValue() >= sessionBean.getLastBranchSetting().getProfitabilityTolerance().doubleValue()) {
                        isControl = false;
                    }

                    stockRecomendedPrice = (((BigDecimal.valueOf(100).add(currentProfitRate)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))).multiply(currentPurchasePrice);

                    //fiyat listesi için karlılık hesapla
                    if (stockPrice != null) {
                        stockPrice.setProfitRate(profitability != null ? profitability : BigDecimal.ZERO);
                    }
                }

                if (stockPrice != null && stockPrice.getPrice() != null) {
                    stockPrice.setStock(obj.getStock());

                    listOfPriceListItem = new ArrayList<>();
                    listOfPriceListItem.add(stockPrice);

                    // Eğer şube ayarlarındaki satın alma birim fiyat kontrolü uygulansın seçeneği açık ise ve ürünün kontrol tarihi bugünden büyükse 
                    if (branchSetting.isIsPurchaseControl()
                            && nowDate.after(purchaseConstraintDate)
                            && currentSalePrice.doubleValue() < currentPurchasePrice.doubleValue()) {//Tarih kontrolü geçemedi

                        obj.setIsCanSaveItem(false);
                        isControl = false;
                    }

                } else {
                    listOfPriceListItem = new ArrayList<>();
                    obj.setIsCanSaveItem(true);
                }

                //kontrolden geçemedi ise dialog aç
                if (!isControl) {
                    context.update("dlg_purchasecontrolinformation");
                    context.execute("PF('dlg_purchasecontrolinformation').show()");
                    context.update("frmPurchaseControlInformation");
                }

            }

        }

        //isControl = true;
        return isControl;
    }

    @Override
    public void save() {
       
        if (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel()) {
            stockMinLevelControl(processType, selectedObject);
        }

        if ((selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59) && selectedObject.getStock().getStockInfo().getMaxStockLevel() != null) {
            maxStockLevelControl(processType, selectedObject);
        }

        if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {

            if (sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {

                if (processType == 1) {
                    controlStock();//aynı stok varmı bak
                }
                //satınalma değilse kaydet
                if (!selectedInvoice.isIsPurchase()) {
                    saveInvoiceItem();
                } else /*Eğer fatura satın alma faturası ise ve ürün akaryakıt ürünü ise tüpraş fiyatı ile karşılaştırma yapılır. */ if (selectedInvoice.isIsPurchase() && selectedObject.getStock().getStockInfo().isIsFuel()) {
                    BigDecimal refineryUnitPrice = fuelStockArticlesControl();
                    if (refineryUnitPrice.compareTo(BigDecimal.ZERO) == 1 && selectedObject.getUnitPrice().compareTo(refineryUnitPrice) == 1) {
                        RequestContext.getCurrentInstance().execute("PF('dlgConfirmRefineriPrice').show()");
                    } else {
                        cnfrmYes();
                    }
                } else {
                    cnfrmYes();
                }
            }

        }


    }

    public void cnfrmYes() {
        isControlPurchase = controlPurchaseConstraint(selectedObject); // satın alma faturası için kontrol sağlar , satış faturası için direkt ekleme yapar.
        //kontrolden geçti ise kaydet
        if (isControlPurchase) {
            saveInvoiceItem();
        }
    }

    public void saveInvoiceItem() {
        if (selectedObject.getQuantity() != null && selectedObject.getQuantity().doubleValue() <= 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    sessionBean.loc.getString("error"),
                    sessionBean.loc.getString("quantitycannnotbezero"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        if (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel()) {
            stockMinLevelControl(processType, selectedObject);
        }

        if ((selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59) && selectedObject.getStock().getStockInfo().getMaxStockLevel() != null) {
            maxStockLevelControl(processType, selectedObject);

        }

        if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
            if (selectedInvoice.isIsPeriodInvoice()) {//Müşteri Mutabakatlarından oluştuysa
                RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide()");
                for (Iterator<InvoiceItem> iterator = listOfObjects.iterator(); iterator.hasNext();) {
                    InvoiceItem value = iterator.next();
                    if (value.getId() == selectedObject.getId()) {
                        iterator.remove();
                        break;
                    }
                }
                listOfObjects.add(selectedObject);
                invoiceProcessBean.getListOfItemForAgreement().clear();
                invoiceProcessBean.getListOfItemForAgreement().addAll(listOfObjects);
                bringInvoiceDiscount();
                calcInvoicePrice();
                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");

            } else if (!isCreateInvFromWaybill && !isCreateInvFromOrder) {//irsaliyeden veya sipaişrten fatura oluşturma DEĞİL ise
                int result = 0;
                sCount = 0;
                //irsaliyeli fatura ise 
                boolean bcontrol = false;
                if (selectedInvoice.getType().getId() == 59) {
                    if (isOrderConnection) {

                        if (selectedObject.getQuantity().compareTo(selectedObject.getControlQuantity()) == 1) {
                            bcontrol = true;
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        } else {
                            if (selectedObject.getOrderItemIds() != null && !selectedObject.getOrderItemIds().equals("")) {
                                controlOrderQuantityForUpdate();
                            }

                        }

                    }

                    for (InvoiceItem item : listOfObjects) {
                        if (item.getStock().getId() == selectedObject.getStock().getId()) {
                            sCount++;
                        }
                    }
                }
                if (!bcontrol) {
                    selectedObject.setStockCount(sCount);
                    selectedObject.setInvoice(selectedInvoice);
                    confrimYes();
                }

            } else {//irsaliyeden veya sipaişrten fatura oluşturulacak ise dialogu sadece kapat ve json oluştur.

                if (isCreateInvFromWaybill) {
                    //tüm ürünlerin para birimi seçildi ise yani tüm alanlar dolduruldu ise json stringi oluştur.
                    //tüm ürünlerin tüm alanları doldurulmadı ise fatura oluşturmasına izin verme

                    if (selectedObject.getQuantity().compareTo(selectedObject.getControlQuantity()) == 1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide()");

                        invoiceProcessBean.setIsCreateInv(true);
                        for (InvoiceItem invi : listOfObjects) {
                            if (invi.getCurrency().getId() == 0) {
                                invoiceProcessBean.setIsCreateInv(false);
                                break;
                            }
                        }
                        calculater(1);
                        calcInvoicePrice();
                        for (Iterator<InvoiceItem> iterator = invoiceProcessBean.getListOfItemForWaybill().iterator(); iterator.hasNext();) {
                            InvoiceItem next = iterator.next();
                            if (next.getId() == selectedObject.getId()) {
                                iterator.remove();
                                break;
                            }
                        }
                        invoiceProcessBean.getListOfItemForWaybill().add(selectedObject);
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab:dtbPayments");
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmToolbarStock");
                        RequestContext.getCurrentInstance().update("frmInvoiceProcess:pgrInvoiceProcess");
                    }
                } else if (isCreateInvFromOrder) {
                    if (selectedObject.getOrderItemIds() != null && !selectedObject.getOrderItemIds().equals("") && selectedObject.getControlQuantity() != null && selectedObject.getQuantity().compareTo(selectedObject.getControlQuantity()) == 1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannotbegreaterthanorderamount") + " " + sessionBean.loc.getString("orderquantity") + ":" + selectedObject.getControlQuantity());
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        selectedObject.setStockCount(1);
                        if (processType == 1) {
                            listOfObjects.add(selectedObject);
                        }
                        RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide()");
                        //tüm ürünlerin para birimi seçildi ise yani tüm alanlar dolduruldu ise json stringi oluştur.
                        //tüm ürünlerin tüm alanları doldurulmadı ise fatura oluşturmasına izin verme
                        invoiceProcessBean.setIsCreateInv(true);
                        for (InvoiceItem invi : listOfObjects) {
                            if (invi.getQuantity() == null || invi.getQuantity().compareTo(BigDecimal.ZERO) <= 0 || invi.getUnitPrice() == null || invi.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                                invoiceProcessBean.setIsCreateInv(false);
                                break;
                            }
                        }

                        if (selectedObject.getOrderItemIds() != null && !selectedObject.getOrderItemIds().equals("")) {
                            controlOrderQuantity();
                        }
                        calculater(1);
                        calcInvoicePrice();
                        for (Iterator<InvoiceItem> iterator = invoiceProcessBean.getListOfItemForOrder().iterator(); iterator.hasNext();) {
                            InvoiceItem next = iterator.next();
                            if (next.getStock().getId() == selectedObject.getStock().getId()) {
                                iterator.remove();
                                break;
                            }
                        }
                        invoiceProcessBean.getListOfItemForOrder().add(selectedObject);
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab:dtbPayments");
                        RequestContext.getCurrentInstance().update("tbvInvoice:frmToolbarStock");
                        RequestContext.getCurrentInstance().update("frmInvoiceProcess:pgrInvoiceProcess");
                    }

                }

            }
        }
    }

    /**
     * Fatura genel kaydetme işlemini oluşturur.
     */
    public void confrimYes() {
        int result = 0;
        if (processType == 1) {
            selectedObject.setStockCount(sCount + 1);//henüz listeye eklenmediği için 
            if (selectedInvoice.isIsWait()) {
                boolean isExcess = false;
                BigDecimal totalQuantity = BigDecimal.ZERO;
                boolean isRemoved = false;
                if (selectedInvoice.getType().getId() == 23 || selectedInvoice.getType().getId() == 27) {
                    for (WaybillItem wi : listWaybillItem) {
                        if (wi.getStock().getId() == selectedObject.getStock().getId()) {
                            if (selectedObject.getQuantity().compareTo(wi.getRemainingQuantity()) == 1) {
                                isExcess = true;
                                break;
                            }
                        }
                    }
                }
                if (!isExcess) {
                    for (Iterator<InvoiceItem> iterator = listOfItemForWaitedInvoice.iterator(); iterator.hasNext();) {
                        boolean isThere = false;
                        InvoiceItem value = iterator.next();
                        if (value.getStock().getId() == selectedObject.getStock().getId() && value.getUnitPrice().compareTo(selectedObject.getUnitPrice()) == 0) {
                            int count = 1;
                            count++;
                            value.setStockCount(count);
                            totalQuantity = totalQuantity.add(value.getQuantity());
                            isThere = true;
                        }
                        if (isThere) {
                            iterator.remove();
                            isRemoved = true;
                        }
                    }
                    if (isRemoved) {
                        selectedObject.setQuantity(totalQuantity.add(selectedObject.getQuantity()));
                        calculater(1);
                    }
                    listOfItemForWaitedInvoice.add(selectedObject);
                    result = invoiceItemService.updateWaitedInvoiceJson(listOfItemForWaitedInvoice, selectedInvoice);
                } else {
                    result = -101;
                }

            } else {
                result = invoiceItemService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            }

        } else if (processType == 2) {
            // eğer güncellenecek ürün listede varsa ve price bilgisi listedeki ürünün price bilgisi ile aynı ise listedeki kayıt güncellenir,selectedObject silinir.
            BigDecimal totalQuantity = BigDecimal.ZERO;
            boolean isRemoved = false;
            boolean isExcess = false;
            if (selectedInvoice.isIsWait()) {
                if (selectedInvoice.getType().getId() == 23 || selectedInvoice.getType().getId() == 27) {
                    for (WaybillItem wi : listWaybillItem) {
                        if (wi.getStock().getId() == selectedObject.getStock().getId()) {
                            wi.setRemainingQuantity(wi.getRemainingQuantity().add(selectedObject.getQuantity()));
                            if (selectedObject.getQuantity().compareTo(wi.getRemainingQuantity()) == 1) {
                                isExcess = true;
                                break;
                            }

                        }
                    }
                }
            }
            if (!isExcess) {
                for (InvoiceItem list : listOfObjects) {
                    boolean isThere = false;

                    if (list.getStock().getId() == selectedObject.getStock().getId() && list.getUnitPrice().compareTo(selectedObject.getUnitPrice()) == 0 && selectedObject.getId() != list.getId()) {
                        int count = 1;
                        count++;
                        list.setStockCount(count);
                        totalQuantity = totalQuantity.add(list.getQuantity());
                        isThere = true;
                    }
                    if (isThere) {
                        list.setInvoice(selectedObject.getInvoice());
                        int returnId = invoiceItemService.delete(list);
                        if (returnId > 0) {
                            isRemoved = true;
                        }
                    }

                }
                if (isRemoved) {
                    selectedObject.setQuantity(totalQuantity.add(selectedObject.getQuantity()));
                    calculater(1);
                }
                if (selectedInvoice.isIsWait()) {
                    for (Iterator<InvoiceItem> iterator = listOfItemForWaitedInvoice.iterator(); iterator.hasNext();) {
                        InvoiceItem value = iterator.next();
                        if (value.getStock().getId() == selectedObject.getStock().getId() && value.getUnitPrice().compareTo(selectedObject.getUnitPrice()) == 0) {
                            iterator.remove();
                            break;
                        }
                    }
                    listOfItemForWaitedInvoice.add(selectedObject);
                    result = invoiceItemService.updateWaitedInvoiceJson(listOfItemForWaitedInvoice, selectedInvoice);
                } else {
                    result = invoiceItemService.update(selectedObject);
                }
            } else {
                result = -101;
            }

        }
        if (result > 0) {
            if (selectedInvoice.isIsWait()) {
                selectedInvoice.setWaitInvoiceItemJson(invoiceItemService.jsonArrayInvoiceItemsForWaitedInvoice(listOfItemForWaitedInvoice, selectedInvoice));
                jsonToListForWaitedInvoice();
            }
        }

        if (selectedObject.getWaybillItemIds() != null && !selectedObject.getWaybillItemIds().equals("")) {//ürün irsaliyeden aktarıldı ise liteyi yeniden çek
            listWaybillItem = waybillItemService.listWaybillItemForInvoice(selectedInvoice);
            if (selectedInvoice.isIsWait()) {
                for (Iterator<WaybillItem> iterator = listWaybillItem.iterator(); iterator.hasNext();) {
                    WaybillItem value = iterator.next();
                    for (InvoiceItem ii : listOfObjects) {
                        if (ii.getStock().getId() == value.getStock().getId()) {
                            value.setRemainingQuantity(value.getRemainingQuantity().subtract(ii.getQuantity()));
                        }
                    }
                    if (value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == 0 || value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == -1) {
                        iterator.remove();
                    }
                }
            }
            RequestContext.getCurrentInstance().update("frmInvoiceWaybillStock:dtbWaybillItem");
        }

        if (result > 0) {
            if (!selectedInvoice.isIsWait()) {
                listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
            }
            bringInvoiceDiscount();
            bringCurrency();
            calcInvoicePrice();
            invoiceProcessBean.setIsSendCenter(true);
            for (InvoiceItem listOfObject : listOfObjects) {
                listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
            }

            if (selectedInvoice.getType().getId() == 59 && !selectedInvoice.isIsPurchase() && !selectedObject.getStock().getStockInfo().isIsMinusStockLevel()) {
                if (selectedObject.getStock().getAvailableQuantity() != null && oldQuantity != null && selectedObject.getQuantity() != null) {
                    selectedObject.getStock().setAvailableQuantity(selectedObject.getStock().getAvailableQuantity().subtract(selectedObject.getQuantity().subtract(oldQuantity)));
                }

            } else if (selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59 && selectedObject.getStock().getStockInfo().getMaxStockLevel() != null) {
                if (selectedObject.getQuantity() != null && oldQuantity != null && selectedObject.getStock().getStockInfo().getBalance() != null) {

                    if (selectedObject.getQuantity().compareTo(oldQuantity) == -1) {
                        selectedObject.getStock().getStockInfo().setBalance(selectedObject.getStock().getStockInfo().getBalance().subtract(selectedObject.getQuantity().subtract(oldQuantity)));
                    } else {
                        selectedObject.getStock().getStockInfo().setBalance(selectedObject.getStock().getStockInfo().getBalance().add(selectedObject.getQuantity().subtract(oldQuantity)));
                    }

                }
            }

            RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide()");
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab:dtbPayments");
            RequestContext.getCurrentInstance().update("frmInvoiceProcess:pgrInvoiceProcess");
        }
        if (result == -101) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (result == -105) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisproductwasenteredbyanotheruser"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            if (!selectedInvoice.isIsWait()) {
                listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
            }
        } else {
            sessionBean.createUpdateMessage(result);
        }
    }

    public void bringUnit() {
        for (StockUnitConnection unitCon : listOfUnit) {
            if (selectedObject.getStock().getUnit().getId() == unitCon.getUnit().getId()) {
                selectedObject.getStock().getUnit().setSortName(unitCon.getUnit().getSortName());
                selectedObject.getStock().getUnit().setUnitRounding(unitCon.getUnit().getUnitRounding());
                selectedObject.setQuantity(null);
            }
        }
        RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:txtStockAmount");
    }

    /**
     * bu metot tabloda gosterılecek toplam iskonto tutarları hesaplar
     */
    public void bringInvoiceDiscount() {
        BigDecimal total = BigDecimal.ZERO;
        totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTemp = BigDecimal.ZERO;

        HashMap<BigDecimal, BigDecimal> taxList = new HashMap<>();
        for (InvoiceItem is : listOfObjects) {
            total = total.add(is.getTotalPrice());
            if (is.getDiscountPrice() != null && is.getDiscountPrice().doubleValue() > 0) {
                totalDiscount = totalDiscount.add(is.getDiscountPrice().multiply(is.getExchangeRate()));
            } else if (is.getDiscountRate() != null && is.getDiscountRate().doubleValue() > 0) {
                totalDiscount = totalDiscount.add((is.getTotalPrice().multiply(is.getDiscountRate()).movePointLeft(2)).multiply(is.getExchangeRate()));
            }
            if (is.getDiscountRate() != null) {
                totalTemp = is.getTotalPrice().subtract(is.getTotalPrice().multiply(is.getDiscountRate()));
            } else {
                totalTemp = is.getTotalPrice();
            }
            if (is.getDiscountPrice2() != null && is.getDiscountPrice2().doubleValue() > 0) {
                totalDiscount = totalDiscount.add(is.getDiscountPrice2().multiply(is.getExchangeRate()));
            } else if (is.getDiscountRate2() != null && is.getDiscountRate2().doubleValue() > 0) {
                totalDiscount = totalDiscount.add((totalTemp.multiply(is.getDiscountRate2()).movePointLeft(2)).multiply(is.getExchangeRate()));
            }
            if (taxList.containsKey(is.getTaxRate())) {//bu oran onceden vardır
                taxList.put(is.getTaxRate(), is.getTotalTax() != null ? taxList.get(is.getTaxRate()).add(is.getTotalTax().multiply(is.getExchangeRate())) : BigDecimal.ZERO);
            } else {//vergi grubu yok ise
                taxList.put(is.getTaxRate(), is.getTotalTax() != null ? is.getTotalTax().multiply(is.getExchangeRate()) : BigDecimal.ZERO);
            }
        }

        StringBuilder sb = new StringBuilder();
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        for (Map.Entry<BigDecimal, BigDecimal> me : taxList.entrySet()) {
            if (me.getKey() != null) {
                sb.append("(%");
                sb.append(formatter.format(me.getKey()));
                sb.append(" : ");
                sb.append(formatter.format(me.getValue()));
                sb.append(") ");
                sb.append(sessionBean.currencySignOrCode(selectedInvoice.getCurrency().getId(), 0));
                sb.append(" - ");
            }

        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 2, sb.length());
        }

        taxRates = sb.toString();
        invoiceProcessBean.setTotalPrice(total);

    }

    /**
     * kitaptan stok secıldıgınde calısır
     *
     */
    public void updateAllInformation() {

        if (stockBookFilterBean.getSelectedData() != null) {
            selectedObject.setStock(stockBookFilterBean.getSelectedData());

            //  selectedObject.getStock().setUnit(stockBookFilterBean.getSelectedData().getUnit());
            if ((!isCreateInvFromOrder || (isCreateInvFromOrder && processType == 1)) && !isCreateInvFromWaybill) {
                selectedObject.setQuantity(null);
                selectedObject.getStock().setUnit(stockBookFilterBean.getSelectedData().getUnit());
            } else {
                selectedObject.getStock().setUnit(selectedObject.getUnit());
            }
            selectedObject.setUnit(selectedObject.getStock().getUnit());
            if (selectedInvoice.getType().getId() == 59 && selectedInvoice.isIsFuel()) {
                listOfFuelWarehouse = invoiceItemService.findFuelStockWarehouse(selectedObject, selectedInvoice);
                if (!listOfFuelWarehouse.isEmpty()) {
                    if (listOfFuelWarehouse.size() == 1) {
                        selectedObject.setWarehouse(listOfFuelWarehouse.get(0));
                        isListFuelWarehouse = false;
                    } else {
                        isListFuelWarehouse = true;
                    }
                } else {
                    isListFuelWarehouse = true;
                }
            }
            if ((!isCreateInvFromOrder || (isCreateInvFromOrder && processType == 1)) && !isCreateInvFromWaybill) {
                listOfUnit = stockAlternativeUnitService.findStockUnitConnection(selectedObject.getStock(), selectedInvoice.getBranchSetting());
            }

            //Stoğun vergisini bul
            taxGroup = taxGroupService.findTaxGroupsKDV(selectedObject.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting());

            if (taxGroup != null) {//stoğun vergisi tanımlı ise
                selectedObject.setTaxRate(taxGroup.getRate());
            } else {
                selectedObject.setTaxRate(BigDecimal.ZERO);
            }

            System.out.println("isCreateInvFromOrder:" + isCreateInvFromOrder);
            System.out.println("isCreateInvFromWaybill:" + isCreateInvFromWaybill);

            if (!isCreateInvFromOrder && !isCreateInvFromWaybill) {

                System.out.println("updateAllInformation fiyat");

                ///////////////---------acenta istasyonu ise-------/////////
                if (selectedInvoice.isIsPurchase() && selectedInvoice.getBranchSetting().getBranch().isIsAgency()) {
                    selectedObject.setIsTaxIncluded(true);
                    if (selectedObject.getStock().getStockInfo().getPurchaseCurrency().getId() == 0) {
                        selectedObject.getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                    } else {
                        selectedObject.getCurrency().setId(selectedObject.getStock().getStockInfo().getPurchaseCurrency().getId());
                    }
                    selectedObject.setUnitPrice(selectedObject.getStock().getStockInfo().getPurchaseRecommendedPrice());

                } else {
                    priceList = priceListItemService.findStockPrice(selectedObject.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting().getBranch());
                    if (priceList != null) {//fiyat listesinde var ise
                        selectedObject.setIsTaxIncluded(priceList.isIs_taxIncluded());
                        selectedObject.getCurrency().setId(priceList.getCurrency().getId());
                        selectedObject.setUnitPrice(priceList.getPrice());
                    } else {
                        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                        selectedObject.setIsTaxIncluded(false);
                    }
                }
            }

            isMaxStockLevel = false;
            isMinStockLevel = false;
            changeIsFree();
            bringCurrency();
            calculater(1);
            RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:grdInvoiceStokProcess");
            stockBookFilterBean.setSelectedData(null);

        }

    }

    /**
     * Bu metot akaryakıy ürünü için tüpraş fiyatını kontrol eder.Satın alınan
     * fiyat hesaplanan fiyattan fazla ise uyarı verir.
     */
    public BigDecimal fuelStockArticlesControl() {
        ContractArticles contractArticles = new ContractArticles();
        RefineryStockPrice refineryStockPrice = new RefineryStockPrice();

        BigDecimal total = BigDecimal.ZERO;

        contractArticles = contractArticleService.findStockArticles(selectedObject.getStock().getId(), selectedInvoice.getBranchSetting().getBranch());
        refineryStockPrice = refineryPurchaseService.findStockRefineryPrice(selectedObject.getStock().getId(), selectedInvoice.getBranchSetting().getBranch());
        InvoiceItem invoiceItem = new InvoiceItem();

        Date begin = new Date();
        Date end = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        begin = (calendar.getTime());
        invoiceItem = invoiceItemService.totalQuantityForInvoice(selectedInvoice, selectedObject.getStock().getId(), begin, end, selectedInvoice.getBranchSetting().getBranch());
        BigDecimal generalQuantity = BigDecimal.ZERO;
        if (invoiceItem.getQuantity() != null) {
            if (invoiceItem.getQuantity().compareTo(BigDecimal.ZERO) == 0) { // o ay içerisindeki o ürüne ait tüm alışları topladık.
                generalQuantity = selectedObject.getQuantity();
            } else {
                generalQuantity = invoiceItem.getQuantity().add(selectedObject.getQuantity());
            }
        } else {
            generalQuantity = selectedObject.getQuantity();
        }

        if (refineryStockPrice.getId() != 0 && refineryStockPrice.getPrice().compareTo(BigDecimal.ZERO) > 0 && contractArticles.getId() != 0) {
            total = BigDecimal.ZERO;
            BigDecimal priceRate = BigDecimal.ZERO;
            switch (contractArticles.getArticltType()) {
                case 1:
                    priceRate = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(contractArticles.getRate1())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                    total = (refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(priceRate);
                    break;
                case 2:
                    BigDecimal rate = BigDecimal.ZERO;
                    BigDecimal rateVal = BigDecimal.ZERO;
                    BigDecimal volume = BigDecimal.ZERO;
                    BigDecimal remainingVolume = BigDecimal.ZERO;
                    if (contractArticles.getVolume1().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume1()) == -1 || generalQuantity.compareTo(contractArticles.getVolume1()) == 0)) {
                        rate = contractArticles.getRate1();

                    } else if (contractArticles.getVolume2().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume2()) == -1 || generalQuantity.compareTo(contractArticles.getVolume2()) == 0) && generalQuantity.compareTo(contractArticles.getVolume1()) > 0) {
                        if (contractArticles.getVolume1().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume1()) == 1)) {
                            rateVal = contractArticles.getRate1();
                            volume = contractArticles.getVolume1();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume1());
                        }
                        rate = contractArticles.getRate2();
                    } else if (contractArticles.getVolume3().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume3()) == -1 || generalQuantity.compareTo(contractArticles.getVolume3()) == 0) && generalQuantity.compareTo(contractArticles.getVolume2()) > 0) {
                        if (contractArticles.getVolume2().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume2()) == 1)) {
                            rateVal = contractArticles.getRate2();
                            volume = contractArticles.getVolume2();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume2());

                        }
                        rate = contractArticles.getRate3();
                    } else if (contractArticles.getVolume4().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume4()) == -1 || generalQuantity.compareTo(contractArticles.getVolume4()) == 0) && generalQuantity.compareTo(contractArticles.getVolume3()) > 0) {
                        if (contractArticles.getVolume3().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume3()) == 1)) {
                            rateVal = contractArticles.getRate3();
                            volume = contractArticles.getVolume3();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume3());

                        }
                        rate = contractArticles.getRate4();
                    } else if (contractArticles.getVolume5().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume5()) == -1 || generalQuantity.compareTo(contractArticles.getVolume5()) == 0) && generalQuantity.compareTo(contractArticles.getVolume4()) > 0) {
                        if (contractArticles.getVolume4().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume4()) == 1)) {
                            rateVal = contractArticles.getRate4();
                            volume = contractArticles.getVolume4();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume4());
                        }
                        rate = contractArticles.getRate5();
                    }

                    if (rate.compareTo(BigDecimal.ZERO) == 1 && rateVal.compareTo(BigDecimal.ZERO) == 0) {
                        priceRate = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rate)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        total = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(priceRate));
                    } else if (rate.compareTo(BigDecimal.ZERO) == 1 && rateVal.compareTo(BigDecimal.ZERO) == 1) {
                        BigDecimal val1 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rateVal)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        BigDecimal tot1 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(val1));

                        BigDecimal val2 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rate)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        BigDecimal tot2 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(val2));

                        total = (((tot1.multiply(volume)).add(tot2.multiply(remainingVolume))).divide(generalQuantity, 4, RoundingMode.HALF_EVEN));
                    } else {
                        total = BigDecimal.ZERO;
                    }
                    break;
                case 3:
                    PriceListItem priceList = priceListItemService.findStockPrice(selectedObject.getStock(), false, selectedInvoice.getBranchSetting().getBranch());
                    BigDecimal t1 = BigDecimal.ZERO;
                    BigDecimal m1 = BigDecimal.ZERO;
                    BigDecimal m2 = BigDecimal.ZERO;
                    if (priceList.getPrice().compareTo(BigDecimal.ZERO) == 1) {
                        t1 = refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost());
                        m1 = priceList.getPrice().subtract(t1);
                        priceRate = (m1.multiply(contractArticles.getBranchProfitRate())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        total = priceList.getPrice().subtract(priceRate);
                    }
                    break;
            }
        }
        return total;
    }

    /**
     * Bu metot vergi dahil hariç seçeneği değiştiğinde tetiklenir. Yeniden
     * birim fiyat hesaplar.
     */
    public void changeIsTax() {

        if (selectedObject.getUnitPrice() != null) {
            if (!selectedObject.isIsTaxIncluded()) {//kdv dahilden hariç hesaplandı(istax false ise vergi dahilden harice çekilmiştir.)
                BigDecimal x = BigDecimal.ONE.add(selectedObject.getTaxRate().divide(new BigDecimal(100), 16, RoundingMode.HALF_EVEN));
                selectedObject.setUnitPrice(selectedObject.getUnitPrice().divide(x, 16, RoundingMode.HALF_EVEN));
            } else {//kdv hariçten dahil hesaplandı(istax true ise vergi hariçten dahile çekilmiştir.)
                BigDecimal x = BigDecimal.ONE.add(selectedObject.getTaxRate().divide(new BigDecimal(100), 16, RoundingMode.HALF_EVEN));
                selectedObject.setUnitPrice(selectedObject.getUnitPrice().multiply(x));
            }
            calculaterIsNotCalcTotalPrice();
        }
    }

    /**
     * Bu metot vergisiz tutar girildiğinde önce birim fiyat var ise miktar
     * hesaplar. birim fiyat yok miktar girildi ise birim fiyat hesaplar
     */
    public void changeTotalPrice() {

        //miktar varsa ve vergisiz tutar giriliyorsa birim fiyat hesapla
        if (selectedObject.getQuantity() != null && selectedObject.getQuantity().doubleValue() > 0
                && selectedObject.getTotalPrice() != null && selectedObject.getTotalPrice().doubleValue() > 0) {

            BigDecimal unTaxPrice = (selectedObject.getTotalPrice().add(selectedObject.getDiscountPrice()).add(selectedObject.getInvoiceDiscountPrice()).add(selectedObject.getDiscountPrice2())).divide(selectedObject.getQuantity(), 4, RoundingMode.HALF_EVEN);
            if (!selectedObject.isIsTaxIncluded()) {
                selectedObject.setUnitPrice(unTaxPrice);
            } else {
                BigDecimal x = BigDecimal.ONE.add(selectedObject.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
                selectedObject.setUnitPrice(unTaxPrice.multiply(x));
            }
        }
        calculaterIsNotCalcTotalPrice();

    }

    /**
     * Bu metot total price değerini yeniden hesaplamak istemediğimz yerlerde
     * çalışır. hesaplar
     *
     */
    public void calculaterIsNotCalcTotalPrice() {
        selectedObject.setIsNotCalcTotalPrice(true);
        calculater(1);
        selectedObject.setIsNotCalcTotalPrice(false);

    }

    /**
     * Bu metot ürnün birim fiyat iskonto vergi ve total money alanlarını
     * hesaplar
     *
     */
    public void calculater(int type) {
        selectedObject = invoiceItemService.calculater(selectedObject, type);
        selectedObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(selectedObject));

    }

    /**
     * Bu metot tablonun alt toplamlarını göstermek için tüm listeyi döner
     *
     * @return
     */
    public BigDecimal sumTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem invoiceStock : listOfObjects) {
//            if (invoiceStock.getTotalPrice() != null && invoiceStock.getTotalPrice().doubleValue() > 0) {
//                total = total.add(invoiceStock.getTotalPrice());
//            }
//            if (invoiceStock.getDiscountPrice() != null && invoiceStock.getDiscountPrice().doubleValue() > 0) {
//                total = total.add(invoiceStock.getDiscountPrice());
//            }
            total = total.add((invoiceStock.getUnitPrice() == null ? BigDecimal.ZERO : invoiceStock.getQuantity() == null ? BigDecimal.ZERO : invoiceStock.getTotalPrice()).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate()));
        }
        return total;
    }

    /**
     * faturya stok eklendiğinde silindiğinde veya güncellendiğinde fatura
     * tutarını yenıden hesaplar ------!!!!Remotecommand ile faturanın iskonto
     * oranı değiştiğinde de tetiklenir.
     */
    public void calcInvoicePrice() {
        System.out.println("selectedInvoice.getDiscountPrice 5:" + selectedInvoice.getDiscountPrice());
        System.out.println("selectedInvoice.getDiscountRate() 5:" + selectedInvoice.getDiscountRate());
        selectedInvoice.setTotalTax(BigDecimal.ZERO);
        selectedInvoice.setTotalPrice(BigDecimal.ZERO);
        selectedInvoice.setTotalMoney(BigDecimal.ZERO);
        BigDecimal invTotalPrice = BigDecimal.ZERO;
        selectedInvoice.setTotalProfit(BigDecimal.ZERO);
        BigDecimal profitTotal = BigDecimal.ZERO;
        BigDecimal tempTotal = BigDecimal.valueOf(0);
        System.out.println("----listOfObject size---" + listOfObjects.size());
        for (InvoiceItem invoiceStock : listOfObjects) {

            if (invoiceStock.getQuantity() == null || invoiceStock.getUnitPrice() == null || invoiceStock.getQuantity().doubleValue() == 0 || invoiceStock.getUnitPrice().doubleValue() == 0) {
                //hesaplama yapma
            } else {
                selectedInvoice.setTotalTax(selectedInvoice.getTotalTax().add((invoiceStock.getTotalTax() == null ? BigDecimal.ZERO : invoiceStock.getTotalTax()).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate())));
                selectedInvoice.setTotalPrice(selectedInvoice.getTotalPrice().add((invoiceStock.getTotalPrice() == null ? BigDecimal.ZERO : invoiceStock.getTotalPrice()).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate())));

                invTotalPrice = invTotalPrice.add(invoiceStock.getQuantity().multiply(invoiceStock.getUnitPrice()).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate()));
                if (invoiceStock.isIsDiscountRate()) {
                    if (invoiceStock.getDiscountRate() != null && invoiceStock.getDiscountRate().doubleValue() > 0) {
                        invoiceStock.setDiscountPrice(invoiceStock.getQuantity().multiply(invoiceStock.getUnitPrice()).multiply(invoiceStock.getDiscountRate()).divide(new BigDecimal(100), 8, RoundingMode.HALF_EVEN));
                    } else {
                        invoiceStock.setDiscountPrice(BigDecimal.ZERO);
                    }
                    invTotalPrice = invTotalPrice.subtract(invoiceStock.getDiscountPrice());
                } else if (!invoiceStock.isIsDiscountRate()) {//tutar girildi ise oran hesapla
                    if (invoiceStock.getDiscountPrice() != null && invoiceStock.getDiscountPrice().doubleValue() > 0) {
                        invTotalPrice = invTotalPrice.subtract(invoiceStock.getDiscountPrice());
                    }
                }
                tempTotal = invoiceStock.getQuantity().multiply(invoiceStock.getUnitPrice()).subtract(invoiceStock.getDiscountPrice() == null ? BigDecimal.valueOf(0) : invoiceStock.getDiscountPrice());
                //***2. iskonto hesaplaması
                if (invoiceStock.isIsDiscountRate2()) {
                    if (invoiceStock.getDiscountRate2() != null && invoiceStock.getDiscountRate2().doubleValue() > 0) {
                        invoiceStock.setDiscountPrice2(tempTotal.multiply(invoiceStock.getDiscountRate2()).divide(new BigDecimal(100), 8, RoundingMode.HALF_EVEN));
                    } else {
                        invoiceStock.setDiscountPrice2(BigDecimal.ZERO);
                    }
                    invTotalPrice = invTotalPrice.subtract(invoiceStock.getDiscountPrice2());
                } else if (!invoiceStock.isIsDiscountRate2()) {//tutar girildi ise oran hesapla
                    if (invoiceStock.getDiscountPrice2() != null && invoiceStock.getDiscountPrice2().doubleValue() > 0) {
                        invTotalPrice = invTotalPrice.subtract(invoiceStock.getDiscountPrice2());
                    }
                }
                //// ***
                if (invoiceStock.getProfitPrice() != null) {
                    System.out.println("-----PROFİT TOTAL---" + invoiceStock.getProfitPrice());

                    profitTotal = profitTotal.add(invoiceStock.getProfitPrice());
                }
            }
        }
        selectedInvoice.setTotalProfit(profitTotal);
        System.out.println("invTotalPrice:" + invTotalPrice);

        //faturada oran girildi ise tutar bul
        if (selectedInvoice.isIsDiscountRate()) {
            if (selectedInvoice.getDiscountRate() != null && selectedInvoice.getDiscountRate().doubleValue() > 0) {
                selectedInvoice.setDiscountPrice(invTotalPrice.multiply(selectedInvoice.getDiscountRate()).divide(new BigDecimal(100), 8, RoundingMode.HALF_EVEN));
            } else {
                selectedInvoice.setDiscountPrice(BigDecimal.ZERO);
            }
        } else if (!selectedInvoice.isIsDiscountRate()) {//tutar girildi ise oran hesapla
            if (invTotalPrice.compareTo(BigDecimal.ZERO) == 0) {
                selectedInvoice.setDiscountRate(BigDecimal.ZERO);
            } else if (selectedInvoice.getDiscountPrice() != null && selectedInvoice.getDiscountPrice().doubleValue() > 0) {
                selectedInvoice.setDiscountRate(new BigDecimal(100).multiply(selectedInvoice.getDiscountPrice()).divide(invTotalPrice, 8, RoundingMode.HALF_EVEN));
            } else {
                selectedInvoice.setDiscountRate(BigDecimal.ZERO);
            }
        }
        System.out.println("selectedInvoice.getDiscountPrice 6:" + selectedInvoice.getDiscountPrice());
        System.out.println("selectedInvoice.getDiscountRate() 6:" + selectedInvoice.getDiscountRate());

        selectedInvoice.setTotalMoney(selectedInvoice.getTotalPrice().add(selectedInvoice.getTotalTax()));

//        if (selectedInvoice.getDiscountPrice() != null && selectedInvoice.getDiscountPrice().doubleValue() > 0) {
//            selectedInvoice.setTotalMoney(selectedInvoice.getTotalMoney().subtract(selectedInvoice.getDiscountPrice()));
//        }
        if (selectedInvoice.getRoundingPrice() != null && selectedInvoice.getRoundingPrice().doubleValue() != 0) {
            if ((selectedInvoice.getRoundingPrice().doubleValue() > 0 && selectedInvoice.getRoundingPrice().doubleValue() <= branchSetting.getRoundingConstraint().doubleValue())
                    || (selectedInvoice.getRoundingPrice().doubleValue() < 0 && selectedInvoice.getRoundingPrice().doubleValue() >= branchSetting.getRoundingConstraint().multiply(new BigDecimal(-1)).doubleValue())) {
                selectedInvoice.setTotalMoney(selectedInvoice.getTotalMoney().add(selectedInvoice.getRoundingPrice()));
            }
        }

        if (!selectedInvoice.isIsPeriodInvoice()) {
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
        }

        listOfTaxs = new ArrayList<>();
        List<InvoiceItem> tempList = new ArrayList<>();
        for (InvoiceItem a : listOfObjects) {
            InvoiceItem i = new InvoiceItem();
            i.setTotalTax((a.getTotalTax() != null ? a.getTotalTax() : BigDecimal.valueOf(0)).multiply(a.getExchangeRate() != null ? a.getExchangeRate() : BigDecimal.valueOf(1)));
            i.setTaxRate(a.getTaxRate());
            if (i.getTotalTax().compareTo(BigDecimal.valueOf(0)) != 0) {
                tempList.add(i);
            }
        }

        listOfTaxs = tempList.stream()
                .collect(Collectors.groupingBy(
                        InvoiceItem::getTaxRate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                InvoiceItem::getTotalTax,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(e2 -> new InvoiceItem(e2.getValue(), e2.getKey()))
                .collect(Collectors.toList());

        RequestContext.getCurrentInstance().update("frmInvoiceProcess:pgrInvoiceProcess");

    }

    /**
     * bu metot yenı bır kalem eklendıgınde tabloda para bırımı gorunmesı ıcın
     */
    public void bringCurrency() {
        for (Currency c : sessionBean.getCurrencies()) {
            if (c.getId() == selectedObject.getCurrency().getId()) {
                selectedObject.getCurrency().setCode(c.getCode());
                selectedObject.getCurrency().setSign(c.getSign());

                selectedObject.setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                exchange = sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0) + " -> " + sessionBean.currencySignOrCode(selectedInvoice.getCurrency().getId(), 0);// örn: $->€

                break;
            }
        }
    }

    public void discountPriceOrRate(int type) {
        if (type == 0) {
            selectedObject.setIsDiscountRate(true);
        } else {
            selectedObject.setIsDiscountRate(false);
        }
    }

    public void discountPriceOrRate2(int type) {
        if (type == 0) {
            selectedObject.setIsDiscountRate2(true);
        } else {
            selectedObject.setIsDiscountRate2(false);
        }
    }

    /**
     * Hızlı ekleme dialogunda oran tutar ikonunu yönetir.
     *
     * @param type 0:oran , 1 :tutar
     */
    public void discountPriceOrRateFromQuickAdd(int type) {
        if (type == 0) {
            selectedStock.setIsDiscountRate(true);
        } else {
            selectedStock.setIsDiscountRate(false);
        }
        //calculater();
        RequestContext.getCurrentInstance().update("frmUpdate");
    }

    /**
     * faturanın iskontosunu hesaplar
     *
     */
    public void calcDiscountFromQuickAdd() {
        if (!selectedStock.isIsDiscountRate()) {//tutar girildi oran hesapla
            if (selectedStock.getDiscountPrice() != null && selectedStock.getDiscountPrice().doubleValue() > 0) {
                if (selectedStock.getTotalPrice() != null && selectedStock.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1) {
                    selectedStock.setDiscountRate(selectedStock.getDiscountPrice().multiply(new BigDecimal(100)).divide(selectedStock.getTotalPrice(), 4, RoundingMode.HALF_EVEN));
                } else {
                    selectedStock.setDiscountRate(BigDecimal.ZERO);
                }

            } else {
                selectedStock.setDiscountRate(BigDecimal.ZERO);
            }
        } else if (selectedStock.isIsDiscountRate()) {//oran girildi ise tutar hesapla
            if (selectedStock.getDiscountRate() != null && selectedStock.getDiscountRate().doubleValue() > 0) {
                selectedStock.setDiscountPrice(selectedStock.getTotalPrice().multiply(selectedStock.getDiscountRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
            } else {
                selectedStock.setDiscountPrice(BigDecimal.ZERO);
            }
        }

        // RequestContext.getCurrentInstance().update("frmUpdate");
    }

    public void calcDiscountFromUpdateData() {
        if (!stockInformation.isIsDiscountRate()) {//tutar girildi oran hesapla
            if (stockInformation.getDiscountPrice() != null && stockInformation.getDiscountPrice().doubleValue() > 0) {
                if (stockInformation.getTotalPrice() != null && stockInformation.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1) {
                    stockInformation.setDiscountRate(stockInformation.getDiscountPrice().multiply(new BigDecimal(100)).divide(selectedObject.getTotalPrice(), 4, RoundingMode.HALF_EVEN));
                } else {
                    stockInformation.setDiscountRate(BigDecimal.ZERO);
                }

            } else {
                stockInformation.setDiscountRate(BigDecimal.ZERO);
            }
        } else if (stockInformation.isIsDiscountRate()) {//oran girildi ise tutar hesapla
            if (stockInformation.getDiscountRate() != null && stockInformation.getDiscountRate().doubleValue() > 0) {
                stockInformation.setDiscountPrice(stockInformation.getTotalPrice().multiply(stockInformation.getDiscountRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
            } else {
                stockInformation.setDiscountPrice(BigDecimal.ZERO);
            }
        }

        // RequestContext.getCurrentInstance().update("frmUpdate");
    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {

            if (isCreateInvFromOrder) {
                listOfObjects.remove(selectedObject);
                for (Iterator<InvoiceItem> iterator = invoiceProcessBean.getListOfItemForOrder().iterator(); iterator.hasNext();) {
                    InvoiceItem next = iterator.next();
                    if (next.getStock().getId() == selectedObject.getStock().getId()) {
                        iterator.remove();
                        break;
                    }
                }
                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide();");
                sessionBean.createUpdateMessage(1);

            } else {
                int sCount = 0;
                //irsaliyeli fatura ise 
                if (selectedInvoice.getType().getId() == 59) {
                    for (InvoiceItem item : listOfObjects) {
                        if (item.getStock().getId() == selectedObject.getStock().getId()) {
                            sCount++;
                        }
                    }
                }
                selectedObject.setStockCount(sCount);

                int result = 0;
                if (selectedInvoice.isIsWait()) {
                    for (Iterator<InvoiceItem> iterator = listOfItemForWaitedInvoice.iterator(); iterator.hasNext();) {
                        InvoiceItem value = iterator.next();
                        if (value.getStock().getId() == selectedObject.getStock().getId() && value.getUnitPrice().compareTo(selectedObject.getUnitPrice()) == 0) {
                            iterator.remove();
                            break;
                        }
                    }
                    result = invoiceItemService.updateWaitedInvoiceJson(listOfItemForWaitedInvoice, selectedInvoice);
                } else {
                    result = invoiceItemService.delete(selectedObject);
                }

                RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide()");
                if (result > 0) {
                    if (selectedInvoice.isIsWait()) {
                        selectedInvoice.setWaitInvoiceItemJson(invoiceItemService.jsonArrayInvoiceItemsForWaitedInvoice(listOfItemForWaitedInvoice, selectedInvoice));
                        jsonToListForWaitedInvoice();
                        if (selectedInvoice.getType().getId() == 23 || selectedInvoice.getType().getId() == 27) {
                            listWaybillItem.clear();
                            for (WaybillItem wi : tempListOfWaybillItem) {
                                for (InvoiceItem ii : listOfObjects) {
                                    if (ii.getStock().getId() == wi.getStock().getId()) {
                                        wi.setRemainingQuantity(wi.getRemainingQuantity().add(ii.getQuantity()));
                                    }
                                }
                            }
                            for (WaybillItem wi : tempListOfWaybillItem) {
                                if (wi.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                                    listWaybillItem.add(wi);
                                }
                            }
                        }

                    } else {
                        listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
                    }

                    if (listOfFilteredObjects != null) {
                        listOfFilteredObjects.clear();
                    }
                    bringInvoiceDiscount();
                    calcInvoicePrice();
                    for (InvoiceItem listOfObject : listOfObjects) {
                        listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
                    }
                    RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                    invoiceProcessBean.setIsSendCenter(true);
                }
                sessionBean.createUpdateMessage(result);
            }
        }
    }

    //Satış faturasında stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde satış faturasıının silinmesi engellemek için çalışır.
    public void salesMaxStockLevelControl(InvoiceItem obj) {

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

    //Satınalma faturasında stoktaki stok eksi bakiyeye düşebilir mi paremetresine göre stok bakiyesinin eksiye düşmesini engellemek amacıyla kontrol yapar.
    public void stockPurchaseLevelControl(InvoiceItem obj) {

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

    public void testBeforeDelete() {
        if (selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59 && (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel())) { // Stok kartında ürün eksiye düşebilir mi seçili değilse
            stockPurchaseLevelControl(selectedObject);
        }

        if (!selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59 && (selectedObject.getStock().getStockInfo().getMaxStockLevel() != null)) {
            salesMaxStockLevelControl(selectedObject);
        }

        if ((((selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() != 27) || (selectedInvoice.getType().getId() == 27 && !selectedInvoice.isIsPurchase())) && (selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isPurchaseMinStockLevel)) || (((!selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() != 27) || (selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 27)) && (selectedObject.getStock().getStockInfo().getMaxStockLevel() == null || !isSalesMaxStockLevel)) || isCreateInvFromOrder) {
            if (sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {
                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";
                relatedRecord = "";
                response = 0;

                if (!controlDeleteList.isEmpty()) {
                    response = controlDeleteList.get(0).getR_response();
                    if (response < 0) { //Var bağlı ise silme uyarı ver
                        switch (response) {
                            case -101:
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtopricedifferenceinvoice");
                                deleteControlMessage1 = sessionBean.getLoc().getString("beforepleaseyoudeleteit");
                                deleteControlMessage2 = sessionBean.getLoc().getString("invoiceno") + " : ";
                                break;
                            default:
                                break;
                        }
                        relatedRecordId = controlDeleteList.get(0).getR_record_id();
                        relatedRecord = controlDeleteList.get(0).getR_recordno();
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordItem");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordItem').show();");
                    } else {//Sil
                        RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:dlgDelete");
                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                    }
                } else {
                    RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                }
            }
        }
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_StockProcess').hide();");
        context.execute("PF('dlg_RelatedRecordItem').hide();");
        context.execute("goToRelatedRecord();");
    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        switch (response) {
            case -101:
                Invoice invoice = new Invoice();
                invoice.setId(relatedRecordId);
                invoice = invoiceService.findInvoice(invoice);
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            default:
                break;
        }
    }

    public void bringStockType() {
        if (!isCreateInvFromOrder) {//Siparişten oluşmadıysa
            if (selectedObject.isIsService()) {
                bookType = "service";
            } else {
                bookType = "stock";
            }
        } else {//Siparişten oluştuysa hizmet ve kampanyalı ürünlerin gelmesi için
            if (selectedObject.isIsService()) {
                bookType = "serviceCreateInvFromOrder";
            } else {
                bookType = "stockCreateInvFromOrder";
            }
        }
    }

    /**
     * Bu metot toplam stok miktarını birime göre gruplar.
     *
     * @return
     */
    public String totalAmountText() {

        HashMap<String, BigDecimal> unitList = new HashMap<>();
        for (InvoiceItem is : listOfObjects) {
            if (unitList.containsKey(is.getUnit().getSortName())) {//bu oran onceden vardır
                unitList.put(is.getUnit().getSortName(), is.getQuantity() != null ? unitList.get(is.getUnit().getSortName()).add(is.getQuantity()) : BigDecimal.ZERO);
            } else {//vergi grubu yok ise
                unitList.put(is.getUnit().getSortName(), is.getQuantity() != null ? is.getQuantity() : BigDecimal.ZERO);
            }
        }

        StringBuilder sb = new StringBuilder();
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        for (Map.Entry<String, BigDecimal> me : unitList.entrySet()) {
            sb.append(formatter.format(me.getValue()));
            sb.append(me.getKey());
            sb.append(" - ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    /**
     * Bu metot irsaliye ürününü faturaya item olarak aktarma işlemini
     * gerçekleştirir.
     *
     * @param item
     * @param type 0 ise butona basılarak gelmiştir; 1 ise hepsi için tek tek
     * çağrılacaktır.
     */
    public void stockTransfer(WaybillItem item, int type) {

        //girilen miktar irsaliye miktarından büyük ise uyarı ver
        if (item.getQuantity().doubleValue() < item.getRemainingQuantity().doubleValue()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            processType = 1;
            selectedObject = new InvoiceItem();
            selectedObject.setTaxRate(BigDecimal.ZERO);
            selectedObject.setTotalTax(BigDecimal.ZERO);
            selectedObject.setInvoice(selectedInvoice);
            selectedObject.setStock(item.getStock());
            selectedObject.setQuantity(item.getRemainingQuantity());
            selectedObject.setCurrency(sessionBean.getUser().getLastBranch().getCurrency());
            selectedObject.setUnitPrice(BigDecimal.ZERO);
            selectedObject.setTotalMoney(BigDecimal.ZERO);
            selectedObject.setTotalPrice(BigDecimal.ZERO);
            selectedObject.setTotalTax(BigDecimal.ZERO);
            selectedObject.setTotalTax(BigDecimal.ZERO);
            selectedObject.setInvoice(selectedInvoice);
            selectedObject.setStockCount(1);
            selectedObject.setWaybillItemIds(String.valueOf(item.getId()));

            listOfUnit.clear();
            StockUnitConnection suc = new StockUnitConnection();
            suc.setStock(selectedObject.getStock());
            suc.setUnit(selectedObject.getStock().getUnit());
            listOfUnit.add(0, suc);

            //Stoğun vergisini bul
            taxGroup = taxGroupService.findTaxGroupsKDV(selectedObject.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting());

            if (taxGroup != null) {//stoğun vergisi tanımlı ise
                selectedObject.setTaxRate(taxGroup.getRate());
            } else {
                selectedObject.setTaxRate(BigDecimal.ZERO);
            }

            if (selectedInvoice.isIsPurchase() && selectedInvoice.getBranchSetting().getBranch().isIsAgency()) {
                selectedObject.setIsTaxIncluded(true);
                if (item.getStock().getStockInfo().getPurchaseCurrency().getId() == 0) {
                    selectedObject.getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                } else {
                    selectedObject.getCurrency().setId(item.getStock().getStockInfo().getPurchaseCurrency().getId());
                }
                selectedObject.setUnitPrice(item.getStock().getStockInfo().getPurchaseRecommendedPrice());

            } else {
                priceList = priceListItemService.findStockPrice(selectedObject.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting().getBranch());
                if (priceList != null) {//fiyat listesinde var ise
                    selectedObject.setIsTaxIncluded(priceList.isIs_taxIncluded());
                    selectedObject.getCurrency().setId(priceList.getCurrency().getId());
                    selectedObject.setUnitPrice(priceList.getPrice());

                } else {
                    selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                    selectedObject.setIsTaxIncluded(false);
                }
            }

            bringCurrency();
            calculater(1);
            calcInvoicePrice();

            //0:butona basıldı ise kaydet, 1:toplu aktarma
            if (type == 0 || type == 1) {

                controlStock();

                //kaydet
                if (type == 0) {
                    save();
                }

            } else {//2:manuel aktarma ise
                RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').show();");
                RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:grdInvoiceStokProcess");
            }
        }
    }

    /**
     * Bu metot gelen irsaliye altındaki tüm ürünleri faturanın ürünlerine ekler
     *
     * @param waybill
     */
    public void transferAll(Waybill waybill) {
        List<InvoiceItem> list = new ArrayList<>();
        List<InvoiceItem> listUpdate = new ArrayList<>();

        for (WaybillItem wbi : listWaybillItem) {
            if (wbi.getWaybill().getId() == waybill.getId()) {
                selectedObject = new InvoiceItem();
                stockTransfer(wbi, 1);

                if (processType == 1) {
                    list.add(selectedObject);//yeni ekleme

                } else {
                    listUpdate.add(selectedObject);//miktar artırma
                }
            }
        }

        int result = 0;

        if (!list.isEmpty()) {

            if (selectedInvoice.isIsWait()) {
                for (InvoiceItem a : list) {
                    listOfItemForWaitedInvoice.add(a);
                }
                result = invoiceItemService.updateWaitedInvoiceJson(listOfItemForWaitedInvoice, selectedInvoice);
            } else {
                result = invoiceItemService.createAll(list, selectedInvoice);
            }

            sessionBean.createUpdateMessage(result);
        }

        if (!listUpdate.isEmpty()) {

            if (selectedInvoice.isIsWait()) {
                for (InvoiceItem ii : listUpdate) {
                    for (Iterator<InvoiceItem> iterator = listOfItemForWaitedInvoice.iterator(); iterator.hasNext();) {
                        InvoiceItem value = iterator.next();
                        if (value.getId() == ii.getId() || (value.getStock().getId() == ii.getStock().getId() && value.getUnitPrice().compareTo(ii.getUnitPrice()) == 0)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
                for (InvoiceItem ii : listUpdate) {
                    listOfItemForWaitedInvoice.add(ii);
                }
                result = invoiceItemService.updateWaitedInvoiceJson(listOfItemForWaitedInvoice, selectedInvoice);
            } else {
                result = invoiceItemService.updateAll(listUpdate, selectedInvoice);
            }

            sessionBean.createUpdateMessage(result);
        }

        if (result > 0) {
            if (selectedInvoice.isIsWait()) {
                selectedInvoice.setWaitInvoiceItemJson(invoiceItemService.jsonArrayInvoiceItemsForWaitedInvoice(listOfItemForWaitedInvoice, selectedInvoice));
                jsonToListForWaitedInvoice();
            } else {
                listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
            }

            bringInvoiceDiscount();
            calcInvoicePrice();
            for (InvoiceItem listOfObject : listOfObjects) {
                listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
            }
            RequestContext.getCurrentInstance().update("frmInvoiceStokTab:dtbStock");

            listWaybillItem = waybillItemService.listWaybillItemForInvoice(selectedInvoice);
            if (selectedInvoice.isIsWait()) {
                for (Iterator<WaybillItem> iterator = listWaybillItem.iterator(); iterator.hasNext();) {
                    WaybillItem value = iterator.next();
                    for (InvoiceItem ii : listOfObjects) {
                        if (ii.getStock().getId() == value.getStock().getId()) {
                            value.setRemainingQuantity(value.getRemainingQuantity().subtract(ii.getQuantity()));
                        }
                    }
                    if (value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == 0 || value.getRemainingQuantity().compareTo(BigDecimal.valueOf(0)) == -1) {
                        iterator.remove();
                    }
                }
            }
            RequestContext.getCurrentInstance().update("frmInvoiceWaybillStock:dtbWaybillItem");
        }
    }

    /**
     * Bu metot irsaliye ürününü fatura aktarırken cell edit olayında tetiklenir
     *
     * @param event
     */
    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedWaybillItem = context.getApplication().evaluateExpressionGet(context, "#{item}", WaybillItem.class);

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        //girilen miktar irsaliye miktarından büyük ise uyarı ver
        if (selectedWaybillItem.getQuantity().doubleValue() < ((BigDecimal) newValue).doubleValue()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            selectedWaybillItem.setRemainingQuantity((BigDecimal) oldValue);
        } else {
            selectedWaybillItem.setRemainingQuantity((BigDecimal) newValue);
        }

    }

    /**
     * Bu metot eklenmek istenen stokun aynısı listede var ise miktarını artırır
     * güncelleme için hazırlar
     */
    public void controlStock() {
        BigDecimal selectedTemp = null;
        //eklenecek ürün faturada zaten var ise miktarını artır güncelle
        for (InvoiceItem invItem : listOfObjects) {

            if (selectedObject.isIsTaxIncluded()) {
                BigDecimal x = BigDecimal.ONE.add(selectedObject.getTaxRate().divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
                selectedTemp = selectedObject.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN);
            } else {
                selectedTemp = selectedObject.getUnitPrice();
            }
            //stoğun aynısı listede var ise
            if (selectedObject.getStock().getId() == invItem.getStock().getId()
                    && selectedObject.getCurrency().getId() == invItem.getCurrency().getId()
                    && selectedObject.getExchangeRate().compareTo(invItem.getExchangeRate()) == 0
                    && selectedTemp.compareTo(invItem.getUnitPrice()) == 0 //hep kdv hariçleri karşılaştırdık.
                    && selectedObject.getTaxRate().compareTo(invItem.getTaxRate()) == 0
                    && selectedObject.getId() != invItem.getId()) {

                selectedObject.setId(invItem.getId());

                //irsaliyeli fatura ise sadece miktarı artır
                if (selectedInvoice.getType().getId() == 59) {
                    selectedObject.setWaybillItemIds(invItem.getWaybillItemIds());
                    selectedObject.setQuantity(selectedObject.getQuantity().add(invItem.getQuantity()));
                    calculater(1);
                    calcInvoicePrice();

                    processType = 2;
                    break;
                } else {
                    //normal faturada irsaliye aktarrırken  farklı irsaliyelerde aynı üründen varsa
                    //miktar bilgisi veritabanına irsaliye miktarı kadar gönderilir.Miktar hesaplaması veritabanındaki prosedürde yapılır.
                    //Ama tutar hesaplamaları stoğu aynı olan irsaliyelerin toplam miktarı üzerinden yapılmalıdır.
                    //bu nedenle önce toplam miktar bulunarak tutar hesaplandı.
                    //daha sonra miktar tekrardan irsaliye miktarına set edildi.
                    BigDecimal tempQuantity = new BigDecimal(BigInteger.ZERO);
                    tempQuantity = selectedObject.getQuantity();

                    String[] ids = invItem.getWaybillItemIds().split(",");
                    for (int j = 0; j < ids.length; j++) {
                        System.out.println("ids[j]" + ids[j]);
                        //if (ids[j].equals(selectedObject.getWaybillItemIds())) {
                        selectedObject.setQuantity(selectedObject.getQuantity().add(new BigDecimal(invItem.getWaybillItemQuantitys().split(",")[j])));
                        //}
                    }

                    calculater(1);
                    calcInvoicePrice();

                    String[] ids1 = invItem.getWaybillItemIds().split(",");
                    for (int j = 0; j < ids1.length; j++) {
                        System.out.println("ids[j]" + ids1[j]);
                        if (ids[j].equals(selectedObject.getWaybillItemIds())) {
                            tempQuantity = tempQuantity.add(new BigDecimal(invItem.getWaybillItemQuantitys().split(",")[j]));
                        }
                    }
                    selectedObject.setQuantity(tempQuantity);

                    processType = 2;
                    break;
                }
            }
        }
    }

    public void openQuickAdd() {
        isMinStockLevel = false;
        isMaxStockLevel = false;
        isSpeedAdd = true;
        listOfProduct = new ArrayList<>();
        for (InvoiceItem obj : listOfObjects) { // ürün listesindeki tüm kayıtlar eklenir.Tip 1 güncellenecek kayıt , 0 eklenecek kayıt
            InvoiceItem obj2 = new InvoiceItem();

            BigDecimal oldQuantity = obj.getQuantity();

            obj.setOldQuantity(oldQuantity);
            obj.setItemProcessType(1);
            obj.setIsCanSaveItem(true);

            listOfProduct.add(obj);

        }
        selectedStock = new InvoiceItem();
        selectedStock.setIsDiscountRate(false);// iskonto tutarı açılacak
        RequestContext.getCurrentInstance().update("frmQuickAddProduct");
        RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').show()");

    }

    public void saveData() {

        String barcode = selectedStock.getStock().getBarcode();
        if (sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {
            if (selectedStock.getQuantity() != null && barcode != null) {
                if (selectedStock.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    boolean isThere = false;
                    for (InvoiceItem invoiceItem : listOfProduct) {
                        if (invoiceItem.getStock().getBarcode().equals(barcode)) {
                            isThere = true;
                            break;
                        }
                    }
                    if (!isThere) {// ürün faturaya daha önce eklenmemiş ise

                        BigDecimal oldQuantity = selectedStock.getQuantity();
                        BigDecimal oldDiscountRate = selectedStock.getDiscountRate();
                        BigDecimal oldDiscountPrice = selectedStock.getDiscountPrice();
                        boolean isRate = selectedStock.isIsDiscountRate();
                        selectedStock = invoiceItemService.findStock(barcode, selectedInvoice, false, selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());

                        selectedStock.setDiscountRate(oldDiscountRate == null ? BigDecimal.ZERO : oldDiscountRate);
                        selectedStock.setDiscountPrice(oldDiscountPrice == null ? BigDecimal.ZERO : oldDiscountPrice);
                        selectedStock.setIsDiscountRate(isRate);

                        //stok yoksa alternatif barkoddan bul
                        if (selectedStock.getStock().getId() <= 0) {
                            selectedStock = invoiceItemService.findStock(barcode, selectedInvoice, true, selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                            if (selectedStock.getQuantity() != null && selectedStock.getQuantity().doubleValue() > 0) {// alternatif barkod karşılığ ile miktar çarpılır
                                selectedStock.setQuantity(oldQuantity.multiply(selectedStock.getQuantity()));
                            }

                            //alternatif barkodla bulunan stok listede varsa
                            for (InvoiceItem invoiceItem : listOfProduct) {
                                if (invoiceItem.getStock().getId() == selectedStock.getStock().getId()) {
                                    //Barkodlar Aynı
                                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    isThere = true;
                                    break;
                                }
                            }
                            if (isThere) {
                                selectedStock = new InvoiceItem();
                                RequestContext.getCurrentInstance().update("frmQuickAddProduct");
                                return;
                            }

                        } else {
                            selectedStock.setQuantity(oldQuantity);
                            selectedStock.setDiscountRate(oldDiscountRate);
                            selectedStock.setDiscountPrice(oldDiscountPrice);
                            selectedStock.setIsDiscountRate(isRate);
                        }

                        if (selectedStock.getStock().getId() > 0) {

                            if ((selectedInvoice.isIsPurchase() && !selectedStock.getStock().getStockInfo().isIsDelist()) || !selectedInvoice.isIsPurchase()) {

                                if (selectedInvoice.getType().getId() == 59 && selectedInvoice.isIsFuel()) {
                                    listOfFuelWarehouse = invoiceItemService.findFuelStockWarehouse(selectedStock, selectedInvoice);
                                    if (!listOfFuelWarehouse.isEmpty()) {
                                        if (listOfFuelWarehouse.size() == 1) {
                                            selectedStock.setWarehouse(listOfFuelWarehouse.get(0));
                                            isListFuelWarehouse = false;
                                        } else {
                                            isListFuelWarehouse = true;
                                        }
                                    } else {
                                        isListFuelWarehouse = true;
                                    }
                                }

                                if (!selectedStock.getStock().getStockInfo().isIsMinusStockLevel()) {
                                    stockMinLevelControl(3, selectedStock);
                                }

                                if ((selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59) && selectedStock.getStock().getStockInfo().getMaxStockLevel() != null) {
                                    maxStockLevelControl(3, selectedStock);
                                }

                                if ((selectedStock.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {

                                    selectedStock.setStockCount(1);
                                    selectedStock.setDiscountPrice(selectedStock.getDiscountPrice() == null ? BigDecimal.ZERO : selectedStock.getDiscountPrice());
                                    selectedStock.setDiscountRate(selectedStock.getDiscountRate() == null ? BigDecimal.ZERO : selectedStock.getDiscountRate());
                                    selectedStock.setTaxRate(BigDecimal.ZERO);
                                    selectedStock.setTotalTax(BigDecimal.ZERO);
                                    selectedStock.setCurrency(selectedInvoice.getCurrency());
                                    selectedStock.setInvoice(selectedInvoice);
                                    selectedStock.setItemProcessType(0);
                                    selectedStock.setIsCanSaveItem(true);
                                    selectedStock.setIsDiscountRate(isRate);

                                    //Stoğun vergisini bul
                                    selectedStockTaxGroup = new TaxGroup();
                                    selectedStockTaxGroup = taxGroupService.findTaxGroupsKDV(selectedStock.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting());

                                    if (selectedStockTaxGroup != null) {//stoğun vergisi tanımlı ise
                                        selectedStock.setTaxRate(selectedStockTaxGroup.getRate());
                                    } else {
                                        selectedStock.setTaxRate(BigDecimal.ZERO);
                                    }
                                    if (selectedInvoice.isIsPurchase() && selectedInvoice.getBranchSetting().getBranch().isIsAgency()) {
                                        selectedStock.setIsTaxIncluded(true);
                                        if (selectedStock.getStock().getStockInfo().getPurchaseCurrency().getId() == 0) {
                                            selectedStock.getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                                        } else {
                                            selectedStock.getCurrency().setId(selectedStock.getStock().getStockInfo().getPurchaseCurrency().getId());
                                        }
                                        selectedStock.setUnitPrice(selectedStock.getStock().getStockInfo().getPurchaseRecommendedPrice());
                                    } else {
                                        stockPriceList = new PriceListItem();
                                        stockPriceList = priceListItemService.findStockPrice(selectedStock.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting().getBranch());
                                        if (stockPriceList != null) {//fiyat listesinde var ise
                                            selectedStock.setIsTaxIncluded(stockPriceList.isIs_taxIncluded());
                                            selectedStock.getCurrency().setId(stockPriceList.getCurrency().getId());
                                            selectedStock.setUnitPrice(stockPriceList.getPrice());

                                        } else {
                                            selectedStock.getCurrency().setId(selectedInvoice.getCurrency().getId());
                                            selectedStock.setIsTaxIncluded(false);
                                            selectedStock.setUnitPrice(BigDecimal.ZERO);
                                            selectedStock.setExchangeRate(selectedInvoice.getExchangeRate());
                                        }
                                    }

                                    selectedStock.setExchangeRate(exchangeService.bringExchangeRate(selectedStock.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser())); //

                                    selectedStock = invoiceItemService.calculater(selectedStock, 1);

                                    calcDiscountFromQuickAdd();

                                    bringInvoiceDiscount();
                                    calcInvoicePrice();

                                    if (selectedInvoice.getType().getId() == 59 && !selectedInvoice.isIsPurchase() && !selectedStock.getStock().getStockInfo().isIsMinusStockLevel()) {
                                        if (selectedStock.getStock().getAvailableQuantity() != null && selectedStock.getQuantity() != null) {
                                            oldQuantity = BigDecimal.ZERO;
                                            selectedStock.getStock().setAvailableQuantity(selectedStock.getStock().getAvailableQuantity().subtract(selectedStock.getQuantity().subtract(oldQuantity)));

                                        }
                                    } else if (selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59 && selectedStock.getStock().getStockInfo().getMaxStockLevel() != null) {

                                        if (selectedStock.getQuantity() != null && selectedStock.getStock().getStockInfo().getBalance() != null) {
                                            oldQuantity = BigDecimal.ZERO;
                                            if (selectedStock.getQuantity().compareTo(oldQuantity) == -1) {
                                                selectedStock.getStock().getStockInfo().setBalance(selectedStock.getStock().getStockInfo().getBalance().subtract(selectedStock.getQuantity().subtract(oldQuantity)));
                                            } else {
                                                selectedStock.getStock().getStockInfo().setBalance(selectedStock.getStock().getStockInfo().getBalance().add(selectedStock.getQuantity().subtract(oldQuantity)));
                                            }
                                        }

                                    }
                                    listOfProduct.add(0, selectedStock);
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

                    } else if (isThere) {
                        //Barkodlar Aynı
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {
                    ///Miktar Hatalı
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterquantityinformation"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else { // barkod veya miktar boş olamaz
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasecheckthebarcodeandquantityinformation"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

            selectedStock = new InvoiceItem();
            RequestContext.getCurrentInstance().update("frmQuickAddProduct");
        }
    }

    /**
     * Hıxlı eklemede rafineri fiyat kontrolünü sorar.Evet denirse çalışır
     */
    /**
     * Bu metot oluşturulan textBoxlarda değiştirilen stok ve miktar
     * bilgilerinin güncellenmesi için kullanılır.
     *
     * @param item
     * @param type 0 ise total değişmiştir.birim fiyat hesaplar 1 ise miktar
     * değişmiştir total hesaplar
     */
    public void updateData(InvoiceItem item, int type) {
        boolean isThereIterator = false;

        if (item.getStock().getBarcode() != null || item.getQuantity() != null || item.getTotalMoney() != null) {

            if (item.getQuantity().compareTo(BigDecimal.ZERO) > 0) {

                if (item.getId() == 0) {//hızlı ekleme ise
                    findStockInformation(item.getStock().getBarcode());

                    //ürün listede var ise çıkar
                    for (Iterator<InvoiceItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
                        InvoiceItem next = iterator.next();
                        if (next.getStock().getId() == item.getStock().getId()) {
                            oldQuantity = BigDecimal.ZERO;
                            stockInformation.setOldQuantity(oldQuantity);

                            isThereIterator = true;
                            stockInformation.setQuantity(next.getQuantity());
                            stockInformation.setTotalPrice(next.getTotalPrice());
                            iterator.remove();
                            break;
                        }
                    }

                } else {// listeden geldi ise
                    stockInformation = item;

                }

                if (stockInformation.getStock().getId() > 0) {

                    if (!stockInformation.getStock().getStockInfo().isIsMinusStockLevel()) { //Stok kartında stok eksi bakiyeye düşebilir mi seçeneği işaretli değil ise eksi bakiye kontrolü yapar.

                        oldQuantity = stockInformation.getOldQuantity();
                        stockMinLevelControl(4, stockInformation);
                    }

                    if ((selectedInvoice.isIsPurchase() && selectedInvoice.getType().getId() == 59) && stockInformation.getStock().getStockInfo().getMaxStockLevel() != null) {//Stokta max ürün seviyesi tanımlı ise max ürün seviyesi kontrolü yapar
                        oldQuantity = stockInformation.getOldQuantity();
                        maxStockLevelControl(4, stockInformation);
                    }

                    if ((stockInformation.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {

                        stockInformation.setDiscountPrice(item.getDiscountPrice());
                        stockInformation.setDiscountRate(item.getDiscountRate());
                        stockInformation.setIsDiscountRate(item.isIsDiscountRate());

                        changeTotalorQuantity(type);//birim fiyat veya total hesaplandı.

                        boolean isThere = false;
                        for (InvoiceItem allItem : listOfProduct) { // ürün açılan hızlı ekleme pencersinde var mı
                            if (allItem.getStock().getId() == stockInformation.getStock().getId()) {
                                isThere = true;
                                break;
                            }
                        }
                        if (!isThere || stockInformation.getId() > 0) { // hızlı ekleme listesinde yoksa veya önceden listeye eklenmişse
                            selectedObject = stockInformation;
                            if (!isThereIterator) { //satın alma faturası ise ve ürün daha önce listeden çıkartılmadı ise 

                                isControlPurchase = controlPurchaseConstraint(stockInformation);
                                if (item.getId() > 0) {// stok listeden geldi ise direkt gğncelle
                                    if (type == 0) {
                                        calculaterIsNotCalcTotalPrice();
                                    } else {
                                        calculater(1);
                                    }
                                    item = selectedObject;
                                    if (selectedInvoice.isIsPurchase() && selectedObject.getStock().getStockInfo().isIsFuel()) {
                                        BigDecimal refineryUnitPrice = fuelStockArticlesControl();
                                        if (refineryUnitPrice.compareTo(BigDecimal.ZERO) == 1 && selectedObject.getUnitPrice().compareTo(refineryUnitPrice) == 1) {
                                            RequestContext.getCurrentInstance().execute("PF('dlgConfirmRefineriPriceQuickAdd').show()");
                                        }
                                    }
                                } else if (item.getId() == 0) {
                                    if (type == 0) {
                                        calculaterIsNotCalcTotalPrice();
                                    } else {
                                        calculater(1);
                                    }
                                    listOfProduct.add(selectedObject);
                                    /*Eğer fatura satın alma faturası ise ve ürün akaryakıt ürünü ise tüpraş fiyatı ile karşılaştırma yapılır. */
                                    if (selectedInvoice.isIsPurchase() && selectedObject.getStock().getStockInfo().isIsFuel()) {
                                        BigDecimal refineryUnitPrice = fuelStockArticlesControl();
                                        if (refineryUnitPrice.compareTo(BigDecimal.ZERO) == 1 && selectedObject.getUnitPrice().compareTo(refineryUnitPrice) == 1) {
                                            RequestContext.getCurrentInstance().execute("PF('dlgConfirmRefineriPriceQuickAdd').show()");
                                        }
                                    }

                                }

                            } else {
                                if (type == 0) {
                                    calculaterIsNotCalcTotalPrice();
                                } else {
                                    calculater(1);
                                }
                                controlPurchaseConstraint(selectedObject);
                                listOfProduct.add(selectedObject);
                                /*Eğer fatura satın alma faturası ise ve ürün akaryakıt ürünü ise tüpraş fiyatı ile karşılaştırma yapılır. */
                                if (selectedInvoice.isIsPurchase() && selectedObject.getStock().getStockInfo().isIsFuel()) {
                                    BigDecimal refineryUnitPrice = fuelStockArticlesControl();

                                    if (refineryUnitPrice.compareTo(BigDecimal.ZERO) == 1 && selectedObject.getUnitPrice().compareTo(refineryUnitPrice) == 1) {
                                        RequestContext.getCurrentInstance().execute("PF('dlgConfirmRefineriPriceQuickAdd').show()");
                                    }
                                }
                            }

                        } else if (isThere) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("productavailableinlist"));
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }

                    }
                }
            } else {
                // 0 giremez
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterquantityinformation"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            for (Iterator<InvoiceItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
                InvoiceItem next = iterator.next();
                if (next.getStock().getId() == item.getStock().getId()) {
                    iterator.remove();
                    break;
                }
            }

        }

        RequestContext.getCurrentInstance().update("frmQuickAddProduct");
        //  RequestContext.getCurrentInstance().update("frmUpdate");

    }

    /**
     *
     * @param type 0 ise total değişmiştir, birim fiyat hesaplar 1 ise miktar
     * veya iskonto tutarı değişmiştir, total hesaplar
     */
    public void changeTotalorQuantity(int type) {
        if (type == 0) {
            BigDecimal oldTotalPrice = stockInformation.getTotalPrice();
            if (stockInformation.getDiscountPrice() != null && stockInformation.getDiscountPrice().doubleValue() > 0) {
                stockInformation.setTotalPrice(stockInformation.getTotalPrice().add(stockInformation.getDiscountPrice()));
            }

            BigDecimal unTaxPrice = stockInformation.getTotalPrice().divide(stockInformation.getQuantity(), 4, RoundingMode.HALF_EVEN);

            if (!stockInformation.isIsTaxIncluded()) {//vergi hariç ise direk bas
                stockInformation.setUnitPrice(unTaxPrice);
            } else { //vergi dahil ise vergisiz birim fiyat hesapla
                BigDecimal x = BigDecimal.ONE.add(stockInformation.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
                stockInformation.setUnitPrice(unTaxPrice.divide(x, 4, RoundingMode.HALF_EVEN));
            }

            stockInformation.setTotalPrice(oldTotalPrice);
        } else {

            stockInformation.setTotalPrice(stockInformation.getQuantity().multiply(stockInformation.getUnitPrice()));
            if (stockInformation.getDiscountPrice() != null) {
                stockInformation.setTotalPrice(stockInformation.getTotalPrice().subtract(stockInformation.getDiscountPrice()));//iskontoyu düştük
            }
        }

    }

    /**
     * Bu metot stok barcode bilgisine göre stoğa ait tüm bilgileri çekmek için
     * kullanılır.
     *
     * @param barcode
     */
    public void findStockInformation(String barcode) {
        stockInformation = new InvoiceItem();
        stockInformation = invoiceItemService.findStock(barcode, selectedInvoice, false, selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());

        //önce alternatif barkod varmı bak
        if (stockInformation.getStock().getId() == 0) {
            BigDecimal oldQuantity = selectedStock.getQuantity();
            //    BigDecimal oldDiscountRate=selectedStock.getDiscountRate();
            //    BigDecimal oldDiscountPrice=selectedStock.getDiscountPrice();
            stockInformation = invoiceItemService.findStock(barcode, selectedInvoice, true, selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
            if (stockInformation.getQuantity() != null && stockInformation.getQuantity().doubleValue() > 0) {
                stockInformation.setQuantity(oldQuantity.multiply(stockInformation.getQuantity()));
            }
        } else {
            stockInformation.setQuantity(selectedStock.getQuantity());
            stockInformation.setDiscountRate(selectedStock.getDiscountRate());
            stockInformation.setDiscountPrice(selectedStock.getDiscountPrice());
            stockInformation.setIsDiscountRate(selectedStock.isIsDiscountRate());
        }

        if (stockInformation.getStock().getId() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("stockinformationnotfound"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            stockInformation.setStockCount(1);
            stockInformation.setQuantity(selectedStock.getQuantity());
            stockInformation.setDiscountPrice(selectedStock.getDiscountPrice());
            stockInformation.setIsDiscountRate(selectedStock.isIsDiscountRate());
            stockInformation.setDiscountRate(selectedStock.getDiscountRate());
            stockInformation.setDiscountPrice2(BigDecimal.valueOf(0));
            stockInformation.setIsDiscountRate2(false);
            stockInformation.setDiscountRate2(BigDecimal.valueOf(0));
            stockInformation.setTaxRate(BigDecimal.ZERO);
            stockInformation.setTotalTax(BigDecimal.ZERO);
            stockInformation.setCurrency(selectedInvoice.getCurrency());
            stockInformation.setInvoice(selectedInvoice);
            stockInformation.setExchangeRate(selectedInvoice.getExchangeRate());

            //Stoğun vergisini bul
            TaxGroup tx = new TaxGroup();
            tx = taxGroupService.findTaxGroupsKDV(stockInformation.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting());

            if (tx != null) {//stoğun vergisi tanımlı ise
                stockInformation.setTaxRate(tx.getRate());
            } else {
                stockInformation.setTaxRate(BigDecimal.ZERO);
            }
            PriceListItem listItem = new PriceListItem();
            listItem = priceListItemService.findStockPrice(stockInformation.getStock(), selectedInvoice.isIsPurchase(), selectedInvoice.getBranchSetting().getBranch());
            if (listItem != null) {//fiyat listesinde var ise
                stockInformation.setIsTaxIncluded(listItem.isIs_taxIncluded());
                stockInformation.getCurrency().setId(listItem.getCurrency().getId());
                stockInformation.setUnitPrice(listItem.getPrice());

            } else {
                stockInformation.getCurrency().setId(selectedInvoice.getCurrency().getId());
                stockInformation.setIsTaxIncluded(false);
                stockInformation.setUnitPrice(BigDecimal.ZERO);
                stockInformation.setExchangeRate(selectedInvoice.getExchangeRate());
            }
            stockInformation.setExchangeRate(exchangeService.bringExchangeRate(stockInformation.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser())); //

            //stockInformation.setIsDiscountRate(false);
            stockInformation = invoiceItemService.calculater(stockInformation, 1);
            bringInvoiceDiscount();
            calcInvoicePrice();

        }
    }

    /**
     * Bu metot girilen tüm stokları kaydetmek için kullanılır.
     */
    public void createInvoiceProductItem() {
        boolean isChangeData = false; // itemlerde güncelleme veya yeni ekleme olduysa isSendCenter değişkeni true yapılmak için tutuldu
        List<InvoiceItem> updateList = new ArrayList<>();
        List<InvoiceItem> tempCreateList = new ArrayList<>();

        for (Iterator<InvoiceItem> iterator = listOfProduct.iterator(); iterator.hasNext();) { // updateleri çıkar , hızlı eklemeleri insert yap
            InvoiceItem next = iterator.next();
            if (next.getId() > 0) {
                updateList.add(next);
                iterator.remove();
            }
        }

        if (updateList.size() > 0) { // update işlemi 
            int result = invoiceItemService.updateAll(updateList, selectedInvoice);
            if (result > 0) {
                isChangeData = true;
                init();
                RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').hide()");
            } else if (result == -101) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
            sessionBean.createUpdateMessage(result);
        }

        if (listOfProduct.size() > 0) { // ekleme işlemi 
            for (InvoiceItem i : listOfProduct) {
                tempCreateList.add(0, i);
            }
            int result = invoiceItemService.createAll(tempCreateList, selectedInvoice);
            if (result > 0) {
                isChangeData = true;
                init();
                RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').hide()");
            } else if (result == -105) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisproductwasenteredbyanotheruser"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                if (!selectedInvoice.isIsWait()) {
                    listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
                    RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                }
            } else if (result == -101) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            RequestContext.getCurrentInstance().execute("PF('dlg_quickaddproduct').hide()");
        }
        if (isChangeData) {
            invoiceProcessBean.setIsSendCenter(true);
        }
        calcInvoicePrice();
        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");

    }

    /**
     * Bu metot kullanılan delete ikonu yardımıyla kayıt satırını silmek için
     * kullanılr.
     *
     * @param item
     */
    public void deleteRecord(InvoiceItem item) {
        for (Iterator<InvoiceItem> iterator = listOfProduct.iterator(); iterator.hasNext();) {
            InvoiceItem allItem = iterator.next();
            if (allItem.getStock().getId() == item.getStock().getId()) {
                iterator.remove();
                break;
            }
        }
        RequestContext.getCurrentInstance().update("frmQuickAddProduct");
    }

    public void updateTable() {
        if (!isCreateInvFromWaybill && !isCreateInvFromOrder) {
            if (selectedInvoice.isIsWait()) {
                jsonToListForWaitedInvoice();
            } else {
                listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
            }

            for (InvoiceItem listOfObject : listOfObjects) {
                listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
            }
            RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
        }
    }

    @Override
    public List<InvoiceItem> findall() {
        return invoiceItemService.listInvoiceStocks(selectedInvoice, "");
    }

    /**
     * Bu metot faturanın iskontosu değiştiğinde dialog açar ürünlerin tavsiye
     * edilen fiyatını hesaplar.
     */
    public void showDiscountChangeDialog() {
        BigDecimal newPurchase;
        BigDecimal recommended;
        BigDecimal salePrice;

        listOfPriceListItem = new ArrayList<>();

        for (InvoiceItem item : listOfObjects) {

            newPurchase = (item.getUnitPrice().multiply(item.getQuantity()))
                    .subtract(item.getDiscountPrice() == null ? BigDecimal.ZERO : item.getDiscountPrice())
                    .subtract(item.getDiscountPrice2() == null ? BigDecimal.ZERO : item.getDiscountPrice2());

            newPurchase = newPurchase.divide(item.getQuantity(), 4, RoundingMode.HALF_EVEN);
            newPurchase = newPurchase.multiply(BigDecimal.ONE.subtract((selectedInvoice.getDiscountRate() == null ? BigDecimal.ZERO : selectedInvoice.getDiscountRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN)));

            item.setUnitPrice(newPurchase);

            salePrice = item.getStock().getStockInfo().getCurrentSalePrice() == null ? BigDecimal.ZERO : item.getStock().getStockInfo().getCurrentSalePrice();

            recommended = item.getTotalPrice().divide(item.getQuantity(), 4, RoundingMode.HALF_EVEN);
            if (recommended.compareTo(BigDecimal.ZERO) == 0) {
                recommended = BigDecimal.ZERO;
            } else {
                recommended = (salePrice.subtract(recommended)).divide(recommended, 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100));
            }
            //satış fiyat listesinde yoksa kar oranını 0 bas
            if (salePrice.compareTo(BigDecimal.ZERO) == 0) {
                item.setOldProfitPercentage(BigDecimal.ZERO);
                item.setNewProfitPercentage(BigDecimal.ZERO);
            } else {
                item.setOldProfitPercentage(recommended);
                item.setNewProfitPercentage(recommended);
            }

            recommended = new BigDecimal(100).add(recommended);
            recommended = recommended.divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
            recommended = recommended.multiply(newPurchase);

            item.setRecommendedPrice(recommended);

            PriceListItem plItem = new PriceListItem();
            plItem.setStock(item.getStock());
            plItem.getStock().getStockInfo().setRecommendedPrice(recommended);
            plItem.getCurrency().setId(item.getCurrency().getId());
            plItem.getStock().getStockInfo().getCurrency().setId(item.getCurrency().getId());
            listOfPriceListItem.add(plItem);

        }
        RequestContext.getCurrentInstance().execute("PF('dlg_discountchangedialog').show();");
        RequestContext.getCurrentInstance().update("frmDiscountChangeDialog");
    }

    /**
     * Bu metot faturanın iskontosu değiştiğinde açılan dialogda önerilen satış
     * fiyatı değiştirilince tetiklenir. DB de değişiklik yapmaz. Yeni karlılık
     * hesaplar. Ali Kurt 31.01.2019
     *
     * @param event
     */
    public void onCellEditPrice(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        InvoiceItem invItem = context.getApplication().evaluateExpressionGet(context, "#{item}", InvoiceItem.class);
        BigDecimal newPrice = new BigDecimal(event.getNewValue().toString());
        BigDecimal oldPrice = new BigDecimal(event.getOldValue().toString());

        for (InvoiceItem item : listOfObjects) {
            if (item.getId() == invItem.getId()) {//önerilen fiyatı değiştirilen ürün ise

                if (item.getStock().getStockInfo().getSaleMandatoryPrice() != null && item.getStock().getStockInfo().getSaleMandatoryPrice().doubleValue() > 0) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    item.setRecommendedPrice(oldPrice);
                    //temp fiyat listemizde var ise sil 
                    for (Iterator<PriceListItem> iterator = listOfPriceListItem.iterator(); iterator.hasNext();) {
                        PriceListItem next = iterator.next();
                        if (next.getStock().getId() == item.getStock().getId()) {
                            iterator.remove();
                        }
                        break;
                    }
                    break;
                }

                //satış fiyatı satınalma fiyatından küçük olamaz!
                if (newPrice.doubleValue() < item.getUnitPrice().doubleValue() && sessionBean.getLastBranchSetting().isIsPurchaseControl()) {
                    item.setRecommendedPrice(oldPrice);
                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("thepurchasepriceoftheproductcannotbehigherthanthesalesprice"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    break;
                }

                item.setRecommendedPrice(newPrice);
                //yeni kar oranını bul
                item.setNewProfitPercentage((item.getRecommendedPrice().subtract(item.getUnitPrice())).divide(item.getUnitPrice(), 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));

                //temp fiyat listemizde var ise sil yeniden ekle
                for (Iterator<PriceListItem> iterator = listOfPriceListItem.iterator(); iterator.hasNext();) {
                    PriceListItem next = iterator.next();
                    if (next.getStock().getId() == item.getStock().getId()) {
                        iterator.remove();
                    }
                    break;
                }
                PriceListItem plItem = priceListItemService.findStockPrice(item.getStock(), false, selectedInvoice.getBranchSetting().getBranch());//satış fiyatı
                if (plItem != null) {//güncelleme yap
                    plItem.setPrice(newPrice);
                    plItem.setStock(item.getStock());
                    plItem.getStock().getStockInfo().setRecommendedPrice(newPrice);
                    plItem.getStock().getStockInfo().getCurrency().setId(plItem.getCurrency().getId());
                } else {//ekleme yap
                    plItem = new PriceListItem();
                    plItem.setStock(item.getStock());
                    plItem.setPrice(newPrice);
                    plItem.getStock().getStockInfo().setRecommendedPrice(newPrice);
                    plItem.getCurrency().setId(item.getCurrency().getId());
                    plItem.getStock().getStockInfo().getCurrency().setId(item.getCurrency().getId());
                }
                listOfPriceListItem.add(plItem);

                break;
            }
        }
    }

    /**
     * Bu metot önerilen satış fiyatlarında değişiklik olanları günceller veya
     * yeni girilenleri ekler. toplu işlem yapar. Ali Kurt 31.01.2019
     *
     * @param type
     */
    public void saveNewSalePrice() {
        if (!listOfPriceListItem.isEmpty()) {
            if (getRendered(143, 0)) {
                int result = priceListItemService.updatingPriceStock(new PriceList(), listOfPriceListItem, selectedInvoice.getBranchSetting().getBranch());
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_discountchangedialog').hide();");
                }
                sessionBean.createUpdateMessage(result);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pricelistcannotbeupdatedbecausethereisnoauthorization")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

        } else {
            RequestContext.getCurrentInstance().execute("PF('dlg_discountchangedialog').hide();");
        }
    }

    /**
     * Bu method fatura bazında iskontonun itemlara ne kadar yansıdığını bulmak
     * için kullanılır.
     *
     * @param invoiceItem
     * @return
     */
    public BigDecimal calcInvoiceItemInvoiceDiscountPrice(InvoiceItem invoiceItem) {

        BigDecimal invoiceItemInvoiceDiscountPrice = new BigDecimal(BigInteger.ZERO);
        if (invoiceItem.getQuantity() == null || invoiceItem.getUnitPrice() == null || invoiceItem.getQuantity().doubleValue() == 0 || invoiceItem.getUnitPrice().doubleValue() == 0) {

            invoiceItemInvoiceDiscountPrice = new BigDecimal(BigInteger.ZERO);

        } else if ((selectedInvoice.getDiscountPrice() != null && selectedInvoice.getDiscountPrice().doubleValue() > 0) || (selectedInvoice.getDiscountRate() != null && selectedInvoice.getDiscountRate().doubleValue() > 0)) {
            invoiceItemInvoiceDiscountPrice = invoiceItem.getUnitPrice().multiply(invoiceItem.getQuantity());

            if (invoiceItem.getTotalTax() != null && invoiceItem.getTotalTax().doubleValue() > 0) {
                invoiceItemInvoiceDiscountPrice = invoiceItemInvoiceDiscountPrice.add(invoiceItem.getTotalTax());
            }

            invoiceItemInvoiceDiscountPrice = invoiceItemInvoiceDiscountPrice.subtract(invoiceItem.getDiscountPrice()).subtract(invoiceItem.getDiscountPrice2() == null ? BigDecimal.valueOf(0) : invoiceItem.getDiscountPrice2());
            invoiceItemInvoiceDiscountPrice = (invoiceItem.getTotalMoney().subtract(invoiceItemInvoiceDiscountPrice)).multiply(BigDecimal.valueOf(-1));
        }

        return invoiceItemInvoiceDiscountPrice;

    }

    /*------------------------------------------------------Stok Excel Upload---------------------------------------------------                                */
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
        context.update("tbvInvoice:form:pgrFileUpload");
    }

    public void clearProducts() {
        stockItem = new Stock();
        excelStockList = new ArrayList<>();
        tempProductList = new ArrayList<>();
        tempStockList = new ArrayList<>();
        excelList = new ArrayList<>();
    }

    /**
     * Bu metot excel dosyası yüklemek için dialogu açar
     */
    public void openFileUpload() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvInvoice:frmtoolbar");
        context.update("tbvInvoice:form:pgrFileUpload");

        sampleList = new ArrayList<>();

        sampleList = invoiceItemService.createSampleList();

        context.execute("PF('dlg_stockfileupload').show();");
        context.update("tbvInvoice:dlgStockFileUpload");
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
        excelStockList.clear();
        excelStockList = invoiceItemService.processUploadFile(uploadedFile.getInputstream(), selectedInvoice);

        if (excelStockList.size() > 500) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("themaximumnumberofproductsthatcanbetransferredfromtheexcelfileshouldbe500pleaseedityourexcelfileandtryagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        } else {
            tempProductList.addAll(excelStockList);
            tempStockList.addAll(excelStockList);

            int count = 0;
            for (InvoiceItem obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
                if (obj.getExcelDataType() == 1) {
                    count++;
                    break;
                }
            }

            if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
                isOpenSaveBtn = true;
            }

            isOpenErrorData = false;

            context.execute("PF('dlg_productViewFirst').show();");
            context.update("tbvInvoice:dlgProductViewFirst");
            context.update("tbvInvoice:frmtoolbarfirst");
            context.update("tbvInvoice:frmProductViewFirst");
            context.update("tbvInvoice:frmProductView:dtbProductViewFirst");
            context.update("tbvInvoice:btnSaveFirst");

            isOpenCancelBtn = false;
        }

    }

    /**
     * Bu metot hatalı kayıtları göstermek/ gizlemek durumunda çalışır.Listeyi
     * günceller
     */
    public void showErrorProductListFirst() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (isOpenErrorData) {

            for (Iterator<InvoiceItem> iterator = tempStockList.iterator(); iterator.hasNext();) {
                InvoiceItem value = iterator.next();
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

        context.update("tbvInvoice:frmProductViewFirst:dtbProductViewFirst");
    }

    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {

            for (Iterator<InvoiceItem> iterator = tempStockList.iterator(); iterator.hasNext();) {
                InvoiceItem value = iterator.next();

                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }

            excelList.clear();
            excelList.addAll(tempStockList);
        } else {

            excelList.clear();
            excelList.addAll(tempProductList);
        }

        context.update("tbvInvoice:frmProductView:dtbProductView");
    }

    //Excelden okunan kayıtların stok bilgileri vs. kontrol ederek aktarım için uygun olup olmadığı bilgisi kullanıcıya gösterilir.
    public void saveControlItem() {
        isAll = false;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_productViewFirst').hide();");
        listOfSelectedItems.clear();
        excelList.clear();
        excelStockList.clear();

        for (InvoiceItem stock : tempProductList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }

        excelList = invoiceItemService.processExcelUpload(excelStockList, selectedInvoice);
        tempProductList.clear();
        tempStockList.clear();
        tempProductList.addAll(excelList);
        tempStockList.addAll(excelList);
        
        int count = 0;
        for (InvoiceItem obj : excelList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
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
        context.update("tbvInvoice:dlgProductView");
        context.update("tbvInvoice:frmtoolbar");
        context.update("tbvInvoice:frmProductView");
        context.update("tbvInvoice:frmProductView:dtbProductView");
        context.update("tbvInvoice:btnSave");

        isOpenCancelBtn = false;

    }

    public void saveItem() {

        if (listOfSelectedItems.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectatleastoneproducttobeinvoiced")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        } else {

            boolean isChangeData = false;//itemlerde değişiklik olunca İsDestroy true yapılmak için tutuldu
            errorList = new ArrayList<>();
            RequestContext context = RequestContext.getCurrentInstance();
            excelList.clear();

            for (InvoiceItem stock : listOfSelectedItems) {
                if (stock.getExcelDataType() == 1) {
                    excelList.add(stock);
                }
            }
            for (InvoiceItem excel : excelList) {
                BigDecimal saleUnitPrice = BigDecimal.ZERO;
                BigDecimal saleExchangeRate = BigDecimal.ZERO;
                if (!excel.getInvoice().isIsPurchase()) { // eğer satış ise zorunlu fiyatı kontrol eder.
                    if (excel.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                        if (excel.getStock().getStockInfo().getSaleMandatoryCurrency().getId() > 0) { // kur karşılaştırması yapılacak
                            saleExchangeRate = saleExchangeRate.add(exchangeService.bringExchangeRate(excel.getStock().getStockInfo().getSaleMandatoryCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                        } else { //Düzelt
                            saleExchangeRate = BigDecimal.ONE;
                        }
                        saleUnitPrice = saleUnitPrice.add(excel.getStock().getStockInfo().getSaleMandatoryPrice().multiply(saleExchangeRate));
                    } else {
                        saleUnitPrice = saleUnitPrice.add(excel.getUnitPrice());
                    }
                    excel.setUnitPrice(saleUnitPrice);
                    excel = invoiceItemService.calculater(excel, 2);
                }
            }

            for (InvoiceItem excel : excelList) {
                boolean isThere = false;
                BigDecimal totalQuantity = BigDecimal.ZERO;
                BigDecimal listUnitPrice = BigDecimal.ZERO;
                BigDecimal listUnitPriceRound = BigDecimal.ZERO;
                BigDecimal excelPriceRound = BigDecimal.ZERO;
                for (InvoiceItem list : listOfObjects) {
                    if (list.getTaxRate() != null) { // kdv her zaman hariç geldiği için hariçten dahil hespaladık.
                        listUnitPrice = list.getUnitPrice().add(list.getUnitPrice().multiply((list.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
                    }
                    // round yapar
                    listUnitPriceRound = (BigDecimal) (listUnitPrice.multiply(BigDecimal.valueOf(100))).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN); // round yapar .
                    excelPriceRound = (BigDecimal) (excel.getUnitPrice().multiply(BigDecimal.valueOf(100))).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN); // round yapar .
                    if (excel.getStock().getId() == list.getStock().getId()
                            && excelPriceRound.compareTo(listUnitPriceRound) == 0) {

                        totalQuantity = totalQuantity.add(excel.getQuantity()).add(list.getQuantity());
                        excel.setId(list.getId());
                        excel.setQuantity(totalQuantity);
                        excel.setWaybillItemIds(list.getWaybillItemIds());
                        excel.setProcessType(1);
                        excel.setStockCount(isThere ? (excel.getStockCount() - 1) : excel.getStockCount());
                        excel = invoiceItemService.calculater(excel, 2);
                        isThere = true;
                        break;
                    } else if (excel.getStock().getId() == list.getStock().getId() && excelPriceRound.compareTo(listUnitPrice) != 0) {
                        // stok count artıcak stok aynoı ise fiyat farklı ise
                        excel.setStockCount(excel.getStockCount() + 1);
                        excel.setWaybillItemIds(list.getWaybillItemIds());
                        isThere = true;

                    }
                }
                if (!isThere) {
                    excel.setId(0);
                    excel.setProcessType(0);
                }

            }

            String resultJson = invoiceItemService.jsonArrayForExcelUpload(selectedInvoice, excelList);
            excelList.clear();

            excelList.addAll(listOfSelectedItems);

            if (resultJson.isEmpty()
                    || resultJson.equals("[]")) {
                isChangeData = true;
                listOfObjects.clear();
                listOfObjects = findall();
                bringInvoiceDiscount();
                calcInvoicePrice();
                for (InvoiceItem listOfObject : listOfObjects) {
                    listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
                }
                RequestContext.getCurrentInstance().update("tbvInvoice:frmWaybillItemsTab:dtbItems");
                sessionBean.createUpdateMessage(1);
                context.execute("PF('dlg_productView').hide();");
                context.execute("PF('dlg_stockfileupload').hide();");

            } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
                JSONArray jsonArr = new JSONArray(resultJson);

                int count = 0;
                for (int m = 0; m < jsonArr.length(); m++) {

                    ErrorItem item = new ErrorItem();
                    item.setId(m + 1);
                    String jsonBarcode = jsonArr.getJSONObject(m).getString("barcode");
                    int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorcode");
                    item.setBarcode(jsonBarcode);
                    item.setErrorCode(jsonErrorCode);
                    switch (item.getErrorCode()) {
                        case -105:
                            item.setErrorString(sessionBean.getLoc().getString("thisproductwasenteredbyanotheruser"));
                            count++;

                            break;
                        case -1:
                            item.setErrorString(sessionBean.getLoc().getString("unsuccesfuloperation")); // sessionBean.getLoc().getString("")
                            break;
                        default:
                            break;
                    }
                    errorList.add(item);
                }

                if (!selectedInvoice.isIsWait() && count > 0) {

                    listOfObjects = invoiceItemService.listInvoiceStocks(selectedInvoice, "");
                    RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
                }

                if (errorList.size() != excelList.size()) {
                    isChangeData = true;
                    listOfObjects.clear();
                    listOfObjects = findall();
                    bringInvoiceDiscount();
                    calcInvoicePrice();
                    for (InvoiceItem listOfObject : listOfObjects) {
                        listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));
                    }
                    RequestContext.getCurrentInstance().update("tbvInvoice:frmWaybillItemsTab:dtbItems");
                }
                context.update("grwProcessMessage");
                context.execute("PF('dlg_productView').hide();");
                context.execute("PF('dlg_productErrorView').show();");
                context.update("tbvInvoice:frmProductErrorView:dtbProductErrorView");
            }
            if (isChangeData) {
                invoiceProcessBean.setIsSendCenter(true);
            }

        }

    }

    public void onCellEditPurchaseProfit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem = context.getApplication().evaluateExpressionGet(context, "#{viewProduct}", InvoiceItem.class);

        BigDecimal oldValue = BigDecimal.ZERO;
        BigDecimal newValue = BigDecimal.ZERO;
        if (event.getColumn().getClientId().contains("clmRecommendedSalesPrice")) {
            oldValue = (BigDecimal) event.getOldValue();
            newValue = (BigDecimal) event.getNewValue();
        }
        int result = 0;
        // kar oranını girilen satış fiyatına göre tekrar hesaplar.
        if (invoiceItem.isIsThereMandatoryPrice()) { // ürünün merkez tarafından belirlenen satış fyatı var ise önerilen satış fiyatını güncellemesine müsade edilmez
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            if (event.getColumn().getClientId().contains("clmRecommendedSalesPrice")) {
                selectedObj.setRecommendedSalesPrice((BigDecimal) event.getOldValue());
            }
            RequestContext.getCurrentInstance().execute("updateDatatable2();");
        } else {
            // eğer eski değer ve yeni değer farklı ise kar oranını bul
            if (newValue.compareTo(BigDecimal.ZERO) > 0 && (oldValue.compareTo(newValue) != 0) && (selectedObj.getUnitPrice().compareTo(BigDecimal.ZERO)) > 0) {
                selectedObj.setProfitRate(((newValue.subtract(invoiceItem.getUnitPrice())).divide(invoiceItem.getUnitPrice(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100)));
            } else {
                selectedObj.setProfitRate(BigDecimal.ZERO);
            }

            // satış fiyat listesine bakılır ürün eklenmiş mi diye
            PriceListItem stockPrice = null;
            stockPrice = priceListItemService.findStockPrice(selectedObj.getStock(), false, selectedInvoice.getBranchSetting().getBranch());// stoğun bsğlı olduğu şubedeki varsılan satış fiyat listesindeki price biligsini getirir.

            //fiyat listesinde vergi hariç ise kdv dahil fiyatını bulduk.Satın alma fiyatı vergi dahil ekleniyor çünkü
            if (stockPrice != null) {
                if (!stockPrice.isIs_taxIncluded()) {
                    TaxGroup taxGroup = taxGroupService.findTaxGroupsKDV(invoiceItem.getStock(), false, selectedInvoice.getBranchSetting());
                    BigDecimal recommendedPrice = stockPrice.getPrice().add(stockPrice.getPrice().multiply((taxGroup.getRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
                    if (((BigDecimal) event.getNewValue()).compareTo(BigDecimal.ZERO) > 0 && recommendedPrice.compareTo((BigDecimal) event.getNewValue()) != 0) {
                        stockPrice.setPrice((BigDecimal) event.getNewValue());
                        stockPrice.setStock(selectedObj.getStock());
                        result = priceListItemService.update(stockPrice);
                        // kaydetme işlemi mesajı verilir.
                        sessionBean.createUpdateMessage(result);
                    }
                } else if (((BigDecimal) event.getNewValue()).compareTo(BigDecimal.ZERO) > 0 && stockPrice.getPrice().compareTo((BigDecimal) event.getNewValue()) != 0) {

                    stockPrice.setPrice((BigDecimal) event.getNewValue());
                    stockPrice.setStock(selectedObj.getStock());

                    result = priceListItemService.update(stockPrice);
                    sessionBean.createUpdateMessage(result);
                }
            } else // eğer fiyat listesinde ürün yoksa ekleme yapacak
            if (((BigDecimal) event.getNewValue()).compareTo(BigDecimal.ZERO) > 0) {
                PriceListItem pli = new PriceListItem();
                pli.setStock(selectedObj.getStock());
                pli.setPrice((BigDecimal) event.getNewValue());
                pli.setCurrency(selectedInvoice.getCurrency());
                pli.setIs_taxIncluded(true);

                result = priceListItemService.createItem(pli, selectedInvoice.getBranchSetting().getBranch());
                sessionBean.createUpdateMessage(result);
            }
            RequestContext.getCurrentInstance().execute("updateDatatable2();");
        }

    }

    /**
     * Gridden iskonto oranı ve tutarı girilince çalışır
     *
     * @param event
     */
    public void onCellEditDiscount(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{invoiceStock}", InvoiceItem.class);
        if (event.getColumn().getClientId().contains("clmDiscountRate")) {
            discountPriceOrRate(0);
        } else if (event.getColumn().getClientId().contains("clmDiscountPrice")) {
            discountPriceOrRate(1);
        } else if (event.getColumn().getClientId().contains("clm2DiscountRate")) {
            discountPriceOrRate2(0);
        } else if (event.getColumn().getClientId().contains("clm2DiscountPrice")) {
            discountPriceOrRate2(1);
        }
        calculater(1);
        processType = 2;
        save();
        RequestContext.getCurrentInstance().execute("updateDatatableForDiscount();");
    }

    //Satın alma faturası e-faturadan gelmiş ise e-fatura stok bilgilerini görüntüler
    public void eInvoiceInformation() {
        listItem = incomingEInvoicesBean.bringEInvoiceItem(selectedInvoice);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_eInvoiceStock').show()");

    }

    //Satış faturasında stoktaki stok eksi bakiyeye düşebilir mi paremetresine göre stok bakiyesinin eksiye düşmesini engellemek amacıyla kontrol yapar.
    public void stockMinLevelControl(int processType, InvoiceItem obj) {
        if (processType == 1 || processType == 3) {
            if (selectedInvoice.getType().getId() == 59 && !selectedInvoice.isIsPurchase()) {
                if (obj.getStock().getAvailableQuantity() != null && obj.getQuantity() != null) {
                    if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity()) == -1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        isMinStockLevel = true;
                    } else {
                        isMinStockLevel = false;
                    }
                }

            }
        } else if (processType == 2 || processType == 4) {
            if (selectedInvoice.getType().getId() == 59 && !selectedInvoice.isIsPurchase()) {
                if (obj.getStock().getAvailableQuantity() != null && oldQuantity != null && obj.getQuantity() != null) {

                    if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        isMinStockLevel = true;
                        if (processType == 4) {
                            stockInformation.setQuantity(oldQuantity);
                        } else if (processType == 2) {
                            selectedObject.setQuantity(oldQuantity);
                        }
                    } else {
                        isMinStockLevel = false;
                    }
                }

            }
        }
    }

    //Satınalma faturasında, stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde alım yapılmasını engellemek için çalışır.
    public void maxStockLevelControl(int processType, InvoiceItem obj) {

        if (processType == 1 || processType == 3) {
            if (obj.getStock().getStockInfo().getBalance() != null && obj.getQuantity() != null) {

                BigDecimal quantity = new BigDecimal(BigInteger.ZERO);
                BigDecimal purchaseAmount = BigDecimal.ZERO;

                purchaseAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
                if (purchaseAmount.compareTo(obj.getQuantity()) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMaxStockLevel = true;
                } else {
                    isMaxStockLevel = false;
                }
            }

        } else if (processType == 2 || processType == 4) {
            if (obj.getStock().getStockInfo().getBalance() != null && obj.getQuantity() != null && oldQuantity != null) {

                BigDecimal prchAmount = BigDecimal.ZERO;
                prchAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());

                if (prchAmount.compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMaxStockLevel = true;

                    if (processType == 4) {
                        stockInformation.setQuantity(oldQuantity);
                    } else if (processType == 2) {
                        selectedObject.setQuantity(oldQuantity);
                    }
                } else {
                    isMaxStockLevel = false;
                }
            }

        }
    }

    /**
     * İrsaliyeden fatura oluşuyorsa ya da normal ve iade faturası ise ürünlerin
     * son fiyatını getirerek günceller
     */
    public void bringPriceForWaybill() {
        String stockList = "";
        List<InvoiceItem> changePriceList = new ArrayList<>();
        List<InvoiceItem> tempPriceList = new ArrayList<>();
        for (InvoiceItem invi : listOfObjects) {
            stockList = stockList + "," + String.valueOf(invi.getStock().getId());
            if (invi.getStock().getId() == 0) {
                stockList = "";
                break;
            }
        }

        if (selectedInvoice.getId() > 0) {

            if (!stockList.equals("")) {
                stockList = stockList.substring(1, stockList.length());
                tempPriceList = invoiceItemService.findInvoiceItemLastPrice(stockList, selectedInvoice.getBranchSetting());
                for (InvoiceItem invoiceItem : listOfObjects) {
                    for (InvoiceItem temp : tempPriceList) {
                        if (invoiceItem.getStock().getId() == temp.getStock().getId()) {
                            if (selectedInvoice.isIsPurchase()) {
                                invoiceItem.getStock().getStockInfo().setCurrentPurchasePrice(temp.getStock().getStockInfo().getCurrentPurchasePrice());
                                invoiceItem.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(temp.getStock().getStockInfo().getCurrentPurchaseCurrency().getId());
                            } else {
                                invoiceItem.getStock().getStockInfo().setCurrentSalePrice(temp.getStock().getStockInfo().getCurrentSalePrice());
                                invoiceItem.getStock().getStockInfo().getCurrentSaleCurrency().setId(temp.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                            }
                            break;
                        }
                    }
                }
            }

            for (InvoiceItem item : listOfObjects) {
                InvoiceItem i = new InvoiceItem();
                if ((selectedInvoice.isIsPurchase() && item.getStock().getStockInfo().getCurrentPurchasePrice() != null && item.getStock().getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.valueOf(0)) == 1)
                        || (!selectedInvoice.isIsPurchase() && item.getStock().getStockInfo().getCurrentSalePrice() != null && item.getStock().getStockInfo().getCurrentSalePrice().compareTo(BigDecimal.valueOf(0)) == 1)) {
                    i.setId(item.getId());
                    i.setIsService(item.isIsService());
                    i.getStock().setId(item.getStock().getId());
                    if (item.getStock().getUnit().getId() == 0) {
                        i.getUnit().setId(item.getUnit().getId());
                    } else {
                        i.getStock().getUnit().setId(item.getStock().getUnit().getId());
                    }
                    if (selectedInvoice.isIsPurchase()) {
                        i.setUnitPrice(item.getStock().getStockInfo().getCurrentPurchasePrice());
                        i.getCurrency().setId(item.getStock().getStockInfo().getCurrentPurchaseCurrency().getId());
                    } else {
                        i.setUnitPrice(item.getStock().getStockInfo().getCurrentSalePrice());
                        i.getCurrency().setId(item.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                    }
                    i.setQuantity(item.getQuantity());
                    i.setTotalPrice(item.getTotalPrice());
                    i.setTaxRate(item.getTaxRate());
                    i.setTotalTax(item.getTotalTax());
                    i.setIsDiscountRate(item.isIsDiscountRate());
                    i.setDiscountRate(item.getDiscountRate());
                    i.setDiscountPrice(item.getDiscountPrice());
                    i.setIsDiscountRate2(item.isIsDiscountRate2());
                    i.setDiscountRate2(item.getDiscountRate2());
                    i.setDiscountPrice2(item.getDiscountPrice2());
                    i.setTotalMoney(item.getTotalMoney());
                    i.setDescription(item.getDescription());
                    i.setStockCount(item.getStockCount());
                    i.setWaybillItemIds(item.getWaybillItemIds());
                    i.setWaybillItemQuantity(item.getWaybillItemQuantity());
                    i.setWaybillItemQuantitys(item.getWaybillItemQuantitys());
                    i.getStock().getStockInfo().setRecommendedPrice(item.getStock().getStockInfo().getRecommendedPrice());
                    i.setInvoice(selectedInvoice);
                    i.setExchangeRate(exchangeService.bringExchangeRate(i.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                    i = invoiceItemService.calculater(i, 1);

                    changePriceList.add(i);
                }

            }
            if (!changePriceList.isEmpty()) {
                int result = invoiceItemService.updateAll(changePriceList, selectedInvoice);
                if (result == -101) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("quantitycannotbegreaterthanbillamount"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    init();
                }
                sessionBean.createUpdateMessage(result);
            }

        } else if (selectedInvoice.getId() == 0) {
            if (!stockList.equals("")) {
                stockList = stockList.substring(1, stockList.length());
                tempPriceList = invoiceItemService.findInvoiceItemLastPrice(stockList, selectedInvoice.getBranchSetting());
                for (InvoiceItem invoiceItem : listOfObjects) {
                    for (InvoiceItem temp : tempPriceList) {
                        if (invoiceItem.getStock().getId() == temp.getStock().getId()) {
                            if (selectedInvoice.isIsPurchase()) {
                                invoiceItem.setUnitPrice(temp.getStock().getStockInfo().getCurrentPurchasePrice());
                                invoiceItem.getCurrency().setId(temp.getStock().getStockInfo().getCurrentPurchaseCurrency().getId());
                            } else {
                                invoiceItem.setUnitPrice(temp.getStock().getStockInfo().getCurrentSalePrice());
                                invoiceItem.getCurrency().setId(temp.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                            }
                            invoiceItem.setExchangeRate(exchangeService.bringExchangeRate(invoiceItem.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));

                            invoiceItem = invoiceItemService.calculater(invoiceItem, 1);

                            break;
                        }
                    }
                }
                calcInvoicePrice();
                for (InvoiceItem listOfObject : listOfObjects) {
                    listOfObject = invoiceItemService.calculater(listOfObject, 1);
                    listOfObject.setInvoiceDiscountPrice(calcInvoiceItemInvoiceDiscountPrice(listOfObject));

                }

                invoiceProcessBean.setIsCreateInv(true);
                for (InvoiceItem invi : listOfObjects) {
                    if (invi.getCurrency().getId() == 0) {
                        invoiceProcessBean.setIsCreateInv(false);
                        break;
                    } else {
                        invi.setExchangeRate(exchangeService.bringExchangeRate(invi.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));
                    }
                }
                invoiceProcessBean.getListOfItemForWaybill().clear();
                invoiceProcessBean.getListOfItemForWaybill().addAll(listOfObjects);

                RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
            }
        }
    }

    public void changeIsFree() {
        if (selectedObject.isIsFree()) {
            selectedObject.setUnitPrice(BigDecimal.valueOf(0));
            calculater(1);
        }

    }

    public void calculateIsFree() {
        if (selectedObject.getUnitPrice() != null) {
            if (selectedObject.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0) {
                selectedObject.setIsFree(true);
                RequestContext.getCurrentInstance().update("frmInvoiceStokProcess:grdInvoiceStokProcess");
            }
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

//            System.out.println("selectedObject getOrderItemIds" + selectedObject.getOrderItemIds());
//            System.out.println("selectedObject getOrderItemQuantitys" + selectedObject.getOrderItemQuantitys());
        }
    }

    // JSON'ı listeye çevirerek gösterim sağlar.
    public void jsonToListForWaitedInvoice() {
        listOfObjects.clear();
        listOfItemForWaitedInvoice.clear();
        if (selectedInvoice.getWaitInvoiceItemJson() != null && !selectedInvoice.getWaitInvoiceItemJson().equals("")) {
            JSONArray jArray = new JSONArray(selectedInvoice.getWaitInvoiceItemJson());
            for (int j = 0; j < jArray.length(); j++) {
                InvoiceItem invi = new InvoiceItem();
                invi.setId(jArray.getJSONObject(j).getInt("id"));
                invi.getStock().setIsService(jArray.getJSONObject(j).getBoolean("is_service"));
                invi.getStock().setId(jArray.getJSONObject(j).getInt("stock_id"));
                invi.getStock().setName(jArray.getJSONObject(j).getString("stockname"));
                invi.getStock().setCode(jArray.getJSONObject(j).getString("stockcode"));
                invi.getStock().setBarcode(jArray.getJSONObject(j).getString("stockbarcode"));
                invi.getUnit().setId(jArray.getJSONObject(j).getInt("unit_id"));
                invi.getUnit().setSortName(jArray.getJSONObject(j).getString("sortname"));
                invi.getUnit().setName(jArray.getJSONObject(j).getString("unitname"));
                invi.getUnit().setUnitRounding(jArray.getJSONObject(j).getInt("unitrounding"));
                invi.setUnitPrice(jArray.getJSONObject(j).getBigDecimal("unitprice").divide((BigDecimal.valueOf(1).add(jArray.getJSONObject(j).getBigDecimal("taxrate").divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN));
                invi.setQuantity(jArray.getJSONObject(j).getBigDecimal("quantity"));
                invi.setTotalPrice(jArray.getJSONObject(j).getBigDecimal("totalprice"));
                invi.setTaxRate(jArray.getJSONObject(j).getBigDecimal("taxrate"));
                invi.setTotalTax(jArray.getJSONObject(j).getBigDecimal("totaltax"));
                invi.setIsDiscountRate(jArray.getJSONObject(j).getBoolean("is_discountrate"));
                invi.setDiscountRate(jArray.getJSONObject(j).getBigDecimal("discountrate"));
                invi.setDiscountPrice(jArray.getJSONObject(j).getBigDecimal("discountprice"));
                invi.setIsDiscountRate2(jArray.getJSONObject(j).getBoolean("is_discountrate2"));
                invi.setDiscountRate2(jArray.getJSONObject(j).getBigDecimal("discountrate2"));
                invi.setDiscountPrice2(jArray.getJSONObject(j).getBigDecimal("discountprice2"));
                invi.getCurrency().setId(jArray.getJSONObject(j).getInt("currency_id"));
                invi.setExchangeRate(jArray.getJSONObject(j).getBigDecimal("exchangerate"));
                invi.setTotalMoney(jArray.getJSONObject(j).getBigDecimal("totalmoney"));
                invi.setDescription(jArray.getJSONObject(j).getString("description"));
                invi.setStockCount(jArray.getJSONObject(j).getInt("stockcount"));
                if (jArray.getJSONObject(j).isNull("waybillitem_id")) {
                    invi.setWaybillItemIds("");
                } else {
                    invi.setWaybillItemIds(jArray.getJSONObject(j).getString("waybillitem_id"));
                }
                Warehouse temp = new Warehouse();
                temp.setId(jArray.getJSONObject(j).getInt("warehouse_id"));
                invi.getInvoice().getListOfWarehouse().add(temp);
                invi.getStock().getStockInfo().setRecommendedPrice(jArray.getJSONObject(j).getBigDecimal("recommendedprice"));
                invi.setIsFree(jArray.getJSONObject(j).getBoolean("is_free"));
                invi.getInvoice().getBranchSetting().getBranch().setId(selectedInvoice.getBranchSetting().getBranch().getId());
                invi.getInvoice().getBranchSetting().setIsCentralIntegration(selectedInvoice.getBranchSetting().isIsCentralIntegration());
                invi.getInvoice().getBranchSetting().setIsInvoiceStockSalePriceList(selectedInvoice.getBranchSetting().isIsInvoiceStockSalePriceList());
                invi.getInvoice().getBranchSetting().getBranch().getCurrency().setId(selectedInvoice.getBranchSetting().getBranch().getCurrency().getId());
                invi.getInvoice().setIsPurchase(selectedInvoice.isIsPurchase());

                invi = invoiceItemService.calculater(invi, 1);
                listOfObjects.add(invi);
                listOfItemForWaitedInvoice.add(invi);
            }
        }

    }

    //Excelden aktarılacak ürün seçildiğinde çalışır, seçilen kayıt eğer hatalı kayıt ise (exceldatatype != 1), seçimini kaldırır.
    public void rowSelect(SelectEvent evt) {

        if (evt != null && evt.getObject() != null
                && evt.getObject() instanceof InvoiceItem) {

            InvoiceItem ei = (InvoiceItem) evt.getObject();

            if (ei.getExcelDataType() != 1) {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("incorrectregistrationcannotbetransferredtotheinvoice")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                listOfSelectedItems.remove(ei);
                RequestContext.getCurrentInstance().update("tbvInvoice:frmProductViewFirst:dtbProductViewFirst");

            } else {

            }

        }

    }

    //Excelden aktarılacak ürünler için hepsi seçildiğinde çalışır, hatalı kayıtların (exceldatatype != 1), seçimini kaldırır.
    public void allSelect() {
        if (isAll) {
            listOfSelectedItems.clear();
            for (InvoiceItem invItem : tempProductList) {
                if (invItem.getExcelDataType() == 1) {
                    listOfSelectedItems.add(invItem);
                }
            }

            if (tempProductList.size() > 0 && listOfSelectedItems.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nonecanbeaddedtotheinvoiceasallrecordsareincorrect")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isAll = false;
            }

        } else {
            listOfSelectedItems.clear();
        }

        RequestContext.getCurrentInstance().update("tbvInvoice:pgrIsError");
        RequestContext.getCurrentInstance().update("tbvInvoice:frmProductView:dtbProductView");

    }

    //Excelden aktarılacak ürünler dialoğunda Temizle seçildiğinde çalışır, seçili kayıtların seçimini kaldırır.
    public void clearSelected() {
        isAll = false;
        listOfSelectedItems.clear();
        RequestContext.getCurrentInstance().update("tbvInvoice:frmProductView:dtbProductView");
        RequestContext.getCurrentInstance().update("tbvInvoice:frmtoolbar");

    }

    public void toggleSelectedCheckbox(ToggleSelectEvent event) {

        if (event.isSelected()) {

            listOfSelectedItems.clear();
            for (InvoiceItem invItem : tempProductList) {
                if (invItem.getExcelDataType() == 1) {
                    listOfSelectedItems.add(invItem);
                }
            }

            if (tempProductList.size() > 0 && listOfSelectedItems.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nonecanbeaddedtotheinvoiceasallrecordsareincorrect")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            listOfSelectedItems.clear();
        }
        RequestContext.getCurrentInstance().update("tbvInvoice:frmProductView:dtbProductView");
    }

}
