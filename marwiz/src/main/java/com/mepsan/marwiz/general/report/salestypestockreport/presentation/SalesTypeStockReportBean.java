/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:04:23
 */
package com.mepsan.marwiz.general.report.salestypestockreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.salestypestockreport.dao.SalesTypeStockReport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import com.mepsan.marwiz.general.report.salestypestockreport.business.ISalesTypeStockReportService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.util.ArrayList;
import java.util.Map;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class SalesTypeStockReportBean extends GeneralReportBean<SalesTypeStockReport> {

    @ManagedProperty(value = "#{salesTypeStockReportService}")
    public ISalesTypeStockReportService salesTypeStockReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private List<SalesTypeStockReport> listOfTotals;
    private List<BranchSetting> listOfBranch;
    private String whereBranch;
    private List<Type> saleTypeList, tempSaleTypeList;

    public void setSalesTypeStockReportService(ISalesTypeStockReportService salesTypeStockReportService) {
        this.salesTypeStockReportService = salesTypeStockReportService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SalesTypeStockReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<SalesTypeStockReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<Type> getSaleTypeList() {
        return saleTypeList;
    }

    public void setSaleTypeList(List<Type> saleTypeList) {
        this.saleTypeList = saleTypeList;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------SalesTypeStockReportBean");

        selectedObject = new SalesTypeStockReport();
        toogleList = Arrays.asList(true, true, true, true, true, true, true);
        tempSaleTypeList = new ArrayList<>();
        saleTypeList = new ArrayList<>();
        tempSaleTypeList = sessionBean.getTypes(15);
        for (Type t : tempSaleTypeList) {
            if (t.getId() != 106) {
                saleTypeList.add(t);
            }
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        listOfBranch = branchSettingService.findUserAuthorizeBranch();

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedObject.getSelectedBranchList().add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedObject.getSelectedBranchList().add(branchSetting);
                    break;
                }
            }
        }

    }

    @Override
    public void find() {
        isFind = true;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getEndDate());
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        calendar.setTime(selectedObject.getBeginDate());
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmCreditCardReportDatatable:dtbCreditCardReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        bringType();
        whereBranch = salesTypeStockReportService.createWhereForBranch(selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList());
        listOfObjects = findall("");

    }

    public void bringType() {

        for (Type c : sessionBean.getTypes(15)) {
            if (c.getId() == selectedObject.getType().getId()) {
                selectedObject.getType().setTag(c.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void createPdf() {
        salesTypeStockReportService.exportPdf(" ", selectedObject, toogleList, listOfTotals, whereBranch);
    }

    public void createExcel() throws IOException {
        salesTypeStockReportService.exportExcel(" ", selectedObject, toogleList, listOfTotals, whereBranch);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(salesTypeStockReportService.exportPrinter(" ", selectedObject, toogleList, listOfTotals, whereBranch)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    @Override
    public LazyDataModel<SalesTypeStockReport> findall(String where) {
        return new CentrowizLazyDataModel<SalesTypeStockReport>() {

            @Override
            public List<SalesTypeStockReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<SalesTypeStockReport> result = salesTypeStockReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, whereBranch);
                listOfTotals = salesTypeStockReportService.totals(where, selectedObject, whereBranch);
                int count = 0;
                for (SalesTypeStockReport total : listOfTotals) {
                    count = count + total.getStock().getId();
                }
                listOfObjects.setRowCount(count);

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
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
