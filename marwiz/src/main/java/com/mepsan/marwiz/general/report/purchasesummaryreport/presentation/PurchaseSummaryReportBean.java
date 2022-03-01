/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:16:34
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.purchasesummaryreport.business.IPurchaseSummaryReportService;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.PurchaseSummaryReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
public class PurchaseSummaryReportBean extends GeneralReportBean<PurchaseSummaryReport> {

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{purchaseSummaryReportService}")
    public IPurchaseSummaryReportService purchaseSummaryReportService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<Stock> listOfStock;
    String createWhere;
    private PurchaseSummaryReport selectedPurchase;
    private LazyDataModel<PurchaseSummaryReport> listOfDetail;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private String branchListForDetail;
    private List<PurchaseSummaryReport> listOfTotals;
    private Map<Integer, PurchaseSummaryReport> currencyTotalsCollection;

    public void setPurchaseSummaryReportService(IPurchaseSummaryReportService purchaseSummaryReportService) {
        this.purchaseSummaryReportService = purchaseSummaryReportService;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public PurchaseSummaryReport getSelectedPurchase() {
        return selectedPurchase;
    }

    public void setSelectedPurchase(PurchaseSummaryReport selectedPurchase) {
        this.selectedPurchase = selectedPurchase;
    }

    public LazyDataModel<PurchaseSummaryReport> getListOfDetail() {
        return listOfDetail;
    }

    public void setListOfDetail(LazyDataModel<PurchaseSummaryReport> listOfDetail) {
        this.listOfDetail = listOfDetail;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
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

    public String getBranchListForDetail() {
        return branchListForDetail;
    }

    public void setBranchListForDetail(String branchListForDetail) {
        this.branchListForDetail = branchListForDetail;
    }

    public AccountBookCheckboxFilterBean getAccountBookCheckboxFilterBean() {
        return accountBookCheckboxFilterBean;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public CentralSupplierBookCheckboxFilterBean getCentralSupplierBookCheckboxFilterBean() {
        return centralSupplierBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

    public List<PurchaseSummaryReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<PurchaseSummaryReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public Map<Integer, PurchaseSummaryReport> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Integer, PurchaseSummaryReport> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------PurchaseSummaryReportBean");
        listOfStock = new ArrayList<>();
        selectedObject = new PurchaseSummaryReport();
        selectedPurchase = new PurchaseSummaryReport();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        currencyTotalsCollection = new HashMap<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false);
            toogleList_test = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, false, false);
            toogleList_test = (Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true));
        }
        setCountToggle(2);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        selectedObject.setBeginDate(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        selectedObject.setEndDate(calendar.getTime());

        listOfBranch = branchSettingService.findUserAuthorizeBranch();
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

        changeBranch();
    }

