/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.04.2020 02:34:53
 */
package com.mepsan.marwiz.finance.bankaccount.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountMovementService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountBranchCon;
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

@ManagedBean
@ViewScoped
public class BankAccountBranchTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{bankAccountBranchService}")
    public IBankAccountBranchService bankAccountBranchService;

    @ManagedProperty(value = "#{bankAccountMovementService}")
    private IBankAccountMovementService bankAccountMovementService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private BankAccountBranchCon selectedObject;
    private BankAccount selectedBankAccount;
    private List<BankAccountBranchCon> listOfObjects;
    private int processType;
    private List<Branch> listBranch;

    public BankAccountBranchCon getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(BankAccountBranchCon selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<BankAccountBranchCon> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<BankAccountBranchCon> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankAccountBranchService(IBankAccountBranchService bankAccountBranchService) {
        this.bankAccountBranchService = bankAccountBranchService;
    }

    public BankAccount getSelectedBankAccount() {
        return selectedBankAccount;
    }

    public void setSelectedBankAccount(BankAccount selectedBankAccount) {
        this.selectedBankAccount = selectedBankAccount;
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

    public void setBankAccountMovementService(IBankAccountMovementService bankAccountMovementService) {
        this.bankAccountMovementService = bankAccountMovementService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------BankAccountBranchTabBean------");
        selectedObject = new BankAccountBranchCon();
        listBranch = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof BankAccount) {
                    selectedBankAccount = (BankAccount) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = findall();
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{308, 309, 310}, 0));

    }

    public void createDialog(int type) {
        processType = type;
        listBranch = branchService.findUserAuthorizeBranch();
        if (type == 1) {
            selectedObject = new BankAccountBranchCon();
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_BankAccountBranch').show()");
    }

    public List<BankAccountBranchCon> findall() {
        return bankAccountBranchService.findBankAccountBranchCon(selectedBankAccount);
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        int result1 = 0;

        for (BankAccountBranchCon con : listOfObjects) {
            if (con.getBranch().getId() == selectedObject.getBranch().getId() && con.getId() != selectedObject.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisbranchisavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        if (processType == 1) {

            result1 = bankAccountBranchService.create(selectedObject, selectedBankAccount);
            if (result1 > 0) {
                result = bankAccountBranchService.createBeginningMovement(selectedObject, selectedBankAccount);
            }
            if (result > 0) {
                selectedObject.setId(result1);
                listOfObjects.add(selectedObject);
            }

        } else {
            result = bankAccountBranchService.update(selectedObject);
        }
        if (result > 0) {
            bringAll();
            context.execute("PF('dlg_BankAccountBranch').hide()");
            context.update("tbvBankAccountProc:frmBankAccountBranchTab:dtbBankAccountBranch");
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
        result = bankAccountMovementService.controlMovement("", selectedBankAccount, selectedObject.getBranch());
        if (result > 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebankhasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().update("frmBankAccountBranchProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = bankAccountBranchService.delete(selectedObject, selectedBankAccount);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvBankAccountProc:frmBankAccountBranchTab:dtbBankAccountBranch");
            context.execute("PF('dlg_BankAccountBranch').hide();");
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
