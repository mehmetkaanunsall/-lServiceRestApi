/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 10:53:09
 */
package com.mepsan.marwiz.inventory.stockoperations.presentation;
   
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documenttemplate.business.DocumentTemplateService;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stockoperations.business.IStockOperationsService;
import com.mepsan.marwiz.inventory.stockoperations.dao.StockOperations;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent; 

@ManagedBean
@ViewScoped
public class StockOperationsBean extends GeneralDefinitionBean<StockOperations> {

    @ManagedProperty(value = "#{stockOperationsService}")
    public IStockOperationsService stockOperationsService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{documentTemplateService}")
    public DocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    private String createWhere;
    private List<Stock> listOfStock;
    private Date beginDate;
    private Date endDate;
    private int process;
    private StockOperations selectedObject;
    private List<Boolean> toogleList;
    private boolean isFind;
    private boolean isPrintTag;
    private List<StockOperations> listOfObjects, selectedObjects, tempSelectedObjects;
    private boolean isAll;

    private String pricetagStyle;
    private PrintDocumentTemplate jsonDocumentDemplate;
    private DocumentTemplate documentTemplate;

    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;
    private int branchId;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Categorization> listOfCategorization;
    private List<Account> listOfAccount;
    private int supplierType;
    private List<BranchSetting> selectedBranchList;

    public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public StockOperations getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StockOperations selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Boolean> getToogleList() {
        return toogleList;
    }

