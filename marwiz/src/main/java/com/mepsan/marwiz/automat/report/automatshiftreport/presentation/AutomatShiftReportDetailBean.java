/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:26:45
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.presentation;

import com.mepsan.marwiz.automat.report.automatshiftreport.business.IAutomatShiftReportDetailService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class AutomatShiftReportDetailBean extends GeneralReportBean<AutomatSales> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{automatShiftReportDetailService}")
    private IAutomatShiftReportDetailService automatShiftReportDetailService;

    private AutomatShift selectedShift;

    private List<AutomatSales> listOfTotals;
    private List<AutomatSales> shiftSaleItemList;

    public List<AutomatSales> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<AutomatSales> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public AutomatShift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(AutomatShift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public List<AutomatSales> getShiftSaleItemList() {
        return shiftSaleItemList;
    }

    public void setShiftSaleItemList(List<AutomatSales> shiftSaleItemList) {
        this.shiftSaleItemList = shiftSaleItemList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAutomatShiftReportDetailService(IAutomatShiftReportDetailService automatShiftReportDetailService) {
        this.automatShiftReportDetailService = automatShiftReportDetailService;
    }

    @Override
    @PostConstruct
    public void init() {
        selectedObject = new AutomatSales();
        selectedShift = new AutomatShift();
        shiftSaleItemList = new ArrayList<>();
        listOfTotals = new ArrayList<>();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof AutomatShift) {
                    selectedShift = (AutomatShift) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }

        selectedObject.setShift(selectedShift);

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);

        find();
    }

    @Override
    public void find() {
        isFind = true;
        listOfObjects = findall(" ");
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
    public LazyDataModel<AutomatSales> findall(String where) {
        return new CentrowizLazyDataModel<AutomatSales>() {
            @Override
            public List<AutomatSales> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<AutomatSales> result = automatShiftReportDetailService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedShift);
                listOfTotals = automatShiftReportDetailService.totals(where, selectedShift);
                int count = 0;
                for (AutomatSales total : listOfTotals) {
                    count = count + total.getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void createDialog() {
        selectedObject.setShift(selectedShift);
        shiftSaleItemList = automatShiftReportDetailService.find(selectedObject);

      //  RequestContext.getCurrentInstance().execute("PF('dlg_ShiftSales').show();");
    }

    public void createPdf() {
        automatShiftReportDetailService.exportPdf(selectedShift, toogleList, listOfTotals, " ");
    }

    public void createExcel() throws IOException {
        automatShiftReportDetailService.exportExcel(selectedShift, toogleList, listOfTotals, " ");
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(automatShiftReportDetailService.exportPrinter(selectedShift, toogleList, listOfTotals, " ")) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

}
