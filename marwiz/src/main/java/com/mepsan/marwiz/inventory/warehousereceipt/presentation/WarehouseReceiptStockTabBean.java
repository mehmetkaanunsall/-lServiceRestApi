/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 12:52:09
 */
package com.mepsan.marwiz.inventory.warehousereceipt.presentation;

import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeUnitService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.ITransferBetweenWarehouseService;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseMovementService;
import com.mepsan.marwiz.inventory.wastereason.business.IWasteReasonService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped

public class WarehouseReceiptStockTabBean extends GeneralDefinitionBean<WarehouseMovement> {

    private int processType;
    private WarehouseReceipt warehouseReceipt;

    private String deleteControlMessage, deleteControlMessage1, deleteControlMessage2, relatedRecord;
    List<CheckDelete> controlDeleteList;
    private int relatedRecordId;
    private List<WasteReason> wasteReasonList;
    private List<Currency> currencyList;

    private List<StockUnitConnection> listOfUnit;
    private boolean isMinStockLevel;
    private BigDecimal oldQuantity;
    private boolean isEntryMinStockLevel;
    private boolean isExitMaxStockLevel;
    private boolean isMaxStockLevel;

    @ManagedProperty(value = "#{warehouseMovementService}")
    private IWarehouseMovementService warehouseMovementService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{waybillService}")
    private IWaybillService waybillService;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{transferBetweenWarehouseService}")
    private ITransferBetweenWarehouseService transferBetweenWarehouseService;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{wasteReasonService}")
    private IWasteReasonService wasteReasonService;

    @ManagedProperty(value = "#{exchangeService}")
    private IExchangeService exchangeService;

    @ManagedProperty(value = "#{stockAlternativeUnitService}")
    private IStockAlternativeUnitService stockAlternativeUnitService;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WarehouseReceipt getWarehouseReceipt() {
        return warehouseReceipt;
    }

