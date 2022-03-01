/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.salesnottransferredtotanı.business.ISalesNotTransferredToTanıService;
import com.mepsan.marwiz.system.salesnottransferredtotanı.dao.SalesNotTransferredToTanı;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author sinem.arslan
 */
@ManagedBean
@ViewScoped
public class SalesNotTransferredToTanıBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{salesNotTransferredToTanıService}")
    public ISalesNotTransferredToTanıService salesNotTransferredToTanıService;

    private SalesNotTransferredToTanı salesNotTransferredToTanı;
    private int result;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public ISalesNotTransferredToTanıService getSalesNotTransferredToTanıService() {
        return salesNotTransferredToTanıService;
    }

    public void setSalesNotTransferredToTanıService(ISalesNotTransferredToTanıService salesNotTransferredToTanıService) {
        this.salesNotTransferredToTanıService = salesNotTransferredToTanıService;
    }

    public SalesNotTransferredToTanı getSalesNotTransferredToTanı() {
        return salesNotTransferredToTanı;
    }

    public void setSalesNotTransferredToTanı(SalesNotTransferredToTanı salesNotTransferredToTanı) {
        this.salesNotTransferredToTanı = salesNotTransferredToTanı;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @PostConstruct
    public void init() {
        List<SalesNotTransferredToTanı> listOfSalesCount = listOfSalesCount();
        salesNotTransferredToTanı = new SalesNotTransferredToTanı();
        if (!listOfSalesCount.isEmpty()) {
            salesNotTransferredToTanı = listOfSalesCount.get(0);
        }

    }

    public List<SalesNotTransferredToTanı> listOfSalesCount() {
        return salesNotTransferredToTanıService.listOfSalesCount();

    }

    public void transferSales() {

        result = 0;
        result = salesNotTransferredToTanıService.transferSales();
        if (result == 1 || result == -1) {
            List<SalesNotTransferredToTanı> listOfSalesCount = listOfSalesCount();
            salesNotTransferredToTanı = new SalesNotTransferredToTanı();
            if (!listOfSalesCount.isEmpty()) {
                salesNotTransferredToTanı = listOfSalesCount.get(0);
            }
            RequestContext.getCurrentInstance().update("frmSalesNotTransferredToTanı");
            sessionBean.createUpdateMessage(1);
        } else {
             sessionBean.createUpdateMessage(result);
        }
    }
}
