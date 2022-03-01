/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:56:00 AM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.presentation;

import com.mepsan.marwiz.automat.report.automatshiftreport.business.IAutomatShiftReportDetailService;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.model.automat.AutomatShiftSales;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
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
public class WashingMachicneShiftDetailReportBean extends GeneralReportBean<AutomatSales> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{automatShiftReportDetailService}")
    private IAutomatShiftReportDetailService automatShiftReportDetailService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    private AutomatShift selectedShift;
    private List<AutomatSales> listOfTotals;

    public AutomatShift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(AutomatShift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public void setAutomatShiftReportDetailService(IAutomatShiftReportDetailService automatShiftReportDetailService) {
        this.automatShiftReportDetailService = automatShiftReportDetailService;
    }

    public List<AutomatSales> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<AutomatSales> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    @PostConstruct
    @Override
    public void init() {
        selectedObject = new AutomatShiftSales();
        selectedShift = new AutomatShift();

        if (sessionBean.parameter instanceof AutomatShift) {
            selectedShift.setId(((AutomatShift) sessionBean.parameter).getId());
            selectedShift.setShiftNo(((AutomatShift) sessionBean.parameter).getShiftNo());
        }
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

    public void createPdf() {
        automatShiftReportDetailService.exportPdf(selectedShift, toogleList, listOfTotals, " ");
    }

    public void createExcel() throws IOException {
        automatShiftReportDetailService.exportExcel(selectedShift, toogleList, listOfTotals, " ");
    }

}
