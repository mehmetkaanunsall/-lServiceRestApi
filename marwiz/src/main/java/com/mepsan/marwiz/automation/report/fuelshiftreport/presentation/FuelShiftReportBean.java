/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:39:57
 *
 * @update 03.10.2018 Samet - Okunan Txt verileri ile ön izleme tablosu
 * oluşturuldu ve kayıt işlemi gerçeklendi.
 *
 * @update 25.10.2018 Ali Kurt - Turpak akaryakıt vardiyasını xml den alma işlemi eklendi.
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.presentation;

import com.mepsan.marwiz.automation.report.fuelshiftreport.business.IFuelShiftService;
import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.FuelShiftReport;
import com.mepsan.marwiz.automation.saletype.business.ISaleTypeService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
public class FuelShiftReportBean extends GeneralReportBean<FuelShiftReport> {

    @ManagedProperty(value = "#{fuelShiftService}")
    public IFuelShiftService fuelShiftService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{saleTypeService}")
    public ISaleTypeService saleTypeService;

    private BranchSetting branchSetting;
    private List<FuelShiftSales> listOfAttendant;
    private List<FuelShiftSales> listOfStock;
    private List<FuelShiftSales> listOfSaleType;
    private PieChartModel pieChartModelSaleType;
    Double totalMoneyForPumper, totalMoneyForStocks, totalMoneyForSaleTypes, totalQuantityForPumper, totalQuantityForStocks, totalQuantityForSaleTypes;
    public List<FuelShiftSales> listExport;
    private int specialItem;

    private FuelShiftReport fuelShiftReport;
    private String where;

    private List<FuelShiftReport> listOfTotals;
    private List<FuelSaleType> listForSaleTypeList;
    private String fuelSaleTypeName;

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public List<FuelShiftSales> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<FuelShiftSales> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public List<FuelShiftSales> getListOfSaleType() {
        return listOfSaleType;
    }

    public void setListOfSaleType(List<FuelShiftSales> listOfSaleType) {
        this.listOfSaleType = listOfSaleType;
    }

    public List<FuelShiftSales> getListOfAttendant() {
        return listOfAttendant;
    }

    public void setListOfAttendant(List<FuelShiftSales> listOfAttendant) {
        this.listOfAttendant = listOfAttendant;
    }

    public List<FuelShiftSales> getListExport() {
        return listExport;
    }

    public void setListExport(List<FuelShiftSales> listExport) {
        this.listExport = listExport;
    }

    public Double getTotalMoneyForPumper() {
        return totalMoneyForPumper;
    }

    public void setTotalMoneyForPumper(Double totalMoneyForPumper) {
        this.totalMoneyForPumper = totalMoneyForPumper;
    }

    public Double getTotalMoneyForStocks() {
        return totalMoneyForStocks;
    }

    public void setTotalMoneyForStocks(Double totalMoneyForStocks) {
        this.totalMoneyForStocks = totalMoneyForStocks;
    }

    public Double getTotalMoneyForSaleTypes() {
        return totalMoneyForSaleTypes;
    }

    public void setTotalMoneyForSaleTypes(Double totalMoneyForSaleTypes) {
        this.totalMoneyForSaleTypes = totalMoneyForSaleTypes;
    }

    public PieChartModel getPieChartModelSaleType() {
        return pieChartModelSaleType;
    }

