/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:33:24
 */
package com.mepsan.marwiz.inventory.pricelist.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.HistoryService;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.pricelist.business.GFPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListItemService;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class PriceListStockTabBean extends GeneralBean<PriceListItem> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{priceListItemService}")
    private IPriceListItemService priceListItemService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    public StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{historyService}")
    public HistoryService historyService;

    @ManagedProperty(value = "#{gfPriceListItemService}")
    public GFPriceListItemService gfPriceListItemService;

    private int processType;
    private PriceList selectedPriceList;
    private List<History> listOfHistoryObjects;
    private String createdPerson;
    private String createdDate;
    private boolean isUpload, isCancel, isSave;
    private String fileName;
    private String filePath;
    private String fileData;
    private List<PriceListItem> excelItemList;
    private PriceListItem excelItem;
    private PriceListItem errorItem;
    private String resultJson;
    private List<ErrorItem> errorList;
    private boolean isErrorDataShow;
    private UploadedFile uploadedFile;
    private List<PriceListItem> listItems;
    List<PriceListItem> tempExcelList;
    private BigDecimal recommended;
    private Currency recommendedCurrency;
    private List<Currency> listOfCurrency;
    private String fileExtension;
    private List<PriceListItem> sampleList;
    private boolean isUpdate;// excelden stok aktarırken fiyat listesinde var olan ama farklı bilgilerle aktarılmak istenen kayıtların veritabanına ilk gidildiğinde güncellenmemesini sağlar
    private List<ErrorItem> errorPriceList;
    private List<PriceListItem> selectedErrorPriceList;//excelden stok aktarırken fiyat listesinde var olan ama farklı bilgilerle aktarılmak istenen kayıtlardan kullanıcının güncelleme için seçtiği kayıtların listesi
    private List<PriceListItem> updateDataList;//excelden stok aktarırken fiyat listesinde var olan ama farklı bilgilerle aktarılmak istenen kayıtlardan kullanıcının güncelleme için onay verdiği kayıtların listesi
    private List<ErrorItem> newErrorList;
    private boolean isAll;

    public void setGfPriceListItemService(GFPriceListItemService gfPriceListItemService) {
        this.gfPriceListItemService = gfPriceListItemService;
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

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public boolean isIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public boolean isIsCancel() {
        return isCancel;
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public List<PriceListItem> getExcelItemList() {
        return excelItemList;
    }

    public void setExcelItemList(List<PriceListItem> excelItemList) {
        this.excelItemList = excelItemList;
    }

    public PriceListItem getExcelItem() {
        return excelItem;
    }

    public void setExcelItem(PriceListItem excelItem) {
        this.excelItem = excelItem;
    }

    public PriceListItem getErrorItem() {
        return errorItem;
    }

    public void setErrorItem(PriceListItem errorItem) {
        this.errorItem = errorItem;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public boolean isIsErrorDataShow() {
        return isErrorDataShow;
    }

    public void setIsErrorDataShow(boolean isErrorDataShow) {
        this.isErrorDataShow = isErrorDataShow;
    }

    public boolean isIsSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public BigDecimal getRecommended() {
        return recommended;
    }

    public void setRecommended(BigDecimal recommended) {
        this.recommended = recommended;
    }

    public Currency getRecommendedCurrency() {
        return recommendedCurrency;
    }

    public void setRecommendedCurrency(Currency recommendedCurrency) {
        this.recommendedCurrency = recommendedCurrency;
    }

    public List<Currency> getListOfCurrency() {
        return listOfCurrency;
    }

    public void setListOfCurrency(List<Currency> listOfCurrency) {
        this.listOfCurrency = listOfCurrency;
    }

    public List<PriceListItem> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<PriceListItem> sampleList) {
        this.sampleList = sampleList;
    }

    public boolean isIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public List<ErrorItem> getErrorPriceList() {
        return errorPriceList;
    }

    public void setErrorPriceList(List<ErrorItem> errorPriceList) {
        this.errorPriceList = errorPriceList;
    }

    public List<PriceListItem> getSelectedErrorPriceList() {
        return selectedErrorPriceList;
    }

    public void setSelectedErrorPriceList(List<PriceListItem> selectedErrorPriceList) {
        this.selectedErrorPriceList = selectedErrorPriceList;
    }

    public List<PriceListItem> getUpdateDataList() {
        return updateDataList;
    }

    public void setUpdateDataList(List<PriceListItem> updateDataList) {
        this.updateDataList = updateDataList;
    }

    public List<ErrorItem> getNewErrorList() {
        return newErrorList;
    }

    public void setNewErrorList(List<ErrorItem> newErrorList) {
        this.newErrorList = newErrorList;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    @PostConstruct
    public void init() {
        System.out.println("----PriceListStockTabBean----");
        isUpload = true;
        isErrorDataShow = false;
        isSave = false;
        isUpdate = false;
        isAll = false;

        if (sessionBean.parameter instanceof PriceList) {
            selectedPriceList = (PriceList) sessionBean.parameter;
            selectedObject = new PriceListItem();
            listOfObjects = findall(" ");
            recommendedCurrency = new Currency();
        }

        setListBtn(sessionBean.checkAuthority(new int[]{142, 143, 144}, 0));

    }

    @Override
    public void create() {
        RequestContext context = RequestContext.getCurrentInstance();
        processType = 1;
        selectedObject = new PriceListItem();
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        selectedObject.setIs_taxIncluded(true);
        context.execute("PF('dlg_StockProcess').show()");
        context.update("dlgStockProcess");
    }

    public void update() {
        processType = 2;
        recommended = null;
        recommendedCurrency.setId(0);
        listOfHistoryObjects = new ArrayList<>();
        if (!selectedPriceList.isIsPurchase()) {
            if (selectedObject.getStock().getStockInfo().getMinProfitRate() != null) {
                if (selectedObject.getStock().getStockInfo().getCurrentPurchasePrice() != null) {
                    BigDecimal b = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().multiply(selectedObject.getStock().getStockInfo().getMinProfitRate()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
                    recommended = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().add(b);
                    recommendedCurrency.setId(selectedObject.getStock().getStockInfo().getCurrentPurchaseCurrency().getId());
                }
            } else if (selectedObject.getStock().getStockInfo().getRecommendedPrice() != null) {
                recommended = selectedObject.getStock().getStockInfo().getRecommendedPrice();
                recommendedCurrency.setId(selectedObject.getStock().getStockInfo().getCurrency().getId());
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_StockProcess').show()");
        context.update("dlgStockProcess");
    }

    @Override
    public void save() {
        int result = 0;
        selectedObject.setPriceList(selectedPriceList);

        boolean isThereSaleMandatory = false;
        if (!selectedPriceList.isIsPurchase() && selectedObject.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            isThereSaleMandatory = true;
        }

        if (!selectedPriceList.isIsPurchase() && processType == 1
                && ((selectedObject.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0 && selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId() > 0)
                && (selectedObject.getCurrency().getId() != selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId()
                || selectedObject.getPrice().compareTo(selectedObject.getStock().getStockInfo().getSaleMandatoryPrice()) != 0))) {

            selectedObject.getCurrency().setId(selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId() <= 0 ? selectedObject.getCurrency().getId() : selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId());
            selectedObject.setPrice(selectedObject.getStock().getStockInfo().getSaleMandatoryPrice());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("itcannotbechangedbecausethestockisamandatorysaleprice")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            RequestContext.getCurrentInstance().update("frmPriceListStockProcess:grdPriceListProcess");

        } else if (!isThereSaleMandatory && recommended != null && selectedObject.getPrice().compareTo(recommended) == -1 && selectedObject.getCurrency().getId() == recommendedCurrency.getId()) {
            RequestContext.getCurrentInstance().update("dlgConfirmSavePriceList");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmSave').show();");
        } else {

            if (processType == 1) {
                result = priceListItemService.create(selectedObject);
                if (result > 0) {
                    listOfObjects = findall(" ");
                    sessionBean.createUpdateMessage(result);
                }

            } else if (processType == 2 && !isThereSaleMandatory) {
                result = priceListItemService.update(selectedObject);
                listOfObjects = findall(" ");
                sessionBean.createUpdateMessage(result);
            } else if (processType == 2) {
                selectedObject.getCurrency().setId(selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId() <= 0 ? selectedObject.getCurrency().getId() : selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId());
                selectedObject.setPrice(selectedObject.getStock().getStockInfo().getSaleMandatoryPrice());

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");

                RequestContext.getCurrentInstance().update("frmPriceListStockProcess:grdPriceListProcess");

            }
            if (result > 0) {
                bringTagOfCurrency();
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_StockProcess').hide();");
                context.update("tbvPriceList:frmPriceListStockTab:dtbStock");
                context.execute("PF('stockPF').filter();");
            }
        }

    }

    public void confirmSave() {
        int result = 0;
        if (processType == 1) {
            result = priceListItemService.create(selectedObject);
        } else {
            result = priceListItemService.update(selectedObject);
        }
        if (result > 0) {
            bringTagOfCurrency();
            listOfObjects = findall(" ");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_StockProcess').hide();");
            context.update("tbvPriceList:frmPriceListStockTab:dtbStock");
            context.execute("PF('stockPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringTagOfCurrency() {
        for (Currency s : sessionBean.getCurrencies()) {
            if (s.getId() == selectedObject.getCurrency().getId()) {
                selectedObject.getCurrency().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }

    public void updateAllInformation() {
        recommended = null;
        recommendedCurrency.setId(0);
        if (stockBookFilterBean.getSelectedData() != null) {

            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            if (!selectedPriceList.isIsPurchase()) {
                if (stockBookFilterBean.getSelectedData().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
                    selectedObject.setPrice(stockBookFilterBean.getSelectedData().getStockInfo().getSaleMandatoryPrice());
                    selectedObject.getCurrency().setId(stockBookFilterBean.getSelectedData().getStockInfo().getSaleMandatoryCurrency().getId());
                } else if (stockBookFilterBean.getSelectedData().getStockInfo().getMinProfitRate() != null && stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchasePrice() != null) {
                    BigDecimal b = stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchasePrice().multiply(stockBookFilterBean.getSelectedData().getStockInfo().getMinProfitRate()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
                    selectedObject.setPrice(stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchasePrice().add(b));
                    selectedObject.getCurrency().setId(stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchaseCurrency().getId());
                    recommended = stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchasePrice().add(b);
                    recommendedCurrency.setId(stockBookFilterBean.getSelectedData().getStockInfo().getCurrentPurchaseCurrency().getId());

                } else if (stockBookFilterBean.getSelectedData().getStockInfo().getRecommendedPrice() != null) {
                    selectedObject.setPrice(stockBookFilterBean.getSelectedData().getStockInfo().getRecommendedPrice());
                    selectedObject.getCurrency().setId(stockBookFilterBean.getSelectedData().getStockInfo().getCurrency().getId());
                    recommended = stockBookFilterBean.getSelectedData().getStockInfo().getRecommendedPrice();
                    recommendedCurrency.setId(stockBookFilterBean.getSelectedData().getStockInfo().getCurrency().getId());

                } else {
                    recommended = null;
                    recommendedCurrency.setId(0);
                    selectedObject.setPrice(null);
                    selectedObject.getCurrency().setId(0);
                }
                RequestContext.getCurrentInstance().update("frmPriceListStockProcess:txtPrice");
                RequestContext.getCurrentInstance().update("frmPriceListStockProcess:slcCurrency");
            }
            RequestContext.getCurrentInstance().update("frmPriceListStockProcess:txtStock");

            stockBookFilterBean.setSelectedData(null);
        }

    }

    public void onCellEdit(CellEditEvent event) {
        int result = 0;
        recommended = null;
        recommendedCurrency.setId(0);
        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{PriceListItem}", PriceListItem.class);
        if (!event.getColumn().getClientId().contains("clmIsTaxIncluded") && !selectedPriceList.isIsPurchase() && selectedObject.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (event.getColumn().getClientId().contains("clmPrice")) {
                selectedObject.setPrice((BigDecimal) event.getOldValue());
            } else if (event.getColumn().getClientId().contains("clmCurrency")) {
                selectedObject.getCurrency().setId((int) event.getOldValue());
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();
            if (!selectedPriceList.isIsPurchase()) {
                if (selectedObject.getStock().getStockInfo().getMinProfitRate() != null) {
                    if (selectedObject.getStock().getStockInfo().getCurrentPurchasePrice() != null) {
                        BigDecimal b = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().multiply(selectedObject.getStock().getStockInfo().getMinProfitRate()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
                        recommended = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().add(b);
                        recommendedCurrency.setId(selectedObject.getStock().getStockInfo().getCurrentPurchaseCurrency().getId());
                    }
                } else if (selectedObject.getStock().getStockInfo().getRecommendedPrice() != null) {
                    recommended = selectedObject.getStock().getStockInfo().getRecommendedPrice();
                    recommendedCurrency.setId(selectedObject.getStock().getStockInfo().getCurrency().getId());

                }
            }
            if (recommended != null && selectedObject.getPrice().compareTo(recommended) == -1 && selectedObject.getCurrency().getId() == recommendedCurrency.getId()) {
                RequestContext.getCurrentInstance().update("dlgConfirmSavePriceList");
                RequestContext.getCurrentInstance().execute("PF('dlgConfirmSave').show();");
            } else {

                result = priceListItemService.update(selectedObject);
                sessionBean.createUpdateMessage(result);
            }
        }
        bringTagOfCurrency();
        RequestContext.getCurrentInstance().execute("updateDatatable()");
    }

    public void goToHistory() {
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", selectedObject.getId(), "inventory.pricelistitem", 0);
        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDateCreated());
        createdPerson = selectedObject.getUserCreated().getFullName() + " - " + selectedObject.getUserCreated().getUsername();
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");
    }

    /**
     * Bu metot aktar butonuna basılınca çalışır. yüklenen excel dosyasnın
     * içindeki verileri okur.Listeye ekler ve görüntüleme yapar.
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void convertData() throws IOException, InvalidFormatException {
        RequestContext context = RequestContext.getCurrentInstance();
        excelItemList = new ArrayList<>();
        errorItem = new PriceListItem();
        errorList = new ArrayList<>();
        errorPriceList = new ArrayList<>();
        selectedErrorPriceList = new ArrayList<>();
        isSave = false;
        isErrorDataShow = false;
        listItems = new ArrayList<>();
        tempExcelList = new ArrayList<>();
        updateDataList = new ArrayList<>();

        context.update("tbvPriceList:frmtoolbar");

        excelItemList = priceListItemService.processUploadFile(uploadedFile.getInputstream());
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
        context.execute("PF('dlg_productView').show();");
        context.update("tbvPriceList:frmtoolbar");
        context.update("tbvPriceList:frmProductView:dtbProductView");
        isCancel = false;
    }

    /**
     * Bu metot okunan dosyayı veritabanına gönderip kayıtları aktarmak için
     * kullanılır.Aktarılmayan kayıtlar için bilgi mesajı kullanıcı tarafında
     * görüntülenir.
     */
    public void saveProduct() {
        isUpdate = false;
        isAll=false;
        errorList.clear();
        updateDataList.clear();
        errorPriceList.clear();
        RequestContext context = RequestContext.getCurrentInstance();
        excelItemList.clear();
        for (PriceListItem priceListItem : tempExcelList) {
            if (priceListItem.getType() == 1) {
                excelItemList.add(priceListItem);
            }
        }
        resultJson = priceListItemService.processStockPriceList(excelItemList, selectedPriceList.getId(), isUpdate);
        excelItemList.clear();
        excelItemList.addAll(tempExcelList);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");
            listOfObjects = findall(" ");
            context.update("tbvPriceList:frmPriceListStockTab:dtbStock");

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
                        item.setErrorString(sessionBean.getLoc().getString("noproductsfoundforbarcodeinformation"));
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("datatypemismatch"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("currencynotfound"));
                        break;
                    case -4:
                        item.setErrorString(sessionBean.getLoc().getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter"));
                        break;
                    case -5:
                        item.setErrorString(sessionBean.getLoc().getString("informationdifferentfromthepricelist"));
                        errorPriceList.add(item); //fiyat listesinde var olan ama farklı bilgilerle tekrar aktarılmak istenen kayıtlar farklı bir listeye ayrıştırılır
                        errorList.remove(item);
                        break;
                    default:
                        break;
                }
                errorList.add(item);
            }

            context.execute("PF('dlg_productView').hide();");

            if (errorPriceList.size() > 0) {
                int id = 1;
                for (PriceListItem priceItem : excelItemList) {

                    for (ErrorItem errItem : errorPriceList) {
                        if (errItem.getErrorCode() == -5) {
                            if (errItem.getBarcode().equals(priceItem.getStock().getBarcode()) && priceItem.getType() != -1) {
                                priceItem.setId(id);
                                updateDataList.add(priceItem);
                                id++;
                                break;
                            }
                        }
                    }

                }
                 context.update("dlg_productErrorView");
                 context.update("tbvPriceList:frmProductErrorView");
                context.execute("PF('dlg_productErrorView').show();");
                context.update("tbvPriceList:frmProductErrorView:dtbProductErrorView");
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                if (jsonArr.length() == excelItemList.size()) {
                    message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                } else {
                    message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
                context.update("grwProcessMessage");
                context.execute("PF('dlg_productErrorView2').show();");
                context.update("tbvPriceList:frmProductErrorView2:dtbProductErrorView2");
            }

            listOfObjects = findall(" ");
            context.update("tbvPriceList:frmPriceListStockTab:dtbStock");
        }
    }

    /**
     * Bu metot listede bulunan hatalı kayıtları göstermek için kullanılır.
     */
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
        context.update("tbvPriceList:frmProductView:dtbProductView");
    }

    /**
     * Bu metot dosyadan aktarım yapılacak exceli seçmek için gerekli olan
     * dialogu açar.
     */
    public void openUploadProcessPage() {
        clearData();
        RequestContext context = RequestContext.getCurrentInstance();
        listOfCurrency = new ArrayList<>();
        listOfCurrency.addAll(sessionBean.getCurrencies());

        sampleList = priceListItemService.createSampleList();
        context.execute("PF('dlg_stockfileupload').show();");
    }

    /**
     * Bu metot dosya seçildikten sonra okumak için hazırlar.Bazı değerleri
     * sıfırlar.
     *
     * @param event
     * @throws IOException
     */
    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        RequestContext context = RequestContext.getCurrentInstance();
        fileData = "";
        context.update("tbvPriceList:form:txtFileName");
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

        fileExtension = fileName.substring(fileName.lastIndexOf("."));


        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

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
        context.update("tbvPriceList:form:pgrFileUpload");

    }

    public void repeatProductShow() {
        isUpload = false;
    }

    public void delete() {
        if (!selectedPriceList.isIsPurchase() && selectedObject.getStock().getStockInfo().getSaleMandatoryPrice().compareTo(BigDecimal.ZERO) > 0 && selectedObject.getStock().getStockInfo().getSaleMandatoryCurrency().getId() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("stockcannotbedeletedbecauseithasbeenaddedbythecenter")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }
        int result = 0;
        result = priceListItemService.delete(selectedObject);
        if (result > 0) {
            listOfObjects = findall(" ");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_StockProcess').hide();");
            context.update("tbvPriceList:frmPriceListStockTab:dtbStock");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void updatePrice() {
        RequestContext context = RequestContext.getCurrentInstance();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        PriceUpdateBean priceUpdateBean = (PriceUpdateBean) viewMap.get("priceUpdateBean");
        priceUpdateBean.getSelectedStocks().clear();
        priceUpdateBean.getListOfObjects().clear();
        priceUpdateBean.setIsAll(false);
        priceUpdateBean.setListOfObjects(priceListItemService.listOfUpdatingPriceStock(selectedPriceList));
        context.update("tbvPriceList:frmPriceUpdateDatatable");
        context.update("dlgUpdatePriceAccordingToTesf");
        context.update("tbvPriceList:frmPriceUpdateDatatable:dtbPriceUpdate");
        context.execute("PF('dlg_UpdatePriceAccordingToTesf').show();");
    }
//Fiyat listesinde zaten var olan ama farklı bilgilerle excelden tekrar aktarılmak istenilen kayıtlardan kullanıcının onayladığı kayıtları günceller

    public void savePriceUpdate() {
        newErrorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedErrorPriceList.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("pleaseselectrecordstoupdate")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
        if (!errorList.isEmpty()) {

            for (Iterator<ErrorItem> iter = errorList.iterator(); iter.hasNext();) {
                ErrorItem errItem = iter.next();

                if (errItem.getErrorCode() == -5) {
                    iter.remove();

                }
            }
        }
        if (selectedErrorPriceList.size() > 0) {
            isUpdate = true;
            resultJson = priceListItemService.processStockPriceList(selectedErrorPriceList, selectedPriceList.getId(), isUpdate);
            if (resultJson == null || resultJson.equals("[]") || resultJson.equals("") && errorList.isEmpty()) {

                sessionBean.createUpdateMessage(1);
                context.execute("PF('dlg_productView').hide();");
                context.execute("PF('dlg_productErrorView').hide();");
                listOfObjects = findall(" ");
                context.update("tbvPriceList:frmPriceListStockTab:dtbStock");

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
                            item.setErrorString(sessionBean.getLoc().getString("noproductsfoundforbarcodeinformation"));
                            break;
                        case -2:
                            item.setErrorString(sessionBean.getLoc().getString("datatypemismatch"));
                            break;
                        case -3:
                            item.setErrorString(sessionBean.getLoc().getString("currencynotfound"));
                            break;
                        case -4:
                            item.setErrorString(sessionBean.getLoc().getString("nochangecanbemadeasthesalespriceoftheproductisdeterminedbythecenter"));
                            break;

                        default:
                            break;
                    }

                    newErrorList.add(item);
                }
                errorList.addAll(newErrorList);
            }
        }

        if (!errorList.isEmpty()) {
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            if (errorList.size() == excelItemList.size()) {
                message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }

            context.update("grwProcessMessage");
            context.execute("PF('dlg_productErrorView').hide();");
            context.execute("PF('dlg_productErrorView2').show();");
            context.update("tbvPriceList:frmProductErrorView2:dtbProductErrorView2");

        } else {
            context.execute("PF('dlg_productErrorView').hide();");
            context.execute("PF('dlg_stockfileupload').hide();");

        }

        listOfObjects = findall(" ");
        context.update("tbvPriceList:frmPriceListStockTab:dtbStock");
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findall(" ");
        } else {
            gfPriceListItemService.makeSearchForPriceList(autoCompleteValue, selectedPriceList);
            listOfObjects = gfPriceListItemService.searchResult;
        }
    }

    @Override
    public LazyDataModel<PriceListItem> findall(String where) {
        return new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<PriceListItem> result = priceListItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedPriceList);
                int count = priceListItemService.count(where, selectedPriceList);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setIsAll() {

        if (isAll) {
            selectedErrorPriceList.clear();
            selectedErrorPriceList.addAll(updateDataList);
            RequestContext.getCurrentInstance().update("frmProductErrorView:dtbProductErrorView");
        } else {
            selectedErrorPriceList.clear();

        }
    }
    
    public void downloadSampleList()
    {
        priceListItemService.downloadSampleList(sampleList);
    }
    //priceListItem
}