    public void setWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        this.warehouseReceipt = warehouseReceipt;
    }

    public void setWarehouseMovementService(IWarehouseMovementService warehouseMovementService) {
        this.warehouseMovementService = warehouseMovementService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
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

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public List<WasteReason> getWasteReasonList() {
        return wasteReasonList;
    }

    public void setWasteReasonList(List<WasteReason> wasteReasonList) {
        this.wasteReasonList = wasteReasonList;
    }

    public IWasteReasonService getWasteReasonService() {
        return wasteReasonService;
    }

    public void setWasteReasonService(IWasteReasonService wasteReasonService) {
        this.wasteReasonService = wasteReasonService;
    }

    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    public IExchangeService getExchangeService() {
        return exchangeService;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setTransferBetweenWarehouseService(ITransferBetweenWarehouseService transferBetweenWarehouseService) {
        this.transferBetweenWarehouseService = transferBetweenWarehouseService;
    }

    public List<StockUnitConnection> getListOfUnit() {
        return listOfUnit;
    }

    public void setListOfUnit(List<StockUnitConnection> listOfUnit) {
        this.listOfUnit = listOfUnit;
    }

    public IStockAlternativeUnitService getStockAlternativeUnitService() {
        return stockAlternativeUnitService;
    }

    public void setStockAlternativeUnitService(IStockAlternativeUnitService stockAlternativeUnitService) {
        this.stockAlternativeUnitService = stockAlternativeUnitService;
    }

    public boolean isIsMinStockLevel() {
        return isMinStockLevel;
    }

    public void setIsMinStockLevel(boolean isMinStockLevel) {
        this.isMinStockLevel = isMinStockLevel;
    }

    public BigDecimal getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(BigDecimal oldQuantity) {
        this.oldQuantity = oldQuantity;
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

    public boolean isIsMaxStockLevel() {
        return isMaxStockLevel;
    }

    public void setIsMaxStockLevel(boolean isMaxStockLevel) {
        this.isMaxStockLevel = isMaxStockLevel;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--------WarehouseReceiptStockTabBean----------");
        warehouseReceipt = new WarehouseReceipt();
        selectedObject = new WarehouseMovement();
        wasteReasonList = new ArrayList<>();
        currencyList = new ArrayList<>();
        listOfUnit = new ArrayList<>();
        wasteReasonList = wasteReasonService.findAll();
        currencyList = sessionBean.getCurrencies();
        isMinStockLevel = false;
        isMaxStockLevel = false;
        oldQuantity = BigDecimal.ZERO;
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof WarehouseReceipt) {
                    warehouseReceipt = (WarehouseReceipt) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }

        listOfObjects = findall();
        if (warehouseReceipt.getId() != 0 && warehouseReceipt.getType().getId() == 76) {
            for (WarehouseMovement whm : listOfObjects) {

                whm.setQuantity(whm.getWasteItemInfo().getAlternativeUnitQuantity());

            }

        }

        controlDeleteList = new ArrayList<>();
        setListBtn(sessionBean.checkAuthority(new int[]{34, 35, 36}, 0));
    }

    @Override
    public void create() {
        listOfUnit.clear();
        processType = 1;
        selectedObject = new WarehouseMovement();
        RequestContext.getCurrentInstance().update("dlgWarehouseReceiptStockProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_warehousereceiptstockproc').show();");

    }

    public void update() {

        if (warehouseReceipt.getType().getId() == 76) {
            listOfUnit = stockAlternativeUnitService.findStockUnitConnection(selectedObject.getStock(), sessionBean.getUser().getLastBranchSetting());
        }
        oldQuantity = selectedObject.getQuantity();
        processType = 2;
        RequestContext.getCurrentInstance().update("dlgWarehouseReceiptStockProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_warehousereceiptstockproc').show();");
    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {
            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            selectedObject.setQuantity(null);
            if (warehouseReceipt.getType().getId() == 76) {
                listOfUnit = stockAlternativeUnitService.findStockUnitConnection(selectedObject.getStock(), sessionBean.getUser().getLastBranchSetting());
                if (selectedObject.getStock().getStockInfo().getCurrentPurchasePrice() == null) {
                    selectedObject.getStock().getStockInfo().setCurrentPurchasePrice(BigDecimal.ZERO);
                }
                BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                bd = ((selectedObject.getStock().getTaxRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                bd = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().multiply(bd);
                bd = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().add(bd);
                selectedObject.getWasteItemInfo().setCurrentPurchasePrice(bd);
                selectedObject.getWasteItemInfo().setCurrency(selectedObject.getStock().getStockInfo().getCurrentPurchaseCurrency());
                selectedObject.getWasteItemInfo().setTaxRate(selectedObject.getStock().getTaxRate());
                selectedObject.getWasteItemInfo().setUnit(selectedObject.getStock().getUnit());
                
            }
            RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtQuantity");
            RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtStock");
            RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtUnit");
            if (warehouseReceipt.getType().getId() == 76) {
                RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtLastUnitPrice");
                RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:slcCurrency");
                RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtTaxRate");
                RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:slcUnit");

            }
            RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:focusQuantity");

            stockBookFilterBean.setSelectedData(null);
        }

    }

    @Override
    public void save() {

        if (sessionBean.isPeriodClosed(warehouseReceipt.getProcessDate())) {
            int resultWaste = 0;
            int result = 0;
            selectedObject.setWarehouseReceipt(warehouseReceipt);
            selectedObject.setWarehouse(warehouseReceipt.getWarehouse());
            if (warehouseReceipt.getType().getId() == 76) {
                selectedObject.getWasteItemInfo().setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getWasteItemInfo().getCurrency(), sessionBean.getUser().getLastBranch().getCurrency(), sessionBean.getUser()));
                selectedObject.getWasteItemInfo().setAlternativeUnitQuantity(selectedObject.getQuantity());
                for (StockUnitConnection unitCon : listOfUnit) {
                    if (selectedObject.getWasteItemInfo().getUnit().getId() == unitCon.getUnit().getId()) {
                        if (unitCon.getQuantity() != null) {
                            BigDecimal quantity = new BigDecimal(BigInteger.ZERO);
                            quantity = selectedObject.getQuantity().divide(unitCon.getQuantity(), 4, RoundingMode.HALF_EVEN);
                            selectedObject.setQuantity(quantity);
                        }
                    }
                }
            }
            if (!warehouseReceipt.isIsDirection() && (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || isMinStockLevel)) { //Stok Eksiye Düşebilir Mi? seçili değilse
                stockLevelControl(processType, selectedObject);
            }

            if (warehouseReceipt.isIsDirection() && (selectedObject.getStock().getStockInfo().getMaxStockLevel() != null)) { //Stokta max ürün seviyesi tanımlı ise max ürün seviyesi kontrolü yapar
                maxStockLevelControl(processType, selectedObject);
            }

            if (selectedObject.getQuantity().compareTo(BigDecimal.ZERO) == 0) { // Miktar sıfır olamaz kontrolü  
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("quantitycannnotbezero"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
            if (processType == 1) {
                if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                    result = warehouseMovementService.create(selectedObject);
                    if (result > 0) {

                        if (warehouseReceipt.getType().getId() == 76) {// Tip Atık ise connection tablosuna kayıt eklenir
                            if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                                resultWaste = warehouseMovementService.createWasteInfo(warehouseReceipt, selectedObject);
                            }
                        }
                        if (resultWaste > 0 || warehouseReceipt.getType().getId() != 76) {

                            listOfObjects = findall();
                            if (warehouseReceipt.getId() != 0 && warehouseReceipt.getType().getId() == 76) {
                                for (WarehouseMovement whm : listOfObjects) {
                                    whm.setQuantity(whm.getWasteItemInfo().getAlternativeUnitQuantity());
                                }

                            }
                        }
                    }

                }

            } else if (processType == 2) {

                if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                    result = warehouseMovementService.update(selectedObject);
                    if (result > 0) {
                        if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                            if (warehouseReceipt.getType().getId() == 76) {// Tip Atık ise connection tablosu güncellenir
                                resultWaste = warehouseMovementService.updateWasteInfo(selectedObject);
                            }
                        }
                        listOfObjects = findall();
                    }
                }

            }

            if ((selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                if (resultWaste > 0 || warehouseReceipt.getType().getId() != 76) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    WarehouseReceiptProcessBean warehouseReceiptProcessBean = (WarehouseReceiptProcessBean) viewMap.get("warehouseReceiptProcessBean");
                    warehouseReceiptProcessBean.setIsSendCenter(true);
                    RequestContext.getCurrentInstance().update("tbvWarehouseReceiptProc:frmWarehouseReceiptStockTab:dtbWarehouseMovement");

                    if (warehouseReceipt.getId() != 0 && warehouseReceipt.getType().getId() == 76) {
                        for (WarehouseMovement whm : listOfObjects) {
                            whm.setQuantity(whm.getWasteItemInfo().getAlternativeUnitQuantity());
                        }

                    }
                }
                RequestContext.getCurrentInstance().execute("PF('dlg_warehousereceiptstockproc').hide();");
                sessionBean.createUpdateMessage((warehouseReceipt.getType().getId() == 76) ? resultWaste : result);
            }

        }

    }

    //Giriş depoda, stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde alım yapılmasını engellemek için çalışır.
    public void maxStockLevelControl(int processType, WarehouseMovement obj) {

        if (processType == 1) {
            if (obj.getStock().getStockInfo().getBalance() != null && obj.getQuantity() != null) {

                BigDecimal purchaseAmount = BigDecimal.ZERO;
                purchaseAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
                if (purchaseAmount.compareTo(obj.getQuantity()) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("maximumstocklevelcannotcontinueprocessing"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMaxStockLevel = true;
                } else {
                    isMaxStockLevel = false;
                }
            }

        } else if (processType == 2) {
            if (obj.getStock().getStockInfo().getBalance() != null && obj.getQuantity() != null && oldQuantity != null) {

                BigDecimal prchAmount = BigDecimal.ZERO;
                prchAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());

                if (prchAmount.compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("maximumstocklevelcannotcontinueprocessing"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isMaxStockLevel = true;

                } else {
                    isMaxStockLevel = false;
                }
            }

        }
    }

//Stoktaki "Stok Eksi Bakiyeye Düşebilir Mi?" işaretli değilse eksi bakiye kontrolü için çalışır
    public void stockLevelControl(int processType, WarehouseMovement obj) {
        if (processType == 1) {
            BigDecimal quantity = new BigDecimal(BigInteger.ZERO);
            quantity = selectedObject.getQuantity();
            if (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel() && obj.getStock().getAvailableQuantity().compareTo(quantity) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isMinStockLevel = true;

            } else {
                isMinStockLevel = false;
            }

        } else if (processType == 2) {
            if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity().subtract(oldQuantity)) == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                isMinStockLevel = true;
                selectedObject.setQuantity(oldQuantity);

            } else {
                isMinStockLevel = false;
            }
        }
    }

    @Override
    public List<WarehouseMovement> findall() {
        return warehouseMovementService.findAll(warehouseReceipt);
    }

    //Giriş depodaki stoktaki stok eksi bakiyeye düşebilir mi paremetresine göre stok bakiyesinin eksiye düşmesini engellemek amacıyla kontrol yapar.
    public void stockEntryLevelControl(WarehouseMovement obj) {

        if (warehouseReceipt.isIsDirection()) {

            if (obj.getStock().getAvailableQuantity() != null && obj.getQuantity() != null) {
                if (obj.getStock().getAvailableQuantity().compareTo(obj.getQuantity()) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thetransactioncannotbecontinuedbecausethestockbalanceisnegative"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isEntryMinStockLevel = true;
                } else {
                    isEntryMinStockLevel = false;
                }
            }
        }
    }

    //Çıkış depo fişinde stokta max ürün seviyesi tanımlı ise max. ürün seviyesi üzerinde çıkış depo fişinin silinmesi engellemek için çalışır.
    public void stockExitLevelControl(WarehouseMovement obj) {

        if (!warehouseReceipt.isIsDirection()) {
            if (obj.getStock().getStockInfo().getMaxStockLevel() != null && obj.getQuantity() != null) {
                BigDecimal salesAmount = BigDecimal.ZERO;
                salesAmount = obj.getStock().getStockInfo().getMaxStockLevel().subtract(obj.getStock().getStockInfo().getBalance());
                if (salesAmount.compareTo(obj.getQuantity()) == -1) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nodeletionispossibleabovethemaximumstocklevel"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isExitMaxStockLevel = true;
                } else {
                    isExitMaxStockLevel = false;
                }
            }
        }
    }

    public void testBeforeDelete() {

        if (warehouseReceipt.isIsDirection() && (!selectedObject.getStock().getStockInfo().isIsMinusStockLevel())) { // Stok kartında ürün eksiye düşebilir mi seçili değilse
            stockEntryLevelControl(selectedObject);
        }

        if (!selectedObject.isIsDirection() && (selectedObject.getStock().getStockInfo().getMaxStockLevel() != null)) {
            stockExitLevelControl(selectedObject);
        }

        if ((warehouseReceipt.isIsDirection() && (selectedObject.getStock().getStockInfo().isIsMinusStockLevel() || !isEntryMinStockLevel)) || (!warehouseReceipt.isIsDirection() && (selectedObject.getStock().getStockInfo().getMaxStockLevel() == null || !isExitMaxStockLevel))) {
            if (sessionBean.isPeriodClosed(warehouseReceipt.getProcessDate())) {
                deleteControlMessage = "";
                deleteControlMessage1 = "";
                deleteControlMessage2 = "";
                relatedRecord = "";
                controlDeleteList.clear();
                controlDeleteList = warehouseMovementService.testBeforeDelete(warehouseReceipt);
                if (!controlDeleteList.isEmpty()) {

                    if (controlDeleteList.get(0).getR_response() < 0) { //Var bağlı ise silme uyarı ver
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
                            default:
                                break;
                        }

                        RequestContext.getCurrentInstance().update("dlgRelatedRecordInfoItem");
                        RequestContext.getCurrentInstance().execute("PF('dlg_RelatedRecordInfoItem').show();");
                    } else {//Sil
                        RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:dlgDelete");
                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                    }
                }
            }
        }
    }

    public void delete() {
        if (sessionBean.isPeriodClosed(warehouseReceipt.getProcessDate())) {

            int result = 0;
            int resultWaste = 0;
            result = warehouseMovementService.delete(selectedObject);
            if (result > 0) {

                if (warehouseReceipt.getType().getId() == 76) { //Tip Atık ise connection tablosundan kayıt silinir 
                    resultWaste = warehouseMovementService.deleteWasteInfo(selectedObject);
                }

                if (resultWaste > 0 || warehouseReceipt.getType().getId() != 76) {
                    listOfObjects.remove(selectedObject);
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    WarehouseReceiptProcessBean warehouseReceiptProcessBean = (WarehouseReceiptProcessBean) viewMap.get("warehouseReceiptProcessBean");
                    warehouseReceiptProcessBean.setIsSendCenter(true);
                    RequestContext.getCurrentInstance().update("tbvWarehouseReceiptProc:frmWarehouseReceiptStockTab:dtbWarehouseMovement");
                    RequestContext.getCurrentInstance().execute("PF('dlg_warehousereceiptstockproc').hide();");

                }

            }
            sessionBean.createUpdateMessage((warehouseReceipt.getType().getId() == 76) ? resultWaste : result);
        }
    }

    public void goToRelatedRecordBefore() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_RelatedRecordInfoItem').hide();");
        context.execute("PF('dlg_warehousereceiptstockproc').hide();");
        context.execute("rcgoToRelatedRecordItem()");

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

    public void totalMoneyUpdate() {
        if (warehouseReceipt.getType().getId() == 76) {
            if (selectedObject.getQuantity() != null) {
                selectedObject.getWasteItemInfo().setTotalMoney(selectedObject.getWasteItemInfo().getCurrentPurchasePrice().multiply(selectedObject.getQuantity()));
            }
        }
    }

    public void bringUnit() {
        for (StockUnitConnection unitCon : listOfUnit) {
            if (selectedObject.getWasteItemInfo().getUnit().getId() == unitCon.getUnit().getId()) {
                selectedObject.getWasteItemInfo().getUnit().setSortName(unitCon.getUnit().getSortName());
                selectedObject.getWasteItemInfo().getUnit().setUnitRounding(unitCon.getUnit().getUnitRounding());
                selectedObject.setQuantity(null);
                if (unitCon.getQuantity() != null) {
                    BigDecimal unitPrice = new BigDecimal(BigInteger.ZERO);
                    unitPrice = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().divide(unitCon.getQuantity(), 4, RoundingMode.HALF_EVEN);
                    BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                    bd = ((selectedObject.getStock().getTaxRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                    bd = unitPrice.multiply(bd);
                    bd = unitPrice.add(bd);
                    selectedObject.getWasteItemInfo().setCurrentPurchasePrice(bd);
                } else {
                    BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                    bd = ((selectedObject.getStock().getTaxRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                    bd = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().multiply(bd);
                    bd = selectedObject.getStock().getStockInfo().getCurrentPurchasePrice().add(bd);
                    selectedObject.getWasteItemInfo().setCurrentPurchasePrice(bd);

                }

                RequestContext.getCurrentInstance().update("frmWarehouseReceiptStockProcess:txtQuantity");

            }

        }
        totalMoneyUpdate();
    }

}
