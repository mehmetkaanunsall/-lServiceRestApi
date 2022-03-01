/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stockpricerequest.presentation;

import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stockpricerequest.business.IStockPriceRequestService;
import com.mepsan.marwiz.service.price.business.ISendPriceChangeRequestService;
import java.math.BigDecimal;
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
public class StockPriceRequestBean extends GeneralDefinitionBean<StockPriceRequest> {

    private int processType;

    @ManagedProperty(value = "#{stockPriceRequestService}")
    private IStockPriceRequestService stockPriceRequestService;
    
    @ManagedProperty(value = "#{sendPriceChangeRequestService}")
    private ISendPriceChangeRequestService sendPriceChangeRequestService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;
    
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setStockPriceRequestService(IStockPriceRequestService stockPriceRequestService) {
        this.stockPriceRequestService = stockPriceRequestService;
    }

    public void setSendPriceChangeRequestService(ISendPriceChangeRequestService sendPriceChangeRequestService) {
        this.sendPriceChangeRequestService = sendPriceChangeRequestService;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
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
        System.out.println("-----------StockPriceRequestBean--------");
        selectedObject = new StockPriceRequest();
        listOfObjects = findall();
        
        setListBtn(sessionBean.checkAuthority(new int[]{44, 45}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new StockPriceRequest();
        selectedObject.getRequestCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        RequestContext.getCurrentInstance().execute("PF('dlg_StockRequest').show();");
    }

    public void update() {
        processType = 2;
    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {

            selectedObject.setStock(stockBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmStockProcess:txtStock");
            RequestContext.getCurrentInstance().update("frmStockProcess:txtAvailablePrice");
            RequestContext.getCurrentInstance().update("frmStockProcess:txtRecommendedSalePrice");
            
            stockBookFilterBean.setSelectedData(null);
        }

    }

    @Override
    public void save() {
        boolean isThere = false;
        for (StockPriceRequest listOfObject : listOfObjects) {
            if (listOfObject.getStock().getId() == selectedObject.getStock().getId() && listOfObject.getApproval() == 0) {
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("ithasthepricerequestwaitingforstock")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            }
        }
        
        if(!isThere){
            if(selectedObject.getRequestPrice().compareTo(BigDecimal.ZERO)==0){
                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("requestedsalepricecannotbezero")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
        
        if(!isThere){
            int result=0;
            if (processType == 1) {

                result = stockPriceRequestService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(0, selectedObject);
                    RequestContext.getCurrentInstance().update("frmStock:dtbStock");
                    if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                        sendPriceChangeRequestService.sendPriceChangeRequest(selectedObject.getId());
                    }
                }
                sessionBean.createUpdateMessage(result);

            }
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('stockPF').filter();");
                RequestContext.getCurrentInstance().execute("PF('dlg_StockRequest').hide();");
            }
        }
    }

    @Override
    public List<StockPriceRequest> findall() {
        return stockPriceRequestService.findall();
    }

}
