package com.mepsan.marwiz.general.report.deficitcreditreport.presentation;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
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
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.mepsan.marwiz.general.report.deficitcreditreport.business.IDeficitCreditService;
import com.mepsan.marwiz.system.branch.business.IBranchService;

/**
 * Bu Class
 *
 * @author Samet Dağ
 * @date 12.11.2018
 */
@ManagedBean
@ViewScoped
public class DeficitCreditBean extends GeneralReportBean<CreditReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{deficitCreditReportService}")
    private IDeficitCreditService deficitCreditReportService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private Account account;
    private int count;
    String createWhere;
    private Currency currency;

    private String totalMoney, paidMoney, remainingMoney;

    private List<Account> listOfAccount;

    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    
    private List<CreditReport> listOfTotal;
    
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getPaidMoney() {
        return paidMoney;
    }

    public void setPaidMoney(String paidMoney) {
        this.paidMoney = paidMoney;
    }

    public String getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(String remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDeficitCreditReportService(IDeficitCreditService deficitCreditReportService) {
        this.deficitCreditReportService = deficitCreditReportService;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
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

    public List<CreditReport> getListOfTotal() {
        return listOfTotal;
    }

    public void setListOfTotal(List<CreditReport> listOfTotal) {
        this.listOfTotal = listOfTotal;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("--------------------DeficitCreditReportBean");
        listOfTotal = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        account = new Account();
        selectedObject = new CreditReport();
        Calendar cal = Calendar.getInstance();
        selectedObject.setEndDate(new Date());
        cal.add(Calendar.MONTH, -1);
        selectedObject.setBeginDate(cal.getTime());
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);
        listOfBranch = branchService.findUserAuthorizeBranch();

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

        find();
    }

    public void changeBranch() {
        listOfAccount.clear();
        selectedObject.getAccountList().clear();
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
    }

    @Override
    public void find() {
        isFind = true;
        listOfTotal.clear();
        selectedObject.getAccountList().clear();
        selectedObject.getAccountList().addAll(listOfAccount);
        selectedObject.getBranchList().clear();
        if (selectedBranchList.isEmpty()) {
            selectedObject.getBranchList().addAll(listOfBranch);
        } else {
            selectedObject.getBranchList().addAll(selectedBranchList);
        }

        createWhere = deficitCreditReportService.createWhere(selectedObject);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmDeficitCreditDatatable:dtbDeficitCredit");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);

        if (selectedObject.getAccountList().size() == 1 && selectedObject.getAccountList().get(0).getId() != 0) {
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
        }

        RequestContext.getCurrentInstance().update("frmDeficitCredit:pnlDate");

        RequestContext.getCurrentInstance().update("pgrDeficitCreditDatatable");

    }

    public void openDialog() {
        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!listOfAccount.isEmpty()) {
            accountBookCheckboxFilterBean.isAll = listOfAccount.get(0).getId() == 0;
        }
        accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            listOfAccount.clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account a = new Account(0);
                    a.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, a);
                }
            } else if (!accountBookCheckboxFilterBean.isAll) {
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfAccount.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmDeficitCredit:txtCustomer");
        }

    }

    @Override
    public LazyDataModel<CreditReport> findall(String where) {
        return new CentrowizLazyDataModel<CreditReport>() {
            @Override
            public List<CreditReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<CreditReport> result = deficitCreditReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                listOfTotal = deficitCreditReportService.totals(where);
                
                int count = 0 ;
                
                for (CreditReport total : listOfTotal) {
                    count += total.getId();
                }
                
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                calculateTotal(); // Genel Toplamı verir

                return result;
            }
        };
    }

    
public void calculateTotal() {
        
        totalMoney = "";
        paidMoney = "";
        remainingMoney = "";

        int count = 0;
        for (CreditReport t : listOfTotal) {
            
            if(count == 1){
            totalMoney += " + " +String.valueOf(sessionBean.getNumberFormat().format(t.getMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0) ;
            paidMoney += " + " +String.valueOf(sessionBean.getNumberFormat().format(t.getPaidMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            remainingMoney += " + " +String.valueOf(sessionBean.getNumberFormat().format(t.getRemainingMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            }else{
            count = 1 ;
            totalMoney += " " +String.valueOf(sessionBean.getNumberFormat().format(t.getMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            paidMoney += " " +String.valueOf(sessionBean.getNumberFormat().format(t.getPaidMoney()))+ " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            remainingMoney += " " +String.valueOf(sessionBean.getNumberFormat().format(t.getRemainingMoney())) + " " + sessionBean.currencySignOrCode(t.getCurrency().getId(), 0);
            
            //String.valueOf(formatter.format(
            // sessionBean.getNumberFormat().format(fuelShift.getTotalMoney()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)
            }
            
        }
 

    }

    public void createPdf() {
        deficitCreditReportService.exportPdf(createWhere, toogleList, selectedBranchList, selectedObject, totalMoney, paidMoney, remainingMoney);
    }

    public void createExcel() throws IOException {
        deficitCreditReportService.exportExcel(createWhere, toogleList, selectedBranchList, selectedObject, totalMoney, paidMoney, remainingMoney);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(deficitCreditReportService.exportPrinter(createWhere, toogleList, selectedBranchList, selectedObject, totalMoney, paidMoney, remainingMoney)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
