/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:16:14 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.presentation;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.automat.report.incomeexpensereport.business.IIncomeExpenseReportService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class IncomeExpenseReportBean extends GeneralReportBean<AutomatSalesReport> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}") // session
    public Marwiz marwiz;

    @ManagedProperty(value = "#{incomeExpenseReportService}")
    public IIncomeExpenseReportService incomeExpenseReportService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    private List<AutomatSalesReport> listOfObject;
    private String createWhere;
    private AutomatSalesReport selectedReport;
    private AutomatSalesReport selectedDetailObject;
    private PieChartModel pieChartModelStock;
    private BarChartModel barModel;
    private List<AutomatSalesReport> listOfIncomeExpenseDetail;
    private List<Stock> listOfStock;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setIncomeExpenseReportService(IIncomeExpenseReportService incomeExpenseReportService) {
        this.incomeExpenseReportService = incomeExpenseReportService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public List<AutomatSalesReport> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<AutomatSalesReport> listOfObject) {
        this.listOfObject = listOfObject;
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

    public AutomatSalesReport getSelectedReport() {
        return selectedReport;
    }

    public void setSelectedReport(AutomatSalesReport selectedReport) {
        this.selectedReport = selectedReport;
    }

    public PieChartModel getPieChartModelStock() {
        return pieChartModelStock;
    }

    public void setPieChartModelStock(PieChartModel pieChartModelStock) {
        this.pieChartModelStock = pieChartModelStock;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public List<AutomatSalesReport> getListOfIncomeExpenseDetail() {
        return listOfIncomeExpenseDetail;
    }

    public void setListOfIncomeExpenseDetail(List<AutomatSalesReport> listOfIncomeExpenseDetail) {
        this.listOfIncomeExpenseDetail = listOfIncomeExpenseDetail;
    }

    public AutomatSalesReport getSelectedDetailObject() {
        return selectedDetailObject;
    }

    public void setSelectedDetailObject(AutomatSalesReport selectedDetailObject) {
        this.selectedDetailObject = selectedDetailObject;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---IncomeExpenseReportBean-");

        selectedObject = new AutomatSalesReport();
        Calendar cal = GregorianCalendar.getInstance();
        isFind = false;
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        selectedObject.setNetTotal(BigDecimal.ZERO);
        listOfStock = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true, true, true); // true
    }

    @Override
    public void find() {
        isFind = true;
        selectedObject.setNetTotal(BigDecimal.valueOf(0));
        selectedObject.setTotalNetExpense(BigDecimal.valueOf(0));
        selectedObject.setTotalNetIncome(BigDecimal.valueOf(0));
        selectedObject.getListOfStock().clear();
        selectedObject.getListOfStock().addAll(listOfStock);

        createWhere = incomeExpenseReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmIncomeExpenseReportDatatable:dtbIncomeExpense");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObject = incomeExpenseReportService.findAll(createWhere);

        if (!listOfObject.isEmpty()) {
            AutomatSalesReport automatSaleReport = new AutomatSalesReport();
            automatSaleReport.getStock().setId(9999999);
            automatSaleReport.getStock().setName(sessionBean.getLoc().getString("electricalexpense")); // elektrik gideri eklenir.
            automatSaleReport.getStock().getUnit().setSortName("kW");

            BigDecimal electricQuantity = BigDecimal.ZERO;
            int electricTime = 0, waterTime = 0;
            BigDecimal electricExpense = BigDecimal.ZERO;
            BigDecimal electricTotalWase = BigDecimal.ZERO;
            BigDecimal waterQuantity = BigDecimal.ZERO, waterExpense = BigDecimal.ZERO, waterTotalWase = BigDecimal.ZERO;

            for (AutomatSalesReport automatSaleReport1 : listOfObject) {
                electricQuantity = electricQuantity.add(automatSaleReport1.getElectricQuantity());
                electricTime += automatSaleReport1.getElectricOperationTime().intValue();
                electricExpense = electricExpense.add(automatSaleReport1.getElectricExpense());
                electricTotalWase = electricTotalWase.add(automatSaleReport1.getTotalElectricAmount());
                waterQuantity = waterQuantity.add(automatSaleReport1.getWaterWorkingAmount());
                waterTime += automatSaleReport1.getWaterWorkingTime();
                waterExpense = waterExpense.add(automatSaleReport1.getWaterExpense());
                waterTotalWase = waterTotalWase.add(automatSaleReport1.getWaterWaste());
            }

            automatSaleReport.setQuantitiy(electricQuantity);
            automatSaleReport.setOperationTime(electricTime);
            automatSaleReport.setTotalExpense(electricExpense);
            automatSaleReport.setWaste(electricTotalWase);
            automatSaleReport.setTotalIncome(BigDecimal.ZERO);
            if (electricExpense.compareTo(BigDecimal.ZERO) == 0) {
                automatSaleReport.setTotalWinnings(electricExpense);
            } else {
                automatSaleReport.setTotalWinnings(electricExpense.multiply(BigDecimal.valueOf(-1)));
            }
            listOfObject.add(automatSaleReport);

            AutomatSalesReport automatSaleReportWater = new AutomatSalesReport();
            automatSaleReportWater.getStock().setId(99999999);
            automatSaleReportWater.getStock().setName(sessionBean.getLoc().getString("waterexpense")); // su gideri eklenir.
            automatSaleReportWater.getStock().getUnit().setSortName("LT");
            automatSaleReportWater.setQuantitiy(waterQuantity);
            automatSaleReportWater.setOperationTime(waterTime);
            automatSaleReportWater.setTotalExpense(waterExpense);
            automatSaleReportWater.setWaste(waterTotalWase);
            automatSaleReportWater.setTotalIncome(BigDecimal.ZERO);
            if (waterExpense.compareTo(BigDecimal.ZERO) == 0) {
                automatSaleReportWater.setTotalWinnings(waterExpense);
            } else {
                automatSaleReportWater.setTotalWinnings(waterExpense.multiply(BigDecimal.valueOf(-1)));
            }
            listOfObject.add(automatSaleReportWater);

        }
        calculateTotal();
        createAreaStockModel();
        createBarModel();
        RequestContext.getCurrentInstance().update("pgrSalesReportDatatable");
        RequestContext.getCurrentInstance().update("frmIncomeExpenseReportDatatable:dtbIncomeExpense");
    }

    public void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (AutomatSalesReport obj : listOfObject) {
            total = total.add(obj.getTotalWinnings());
            totalIncome = totalIncome.add(obj.getTotalIncome());
            totalExpense = totalExpense.add(obj.getTotalExpense());
        }
        selectedObject.setTotalNetExpense(totalExpense);
        selectedObject.setTotalNetIncome(totalIncome);
        selectedObject.setNetTotal(total);
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
            RequestContext.getCurrentInstance().update("frmIncomeExpenseReport:txtStock");
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

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //stoğa göre pasta tasarımı
    void createAreaStockModel() {
        pieChartModelStock = new PieChartModel();
        pieChartModelStock.setSeriesColors("9ebcde, f8b5bb,cccccc,dec09e, bcde9e, feb417");
        for (AutomatSalesReport report : listOfObject) {
            if (report.getStock().getId() > 0) {
                String name = report.getStock().getName();
                pieChartModelStock.set(name, report.getWaste());
            }
        }

        if (listOfObject.isEmpty()) {
            pieChartModelStock.set(sessionBean.getLoc().getString("stock"), 0);
        }
        pieChartModelStock.setLegendPosition("w");
    }

    //gelir gidere göre 
    void createBarModel() {
        barModel = new BarChartModel();
        barModel.setSeriesColors("4E6789 ,eb5858, d55f5d, ed7d31, aaca65");
        ChartSeries income = new ChartSeries();
        income.setLabel(sessionBean.getLoc().getString("income"));

        ChartSeries expense = new ChartSeries();
        expense.setLabel(sessionBean.getLoc().getString("expense"));

        for (AutomatSalesReport report : listOfObject) {
            if (report.getStock().getId() > 0) {
                String name = report.getStock().getName();
                income.set(name, report.getTotalIncome());
                expense.set(name, report.getTotalExpense());
            }
        }
        barModel.addSeries(income);
        barModel.addSeries(expense);
        barModel.setExtender("extenderName");
        barModel.setLegendPosition("w");

    }

    public void createPdf() {
        incomeExpenseReportService.exportPdf(createWhere, toogleList, selectedObject, marwiz.getPageIdOfGoToPage(), listOfObject);
    }

    public void createExcel() throws IOException {
        incomeExpenseReportService.exportExcel(createWhere, toogleList, selectedObject, marwiz.getPageIdOfGoToPage(), listOfObject);
    }

    public void createPrinter() throws IOException {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(incomeExpenseReportService.exportPrinter(createWhere, toogleList, selectedObject, marwiz.getPageIdOfGoToPage(), listOfObject)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    @Override
    public LazyDataModel<AutomatSalesReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void showDetail() {
        listOfIncomeExpenseDetail = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = " AND sl.saledatetime BETWEEN '" + dateFormat.format(selectedObject.getBeginDate()) + "' AND '" + dateFormat.format(selectedObject.getEndDate()) + "' AND sl.stock_id= " + selectedReport.getStock().getId();

        if (selectedObject.getShiftNo() != null) {
            where += ((!selectedObject.getShiftNo().equals("")) ? " AND sl.shiftno = '" + selectedObject.getShiftNo().replace("'", "") + "' " : "");
        }

        listOfIncomeExpenseDetail = incomeExpenseReportService.listOfDetail(where);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmShiftDetailReportDatatable:dtbShiftDetailReport");
        dataTable.setFirst(0);

        RequestContext.getCurrentInstance().update("dlgIncomeExpenseDetailProc");
        RequestContext.getCurrentInstance().execute("PF('dlg_incomeExpenseDetailProc').show()");
    }

}
