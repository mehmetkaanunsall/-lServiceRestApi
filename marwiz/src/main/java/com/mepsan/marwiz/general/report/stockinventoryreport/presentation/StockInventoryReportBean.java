/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.presentation;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
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
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.stockinventoryreport.business.IStockInventoryReportService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.mepsan.marwiz.general.report.stockinventoryreport.dao.StockInventoryReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author tugcan.koparan
 */
@ManagedBean
@ViewScoped
public class StockInventoryReportBean extends GeneralReportBean<StockInventoryReport> {

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockInventoryReportService}")
    private IStockInventoryReportService stockInventoryReportService;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    private String createWhere;
    private List<StockInventoryReport> listOfTotals;
    private Map<Integer, BigDecimal> currencyTotals;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    int centralIntegrationIf = 0;
    private List<CentralSupplier> listOfCentralSupplier;
    private boolean isCentralSupplier;
    private boolean isCentralSupplierIconView;
    private boolean isCenterBranch;

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
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

    public void setStockInventoryReportService(IStockInventoryReportService stockInventoryReportService) {
        this.stockInventoryReportService = stockInventoryReportService;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
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

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    @Override
    @PostConstruct
    public void init() {
        currencyTotals = new HashMap<>();
        listOfTotals = new ArrayList<>();
        selectedObject = new StockInventoryReport();
        selectedObject.setIsTax(true);
        selectedObject.setZeroStock(true);
        listOfBranch = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch(); // kullanıcının yetkili olduğu branch listesini çeker
        selectedBranchList = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
        }

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

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        selectedObject.setCost(1);
        selectedObject.setReportType(1);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(calendar.getTime());
        selectedObject.setDate(calendar.getTime());
    }

    /**
     * Bu metot branch değiştiği anda string olara branch id leri birleştirerek
     * where şartı oluşturur.
     */
    public void changeBranch() {
        branchList = "";

        selectedObject.getListOfAccount().clear();
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        selectedObject.getListOfCentralSupplier().clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        selectedObject.getListOfStock().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        int countCentralIntegration = 0;
        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                countCentralIntegration++;
            }
        }
        if (countCentralIntegration > 0 || selectedBranchList.size() == 0) {
            isCentralSupplierIconView = true;
        } else {
            isCentralSupplierIconView = false;
            isCentralSupplier = false;
        }

        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                    break;
                }
            }
        } else {

            for (BranchSetting branchSetting : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                    break;
                }
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());

        }

        //  RequestContext.getCurrentInstance().execute("renderBookIcon('" + isThereListBranch + "')");
    }

    @Override
    public void find() {
        isCenterBranch = false;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setDate(calendar.getTime());

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmStockInventoryReportDataTable:dtbInventoryReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        switch (selectedObject.getReportType()) {
            case 1:
                if (isCentralSupplierIconView) {
                    toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, false, true, true, true, true, true);
                    setCountToggle(1);
                } else {
                    toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, false, true, true, true, true, true);
                    setCountToggle(2);
                }
            case 2:
                if (isCentralSupplierIconView) {
                    toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, false, true, true, true, true, true);
                    setCountToggle(1);
                } else {
                    toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, false, true, true, true, true, true);
                    setCountToggle(2);
                }
            case 3:
                if (isCentralSupplierIconView) {
                    toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, false, true, true, true, true, true);
                    setCountToggle(1);
                } else {
                    toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, false, true, true, true, true, true);
                    setCountToggle(2);
                }

                break;
            case 4:
                toogleList = Arrays.asList(true, false, false, false, false, false, false, false, false, true, true, false, true, false, true);
                setCountToggle(10);
                break;
            case 5:
                toogleList = Arrays.asList(true, false, false, false, false, false, false, false, false, true, true, false, true, false, true);
                setCountToggle(10);
                break;
            default:
                break;
        }
        isFind = true;
        boolean isThere = false;
        int count = 0;
        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                isThere = false;
                if (branchSetting.isIsCentralIntegration()) {
                    isThere = true;
                }
                if (isThere) {
                    count++;
                }
                if (branchSetting.getBranch().isIsCentral()) {
                    isCenterBranch = true;
                }
            }

            centralIntegrationIf = -1;
            if (listOfBranch.size() > 1) {
                if (count >= 1 && count < listOfBranch.size()) {
                    centralIntegrationIf = -1;
                } else if (count == listOfBranch.size() && isThere) {
                    centralIntegrationIf = 1;
                } else if (count == 0 && !isThere) {
                    centralIntegrationIf = 0;
                }
            } else if (listOfBranch.size() == 1) {
                if (listOfBranch.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 1;
                } else if (!listOfBranch.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 0;
                }
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                isThere = false;
                if (branchSetting.isIsCentralIntegration()) {
                    isThere = true;
                }
                if (isThere) {
                    count++;
                }
                if (branchSetting.getBranch().isIsCentral()) {
                    isCenterBranch = true;
                }
            }

            centralIntegrationIf = -1;
            if (selectedBranchList.size() > 1) {
                if (count >= 1 && count < selectedBranchList.size()) {
                    centralIntegrationIf = -1;
                } else if (count == selectedBranchList.size() && isThere) {
                    centralIntegrationIf = 1;
                } else if (count == 0 && !isThere) {
                    centralIntegrationIf = 0;
                }
            } else if (selectedBranchList.size() == 1) {
                if (selectedBranchList.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 1;
                } else if (!selectedBranchList.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 0;
                }
            }
        }

        createWhere = stockInventoryReportService.createWhere(selectedObject, centralIntegrationIf);
        listOfObjects = findall(createWhere);
    }

    public String totals(int type) {
        String total = "";
        currencyTotals.clear();

        for (StockInventoryReport stockInventoryReport : listOfTotals) {

            if (type == 1) {
                if (stockInventoryReport.getLastSaleCurreny().getId() != 0) {
                    if (currencyTotals.containsKey(stockInventoryReport.getLastSaleCurreny().getId())) {
                        BigDecimal old = currencyTotals.get(stockInventoryReport.getLastSaleCurreny().getId());
                        currencyTotals.put(stockInventoryReport.getLastSaleCurreny().getId(), old.add(stockInventoryReport.getLastSaleCost()));
                    } else {
                        currencyTotals.put(stockInventoryReport.getLastSaleCurreny().getId(), stockInventoryReport.getLastSaleCost());

                    }
                }

            } else {

                if (selectedObject.getCost() == 2 || selectedObject.getCost() == 3) {
                    stockInventoryReport.getLastPurchaseCurreny().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                }
                if (stockInventoryReport.getLastPurchaseCurreny().getId() != 0) {

                    if (currencyTotals.containsKey(stockInventoryReport.getLastPurchaseCurreny().getId())) {
                        BigDecimal old = currencyTotals.get(stockInventoryReport.getLastPurchaseCurreny().getId());
                        currencyTotals.put(stockInventoryReport.getLastPurchaseCurreny().getId(), old.add(stockInventoryReport.getLastPurchaseCost()));
                    } else {
                        currencyTotals.put(stockInventoryReport.getLastPurchaseCurreny().getId(), stockInventoryReport.getLastPurchaseCost());

                    }
                }
            }

        }

        for (Map.Entry<Integer, BigDecimal> entry : currencyTotals.entrySet()) {

            total += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0) + " \n ";

        }
        if (total.isEmpty() || total.equals("")) {

            total = "0 " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        } else {
            total = total.substring(0, total.length() - 2);
        }

        return total;
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
    public LazyDataModel<StockInventoryReport> findall(String where) {
        return new CentrowizLazyDataModel<StockInventoryReport>() {
            @Override
            public List<StockInventoryReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<StockInventoryReport> result = stockInventoryReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, branchList, centralIntegrationIf, isCenterBranch, centralSupplierBookCheckboxFilterBean.getSupplierType(), selectedObject.getReportType() == 3 ? isCentralSupplier : false);
                listOfTotals = stockInventoryReportService.totals(where, selectedObject, branchList, centralIntegrationIf, isCenterBranch,  centralSupplierBookCheckboxFilterBean.getSupplierType(),selectedObject.getReportType() == 3 ? isCentralSupplier : false);
                int count = 0;
                for (StockInventoryReport stockInventoryReport : listOfTotals) {
                    switch (selectedObject.getReportType()) {
                        case 1:
                        case 2:
                        case 3:
                            count += stockInventoryReport.getStock().getId();
                            break;
                        case 4:
                        case 5:
                            count += stockInventoryReport.getTax().getId();
                            break;
                        default:
                            break;
                    }
                }
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    public void updateAllInformation(ActionEvent actionEvent) {
        if (actionEvent.getComponent().getParent().getParent().getParent().getId().equals("frmCategoryBookFilterCheckbox")) {
            selectedObject.getListOfStockCategorization().clear();
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

            selectedObject.getListOfStockCategorization().addAll(categoryBookCheckboxFilterBean.getListOfCategorization());

            if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmStockInventoryReport:txtCategory");
        } else if (actionEvent.getComponent().getParent().getParent().getId().equals("frmAccountBookFilterCheckbox")) {
            selectedObject.getListOfAccount().clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account a = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(a)) {
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
            selectedObject.getListOfAccount().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("supplier") + " " + sessionBean.loc.getString("selected"));
            }

            RequestContext.getCurrentInstance().update("frmStockInventoryReport:txtSupplier");
        } else if (actionEvent.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
            selectedObject.getListOfStock().clear();
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
            selectedObject.getListOfStock().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmStockInventoryReport:txtStock");
        } else if (actionEvent.getComponent().getParent().getParent().getId().equals("frmCentralSupplierBookFilterCheckbox")) {
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
            RequestContext.getCurrentInstance().update("frmStockInventoryReport:txtCentralSupplier");
        }
    }

    public void openDialog(int type) {
        if (type == 1) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!selectedObject.getListOfStockCategorization().isEmpty()) {
                if (selectedObject.getListOfStockCategorization().get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }
            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(selectedObject.getListOfStockCategorization());
        } else if (type == 2) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!selectedObject.getListOfStock().isEmpty()) {
                if (selectedObject.getListOfStock().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getListOfStock());
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
            if (!selectedObject.getListOfAccount().isEmpty()) {
                if (selectedObject.getListOfAccount().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getListOfAccount());
        }
    }

    public void createPdf() {
        stockInventoryReportService.exportPdf(createWhere, selectedObject, toogleList, listOfTotals, branchList, centralIntegrationIf, selectedBranchList, selectedObject.getReportType() == 3 ? isCentralSupplier : false, isCenterBranch, centralSupplierBookCheckboxFilterBean.getSupplierType());
    }

    public void createExcel() {
        stockInventoryReportService.exportExcel(createWhere, selectedObject, toogleList, listOfTotals, branchList, centralIntegrationIf, selectedBranchList,selectedObject.getReportType() == 3 ? isCentralSupplier : false, isCenterBranch, centralSupplierBookCheckboxFilterBean.getSupplierType());
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(stockInventoryReportService.exportPrinter(createWhere, selectedObject, toogleList, listOfTotals, branchList, centralIntegrationIf, selectedBranchList, selectedObject.getReportType() == 3 ? isCentralSupplier : false, isCenterBranch, centralSupplierBookCheckboxFilterBean.getSupplierType())) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void centralOrLocalSupplier(int type) {
        selectedObject.getListOfAccount().clear();
        listOfCentralSupplier.clear();
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        if (type == 0) {
            isCentralSupplier = false;
        } else {
            isCentralSupplier = true;
        }
    }
}
