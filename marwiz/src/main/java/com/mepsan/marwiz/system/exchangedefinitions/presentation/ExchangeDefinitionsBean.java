/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.system.exchangedefinitions.business.IExchangeDefinitionsService;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import static org.primefaces.component.focus.Focus.PropertyKeys.context;
import org.primefaces.context.RequestContext;

/**
 *
 * @author sinem.arslan
 */
@ManagedBean
@ViewScoped
public class ExchangeDefinitionsBean extends GeneralDefinitionBean<Currency> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeDefinitionsService}")
    public IExchangeDefinitionsService exchangeDefinitionsService;

    private int processType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setExchangeDefinitionsService(IExchangeDefinitionsService exchangeDefinitionsService) {
        this.exchangeDefinitionsService = exchangeDefinitionsService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = findall();
        setListBtn(sessionBean.checkAuthority(new int[]{342}, 0));
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        if (selectedObject.getLimitUp() != null && selectedObject.getLimitUp().compareTo(BigDecimal.ZERO) == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    sessionBean.loc.getString("error"),
                    sessionBean.loc.getString("quantitycannnotbezero"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            int result = 0;
            RequestContext context = RequestContext.getCurrentInstance();

            result = exchangeDefinitionsService.update(selectedObject);

            if (result
                    > 0) {
                context.execute("PF('dlg_exchangedefinitionproc').hide();");
                context.execute("PF('exchangeDefinitionPF').filter();");
                context.update("frmExchangeDefinitionProcess:dtbExchangeDefinition");
            }

            sessionBean.createUpdateMessage(result);
        }
    }

    @Override
    public List<Currency> findall() {
        return exchangeDefinitionsService.findAll();
    }

    /**
     * Bu metot güncelleştirme işlemi için dialog açar
     */
    public void update() {
        processType = 2;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_exchangedefinitionproc').show();");
    }
}
