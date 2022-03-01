/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 08:51:55
 */
package com.mepsan.marwiz.inventory.warehousereceipt.presentation;

import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.ITransferBetweenWarehouseService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseReceiptService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped

public class WarehouseReceiptProcessBean extends AuthenticationLists {

    private int processType;
    private WarehouseReceipt selectedObject;
    private List<Warehouse> listOfWarehouse;
    private List<Type> listOfType;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private boolean isDeleteButton;

    private boolean isSendCenter = false;
    private int activeIndex;
    private boolean isEntryMinStockLevel;
    private boolean isExitMaxStockLevel;
    private boolean isThere;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{warehouseReceiptService}")
    private IWarehouseReceiptService warehouseReceiptService;

    @ManagedProperty(value = "#{waybillService}")
    private IWaybillService waybillService;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{transferBetweenWarehouseService}")
    private ITransferBetweenWarehouseService transferBetweenWarehouseService;

    public void setWarehouseReceiptService(IWarehouseReceiptService warehouseReceiptService) {
        this.warehouseReceiptService = warehouseReceiptService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WarehouseReceipt getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WarehouseReceipt selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
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

    public List<CheckDelete> getControlDeleteList() {
        return controlDeleteList;
    }

    public void setControlDeleteList(List<CheckDelete> controlDeleteList) {
        this.controlDeleteList = controlDeleteList;
    }

    public int getRelatedRecordId() {
        return relatedRecordId;
    }

    public void setRelatedRecordId(int relatedRecordId) {
        this.relatedRecordId = relatedRecordId;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public boolean isIsDeleteButton() {
        return isDeleteButton;
    }

    public void setIsDeleteButton(boolean isDeleteButton) {
        this.isDeleteButton = isDeleteButton;
    }

    public boolean isIsSendCenter() {
        return isSendCenter;
    }

    public void setIsSendCenter(boolean isSendCenter) {
        this.isSendCenter = isSendCenter;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setTransferBetweenWarehouseService(ITransferBetweenWarehouseService transferBetweenWarehouseService) {
        this.transferBetweenWarehouseService = transferBetweenWarehouseService;
    }

    public boolean isIsEntryMinStockLevel() {
        return isEntryMinStockLevel;
    }

    public void setIsEntryMinStockLevel(boolean isEntryMinStockLevel) {
        this.isEntryMinStockLevel = isEntryMinStockLevel;
    }

    public boolean isIsExitMaxStockLevel() {
        return isExitMaxStockLevel;
    }

    public void setIsExitMaxStockLevel(boolean isExitMaxStockLevel) {
        this.isExitMaxStockLevel = isExitMaxStockLevel;
    }

    public boolean isIsThere() {
        return isThere;
    }

    public void setIsThere(boolean isThere) {
        this.isThere = isThere;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------WarehouseReceiptProcessBean----------");
        listOfWarehouse = new ArrayList<>();
        listOfType = new ArrayList<>();
        controlDeleteList = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof WarehouseReceipt) {
                    selectedObject = (WarehouseReceipt) ((ArrayList) sessionBean.parameter).get(i);
                    if (selectedObject.getId() == 0) {//ekleme
                        processType = 1;
                        Calendar c = Calendar.getInstance();
                        selectedObject.setProcessDate(c.getTime());
                        for (Type type : sessionBean.getTypes(8)) {
                            if (type.getId() == 6 || type.getId() == 9) {
                                listOfType.add(type);
                            }
                            if (type.getId() == 76) {
                                if (!selectedObject.isIsDirection()) {
                                    listOfType.add(type);
                                }
                            }
                        }
                        listOfWarehouse = warehouseService.selectListWarehouse(" AND iw.is_fuel=FALSE ");
                    } else {
                        processType = 2;
                        if (selectedObject.getType().getId() == 6 || selectedObject.getType().getId() == 9) {
                            for (Type type : sessionBean.getTypes(8)) {
                                if (type.getId() == 6 || type.getId() == 9) {
                                    listOfType.add(type);
                                }
                                if (type.getId() == 76) {
                                    if (!selectedObject.isIsDirection()) {
                                        listOfType.add(type);
                                    }
                                }
                            }
                        } else {
                            for (Type type : sessionBean.getTypes(8)) {
                                if (type.getId() == 76) { //tipi atıksa çıkış fişiyse ekle
                                    if (!selectedObject.isIsDirection()) {
                                        listOfType.add(type);
                                    }
                                } else {
                                    listOfType.add(type);
                                }
                            }
                        }
                        listOfWarehouse = warehouseService.selectListWarehouse("  ");
                    }
                    break;
                }
            }
        }
        setListBtn(sessionBean.checkAuthority(new int[]{32, 33, 344}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{9}, 1));
        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }
    }

    public void changeEntryExit() {
        if (!selectedObject.isIsDirection()) {
            for (Type type : sessionBean.getTypes(8)) {
                if (type.getId() == 76) { //tipi atıksa çıkış fişiyse ekle
                    listOfType.add(type);
                }
            }
        } else {
            for (Iterator<Type> iterator = listOfType.iterator(); iterator.hasNext();) {
                Type next = iterator.next();
                if (next.getId() == 76) {
                    iterator.remove();
                }
            }
        }
    }

    public void save() {
        if (sessionBean.isPeriodClosed(selectedObject.getProcessDate())) {
            int result = 0;
            if (processType == 1) {
                result = warehouseReceiptService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    isSendCenter = true;
                    List<Object> list = new ArrayList<>();
                    list.add(selectedObject);
                    marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceiptprocess.xhtml", list, 1, 40);
                }
            } else if (processType == 2) {

                result = warehouseReceiptService.update(selectedObject);
                if (result > 0) {
                    isSendCenter = true;
                    marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceipt.xhtml", null, 1, 39);
                }
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    /**
     * Bu metot sayfadan çıkıldığı anda tetiklenir. Merkez entegrasyonu var ise
     * ve değişiklik oldu ise gerekli fonksiyonu tetkiler
     */
    @PreDestroy
    public void destroy() {
        int result;
        if (selectedObject.getType().getId() == 76 && isSendCenter && sessionBean.getLastBranchSetting().isIsCentralIntegration()) {//merkeze gönderilecek ise ve merkez entegrasyonu var ise 
            result = warehouseReceiptService.sendWasteCenter(selectedObject);
        }
    }

    //Giriş depodaki stoktaki stok eksi bakiyeye düşebilir mi paremetresine göre stok bakiyesinin eksiye düşmesini engellemek amacıyla kontrol yapar.
    public void stockEntryLevelControl(WarehouseMovement obj) {

        if (selectedObject.isIsDirection()) {
                if (obj.getStock().getAvailableQuantity() != null && obj.getQuantity() != null) {
                    if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity()) == -1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        isEntryMinStockLevel = true;
                        isThere = true;
                    } else {
                        isEntryMinStockLevel = false;
                    }
                }
        }
    }

    //Çıkış depodaki stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde çıkış depo fişinin silinmesi engellemek için çalışır.
    public void stockExitLevelControl(WarehouseMovement obj) {
        
        if (!selectedObject.isIsDirection()) {    
                if (obj.getStock().getStockInfo().getMaxStockLevel() != null && obj.getQuantity() != null) {
                    BigDecimal exitAmount = BigDecimal.ZERO;
                    exitAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
                    if (exitAmount.compareTo(obj.getQuantity()) == -1) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nodeletionispossibleabovethemaximumstocklevel"));
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        isExitMaxStockLevel = true;
                        isThere = true;
                    } else {
                        isExitMaxStockLevel = false;
                    }
                }
        }
    }

    public void testBeforeDelete() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        WarehouseReceiptStockTabBean warehouseReceiptStockTabBean = (WarehouseReceiptStockTabBean) viewMap.get("warehouseReceiptStockTabBean");
        
        for (WarehouseMovement item : warehouseReceiptStockTabBean.getListOfObjects()) {
            if (selectedObject.isIsDirection() && !item.getStock().getStockInfo().isIsMinusStockLevel()) { // Stok kartında ürün eksiye düşebilir mi seçili değilse
                stockEntryLevelControl(item);
                if (isEntryMinStockLevel) {
                    break;
                }
            }

            if (!selectedObject.isIsDirection() && item.getStock().getStockInfo().getMaxStockLevel() != null) {
                stockExitLevelControl(item);
                if (isExitMaxStockLevel) {
                    break;
                }
            }
        }
        if (!isThere) {
            if (sessionBean.isPeriodClosed(selectedObject.getProcessDate())) {
                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";
                relatedRecord = "";
                controlDeleteList.clear();
                controlDeleteList = warehouseReceiptService.testBeforeDelete(selectedObject);
                if (!controlDeleteList.isEmpty()) {
                    if (controlDeleteList.get(0).getR_response() < 0) { //Var bağlı ise silme uyarı ver
                        isDeleteButton = false;
                        switch (controlDeleteList.get(0).getR_response()) {
                            case -101: //depolar arası transfere bağlı
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtotransferbetweenwarehouses");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyoudeleteitfromtransferbetweenwarehouses");
                                deleteControlMessage2 = sessionBean.getLoc().getString("receiptno") + " : ";
                                relatedRecord = controlDeleteList.get(0).getR_recordno() == null ? "-" : controlDeleteList.get(0).getR_recordno();
                                relatedRecordId = controlDeleteList.get(0).getR_record_id();
                                break;
                            case -102: //depo sayımına bağlı
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtostocktaking");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseyouopenthestatusofstocktaking");
                                deleteControlMessage2 = sessionBean.getLoc().getString("warehousestocktaking") + " : ";
                                relatedRecord = controlDeleteList.get(0).getR_recordno();
                                relatedRecordId = controlDeleteList.get(0).getR_record_id();
                                break;
                            case -103: // fiş satışına bağlı ise
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtosaleitcannotbedeleted");
                                deleteControlMessage1 = sessionBean.getLoc().getString("receiptno") + " : " + controlDeleteList.get(0).getR_recordno();
                                deleteControlMessage2 = "";
                                break;
                            case -104: // irsaliyeye bağlı ise
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtowaybill");
                                deleteControlMessage1 = sessionBean.getLoc().getString("pleaseuyoudeletethewaybill");
                                deleteControlMessage2 = sessionBean.getLoc().getString("documentnumber");
                                relatedRecord = controlDeleteList.get(0).getR_recordno();
                                relatedRecordId = controlDeleteList.get(0).getR_record_id();
                                break;
                            case -105: // akaryakıt vardiyasına bağlı ise
                                deleteControlMessage = sessionBean.getLoc().getString("thisrecordisrelatedtofuelshiftitcannotbedeleted");
                                deleteControlMessage1 = sessionBean.getLoc().getString("shiftno") + " : " + controlDeleteList.get(0).getR_recordno();
                                deleteControlMessage2 = "";
                                break;
                            default:
                                break;
                        }
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    } else {//Sil
                        deleteControlMessage = sessionBean.getLoc().getString("warehousereceiptdelete");
                        deleteControlMessage1 = sessionBean.getLoc().getString("areyousureyouwanttocontinue");
                        deleteControlMessage2 = "";
                        isDeleteButton = true;
                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfo");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfo').show();");
                    }
                }
            }
        }
    }

    public void delete() {
        int result = 0;
        int resultWaste = 0;
        result = warehouseReceiptService.delete(selectedObject);
        if (result > 0) {
            if (selectedObject.getType().getId() == 76) {
                resultWaste = warehouseReceiptService.deleteWasteInfo(selectedObject);
            }
            if (resultWaste > 0 || selectedObject.getType().getId() != 76) {
                isSendCenter = true;
                marwiz.goToPage("/pages/inventory/warehousereceipt/warehousereceipt.xhtml", null, 1, 39);
            }
        }
        sessionBean.createUpdateMessage(selectedObject.getType().getId() == 76 ? resultWaste : result);
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfo').hide();");
        context.execute("rcgoToRelatedRecord()");
    }

    public void goToRelatedRecord() {
        List<Object> list = new ArrayList<>();
        for (Object object : (ArrayList) sessionBean.parameter) {
            list.add(object);
        }
        if (controlDeleteList.get(0).getR_response() == -101) { // depo sayımına bağlı
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof WarehouseTransfer) {
                    list.remove(i);
                }
            }
            WarehouseTransfer warehouseTransfer = new WarehouseTransfer();
            warehouseTransfer.getWarehouseReceipt().setId(relatedRecordId);
            warehouseTransfer = transferBetweenWarehouseService.find(warehouseTransfer);
            list.add(warehouseTransfer);
            marwiz.goToPage("/pages/inventory/transferbetweenwarehouses/transferbetweenwarehousesprocess.xhtml", list, 1, 215);
        } else if (controlDeleteList.get(0).getR_response() == -102) { // depo sayımına bağlı
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof StockTaking) {
                    list.remove(i);
                }
            }
            StockTaking stockTaking = new StockTaking();
            stockTaking.setId(relatedRecordId);
            stockTaking = stockTakingService.find(stockTaking);
            list.add(stockTaking);
            marwiz.goToPage("/pages/inventory/stocktaking/stocktakingprocess.xhtml", list, 1, 54);
        } else if (controlDeleteList.get(0).getR_response() == -104) { //irsaliyeye bağlı ise
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Waybill) {
                    list.remove(i);
                }
            }
            Waybill waybill = new Waybill();
            waybill = waybillService.find(relatedRecordId);
            list.add(waybill);
            marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 1, 41);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }
    //Sap ye başarılı olarak gönderilmiş bir depo fişi güncellenmek istendiğinde çalışır.Log tablosunu günceller.

    public void openUpdate() {
        int result = 0;
        result = warehouseReceiptService.updateLogSap(selectedObject);
        if (result > 0) {
            selectedObject.setLogSapİsSend(false);
            RequestContext.getCurrentInstance().update("frmWarehouseReceiptProcess");
            RequestContext.getCurrentInstance().update("tbvWarehouseReceiptProc");
            RequestContext.getCurrentInstance().update("dlgWarehouseReceiptStockProc");

            sessionBean.createUpdateMessage(result);
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("couldnotopentoeditwarehousereceipt") + sessionBean.loc.getString("tryagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }
}
