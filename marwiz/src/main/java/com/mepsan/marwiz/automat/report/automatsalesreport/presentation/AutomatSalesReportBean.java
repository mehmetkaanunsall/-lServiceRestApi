/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 11:54:13
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.presentation;

import com.mepsan.marwiz.automat.report.automatsalesreport.business.IAutomatSalesReportService;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneNozzleService;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicnePlatformService;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneService;
import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicneTankService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceService;
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
public class AutomatSalesReportBean extends GeneralReportBean<AutomatSalesReport> {

    @ManagedProperty(value = "#{automatSalesReportService}")
    private IAutomatSalesReportService automatSalesReportService;

    @ManagedProperty(value = "#{washingMachicneService}")
    private IWashingMachicneService washingMachicneService;

    @ManagedProperty(value = "#{automationDeviceService}")
    private IAutomationDeviceService automationDeviceService;

    @ManagedProperty(value = "#{washingMachicnePlatformService}")
    private IWashingMachicnePlatformService washingMachicnePlatformService;

    @ManagedProperty(value = "#{washingMachicneNozzleService}")
    private IWashingMachicneNozzleService washingMachicneNozzleService;

    @ManagedProperty(value = "#{washingMachicneTankService}")
    private IWashingMachicneTankService washingMachicneTankService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private List<WashingPlatform> platformList;
    private List<WashingNozzle> nozzleList;
    private List<WashingTank> tankList;
    private List<AutomationDevice> vendingMachineList;
    private List<WashingMachicne> washingMachineList;
    private AutomatSalesReport selectedAutomatSales;
    private List<Stock> listOfStock;

    private List<AutomatSalesReport> listOfTotals;

    String createWhere;

    public List<WashingPlatform> getPlatformList() {
        return platformList;
    }

    public void setPlatformList(List<WashingPlatform> platformList) {
        this.platformList = platformList;
    }

    public List<WashingNozzle> getNozzleList() {
        return nozzleList;
    }

    public void setNozzleList(List<WashingNozzle> nozzleList) {
        this.nozzleList = nozzleList;
    }

    public List<WashingTank> getTankList() {
        return tankList;
    }

    public void setTankList(List<WashingTank> tankList) {
        this.tankList = tankList;
    }

    public List<AutomatSalesReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<AutomatSalesReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public void setAutomatSalesReportService(IAutomatSalesReportService automatSalesReportService) {
        this.automatSalesReportService = automatSalesReportService;
    }

    public AutomatSalesReport getSelectedAutomatSales() {
        return selectedAutomatSales;
    }

    public void setSelectedAutomatSales(AutomatSalesReport selectedAutomatSales) {
        this.selectedAutomatSales = selectedAutomatSales;
    }

    public List<AutomationDevice> getVendingMachineList() {
        return vendingMachineList;
    }

    public void setVendingMachineList(List<AutomationDevice> vendingMachineList) {
        this.vendingMachineList = vendingMachineList;
    }

    public List<WashingMachicne> getWashingMachineList() {
        return washingMachineList;
    }

    public void setWashingMachineList(List<WashingMachicne> washingMachineList) {
        this.washingMachineList = washingMachineList;
    }

    public void setWashingMachicneService(IWashingMachicneService washingMachicneService) {
        this.washingMachicneService = washingMachicneService;
    }

    public void setAutomationDeviceService(IAutomationDeviceService automationDeviceService) {
        this.automationDeviceService = automationDeviceService;
    }

    public void setWashingMachicnePlatformService(IWashingMachicnePlatformService washingMachicnePlatformService) {
        this.washingMachicnePlatformService = washingMachicnePlatformService;
    }

    public void setWashingMachicneNozzleService(IWashingMachicneNozzleService washingMachicneNozzleService) {
        this.washingMachicneNozzleService = washingMachicneNozzleService;
    }

    public void setWashingMachicneTankService(IWashingMachicneTankService washingMachicneTankService) {
        this.washingMachicneTankService = washingMachicneTankService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    @Override
    @PostConstruct
    public void init() {
        selectedObject = new AutomatSalesReport();
        selectedAutomatSales = new AutomatSalesReport();
        platformList = new ArrayList<>();
        tankList = new ArrayList();
        nozzleList = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        washingMachineList = new ArrayList<>();
        vendingMachineList = new ArrayList<>();
        listOfStock = new ArrayList<>();

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());

        selectedObject.setMinSalesPrice(null);
        selectedObject.setMaxSalesPrice(null);

        washingMachineList = washingMachicneService.selectWashinMachine(" ");

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);

    }

    @Override
    public void find() {
        isFind = true;
        setCountToggle(0);

        selectedObject.getListOfStock().clear();
        selectedObject.getListOfStock().addAll(listOfStock);

        createWhere = automatSalesReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmAutomatSalesReportDatatable:dtbAutomatSalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmAutomatSalesReportDatatable:dtbAutomatSalesReport");
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
    public LazyDataModel<AutomatSalesReport> findall(String where) {
        return new CentrowizLazyDataModel<AutomatSalesReport>() {
            @Override
            public List<AutomatSalesReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<AutomatSalesReport> result = automatSalesReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject);
                listOfTotals = automatSalesReportService.totals(where);
                int count = 0;
                for (AutomatSalesReport total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    /* public void createSalesDetailDialog() {
        saleItemList = automatSalesReportService.findSaleItem(selectedAutomatSales);
        RequestContext.getCurrentInstance().execute("PF('dlg_SalesDetail').show();");
    }*/
    public void changeAutomatType() {
        washingMachineList.clear();
        vendingMachineList.clear();
        selectedObject.getListOfVendingMachine().clear();
        selectedObject.getListOfWashingMachine().clear();

        washingMachineList = washingMachicneService.selectWashinMachine(" ");

    }

    public void changeWashingMachine() {
        selectedObject.getListOfPlatform().clear();
        selectedObject.getListOfTank().clear();
        platformList.clear();
        tankList.clear();
        if (!selectedObject.getListOfWashingMachine().isEmpty()) {
            if (selectedObject.getListOfWashingMachine().size() == 1) {
                platformList = washingMachicnePlatformService.findAll(selectedObject.getListOfWashingMachine().get(0));
                tankList = washingMachicneTankService.findAll(selectedObject.getListOfWashingMachine().get(0));
            }
        }

    }

    public void changePlatform() {
        selectedObject.getListOfNozzle().clear();
        nozzleList.clear();
        if (!selectedObject.getListOfPlatform().isEmpty()) {
            if (selectedObject.getListOfPlatform().size() == 1) {
                nozzleList = washingMachicneNozzleService.findAll(selectedObject.getListOfPlatform().get(0));
            }
        }

    }

    public void createPdf() {
        automatSalesReportService.exportPdf(createWhere, selectedObject, toogleList, listOfTotals);
    }

    public void createExcel() throws IOException {
        automatSalesReportService.exportExcel(createWhere, selectedObject, toogleList, listOfTotals);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(automatSalesReportService.exportPrinter(createWhere, selectedObject, toogleList, listOfTotals)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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
            RequestContext.getCurrentInstance().update("frmAutomatSalesReport:txtStock");
        }

    }

    public void openDialog() {
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
