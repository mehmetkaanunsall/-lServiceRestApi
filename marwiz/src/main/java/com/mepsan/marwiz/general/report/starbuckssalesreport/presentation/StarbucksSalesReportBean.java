/**
 * Bu class şubelerde bulunan starbucks cihazına ait satışları listelemek için kullanılır.
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:07:18 PM
 */
package com.mepsan.marwiz.general.report.starbuckssalesreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.starbuckssalesreport.business.IStarbucksSalesReportService;
import com.mepsan.marwiz.general.report.starbuckssalesreport.dao.StarbucksMachicneSales;
import com.mepsan.marwiz.inventory.starbucksstock.business.IStarbucksStockService;
import java.io.IOException;
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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class StarbucksSalesReportBean extends GeneralReportBean<StarbucksMachicneSales> {

    @ManagedProperty(value = "#{starbucksSalesReportService}")
    private IStarbucksSalesReportService starbucksSalesReportService;

    @ManagedProperty(value = "#{starbucksStockService}")
    private IStarbucksStockService starbucksStockService;

    private Date beginDate;
    private Date endDate;
    private List<StarbucksMachicneSales> listOfSales;
    private List<StarbucksStock> listOfStarbucksStock;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStarbucksSalesReportService(IStarbucksSalesReportService starbucksSalesReportService) {
        this.starbucksSalesReportService = starbucksSalesReportService;
    }

    public List<StarbucksMachicneSales> getListOfSales() {
        return listOfSales;
    }

    public void setListOfSales(List<StarbucksMachicneSales> listOfSales) {
        this.listOfSales = listOfSales;
    }

    public void setStarbucksStockService(IStarbucksStockService starbucksStockService) {
        this.starbucksStockService = starbucksStockService;
    }

    public List<StarbucksStock> getListOfStarbucksStock() {
        return listOfStarbucksStock;
    }

    public void setListOfStarbucksStock(List<StarbucksStock> listOfStarbucksStock) {
        this.listOfStarbucksStock = listOfStarbucksStock;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-StarbucksSalesReportBean--");
        setListBtn(new ArrayList<>());
        listOfStarbucksStock = new ArrayList<>();
        Calendar cal = GregorianCalendar.getInstance();
        endDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        beginDate = cal.getTime();
        toogleList = Arrays.asList(true, true, true, true, true);
        listOfStarbucksStock = starbucksStockService.findAll();
    }

    /**
     * Bu metot arama kriterlerine göre arama butonuna basınca çalışır.
     */
    @Override
    public void find() {
        isFind = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        beginDate = calendar.getTime();

        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        endDate = calendar.getTime();

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmSalesReportDatatable:dtbSalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfObjects = findall("");

        listOfSales = findSales();

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<StarbucksMachicneSales> findSales() {
        return starbucksSalesReportService.findSales(beginDate, endDate, listOfStarbucksStock);
    }

    @Override
    public LazyDataModel<StarbucksMachicneSales> findall(String where) {
        return new CentrowizLazyDataModel<StarbucksMachicneSales>() {
            @Override
            public List<StarbucksMachicneSales> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<StarbucksMachicneSales> result = starbucksSalesReportService.listOfSale(first, pageSize, beginDate, endDate, listOfStarbucksStock);
                int count = starbucksSalesReportService.count();
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void createPdf() {
        starbucksSalesReportService.exportPdf(beginDate, endDate, selectedObject, toogleList, listOfSales, listOfStarbucksStock);
    }

    public void createExcel() throws IOException {
        starbucksSalesReportService.exportExcel(beginDate, endDate, selectedObject, toogleList, listOfSales, listOfStarbucksStock);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(starbucksSalesReportService.exportPrint(beginDate, endDate, selectedObject, toogleList, listOfSales, listOfStarbucksStock)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

}
