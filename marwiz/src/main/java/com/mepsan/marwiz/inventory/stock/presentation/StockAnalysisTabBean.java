/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 15:30:00
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.inventory.stock.business.IStockAnalysisService;
import com.mepsan.marwiz.inventory.stock.dao.StockAnalysis;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean
@ViewScoped
public class StockAnalysisTabBean {

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockAnalysisService}")
    public IStockAnalysisService stockAnalysisService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private Stock selectedStock;
    private List<StockAnalysis> listOfMonth;
    private List<StockAnalysis> listOfThreeMonth;
    private StockAnalysis selectedObject;
    private LineChartModel lineMonth;
    private LineChartModel lineThreeMonth;
    private Branch branch;
    private List<Branch> listOfBranch;
    private List<StockAnalysis> listOfLastYearPrice;
    private LineChartModel lineYear;
    

    public LineChartModel getLineMonth() {
        return lineMonth;
    }

    public void setLineMonth(LineChartModel lineMonth) {
        this.lineMonth = lineMonth;
    }

    public LineChartModel getLineThreeMonth() {
        return lineThreeMonth;
    }

    public void setLineThreeMonth(LineChartModel lineThreeMonth) {
        this.lineThreeMonth = lineThreeMonth;
    }

    public StockAnalysis getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StockAnalysis selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<StockAnalysis> getListOfMonth() {
        return listOfMonth;
    }

    public void setListOfMonth(List<StockAnalysis> listOfMonth) {
        this.listOfMonth = listOfMonth;
    }

    public List<StockAnalysis> getListOfThreeMonth() {
        return listOfThreeMonth;
    }

    public void setListOfThreeMonth(List<StockAnalysis> listOfThreeMonth) {
        this.listOfThreeMonth = listOfThreeMonth;
    }

    public Stock getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(Stock selectedStock) {
        this.selectedStock = selectedStock;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockAnalysisService(IStockAnalysisService stockAnalysisService) {
        this.stockAnalysisService = stockAnalysisService;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<StockAnalysis> getListOfLastYearPrice() {
        return listOfLastYearPrice;
    }

    public void setListOfLastYearPrice(List<StockAnalysis> listOfLastYearPrice) {
        this.listOfLastYearPrice = listOfLastYearPrice;
    }

   

    public LineChartModel getLineYear() {
        return lineYear;
    }

    public void setLineYear(LineChartModel lineYear) {
        this.lineYear = lineYear;
    }

    @PostConstruct
    public void init() {

        listOfBranch = new ArrayList<>();
        branch = new Branch();
        System.out.println("--StockAnalysisTabBean----");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    selectedStock = (Stock) ((ArrayList) sessionBean.parameter).get(i);

                    listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

                    for (Branch b : listOfBranch) {
                        if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                            branch.setId(b.getId());
                            break;
                        }
                    }

                    find();

                }
            }
        }

    }

    private void createLineModelDays() {
        lineMonth = new LineChartModel();
        lineMonth.setExtender("overrideAxis");
        lineMonth.setLegendPosition("e");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Axis ax = lineMonth.getAxis(AxisType.Y);
        ax.setMin(0);
        ax.setTickFormat("%.2f");

        Axis axx = lineMonth.getAxis(AxisType.X);
        axx.setMin(1);
        axx.setTickFormat("%d");
        axx.setTickCount(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        axx.setTickAngle(45);
        axx.setMax(cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Satış Sayısı");
        series1.setSmoothLine(true);
        listOfMonth.stream().forEach((sa) -> {
            series1.set(sa.getDay(), sa.getLastMonth());
        });
        lineMonth.addSeries(series1);
        lineMonth.setDatatipFormat("<span style='display:none'>%s</span><span>%s</span>");
        lineMonth.setShadow(true);
        lineMonth.setSeriesColors("8784BD, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");
        lineMonth.setAnimate(true);

    }

        private void createLineModelYears() {
        lineYear = new LineChartModel();
        lineYear.setLegendPosition("e");
        Axis ay = lineYear.getAxis(AxisType.Y);
        ay.setMin(0);
        ay.setTickFormat("%.2f");

        lineYear.setExtender("overrideAxis");
        lineYear.setLegendPosition("e");
        lineYear.setShowPointLabels(true);

        Calendar cal = Calendar.getInstance();
        lineYear.getAxes().put(AxisType.X, new CategoryAxis());
        Axis axx = lineYear.getAxis(AxisType.X);
        axx.setTickAngle(45);
        axx.setTickFormat("%d");

        LineChartSeries lcs = new LineChartSeries();
        lcs.setLabel("Satış Fiyatı ");
        
        listOfLastYearPrice.stream().forEach((sa) -> {
            lcs.set(sa.getMonth(), sa.getLastSalePrice());
        });
        lcs.setSmoothLine(true);
        lineYear.addSeries(lcs);
        lineYear.setDatatipFormat("<span style='display:none'>%s</span><span>%s</span>");
        lineYear.setShadow(true);
        lineYear.setAnimate(true);
        lineYear.setSeriesColors("E90C45  , CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");

        
            
            for (StockAnalysis listOfYearPrices : listOfLastYearPrice) {
                System.out.println("----listOfLastYearPrice------" + listOfYearPrices.getLastSalePrice());
                System.out.println("----mohtn------" + listOfYearPrices.getMonth());
            }
    }

    private void createLineModelMonths() {

        lineThreeMonth = new LineChartModel();
        lineThreeMonth.setLegendPosition("e");
        Axis ax = lineThreeMonth.getAxis(AxisType.Y);
        ax.setMin(0);
        ax.setTickFormat("%.2f");

        lineThreeMonth.setExtender("overrideAxis");
        lineThreeMonth.setLegendPosition("e");
        lineThreeMonth.setShowPointLabels(true);
        lineThreeMonth.getAxes().put(AxisType.X, new CategoryAxis());
        Axis ay = lineThreeMonth.getAxis(AxisType.Y);
        ay.setMin(0);

        LineChartSeries lcs = new LineChartSeries();
        lcs.setLabel("Satış Sayısı");
        lcs.setSmoothLine(true);
        listOfThreeMonth.stream().forEach((sa) -> {
            lcs.set(sa.getMonth() + (sa.getYear() * 100), sa.getLastMonth());
        });
        
        for (StockAnalysis sa : listOfThreeMonth) {
                System.out.println("----listOfThreeMonth-----" + sa.getMonth() + (sa.getYear() * 100));
                System.out.println("----listOfThreeMonth------" + sa.getLastMonth());
            }
        lineThreeMonth.addSeries(lcs);
        lineThreeMonth.setDatatipFormat("<span style='display:none'>%s</span><span>%s</span>");
        lineThreeMonth.setShadow(true);
        lineThreeMonth.setSeriesColors("FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");
        lineThreeMonth.setAnimate(true);

    }

    public void find() {
        listOfMonth = stockAnalysisService.listOfMonthAverage(selectedStock, branch);
        listOfThreeMonth = stockAnalysisService.listOfThreeMonthAverage(selectedStock, branch);
        selectedObject = stockAnalysisService.selectStockAnalysis(selectedStock, branch);
        listOfLastYearPrice = stockAnalysisService.listOfYearAverage(selectedStock, branch);

        createLineModelDays();
        createLineModelMonths();
        createLineModelYears();
    }

}
