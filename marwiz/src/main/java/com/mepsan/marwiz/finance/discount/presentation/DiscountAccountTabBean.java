/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:15:42
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.finance.discount.business.IDiscountAccountTabService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.IHistoryService;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class DiscountAccountTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{discountAccountTabService}")
    private IDiscountAccountTabService discountAccountTabService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{historyService}")
    public IHistoryService historyService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDiscountAccountTabService(IDiscountAccountTabService discountAccountTabService) {
        this.discountAccountTabService = discountAccountTabService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    private DiscountAccountConnection selectedObjectAccount;

    private Discount selectedDiscount;
    private List<DiscountAccountConnection> listOfObjectAccount;

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    private int processType;

    private List<History> listOfHistoryObjects;
    private String createdPerson;
    private String createdDate;

    public Discount getSelectedDiscount() {
        return selectedDiscount;
    }

    public void setSelectedDiscount(Discount selectedDiscount) {
        this.selectedDiscount = selectedDiscount;
    }

    public DiscountAccountConnection getSelectedObjectAccount() {
        return selectedObjectAccount;
    }

    public void setSelectedObjectAccount(DiscountAccountConnection selectedObjectAccount) {
        this.selectedObjectAccount = selectedObjectAccount;
    }

    public List<DiscountAccountConnection> getListOfObjectAccount() {
        return listOfObjectAccount;
    }

    public void setListOfObjectAccount(List<DiscountAccountConnection> listOfObjectAccount) {
        this.listOfObjectAccount = listOfObjectAccount;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @PostConstruct
    public void init() {
        System.out.println("----DiscountAccountTabBean");
        if (sessionBean.parameter instanceof Discount) {
            selectedDiscount = (Discount) sessionBean.parameter;
            selectedObjectAccount = new DiscountAccountConnection();
            listOfObjectAccount = discountAccountTabService.listofDiscountAccount(selectedDiscount, " AND dac.account_id > 0 ");
        }
        
        setListBtn(sessionBean.checkAuthority(new int[]{101, 103, 104}, 0));
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void createDialog(int type) {

        processType = type;
        listOfHistoryObjects = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedDiscount.isIsAllCustomer()) {
            if (processType == 1) { //ekle
                selectedObjectAccount = new DiscountAccountConnection();

            } else if (processType == 2) {

            }
            context.execute("PF('dlg_CustomerProcess').show()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("youdontselectcustomerbecauseofallcustomerissigned")));
            RequestContext.getCurrentInstance().update("tbvDiscount:frmCustomerTab:grwDiscountCustomerTab");
        }

    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObjectAccount.setAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmCustomerProcess:txtAccountName");
        }
    }

    public void save() {
        int result = 0;
        selectedObjectAccount.setDiscount(selectedDiscount);
        if (processType == 1) {
            result = discountAccountTabService.create(selectedObjectAccount);
            if (result > 0) {
                selectedObjectAccount.setId(result);
                listOfObjectAccount.add(selectedObjectAccount);
            }
        } else {
            result = discountAccountTabService.update(selectedObjectAccount);
        }
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_CustomerProcess').hide();");
            context.update("tbvDiscount:frmCustomerTab:dtbCustomerTab");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void testBeforeDelete() {
        int result = 0;
        result = discountAccountTabService.testBeforeDelete(selectedObjectAccount);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmCustomerProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("campaigndetailslinkedtothiscampaigncannotbedeletedbecauseithasacurrentorcurrentcategory")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = discountAccountTabService.delete(selectedObjectAccount);
        if (result > 0) {
            listOfObjectAccount.remove(selectedObjectAccount);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_CustomerProcess').hide();");
            context.update("tbvDiscount:frmCustomerTab:dtbCustomerTab");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void goToHistory() {
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", selectedObjectAccount.getId(), "finance.discount_account_con", 0);
        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObjectAccount.getDateCreated());
        try {
            createdPerson = selectedObjectAccount.getUserCreated().getFullName() + " - " + selectedObjectAccount.getUserCreated().getUsername();

        } catch (Exception e) {
        }
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");

    }

}
