/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.paymenttype.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.PaymentType;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.paymenttype.business.IPaymentTypeService;
import java.util.Arrays;
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
 * @author m.duzoylum
 */
@ManagedBean
@ViewScoped
public class PaymentTypeBean extends GeneralDefinitionBean<PaymentType> {

    private int processType;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{paymentTypeService}")
    private IPaymentTypeService paymentTypeService;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IPaymentTypeService getPaymentTypeService() {
        return paymentTypeService;
    }

    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------Payment Type Bean--------");
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{328, 329, 330}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new PaymentType();
        RequestContext.getCurrentInstance().execute("PF('dlg_paymenttypeProcess').show();");
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_paymenttypeProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;
        boolean check = false;

        //Ödeme tipinin aynı olmamasını kontrol ediyoruz
        for (PaymentType pt : listOfObjects) {
            if (pt.getId() != selectedObject.getId()) {
                if (pt.getEntegrationcode().compareTo(selectedObject.getEntegrationcode()) == 0) {
                    check = true;
                }
            }
        }

        if (check) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesameintegrationcodecannotbeaddedagain")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (processType == 1) {
                result = paymentTypeService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                    RequestContext.getCurrentInstance().execute("PF('dlg_paymenttypeProcess').hide();");
                }
            } else if (processType == 2) {
                result = paymentTypeService.update(selectedObject);
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_paymenttypeProcess').hide();");
                }
            }
            if (result > 0) {
                RequestContext.getCurrentInstance().update("frmPaymenttype:dtbPaymenttype");
                RequestContext.getCurrentInstance().execute("PF('paymenttypePF').filter();");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void delete() {
        int result = 0;
        result = paymentTypeService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_paymenttypeProcess').hide();");
            context.update("frmPaymenttype:dtbPaymenttype");
            context.execute("PF('paymenttypePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<PaymentType> findall() {
        return paymentTypeService.listofPayment(sessionBean.getUser().getLastBranch().getId());
    }

}
