/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.einvoiceintegration.business.IStockEInvoiceUnitConService;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IncomingInvoicesItem;
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
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class StockEInvoiceUnitConBean extends GeneralDefinitionBean<StockEInvoiceUnitCon> {

    @ManagedProperty(value = "#{stockEInvoiceUnitConService}")
    private IStockEInvoiceUnitConService stockEInvoiceUnitConService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private IncomingInvoicesItem selectedInvoiceItem;
    private int processType;

    public IncomingInvoicesItem getSelectedInvoiceItem() {
        return selectedInvoiceItem;
    }

    public void setSelectedInvoiceItem(IncomingInvoicesItem selectedInvoiceItem) {
        this.selectedInvoiceItem = selectedInvoiceItem;
    }

    public IStockEInvoiceUnitConService getStockEInvoiceUnitConService() {
        return stockEInvoiceUnitConService;
    }

    public void setStockEInvoiceUnitConService(IStockEInvoiceUnitConService stockEInvoiceUnitConService) {
        this.stockEInvoiceUnitConService = stockEInvoiceUnitConService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @PostConstruct
    @Override
    public void init() {
        listOfObjects = new ArrayList();
        System.out.println("----Stock E Invoice Unit Con Bean----");
        selectedObject = new StockEInvoiceUnitCon();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomingEInvoicesBean incomingEInvoicesBean = (IncomingEInvoicesBean) viewMap.get("incomingEInvoicesBean");
        if (incomingEInvoicesBean != null) {

            selectedInvoiceItem = incomingEInvoicesBean.getSelectedInvoiceItem();
            selectedObject.setStockId(selectedInvoiceItem.getStock().getId());
            selectedObject.setStockIntegrationCode(selectedInvoiceItem.getOldUnitName());
        }

        if (selectedObject.getStockId() != 0) {
            listOfObjects = findall();
            if (!listOfObjects.isEmpty()) {

                selectedObject.setQuantity(listOfObjects.get(0).getQuantity());
                selectedObject.setId(listOfObjects.get(0).getId());
                incomingEInvoicesBean.getSelectedInvoiceItem().setStockEInvoiceUnitCon(selectedObject);
                processType = 2;
            } else {
                processType = 1;
            }
        }
    }

    @Override
    public void create() {
        int result = 0;
        result = stockEInvoiceUnitConService.create(selectedObject);
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_stockeinvoiceunitconproc').hide();");
            selectedObject.setId(result);
            updateStockUnitMatch(false);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void update() {
        int result = 0;
        result = stockEInvoiceUnitConService.update(selectedObject);
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_stockeinvoiceunitconproc').hide();");
            updateStockUnitMatch(false);
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public void save() {

        if (processType == 1) {
            create();
        } else if (processType == 2) {
            update();
        }
    }

    @Override
    public List<StockEInvoiceUnitCon> findall() {

        return stockEInvoiceUnitConService.findAll(selectedObject);
    }

    public void updateStockUnitMatch(boolean isDelete) {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomingEInvoicesBean incomingEInvoicesBean = (IncomingEInvoicesBean) viewMap.get("incomingEInvoicesBean");
        if (incomingEInvoicesBean != null) {
            incomingEInvoicesBean.getSelectedInvoiceItem().setStockEInvoiceUnitCon(selectedObject);
            incomingEInvoicesBean.updateStockUnitMatch();

        }

    }

    public void delete() {

        int result = 0;
        if (selectedObject.getId() > 0) {
            result = stockEInvoiceUnitConService.delete(selectedObject);
            sessionBean.createUpdateMessage(result);

            if (result > 0) {
                selectedObject.setId(0);
                selectedObject.setQuantity(BigDecimal.ZERO);
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                IncomingEInvoicesBean incomingEInvoicesBean = (IncomingEInvoicesBean) viewMap.get("incomingEInvoicesBean");
                if (incomingEInvoicesBean != null) {
                    incomingEInvoicesBean.getSelectedInvoiceItem().setStockEInvoiceUnitCon(selectedObject);
//
//                    for (IncomingInvoicesItem item : incomingEInvoicesBean.getListItem()) {
//                        if (item.getId() == incomingEInvoicesBean.getSelectedInvoiceItem().getId()) {
//                            item.getStock().setStockEInvoiceUnitCon(selectedObject);
//                            item.setStockEInvoiceUnitCon(selectedObject);
//                        }
//                    }

                    incomingEInvoicesBean.fuelResponseYes();
                    RequestContext context = RequestContext.getCurrentInstance();
                    RequestContext.getCurrentInstance().update("tbvEInvoice:frmEInvoiceStokTab:dtbStock");
                     context.execute("PF('dlg_stockeinvoiceunitconproc').hide();");
                                          context.execute("PF('dlg_StockUnit').hide();");

                    RequestContext.getCurrentInstance().update("dlg_EInvoiceItem");

                }

            }
        }

    }

}
