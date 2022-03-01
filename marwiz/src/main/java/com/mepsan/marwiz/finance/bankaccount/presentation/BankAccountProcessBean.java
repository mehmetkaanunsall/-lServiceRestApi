/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:16:03
 */
package com.mepsan.marwiz.finance.bankaccount.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountMovementService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.general.common.BankBookFilterBean;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
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
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class BankAccountProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{bankBranchService}")
    private IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{bankAccountBranchService}")
    private IBankAccountBranchService bankAccountBranchService;

    @ManagedProperty(value = "#{bankBookFilterBean}")
    private BankBookFilterBean bankBookFilterBean;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{bankAccountMovementService}")
    private IBankAccountMovementService bankAccountMovementService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    private BankAccount selectedObject;
    private int processType, activeIndex;
    private List<BankBranch> listBankBranch;
    private int movesize;
    private List<BankAccount> listOfBankAccount;
    private BigDecimal remainingLimit;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public BankAccount getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(BankAccount selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<BankBranch> getListBankBranch() {
        return listBankBranch;
    }

    public void setListBankBranch(List<BankBranch> listBankBranch) {
        this.listBankBranch = listBankBranch;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public void setBankBookFilterBean(BankBookFilterBean bankBookFilterBean) {
        this.bankBookFilterBean = bankBookFilterBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public int getMovesize() {
        return movesize;
    }

    public void setMovesize(int movesize) {
        this.movesize = movesize;
    }

    public void setBankAccountMovementService(IBankAccountMovementService bankAccountMovementService) {
        this.bankAccountMovementService = bankAccountMovementService;
    }

    public void setBankAccountBranchService(IBankAccountBranchService bankAccountBranchService) {
        this.bankAccountBranchService = bankAccountBranchService;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public BigDecimal getRemainingLimit() {
        return remainingLimit;
    }

    public void setRemainingLimit(BigDecimal remainingLimit) {
        this.remainingLimit = remainingLimit;
    }

    @PostConstruct
    public void init() {
        System.out.println("----------------------BankAccountProcessBean");
        listBankBranch = new ArrayList<>();
        selectedObject = new BankAccount();
        selectedObject.getBankAccountBranchCon().setCommissionBankAccount(new BankAccount());
        listOfBankAccount = new ArrayList<>();
        listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", sessionBean.getUser().getLastBranch());
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof BankAccount) {
                    selectedObject = (BankAccount) ((ArrayList) sessionBean.parameter).get(i);
                    processType = 2;
                    calculateRemainingLimit();
                    listBankBranch = bankBranchService.selectBankBranchForBank(selectedObject.getBankBranch().getBank());
//                    movesize = bankAccountMovementService.controlMovement("", selectedObject, new Branch());
                    break;
                }
            }
        } else {
            processType = 1;
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            movesize = 0;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{111, 112}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{28, 70, 62}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    /**
     * Bu metot banka hesbaı kaydetmek için kullanılır.Tip 1 yeni hesap
     * ekler,tip 2 güncelleştirme yapar.
     */
    public void save() {
        boolean isCommission = false;
        boolean isReset = false;
        if (selectedObject.getType().getId() == 16 && selectedObject.getBankAccountBranchCon().getCommissionRate() != null
                && selectedObject.getBankAccountBranchCon().getCommissionRate().compareTo(BigDecimal.valueOf(0)) != 0) {
            if (selectedObject.getBankAccountBranchCon().getCommissionBankAccount().getId() == 0 || selectedObject.getBankAccountBranchCon().getCommissionIncomeExpense().getId() == 0) {
                isCommission = true;
            }

        } else if (selectedObject.getType().getId() == 16 && (selectedObject.getBankAccountBranchCon().getCommissionRate() == null
                || selectedObject.getBankAccountBranchCon().getCommissionRate().compareTo(BigDecimal.valueOf(0)) == 0)) {
            if (selectedObject.getBankAccountBranchCon().getCommissionBankAccount().getId() != 0 || selectedObject.getBankAccountBranchCon().getCommissionIncomeExpense().getId() != 0) {
                isReset = true;
            }
        }
        if (isReset) {
            selectedObject.getBankAccountBranchCon().setCommissionBankAccount(new BankAccount());
            selectedObject.getBankAccountBranchCon().setCommissionIncomeExpense(new IncomeExpense());
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:slcCommissionBankAccount");
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:slcIncomeExpense");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("commissionbankaccountandexpenseisresetedbecausecommissionrateisnotbeentered")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (isCommission) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("expenseandcommissionbankaccountmustbeenteredforcommission")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            int result = 0;
            if (processType == 1) {
                result = bankAccountService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    List<Object> list = new ArrayList<>();
                    list.add(selectedObject);
                    marwiz.goToPage("/pages/finance/bankaccount/bankaccountprocess.xhtml", list, 1, 20);
                }

            } else if (processType == 2) {

                result = bankAccountService.update(selectedObject);
                if (result > 0) {
                    marwiz.goToPage("/pages/finance/bankaccount/bankaccount.xhtml", null, 1, 7);
                }
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void updateAllInformation() {
        if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            selectedObject.getBankAccountBranchCon().setCommissionIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:slcIncomeExpense");

            if (selectedObject.getBankAccountBranchCon().getCommissionIncomeExpense().getId() == 0) {
                if (activeIndex == 28) {
                    RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmGridToolbar");
                }
            }
            incomeExpenseBookFilterBean.setSelectedData(null);
        } else {
            listBankBranch = bankBookFilterBean.getSelectedData().getListOfBranchs();
            if (listBankBranch.size() > 0) {
                selectedObject.setBankBranch(listBankBranch.get(0));
            } else {
                selectedObject.setBankBranch(new BankBranch());
            }
            selectedObject.getBankBranch().setBank(bankBookFilterBean.getSelectedData());

            RequestContext.getCurrentInstance().update("frmBankAccountProcess:txtBankName");
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:slcBranch");
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:txtBranchCode");
        }

    }

    /**
     * Bu metot seçili olan banka bilgisine göre şube kodunu combobax a set
     * etmek için kullanılmıştır.
     */
    public void updateBranchCode() {

        for (BankBranch bankBranch : listBankBranch) {
            if (bankBranch.getId() == selectedObject.getBankBranch().getId()) {
                selectedObject.getBankBranch().setCode(bankBranch.getCode());
            }
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void testBeforeDelete() {
        int result = 0;
        result = bankAccountBranchService.testBeforeDeleteBankAccount(selectedObject);
        if (result == 0) {
            RequestContext.getCurrentInstance().update("frmBankAccountProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else if (result == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebankhasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = bankAccountService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/finance/bankaccount/bankaccount.xhtml", null, 1, 7);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void changeType() {
        selectedObject.getBankAccountBranchCon().setCommissionBankAccount(new BankAccount());
        selectedObject.getBankAccountBranchCon().setCommissionIncomeExpense(new IncomeExpense());
        selectedObject.getBankAccountBranchCon().setCommissionRate(null);
        if (activeIndex == 28) {
            RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmGridToolbar");
        }
    }

    public void changeCommissionBankAccount() {
        if (selectedObject.getBankAccountBranchCon().getCommissionBankAccount().getId() == 0) {
            if (activeIndex == 28) {
                RequestContext.getCurrentInstance().update("tbvBankAccountProc:frmGridToolbar");
            }
        }
    }

    public void calculateRemainingLimit() {
        BigDecimal limit = BigDecimal.valueOf(0);
        if (selectedObject.getCreditCardLimit() != null && selectedObject.getBankAccountBranchCon().getBalance() != null) {
            limit = selectedObject.getCreditCardLimit().add(selectedObject.getBankAccountBranchCon().getBalance());
        }
        setRemainingLimit(limit);
    }

}