    public void setToogleList(List<Boolean> toogleList) {
        this.toogleList = toogleList;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public void setStockOperationsService(IStockOperationsService stockOperationsService) {
        this.stockOperationsService = stockOperationsService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
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

    public List<StockOperations> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<StockOperations> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public List<StockOperations> getSelectedObjects() {
        return selectedObjects;
    }

    public void setSelectedObjects(List<StockOperations> selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public String getPricetagStyle() {
        return pricetagStyle;
    }

    public void setPricetagStyle(String pricetagStyle) {
        this.pricetagStyle = pricetagStyle;
    }

    public PrintDocumentTemplate getJsonDocumentDemplate() {
        return jsonDocumentDemplate;
    }

    public void setJsonDocumentDemplate(PrintDocumentTemplate jsonDocumentDemplate) {
        this.jsonDocumentDemplate = jsonDocumentDemplate;
    }

    public DocumentTemplate getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public boolean isIsPrintTag() {
        return isPrintTag;
    }

    public void setIsPrintTag(boolean isPrintTag) {
        this.isPrintTag = isPrintTag;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public CentralSupplierBookCheckboxFilterBean getCentralSupplierBookCheckboxFilterBean() {
        return centralSupplierBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public CategoryBookCheckboxFilterBean getCategoryBookCheckboxFilterBean() {
        return categoryBookCheckboxFilterBean;
    }

    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(int supplierType) {
        this.supplierType = supplierType;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<StockOperations> getTempSelectedObjects() {
        return tempSelectedObjects;
    }

    public void setTempSelectedObjects(List<StockOperations> tempSelectedObjects) {
        this.tempSelectedObjects = tempSelectedObjects;
    }

    
    
    @PostConstruct
    public void init() {
        System.out.println("------------StockOperationsBean");
        selectedObject = new StockOperations();
        process = 2;
        isPrintTag = true;
        listOfStock = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true, true, true);
        selectedObjects = new ArrayList<>();
        tempSelectedObjects = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfCategorization = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        selectedBranchList.add(sessionBean.getUser().getLastBranchSetting());
        Calendar calendar = GregorianCalendar.getInstance();
        endDate = new Date();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();

        calendar.setTime(endDate);
        calendar.add(Calendar.MONTH, -3);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        beginDate = calendar.getTime();
        if (!sessionBean.getLastBranchSetting().isIsCentralIntegration()) {
            process = 1;
        }
        branchId = sessionBean.getUser().getLastBranchSetting().getBranch().getId();
        setListBtn(sessionBean.checkAuthority(new int[]{143}, 0));
        isCentralSupplierIconView = sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration();

    }

    public Currency findCurrency(int currency_id) {
        Currency currency = new Currency(currency_id);
        return currency;

    }

    public void find() {

        isFind = true;

        selectedObject.getListOfCategorization().clear();

        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        createWhere = stockOperationsService.createWhere(beginDate, endDate, listOfStock, process, selectedObject, isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmStockOperations:dtbStockOperations");
        if (dataTable != null) {
            dataTable.setFirst(0);
            dataTable.resetValue();
            dataTable.reset();
            dataTable.setFilters(null);
        }
        selectedObjects.clear();
        isAll = false;

        listOfObjects = findall(createWhere);

        for (StockOperations listOfObject : listOfObjects) {
            listOfObject.setTicketCount(1); // LİSTE İLK YÜKLENDİĞİNDE ETİKET SAYILARI DEFAULT "1" GELMESİ İÇİN 
        }

        setAutoCompleteValue(null);
        RequestContext.getCurrentInstance().execute("PF('stockOperationsPF').filter()");
        //   listOfFilteredObjects = new ArrayList<>();
        //    listOfFilteredObjects.addAll(listOfObjects);

        //  RequestContext.getCurrentInstance().execute("document.getElementById('frmStockOperations:frmToolbarStockOperations:globalFilter').value=null;");
        /// RequestContext.getCurrentInstance().execute("updateDatatable()");
        /// RequestContext.getCurrentInstance().execute("PF('stockOperationsPF').filter();");
        // clearFilter("stockOperationsPF");
    }

    public List<StockOperations> findall(String where) {
        return stockOperationsService.findAll(where, process);
    }

    public Date formatStringToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date processDate = null;
        try {
            String dates = date.replaceAll("\"", "");
            processDate = formatter.parse(dates);
        } catch (ParseException ex) {
            Logger.getLogger(StockOperationsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processDate;

    }

    //Fiyat Bilgileri daha önceden varsa güncellenir yoksa eklenir.
    public void processPriceList() {
        if (process == 2) {
            if (selectedObjects.size() > 0) {
                int result = stockOperationsService.processPriceList(selectedObjects);
                sessionBean.createUpdateMessage(result);
                if (result > 0) {
                    /* isAll = false;
                    isFind = false;
                    selectedObjects.clear();
                    listOfObjects.clear();
                    RequestContext.getCurrentInstance().update("pgrStockOperationsDatatable");*/
                    callChangePriceList();
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", sessionBean.getLoc().getString("chooseatleastoneproduct")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", sessionBean.getLoc().getString("thisoperationonlyworksonthepricerecommendation")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void callChangePriceList() {
        //  RequestContext.getCurrentInstance().execute("document.getElementById('frmStockOperations:frmToolbarStockOperations:globalFilter').value=null;");
        if (isPrintTag) {//Etikey Yazdırılsın MI?
            printStockTag();
            RequestContext.getCurrentInstance().update("printPanel");
        }

    }

    public void centralOrLocalSupplier(int type) {
        listOfAccount.clear();
        listOfCentralSupplier = new ArrayList<>();

        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        if (type == 0) {

            isCentralSupplier = false;

        } else {
            isCentralSupplier = true;

        }

    }

    public void updateAllInformation(ActionEvent event) {

        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {

            listOfStock.clear();
            if (stockBookCheckboxFilterBean.isAll) {
                Stock s = new Stock(0);
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Stock stock = new Stock(0);
                    stock.setName(sessionBean.loc.getString("all"));
                    stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
                }
            } else if (!stockBookCheckboxFilterBean.isAll) {
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmStockOperations:txtStock");

        } else if (event.getComponent().getParent().getParent().getId().equals("frmCentralSupplierBookFilterCheckbox")) {
            listOfCentralSupplier.clear();
            if (centralSupplierBookCheckboxFilterBean.isAll) {
                CentralSupplier s = new CentralSupplier(0);
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    CentralSupplier centralSupplier = new CentralSupplier(0);
                    centralSupplier.setName(sessionBean.loc.getString("all"));
                    centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().add(0, centralSupplier);
                }
            } else if (!centralSupplierBookCheckboxFilterBean.isAll) {
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().remove(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfCentralSupplier.addAll(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList());

            if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("centralsupplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmStockOperations:txtCentralSupplier");

        } else if (event.getComponent().getParent().getParent().getParent().getId().equals("frmCategoryBookFilter")) {

            listOfCategorization.clear();
            if (categoryBookCheckboxFilterBean.isAll) {
                Categorization s = new Categorization(0);
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
                }
            } else if (!categoryBookCheckboxFilterBean.isAll) {
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                    if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                        categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                    }
                }
            }
            listOfCategorization.addAll(categoryBookCheckboxFilterBean.getListOfCategorization());

            if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
            }

            RequestContext.getCurrentInstance().update("frmStockOperations:txtCategory");
        } else if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            listOfAccount.clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account account = new Account(0);
                    account.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, account);
                }
            } else if (!accountBookCheckboxFilterBean.isAll) {
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfAccount.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("supplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmStockOperations:txtSupplier");
        }

    }

    public void setIsAll() {
        if (isAll) {
            selectedObjects.clear();
            boolean isThere = false;
            if (process == 2) {
                for (StockOperations list : listOfObjects) {
                    if (list.getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                        isThere = true;
                    } else {
                        selectedObjects.add(list);
                    }
                }
                if (isThere) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("onlyproductswhosesalespriceisnotdeterminedbythecentercanbeselected")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else {
                selectedObjects.addAll(listOfObjects);
            }

        } else {
            selectedObjects.clear();
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        } else if (type == 2) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }
            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
        } else if (type == 3) {
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfCentralSupplier.isEmpty()) {
                if (listOfCentralSupplier.get(0).getId() == 0) {
                    centralSupplierBookCheckboxFilterBean.isAll = true;
                } else {
                    centralSupplierBookCheckboxFilterBean.isAll = false;
                }
            }
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfCentralSupplier);
        }
        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!listOfAccount.isEmpty()) {
            if (listOfAccount.get(0).getId() == 0) {
                accountBookCheckboxFilterBean.isAll = true;
            } else {
                accountBookCheckboxFilterBean.isAll = false;
            }
        }
        accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
    }

    /**
     * Stokları Veri Tabanından Yapılan Template Tasarıma Göre Printer dan
     * Çıkarılmasını sağlar.
     *
     */
    public void printStockTag() {
        if (selectedObjects.size() > 0) {

            tempSelectedObjects.clear();

            for (StockOperations selectedObject1 : selectedObjects) {
                for (int i = 0; i < selectedObject1.getTicketCount(); i++) {
                    tempSelectedObjects.add(selectedObject1);
                }

            }
            
            documentTemplate = documentTemplateService.bringInvoiceTemplate(72);
            Gson gson = new Gson();
            jsonDocumentDemplate = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
            }.getType());

            StringBuilder sb = new StringBuilder();
            sb.append("\n.pricetagsize{");
            sb.append("width:");
            sb.append(documentTemplate.getWidth().multiply(BigDecimal.valueOf(4.5)));
            sb.append("px;");
            sb.append("height:");
            sb.append(documentTemplate.getHeight().multiply(BigDecimal.valueOf(4.5)));
            sb.append("px;");
            sb.append("}");
            for (DocumentTemplateObject dto : jsonDocumentDemplate.getListOfObjects()) {
                Double val = 0.0;
                if (dto.getKeyWord().contains("containerpnl") || dto.getKeyWord().contains("imagepnl") || dto.getKeyWord().contains("stockbarcode")) {
                    val = 4.5;
                } else {
                    val = 2.3;
                }

                sb.append("\n.");
                sb.append(dto.getKeyWord());
                sb.append("{");
                sb.append("left:");
                if (dto.getKeyWord().contains("stockbarcode")) {
                    sb.append(dto.getLeft() * 4);
                } else {
                    sb.append(dto.getLeft() * val);
                }
                sb.append("px;");
                sb.append("top:");
                sb.append(dto.getTop() * val);
                sb.append("px;");
                sb.append("width:");
                if (dto.getKeyWord().contains("textpnl") || dto.getKeyWord().contains("productnamepnl") || dto.getKeyWord().contains("salepricepnl")) {
                    sb.append(dto.getWidth() * 4.5);
                } else {
                    sb.append(dto.getWidth() * val);
                }
                sb.append("px;");
                sb.append("height:");
                if (dto.getKeyWord().contains("stockbarcode")) {
                    sb.append(dto.getHeight() * 6.0);
                } else {
                    sb.append(dto.getHeight() * val);
                }
                sb.append("px;");
                sb.append("text-align:");
                sb.append(dto.getFontAlign());
                sb.append(";");
                sb.append("font-size:");
                sb.append(dto.getFontSize());
                sb.append("pt;");
                for (String string : dto.getFontStyle()) {
                    if (string.equals("bold")) {
                        sb.append("font-weight:");
                        sb.append("bold!important;");
                    }
                    if (string.equals("italic")) {
                        sb.append("font-style:");
                        sb.append("italic;"); 
                    }
                }
                sb.append("}\n");
            }
            pricetagStyle = sb.toString();
            RequestContext.getCurrentInstance().execute("printData();");

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", sessionBean.getLoc().getString("chooseatleastoneproduct")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void onRowSelect(SelectEvent event) {
        if (process == 2) {
            if (((StockOperations) event.getObject()).getSaleMandatoryPrice() != null && ((StockOperations) event.getObject()).getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                selectedObjects.remove((StockOperations) event.getObject());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
    }

    public void onToggleSelect(ToggleSelectEvent event) {
        if (process == 2) {
            if (event.isSelected()) {
                Iterator i = selectedObjects.iterator();
                boolean isThere = false;
                while (i.hasNext()) {
                    StockOperations operations;
                    operations = (StockOperations) i.next();
                    if (operations.getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                        isThere = true;
                        i.remove();
                    }
                }
                if (isThere) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("onlyproductswhosesalespriceisnotdeterminedbythecentercanbeselected")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            }
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockOperations> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal calculateUnitPrice(StockOperations stockOperations) {
        BigDecimal returnValue = BigDecimal.ZERO;

        if (stockOperations.getStock().getStockInfo().getSaleMandatoryPrice() != null && stockOperations.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            returnValue = stockOperations.getStock().getStockInfo().getSaleMandatoryPrice();
        } else if (stockOperations.getStock().getStockInfo().getCurrentSalePrice() != null && stockOperations.getStock().getStockInfo().getCurrentSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            returnValue = stockOperations.getStock().getStockInfo().getCurrentSalePrice();
        }

        if (stockOperations.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().getId() != 0) {
            if (stockOperations.getStock().getStockInfo().getWeight() != null && stockOperations.getStock().getStockInfo().getWeightUnit().getMainWeight() != null) {
                if (stockOperations.getStock().getStockInfo().getWeight().compareTo(BigDecimal.ZERO) > 0) {
                    returnValue = (returnValue.multiply(stockOperations.getStock().getStockInfo().getWeightUnit().getMainWeight())).divide(stockOperations.getStock().getStockInfo().getWeight(), RoundingMode.HALF_EVEN);
                }
            }
        } else {
            if (stockOperations.getStock().getStockInfo().getWeight() != null) {
                if (stockOperations.getStock().getStockInfo().getWeight().compareTo(BigDecimal.ZERO) > 0) {
                    returnValue = returnValue.divide(stockOperations.getStock().getStockInfo().getWeight(), RoundingMode.HALF_EVEN);
                }
            }
        }
        return returnValue;
    }

}
