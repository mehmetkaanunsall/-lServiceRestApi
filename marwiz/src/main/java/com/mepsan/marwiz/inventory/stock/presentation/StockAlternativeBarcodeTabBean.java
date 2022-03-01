/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeBarcodeService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
public class StockAlternativeBarcodeTabBean extends AuthenticationLists {

    private List<StockAlternativeBarcode> stockAlternativeBarcodeList;
    private StockAlternativeBarcode selectedObject;
    private Stock stock;
    private int processType;
    private BigDecimal oldQuantity;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockAlternativeBarcodeService}")
    public IStockAlternativeBarcodeService stockAlternativeBarcodeService;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    public List<StockAlternativeBarcode> getStockAlternativeBarcodeList() {
        return stockAlternativeBarcodeList;
    }

    public void setStockAlternativeBarcodeList(List<StockAlternativeBarcode> stockAlternativeBarcodeList) {
        this.stockAlternativeBarcodeList = stockAlternativeBarcodeList;
    }

    public StockAlternativeBarcode getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StockAlternativeBarcode selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockAlternativeBarcodeService(IStockAlternativeBarcodeService stockAlternativeBarcodeService) {
        this.stockAlternativeBarcodeService = stockAlternativeBarcodeService;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public BigDecimal getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(BigDecimal oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    @PostConstruct
    public void init() {

        System.out.println("----StockAlternativeBarcodeTabBean----");
        oldQuantity = BigDecimal.ZERO;

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    stock = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    stockAlternativeBarcodeList = stockAlternativeBarcodeService.findAll(stock);
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{132, 133, 134}, 0));

    }

    /**
     * Alternatif barkod eklemek ya da güncelemek için dialog açar.
     *
     * @param type
     */
    public void createDialog(int type) {

        processType = type;

        if (type == 1) { //ekle
            selectedObject = new StockAlternativeBarcode();
            selectedObject.setStock(stock);
        } else {
            oldQuantity = selectedObject.getQuantity();
            selectedObject.setStock(stock);

        }

        RequestContext.getCurrentInstance().execute("PF('dlg_alternativebarcodesproc').show()");
    }

    /**
     * Kaydet butonuna basıldığında işlem tipine göre ekleme ya da güncelleme
     * yapar.
     */
    public void save() {

        int result = 0;
        boolean isThere = false;

        if (selectedObject.getBarcode() != null) {
            int count = stockService.stockBarcodeControl(selectedObject);
            if (count > 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisbarcodeisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }

//        for (StockAlternativeBarcode stockAlternativeBarcode : stockAlternativeBarcodeList) {
//            if (stockAlternativeBarcode.getBarcode().toUpperCase().trim().equals(selectedObject.getBarcode().toUpperCase().trim()) && selectedObject.getId() != stockAlternativeBarcode.getId()) {
//                isThere = true;
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisnoisavailableinthesystem")));
//                RequestContext.getCurrentInstance().update("grwProcessMessage");
//                break;
//            }
//        }
        if (!isThere) {
            if (processType == 1) {
                if (selectedObject.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("enteredvaluemustbegreaterthanzero")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    selectedObject.setIsOtherBranch(true);
                    result = stockAlternativeBarcodeService.create(selectedObject);
                    sessionBean.createUpdateMessage(result);
                }
                if (result > 0) {
                    selectedObject.setId(result);
                    stockAlternativeBarcodeList.add(selectedObject);
                }
            } else {
                if (selectedObject.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("enteredvaluemustbegreaterthanzero")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    selectedObject.setQuantity(oldQuantity);
                } else {
                    result = stockAlternativeBarcodeService.update(selectedObject);
                    sessionBean.createUpdateMessage(result);
                }
            }
            if (result > 0) {
                RequestContext.getCurrentInstance().update("tbvStokProc:frmAlternativeBarcodes:dtbAlternativeBarcodes");
                RequestContext.getCurrentInstance().execute("PF('dlg_alternativebarcodesproc').hide()");
            }
        }
    }

    public void delete() {
        int result = 0;
        result = stockAlternativeBarcodeService.delete(selectedObject);
        if (result > 0) {
            stockAlternativeBarcodeList.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_alternativebarcodesproc').hide();");
            context.update("tbvStokProc:frmAlternativeBarcodes:dtbAlternativeBarcodes");
        }
        sessionBean.createUpdateMessage(result);
    }

}
