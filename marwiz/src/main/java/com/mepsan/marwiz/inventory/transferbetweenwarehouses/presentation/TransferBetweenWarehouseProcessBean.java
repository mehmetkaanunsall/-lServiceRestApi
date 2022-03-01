/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.02.2018 03:38:17
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.GFWarehouseTransferMovementService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.ITransferBetweenWarehouseService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao.ErrorItems;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseMovementService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class TransferBetweenWarehouseProcessBean extends GeneralBean<WarehouseMovement> {

    private Warehouse entryWarehouse;
    private Warehouse exitWarehouse;
    private List<Warehouse> listOfEntryWarehouse;
    private List<Warehouse> listOfExitWarehouse;
    private boolean isDisable;
    private List<WarehouseMovement> selectedList;
    private WarehouseTransfer selectedWarehouseTransfer;
    private int processType;
    private boolean isDialog;

    private String fileName;
    private String fileData;
    private UploadedFile uploadedFile;
    private boolean isUpload, isCancel, isSave;
    private boolean isErrorDataShow;
    private List<WarehouseMovement> sampleList;
    private String fileExtension;
    private List<WarehouseMovement> excelItemList;
    private WarehouseMovement errorItem;
    private List<ErrorItems> errorList;
    private WarehouseMovement selectedQuantityList;
    private List<ErrorItems> errorQuantityList;
    private List<WarehouseMovement> listItems;
    List<WarehouseMovement> tempExcelList;
    private List<WarehouseMovement> updateDataList;
    private List<ErrorItems> newErrorList;
    private boolean isAll;
    private String resultJson;

    List<WarehouseMovement> tempQuantityList; //Geçici miktar listesi

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{warehouseMovementService}")
    private IWarehouseMovementService warehouseMovementService;

    @ManagedProperty(value = "#{transferBetweenWarehouseService}")
    private ITransferBetweenWarehouseService transferBetweenWarehouseService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{gfWarehouseTransferMovementService}")
    private GFWarehouseTransferMovementService gfWarehouseTransferMovementService;

    public Warehouse getEntryWarehouse() {
        return entryWarehouse;
    }

    public void setEntryWarehouse(Warehouse entryWarehouse) {
        this.entryWarehouse = entryWarehouse;
    }

    public Warehouse getExitWarehouse() {
        return exitWarehouse;
    }

    public void setExitWarehouse(Warehouse exitWarehouse) {
        this.exitWarehouse = exitWarehouse;
    }

    public List<Warehouse> getListOfEntryWarehouse() {
        return listOfEntryWarehouse;
    }

    public void setListOfEntryWarehouse(List<Warehouse> listOfEntryWarehouse) {
        this.listOfEntryWarehouse = listOfEntryWarehouse;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public boolean isIsDisable() {
        return isDisable;
    }

    public void setIsDisable(boolean isDisable) {
        this.isDisable = isDisable;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setTransferBetweenWarehouseService(ITransferBetweenWarehouseService transferBetweenWarehouseService) {
        this.transferBetweenWarehouseService = transferBetweenWarehouseService;
    }

    public void setWarehouseMovementService(IWarehouseMovementService warehouseMovementService) {
        this.warehouseMovementService = warehouseMovementService;
    }

    public List<WarehouseMovement> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<WarehouseMovement> selectedList) {
        this.selectedList = selectedList;
    }

    public List<Warehouse> getListOfExitWarehouse() {
        return listOfExitWarehouse;
    }

    public void setListOfExitWarehouse(List<Warehouse> listOfExitWarehouse) {
        this.listOfExitWarehouse = listOfExitWarehouse;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WarehouseTransfer getSelectedWarehouseTransfer() {
        return selectedWarehouseTransfer;
    }

    public void setSelectedWarehouseTransfer(WarehouseTransfer selectedWarehouseTransfer) {
        this.selectedWarehouseTransfer = selectedWarehouseTransfer;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public boolean isIsDialog() {
        return isDialog;
    }

    public void setIsDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
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

    public boolean isIsErrorDataShow() {
        return isErrorDataShow;
    }

    public void setIsErrorDataShow(boolean isErrorDataShow) {
        this.isErrorDataShow = isErrorDataShow;
    }

    public List<WarehouseMovement> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<WarehouseMovement> sampleList) {
        this.sampleList = sampleList;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public List<WarehouseMovement> getExcelItemList() {
        return excelItemList;
    }

    public void setExcelItemList(List<WarehouseMovement> excelItemList) {
        this.excelItemList = excelItemList;
    }

    public WarehouseMovement getErrorItem() {
        return errorItem;
    }

    public void setErrorItem(WarehouseMovement errorItem) {
        this.errorItem = errorItem;
    }

    public List<ErrorItems> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItems> errorList) {
        this.errorList = errorList;
    }

    public WarehouseMovement getSelectedQuantityList() {
        return selectedQuantityList;
    }

    public void setSelectedQuantityList(WarehouseMovement selectedQuantityList) {
        this.selectedQuantityList = selectedQuantityList;
    }

    public List<ErrorItems> getErrorQuantityList() {
        return errorQuantityList;
    }

    public void setErrorQuantityList(List<ErrorItems> errorQuantityList) {
        this.errorQuantityList = errorQuantityList;
    }

    public List<WarehouseMovement> getListItems() {
        return listItems;
    }

    public void setListItems(List<WarehouseMovement> listItems) {
        this.listItems = listItems;
    }

    public List<WarehouseMovement> getTempExcelList() {
        return tempExcelList;
    }

    public void setTempExcelList(List<WarehouseMovement> tempExcelList) {
        this.tempExcelList = tempExcelList;
    }

    public List<WarehouseMovement> getUpdateDataList() {
        return updateDataList;
    }

    public void setUpdateDataList(List<WarehouseMovement> updateDataList) {
        this.updateDataList = updateDataList;
    }

    public List<ErrorItems> getNewErrorList() {
        return newErrorList;
    }

    public void setNewErrorList(List<ErrorItems> newErrorList) {
        this.newErrorList = newErrorList;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public List<WarehouseMovement> getTempQuantityList() {
        return tempQuantityList;
    }

    public void setTempQuantityList(List<WarehouseMovement> tempQuantityList) {
        this.tempQuantityList = tempQuantityList;
    }

    public GFWarehouseTransferMovementService getGfWarehouseTransferMovementService() {
        return gfWarehouseTransferMovementService;
    }

    public void setGfWarehouseTransferMovementService(GFWarehouseTransferMovementService gfWarehouseTransferMovementService) {
        this.gfWarehouseTransferMovementService = gfWarehouseTransferMovementService;
    }

    @PostConstruct
    public void init() {

        selectedWarehouseTransfer = new WarehouseTransfer();
        selectedObject = new WarehouseMovement();
        tempQuantityList = new ArrayList<>();

        System.out.println("-----TransferBetweenWarehouseProcessBean------------");
        entryWarehouse = new Warehouse();
        exitWarehouse = new Warehouse();
        listOfEntryWarehouse = new ArrayList<>();
        listOfExitWarehouse = new ArrayList<>();
        listOfEntryWarehouse = warehouseService.selectListAllWarehouse(" AND iw.is_fuel=FALSE ");
        isUpload = true;
        isErrorDataShow = false;
        selectedList = new ArrayList<>();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof WarehouseTransfer) {
                    selectedWarehouseTransfer = (WarehouseTransfer) ((ArrayList) sessionBean.parameter).get(i);
                    processType = 2;
                    listOfExitWarehouse = warehouseService.selectListAllWarehouse(" AND iw.is_fuel=FALSE ");
                    exitWarehouse = selectedWarehouseTransfer.getWarehouseReceipt().getWarehouse();
                    entryWarehouse = selectedWarehouseTransfer.getTransferWarehouseReceipt().getWarehouse();
                    listOfObjects = findAll("", 1);
                    break;
                }
            }
        } else {
            processType = 1;
            listOfExitWarehouse = warehouseService.selectListWarehouse(" AND iw.is_fuel=FALSE ");
        }

        isDisable = false;

        setListBtn(sessionBean.checkAuthority(new int[]{318, 319, 320}, 0));

    }

    public void changeWarehouse(String type) {

        tempQuantityList.clear();
        if (entryWarehouse.getId() == exitWarehouse.getId()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("betweensamewarehousecannotbetransferred")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            if ("exit".equals(type)) {
                exitWarehouse.setId(0);
                RequestContext.getCurrentInstance().update("frmWarehouseTransferProcess:slcExitWarehouse");
            } else if ("entry".equals(type)) {
                entryWarehouse.setId(0);
                RequestContext.getCurrentInstance().update("frmWarehouseTransferProcess:slcEntryWarehouse");
            }
        } else {
            for (Warehouse warehouse : listOfEntryWarehouse) {
                if (entryWarehouse.getId() == warehouse.getId()) {
                    entryWarehouse.getBranch().setId(warehouse.getBranch().getId());
                    break;
                }
            }
            listOfObjects = findAll("", 0);
            RequestContext.getCurrentInstance().execute("PF('warehouseMovementPF').filter();");
            RequestContext.getCurrentInstance().update("tbvWarehouseTransferProc:frmWarehouseTransferStockTab");
            RequestContext.getCurrentInstance().update("tbvWarehouseTransferProc:frmToolbarWarehouseMovement");
            RequestContext.getCurrentInstance().update("tbvWarehouseTransferProc:frmWarehouseTransferStockTab:dtbWarehouseMovement");
            RequestContext.getCurrentInstance().update("tbvWarehouseTransferProc:frmTransfer");
        }

    }

    public void save() {
        RequestContext.getCurrentInstance().execute("transferSave();");
    }

    public void remoteSave() {

        selectedList.clear();

        if (!tempQuantityList.isEmpty()) {
            selectedList.addAll(tempQuantityList);
        }

        if (selectedList.isEmpty()) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("atleastonestockmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            System.out.println("remoteSave else");
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmTransfer').show();");

        }

    }

    public void confirmSave() {
        int result = 0;
        if (processType == 1) {
            for (Iterator<WarehouseMovement> iterator = selectedList.iterator(); iterator.hasNext();) {
                WarehouseMovement value = iterator.next();
                if (value.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    iterator.remove();
                }
            }

            result = transferBetweenWarehouseService.save(entryWarehouse, exitWarehouse, selectedList, 0, selectedWarehouseTransfer);
        } else {
            result = transferBetweenWarehouseService.save(entryWarehouse, exitWarehouse, selectedList, 1, selectedWarehouseTransfer);
        }

        if (result > 0) {
            marwiz.goToPage("/pages/inventory/transferbetweenwarehouses/transferbetweenwarehouses.xhtml", null, 1, 47);
        }

        sessionBean.createUpdateMessage(result);
    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedWarehouseTransfer.getProcessDate())) {
            int result = 0;
            if (isDialog) {
                result = transferBetweenWarehouseService.delete(entryWarehouse, exitWarehouse, selectedList, 3, selectedWarehouseTransfer, selectedObject);
                if (result > 0) {
                    RequestContext.getCurrentInstance().update("tbvWarehouseTransferProc:frmWarehouseTransferStockTab:dtbWarehouseMovement");
                    RequestContext.getCurrentInstance().execute("PF('warehouseMovementPF').filter();");
                    RequestContext.getCurrentInstance().execute("PF('dlg_WarehouseTransferStockProc').hide();");
                }
            } else {
                selectedObject = new WarehouseMovement();

                result = transferBetweenWarehouseService.delete(entryWarehouse, exitWarehouse, selectedList, 2, selectedWarehouseTransfer, selectedObject);

                if (result > 0) {
                    marwiz.goToPage("/pages/inventory/transferbetweenwarehouses/transferbetweenwarehouses.xhtml", null, 1, 47);
                }
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onCellEdit(CellEditEvent event) {

        WarehouseMovement newValue = new WarehouseMovement();
        FacesContext context = FacesContext.getCurrentInstance();
        newValue = context.getApplication().evaluateExpressionGet(context, "#{WarehouseMovement}", WarehouseMovement.class);

        boolean isThere = true;
        if (tempQuantityList.isEmpty()) {
            tempQuantityList.add(newValue);

        } else {
            isThere = true;
            for (WarehouseMovement warehouseMovement : tempQuantityList) {
                if (newValue.getStock().getId() == warehouseMovement.getStock().getId()) {
                    warehouseMovement.setQuantity(newValue.getQuantity());
                    isThere = true;
                    break;

                } else {
                    isThere = false;
                }
            }

            if (!isThere) {
                tempQuantityList.add(newValue);

            }
        }

    }

    public List changeQuantity(List<WarehouseMovement> result) {
        for (WarehouseMovement obj : tempQuantityList) {
            for (WarehouseMovement wm : result) {
                if (obj.getStock().getId() == wm.getStock().getId()) {
                    wm.setQuantity(obj.getQuantity());
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Stok listesini lazy data modele göre çeker.
     *
     * @param where
     * @param type
     * @return
     */
    public LazyDataModel<WarehouseMovement> findAll(String where, int type) {
        return new CentrowizLazyDataModel<WarehouseMovement>() {

            @Override
            public List<WarehouseMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseMovement> result = warehouseMovementService.findAllAccordingToWarehouse(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
                int count = warehouseMovementService.count(where, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                changeQuantity(result);
                return result;
            }
        };
    }

    public void update() {

        isDialog = true;
        RequestContext.getCurrentInstance().update("dlgWarehouseTransferStockProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_WarehouseTransferStockProc').show();");
    }

    public void closeDialog() {
        isDialog = false;
    }

    /*
     * Bu metot dosya seçildikten sonra okumak için hazırlar.Bazı değerleri
     * sıfırlar.
     *
     * @param event
     * @throws IOException
     */
    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        RequestContext context = RequestContext.getCurrentInstance();
        fileData = "";
        context.update("tbvWarehouseTransferProc:form:txtFileName");
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

    /*
     * Bu metot Dosya yüklemek istenildiğinde tekrar dosya yükle butonuna
     * tıklanıldığında verileri sıfırlar.
     */
    public void clearData() {
        fileName = "";
        fileData = "";
        uploadedFile = null;
        isCancel = true;
        isUpload = true;
        isSave = false;
        isErrorDataShow = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvWarehouseTransferProc:form:pgrFileUpload");
    }

    public void repeatProductShow() {
        isUpload = false;
    }

    public void openUploadProcessPage() {
        clearData();
        RequestContext context = RequestContext.getCurrentInstance();
        sampleList = transferBetweenWarehouseService.createSampleList();
        context.execute("PF('dlg_quantityfileupload').show();");
    }

    /*
     * Bu metot aktar butonuna basılınca çalışır. yüklenen excel dosyasnın
     * içindeki verileri okur.Listeye ekler ve görüntüleme yapar.
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void convertData() throws IOException, InvalidFormatException {
        RequestContext context = RequestContext.getCurrentInstance();
        excelItemList = new ArrayList<>();
        errorItem = new WarehouseMovement();
        errorList = new ArrayList<>();
        errorQuantityList = new ArrayList<>();
        isSave = false;
        isErrorDataShow = false;
        listItems = new ArrayList<>();
        tempExcelList = new ArrayList<>();
        updateDataList = new ArrayList<>();

        context.update("tbvWarehouseTransferProc:frmtoolbar");

        excelItemList = transferBetweenWarehouseService.processUploadFile(uploadedFile.getInputstream());
        tempExcelList.addAll(excelItemList);
        listItems.addAll(excelItemList);

        int count = 0;
        for (WarehouseMovement obj : excelItemList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isSave = true;
        }
        context.execute("PF('dlg_productView').show();");
        context.update("tbvWarehouseTransferProc:frmtoolbar");
        context.update("tbvWarehouseTransferProc:frmProductView:dtbProductView");
        isCancel = false;
    }

    /*
     * Bu metot listede bulunan hatalı kayıtları göstermek için kullanılır.
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isErrorDataShow) {
            for (Iterator<WarehouseMovement> iterator = listItems.iterator(); iterator.hasNext();) {
                WarehouseMovement value = iterator.next();
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
        context.update("tbvWarehouseTransferProc:frmProductView:dtbProductView");
    }

    /*
     * Bu metot okunan dosyayı veritabanına gönderip kayıtları aktarmak için
     * kullanılır.Aktarılmayan kayıtlar için bilgi mesajı kullanıcı tarafında
     * görüntülenir.
     */
    public void saveProduct() {
        String resultJson = "";
        isAll = false;
        errorList.clear();
        updateDataList.clear();
        errorQuantityList.clear();

        RequestContext context = RequestContext.getCurrentInstance();
        excelItemList.clear();
        for (WarehouseMovement warehouseMovement : tempExcelList) {
            if (warehouseMovement.getType() == 1) {
                excelItemList.add(warehouseMovement);
            }
        }
        resultJson = transferBetweenWarehouseService.jsonArrayForExcelUpload(excelItemList, exitWarehouse.getId(), entryWarehouse);
        excelItemList.clear();
        excelItemList.addAll(tempExcelList);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");
            listOfObjects = findAll("", 1);
            context.update("tbvWarehouseTransferProc:frmWarehouseTransferStockTab");

        } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
            JSONArray jsonArr = new JSONArray(resultJson);
            for (int m = 0; m < jsonArr.length(); m++) {
                ErrorItems item = new ErrorItems();
                item.setErrorCode(jsonArr.getJSONObject(m).getInt("errorcode"));
                item.getStock().setId(jsonArr.getJSONObject(m).getInt("stock_id"));
                item.setQuantity(jsonArr.getJSONObject(m).getBigDecimal("quantity"));
                item.getStock().setBarcode(jsonArr.getJSONObject(m).getString("barcode"));

                switch (item.getErrorCode()) {
                    case -1:
                        item.setErrorString(sessionBean.getLoc().getString("noproductsfoundforbarcodeinformation"));
                        errorList.add(item);
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("productnotfoundinwarehouse"));
                        errorList.add(item);
                        break;

                    case 1:

                        errorQuantityList.add(item);
                        break;

                    default:
                        break;
                }

            }

            context.execute("PF('dlg_productView').hide();");

            if (errorQuantityList.size() > 0) {
                boolean isThere = true;
                for (ErrorItems errItem : errorQuantityList) {
                    if (tempQuantityList.isEmpty()) {

                        WarehouseMovement obj = new WarehouseMovement();

                        obj.getStock().setId((errItem.getStock().getId()));
                        obj.setQuantity(errItem.getQuantity());

                        tempQuantityList.add(obj);

                    } else {
                        isThere = true;
                        for (WarehouseMovement warehouseMovement : tempQuantityList) {

                            if (errItem.getStock().getId() == warehouseMovement.getStock().getId()) {
                                warehouseMovement.setQuantity(errItem.getQuantity());
                                isThere = true;
                                break;
                            } else {
                                isThere = false;

                            }

                        }
                        if (!isThere) {
                            WarehouseMovement obj = new WarehouseMovement();
                            obj.setQuantity(errItem.getQuantity());
                            obj.getStock().setId(errItem.getStock().getId());
                            tempQuantityList.add(obj);
                        }
                    }
                }

                context.update("tbvWarehouseTransferProc:frmWarehouseTransferStockTab:dtbWarehouseMovement");

                if (!errorList.isEmpty()) {
                    context.update("dlg_productErrorView");
                    context.update("tbvWarehouseTransferProc:frmProductErrorView");
                    context.execute("PF('dlg_productErrorView').show();");
                    context.update("tbvWarehouseTransferProc:frmProductErrorView:dtbProductErrorView");

                    FacesMessage message = new FacesMessage();
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("grwProcessMessage");

                } else {
                    context.execute("PF('dlg_quantityfileupload').hide();");
                    sessionBean.createUpdateMessage(1);
                }
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("grwProcessMessage");
                context.update("tbvWarehouseTransferProc:frmProductErrorView");
                context.execute("PF('dlg_productErrorView').show();");
                context.update("tbvWarehouseTransferProc:frmProductErrorView:dtbProductErrorView");

            }

        }
    }

    @Override
    public void generalFilter() {
        int type = 0;
        if (processType == 1) {
            type = 0;
        } else if (processType == 2) {
            type = 1;
        }
        if (autoCompleteValue == null) {
            listOfObjects = findAll(" ", type);
        } else {
            gfWarehouseTransferMovementService.makeSearch(autoCompleteValue, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
            listOfObjects = gfWarehouseTransferMovementService.searchResult;
        }
    }

    @Override
    public LazyDataModel<WarehouseMovement> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
