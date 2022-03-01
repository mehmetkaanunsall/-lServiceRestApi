package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountBranchService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class AccountBranchTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{accountBranchService}")
    public IAccountBranchService accountBranchService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private AccountBranchCon selectedObject;
    private Account selectedAccount;
    private List<AccountBranchCon> listOfObjects;
    private int processType;
    private List<Branch> listBranch;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBranchService(IAccountBranchService accountBranchService) {
        this.accountBranchService = accountBranchService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public AccountBranchCon getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AccountBranchCon selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public List<AccountBranchCon> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<AccountBranchCon> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Branch> getListBranch() {
        return listBranch;
    }

    public void setListBranch(List<Branch> listBranch) {
        this.listBranch = listBranch;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------AccountBranchTabBean------");
        selectedObject = new AccountBranchCon();
        listBranch = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedAccount = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = findall();
                }
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {
            setListBtn(sessionBean.checkAuthority(new int[]{311, 312, 313}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{314, 315, 316}, 0));

        }

    }

    public void createDialog(int type) {
        processType = type;
        listBranch = branchService.findUserAuthorizeBranch();
        if (type == 1) {
            selectedObject = new AccountBranchCon();

        }
        selectedObject.setAccount(selectedAccount);
        RequestContext.getCurrentInstance().execute("PF('dlg_AccountBranch').show()");
    }

    public List<AccountBranchCon> findall() {
        return accountBranchService.findAccountBranchCon(selectedAccount);
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;

        for (AccountBranchCon con : listOfObjects) {
            if (con.getBranch().getId() == selectedObject.getBranch().getId() && con.getId() != selectedObject.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisbranchisavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        if (processType == 1) {

            result = accountBranchService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }

        } else {
            result = accountBranchService.update(selectedObject);
        }
        if (result > 0) {
            bringAll();
            context.execute("PF('dlg_AccountBranch').hide()");
            context.update("tbvAccountProc:frmAccountBranchTab:dtbAccountBranch");
        }
        sessionBean.createUpdateMessage(result);
    }

    public boolean renderedColumnValue(BigDecimal balance, int type) {
        if (type == 1) {

            if (balance.compareTo(BigDecimal.valueOf(0)) == -1) {
                return true;
            } else {
                return false;
            }

        } else if (type == 2) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void testBeforeDelete() {
        int result = 0;
        result = accountBranchService.testBeforeDelete(selectedObject);
        if (result > 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseaccounthasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().update("frmAccountBranchProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = accountBranchService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvAccountProc:frmAccountBranchTab:dtbAccountBranch");
            context.execute("PF('dlg_AccountBranch').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringAll() {
        for (Branch branch : listBranch) {
            if (branch.getId() == selectedObject.getBranch().getId()) {
                selectedObject.getBranch().setName(branch.getName());
                break;
            }
        }

    }

}
