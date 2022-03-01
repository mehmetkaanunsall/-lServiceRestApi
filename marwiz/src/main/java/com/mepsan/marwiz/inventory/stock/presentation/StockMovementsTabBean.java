/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 15:30:38
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.stock.business.IStockMovementService;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean
@ViewScoped
public class StockMovementsTabBean extends GeneralReportBean<StockMovement> {

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockMovementService}")
    public IStockMovementService stockMovementService;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{invoiceService}")
    public IInvoiceService invoiceService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    private List<StockMovement> listOfMovement;
    private List<StockMovement> listOfWarehouseAva;
    private Stock selectedStock;
    private BarChartModel warehouseChart;
    private Date beginDate, endDate;
    private int opType;
    private List<Warehouse> listOfWarehouses;
    private Warehouse selectedWarehouse;
    private List<Branch> selectedBranchList;
    private List<Branch> listOfBranch;
    private BigDecimal totalIncoming, totalOutcoming, remainingAmount, transferAmount;

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Warehouse getSelectedWarehouse() {
        return selectedWarehouse;
    }

    public void setSelectedWarehouse(Warehouse selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
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

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public List<Warehouse> getListOfWarehouses() {
        return listOfWarehouses;
    }

    public void setListOfWarehouses(List<Warehouse> listOfWarehouses) {
        this.listOfWarehouses = listOfWarehouses;
    }

    public BarChartModel getWarehouseChart() {
        return warehouseChart;
    }

    public void setWarehouseChart(BarChartModel warehouseChart) {
        this.warehouseChart = warehouseChart;
    }

    public List<StockMovement> getListOfWarehouseAva() {
        return listOfWarehouseAva;
    }

    public void setListOfWarehouseAva(List<StockMovement> listOfWarehouseAva) {
        this.listOfWarehouseAva = listOfWarehouseAva;
    }

    public Stock getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(Stock selectedStock) {
        this.selectedStock = selectedStock;
    }

    public List<StockMovement> getListOfMovement() {
        return listOfMovement;
    }

    public void setListOfMovement(List<StockMovement> listOfMovement) {
        this.listOfMovement = listOfMovement;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockMovementService(IStockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    public List<Branch> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<Branch> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public BigDecimal getTotalIncoming() {
        return totalIncoming;
    }

    public void setTotalIncoming(BigDecimal totalIncoming) {
        this.totalIncoming = totalIncoming;
    }

    public BigDecimal getTotalOutcoming() {
        return totalOutcoming;
    }

    public void setTotalOutcoming(BigDecimal totalOutcoming) {
        this.totalOutcoming = totalOutcoming;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    @PostConstruct
    @Override
    public void init() {

        selectedWarehouse = new Warehouse();
        selectedObject = new StockMovement();
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        listOfWarehouses = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);

        System.out.println("--StockMovementTabBean----");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    selectedStock = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    setOpType(3);
                    Calendar calendar = GregorianCalendar.getInstance();
                    setEndDate(new Date());
                    calendar.setTime(getEndDate());
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    setEndDate(calendar.getTime());

                    calendar.setTime(getEndDate());
                    calendar.add(Calendar.MONTH, -1);
                    calendar.set(Calendar.HOUR_OF_DAY, 00);
                    calendar.set(Calendar.MINUTE, 00);
                    calendar.set(Calendar.SECOND, 00);
                    setBeginDate(calendar.getTime());

                    listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

                    if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                        for (Branch br : listOfBranch) {
                            selectedBranchList.add(br);
                        }
                    } else {
                        for (Branch br : listOfBranch) {
                            if (br.getId() == sessionBean.getUser().getLastBranch().getId()) {
                                selectedBranchList.add(br);
                                break;
                            }
                        }
                    }

                    changeBranch();

                    find();
                }
            }
        }

    }

    @Override
    public void find() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvStokProc:frmMovements:dtbItems");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(" ");
        listOfWarehouseAva = stockMovementService.listOfWarehouseAvailability(selectedStock, selectedWarehouse, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
        if (selectedWarehouse.getId() != 0) {
            for (Warehouse wh : listOfWarehouses) {
                if (selectedWarehouse.getId() == wh.getId()) {
                    selectedWarehouse.setName(wh.getName());
                }
            }
        }

        createPieChartModel();

    }

    @Override
    public LazyDataModel<StockMovement> findall(String where) {

        return new CentrowizLazyDataModel<StockMovement>() {

            @Override
            public List<StockMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {

                List<StockMovement> result = stockMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, "", selectedStock, opType, beginDate, endDate, selectedWarehouse, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
                StockMovement stockMovement = stockMovementService.count("", selectedStock, opType, beginDate, endDate, selectedWarehouse, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList);
                listOfObjects.setRowCount(stockMovement.getId());
                calculate(stockMovement);
                return result;
            }
        };
    }

    public void calculate(StockMovement result) {
        remainingAmount = BigDecimal.valueOf(0);
        totalOutcoming = BigDecimal.valueOf(0);
        totalIncoming = BigDecimal.valueOf(0);
        transferAmount = BigDecimal.valueOf(0);

        totalIncoming = result.getTotalIncoming();
        totalOutcoming = result.getTotalOutcoming();
        transferAmount = result.getTransferAmount();
        remainingAmount = transferAmount.add(totalIncoming.subtract(totalOutcoming));

    }

    public void createPieChartModel() {
        warehouseChart = new BarChartModel();

        warehouseChart.setExtender("overrideAxis");

        warehouseChart.setShadow(true);
        warehouseChart.setSeriesColors("FFC266, D284BD, 8784BD, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");
        warehouseChart.setAnimate(true);
        warehouseChart.setLegendRows(0);
        Axis ay = warehouseChart.getAxis(AxisType.Y);
        ay.setTickFormat("%.2f");
        Axis ax = warehouseChart.getAxis(AxisType.X);
        ax.setTickAngle(30);
        warehouseChart.setDatatipFormat("<span style='display:none'>%s</span><span>%s</span>");

        warehouseChart.getSeries().clear();

        if (listOfWarehouseAva.size() > 0) {

            ChartSeries chartSeries2 = new ChartSeries();
            chartSeries2.setLabel("");
            listOfWarehouseAva.stream().forEach((move) -> {
                chartSeries2.set(move.getWarehouse().getName(), move.getQuantity());
            });
            warehouseChart.getSeries().add(chartSeries2);

        } else {
            ChartSeries cs = new ChartSeries();
            cs.setLabel("NA");
            cs.set("", 0);
            warehouseChart.addSeries(cs);
        }

    }

    public void gotoDetail() {
        List<Object> list = new ArrayList<>();
        list.addAll((ArrayList) sessionBean.getParameter());
        
        
        if (selectedObject.getProcessType() == 7 && selectedObject.getInvoice().getId() != 0) {//Fatura İşlemleri
            selectedObject.setInvoice(invoiceService.findInvoice(selectedObject.getInvoice()));
            list.add(selectedObject.getInvoice());
            marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 1, 26);
        }
        
        
        

    }

    @Override
    public void create() {

    }

    @Override
    public void save() {

    }

    public void generalFilter() {
    }

    public void detailFilter() {
    }

    public void createPdf() throws IOException {
        stockMovementService.exportPdf("", selectedObject, toogleList, selectedStock, opType, beginDate, endDate, selectedWarehouse, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, totalIncoming, totalOutcoming, remainingAmount, transferAmount);
    }

    public void createExcel() throws IOException {
        stockMovementService.exportExcel("", selectedObject, toogleList, selectedStock, opType, beginDate, endDate, selectedWarehouse, selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, totalIncoming, totalOutcoming, remainingAmount, transferAmount);
    }

    public void changeBranch() {

        String branchList = "";
        String branchWhere = "";
        if (selectedBranchList.isEmpty()) {
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }

        } else {
            for (Branch br : selectedBranchList) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            branchWhere = branchWhere + " AND iw.branch_id IN(" + branchList + ") ";
        }
        listOfWarehouses = warehouseService.selectListAllWarehouse(branchWhere);
    }
}
