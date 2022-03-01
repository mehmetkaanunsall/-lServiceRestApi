/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:33:25
 */
package com.mepsan.marwiz.general.report.salesreturnreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.ReceiptReturnReport;
import java.io.IOException;
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
import com.mepsan.marwiz.general.report.salesreturnreport.business.ISalesReturnReportService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.util.ArrayList;
import java.util.HashMap;

@ManagedBean
@ViewScoped
public class SalesReturnReportBean extends GeneralReportBean<ReceiptReturnReport> {

    @ManagedProperty(value = "#{salesReturnReportService}")
    public ISalesReturnReportService salesReturnReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private String createWhere;
    private List<BranchSetting> selectedBranchList;
    private List<BranchSetting> listOfBranch;
    private String createWhereBranch;
    private List<ReceiptReturnReport> listOfTotals;
    private Map<Integer, ReceiptReturnReport> currencyTotalsCollection;

    public void setSalesReturnReportService(ISalesReturnReportService salesReturnReportService) {
        this.salesReturnReportService = salesReturnReportService;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<ReceiptReturnReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<ReceiptReturnReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public Map<Integer, ReceiptReturnReport> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Integer, ReceiptReturnReport> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------SalesReturnReportBean");

        selectedObject = new ReceiptReturnReport();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);

        Calendar cal = GregorianCalendar.getInstance();
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

        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        currencyTotalsCollection = new HashMap<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch();

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) { // kullanıcının default branch bilgisini seçili getirmek için kullanılır.
                    selectedBranchList.add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) { // kullanıcının default branch bilgisini seçili getirmek için kullanılır.
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranchList.add(branchSetting);
                    break;
                }
            }
        }

    }

    @Override
    public void find() {
        isFind = true;
        if (!listOfTotals.isEmpty()) {
            listOfTotals.clear();
        }
        if (!currencyTotalsCollection.isEmpty()) {
            currencyTotalsCollection.clear();
        }

        createWhere = salesReturnReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmReceiptReturnDatatable:dtbReceiptReturn");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        createWhereBranch = salesReturnReportService.createWhereBranch(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
        listOfObjects = findall(createWhere);

    }

    @Override
    public LazyDataModel<ReceiptReturnReport> findall(String where) {
        return new CentrowizLazyDataModel<ReceiptReturnReport>() {
            @Override
            public List<ReceiptReturnReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                List<ReceiptReturnReport> result = salesReturnReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, createWhereBranch);
                listOfTotals = salesReturnReportService.totals(where, createWhereBranch);
                int count = 0;
                if (listOfTotals.size() > 0) {
                    count = listOfTotals.get(0).getId();
                    currencyTotalsCollection = calculateOverallTotal();
                }

                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }

        };

    }

    public void createPdf() {
        salesReturnReportService.exportPdf(createWhere, selectedObject, toogleList, createWhereBranch, selectedBranchList, listOfTotals, currencyTotalsCollection);
    }

    public void createExcel() throws IOException {
        salesReturnReportService.exportExcel(createWhere, selectedObject, toogleList, createWhereBranch, selectedBranchList, listOfTotals, currencyTotalsCollection);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(salesReturnReportService.exportPrinter(createWhere, selectedObject, toogleList, createWhereBranch, selectedBranchList, listOfTotals, currencyTotalsCollection)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<Integer, ReceiptReturnReport> calculateOverallTotal() {
        if (!currencyTotalsCollection.isEmpty()) {
            currencyTotalsCollection.clear();
        }
        for (ReceiptReturnReport rr : listOfTotals) {

            if (currencyTotalsCollection.containsKey(rr.getCurrency().getId())) {

                ReceiptReturnReport old = new ReceiptReturnReport();
                old.setCurrency(currencyTotalsCollection.get(rr.getCurrency().getId()).getCurrency());

                old.setTotalMoney(currencyTotalsCollection.get(rr.getCurrency().getId()).getTotalMoney());
                old.setTotalPrice(currencyTotalsCollection.get(rr.getCurrency().getId()).getTotalPrice());
                old.setQuantity(currencyTotalsCollection.get(rr.getCurrency().getId()).getQuantity());

                old.setTotalMoney(old.getTotalMoney().add(rr.getTotalMoney()));
                old.setTotalPrice(old.getTotalPrice().add(rr.getTotalPrice()));
                old.setQuantity(old.getQuantity().add(rr.getQuantity()));
                currencyTotalsCollection.put(rr.getCurrency().getId(), old);

            } else {

                ReceiptReturnReport oldNew = new ReceiptReturnReport();
                oldNew.setCurrency(rr.getCurrency());
                oldNew.setTotalMoney(rr.getTotalMoney());
                oldNew.setTotalPrice(rr.getTotalPrice());
                oldNew.setQuantity(rr.getQuantity());

                currencyTotalsCollection.put(rr.getCurrency().getId(), oldNew);
            }

        }

        return currencyTotalsCollection;

    }
}
