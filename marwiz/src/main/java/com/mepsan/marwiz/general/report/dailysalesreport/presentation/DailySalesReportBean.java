/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 02:22:06
 */
package com.mepsan.marwiz.general.report.dailysalesreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.dailysalesreport.business.IDailySalesReportService;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.DailySalesReport;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.SubPivot;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import com.mepsan.marwiz.system.branch.dao.IBranchSettingDao;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class DailySalesReportBean extends GeneralReportBean<DailySalesReport> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{dailySalesReportService}")
    public IDailySalesReportService dailySalesReportService;

    private List<DailySalesReport> listOfDailySalesReport;
    private DailySalesReport dailySalesReport;
    private List<DailySalesReport> listOfEmpty;
    private HashMap<Integer, BigDecimal> groupCurrencyTotalDiscount, groupCurrencyTotalGiro;
    private DailySalesReport totalDailySaleTotal;

    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;

    private Map<Date, DailySalesReport> currencyTotalsCollection;
    private List<DailySalesReport> listSaleProcessDate;
    private String sortOrder;
    private int sorting;
    private boolean sortBy;
    private String sortByTable;

    public void setDailySalesReportService(IDailySalesReportService dailySalesReportService) {
        this.dailySalesReportService = dailySalesReportService;
    }

    public List<DailySalesReport> getListOfDailySalesReport() {
        return listOfDailySalesReport;
    }

    public void setListOfDailySalesReport(List<DailySalesReport> listOfDailySalesReport) {
        this.listOfDailySalesReport = listOfDailySalesReport;
    }

    public List<DailySalesReport> getListOfEmpty() {
        return listOfEmpty;
    }

    public void setListOfEmpty(List<DailySalesReport> listOfEmpty) {
        this.listOfEmpty = listOfEmpty;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public DailySalesReport getTotalDailySaleTotal() {
        return totalDailySaleTotal;
    }

    public void setTotalDailySaleTotal(DailySalesReport totalDailySaleTotal) {
        this.totalDailySaleTotal = totalDailySaleTotal;
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

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public Map<Date, DailySalesReport> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Date, DailySalesReport> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    public List<DailySalesReport> getListSaleProcessDate() {
        return listSaleProcessDate;
    }

    public void setListSaleProcessDate(List<DailySalesReport> listSaleProcessDate) {
        this.listSaleProcessDate = listSaleProcessDate;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public boolean isSortBy() {
        return sortBy;
    }

    public void setSortBy(boolean sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortByTable() {
        return sortByTable;
    }

    public void setSortByTable(String sortByTable) {
        this.sortByTable = sortByTable;
    }

    @Override
    @PostConstruct
    public void init() {

        selectedObject = new DailySalesReport();
        dailySalesReport = new DailySalesReport();
        listOfDailySalesReport = new ArrayList<>();
        listOfEmpty = new ArrayList<>();
        groupCurrencyTotalDiscount = new HashMap<>();
        groupCurrencyTotalGiro = new HashMap<>();
        totalDailySaleTotal = new DailySalesReport();
        currencyTotalsCollection = new HashMap<>();
        listSaleProcessDate = new ArrayList<>();
        Calendar cal = GregorianCalendar.getInstance();
        selectedObject.setEndDate(cal.getTime());
        selectedObject.setBeginDate(cal.getTime());

        sorting = 2;
        sortBy = true; //ASC
        sortByTable = "ascending"; //ASC

        listOfBranch = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch(); // kullanıcının yetkili olduğu branch listesini çeker

        selectedBranchList = new ArrayList<>();
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

    }

    @Override
    public void find() {
        currencyTotalsCollection = new HashMap<>();

        isFind = true;
        if (!listSaleProcessDate.isEmpty()) {
            listSaleProcessDate.clear();
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        calendar.setTime(selectedObject.getBeginDate());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmDailySalesReportDatatable:dtbDailySalesReport");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        branchList = "";
        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        dailySalesReport = findAll(branchList);
        try {
            convertJsonToObject();

        } catch (ParseException ex) {
            Logger.getLogger(DailySalesReportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculateTotal();
        if (!listOfDailySalesReport.isEmpty()) {
            calculateRowGroupTotal();
        }
    }

    public DailySalesReport findAll(String branchList) {
        sortOrder = "";
        switch (sorting) {
            case 1:
                sortOrder = " brn.name ";
                break;
            case 3:
                sortOrder = " SUM(DISTINCT sl.totaldiscount) ";
                break;
            case 4:
                sortOrder = " SUM(slp.price*slp.exchangerate) ";
                break;
            default:
                break;
        }

        if (sorting != 2) {
            sortByTable = "ascending";
            if (sortBy) {// ASC
                sortOrder = sortOrder + " ASC ";
            } else {//DESC
                sortOrder = sortOrder + " DESC ";
            }
        } else {
            if (sortBy) {// ASC
                sortByTable = "ascending";
            } else {//DESC
                sortByTable = "descending";
            }
        }

        return dailySalesReportService.findAll(selectedObject, branchList, sortOrder);
    }

    public void convertJsonToObject() throws ParseException {
        listOfDailySalesReport.clear();
        JSONArray jsonArr = new JSONArray(dailySalesReport.getStringResult());

        for (int m = 0; m < jsonArr.length(); m++) {
            DailySalesReport item = new DailySalesReport();
            for (int j = 1; j < jsonArr.getJSONObject(m).length() - 5; j++) {
                SubPivot sap = new SubPivot();
                if (m == 0) {
                    sap.getType().setTag(jsonArr.getJSONObject(m).getString("p" + String.valueOf(j)));
                } else {
                    sap.setTotalPrice(jsonArr.getJSONObject(m).getBigDecimal("p" + String.valueOf(j)));
                }
                item.getSubList().add(sap);
            }

            if (m != 0) {
                String dateStr = jsonArr.getJSONObject(m).getString("saledate");
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date pDate = sdf.parse(dateStr);
                item.setProcessDate(pDate);
                item.getSubList().get(0).getType().setTag("Nakit");
                item.setTotalDiscount(jsonArr.getJSONObject(m).getBigDecimal("totaldiscount"));
                item.setTotalMoney(jsonArr.getJSONObject(m).getBigDecimal("totalgiro"));
                item.getCurrency().setId(jsonArr.getJSONObject(m).getInt("currency"));
                item.getBranchSetting().getBranch().setId(jsonArr.getJSONObject(m).getInt("branchid"));
                item.getBranchSetting().getBranch().setName(jsonArr.getJSONObject(m).getString("branchname"));
            }

            listOfDailySalesReport.add(item);

        }
//        System.out.println("-----subpivot---"+listOfDailySalesReport.get(1).getSubList().get(0).getType().getTag());

    }

    public DailySalesReport calculateTotal() {
        groupCurrencyTotalDiscount.clear();
        groupCurrencyTotalGiro.clear();

        totalDailySaleTotal = new DailySalesReport();
        String saleTotal = "";
        int temp = 0;

        for (int i = 1; i < listOfDailySalesReport.size(); i++) {
            if (groupCurrencyTotalDiscount.containsKey(listOfDailySalesReport.get(i).getCurrency().getId())) {
                BigDecimal old = groupCurrencyTotalDiscount.get(listOfDailySalesReport.get(i).getCurrency().getId());
                groupCurrencyTotalDiscount.put(listOfDailySalesReport.get(i).getCurrency().getId(), old.add(listOfDailySalesReport.get(i).getTotalDiscount()));
            } else {
                groupCurrencyTotalDiscount.put(listOfDailySalesReport.get(i).getCurrency().getId(), listOfDailySalesReport.get(i).getTotalDiscount());
            }

            if (groupCurrencyTotalGiro.containsKey(listOfDailySalesReport.get(i).getCurrency().getId())) {
                BigDecimal old = groupCurrencyTotalGiro.get(listOfDailySalesReport.get(i).getCurrency().getId());
                groupCurrencyTotalGiro.put(listOfDailySalesReport.get(i).getCurrency().getId(), old.add(listOfDailySalesReport.get(i).getTotalMoney()));
            } else {
                groupCurrencyTotalGiro.put(listOfDailySalesReport.get(i).getCurrency().getId(), listOfDailySalesReport.get(i).getTotalMoney());
            }
        }
        /////Total Discount
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotalDiscount.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTotal += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTotal += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (saleTotal.equals("")) {
            saleTotal = "0.0";
        }
        totalDailySaleTotal.setOverallTotalDiscount(saleTotal);
        /////////////////

        /////Total Giro
        temp = 0;
        saleTotal = "";

        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotalGiro.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTotal += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTotal += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (saleTotal.equals("")) {
            saleTotal = "0.0";
        }
        totalDailySaleTotal.setOverallTotalGiro(saleTotal);
        ////////////////

        return totalDailySaleTotal;
    }

    public String calcTotalDynamicColumn(int index) {
        groupCurrencyTotalGiro.clear();
        if (listOfDailySalesReport.size() > 1) {
            for (int i = 1; i < listOfDailySalesReport.size(); i++) {
                if (index != listOfDailySalesReport.get(0).getSubList().size()) {
                    if (groupCurrencyTotalGiro.containsKey(listOfDailySalesReport.get(i).getCurrency().getId())) {
                        BigDecimal old = groupCurrencyTotalGiro.get(listOfDailySalesReport.get(i).getCurrency().getId());
                        groupCurrencyTotalGiro.put(listOfDailySalesReport.get(i).getCurrency().getId(), old.add(listOfDailySalesReport.get(i).getSubList().get(index).getTotalPrice()));
                    } else {
                        groupCurrencyTotalGiro.put(listOfDailySalesReport.get(i).getCurrency().getId(), listOfDailySalesReport.get(i).getSubList().get(index).getTotalPrice());
                    }
                }
            }
        }

        int temp = 0;
        String saleTotal = "";

        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotalGiro.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTotal += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTotal += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (saleTotal.equals("")) {
            saleTotal = "0.0";
        }
        return saleTotal;
    }

    public void createPdf() {
        dailySalesReportService.exportPdf(selectedObject, selectedBranchList, listOfDailySalesReport, totalDailySaleTotal, listSaleProcessDate, sorting, sortBy);
    }

    public void createExcel() throws IOException {
        dailySalesReportService.exportExcel(selectedObject, selectedBranchList, listOfDailySalesReport, totalDailySaleTotal, listSaleProcessDate, sorting, sortBy);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(dailySalesReportService.exportPrinter(selectedObject, selectedBranchList, listOfDailySalesReport, totalDailySaleTotal, listSaleProcessDate, sorting, sortBy)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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
    public LazyDataModel<DailySalesReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<Date, DailySalesReport> calculateRowGroupTotal() {

        for (DailySalesReport total : listOfDailySalesReport) {

            if (total.getTotalMoney() != null && total.getProcessDate() != null) {
                if (currencyTotalsCollection.containsKey(total.getProcessDate())) {

                    DailySalesReport old = new DailySalesReport();
                    old.setProcessDate(currencyTotalsCollection.get(total.getProcessDate()).getProcessDate());

                    old.setTotalMoney(currencyTotalsCollection.get(total.getProcessDate()).getTotalMoney());

                    old.setTotalMoney(old.getTotalMoney().add(total.getTotalMoney()));

                    currencyTotalsCollection.put(total.getProcessDate(), old);

                } else {

                    DailySalesReport oldNew = new DailySalesReport();
                    oldNew.setProcessDate(total.getProcessDate());
                    oldNew.setTotalMoney(total.getTotalMoney());

                    currencyTotalsCollection.put(total.getProcessDate(), oldNew);
                }

            }

        }

        for (Map.Entry<Date, DailySalesReport> entry : currencyTotalsCollection.entrySet()) {
            DailySalesReport dailySale = new DailySalesReport();
            dailySale.setProcessDate(entry.getKey());
            dailySale.setTotalMoney(entry.getValue().getTotalMoney());
            listSaleProcessDate.add(dailySale);
        }

        return currencyTotalsCollection;

    }
   
}
