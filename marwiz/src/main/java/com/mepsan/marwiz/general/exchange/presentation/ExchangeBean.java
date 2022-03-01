/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.exchange.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.text.SimpleDateFormat;
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
 * @author Mepsan
 */
@ManagedBean
@ViewScoped
public class ExchangeBean extends GeneralDefinitionBean<Exchange> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeService}")
    private IExchangeService exchangeService;

    private List<Exchange> listOfExc;

    public List<Exchange> getListOfExc() {
        return listOfExc;
    }

    public void setListOfExc(List<Exchange> listOfExc) {
        this.listOfExc = listOfExc;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IExchangeService getExchangeService() {
        return exchangeService;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = findall();
    }

    @Override
    public void create() {
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Exchange> findall() {
        return exchangeService.findAll(sessionBean.getUser());
    }

    public void exchangeRate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Exchange obj = exchangeService.updateExchange();
        if (obj.getErrorCode() == 1) {
            listOfObjects = findall();
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("frmExchange:dtbExchange");
            sessionBean.createUpdateMessage(1);
        } else if (obj.getErrorCode() == -1) {
            sessionBean.createUpdateMessage(-1);
        } else if (obj.getErrorCode() == -2) {//tcmb den çekilen tarihle Aynı güne ait kur kaydı mevcut
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("theratesofthecurrentexchangeratedateonthetcmbsitehavealreadybeendrawn") + " " + sessionBean.loc.getString("currentexchangeratedate") + " : " + dateFormat.format(obj.getExchangeDate())));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("exchangeratebelogstotodaywaspulledalready")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

}
