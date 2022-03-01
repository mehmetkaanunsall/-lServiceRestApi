/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.presentation;

import com.google.common.collect.HashBiMap;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.warehousemovementreport.business.IWarehouseMovementReportService;
import com.mepsan.marwiz.general.report.warehousemovementreport.dao.WarehouseMovementReport;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class WarehouseMovementReportBean extends GeneralReportBean<WarehouseMovementReport> {

    private List<Warehouse> listOfWarehouse;
    private String createWhere;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account> listOfAccount;
    private boolean isCentralSupplier;
    private List<WarehouseMovementReport> listOfTotals;
    private Map<Integer, WarehouseMovementReport> currencyTotalsCollection;
    private List<Type> listOfType;

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    @ManagedProperty(value = "#{warehouseMovementReportService}")
    private IWarehouseMovementReportService warehouseMovementReportService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{centralSupplierBookCheckboxFilterBean}")
    private CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setWarehouseMovementReportService(IWarehouseMovementReportService warehouseMovementReportService) {
        this.warehouseMovementReportService = warehouseMovementReportService;
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

    public List<WarehouseMovementReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<WarehouseMovementReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public Map<Integer, WarehouseMovementReport> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Integer, WarehouseMovementReport> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---WarehouseMovementReportBean----");
        selectedObject = new WarehouseMovementReport();
        listOfWarehouse = new ArrayList<>();
        listOfCentralSupplier = new ArrayList<>();
        listOfAccount = new ArrayList<>();
        listOfWarehouse = warehouseService.selectListWarehouse(" ");
        listOfTotals = new ArrayList<>();
        currencyTotalsCollection = new HashMap<>();
        listOfType = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        cal.setTime(selectedObject.getEndDate());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        listOfType = sessionBean.getTypes(8);

       
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            setCountToggle(0);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, false, true, true, true, true, true, true);
            setCountToggle(1);
        }
    }

    @Override
    public void find() {
        isFind = true;

        selectedObject.getListOfAccount().clear();
        selectedObject.getListOfAccount().addAll(listOfAccount);

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            selectedObject.getListOfCentralSupplier().clear();
            selectedObject.getListOfCentralSupplier().addAll(listOfCentralSupplier);
        }
        createWhere = warehouseMovementReportService.createWhere(selectedObject, centralSupplierBookCheckboxFilterBean.getSupplierType(), isCentralSupplier);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmWarehouseMovementReportDatatable:dtbWarehouseMovementReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        for (Type type : listOfType) {
            if (type.getId() == selectedObject.getType().getId()) {
                selectedObject.getType().setTag(type.getNameMap().get(sessionBean.getUser().getLanguage().getId()).getName());
            }
        }

        if (!currencyTotalsCollection.isEmpty()) {
            currencyTotalsCollection.clear();
        }
        listOfTotals = warehouseMovementReportService.totals(createWhere,selectedObject);

        currencyTotalsCollection = calculateOverallTotal();
        listOfObjects = findall(createWhere);
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
    public LazyDataModel<WarehouseMovementReport> findall(String where) {
        return new CentrowizLazyDataModel<WarehouseMovementReport>() {
            @Override
            public List<WarehouseMovementReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseMovementReport> result = warehouseMovementReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject);
                int count = 0;
                if (listOfTotals.size() > 0) {
                    for (WarehouseMovementReport total : listOfTotals) {
                        count = count + total.getId();
                    }
                }

                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
            selectedObject.getSelectedStocks().clear();
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
            selectedObject.getSelectedStocks().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            RequestContext.getCurrentInstance().update("frmWarehouseMovementReport:txtStock");
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
            RequestContext.getCurrentInstance().update("frmWarehouseMovementReport:txtCentralSupplier");
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
            RequestContext.getCurrentInstance().update("frmWarehouseMovementReport:txtSupplier");
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!selectedObject.getSelectedStocks().isEmpty()) {
                if (selectedObject.getSelectedStocks().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getSelectedStocks());
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
        warehouseMovementReportService.exportPdf(createWhere, selectedObject, toogleList, isCentralSupplier, currencyTotalsCollection);
    }

    public void createExcel() throws IOException {
        warehouseMovementReportService.exportExcel(createWhere, selectedObject, toogleList, isCentralSupplier, currencyTotalsCollection);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(warehouseMovementReportService.exportPrinter(createWhere, selectedObject, toogleList, isCentralSupplier, currencyTotalsCollection)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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

    public Map<Integer, WarehouseMovementReport> calculateOverallTotal() {

        for (WarehouseMovementReport total : listOfTotals) {
            if (total.getCurrency().getId() == 0) {
                total.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

            }
            if (total.getTotalMoney() != null && total.getTotalTax() != null && total.getQuantity() != null && total.getCurrency().getId() != 0) {
                if (currencyTotalsCollection.containsKey(total.getCurrency().getId())) {

                    WarehouseMovementReport old = new WarehouseMovementReport();
                    old.setCurrency(currencyTotalsCollection.get(total.getCurrency().getId()).getCurrency());

                    old.setTotalMoney(currencyTotalsCollection.get(total.getCurrency().getId()).getTotalMoney());
                    old.setTotalTax(currencyTotalsCollection.get(total.getCurrency().getId()).getTotalTax());
                    old.setQuantity(currencyTotalsCollection.get(total.getCurrency().getId()).getQuantity());

                    old.setTotalMoney(old.getTotalMoney().add(total.getTotalMoney()));
                    old.setTotalTax(old.getTotalTax().add(total.getTotalTax()));
                    old.setQuantity(old.getQuantity().add(total.getQuantity()));
                    currencyTotalsCollection.put(total.getCurrency().getId(), old);

                } else {

                    WarehouseMovementReport oldNew = new WarehouseMovementReport();
                    oldNew.setCurrency(total.getCurrency());
                    oldNew.setTotalMoney(total.getTotalMoney());
                    oldNew.setTotalTax(total.getTotalTax());
                    oldNew.setQuantity(total.getQuantity());

                    currencyTotalsCollection.put(total.getCurrency().getId(), oldNew);
                }

            }

        }

        return currencyTotalsCollection;

    }

}
