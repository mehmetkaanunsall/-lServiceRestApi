/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class StockTakingMinusStockBean extends GeneralDefinitionBean<StockTakingItem> {

    private StockTaking stockTaking;

    private List<StockTakingItem> listOfMinusStocks;
    private List<StockTakingItem> listOfPositiveStocks;

    private List<StockTakingItem> tempList;

    private boolean isResetAll;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{stockTakingItemService}")
    private IStockTakingItemService stockTakingItemService;

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockTakingItemService(IStockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    public boolean isIsResetAll() {
        return isResetAll;
    }

    public void setIsResetAll(boolean isResetAll) {
        this.isResetAll = isResetAll;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---------StockTakingMinusStockBean---------");
        stockTaking = new StockTaking();
        listOfMinusStocks = new ArrayList<>();
        listOfPositiveStocks = new ArrayList<>();
        listOfObjects = new ArrayList<>();
        tempList = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    stockTaking = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void openDialog() {
        isResetAll = false;
        listOfObjects = stockTakingItemService.findAllMinusStocks(stockTaking);
        if (!listOfObjects.isEmpty()) {
            RequestContext.getCurrentInstance().update("dlgStockTakingMinusStock");
            setAutoCompleteValue(null);
            RequestContext.getCurrentInstance().execute("PF('stockTakingMinusStockPF').filter();");
            RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingMinusStock').show();");

        } else {
            // yoksa sayımı bitirme dialoğunu açar.

            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            StockTakingUncountedStocksBean stockTakingUncountedStocksBean = (StockTakingUncountedStocksBean) viewMap.get("stockTakingUncountedStocksBean");
            stockTakingUncountedStocksBean.openDialog();
            
//            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
//            StockTakingEmployeeBean stockTakingEmployeeBean = (StockTakingEmployeeBean) viewMap.get("stockTakingEmployeeBean");
//            stockTakingEmployeeBean.openDialog();

        }
    }

    @Override
    public void save() {
        int result = 0;
        listOfMinusStocks.clear();
        listOfPositiveStocks.clear();
        for (StockTakingItem listOfObject : listOfObjects) {
            if (listOfObject.getRealQuantity().compareTo(BigDecimal.ZERO) == -1) {
                listOfMinusStocks.add(listOfObject);
            } else {
                listOfPositiveStocks.add(listOfObject);
            }
        }

        result = stockTakingItemService.processStockTakingItem(1, stockTaking, listOfPositiveStocks ,false);

        if (result > 0) {


            //  güncelledikten sonra tabı günceller.  
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            StockTakingStockTabBean stockTakingStockTabBean = (StockTakingStockTabBean) viewMap.get("stockTakingStockTabBean");
            stockTakingStockTabBean.setListOfObjects(stockTakingStockTabBean.findAll("", stockTaking)); //DÜZELT
            RequestContext.getCurrentInstance().update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");

            if (!listOfMinusStocks.isEmpty()) {
                listOfObjects.clear();
                listOfObjects.addAll(listOfMinusStocks);
                RequestContext.getCurrentInstance().update("frmStockTakingMinusStock");
                setAutoCompleteValue(null);
                RequestContext.getCurrentInstance().execute("PF('stockTakingMinusStockPF').filter();");

            } else {
                RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingMinusStock').hide();");
                StockTakingUncountedStocksBean stockTakingUncountedStocksBean = (StockTakingUncountedStocksBean) viewMap.get("stockTakingUncountedStocksBean");
                stockTakingUncountedStocksBean.openDialog();
            }

        }

    }

    @Override
    public List<StockTakingItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void resetAll() {
        if (isResetAll) {

            tempList = new ArrayList<>();
            for (StockTakingItem s : listOfObjects) {
                StockTakingItem item = new StockTakingItem();
                item.setId(s.getId());
                item.getStock().setId(s.getStock().getId());
                item.getStock().setName(s.getStock().getName());
                item.getStock().setBarcode(s.getStock().getBarcode());
                item.getStock().setCode(s.getStock().getCode());
                item.getStock().setCenterProductCode(s.getStock().getCenterProductCode());
                item.setSystemQuantity(s.getSystemQuantity());
                item.setRealQuantity(s.getRealQuantity());
                item.setProcessDate(s.getProcessDate());
                tempList.add(item);
                s.setRealQuantity(BigDecimal.ZERO);
            }
            RequestContext.getCurrentInstance().update("frmStockTakingMinusStock");
            setAutoCompleteValue(null);
            RequestContext.getCurrentInstance().execute("PF('stockTakingMinusStockPF').filter();");
        } else {
            listOfObjects = new ArrayList<>();
            listOfObjects.addAll(tempList);
            RequestContext.getCurrentInstance().update("frmStockTakingMinusStock");
            setAutoCompleteValue(null);
            RequestContext.getCurrentInstance().execute("PF('stockTakingMinusStockPF').filter();");
        }
    }

}
