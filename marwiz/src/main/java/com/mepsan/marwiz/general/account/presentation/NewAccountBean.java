/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   11.10.2018 02:55:42
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.service.invoice.business.IInvoiceRequestService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class NewAccountBean  extends AuthenticationLists{

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    private AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{accountService}")
    public IAccountService accountService;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{invoiceRequestService}")
    private IInvoiceRequestService invoiceRequestService;

    private Account selectedObject;
    private int processType;

    public Account getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Account selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setInvoiceRequestService(IInvoiceRequestService invoiceRequestService) {
        this.invoiceRequestService = invoiceRequestService;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------------NewAccountBean-------(query yok)");
        processType = 1;
        
       setListBtn(sessionBean.checkAuthority(new int[]{56, 59}, 0));

    }

    /**
     * Insert işleminde dialogun açılmasını sağlayan fonksiyondur.
     */
    public void create() {
        selectedObject = new Account();
        if (accountBookFilterBean.getType() != null) {
            if (accountBookFilterBean.getType().equals("Personel")) {//Personeller sayfası ise
                selectedObject.getType().setId(5);
                selectedObject.setIsEmployee(true);
                selectedObject.setIsPerson(true);
            }
        }
        citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getCity());
        RequestContext.getCurrentInstance().execute("PF('dlg_accountProcess').show();");
    }

    /**
     * Hangi sayfadan geldiğine göre objeyi set ederek insert fonksiyonunu
     * çağıran fonksiyondur.
     */
    public void save() {
        int result = 0;

        result = accountService.create(selectedObject);
        if (result > 0) {
            selectedObject.setId(result);
            accountBookFilterBean.refresh();
            RequestContext.getCurrentInstance().execute("PF('dlg_accountProcess').hide();");

        }
        sessionBean.createUpdateMessage(result);

    }

    public void taxpayerİnquiryRequest() {
        Account obj = new Account();
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            List<Account> list = accountService.taxPayerİnquiryRequest(selectedObject);

            if (!list.isEmpty()) {
                obj = list.get(0);

                if (obj.getTaxpayertype_id() == 1) {
                    selectedObject.setTagInfo(obj.getTagInfo());
                    selectedObject.setTitle(obj.getTitle());
                    selectedObject.setTaxpayertype_id(obj.getTaxpayertype_id());

                } else {

                    selectedObject.setTaxpayertype_id(obj.getTaxpayertype_id());

                }
                int result = accountService.update(selectedObject);

                if (result > 0 && selectedObject.getTaxpayertype_id() == 1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else if (result > 0 && selectedObject.getTaxpayertype_id() != 2) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }

            }
            RequestContext.getCurrentInstance().update("frmNewAccount:pgrNewAccount");
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

            obj = accountService.taxPayerİnquiryRequestU(selectedObject);

            if (obj.getTaxpayertype_id() > 0) {
                selectedObject.setTaxpayertype_id(obj.getTaxpayertype_id());
                if (obj.getTaxpayertype_id() == 1) {
                    obj = accountService.requestAccountInfo(selectedObject);
                    selectedObject.setTaxNo(obj.getTaxNo());
                    selectedObject.setTitle(obj.getTitle());
                    selectedObject.setTagInfo(obj.getTagInfo());
                }

                int result = accountService.update(selectedObject);
                if (result > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sessionBean.loc.getString("failedtoupdatecurrentinformation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }
                RequestContext.getCurrentInstance().update("frmAccountProcess:pgrAccountProcess");

            }

        }

    }

}
