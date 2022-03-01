/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:16:33 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.presentation;

import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.business.IStationSalesSummaryReportService;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.dao.StationSalesSummaryReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class StationSalesSummaryReportBean {

    @ManagedProperty(value = "#{stationSalesSummaryReportService}")
    public IStationSalesSummaryReportService stationSalesSummaryReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{creditService}")
    public ICreditService creditService;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private StationSalesSummaryReport selectedObject;
    private List<StationSalesSummaryReport> listOfFuelSales;
    private List<StationSalesSummaryReport> listOfFuelCollections;
    private List<StationSalesSummaryReport> listOfMarketSales;
    private List<StationSalesSummaryReport> listOfMarketCollections;
    private List<StationSalesSummaryReport> listOfTotalSales;
    private List<StationSalesSummaryReport> listOfTotalCollections;
    private List<StationSalesSummaryReport> listOfFuelOutherMoney;
    private List<StationSalesSummaryReport> listOfMarketOutherMoney;
    private List<StationSalesSummaryReport> listOfTotals;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsMarketCollection;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsSales;
    private Map<Integer, StationSalesSummaryReport> currencyTotalsCollection;
    private boolean isFind;
    private boolean renderExportButton = true;
    private BigDecimal fuelSaleTotalLiter, fuelSaleTotalMoney, fuelCollectionTotalMoney, marketSalesTotalPrice, marketCollectionTotalPrices, generalSalesTotal, generalCollectionTotal;
    private BigDecimal marketDeptTotal, incomeExpenceMoney, moreMoney, accountCollectionPayment;

    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private int branchId;
    private boolean isThereListBranch;
    private String createWhere;

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Integer, StationSalesSummaryReport> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsSales() {
        return currencyTotalsSales;
    }

    public void setCurrencyTotalsSales(Map<Integer, StationSalesSummaryReport> currencyTotalsSales) {
        this.currencyTotalsSales = currencyTotalsSales;
    }

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsMarketSales() {
        return currencyTotalsMarketSales;
    }

    public void setCurrencyTotalsMarketSales(Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales) {
        this.currencyTotalsMarketSales = currencyTotalsMarketSales;
    }

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsMarketCollection() {
        return currencyTotalsMarketCollection;
    }

    public void setCurrencyTotalsMarketCollection(Map<Integer, StationSalesSummaryReport> currencyTotalsMarketCollection) {
        this.currencyTotalsMarketCollection = currencyTotalsMarketCollection;
    }

    public List<StationSalesSummaryReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<StationSalesSummaryReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsFuelSales() {
        return currencyTotalsFuelSales;
    }

    public void setCurrencyTotalsFuelSales(Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales) {
        this.currencyTotalsFuelSales = currencyTotalsFuelSales;
    }

    public Map<Integer, StationSalesSummaryReport> getCurrencyTotalsFuelCollection() {
        return currencyTotalsFuelCollection;
    }

    public void setCurrencyTotalsFuelCollection(Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection) {
        this.currencyTotalsFuelCollection = currencyTotalsFuelCollection;
    }

    public List<StationSalesSummaryReport> getListOfMarketOutherMoney() {
        return listOfMarketOutherMoney;
    }

    public void setListOfMarketOutherMoney(List<StationSalesSummaryReport> listOfMarketOutherMoney) {
        this.listOfMarketOutherMoney = listOfMarketOutherMoney;
    }

    public List<StationSalesSummaryReport> getListOfFuelOutherMoney() {
        return listOfFuelOutherMoney;
    }

    public void setListOfFuelOutherMoney(List<StationSalesSummaryReport> listOfFuelOutherMoney) {
        this.listOfFuelOutherMoney = listOfFuelOutherMoney;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
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

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public void setStationSalesSummaryReportService(IStationSalesSummaryReportService stationSalesSummaryReportService) {
        this.stationSalesSummaryReportService = stationSalesSummaryReportService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCreditService(ICreditService creditService) {
        this.creditService = creditService;
    }

    public StationSalesSummaryReport getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(StationSalesSummaryReport selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<StationSalesSummaryReport> getListOfFuelSales() {
        return listOfFuelSales;
    }

    public void setListOfFuelSales(List<StationSalesSummaryReport> listOfFuelSales) {
        this.listOfFuelSales = listOfFuelSales;
    }

    public List<StationSalesSummaryReport> getListOfFuelCollections() {
        return listOfFuelCollections;
    }

    public void setListOfFuelCollections(List<StationSalesSummaryReport> listOfFuelCollections) {
        this.listOfFuelCollections = listOfFuelCollections;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public boolean isRenderExportButton() {
        return renderExportButton;
    }

    public void setRenderExportButton(boolean renderExportButton) {
        this.renderExportButton = renderExportButton;
    }

    public BigDecimal getFuelSaleTotalLiter() {
        return fuelSaleTotalLiter;
    }

    public void setFuelSaleTotalLiter(BigDecimal fuelSaleTotalLiter) {
        this.fuelSaleTotalLiter = fuelSaleTotalLiter;
    }

    public BigDecimal getFuelSaleTotalMoney() {
        return fuelSaleTotalMoney;
    }

    public void setFuelSaleTotalMoney(BigDecimal fuelSaleTotalMoney) {
        this.fuelSaleTotalMoney = fuelSaleTotalMoney;
    }

    public BigDecimal getFuelCollectionTotalMoney() {
        return fuelCollectionTotalMoney;
    }

    public void setFuelCollectionTotalMoney(BigDecimal fuelCollectionTotalMoney) {
        this.fuelCollectionTotalMoney = fuelCollectionTotalMoney;
    }

    public List<StationSalesSummaryReport> getListOfMarketSales() {
        return listOfMarketSales;
    }

    public void setListOfMarketSales(List<StationSalesSummaryReport> listOfMarketSales) {
        this.listOfMarketSales = listOfMarketSales;
    }

    public BigDecimal getMarketSalesTotalPrice() {
        return marketSalesTotalPrice;
    }

    public void setMarketSalesTotalPrice(BigDecimal marketSalesTotalPrice) {
        this.marketSalesTotalPrice = marketSalesTotalPrice;
    }

    public List<StationSalesSummaryReport> getListOfMarketCollections() {
        return listOfMarketCollections;
    }

    public void setListOfMarketCollections(List<StationSalesSummaryReport> listOfMarketCollections) {
        this.listOfMarketCollections = listOfMarketCollections;
    }

    public BigDecimal getMarketCollectionTotalPrices() {
        return marketCollectionTotalPrices;
    }

    public void setMarketCollectionTotalPrices(BigDecimal marketCollectionTotalPrices) {
        this.marketCollectionTotalPrices = marketCollectionTotalPrices;
    }

    public BigDecimal getMarketDeptTotal() {
        return marketDeptTotal;
    }

    public void setMarketDeptTotal(BigDecimal marketDeptTotal) {
        this.marketDeptTotal = marketDeptTotal;
    }

    public List<StationSalesSummaryReport> getListOfTotalSales() {
        return listOfTotalSales;
    }

    public void setListOfTotalSales(List<StationSalesSummaryReport> listOfTotalSales) {
        this.listOfTotalSales = listOfTotalSales;
    }

    public BigDecimal getGeneralSalesTotal() {
        return generalSalesTotal;
    }

    public void setGeneralSalesTotal(BigDecimal generalSalesTotal) {
        this.generalSalesTotal = generalSalesTotal;
    }

    public List<StationSalesSummaryReport> getListOfTotalCollections() {
        return listOfTotalCollections;
    }

    public void setListOfTotalCollections(List<StationSalesSummaryReport> listOfTotalCollections) {
        this.listOfTotalCollections = listOfTotalCollections;
    }

    public BigDecimal getGeneralCollectionTotal() {
        return generalCollectionTotal;
    }

    public void setGeneralCollectionTotal(BigDecimal generalCollectionTotal) {
        this.generalCollectionTotal = generalCollectionTotal;
    }

    public BigDecimal getIncomeExpenceMoney() {
        return incomeExpenceMoney;
    }

    public void setIncomeExpenceMoney(BigDecimal incomeExpenceMoney) {
        this.incomeExpenceMoney = incomeExpenceMoney;
    }

    public BigDecimal getMoreMoney() {
        return moreMoney;
    }

    public void setMoreMoney(BigDecimal moreMoney) {
        this.moreMoney = moreMoney;
    }

    public BigDecimal getAccountCollectionPayment() {
        return accountCollectionPayment;
    }

    public void setAccountCollectionPayment(BigDecimal accountCollectionPayment) {
        this.accountCollectionPayment = accountCollectionPayment;
    }

    @PostConstruct
    public void init() {
        System.out.println("----StationSalesSummaryReportBean----");

        selectedObject = new StationSalesSummaryReport();

        isFind = false;
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());

        listOfFuelSales = new ArrayList<>();
        listOfFuelCollections = new ArrayList<>();
        listOfMarketSales = new ArrayList<>();
        listOfMarketCollections = new ArrayList<>();
        listOfTotalSales = new ArrayList<>();
        listOfTotalCollections = new ArrayList<>();
        listOfFuelOutherMoney = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        currencyTotalsFuelCollection = new HashMap<>();
        currencyTotalsFuelSales = new HashMap<>();
        currencyTotalsMarketSales = new HashMap<>();
        currencyTotalsMarketCollection = new HashMap<>();
        currencyTotalsSales = new HashMap<>();
        currencyTotalsCollection = new HashMap<>();

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

    /**
     * Belirlenen tarihler arasında arama yapar.
     */
    public void find() {
        isFind = true;
        createWhere = stationSalesSummaryReportService.createWhere(selectedBranchList);
        listOfFuelSales.clear();
        listOfFuelCollections.clear();
        listOfMarketSales.clear();
        listOfMarketCollections.clear();
        listOfTotalSales.clear();
        listOfTotalCollections.clear();
        listOfFuelOutherMoney.clear();
        listOfTotals.clear();

        currencyTotalsFuelSales.clear();
        currencyTotalsFuelCollection.clear();
        currencyTotalsMarketSales.clear();
        currencyTotalsMarketCollection.clear();
        currencyTotalsSales.clear();
        currencyTotalsCollection.clear();

        findFuelCollections();
        //   findFuelSales();
        findMarketCollections();
        //  findMarketSales();

        totals(1);
        totals(2);
        totals(3);
        totals(4);

        findTotalSales();
        totals(5);
        findTotalCollections();
        totals(6);

    }

    /**
     * Akaryakıt satışlarını çeker.
     */
    public void findFuelSales() {
        listOfFuelSales = stationSalesSummaryReportService.findFuelSales(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);
        fuelSaleTotalMoney = fuelSaleTotalLiter = BigDecimal.ZERO;

    }

    /**
     * Akaryakıt tahsilatlarını çeker
     */
    public void findFuelCollections() {
        listOfFuelCollections = stationSalesSummaryReportService.findFuelCollections(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);

        listOfFuelOutherMoney = stationSalesSummaryReportService.findFuelSalesOutherMoney(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);

        moreMoney = BigDecimal.ZERO;
        incomeExpenceMoney = BigDecimal.ZERO;
        fuelCollectionTotalMoney = BigDecimal.ZERO;
        accountCollectionPayment = BigDecimal.ZERO;

        findFuelSales();

        for (Iterator<StationSalesSummaryReport> iterator = listOfFuelCollections.iterator(); iterator.hasNext();) {
            StationSalesSummaryReport next = iterator.next();
            if (next.getFuelSaleTypeId() == 0) {
                iterator.remove();
            }
        }

        for (StationSalesSummaryReport summaryReport : listOfFuelOutherMoney) {

            if (summaryReport.getMoreMoney().compareTo(BigDecimal.ZERO) > 0) {// pompacı fazlası satış listesine eklenir.
                StationSalesSummaryReport moremoneyObj = new StationSalesSummaryReport();
                moremoneyObj.setFuelStockName(sessionBean.getLoc().getString("attendantsurplus"));
                moremoneyObj.setFuelStockQuantity(BigDecimal.ZERO);
                moremoneyObj.setFuelStockUnitPrice(BigDecimal.ZERO);
                moremoneyObj.setAccountCollectionPaymentMoney(BigDecimal.ZERO);
                moremoneyObj.setFuelStockSalesTotal(summaryReport.getMoreMoney());
                moremoneyObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                moremoneyObj.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());
                listOfFuelSales.add(moremoneyObj);

            }

            if (summaryReport.getMoreMoney().compareTo(BigDecimal.ZERO) < 0) { // pompacı açığı tahsilat listesine eklenir.
                StationSalesSummaryReport moremoneyObj = new StationSalesSummaryReport();
                moremoneyObj.setFuelCollectionSalesTotal(summaryReport.getMoreMoney());
                moremoneyObj.setMoreMoney(BigDecimal.ZERO);
                moremoneyObj.setIncomeExpenseMoney(BigDecimal.ZERO);
                moremoneyObj.setAccountCollectionPaymentMoney(BigDecimal.ZERO);
                moremoneyObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                moremoneyObj.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                if (moremoneyObj.getFuelCollectionSalesTotal().compareTo(BigDecimal.ZERO) < 0) {
                    moremoneyObj.setFuelCollectionName(sessionBean.getLoc().getString("attendantmissingamount"));
                }
                listOfFuelCollections.add(moremoneyObj);
            }

            if (summaryReport.getIncomeExpenseMoney().compareTo(BigDecimal.ZERO) > 0) { // gelir satış listesine eklendi.
                StationSalesSummaryReport moremoneyObj = new StationSalesSummaryReport();
                moremoneyObj.setFuelStockName(sessionBean.getLoc().getString("income"));
                moremoneyObj.setFuelStockQuantity(BigDecimal.ZERO);
                moremoneyObj.setFuelStockUnitPrice(BigDecimal.ZERO);
                moremoneyObj.setFuelStockSalesTotal(summaryReport.getIncomeExpenseMoney());
                moremoneyObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                moremoneyObj.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                listOfFuelSales.add(moremoneyObj);
            }

            if (summaryReport.getIncomeExpenseMoney().compareTo(BigDecimal.ZERO) < 0) { // gider tahsilat listesine eklendi.
                StationSalesSummaryReport incomeExpenceMoneyObj = new StationSalesSummaryReport();
                incomeExpenceMoneyObj.setFuelCollectionSalesTotal(summaryReport.getIncomeExpenseMoney());
                incomeExpenceMoneyObj.setMoreMoney(BigDecimal.ZERO);
                incomeExpenceMoneyObj.setIncomeExpenseMoney(BigDecimal.ZERO);
                incomeExpenceMoneyObj.setAccountCollectionPaymentMoney(BigDecimal.ZERO);
                incomeExpenceMoneyObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                incomeExpenceMoneyObj.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                if (incomeExpenceMoneyObj.getFuelCollectionSalesTotal().compareTo(BigDecimal.ZERO) < 0) {
                    incomeExpenceMoneyObj.setFuelCollectionName(sessionBean.getLoc().getString("otherexpenses"));
                }
                listOfFuelCollections.add(incomeExpenceMoneyObj);
            }

            if (summaryReport.getAccountCollectionPaymentMoney().compareTo(BigDecimal.ZERO) > 0) { // cari tahsilat satış listesine eklendi.
                StationSalesSummaryReport accountCollection = new StationSalesSummaryReport();
                accountCollection.setFuelStockName(sessionBean.getLoc().getString("accountcollection"));
                accountCollection.setFuelStockQuantity(BigDecimal.ZERO);
                accountCollection.setFuelStockUnitPrice(BigDecimal.ZERO);
                accountCollection.setFuelStockSalesTotal(summaryReport.getAccountCollectionPaymentMoney());
                accountCollection.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                accountCollection.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                listOfFuelSales.add(accountCollection);
            }

            if (summaryReport.getAccountCollectionPaymentMoney().compareTo(BigDecimal.ZERO) < 0) { // cari ödeme tahsilat listesine eklendi.
                
                StationSalesSummaryReport accountPayment = new StationSalesSummaryReport();
                accountPayment.setFuelCollectionSalesTotal(summaryReport.getAccountCollectionPaymentMoney());
                accountPayment.setMoreMoney(BigDecimal.ZERO);
                accountPayment.setIncomeExpenseMoney(BigDecimal.ZERO);
                accountPayment.setAccountCollectionPaymentMoney(BigDecimal.ZERO);
                accountPayment.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                accountPayment.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                if (accountPayment.getFuelCollectionSalesTotal().compareTo(BigDecimal.ZERO) < 0) {
                    accountPayment.setFuelCollectionName(sessionBean.getLoc().getString("accountpayment"));
                }
                listOfFuelCollections.add(accountPayment);
            }

        }

        for (StationSalesSummaryReport fuelCollections : listOfFuelCollections) {
            moreMoney = moreMoney.add(fuelCollections.getMoreMoney());
            incomeExpenceMoney = incomeExpenceMoney.add(fuelCollections.getIncomeExpenseMoney());
            accountCollectionPayment = accountCollectionPayment.add(fuelCollections.getAccountCollectionPaymentMoney());
        }

        for (StationSalesSummaryReport fuelCollections : listOfFuelCollections) { // alt toplam 
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.add(fuelCollections.getFuelCollectionSalesTotal());
            Unit unit = new Unit();
            unit.setId(37);
            unit.setSortName("LT");
            fuelCollections.setStockUnit(unit);
        }

        for (StationSalesSummaryReport listOfFuelSale : listOfFuelSales) { // tüm tutarları toplar
            fuelSaleTotalLiter = fuelSaleTotalLiter.add(listOfFuelSale.getFuelStockQuantity());
            fuelSaleTotalMoney = fuelSaleTotalMoney.add(listOfFuelSale.getFuelStockSalesTotal());
        }

        if (incomeExpenceMoney.compareTo(BigDecimal.ZERO) > 0) { // gelir mi gider mi ona göre ekleme yada çıkartma yapar totalden
            fuelSaleTotalMoney = fuelSaleTotalMoney.subtract(incomeExpenceMoney);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(incomeExpenceMoney);
        } else {
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(incomeExpenceMoney);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(incomeExpenceMoney);
        }

        if (moreMoney.compareTo(BigDecimal.ZERO) > 0) {
            fuelSaleTotalMoney = fuelSaleTotalMoney.subtract(moreMoney);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(moreMoney);
        } else {
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(moreMoney);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(moreMoney);

        }
        
         if (accountCollectionPayment.compareTo(BigDecimal.ZERO) > 0) {
            fuelSaleTotalMoney = fuelSaleTotalMoney.subtract(accountCollectionPayment);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(accountCollectionPayment);
        } else {
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(accountCollectionPayment);
            fuelCollectionTotalMoney = fuelCollectionTotalMoney.subtract(accountCollectionPayment);

        }
        

    }

    public Map<Integer, StationSalesSummaryReport> totals(int type) {

        String total = "";

        switch (type) {

            case 1: // Akaryakıt satışları
                for (StationSalesSummaryReport stationSalesSummaryReport : listOfFuelSales) {

                    if (currencyTotalsFuelSales.containsKey(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setBranchSetting(currencyTotalsFuelSales.get(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId()).getBranchSetting());
                        old.setFuelStockSalesTotal(currencyTotalsFuelSales.get(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId()).getFuelStockSalesTotal());
                        old.setFuelStockQuantity(currencyTotalsFuelSales.get(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId()).getFuelStockQuantity());

                        old.setFuelStockSalesTotal(old.getFuelStockSalesTotal().add(stationSalesSummaryReport.getFuelStockSalesTotal()));
                        old.setFuelStockQuantity(old.getFuelStockQuantity().add(stationSalesSummaryReport.getFuelStockQuantity()));

                        currencyTotalsFuelSales.put(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();

                        oldNew.setBranchSetting(stationSalesSummaryReport.getBranchSetting());
                        oldNew.setFuelStockSalesTotal(stationSalesSummaryReport.getFuelStockSalesTotal());
                        oldNew.setFuelStockQuantity(stationSalesSummaryReport.getFuelStockQuantity());

                        currencyTotalsFuelSales.put(stationSalesSummaryReport.getBranchSetting().getBranch().getCurrency().getId(), oldNew);
                    }
                }
                break;

            case 2: // Akaryakıt Tahsilatları
                for (StationSalesSummaryReport st : listOfFuelCollections) {

                    if (currencyTotalsFuelCollection.containsKey(st.getBranchSetting().getBranch().getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setCurrency(currencyTotalsFuelCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getCurrency());

                        old.setBranchSetting(currencyTotalsFuelCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getBranchSetting());
                        old.setFuelCollectionSalesTotal(currencyTotalsFuelCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getFuelCollectionSalesTotal());

                        old.setFuelCollectionSalesTotal(old.getFuelCollectionSalesTotal().add(st.getFuelCollectionSalesTotal()));

                        currencyTotalsFuelCollection.put(st.getBranchSetting().getBranch().getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();
                        oldNew.setCurrency(st.getBranchSetting().getBranch().getCurrency());
                        oldNew.setBranchSetting(st.getBranchSetting());
                        oldNew.setFuelCollectionSalesTotal(st.getFuelCollectionSalesTotal());

                        currencyTotalsFuelCollection.put(st.getBranchSetting().getBranch().getCurrency().getId(), oldNew);
                    }

                }
                break;

            case 3:

                for (StationSalesSummaryReport st : listOfMarketSales) {

                    if (currencyTotalsMarketSales.containsKey(st.getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setCurrency(currencyTotalsMarketSales.get(st.getCurrency().getId()).getCurrency());
                        old.setMarketSaleTotalMoney(currencyTotalsMarketSales.get(st.getCurrency().getId()).getMarketSaleTotalMoney());

                        old.setMarketSaleTotalMoney(old.getMarketSaleTotalMoney().add(st.getMarketSaleTotalMoney()));
                        currencyTotalsMarketSales.put(st.getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();

                        oldNew.setCurrency(st.getCurrency());
                        oldNew.setMarketSaleTotalMoney(st.getMarketSaleTotalMoney());

                        currencyTotalsMarketSales.put(st.getCurrency().getId(), oldNew);
                    }

                }
                break;

            case 4:

                for (StationSalesSummaryReport st : listOfMarketCollections) {

                    if (currencyTotalsMarketCollection.containsKey(st.getBranchSetting().getBranch().getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setCurrency(currencyTotalsMarketCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getCurrency());
                        old.setBranchSetting(currencyTotalsMarketCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getBranchSetting());
                        old.setMarketCollectionTotalMoney(currencyTotalsMarketCollection.get(st.getBranchSetting().getBranch().getCurrency().getId()).getMarketCollectionTotalMoney());

                        old.setMarketCollectionTotalMoney(old.getMarketCollectionTotalMoney().add(st.getMarketCollectionTotalMoney()));
                        currencyTotalsMarketCollection.put(st.getBranchSetting().getBranch().getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();
                        oldNew.setCurrency(st.getBranchSetting().getBranch().getCurrency());
                        oldNew.setBranchSetting(st.getBranchSetting());
                        oldNew.setMarketCollectionTotalMoney(st.getMarketCollectionTotalMoney());

                        currencyTotalsMarketCollection.put(st.getBranchSetting().getBranch().getCurrency().getId(), oldNew);
                    }

                }
                break;

            case 5:

                for (StationSalesSummaryReport st : listOfTotalSales) {

                    if (currencyTotalsSales.containsKey(st.getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setCurrency(currencyTotalsSales.get(st.getCurrency().getId()).getCurrency());
                        old.setTotalSalesPrice(currencyTotalsSales.get(st.getCurrency().getId()).getTotalSalesPrice());

                        old.setTotalSalesPrice(old.getTotalSalesPrice().add(st.getTotalSalesPrice()));
                        currencyTotalsSales.put(st.getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();
                        oldNew.setCurrency(st.getCurrency());
                        oldNew.setTotalSalesPrice(st.getTotalSalesPrice());

                        currencyTotalsSales.put(st.getCurrency().getId(), oldNew);

                    }

                }
                break;

            case 6:

                for (StationSalesSummaryReport st : listOfTotalCollections) {

                    if (currencyTotalsCollection.containsKey(st.getCurrency().getId())) {

                        StationSalesSummaryReport old = new StationSalesSummaryReport();
                        old.setCurrency(currencyTotalsCollection.get(st.getCurrency().getId()).getCurrency());
                        old.setTotalCollectionPrice(currencyTotalsCollection.get(st.getCurrency().getId()).getTotalCollectionPrice());

                        old.setTotalCollectionPrice(old.getTotalCollectionPrice().add(st.getTotalCollectionPrice()));
                        currencyTotalsCollection.put(st.getCurrency().getId(), old);

                    } else {

                        StationSalesSummaryReport oldNew = new StationSalesSummaryReport();

                        oldNew.setCurrency(st.getCurrency());
                        oldNew.setTotalCollectionPrice(st.getTotalCollectionPrice());

                        currencyTotalsCollection.put(st.getCurrency().getId(), oldNew);
                    }

                }
                break;

            default:
                break;

        }

        if (type == 1) {
            return currencyTotalsFuelSales;
        } else if (type == 2) {
            return currencyTotalsFuelCollection;

        } else if (type == 3) {

            return currencyTotalsMarketSales;
        } else if (type == 4) {

            return currencyTotalsMarketCollection;
        } else if (type == 5) {

            return currencyTotalsSales;
        } else if (type == 6) {
            return currencyTotalsCollection;
        } else {
            return null;
        }
    }

    /**
     * Market satışları çekilir.
     */
    public void findMarketSales() {
        listOfMarketSales = stationSalesSummaryReportService.findMarketSales(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);

        if (listOfMarketSales.size() == 0) {
            StationSalesSummaryReport salesSummaryReport = new StationSalesSummaryReport();
            salesSummaryReport.setSalesTypeName(sessionBean.getLoc().getString("sundry"));
            salesSummaryReport.setMarketSaleTotalMoney(BigDecimal.ZERO);
            salesSummaryReport.setMarketSalesQuantity(BigDecimal.ZERO);

            listOfMarketSales.add(salesSummaryReport);
        } else {
            for (StationSalesSummaryReport listOfMarketSale : listOfMarketSales) {
                listOfMarketSale.setSalesTypeName(sessionBean.getLoc().getString("sundry"));
            }
        }

        marketSalesTotalPrice = BigDecimal.ZERO;

    }

    /**
     * Market tahsilatları çekilir.
     */
    public void findMarketCollections() {
        listOfMarketCollections = stationSalesSummaryReportService.findMarketCollections(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);
        listOfMarketOutherMoney = stationSalesSummaryReportService.findMarketSalesOutherMoney(selectedObject.getBeginDate(), selectedObject.getEndDate(), createWhere, selectedBranchList);

        marketCollectionTotalPrices = BigDecimal.ZERO;
        marketDeptTotal = BigDecimal.ZERO;

        findMarketSales();

        for (StationSalesSummaryReport summaryReport : listOfMarketOutherMoney) {

            if (summaryReport.getMarketDeptMoney().compareTo(BigDecimal.ZERO) > 0) { //fazla varsa mevcut listeye eklenir
                StationSalesSummaryReport moremoneyObj = new StationSalesSummaryReport();
                moremoneyObj.setSalesTypeName(sessionBean.getLoc().getString("cashierreceivableamount"));
                moremoneyObj.setMarketSalesQuantity(BigDecimal.ZERO);
                moremoneyObj.setMarketSaleTotalMoney(summaryReport.getMarketDeptMoney());
                moremoneyObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                moremoneyObj.getCurrency().setId(summaryReport.getCurrency().getId());

                listOfMarketSales.add(moremoneyObj);

            }

            if (summaryReport.getMarketDeptMoney().compareTo(BigDecimal.ZERO) < 0) { // eğer açık varsa tahsilat listesine eklenir.
                StationSalesSummaryReport deptObj = new StationSalesSummaryReport();
                deptObj.setMarketCollectionTypeName(sessionBean.getLoc().getString("cashierdebtamount"));
                deptObj.setMarketCollectionTotalMoney(summaryReport.getMarketDeptMoney());
                deptObj.setMarketDeptMoney(BigDecimal.ZERO);
                deptObj.getBranchSetting().getBranch().setName(summaryReport.getBranchSetting().getBranch().getName());
                deptObj.getBranchSetting().getBranch().getCurrency().setId(summaryReport.getBranchSetting().getBranch().getCurrency().getId());

                listOfMarketCollections.add(deptObj);
            }

        }

        for (StationSalesSummaryReport listOfMarketCollection : listOfMarketCollections) { // alt toplam hesaplanır.
            marketCollectionTotalPrices = marketCollectionTotalPrices.add(listOfMarketCollection.getMarketCollectionTotalMoney());
        }

        for (StationSalesSummaryReport listOfMarketSale : listOfMarketSales) {

            marketSalesTotalPrice = marketSalesTotalPrice.add(listOfMarketSale.getMarketSaleTotalMoney());
        }

        for (StationSalesSummaryReport listOfMarketCollection : listOfMarketCollections) { // açık / fazla toplanır.
            marketDeptTotal = marketDeptTotal.add(listOfMarketCollection.getMarketDeptMoney());
        }

        if (marketDeptTotal.compareTo(BigDecimal.ZERO) > 0) {
            marketSalesTotalPrice = marketSalesTotalPrice.subtract(marketDeptTotal);
        }
        if (marketDeptTotal.compareTo(BigDecimal.ZERO) < 0) {
            marketCollectionTotalPrices = marketCollectionTotalPrices.subtract(marketDeptTotal);
        }

    }

    /**
     * Toplam market ve akaryakıt satış tutarlarını listeye ekler.
     */
    public void findTotalSales() {

        listOfTotalSales = new ArrayList<>();

        for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsMarketSales.entrySet()) {
            StationSalesSummaryReport st = new StationSalesSummaryReport();

            st.setTotalSalesPrice(entry.getValue().getMarketSaleTotalMoney());
            st.setCurrency(entry.getValue().getCurrency());
            st.setTotalSalesName(sessionBean.getLoc().getString("market"));

            listOfTotalSales.add(st);
        }

        for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsFuelSales.entrySet()) {
            StationSalesSummaryReport st1 = new StationSalesSummaryReport();

            st1.setTotalSalesPrice(entry.getValue().getFuelStockSalesTotal());
            st1.setCurrency(entry.getValue().getBranchSetting().getBranch().getCurrency());
            st1.setTotalSalesName(sessionBean.getLoc().getString("fuels"));

            listOfTotalSales.add(st1);
        }

    }

    /**
     * Market ve akaryakıtın toplam tahsilatlarını listeye ekler..
     */
    public void findTotalCollections() {

        listOfTotalCollections = new ArrayList<>();

        listOfTotalCollections = new ArrayList<>();

        for (StationSalesSummaryReport listOfFuelCollections : listOfFuelCollections) {
            StationSalesSummaryReport stationSalesSummaryReport = new StationSalesSummaryReport();

            stationSalesSummaryReport.setTotalCollectionName(listOfFuelCollections.getFuelCollectionName());
            stationSalesSummaryReport.setTotalCollectionPrice(listOfFuelCollections.getFuelCollectionSalesTotal());
            stationSalesSummaryReport.setBranchName(listOfFuelCollections.getBranchSetting().getBranch().getName());
            stationSalesSummaryReport.setCurrency(listOfFuelCollections.getBranchSetting().getBranch().getCurrency());

            listOfTotalCollections.add(stationSalesSummaryReport);
        }

        for (StationSalesSummaryReport listOfMarketCollections : listOfMarketCollections) {
            StationSalesSummaryReport stationSalesSummaryReport = new StationSalesSummaryReport();

            stationSalesSummaryReport.setTotalCollectionName(listOfMarketCollections.getMarketCollectionTypeName());
            stationSalesSummaryReport.setTotalCollectionPrice(listOfMarketCollections.getMarketCollectionTotalMoney());
            stationSalesSummaryReport.setBranchName(listOfMarketCollections.getBranchSetting().getBranch().getName());
            stationSalesSummaryReport.setCurrency(listOfMarketCollections.getBranchSetting().getBranch().getCurrency());
            listOfTotalCollections.add(stationSalesSummaryReport);

        }

        generalCollectionTotal = BigDecimal.ZERO;
        for (StationSalesSummaryReport listOfTotalCollection : listOfTotalCollections) { // tüm akaryakıt ve market tahsilatları alt toplam olarak toplanır.
            // generalCollectionTotal = generalCollectionTotal.add(listOfTotalCollection.getTotalCollectionPrice());
        }

        generalCollectionTotal = generalCollectionTotal.add(fuelCollectionTotalMoney);
        generalCollectionTotal = generalCollectionTotal.add(marketCollectionTotalPrices);
    }

    public void createExcel() {
        stationSalesSummaryReportService.createExcel(selectedObject.getBeginDate(), selectedObject.getEndDate(), listOfFuelSales, listOfFuelCollections,
                listOfMarketSales, listOfMarketCollections, listOfTotalSales, listOfTotalCollections, fuelSaleTotalLiter, fuelSaleTotalMoney, fuelCollectionTotalMoney, marketSalesTotalPrice,
                marketCollectionTotalPrices, generalSalesTotal, generalCollectionTotal, selectedBranchList, currencyTotalsFuelSales, currencyTotalsFuelCollection, currencyTotalsMarketSales, currencyTotalsMarketCollection,
                currencyTotalsSales, currencyTotalsCollection);
    }

    public void createPdf() {
        stationSalesSummaryReportService.createPdf(selectedObject.getBeginDate(), selectedObject.getEndDate(), listOfFuelSales, listOfFuelCollections,
                listOfMarketSales, listOfMarketCollections, listOfTotalSales, listOfTotalCollections, fuelSaleTotalLiter, fuelSaleTotalMoney, fuelCollectionTotalMoney, marketSalesTotalPrice,
                marketCollectionTotalPrices, generalSalesTotal, generalCollectionTotal, selectedBranchList, currencyTotalsFuelSales, currencyTotalsFuelCollection, currencyTotalsMarketSales, currencyTotalsMarketCollection,
                currencyTotalsSales, currencyTotalsCollection);

    }

}
