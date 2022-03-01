/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:15:28
 */
package com.mepsan.marwiz.general.report.salessummaryreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.salessummaryreport.business.ISalesSummaryReportService;
import com.mepsan.marwiz.general.report.salessummaryreport.dao.SalesSummaryReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class SalesSummaryReportBean extends GeneralReportBean<SalesSummaryReport> {

    @ManagedProperty(value = "#{salesSummaryReportService}")
    public ISalesSummaryReportService salesSummaryReportService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    private String createWhere;
    private List<Stock> listOfStock;
    private Currency currency;
    private List<Categorization> listOfCategorization;
    private List<SalesSummaryReport> listOfTotals;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private int branchId;
    private boolean isThereListBranch;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;

    public void setSalesSummaryReportService(ISalesSummaryReportService salesSummaryReportService) {
        this.salesSummaryReportService = salesSummaryReportService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public List<SalesSummaryReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<SalesSummaryReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------SalesSummaryReportBean");
        listOfStock = new ArrayList<>();
        selectedObject = new SalesSummaryReport();
        listOfCategorization = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch(); // kullanıcının yetkili olduğu branch listesini çeker
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }

        selectedBranchList = new ArrayList<>();
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedBranchList.add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranchList.add(branchSetting);
                    break;
                }
            }
        }

        isThereListBranch = false;
        listOfTotals = new ArrayList<>();

        currency = new Currency();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        selectedObject.setBeginDate(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        selectedObject.setEndDate(calendar.getTime());
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            setCountToggle(0);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true);
            setCountToggle(1);
        }

        changeBranch();
        selectedObject.getAccount().setId(1);
        selectedObject.getAccount().setIsEmployee(false);
        selectedObject.getAccount().setName(sessionBean.getLoc().getString("retailsalecustomer"));       
    }

    @Override
    public void find() {
        isFind = true;
        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        createWhere = salesSummaryReportService.createWhere(selectedObject, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList,  isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSalesSummaryDatatable:dtbSalesSummary");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall(createWhere);
    }

    @Override
    public LazyDataModel<SalesSummaryReport> findall(String where) {
        return new CentrowizLazyDataModel<SalesSummaryReport>() {
            @Override
            public List<SalesSummaryReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<SalesSummaryReport> result = salesSummaryReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList, selectedObject);
                listOfTotals = salesSummaryReportService.totals(where, branchList, selectedObject);
                int count = 0;
                for (SalesSummaryReport total : listOfTotals) {
                    count = count + total.getStock().getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    /**
     * Bu metot branch değiştiği anda string olara branch id leri birleştirerek
     * where şartı oluşturur.
     */
    public void changeBranch() {
        branchList = "";
        if (selectedBranchList.size() == 1) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = selectedBranchList.get(0).getBranch().getId();
            selectedObject.setBranchSetting(selectedBranchList.get(0));
            isThereListBranch = false;

            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            listOfStock.clear();
            selectedObject.getStockList().clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

            if (selectedBranchList.get(0).isIsCentralIntegration() && selectedBranchList.get(0).getBranch().getConceptType() == 1) {
                isCentralSupplierIconView = true;
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(0);

            } else {
                toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true);
                setCountToggle(1);
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
            }
        } else {
            isThereListBranch = true;
            branchId = -1;

            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            listOfStock.clear();
            selectedObject.getStockList().clear();
            listOfAccount.clear();
            listOfCentralSupplier.clear();
            accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            int countCentralIntegration = 0;
            for (BranchSetting branchSetting : selectedBranchList) {
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
            if (countCentralIntegration > 0 || selectedBranchList.size() == 0) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(0);
                isCentralSupplierIconView = true;
            } else {
                isCentralSupplierIconView = false;
                isCentralSupplier = false;
                toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true);
                setCountToggle(1);
            }
        }
        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        selectedObject.getAccount().setName("");
         selectedObject.setAccount(new Account());

    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
            listOfStock.clear();
            if (stockBookCheckboxFilterBean.isAll) {
                Stock s = new Stock(0);
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Stock stock = new Stock(0);
                    stock.setName(sessionBean.loc.getString("all"));
                    stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
                }
            } else if (!stockBookCheckboxFilterBean.isAll) {
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmSalesSummary:txtStock");
        } else if (event.getComponent().getParent().getParent().getId().equals("frmCentralSupplierBookFilterCheckbox")) {
            listOfCentralSupplier.clear();
            if (centralSupplierBookCheckboxFilterBean.isAll) {
                CentralSupplier s = new CentralSupplier(0);
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    CentralSupplier centralSupplier = new CentralSupplier(0);
                    centralSupplier.setName(sessionBean.loc.getString("all"));
                    centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().add(0, centralSupplier);
                }
            } else if (!centralSupplierBookCheckboxFilterBean.isAll) {
                if (!centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().remove(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfCentralSupplier.addAll(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList());

            if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                centralSupplierBookCheckboxFilterBean.setSelectedCount(centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("centralsupplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmSalesSummary:txtCentralSupplier");
        } else if (event.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            listOfAccount.clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account account = new Account(0);
                    account.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, account);
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
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("supplier") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmSalesSummary:txtSupplier");
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

            RequestContext.getCurrentInstance().update("frmSalesSummary:txtCategory");
        }

    }

    public void updateAllInformation() {

        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account(0);
                account.setIsPerson(false);
                account.setName(sessionBean.loc.getString("all"));
                selectedObject.setAccount(account);
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("frmSalesSummary:txtCustomer");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        } else if (type == 2) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }

            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
        } else if (type == 3) {
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfCentralSupplier.isEmpty()) {
                if (listOfCentralSupplier.get(0).getId() == 0) {
                    centralSupplierBookCheckboxFilterBean.isAll = true;
                } else {
                    centralSupplierBookCheckboxFilterBean.isAll = false;
                }
            }
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfCentralSupplier);
        } else if (type == 4) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfAccount.isEmpty()) {
                if (listOfAccount.get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
        }
    }

    public void createPdf() {
        salesSummaryReportService.exportPdf(createWhere, selectedObject, toogleList, listOfTotals, selectedBranchList, branchList, isCentralSupplier);
    }

    public void createExcel() throws IOException {
        salesSummaryReportService.exportExcel(createWhere, selectedObject, toogleList, listOfTotals, selectedBranchList, branchList, isCentralSupplier);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(salesSummaryReportService.exportPrinter(createWhere, selectedObject, toogleList, listOfTotals, selectedBranchList, branchList, isCentralSupplier)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void centralOrLocalSupplier(int type) {
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        if (type == 0) {
            isCentralSupplier = false;
            selectedObject.getListOfCentralSupplier().clear();
        } else {
            isCentralSupplier = true;
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

}
