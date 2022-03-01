/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.presentation;

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
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.business.IEntryExitSummaryReportService;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.dao.EntryExitSummary;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class EntryExitSummaryReportBean extends GeneralReportBean<EntryExitSummary> {

    private List<Categorization> listOfCategorization;
    private List<Stock> listOfStock;
    private List<Warehouse> listOfWarehouse;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    String createWhere = "";
    private boolean isCentralSupplier;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private boolean isCentralSupplierIconView;

    @ManagedProperty(value = "#{entryExitSummaryReportService}")
    private IEntryExitSummaryReportService entryExitSummaryReportService;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public void setEntryExitSummaryReportService(IEntryExitSummaryReportService entryExitSummaryReportService) {
        this.entryExitSummaryReportService = entryExitSummaryReportService;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public boolean isIsCentralSupplier() {
        return isCentralSupplier;
    }

    public void setIsCentralSupplier(boolean isCentralSupplier) {
        this.isCentralSupplier = isCentralSupplier;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setCentralSupplierBookCheckboxFilterBean(CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean) {
        this.centralSupplierBookCheckboxFilterBean = centralSupplierBookCheckboxFilterBean;
    }

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

    public boolean isIsCentralSupplierIconView() {
        return isCentralSupplierIconView;
    }

    public void setIsCentralSupplierIconView(boolean isCentralSupplierIconView) {
        this.isCentralSupplierIconView = isCentralSupplierIconView;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------------EntryExitSummaryReportBean-------------");
        selectedObject = new EntryExitSummary();
        listOfCategorization = new ArrayList<>();
        listOfStock = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, true);
        }

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

    @Override
    public void find() {
        isFind = true;

        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (selectedBranchList.isEmpty()) {
            selectedObject.getListOfBranch().clear();
            selectedObject.getListOfBranch().addAll(listOfBranch);
        } else {
            selectedObject.getListOfBranch().clear();
            selectedObject.getListOfBranch().addAll(selectedBranchList);
        }

        if (isCentralSupplier) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }

        createWhere = entryExitSummaryReportService.createWhere(selectedObject, isCentralSupplier, centralSupplierBookCheckboxFilterBean.getSupplierType());
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmEntyExitSummaryReportDatatable:dtbEntyExitSummaryReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall(createWhere);
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
            RequestContext.getCurrentInstance().update("frmEntyExitSummaryReport:txtStock");
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
            RequestContext.getCurrentInstance().update("frmEntyExitSummaryReport:txtCentralSupplier");
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
            RequestContext.getCurrentInstance().update("frmEntyExitSummaryReport:txtSupplier");
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

            RequestContext.getCurrentInstance().update("frmEntyExitSummaryReport:txtCategory");
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }

            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
        } else if (type == 2) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
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

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<EntryExitSummary> findall(String where) {

        return new CentrowizLazyDataModel<EntryExitSummary>() {

            @Override
            public List<EntryExitSummary> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<EntryExitSummary> result = entryExitSummaryReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject);
                int count = entryExitSummaryReportService.count(where, selectedObject);
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void createPdf() {
        entryExitSummaryReportService.exportPdf(createWhere, selectedObject, toogleList, isCentralSupplier);
    }

    public void createExcel() throws IOException {
        entryExitSummaryReportService.exportExcel(createWhere, selectedObject, toogleList, isCentralSupplier);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(entryExitSummaryReportService.exportPrinter(createWhere, selectedObject, toogleList, isCentralSupplier)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public BigDecimal calculateTransferringBalance(EntryExitSummary obj) {

        BigDecimal b = new BigDecimal(BigInteger.ZERO);
        b = obj.getExitAmount().add(obj.getLastQuantity()).subtract(obj.getEntryAmount());
        return b;
    }

    //Stok devir hızını hesaplar
    public BigDecimal calculateStockTurnoverRate(EntryExitSummary obj) {

        //Stok devir hızı=Satılan ürün miktarı/((Devir miktarı+Kalan miktar) / 2)
        BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
        transferringBalance = calculateTransferringBalance(obj);

        BigDecimal result = new BigDecimal(BigInteger.ZERO);
        try {
            result = obj.getExitAmount().divide((transferringBalance.add(obj.getLastQuantity()).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
        } catch (Exception e) {
        }

        return result;
    }

    //Stok tutma süresini hesaplar
    public BigDecimal calculateStockHoldingTime(EntryExitSummary obj) {

        // Stok tutma süresi=İki tarih arası gün sayısı / stok devir hızı
        Long dayCount; //gün farkı 
        Long difference;

        difference = selectedObject.getEndDate().getTime() - selectedObject.getBeginDate().getTime();
        dayCount = (Long) (difference / (1000 * 60 * 60 * 24));

        BigDecimal stockHoldingTime = new BigDecimal(BigInteger.ZERO);
        try {

            stockHoldingTime = (BigDecimal.valueOf(dayCount).divide(calculateStockTurnoverRate(obj), 4, RoundingMode.HALF_EVEN));
        } catch (Exception e) {
        }

        return stockHoldingTime;
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

    public void changeBranch() {
        listOfStock.clear();
        listOfAccount.clear();
        listOfCentralSupplier.clear();
        selectedObject.getStockList().clear();
        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfCentralSupplier().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        centralSupplierBookCheckboxFilterBean.setSelectedCount(sessionBean.getLoc().getString("all"));
        selectedObject.getWarehouseList().clear();

        int countCentralIntegration = 0;

        String branchList = "";
        String branchWhere = "";
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

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            branchWhere = branchWhere + " AND iw.branch_id IN(" + branchList + ") ";
        }

        if (countCentralIntegration > 0) {
            isCentralSupplierIconView = true;
            toogleList.set(6, true);
        } else {
            isCentralSupplierIconView = false;
            isCentralSupplier = false;
            toogleList.set(6, false);
        }
        listOfWarehouse = warehouseService.selectListAllWarehouse(branchWhere);

    }
}
