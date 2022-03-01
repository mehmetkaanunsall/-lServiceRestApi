/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.02.2018 03:00:36
 */
package com.mepsan.marwiz.general.report.totalgiroreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.totalgiroreport.business.ITotalGiroReportService;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.TotalGiroReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

@ManagedBean
@ViewScoped
public class TotalGiroReportBean extends GeneralReportBean<TotalGiroReport> {

    @ManagedProperty(value = "#{totalGiroReportService}")
    public ITotalGiroReportService totalGiroReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private String createWhere;
    private String totalGiro;
    private Currency currency;
    private List<TotalGiroReport> listOfTotalGiroReport;
    private HashMap<Integer, BigDecimal> groupCurrencyForTotal;
    private List<TotalGiroReport> listOfTotal;
    private List<BranchSetting> listOfBranch;
    String whereBranch;

    public void setTotalGiroReportService(ITotalGiroReportService totalGiroReportService) {
        this.totalGiroReportService = totalGiroReportService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public String getTotalGiro() {
        return totalGiro;
    }

    public void setTotalGiro(String totalGiro) {
        this.totalGiro = totalGiro;
    }

    public List<TotalGiroReport> getListOfTotalGiroReport() {
        return listOfTotalGiroReport;
    }

    public void setListOfTotalGiroReport(List<TotalGiroReport> listOfTotalGiroReport) {
        this.listOfTotalGiroReport = listOfTotalGiroReport;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfTotalGiroReport = new ArrayList<>();
        listOfTotal = new ArrayList<>();

        selectedObject = new TotalGiroReport();
        toogleList = Arrays.asList(true, true, true);
        currency = new Currency();
        Calendar cal = Calendar.getInstance();
        selectedObject.setEndDate(new Date());
        cal.setTime(selectedObject.getEndDate());
        cal.add(Calendar.MONTH, -1);
        selectedObject.setBeginDate(cal.getTime());

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
        listOfTotalGiroReport.clear();
        listOfTotal.clear();
        totalGiro = "";

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());
        groupCurrencyForTotal = new HashMap<>();

        calendar.setTime(selectedObject.getBeginDate());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(calendar.getTime());

        createWhere = totalGiroReportService.createWhere(selectedObject);

        whereBranch = totalGiroReportService.createWhereBranch(selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList());

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmTotalGiroDatatable:dtbTotalGiro");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfTotalGiroReport = findAll(createWhere, whereBranch);

        for (TotalGiroReport t : listOfTotalGiroReport) {
            if (groupCurrencyForTotal.containsKey(t.getCurrency().getId())) {
                BigDecimal old = groupCurrencyForTotal.get(t.getCurrency().getId());
                groupCurrencyForTotal.put(t.getCurrency().getId(), old.add(t.getPrice()));
            } else {
                groupCurrencyForTotal.put(t.getCurrency().getId(), t.getPrice());
            }
        }

        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyForTotal.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.valueOf(0)) == 1) {
                TotalGiroReport tgiro = new TotalGiroReport();
                tgiro.setPrice(entry.getValue());
                tgiro.getCurrency().setId(entry.getKey());
                listOfTotal.add(tgiro);
            }

        }
        int count = 0;
        for (TotalGiroReport r : listOfTotal) {

            if (count == 1) {
                totalGiro += " + " + String.valueOf(formatter.format(r.getPrice()));
                totalGiro += " " + sessionBean.currencySignOrCode(r.getCurrency().getId(), 0);
            } else {
                count = 1;
                totalGiro += String.valueOf(formatter.format(r.getPrice()));
                totalGiro += " " + sessionBean.currencySignOrCode(r.getCurrency().getId(), 0);
            }

        }

        RequestContext.getCurrentInstance().execute("count=" + listOfTotalGiroReport.size() + ";");
    }

    public List<TotalGiroReport> findAll(String where, String whereBranch) {
        return totalGiroReportService.findAll(where, whereBranch);
    }

    public void createPdf() {
        totalGiroReportService.exportPdf(selectedObject, listOfTotalGiroReport, totalGiro, toogleList, whereBranch);
    }

    public void createExcel() throws IOException {
        totalGiroReportService.exportExcel(selectedObject, listOfTotalGiroReport, totalGiro, toogleList, whereBranch);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(totalGiroReportService.exportPrinter(selectedObject, listOfTotalGiroReport, totalGiro, toogleList, whereBranch)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

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
    public LazyDataModel<TotalGiroReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