    public void setPieChartModelSaleType(PieChartModel pieChartModelSaleType) {
        this.pieChartModelSaleType = pieChartModelSaleType;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setFuelShiftService(IFuelShiftService fuelShiftService) {
        this.fuelShiftService = fuelShiftService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public FuelShiftReport getFuelShiftReport() {
        return fuelShiftReport;
    }

    public void setFuelShiftReport(FuelShiftReport fuelShiftReport) {
        this.fuelShiftReport = fuelShiftReport;
    }

    public List<FuelShiftReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<FuelShiftReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    public Double getTotalQuantityForPumper() {
        return totalQuantityForPumper;
    }

    public void setTotalQuantityForPumper(Double totalQuantityForPumper) {
        this.totalQuantityForPumper = totalQuantityForPumper;
    }

    public Double getTotalQuantityForStocks() {
        return totalQuantityForStocks;
    }

    public void setTotalQuantityForStocks(Double totalQuantityForStocks) {
        this.totalQuantityForStocks = totalQuantityForStocks;
    }

    public Double getTotalQuantityForSaleTypes() {
        return totalQuantityForSaleTypes;
    }

    public void setTotalQuantityForSaleTypes(Double totalQuantityForSaleTypes) {
        this.totalQuantityForSaleTypes = totalQuantityForSaleTypes;
    }

    public List<FuelSaleType> getListForSaleTypeList() {
        return listForSaleTypeList;
    }

    public void setListForSaleTypeList(List<FuelSaleType> listForSaleTypeList) {
        this.listForSaleTypeList = listForSaleTypeList;
    }

    public String getFuelSaleTypeName() {
        return fuelSaleTypeName;
    }

    public void setFuelSaleTypeName(String fuelSaleTypeName) {
        this.fuelSaleTypeName = fuelSaleTypeName;
    }

    public void setSaleTypeService(ISaleTypeService saleTypeService) {
        this.saleTypeService = saleTypeService;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------FuelShiftReportBean");
        branchSetting = sessionBean.getLastBranchSetting();
        selectedObject = new FuelShiftReport();

        totalMoneyForPumper = 0.0;
        totalMoneyForStocks = 0.0;
        totalMoneyForSaleTypes = 0.0;
        listExport = new ArrayList<>();

        fuelShiftReport = new FuelShiftReport();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        fuelShiftReport.setReportBeginDate(calendar.getTime());

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        fuelShiftReport.setReportEndDate(calendar.getTime());

        listOfTotals = new ArrayList<>();

        specialItem = sessionBean.getUser().getLastBranchSetting().getSpecialItem();

        if (specialItem == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, true, false, false, false, false, true, true, true);
        }

        find();

    }

    @Override
    public LazyDataModel<FuelShiftReport> findall(String where) {
        return new CentrowizLazyDataModel<FuelShiftReport>() {
            @Override
            public List<FuelShiftReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<FuelShiftReport> result = fuelShiftService.findAll(first, pageSize, sortField, "", filters, where, branchSetting);
                int count = 0;
                for (FuelShiftReport fuelshift : listOfTotals) {
                    count += fuelshift.getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    @Override
    public void find() {
        where = fuelShiftService.createWhere(fuelShiftReport);
        listOfTotals = fuelShiftService.totals(where, branchSetting);
        listOfObjects = findall(where);
    }

    /**
     * Double clickle çalışan fonksiyondur.
     */
    public void goToDetail() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/automation/report/fuelshiftreport/fuelshiftsalereport.xhtml", list, 0, 90);

    }

    public void createPdf() {
        fuelShiftService.exportPdf(where, toogleList, fuelShiftReport, branchSetting, listOfTotals);
    }

    public void createExcel() throws IOException {
        fuelShiftService.exportExcel(where, toogleList, fuelShiftReport, branchSetting, listOfTotals);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(fuelShiftService.exportPrinter(where, toogleList, fuelShiftReport, branchSetting, listOfTotals)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public String calculatePercentage(BigDecimal numberToConvert) {
        return String.format("%.2f", ((numberToConvert.doubleValue() / totalMoneyForPumper) * 100));
    }

    public void print(FuelShift fuelShift) {
        List<Object> list = new ArrayList<>();
        list.add(fuelShift);
        marwiz.goToPage("/pages/automation/report/fuelshiftreport/shiftreportprintdetail.xhtml", list, 0, 91);
    }

    /**
     * Graphic butonuna tıklandığında çalışır.
     *
     * @param fuelShift
     */
    public void openDialog(FuelShift fuelShift) {

        totalMoneyForPumper = 0.0;
        totalMoneyForStocks = 0.0;
        totalMoneyForSaleTypes = 0.0;
        totalQuantityForPumper = 0.0;
        totalQuantityForStocks = 0.0;
        totalQuantityForSaleTypes = 0.0;
        fuelSaleTypeName = "";

        //pompacı
        listOfAttendant = fuelShiftService.findAttendantSales(fuelShift, branchSetting);
        listOfAttendant.forEach((FuelShiftSales fuelShiftSales) -> {
            totalMoneyForPumper += fuelShiftSales.getTotalMoney().doubleValue();
            totalQuantityForPumper += fuelShiftSales.getLiter().doubleValue();
        });

        //Stok
        listOfStock = fuelShiftService.findStockNameSales(fuelShift, branchSetting);
        listOfStock.forEach((fuelShiftSales) -> {
            totalMoneyForStocks += fuelShiftSales.getTotalMoney().doubleValue();
            totalQuantityForStocks += fuelShiftSales.getLiter().doubleValue();
        });

        //Satış tipi
        if (branchSetting.getAutomationId() == 5) {
            listForSaleTypeList = saleTypeService.findSaleTypeForBranch("AND fst.branch_id = " + sessionBean.getUser().getLastBranch().getId());
            listForSaleTypeList.forEach((saleTypelist) -> {
                if (saleTypelist.getTypeno() == 3) {
                    fuelSaleTypeName = saleTypelist.getName();
                }
            });
        }
        listOfSaleType = fuelShiftService.findSaleTypeSales(fuelShift, branchSetting);
        listOfSaleType.forEach((fuelShiftSales) -> {
            totalMoneyForSaleTypes += fuelShiftSales.getTotalMoney().doubleValue();
            totalQuantityForSaleTypes += fuelShiftSales.getLiter().doubleValue();
        });

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_FuelShiftGraphic').show();");

        createPieModel();
    }

    public void createPieModel() {
        pieChartModelSaleType = new PieChartModel();
        pieChartModelSaleType.setSeriesColors("83BFFF, FFC266, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");

        listOfSaleType.forEach((fuelShiftSales) -> {
            pieChartModelSaleType.set("" + fuelShiftSales.getFuelSaleType().getName(), Double.parseDouble(calculatePercentage(fuelShiftSales.getTotalMoney()).replaceAll(",", ".")));
        });

        // pieChartModelSaleType.setTitle(sessionBean.getLoc().getString("salestype"));
        pieChartModelSaleType.setLegendPosition("w");

        pieChartModelSaleType.setShadow(false);
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
