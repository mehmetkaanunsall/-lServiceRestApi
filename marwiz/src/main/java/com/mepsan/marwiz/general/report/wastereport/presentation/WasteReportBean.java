/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.presentation;

import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.wastereport.business.IWasteReportService;
import com.mepsan.marwiz.general.report.wastereport.dao.WasteReport;
import com.mepsan.marwiz.inventory.wastereason.business.IWasteReasonService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
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

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class WasteReportBean extends GeneralReportBean<WasteReport> {

    private List<Categorization> listOfCategorization;
    private List<Stock> listOfStock;
    String createWhere = "";
    private List<WasteReport> listOfTotals;
    private List<WasteReason> listOfWasteReason;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private int branchId;

    @ManagedProperty(value = "#{wasteReportService}")
    private IWasteReportService wasteReportService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{wasteReasonService}")
    private IWasteReasonService wasteReasonService;

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

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public void setWasteReportService(IWasteReportService wasteReportService) {
        this.wasteReportService = wasteReportService;
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

    public List<WasteReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<WasteReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<WasteReason> getListOfWasteReason() {
        return listOfWasteReason;
    }

    public void setListOfWasteReason(List<WasteReason> listOfWasteReason) {
        this.listOfWasteReason = listOfWasteReason;
    }

    public IWasteReasonService getWasteReasonService() {
        return wasteReasonService;
    }

    public void setWasteReasonService(IWasteReasonService wasteReasonService) {
        this.wasteReasonService = wasteReasonService;
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

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------------WasteReportBean-------------");
        selectedObject = new WasteReport();
        listOfCategorization = new ArrayList<>();
        listOfStock = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        listOfWasteReason = new ArrayList<>();
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

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);

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
        selectedObject.getStockList().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        if (selectedBranchList.size() == 1) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = selectedBranchList.get(0).getBranch().getId();
        } else {
            branchId = -1;
        }

        int countCentral = 0;
        int countNotCentral = 0;
        int wasteWhere = -1;
        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                }
                if (branchSetting.isIsCentralIntegration()) {
                    countCentral++;
                } else {
                    countNotCentral++;
                }
            }
            if (listOfBranch.size() == countCentral) {//Hepsinin merkezi entegrasyonu vardır
                wasteWhere = 0;
            } else if (listOfBranch.size() == countNotCentral) {
                wasteWhere = 1;
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                }

                if (branchSetting.isIsCentralIntegration()) {
                    countCentral++;
                } else {
                    countNotCentral++;
                }
            }

            if (selectedBranchList.size() == countCentral) {//Hepsinin merkezi entegrasyonu vardır
                wasteWhere = 0;
            } else if (selectedBranchList.size() == countNotCentral) {
                wasteWhere = 1;
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        listOfWasteReason = wasteReasonService.selectWasteReason(wasteWhere);

    }

    @Override
    public void find() {
        isFind = true;

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(selectedObject.getEndDate());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());

        cal.setTime(selectedObject.getBeginDate());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());

        selectedObject.getCategorizationList().clear();
        selectedObject.getCategorizationList().addAll(listOfCategorization);

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        if (selectedObject.getWasteReason().getId() != 0) {
            for (WasteReason wr : listOfWasteReason) {
                if (wr.getId() == selectedObject.getWasteReason().getId()) {
                    selectedObject.getWasteReason().setName(wr.getName());
                }
            }
        }

        createWhere = wasteReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmWasteReportDatatable:dtbWasteReport");
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
            RequestContext.getCurrentInstance().update("frmWasteReport:txtStock");
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

            RequestContext.getCurrentInstance().update("frmWasteReport:txtCategory");
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
        } else {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
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
    public LazyDataModel<WasteReport> findall(String where) {
        return new CentrowizLazyDataModel<WasteReport>() {
            @Override
            public List<WasteReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WasteReport> result = wasteReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, branchList);
                listOfTotals = wasteReportService.totals(where, branchList);
                int count = 0;
                for (WasteReport total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void createPdf() {

        wasteReportService.exportPdf(createWhere, selectedObject, toogleList, branchList, selectedBranchList, listOfTotals);
    }

    public void createExcel() throws IOException {
        wasteReportService.exportExcel(createWhere, selectedObject, toogleList, branchList, selectedBranchList, listOfTotals);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(wasteReportService.exportPrinter(createWhere, selectedObject, toogleList, branchList, selectedBranchList, listOfTotals)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public BigDecimal calculateTotal(BigDecimal price, BigDecimal quantity) {
        if (price != null && quantity != null) {
            return price.multiply(quantity);
        }
        return BigDecimal.valueOf(0);
    }

}
