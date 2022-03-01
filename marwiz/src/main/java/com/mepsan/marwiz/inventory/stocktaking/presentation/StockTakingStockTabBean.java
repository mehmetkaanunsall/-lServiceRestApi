/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 07:44:47
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.HistoryService;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import com.mepsan.marwiz.inventory.stocktaking.business.GFStockTakingItemService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.json.JSONArray;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class StockTakingStockTabBean extends GeneralBean<StockTakingItem> {

    private StockTaking stockTaking;
    private List<StockTakingItem> sampleList;
    private UploadedFile uploadedFile;
    private String fileNames, fileName;
    private boolean isOpenTransferBtn, isOpenCancelBtn, isOpenSaveBtn, isOpenErrorData;
    private List<StockTakingItem> excelStockList;// Excel ve txt dosyasından upload işleminde ortak kullanıldı
    private List<StockTakingItem> tempProductList;
    private List<StockTakingItem> tempStockList;
    private StockTakingItem stockTakingItem;
    private List<ErrorItem> errorList;
    private StockMovement stockMovement;

    private List<StockTakingItem> listOfItems;
    private int barcodeLengthStart; // Txt dosya formatını kullanıcıdan almak için kullanıldı
    private int barcodeLengthEnd;
    private int pieceLengthStart;
    private int pieceLengthEnd;
    private int processDateLengthStart;
    private int processDateLengthEnd;
    private Date batchProcessDate;
    private boolean isBatchProcessDate;
    private int dateFormatId;
    private List<StockTakingItem> listOfİtemUpdate;
    private boolean isOpenUpdate;
    private boolean changeOpenUpdate;
    private StockTakingItem changeOpenUpdateObject;
    private List<StockTakingItem> listChangeOpenUpdate;
    private List<History> listOfHistoryObjects;
    private String createdDate;
    private String createdPerson;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{stockTakingItemService}")
    private IStockTakingItemService stockTakingItemService;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{gFStockTakingItemService}")
    private GFStockTakingItemService gFStockTakingItemService;

    @ManagedProperty(value = "#{historyService}")
    private HistoryService historyService;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public void setStockTakingItemService(IStockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    public List<StockTakingItem> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<StockTakingItem> sampleList) {
        this.sampleList = sampleList;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public boolean isIsOpenTransferBtn() {
        return isOpenTransferBtn;
    }

    public void setIsOpenTransferBtn(boolean isOpenTransferBtn) {
        this.isOpenTransferBtn = isOpenTransferBtn;
    }

    public boolean isIsOpenCancelBtn() {
        return isOpenCancelBtn;
    }

    public void setIsOpenCancelBtn(boolean isOpenCancelBtn) {
        this.isOpenCancelBtn = isOpenCancelBtn;
    }

    public boolean isIsOpenSaveBtn() {
        return isOpenSaveBtn;
    }

    public void setIsOpenSaveBtn(boolean isOpenSaveBtn) {
        this.isOpenSaveBtn = isOpenSaveBtn;
    }

    public List<StockTakingItem> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<StockTakingItem> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public List<StockTakingItem> getTempProductList() {
        return tempProductList;
    }

    public void setTempProductList(List<StockTakingItem> tempProductList) {
        this.tempProductList = tempProductList;
    }

    public List<StockTakingItem> getTempStockList() {
        return tempStockList;
    }

    public void setTempStockList(List<StockTakingItem> tempStockList) {
        this.tempStockList = tempStockList;
    }

    public StockTakingItem getStockTakingItem() {
        return stockTakingItem;
    }

    public void setStockTakingItem(StockTakingItem stockTakingItem) {
        this.stockTakingItem = stockTakingItem;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public boolean isIsOpenErrorData() {
        return isOpenErrorData;
    }

    public void setIsOpenErrorData(boolean isOpenErrorData) {
        this.isOpenErrorData = isOpenErrorData;
    }

    public int getBarcodeLengthStart() {
        return barcodeLengthStart;
    }

    public void setBarcodeLengthStart(int barcodeLengthStart) {
        this.barcodeLengthStart = barcodeLengthStart;
    }

    public int getBarcodeLengthEnd() {
        return barcodeLengthEnd;
    }

    public void setBarcodeLengthEnd(int barcodeLengthEnd) {
        this.barcodeLengthEnd = barcodeLengthEnd;
    }

    public int getPieceLengthStart() {
        return pieceLengthStart;
    }

    public void setPieceLengthStart(int pieceLengthStart) {
        this.pieceLengthStart = pieceLengthStart;
    }

    public int getPieceLengthEnd() {
        return pieceLengthEnd;
    }

    public void setPieceLengthEnd(int pieceLengthEnd) {
        this.pieceLengthEnd = pieceLengthEnd;
    }

    public int getProcessDateLengthStart() {
        return processDateLengthStart;
    }

    public void setProcessDateLengthStart(int processDateLengthStart) {
        this.processDateLengthStart = processDateLengthStart;
    }

    public int getProcessDateLengthEnd() {
        return processDateLengthEnd;
    }

    public void setProcessDateLengthEnd(int processDateLengthEnd) {
        this.processDateLengthEnd = processDateLengthEnd;
    }

    public Date getBatchProcessDate() {
        return batchProcessDate;
    }

    public void setBatchProcessDate(Date batchProcessDate) {
        this.batchProcessDate = batchProcessDate;
    }

    public boolean isIsBatchProcessDate() {
        return isBatchProcessDate;
    }

    public void setIsBatchProcessDate(boolean isBatchProcessDate) {
        this.isBatchProcessDate = isBatchProcessDate;
    }

    public int getDateFormatId() {
        return dateFormatId;
    }

    public void setDateFormatId(int dateFormatId) {
        this.dateFormatId = dateFormatId;
    }

    public List<StockTakingItem> getListOfİtemUpdate() {
        return listOfİtemUpdate;
    }

    public void setListOfİtemUpdate(List<StockTakingItem> listOfİtemUpdate) {
        this.listOfİtemUpdate = listOfİtemUpdate;
    }

    public GFStockTakingItemService getgFStockTakingItemService() {
        return gFStockTakingItemService;
    }

    public void setgFStockTakingItemService(GFStockTakingItemService gFStockTakingItemService) {
        this.gFStockTakingItemService = gFStockTakingItemService;
    }

    public boolean isIsOpenUpdate() {
        return isOpenUpdate;
    }

    public void setIsOpenUpdate(boolean isOpenUpdate) {
        this.isOpenUpdate = isOpenUpdate;
    }

    public boolean isChangeOpenUpdate() {
        return changeOpenUpdate;
    }

    public void setChangeOpenUpdate(boolean changeOpenUpdate) {
        this.changeOpenUpdate = changeOpenUpdate;
    }

    public StockTakingItem getChangeOpenUpdateObject() {
        return changeOpenUpdateObject;
    }

    public void setChangeOpenUpdateObject(StockTakingItem changeOpenUpdateObject) {
        this.changeOpenUpdateObject = changeOpenUpdateObject;
    }

    public List<StockTakingItem> getListChangeOpenUpdate() {
        return listChangeOpenUpdate;
    }

    public void setListChangeOpenUpdate(List<StockTakingItem> listChangeOpenUpdate) {
        this.listChangeOpenUpdate = listChangeOpenUpdate;
    }

    public List<StockTakingItem> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(List<StockTakingItem> listOfItems) {
        this.listOfItems = listOfItems;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---------StockTakingStockTabBean---------");
        stockTaking = new StockTaking();
        listOfItems = new ArrayList<>();
        listOfİtemUpdate = new ArrayList<>();
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        isOpenUpdate = false;

        changeOpenUpdate = false;
        listChangeOpenUpdate = new ArrayList<>();
        selectedObject = new StockTakingItem();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    stockTaking = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof StockMovement) {//Ürün Hareketleri Tabından Geldi İSe
                    stockMovement = (StockMovement) ((ArrayList) sessionBean.parameter).get(i);
                    if (stockMovement.getStockTaking().getId() > 0) {
                        List<StockTaking> stockTakings = stockTakingService.stockTakingDetail(stockMovement.getStockTaking());
                        if (stockTakings.size() > 0) {
                            stockTaking = stockTakings.get(0);
                        }
                    }

                }
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking || ((ArrayList) sessionBean.parameter).get(i) instanceof StockMovement) {
                    listOfObjects = findAll("", stockTaking);

                }
            }
        }
        setListBtn(sessionBean.checkAuthority(new int[]{41}, 0));

    }

    /**
     * Stok Sayfasının Hareket Tabına Geçiş Yapar
     *
     */
    public void gotoStockMovement() {

        List<Stock> list = stockService.findAll(0, 20, null, "ASC", null, " AND stck.id = " + selectedObject.getStock().getId());
        Stock stock = new Stock();
        if (list.size() > 0) {
            stock = list.get(0);
        }
        List<Object> items = new ArrayList<>();
        items.addAll((ArrayList) sessionBean.getParameter());
        items.add(stock);

        marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", items, 1, 12);
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void exportStocks() throws IOException {
        stockTakingItemService.exportStocks("", stockTaking);

    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

//        if (newValue != null && !newValue.equals(oldValue)) {
//
//            RequestContext.getCurrentInstance().execute("cellEdt()");
//        }
    }

    /**
     * Farkı kapat butonuna basıldığında stocktaking item tablosuna kayıt ekler
     *
     * @param stockTakingItem
     */
    public void closeDifference(StockTakingItem stockTakingItem) {

        if (stockTakingItem.getRealQuantity() != null && stockTakingItem.getRealQuantity().compareTo(BigDecimal.ZERO) == -1) {
            stockTakingItem.setRealQuantity(null);
        }
        if (stockTakingItem.getRealQuantity() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseenterthequantitytoupdate")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            listOfItems.clear();
            stockTakingItem.setStockTaking(stockTaking);
            listOfItems.add(stockTakingItem);
            int result = 0;
            result = stockTakingItemService.processStockTakingItem(0, stockTaking, listOfItems, false);
            if (result > 0) {

                stockTakingItem.setId(result);
                stockTakingItem.setOpenUpdate(false);
                if (!listChangeOpenUpdate.isEmpty()) {

                    for (Iterator<StockTakingItem> iterator = listChangeOpenUpdate.iterator(); iterator.hasNext();) {
                        StockTakingItem next = iterator.next();
                        if (next.getId() == stockTakingItem.getId()) {
                            iterator.remove();
                            break;
                        }
                    }

                }
                changeOpenUpdate = true;
                RequestContext.getCurrentInstance().update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");

            }
            sessionBean.createUpdateMessage(result);
        }
    }

    /**
     * Tüm farkları kapat butonuna basıldığında itemları listeye atar listedeki
     * elemanları item tablosuna ekler.
     */
    public void closeAllDifferences() {
        isOpenUpdate = true;
        List<StockTakingItem> listUpdate = new ArrayList<>();
        List<StockTakingItem> list = new ArrayList<>();

        for (StockTakingItem sti : listOfİtemUpdate) {

            if (sti.getRealQuantity() != null && sti.getRealQuantity().compareTo(BigDecimal.ZERO) == -1) {
                sti.setRealQuantity(null);
            }
            if (sti.getId() == 0 && sti.getRealQuantity() != null) {
                sti.setStockTaking(stockTaking);
                listUpdate.add(sti);
            } else if (sti.getId() > 0 && sti.isOpenUpdate() && sti.getRealQuantity() != null) {
                sti.setStockTaking(stockTaking);
                listUpdate.add(sti);
            }
        }

        listOfItems.clear();
        listOfItems.addAll(listUpdate);
        int result = stockTakingItemService.processStockTakingItem(0, stockTaking, listOfItems, false);
        if (result > 0) {
            listOfObjects = findAll("", stockTaking);

            RequestContext.getCurrentInstance().update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");
            if (!listOfİtemUpdate.isEmpty()) {
                listOfİtemUpdate.clear();
            }

            if (!listOfItems.isEmpty()) {
                listOfItems.clear();
            }
            if (!listChangeOpenUpdate.isEmpty()) {

                listChangeOpenUpdate.clear();
            }
        }
        sessionBean.createUpdateMessage(result);

    }

    /**
     * Sayımı bitir butonuna basıldığında stock taking itemları çekerek listeye
     * atar. Sonra bu listedeki elemanların farkı kapattıktan sonra depoya giriş
     * çıkışlarını tek tek kontrol ederek varsa olanları tekrar listeler.
     */
    public void finishStockTaking() {

        List<StockTakingItem> deletedStockTakingItemList = new ArrayList<>();

        //----------Sayımda seçilen kategorilere ait olmayan stok itemlar varsa onları bularak sayımdan siler.
        if (stockTaking.getCategories() != null && !"".equals(stockTaking.getCategories())) {
            deletedStockTakingItemList = stockTakingItemService.findWithoutCategorization(stockTaking, stockTaking.getCategories());
            if (!deletedStockTakingItemList.isEmpty()) {
                int result = stockTakingItemService.delete(deletedStockTakingItemList);
            }
        }

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        StockTakingStockSaleControlBean stockSaleControlBean = (StockTakingStockSaleControlBean) viewMap.get("stockTakingStockSaleControlBean");
        stockSaleControlBean.openDialog();

//        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
//        StockTakingUncountedStocksBean stockTakingUncountedStocksBean = (StockTakingUncountedStocksBean) viewMap.get("stockTakingUncountedStocksBean");
//        stockTakingUncountedStocksBean.openDialog();
    }

    public NumberFormat unitNumberFormat(int currencyRounding) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(currencyRounding);
        formatter.setMinimumFractionDigits(currencyRounding);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

    public void changeOpenUpdate(StockTakingItem stockTakingItem) {
        changeOpenUpdate = true;
        changeOpenUpdateObject = new StockTakingItem();
        setChangeOpenUpdateObject(stockTakingItem);
        listChangeOpenUpdate.add(stockTakingItem);

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvStockTakingProc:frmStockTakingStockTab");

    }

    public void openUploadProcessPage() throws ParseException {
        // SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvStockTakingProc:frmtoolbar");
        context.update("tbvStockTakingProc:form:pgrFileUpload");

        sampleList = new ArrayList<>();
        /**
         * Örnek liste çekilir.
         */
        sampleList = stockTakingItemService.createSampleList();

        context.execute("PF('dlg_stockfileupload').show();");

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
        isBatchProcessDate = false;

    }

    public void clearProducts() {
        stockTakingItem = new StockTakingItem();
        excelStockList = new ArrayList<>();
        tempProductList = new ArrayList<>();
        tempStockList = new ArrayList<>();

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
        // isErrorDataShow = false;

        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

        if (fileNames.contains(".txt")) { //Txt dosyası ise dosya formatı bilgileri kontrol edilir
            txtFormatInformationControl();
        }

    }

    /**
     * Bu metot aktar butonuna basınca çalışır.Excelden veya txt dosyasından
     * okuduğu verileri istenilen formda geriye döndürür.
     */
    public void convertUploadData() {
        clearProducts();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvStockTakingProc:form1");
        try {
            if (fileNames.contains(".txt")) {
                excelStockList = stockTakingItemService.processUploadFileTxt(uploadedFile.getInputstream(), barcodeLengthStart, barcodeLengthEnd, pieceLengthStart, pieceLengthEnd, processDateLengthStart, processDateLengthEnd, batchProcessDate, dateFormatId);
            } else {
                excelStockList = stockTakingItemService.processUploadFile(uploadedFile.getInputstream());
            }

            tempProductList.addAll(excelStockList);
            tempStockList.addAll(excelStockList);

            int count = 0;
            for (StockTakingItem obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
                if (obj.getExcelDataType() == 1) {
                    count++;
                    break;
                }
            }

            if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
                isOpenSaveBtn = true;
            }
            context.execute("PF('dlg_productView').show();");
            context.update("tbvStockTakingProc:btnSave");

            isOpenCancelBtn = false;

        } catch (Exception e) {

        }
    }

    /**
     * Bu metot listede bulunan hatalı kayıtları göstermek için kullanılır.
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<StockTakingItem> iterator = tempStockList.iterator(); iterator.hasNext();) {
                StockTakingItem value = iterator.next();
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
        context.update("tbvStockTakingProc:frmProductView:dtbProductView");
    }

    public void saveProduct() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();
        for (StockTakingItem stock : tempProductList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }
        String resultJson = stockTakingItemService.importStockTakingItem(excelStockList, stockTaking);
        excelStockList.clear();
        excelStockList.addAll(tempProductList);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");

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
                        item.setErrorString(sessionBean.getLoc().getString("stockinformationnotfound"));
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("stocknotfoundinselectedcategories"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("productnotfoundinwarehouse"));
                        break;
                    case -4:
                        item.setErrorString(sessionBean.getLoc().getString("cannotbeaddedtothecountasitisaserviceproduct"));
                        break;
                    case -5:
                        item.setErrorString(sessionBean.getLoc().getString("sincetheroundingvalueoftheproductsunitis0enteranondecimalvalueinthequantityfield"));
                        break;
                    default:
                        break;
                }

                errorList.add(item);
            }
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            if (jsonArr.length() == excelStockList.size()) {
                message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            context.update("grwProcessMessage");
            context.execute("PF('dlg_productView').hide();");
            context.execute("PF('dlg_productErrorView').show();");
            context.update("tbvStockTakingProc:frmProductErrorView:dtbProductErrorView");

        }
        listOfObjects = findAll("", stockTaking);

        context.update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");
    }

    //Txt dosyası yükle butonuna basılınca çalışır
    public void openTxtUpload() {
        isOpenTransferBtn = true;
        isOpenCancelBtn = true;
        isOpenSaveBtn = false;
        isBatchProcessDate = false;
        fileNames = "";
        uploadedFile = null;
        barcodeLengthStart = 0;
        barcodeLengthEnd = 0;
        pieceLengthStart = 0;
        pieceLengthEnd = 0;
        processDateLengthStart = 0;
        processDateLengthEnd = 0;
        batchProcessDate = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvStockTakingProc:form1");
        context.update("tbvStockTakingProc:form1:pgrFileUploadTxt");
        context.execute("PF('dlg_fileUploadTxt').show()");

    }

    // Txt dosyası upload işleminde dosya formatı tanımlarında eksik bilgi var mı diye kontrol yapar
    public void txtFormatInformationControl() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvStockTakingProc:form1:pgrFileUploadTxt");
        if (barcodeLengthEnd == 0 || pieceLengthEnd == 0) {
            isOpenTransferBtn = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasedonotleavethelinedefinitionsemptyforthetxtfileformat")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (processDateLengthEnd == 0 && batchProcessDate == null) { // Tarih tanımları yapılmamış ise 
                isOpenTransferBtn = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasedefinethetransactiondateforthetxtfileformat")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else if (processDateLengthEnd != 0 && batchProcessDate != null) {// Her iki tarih tanımı da yapılmış ise 
                isOpenTransferBtn = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleasedefineonlyoneofthetransactiondateorbatchdatefieldsforthetxtfileformat")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else if (processDateLengthEnd != 0 && batchProcessDate == null) { // İşlem tarihi tanımı yapılmış ise Toplu İşlem Tarihi tanımı kapatılır
                isBatchProcessDate = true;
                context.update("tbvStockTakingProc:form1:cldProcessDate");
            }

        }
    }

    public void clear() {
        boolean isTxt = false;
        if (fileNames.contains(".txt")) {
            isTxt = true;
        }
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        if (isTxt) {
            isBatchProcessDate = false; // Kapatılan toplu işlem tarihi alanı tekrar açılır
            context.update("tbvStockTakingProc:form1:cldProcessDate");
            context.update("tbvStockTakingProc:form1:pgrFileUploadTxt");
        } else {
            context.update("tbvStockTakingProc:form:pgrFileUpload");
        }

    }

    public void createExcel() throws IOException {
        stockTakingItemService.exportExcel(listOfİtemUpdate, stockTaking);

    }

    public void createPdf() throws IOException {

        stockTakingItemService.exportPdf(listOfİtemUpdate, stockTaking);

    }

    public void dateUpdate() {
        if (processDateLengthEnd > 0) {
            isBatchProcessDate = true;
        } else {
            isBatchProcessDate = false;
        }
    }

    public void realQuantityUpdate(StockTakingItem obj) {

        if (obj.getRealQuantity() != null && obj.getRealQuantity().compareTo(BigDecimal.ZERO) == -1) {
            obj.setRealQuantity(null);
        }
        if (obj.getId() == 0 && obj.getRealQuantity() != null) {
            obj.setStockTaking(stockTaking);
            listOfİtemUpdate.add(obj);
        } else if (obj.getId() > 0 && obj.isOpenUpdate() && obj.getRealQuantity() != null) {
            obj.setStockTaking(stockTaking);
            listOfİtemUpdate.add(obj);
        }

    }

    public LazyDataModel<StockTakingItem> findAll(String where, StockTaking obj) {
        return new CentrowizLazyDataModel<StockTakingItem>() {
            @Override
            public List<StockTakingItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<StockTakingItem> result = stockTakingItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, obj);
                int count = stockTakingItemService.count(where, obj);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                if (!isOpenUpdate) {
                    result = changeAmountentered(result);
                    result = changeIsOpenUpdate(result);
                } else {
                    isOpenUpdate = false;
                }

                if (changeOpenUpdate) {
                    changeOpenUpdateValue(result);
                }

                return result;
            }
        };
    }

    public List<StockTakingItem> changeOpenUpdateValue(List<StockTakingItem> result) { //Aç butonuna basılınca listeyi update ederken çalışır

        for (StockTakingItem sti : result) {
            for (StockTakingItem update : listChangeOpenUpdate) {
                if (sti.getId() == update.getId()) {
                    if (isOpenUpdate) {
                        if (sti.getId() != changeOpenUpdateObject.getId()) {
                            sti.setOpenUpdate(!update.isOpenUpdate());
                        }
                    } else {

                        sti.setOpenUpdate(!update.isOpenUpdate());
                    }

                    break;
                }
            }
        }
        changeOpenUpdate = false;
        return result;
    }

    public List<StockTakingItem> changeAmountentered(List<StockTakingItem> result) {//

        for (StockTakingItem sti : result) {
            for (StockTakingItem update : listOfİtemUpdate) {
                if (update.getId() != 0) {
                    if (update.getId() == sti.getId()) {
                        sti.setRealQuantity(update.getRealQuantity());
                        sti.setOpenUpdate(false);
                    }
                } else {
                    if (update.getStock().getId() == sti.getStock().getId()) {
                        sti.setRealQuantity(update.getRealQuantity());
                    }

                }

            }
        }
        return result;
    }

    public List<StockTakingItem> changeIsOpenUpdate(List<StockTakingItem> result) {  //Güncelle butonu kontrolü için çalışır

        for (StockTakingItem sti : result) {
            for (StockTakingItem update : listChangeOpenUpdate) {
                if (update.getId() == sti.getId()) {
                    sti.setOpenUpdate(true);
                }
            }
        }

        return result;
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findAll(" ", stockTaking);
        } else {
            gFStockTakingItemService.makeSearch(autoCompleteValue, "", stockTaking);
            listOfObjects = gFStockTakingItemService.searchResult;

        }
    }

    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public LazyDataModel<StockTakingItem> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void goToHistory() {

        listOfHistoryObjects = new ArrayList<>();
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", selectedObject.getId(), "inventory.stocktakingitem", 0);

        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDateCreated());
        createdPerson = selectedObject.getUserCreated().getFullName() + " - " + selectedObject.getUserCreated().getUsername();

        if (createdDate == null) {
            createdDate = " ";
        }
        if (selectedObject.getUserCreated().getFullName()== null || selectedObject.getUserCreated().getUsername() == null) {
            createdPerson = " ";
        }

        System.out.println("*************selectedObject.getUserCreated().getFullName()**********"+selectedObject.getUserCreated().getFullName());
        System.out.println("******selectedObject.getUserCreated().getUsername()*********" + selectedObject.getUserCreated().getUsername());
        System.out.println("***createdPerson******" + createdPerson + "\n *****createdDate******" + createdDate);
        RequestContext.getCurrentInstance().execute("PF('dlgHistory').loadContents()");
    }
}
