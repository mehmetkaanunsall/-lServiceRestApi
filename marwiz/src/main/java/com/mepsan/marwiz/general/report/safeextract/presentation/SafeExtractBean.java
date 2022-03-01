/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 17:01:40
 */
package com.mepsan.marwiz.general.report.safeextract.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.financingdocument.business.IFinancingDocumentService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import com.mepsan.marwiz.general.report.safeextract.business.ISafeExtractService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@ViewScoped
public class SafeExtractBean extends GeneralReportBean<SafeMovement> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeExtractService}")
    public ISafeExtractService safeExtractService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{financingDocumentService}")
    public IFinancingDocumentService financingDocumentService;

    @ManagedProperty(value = "#{incomeExpenseService}")
    public IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private String createWhere;
    private List<Safe> listOfSafeForExtract;
    private List<Safe> listOfSelectedSafe;
    private List<SafeMovement> listOfSafeMovement;

    private boolean isBack;
    private SafeMovement selectedSafeMovement;
    private List<Safe> tempList;

    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private List<Branch> tempBranchList;

    private boolean isIncomeExpense;
    private String beanName;

    private HashMap<Integer, SafeMovement> subTotals;
    private String subTotalIncome, subTotalOutcome, subTotalBalance;

    public List<Safe> getListOfSafeForExtract() {
        return listOfSafeForExtract;
    }

    public void setListOfSafeForExtract(List<Safe> listOfSafeForExtract) {
        this.listOfSafeForExtract = listOfSafeForExtract;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeExtractService(ISafeExtractService safeExtractService) {
        this.safeExtractService = safeExtractService;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public List<SafeMovement> getListOfSafeMovement() {
        return listOfSafeMovement;
    }

    public void setListOfSafeMovement(List<SafeMovement> listOfSafeMovement) {
        this.listOfSafeMovement = listOfSafeMovement;
    }

    public boolean isIsBack() {
        return isBack;
    }

    public void setIsBack(boolean isBack) {
        this.isBack = isBack;
    }

    public SafeMovement getSelectedSafeMovement() {
        return selectedSafeMovement;
    }

    public void setSelectedSafeMovement(SafeMovement selectedSafeMovement) {
        this.selectedSafeMovement = selectedSafeMovement;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setFinancingDocumentService(IFinancingDocumentService financingDocumentService) {
        this.financingDocumentService = financingDocumentService;
    }

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
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

    public List<Safe> getListOfSelectedSafe() {
        return listOfSelectedSafe;
    }

    public void setListOfSelectedSafe(List<Safe> listOfSelectedSafe) {
        this.listOfSelectedSafe = listOfSelectedSafe;
    }

    public HashMap<Integer, SafeMovement> getSubTotals() {
        return subTotals;
    }

    public void setSubTotals(HashMap<Integer, SafeMovement> subTotals) {
        this.subTotals = subTotals;
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
        System.out.println("------SafeReportBean-------");
        selectedObject = new SafeMovement();
        listOfSafeMovement = new ArrayList<>();
        subTotals = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());

        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        tempBranchList = new ArrayList<>();

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

        listOfSafeForExtract = safeService.selectSafe(selectedBranchList);
        listOfSelectedSafe = new ArrayList<>();
        selectedSafeMovement = new SafeMovement();
        tempList = new ArrayList<>();

        toogleList = Arrays.asList(true, true, true, true, true);
        beanName = "safeExtractBean";

        if (sessionBean.parameter instanceof ArrayList) {

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof SafeMovement) {
                    selectedObject = ((SafeMovement) ((ArrayList) sessionBean.parameter).get(i));
                    isBack = true;
                    for (Safe b : listOfSafeForExtract) {
                        if (b.getId() == selectedObject.getSafe().getId()) {
                            listOfSelectedSafe.add(b);
                            break;
                        }

                    }
                    break;
                }
            }
        } else {
            if (listOfSafeForExtract.size() == 1) {
                listOfSelectedSafe.addAll(listOfSafeForExtract);
            }
        }

        find();
    }

    public void changeBranch() {
        listOfSelectedSafe.clear();
        if (selectedBranchList.isEmpty()) {
            listOfSafeForExtract = safeService.selectSafe(listOfBranch);
        } else {
            listOfSafeForExtract = safeService.selectSafe(selectedBranchList);
        }
    }

    @Override
    public void find() {
        isFind = true;

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSafeReportDatatableDetail:dtbSafeDetail");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        /*Tek Kasa Seçilirse Kasa İçerisindeki Tüm Hareketler toplanarak getirilir.*/
        if (listOfSelectedSafe.size() == 1 && selectedBranchList.size() == 1) {

            selectedObject.setSafe(listOfSelectedSafe.get(0));
            selectedObject.getListOfSafe().clear();
            selectedObject.getListOfSafe().add(listOfSelectedSafe.get(0));
            selectedObject.getListOfBranch().clear();
            selectedObject.getListOfBranch().addAll(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);

        } else if (selectedBranchList.isEmpty() ? listOfBranch.size() > 1 : selectedBranchList.size() > 1) {
            selectedObject.getListOfSafe().clear();
            if (listOfSelectedSafe.isEmpty()) {
                selectedObject.getListOfSafe().addAll(listOfSafeForExtract);
            } else {
                selectedObject.getListOfSafe().addAll(listOfSelectedSafe);
            }
            selectedObject.getListOfBranch().clear();
            selectedObject.getListOfBranch().addAll(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
        }
        RequestContext.getCurrentInstance().update("frmSafeReport:pngMovement");
        if (selectedBranchList.isEmpty()) {
            listOfSafeMovement = safeExtractService.findAll(safeExtractService.createWhere(listOfSelectedSafe, listOfBranch));
        } else {
            listOfSafeMovement = safeExtractService.findAll(safeExtractService.createWhere(listOfSelectedSafe, selectedBranchList));

        }
        if (!listOfSafeMovement.isEmpty()) {
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
    public LazyDataModel<SafeMovement> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createPdf() {
        safeExtractService.exportPdf(createWhere, listOfSafeMovement, listOfSelectedSafe, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance);

    }

    public void createExcel() throws IOException {
        safeExtractService.exportExcel(createWhere, listOfSafeMovement, listOfSelectedSafe, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance);

    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(safeExtractService.exportPrinter(createWhere, listOfSafeMovement, listOfSelectedSafe, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, toogleList, subTotalIncome, subTotalOutcome, subTotalBalance)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void seeSafeExtractDetail() {
        if (!isBack) {
            isBack = true;
            tempBranchList.clear();
            tempBranchList.addAll(selectedBranchList);
            tempList.clear();
            tempList.addAll(listOfSelectedSafe);
        }

        selectedBranchList.clear();
        for (Branch b : listOfBranch) {
            if (b.getId() == selectedSafeMovement.getBranch().getId()) {
                selectedBranchList.add(b);
                break;
            }

        }

        changeBranch();

        listOfSelectedSafe.clear();
        for (Safe b : listOfSafeForExtract) {
            if (b.getId() == selectedSafeMovement.getSafe().getId()) {
                listOfSelectedSafe.add(b);
                break;
            }

        }

        RequestContext.getCurrentInstance().update("frmSafeReport:safeReportDates");
        RequestContext.getCurrentInstance().update("frmSafeReport:slcSafe");
        RequestContext.getCurrentInstance().update("frmSafeReport:pnlWhereBranch");
        find();
        RequestContext.getCurrentInstance().update("pgrSafeReportDatatable");

    }

    public void goToBack() {
        isBack = false;

        selectedBranchList.clear();
        selectedBranchList.addAll(tempBranchList);

        changeBranch();

        listOfSelectedSafe.clear();

        listOfSelectedSafe.addAll(tempList);

        RequestContext.getCurrentInstance().update("frmSafeReport:safeReportDates");
        RequestContext.getCurrentInstance().update("frmSafeReport:slcSafe");
        RequestContext.getCurrentInstance().update("frmSafeReport:pnlWhereBranch");
        find();
        RequestContext.getCurrentInstance().update("pgrSafeReportDatatable");

    }

    public void calcSubTotals() {
        subTotalIncome = "";
        subTotalOutcome = "";
        subTotalBalance = "";

        subTotals.clear();

        for (SafeMovement u : listOfSafeMovement) {

            if (subTotals.containsKey(u.getSafe().getCurrency().getId())) {
                SafeMovement old = new SafeMovement();
                old.setTotalIncoming(BigDecimal.ZERO);
                old.setTotalOutcoming(BigDecimal.ZERO);
                old.setBalance(BigDecimal.ZERO);
                old.setTotalIncoming(subTotals.get(u.getSafe().getCurrency().getId()).getTotalIncoming().add(u.getTotalIncoming()));
                old.setTotalOutcoming(subTotals.get(u.getSafe().getCurrency().getId()).getTotalOutcoming().add(u.getTotalOutcoming()));
                old.setBalance(subTotals.get(u.getSafe().getCurrency().getId()).getBalance().add(u.getBalance()));
                subTotals.put(u.getSafe().getCurrency().getId(), old);
            } else {
                subTotals.put(u.getSafe().getCurrency().getId(), u);
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, SafeMovement> entry : subTotals.entrySet()) {
            if (temp == 0) {
                temp = 1;

                if (entry.getKey() != 0) {

                    subTotalIncome += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalIncoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    subTotalOutcome += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalOutcoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    subTotalBalance += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

                }
            } else if (entry.getKey() != 0) {

                subTotalIncome += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalIncoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                subTotalOutcome += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getTotalOutcoming())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                subTotalBalance += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

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
