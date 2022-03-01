/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 13.09.2018 11:55:18
 */
package com.mepsan.marwiz.inventory.pricelist.presentation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.centralsupplier.business.GFCentralSupplierService;
import com.mepsan.marwiz.general.centralsupplier.business.ICentralSupplierService;
import com.mepsan.marwiz.general.documenttemplate.business.DocumentTemplateService;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.printer.business.IPrinterService;
import com.mepsan.marwiz.inventory.pricelist.business.GFPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListBatchOperationsService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class PriceListBatchOpreationsBean extends GeneralDefinitionBean<PriceListItem> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{categorizationService}")
    private ICategorizationService categorizationService;

    @ManagedProperty(value = "#{priceListItemService}")
    private IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{accountService}")
    private IAccountService accountService;

    @ManagedProperty(value = "#{centralSupplierService}")
    private ICentralSupplierService centralSupplierService;

    @ManagedProperty(value = "#{priceListBatchOperationsService}")
    private IPriceListBatchOperationsService priceListBatchOperationsService;

    @ManagedProperty(value = "#{documentTemplateService}")
    public DocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{gfPriceListItemService}")
    public GFPriceListItemService gfPriceListItemService;

    @ManagedProperty(value = "#{gfCentralSupplierService}")
    public GFCentralSupplierService gfCentralSupplierService;

    @ManagedProperty(value = "#{printerService}")
    public IPrinterService printerService;

    private int stockType;
    private int processType;
    private PriceList selectedPriceList;
    private String pricetagStyle;
    private PrintDocumentTemplate jsonDocumentDemplate;
    private DocumentTemplate documentTemplate;

    private List<Categorization> selectedCategorizationList;

    public List<Categorization> getSelectedCategorizationList() {
        return selectedCategorizationList;
    }

    public void setSelectedCategorizationList(List<Categorization> selectedCategorizationList) {
        this.selectedCategorizationList = selectedCategorizationList;
    }

    private List<PriceListItem> tagStockList;

    private BigDecimal price;
    private boolean isRate;

    private TreeNode rootCategories;
    private TreeNode[] selectedCategories;
    private Categorization categorys, selectedDataCategories;
    private TreeNode findTreeNode;
    private boolean isSelectAllCategory, isSelectAllStock, isSelectAllAccount, isSelectAllRecordedStock, isSelectAllCentralSupplier;

    private List<PriceListItem> sampleList;
    private String fileData;
    private boolean isUpload, isCancel, isSave, isErrorDataShow;
    private String fileName;
    private UploadedFile uploadedFile;
    private List<PriceListItem> excelItemList;
    private List<PriceListItem> listItems;
    List<PriceListItem> tempExcelList;
    private List<Account> accountList;
    private List<Account> selectedAccountList;
    private List<Account> listOfFilteredAccountObjects;
    private String autoCompleteValueAccount;
    private LazyDataModel<PriceListItem> stockList;
    private List<PriceListItem> selectedStcokList;
    private List<PriceListItem> listOfFilteredStockObjects;
    private String autoCompleteValueStock, autoCompleteValueRecordedStock, autoCompleteValueCentralSupplier;
    private List<PriceListItem> dataList;
    private List<PriceListItem> tempSelectedDataList, tempUnselectedDataList;
    private LazyDataModel<PriceListItem> recordedStockList;
    private List<PriceListItem> selectedRecordedStock;
    private List<PriceListItem> dataListRecordedStock;
    private List<PriceListItem> tempSelectedDataListRecordedStock, tempUnselectedDataListRecordedStock;
    private List<CentralSupplier> selectedCentralSupplierList;
    private List<CentralSupplier> dataListCentralSupplier;
    private List<CentralSupplier> tempSelectedDataListCentralSupplier, tempUnselectedDataListCentralSupplier;
    private LazyDataModel<CentralSupplier> centralSupplierList;
    private int printType, oldTagQuantity;
    private List<PriceListItem> tempTagQuantityStockList; //Ürünler geçici etiket miktar listesi
    private List<CentralSupplier> tempTagQuantityCentralSupplierList; //Merkezi tedarikçiler geçici etiket miktar listesi
    private List<Account> tempTagQuantityAccountList; //Tedarikçiler geçici etiket miktar listesi
    private List<Categorization> tempTagQuantityCategoryList; //Kategoriler geçici etiket miktar listesi
    private List<PriceListItem> tempItemList;

    public List<PriceListItem> getTempSelectedDataList() {
        return tempSelectedDataList;
    }

    public void setTempSelectedDataList(List<PriceListItem> tempSelectedDataList) {
        this.tempSelectedDataList = tempSelectedDataList;
    }

    public List<PriceListItem> getTempSelectedDataListRecordedStock() {
        return tempSelectedDataListRecordedStock;
    }

    public void setTempSelectedDataListRecordedStock(List<PriceListItem> tempSelectedDataListRecordedStock) {
        this.tempSelectedDataListRecordedStock = tempSelectedDataListRecordedStock;
    }

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public PriceList getSelectedPriceList() {
        return selectedPriceList;
    }

    public void setSelectedPriceList(PriceList selectedPriceList) {
        this.selectedPriceList = selectedPriceList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setPriceListBatchOperationsService(IPriceListBatchOperationsService priceListBatchOperationsService) {
        this.priceListBatchOperationsService = priceListBatchOperationsService;
    }

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    public TreeNode getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(TreeNode rootCategories) {
        this.rootCategories = rootCategories;
    }

    public TreeNode[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(TreeNode[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public Categorization getCategorys() {
        return categorys;
    }

    public void setCategorys(Categorization categorys) {
        this.categorys = categorys;
    }

    public Categorization getSelectedDataCategories() {
        return selectedDataCategories;
    }

    public void setSelectedDataCategories(Categorization selectedDataCategories) {
        this.selectedDataCategories = selectedDataCategories;
    }

    public TreeNode getFindTreeNode() {
        return findTreeNode;
    }

    public void setFindTreeNode(TreeNode findTreeNode) {
        this.findTreeNode = findTreeNode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isIsSelectAllCategory() {
        return isSelectAllCategory;
    }

    public void setIsSelectAllCategory(boolean isSelectAllCategory) {
        this.isSelectAllCategory = isSelectAllCategory;
    }

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    public boolean isIsSelectAllStock() {
        return isSelectAllStock;
    }

    public void setIsSelectAllStock(boolean isSelectAllStock) {
        this.isSelectAllStock = isSelectAllStock;
    }

    public boolean isIsSelectAllAccount() {
        return isSelectAllAccount;
    }

    public void setIsSelectAllAccount(boolean isSelectAllAccount) {
        this.isSelectAllAccount = isSelectAllAccount;
    }

    public boolean isIsRate() {
        return isRate;
    }

    public void setIsRate(boolean isRate) {
        this.isRate = isRate;
    }

    public String getPricetagStyle() {
        return pricetagStyle;
    }

    public void setPricetagStyle(String pricetagStyle) {
        this.pricetagStyle = pricetagStyle;
    }

    public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public List<PriceListItem> getSelectedStcokList() {
        return selectedStcokList;
    }

    public void setSelectedStcokList(List<PriceListItem> selectedStcokList) {
        this.selectedStcokList = selectedStcokList;
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

    public List<PriceListItem> getTagStockList() {
        return tagStockList;
    }

    public void setTagStockList(List<PriceListItem> tagStockList) {
        this.tagStockList = tagStockList;
    }

    public List<PriceListItem> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<PriceListItem> sampleList) {
        this.sampleList = sampleList;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public boolean isIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public boolean isIsCancel() {
        return isCancel;
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public boolean isIsSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIsErrorDataShow() {
        return isErrorDataShow;
    }

    public void setIsErrorDataShow(boolean isErrorDataShow) {
        this.isErrorDataShow = isErrorDataShow;
    }

    public List<PriceListItem> getExcelItemList() {
        return excelItemList;
    }

    public void setExcelItemList(List<PriceListItem> excelItemList) {
        this.excelItemList = excelItemList;
    }

    public List<Account> getListOfFilteredAccountObjects() {
        return listOfFilteredAccountObjects;
    }

    public void setListOfFilteredAccountObjects(List<Account> listOfFilteredAccountObjects) {
        this.listOfFilteredAccountObjects = listOfFilteredAccountObjects;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public List<Account> getSelectedAccountList() {
        return selectedAccountList;
    }

    public void setSelectedAccountList(List<Account> selectedAccountList) {
        this.selectedAccountList = selectedAccountList;
    }

    public LazyDataModel<PriceListItem> getStockList() {
        return stockList;
    }

    public void setStockList(LazyDataModel<PriceListItem> stockList) {
        this.stockList = stockList;
    }

    public List<PriceListItem> getListOfFilteredStockObjects() {
        return listOfFilteredStockObjects;
    }

    public void setListOfFilteredStockObjects(List<PriceListItem> listOfFilteredStockObjects) {
        this.listOfFilteredStockObjects = listOfFilteredStockObjects;
    }

    public String getAutoCompleteValueStock() {
        return autoCompleteValueStock;
    }

    public void setAutoCompleteValueStock(String autoCompleteValueStock) {
        this.autoCompleteValueStock = autoCompleteValueStock;
    }

    public String getAutoCompleteValueAccount() {
        return autoCompleteValueAccount;
    }

    public void setAutoCompleteValueAccount(String autoCompleteValueAccount) {
        this.autoCompleteValueAccount = autoCompleteValueAccount;
    }

    public void setGfPriceListItemService(GFPriceListItemService gfPriceListItemService) {
        this.gfPriceListItemService = gfPriceListItemService;
    }

    public String getAutoCompleteValueRecordedStock() {
        return autoCompleteValueRecordedStock;
    }

    public void setAutoCompleteValueRecordedStock(String autoCompleteValueRecordedStock) {
        this.autoCompleteValueRecordedStock = autoCompleteValueRecordedStock;
    }

    public LazyDataModel<PriceListItem> getRecordedStockList() {
        return recordedStockList;
    }

    public void setRecordedStockList(LazyDataModel<PriceListItem> recordedStockList) {
        this.recordedStockList = recordedStockList;
    }

    public List<PriceListItem> getSelectedRecordedStock() {
        return selectedRecordedStock;
    }

    public void setSelectedRecordedStock(List<PriceListItem> selectedRecordedStock) {
        this.selectedRecordedStock = selectedRecordedStock;
    }

    public boolean isIsSelectAllRecordedStock() {
        return isSelectAllRecordedStock;
    }

    public void setIsSelectAllRecordedStock(boolean isSelectAllRecordedStock) {
        this.isSelectAllRecordedStock = isSelectAllRecordedStock;
    }

    public boolean isIsSelectAllCentralSupplier() {
        return isSelectAllCentralSupplier;
    }

    public void setIsSelectAllCentralSupplier(boolean isSelectAllCentralSupplier) {
        this.isSelectAllCentralSupplier = isSelectAllCentralSupplier;
    }

    public LazyDataModel<CentralSupplier> getCentralSupplierList() {
        return centralSupplierList;
    }

    public void setCentralSupplierList(LazyDataModel<CentralSupplier> centralSupplierList) {
        this.centralSupplierList = centralSupplierList;
    }

    public List<CentralSupplier> getSelectedCentralSupplierList() {
        return selectedCentralSupplierList;
    }

    public void setSelectedCentralSupplierList(List<CentralSupplier> selectedCentralSupplierList) {
        this.selectedCentralSupplierList = selectedCentralSupplierList;
    }

    public void setCentralSupplierService(ICentralSupplierService centralSupplierService) {
        this.centralSupplierService = centralSupplierService;
    }

    public String getAutoCompleteValueCentralSupplier() {
        return autoCompleteValueCentralSupplier;
    }

    public void setAutoCompleteValueCentralSupplier(String autoCompleteValueCentralSupplier) {
        this.autoCompleteValueCentralSupplier = autoCompleteValueCentralSupplier;
    }

    public void setGfCentralSupplierService(GFCentralSupplierService gfCentralSupplierService) {
        this.gfCentralSupplierService = gfCentralSupplierService;
    }

    public List<CentralSupplier> getTempSelectedDataListCentralSupplier() {
        return tempSelectedDataListCentralSupplier;
    }

    public void setTempSelectedDataListCentralSupplier(List<CentralSupplier> tempSelectedDataListCentralSupplier) {
        this.tempSelectedDataListCentralSupplier = tempSelectedDataListCentralSupplier;
    }

    public void setPrinterService(IPrinterService printerService) {
        this.printerService = printerService;
    }

    public List<PriceListItem> getTempTagQuantityStockList() {
        return tempTagQuantityStockList;
    }

    public void setTempQuantityList(List<PriceListItem> tempTagQuantityStockList) {
        this.tempTagQuantityStockList = tempTagQuantityStockList;
    }

    public List<CentralSupplier> getTempTagQuantityCentralSupplierList() {
        return tempTagQuantityCentralSupplierList;
    }

    public void setTempTagQuantityCentralSupplierList(List<CentralSupplier> tempTagQuantityCentralSupplierList) {
        this.tempTagQuantityCentralSupplierList = tempTagQuantityCentralSupplierList;
    }

    public List<Account> getTempTagQuantityAccountList() {
        return tempTagQuantityAccountList;
    }

    public void setTempTagQuantityAccountList(List<Account> tempTagQuantityAccountList) {
        this.tempTagQuantityAccountList = tempTagQuantityAccountList;
    }

    public List<Categorization> getTempTagQuantityCategoryList() {
        return tempTagQuantityCategoryList;
    }

    public void setTempTagQuantityCategoryList(List<Categorization> tempTagQuantityCategoryList) {
        this.tempTagQuantityCategoryList = tempTagQuantityCategoryList;
    }

    public List<PriceListItem> getTempItemList() {
        return tempItemList;
    }

    public void setTempItemList(List<PriceListItem> tempItemList) {
        this.tempItemList = tempItemList;
    }

    public int getOldTagQuantity() {
        return oldTagQuantity;
    }

    public void setOldTagQuantity(int oldTagQuantity) {
        this.oldTagQuantity = oldTagQuantity;
    }

    @PostConstruct
    public void init() {
        System.out.println("----PriceListBatchOperationTabBean----");
        isUpload = true;
        isSave = false;
        isErrorDataShow = false;
        selectedStcokList = new ArrayList<>();
        selectedRecordedStock = new ArrayList<>();
        selectedCentralSupplierList = new ArrayList<>();
        tempItemList = new ArrayList<>();
        tempSelectedDataList = new ArrayList<>();
        tempSelectedDataListCentralSupplier = new ArrayList<>();
        tempTagQuantityStockList = new ArrayList<>();
        tempTagQuantityCentralSupplierList = new ArrayList<>();
        tempTagQuantityAccountList = new ArrayList<>();
        tempTagQuantityCategoryList = new ArrayList<>();
        if (sessionBean.parameter instanceof PriceList) {
            selectedPriceList = (PriceList) sessionBean.parameter;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{143}, 0));

    }

    /*
    Seçilmiş Kategorileri, Stokları veya Tedarikçileri where Şartına Göndermek İçin Birleştirme Yapar
     */
    public String createWhere() {
        String idListStr = "";
        if (processType != 3) {
            if (stockType == 1) {
                if (selectedCategorizationList.size() > 0) {
                    for (Categorization cate : selectedCategorizationList) {
                        idListStr = idListStr + "," + String.valueOf(cate.getId());
                        if (cate.getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }
                    if (!idListStr.equals("")) {
                        idListStr = idListStr.substring(1, idListStr.length());
                    }
                }

            } else if (stockType == 2) {
                if (tempSelectedDataList.size() > 0) {
                    for (PriceListItem stock : tempSelectedDataList) {
                        idListStr = idListStr + "," + String.valueOf(stock.getStock().getId());
                        if (stock.getStock().getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }
                    if (!idListStr.equals("")) {
                        idListStr = idListStr.substring(1, idListStr.length());
                    }

                }
            } else if (stockType == 3 || stockType == 4) {
                if (selectedAccountList.size() > 0) {
                    for (Account account : selectedAccountList) {
                        idListStr = idListStr + "," + String.valueOf(account.getId());
                        if (account.getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }
                    if (!idListStr.equals("")) {
                        idListStr = idListStr.substring(1, idListStr.length());
                    }

                }
            } else if (stockType == 5) {
                if (isSelectAllCentralSupplier) {
                    List<CentralSupplier> tList = new ArrayList<>();
                    tList = centralSupplierService.findAllCentralSupplier(" ");
                    for (CentralSupplier p : tList) {
                        idListStr = idListStr + "," + String.valueOf(p.getId());
                        if (p.getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }
                    if (!idListStr.equals("")) {
                        idListStr = idListStr.substring(1, idListStr.length());
                    }
                } else if (tempSelectedDataListCentralSupplier.size() > 0) {
                    for (CentralSupplier centralSupplier : tempSelectedDataListCentralSupplier) {
                        idListStr = idListStr + "," + String.valueOf(centralSupplier.getId());
                        if (centralSupplier.getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }
                    if (!idListStr.equals("")) {
                        idListStr = idListStr.substring(1, idListStr.length());
                    }
                }
            }
        } else {
            if (tempSelectedDataListRecordedStock.size() > 0) {
                for (PriceListItem stock : tempSelectedDataListRecordedStock) {
                    idListStr = idListStr + "," + String.valueOf(stock.getStock().getId());
                    if (stock.getStock().getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
            }
        }

        return idListStr;
    }

    public void update() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        String where = "";
        String idListStr = "";
        if (stockType == 1) {
            if (selectedCategorizationList.size() > 0) {
                for (Categorization cate : selectedCategorizationList) {
                    idListStr = idListStr + "," + String.valueOf(cate.getId());
                    if (cate.getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else {
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonecategory"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }

        } else if (stockType == 2) {
            if (isSelectAllStock) {
                List<PriceListItem> tList = new ArrayList<>();
                tList = priceListItemService.listofPriceListItem(selectedPriceList, " ");
                for (PriceListItem p : tList) {
                    if (p.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) == 0) {
                        idListStr = idListStr + "," + String.valueOf(p.getStock().getId());
                        if (p.getStock().getId() == 0) {
                            idListStr = "";
                            break;
                        }
                    }

                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else if (tempSelectedDataList.size() > 0) {
                for (PriceListItem stock : tempSelectedDataList) {
                    idListStr = idListStr + "," + String.valueOf(stock.getStock().getId());
                    if (stock.getStock().getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else {
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("chooseatleastoneproduct"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        } else if (stockType == 3 || stockType == 4) {
            if (selectedAccountList.size() > 0) {
                for (Account account : selectedAccountList) {
                    idListStr = idListStr + "," + String.valueOf(account.getId());
                    if (account.getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else {
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        } else if (stockType == 5) {
            if (isSelectAllCentralSupplier) {
                List<CentralSupplier> tList = new ArrayList<>();
                tList = centralSupplierService.findAllCentralSupplier(" ");
                for (CentralSupplier p : tList) {
                    idListStr = idListStr + "," + String.valueOf(p.getId());
                    if (p.getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else if (tempSelectedDataListCentralSupplier.size() > 0) {
                for (CentralSupplier centralSupplier : tempSelectedDataListCentralSupplier) {
                    idListStr = idListStr + "," + String.valueOf(centralSupplier.getId());
                    if (centralSupplier.getId() == 0) {
                        idListStr = "";
                        break;
                    }
                }
                if (!idListStr.equals("")) {
                    idListStr = idListStr.substring(1, idListStr.length());
                }
                int result = priceListBatchOperationsService.updateStocks(stockType, selectedPriceList.getId(), isRate, price, idListStr);
                sessionBean.createUpdateMessage(result);
            } else {
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        }
    }

    @Override
    public void create() {

    }

    @Override
    public void save() {

    }

    public void onCellEditStock(CellEditEvent event) {
        PriceListItem newValue = new PriceListItem();
        FacesContext context = FacesContext.getCurrentInstance();
        newValue = context.getApplication().evaluateExpressionGet(context, "#{priceListItem}", PriceListItem.class);

        boolean isThere = true;
        if (tempTagQuantityStockList.isEmpty()) {
            tempTagQuantityStockList.add(newValue);

        } else {
            isThere = true;
            for (PriceListItem priceListItem : tempTagQuantityStockList) {
                if (newValue.getId() == priceListItem.getId()) {
                    priceListItem.setTagQuantity(newValue.getTagQuantity());
                    isThere = true;
                    break;

                } else {
                    isThere = false;
                }
            }

            if (!isThere) {
                tempTagQuantityStockList.add(newValue);
            }
            for (PriceListItem p : tempSelectedDataList) {
                for (PriceListItem pi : tempTagQuantityStockList) {
                    if (p.getStock().getId() == pi.getStock().getId()) {
                        if (p.getTagQuantity() != pi.getTagQuantity()) {
                            p.setTagQuantity(pi.getTagQuantity());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Ürün tipi combobox ın ürünlerdeki etiket sayısı değiştikçe Lazy de
     * tutulmasını sağlar.
     *
     * @return
     */
    public List changeQuantity(List<PriceListItem> dataList) {
        for (PriceListItem obj : tempTagQuantityStockList) {
            for (PriceListItem wm : dataList) {
                if (obj.getId() == wm.getId()) {
                    wm.setTagQuantity(obj.getTagQuantity());
                    break;
                }
            }
        }
        return dataList;
    }

    public void onCellEditCentralSupplier(CellEditEvent event) {
        CentralSupplier newValue = new CentralSupplier();
        FacesContext context = FacesContext.getCurrentInstance();
        newValue = context.getApplication().evaluateExpressionGet(context, "#{centralSupplier}", CentralSupplier.class);

        boolean isThere = true;
        if (tempTagQuantityCentralSupplierList.isEmpty()) {
            tempTagQuantityCentralSupplierList.add(newValue);

        } else {
            isThere = true;
            for (CentralSupplier centralSupplier : tempTagQuantityCentralSupplierList) {
                if (newValue.getId() == centralSupplier.getId()) {
                    centralSupplier.setTagQuantity(newValue.getTagQuantity());
                    isThere = true;
                    break;

                } else {
                    isThere = false;
                }
            }

            if (!isThere) {
                tempTagQuantityCentralSupplierList.add(newValue);
            }
        }
    }

    /**
     * Ürün tipi combobox ın merkezi tedarikçilerdeki etiket sayısı değiştikçe
     * Lazy de tutulmasını sağlar.
     *
     * @return
     */
    public List changeQuantityCentralSupplier(List<CentralSupplier> dataListCentralSupplier) {
        for (CentralSupplier obj : tempTagQuantityCentralSupplierList) {
            for (CentralSupplier wm : dataListCentralSupplier) {
                if (obj.getId() == wm.getId()) {
                    wm.setTagQuantity(obj.getTagQuantity());
                    break;
                }
            }
        }
        return dataListCentralSupplier;
    }

    public void onCellEditAccount(CellEditEvent event) {
        Account newValue = new Account();
        FacesContext context = FacesContext.getCurrentInstance();
        newValue = context.getApplication().evaluateExpressionGet(context, "#{account}", Account.class);

        boolean isThere = true;
        if (tempTagQuantityAccountList.isEmpty()) {
            tempTagQuantityAccountList.add(newValue);

        } else {
            isThere = true;
            for (Account account : tempTagQuantityAccountList) {
                if (newValue.getId() == account.getId()) {
                    account.setTagQuantity(newValue.getTagQuantity());
                    isThere = true;
                    break;

                } else {
                    isThere = false;
                }
            }

            if (!isThere) {
                tempTagQuantityAccountList.add(newValue);
            }
        }
    }

    public void openTagDialog() {
        RequestContext.getCurrentInstance().update("dlgOpenTagQuantity");
        RequestContext.getCurrentInstance().execute("PF('dlg_openTagQuantity').show()");
        categorys = selectedDataCategories;
        oldTagQuantity = selectedDataCategories.getTagQuantity();
    }

    public void saveTagCategories() {
        if (selectedDataCategories.getTagQuantity() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannnotbezero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            selectedDataCategories.setTagQuantity(oldTagQuantity);
        } else {
            if (!tempTagQuantityCategoryList.contains(selectedDataCategories)) {
                tempTagQuantityCategoryList.add(selectedDataCategories);
            }
            oldTagQuantity = selectedDataCategories.getTagQuantity();
            RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab:pgrCategorizationList");
            RequestContext.getCurrentInstance().execute("PF('dlg_openTagQuantity').hide()");
        }
    }

    public void closeCategoryDialog() {
        if (oldTagQuantity != selectedDataCategories.getTagQuantity()) {
            selectedDataCategories.setTagQuantity(oldTagQuantity);
        }
    }

    public void changeProcessType() {
        autoCompleteValueStock = "";
        autoCompleteValueCentralSupplier = "";

        if (processType == 2 || processType == 1) {
            selectedStcokList.clear();
            tempSelectedDataList.clear();
            tempTagQuantityStockList.clear();
            tempUnselectedDataList = new ArrayList<>();
            dataList = new ArrayList<>();
            stockList = findAllStock(" ");
            isSelectAllStock = false;
            //Kategoriler için
            selectedCategorizationList = new ArrayList<>();
            tempTagQuantityCategoryList.clear();
            rootCategories = createCategoryTree();
            //Merkezi tedarikçiler için
            tempTagQuantityCentralSupplierList.clear();
            isSelectAllCentralSupplier = false;
            selectedCentralSupplierList = new ArrayList<>();
            tempSelectedDataListCentralSupplier = new ArrayList<>();
            tempUnselectedDataListCentralSupplier = new ArrayList<>();
            dataListCentralSupplier = new ArrayList<>();
            centralSupplierList = findAllCentralSupplier(" ");
            //Tedarikçi için listeler temizlendi.
            isSelectAllAccount = false;
            selectedAccountList = new ArrayList<>();
            tempTagQuantityAccountList.clear();
        } else if (processType == 3) {
            stockType = 0;
            selectedRecordedStock.clear();
            tempSelectedDataListRecordedStock = new ArrayList<>();
            tempUnselectedDataListRecordedStock = new ArrayList<>();
            dataListRecordedStock = new ArrayList<>();
            recordedStockList = findAllRecordedStock(" ");
            isSelectAllRecordedStock = false;
        }
    }

    public void changeStockType() {
        autoCompleteValueStock = "";
        autoCompleteValueCentralSupplier = "";

        if (stockType == 1) {
            selectedCategorizationList = new ArrayList<>();
            rootCategories = createCategoryTree();
        } else if (stockType == 2) {
            selectedStcokList = new ArrayList<>();
            tempSelectedDataList = new ArrayList<>();
            tempUnselectedDataList = new ArrayList<>();
            dataList = new ArrayList<>();
            stockList = findAllStock(" ");
        } else if (stockType == 3) {
            isSelectAllAccount = false;
            selectedAccountList = new ArrayList<>();
            tempTagQuantityAccountList.clear();
            accountList = createAccountTable();
        } else if (stockType == 4) {
            isSelectAllAccount = false;
            selectedAccountList = new ArrayList<>();
            tempTagQuantityAccountList.clear();
            accountList = accountService.findAllAccount(35);
        } else if (stockType == 5) {
            isSelectAllCentralSupplier = false;
            selectedCentralSupplierList = new ArrayList<>();
            tempSelectedDataListCentralSupplier = new ArrayList<>();
            tempUnselectedDataListCentralSupplier = new ArrayList<>();
            dataListCentralSupplier = new ArrayList<>();
            centralSupplierList = findAllCentralSupplier(" ");
        }
    }

    public void changedRatePrice() {
        isRate = !isRate;
    }

    public void confirmShow() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_WARN);
        message.setSummary(sessionBean.getLoc().getString("warning"));
        boolean runnable = true;
        if (stockType == 1) {
            if (selectedCategorizationList.size() <= 0) {
                runnable = false;
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonecategory"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        } else if (stockType == 2) {
            if (!isSelectAllStock && tempSelectedDataList.size() <= 0) {
                runnable = false;
                message.setDetail(sessionBean.getLoc().getString("chooseatleastoneproduct"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        } else if (stockType == 3 || stockType == 4) {
            if (selectedAccountList.size() <= 0) {
                runnable = false;
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        } else if (stockType == 5) {
            if (!isSelectAllCentralSupplier && tempSelectedDataListCentralSupplier.size() <= 0) {
                runnable = false;
                message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        }
        if (runnable) {
            context.execute("PF('dlgDeleteVar').show()");
        }

    }

    /**
     * Stokları Veri Tabanından Yapılan Template Tasarıma Göre Printer dan
     * Çıkarılmasını sağlar.
     *
     * @param priceList
     */
    public void printStockTag(List<PriceListItem> priceList) {

        documentTemplate = documentTemplateService.bringInvoiceTemplate(72);
        if (documentTemplate != null) {

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
// RequestContext.getCurrentInstance().execute("setTimeout(printData,5000);");
            RequestContext.getCurrentInstance().execute("printData();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", sessionBean.getLoc().getString("templatenotfound")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public boolean isEquals(int num1, int num2) {
        if (num1 == num2) {
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal calculateUnitPrice(PriceListItem priceListItem) {

        BigDecimal returnValue = BigDecimal.ZERO;
        if (priceListItem.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            returnValue = priceListItem.getStock().getStockInfo().getSaleMandatoryPrice();
        } else if (priceListItem.getStock().getStockInfo().getCurrentSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            returnValue = priceListItem.getStock().getStockInfo().getCurrentSalePrice();
        }

        if (priceListItem.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().getId() != 0) {
            if (priceListItem.getStock().getStockInfo().getWeight() != null && priceListItem.getStock().getStockInfo().getWeightUnit().getMainWeight() != null) {
                if (priceListItem.getStock().getStockInfo().getWeight().compareTo(BigDecimal.ZERO) > 0) {
                    returnValue = (returnValue.multiply(priceListItem.getStock().getStockInfo().getWeightUnit().getMainWeight())).divide(priceListItem.getStock().getStockInfo().getWeight(), RoundingMode.HALF_EVEN);
                }
            }
        } else if (priceListItem.getStock().getStockInfo().getWeightUnit().getId() != 0) {
            if (priceListItem.getStock().getStockInfo().getWeight() != null) {
                if (priceListItem.getStock().getStockInfo().getWeight().compareTo(BigDecimal.ZERO) > 0) {
                    returnValue = returnValue.divide(priceListItem.getStock().getStockInfo().getWeight(), RoundingMode.HALF_EVEN);
                }
            }
        }
        return returnValue;

    }

    /**
     * Ürünleri Yazdıran Fonksiyonç
     */
    public void printTag() {
        FacesMessage message = new FacesMessage();
        RequestContext context = RequestContext.getCurrentInstance();

        tagStockList = new ArrayList<>();
        if (processType != 3) {
            if (stockType == 2) {//Elle Seçilen Stokları Alır.
                if (isSelectAllStock) {
                    tagStockList = priceListItemService.listofPriceListItem(selectedPriceList, " ");
                    for (PriceListItem t : tempTagQuantityStockList) {
                        for (int i = 0; i < t.getTagQuantity() - 1; i++) {
                            tagStockList.add(t);
                        }
                    }
                    Collections.sort(tagStockList, (PriceListItem priceListItem1, PriceListItem priceListItem2) -> priceListItem1.getStock().getName().compareTo(priceListItem2.getStock().getName()));
                    if (printType == 0) {
                        printStockTag(tagStockList);
                    } else {
                        printTagFromDevice(tagStockList);
                    }
                } else if (tempSelectedDataList.size() > 0) {
                    for (PriceListItem p : tempSelectedDataList) {
                        for (int i = 0; i < p.getTagQuantity(); i++) {
                            tagStockList.add(p);
                        }
                    }

                    if (printType == 0) {
                        printStockTag(tagStockList);
                    } else {
                        printTagFromDevice(tagStockList);
                    }
                } else {
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("chooseatleastoneproduct"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
                }
            } else if (stockType == 1) {//Kategori
                if (selectedCategorizationList.size() > 0) {
                    List<PriceListItem> tempList3 = new ArrayList<>();
                    tempList3 = priceListItemService.listOfStock(stockType, selectedPriceList.getId(), createWhere());
                    for (PriceListItem pli : tempList3) {
                        for (Categorization a : tempTagQuantityCategoryList) {
                            if (pli.getCategorization().getId() == a.getId()) {
                                pli.setTagQuantity(a.getTagQuantity());
                            }
                        }
                    }
                    HashMap<Integer, PriceListItem> hashMap = new HashMap();
                    for (PriceListItem s : tempList3) {
                        if (hashMap.containsKey(s.getStock().getId())) {
                            PriceListItem old = hashMap.get(s.getStock().getId());
                            if (s.getTagQuantity() > old.getTagQuantity()) {
                                old.setTagQuantity(s.getTagQuantity());
                            }
                        } else {
                            hashMap.put(s.getStock().getId(), s);
                        }
                    }

                    for (Map.Entry<Integer, PriceListItem> entry : hashMap.entrySet()) {
                        Integer key = entry.getKey();
                        PriceListItem value = entry.getValue();
                        if (value.getTagQuantity() > 1) {
                            for (int i = 0; i < value.getTagQuantity(); i++) {
                                tagStockList.add(value);
                            }
                        } else {
                            if (!tagStockList.contains(value)) {
                                tagStockList.add(value);
                            }
                        }
                    }

                    if (printType == 0) {
                        printStockTag(tagStockList);
                    } else {
                        printTagFromDevice(tagStockList);
                    }

                } else {
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("chooseatleastonecategory"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
                }

            } else if (stockType == 5) {//Merkezi Tedarikçi
                if (tempSelectedDataListCentralSupplier.size() > 0 || isSelectAllCentralSupplier) {
                    List<PriceListItem> tempList1 = new ArrayList<>();
                    tempList1 = priceListItemService.listOfStock(stockType, selectedPriceList.getId(), createWhere());
                    for (PriceListItem pli : tempList1) {
                        for (CentralSupplier cs : tempTagQuantityCentralSupplierList) {
                            if (pli.getStock().getCentralSupplier().getId() == cs.getId()) {
                                pli.setTagQuantity(cs.getTagQuantity());
                            }
                        }
                    }
                    for (PriceListItem t : tempList1) {
                        for (int i = 0; i < t.getTagQuantity(); i++) {
                            tagStockList.add(t);
                        }
                    }
                    if (printType == 0) {
                        printStockTag(tagStockList);
                    } else {
                        printTagFromDevice(tagStockList);
                    }
                } else {
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
                }
            } else {//Supplier
                if (selectedAccountList.size() > 0) {
                    List<PriceListItem> tempList2 = new ArrayList<>();
                    tempList2 = priceListItemService.listOfStock(stockType, selectedPriceList.getId(), createWhere());
                    for (PriceListItem pli : tempList2) {
                        for (Account a : tempTagQuantityAccountList) {
                            if (stockType == 4) {
                                if (pli.getStock().getSupplier().getId() == a.getId()) {
                                    pli.setTagQuantity(a.getTagQuantity());
                                }
                            } else if (stockType == 3) {
                                if (pli.getWaybill().getAccount().getId() == a.getId()) {
                                    pli.setTagQuantity(a.getTagQuantity());
                                }
                            }
                        }
                    }

                    HashMap<Integer, PriceListItem> hashMap = new HashMap();
                    for (PriceListItem s : tempList2) {

                        if (hashMap.containsKey(s.getStock().getId())) {
                            PriceListItem old = hashMap.get(s.getStock().getId());
                            if (s.getTagQuantity() > old.getTagQuantity()) {
                                old.setTagQuantity(s.getTagQuantity());
                            }
                        } else {
                            hashMap.put(s.getStock().getId(), s);
                        }
                    }

                    for (Map.Entry<Integer, PriceListItem> entry : hashMap.entrySet()) {
                        PriceListItem value = entry.getValue();
                        for (int i = 0; i < value.getTagQuantity(); i++) {
                            tagStockList.add(value);
                        }
                    }

                    if (printType == 0) {
                        printStockTag(tagStockList);
                    } else {
                        printTagFromDevice(tagStockList);
                    }

                } else {
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("chooseatleastonesupplier"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
                }
            }

        } else if (processType == 3) {
            if (isSelectAllRecordedStock) {
                Map<String, Object> d = null;
                List<PriceListItem> tempList = new ArrayList<>();
                tempList = priceListItemService.findAllRecordedStock(0, 0, "", "", d, "", selectedPriceList, 2);
                for (PriceListItem p : tempList) {
                    if (p.getPrintTagQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                        for (int i = 0; i < p.getPrintTagQuantity().doubleValue(); i++) {
                            tagStockList.add(p);
                        }
                    }
                }
                if (printType == 0) {
                    printStockTag(tagStockList);
                } else {
                    printTagFromDevice(tagStockList);
                }
            } else if (tempSelectedDataListRecordedStock.size() > 0) {
                for (PriceListItem p : tempSelectedDataListRecordedStock) {
                    if (p.getPrintTagQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                        for (int i = 0; i < p.getPrintTagQuantity().doubleValue(); i++) {
                            tagStockList.add(p);
                        }
                    }
                }
                if (printType == 0) {
                    printStockTag(tagStockList);
                } else {
                    printTagFromDevice(tagStockList);
                }
            } else {
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("chooseatleastoneproduct"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
            }
        }

    }

    /*Account Ağacları*/
    public List<Account> createAccountTable() {
        return accountService.findAllAccount(3);
    }

    /**
     * Tüm kategoriler seçildiğinde checkboxa göre tümünü ekler ya da çıkarır.
     */
    public void selectAllAccounts() {
        selectedAccountList.clear();
        if (isSelectAllAccount) {
            selectedAccountList.addAll(accountList);
        }
    }

    public void selectAllCentralSupplier() {
        if (isSelectAllCentralSupplier) {
            selectedCentralSupplierList.clear();
            tempSelectedDataListCentralSupplier.clear();
        }
    }

    public LazyDataModel<PriceListItem> findAllStock(String where) {
        return new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                dataList = priceListItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedPriceList);

                int count = priceListItemService.count(where, selectedPriceList);
                stockList.setRowCount(count);
                changeQuantity(dataList);
                changeQuantityExcel(dataList);
                selectedStcokList.clear();
                selectedStcokList.addAll(tempSelectedDataList);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return dataList;
            }
        };
    }

    public LazyDataModel<CentralSupplier> findAllCentralSupplier(String where) {
        return new CentrowizLazyDataModel<CentralSupplier>() {
            @Override
            public List<CentralSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                dataListCentralSupplier = centralSupplierService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);

                int count = centralSupplierService.count(where);
                centralSupplierList.setRowCount(count);
                changeQuantityCentralSupplier(dataListCentralSupplier);
                selectedCentralSupplierList.clear();
                selectedCentralSupplierList.addAll(tempSelectedDataListCentralSupplier);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return dataListCentralSupplier;
            }
        };
    }

    /**
     * Tüm kategoriler seçildiğinde checkboxa göre tümünü ekler ya da çıkarır.
     */
    public void selectAllStocks() {
        if (isSelectAllStock) {
            selectedStcokList.clear();
            tempSelectedDataList.clear();
        }

    }

    /*Kategoriler Ağacı*/
    public TreeNode createCategoryTree() {
        categorys = new Categorization();
        categorys.setItem(new Item(Integer.valueOf(2)));
        rootCategories = new DefaultTreeNode(new Categorization(), null);
        rootCategories.setExpanded(true);
        List<Categorization> listCegorization = categorizationService.listCategorization(categorys);
        for (Categorization categorization : listCegorization) {
            if (categorization.getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(categorization, rootCategories);
                findChildrenCategorization(parentTreeNode, listCegorization);
            }
        }

        return rootCategories;
    }

    public void findChildrenCategorization(DefaultTreeNode treeNode, List<Categorization> list) {

        for (Categorization categorization : list) {
            if (categorization.getParentId().getId() != 0) {
                if (categorization.getParentId().getId() == ((Categorization) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(categorization, treeNode);
                    findChildrenCategorization(childTreeNode, list);
                }
            }
        }
    }

    /**
     * Treede unselect event ile datalisten kategoriyi silen fonksiyondur.
     *
     * @param event
     */
    public void onNodeCategorizationUnSelect(NodeUnselectEvent event) {
        Categorization unSelectCategory = (Categorization) event.getTreeNode().getData();
        Categorization categorizationConnection = newCategorizationConnection(unSelectCategory);
        if (selectedCategorizationList.contains(categorizationConnection)) {
            selectedCategorizationList.remove(categorizationConnection);
            //stockCategorizationService.update(categorizationConnection);
            unSelectCategorizationChild(event.getTreeNode());
            unSelectCategorizationParent(event.getTreeNode());

        }

    }

    /**
     * Treede select event ile dataliste veriyi ekleyen fonksiyondur.
     *
     * @param event
     */
    public void onNodeCategorizationSelect(NodeSelectEvent event) {

        Categorization selectCategory = (Categorization) event.getTreeNode().getData();
        Categorization categorizationConnection = newCategorizationConnection(selectCategory);
        //stockCategorizationService.create(categorizationConnection);
        selectedCategorizationList.add(categorizationConnection);
        selectCategorizationChild(event.getTreeNode());
        selectCategorizatonParent(event.getTreeNode());

    }

    /**
     * Select eventi ile seçilen kategorinin parentlarını bulup dataliste
     * ekleyen fonksiyondur.
     *
     * @param node
     */
    public void selectCategorizatonParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            Categorization categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (!selectedCategorizationList.contains(categorizationConnection)) {

                if (node.getParent().isSelected() == true) {
                    selectedCategorizationList.add(categorizationConnection);
                    //stockCategorizationService.create(categorizationConnection);
                }

            }
            node = node.getParent();
        }
    }

    /**
     * Select eventi ile seçilen kategorinin childrenlarını bulup dataliste
     * ekleyen fonksiyondur.
     *
     * @param node seçilen kategori
     */
    public void selectCategorizationChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {

            for (TreeNode treeNode : children) {
                Categorization categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (!selectedCategorizationList.contains(categorizationConnection)) {
                    selectedCategorizationList.add(categorizationConnection);
                    //  stockCategorizationService.create(categorizationConnection);
                }

                selectCategorizationChild(treeNode);
            }
        }

    }

    /**
     * Unselect eventi ile seçimi kaldırılan kategorinin childrenlarını bulup
     * datalistten silen fonksiyondur.
     *
     * @param node
     */
    public void unSelectCategorizationChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {
                Categorization categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (selectedCategorizationList.contains(categorizationConnection)) {
                    selectedCategorizationList.remove(categorizationConnection);
                    //stockCategorizationService.update(categorizationConnection);
                }

                unSelectCategorizationChild(treeNode);
            }
        }

    }

    /**
     * Seçilen kategorinin parentlarını bularak listede varsa silen
     * fonksiyondur.
     *
     * @param node
     */
    public void unSelectCategorizationParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            Categorization categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (selectedCategorizationList.contains(categorizationConnection)) {
                selectedCategorizationList.remove(categorizationConnection);
                //stockCategorizationService.update(categorizationConnection);
            }
            node = node.getParent();
        }
    }

    /**
     * Tüm kategoriler seçildiğinde checkboxa göre tümünü ekler ya da çıkarır.
     */
    public void selectAllCategory() {
        if (isSelectAllCategory) {
            // stockCategorizationService.allCreat(selectedObject, selectedCategoryList, item);

        } else {
            //   stockCategorizationService.allUpdate(selectedObject, selectedCategoryList);
        }
        selectedCategorizationList = new ArrayList<>();
        allChildrens(rootCategories, isSelectAllCategory);
    }

    /**
     * Tümünü seçtiğinde tüm tree yi dolaşarak ekleme ya da çıkarma yapar.
     *
     * @param node
     * @param b
     */
    public void allChildrens(TreeNode node, boolean b) {
        for (TreeNode treeNode : node.getChildren()) {
            if (b) {
                treeNode.setSelected(true);
                Categorization categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                selectedCategorizationList.add(categorizationConnection);
            } else {
                treeNode.setSelected(false);
            }
            allChildrens(treeNode, b);
        }
    }

    /**
     * Bu methot yeni categoryconnection oluşturur.
     *
     * @param categorization
     * @return
     */
    public Categorization newCategorizationConnection(Categorization categorization) {
        Categorization categorizationConnection = new Categorization();
        //  categorizationConnection.setStock(se);
        categorizationConnection = categorization;
        return categorizationConnection;
    }

    @Override
    public List<PriceListItem> findall() {

        return null;
    }

    public void openFileUpload() {
        clearData();
        RequestContext context = RequestContext.getCurrentInstance();

        sampleList = new ArrayList<>();

        PriceListItem pli1 = new PriceListItem();
        pli1.getStock().setBarcode("8690504080886");
        pli1.setTagQuantity(1);
        sampleList.add(pli1);

        PriceListItem pli2 = new PriceListItem();
        pli2.getStock().setBarcode("8690504033936");
        pli2.setTagQuantity(2);
        sampleList.add(pli2);

        PriceListItem pli3 = new PriceListItem();
        pli3.getStock().setBarcode("8690504015239");
        pli3.setTagQuantity(3);
        sampleList.add(pli3);

        PriceListItem pli4 = new PriceListItem();
        pli4.getStock().setBarcode("8690504007203");
        pli4.setTagQuantity(4);
        sampleList.add(pli4);

        context.execute("PF('dlg_FileUploadForTag').show();");
    }

    /**
     * Bu metot Dosya yüklemek istenildiğinde tekrar dosya yükle butonuna
     * tıklanıldığında verileri sıfırlar.
     */
    public void clearData() {
        fileName = "";
        fileData = "";
        uploadedFile = null;
        isUpload = true;
        isCancel = true;
        isSave = false;
        isErrorDataShow = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmFileUploadForTag:pgrFileUpload");

    }

    /**
     * Bu metot dosya seçildilten okumak için hazırlar.Bazı değerleri sıfırlar.
     *
     * @param event
     * @throws IOException
     */
    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        RequestContext context = RequestContext.getCurrentInstance();
        fileData = "";
        context.update("frmFileUploadForTag:txtFileName");
        uploadedFile = event.getFile();
        fileName = uploadedFile.getFileName();
        String s = new String(fileName.getBytes(Charset.defaultCharset()), "UTF-8"); // gelen türkçe karakterli excel dosyasının adını utf8 formatında düzenler.
        String substringData = "";
        if (s.length() > 20) { // eğer gelen fileName değeri 20 den büyük ise substring yapılır.
            substringData = s.substring(0, 20);
        } else {
            substringData = s;
        }
        fileData = substringData.toLowerCase();

        isUpload = false;
        isCancel = false;
        isErrorDataShow = false;

        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

    }

    public void convertData() throws IOException, InvalidFormatException {
        RequestContext context = RequestContext.getCurrentInstance();
        excelItemList = new ArrayList<>();
        isSave = false;
        isErrorDataShow = false;
        listItems = new ArrayList<>();
        tempExcelList = new ArrayList<>();

        context.update("frmUploaddToolbarForTag");

        excelItemList = priceListBatchOperationsService.processUploadFileStock(uploadedFile.getInputstream());
        tempExcelList.addAll(excelItemList);
        listItems.addAll(excelItemList);

        int count = 0;
        for (PriceListItem obj : excelItemList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isSave = true;
        }
        context.execute("PF('dlg_ProductViewForTag').show();");
        context.update("frmUploaddToolbarForTag");
        context.update("frmUploaddToolbarForTag:dtbProductViewForTag");

        isCancel = false;

    }

    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isErrorDataShow) {
            for (Iterator<PriceListItem> iterator = listItems.iterator(); iterator.hasNext();) {
                PriceListItem value = iterator.next();
                if (value.getType() == 1) {
                    iterator.remove();
                }
            }
            excelItemList.clear();
            excelItemList.addAll(listItems);
        } else {
            excelItemList.clear();
            excelItemList.addAll(tempExcelList);
        }
        context.update("frmUploaddToolbarForTag:dtbProductViewForTag");
    }

    public void repeatProductShow() {
        isUpload = false;
    }

    public void saveProduct() {
        tempItemList = new ArrayList<>();
        tempItemList = priceListItemService.matchExcelToList(excelItemList, selectedPriceList);
        for (PriceListItem pi : tempItemList) {
            for (PriceListItem pp : excelItemList) {
                if (pi.getStock().getBarcode().equals(pp.getStock().getBarcode()) && pp.getTagQuantity() > 0) {
                    pi.setTagQuantity(pp.getTagQuantity());
                    pi.setType(pp.getType());
                }
                if ((!tempSelectedDataList.contains(pi)) && pi.getType() > 0) {
                    tempSelectedDataList.add(pi);
                    tempTagQuantityStockList.add(pi);
                }
            }
        }
        RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab:pngStockCountList");
        RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab:dtbStock");
        RequestContext.getCurrentInstance().execute("PF('dlg_ProductViewForTag').hide();");
        RequestContext.getCurrentInstance().execute("PF('dlg_FileUploadForTag').hide();");

    }

    public List changeQuantityExcel(List<PriceListItem> dataList) {
        for (PriceListItem obj : tempItemList) {
            for (PriceListItem wm : dataList) {
                if (obj.getId() == wm.getId()) {
                    wm.setTagQuantity(obj.getTagQuantity());
                    break;
                }
            }
        }
        return dataList;
    }

    public void changeSelected() {

        List<PriceListItem> temp = new ArrayList();
        if (autoCompleteValueStock == null || "".equals(autoCompleteValueStock)) {

            for (PriceListItem pi : dataList) {
                for (Iterator<PriceListItem> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    PriceListItem next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {
            for (PriceListItem pi : gfPriceListItemService.searchDataList) {
                for (Iterator<PriceListItem> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    PriceListItem next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataList.addAll(selectedStcokList);
        for (PriceListItem pi : temp) {
            if (!tempSelectedDataList.contains(pi)) {
                tempUnselectedDataList.add(pi);
            }
        }
        for (PriceListItem pi : tempSelectedDataList) {
            if (tempUnselectedDataList.contains(pi)) {
                tempUnselectedDataList.remove(pi);
            }
        }

    }

    public void onRowSelect(SelectEvent event) {
        PriceListItem selectStock = (PriceListItem) event.getObject();
        if (processType == 2 && selectStock.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            for (Iterator<PriceListItem> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                PriceListItem next = iterator.next();
                if (next.getId() == selectStock.getId()) {
                    iterator.remove();
                    break;
                }
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab");
        }
    }

    public void generalFilter() {
        if (autoCompleteValueStock == null) {
            stockList = findAllStock(" ");
        } else {
            gfPriceListItemService.makeSearchForFilter(autoCompleteValueStock, "pricelistbatchoperation", selectedPriceList);
            stockList = gfPriceListItemService.searchResult;
        }
    }

    public void generalFilterRecordedStock() {
        if (autoCompleteValueRecordedStock == null) {
            recordedStockList = findAllRecordedStock(" ");
        } else {
            gfPriceListItemService.makeSearchForRecordedStockFilter(autoCompleteValueRecordedStock, "pricelistbatchoperationrecordstock", selectedPriceList);
            recordedStockList = gfPriceListItemService.searchResult;
        }
    }

    public void generalFilterCentralSupplier() {
        if (autoCompleteValueCentralSupplier == null) {
            centralSupplierList = findAllCentralSupplier(" ");
        } else {
            gfCentralSupplierService.makeSearchForFilter(autoCompleteValueCentralSupplier, "pricelistbatchoperation");
            centralSupplierList = gfCentralSupplierService.searchResult;
        }
    }

    public LazyDataModel<PriceListItem> findAllRecordedStock(String where) {
        return new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                dataListRecordedStock = priceListItemService.findAllRecordedStock(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedPriceList, 1);

                int count = priceListItemService.countRecordedStock(where, selectedPriceList);
                recordedStockList.setRowCount(count);
                selectedRecordedStock.clear();
                selectedRecordedStock.addAll(tempSelectedDataListRecordedStock);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return dataListRecordedStock;
            }
        };
    }

    public void changeSelectedRecordedStock() {

        List<PriceListItem> temp = new ArrayList();
        if (autoCompleteValueRecordedStock == null || "".equals(autoCompleteValueRecordedStock)) {

            for (PriceListItem pi : dataListRecordedStock) {
                for (Iterator<PriceListItem> iterator = tempSelectedDataListRecordedStock.iterator(); iterator.hasNext();) {
                    PriceListItem next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {
            for (PriceListItem pi : gfPriceListItemService.searchDataList) {
                for (Iterator<PriceListItem> iterator = tempSelectedDataListRecordedStock.iterator(); iterator.hasNext();) {
                    PriceListItem next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataListRecordedStock.addAll(selectedRecordedStock);
        for (PriceListItem pi : temp) {
            if (!tempSelectedDataListRecordedStock.contains(pi)) {
                tempUnselectedDataListRecordedStock.add(pi);
            }
        }
        for (PriceListItem pi : tempSelectedDataListRecordedStock) {
            if (tempUnselectedDataListRecordedStock.contains(pi)) {
                tempUnselectedDataListRecordedStock.remove(pi);
            }
        }

    }

    public void beforeDeleteRecordStock() {
        boolean isDelete = false;
        if (!isSelectAllRecordedStock) {
            if (!tempSelectedDataListRecordedStock.isEmpty()) {
                isDelete = true;
            }
        } else {
            if (!dataListRecordedStock.isEmpty()) {
                isDelete = true;
            }
        }
        if (!isDelete) {
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            message.setDetail(sessionBean.getLoc().getString("chooseatleastoneproduct"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab:growlMessage");
        } else {
            RequestContext.getCurrentInstance().update("dlgConfirmDeleteRecordStock");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmDeleteRecordStock').show();");
        }

    }

    public void deleteRecordStock() {
        String deleteList = "";
        if (!isSelectAllRecordedStock) {
            if (!tempSelectedDataListRecordedStock.isEmpty()) {
                for (PriceListItem p : tempSelectedDataListRecordedStock) {
                    deleteList = deleteList + "," + String.valueOf(p.getLogPrintTagId());
                    if (p.getLogPrintTagId() == 0) {
                        deleteList = "";
                        break;
                    }
                }
                if (!deleteList.equals("")) {
                    deleteList = deleteList.substring(1, deleteList.length());
                }
            }
        }
        int result = priceListItemService.deleteRecordedStock(deleteList);
        if (result > 0) {
            selectedRecordedStock.clear();
            tempSelectedDataListRecordedStock.clear();
            RequestContext.getCurrentInstance().update("tbvPriceList:frmPriceListBatchOperationsTab:pgrRecordedStocksList");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void selectAllRecordedStocks() {
        if (isSelectAllRecordedStock) {
            selectedRecordedStock.clear();
            tempSelectedDataListRecordedStock.clear();
        }

    }

    public void changeSelectedCentralSupplier() {

        List<CentralSupplier> temp = new ArrayList();
        if (autoCompleteValueCentralSupplier == null || "".equals(autoCompleteValueCentralSupplier)) {

            for (CentralSupplier pi : dataListCentralSupplier) {
                for (Iterator<CentralSupplier> iterator = tempSelectedDataListCentralSupplier.iterator(); iterator.hasNext();) {
                    CentralSupplier next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {
            for (CentralSupplier pi : gfCentralSupplierService.searchDataList) {
                for (Iterator<CentralSupplier> iterator = tempSelectedDataListCentralSupplier.iterator(); iterator.hasNext();) {
                    CentralSupplier next = iterator.next();
                    if (next.getId() == pi.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataListCentralSupplier.addAll(selectedCentralSupplierList);
        for (CentralSupplier pi : temp) {
            if (!tempSelectedDataListCentralSupplier.contains(pi)) {
                tempUnselectedDataListCentralSupplier.add(pi);
            }
        }
        for (CentralSupplier pi : tempSelectedDataListCentralSupplier) {
            if (tempUnselectedDataListCentralSupplier.contains(pi)) {
                tempUnselectedDataListCentralSupplier.remove(pi);
            }
        }

    }

    public void goToBeforePrintTag(int type) {
        printType = type; //0 ise normal yazdır 1 ise cihazdan yazdır
        printTag();
    }

    public void printTagFromDevice(List<PriceListItem> priceList) {

        Printer printer = new Printer();
        printer = printerService.listOfPrinterAccordingToType(98, sessionBean.getUser().getLastBranch());
        if (printer.getId() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.getLoc().getString("pleaseyoudefineprinter"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            JsonObject jsonObject = new JsonObject();

            documentTemplate = documentTemplateService.bringInvoiceTemplate(72);
            if (documentTemplate != null) {
                Gson gson = new Gson();
                jsonDocumentDemplate = gson.fromJson(documentTemplate.getJson(), new TypeToken<PrintDocumentTemplate>() {
                }.getType());
                jsonObject.addProperty("width", documentTemplate.getWidth());
                jsonObject.addProperty("height", documentTemplate.getHeight());
                jsonObject.addProperty("is_vertical", documentTemplate.isIsVertical());
                JsonArray jArrayObj = new JsonArray();
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

                    jArrayObj.add(lv);
                }
                jsonObject.add("listOfObjects", jArrayObj);

//            JsonArray data = new JsonArray();
//            Stream.of(documentTemplate.getJson())
//                      .forEach(data::add);
//            jsonObject.add("listOfObjects", data);
//            documentTemplate.setJson(documentTemplate.getJson().substring(17, documentTemplate.getJson().length()));
//            documentTemplate.setJson(documentTemplate.getJson().substring(0, documentTemplate.getJson().length() - 1));
//            jsonObject.addProperty("listOfObjects", documentTemplate.getJson());
                JsonArray jArrayValues = new JsonArray();
                for (PriceListItem pi : priceList) {
                    JsonArray jArrayItems = new JsonArray();
                    for (DocumentTemplateObject dto : jsonDocumentDemplate.getListOfObjects()) {
                        JsonObject itemJson = new JsonObject();
                        itemJson.addProperty("keyWord", dto.getKeyWord());
                        if (dto.getKeyWord().contains("productnamepnl")) {
                            itemJson.addProperty("value", pi.getStock().getName());
                        } else if (dto.getKeyWord().contains("stockbarcodepnl")) {
                            itemJson.addProperty("value", pi.getStock().getBarcode());
                        } else if (dto.getKeyWord().contains("domesticproductsimagepnl") && (sessionBean.getUser().getLastBranch().getCounty().getId() == pi.getStock().getCountry().getId())) {
                            itemJson.addProperty("value", documentTemplate.getId() + "_" + dto.getKeyWord() + ".png");
                        } else if (dto.getKeyWord().contains("imagepnl") && !dto.getKeyWord().contains("domesticproductsimagepnl")) {
                            itemJson.addProperty("value", documentTemplate.getId() + "_" + dto.getKeyWord() + ".png");
                        } else if (dto.getKeyWord().contains("container")) {
                            itemJson.addProperty("value", dto.getName());
                        } else if (dto.getKeyWord().contains("textpnl")) {
                            itemJson.addProperty("value", dto.getName());
                        } else if (dto.getKeyWord().contains("madeinpnl")) {
                            if (pi.getStock().getCountry().getTag() == null) {
                                itemJson.addProperty("value", "");
                            } else {
                                itemJson.addProperty("value", pi.getStock().getCountry().getTag());
                            }
                        } else if (dto.getKeyWord().contains("lastunitpricechangedatepnl")) {
                            if (pi.getStock().getStockInfo().getLastSalePriceChangeDate() == null) {
                                itemJson.addProperty("value", "");
                            } else {
                                SimpleDateFormat sd = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat());
                                itemJson.addProperty("value", sd.format(pi.getStock().getStockInfo().getLastSalePriceChangeDate()));
                            }
                        } else if (dto.getKeyWord().contains("salepricepnl")) {
                            if (pi.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.valueOf(0)) == 1) {
                                itemJson.addProperty("value", sessionBean.getNumberFormat().format(pi.getStock().getStockInfo().getSaleMandatoryPrice()) + sessionBean.currencySignOrCode(pi.getStock().getStockInfo().getSaleMandatoryCurrency().getId(), 0));
                            } else {
                                if (pi.getPriceWithTax() == null) {
                                    itemJson.addProperty("value", 0);
                                } else {
                                    itemJson.addProperty("value", sessionBean.getNumberFormat().format(pi.getPriceWithTax()) + sessionBean.currencySignOrCode(pi.getCurrency().getId(), 0));
                                }
                            }
                        } else if (dto.getKeyWord().contains("lastchangingunitpricepnl")) {
                            if (pi.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().getId() == 0) {
                                itemJson.addProperty("value", "");
                            } else {
                                String valueUnitPrice = "";
                                valueUnitPrice = sessionBean.getNumberFormat().format(calculateUnitPrice(pi)) + " ";
                                if (pi.getStock().getStockInfo().getSaleMandatoryCurrency().getId() > 0) {
                                    valueUnitPrice = valueUnitPrice + sessionBean.currencySignOrCode(pi.getStock().getStockInfo().getSaleMandatoryCurrency().getId(), 0);
                                } else if (pi.getStock().getStockInfo().getCurrentSaleCurrency().getId() == 0) {
                                    valueUnitPrice = valueUnitPrice + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
                                } else {
                                    valueUnitPrice = valueUnitPrice + sessionBean.currencySignOrCode(pi.getStock().getStockInfo().getCurrentSaleCurrency().getId(), 0);
                                }
                                if (pi.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().getId() > 0) {
                                    valueUnitPrice = valueUnitPrice + " /" + pi.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().getSortName();
                                }
                                itemJson.addProperty("value", valueUnitPrice);
                            }
                        }
                        jArrayItems.add(itemJson);
                    }
                    jArrayValues.add(jArrayItems);
                }

                jsonObject.add("listOfValues", jArrayValues);
            }

            //System.out.println("--printTagFromDevice--" + jsonObject.toString());
            String result = printerService.sendPrinterDevice(jsonObject.toString(), printer, 0);
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
}