    public void changeBranch() {
        branchList = "";
        listOfStock.clear();
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        selectedObject.getStockList().clear();
        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfCentralSupplier().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        int countCentralIntegration = 0;

        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                    countCentralIntegration++;
                }
            }
        }

        if (countCentralIntegration > 0) {
            isCentralSupplierIconView = true;
            toogleList.set(6, true);
            toogleList_test.set(6, true);

        } else {
            isCentralSupplierIconView = false;
            isCentralSupplier = false;
            toogleList.set(6, false);
            toogleList_test.set(6, false);
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

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

        calendar.setTime(selectedObject.getBeginDate());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());

        if (!listOfTotals.isEmpty()) {
            listOfTotals.clear();
        }
        if (!currencyTotalsCollection.isEmpty()) {
            currencyTotalsCollection.clear();
        }

        createWhere = purchaseSummaryReportService.createWhere(selectedObject, isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmPurchaseSummaryDatatable:dtbPurchaseSummary");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(createWhere);

        branchListForDetail = branchList;
    }

    @Override
    public LazyDataModel<PurchaseSummaryReport> findall(String where) {
        return new CentrowizLazyDataModel<PurchaseSummaryReport>() {
            @Override
            public List<PurchaseSummaryReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<PurchaseSummaryReport> result = purchaseSummaryReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchList);
                listOfTotals = purchaseSummaryReportService.totals(where, branchList);
                int count = 0;

                if (listOfTotals.size() > 0) {
                    for (PurchaseSummaryReport total : listOfTotals) {
                        count = count + total.getId();
                    }

                    currencyTotalsCollection = calculateOverallTotal();
                }

                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
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
            RequestContext.getCurrentInstance().update("frmPurchaseSummary:txtStock");
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
            RequestContext.getCurrentInstance().update("frmPurchaseSummary:txtCentralSupplier");
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
            RequestContext.getCurrentInstance().update("frmPurchaseSummary:txtSupplier");
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
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfCentralSupplier.isEmpty()) {
                if (listOfCentralSupplier.get(0).getId() == 0) {
                    centralSupplierBookCheckboxFilterBean.isAll = true;
                } else {
                    centralSupplierBookCheckboxFilterBean.isAll = false;
                }
            }
            centralSupplierBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfCentralSupplier);
        } else if (type == 3) {
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
        purchaseSummaryReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier, listOfTotals, currencyTotalsCollection);
    }

    public void createExcel() throws IOException {
        purchaseSummaryReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier, listOfTotals, currencyTotalsCollection);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(purchaseSummaryReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedBranchList, isCentralSupplier, listOfTotals, currencyTotalsCollection)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public void openDetailDialog() {
        listOfDetail = findAllDetail(createWhere);
        RequestContext.getCurrentInstance().execute("PF('dlg_PurchaseDetail').show()");
    }

    public LazyDataModel<PurchaseSummaryReport> findAllDetail(String where) {
        return new CentrowizLazyDataModel<PurchaseSummaryReport>() {
            @Override
            public List<PurchaseSummaryReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<PurchaseSummaryReport> result = purchaseSummaryReportService.findAllDetail(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedPurchase, branchListForDetail);

                int count = purchaseSummaryReportService.countDetail(where, selectedPurchase, branchListForDetail);
                listOfDetail.setRowCount(count);

                return result;
            }
        };
    }

    public void centralOrLocalSupplier(int type) {
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        if (type == 0) {
            isCentralSupplier = false;
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

    public Map<Integer, PurchaseSummaryReport> calculateOverallTotal() {
        if (!currencyTotalsCollection.isEmpty()) {
            currencyTotalsCollection.clear();
        }
        for (PurchaseSummaryReport total : listOfTotals) {

            if (currencyTotalsCollection.containsKey(total.getCurrency().getId())) {

                PurchaseSummaryReport old = new PurchaseSummaryReport();
                old.setCurrency(currencyTotalsCollection.get(total.getCurrency().getId()).getCurrency());

                old.setTotalMoney(currencyTotalsCollection.get(total.getCurrency().getId()).getTotalMoney());
                old.setPremiumAmount(currencyTotalsCollection.get(total.getCurrency().getId()).getPremiumAmount());
                old.setQuantity(currencyTotalsCollection.get(total.getCurrency().getId()).getQuantity());
                old.setTotalMoneyByStock(currencyTotalsCollection.get(total.getCurrency().getId()).getTotalMoneyByStock());
                old.setTotalQuantityByStock(currencyTotalsCollection.get(total.getCurrency().getId()).getTotalQuantityByStock());

                old.setTotalMoney(old.getTotalMoney().add(total.getTotalMoney()));
                old.setPremiumAmount(old.getPremiumAmount().add(total.getPremiumAmount()));
                old.setQuantity(old.getQuantity().add(total.getQuantity()));
                old.setTotalMoneyByStock(old.getTotalMoneyByStock().add(total.getTotalMoneyByStock()));
                old.setTotalQuantityByStock(old.getTotalQuantityByStock().add(total.getTotalQuantityByStock()));

                currencyTotalsCollection.put(total.getCurrency().getId(), old);

            } else {

                PurchaseSummaryReport oldNew = new PurchaseSummaryReport();
                oldNew.setCurrency(total.getCurrency());
                oldNew.setTotalMoney(total.getTotalMoney());
                oldNew.setPremiumAmount(total.getPremiumAmount());
                oldNew.setQuantity(total.getQuantity());
                oldNew.setTotalMoneyByStock(total.getTotalMoneyByStock());
                oldNew.setTotalQuantityByStock(total.getTotalQuantityByStock());

                currencyTotalsCollection.put(total.getCurrency().getId(), oldNew);
            }

        }

        return currencyTotalsCollection;

    }

}
