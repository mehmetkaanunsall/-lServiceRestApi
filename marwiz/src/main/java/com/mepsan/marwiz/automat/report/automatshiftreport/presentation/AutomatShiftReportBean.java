/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:45:05 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.presentation;

import com.mepsan.marwiz.automat.report.automatshiftreport.business.IAutomatShiftReportService;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@ViewScoped
public class AutomatShiftReportBean extends GeneralReportBean<AutomatShiftReport> {

    @ManagedProperty(value = "#{automatShiftReportService}")
    private IAutomatShiftReportService automatShiftReportService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    private int count;
    String createWhere;
    private BigDecimal stockPercent, paymentTypePercent, platformPercent;
    private PieChartModel pieChartModelStock;
    private PieChartModel pieChartModelPaymentType;
    private int processType;

    private List<AutomatSales> washingMachicneSaleStockList;
    private List<AutomatSales> washingMachicneSalePlatformList;
    private List<AutomatSales> washingMachicneSalePaymentTypeList;

    public void setAutomatShiftReportService(IAutomatShiftReportService automatShiftReportService) {
        this.automatShiftReportService = automatShiftReportService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public BigDecimal getStockPercent() {
        return stockPercent;
    }

    public void setStockPercent(BigDecimal stockPercent) {
        this.stockPercent = stockPercent;
    }

    public BigDecimal getPaymentTypePercent() {
        return paymentTypePercent;
    }

    public void setPaymentTypePercent(BigDecimal paymentTypePercent) {
        this.paymentTypePercent = paymentTypePercent;
    }

    public BigDecimal getPlatformPercent() {
        return platformPercent;
    }

    public void setPlatformPercent(BigDecimal platformPercent) {
        this.platformPercent = platformPercent;
    }

    public PieChartModel getPieChartModelStock() {
        return pieChartModelStock;
    }

    public void setPieChartModelStock(PieChartModel pieChartModelStock) {
        this.pieChartModelStock = pieChartModelStock;
    }

    public PieChartModel getPieChartModelPaymentType() {
        return pieChartModelPaymentType;
    }

    public void setPieChartModelPaymentType(PieChartModel pieChartModelPaymentType) {
        this.pieChartModelPaymentType = pieChartModelPaymentType;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<AutomatSales> getWashingMachicneSaleStockList() {
        return washingMachicneSaleStockList;
    }

    public void setWashingMachicneSaleStockList(List<AutomatSales> washingMachicneSaleStockList) {
        this.washingMachicneSaleStockList = washingMachicneSaleStockList;
    }

    public List<AutomatSales> getWashingMachicneSalePlatformList() {
        return washingMachicneSalePlatformList;
    }

    public void setWashingMachicneSalePlatformList(List<AutomatSales> washingMachicneSalePlatformList) {
        this.washingMachicneSalePlatformList = washingMachicneSalePlatformList;
    }

    public List<AutomatSales> getWashingMachicneSalePaymentTypeList() {
        return washingMachicneSalePaymentTypeList;
    }

    public void setWashingMachicneSalePaymentTypeList(List<AutomatSales> washingMachicneSalePaymentTypeList) {
        this.washingMachicneSalePaymentTypeList = washingMachicneSalePaymentTypeList;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("======AutomatShiftReportBean===");

        selectedObject = new AutomatShiftReport();
        washingMachicneSaleStockList = new ArrayList<>();
        washingMachicneSalePlatformList = new ArrayList<>();
        listOfObjects = findall(" ");

        toogleList = Arrays.asList(true, true, true, true, true);
    }

    @Override
    public void find() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public LazyDataModel<AutomatShiftReport> findall(String where) {
        return new CentrowizLazyDataModel<AutomatShiftReport>() {
            @Override
            public List<AutomatShiftReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<AutomatShiftReport> result = automatShiftReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                count = automatShiftReportService.count(where);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void cellProcess(String shiftNo, int shiftId, int type, Date beginDate, Date endDate) {
        if (type == 2) {

            selectedObject = new AutomatShiftReport();
            selectedObject.setId(shiftId);
            selectedObject.setShiftNo(shiftNo);
            selectedObject.setBeginDate(beginDate);
            selectedObject.setEndDate(endDate);

            washingMachicneSaleStockList.clear();
            washingMachicneSalePlatformList.clear();

            stockPercent = BigDecimal.ZERO;
            washingMachicneSaleStockList = automatShiftReportService.listOfSaleStock(selectedObject);
            for (AutomatSales report : washingMachicneSaleStockList) {
                if (report.getStock().getId() > 0) {
                    stockPercent = stockPercent.add(report.getOperationAmount());
                }
            }
            createAreaStockModel();

            platformPercent = BigDecimal.ZERO;
            washingMachicneSalePlatformList = automatShiftReportService.listOfSalePlatform(selectedObject);
            for (AutomatSales report : washingMachicneSalePlatformList) {
                if (report.getPlatformNo() != null) {
                    platformPercent = platformPercent.add(report.getOperationAmount());
                }
            }

            paymentTypePercent = BigDecimal.ZERO;
            washingMachicneSalePaymentTypeList = automatShiftReportService.listOfSalePaymentType(selectedObject);
            for (AutomatSales report : washingMachicneSalePaymentTypeList) {
                if (report.getPaymentType() > 0) {
                    paymentTypePercent = paymentTypePercent.add(report.getOperationAmount());
                }
            }
            System.out.println("---paymentTypePercent----" + paymentTypePercent);
            createAreaPaymentTypeModel();

            RequestContext.getCurrentInstance().update("frmShiftGraphicProc");
            RequestContext.getCurrentInstance().update("frmSaleGraphic2");
            RequestContext.getCurrentInstance().execute("PF('dlg_ShiftGraphic').show()");
        } else if (type == 1) {
            marwiz.goToPage("/pages/mqm/report/shiftreport/shiftreportdetail", selectedObject, 0, 737);
        } else if (type == 3) {
            processType = 2;
            selectedObject = new AutomatShiftReport();
            selectedObject.setId(shiftId);
            selectedObject.setShiftNo(shiftNo);
            selectedObject.setBeginDate(beginDate);
            selectedObject.setEndDate(endDate);
            RequestContext.getCurrentInstance().update("frmShiftPrint");
            marwiz.goToPage("/pages/automat/report/automatshiftreport/automatshiftreportprintdetail.xhtml", selectedObject, 0, 124);
        }

    }

    //stoğa göre pasta tasarımı
    void createAreaStockModel() {
        pieChartModelStock = new PieChartModel();
        pieChartModelStock.setSeriesColors("83BFFF, FFC266, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");
        for (AutomatSales report : washingMachicneSaleStockList) {
            if (report.getStock().getId() > 0) {
                //  pieChartModelStock.set(report.getStationUscTank().getStationStock().getStockName(), report.getLiter());
                String name = report.getStock().getName() + (report.getStock().getUnit().getSortName() == null ? " (LT) " : "(" + report.getStock().getUnit().getSortName() + ")");
                pieChartModelStock.set(name, report.getOperationAmount());

            }

            if (washingMachicneSaleStockList.isEmpty()) {
                pieChartModelStock.set(sessionBean.getLoc().getString("stock"), 0);
            }
            pieChartModelStock.setLegendPosition("w");

        }
    }

    //ödemelere göre pasta dilimi
    void createAreaPaymentTypeModel() {
        pieChartModelPaymentType = new PieChartModel();
        pieChartModelPaymentType.setSeriesColors("83BFFF, FFC266, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");

        for (AutomatSales report : washingMachicneSalePaymentTypeList) {
            if (report.getPaymentType() > 0) {
                String name1 = report.getPaymentType() == 1 ? sessionBean.getLoc().getString("cash") : report.getPaymentType() == 2 ? sessionBean.getLoc().getString("barcode") : sessionBean.getLoc().getString("mobilepayment");
                String Name = name1 + (report.getStock().getUnit().getSortName() == null ? "( LT) " : "(" + report.getStock().getUnit().getSortName() + ")");
                pieChartModelPaymentType.set(Name, report.getOperationAmount());
            }
        }
        if (washingMachicneSalePaymentTypeList.isEmpty()) {
            pieChartModelPaymentType.set(sessionBean.getLoc().getString("paymenttype"), 0);
        }
        pieChartModelPaymentType.setLegendPosition("w");

    }

    public void goToDetail() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/automat/report/automatshiftreport/automatshiftreportdetail.xhtml", list, 0, 123);
    }

    public void createPdf() {
        automatShiftReportService.exportPdf(createWhere, toogleList);
    }

    public void createExcel() throws IOException {
        automatShiftReportService.exportExcel(createWhere, toogleList);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(automatShiftReportService.exportPrinter(createWhere, toogleList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

}
