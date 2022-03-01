/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 12:06:04
 */
package com.mepsan.marwiz.general.report.accountextract.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.accountextract.business.IAccountExtractService;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
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

@ManagedBean
@ViewScoped
public class AccountExtractBean extends GeneralReportBean<AccountExtract> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountExtractService}")
    public IAccountExtractService accountExtractService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    private Account account;
    private int count;
    String createWhere;
    private int findCount;
    private List<Account> listOfAccount;
    private AccountExtract selectedAccountExtract;
    private List<Account> tempList;
    private boolean isBack, isTemp, isDetail;
    private int pageId;
    private AccountExtract tempAccountExtract;
    private List<Categorization> listOfCategorization;
    private List<AccountExtract> listOfTotals;
    private String sortFieldForImport, sortOrderForImport;
    private List<Branch> listOfBranch;
    private List<Branch> selectedBranchList;
    private List<Branch> tempBranchList;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountExtractService(IAccountExtractService accountExtractService) {
        this.accountExtractService = accountExtractService;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public int getFindCount() {
        return findCount;
    }

    public void setFindCount(int findCount) {
        this.findCount = findCount;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public AccountExtract getSelectedAccountExtract() {
        return selectedAccountExtract;
    }

    public void setSelectedAccountExtract(AccountExtract selectedAccountExtract) {
        this.selectedAccountExtract = selectedAccountExtract;
    }

    public boolean isIsBack() {
        return isBack;
    }

    public void setIsBack(boolean isBack) {
        this.isBack = isBack;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public AccountExtract getTempAccountExtract() {
        return tempAccountExtract;
    }

    public void setTempAccountExtract(AccountExtract tempAccountExtract) {
        this.tempAccountExtract = tempAccountExtract;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public boolean isIsDetail() {
        return isDetail;
    }

    public void setIsDetail(boolean isDetail) {
        this.isDetail = isDetail;
    }

    public List<AccountExtract> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<AccountExtract> listOfTotals) {
        this.listOfTotals = listOfTotals;
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
        System.out.println("--------------------AccountExtractBean");
        account = new Account();
        findCount = 0;
        listOfAccount = new ArrayList<>();
        listOfCategorization = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        selectedObject = new AccountExtract();
        selectedAccountExtract = new AccountExtract();
        tempList = new ArrayList<>();
        pageId = marwiz.getPageIdOfGoToPage();
        tempAccountExtract = new AccountExtract();
        isDetail = false;
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        tempBranchList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        selectedObject.setEndDate(new Date());
        cal.add(Calendar.MONTH, -3);
        selectedObject.setBeginDate(cal.getTime());
        toogleList = Arrays.asList(true, true, true, true, true);

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof AccountExtract) {
                    selectedObject = ((AccountExtract) ((ArrayList) sessionBean.parameter).get(i));
                    isBack = true;
                    accountBookCheckboxFilterBean.isAll = false;
                    accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(((AccountExtract) ((ArrayList) sessionBean.parameter).get(i)).getAccountList().get(0));
                    accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));

                    listOfAccount.add(((AccountExtract) ((ArrayList) sessionBean.parameter).get(i)).getAccountList().get(0));
                    break;
                }
            }
        }
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
        findCount++;

        selectedObject.getAccountList().clear();
        selectedObject.getAccountList().addAll(listOfAccount);
        selectedObject.getCategorizationList().clear();
        selectedObject.getCategorizationList().addAll(listOfCategorization);
        selectedObject.getBranchList().clear();

        //detaydayken tekrar birden fazla şube ve cari seçerse isDetail false çeker.
        if (!(selectedObject.getAccountList().size() == 1 && selectedObject.getAccountList().get(0).getId() != 0 && selectedBranchList.size() == 1)) {
            isDetail = false;
        }

        if (isDetail) {
            selectedObject.getBranchList().add(selectedAccountExtract.getBranch());
        } else {
            if (selectedBranchList.isEmpty()) {
                selectedObject.getBranchList().addAll(listOfBranch);
            } else {
                selectedObject.getBranchList().addAll(selectedBranchList);
            }
        }

        createWhere = accountExtractService.createWhere(selectedObject, pageId);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmAccountExtractDatatable:dtbExtract");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);

        if (selectedBranchList.isEmpty() ? listOfBranch.size() > 1 && selectedObject.getAccountList().size() == 1 : selectedBranchList.size() > 1 && selectedObject.getAccountList().size() == 1) {
            isDetail = true;
            selectedObject.getBranchList().clear();
            selectedObject.getBranchList().addAll(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
            RequestContext.getCurrentInstance().update("frmMovementTab:dtbMovement");
        }

        // birden fazla şube seçimi olduğu için burası kaldırıldı.artık hep tıklayınca detay açılacak.
//        if (selectedObject.getAccountList().size() == 1 && selectedObject.getAccountList().get(0).getId() != 0) {
//
//            int result = accountExtractService.findAccountCount(selectedObject, pageId);
//            System.out.println("--findCategoryAccount---result-----" + result);
//            if (result == 1) {
//                isDetail = true;
//            } else {
//                isDetail = false;
//            }
//
//            List<Object> list = new ArrayList<>();
//            if (isDetail) {
//                selectedObject.setBranchSetting(selectedAccountExtract.getBranchSetting());
//            }
//            list.add(selectedObject);
//            sessionBean.setParameter(list);
//            System.out.println("---termdate----extract---" + selectedObject.getTermDate());
//            RequestContext.getCurrentInstance().update("frmMovementTab:dtbMovement");
//        } else {
//            isDetail = false;
//        }
        if (isDetail) {
            List<Object> list = new ArrayList<>();
            list.add(selectedObject);
            sessionBean.setParameter(list);
            System.out.println("---termdate----extract---" + selectedObject.getTermDate());
            RequestContext.getCurrentInstance().update("frmMovementTab:dtbMovement");
        }
        RequestContext.getCurrentInstance().update("frmAccountExtract:pnlWhere");
        RequestContext.getCurrentInstance().update("frmAccountExtract:pnlWhereBranch");
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            listOfAccount.clear();
            tempList.clear();
            isTemp = accountBookCheckboxFilterBean.isAll;
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
            tempList.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
            }

            find();
            RequestContext.getCurrentInstance().update("frmAccountExtract:pnlWhere");
            RequestContext.getCurrentInstance().update("pgrExtractDatatable");
            RequestContext.getCurrentInstance().update("frmAccountExtract:txtCustomer");
        } else {
            listOfCategorization.clear();
            if (categoryBookCheckboxFilterBean.isAll) {
                Categorization s = new Categorization(0);
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
                }
            } else if (!categoryBookCheckboxFilterBean.isAll) {
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                    if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                        categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                    }
                }
            }
            listOfCategorization.addAll(categoryBookCheckboxFilterBean.getListOfCategorization());
            if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
            }

            find();
            RequestContext.getCurrentInstance().update("frmAccountExtract:txtCategory");
            RequestContext.getCurrentInstance().update("pgrExtractDatatable");
        }

    }

    public void openDialog(int i) {

        if (i == 1) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfAccount.isEmpty()) {
                if (listOfAccount.get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
        } else {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }

            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
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
    public LazyDataModel<AccountExtract> findall(String where) {
        return new CentrowizLazyDataModel<AccountExtract>() {
            @Override
            public List<AccountExtract> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<AccountExtract> result = accountExtractService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject);
                listOfTotals = accountExtractService.totals(where, selectedObject);
                count = 0;
                for (AccountExtract total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);
                if (!result.isEmpty()) {
                    tempAccountExtract = result.get(0);
                }
                sortFieldForImport = sortField;
                sortOrderForImport = convertSortOrder(sortOrder);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void createPdf() {
        accountExtractService.exportPdf(createWhere, selectedObject, toogleList, pageId, listOfTotals, sortFieldForImport, sortOrderForImport);//);
    }

    public void createExcel() throws IOException {
        accountExtractService.exportExcel(createWhere, selectedObject, toogleList, pageId, listOfTotals, sortFieldForImport, sortOrderForImport);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(accountExtractService.exportPrinter(createWhere, selectedObject, toogleList, pageId, listOfTotals, sortFieldForImport, sortOrderForImport)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void seeAccountExtractDetail() {
        isDetail = true;
        if (!isBack) {
            isBack = true;
            tempBranchList.clear();
            tempBranchList.addAll(selectedBranchList);
        }

        selectedBranchList.clear();

        for (Branch b : listOfBranch) {
            if (b.getId() == selectedAccountExtract.getBranch().getId()) {
                selectedBranchList.add(b);
                break;
            }

        }

        changeBranch();

        accountBookCheckboxFilterBean.isAll = false;
        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
        accountBookCheckboxFilterBean.getTempSelectedDataList().add(selectedAccountExtract);
        accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
        selectedAccountExtract.setTermDate(selectedObject.getTermDate());
        selectedAccountExtract.setTermDateOpType(selectedObject.getTermDateOpType());
        listOfAccount.clear();
        listOfAccount.add(selectedAccountExtract);
        find();
        RequestContext.getCurrentInstance().update("frmAccountExtract:pnlWhere");
        RequestContext.getCurrentInstance().update("frmAccountExtract:txtCustomer");
        RequestContext.getCurrentInstance().update("pgrExtractDatatable");

    }

    public void goToBack() {
        isBack = false;
        isDetail = false;
        listOfAccount.clear();

        selectedBranchList.clear();
        selectedBranchList.addAll(tempBranchList);

        accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
        accountBookCheckboxFilterBean.isAll = isTemp;

        accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(tempList);

        if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
        }

        listOfAccount.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
        find();
        RequestContext.getCurrentInstance().update("frmAccountExtract:pnlWhere");
        RequestContext.getCurrentInstance().update("frmAccountExtract:txtCustomer");
        RequestContext.getCurrentInstance().update("pgrExtractDatatable");

    }

    public void termDateUpdate() {

        if (selectedObject.getTermDate() == null) {
            selectedObject.setTermDateOpType(0);
        }
    }
}
