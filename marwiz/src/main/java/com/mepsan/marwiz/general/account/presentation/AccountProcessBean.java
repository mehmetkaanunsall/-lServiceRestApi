/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 03:23:03
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.service.invoice.business.IInvoiceRequestService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class AccountProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{accountService}")
    public IAccountService accountService;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{invoiceRequestService}")
    private IInvoiceRequestService invoiceRequestService;

    private Account selectedObject;
    private int processType, activeIndex;

    public void setInvoiceRequestService(IInvoiceRequestService invoiceRequestService) {
        this.invoiceRequestService = invoiceRequestService;
    }

    public Account getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Account selectedObject) {
        this.selectedObject = selectedObject;
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

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------------------AccountProcessBean");
        selectedObject = new Account();

        if (marwiz.getPageIdOfGoToPage() == 87) {

            selectedObject.getType().setId(5);
            selectedObject.setIsEmployee(true);
        } else {

            selectedObject.setIsEmployee(false);
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {//Cari İşlemleri sayfası için

            setListBtn(sessionBean.checkAuthority(new int[]{57, 58}, 0));
            setListTab(sessionBean.checkAuthority(new int[]{11, 71, 12, 13, 14, 15, 16, 66, 60, 17}, 1));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{60, 61}, 0));
            setListTab(sessionBean.checkAuthority(new int[]{18, 72, 19, 20, 21, 67, 61, 22}, 1));
        }

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedObject = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    processType = 2;

                    if (!getListTab().isEmpty()) {
                        activeIndex = getListTab().get(0);
                    }

                    citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getCity());
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Object) {
                    processType = 1;
                }
            }
        }
    }

    public void save() {
        int result = 0;
        boolean isLongName = false;
        if (marwiz.getPageIdOfGoToPage() == 87) {//Personeller sayfası ise
            selectedObject.setIsEmployee(true);
            selectedObject.setIsPerson(Boolean.TRUE);
            if (processType == 2) {
                int longValue = 0;
                longValue = accountService.controlCashierUser(selectedObject);
                if (longValue == 1) {
                    String namesurname = "";
                    namesurname = selectedObject.getOnlyAccountName()+ selectedObject.getTitle();
                    if (namesurname.length() > 24) {
                        isLongName = true;
                    }
                }
            }
        } else {
            selectedObject.setIsEmployee(false);
        }

        if (isLongName) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisnameandsurnamearelongforcashier")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            if (processType == 1) {
                result = accountService.create(selectedObject);

                if (result > 0) {
                    selectedObject.setUserCreated(sessionBean.getUser());
                    selectedObject.setDateCreated(new Date());
                    selectedObject.setId(result);
                    selectedObject.setName(selectedObject.getOnlyAccountName());
                    ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    list.add(selectedObject);
                    if (!selectedObject.isIsEmployee()) {
                        marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 1, 11);
                    } else {
                        marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 1, 87);
                    }
                }

            } else if (processType == 2) {

                result = accountService.update(selectedObject);
                if (result > 0) {
                    ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).size() - 1);
                    List<Object> list = new ArrayList<>();
                    list.addAll((ArrayList) sessionBean.parameter);
                    if (!selectedObject.isIsEmployee()) {
                        marwiz.goToPage("/pages/general/account/account.xhtml", null, 1, 1);
                    } else {
                        marwiz.goToPage("/pages/general/account/account.xhtml", null, 1, 86);
                    }

                }
            }

            if (result == -1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thiscurrentcodeisavailableinthesystempleaseenteradifferentcode")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    public void testBeforeDelete() {
        int result = 0;
        result = accountService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmAccountProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else if (result == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseaccounthasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (result == 2) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseaccounthasshiftpayment")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = accountService.delete(selectedObject);
        if (result > 0) {
            List<Object> list = new ArrayList<>();
            list.addAll((ArrayList) sessionBean.parameter);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Account) {
                    list.remove(list.get(i));
                }
            }
            if (!selectedObject.isIsEmployee()) {
                marwiz.goToPage("/pages/general/account/account.xhtml", list, 1, 1);
            } else {
                marwiz.goToPage("/pages/general/account/account.xhtml", list, 1, 86);
            }
        }
        sessionBean.createUpdateMessage(result);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public int empType(List<Type> listType) {
        return listType.get(listType.size() - 1).getId();
    }

    //Carinin mükellef tipini sorgular.
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
                if (result > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            }
            RequestContext.getCurrentInstance().update("frmAccountProcess:pgrAccountProcess");
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
