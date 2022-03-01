/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
public class StockTakingUncountedStocksBean extends GeneralDefinitionBean<StockTakingItem> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{stockTakingItemService}")
    private IStockTakingItemService stockTakingItemService;

    private List<StockTakingItem> listOfSelectedObjects;
    private StockTaking stockTaking;
    private List<StockTakingItem> listOfItems;
    private boolean isAll;

    public List<StockTakingItem> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<StockTakingItem> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public void setStockTakingItemService(IStockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {

        stockTaking = new StockTaking();
        listOfItems = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    stockTaking = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }
        listOfObjects = new ArrayList<>();
        listOfSelectedObjects = new ArrayList<>();

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setIsAll() {
        if (isAll) {
            listOfSelectedObjects.addAll(listOfObjects);
        } else {
            listOfSelectedObjects.clear();
        }

    }

    @Override
    public List<StockTakingItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void openDialog() {
        listOfObjects = stockTakingItemService.findAllUncountedStocks(stockTaking);

        // sayılmayan ürün yoksa devam eder.
        if (listOfObjects.isEmpty()) {
            goon();
        } else { // sayılmayan ürün varsa sayılmayan ürünler dialoğunu açar.
            RequestContext.getCurrentInstance().update("frmUncountedStocks");
            setAutoCompleteValue(null);
            RequestContext.getCurrentInstance().execute("PF('uncountedStocksPF').filter();");
            RequestContext.getCurrentInstance().execute("PF('dlg_UncountedStocks').show();");
        }
    }

    public void goon() {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();

        //sayımı bitirme dialoğunu açar.
        StockTakingEmployeeBean stockTakingEmployeeBean = (StockTakingEmployeeBean) viewMap.get("stockTakingEmployeeBean");
        stockTakingEmployeeBean.openDialog();

//-----------sayımdan sonraki giriş çıkışları bulur.
//        StockTakingStockSaleControlBean stockTakingStockSaleControlBean = (StockTakingStockSaleControlBean) viewMap.get("stockTakingStockSaleControlBean");
//        stockTakingStockSaleControlBean.openDialog();
    }

    public void reset() {

        listOfItems.clear();
        boolean isReset = true ; // Sayılmayan ürünler dialogunda sıfırla butonuna tıklandı mı?

        //seçili olanların miktarını sıfır yaparak item tablosuna ekler.
        for (StockTakingItem sti : listOfSelectedObjects) {

            sti.setRealQuantity(BigDecimal.ZERO);
            sti.setStockTaking(stockTaking);
            listOfItems.add(sti);

        }

        int result = stockTakingItemService.processStockTakingItem(0, stockTaking, listOfItems , isReset);
        if (result > 0) {

            //  ekledikten sonra tabı günceller.  
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            StockTakingStockTabBean stockTakingStockTabBean = (StockTakingStockTabBean) viewMap.get("stockTakingStockTabBean");
            stockTakingStockTabBean.setListOfObjects(stockTakingStockTabBean.findAll("", stockTaking));
            RequestContext.getCurrentInstance().update("tbvStockTakingProc:frmStockTakingStockTab:dtbStockTakingItem");

            listOfSelectedObjects.clear();
            listOfObjects.clear();

            // sayılmayan stok kalmış mı diye tekrar bakar.
            openDialog();

        }

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

}
