/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockAlternativeUnitService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
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
public class StockAlternativeUnitsTabBean extends AuthenticationLists {

    private List<StockUnitConnection> stockUnitList;
    private StockUnitConnection selectedObject;
    private List<Unit> unitList;
    private Stock stock;
    private int processType;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockAlternativeUnitService}")
    public IStockAlternativeUnitService stockAlternativeUnitService;

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    public List<StockUnitConnection> getStockUnitList() {
        return stockUnitList;
    }

    public void setStockUnitList(List<StockUnitConnection> stockUnitList) {
        this.stockUnitList = stockUnitList;
    }

    public StockUnitConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StockUnitConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
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

    public void setStockAlternativeUnitService(IStockAlternativeUnitService stockAlternativeUnitService) {
        this.stockAlternativeUnitService = stockAlternativeUnitService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    @PostConstruct
    public void init() {

        System.out.println("----StockAlternativeUnitTabBean----");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    stock = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    stockUnitList = stockAlternativeUnitService.findAll(stock);
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{129, 130, 131}, 0));

    }

    /**
     * Alternatif birim eklemek ya da güncelemek için dialog açar.
     *
     * @param type
     */
    public void createDialog(int type) {

        processType = type;
        unitList = unitService.findAll();

        for (Iterator<Unit> iterator = unitList.iterator(); iterator.hasNext();) {
            Unit next = iterator.next();
            if (next.getId() == stock.getUnit().getId()) {
                iterator.remove();
            }
            for (StockUnitConnection stockUnitConnection : stockUnitList) {
                if (stockUnitConnection.getUnit().getId() == next.getId()) {
                    iterator.remove();
                }
            }
        }

        if (type == 1) { //ekle
            selectedObject = new StockUnitConnection();
            selectedObject.setStock(stock);
        } else {
            unitList.add(0, selectedObject.getUnit());

        }

        RequestContext.getCurrentInstance().execute("PF('dlg_alternativeunitsproc').show()");
    }

    /**
     * Kaydet butonuna basıldığında işlem tipine göre ekleme ya da güncelleme
     * yapar.
     */
    public void save() {

        if (selectedObject.getQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
            int result = 0;
            if (processType == 1) {
                selectedObject.setIsOtherBranch(true);
                result = stockAlternativeUnitService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    stockUnitList.add(selectedObject);
                }
            } else {
                result = stockAlternativeUnitService.update(selectedObject);
            }
            if (result > 0) {
                bringUnit();
                RequestContext.getCurrentInstance().execute("PF('dlg_alternativeunitsproc').hide()");
            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("pleaseenterbiggerthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    /**
     * Alternatif birimi kaydettikten sonra adını set ederek gridde görünmesini
     * sağlar.
     */
    public void bringUnit() {
        for (Unit u : unitList) {
            if (u.getId() == selectedObject.getUnit().getId()) {
                selectedObject.getUnit().setName(u.getName());
                selectedObject.getUnit().setUnitRounding(u.getUnitRounding());
            }
        }
    }

    public void delete() {
        int result = 0;
        result = stockAlternativeUnitService.delete(selectedObject);
        if (result > 0) {
            stockUnitList.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_alternativeunitsproc').hide();");
            context.update("tbvStokProc:frmAlternativeUnits:dtbAlternativeUnits");
        }
        sessionBean.createUpdateMessage(result);
    }
}
