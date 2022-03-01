/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 09:54:15
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceItemService;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.InvoiceBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.documenttemplate.business.DocumentTemplateService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.SendPurchase;
import com.mepsan.marwiz.general.model.log.SendSale;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import com.mepsan.marwiz.general.printer.business.IPrinterService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.service.purchace.business.ISendPurchaseService;
import com.mepsan.marwiz.service.sale.business.ISendSaleService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class InvoiceProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceBookFilterBean}")
    public InvoiceBookFilterBean invoiceBookFilterBean;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{invoiceItemService}")
    public IInvoiceItemService invoiceItemService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{documentTemplateService}")
    public DocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{sendSaleService}")
    public ISendSaleService sendSaleService;

    @ManagedProperty(value = "#{sendPurchaseService}")
    public ISendPurchaseService sendPurchaseService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{printerService}")
    public IPrinterService printerService;

    private Invoice selectedObject, duplicateInvoice;
    private List<Warehouse> listWarehouses;
    private int processType;
    private int activeIndex;
    private BigDecimal totalPrice;//faturanın stoklarının toplam fıyatı
    private String exchange;
    private Waybill waybill;
    private boolean isCreateInvFromWaybill;//irsaliyeden fatura oluşturma mı ? true : evet
    private boolean isCreateInv;//fatura oluşturulabilir mi? ürünlerinin tüm alanları dolduruldumu bilgisini tutar.
    private List<Waybill> selectedWaybill;
    private List<Waybill> listOfWaybill;
    private Date invDate;
    private List<Status> listOfStatus;
    private List<DocumentNumber> listOfDocumentNumber;
    private boolean isSave;
    private List<InvoiceItem> listOfItemForWaybill;//irsaliyeden fatura oluşturulurken tutulur.
    private boolean isSendCenter = false;
    private String wordFromNumber;
    private List<InvoiceItem> listOfItemForAgreement;//mutabakattan fatura oluşturulurken tutulur.
    private boolean isCreateInvFromCustAgreement;//müşteri mutabakatından oluşturma mı ? true : evet
    private int oldStatusId;
    private CustomerAgreements customerAgreements;
    private String info;
    private BranchSetting branchSetting;
    private CreditReport credit;
    private BigDecimal oldDiscountPrice, oldDiscountRate;
    private String warehouseName;
    List<CheckDelete> controlDeleteList;
    private boolean isChangeInfoDialog;
    private Invoice selectedChangingInvoice;
    private List<BranchSetting> listOfBranch;
    private BranchSetting branchSettingForSelection;
    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    private int relatedRecordId;
    private boolean isUpdate;
    private boolean isCreateInvFromOrder;
    private Order order;
    private List<InvoiceItem> listOfItemForOrder;//siparişten fatura oluşturulurken tutulur.
    private boolean isDelete;

    private boolean isPurchaseMinStockLevel;
    private boolean isSalesMaxStockLevel;
    private boolean isThere;

    public boolean isIsThere() {
        return isThere;
    }

    public void setIsThere(boolean isThere) {
        this.isThere = isThere;
    }

    public boolean isIsSalesMaxStockLevel() {
        return isSalesMaxStockLevel;
    }

    public void setIsSalesMaxStockLevel(boolean isSalesMaxStockLevel) {
        this.isSalesMaxStockLevel = isSalesMaxStockLevel;
    }

    public boolean isIsPurchaseMinStockLevel() {
        return isPurchaseMinStockLevel;
    }

    public void setIsPurchaseMinStockLevel(boolean isPurchaseMinStockLevel) {
        this.isPurchaseMinStockLevel = isPurchaseMinStockLevel;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getWordFromNumber() {
        return wordFromNumber;
    }

    public BigDecimal getOldDiscountPrice() {
        return oldDiscountPrice;
    }

    public void setOldDiscountPrice(BigDecimal oldDiscountPrice) {
        this.oldDiscountPrice = oldDiscountPrice;
    }

    public Invoice getDuplicateInvoice() {
        return duplicateInvoice;
    }

    public void setDuplicateInvoice(Invoice duplicateInvoice) {
        this.duplicateInvoice = duplicateInvoice;
    }

    public void setWordFromNumber(String wordFromNumber) {
        this.wordFromNumber = wordFromNumber;
    }

    public List<InvoiceItem> getListOfItemForWaybill() {
        return listOfItemForWaybill;
    }

    public void setSendPurchaseService(ISendPurchaseService sendPurchaseService) {
        this.sendPurchaseService = sendPurchaseService;
    }

    public void setSendSaleService(ISendSaleService sendSaleService) {
        this.sendSaleService = sendSaleService;
    }

    public boolean isIsSendCenter() {
        return isSendCenter;
    }

    public void setIsSendCenter(boolean isSendCenter) {
        this.isSendCenter = isSendCenter;
    }

    public void setListOfItemForWaybill(List<InvoiceItem> listOfItemForWaybill) {
        this.listOfItemForWaybill = listOfItemForWaybill;
    }

    public Date getInvDate() {
        return invDate;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    public List<Status> getListOfStatus() {
        return listOfStatus;
    }

    public void setListOfStatus(List<Status> listOfStatus) {
        this.listOfStatus = listOfStatus;
    }

    public void setInvDate(Date invDate) {
        this.invDate = invDate;
    }

    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }

    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }

    public List<Waybill> getSelectedWaybill() {
        return selectedWaybill;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public List<Waybill> getListOfWaybill() {
        return listOfWaybill;
    }

    public BigDecimal getOldDiscountRate() {
        return oldDiscountRate;
    }

    public void setOldDiscountRate(BigDecimal oldDiscountRate) {
        this.oldDiscountRate = oldDiscountRate;
    }

    public void setListOfWaybill(List<Waybill> listOfWaybill) {
        this.listOfWaybill = listOfWaybill;
    }

    public void setSelectedWaybill(List<Waybill> selectedWaybill) {
        this.selectedWaybill = selectedWaybill;
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

    public boolean isIsCreateInv() {
        return isCreateInv;
    }

    public void setIsCreateInv(boolean isCreateInv) {
        this.isCreateInv = isCreateInv;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Invoice getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Invoice selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public List<InvoiceItem> getListOfItemForAgreement() {
        return listOfItemForAgreement;
    }

    public void setListOfItemForAgreement(List<InvoiceItem> listOfItemForAgreement) {
        this.listOfItemForAgreement = listOfItemForAgreement;
    }

    public boolean isIsCreateInvFromCustAgreement() {
        return isCreateInvFromCustAgreement;
    }

    public void setIsCreateInvFromCustAgreement(boolean isCreateInvFromCustAgreement) {
        this.isCreateInvFromCustAgreement = isCreateInvFromCustAgreement;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public boolean isIsCreateInvFromWaybill() {
        return isCreateInvFromWaybill;
    }

    public void setIsCreateInvFromWaybill(boolean isCreateInvFromWaybill) {
        this.isCreateInvFromWaybill = isCreateInvFromWaybill;
    }

    public boolean isIsChangeInfoDialog() {
        return isChangeInfoDialog;
    }

    public void setIsChangeInfoDialog(boolean isChangeInfoDialog) {
        this.isChangeInfoDialog = isChangeInfoDialog;
    }

    public Invoice getSelectedChangingInvoice() {
        return selectedChangingInvoice;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setSelectedChangingInvoice(Invoice selectedChangingInvoice) {
        this.selectedChangingInvoice = selectedChangingInvoice;
    }

    public BranchSetting getBranchSettingForSelection() {
        return branchSettingForSelection;
    }

    public void setBranchSettingForSelection(BranchSetting branchSettingForSelection) {
        this.branchSettingForSelection = branchSettingForSelection;
    }

    public void setInvoiceBookFilterBean(InvoiceBookFilterBean invoiceBookFilterBean) {
        this.invoiceBookFilterBean = invoiceBookFilterBean;
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

    public int getRelatedRecordId() {
        return relatedRecordId;
    }

    public void setRelatedRecordId(int relatedRecordId) {
        this.relatedRecordId = relatedRecordId;
    }

    public void setInvoiceItemService(IInvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    public void setPrinterService(IPrinterService printerService) {
        this.printerService = printerService;
    }

    public boolean isIsCreateInvFromOrder() {
        return isCreateInvFromOrder;
    }

    public void setIsCreateInvFromOrder(boolean isCreateInvFromOrder) {
        this.isCreateInvFromOrder = isCreateInvFromOrder;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<InvoiceItem> getListOfItemForOrder() {
        return listOfItemForOrder;
    }

    public void setListOfItemForOrder(List<InvoiceItem> listOfItemForOrder) {
        this.listOfItemForOrder = listOfItemForOrder;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------------------InvoiceProcessBean");
        listOfStatus = new ArrayList<>();
        listOfStatus = sessionBean.getStatus(17);
        listOfBranch = new ArrayList<>();
        branchSettingForSelection = new BranchSetting();
        listOfBranch = invoiceItemService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        branchSetting = sessionBean.getLastBranchSetting();

        controlDeleteList = new ArrayList<>();
        selectedChangingInvoice = new Invoice();

        if (sessionBean.parameter instanceof ArrayList) {

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Invoice) {//Fatura ise
                    selectedObject = (Invoice) ((ArrayList) sessionBean.parameter).get(i);

                    isCreateInvFromCustAgreement = false;
                    isCreateInvFromWaybill = false;
                    isCreateInvFromOrder = false;
                    if (selectedObject.getId() == 0) {//ekleme ise
                        processType = 1;
                        selectedObject.setPriceDifferenceInvoice(new Invoice());
                        selectedObject.setInvoiceDate(new Date());
                        selectedObject.setDispatchDate(new Date());
                        selectedObject.setDueDate(new Date());
                        selectedObject.setRoundingPrice(BigDecimal.ZERO);
                        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                        selectedObject.getType().setId(59);//irsaliyeli fatura olacak
                        selectedObject.getStatus().setId(28);//default açık

                        for (BranchSetting b : listOfBranch) {
                            if (b.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                                selectedObject.getBranchSetting().getBranch().setId(b.getBranch().getId());
                                selectedObject.getBranchSetting().getBranch().setIsAgency(b.getBranch().isIsAgency());
                                selectedObject.getBranchSetting().setIsCentralIntegration(b.isIsCentralIntegration());
                                selectedObject.getBranchSetting().setIsUnitPriceAffectedByDiscount(b.isIsUnitPriceAffectedByDiscount());
                                selectedObject.getBranchSetting().setParoAccountCode(b.getParoAccountCode());
                                selectedObject.getBranchSetting().setParoUrl(b.getParoUrl());
                                selectedObject.getBranchSetting().setParoResponsibleCode(b.getParoResponsibleCode());
                                selectedObject.getBranchSetting().setParoBranchCode(b.getParoBranchCode());
                                break;
                            }
                        }
                        changeBranch();
                        if (!listWarehouses.isEmpty()) {
                            selectedObject.setWarehouse(listWarehouses.get(0));
                        }
                        selectedObject.setDeliveryPerson(sessionBean.getUser().getFullName());

                        changeExchange();
                        int index = 0;
                        for (int j = 0; j < listOfStatus.size(); j++) {
                            if (listOfStatus.get(j).getId() == 30) {//iptal durumu ise çıkar
                                index = j;
                            }
                        }
                        listOfStatus.remove(index);

                    } else {
                        processType = 2;
                        controlDeleteList.clear();
                        controlDeleteList = invoiceService.testBeforeDelete(selectedObject);
                        if (!controlDeleteList.isEmpty()) {
                            if (controlDeleteList.get(0).getR_response() < 0) {
                                if (controlDeleteList.get(0).getR_response() == -101) {
                                    isUpdate = true;
                                }
                            }
                        }
                        if (selectedObject.getType().getId() == 26) {//Fiyat Farkı
                            listOfStatus.clear();
                            for (Status statu : sessionBean.getStatus(17)) {
                                if (statu.getId() != 30) {
                                    listOfStatus.add(statu);
                                }
                            }
                        } else {
                            selectedObject.setPriceDifferenceInvoice(new Invoice());
                        }
                        Calendar c = Calendar.getInstance();
                        c.setTime(selectedObject.getInvoiceDate());
                        c.add(Calendar.MONTH, -1);
                        invDate = c.getTime();
                        changeExchange();
                        listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " ");
                        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                        setDocument();
                        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                        info = sessionBean.getLoc().getString("thisinvoiceiscreatedfromcreditsales");
                        RequestContext.getCurrentInstance().update("frmInvoiceProcess");
                    }
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Waybill) {//irsaliyeden fatura oluşturulacak ise waybill gelir.
                    waybill = (Waybill) ((ArrayList) sessionBean.parameter).get(i);

                    listOfItemForWaybill = new ArrayList<>();
                    isCreateInvFromWaybill = true;
                    selectedObject = new Invoice();
                    selectedObject.setPriceDifferenceInvoice(new Invoice());
                    selectedObject.setAccount(waybill.getAccount());
                    selectedObject.setDescription(waybill.getDescription());
                    selectedObject.setDispatchAddress(waybill.getDispatchAddress());
                    selectedObject.setDispatchDate(waybill.getDispatchDate());
                    selectedObject.setInvoiceDate(waybill.getWaybillDate());
                    selectedObject.setDueDate(new Date());
                    selectedObject.setDocumentNumber(waybill.getDocumentNumber());
                    selectedObject.setIsPurchase(waybill.isIsPurchase());
                    selectedObject.getStatus().setId(28);//açık
                    selectedObject.setRoundingPrice(BigDecimal.ZERO);
                    selectedObject.getType().setId(waybill.getType().getId() == 22 ? 27 : 23);
                    selectedObject.getBranchSetting().getBranch().setId(waybill.getBranchSetting().getBranch().getId());
                    selectedObject.getBranchSetting().setIsCentralIntegration(waybill.getBranchSetting().isIsCentralIntegration());
                    selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(waybill.getBranchSetting().isIsInvoiceStockSalePriceList());
                    selectedObject.getBranchSetting().getBranch().getCurrency().setId(waybill.getBranchSetting().getBranch().getCurrency().getId());
                    selectedObject.getBranchSetting().getBranch().setIsAgency(waybill.getBranchSetting().getBranch().isIsAgency());
                    selectedObject.getBranchSetting().setIsUnitPriceAffectedByDiscount(waybill.getBranchSetting().isIsUnitPriceAffectedByDiscount());
                    selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                    selectedObject.setIsFuel(waybill.isIsFuel());

                    for (BranchSetting b : listOfBranch) {
                        if (b.getBranch().getId() == selectedObject.getBranchSetting().getBranch().getId()) {
                            selectedObject.getBranchSetting().setParoAccountCode(b.getParoAccountCode());
                            selectedObject.getBranchSetting().setParoUrl(b.getParoUrl());
                            selectedObject.getBranchSetting().setParoResponsibleCode(b.getParoResponsibleCode());
                            selectedObject.getBranchSetting().setParoBranchCode(b.getParoBranchCode());
                            break;
                        }
                    }

                    branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                    changeExchange();
                    changeDueDate();
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                    setDocument();
                    processType = 2;
                    RequestContext.getCurrentInstance().update("frmInvoiceProcess");

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {//siparişten fatura oluşturulacak ise sipariş gelir.
                    order = (Order) ((ArrayList) sessionBean.parameter).get(i);

                    listOfItemForOrder = new ArrayList<>();
                    isCreateInvFromOrder = true;
                    selectedObject = new Invoice();
                    selectedObject.setPriceDifferenceInvoice(new Invoice());
                    selectedObject.setAccount(order.getAccount());
                    selectedObject.setDescription("");
                    selectedObject.setDispatchAddress("");
                    selectedObject.setDispatchDate(new Date());
                    selectedObject.setInvoiceDate(new Date());
                    selectedObject.setDueDate(new Date());
                    selectedObject.setDocumentNumber(order.getDocumentNumber());
                    selectedObject.setIsPurchase(true);
                    selectedObject.getStatus().setId(28);//açık
                    selectedObject.setRoundingPrice(BigDecimal.ZERO);
                    selectedObject.getType().setId(59);
                    selectedObject.getBranchSetting().getBranch().setId(order.getBranchSetting().getBranch().getId());
                    selectedObject.getBranchSetting().setIsCentralIntegration(order.getBranchSetting().isIsCentralIntegration());
                    selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(order.getBranchSetting().isIsInvoiceStockSalePriceList());
                    selectedObject.getBranchSetting().getBranch().getCurrency().setId(order.getBranchSetting().getBranch().getCurrency().getId());
                    selectedObject.getBranchSetting().getBranch().setIsAgency(order.getBranchSetting().getBranch().isIsAgency());
                    selectedObject.getBranchSetting().setIsUnitPriceAffectedByDiscount(order.getBranchSetting().isIsUnitPriceAffectedByDiscount());
                    selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                
                    branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                    changeExchange();
                    changeDueDate();
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                    setDocument();
                    listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " AND iw.status_id = 13 ");
                    processType = 2;

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof CustomerAgreements) {//müşteri mutabakatlarından geldiyse
                    customerAgreements = (CustomerAgreements) ((ArrayList) sessionBean.parameter).get(i);
                    isCreateInvFromCustAgreement = true;
                    credit = new CreditReport();

                    selectedObject = new Invoice();
                    selectedObject.setPriceDifferenceInvoice(new Invoice());
                    selectedObject.setAccount(customerAgreements.getAccount());
                    selectedObject.setRoundingPrice(BigDecimal.ZERO);
                    selectedObject.setInvoiceDate(new Date());
                    selectedObject.setDispatchDate(new Date());
                    selectedObject.setDueDate(new Date());
                    selectedObject.getType().setId(23);
                    selectedObject.getStatus().setId(28);//açık
                    selectedObject.getCurrency().setId(customerAgreements.getCurrency().getId());
                    selectedObject.setDispatchAddress(customerAgreements.getAccount().getAddress());
                    selectedObject.setIsPeriodInvoice(true);
                    selectedObject.setIsPurchase(false);
                    selectedObject.getBranchSetting().getBranch().setId(customerAgreements.getBranchSetting().getBranch().getId());
                    selectedObject.getBranchSetting().setIsCentralIntegration(customerAgreements.getBranchSetting().isIsCentralIntegration());
                    selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(customerAgreements.getBranchSetting().isIsInvoiceStockSalePriceList());
                    selectedObject.getBranchSetting().getBranch().getCurrency().setId(customerAgreements.getBranchSetting().getBranch().getCurrency().getId());
                    selectedObject.getBranchSetting().getBranch().setIsAgency(customerAgreements.getBranchSetting().getBranch().isIsAgency());
                    selectedObject.getBranchSetting().setIsUnitPriceAffectedByDiscount(customerAgreements.getBranchSetting().isIsUnitPriceAffectedByDiscount());
                    for (BranchSetting b : listOfBranch) {
                        if (b.getBranch().getId() == selectedObject.getBranchSetting().getBranch().getId()) {
                            selectedObject.getBranchSetting().setParoAccountCode(b.getParoAccountCode());
                            selectedObject.getBranchSetting().setParoUrl(b.getParoUrl());
                            selectedObject.getBranchSetting().setParoResponsibleCode(b.getParoResponsibleCode());
                            selectedObject.getBranchSetting().setParoBranchCode(b.getParoBranchCode());
                            break;
                        }
                    }
                    branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                    listOfItemForAgreement = new ArrayList<>();
                    changeExchange();
                    changeDueDate();
                    RequestContext.getCurrentInstance().update("frmInvoiceProcess:outExchange frmInvoiceProcess:txtExchangeRate");
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                    setDocument();
                    processType = 2;

                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof CreditReport) {//kredi sayfasından geldi ise
                    credit = (CreditReport) ((ArrayList) sessionBean.parameter).get(i);
                    isCreateInvFromCustAgreement = true;
                    selectedObject = new Invoice();
                    selectedObject.setPriceDifferenceInvoice(new Invoice());

                    customerAgreements = new CustomerAgreements();

                    customerAgreements.setBeginDate(credit.getBeginDate());
                    customerAgreements.setEndDate(credit.getEndDate());
                    customerAgreements.setAccount(credit.getAccount());
                    selectedObject.setAccount(credit.getAccount());
                    selectedObject.setInvoiceDate(new Date());
                    selectedObject.setRoundingPrice(BigDecimal.ZERO);
                    selectedObject.setDispatchDate(new Date());
                    selectedObject.setDueDate(new Date());
                    selectedObject.getType().setId(23);
                    selectedObject.getStatus().setId(28);//açık
                    selectedObject.getCurrency().setId(credit.getCurrency().getId());
                    selectedObject.setDispatchAddress(credit.getAccount().getAddress());
                    selectedObject.setIsPeriodInvoice(true);
                    selectedObject.setIsPurchase(false);
                    selectedObject.getBranchSetting().getBranch().setId(credit.getBranchSetting().getBranch().getId());
                    selectedObject.getBranchSetting().setIsCentralIntegration(credit.getBranchSetting().isIsCentralIntegration());
                    selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(credit.getBranchSetting().isIsInvoiceStockSalePriceList());
                    selectedObject.getBranchSetting().getBranch().getCurrency().setId(credit.getBranchSetting().getBranch().getCurrency().getId());
                    selectedObject.getBranchSetting().getBranch().setIsAgency(credit.getBranchSetting().getBranch().isIsAgency());
                    selectedObject.getBranchSetting().setIsUnitPriceAffectedByDiscount(credit.getBranchSetting().isIsUnitPriceAffectedByDiscount());
                    for (BranchSetting b : listOfBranch) {
                        if (b.getBranch().getId() == selectedObject.getBranchSetting().getBranch().getId()) {
                            selectedObject.getBranchSetting().setParoAccountCode(b.getParoAccountCode());
                            selectedObject.getBranchSetting().setParoUrl(b.getParoUrl());
                            selectedObject.getBranchSetting().setParoResponsibleCode(b.getParoResponsibleCode());
                            selectedObject.getBranchSetting().setParoBranchCode(b.getParoBranchCode());
                            break;
                        }
                    }
                    branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                    changeExchange();
                    changeDueDate();
                    listOfItemForAgreement = new ArrayList<>();
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                    setDocument();
                    RequestContext.getCurrentInstance().update("frmInvoiceProcess:outExchange frmInvoiceProcess:txtExchangeRate");
                    processType = 2;
                }
            }
        }
        oldStatusId = selectedObject.getStatus().getId();
        oldDiscountPrice = selectedObject.getDiscountPrice();
        oldDiscountRate = selectedObject.getDiscountRate();

        if (selectedObject.getPosId() > 0) {
            info = sessionBean.loc.getString("thisinvoiceiscreatedbypos");
        }
        if (selectedObject.getType().getId() == 27) {
            info = sessionBean.loc.getString("thisinvoiceisreturninvoice");
        }

        selectedObject.getListOfWarehouse().clear();
        warehouseName = "";
        if (processType == 2) {
            if (!selectedObject.isIsFuel()) {

                if (selectedObject.getWarehouseIdList() != null && !selectedObject.getWarehouseIdList().equals("")) {
                    String[] parts = selectedObject.getWarehouseIdList().split(",");
                    for (int i = 0; i < parts.length; i++) {
                        for (Warehouse w : listWarehouses) {
                            if (w.getId() == Integer.valueOf(parts[i]) && !selectedObject.getListOfWarehouse().contains(w)) {
                                selectedObject.getListOfWarehouse().add(w);
                                warehouseName = warehouseName + "," + w.getName();
                            }
                        }
                    }
                    if (!warehouseName.equals("")) {
                        warehouseName = warehouseName.substring(1, warehouseName.length());
                    }
                }

            }

            if (selectedObject.getType().getId() == 26) {// Fiyat Farkı Faturası
                selectedObject.getPriceDifferenceInvoice().setDocumentSerial(selectedObject.getPriceDifferenceInvoice().getDocumentSerial() + "" + selectedObject.getPriceDifferenceInvoice().getDocumentNumber());
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{2, 3, 4, 343}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{1, 2, 3, 4}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    /**
     * Bu metotfatura kaydetmek için kullanılır.Tip 1 yeni fatura ekler,tip 2
     * güncelleştirme yapar.
     */
    public void save() {
        if ((!selectedObject.isIsPurchase() && selectedObject.getdNumber().getActualNumber() <= selectedObject.getdNumber().getEndNumber() && selectedObject.getdNumber().getActualNumber() >= selectedObject.getdNumber().getBeginNumber()) || selectedObject.isIsPurchase()) {

            if (isChangeInfoDialog) {///Cari değiştime için kaydetme
                RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmChangeInfo').show();");

            } else if (sessionBean.isPeriodClosed(selectedObject.getInvoiceDate())) {
                boolean isSaveDocNumber = true;

                if (sessionBean.getUser().getLastBranch().isIsAgency()) {

                    if (selectedObject.isIsPurchase()) {
                        if (selectedObject.getDocumentSerial().length() + selectedObject.getDocumentNumber().length() > 16) {
                            isSaveDocNumber = false;
                        }
                    } else {

                        if (processType == 1) {
                            if (String.valueOf(selectedObject.getdNumber().getActualNumber()).length() + selectedObject.getdNumber().getSerial().length() > 16) {
                                isSaveDocNumber = false;
                            }
                        } else {
                            if (String.valueOf(selectedObject.getdNumber().getActualNumber()).length() + selectedObject.getDocumentSerial().length() > 16) {
                                isSaveDocNumber = false;
                            }
                        }
                    }
                }

                if (isSaveDocNumber) {

                    int result = 0;
                    if (selectedObject.getStatus().getId() == 30 && !isSave) {//iptale çekildi ise ve ilk soruldu ise uyarı ver; isSave true ise girmez devam eder.
                        isSave = false;
                        RequestContext.getCurrentInstance().execute("PF('dlgConfirm').show();");
                        return;
                    }

                    if (!isCreateInvFromWaybill && !isCreateInvFromOrder) {//irsaliyeden veya siparişten fatura oluşturma DEĞİL

                        if (selectedObject.getdNumber().getId() > 0) {//satş faturası ise ve belge seçti ise 
                            selectedObject.setDocumentNumber(selectedObject.getdNumber().getActualNumber() + "");
                        }

                        if (processType == 1) {

                            selectedObject.getListOfWarehouse().clear();
                            selectedObject.getListOfWarehouse().add(selectedObject.getWarehouse());
                            selectedObject.setWarehouseIdList(Integer.toString(selectedObject.getWarehouse().getId()));
                            //iskonto değişikliği anında itemlara yansımaması için temp değişkende tuttuk.
                            selectedObject.setDiscountPrice(oldDiscountPrice);
                            selectedObject.setDiscountRate(oldDiscountRate);

                            if (branchSettingForSelection.isIsEInvoice()) {
                                if ((!selectedObject.isIsPurchase() && selectedObject.getType().getId() != 26 && selectedObject.getType().getId() != 27) || (selectedObject.isIsPurchase() && selectedObject.getType().getId() == 27)) {
                                    selectedObject.setIsEInvoice(true);
                                } else {
                                    selectedObject.setIsEInvoice(false);
                                }
                            }

                            result = invoiceService.create(selectedObject);
                            if (result > 0) {
                                selectedObject.setUserCreated(sessionBean.getUser());
                                selectedObject.setDateCreated(new Date());
                                isSendCenter = true;

                                selectedObject.setId(result);
                                if (selectedObject.getdNumber().getId() > 0) {
                                    selectedObject.setDocumentNumber("" + selectedObject.getdNumber().getActualNumber());
                                }
                                activeIndex = 1;
                                selectedObject.setTotalMoney(BigDecimal.ZERO);
                                selectedObject.setTotalTax(BigDecimal.ZERO);
                                selectedObject.setTotalPrice(BigDecimal.ZERO);
                                List<Object> list = new ArrayList<>();
                                list.add(selectedObject);
                                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                            }
                        } else if (processType == 2) {

                            //iskonto değişikliği anında itemlara yansımaması için temp değişkende tuttuk.
                            BigDecimal oldDisRate = selectedObject.getDiscountRate(),
                                    oldDisPrice = selectedObject.getDiscountPrice();

                            //veritabanına gitmesi için yenide objeye attık.
                            selectedObject.setDiscountPrice(oldDiscountPrice);
                            selectedObject.setDiscountRate(oldDiscountRate);
                            if (selectedObject.isIsWait()) {
                                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                                InvoiceItemTabBean invoiceItemTabBean = (InvoiceItemTabBean) viewMap.get("invoiceItemTabBean");
                                selectedObject.setWaitInvoiceItemJson(invoiceItemService.jsonArrayInvoiceItemsForWaitedInvoice(invoiceItemTabBean.listOfObjects, selectedObject));
                            }
                            result = invoiceService.update(selectedObject);//güncelle

                            //satınalma faturasında PARA tipindeki iskonto tutarı değişti ise dialog aç. 
                            if (oldDiscountPrice == null) {
                                oldDiscountPrice = BigDecimal.ZERO;
                            }

                            if (result > 0) {
                                //iskonto birim fiyatı etkilesin ise ve satınalma faturasında karlılık değişimi var ise dialog aç
                                if (selectedObject.getDiscountPrice() == null || selectedObject.getDiscountRate() == null) {
                                    selectedObject.setDiscountPrice(BigDecimal.ZERO);
                                    selectedObject.setDiscountRate(BigDecimal.ZERO);
                                }
                                if (oldDisRate == null || oldDisPrice == null) {
                                    oldDisRate = BigDecimal.ZERO;
                                    oldDisPrice = BigDecimal.ZERO;
                                }

                                if (selectedObject.getBranchSetting().isIsUnitPriceAffectedByDiscount()
                                        && selectedObject.isIsPurchase()
                                        && !isCreateInvFromCustAgreement
                                        && !isCreateInvFromWaybill
                                        && (//iskonto tipi tutar ise ve değişti ise
                                        (selectedObject.getDiscountPrice().compareTo(oldDisPrice) != 0 && !selectedObject.isIsDiscountRate())
                                        ||//veya iskonto tipi oran ise ve değişti ise
                                        (selectedObject.getDiscountRate().compareTo(oldDisRate) != 0 && selectedObject.isIsDiscountRate()))) {
                                    RequestContext.getCurrentInstance().execute("sowDiscountChange();");

                                } else {
                                    isSendCenter = true;
                                    marwiz.goToPage("/pages/finance/invoice/invoice.xhtml", null, 1, 24);
                                }
                            }
                        }

                    } else if (isCreateInv) {//irsaliyeden veya siparişten fatura oluşacak ise ve ürünlerin json bilgisi oluştu ise

                        if (isCreateInvFromWaybill) {
                            calculateDiscountForAgreementAndWaybill(listOfItemForWaybill);
                            selectedObject.setDiscountPrice(oldDiscountPrice);
                            selectedObject.setDiscountRate(oldDiscountRate);

                            if (branchSettingForSelection.isIsEInvoice()) {
                                if ((!selectedObject.isIsPurchase() && selectedObject.getType().getId() != 26 && selectedObject.getType().getId() != 27) || (selectedObject.isIsPurchase() && selectedObject.getType().getId() == 27)) {
                                    selectedObject.setIsEInvoice(true);
                                } else {
                                    selectedObject.setIsEInvoice(false);
                                }
                            }

                            result = invoiceService.createInvoiceForWaybill(selectedObject, waybill, listOfItemForWaybill);//irsaliyeden faturayı oluştur.
                            selectedObject.setId(result);
                            if (result > 0) {
                                isSendCenter = true;
                                selectedObject.setTotalMoney(BigDecimal.ZERO);
                                selectedObject.setTotalTax(BigDecimal.ZERO);
                                selectedObject.setTotalPrice(BigDecimal.ZERO);

                                for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Waybill) {//irsaliyeden fatura oluşturulacak ise irsaliye objesi gelir.
                                        ((Waybill) ((ArrayList) sessionBean.parameter).get(i)).getStatus().setId(26);//kapattık  
                                        ((Waybill) ((ArrayList) sessionBean.parameter).get(i)).setIsInvoice(true);
                                    }
                                }
                                destroy(); //paro log tablosuna kayıt atılması için çağırıldı.gotopage type 0 ile kullanıldığında destroy çalışmıyordu.
                                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", sessionBean.parameter, 0, 41);//irsaliye işlemlerine geri döndük
                            }
                        } else if (isCreateInvFromOrder) {
                            String orderIds = "";
                            for (InvoiceItem invoiceItem : listOfItemForOrder) {
                                if (invoiceItem.getOrderIds() != null && !invoiceItem.getOrderIds().equals("")) {
                                    orderIds = orderIds + invoiceItem.getOrderIds() + ",";
                                }

                            }
                            orderIds = orderIds.substring(0, orderIds.length() - 1);
//                        System.out.println("orderIds"+orderIds);
                            String[] split = orderIds.split(",");
                            orderIds = "";
                            for (int i = 0; i < split.length; i++) {
                                int flag = 0;

                                for (int j = 0; j < i; j++) {
                                    if (split[i].equals(split[j])) {
                                        flag = 1;
                                        break;
                                    }
                                }

                                if (flag == 0) {
                                    orderIds = orderIds + split[i] + ",";
                                }

                            }
                            orderIds = orderIds.substring(0, orderIds.length() - 1);
//                        System.out.println("orderIds"+orderIds);
                            selectedObject.setOrderIds(orderIds);
                            selectedObject.getListOfWarehouse().clear();
                            selectedObject.getListOfWarehouse().add(selectedObject.getWarehouse());

                            calculateDiscountForAgreementAndWaybill(listOfItemForOrder);
                            selectedObject.setDiscountPrice(oldDiscountPrice);
                            selectedObject.setDiscountRate(oldDiscountRate);

                            result = invoiceService.createInvoiceForOrder(selectedObject, listOfItemForOrder);//irsaliyeden faturayı oluştur.
                            selectedObject.setId(result);
                            if (result > 0) {
                                isSendCenter = true;
                                selectedObject.setTotalMoney(BigDecimal.ZERO);
                                selectedObject.setTotalTax(BigDecimal.ZERO);
                                selectedObject.setTotalPrice(BigDecimal.ZERO);

                                for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {//siparişten fatura oluşturulacak ise siparişten objesi gelir.
                                        ((Order) ((ArrayList) sessionBean.parameter).get(i)).getStatus().setId(60);//kapattık  
                                        ((Order) ((ArrayList) sessionBean.parameter).get(i)).setIsInvoice(true);
                                    }
                                }
                                marwiz.goToPage("/pages/finance/order/order.xhtml", sessionBean.parameter, 1, 228);//sipariş sayfasına geri döndük
                            }
                        }

                    } else { // faturanın itemlarının tüm alanları doldurulmadı uyarı ver

                        if (selectedObject.getId() == 0) {
                            result = -102;
                        } else {
                            //iskonto değişikliği anında itemlara yansımaması için temp değişkende tuttuk.
                            BigDecimal oldDisRate = selectedObject.getDiscountRate(),
                                    oldDisPrice = selectedObject.getDiscountPrice();

                            //veritabanına gitmesi için yenide objeye attık.
                            selectedObject.setDiscountPrice(oldDiscountPrice);
                            selectedObject.setDiscountRate(oldDiscountRate);
                            result = invoiceService.update(selectedObject);//güncelle

                            //satınalma faturasında PARA tipindeki iskonto tutarı değişti ise dialog aç. 
                            if (oldDiscountPrice == null) {
                                oldDiscountPrice = BigDecimal.ZERO;
                            }

                            if (result > 0) {
                                //iskonto birim fiyatı etkilesin ise ve satınalma faturasında karlılık değişimi var ise dialog aç
                                if (selectedObject.getDiscountPrice() == null || selectedObject.getDiscountRate() == null) {
                                    selectedObject.setDiscountPrice(BigDecimal.ZERO);
                                    selectedObject.setDiscountRate(BigDecimal.ZERO);
                                }
                                if (oldDisRate == null || oldDisPrice == null) {
                                    oldDisRate = BigDecimal.ZERO;
                                    oldDisPrice = BigDecimal.ZERO;
                                }

                                if (selectedObject.getBranchSetting().isIsUnitPriceAffectedByDiscount()
                                        && selectedObject.isIsPurchase()
                                        && !isCreateInvFromCustAgreement
                                        && !isCreateInvFromWaybill
                                        && (//iskonto tipi tutar ise ve değişti ise
                                        (selectedObject.getDiscountPrice().compareTo(oldDisPrice) != 0 && !selectedObject.isIsDiscountRate())
                                        ||//veya iskonto tipi oran ise ve değişti ise
                                        (selectedObject.getDiscountRate().compareTo(oldDisRate) != 0 && selectedObject.isIsDiscountRate()))) {
                                    RequestContext.getCurrentInstance().execute("sowDiscountChange();");

                                } else {
                                    isSendCenter = true;
                                    marwiz.goToPage("/pages/finance/invoice/invoice.xhtml", null, 1, 24);
                                }
                            }
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

                        case -103://aynı belge nolu fatura var ise dialog aç
                            duplicateInvoice = invoiceService.findDuplicateInvoice(selectedObject);
                            RequestContext.getCurrentInstance().execute("PF('dlgDocumentNumberWarning').show();");
                            break;
                        case -104://kasa -ye düşürülünce hata ver
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                            break;

                        default:
                            sessionBean.createUpdateMessage(result);
                            break;
                    }
                } else {

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("serialnumberandsequencenumbercannotexceed16charactersintotal")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesequencenumberenteredmustbeavaluebetweenthestartnumberandtheendnumberpleaseenterasequencenumberinthisrange")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }
        isDelete = false;

    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            if (isChangeInfoDialog) {
                selectedChangingInvoice.setAccount(accountBookFilterBean.getSelectedData());
                if (!selectedObject.isIsPurchase()) {
                    selectedChangingInvoice.setDispatchAddress(selectedChangingInvoice.getAccount().getAddress());
                }
                selectedChangingInvoice.setTaxPayerTypeId(accountBookFilterBean.getSelectedData().getTaxpayertype_id());
                changeDueDate();
                RequestContext.getCurrentInstance().update("frmChangeInvoiceInfo:pgrChangeInvoiceInfo");
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
                selectedObject.setDispatchAddress(selectedObject.isIsPurchase() ? "" : selectedObject.getAccount().getAddress());
                selectedObject.setTaxPayerTypeId(accountBookFilterBean.getSelectedData().getTaxpayertype_id());
                changeDueDate();
                RequestContext.getCurrentInstance().update("frmInvoiceProcess:pgrInvoiceProcess");
            }
            accountBookFilterBean.setSelectedData(null);
        } else if (invoiceBookFilterBean.getSelectedData() != null) {
            selectedObject.setPriceDifferenceInvoice(invoiceBookFilterBean.getSelectedData());
            selectedObject.getPriceDifferenceInvoice().setDocumentSerial(selectedObject.getPriceDifferenceInvoice().getDocumentSerial() + "" + selectedObject.getPriceDifferenceInvoice().getDocumentNumber());

            RequestContext.getCurrentInstance().update("frmInvoiceProcess:txtConnectedInvoice");
            RequestContext.getCurrentInstance().update("frmInvoiceProcess:cldInvoiceDate");
            invoiceBookFilterBean.setSelectedData(null);

        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab");
        RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoicePaymentsTab");
    }

    /**
     * faturanın iskontosunu hesaplar
     *
     */
    public void calcDiscount() {

        //-------------faturanın iskontosuz tutarı bulunarak, bu tutar üzerinden tekrar hesaplama yapıldı.
        //-------------bu şekilde yapmayınca eski iskontolu fiyatın üstüne tekrar yeni girilen iskontoyu uyguluyordu.
        BigDecimal invTotalPriceWithoutDiscount = BigDecimal.ZERO;
        if (selectedObject.getDiscountPrice() != null && selectedObject.getDiscountPrice().doubleValue() > 0) {
            invTotalPriceWithoutDiscount = selectedObject.getTotalPrice().add(selectedObject.getDiscountPrice());
        } else {
            invTotalPriceWithoutDiscount = selectedObject.getTotalPrice();
        }

        if (!selectedObject.isIsDiscountRate()) {//tutar girildi oran hesapla
            if (oldDiscountPrice != null && oldDiscountPrice.doubleValue() > 0) {
                if (invTotalPriceWithoutDiscount != null && invTotalPriceWithoutDiscount.compareTo(BigDecimal.valueOf(0)) == 1) {
                    oldDiscountRate = oldDiscountPrice.multiply(new BigDecimal(100)).divide(invTotalPriceWithoutDiscount, 4, RoundingMode.HALF_EVEN);
                } else {
                    oldDiscountRate = BigDecimal.ZERO;
                }

            } else {
                oldDiscountRate = BigDecimal.ZERO;
            }
        } else if (selectedObject.isIsDiscountRate()) {//oran girildi ise tutar hesapla
            if (oldDiscountRate != null && oldDiscountRate.doubleValue() > 0) {
                oldDiscountPrice = (invTotalPriceWithoutDiscount.multiply(oldDiscountRate)).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
            } else {
                oldDiscountPrice = BigDecimal.ZERO;
            }
        }
        selectedObject.setTotalPrice(invTotalPriceWithoutDiscount.subtract(oldDiscountPrice));
        calcInvoicePrice();
        //RequestContext.getCurrentInstance().update("tbvInvoice:frmInvoiceStokTab:dtbStock");
        //tüm ürünlere iskonto oranını uygula
        RequestContext.getCurrentInstance().execute("invItemCalculater();");
    }

    /**
     * faturya iskonto veya yuvarlama değeri girildiğinde tutarını yenıden
     * hesaplar
     */
    public void calcInvoicePrice() {

        selectedObject.setTotalMoney(selectedObject.getTotalPrice().add(selectedObject.getTotalTax()));

        if (selectedObject.getDiscountPrice() != null && selectedObject.getDiscountPrice().doubleValue() > 0) {
            selectedObject.setTotalMoney(selectedObject.getTotalMoney().subtract(selectedObject.getDiscountPrice()));
        }

        if (selectedObject.getRoundingPrice() != null && selectedObject.getRoundingPrice().doubleValue() != 0) {
            if ((selectedObject.getRoundingPrice().doubleValue() > 0 && selectedObject.getRoundingPrice().doubleValue() <= branchSetting.getRoundingConstraint().doubleValue())
                    || (selectedObject.getRoundingPrice().doubleValue() < 0 && selectedObject.getRoundingPrice().doubleValue() >= branchSetting.getRoundingConstraint().multiply(new BigDecimal(-1)).doubleValue())) {
                selectedObject.setTotalMoney(selectedObject.getTotalMoney().add(selectedObject.getRoundingPrice()));
            }
        } else {
            selectedObject.setRoundingPrice(BigDecimal.ZERO);
        }
    }

    /**
     * bu metot para birimi değiştiğinde tetiklenir.
     */
    public void changeExchange() {
        selectedObject.setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getCurrency(), sessionBean.getUser().getLastBranch().getCurrency(), sessionBean.getUser()));
        exchange = "(" + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0) + " -> " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) + ")";// örn: $->€
    }

    public void discountPriceOrRate(int type) {
        if (type == 0) {
            selectedObject.setIsDiscountRate(true);
        } else {
            selectedObject.setIsDiscountRate(false);;
        }
    }

    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getdNumber().getId()) {
                selectedObject.getdNumber().setActualNumber(dn.getActualNumber());
                selectedObject.getdNumber().setSerial(dn.getSerial());
                selectedObject.setDocumentSerial(dn.getSerial());
                selectedObject.setDocumentNumber("" + dn.getActualNumber());
                selectedObject.getdNumber().setBeginNumber(dn.getBeginNumber());
                selectedObject.getdNumber().setEndNumber(dn.getEndNumber());

                break;
            }
        }
    }

    public void setDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getdNumber().getId()) {
                selectedObject.getdNumber().setBeginNumber(dn.getBeginNumber());
                selectedObject.getdNumber().setEndNumber(dn.getEndNumber());
                break;
            }
        }
    }

    /**
     * fatura iptale çekilsinmi sorulduğunda yes tıklanırsa çalışır.
     *
     * @param isYes true gelirse iptal olarak kaydeder. false gelirse geri alır.
     */
    public void saveYesNo(boolean isYes) {
        if (isYes) {
            isSave = true;
            save();
        } else {
            selectedObject.getStatus().setId(oldStatusId);//tekrar durumu eski haline çektik
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
                isThere = true;
            } else {
                isPurchaseMinStockLevel = false;
            }
        } else {
            isPurchaseMinStockLevel = false;
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
                isThere = true;
            } else {
                isSalesMaxStockLevel = false;
            }

        } else {
            isSalesMaxStockLevel = false;
        }

    }

    public void testBeforeDelete() {
        isThere = false;
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        InvoiceItemTabBean invoiceItemTabBean = (InvoiceItemTabBean) viewMap.get("invoiceItemTabBean");

        for (InvoiceItem item : invoiceItemTabBean.getListOfObjects()) {

            if (!selectedObject.isIsPurchase() && selectedObject.getType().getId() == 59 && (item.getStock().getStockInfo().getMaxStockLevel() != null)) {
                salesMaxStockLevelControl(item);
                if (isSalesMaxStockLevel) {
                    break;
                }
            }

            if (selectedObject.isIsPurchase() && selectedObject.getType().getId() == 59 && (!item.getStock().getStockInfo().isIsMinusStockLevel())) { // Stok kartında ürün eksiye düşebilir mi seçili değilse                
                stockPurchaseLevelControl(item);
                if (isPurchaseMinStockLevel) {
                    break;
                }
            }

        }
        if (!isThere) {
            if (sessionBean.isPeriodClosed(selectedObject.getInvoiceDate())) {
                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";
                relatedRecord = "";
                controlDeleteList.clear();
                controlDeleteList = invoiceService.testBeforeDelete(selectedObject);
                if (!controlDeleteList.isEmpty()) {
                    if (controlDeleteList.get(0).getR_response() < 0) {
                        if (controlDeleteList.get(0).getR_response() == -100) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        } else if (controlDeleteList.get(0).getR_response() == -101) {
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtopricedifferenceinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("beforepleaseyoudeleteit");
                            deleteControlMessage2 = sessionBean.getLoc().getString("invoiceno") + " : ";
                            relatedRecordId = controlDeleteList.get(0).getR_record_id();
                            relatedRecord = controlDeleteList.get(0).getR_recordno();
                            RequestContext.getCurrentInstance().update("dlgRelatedRecordInvoice");
                            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInvoice').show();");
                        }
                    } else {
                        RequestContext.getCurrentInstance().execute("PF('dlgConfirmDelete').show();");
                    }
                } else {
                    RequestContext.getCurrentInstance().execute("PF('dlgConfirmDelete').show();");
                }
            }
        }
    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedObject.getInvoiceDate())) {
            int result = 0;
            if (!selectedObject.isIsPeriodInvoice()) {
                result = invoiceService.delete(selectedObject);
            } else {
                result = invoiceService.deletePeriodInvoice(selectedObject);
            }
            if (result > 0) {
                isDelete = true;
                isSendCenter = true;
                selectedObject.setDeleted(true);
                marwiz.goToPage("/pages/finance/invoice/invoice.xhtml", null, 1, 24);
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInvoice').hide();");
        context.execute("goToRelatedRecordInvoice();");
    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        switch (controlDeleteList.get(0).getR_response()) {
            case -101:
                Invoice invoice = new Invoice();
                invoice.setId(relatedRecordId);
                invoice = invoiceService.findInvoice(invoice);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Invoice) {
                        list.remove(i);
                    }
                }
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            default:
                break;
        }
    }

    /**
     * Bu metot sayfadan çıkıldığı anda tetiklenir. Merkez entegrasyonu var ise
     * ve değişikli oldu ise gerekli fonksiyonu tetkiler
     */
    @PreDestroy
    public void destroy() {
        int result;
        if (isSendCenter && selectedObject.getBranchSetting().isIsCentralIntegration()) {//merkeze gönderilecek ise ve merkez entegrasyonu var ise ve silinmedi ise

            result = invoiceService.sendInvoiceCenter(selectedObject);

            if (result > 0) {//işlem başarılı loga kayıt eklendi ise gönderme metodunu çağır. dönen değer satış id si
                if (!selectedObject.isIsPurchase()) {//satış ise
                    SendSale sale = sendSaleService.findBySaleId(result);
                    sendSaleService.sendSaleToCenter(sale);
                } else {//satınalma ise
                    SendPurchase purchase = sendPurchaseService.findByInvoiceId(selectedObject.getId());
                    sendPurchaseService.sendPurchaseToCenter(purchase);
                }
            }
        }

        System.out.println("----isSendCenter----" + isSendCenter);
        System.out.println("----selectedObject.getBranchSetting().getParoUrl()----" + selectedObject.getBranchSetting().getParoUrl());
        System.out.println("----selectedObject.getBranchSetting().getParoAccountCode()----" + selectedObject.getBranchSetting().getParoAccountCode());
        System.out.println("----selectedObject.getBranchSetting().getParoBranchCode()----" + selectedObject.getBranchSetting().getParoBranchCode());
        System.out.println("----selectedObject.getBranchSetting().getParoResponsibleCode()----" + selectedObject.getBranchSetting().getParoResponsibleCode());
        System.out.println("----selectedObject.isIsPurchase()----" + selectedObject.isIsPurchase());

        if (isSendCenter && !selectedObject.isIsPurchase()) {
            if (selectedObject.getBranchSetting().getParoUrl() != null
                    && selectedObject.getBranchSetting().getParoAccountCode() != null
                    && selectedObject.getBranchSetting().getParoBranchCode() != null
                    && selectedObject.getBranchSetting().getParoResponsibleCode() != null) {
                int saleId = invoiceService.findSaleForInvoice(selectedObject.getBranchSetting(), selectedObject.getId(), isDelete);
                System.out.println("----saleID---" + saleId);
                invoiceService.createParoSales(saleId);
            }

        }

    }

    public void loadJson() {
        DocumentTemplate documentTemplate = documentTemplateService.bringInvoiceTemplate(62);
        Gson gson = new Gson();
        PrintDocumentTemplate logs = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
        }.getType());

        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("printPanel");
        droppable.getChildren().clear();

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        InvoiceItemTabBean invoiceItemTabBean = (InvoiceItemTabBean) viewMap.get("invoiceItemTabBean");
        InvoicePriceDifferenceStockTabBean invoicePriceDifferenceStockTabBean = (InvoicePriceDifferenceStockTabBean) viewMap.get("invoicePriceDifferenceStockTabBean");

        if (logs != null) {
            double table_height = 0;
            double table_top = 0;

            if (selectedObject.getType().getId() != 26) {
                for (InvoiceItem i : invoiceItemTabBean.getListOfObjects()) {
                    table_height = table_height + (i.getStock().getName().length() * 2) + 8;
                }
            } else {
                for (InvoiceItem i : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                    table_height = table_height + (i.getStock().getName().length() * 2) + 8;
                }
            }

            for (DocumentTemplateObject dto : logs.getListOfObjects()) {
                if (dto.getKeyWord().contains("itemspnl")) {
                    table_top = dto.getTop();
                    break;
                }
            }

            for (DocumentTemplateObject dto : logs.getListOfObjects()) {
                if (dto.getKeyWord().contains("container")) {
                    OutputPanel op = new OutputPanel();
                    if (dto.getTop() < table_top) {
                        op.setStyle("border: 1px solid black ;width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                    } else {
                        dto.setTop(dto.getTop() * 4 + table_height);
                        op.setStyle("border: 1px solid black ;width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() + "px;left:" + dto.getLeft() * 4 + "px;");
                    }
                    op.setId(dto.getKeyWord());

                    droppable.getChildren().add(op);
                } else if (dto.getKeyWord().contains("imagepnl")) {
                    GraphicImage gr = new GraphicImage();
                    if (dto.getTop() < table_top) {
                        gr.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                    } else {
                        dto.setTop(dto.getTop() * 4 + table_height);
                        gr.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() + "px;left:" + dto.getLeft() * 4 + "px;");
                    }
                    gr.setUrl("../upload/template/" + documentTemplate.getId() + "_" + dto.getKeyWord() + "." + "png");
                    gr.setCache(false);
                    droppable.getChildren().add(gr);
                    RequestContext.getCurrentInstance().update("printPanel");
                } else if (dto.getKeyWord().contains("itemspnl")) {
                    OutputPanel op = new OutputPanel();
                    op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                    op.setId(dto.getKeyWord());

                    droppable.getChildren().add(op);
                    RequestContext.getCurrentInstance().update("printPanel");
                    String direction = documentTemplate.isIsVertical() == true ? "horizontal" : "landscape";

                    StringBuilder sb = new StringBuilder();
                    sb.append(
                            " <style>"
                            + "        #" + dto.getKeyWord() + " table {"
                            + "            font-family: arial, sans-serif;"
                            + "            border-collapse: collapse;"
                            + "            width: 100%;"
                            + "       table-layout: fixed;"
                            + "        }"
                            + "        #" + dto.getKeyWord() + " table tr td, #" + dto.getKeyWord() + " table tr th {"
                            + "            border: 1px solid #dddddd;"
                            + "            text-align: " + dto.getFontAlign() + ";"
                            + "            padding: 8px;"
                            + "            word-wrap: break-word;"
                            // + "            height: 33px;"
                            + "            font-size: " + dto.getFontSize() + "pt;"
                            + "        }"
                            + "   @page { size: " + direction + ";"
                            + "margin-top: " + documentTemplate.getMargin_top() + "mm;"
                            + "margin-bottom: " + documentTemplate.getMargin_bottom() + "mm;"
                            + "margin-left: " + documentTemplate.getMargin_left() + "mm;"
                            + "margin-right: " + documentTemplate.getMargin_right() + "mm;}"
                            + "   @media print {"
                            + "     html, body {"
                            + "    width: " + documentTemplate.getWidth() + "mm;"
                            + "    height: " + documentTemplate.getHeight() + "mm;"
                            + "     }}"
                            + "    </style> ");
                    sb.append("<table><colgroup> ");

                    System.out.println("margin " + documentTemplate.getMargin_top());
                    List<Integer> widthList = new ArrayList<>();
                    int id = 0;
                    int countWidths = 0;
                    int j = 0;
                    for (DataTableColumn dtc : logs.getItems()) {
                        if (dtc.isVisibility()) {
                            int width = (int) (dtc.getWidth() * 100 / (dto.getWidth() * 3.5));
                            widthList.add(id, width);
                            countWidths = countWidths + width;
                            id++;
                            if (width == 0) {
                                j++;
                            }
                        }
                    }
                    for (Integer i : widthList) {
                        if (i == 0) {
                            i = (100 - countWidths) / j;
                        }
                        sb.append("<col style=\"width:" + i + "%\" />");
                    }
                    sb.append("</colgroup>");

                    if (!dto.isLabel()) {
                        sb.append("<tr>");
                        for (DataTableColumn dtc : logs.getItems()) {
                            if (dtc.isVisibility()) {
                                sb.append("<th> ").append(sessionBean.getLoc().getString(dtc.getId())).append("</th>");
                            }
                        }
                    }
                    sb.append("</tr>");
                    //  for (int i = 0; i < rowCount - 1; i++) {
                    if (selectedObject.getType().getId() != 26) {
                        for (int i = 0; i < invoiceItemTabBean.getListOfObjects().size(); i++) {

                            sb.append("<tr>");
                            if (i < invoiceItemTabBean.getListOfObjects().size()) {
                                InvoiceItem inv = invoiceItemTabBean.getListOfObjects().get(i);

                                if (logs.getItems().get(0).isVisibility()) {

                                    sb.append("<td><div style = \"height : " + (inv.getStock().getName().length() * 2) + "px;\">").append((inv.getStock().getName()) == null ? "" : inv.getStock().getName()).append("</div></td>");
                                }
                                if (logs.getItems().get(1).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getCode()) == null ? "" : inv.getStock().getCode()).append("</td>");

                                }
                                if (logs.getItems().get(2).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getBarcode()) == null ? "" : inv.getStock().getBarcode()).append("</td>");

                                }
                                if (logs.getItems().get(3).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getDescription()) == null ? "" : inv.getStock().getDescription()).append("</td>");

                                }
                                if (logs.getItems().get(4).isVisibility()) {
                                    sb.append("<td>").append((inv.getQuantity()) == null ? "" : sessionBean.getNumberFormat().format(inv.getQuantity())).append(inv.getStock().getUnit().getSortName() == null ? "" : inv.getStock().getUnit().getSortName()).append("</td>");

                                }
                                if (logs.getItems().get(5).isVisibility()) {
                                    sb.append("<td>").append((inv.getUnitPrice()) == null ? "" : sessionBean.getNumberFormat().format(inv.getUnitPrice()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");

                                }
                                if (logs.getItems().get(6).isVisibility()) {
                                    sb.append("<td>").append((inv.getDiscountRate()) == null ? "" : sessionBean.getNumberFormat().format(inv.getDiscountRate())).append("%").append("</td>");
                                }
                                if (logs.getItems().get(7).isVisibility()) {
                                    sb.append("<td>").append((inv.getTaxRate()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTaxRate())).append("%").append("</td>");
                                }
                                try {
                                    if (logs.getItems().get(8).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalTax()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalTax()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                    if (logs.getItems().get(9).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalPrice()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                    if (logs.getItems().get(10).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalMoney()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                if (logs.getItems().get(0).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(1).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(2).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(3).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(4).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(5).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(6).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(7).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                try {
                                    if (logs.getItems().get(8).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                    if (logs.getItems().get(9).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                    if (logs.getItems().get(10).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                } catch (Exception e) {
                                }

                            }
                            sb.append("</tr>");

                        }
                    } else {
                        for (int i = 0; i < invoicePriceDifferenceStockTabBean.getListOfObjects().size(); i++) {

                            sb.append("<tr>");
                            if (i < invoicePriceDifferenceStockTabBean.getListOfObjects().size()) {
                                InvoiceItem inv = invoicePriceDifferenceStockTabBean.getListOfObjects().get(i);

                                if (logs.getItems().get(0).isVisibility()) {

                                    sb.append("<td><div style = \"height : " + (inv.getStock().getName().length() * 2) + "px;\">").append((inv.getStock().getName()) == null ? "" : inv.getStock().getName()).append("</div></td>");
                                }
                                if (logs.getItems().get(1).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getCode()) == null ? "" : inv.getStock().getCode()).append("</td>");

                                }
                                if (logs.getItems().get(2).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getBarcode()) == null ? "" : inv.getStock().getBarcode()).append("</td>");

                                }
                                if (logs.getItems().get(3).isVisibility()) {
                                    sb.append("<td>").append((inv.getStock().getDescription()) == null ? "" : inv.getStock().getDescription()).append("</td>");

                                }
                                if (logs.getItems().get(4).isVisibility()) {
                                    sb.append("<td>").append((inv.getQuantity()) == null ? "" : sessionBean.getNumberFormat().format(inv.getQuantity())).append(inv.getStock().getUnit().getSortName() == null ? "" : inv.getStock().getUnit().getSortName()).append("</td>");

                                }
                                if (logs.getItems().get(5).isVisibility()) {
                                    sb.append("<td>").append((inv.getOldUnitPrice()) == null ? "" : sessionBean.getNumberFormat().format(inv.getOldUnitPrice()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");

                                }
                                if (logs.getItems().get(6).isVisibility()) {
                                    sb.append("<td>").append((inv.getUnitPrice()) == null ? "" : sessionBean.getNumberFormat().format(inv.getUnitPrice()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                }
                                if (logs.getItems().get(7).isVisibility()) {
                                    sb.append("<td>").append((inv.getTaxRate()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTaxRate())).append("%").append("</td>");
                                }
                                try {
                                    if (logs.getItems().get(8).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalTax()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalTax()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                    if (logs.getItems().get(9).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalPrice()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                    if (logs.getItems().get(10).isVisibility()) {
                                        sb.append("<td>").append((inv.getTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(inv.getTotalMoney()) + " " + sessionBean.currencySignOrCode(inv.getCurrency().getId(), 0)).append("</td>");
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                if (logs.getItems().get(0).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(1).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(2).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(3).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(4).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(5).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(6).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                if (logs.getItems().get(7).isVisibility()) {
                                    sb.append("<td></td>");
                                }
                                try {
                                    if (logs.getItems().get(8).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                    if (logs.getItems().get(9).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                    if (logs.getItems().get(10).isVisibility()) {
                                        sb.append("<td></td>");
                                    }
                                } catch (Exception e) {
                                }

                            }
                            sb.append("</tr>");

                        }
                    }

                    sb.append("</table>");

                    RequestContext.getCurrentInstance().execute("$('#" + dto.getKeyWord() + "').append('" + sb + "')");
                } else {

                    OutputPanel op = new OutputPanel();
                    if (dto.getTop() < table_top) {
                        op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                    } else {
                        dto.setTop((dto.getTop() * 4) + table_height);
                        op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() + "px;left:" + dto.getLeft() * 4 + "px;");
                    }
                    OutputLabel label = new OutputLabel();
                    OutputLabel labelTitle = new OutputLabel();
                    labelTitle.setRendered(!dto.isLabel());
                    if (dto.getFontStyle().size() > 0) {
                        String style = "";
                        for (String s : dto.getFontStyle()) {
                            if (s.equals("italic")) {
                                style = style + "font-style:italic;";
                            } else {
                                style = style + "font-weight:700 !important;";
                            }
                        }
                        labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                        label.setStyle(style + "word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                    } else {
                        labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                        label.setStyle("word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");

                    }

                    if (!(dto.getKeyWord().contains("branchname") || /*dto.getKeyWord().contains("branchaddress") || dto.getKeyWord().contains("branchmail") || dto.getKeyWord().contains("branchtelephone")
                        || dto.getKeyWord().contains("branchtaxnumber") || dto.getKeyWord().contains("branchtaxoffice") ||*/ dto.getKeyWord().contains("grandtotalmoneywrite"))) {
                        labelTitle.setValue(dto.getName() + " : ");
                    }
                    if (dto.getKeyWord().trim().contains("textpnl")) {
                        labelTitle.setValue(dto.getName());
                    }

                    if (dto.getKeyWord().contains("customertitlepnl")) {

                        label.setValue(((selectedObject.getAccount().getTitle()) == null ? "" : selectedObject.getAccount().getTitle()));

                    } else if (dto.getKeyWord().contains("customeraddresspnl")) {

                        label.setValue(((selectedObject.getAccount().getAddress()) == null ? "" : selectedObject.getAccount().getAddress()));

                    } else if (dto.getKeyWord().contains("customerphonepnl")) {
                        label.setValue(((selectedObject.getAccount().getPhone()) == null ? "" : selectedObject.getAccount().getPhone()));
                    } else if (dto.getKeyWord().contains("customertaxofficepnl")) {

                        label.setValue(((selectedObject.getAccount().getTaxOffice()) == null ? "" : selectedObject.getAccount().getTaxOffice()));
                    } else if (dto.getKeyWord().contains("customertaxofficenumberpnl")) {
                        String a = (selectedObject.getAccount().getTaxOffice()) == null ? " " : selectedObject.getAccount().getTaxOffice();
                        a += "  " + (selectedObject.getAccount().getTaxNo()) == null ? " " : selectedObject.getAccount().getTaxNo();
                        label.setValue(a);
                    } else if (dto.getKeyWord().contains("customertaxnumberpnl")) {

                        label.setValue(((selectedObject.getAccount().getTaxNo()) == null ? "" : selectedObject.getAccount().getTaxNo()));

                    } else if (dto.getKeyWord().contains("customerbalancepnl")) {

                        label.setValue(((selectedObject.getAccount().getBalance()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getAccount().getBalance()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));

                    } else if (dto.getKeyWord().contains("invoicenopnl")) {
                        String a = ((selectedObject.getDocumentSerial()) == null ? "" : selectedObject.getDocumentSerial());
                        a += ((selectedObject.getDocumentNumber()) == null ? "" : selectedObject.getDocumentNumber());
                        label.setValue(((a) == null ? "" : a));

                    } else if (dto.getKeyWord().contains("dispatchdatepnl")) {

                        label.setValue(((selectedObject.getDispatchDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDispatchDate())));

                    } else if (dto.getKeyWord().contains("dispatchaddresspnl")) {

                        label.setValue(((selectedObject.getDispatchAddress()) == null ? "" : selectedObject.getDispatchAddress()));

                    } else if (dto.getKeyWord().contains("duedatepnl")) {

                        label.setValue(((selectedObject.getDueDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDueDate())));

                    } else if (dto.getKeyWord().contains("totalpricepnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            label.setValue(((selectedObject.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalPrice()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            BigDecimal totalprice = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalprice = totalprice.add(item.getTotalPrice());
                            }
                            label.setValue(((totalprice) == null ? "" : sessionBean.getNumberFormat().format(totalprice) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("totaldiscountpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            label.setValue(((selectedObject.getTotalDiscount()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalDiscount()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            label.setValue("");
                        }
                    } else if (dto.getKeyWord().contains("totaltaxpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            label.setValue(((selectedObject.getTotalTax()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalTax()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            BigDecimal totalTax = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalTax = totalTax.add(item.getTotalTax());
                            }

                            label.setValue(((totalTax) == null ? "" : sessionBean.getNumberFormat().format(totalTax) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("exchangeratepnl")) {
                        label.setValue(((selectedObject.getExchangeRate()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getExchangeRate())));
                    } else if (dto.getKeyWord().contains("totalmoneypnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            label.setValue(((selectedObject.getTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalMoney()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            label.setValue(((selectedObject.getPriceDifferenceTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getPriceDifferenceTotalMoney()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("invoicedatepnl")) {
                        label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("branchnamepnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getName()) == null ? "" : branchSettingForSelection.getBranch().getName()));
                    } else if (dto.getKeyWord().contains("branchaddresspnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getAddress()) == null ? "" : branchSettingForSelection.getBranch().getAddress()));
                    } else if (dto.getKeyWord().contains("branchmailpnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getMail()) == null ? "" : branchSettingForSelection.getBranch().getMail()));
                    } else if (dto.getKeyWord().contains("branchtelephonepnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getPhone()) == null ? "" : branchSettingForSelection.getBranch().getPhone()));
                    } else if (dto.getKeyWord().contains("branchtaxofficepnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getTaxOffice()) == null ? "" : branchSettingForSelection.getBranch().getTaxOffice()));
                    } else if (dto.getKeyWord().contains("branchtaxnumberpnl")) {
                        label.setValue(((branchSettingForSelection.getBranch().getTaxNo()) == null ? "" : branchSettingForSelection.getBranch().getTaxNo()));
                    } else if (dto.getKeyWord().contains("cashpnl")) {
                        label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("checkbillpnl")) {
                        label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("totalpricetaxpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            label.setValue(((selectedObject.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalPrice()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            BigDecimal totalprice = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalprice = totalprice.add(item.getTotalPrice());
                            }
                            label.setValue(((totalprice) == null ? "" : sessionBean.getNumberFormat().format(totalprice) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("grandtotalmoneywritepnl")) {
                        label.setValue(wordFromNumber);
                    } else if (dto.getKeyWord().contains("recipientpersonpnl")) {
                        label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("deliverypersonpnl")) {
                        label.setValue(((selectedObject.getDeliveryPerson()) == null ? "" : selectedObject.getDeliveryPerson()));
                    } else if (dto.getKeyWord().contains("signaturepnl")) {
                        label.setValue(((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    }

                    op.getChildren().add(labelTitle);
                    op.getChildren().add(label);
                    droppable.getChildren().add(op);
                    RequestContext.getCurrentInstance().update("printPanel");

                }
            }
        }

        RequestContext.getCurrentInstance().execute("printData();");

    }

    /**
     * Müşteri Mutabakatları sayfasından gelip faturalandır butonuna basıldığı
     * sırada çalışan fonksiyon
     *
     */
    public void createInvoice() {
        int result = 0;
        calculateDiscountForAgreementAndWaybill(listOfItemForAgreement);
        selectedObject.setDiscountPrice(oldDiscountPrice);
        selectedObject.setDiscountRate(oldDiscountRate);
        if (branchSettingForSelection.isIsEInvoice()) {
            if ((!selectedObject.isIsPurchase() && selectedObject.getType().getId() != 26 && selectedObject.getType().getId() != 27) || (selectedObject.isIsPurchase() && selectedObject.getType().getId() == 27)) {
                selectedObject.setIsEInvoice(true);
            } else {
                selectedObject.setIsEInvoice(false);
            }
        }

        result = invoiceService.createInvoiceForAgreement(selectedObject, listOfItemForAgreement, customerAgreements, credit);
        if (result > 0) {

            selectedObject.setId(result);
            selectedObject = invoiceService.findInvoice(selectedObject);
            List<Object> list = new ArrayList<>();
            for (Object object : (ArrayList) sessionBean.parameter) {
                list.add(object);
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof CustomerAgreements) {
                    list.remove(i);
                } else if (list.get(i) instanceof CreditReport) {
                    list.remove(i);
                    credit.setIsInvoice(true);
                    credit.setIsPaid(true);
                    credit.setRemainingMoney(BigDecimal.ZERO);
                    list.add(credit);
                } else if (list.get(i) instanceof Invoice) {
                    list.remove(i);
                    // list.add(selectedObject);
                }
            }
            list.add(selectedObject);

            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
        }
        sessionBean.createUpdateMessage(result);

    }

    public void calculateDiscountForAgreementAndWaybill(List<InvoiceItem> listOfInvItem) {
        BigDecimal invdiscountrate = BigDecimal.valueOf(0);
        BigDecimal uprice = BigDecimal.valueOf(0);
        if (!selectedObject.isIsDiscountRate()) {
            for (InvoiceItem i : listOfInvItem) {
                BigDecimal temp = BigDecimal.valueOf(0);

                if (i.getTaxRate() != null) {
                    temp = BigDecimal.valueOf(1).add(i.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
                    if (i.isIsTaxIncluded()) {
                        BigDecimal x = BigDecimal.ONE.add(i.getTaxRate().divide(new BigDecimal(100.0000), 16, RoundingMode.HALF_EVEN));
                        uprice = i.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN);
                    } else {
                        uprice = i.getUnitPrice();
                    }
                } else {
                    uprice = i.getUnitPrice();
                }

                if (temp.compareTo(BigDecimal.valueOf(0)) != 0) {
                    invdiscountrate = invdiscountrate.add(((uprice
                            .multiply(i.getQuantity())).subtract(i.getDiscountPrice())).multiply(i.getExchangeRate()));
                }

            }
            if (invdiscountrate.compareTo(BigDecimal.valueOf(0)) == 0) {
                invdiscountrate = BigDecimal.valueOf(0);
            } else if (invdiscountrate.compareTo(BigDecimal.valueOf(0)) != 0) {
                if (oldDiscountPrice != null) {
                    invdiscountrate = (BigDecimal.valueOf(100).multiply(oldDiscountPrice)).divide(invdiscountrate, 4, RoundingMode.HALF_EVEN);
                } else {
                    invdiscountrate = BigDecimal.valueOf(0);
                }
            }
        }

        for (InvoiceItem invoiceItem : listOfInvItem) {
            if (invoiceItem.isIsTaxIncluded()) {
                if (invoiceItem.getTaxRate() != null) {
                    BigDecimal x = BigDecimal.ONE.add(invoiceItem.getTaxRate().divide(new BigDecimal(100.0000), 16, RoundingMode.HALF_EVEN));
                    uprice = invoiceItem.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN);
                } else {
                    uprice = invoiceItem.getUnitPrice();
                }

            } else {
                uprice = invoiceItem.getUnitPrice();
            }
            BigDecimal tprice = BigDecimal.valueOf(0);
            if (selectedObject.isIsDiscountRate()) {
                tprice = ((uprice
                        .multiply(invoiceItem.getQuantity())).subtract(invoiceItem.getDiscountPrice())).multiply(BigDecimal.valueOf(1).subtract((oldDiscountRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
            } else if (!selectedObject.isIsDiscountRate()) {
                tprice = uprice.multiply(invoiceItem.getQuantity());
                tprice = (tprice.subtract(invoiceItem.getDiscountPrice())).multiply(BigDecimal.valueOf(1).subtract((invdiscountrate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
            } else {
                tprice = ((uprice
                        .multiply(invoiceItem.getQuantity())).subtract(invoiceItem.getDiscountPrice()));

            }
            if (oldDiscountPrice == null || oldDiscountPrice.compareTo(BigDecimal.valueOf(0)) != 1) {
                if (invoiceItem.getDiscountPrice().compareTo(BigDecimal.valueOf(0)) == 1) {
                    selectedObject.setTotalDiscount(BigDecimal.valueOf(0.1));
                }
            }
            invoiceItem.setTotalPrice(tprice);
            if (invoiceItem.getTaxRate() != null) {
                invoiceItem.setTotalTax(tprice.multiply((invoiceItem.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
                invoiceItem.setTotalMoney(tprice.multiply(BigDecimal.valueOf(1).add(invoiceItem.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
            } else {
                invoiceItem.setTotalTax(BigDecimal.ZERO);
                invoiceItem.setTotalMoney(tprice);
            }

        }
        if (oldDiscountPrice != null && oldDiscountPrice.compareTo(BigDecimal.valueOf(0)) == 1) {
            selectedObject.setTotalDiscount(BigDecimal.valueOf(0.1));
        }

    }

    /**
     * Faturaya Ait Excel Dosyasını Oluşturur.
     */
    public void createExcelFile() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        if (selectedObject.getType().getId() != 26) {
            InvoiceItemTabBean invoiceItemTabBean = (InvoiceItemTabBean) viewMap.get("invoiceItemTabBean");
            invoiceService.createExcelFile(selectedObject, invoiceItemTabBean.getListOfObjects(), invoiceItemTabBean.totalAmountText(),
                    invoiceItemTabBean.sumTotalPrice(), invoiceItemTabBean.getTotalDiscount(), invoiceItemTabBean.getTaxRates(), invoiceItemTabBean.getListOfTaxs(), branchSettingForSelection);

        } else {
            InvoicePriceDifferenceStockTabBean invoicePriceDifferenceStockTabBean = (InvoicePriceDifferenceStockTabBean) viewMap.get("invoicePriceDifferenceStockTabBean");
            invoiceService.createExcelFile(selectedObject, invoicePriceDifferenceStockTabBean.getListOfObjects(), invoicePriceDifferenceStockTabBean.totalAmountText(),
                    invoicePriceDifferenceStockTabBean.sumTotalPrice(), BigDecimal.valueOf(0), invoicePriceDifferenceStockTabBean.bringTaxrates(), invoicePriceDifferenceStockTabBean.getListOfTaxs(), branchSettingForSelection);

        }
    }

    public void createChangingInfo() {

        isChangeInfoDialog = true;
        selectedChangingInvoice = new Invoice();
        selectedChangingInvoice.setAccount(selectedObject.getAccount());
        selectedChangingInvoice.setDispatchAddress(selectedObject.getDispatchAddress());
        if (selectedObject.isIsPurchase()) {
            selectedChangingInvoice.setDocumentSerial(selectedObject.getDocumentSerial());
            selectedChangingInvoice.setDocumentNumber(selectedObject.getDocumentNumber());
        } else {
            selectedChangingInvoice.getdNumber().setId(selectedObject.getdNumber().getId());
            selectedChangingInvoice.getdNumber().setActualNumber(selectedObject.getdNumber().getActualNumber());
        }
        selectedChangingInvoice.setInvoiceDate(selectedObject.getInvoiceDate());
        selectedChangingInvoice.setDueDate(selectedObject.getDueDate());
        RequestContext.getCurrentInstance().update("pngAccountBook");
        RequestContext.getCurrentInstance().update("dlgChangeInvoiceInfo");
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeInvoiceInfo').show();");
    }

    public void resetChangingInfoDialog() {
        isChangeInfoDialog = false;
    }

    public void confirmSaveBefore() {
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeInvoiceInfo').hide();");
        RequestContext.getCurrentInstance().execute("saveChangingRecord();");
    }

    public void confirmSave() {
        isChangeInfoDialog = false;
        selectedObject.setAccount(selectedChangingInvoice.getAccount());
        selectedObject.setDispatchAddress(selectedChangingInvoice.getDispatchAddress());
        selectedObject.setTaxPayerTypeId(selectedChangingInvoice.getTaxPayerTypeId());
        selectedObject.setDueDate(selectedChangingInvoice.getDueDate());

        if (selectedObject.isIsPurchase()) {
            selectedObject.setDocumentSerial(selectedChangingInvoice.getDocumentSerial());
            selectedObject.setDocumentNumber(selectedChangingInvoice.getDocumentNumber());
        } else {
            selectedObject.getdNumber().setId(selectedChangingInvoice.getdNumber().getId());
            selectedObject.getdNumber().setActualNumber(selectedChangingInvoice.getdNumber().getActualNumber());
        }

        save();
    }

    public void changeDueDate() {
        if (isChangeInfoDialog) {
            if (selectedChangingInvoice.getAccount().getDueDay() != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(selectedChangingInvoice.getInvoiceDate());
                c.add(Calendar.DAY_OF_MONTH, selectedChangingInvoice.getAccount().getDueDay());
                selectedChangingInvoice.setDueDate(c.getTime());
                RequestContext.getCurrentInstance().update("frmChangeInvoiceInfo:cldChangeDueDate");

            }
        } else if (!isChangeInfoDialog) {
            if (selectedObject.getAccount().getDueDay() != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(selectedObject.getInvoiceDate());
                c.add(Calendar.DAY_OF_MONTH, selectedObject.getAccount().getDueDay());
                selectedObject.setDueDate(c.getTime());
                RequestContext.getCurrentInstance().update("frmInvoiceProcess:cldDueDate");

            }
        }
    }

    public void changeBranch() {

        selectedObject.getAccount().setId(0);
        selectedObject.getAccount().setName("");
        selectedObject.getAccount().setTitle("");
        for (BranchSetting b : listOfBranch) {
            if (b.getBranch().getId() == selectedObject.getBranchSetting().getBranch().getId()) {
                selectedObject.getBranchSetting().getBranch().setId(b.getBranch().getId());
                selectedObject.getBranchSetting().setIsCentralIntegration(b.isIsCentralIntegration());
                selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(b.isIsInvoiceStockSalePriceList());
                selectedObject.getBranchSetting().getBranch().getCurrency().setId(b.getBranch().getCurrency().getId());
                selectedObject.getBranchSetting().getBranch().setIsAgency(b.getBranch().isIsAgency());
                break;
            }
        }
        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//irsaliye için seri numarları çektik.
        setDocument();
        if (processType == 1) {
            listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " AND iw.status_id = 13 ");
        } else {
            listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " ");
        }

    }

    public void printFromDevice() {

        Printer printer = new Printer();
        printer = printerService.listOfPrinterAccordingToType(97, selectedObject.getBranchSetting().getBranch());
        if (printer.getId() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.getLoc().getString("pleaseyoudefineprinter"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            JsonObject jsonObject = new JsonObject();

            DocumentTemplate documentTemplate = documentTemplateService.bringInvoiceTemplate(62);
            Gson gson = new Gson();
            PrintDocumentTemplate jsonDocumentDemplate = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
            }.getType());
            if (documentTemplate != null) {
                jsonObject.addProperty("height", documentTemplate.getHeight());
                jsonObject.addProperty("width", documentTemplate.getWidth());
                JsonArray jArrayObj = new JsonArray();
                for (DataTableColumn dt : jsonDocumentDemplate.getItems()) {
                    JsonObject lv = new JsonObject();
                    lv.addProperty("id", dt.getId());
                    lv.addProperty("width", dt.getWidth());
                    lv.addProperty("visibility", dt.isVisibility());
                    lv.addProperty("index", dt.getIndex());

                    jArrayObj.add(lv);
                }
                jsonObject.add("items", jArrayObj);

                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                InvoiceItemTabBean invoiceItemTabBean = (InvoiceItemTabBean) viewMap.get("invoiceItemTabBean");
                InvoicePriceDifferenceStockTabBean invoicePriceDifferenceStockTabBean = (InvoicePriceDifferenceStockTabBean) viewMap.get("invoicePriceDifferenceStockTabBean");

                JsonArray jArrayItemValues = new JsonArray();

                NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
                DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
                decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
                decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
                decimalFormatSymbols.setCurrencySymbol("");
                ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbols);

                for (InvoiceItem item : selectedObject.getType().getId() == 26 ? invoicePriceDifferenceStockTabBean.getListOfObjects() : invoiceItemTabBean.getListOfObjects()) {
                    JsonObject itemValueJson = new JsonObject();

                    for (DataTableColumn dc : jsonDocumentDemplate.getItems()) {
                        if (dc.getId().contains("stockname")) {
                            itemValueJson.addProperty(dc.getId(), item.getStock().getName());
                        } else if (dc.getId().contains("stockcode")) {
                            itemValueJson.addProperty(dc.getId(), item.getStock().getCode());
                        } else if (dc.getId().contains("stockbarcode")) {
                            itemValueJson.addProperty(dc.getId(), item.getStock().getBarcode());
                        } else if (dc.getId().contains("description")) {
                            itemValueJson.addProperty(dc.getId(), item.getDescription());
                        } else if (dc.getId().contains("quantity")) {
                            formatterUnit.setMaximumFractionDigits(item.getUnit().getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(item.getUnit().getUnitRounding());

                            itemValueJson.addProperty(dc.getId(), formatterUnit.format(item.getQuantity()));
                        } else if (dc.getId().contains("unitprice")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getUnitPrice()));
                        } else if (dc.getId().contains("discountrate")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getDiscountRate()));
                        } else if (dc.getId().contains("taxrate")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getTaxRate()));
                        } else if (dc.getId().contains("taxprice")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getTotalTax()));
                        } else if (dc.getId().contains("taxfreeamount")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getTotalPrice()));
                        } else if (dc.getId().contains("totalprice")) {
                            itemValueJson.addProperty(dc.getId(), sessionBean.getNumberFormat().format(item.getTotalMoney()));
                        }

                    }
                    jArrayItemValues.add(itemValueJson);
                }
                jsonObject.add("itemValues", jArrayItemValues);//ItemValues

                //ListOfObjects
                JsonArray jArrayListOfObjects = new JsonArray();
                for (DocumentTemplateObject dt : jsonDocumentDemplate.getListOfObjects()) {
                    JsonObject lv = new JsonObject();
                    lv.addProperty("id", dt.getId());
                    lv.addProperty("fontSize", dt.getFontSize());
                    JsonArray j = new JsonArray();
                    for (int i = 0; i < dt.getFontStyle().size(); i++) {
                        j.add(dt.getFontStyle().get(i));
                    }
                    lv.add("fontStyle", j);
                    lv.addProperty("fontAlign", dt.getFontAlign());
                    lv.addProperty("left", dt.getLeft());
                    lv.addProperty("top", dt.getTop());
                    lv.addProperty("width", dt.getWidth());
                    lv.addProperty("height", dt.getHeight());
                    lv.addProperty("name", dt.getName());
                    lv.addProperty("keyWord", dt.getKeyWord());
                    lv.addProperty("label", dt.isLabel());

                    jArrayListOfObjects.add(lv);
                }
                jsonObject.add("listOfObjects", jArrayListOfObjects);

                JsonArray jArrayListOfObjectsValues = new JsonArray();
                for (DocumentTemplateObject dto : jsonDocumentDemplate.getListOfObjects()) {
                    JsonObject itemObj = new JsonObject();
                    itemObj.addProperty("key", dto.getKeyWord());
                    if (dto.getKeyWord().contains("branchname")) {
                        itemObj.addProperty("value", selectedObject.getBranchSetting().getBranch().getName());
                    } else if (dto.getKeyWord().contains("textpnl")) {
                        itemObj.addProperty("value", dto.getName());
                    } else if (dto.getKeyWord().contains("customertitlepnl")) {
                        itemObj.addProperty("value", selectedObject.getAccount().getTitle());
                    } else if (dto.getKeyWord().contains("customeraddresspnl")) {
                        itemObj.addProperty("value", selectedObject.getAccount().getAddress());
                    } else if (dto.getKeyWord().contains("customerphonepnl")) {
                        itemObj.addProperty("value", selectedObject.getAccount().getPhone());
                    } else if (dto.getKeyWord().contains("customertaxofficepnl")) {
                        itemObj.addProperty("value", selectedObject.getAccount().getTaxOffice());
                    } else if (dto.getKeyWord().contains("customertaxofficenumberpnl")) {
                        String toffice = (selectedObject.getAccount().getTaxOffice()) == null ? " " : selectedObject.getAccount().getTaxOffice();
                        toffice += "  " + (selectedObject.getAccount().getTaxNo()) == null ? " " : selectedObject.getAccount().getTaxNo();
                        itemObj.addProperty("value", toffice);
                    } else if (dto.getKeyWord().contains("customertaxnumberpnl")) {
                        itemObj.addProperty("value", selectedObject.getAccount().getTaxNo());
                    } else if (dto.getKeyWord().contains("customerbalancepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getAccount().getBalance()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getAccount().getBalance()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                    } else if (dto.getKeyWord().contains("invoicenopnl")) {
                        String a = ((selectedObject.getDocumentSerial()) == null ? "" : selectedObject.getDocumentSerial());
                        a += ((selectedObject.getDocumentNumber()) == null ? "" : selectedObject.getDocumentNumber());
                        itemObj.addProperty("value", a);
                    } else if (dto.getKeyWord().contains("dispatchdatepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getDispatchDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDispatchDate())));
                    } else if (dto.getKeyWord().contains("dispatchaddresspnl")) {
                        itemObj.addProperty("value", selectedObject.getDispatchAddress());
                    } else if (dto.getKeyWord().contains("duedatepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getDueDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDueDate())));
                    } else if (dto.getKeyWord().contains("totalpricepnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            itemObj.addProperty("value", ((selectedObject.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalPrice()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {

                            BigDecimal totalprice = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalprice = totalprice.add(item.getTotalPrice());
                            }

                            itemObj.addProperty("value", ((totalprice) == null ? "" : sessionBean.getNumberFormat().format(totalprice) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("totaldiscountpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            itemObj.addProperty("value", ((selectedObject.getTotalDiscount()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalDiscount()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            itemObj.addProperty("value", "");
                        }
                    } else if (dto.getKeyWord().contains("totaltaxpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            itemObj.addProperty("value", ((selectedObject.getTotalTax()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalTax()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            BigDecimal totalTax = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalTax = totalTax.add(item.getTotalTax());
                            }
                            itemObj.addProperty("value", ((totalTax) == null ? "" : sessionBean.getNumberFormat().format(totalTax) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("exchangeratepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getExchangeRate()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getExchangeRate())));
                    } else if (dto.getKeyWord().contains("totalmoneypnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            itemObj.addProperty("value", ((selectedObject.getTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalMoney()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {
                            itemObj.addProperty("value", ((selectedObject.getPriceDifferenceTotalMoney()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getPriceDifferenceTotalMoney()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("invoicedatepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("branchnamepnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getName());
                    } else if (dto.getKeyWord().contains("branchaddresspnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getAddress());
                    } else if (dto.getKeyWord().contains("branchmailpnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getMail());
                    } else if (dto.getKeyWord().contains("branchtelephonepnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getPhone());
                    } else if (dto.getKeyWord().contains("branchtaxofficepnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getTaxOffice());
                    } else if (dto.getKeyWord().contains("branchtaxnumberpnl")) {
                        itemObj.addProperty("value", branchSettingForSelection.getBranch().getTaxNo());
                    } else if (dto.getKeyWord().contains("cashpnl")) {
                        itemObj.addProperty("value", ((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("checkbillpnl")) {
                        itemObj.addProperty("value", ((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("totalpricetaxpnl")) {
                        if (selectedObject.getType().getId() != 26) {
                            itemObj.addProperty("value", ((selectedObject.getTotalPrice()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getTotalPrice()) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        } else {

                            BigDecimal totalprice = BigDecimal.ZERO;
                            for (InvoiceItem item : invoicePriceDifferenceStockTabBean.getListOfObjects()) {
                                totalprice = totalprice.add(item.getTotalPrice());
                            }

                            itemObj.addProperty("value", ((totalprice) == null ? "" : sessionBean.getNumberFormat().format(totalprice) + sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0)));
                        }
                    } else if (dto.getKeyWord().contains("grandtotalmoneywritepnl")) {
                        itemObj.addProperty("value", wordFromNumber);
                    } else if (dto.getKeyWord().contains("recipientpersonpnl")) {
                        itemObj.addProperty("value", ((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("deliverypersonpnl")) {
                        itemObj.addProperty("value", selectedObject.getDeliveryPerson());
                    } else if (dto.getKeyWord().contains("signaturepnl")) {
                        itemObj.addProperty("value", ((selectedObject.getInvoiceDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getInvoiceDate())));
                    } else if (dto.getKeyWord().contains("imagepnl")) {
                        itemObj.addProperty("value", documentTemplate.getId() + "_" + dto.getKeyWord() + ".png");
                    }

                    jArrayListOfObjectsValues.add(itemObj);

                }

                jsonObject.add("listOfValues", jArrayListOfObjectsValues);

            }

            // System.out.println("-----------json" + jsonObject.toString());
            String result = printerService.sendPrinterDevice(jsonObject.toString(), printer, 1);
            FacesMessage message;
            if (result.equals(sessionBean.getLoc().getString("succesfuloperation"))) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), result);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), result);
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }
//Daha önce SAP'ye gönderilmiş satış faturası üzerinde güncelleme yapılacağında Sap'ye ters kayıt gönderir.

    public void sendSapReverse() {

        if (invoiceService.sendSapReverse(selectedObject)) {
            selectedObject.setSapLogIsSend(false);
            RequestContext.getCurrentInstance().update("frmInvoiceProcess");
            RequestContext.getCurrentInstance().update("tbvInvoice");
            RequestContext.getCurrentInstance().update("dlgStockProcess");
            RequestContext.getCurrentInstance().update("dlgPaymentProcess");
            invoiceService.insertHistory(selectedObject);
            sessionBean.createUpdateMessage(1);

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("reverserecordcouldnotbecreated") + sessionBean.loc.getString("tryagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    //Sap ye başarılı olarak gönderilmiş bir satınalma faturası güncellenmek istendiğinde çalışır.Log tablosunu günceller.
    public void openUpdate() {
        int result = 0;
        result = invoiceService.updateLogSap(selectedObject);

        if (result > 0) {

            selectedObject.setSapLogIsSend(false);
            selectedObject.setSapIsSendWaybill(false);

            RequestContext.getCurrentInstance().update("frmInvoiceProcess");

            RequestContext.getCurrentInstance().update("tbvInvoice");
            RequestContext.getCurrentInstance().update("dlgStockProcess");
            RequestContext.getCurrentInstance().update("dlgPaymentProcess");

            invoiceService.insertHistory(selectedObject);

            sessionBean.createUpdateMessage(result);

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("couldnotopentoeditinvoice") + sessionBean.loc.getString("tryagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void changeFuelInvoice() {

        if (selectedObject.isIsFuel()) {
            selectedObject.getListOfWarehouse().clear();
        }

    }
}
