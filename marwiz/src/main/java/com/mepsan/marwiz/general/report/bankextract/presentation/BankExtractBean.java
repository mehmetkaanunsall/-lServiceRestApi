/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 08.03.2018 15:44:44
 */
package com.mepsan.marwiz.general.report.bankextract.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.mepsan.marwiz.general.report.bankextract.business.IBankExtractService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.util.HashMap;

@ManagedBean
@ViewScoped
public class BankExtractBean extends GeneralReportBean<BankAccountMovement> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{bankExtractService}")
    public IBankExtractService bankExtractService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{incomeExpenseService}")
    public IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private Date beginDate, endDate;
    private String createWhere;
    private List<BankAccount> listOfBank;
    private List<BankAccount> listOfSelectedBank;

    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private List<Branch> tempBranchList;

    private boolean isBack;
    private BankAccountMovement selectedBankMovement;
    private List<BankAccount> tempList;

    private List<BankAccountMovement> listOfBankMovement;
    private int processType;

    private boolean isIncomeExpense;
    private String beanName;

    private HashMap<Integer, BankAccountMovement> subTotals;
    private String subTotalIncome, subTotalOutcome, subTotalBalance;

    public List<BankAccount> getListOfBank() {
        return listOfBank;
    }

    public void setListOfBank(List<BankAccount> listOfBank) {
        this.listOfBank = listOfBank;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankExtractService(IBankExtractService bankExtractService) {
        this.bankExtractService = bankExtractService;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isIsBack() {
        return isBack;
    }

    public void setIsBack(boolean isBack) {
        this.isBack = isBack;
    }

    public BankAccountMovement getSelectedBankMovement() {
        return selectedBankMovement;
    }

    public void setSelectedBankMovement(BankAccountMovement selectedBankMovement) {
        this.selectedBankMovement = selectedBankMovement;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public boolean isIsIncomeExpense() {
        return isIncomeExpense;
    }

    public void setIsIncomeExpense(boolean isIncomeExpense) {
        this.isIncomeExpense = isIncomeExpense;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public List<BankAccount> getListOfSelectedBank() {
        return listOfSelectedBank;
    }

    public void setListOfSelectedBank(List<BankAccount> listOfSelectedBank) {
        this.listOfSelectedBank = listOfSelectedBank;
    }

    public List<BankAccountMovement> getListOfBankMovement() {
        return listOfBankMovement;
    }

    public void setListOfBankMovement(List<BankAccountMovement> listOfBankMovement) {
        this.listOfBankMovement = listOfBankMovement;
    }

    public String getSubTotalIncome() {
        return subTotalIncome;
    }

    public void setSubTotalIncome(String subTotalIncome) {
        this.subTotalIncome = subTotalIncome;
    }

    public String getSubTotalOutcome() {
        return subTotalOutcome;
    }

    public void setSubTotalOutcome(String subTotalOutcome) {
        this.subTotalOutcome = subTotalOutcome;
    }

    public String getSubTotalBalance() {
        return subTotalBalance;
    }

    public void setSubTotalBalance(String subTotalBalance) {
        this.subTotalBalance = subTotalBalance;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<Branch> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<Branch> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------BankReportBean-------");
        selectedObject = new BankAccountMovement();
        selectedBankMovement = new BankAccountMovement();
        listOfBankMovement = new ArrayList<>();
        subTotals = new HashMap<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        tempBranchList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        beginDate = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (Branch branch : listOfBranch) {
                selectedBranchList.add(branch);
            }
        } else {
            for (Branch branch : listOfBranch) {
                if (branch.getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranchList.add(branch);
                    break;
                }
            }
        }

        listOfBank = bankAccountService.bankAccountForSelect(" ", selectedBranchList);
        listOfSelectedBank = new ArrayList<>();
        selectedBankMovement = new BankAccountMovement();
        tempList = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true);

        if (listOfBank.size() == 1) {
            listOfSelectedBank.addAll(listOfBank);
        }

        beanName = "bankExtractBean";

        find();
    }

    public void changeBranch() {
        listOfSelectedBank.clear();
        if (selectedBranchList.isEmpty()) {
            listOfBank = bankAccountService.bankAccountForSelect(" ", listOfBranch);
        } else {
            listOfBank = bankAccountService.bankAccountForSelect(" ", selectedBranchList);
        }
    }

    @Override
    public void find() {
        isFind = true;

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmBankReportDatatableDetail:dtbBankDetail");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        if (listOfSelectedBank.size() == 1 && selectedBranchList.size() == 1) {
            selectedObject.setBankAccount(listOfSelectedBank.get(0));
            selectedObject.getBranchList().clear();
            selectedObject.getBranchList().addAll(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
            RequestContext.getCurrentInstance().update("frmBankReport:pngMovement");
        } else if (selectedBranchList.isEmpty() ? listOfBranch.size() > 1 && listOfSelectedBank.size() == 1 : selectedBranchList.size() > 1 && listOfSelectedBank.size() == 1) {
            selectedObject.setBankAccount(listOfSelectedBank.get(0));
            selectedObject.getBranchList().clear();
            selectedObject.getBranchList().addAll(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
            RequestContext.getCurrentInstance().update("frmBankReport:pngMovement");
        }

        if (selectedBranchList.isEmpty()) {
            listOfBankMovement = bankExtractService.findAll(beginDate, endDate, bankExtractService.createWhere(listOfSelectedBank, listOfBranch));
        } else {
            listOfBankMovement = bankExtractService.findAll(beginDate, endDate, bankExtractService.createWhere(listOfSelectedBank, selectedBranchList));

        }
        if (!listOfBankMovement.isEmpty()) {
            calcSubTotals();
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<BankAccountMovement> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createPdf() {
        bankExtractService.exportPdf(createWhere, listOfBankMovement, beginDate, endDate, listOfSelectedBank, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance);
    }

    public void createExcel() throws IOException {
        bankExtractService.exportExcel(createWhere, listOfBankMovement, beginDate, endDate, listOfSelectedBank, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(bankExtractService.exportPrinter(createWhere, listOfBankMovement, beginDate, endDate, listOfSelectedBank, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void seeBankExtractDetail() {
        if (!isBack) {
            isBack = true;
            tempBranchList.clear();
            tempBranchList.addAll(selectedBranchList);
            tempList.clear();
            tempList.addAll(listOfSelectedBank);
        }

        selectedBranchList.clear();

        for (Branch b : listOfBranch) {
            if (b.getId() == selectedBankMovement.getBranch().getId()) {
                selectedBranchList.add(b);
                break;
            }

        }

        changeBranch();

        listOfSelectedBank.clear();
        for (BankAccount b : listOfBank) {
            if (b.getId() == selectedBankMovement.getBankAccount().getId()) {
                listOfSelectedBank.add(b);
                break;
            }

        }
        RequestContext.getCurrentInstance().update("frmBankReport:bankReportDates");
        RequestContext.getCurrentInstance().update("frmBankReport:slcBank");
        RequestContext.getCurrentInstance().update("frmBankReport:pnlWhereBranch");
        find();
        RequestContext.getCurrentInstance().update("pgrBankReportDatatable");

    }

    public void goToBack() {
        isBack = false;

        selectedBranchList.clear();
        selectedBranchList.addAll(tempBranchList);

        changeBranch();
        listOfSelectedBank.clear();

        listOfSelectedBank.addAll(tempList);

        RequestContext.getCurrentInstance().update("frmBankReport:bankReportDates");
        RequestContext.getCurrentInstance().update("frmBankReport:slcBank");
        RequestContext.getCurrentInstance().update("frmBankReport:pnlWhereBranch");
        find();
        RequestContext.getCurrentInstance().update("pgrBankReportDatatable");

    }

    public void calcSubTotals() {
        subTotalIncome = "";
        subTotalOutcome = "";
        subTotalBalance = "";

        subTotals.clear();

        for (BankAccountMovement u : listOfBankMovement) {

            if (subTotals.containsKey(u.getBankAccount().getCurrency().getId())) {
                BankAccountMovement old = new BankAccountMovement();
                old.setTotalIncoming(BigDecimal.ZERO);
                old.setTotalOutcoming(BigDecimal.ZERO);
                old.getBankAccount().setBalance(BigDecimal.ZERO);
                old.setTotalIncoming(subTotals.get(u.getBankAccount().getCurrency().getId()).getTotalIncoming().add(u.getTotalIncoming()));
                old.setTotalOutcoming(subTotals.get(u.getBankAccount().getCurrency().getId()).getTotalOutcoming().add(u.getTotalOutcoming()));
                old.getBankAccount().setBalance(subTotals.get(u.getBankAccount().getCurrency().getId()).getBankAccount().getBalance().add(u.getBankAccount().getBalance()));
                subTotals.put(u.getBankAccount().getCurrency().getId(), old);
            } else {
                subTotals.put(u.getBankAccount().getCurrency().getId(), u);
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, BankAccountMovement> entry : subTotals.entrySet()) {
            if (temp == 0) {
                temp = 1;

                if (entry.getKey() != 0) {

                    subTotalIncome += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalIncoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    subTotalOutcome += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalOutcoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    subTotalBalance += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBankAccount().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

                }
            } else if (entry.getKey() != 0) {

                subTotalIncome += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalIncoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                subTotalOutcome += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalOutcoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                subTotalBalance += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBankAccount().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

            }

        }
        if (subTotalIncome.isEmpty() || subTotalIncome.equals("")) {
            subTotalIncome = "0";
        }
        if (subTotalOutcome.isEmpty() || subTotalOutcome.equals("")) {
            subTotalOutcome = "0";
        }
        if (subTotalBalance.isEmpty() || subTotalBalance.equals("")) {
            subTotalBalance = "0";
        }
    }

}
