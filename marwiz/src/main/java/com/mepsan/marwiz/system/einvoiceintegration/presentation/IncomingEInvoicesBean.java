/**
 *
 * @author elif.mart
 */
package com.mepsan.marwiz.system.einvoiceintegration.presentation;

import com.mepsan.marwiz.finance.invoice.business.InvoiceService;
import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeUnitService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.system.einvoiceintegration.business.IIncomingEInvoicesService;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IncomingInvoicesItem;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.extensions.component.slideout.SlideOut;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class IncomingEInvoicesBean extends GeneralReportBean<EInvoice> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{incomingEInvoicesService}")
    private IIncomingEInvoicesService incomingEInvoicesService;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    private AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    @ManagedProperty(value = "#{invoiceService}")
    private InvoiceService invoiceService;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{accountService}")
    private IAccountService accountService;

    @ManagedProperty(value = "#{stockAlternativeUnitService}")
    private IStockAlternativeUnitService stockAlternativeUnitService;

    private List<IncomingInvoicesItem> listOfInvoiceItem;
    private List<IncomingInvoicesItem> listItem;
    private List<IncomingEInvoice> listIncomingInvoices;
    private List<Unit> listUnit;
    private List<Warehouse> listWarehouses;
    private List<Currency> listCurrency;
    private List<Account> listAccount;
    private EInvoice selectedInvoice;
    private IncomingInvoicesItem selectedInvoiceItem;
    private int processType;
    private Unit unit;
    private int result;
    private boolean isThereCurrent;
    private BigDecimal totalDiscount, totalPrice;
    private String taxRates;
    private boolean isMatch;
    private boolean isApprolYes;
    private boolean isApprolNo;
    private String invoiceItems;
    private String waybillItems;
    private String eInvoiceNumber;
    private Invoice invoice;
    private Invoice invoiceControl;
    private String eInvoiceUUID;
    private String approvalMessage;
    private String approvalWSMessage;
    private boolean approved;
    private boolean isApprovalResponse;
    private boolean isApproval;
    private boolean rejectResponse;
    private boolean isBasicRecord;
    private int oldApprovalStatusId;
    private int appResult;
    private boolean isMaxStockLevel;
    private Date beginDate, endDate;
    private int accountId = 0;
    private int unitId = 0;
    private List<Warehouse> listOfFuelWarehouse;

    private IncomingInvoicesItem oldInvoiceItem;

    private List<IncomingInvoicesItem> listItemOld;
    private Unit oldUnit;
    private String stockEInvoiceIntegrationCodeList;
    private List<EInvoice> listGibInvoices;
    private boolean isIncomingInvoice;
    private String createWhere;
    private List<EInvoice> listOfSelectedObjects;
    private List<EInvoice> tempSelectedList;

    public int getAppResult() {
        return appResult;
    }

    public void setAppResult(int appResult) {
        this.appResult = appResult;
    }

    public int getOldApprovalStatusId() {
        return oldApprovalStatusId;
    }

    public void setOldApprovalStatusId(int oldApprovalStatusId) {
        this.oldApprovalStatusId = oldApprovalStatusId;
    }

    public boolean isIsBasicRecord() {
        return isBasicRecord;
    }

    public void setIsBasicRecord(boolean isBasicRecord) {
        this.isBasicRecord = isBasicRecord;
    }

    public String getApprovalWSMessage() {
        return approvalWSMessage;
    }

    public void setApprovalWSMessage(String approvalWSMessage) {
        this.approvalWSMessage = approvalWSMessage;
    }

    public boolean isIsApprovalResponse() {
        return isApprovalResponse;
    }

    public void setIsApprovalResponse(boolean isApprovalResponse) {
        this.isApprovalResponse = isApprovalResponse;
    }

    public boolean isIsApproval() {
        return isApproval;
    }

    public void setIsApproval(boolean isApproval) {
        this.isApproval = isApproval;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApprovalMessage() {
        return approvalMessage;
    }

    public void setApprovalMessage(String approvalMessage) {
        this.approvalMessage = approvalMessage;
    }

    public String geteInvoiceUUID() {
        return eInvoiceUUID;
    }

    public void seteInvoiceUUID(String eInvoiceUUID) {
        this.eInvoiceUUID = eInvoiceUUID;
    }

    public Invoice getInvoiceControl() {
        return invoiceControl;
    }

    public void setInvoiceControl(Invoice invoiceControl) {
        this.invoiceControl = invoiceControl;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String geteInvoiceNumber() {
        return eInvoiceNumber;
    }

    public void seteInvoiceNumber(String eInvoiceNumber) {
        this.eInvoiceNumber = eInvoiceNumber;
    }

    public String getWaybillItems() {
        return waybillItems;
    }

    public void setWaybillItems(String waybillItems) {
        this.waybillItems = waybillItems;
    }

    public String getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(String invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public boolean isIsApprolNo() {
        return isApprolNo;
    }

    public void setIsApprolNo(boolean isApprolNo) {
        this.isApprolNo = isApprolNo;
    }

    public boolean isIsApprolYes() {
        return isApprolYes;
    }

    public void setIsApprolYes(boolean isApprolYes) {
        this.isApprolYes = isApprolYes;
    }

    public boolean isIsMatch() {
        return isMatch;
    }

    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public String getTaxRates() {
        return taxRates;
    }

    public void setTaxRates(String taxRates) {
        this.taxRates = taxRates;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public boolean isIsThereCurrent() {
        return isThereCurrent;
    }

    public void setIsThereCurrent(boolean isThereCurrent) {
        this.isThereCurrent = isThereCurrent;
    }

    public List<Account> getListAccount() {
        return listAccount;
    }

    public void setListAccount(List<Account> listAccount) {
        this.listAccount = listAccount;
    }

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public List<Warehouse> getListWarehouses() {
        return listWarehouses;
    }

    public void setListWarehouses(List<Warehouse> listWarehouses) {
        this.listWarehouses = listWarehouses;
    }

    public List<IncomingEInvoice> getListIncomingInvoices() {
        return listIncomingInvoices;
    }

    public void setListIncomingInvoices(List<IncomingEInvoice> listIncomingInvoices) {
        this.listIncomingInvoices = listIncomingInvoices;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public List<Unit> getListUnit() {
        return listUnit;
    }

    public void setListUnit(List<Unit> listUnit) {
        this.listUnit = listUnit;
    }

    public IncomingInvoicesItem getSelectedInvoiceItem() {
        return selectedInvoiceItem;
    }

    public void setSelectedInvoiceItem(IncomingInvoicesItem selectedInvoiceItem) {
        this.selectedInvoiceItem = selectedInvoiceItem;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(EInvoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public List<IncomingInvoicesItem> getListItem() {
        return listItem;
    }

    public void setListItem(List<IncomingInvoicesItem> listItem) {
        this.listItem = listItem;
    }

    public EInvoice getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(EInvoice selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<IncomingInvoicesItem> getListOfInvoiceItem() {
        return listOfInvoiceItem;
    }

    public void setListOfInvoiceItem(List<IncomingInvoicesItem> listOfInvoiceItem) {
        this.listOfInvoiceItem = listOfInvoiceItem;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setIncomingEInvoicesService(IIncomingEInvoicesService incomingEInvoicesService) {
        this.incomingEInvoicesService = incomingEInvoicesService;
    }

    public boolean isRejectResponse() {
        return rejectResponse;
    }

    public void setRejectResponse(boolean rejectResponse) {
        this.rejectResponse = rejectResponse;
    }

    public boolean isIsMaxStockLevel() {
        return isMaxStockLevel;
    }

    public void setIsMaxStockLevel(boolean isMaxStockLevel) {
        this.isMaxStockLevel = isMaxStockLevel;
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public void setStockAlternativeUnitService(IStockAlternativeUnitService stockAlternativeUnitService) {
        this.stockAlternativeUnitService = stockAlternativeUnitService;
    }

    public List<Warehouse> getListOfFuelWarehouse() {
        return listOfFuelWarehouse;
    }

    public void setListOfFuelWarehouse(List<Warehouse> listOfFuelWarehouse) {
        this.listOfFuelWarehouse = listOfFuelWarehouse;
    }

    public IncomingInvoicesItem getOldInvoiceItem() {
        return oldInvoiceItem;
    }

    public void setOldInvoiceItem(IncomingInvoicesItem oldInvoiceItem) {
        this.oldInvoiceItem = oldInvoiceItem;
    }

    public List<IncomingInvoicesItem> getListItemOld() {
        return listItemOld;
    }

    public void setListItemOld(List<IncomingInvoicesItem> listItemOld) {
        this.listItemOld = listItemOld;
    }

    public Unit getOldUnit() {
        return oldUnit;
    }

    public void setOldUnit(Unit oldUnit) {
        this.oldUnit = oldUnit;
    }

    public String getStockEInvoiceIntegrationCodeList() {
        return stockEInvoiceIntegrationCodeList;
    }

    public void setStockEInvoiceIntegrationCodeList(String stockEInvoiceIntegrationCodeList) {
        this.stockEInvoiceIntegrationCodeList = stockEInvoiceIntegrationCodeList;
    }

    public List<EInvoice> getListGibInvoices() {
        return listGibInvoices;
    }

    public void setListGibInvoices(List<EInvoice> listGibInvoices) {
        this.listGibInvoices = listGibInvoices;
    }

    public boolean isIsIncomingInvoice() {
        return isIncomingInvoice;
    }

    public void setIsIncomingInvoice(boolean isIncomingInvoice) {
        this.isIncomingInvoice = isIncomingInvoice;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<EInvoice> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<EInvoice> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public List<EInvoice> getTempSelectedList() {
        return tempSelectedList;
    }

    public void setTempSelectedList(List<EInvoice> tempSelectedList) {
        this.tempSelectedList = tempSelectedList;
    }

    @Override
    public void init() {

        System.out.println("------INCOMİNG E INVOİCES BEAN");
        selectedObject = new EInvoice();
        listItem = new ArrayList<>();
        listUnit = new ArrayList<>();
        listIncomingInvoices = new ArrayList<>();
        listWarehouses = new ArrayList<>();
        listCurrency = new ArrayList<>();
        selectedInvoice = new EInvoice();
        selectedInvoiceItem = new IncomingInvoicesItem();
        listOfFuelWarehouse = new ArrayList<>();
        listGibInvoices = new ArrayList<>();
        listOfSelectedObjects = new ArrayList<>();
        tempSelectedList = new ArrayList<>();
        processType = 1;
        isApproval = true;
        isApprovalResponse = false;
        isThereCurrent = false;
        isApprolNo = false;
        isApprolYes = false;
        approved = false;
        isBasicRecord = false;
        isIncomingInvoice = false;

        unit = new Unit();
        listItemOld = new ArrayList<>();
        oldUnit = new Unit();
        Date date1 = new Date();
        selectedObject.setDueDate(date1);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        cal.add(Calendar.MONTH, -1);
        beginDate = cal.getTime();

        setListBtn(sessionBean.checkAuthority(new int[]{129, 130}, 0));

    }

    @Override
    public void find() {
        createWhere = "";
        listGibInvoices = new ArrayList<>();
        listItemOld = new ArrayList<>();
        listWarehouses = warehouseService.selectListWarehouse(" ");
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            incomingEInvoicesService.listOfIncomingInvoice();
            listOfObjects = findall(" ");
            listOfInvoiceItem = incomingEInvoicesService.listOfİtem();
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

            if (eInvoiceIntegrationBean.getInvoiceSourceType() == 2 && eInvoiceIntegrationBean.getOperationType() != 3) {
                listGibInvoices = incomingEInvoicesService.uListOfIncomingInvoice(eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());
                isIncomingInvoice = true;
                listOfInvoiceItem = incomingEInvoicesService.listOfİtem();
                if (!listOfInvoiceItem.isEmpty()) {
                    listItemOld.addAll(listOfInvoiceItem);
                }
            } else if ((eInvoiceIntegrationBean.getOperationType() == 1 && eInvoiceIntegrationBean.getInvoiceSourceType() == 1) || eInvoiceIntegrationBean.getOperationType() == 3) {
                isIncomingInvoice = false;
                //  createWhere = incomingEInvoicesService.createWhere(eInvoiceIntegrationBean.getAccountList(), eInvoiceIntegrationBean.getInvoiceNo(), eInvoiceIntegrationBean.getOperationType());
                listOfObjects = findall(createWhere);
                listOfInvoiceItem = incomingEInvoicesService.listOfİtem();
                if (!listOfInvoiceItem.isEmpty()) {
                    listItemOld.addAll(listOfInvoiceItem);
                }

                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                if (dataTable != null) {
                    dataTable.setFirst(0);
                }

            }

        }
    }

    public void findInvoices() {
        listOfSelectedObjects = new ArrayList<>();
        tempSelectedList = new ArrayList<>();
        find();
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");

        isMatch = true;
        boolean isThereFuel = true;
        if (isApprolYes) { // Fatura kaydedilmeden önce onay yanıtı gönderilecekse

            if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

                int ieInvoiceId = 0;
                int ieInvoiceApprovalStatusId = 0;
                String ieInvoiceApprovalDescription = "";

                selectedObject.setIsPurchase(true);
                for (int j = 0; j < listItem.size(); j++) {
                    listItem.get(j).setId(0);
                    listItem.get(j).setIsDiscountRate(false);
                    listItem.get(j).setStockCount(1);
                    listItem.get(j).getInvoice().getWarehouse().setId(selectedObject.getWarehouse().getId());
                }
                if (selectedObject.getType().getId() == 27) {
                    selectedObject.setIsPurchase(false);
                    invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);
                    waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);
                    ieInvoiceId = selectedObject.getId();
                    selectedObject.setId(0);
                    selectedObject.setIsEInvoice(true);
                    if (approved) { //fatura için onay yanıtı zaten gönderilmiş ise 
                        ieInvoiceApprovalStatusId = 4;
                        ieInvoiceApprovalDescription = approvalWSMessage;
                    } else {
                        ieInvoiceApprovalStatusId = 2;
                        ieInvoiceApprovalDescription = "Fatura Onaylandı";
                    }
                    result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

                } else if (selectedObject.getType().getId() == 59) {
                    waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);
                    invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);

                    ieInvoiceId = selectedObject.getId();
                    selectedObject.setId(0);
                    selectedObject.setIsEInvoice(true);
                    if (approved) {
                        ieInvoiceApprovalStatusId = 4;
                        ieInvoiceApprovalDescription = approvalWSMessage;
                    } else {
                        ieInvoiceApprovalStatusId = 2;
                        ieInvoiceApprovalDescription = "Fatura Onaylandı";
                    }
                    result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

                }
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                    RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");

                    if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                    } else {
                        listGibInvoices.remove(selectedObject);
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                    }
                    approved = false;
                }
                FacesMessage message;
                switch (result) {
                    case -101://açık vardiya yok uyarı ver
                        message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                sessionBean.loc.getString("warning"),
                                sessionBean.loc.getString("noopenshift"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;

                    case -102:// faturanın itemlarının tüm alanları doldurulmadı uyarı ver
                        message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                sessionBean.loc.getString("warning"),
                                sessionBean.loc.getString("wefillinallthefieldsinstocks"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;

                    case -103://aynı belge nolu fatura var ise uyarı ver
                        Invoice invoice = new Invoice();
                        invoice.getAccount().setId(selectedObject.getAccount().getId());
                        invoice.setDocumentNumber(selectedObject.getDocumentNumber());
                        invoice.setDocumentSerial(selectedObject.getDocumentSerial());
                        invoiceControl = invoiceService.findDuplicateInvoice(invoice);
                        if (invoiceControl.getId() != 0) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("registrationwiththesamedocumentnumberavailable")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }
                        break;
                    case -104://kasa -ye düşürülünce hata ver
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    default:
                        sessionBean.createUpdateMessage(result);
                        break;
                }
            } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
                int ieInvoiceId = 0;
                int ieInvoiceApprovalStatusId = 0;
                String ieInvoiceApprovalDescription = "";

                selectedObject.setIsPurchase(true);
                oldApprovalStatusId = selectedObject.getApprovalStatusId();
                selectedObject.setApprovalStatusId(2);
                appResult = incomingEInvoicesService.sendApproval(selectedObject);
                if (appResult == 1 || appResult == 2) {
                    for (int j = 0; j < listItem.size(); j++) {
                        listItem.get(j).setId(0);
                        listItem.get(j).setIsDiscountRate(false);
                        listItem.get(j).setStockCount(1);
                        listItem.get(j).getInvoice().getWarehouse().setId(selectedObject.getWarehouse().getId());
                    }
                    if (selectedObject.getType().getId() == 27) {
                        selectedObject.setIsPurchase(false);
                        invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);
                        waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);

                        ieInvoiceId = selectedObject.getId();
                        selectedObject.setId(0);
                        selectedObject.setIsEInvoice(true);
                        ieInvoiceApprovalStatusId = 2;
                        ieInvoiceApprovalDescription = selectedObject.getApprovalDescription();
                        result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

                    } else if (selectedObject.getType().getId() == 59) {
                        waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);
                        invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);

                        ieInvoiceId = selectedObject.getId();
                        selectedObject.setId(0);
                        selectedObject.setIsEInvoice(true);
                        ieInvoiceApprovalStatusId = 2;
                        ieInvoiceApprovalDescription = selectedObject.getApprovalDescription();
                        result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

                    }

                    if (result > 0) {
                        RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                        RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");

                        if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                            RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                        } else {
                            listGibInvoices.remove(selectedObject);
                            RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                        }
                    }

                    FacesMessage message;
                    switch (result) {
                        case -101://açık vardiya yok uyarı ver
                            message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    sessionBean.loc.getString("warning"),
                                    sessionBean.loc.getString("noopenshift"));
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                            break;

                        case -102:// faturanın itemlarının tüm alanları doldurulmadı uyarı ver
                            message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    sessionBean.loc.getString("warning"),
                                    sessionBean.loc.getString("wefillinallthefieldsinstocks"));
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                            break;

                        case -103://aynı belge nolu fatura var ise uyarı ver
                            Invoice invoice = new Invoice();

                            invoice.getAccount().setId(selectedObject.getAccount().getId());
                            invoice.setDocumentNumber(selectedObject.getDocumentNumber());
                            invoice.setDocumentSerial(selectedObject.getDocumentSerial());

                            invoiceControl = invoiceService.findDuplicateInvoice(invoice);
                            if (invoiceControl.getId() != 0) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("registrationwiththesamedocumentnumberavailable")));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");

                            }

                            break;
                        case -104://kasa -ye düşürülünce hata ver
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                            break;

                        default:
                            sessionBean.createUpdateMessage(result);
                            break;
                    }
                } else if (appResult == 3) {
                    selectedObject.setApprovalStatusId(oldApprovalStatusId);
                    approvalWSMessage = incomingEInvoicesService.approvalMessage();
                    approvalMessage = sessionBean.loc.getString("theinvoicewaspreviouslyrejectedandcannotbeansweredagain") + "  " + sessionBean.loc.getString("invoicewillnotbesavedandremovedfromthelistasitisinthecaseofrejection");
                    RequestContext.getCurrentInstance().update("dlgConfirmOK");
                    RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show()");
                }
            }
        } else if (isApprolNo) {//Red yanıtı gönderilecek ise 
            oldApprovalStatusId = selectedObject.getApprovalStatusId();
            selectedObject.setApprovalStatusId(3);
            if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
                appResult = incomingEInvoicesService.sendApproval(selectedObject);
                if (appResult == 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");

                    RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");
                    if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                    } else {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                    }
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                    IncomingEInvoice iei = new IncomingEInvoice();
                    iei.setId(selectedObject.getId());
                    iei.setInvoiceId(0);
                    iei.setIsSuccess(true);
                    iei.setResponseCode("0");
                    iei.setResponseDescription("Fatura Reddedildi");
                    iei.setApprovalStatusId(3);
                    iei.setApprovalDescription(selectedObject.getApprovalDescription());
                    int resultUpdate = incomingEInvoicesService.update(iei);

                } else if (appResult == 2) {//Fatura Onaylı Durumda 
                    selectedObject.setApprovalStatusId(oldApprovalStatusId);
                    approvalMessage = sessionBean.loc.getString("cannotberejectednowbecausetheinvoicehasbeenpreviouslyapproved") + "  " + sessionBean.loc.getString("invoicemustbepostedasapproved");
                    RequestContext.getCurrentInstance().update("dlgConfirmOK");
                    RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show()");

                } else if (appResult == 3) {// Fatura Red Durumunda 
                    selectedObject.setApprovalStatusId(oldApprovalStatusId);
                    approvalWSMessage = incomingEInvoicesService.approvalMessage();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");

                    RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");
                    if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                    } else {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                    }
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                    IncomingEInvoice iei = new IncomingEInvoice();

                    iei.setId(selectedObject.getId());
                    iei.setInvoiceId(0);
                    iei.setIsSuccess(true);
                    iei.setResponseCode("0");
                    iei.setResponseDescription("Fatura Reddedildi");
                    iei.setApprovalStatusId(3);
                    iei.setApprovalDescription(selectedObject.getApprovalDescription());
                    int resultUpdate = incomingEInvoicesService.update(iei);

                } else {
                    approvalWSMessage = incomingEInvoicesService.approvalMessage();
                    RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + approvalWSMessage));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
                appResult = incomingEInvoicesService.sendUApproval(selectedObject, eInvoiceUUID);
                approvalWSMessage = incomingEInvoicesService.approvalMessage();
                if (appResult == 0) { // Fatura daha önce zaten yanıtlanmış vb. durumlar sebebi ile yanıt verilemiyor ise
                    selectedObject.setApprovalStatusId(oldApprovalStatusId);
                    rejectStatus(approvalWSMessage);
                }

                if (appResult == 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                    RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");
                    if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                    } else {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                    }
                    IncomingEInvoice iei = new IncomingEInvoice();

                    iei.setId(selectedObject.getId());
                    iei.setIsSuccess(true);
                    iei.setResponseCode("0");
                    iei.setResponseDescription("Fatura Reddedildi");
                    iei.setApprovalStatusId(3);
                    iei.setApprovalDescription(selectedObject.getApprovalDescription());
                    incomingEInvoicesService.update(iei);
                }
            }
        } else if (!isApprolNo && !isApprolYes) {

            if (selectedObject.getType().getId() == 59 || selectedObject.getType().getId() == 27) {

                selectedObject.getStatus().setId(28);
                for (int j = 0; j < listWarehouses.size(); j++) {
                    if (listWarehouses.get(j).getId() == selectedObject.getWarehouse().getId()) {

                        selectedObject.getWarehouse().setName(listWarehouses.get(j).getName());
                    }
                }

                selectedObject.setDeliveryPerson(sessionBean.getUser().getFullName());
                selectedObject.setDocumentSerial(eInvoiceNumber.substring(0, 3));
                selectedObject.setDocumentNumber(eInvoiceNumber.substring(3, eInvoiceNumber.length()));

                selectedObject.getListOfWarehouse().clear();
                selectedObject.getListOfWarehouse().add(selectedObject.getWarehouse());
                selectedObject.setWarehouseIdList(Integer.toString(selectedObject.getWarehouse().getId()));
                selectedObject.setJsonWarehouses(invoiceService.jsonArrayWarehouses(selectedObject.getListOfWarehouse()));
                selectedObject.setIsPurchase(true);
                selectedObject.setIsPeriodInvoice(false);

                for (IncomingInvoicesItem iItem : listItem) {
                    if (iItem.getStock().getId() == 0 || iItem.getUnit().getId() == 0) {
                        isMatch = false;
                    }
                    if (selectedObject.isIsFuel()) {

                        if (iItem.getWarehouse().getId() == 0) {
                            isThereFuel = false;
                        }
                    }
                }
                if (selectedObject.getAccount().getId() == 0 || !isMatch || !isThereFuel) {
                    if (!isThereFuel) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleaseselectthetankforallwarehouseproducts")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");

                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("allmatchesmustbedonetoımportınvoice")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }

                } else {
                    if (selectedObject.getInvoiceScenarioId() == 2) {
                        approvalMessage = sessionBean.loc.getString("commercialeinvoicewillbeapprovedandsaveddoyouwanttocontinue");
                        RequestContext.getCurrentInstance().update("dlgConfirm");
                        RequestContext.getCurrentInstance().execute("PF('dlg_Confirm').show()");
                    } else {
                        if (!isBasicRecord) {
                            approvalMessage = sessionBean.loc.getString("invoicewillbesavedandremovedfromthelistdoyouwanttocontinue");
                            RequestContext.getCurrentInstance().update("dlgConfirm");
                            RequestContext.getCurrentInstance().execute("PF('dlg_Confirm').show()");
                        } else {
                            int ieInvoiceId = 0;
                            int ieInvoiceApprovalStatusId = 0;
                            String ieInvoiceApprovalDescription = "";

                            for (int j = 0; j < listItem.size(); j++) {
                                listItem.get(j).setId(0);
                                listItem.get(j).setIsDiscountRate(false);
                                listItem.get(j).setStockCount(1);
                                listItem.get(j).getInvoice().getWarehouse().setId(selectedObject.getWarehouse().getId());
                                listItem.get(j).getInvoice().setIsFuel(selectedObject.isIsFuel());

                            }

                            if (selectedObject.getType().getId() == 27) {
                                selectedObject.setIsPurchase(false);
                                invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);
                                waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);
                                ieInvoiceId = selectedObject.getId();
                                selectedObject.setId(0);
                                selectedObject.setIsEInvoice(true);
                                result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);
                            } else if (selectedObject.getType().getId() == 59) {
                                selectedObject.getStatus().setId(28);
                                waybillItems = incomingEInvoicesService.jsonArrayWaybillItems(listItem);

                                invoiceItems = incomingEInvoicesService.jsonArrayInvoiceItems(listItem);
                                ieInvoiceId = selectedObject.getId();
                                selectedObject.setId(0);
                                selectedObject.setIsEInvoice(true);
                                result = incomingEInvoicesService.createInvoice(selectedObject, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

                            }

                            if (result > 0) {
                                RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");
                                if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                                } else {
                                    listGibInvoices.remove(selectedObject);
                                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                                }
                            }

                            FacesMessage message;
                            switch (result) {
                                case -101://açık vardiya yok uyarı ver
                                    message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            sessionBean.loc.getString("warning"),
                                            sessionBean.loc.getString("noopenshift"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    break;

                                case -102:// faturanın itemlarının tüm alanları doldurulmadı uyarı ver
                                    message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            sessionBean.loc.getString("warning"),
                                            sessionBean.loc.getString("wefillinallthefieldsinstocks"));
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    break;

                                case -103://aynı belge nolu fatura var ise uyarı ver
                                    Invoice invoice = new Invoice();

                                    invoice.getAccount().setId(selectedObject.getAccount().getId());
                                    invoice.setDocumentNumber(selectedObject.getDocumentNumber());
                                    invoice.setDocumentSerial(selectedObject.getDocumentSerial());

                                    invoiceControl = invoiceService.findDuplicateInvoice(invoice);
                                    if (invoiceControl.getId() != 0) {
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("registrationwiththesamedocumentnumberavailable")));
                                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    }
                                    break;
                                case -104://kasa -ye düşürülünce hata ver
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                                    break;

                                default:
                                    sessionBean.createUpdateMessage(result);
                                    break;
                            }
                        }
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("invoicecannotbesavedbecauseinvoicetypedoesnotmatch")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
    }

    @Override
    public LazyDataModel<EInvoice> findall(String where) {
        return new CentrowizLazyDataModel<EInvoice>() {
            @Override
            public List<EInvoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<EInvoice> result = new ArrayList<>();
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");

                if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
                    createWhere = incomingEInvoicesService.createWhere(eInvoiceIntegrationBean.getAccountList(), eInvoiceIntegrationBean.getInvoiceNo(), eInvoiceIntegrationBean.getOperationType(), eInvoiceIntegrationBean.getDateFilterType(), eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());

                    result = incomingEInvoicesService.findInMarwizEInvoices(first, pageSize, sortField, convertSortOrder(sortOrder), filters, eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate(), createWhere, true);
                } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
                    result = incomingEInvoicesService.listGetInvoices(first, pageSize, sortField, convertSortOrder(sortOrder), filters, eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());
                }

                int count = 0;
                count = incomingEInvoicesService.count(createWhere, eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                listOfSelectedObjects.clear();
                if (!tempSelectedList.isEmpty()) {
                    listOfSelectedObjects.addAll(tempSelectedList);

                }
                return result;

            }

        };

    }

    //Faturanın itemını görüntülemek için dialog açar
    public void goToInvoiceDetail() {
        processType = 1;
        listItem = new ArrayList<>();
        eInvoiceNumber = selectedObject.getDocumentNumber();
        eInvoiceUUID = selectedObject.getDocumentSerial();
        for (IncomingInvoicesItem invI : listOfInvoiceItem) {
            if (invI.getInvoice().getId() == selectedObject.getId()) {
                unitId = 0;
                if (invI.getUnit().getId() != 0) {
                    unitId = invI.getUnit().getId();
                } else {
                    invI.getUnit().setId(0);
                    invI.getUnit().setSortName(invI.getOldUnitName());
                }

                invI.getStock().setName(invI.getOldStockName());
                invI.getStock().setId(0);
                listItem.add(invI);
            }
        }

        accountId = 0;
        if (selectedObject.getAccount().getId() != 0) {
            accountId = selectedObject.getAccount().getId();
        } else {
            selectedObject.getAccount().setTitle(selectedObject.getOldAccountName());
            selectedObject.getAccount().setTaxNo(selectedObject.getOldTaxNo());
        }

        bringInvoiceDiscount();
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("frmEInvoiceProcess");
        RequestContext.getCurrentInstance().update("frmEInvoiceStokTab:dtbStock");
        context.execute("PF('dlg_EInvoiceItem').show()");
    }

    //Fatura kaydetme işlemi için dialog açar
    public void goToSave() {
        stockEInvoiceIntegrationCodeList = "";
        selectedObject.setBranchSetting(sessionBean.getUser().getLastBranchSetting());
        isApprolNo = false;
        isApprolYes = false;
        eInvoiceNumber = "";
        isMaxStockLevel = false;
        processType = 2;
        isThereCurrent = false;
        selectedObject.setIsPurchase(true);
        selectedObject.getWarehouse().setId(0);
        if (!listWarehouses.isEmpty()) {
            selectedObject.getWarehouse().setId(listWarehouses.get(0).getId());

        }

        String where = " AND acc.is_employee = FALSE ";
        if (!sessionBean.getUser().getLastBranchSetting().isIsProcessPassiveAccount()) {
            where = where + " AND acc.status_id <> 6 ";
        }
        where = where + " AND acc.taxno='" + selectedObject.getAccount().getTaxNo() + "'";
        listAccount = accountService.findAll(where);
        if (listAccount.size() > 0) {
            isThereCurrent = true;
            selectedObject.setAccount(listAccount.get(0));
        }

        // Fatura kaydedilmeden diyalog kapatılırsa listeyi yeniden çekmemek için eski cari bilgileri tutuldu
        accountId = 0;
        if (selectedObject.getAccount().getId() != 0) {
            accountId = selectedObject.getAccount().getId();
        } else {
            selectedObject.getAccount().setTitle(selectedObject.getOldAccountName());
            selectedObject.getAccount().setTaxNo(selectedObject.getOldTaxNo());
        }

        eInvoiceNumber = selectedObject.getDocumentNumber();
        eInvoiceUUID = selectedObject.getDocumentSerial();

        selectedObject.setOldAccountName(selectedObject.getAccount().getTitle());
        selectedObject.setDueDate(new Date());
        selectedObject.setDispatchDate(new Date());
        listItem = new ArrayList<>();
        listItemOld = new ArrayList<>();
        for (IncomingInvoicesItem invI : listOfInvoiceItem) {

            if (invI.getInvoice().getId() == selectedObject.getId()) {

                stockEInvoiceIntegrationCodeList = stockEInvoiceIntegrationCodeList + ",'" + invI.getOldStockEntegrationCode() + "'";

                IncomingInvoicesItem oldItem = new IncomingInvoicesItem();
                oldItem.setId(invI.getId());
                oldItem.setQuantity(invI.getQuantity());
                oldItem.setUnitPrice(invI.getUnitPrice());
                oldItem.setDiscountPrice(invI.getDiscountPrice());
                oldItem.setDiscountRate(invI.getDiscountRate());
                oldItem.setTaxRate(invI.getTaxRate());
                oldItem.setTotalTax(invI.getTotalTax());
                oldItem.setTotalPrice(invI.getTotalPrice());
                oldItem.setTotalMoney(invI.getTotalMoney());
                oldItem.setStock(invI.getStock());
                oldItem.setOldStockName(invI.getOldStockName());
                oldItem.setOldStockEntegrationCode(invI.getOldStockEntegrationCode());
                oldItem.setOldUnit(invI.getUnit());
                oldItem.setOldUnitName(invI.getOldUnitName());
                oldItem.setUnit(invI.getUnit());
                oldItem.setCurrency(invI.getCurrency());
                oldItem.setExchangeRate(invI.getExchangeRate());

                // Fatura kaydedilmeden diyalog kapatılırsa listeyi yeniden çekmemek için eski stok bilgileri tutuldu
                unitId = 0;
                if (invI.getUnit().getId() != 0) {
                    unitId = invI.getUnit().getId();
                } else {
                    invI.getUnit().setId(0);
                    invI.getUnit().setSortName(invI.getOldUnitName());
                }

                invI.getStock().setName(invI.getOldStockName());
                invI.getStock().setId(0);
//                String whereUnit = "";
//                whereUnit = " AND gunt.internationalcode = " + "'" + invI.getUnit().getSortName() + "'";
//                listUnit = incomingEInvoicesService.bringUnit(whereUnit);

//                if (listUnit.size() > 0) {
//                    invI.setIsThereUnit(true);
//                    invI.setUnit(listUnit.get(0));
//                    invI.getStock().setUnit(listUnit.get(0));
//                } else {
//                    invI.setIsThereUnit(false);
//                }
                listItem.add(invI);
                listItemOld.add(oldItem);
            }
        }

        if (!stockEInvoiceIntegrationCodeList.equals("")) {
            stockEInvoiceIntegrationCodeList = stockEInvoiceIntegrationCodeList.substring(1, stockEInvoiceIntegrationCodeList.length());
        }

        stockMatchEntegrationCode();

        bringInvoiceDiscount();
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("frmEInvoiceProcess:pgrEInvoiceProcess");
        RequestContext.getCurrentInstance().update("frmEInvoiceProcess");
        RequestContext.getCurrentInstance().update("dlg_EInvoiceItem");
        context.execute("PF('dlg_EInvoiceItem').show()");
        RequestContext.getCurrentInstance().update("pngAccountBook:showBook");

    }

    //itemleri stok entegrasyon koduna göre eşletirip birim eşleşmesini yapıp birim miktarına göre yeniden hesaplamaları yapar
    public void stockMatchEntegrationCode() {
        List<Stock> result = new ArrayList<>();
        boolean isUnit = false;
        result = incomingEInvoicesService.listStock(stockEInvoiceIntegrationCodeList, selectedObject);

        for (IncomingInvoicesItem item : listItem) {
            isMaxStockLevel = false;
            isUnit = false;
            for (Stock stock : result) {
                if ((item.getOldStockEntegrationCode() != null && stock.getStockInfo().geteInvoiceIntegrationCode() != null) && item.getOldStockEntegrationCode().equals(stock.getStockInfo().geteInvoiceIntegrationCode())) {

                    if (stock.getStockInfo().getMaxStockLevel() != null) {

                        maxStockLevelControl(stock, 2, item);
                    }

                    if (!isMaxStockLevel) {

                        item.setStock(stock);
                        item.setOldUnit(stock.getUnit());
                        listUnit = incomingEInvoicesService.bringUnit(item.getStock());
                        listUnit.add(0, item.getStock().getUnit());
                        if (item.getStock().getStockEInvoiceUnitCon().getId() != 0) {
                            if (item.getOldUnitName().equals(item.getStock().getStockEInvoiceUnitCon().getStockIntegrationCode())) {
                                item.setUnit(item.getStock().getUnit());
                                item.getUnit().setId(item.getStock().getUnit().getId());
                                item.setIsThereUnit(true);
                                item.setStockEInvoiceUnitCon(item.getStock().getStockEInvoiceUnitCon());
                                isUnit = true;
                                item = calculateItemAmounts(item);

                            }

                        } else {

                            if (!listUnit.isEmpty()) {
                                for (Unit unit : listUnit) {

                                    if (unit.getInternationalCode() != null && unit.getInternationalCode().equals(item.getUnit().getSortName())) {

                                        item.setUnit(unit);
                                        item.getUnit().setId(unit.getId());
                                        item.setIsThereUnit(true);
                                        isUnit = true;
                                    }
                                }

                            }

                        }

                    }
                }
            }

            if (!isUnit) {
                item.getUnit().setId(0);
                item.getUnit().setSortName(item.getOldUnitName());

            }
        }
    }

    public void goToApproval() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_EInvoiceApproval').show()");
    }

    //Cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmEInvoiceProcess:pgrEInvoiceProcess");
            RequestContext.getCurrentInstance().update("frmEInvoiceProcess:btnAccountName");
            RequestContext.getCurrentInstance().update("frmEInvoiceProcess:btnAccountName1");

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_Account').hide();");
        }
        if (stockBookFilterBean.getSelectedData() != null) {
            oldUnit = new Unit();
            isMaxStockLevel = false;
            if (stockBookFilterBean.getSelectedData().getStockInfo().getMaxStockLevel() != null) {

                maxStockLevelControl(stockBookFilterBean.getSelectedData(), 1, selectedInvoiceItem);
            }

            if (!isMaxStockLevel) {
                selectedInvoiceItem.setStock(stockBookFilterBean.getSelectedData());

                if (selectedInvoiceItem.getOldStockEntegrationCode() != null) {
                    List<Stock> listStockInfos = new ArrayList<>();
                    String stockInfoIds = "";
                    if (stockBookFilterBean.getSelectedData().getStockInfo().geteInvoiceIntegrationCode() == null || !selectedInvoiceItem.getOldStockEntegrationCode().equals(stockBookFilterBean.getSelectedData().getStockInfo().geteInvoiceIntegrationCode())) {
                        listStockInfos = incomingEInvoicesService.findStockInfo(selectedInvoiceItem.getOldStockEntegrationCode());
                        if (!listStockInfos.isEmpty()) {
                            for (Stock stock : listStockInfos) {
                                stockInfoIds = stockInfoIds + "," + stock.getStockInfo().getId();
                            }
                            if (!stockInfoIds.isEmpty()) {
                                stockInfoIds = stockInfoIds.substring(1, stockInfoIds.length());
                            }

                        }

                        incomingEInvoicesService.updateStockIntegrationCode(selectedInvoiceItem, stockInfoIds);
                    }
                }

                oldUnit.setId(stockBookFilterBean.getSelectedData().getUnit().getId());
                oldUnit.setName(stockBookFilterBean.getSelectedData().getUnit().getName());
                oldUnit.setSortName(stockBookFilterBean.getSelectedData().getUnit().getSortName());
                oldUnit.setUnitRounding(stockBookFilterBean.getSelectedData().getUnit().getUnitRounding());

                if (selectedObject.isIsFuel()) {
                    listOfFuelWarehouse = incomingEInvoicesService.findFuelStockWarehouse(selectedInvoiceItem);
                    if (!listOfFuelWarehouse.isEmpty()) {
                        if (listOfFuelWarehouse.size() == 1) {
                            selectedInvoiceItem.setIsListFuelWarehouse(false);
                        } else {
                            selectedInvoiceItem.setIsListFuelWarehouse(true);
                        }
//                        selectedInvoiceItem.setWarehouse(listOfFuelWarehouse.get(0));
                        selectedInvoiceItem.getWarehouse().setId(listOfFuelWarehouse.get(0).getId());

                        if (!listOfFuelWarehouse.isEmpty()) {
                            selectedInvoiceItem.setListOfFuelWarehouse(listOfFuelWarehouse);
                        }

                    }
                }

                for (int i = 0; i < listItem.size(); i++) {
                    if (listItem.get(i).getId() == selectedInvoiceItem.getId()) {
                        listItem.get(i).setStock(selectedInvoiceItem.getStock());
                        listItem.get(i).setWarehouse(selectedInvoiceItem.getWarehouse());

                        listUnit = incomingEInvoicesService.bringUnit(listItem.get(i).getStock());
                        listUnit.add(0, selectedInvoiceItem.getStock().getUnit());

                        if (selectedInvoiceItem.getStock().getStockEInvoiceUnitCon().getId() != 0) {

                            listItem.get(i).setUnit(selectedInvoiceItem.getStock().getUnit());
                            listItem.get(i).getUnit().setId(selectedInvoiceItem.getStock().getUnit().getId());
                            listItem.get(i).setIsThereUnit(true);
                            selectedInvoiceItem.setStockEInvoiceUnitCon(selectedInvoiceItem.getStock().getStockEInvoiceUnitCon());

                            selectedInvoiceItem = calculateItemAmounts(selectedInvoiceItem);

                        } else {
                            if (!listUnit.isEmpty()) {
                                for (Unit unit : listUnit) {

                                    if (unit.getInternationalCode() != null && unit.getInternationalCode().equals(listItem.get(i).getUnit().getSortName())) {
                                        listItem.get(i).setUnit(unit);
                                        listItem.get(i).getUnit().setId(unit.getId());
                                        listItem.get(i).setIsThereUnit(true);
                                    }
                                }

                            }

                        }

                    }
                }
                RequestContext.getCurrentInstance().update("frmIncomingEInvoiceStockProcess:slcWarehouse");
                RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock");
                RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock:0:btnStockName");
                RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock:0:btnStockName1");
                RequestContext.getCurrentInstance().update("dlg_Stock");
                RequestContext context = RequestContext.getCurrentInstance();
                if (!selectedObject.isIsFuel()) {
                    context.execute("PF('dlg_Stock').hide();");
                }
            }
        }

    }

    //Stok birim eşleştirme işlemi için dialog açar
    public void goToStockUnit() {
        oldUnit = new Unit();
        oldUnit.setId(selectedInvoiceItem.getStock().getUnit().getId());
        oldUnit.setName(selectedInvoiceItem.getStock().getUnit().getName());
        oldUnit.setSortName(selectedInvoiceItem.getStock().getUnit().getSortName());
        oldUnit.setUnitRounding(selectedInvoiceItem.getStock().getUnit().getUnitRounding());

        if (selectedInvoiceItem.getStock().getId() != 0) {

            listUnit = incomingEInvoicesService.bringUnit(selectedInvoiceItem.getStock());
            listUnit.add(0, selectedInvoiceItem.getStock().getUnit());

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_StockUnit').show()");

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasedothestockmatchfirsttobeabletomatchtheunit")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    //Stok eşleştirme işlemi için dialog açar
    public void goToStock() {

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_Stock').show()");

    }

    //Ticari faturaya ret yanıtı göndermek için dialog açar
    public void goToReject() {

        if (isApprolYes) {
            isApprolYes = false;
        }
        isApprolNo = true;
        approvalMessage = sessionBean.loc.getString("invoicewillberejectedandremovedfromthelistdoyouwanttocontinue");
        RequestContext.getCurrentInstance().update("dlgConfirmApproval");
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmApproval').show()");

    }

    public void closeEvent() {
        if (isApprolNo) {
            isApprolNo = false;
        } else if (isApprolYes) {
            isApprolYes = false;
        }
    }

    public void unitControl() {

        for (int i = 0; i < listItem.size(); i++) {
            if (selectedInvoiceItem.getId() == listItem.get(i).getId()) {
                listItem.get(i).getUnit().setId(selectedInvoiceItem.getUnit().getId());
                for (int j = 0; j < listUnit.size(); j++) {
                    if (listItem.get(i).getUnit().getId() == listUnit.get(j).getId()) {
                        listItem.get(i).getUnit().setName(listUnit.get(j).getName());
                        listItem.get(i).getUnit().setSortName(listUnit.get(j).getSortName());
                    }
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock");
        RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock:0:btnUnitName");
        RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock:0:btnUnitName1");
        context.execute("PF('dlg_StockUnit').hide();");

        selectedInvoiceItem.getStock().setUnit(oldUnit);
    }

    public String totalAmountText() {

        HashMap<String, BigDecimal> unitList = new HashMap<>();
        for (IncomingInvoicesItem is : listItem) {
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

    public BigDecimal sumTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (IncomingInvoicesItem invoiceStock : listItem) {
//            if (invoiceStock.getTotalPrice() != null && invoiceStock.getTotalPrice().doubleValue() > 0) {
//                total = total.add(invoiceStock.getTotalPrice());
//            }
//            if (invoiceStock.getDiscountPrice() != null && invoiceStock.getDiscountPrice().doubleValue() > 0) {
//                total = total.add(invoiceStock.getDiscountPrice());
//            }
            total = total.add((invoiceStock.getUnitPrice() == null ? BigDecimal.ZERO : invoiceStock.getQuantity() == null ? BigDecimal.ZERO : invoiceStock.getUnitPrice().multiply(invoiceStock.getQuantity())).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate()));
        }
        return total;
    }

    public void bringInvoiceDiscount() {

        BigDecimal total = BigDecimal.ZERO;
        totalDiscount = BigDecimal.ZERO;
        HashMap<BigDecimal, BigDecimal> taxList = new HashMap<>();
        for (IncomingInvoicesItem is : listItem) {
            if (is.getTotalPrice() != null) {
                total = total.add(is.getTotalPrice());
            }

            if (is.getDiscountPrice() != null && is.getDiscountPrice().doubleValue() > 0) {
                if (is.getExchangeRate() != null) {
                    totalDiscount = totalDiscount.add(is.getDiscountPrice().multiply(is.getExchangeRate()));
                }
            } else if (is.getDiscountRate() != null && is.getDiscountRate().doubleValue() > 0) {
                if (is.getExchangeRate() != null && is.getTotalPrice() != null) {
                    totalDiscount = totalDiscount.add((is.getTotalPrice().multiply(is.getDiscountRate()).movePointLeft(2)).multiply(is.getExchangeRate()));
                }
            }

            if (is.getTaxRate() != null && is.getExchangeRate() != null) {

                if (taxList.containsKey(is.getTaxRate())) {//bu oran onceden vardır
                    taxList.put(is.getTaxRate(), is.getTotalTax() != null ? taxList.get(is.getTaxRate()).add(is.getTotalTax().multiply(is.getExchangeRate())) : BigDecimal.ZERO);
                } else {//vergi grubu yok ise
                    taxList.put(is.getTaxRate(), is.getTotalTax() != null ? is.getTotalTax().multiply(is.getExchangeRate()) : BigDecimal.ZERO);
                }
            }

        }

        StringBuilder sb = new StringBuilder();
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        for (Map.Entry<BigDecimal, BigDecimal> me : taxList.entrySet()) {
            sb.append("(%");
            sb.append(formatter.format(me.getKey()));
            sb.append(" : ");
            sb.append(formatter.format(me.getValue()));
            sb.append(") ");
            sb.append(sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0));
            sb.append(" - ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 2, sb.length());
        }

        taxRates = sb.toString();
        totalPrice = total;

    }

    //Ticari faturaya onay yanıtı göndermek için dialog açar
    public void approvalResponseYes() {

        if (selectedObject.getInvoiceScenarioId() == 2) {
            isApproval = true;
            isApprovalResponse = true;
            isApprolYes = true;

            RequestContext context = RequestContext.getCurrentInstance();
            selectedObject.setApprovalDescription(" ");
            if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
                RequestContext.getCurrentInstance().update("dlgApproval");
                context.execute("PF('dlg_Approval').show()");
            } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
                selectedObject.setApprovalStatusId(2);
                int appresult = incomingEInvoicesService.sendUApproval(selectedObject, eInvoiceUUID);
                if (appresult == 0) {//Fatura daha önce yanıtlanmışsa
                    approvalWSMessage = incomingEInvoicesService.approvalMessage();
                    if (approvalWSMessage.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Approved")) {
                        appResponseYes();
                    } else if (approvalWSMessage.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Declined")) {
                        approvalMessage = sessionBean.loc.getString("theinvoicewaspreviouslyrejectedandcannotbeansweredagain") + "  " + sessionBean.loc.getString("invoicewillnotbesavedandremovedfromthelistasitisinthecaseofrejection");
                        RequestContext.getCurrentInstance().update("dlgConfirmOK");
                        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show()");

                    } else if (approvalWSMessage.contains("Fatura sisteme geldikten 8 gün ve daha sonrasında uygulama yanıtı gönderilemez")) {
                        approvalMessage = sessionBean.loc.getString("applicationresponsecannotbesent8daysaftertheinvoicearrivesinthesystem") + sessionBean.loc.getString("youwillbedeemedtohaveapprovedtheinvoicebecausetheresponseperiodhasexpired") + ". " + sessionBean.loc.getString("doyouwanttosavetheinvoice");
                        RequestContext.getCurrentInstance().update("dlgConfirmApproval");
                        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmApproval').show()");
                    } else if (approvalWSMessage.contains("Bu faturaya yanıt daha önce gönderildi")) {

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("theinvoicehasalreadybeensentandtheresponseisinprogresspleasetryagainlater")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");

                    } else {
                        isApprolYes = false;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + approvalWSMessage));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }

                } else if (appresult == 1) {
                    save();
                }
            }
        } else {
            isBasicRecord = true;
            save();
        }
    }

    public void approvalResponseNo() {
        isApproval = false;
        isApprovalResponse = true;
    }

    //Ticari fatura daha önce yanıtlanmış ve faturayı kaydetme işlemine devam etmek isteniliyorsa 
    public void appResponseYes() {
        if (isApprolYes) {
            approved = true;
            save();
        }
        if (isApprolNo) {
            rejectResponse = true;
            RequestContext context = RequestContext.getCurrentInstance();
            selectedObject.setApprovalDescription(" ");
            RequestContext.getCurrentInstance().update("dlgApproval");
            context.execute("PF('dlg_Approval').show()");
        }
    }

    public void appResponseNo() {
        isApprolYes = false;
        if (isApprolNo) {
            isApprolNo = false;
        }
    }

    //Fatura daha önce reddedildiği için yanıt gönderilemiyor ise
    public void noApprovalResponse() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            if (approvalWSMessage.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Declined")) {

                IncomingEInvoice incomingEInvoice = new IncomingEInvoice();
                incomingEInvoice.setId(selectedObject.getId());
                incomingEInvoice.setApprovalStatusId(4);
                incomingEInvoice.setApprovalDescription(approvalWSMessage);
                incomingEInvoice.setIsSuccess(true);
                incomingEInvoice.setResponseDescription("Fatura Reddedildi");
                incomingEInvoice.setResponseCode("0");
                incomingEInvoicesService.update(incomingEInvoice);

                RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");

                if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                } else {
                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                }
                isApprolNo = false;
            } else if (approvalWSMessage.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Approved")) {
                isApprolNo = false;
            }
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            if (appResult == 2) {

                isApprolNo = false;

            } else if (appResult == 3) {

                IncomingEInvoice incomingEInvoice = new IncomingEInvoice();
                incomingEInvoice.setId(selectedObject.getId());
                incomingEInvoice.setApprovalStatusId(4);
                incomingEInvoice.setApprovalDescription(approvalWSMessage);
                incomingEInvoice.setIsSuccess(true);
                incomingEInvoice.setResponseCode("0");
                incomingEInvoice.setResponseDescription("Fatura Reddedildi");

                incomingEInvoicesService.update(incomingEInvoice);

                RequestContext.getCurrentInstance().execute("PF('dlg_Approval').hide();");
                RequestContext.getCurrentInstance().execute("PF('dlg_EInvoiceItem').hide();");
                if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                } else {
                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                }
                isApprolNo = false;
            }
        }
    }

    //Ret yanıtı durumlarına göre işlem yapar
    public void rejectStatus(String message) {
        if (message.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Declined")) { //Fatura daha önce reddedilmiş ise
            noApprovalResponse();
        } else if (message.contains("Faturanın şuanki durumu yanıt vermek için uygun değil, Durum: Approved")) { //Fatura daha önce onaylanmış ise
            approvalMessage = sessionBean.loc.getString("cannotberejectednowbecausetheinvoicehasbeenpreviouslyapproved") + "  " + sessionBean.loc.getString("invoicemustbepostedasapproved");
            RequestContext.getCurrentInstance().update("dlgConfirmOK");
            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmOK').show()");
        } else if (message.contains("Bu faturaya yanıt daha önce gönderildi")) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("theinvoicehasalreadybeensentandtheresponseisinprogresspleasetryagainlater")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + message));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    //Cari eşleştirme işlemi için dialog açar
    public void goToAccountUpdate() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_Account').show()");
    }

    public void updateTable() {
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
//            listOfObject = incomingEInvoicesService.listGetInvoices();
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");
//            listOfObject = incomingEInvoicesService.uListGetInvoices(eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());
        }
        listOfInvoiceItem = incomingEInvoicesService.listOfİtem();
    }

    public void updateObj() {
        selectedObject.getWarehouse().setId(0);
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");
        if (accountId != 0) {
            selectedObject.getAccount().setId(accountId);
        } else {
            selectedObject.getAccount().setTitle(selectedObject.getOldAccountName());
            selectedObject.getAccount().setId(0);
            selectedObject.getAccount().setTaxNo(selectedObject.getOldTaxNo());
        }

        for (IncomingInvoicesItem invI : listOfInvoiceItem) {
            if (invI.getInvoice().getId() == selectedObject.getId()) {

                if (unitId != 0) {
                    invI.getUnit().setId(unitId);
                } else {
                    invI.getUnit().setId(0);
                    invI.getUnit().setSortName(invI.getOldUnitName());
                }
            }
        }
        if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
            RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
        } else {
            RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
        }
        RequestContext.getCurrentInstance().update("frmEInvoiceProcess");
    }

    //Fatura sayfasında kaydedilmiş e-faturaların, web servisten gelen e-fatura item verilerinin görüntülenebilmesi için xml datadan item listesini oluşturur.
    public List<IncomingInvoicesItem> bringEInvoiceItem(Invoice obj) {
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            listItem = incomingEInvoicesService.bringItemList(obj);
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            listItem = incomingEInvoicesService.uBringItemList(obj);
        }
        return listItem;
    }

    // Stokta max seviye tanımlanmışsa satın alma işlemlerinde max seviyenin üzerine çıkılmaması için çalışır
    public void maxStockLevelControl(Stock obj, int type, IncomingInvoicesItem item) {

        BigDecimal purchaseAmount = BigDecimal.ZERO;
        if (obj.getStockInfo().getMaxStockLevel() != null && obj.getStockInfo().getBalance() != null) {
            purchaseAmount = obj.getStockInfo().getMaxStockLevel().subtract(obj.getStockInfo().getBalance());
        }

        if (purchaseAmount.compareTo(item.getQuantity()) == -1) {
            if (type == 1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("purchasecannotbeperformedabovethemaximumstocklevel"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

            isMaxStockLevel = true;
        } else {
            isMaxStockLevel = false;
        }

    }

    public void stockEInvoiceUnitCon() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgStockEInvoiceUnitConProc");
        context.execute("PF('dlg_stockeinvoiceunitconproc').show();");

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        StockEInvoiceUnitConBean stockEInvoiceUnitConBean = (StockEInvoiceUnitConBean) viewMap.get("stockEInvoiceUnitConBean");
        if (stockEInvoiceUnitConBean != null) {
            stockEInvoiceUnitConBean.init();
        }
    }

    public void changeFuelWarehouse() {

    }

    //Connection tablosuna birim içerik miktar kaydı atılınca veya güncellenince itemın birimini seçilen stok un birimi ile eşleştirir.
    public void updateStockUnitMatch() {

        selectedInvoiceItem.getUnit().setId(selectedInvoiceItem.getStock().getUnit().getId());

        unitControl();

        selectedInvoiceItem = calculateItemAmounts(selectedInvoiceItem);

    }

    //Connection tablosunda birim içerik miktar kaydı varsa itemın miktar, birim vs. tutarlarını hesaplar.
    public IncomingInvoicesItem calculateItemAmounts(IncomingInvoicesItem obj) {

        for (IncomingInvoicesItem itemOld : listItemOld) {
            if (obj.getId() == itemOld.getId()) {

                obj.setQuantity(itemOld.getQuantity());
                obj.setUnitPrice(itemOld.getUnitPrice());
                obj.setDiscountPrice(itemOld.getDiscountPrice());
                obj.setDiscountRate(itemOld.getDiscountRate());
                obj.setTaxRate(itemOld.getTaxRate());
                obj.setTotalTax(itemOld.getTotalTax());
                obj.setTotalPrice(itemOld.getTotalPrice());
                obj.setTotalMoney(itemOld.getTotalMoney());
            }
        }

        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal unitPrice = BigDecimal.ZERO;
        if (obj.getStockEInvoiceUnitCon().getId() != 0) {
            if (obj.getStockEInvoiceUnitCon().getQuantity() != null && obj.getStockEInvoiceUnitCon().getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                quantity = obj.getQuantity().multiply(obj.getStockEInvoiceUnitCon().getQuantity());

            }
            unitPrice = obj.getUnitPrice().divide(obj.getStockEInvoiceUnitCon().getQuantity(), 4, RoundingMode.HALF_EVEN);//Yeni miktar hesaplandı.

            obj.setUnitPrice(unitPrice);
            obj.setQuantity(quantity);
            obj = incomingEInvoicesService.calculaterItem(obj);

        }

        return obj;
    }

    public void changeFuel() {

        RequestContext.getCurrentInstance().update("dlgConfirmFuel");
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmFuel').show()");

    }

    public void fuelResponseYes() {
        listItem.clear();

        for (IncomingInvoicesItem oldItem : listItemOld) {
            IncomingInvoicesItem oldItemObj = new IncomingInvoicesItem();
            oldItemObj.setId(oldItem.getId());
            oldItemObj.setQuantity(oldItem.getQuantity());
            oldItemObj.setUnitPrice(oldItem.getUnitPrice());
            oldItemObj.setDiscountPrice(oldItem.getDiscountPrice());
            oldItemObj.setDiscountRate(oldItem.getDiscountRate());
            oldItemObj.setTaxRate(oldItem.getTaxRate());
            oldItemObj.setTotalTax(oldItem.getTotalTax());
            oldItemObj.setTotalPrice(oldItem.getTotalPrice());
            oldItemObj.setTotalMoney(oldItem.getTotalMoney());
            oldItemObj.setStock(oldItem.getStock());
            oldItemObj.setOldStockName(oldItem.getOldStockName());
            oldItemObj.setOldStockEntegrationCode(oldItem.getOldStockEntegrationCode());
            oldItemObj.setOldUnit(oldItem.getOldUnit());
            oldItemObj.setOldUnitName(oldItem.getOldUnitName());
            oldItemObj.setUnit(oldItem.getUnit());
            oldItemObj.setCurrency(oldItem.getCurrency());
            oldItemObj.setExchangeRate(oldItem.getExchangeRate());
            listItem.add(oldItemObj);

        }

        stockEInvoiceIntegrationCodeList = "";

        for (IncomingInvoicesItem invI : listItem) {
            stockEInvoiceIntegrationCodeList = stockEInvoiceIntegrationCodeList + ",'" + invI.getOldStockEntegrationCode() + "'";
        }

        if (!stockEInvoiceIntegrationCodeList.equals("")) {
            stockEInvoiceIntegrationCodeList = stockEInvoiceIntegrationCodeList.substring(1, stockEInvoiceIntegrationCodeList.length());
        }

        stockMatchEntegrationCode();

        if (selectedObject.isIsFuel()) {
            selectedObject.getWarehouse().setId(0);
            for (IncomingInvoicesItem item : listItem) {

                if (item.getStock().getId() != 0) {

                    listOfFuelWarehouse = incomingEInvoicesService.findFuelStockWarehouse(item);
                    if (!listOfFuelWarehouse.isEmpty()) {
                        if (listOfFuelWarehouse.size() == 1) {
                            item.setIsListFuelWarehouse(false);
                        } else {
                            item.setIsListFuelWarehouse(true);
                        }

//                        item.setWarehouse(listOfFuelWarehouse.get(0));
                        item.getWarehouse().setId(listOfFuelWarehouse.get(0).getId());
                        if (!listOfFuelWarehouse.isEmpty()) {
                            item.setListOfFuelWarehouse(listOfFuelWarehouse);
                        }

                    }

                }

            }

        } else {
            selectedObject.getWarehouse().setId(0);
            if (!listWarehouses.isEmpty()) {
                selectedObject.getWarehouse().setId(listWarehouses.get(0).getId());
            }
        }

        bringInvoiceDiscount();
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("frmEInvoiceProcess:pgrEInvoiceProcess");
        RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock");
        RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock:clmStockUnitCode");

        RequestContext.getCurrentInstance().update("frmEInvoiceProcess");
        RequestContext.getCurrentInstance().update("dlg_EInvoiceItem");

    }

    public void fuelResponseNo() {

        if (selectedObject.isIsFuel()) {
            selectedObject.setIsFuel(false);
        } else {
            selectedObject.setIsFuel(true);
        }

        RequestContext.getCurrentInstance().update("frmEInvoiceProcess");

    }

    public void changeInvoiceSource() {
        if (tempSelectedList != null && !tempSelectedList.isEmpty()) {
            tempSelectedList.clear();
        }
        if (listOfSelectedObjects != null && !listOfSelectedObjects.isEmpty()) {
            listOfSelectedObjects.clear();
        }

        RequestContext.getCurrentInstance().update("pngIncomingEInvoicesDatatable");

    }

    public void addArchive() {

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");
        List<EInvoice> list = new ArrayList<>();

        int result;
        String ids = "";

        if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
            if (!tempSelectedList.isEmpty()) {
                list.addAll(tempSelectedList);
            }
        } else {
            if (!listOfSelectedObjects.isEmpty()) {
                list.addAll(listOfSelectedObjects);
            }
        }

        if (!list.isEmpty()) {
            for (EInvoice obj : list) {
                ids = ids + "," + obj.getId();
            }
            if (!ids.isEmpty()) {
                ids = ids.substring(1, ids.length());
                result = incomingEInvoicesService.updateArchive(ids, 1);
                if (result > 0) {

                    if (eInvoiceIntegrationBean.getInvoiceSourceType() == 1) {
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                        tempSelectedList.clear();
                    } else {
                        for (EInvoice ei : listOfSelectedObjects) {
                            for (Iterator<EInvoice> iterator = listGibInvoices.iterator(); iterator.hasNext();) {
                                EInvoice next = iterator.next();
                                if (next.getId() == ei.getId()) {
                                    iterator.remove();

                                }
                            }

                        }
                        listOfSelectedObjects.clear();
                        RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesGIB");
                    }

                }
                sessionBean.createUpdateMessage(result);
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), "  " + sessionBean.loc.getString("pleaseselectinvoicestobearchived")));
            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

        }

    }

    public void rowSelect(SelectEvent evt) {

        boolean isThere;
        if (evt != null && evt.getObject() != null
                && evt.getObject() instanceof EInvoice) {

            EInvoice ei = (EInvoice) evt.getObject();

            if (tempSelectedList.isEmpty()) {
                tempSelectedList.add(ei);
            } else {

                isThere = false;
                for (EInvoice obj : tempSelectedList) {
                    if (obj.getId() == ei.getId()) {
                        isThere = true;
                    }
                }

                if (!isThere) {
                    tempSelectedList.add(ei);
                }
            }

        }

    }

    public void rowUnSelect(UnselectEvent evt) {

        if (evt != null && evt.getObject() != null
                && evt.getObject() instanceof EInvoice) {

            EInvoice ei = (EInvoice) evt.getObject();
            if (!tempSelectedList.isEmpty()) {

                for (Iterator<EInvoice> iterator = tempSelectedList.iterator(); iterator.hasNext();) {
                    EInvoice next = iterator.next();
                    if (next.getId() == ei.getId()) {
                        iterator.remove();
                        break;
                    }
                }

            }

        }

    }

    public void toggleSelect(ToggleSelectEvent evt) {

        if (evt.isSelected()) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");

            if ((eInvoiceIntegrationBean.getOperationType() == 3 && eInvoiceIntegrationBean.getArchiveOperationType() == 1) || (eInvoiceIntegrationBean.getOperationType() == 1 && eInvoiceIntegrationBean.getInvoiceSourceType() == 1)) {
                if (listOfObjects.getRowCount() > 20) {
                    createWhere = incomingEInvoicesService.createWhere(eInvoiceIntegrationBean.getAccountList(), eInvoiceIntegrationBean.getInvoiceNo(), eInvoiceIntegrationBean.getOperationType(), eInvoiceIntegrationBean.getDateFilterType(), eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate());
                    listOfSelectedObjects = incomingEInvoicesService.findInMarwizEInvoices(0, 0, "", "", new HashMap<String, Object>(), eInvoiceIntegrationBean.getBeginDate(), eInvoiceIntegrationBean.getEndDate(), createWhere, false);
                }
            }

            if (!tempSelectedList.isEmpty()) {
                tempSelectedList.clear();
            }
            tempSelectedList.addAll(listOfSelectedObjects);
        } else {
            if (!tempSelectedList.isEmpty()) {
                tempSelectedList.clear();
            }
        }

    }

    //Seçilen kayıtları arşivden çıkarır
    public void unArchive() {

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        EInvoiceIntegrationBean eInvoiceIntegrationBean = (EInvoiceIntegrationBean) viewMap.get("eInvoiceIntegrationBean");

        int result;
        String ids = "";

        if (!tempSelectedList.isEmpty()) {
            for (EInvoice obj : tempSelectedList) {
                ids = ids + "," + obj.getId();
            }
            if (!ids.isEmpty()) {
                ids = ids.substring(1, ids.length());
                result = incomingEInvoicesService.updateArchive(ids, 0);
                if (result > 0) {

                    RequestContext.getCurrentInstance().update("frmIncomingEInvoices:dtbIncomingEInvoicesInMarwiz");
                    tempSelectedList.clear();

                }
                sessionBean.createUpdateMessage(result);
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), "  " + sessionBean.loc.getString("pleaseselecttheinvoicestoberemovedfromthearchive")));
            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

        }

    }

}
