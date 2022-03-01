/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 29.01.2018 13:24:54
 */
package com.mepsan.marwiz.finance.waybill.presentation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillItemService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.documenttemplate.business.DocumentTemplateService;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class WaybillProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{waybillService}")
    private IWaybillService waybillService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{waybillItemService}")
    public IWaybillItemService waybillItemService;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{documentTemplateService}")
    public DocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private int processType;
    private List<Warehouse> listWarehouses;
    private Waybill selectedObject;
    private int activeIndex;
    boolean isEpdk;
    private List<Status> listOfStatus;
    private List<DocumentNumber> listOfDocumentNumber;
    private CheckDelete checkDelete;
    private String deleteControlMessage = "";
    private String deleteControlMessage1 = "";
    private String deleteControlMessage2 = "";
    private String relatedRecord = "";
    private Waybill selectedChangingWaybill;
    private boolean isChangeInfoDialog;
    private List<BranchSetting> listOfBranch;
    private BranchSetting branchSettingForSelection;

    private Order order;
    private List<WaybillItem> listOfItemForOrder;//siparişten irsaliye oluşturulurken tutulur.
    private boolean isCreateWaybillFromOrder;
    private boolean isCreateWaybill;//irsaliye oluşturulabilir mi? ürünlerinin tüm alanları dolduruldumu bilgisini tutar.

    private boolean isPurchaseMinStockLevel;
    private boolean isSalesMaxStockLevel;
    private boolean isThere;

    public CheckDelete getCheckDelete() {
        return checkDelete;
    }

    public void setCheckDelete(CheckDelete checkDelete) {
        this.checkDelete = checkDelete;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
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

    public List<Warehouse> getListWarehouses() {
        return listWarehouses;
    }

    public List<Status> getListOfStatus() {
        return listOfStatus;
    }

    public void setListOfStatus(List<Status> listOfStatus) {
        this.listOfStatus = listOfStatus;
    }

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }

    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }

    public void setListWarehouses(List<Warehouse> listWarehouses) {
        this.listWarehouses = listWarehouses;
    }

    public void setWaybillItemService(IWaybillItemService waybillItemService) {
        this.waybillItemService = waybillItemService;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public boolean isIsEpdk() {
        return isEpdk;
    }

    public void setIsEpdk(boolean isEpdk) {
        this.isEpdk = isEpdk;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Waybill getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Waybill selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public Waybill getSelectedChangingWaybill() {
        return selectedChangingWaybill;
    }

    public void setSelectedChangingWaybill(Waybill selectedChangingWaybill) {
        this.selectedChangingWaybill = selectedChangingWaybill;
    }

    public boolean isIsChangeInfoDialog() {
        return isChangeInfoDialog;
    }

    public void setIsChangeInfoDialog(boolean isChangeInfoDialog) {
        this.isChangeInfoDialog = isChangeInfoDialog;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<WaybillItem> getListOfItemForOrder() {
        return listOfItemForOrder;
    }

    public void setListOfItemForOrder(List<WaybillItem> listOfItemForOrder) {
        this.listOfItemForOrder = listOfItemForOrder;
    }

    public boolean isIsCreateWaybillFromOrder() {
        return isCreateWaybillFromOrder;
    }

    public void setIsCreateWaybillFromOrder(boolean isCreateWaybillFromOrder) {
        this.isCreateWaybillFromOrder = isCreateWaybillFromOrder;
    }

    public boolean isIsCreateWaybill() {
        return isCreateWaybill;
    }

    public void setIsCreateWaybill(boolean isCreateWaybill) {
        this.isCreateWaybill = isCreateWaybill;
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

    public boolean isIsThere() {
        return isThere;
    }

    public void setIsThere(boolean isThere) {
        this.isThere = isThere;
    }

    @PostConstruct
    public void init() {
        System.out.println("-------------WaybillProcessBean");

        listOfStatus = sessionBean.getStatus(16);
        listOfBranch = new ArrayList<>();
        branchSettingForSelection = new BranchSetting();
        listOfItemForOrder = new ArrayList<>();

        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Waybill) {
                    isCreateWaybillFromOrder = false;
                    selectedObject = (Waybill) ((ArrayList) sessionBean.parameter).get(i);
//                    selectedObject.getType().setId(21);
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {//siparişten fatura oluşturulacak ise sipariş gelir.
                    order = (Order) ((ArrayList) sessionBean.parameter).get(i);

                    System.out.println("order accoutn" + order.getAccount().getName());

                    isCreateWaybillFromOrder = true;
                    selectedObject = new Waybill();
                    selectedObject.setAccount(order.getAccount());
                    selectedObject.setDescription("");
                    selectedObject.setDispatchAddress("");
                    selectedObject.setDispatchDate(new Date());
                    selectedObject.setWaybillDate(new Date());
                    selectedObject.setDocumentNumber(order.getDocumentNumber());
                    selectedObject.setIsPurchase(true);
                    selectedObject.getStatus().setId(43);//açık
                    selectedObject.getType().setId(21);
                    selectedObject.getBranchSetting().getBranch().setId(order.getBranchSetting().getBranch().getId());
                    selectedObject.getBranchSetting().setIsCentralIntegration(order.getBranchSetting().isIsCentralIntegration());
                    selectedObject.getBranchSetting().setIsInvoiceStockSalePriceList(order.getBranchSetting().isIsInvoiceStockSalePriceList());
                    selectedObject.getBranchSetting().getBranch().getCurrency().setId(order.getBranchSetting().getBranch().getCurrency().getId());
                    selectedObject.getBranchSetting().getBranch().setIsAgency(order.getBranchSetting().getBranch().isIsAgency());
                    branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
                    listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(17), selectedObject.getBranchSetting().getBranch());//fatura için seri numarları çektik.
                    setDocument();
                    listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " AND iw.status_id = 13 ");
                    processType = 2;

                }
            }
        }

        if (selectedObject.getId() > 0 || isCreateWaybillFromOrder) {
            if (selectedObject.getId() > 0) {
                listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " ");
            }
            listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(16), selectedObject.getBranchSetting().getBranch());//irsaliye için seri numarları çektik.
            setDocument();
            processType = 2;
            selectedObject.getListOfWarehouse().clear();
            if (selectedObject.getWarehouseIdList() != null && !selectedObject.getWarehouseIdList().equals("")) {
                String[] parts = selectedObject.getWarehouseIdList().split(",");
                for (int i = 0; i < parts.length; i++) {
                    for (Warehouse w : listWarehouses) {
                        if (w.getId() == Integer.valueOf(parts[i]) && !selectedObject.getListOfWarehouse().contains(w)) {
                            selectedObject.getListOfWarehouse().add(w);
                        }
                    }
                }
            }
            branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());
        } else {
            processType = 1;
            for (BranchSetting b : listOfBranch) {
                if (b.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedObject.getBranchSetting().getBranch().setId(b.getBranch().getId());
                    break;
                }
            }
            changeBranch();

            selectedObject.getType().setId(21);
            int index = 0;
            for (int j = 0; j < listOfStatus.size(); j++) {
                if (listOfStatus.get(j).getId() == 27) {//iptal durumu ise çıkar
                    index = j;
                }
            }
            listOfStatus.remove(index);
            selectedObject.setWaybillDate(new Date());
            selectedObject.setDispatchDate(new Date());
            if (!listWarehouses.isEmpty()) {
                selectedObject.setWarehouse(listWarehouses.get(0));
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{12, 13, 14, 2, 345}, 0));//Faturalaştır için faturanın save button_idsi kullanıldı.
        setListTab(sessionBean.checkAuthority(new int[]{5, 6}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

        System.out.println("isCreateWaybillFromOrder" + isCreateWaybillFromOrder);

    }

    public void save() {

        if ((!selectedObject.isIsPurchase() && selectedObject.getdNumber().getActualNumber() <= selectedObject.getdNumber().getEndNumber() && selectedObject.getdNumber().getActualNumber() >= selectedObject.getdNumber().getBeginNumber()) || selectedObject.isIsPurchase()) {

            if (isChangeInfoDialog) {///Cari değiştime için kaydetme
                RequestContext.getCurrentInstance().execute("PF('dlg_WaybillConfirmChangeInfo').show();");

            } else if (sessionBean.isPeriodClosed(selectedObject.getWaybillDate())) {

                boolean isSave = true;

                if (sessionBean.getUser().getLastBranch().isIsAgency() && selectedObject.isIsPurchase()) {

                    if (selectedObject.isIsPurchase()) {
                        if (selectedObject.getDocumentNumber().length() > 16) {
                            isSave = false;
                        }
                    } else {
                        if (String.valueOf(selectedObject.getdNumber().getActualNumber()).length() + selectedObject.getdNumber().getSerial().length() > 16) {
                            isSave = false;
                        }
                    }
                }

                if (!isSave) {

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("documentnumbercannotexceed16characters")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else {

                    int result = 0;
                    if (isCreateWaybillFromOrder) {
                        if (isCreateWaybill) {
                            String orderIds = "";
                            for (WaybillItem waybillItem : listOfItemForOrder) {
                                if (waybillItem.getOrderIds() != null && !waybillItem.getOrderIds().equals("")) {
                                    orderIds = orderIds + waybillItem.getOrderIds() + ",";
                                }

                            }
                            orderIds = orderIds.substring(0, orderIds.length() - 1);
                            System.out.println("orderIds" + orderIds);
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
                            System.out.println("orderIds" + orderIds);
                            selectedObject.setOrderIds(orderIds);
                            selectedObject.getListOfWarehouse().clear();
                            selectedObject.getListOfWarehouse().add(selectedObject.getWarehouse());
                            result = waybillService.createWaybillForOrder(selectedObject, listOfItemForOrder);
                            selectedObject.setId(result);
                            if (result > 0) {

                                for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {//siparişten fatura oluşturulacak ise siparişten objesi gelir.
                                        ((Order) ((ArrayList) sessionBean.parameter).get(i)).getStatus().setId(60);//kapattık  
                                    }
                                }
                                marwiz.goToPage("/pages/finance/order/order.xhtml", sessionBean.parameter, 1, 228);//sipariş sayfasına geri döndük
                            }
                        } else {
                            if (selectedObject.getId() == 0) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("wefillinallthefieldsinstocks")));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");

                            }
                        }
                    } else {

                        if (selectedObject.getdNumber().getId() > 0) {
                            selectedObject.setDocumentNumber("" + selectedObject.getdNumber().getActualNumber());
                        }
                        if (processType == 1) {//create
                            selectedObject.getListOfWarehouse().clear();
                            selectedObject.getListOfWarehouse().add(selectedObject.getWarehouse());
                            result = waybillService.create(selectedObject);
                            if (result > 0) {
                                if (selectedObject.getdNumber().getId() > 0) {
                                    selectedObject.setDocumentNumber("" + selectedObject.getdNumber().getActualNumber());
                                }

                                selectedObject.setId(result);

                                if (!selectedObject.isIsFuel()) {
                                    selectedObject.setWarehouseIdList(Integer.toString(selectedObject.getWarehouse().getId()));
                                    for (Warehouse listWarehouse : listWarehouses) {
                                        selectedObject.setWarehouseNameList(listWarehouse.getName());
                                        break;
                                    }
                                }

                                ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                                List<Object> list = new ArrayList<>();
                                list.addAll((ArrayList) sessionBean.parameter);
                                list.add(selectedObject);
                                selectedObject.setIsInvoice(false);
                                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 1, 41);

                            }

                        } else if (processType == 2) {
                            result = waybillService.update(selectedObject);
                            if (result > 0) {
                                ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                                List<Object> list = new ArrayList<>();
                                list.addAll((ArrayList) sessionBean.parameter);
                                marwiz.goToPage("/pages/finance/waybill/waybill.xhtml", list, 1, 25);
                            }
                        }
                        sessionBean.createUpdateMessage(result);
                    }

                }
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesequencenumberenteredmustbeavaluebetweenthestartnumberandtheendnumberpleaseenterasequencenumberinthisrange")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }
    //cari seçildiğinde calısır

    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null) {
            if (isChangeInfoDialog) {
                selectedChangingWaybill.setAccount(accountBookFilterBean.getSelectedData());
                if (!selectedObject.isIsPurchase()) {
                    selectedChangingWaybill.setDispatchAddress(selectedChangingWaybill.getAccount().getAddress());
                }
                accountBookFilterBean.setSelectedData(null);

                RequestContext.getCurrentInstance().update("frmChangeWaybillInfo:pgrChangeWaybillInfo");
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
                selectedObject.setDispatchAddress(selectedObject.isIsPurchase() ? "" : accountBookFilterBean.getSelectedData().getAddress());
                accountBookFilterBean.setSelectedData(null);

                RequestContext.getCurrentInstance().update("frmWaybillProcess");
            }
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    /**
     * İrsaliyeyi faturalaştırma butonu
     */
    public void createInvoice() {

        List<WaybillItem> listItem = waybillItemService.listWaybillItemOpenStock(selectedObject);

        if (!listItem.isEmpty()) {//Fatura oluşturabilir ise
            List<Object> list = new ArrayList<>();
            JsonArray jsonArray = new JsonArray();

            for (WaybillItem item : listItem) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", item.getId());
                jsonObject.addProperty("stock_id", item.getStock().getId());
                jsonObject.addProperty("stock_name", item.getStock().getName());
                jsonObject.addProperty("stock_barcode", item.getStock().getBarcode());
                jsonObject.addProperty("unit_id", item.getStock().getUnit().getId());
                jsonObject.addProperty("unit_sortname", item.getStock().getUnit().getSortName());
                jsonObject.addProperty("unit_name", item.getStock().getUnit().getName());
                jsonObject.addProperty("unit_rounding", item.getStock().getUnit().getUnitRounding());
                jsonObject.addProperty("quantity", item.getQuantity());
                jsonObject.addProperty("remainingquantity", item.getRemainingQuantity());
                jsonObject.addProperty("description", item.getDescription() == null ? "" : item.getDescription());
                jsonObject.addProperty("currenctsaleprice", item.getStock().getStockInfo().getCurrentSalePrice());
                jsonObject.addProperty("currenctsalecurrency", item.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                jsonObject.addProperty("taxrate", item.getTaxGroup().getRate());
                jsonObject.addProperty("unit_rounding", item.getStock().getUnit().getUnitRounding());
                if (selectedObject.isIsPurchase() && selectedObject.getBranchSetting().getBranch().isIsAgency()) {
                    jsonObject.addProperty("pricelistprice", item.getStock().getStockInfo().getPurchaseRecommendedPrice() == null ? BigDecimal.valueOf(0) : item.getStock().getStockInfo().getPurchaseRecommendedPrice());
                    if (item.getStock().getStockInfo().getPurchaseCurrency().getId() == 0) {
                        jsonObject.addProperty("pricelistcurrency", selectedObject.getBranchSetting().getBranch().getCurrency().getId());
                    } else {
                        jsonObject.addProperty("pricelistcurrency", item.getStock().getStockInfo().getPurchaseCurrency().getId());
                    }
                    jsonObject.addProperty("pricelistcurrencyname", item.getStock().getStockInfo().getPurchaseCurrency().getId() == 0 ? "" : item.getStock().getStockInfo().getPurchaseCurrency().getTag());
                    jsonObject.addProperty("pricelisttaxincluded", true);

                } else {
                    jsonObject.addProperty("pricelistprice", item.getPriceListItem().getPrice() == null ? BigDecimal.valueOf(0) : item.getPriceListItem().getPrice());
                    jsonObject.addProperty("pricelistcurrency", item.getPriceListItem().getCurrency().getId() == 0 ? selectedObject.getBranchSetting().getBranch().getCurrency().getId() : item.getPriceListItem().getCurrency().getId());
                    jsonObject.addProperty("pricelistcurrencyname", item.getPriceListItem().getCurrency().getId() == 0 ? "" : item.getPriceListItem().getCurrency().getTag());
                    jsonObject.addProperty("pricelisttaxincluded", item.getPriceListItem().isIs_taxIncluded());
                }

                if (selectedObject.isIsFuel()) {

                    jsonObject.addProperty("warehouse_id", item.getWarehouse().getId());
                    jsonObject.addProperty("warehouse_name", item.getWarehouse().getName());
                }

                jsonArray.add(jsonObject);
            }
            list.add(selectedObject);
            list.add(jsonArray);
            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
        } else {//Fatura için ürün yok uyarı ver
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("alltheproductsinthiswaybillhavealreadybeentransferredtotheinvoice"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getdNumber().getId()) {
                selectedObject.getdNumber().setActualNumber(dn.getActualNumber());
                selectedObject.setDocumentSerial(dn.getSerial());
                selectedObject.getdNumber().setBeginNumber(dn.getBeginNumber());
                selectedObject.getdNumber().setEndNumber(dn.getEndNumber());
            }
        }
    }

    public void setDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getdNumber().getId()) {
                selectedObject.getdNumber().setBeginNumber(dn.getBeginNumber());
                selectedObject.getdNumber().setEndNumber(dn.getEndNumber());
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
                isThere = true;
            } else {
                isPurchaseMinStockLevel = false;
            }
        } else {
            isPurchaseMinStockLevel = false;
        }

    }

    //Satış faturasında stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde satış faturasıının silinmesi engellemek için çalışır.
    public void salesMaxStockLevelControl(WaybillItem obj) {

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
        WaybillItemTabBean waybillItemTabBean = (WaybillItemTabBean) viewMap.get("waybillItemTabBean");
        for (WaybillItem item : waybillItemTabBean.getListOfObjects()) {
            if (((selectedObject.isIsPurchase() && selectedObject.getType().getId() != 22) || (selectedObject.getType().getId() == 22 && !selectedObject.isIsPurchase())) && (!item.getStock().getStockInfo().isIsMinusStockLevel())) { // Stok kartında ürün eksiye düşebilir mi seçili değilse
                stockPurchaseLevelControl(item);
                if (isPurchaseMinStockLevel) {
                    break;
                }
            }

            if (((!selectedObject.isIsPurchase() && selectedObject.getType().getId() != 22) || (selectedObject.isIsPurchase() && selectedObject.getType().getId() == 22)) && (item.getStock().getStockInfo().getMaxStockLevel() != null)) {
                salesMaxStockLevelControl(item);
                if (isSalesMaxStockLevel) {
                    break;
                }
            }
        }
        if (!isThere) {
            if (sessionBean.isPeriodClosed(selectedObject.getWaybillDate())) {

                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";

                checkDelete = waybillService.testBeforeDelete(selectedObject);

                if (checkDelete != null) {
                    switch (checkDelete.getR_response()) {
                        case -101://faturaya bağlı ise silme uyarı ver
                            deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtoinvoice");
                            deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeletetheinvoiceitem");
                            deleteControlMessage2 = sessionBean.getLoc().getString("documentnumber");
                            relatedRecord = checkDelete.getR_recordno();
                            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoWaybill");
                            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoWaybill').show();");
                            break;
                        default:
                            //Sil
                            checkDelete.setR_response(1);
                            deleteControlMessage = sessionBean.getLoc().getString("waybilldelete");
                            deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                            deleteControlMessage2 = "";
                            relatedRecord = "";
                            RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoWaybill");
                            RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoWaybill').show();");
                            break;
                    }
                }
            }
        }
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfoWaybill').hide();");
        context.execute("goToRelatedRecordWaybill();");

    }

    public void goToRelatedRecord() {

        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        switch (checkDelete.getR_response()) {
            case -101:
                Invoice invoice = new Invoice();
                invoice.setId(checkDelete.getR_record_id());
                invoice = invoiceService.findInvoice(invoice);
                list.add(invoice);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
                break;
            default:
                break;
        }
    }

    public void delete() {
        int result = 0;
        result = waybillService.delete(selectedObject);
        if (result > 0) {
            List<Object> list = new ArrayList<>();
            list.addAll((ArrayList) sessionBean.parameter);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Waybill) {
                    list.remove(list.get(i));
                }
            }
            marwiz.goToPage("/pages/finance/waybill/waybill.xhtml", list, 1, 25);
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_waybillitem').hide();");
        sessionBean.createUpdateMessage(result);
    }

    /**
     * İrsaliyeye Ait Excel Dosyasını Oluşturur.
     */
    public void createExcelFile() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        WaybillItemTabBean waybillItemTabBean = (WaybillItemTabBean) viewMap.get("waybillItemTabBean");
        waybillService.createExcelFile(selectedObject, waybillItemTabBean.getListOfObjects());
    }

    public void loadJson() {
        DocumentTemplate documentTemplate = documentTemplateService.bringInvoiceTemplate(63);
        Gson gson = new Gson();
        PrintDocumentTemplate logs = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
        }.getType());

        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("printPanel");
        droppable.getChildren().clear();

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        WaybillItemTabBean waybillItemTabBean = (WaybillItemTabBean) viewMap.get("waybillItemTabBean");

        double table_height_name = 0;
        double table_height_description = 0;
        double table_height = 0;
        double table_top = 0;
        boolean isStockName = false;

        for (WaybillItem i : waybillItemTabBean.getListOfObjects()) {
            table_height_name = table_height_name + (i.getStock().getName().length() * 2) + 8;
            table_height_description = table_height_description + ((i.getDescription() != null ? i.getDescription().length() : 0) * 2) + 8;
            if (table_height_name > table_height_description) {
                table_height = table_height_name;
                isStockName = true;
            } else {
                table_height = table_height_description;
                isStockName = false;
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
            } else if (dto.getKeyWord().equals("itemspnl")) {
                OutputPanel op = new OutputPanel();
                op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                op.setId(dto.getKeyWord());

                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");
                String direction = documentTemplate.isIsVertical() == true ? "horizontal" : "landscape";

                StringBuilder sb = new StringBuilder();
                sb.append(
                          " <style>"
                          + "        #itemspnl table {"
                          + "            font-family: arial, sans-serif;"
                          + "            border-collapse: collapse;"
                          + "            width: 100%;"
                          + "       table-layout: fixed;"
                          + "        }"
                          + "        #itemspnl table tr td, #itemspnl table tr th {"
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
                List<Integer> widthList = new ArrayList<>();
                int id = 0;
                int countWidths = 0;
                int j = 0;
                for (DataTableColumn dtc : logs.getItems()) {
                    if (dtc.isVisibility()) {
                        int width = (int) (dtc.getWidth() * 100 / (dto.getWidth() * 4));
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

                sb.append("<tr>");
                for (DataTableColumn dtc : logs.getItems()) {
                    if (dtc.isVisibility()) {
                        sb.append("<th> ").append(sessionBean.getLoc().getString(dtc.getId())).append("</th>");
                    }
                }
                sb.append("</tr>");

                int rowCount = (int) (dto.getHeight() / (33 * 0.25));

                NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

                formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
                DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
                decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
                decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
                decimalFormatSymbolsUnit.setCurrencySymbol("");
                ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

                for (int i = 0; i < waybillItemTabBean.getListOfObjects().size(); i++) {
                    sb.append("<tr>");
                    if (i < waybillItemTabBean.getListOfObjects().size()) {
                        WaybillItem witem = waybillItemTabBean.getListOfObjects().get(i);

                        formatterUnit.setMaximumFractionDigits(witem.getStock().getUnit().getUnitRounding());
                        formatterUnit.setMinimumFractionDigits(witem.getStock().getUnit().getUnitRounding());

                        if (logs.getItems().get(0).isVisibility()) {
                            sb.append("<td><div style = \"height : " + (isStockName ? (witem.getStock().getName().length() * 2) : (witem.getStock().getDescription().length() * 2)) + "px;\">").append((witem.getStock().getName()) == null ? "" : witem.getStock().getName()).append("</div></td>");
                        }
                        if (logs.getItems().get(1).isVisibility()) {
                            sb.append("<td></td>");
                        }
                        if (logs.getItems().get(2).isVisibility()) {
                            sb.append("<td><div style = \"height : " + (isStockName ? (witem.getStock().getName().length() * 2) : (witem.getStock().getDescription().length() * 2)) + "px;\">").append((witem.getDescription()) == null ? "" : witem.getDescription()).append("</div></td>");
                        }
                        if (logs.getItems().get(3).isVisibility()) {
                            sb.append("<td>").append((witem.getQuantity()) == null ? "" : formatterUnit.format(witem.getQuantity())).append("</td>");
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
                    }
                    sb.append("</tr>");

                }
                sb.append("</table>");

                RequestContext.getCurrentInstance().execute("$('#" + dto.getKeyWord() + "').append('" + sb + "')");
            } else {
                OutputPanel op = new OutputPanel();

                if (dto.getTop() < table_top) {
                    op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                } else {
                    dto.setTop(dto.getTop() * 4 + table_height);
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
                    String a = (selectedObject.getAccount().getTaxOffice() == null ? " " : selectedObject.getAccount().getTaxOffice());
                    a += "  " + (selectedObject.getAccount().getTaxNo() == null ? " " : selectedObject.getAccount().getTaxNo());
                    label.setValue(a);
                } else if (dto.getKeyWord().contains("customertaxnumberpnl")) {

                    label.setValue(((selectedObject.getAccount().getTaxNo()) == null ? "" : selectedObject.getAccount().getTaxNo()));

                } else if (dto.getKeyWord().contains("customerbalancepnl")) {

                    label.setValue(((selectedObject.getAccount().getBalance()) == null ? "" : sessionBean.getNumberFormat().format(selectedObject.getAccount().getBalance()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)));

                } else if (dto.getKeyWord().contains("invoicenopnl")) {
                    String a = ((selectedObject.getDocumentSerial()) == null ? "" : selectedObject.getDocumentSerial());
                    a += ((selectedObject.getDocumentNumber()) == null ? "" : selectedObject.getDocumentNumber());
                    label.setValue(((a) == null ? "" : a));
                } else if (dto.getKeyWord().contains("dispatchdatepnl")) {
                    label.setValue(((selectedObject.getDispatchDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDispatchDate())));
                } else if (dto.getKeyWord().contains("dispatchaddresspnl")) {
                    label.setValue(((selectedObject.getDispatchAddress()) == null ? "" : selectedObject.getDispatchAddress()));
                } else if (dto.getKeyWord().contains("invoicedatepnl")) {
                    label.setValue(((selectedObject.getWaybillDate()) == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getWaybillDate())));
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
                } else if (dto.getKeyWord().contains("totalpricetaxpnl")) {
                } else if (dto.getKeyWord().contains("totalmoneypnl")) {
                } else if (dto.getKeyWord().contains("totaltaxpnl")) {
                } else if (dto.getKeyWord().contains("deliverypersonpnl")) {
                }
                if (selectedObject.isIsPurchase()) {
                    if (dto.getKeyWord().contains("recipientpersonpnl")) {
                        label.setValue(((selectedObject.getDeliveryPerson()) == null ? "" : selectedObject.getDeliveryPerson()));
                    }
                } else if (dto.getKeyWord().contains("deliverypersonpnl")) {
                    label.setValue(((selectedObject.getDeliveryPerson()) == null ? "" : selectedObject.getDeliveryPerson()));
                }
                op.getChildren().add(labelTitle);
                op.getChildren().add(label);
                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");
            }
        }

        RequestContext.getCurrentInstance().execute("printData();");

    }

    public void createChangingInfo() {

        isChangeInfoDialog = true;
        selectedChangingWaybill = new Waybill();
        selectedChangingWaybill.setAccount(selectedObject.getAccount());
        selectedChangingWaybill.setDispatchAddress(selectedObject.getDispatchAddress());
        if (selectedObject.isIsPurchase()) {
            selectedChangingWaybill.setDocumentNumber(selectedObject.getDocumentNumber());
        } else {
            selectedChangingWaybill.getdNumber().setId(selectedObject.getdNumber().getId());
            selectedChangingWaybill.getdNumber().setActualNumber(selectedObject.getdNumber().getActualNumber());
        }
        RequestContext.getCurrentInstance().update("pngWaybillAccountBook");
        RequestContext.getCurrentInstance().update("dlgChangeWaybillInfo");
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeWaybillInfo').show();");
    }

    public void resetChangingInfoDialog() {
        isChangeInfoDialog = false;
    }

    public void confirmSaveBefore() {
        RequestContext.getCurrentInstance().execute("PF('dlg_ChangeWaybillInfo').hide();");
        RequestContext.getCurrentInstance().execute("saveChangingRecord();");
    }

    public void confirmSave() {
        isChangeInfoDialog = false;
        selectedObject.setAccount(selectedChangingWaybill.getAccount());
        selectedObject.setDispatchAddress(selectedChangingWaybill.getDispatchAddress());

        if (selectedObject.isIsPurchase()) {
            selectedObject.setDocumentNumber(selectedChangingWaybill.getDocumentNumber());
        } else {
            selectedObject.getdNumber().setId(selectedChangingWaybill.getdNumber().getId());
            selectedObject.getdNumber().setActualNumber(selectedChangingWaybill.getdNumber().getActualNumber());
        }
        save();
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
                break;
            }
        }
        branchSettingForSelection = branchSettingService.findBranchSetting(selectedObject.getBranchSetting().getBranch());

        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(16), selectedObject.getBranchSetting().getBranch());//irsaliye için seri numarları çektik.
        setDocument();
        if (processType == 1) {
            listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " AND iw.status_id = 13 ");
        } else {
            listWarehouses = warehouseService.selectListWarehouseForBranch(selectedObject.getBranchSetting().getBranch(), " ");
        }
    }

    //Sap ye başarılı olarak gönderilmiş bir satınalma irsaliyesi güncellenmek istendiğinde çalışır.Log tablosunu günceller.
    public void openUpdate() {
        int result = 0;
        result = waybillService.updateLogSap(selectedObject);

        if (result > 0) {
            selectedObject.setSapLogİsSend(false);
            RequestContext.getCurrentInstance().update("frmWaybillProcess");

            RequestContext.getCurrentInstance().update("tbvWaybill");
            RequestContext.getCurrentInstance().update("dlgWaybillItem");

            sessionBean.createUpdateMessage(result);

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("couldnotopentoeditwaybill") + sessionBean.loc.getString("tryagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void changeFuelWaybill() {
        if (selectedObject.isIsFuel()) {
            selectedObject.getListOfWarehouse().clear();
        }

    }

}
