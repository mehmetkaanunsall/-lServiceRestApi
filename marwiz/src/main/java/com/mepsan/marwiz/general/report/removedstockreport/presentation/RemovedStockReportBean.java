/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 11.12.2018 15:36:29
 */
package com.mepsan.marwiz.general.report.removedstockreport.presentation;

import com.google.gson.Gson;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportService;
import com.mepsan.marwiz.general.report.removedstockreport.business.IRemovedStockReportService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javafx.scene.control.Toggle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class RemovedStockReportBean extends GeneralReportBean<RemovedStock> {

    @ManagedProperty(value = "#{removedStockReportService}")
    public IRemovedStockReportService removedStockReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftReportService}")
    public IMarketShiftReportService marketShiftReportService;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<Integer> yearList;
    private List<RemovedStockReport> removedStockReportMonthlyList;
    private RemovedStockReport selectedRemovedStockReport;
    private List<RemovedStock> removedStockReportList;
    private UserData selectedUserData;
    private Date beginDate, endDate;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;

    private int year;
    private boolean isClickedEmployee;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setRemovedStockReportService(IRemovedStockReportService removedStockReportService) {
        this.removedStockReportService = removedStockReportService;
    }

    public void setMarketShiftReportService(IMarketShiftReportService marketShiftReportService) {
        this.marketShiftReportService = marketShiftReportService;
    }

    public List<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isIsClickedEmployee() {
        return isClickedEmployee;
    }

    public void setIsClickedEmployee(boolean isClickedEmployee) {
        this.isClickedEmployee = isClickedEmployee;
    }

    public List<RemovedStockReport> getRemovedStockReportMonthlyList() {
        return removedStockReportMonthlyList;
    }

    public void setRemovedStockReportMonthlyList(List<RemovedStockReport> removedStockReportMonthlyList) {
        this.removedStockReportMonthlyList = removedStockReportMonthlyList;

    }

    public List<RemovedStock> getRemovedStockReportList() {
        return removedStockReportList;
    }

    public void setRemovedStockReportList(List<RemovedStock> removedStockReportList) {
        this.removedStockReportList = removedStockReportList;
    }

    public UserData getSelectedUserData() {
        return selectedUserData;
    }

    public void setSelectedUserData(UserData selectedUserData) {
        this.selectedUserData = selectedUserData;
    }

    public RemovedStockReport getSelectedRemovedStockReport() {
        return selectedRemovedStockReport;
    }

    public void setSelectedRemovedStockReport(RemovedStockReport selectedRemovedStockReport) {
        this.selectedRemovedStockReport = selectedRemovedStockReport;
    }

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

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
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

    @PostConstruct
    @Override
    public void init() {
        isClickedEmployee = false;
        yearList = new ArrayList<>();
        selectedUserData = new UserData();
        removedStockReportMonthlyList = new ArrayList<>();

        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();

        Calendar begin = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY, 00);
        begin.set(Calendar.MINUTE, 00);
        begin.set(Calendar.SECOND, 00);
        beginDate = begin.getTime();
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        endDate = end.getTime();

        Calendar date = new GregorianCalendar();
        date.setTime(new Date());

        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
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

        //this.year
        year = date.get(Calendar.YEAR);
        for (int i = year - 10; i <= year; i++) {
            yearList.add(i);
        }
        changeBranch();

    }

    public void changeMonthlyLog() {
        isClickedEmployee = false;
        removedStockReportMonthlyList = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, 0, 1);
        removedStockReportMonthlyList = removedStockReportService.listOfMonthlyLog(calendar.getTime(), branchList);
        chartYearlyGraphic();
        chartMonthlyGraphic();
        chartDailyGraphic();
        chartWeeklyGraphic();
    }

    public void chartYearlyGraphic() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, 0, 1);
        List<ChartItem> list = removedStockReportService.yearlyRemovedStock(calendar.getTime(), branchList);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("yearlyremovedstock(" + "'" + data + "'" + ")");
    }

    public void chartMonthlyGraphic() {
        Calendar beginDate = Calendar.getInstance();
        beginDate.set(Calendar.DAY_OF_MONTH, 1);
        beginDate.set(Calendar.HOUR_OF_DAY, 00);
        beginDate.set(Calendar.MINUTE, 00);
        beginDate.set(Calendar.SECOND, 00);

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);

        List<ChartItem> list = removedStockReportService.monthlyRemovedStock(beginDate.getTime(), endDate.getTime(), branchList);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("monthlyremovedstock(" + "'" + data + "'" + ")");
    }

    public void chartDailyGraphic() {
        List<ChartItem> list = removedStockReportService.dailyRemovedStock(branchList);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("dailyremovedstock(" + "'" + data + "'" + ")");
    }

    public void chartWeeklyGraphic() {
        Calendar beginDate = Calendar.getInstance();
        beginDate.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
        beginDate.set(Calendar.HOUR_OF_DAY, 00);
        beginDate.set(Calendar.MINUTE, 00);
        beginDate.set(Calendar.SECOND, 00);

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
        endDate.add(Calendar.DATE, 6);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);



        List<ChartItem> list = removedStockReportService.weeklyRemovedStock(beginDate.getTime(), endDate.getTime(), branchList);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("weeklyremovedstock(" + "'" + data + "'" + ")");
    }

    public void selectEmploeyee() {
        if (!isClickedEmployee) {
            RequestContext.getCurrentInstance().execute("Centrowiz.panelToggle();");
        }
        selectedUserData = selectedRemovedStockReport.getRemovedStockReport().getUserData();
        isClickedEmployee = true;
        findStockDetail(selectedUserData);

    }

    public void findStockDetail(UserData userData) {
        removedStockReportList = new ArrayList<>();
        removedStockReportList = removedStockReportService.listOfLog(beginDate, endDate, userData, selectedRemovedStockReport.getBranch());
    }

    public void changeShiftDetail() {
        findStockDetail(selectedUserData);
    }

    public void shiftClicked() {
        findStockDetail(selectedUserData);

    }

    public void createPrinter() {
        List<Boolean> tgList = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true);
        }
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(removedStockReportService.exportPrinter(beginDate, endDate, selectedUserData, tgList, selectedRemovedStockReport.getBranch())) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void createExcel() {
        List<Boolean> tgList = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true);
        }
        removedStockReportService.exportExcel(beginDate, endDate, selectedUserData, tgList, selectedRemovedStockReport.getBranch());
    }

    public void createPdf() {
        List<Boolean> tgList = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            tgList = Arrays.asList(true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true);
        }
        removedStockReportService.exportPdf(beginDate, endDate, selectedUserData, tgList, selectedRemovedStockReport.getBranch());
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
    public LazyDataModel<RemovedStock> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void find() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        selectedRemovedStockReport = new RemovedStockReport();
        branchList = "";
        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                    break;
                }
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
                if (branchSetting.getBranch().getId() == 0) {
                    branchList = "";
                    break;
                }
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        changeMonthlyLog();
        
    }

}
