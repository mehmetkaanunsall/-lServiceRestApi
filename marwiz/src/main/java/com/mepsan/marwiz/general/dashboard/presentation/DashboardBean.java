/**
 * Dashboardda kullanıcı widgetlar seçebilecek , görüntüleyebilecek ve
 * değiştirebilecektir.
 * Bu classta dashboarda widget ekleme ,dashboarddan widget kaldırma, kaydetme
 * gibi işlemler yapılmaktadır.
 *
 *    
 * @author Gozde Gursel
 *
 *   
 */
package com.mepsan.marwiz.general.dashboard.presentation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.dashboard.business.IUserNotificationService;
import com.mepsan.marwiz.general.dashboard.business.IWidgetUserDataConService;
import com.mepsan.marwiz.general.dashboard.dao.UserNotification;
import com.mepsan.marwiz.general.dashboard.dao.WelcomeWidget;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.extensions.component.slideout.SlideOut;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class DashboardBean {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}") // session
    public Marwiz marwiz;

    @ManagedProperty(value = "#{userDataService}")
    public IUserDataService userDataService;

    @ManagedProperty(value = "#{widgetUserDataConService}")
    private IWidgetUserDataConService widgetUserDataConService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{userNotificationService}")
    private IUserNotificationService userNotificationService;

    @ManagedProperty(value = "#{stockService}")
    private IStockService stockService;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    private List<WidgetUserDataCon> userWidgets;
    private List<ChartItem> decreasingStocksList;
    private List<ChartItem> salesBySaleTypeList;
    private List<ChartItem> stockRequestList;
    private List<ChartItem> returnedProductsList;
    private List<ChartItem> cashierList;
    private List<ChartItem> recorveriesList;
    private List<ChartItem> graphicRecorveriesList;
    private List<ChartItem> paymentsList;
    private List<ChartItem> graphicPaymentList;
    private List<ChartItem> weeklyCashFlowList;
    private List<ChartItem> tempProductVaryingList;
    private List<ChartItem> purchasePriceHighProductList;
    private List<ChartItem> productProfitalibilityList;
    private ArrayList<ChartItem> stationBySalesForWashingmachicne;
    private ArrayList<ChartItem> washingSalesByQuantityList;
    private List<ChartItem> washingSalesByTurnoverList;
    private List<ChartItem> washingSystemSalesList;
    private List<ChartItem> duePaymentsList;
    private List<ChartItem> fuelStockList;
    private List<ChartItem> monthlSalesByCategorizationList;
    private List<ChartItem> fuelShiftSalesList;
    private List<ChartItem> tempStockSalesList;
    private List<ChartItem> listOfStockSales;
    private List<ChartItem> listOfBrandSales;
    private List<ChartItem> tempSalesByBrandList;
    private List<ChartItem> tempCustomerPurchasesList;
    private List<ChartItem> listOfCustomerPurchases;
    private List<ChartItem> listOfPumperSales;
    private List<ChartItem> tempPumperSalesList;
    private List<ChartItem> tempCashierSalesList;

    private ChartItem selectedPricesVaryingProducts;
    HashSet<String> runnedWidgets;
    private DashboardModel model;
    private int stationsBySalesPeriod;
    private Date beginDate, endDate;
    private int typeSelectedInterval, customersBySellMontlyType, mostSoldStockType, monthlySalesBybBrandType, salesByCashierType, salesBySalesType, duePayments, salesByPumperType, fuelStocktType;
    private List<WelcomeWidget> welcomeList;
    private int stationsBySalesPeriodForWashingMachicne;
    private int creditCount, cashCount, tickCount;
    private BigDecimal total, totalWeeklyEntry, totalWeeklyOutFlow;
    private String recoiveriesName, paymentName;
    private ChartItem totalRecoveries, totalPayments;
    private List<Categorization> listOfCategorization;
    private List<UserNotification> selectedNotifications;
    private boolean isAllBranchesFromWelcome, isAllBranchesFromWashingSystemSales, isAllBranchesFromWashingMachineSalesByTurnover, isAllBranchesFromWashingMachineSalesByQuantity, isAllBranchesFromCustomerPurchase, isAllBranchesFromWashingMachineProfitability, isAllBranchesFromSalesByCategorization, isAllBranchesFromDecreasingStock, isAllBranchesFromSaleType, isAllBranchesFromReturnedStock, isAllBranchesFromStockSale, isAllBranchesFromRecoveries, isAllBranchesFromPayments, isAllBranchesFromWeeklyCashFlow, isAllBranchesFromPricesVaryingProducts, isAllBranchesFromPurchasePriceProducts, isAllBranchesFromProductProfitalibility, isAllBranchesFromBrandSales, isAllBranchesFromCashierSales, isAllBranchesFromDuePayments, isAllBranchesFromPumperSales;
    private BigDecimal totalNetExpense;
    private BigDecimal totalNetIncome;
    private BigDecimal netTotal, totalQuantity, totalSales;
    private int monthlySalesCategoryType; // kategorilere göre aylık satışlardaki combobox için
    private int monthlySalesByCategorizationType; // kategorilere göre satışlardaki günlük,haftalık,aylık tiplerini tutmak için
    private boolean isAllBranchesFromFuelStock;// AKARYAKIT KALAN STOK 
    private int washingSalesByQuantiy, washingSalesByTurnover, washingSales;
    private int fuelShiftSalesType; //Akaryakıt vardiya satışları 
    private boolean isAllBranchesFromFuelShiftSales;
    private int changeBrand, changeStock, changeCustomerPurchases, changePumper, changeCashier;//Grafik içeren widgetlarda ikona tıklandığını belirleyebilmek için tutulan integer değişkenler

    private boolean tempStockSale;
    private boolean tempBrandSale;
    private boolean tempCashierSale;
    private boolean tempWashingMachineSalesByTurnover;
    private boolean tempCustomerPurchase;
    private boolean tempSaleType;
    private boolean tempWashingMachineProfitability;
    private boolean tempWashingMachineSalesByQuantity;
    private boolean tempWashingSystemSales;
    private boolean tempDuePayments;
    private boolean tempPumperSales;
    private boolean tempFuelStock;
    private boolean tempSaleCategorization;
    private boolean tempFuelShifSales;
    List<UserNotification> listNotUser;

    private List<Unit> listOfUnit;

    private UserNotification userNotification;

    private LazyDataModel<UserNotification> listOfUserNotifications;

    private List<UserNotification> tempNotificationList;

    private boolean isAllNotification;
    private LazyDataModel<ChartItem> tempProductProfitalibility;
    private LazyDataModel<ChartItem> lazyPricesVaryingProductsList;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getMonthlySalesCategoryType() {
        return monthlySalesCategoryType;
    }

    public List<UserNotification> getListNotUser() {
        return listNotUser;
    }

    public void setListNotUser(List<UserNotification> listNotUser) {
        this.listNotUser = listNotUser;
    }

    public void setMonthlySalesCategoryType(int monthlySalesCategoryType) {
        this.monthlySalesCategoryType = monthlySalesCategoryType;
    }

    public int getMonthlySalesByCategorizationType() {
        return monthlySalesByCategorizationType;
    }

    public void setMonthlySalesByCategorizationType(int monthlySalesByCategorizationType) {
        this.monthlySalesByCategorizationType = monthlySalesByCategorizationType;
    }

    public int getWashingSalesByQuantiy() {
        return washingSalesByQuantiy;
    }

    public boolean isIsAllBranchesFromWashingSystemSales() {
        return isAllBranchesFromWashingSystemSales;
    }

    public void setIsAllBranchesFromWashingSystemSales(boolean isAllBranchesFromWashingSystemSales) {
        this.isAllBranchesFromWashingSystemSales = isAllBranchesFromWashingSystemSales;
    }

    public int getWashingSales() {
        return washingSales;
    }

    public int getChangeBrand() {
        return changeBrand;
    }

    public void setChangeBrand(int changeBrand) {
        this.changeBrand = changeBrand;
    }

    public int getChangeStock() {
        return changeStock;
    }

    public void setChangeStock(int changeStock) {
        this.changeStock = changeStock;
    }

    public int getChangeCustomerPurchases() {
        return changeCustomerPurchases;
    }

    public void setChangeCustomerPurchases(int changeCustomerPurchases) {
        this.changeCustomerPurchases = changeCustomerPurchases;
    }

    public int getChangePumper() {
        return changePumper;
    }

    public void setChangePumper(int changePumper) {
        this.changePumper = changePumper;
    }

    public int getChangeCashier() {
        return changeCashier;
    }

    public void setChangeCashier(int changeCashier) {
        this.changeCashier = changeCashier;
    }

    public void setWashingSales(int washingSales) {
        this.washingSales = washingSales;
    }

    public int getWashingSalesByTurnover() {
        return washingSalesByTurnover;
    }

    public boolean isIsAllBranchesFromWashingMachineSalesByTurnover() {
        return isAllBranchesFromWashingMachineSalesByTurnover;
    }

    public void setIsAllBranchesFromWashingMachineSalesByTurnover(boolean isAllBranchesFromWashingMachineSalesByTurnover) {
        this.isAllBranchesFromWashingMachineSalesByTurnover = isAllBranchesFromWashingMachineSalesByTurnover;
    }

    public void setWashingSalesByTurnover(int washingSalesByTurnover) {
        this.washingSalesByTurnover = washingSalesByTurnover;
    }

    public void setWashingSalesByQuantiy(int washingSalesByQuantiy) {
        this.washingSalesByQuantiy = washingSalesByQuantiy;
    }

    public List<ChartItem> getWashingSalesByTurnoverList() {
        return washingSalesByTurnoverList;
    }

    public List<ChartItem> getMonthlSalesByCategorizationList() {
        return monthlSalesByCategorizationList;
    }

    public List<ChartItem> getListOfBrandSales() {
        return listOfBrandSales;
    }

    public void setListOfBrandSales(List<ChartItem> listOfBrandSales) {
        this.listOfBrandSales = listOfBrandSales;
    }

    public List<ChartItem> getTempSalesByBrandList() {
        return tempSalesByBrandList;
    }

    public void setTempSalesByBrandList(List<ChartItem> tempSalesByBrandList) {
        this.tempSalesByBrandList = tempSalesByBrandList;
    }

    public List<ChartItem> getTempCustomerPurchasesList() {
        return tempCustomerPurchasesList;
    }

    public void setTempCustomerPurchasesList(List<ChartItem> tempCustomerPurchasesList) {
        this.tempCustomerPurchasesList = tempCustomerPurchasesList;
    }

    public List<ChartItem> getListOfCustomerPurchases() {
        return listOfCustomerPurchases;
    }

    public void setListOfCustomerPurchases(List<ChartItem> listOfCustomerPurchases) {
        this.listOfCustomerPurchases = listOfCustomerPurchases;
    }

    public List<ChartItem> getListOfPumperSalesList() {
        return listOfPumperSales;
    }

    public void setListOfPumperSales(List<ChartItem> listOfPumperSales) {
        this.listOfPumperSales = listOfPumperSales;
    }

    public List<ChartItem> getTempPumperSalesList() {
        return tempPumperSalesList;
    }

    public void setTempPumperSalesList(List<ChartItem> tempPumperSalesList) {
        this.tempPumperSalesList = tempPumperSalesList;
    }

    public List<ChartItem> getTempCashierSalesList() {
        return tempCashierSalesList;
    }

    public void setTempCashierSalesList(List<ChartItem> tempCashierSalesList) {
        this.tempCashierSalesList = tempCashierSalesList;
    }

    public void setMonthlSalesByCategorizationList(List<ChartItem> monthlSalesByCategorizationList) {
        this.monthlSalesByCategorizationList = monthlSalesByCategorizationList;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public void setWashingSalesByTurnoverList(List<ChartItem> washingSalesByTurnoverList) {
        this.washingSalesByTurnoverList = washingSalesByTurnoverList;
    }

    public ArrayList<ChartItem> getWashingSalesByQuantityList() {
        return washingSalesByQuantityList;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setWashingSalesByQuantityList(ArrayList<ChartItem> washingSalesByQuantityList) {
        this.washingSalesByQuantityList = washingSalesByQuantityList;
    }

    public boolean isIsAllBranchesFromWashingMachineSalesByQuantity() {
        return isAllBranchesFromWashingMachineSalesByQuantity;
    }

    public void setIsAllBranchesFromWashingMachineSalesByQuantity(boolean isAllBranchesFromWashingMachineSalesByQuantity) {
        this.isAllBranchesFromWashingMachineSalesByQuantity = isAllBranchesFromWashingMachineSalesByQuantity;
    }

    public int getStationsBySalesPeriodForWashingMachicne() {
        return stationsBySalesPeriodForWashingMachicne;
    }

    public BigDecimal getTotalNetExpense() {
        return totalNetExpense;
    }

    public void setTotalNetExpense(BigDecimal totalNetExpense) {
        this.totalNetExpense = totalNetExpense;
    }

    public BigDecimal getTotalNetIncome() {
        return totalNetIncome;
    }

    public void setTotalNetIncome(BigDecimal totalNetIncome) {
        this.totalNetIncome = totalNetIncome;
    }

    public List<ChartItem> getWashingSystemSalesList() {
        return washingSystemSalesList;
    }

    public void setWashingSystemSalesList(List<ChartItem> washingSystemSalesList) {
        this.washingSystemSalesList = washingSystemSalesList;
    }

    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

    public void setStationsBySalesPeriodForWashingMachicne(int stationsBySalesPeriodForWashingMachicne) {
        this.stationsBySalesPeriodForWashingMachicne = stationsBySalesPeriodForWashingMachicne;
    }

    public List<WidgetUserDataCon> getUserWidgets() {
        return userWidgets;
    }

    public ArrayList<ChartItem> getStationBySalesForWashingmachicne() {
        return stationBySalesForWashingmachicne;
    }

    public void setStationBySalesForWashingmachicne(ArrayList<ChartItem> stationBySalesForWashingmachicne) {
        this.stationBySalesForWashingmachicne = stationBySalesForWashingmachicne;
    }

    public boolean isIsAllBranchesFromPricesVaryingProducts() {
        return isAllBranchesFromPricesVaryingProducts;
    }

    public void setIsAllBranchesFromPricesVaryingProducts(boolean isAllBranchesFromPricesVaryingProducts) {
        this.isAllBranchesFromPricesVaryingProducts = isAllBranchesFromPricesVaryingProducts;
    }

    public boolean isIsAllBranchesFromWashingMachineProfitability() {
        return isAllBranchesFromWashingMachineProfitability;
    }

    public void setIsAllBranchesFromWashingMachineProfitability(boolean isAllBranchesFromWashingMachineProfitability) {
        this.isAllBranchesFromWashingMachineProfitability = isAllBranchesFromWashingMachineProfitability;
    }

    public boolean isIsAllBranchesFromPurchasePriceProducts() {
        return isAllBranchesFromPurchasePriceProducts;
    }

    public boolean isIsAllBranchesFromProductProfitalibility() {
        return isAllBranchesFromProductProfitalibility;
    }

    public void setIsAllBranchesFromProductProfitalibility(boolean isAllBranchesFromProductProfitalibility) {
        this.isAllBranchesFromProductProfitalibility = isAllBranchesFromProductProfitalibility;
    }

    public void setIsAllBranchesFromPurchasePriceProducts(boolean isAllBranchesFromPurchasePriceProducts) {
        this.isAllBranchesFromPurchasePriceProducts = isAllBranchesFromPurchasePriceProducts;
    }

    public boolean isIsAllBranchesFromWeeklyCashFlow() {
        return isAllBranchesFromWeeklyCashFlow;
    }

    public void setIsAllBranchesFromWeeklyCashFlow(boolean isAllBranchesFromWeeklyCashFlow) {
        this.isAllBranchesFromWeeklyCashFlow = isAllBranchesFromWeeklyCashFlow;
    }

    public boolean isIsAllBranchesFromSaleType() {
        return isAllBranchesFromSaleType;
    }

    public void setIsAllBranchesFromSaleType(boolean isAllBranchesFromSaleType) {
        this.isAllBranchesFromSaleType = isAllBranchesFromSaleType;
    }

    public boolean isIsAllBranchesFromPayments() {
        return isAllBranchesFromPayments;
    }

    public void setIsAllBranchesFromPayments(boolean isAllBranchesFromPayments) {
        this.isAllBranchesFromPayments = isAllBranchesFromPayments;
    }

    public boolean isIsAllBranchesFromRecoveries() {
        return isAllBranchesFromRecoveries;
    }

    public void setIsAllBranchesFromRecoveries(boolean isAllBranchesFromRecoveries) {
        this.isAllBranchesFromRecoveries = isAllBranchesFromRecoveries;
    }

    public void setUserWidgets(List<WidgetUserDataCon> userWidgets) {
        this.userWidgets = userWidgets;
    }

    public boolean isIsAllBranchesFromStockSale() {
        return isAllBranchesFromStockSale;
    }

    public void setIsAllBranchesFromStockSale(boolean isAllBranchesFromStockSale) {
        this.isAllBranchesFromStockSale = isAllBranchesFromStockSale;
    }

    public boolean isIsAllBranchesFromCashierSales() {
        return isAllBranchesFromCashierSales;
    }

    public void setIsAllBranchesFromCashierSales(boolean isAllBranchesFromCashierSales) {
        this.isAllBranchesFromCashierSales = isAllBranchesFromCashierSales;
    }

    public boolean isIsAllBranchesFromBrandSales() {
        return isAllBranchesFromBrandSales;
    }

    public void setIsAllBranchesFromBrandSales(boolean isAllBranchesFromBrandSales) {
        this.isAllBranchesFromBrandSales = isAllBranchesFromBrandSales;
    }

    public boolean isIsAllBranchesFromDecreasingStock() {
        return isAllBranchesFromDecreasingStock;
    }

    public void setIsAllBranchesFromDecreasingStock(boolean isAllBranchesFromDecreasingStock) {
        this.isAllBranchesFromDecreasingStock = isAllBranchesFromDecreasingStock;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public boolean isIsAllBranchesFromSalesByCategorization() {
        return isAllBranchesFromSalesByCategorization;
    }

    public void setIsAllBranchesFromSalesByCategorization(boolean isAllBranchesFromSalesByCategorization) {
        this.isAllBranchesFromSalesByCategorization = isAllBranchesFromSalesByCategorization;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IUserDataService getUserDataService() {
        return userDataService;
    }

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setUserNotificationService(IUserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    public void setWidgetUserDataConService(IWidgetUserDataConService widgetUserDataConService) {
        this.widgetUserDataConService = widgetUserDataConService;
    }

    public DashboardModel getModel() {
        return model;
    }

    public void setModel(DashboardModel model) {
        this.model = model;
    }

    public List<ChartItem> getDecreasingStocksList() {
        return decreasingStocksList;
    }

    public void setDecreasingStocksList(List<ChartItem> decreasingStocksList) {
        this.decreasingStocksList = decreasingStocksList;
    }

    public List<ChartItem> getSalesBySaleTypeList() {
        return salesBySaleTypeList;
    }

    public void setSalesBySaleTypeList(List<ChartItem> salesBySaleTypeList) {
        this.salesBySaleTypeList = salesBySaleTypeList;
    }

    public List<ChartItem> getDuePaymentsList() {
        return duePaymentsList;
    }

    public void setDuePaymentsList(List<ChartItem> duePaymentsList) {
        this.duePaymentsList = duePaymentsList;
    }

    public List<ChartItem> getFuelShiftSalesList() {
        return fuelShiftSalesList;
    }

    public void setFuelShiftSalesList(List<ChartItem> fuelShiftSalesList) {
        this.fuelShiftSalesList = fuelShiftSalesList;
    }

    public int getDuePayments() {
        return duePayments;
    }

    public void setDuePayments(int duePayments) {
        this.duePayments = duePayments;
    }

    public int getFuelShiftSalesType() {
        return fuelShiftSalesType;
    }

    public void setFuelShiftSalesType(int fuelShiftSalesType) {
        this.fuelShiftSalesType = fuelShiftSalesType;
    }

    public int getSalesByPumperType() {
        return salesByPumperType;
    }

    public void setSalesByPumperType(int salesByPumperType) {
        this.salesByPumperType = salesByPumperType;
    }

    public boolean isIsAllBranchesFromDuePayments() {
        return isAllBranchesFromDuePayments;
    }

    public void setIsAllBranchesFromDuePayments(boolean isAllBranchesFromDuePayments) {
        this.isAllBranchesFromDuePayments = isAllBranchesFromDuePayments;
    }

    public boolean getIsAllBranchesFromFuelShiftSales() {
        return isAllBranchesFromFuelShiftSales;
    }

    public void setIsAllBranchesFromFuelShiftSales(boolean isAllBranchesFromFuelShiftSales) {
        this.isAllBranchesFromFuelShiftSales = isAllBranchesFromFuelShiftSales;
    }

    public boolean isTempFuelShifSales() {
        return tempFuelShifSales;
    }

    public void setTempFuelShifSales(boolean tempFuelShifSales) {
        this.tempFuelShifSales = tempFuelShifSales;
    }

    public boolean isTempDuePayments() {
        return tempDuePayments;
    }

    public void setTempDuePayments(boolean tempDuePayments) {
        this.tempDuePayments = tempDuePayments;
    }

    public List<ChartItem> getStockRequestList() {
        return stockRequestList;
    }

    public void setStockRequestList(List<ChartItem> stockRequestList) {
        this.stockRequestList = stockRequestList;
    }

    public int getStationsBySalesPeriod() {
        return stationsBySalesPeriod;
    }

    public void setStationsBySalesPeriod(int stationsBySalesPeriod) {
        this.stationsBySalesPeriod = stationsBySalesPeriod;
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

    public int getTypeSelectedInterval() {
        return typeSelectedInterval;
    }

    public void setTypeSelectedInterval(int typeSelectedInterval) {
        this.typeSelectedInterval = typeSelectedInterval;
    }

    public int getCustomersBySellMontlyType() {
        return customersBySellMontlyType;
    }

    public void setCustomersBySellMontlyType(int customersBySellMontlyType) {
        this.customersBySellMontlyType = customersBySellMontlyType;
    }

    public boolean isTempSaleCategorization() {
        return tempSaleCategorization;
    }

    public void setTempSaleCategorization(boolean tempSaleCategorization) {
        this.tempSaleCategorization = tempSaleCategorization;
    }

    public int getMostSoldStockType() {
        return mostSoldStockType;
    }

    public void setMostSoldStockType(int mostSoldStockType) {
        this.mostSoldStockType = mostSoldStockType;
    }

    public int getSalesByCashierType() {
        return salesByCashierType;
    }

    public void setSalesByCashierType(int salesByCashierType) {
        this.salesByCashierType = salesByCashierType;
    }

    public List<ChartItem> getReturnedProductsList() {
        return returnedProductsList;
    }

    public boolean isIsAllBranchesFromCustomerPurchase() {
        return isAllBranchesFromCustomerPurchase;
    }

    public void setIsAllBranchesFromCustomerPurchase(boolean isAllBranchesFromCustomerPurchase) {
        this.isAllBranchesFromCustomerPurchase = isAllBranchesFromCustomerPurchase;
    }

    public void setReturnedProductsList(List<ChartItem> returnedProductsList) {
        this.returnedProductsList = returnedProductsList;
    }

    public int getSalesBySalesType() {
        return salesBySalesType;
    }

    public void setSalesBySalesType(int salesBySalesType) {
        this.salesBySalesType = salesBySalesType;
    }

    public int getDuepayments() {
        return duePayments;
    }

    public void setDuepayments(int duepayments) {
        this.duePayments = duePayments;
    }

    public boolean isIsAllBranchesFromPumperSales() {
        return isAllBranchesFromPumperSales;
    }

    public void setIsAllBranchesFromPumperSales(boolean isAllBranchesFromPumperSales) {
        this.isAllBranchesFromPumperSales = isAllBranchesFromPumperSales;
    }

    public boolean isTempPumperSales() {
        return tempPumperSales;
    }

    public void setTempPumperSales(boolean tempPumperSales) {
        this.tempPumperSales = tempPumperSales;
    }

    public int getMonthlySalesBybBrandType() {
        return monthlySalesBybBrandType;
    }

    public void setMonthlySalesBybBrandType(int monthlySalesBybBrandType) {
        this.monthlySalesBybBrandType = monthlySalesBybBrandType;
    }

    public List<ChartItem> getCashierList() {
        return cashierList;
    }

    public void setCashierList(List<ChartItem> cashierList) {
        this.cashierList = cashierList;
    }

    public List<WelcomeWidget> getWelcomeList() {
        return welcomeList;
    }

    public void setWelcomeList(List<WelcomeWidget> welcomeList) {
        this.welcomeList = welcomeList;
    }

    public boolean isIsAllBranchesFromWelcome() {
        return isAllBranchesFromWelcome;
    }

    public void setIsAllBranchesFromWelcome(boolean isAllBranchesFromWelcome) {
        this.isAllBranchesFromWelcome = isAllBranchesFromWelcome;
    }

    public List<ChartItem> getFuelStockList() {
        return fuelStockList;
    }

    public void setFuelStockList(List<ChartItem> fuelStockList) {
        this.fuelStockList = fuelStockList;
    }

    public int getFuelStocktType() {
        return fuelStocktType;
    }

    public void setFuelStocktType(int fuelStocktType) {
        this.fuelStocktType = fuelStocktType;
    }

    public boolean isIsAllBranchesFromFuelStock() {
        return isAllBranchesFromFuelStock;
    }

    public void setIsAllBranchesFromFuelStock(boolean isAllBranchesFromFuelStock) {
        this.isAllBranchesFromFuelStock = isAllBranchesFromFuelStock;
    }

    public boolean isTempFuelStock() {
        return tempFuelStock;
    }

    public void setTempFuelStock(boolean tempFuelStock) {
        this.tempFuelStock = tempFuelStock;
    }

    public int getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(int creditCount) {
        this.creditCount = creditCount;
    }

    public int getCashCount() {
        return cashCount;
    }

    public void setCashCount(int cashCount) {
        this.cashCount = cashCount;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ChartItem> getRecorveriesList() {
        return recorveriesList;
    }

    public void setRecorveriesList(List<ChartItem> recorveriesList) {
        this.recorveriesList = recorveriesList;
    }

    public String getRecoiveriesName() {
        return recoiveriesName;
    }

    public void setRecoiveriesName(String recoiveriesName) {
        this.recoiveriesName = recoiveriesName;
    }

    public List<ChartItem> getPaymentsList() {
        return paymentsList;
    }

    public void setPaymentsList(List<ChartItem> paymentsList) {
        this.paymentsList = paymentsList;
    }

    public List<ChartItem> getGraphicPaymentList() {
        return graphicPaymentList;
    }

    public void setGraphicPaymentList(List<ChartItem> graphicPaymentList) {
        this.graphicPaymentList = graphicPaymentList;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public List<ChartItem> getWeeklyCashFlowList() {
        return weeklyCashFlowList;
    }

    public void setWeeklyCashFlowList(List<ChartItem> weeklyCashFlowList) {
        this.weeklyCashFlowList = weeklyCashFlowList;
    }

    public ChartItem getTotalRecoveries() {
        return totalRecoveries;
    }

    public void setTotalRecoveries(ChartItem totalRecoveries) {
        this.totalRecoveries = totalRecoveries;
    }

    public ChartItem getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(ChartItem totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalWeeklyEntry() {
        return totalWeeklyEntry;
    }

    public void setTotalWeeklyEntry(BigDecimal totalWeeklyEntry) {
        this.totalWeeklyEntry = totalWeeklyEntry;
    }

    public BigDecimal getTotalWeeklyOutFlow() {
        return totalWeeklyOutFlow;
    }

    public void setTotalWeeklyOutFlow(BigDecimal totalWeeklyOutFlow) {
        this.totalWeeklyOutFlow = totalWeeklyOutFlow;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public List<UserNotification> getSelectedNotifications() {
        return selectedNotifications;
    }

    public void setSelectedNotifications(List<UserNotification> selectedNotifications) {
        this.selectedNotifications = selectedNotifications;
    }

    public ChartItem getSelectedPricesVaryingProducts() {
        return selectedPricesVaryingProducts;
    }

    public void setSelectedPricesVaryingProducts(ChartItem selectedPricesVaryingProducts) {
        this.selectedPricesVaryingProducts = selectedPricesVaryingProducts;
    }

    public List<ChartItem> getTempProductVaryingList() {
        return tempProductVaryingList;
    }

    public void setTempProductVaryingList(List<ChartItem> tempProductVaryingList) {
        this.tempProductVaryingList = tempProductVaryingList;
    }

    public List<ChartItem> getTempStockSalesList() {
        return tempStockSalesList;
    }

    public void setTempStockSalesList(List<ChartItem> tempStockSalesList) {
        this.tempStockSalesList = tempStockSalesList;
    }

    public List<ChartItem> getListOfStockSales() {
        return listOfStockSales;
    }

    public void setListOfStockSales(List<ChartItem> listOfStockSales) {
        this.listOfStockSales = listOfStockSales;
    }

    public List<ChartItem> getPurchasePriceHighProductList() {
        return purchasePriceHighProductList;
    }

    public void setPurchasePriceHighProductList(List<ChartItem> purchasePriceHighProductList) {
        this.purchasePriceHighProductList = purchasePriceHighProductList;
    }

    public List<ChartItem> getProductProfitalibilityList() {
        return productProfitalibilityList;
    }

    public void setProductProfitalibilityList(List<ChartItem> productProfitalibilityList) {
        this.productProfitalibilityList = productProfitalibilityList;
    }

    public boolean isIsAllBranchesFromReturnedStock() {
        return isAllBranchesFromReturnedStock;
    }

    public void setIsAllBranchesFromReturnedStock(boolean isAllBranchesFromReturnedStock) {
        this.isAllBranchesFromReturnedStock = isAllBranchesFromReturnedStock;
    }

    public boolean isTempStockSale() {
        return tempStockSale;
    }

    public void setTempStockSale(boolean tempStockSale) {
        this.tempStockSale = tempStockSale;
    }

    public boolean isTempBrandSale() {
        return tempBrandSale;
    }

    public void setTempBrandSale(boolean tempBrandSale) {
        this.tempBrandSale = tempBrandSale;
    }

    public boolean isTempCashierSale() {
        return tempCashierSale;
    }

    public void setTempCashierSale(boolean tempCashierSale) {
        this.tempCashierSale = tempCashierSale;
    }

    public boolean isTempWashingMachineSalesByTurnover() {
        return tempWashingMachineSalesByTurnover;
    }

    public void setTempWashingMachineSalesByTurnover(boolean tempWashingMachineSalesByTurnover) {
        this.tempWashingMachineSalesByTurnover = tempWashingMachineSalesByTurnover;
    }

    public boolean isTempCustomerPurchase() {
        return tempCustomerPurchase;
    }

    public void setTempCustomerPurchase(boolean tempCustomerPurchase) {
        this.tempCustomerPurchase = tempCustomerPurchase;
    }

    public boolean isTempSaleType() {
        return tempSaleType;
    }

    public void setTempSaleType(boolean tempSaleType) {
        this.tempSaleType = tempSaleType;
    }

    public boolean isTempWashingMachineProfitability() {
        return tempWashingMachineProfitability;
    }

    public void setTempWashingMachineProfitability(boolean tempWashingMachineProfitability) {
        this.tempWashingMachineProfitability = tempWashingMachineProfitability;
    }

    public boolean isTempWashingMachineSalesByQuantity() {
        return tempWashingMachineSalesByQuantity;
    }

    public void setTempWashingMachineSalesByQuantity(boolean tempWashingMachineSalesByQuantity) {
        this.tempWashingMachineSalesByQuantity = tempWashingMachineSalesByQuantity;
    }

    public boolean isTempWashingSystemSales() {
        return tempWashingSystemSales;
    }

    public void setTempWashingSystemSales(boolean tempWashingSystemSales) {
        this.tempWashingSystemSales = tempWashingSystemSales;
    }

    public List<Unit> getListOfUnit() {
        return listOfUnit;
    }

    public void setListOfUnit(List<Unit> listOfUnit) {
        this.listOfUnit = listOfUnit;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public UserNotification getUserNotification() {
        return userNotification;
    }

    public void setUserNotification(UserNotification userNotification) {
        this.userNotification = userNotification;
    }

    public LazyDataModel<UserNotification> getListOfUserNotifications() {
        return listOfUserNotifications;
    }

    public void setListOfUserNotifications(LazyDataModel<UserNotification> listOfUserNotifications) {
        this.listOfUserNotifications = listOfUserNotifications;
    }

    public List<UserNotification> getTempNotificationList() {
        return tempNotificationList;
    }

    public void setTempNotificationList(List<UserNotification> tempNotificationList) {
        this.tempNotificationList = tempNotificationList;
    }

    public boolean isIsAllNotification() {
        return isAllNotification;
    }

    public void setIsAllNotification(boolean isAllNotification) {
        this.isAllNotification = isAllNotification;
    }

    public LazyDataModel<ChartItem> getTempProductProfitalibility() {
        return tempProductProfitalibility;
    }

    public void setTempProductProfitalibility(LazyDataModel<ChartItem> tempProductProfitalibility) {
        this.tempProductProfitalibility = tempProductProfitalibility;
    }

    public LazyDataModel<ChartItem> getLazyPricesVaryingProductsList() {
        return lazyPricesVaryingProductsList;
    }

    public void setLazyPricesVaryingProductsList(LazyDataModel<ChartItem> lazyPricesVaryingProductsList) {
        this.lazyPricesVaryingProductsList = lazyPricesVaryingProductsList;
    }

    @PostConstruct
    public void init() {
        System.out.println("----DashboardBean----");
        model = new DefaultDashboardModel();
        listOfUserNotifications = findAll("", sessionBean.getUser());
        RequestContext.getCurrentInstance().execute("showNotification()");
        selectedNotifications = new ArrayList<>();
        tempNotificationList = new ArrayList<>();
        listNotUser = new ArrayList<>();

        beginDate = new Date();
        endDate = new Date();

        for (int i = 0; i < 3; i++) {
            DefaultDashboardColumn defaultDashboardColumn = new DefaultDashboardColumn();
            defaultDashboardColumn.setStyleClass("Container33 Responsive");
            model.addColumn(defaultDashboardColumn);
        }
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            isAllBranchesFromCustomerPurchase = true;
            isAllBranchesFromSalesByCategorization = true;
            isAllBranchesFromDecreasingStock = true;
            isAllBranchesFromCashierSales = true;
            isAllBranchesFromSaleType = true;
            isAllBranchesFromReturnedStock = true;
            isAllBranchesFromRecoveries = true;
            isAllBranchesFromPayments = true;
            isAllBranchesFromWeeklyCashFlow = true;
            isAllBranchesFromPricesVaryingProducts = true;
            isAllBranchesFromPurchasePriceProducts = true;
            isAllBranchesFromProductProfitalibility = true;
            isAllBranchesFromWashingMachineProfitability = true;
            isAllBranchesFromWashingMachineSalesByQuantity = true;
            isAllBranchesFromWashingMachineSalesByTurnover = true;
            isAllBranchesFromWashingSystemSales = true;
            isAllBranchesFromStockSale = true;
            isAllBranchesFromBrandSales = true;
            isAllBranchesFromWelcome = true;
            isAllBranchesFromDuePayments = true;
            isAllBranchesFromPumperSales = true;
            isAllBranchesFromFuelShiftSales = true;
            isAllBranchesFromFuelStock = true;

        }

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempBrandSale = true;
            tempCashierSale = true;
            tempStockSale = true;
            tempWashingMachineSalesByTurnover = true;
            tempCustomerPurchase = true;
            tempSaleType = true;
            tempWashingMachineProfitability = true;
            tempWashingMachineSalesByQuantity = true;
            tempWashingSystemSales = true;
            tempDuePayments = true;
            tempPumperSales = true;
            tempSaleCategorization = true;
            tempFuelShifSales = true;
            tempFuelStock = true;

        }
        runnedWidgets = new HashSet<>();
        listOfUnit = new ArrayList<>();

        listOfUnit = unitService.listOfUnit();

        selectedPricesVaryingProducts = new ChartItem();
        listOfBrandSales = new ArrayList<>();
        tempSalesByBrandList = new ArrayList<>();
        createWidgets();
    }

    public void createWidgets() {

        userWidgets = widgetUserDataConService.findAll();
        for (WidgetUserDataCon userWidget : userWidgets) {
            model.getColumn(userWidget.getCol()).addWidget(userWidget.getWidget().getName());
            switch (userWidget.getWidget().getName()) {
                case "productsales":
                    mostSoldStockType = 2;
                    break;
                case "customerpurchases":
                    customersBySellMontlyType = 2;
                    break;
                case "brandsales":
                    monthlySalesBybBrandType = 2;
                    break;
                case "cashiersales":
                    salesByCashierType = 2;
                    break;
                case "salesbysaletype":
                    salesBySalesType = 2;
                    break;
                case "purchasepricehighproducts":
                    purchasePriceHighProductList = new ArrayList<>();
                    break;
                case "washingsystemproductsalesbyquantity":
                    washingSalesByQuantiy = 2;
                    washingSalesByQuantityList = new ArrayList<>();
                    break;
                case "washingsystemproductsalesbyturnover":
                    washingSalesByTurnover = 2;
                    washingSalesByTurnoverList = new ArrayList<>();
                    break;

                case "washingsystemsales":
                    washingSales = 2;
                    washingSystemSalesList = new ArrayList<>();
                    break;

                case "productprofitalibility":
                    productProfitalibilityList = new ArrayList<>();
                    break;
                case "washingmachineprofitability":
                    stationsBySalesPeriodForWashingMachicne = 2;
                    stationBySalesForWashingmachicne = new ArrayList<>();
                    break;
                case "duepayments":
                    duePayments = 2;
                    duePaymentsList = new ArrayList<>();
                    break;
                case "pumpersales":
                    salesByPumperType = 2;
                    break;
                case "monthlysalesbycategorization":
                    monthlySalesByCategorizationType = 2;
                    listOfCategorization = new ArrayList<>();
                    break;

                case "fuelshiftsales":
                    fuelShiftSalesType = 2;
                    fuelShiftSalesList = new ArrayList<>();
                default:
                    break;

                case "fuelremainingstockinfo":
                    fuelStocktType = 2;
                    fuelStockList = new ArrayList<>();
                    break;

            }
        }

    }

    public void handleReorder(DashboardReorderEvent event) {
        Integer columnIndex = event.getColumnIndex();
        Integer senderColumnIndex = event.getSenderColumnIndex();

        ArrayList<WidgetUserDataCon> reorderedWidgets = getReorderedWidgets(columnIndex);
        if (senderColumnIndex != null) {
            reorderedWidgets.addAll(getReorderedWidgets(senderColumnIndex));
        }

        for (WidgetUserDataCon reorderedWidget : reorderedWidgets) {
            widgetUserDataConService.update(reorderedWidget);
        }
    }

    public ArrayList<WidgetUserDataCon> getReorderedWidgets(int columnIndex) {
        DashboardColumn dashboardColumn = model.getColumn(columnIndex);
        ArrayList<WidgetUserDataCon> reorderedWidgets = new ArrayList<>();
        for (int i = 0; i < dashboardColumn.getWidgetCount(); i++) {
            String widget = dashboardColumn.getWidget(i);
            for (WidgetUserDataCon userWidget : userWidgets) {
                if (userWidget.getWidget().getName().equals(widget)) {
                    if (userWidget.getCol() != columnIndex || userWidget.getRow() != i) {
                        userWidget.setCol(columnIndex);
                        userWidget.setRow(i);
                        reorderedWidgets.add(userWidget);
                    }
                }
            }
        }
        return reorderedWidgets;
    }

    public void onClose(ActionEvent event) {
        List<String> widgets = new ArrayList<>();
        String widgetId = (String) event.getComponent().getAttributes().get("widgetid");
        for (int i = 0; i < model.getColumnCount(); i++) {
            DashboardColumn column = model.getColumn(i);
            for (int j = 0; j < column.getWidgetCount(); j++) {
                if (column.getWidget(j).equals(widgetId)) {
                    column.removeWidget(widgetId);
                }
            }
        }
        for (WidgetUserDataCon userWidget : userWidgets) {
            if (userWidget.getWidget().getName().equals(widgetId)) {
                widgetUserDataConService.delete(userWidget);
            } else {
                widgets.add(userWidget.getWidget().getName());
            }
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dshWidget");
        String data = new Gson().toJson(widgets);
        context.execute("updateDshWidget(" + "'" + data + "'" + ")");
    }

    public boolean userHasWidget(String widgetName) {

        for (WidgetUserDataCon userWidget : userWidgets) {
            if (userWidget.getWidget().getName().equals(widgetName)) {
                return true;
            }
        }

        return false;
    }

    public void removeWidget(String widgetName) {
        runnedWidgets.remove(widgetName);
//        RequestContext.getCurrentInstance().update(widgetName);
    }

    //Welcome
    public void createWelcome(String widgetName) {

        welcomeList = new ArrayList<>();
        List<WelcomeWidget> chartItems = new ArrayList<>();

        welcomeList = widgetUserDataConService.getWelcome(isAllBranchesFromWelcome);

        runnedWidgets.add(widgetName);
        boolean isThere = false;
        total = BigDecimal.valueOf(0);
        for (WelcomeWidget widget : welcomeList) { // vardiyadaki satışların toplamları currencylere göre yapılması istenildiği için burada eklendi.
            isThere = false;
            for (WelcomeWidget chartItem : chartItems) {
                if (chartItem.getCurrency().getId() == widget.getCurrency().getId()) {
                    isThere = true;
                    break;
                } else {
                    isThere = false;
                }
            }
            if (!isThere) {
                WelcomeWidget widgetC = new WelcomeWidget();
                widgetC.getType().setId(-1);
                widgetC.getType().setTag(sessionBean.getLoc().getString("total"));
                widgetC.getCurrency().setId(widget.getCurrency().getId());
                chartItems.add(widgetC);
            }
        }

        for (WelcomeWidget chartItem : chartItems) {
            total = BigDecimal.ZERO;
            for (WelcomeWidget welcomes : welcomeList) {
                if (chartItem.getCurrency().getId() == welcomes.getCurrency().getId()) {
                    total = total.add(welcomes.getTotalPrice());
                    chartItem.setTotalPrice(total);
                }
            }
        }
        welcomeList.addAll(chartItems);

    }

    public boolean isWidgetCreated(String widgetName) {
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            isAllBranchesFromStockSale = tempStockSale;
            isAllBranchesFromBrandSales = tempBrandSale;
            isAllBranchesFromCashierSales = tempCashierSale;
            isAllBranchesFromWashingMachineSalesByTurnover = tempWashingMachineSalesByTurnover;
            isAllBranchesFromCustomerPurchase = tempCustomerPurchase;
            isAllBranchesFromSaleType = tempSaleType;
            isAllBranchesFromWashingMachineProfitability = tempWashingMachineProfitability;
            isAllBranchesFromWashingMachineSalesByQuantity = tempWashingMachineSalesByQuantity;
            isAllBranchesFromWashingSystemSales = tempWashingSystemSales;
            isAllBranchesFromDuePayments = tempDuePayments;
            isAllBranchesFromPumperSales = tempPumperSales;
            isAllBranchesFromFuelStock = tempFuelStock;
            isAllBranchesFromSalesByCategorization = tempSaleCategorization;
            isAllBranchesFromFuelShiftSales = tempFuelShifSales;

        }

        return runnedWidgets.contains(widgetName);
    }

    // Stok Satışları
    public void createMostSoldStocks(String widgetName, int type) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempStockSale = isAllBranchesFromStockSale;
        }

        mostSoldStockType = type;
        tempStockSalesList = new ArrayList<>();
        tempStockSalesList = widgetUserDataConService.getMostSoldStocks(type, beginDate, endDate, isAllBranchesFromStockSale, changeStock);

        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmDashboard:checkboxproductsales");
        String data = new Gson().toJson(tempStockSalesList);
        context.execute("mostsoldstocks(" + "'" + data + "'" + ")");

    }

    //Müşteri Alışları
    public void createCustomersBySellMontly(String widgetName, int type) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempCustomerPurchase = isAllBranchesFromCustomerPurchase;
        }

        tempCustomerPurchasesList = new ArrayList<>();
        customersBySellMontlyType = type;
        tempCustomerPurchasesList = widgetUserDataConService.getMostCustomersBySale(type, beginDate, endDate, isAllBranchesFromCustomerPurchase, changeCustomerPurchases);

        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(tempCustomerPurchasesList);
        context.execute("customerswhichmakeapurchasemonthly(" + "'" + data + "'" + ")");

    }

    //Marka Satışları
    public void createMonthlySalesByBrand(String widgetName, int type) {

        monthlySalesBybBrandType = type;

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempBrandSale = isAllBranchesFromBrandSales;
        }

        tempSalesByBrandList = new ArrayList<>();
        tempSalesByBrandList = widgetUserDataConService.getSalesByBrand(type, beginDate, endDate, isAllBranchesFromBrandSales, changeBrand);

        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(tempSalesByBrandList);
        context.execute("monthlysalesbybrand(" + "'" + data + "'" + ")");

    }

    //Pompacı Satışları
    public void createSalesByPumper(String widgetName, int type) {

        salesByPumperType = type;
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempPumperSales = isAllBranchesFromPumperSales;
        }

        tempPumperSalesList = new ArrayList<>();
        tempPumperSalesList = widgetUserDataConService.getSalesByPumper(salesByPumperType, beginDate, endDate, isAllBranchesFromPumperSales, changePumper);

        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(tempPumperSalesList);
        context.execute("salesbypumper(" + "'" + data + "'" + ")");

    }

    //Azalan Stoklar
    public void createDecreasingStocks(String widgetName) {

        decreasingStocksList = new ArrayList<>();
        decreasingStocksList = widgetUserDataConService.getDecreasingStocks(isAllBranchesFromDecreasingStock);

        runnedWidgets.add(widgetName);

    }

    //En Çok İade Edilen Ürünler
    public void createReturnedStock(String widgetName) {

        returnedProductsList = new ArrayList<>();
        returnedProductsList = widgetUserDataConService.getReturnedStock(isAllBranchesFromReturnedStock);

        runnedWidgets.add(widgetName);

    }

    //Kasiyere Göre Aylık Satışlar
    public void createSalesByCashier(String widgetName, int type) {

        salesByCashierType = type;

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempCashierSale = isAllBranchesFromCashierSales;
        }

        tempCashierSalesList = new ArrayList<>();
        tempCashierSalesList = widgetUserDataConService.getSalesByCashier(type, beginDate, endDate, isAllBranchesFromCashierSales, changeCashier);

        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(tempCashierSalesList);
        context.execute("salesbycashier(" + "'" + data + "'" + ")");

    }

    //Satış Tipine GÖre Satışlar
    public void createSalesBySaleType(String widgetName, int type) {
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempSaleType = isAllBranchesFromSaleType;
        }
        salesBySalesType = type;
        salesBySaleTypeList = new ArrayList<>();
        salesBySaleTypeList = widgetUserDataConService.getSalesBySaleType(type, beginDate, endDate, isAllBranchesFromSaleType);
        runnedWidgets.add(widgetName);

    }

    //Vadesi Yaklaşan Ödemeler
    public void createDuePayments(String widgetName, int type) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempDuePayments = isAllBranchesFromDuePayments;
        }

        duePayments = type;
        duePaymentsList = new ArrayList<>();
        duePaymentsList = widgetUserDataConService.getDuePayments(type, beginDate, endDate, isAllBranchesFromDuePayments);

        runnedWidgets.add(widgetName);

    }

    public void createFuelShiftSales(String widgetName, int type) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempFuelShifSales = isAllBranchesFromFuelShiftSales;
        }

        fuelShiftSalesType = type;
        fuelShiftSalesList = new ArrayList<>();
        fuelShiftSalesList = widgetUserDataConService.getFuelShiftSales(type, beginDate, endDate, isAllBranchesFromFuelShiftSales);
        runnedWidgets.add(widgetName);

    }

    //Aylık Kategorilere Göre Satışlar
    public void createMonthlySalesByCategorization(String widgetName, int type2, int type) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempSaleCategorization = isAllBranchesFromSalesByCategorization;
        }

        monthlySalesByCategorizationType = type;
        monthlySalesCategoryType = type2;

        List<ChartItem> list = widgetUserDataConService.getSalesByCategorization(type, beginDate, endDate, listOfCategorization, isAllBranchesFromSalesByCategorization, type2);
        runnedWidgets.add(widgetName);
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(list);
        context.execute("monthlysalesbycategorization(" + "'" + data + "'" + ")");
    }

    //Akaryakıt Kalan Stok Bilgileri
    public void createFuelStock(String widgetName) {

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempFuelStock = isAllBranchesFromFuelStock;
        }

        fuelStockList = new ArrayList<>();
        fuelStockList = widgetUserDataConService.getFuelStock(isAllBranchesFromFuelStock);

        runnedWidgets.add(widgetName);
    }

    //Tahsilatlar
    public void createRecoveries(String widgetName) {

        BigDecimal totalCheque = BigDecimal.ZERO,
                delayedPaymentCheque = BigDecimal.ZERO,
                totalBill = BigDecimal.ZERO,
                delayedPaymentBill = BigDecimal.ZERO,
                totalCredit = BigDecimal.ZERO,
                delayedPaymentCredit = BigDecimal.ZERO,
                amountCollectedCredit = BigDecimal.ZERO,
                amountCollectedCheque = BigDecimal.ZERO,
                amountCollectedBill = BigDecimal.ZERO,
                totalOpenPayments = BigDecimal.ZERO,
                totalGeneralDelayed = BigDecimal.ZERO,
                totalGeneral = BigDecimal.ZERO, totalRemainingGeneral = BigDecimal.ZERO;
        recorveriesList = new ArrayList<>();
        graphicRecorveriesList = new ArrayList<>();
        graphicRecorveriesList = widgetUserDataConService.getRecorveries(isAllBranchesFromRecoveries);

        for (ChartItem recorveries : graphicRecorveriesList) {
            if (recorveries.getTypeId() == 1) { // çek 
                totalCheque = totalCheque.add(recorveries.getBigDecimal1());
                if (recorveries.getTypeId2() == 1) { // çekin gecikmiş ödemeler toplamı
                    delayedPaymentCheque = delayedPaymentCheque.add(recorveries.getBigDecimal1());
                }
            } else if (recorveries.getTypeId() == 2) { // senet
                totalBill = totalBill.add(recorveries.getBigDecimal1());
                if (recorveries.getTypeId2() == 1) {// senetin gecikmiş ödemeler toplamı
                    delayedPaymentBill = delayedPaymentBill.add(recorveries.getBigDecimal1());
                }
            } else if (recorveries.getTypeId() == 3) { // kredi
                totalCredit = totalCredit.add(recorveries.getBigDecimal1());
                if (recorveries.getTypeId2() == 1) { // kredinin gecikmiş ödemeler toplamı
                    delayedPaymentCredit = delayedPaymentCredit.add(recorveries.getBigDecimal1());
                }
            } else if (recorveries.getTypeId() == 4) {
                totalOpenPayments = totalOpenPayments.add(recorveries.getBigDecimal1());
            }
        }
        ChartItem chartItemCheque = new ChartItem(); // çek bilgileri listeye eklenir.
        chartItemCheque.setName1(sessionBean.getLoc().getString("cheque"));
        chartItemCheque.setBigDecimal1(totalCheque);
        chartItemCheque.setTypeId(1);
        chartItemCheque.setBigDecimal2(delayedPaymentCheque);
        chartItemCheque.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());

        amountCollectedCheque = totalCheque.subtract(delayedPaymentCheque);
        chartItemCheque.setBigDecimal3(amountCollectedCheque);
        recorveriesList.add(chartItemCheque);

        ChartItem chartItemBill = new ChartItem(); // sneet bilgileri listeye eklenir.
        chartItemBill.setName1(sessionBean.getLoc().getString("bill"));
        chartItemBill.setBigDecimal1(totalBill);
        chartItemBill.setTypeId(2);
        chartItemBill.setBigDecimal2(delayedPaymentBill);
        chartItemBill.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());

        amountCollectedBill = totalBill.subtract(delayedPaymentBill);
        chartItemBill.setBigDecimal3(amountCollectedBill);
        recorveriesList.add(chartItemBill);

        ChartItem chartItemCredit = new ChartItem(); // kredi bilgileri listeye eklenir.
        chartItemCredit.setName1(sessionBean.getLoc().getString("credit"));
        chartItemCredit.setBigDecimal1(totalCredit);
        chartItemCredit.setTypeId(3);
        chartItemCredit.setBigDecimal2(delayedPaymentCredit);
        chartItemCredit.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());
        amountCollectedCredit = totalCredit.subtract(delayedPaymentCredit);
        chartItemCredit.setBigDecimal3(amountCollectedCredit);
        recorveriesList.add(chartItemCredit);

        ChartItem chartItemOpenPayments = new ChartItem();
        chartItemOpenPayments.setName1(sessionBean.getLoc().getString("open"));
        chartItemOpenPayments.setBigDecimal1(totalOpenPayments);
        chartItemOpenPayments.setTypeId(4);
        chartItemOpenPayments.setBigDecimal2(BigDecimal.ZERO);
        chartItemOpenPayments.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());
        chartItemOpenPayments.setBigDecimal3(BigDecimal.ZERO);
        recorveriesList.add(chartItemOpenPayments);

        totalGeneralDelayed = totalGeneralDelayed.add(delayedPaymentCheque).add(delayedPaymentBill).add(delayedPaymentCredit);
        totalGeneral = totalGeneral.add(totalCheque).add(totalBill).add(totalCredit).add(totalOpenPayments);
        totalRemainingGeneral = totalRemainingGeneral.add(amountCollectedBill).add(amountCollectedCheque).add(amountCollectedCredit);

        totalRecoveries = new ChartItem();
        totalRecoveries.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());
        totalRecoveries.setName1(sessionBean.getLoc().getString("total"));
        totalRecoveries.setBigDecimal1(totalGeneral); // genel toplam 
        totalRecoveries.setBigDecimal2(totalGeneralDelayed); // gecikmiş toplam 
        totalRecoveries.setBigDecimal3(totalRemainingGeneral); // kalan toplam

        runnedWidgets.add(widgetName);

    }

    /**
     * Bu metot tahsilatlar widgetinde herhangi bir kayda tıklanıldığında dialog
     * açarak chart üzerinde verileri gösterir.
     */
    public void openRecorveriesGraphic() {
        String type;
        type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("recoiveriesType");
        recoiveriesName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("recoiveriesName");
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(graphicRecorveriesList);

        context.execute("recorveries(" + "'" + data + "'" + "," + Integer.valueOf(type) + ")");
        context.execute("PF('dlg_recorveries').show();");
        context.update("dlgRecorveries");
    }

    public void createPayments(String widgetName) {

        BigDecimal totalCheque = BigDecimal.ZERO,
                delayedPaymentCheque = BigDecimal.ZERO,
                totalBill = BigDecimal.ZERO,
                delayedPaymentBill = BigDecimal.ZERO,
                totalCredit = BigDecimal.ZERO,
                delayedPaymentCredit = BigDecimal.ZERO,
                amountCollectedCredit = BigDecimal.ZERO,
                amountCollectedCheque = BigDecimal.ZERO,
                amountCollectedBill = BigDecimal.ZERO,
                totalOpenPayments = BigDecimal.ZERO,
                totalGeneralDelayed = BigDecimal.ZERO,
                totalGeneral = BigDecimal.ZERO, totalRemainingGeneral = BigDecimal.ZERO;
        paymentsList = new ArrayList<>();
        graphicPaymentList = new ArrayList<>();
        graphicPaymentList = widgetUserDataConService.getPayments(isAllBranchesFromPayments);

        for (ChartItem payments : graphicPaymentList) {
            if (payments.getTypeId() == 1) { // çek 
                totalCheque = totalCheque.add(payments.getBigDecimal1());
                if (payments.getTypeId2() == 1) { // çekin gecikmiş ödemeler toplamı
                    delayedPaymentCheque = delayedPaymentCheque.add(payments.getBigDecimal1());
                }
            } else if (payments.getTypeId() == 2) { // senet
                totalBill = totalBill.add(payments.getBigDecimal1());
                if (payments.getTypeId2() == 1) {// senetin gecikmiş ödemeler toplamı
                    delayedPaymentBill = delayedPaymentBill.add(payments.getBigDecimal1());
                }
            } else if (payments.getTypeId() == 3) { // kredi
                totalCredit = totalCredit.add(payments.getBigDecimal1());
                if (payments.getTypeId2() == 1) { // kredinin gecikmiş ödemeler toplamı
                    delayedPaymentCredit = delayedPaymentCredit.add(payments.getBigDecimal1());
                }
            } else if (payments.getTypeId() == 4) {
                totalOpenPayments = totalOpenPayments.add(payments.getBigDecimal1());
            }
        }
        ChartItem chartItemCheque = new ChartItem(); // çek bilgileri listeye eklenir.
        chartItemCheque.setName1(sessionBean.getLoc().getString("cheque"));
        chartItemCheque.setBigDecimal1(totalCheque);
        chartItemCheque.setTypeId(1);
        chartItemCheque.setBigDecimal2(delayedPaymentCheque);
        chartItemCheque.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());

        amountCollectedCheque = totalCheque.subtract(delayedPaymentCheque);
        chartItemCheque.setBigDecimal3(amountCollectedCheque);
        paymentsList.add(chartItemCheque);

        ChartItem chartItemBill = new ChartItem(); // senet bilgileri listeye eklenir.
        chartItemBill.setName1(sessionBean.getLoc().getString("bill"));
        chartItemBill.setBigDecimal1(totalBill);
        chartItemBill.setTypeId(2);
        chartItemBill.setBigDecimal2(delayedPaymentBill);
        chartItemBill.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());

        amountCollectedBill = totalBill.subtract(delayedPaymentBill);
        chartItemBill.setBigDecimal3(amountCollectedBill);
        paymentsList.add(chartItemBill);

        ChartItem chartItemCredit = new ChartItem(); // kredi bilgileri listeye eklenir.
        chartItemCredit.setName1(sessionBean.getLoc().getString("credit"));
        chartItemCredit.setBigDecimal1(totalCredit);
        chartItemCredit.setTypeId(3);
        chartItemCredit.setBigDecimal2(delayedPaymentCredit);
        chartItemCredit.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());

        amountCollectedCredit = totalCredit.subtract(delayedPaymentCredit);
        chartItemCredit.setBigDecimal3(amountCollectedCredit);
        paymentsList.add(chartItemCredit);

        ChartItem chartItemOpenPayments = new ChartItem();
        chartItemOpenPayments.setName1(sessionBean.getLoc().getString("open"));
        chartItemOpenPayments.setBigDecimal1(totalOpenPayments);
        chartItemOpenPayments.setTypeId(4);
        chartItemOpenPayments.setBigDecimal2(BigDecimal.ZERO);
        chartItemOpenPayments.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());
        chartItemOpenPayments.setBigDecimal3(BigDecimal.ZERO);
        paymentsList.add(chartItemOpenPayments);

        totalPayments = new ChartItem();
        totalGeneralDelayed = totalGeneralDelayed.add(delayedPaymentCheque).add(delayedPaymentBill).add(delayedPaymentCredit);
        totalGeneral = totalGeneral.add(totalCheque).add(totalBill).add(totalCredit).add(totalOpenPayments);
        totalRemainingGeneral = totalRemainingGeneral.add(amountCollectedBill).add(amountCollectedCheque).add(amountCollectedCredit);

        totalPayments.setCurrency1(sessionBean.getUser().getLastBranch().getCurrency());
        totalPayments.setBigDecimal1(totalGeneral); // genel toplam
        totalPayments.setBigDecimal2(totalGeneralDelayed); // geciken toplam
        totalPayments.setBigDecimal3(totalRemainingGeneral); // kalan toplam 

        runnedWidgets.add(widgetName);

    }

    public void openPaymentsGraphic() {

        String type;
        type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paymentsType");
        paymentName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paymentsName");
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(graphicPaymentList);

        context.execute("payments(" + "'" + data + "'" + "," + Integer.valueOf(type) + ")");
        context.execute("PF('dlg_payments').show();");
        context.update("dlgPayments");
    }

    public void createWeeklyCashFlow(String widgetName) {

        weeklyCashFlowList = new ArrayList<>();
        weeklyCashFlowList = widgetUserDataConService.getWeeklyCashFlow(isAllBranchesFromWeeklyCashFlow);
        totalWeeklyEntry = BigDecimal.ZERO;
        totalWeeklyOutFlow = BigDecimal.ZERO;

        for (ChartItem weeklyCash : weeklyCashFlowList) {
            totalWeeklyEntry = totalWeeklyEntry.add(weeklyCash.getBigDecimal1());
            totalWeeklyOutFlow = totalWeeklyOutFlow.add(weeklyCash.getBigDecimal2());
        }
        runnedWidgets.add(widgetName);

    }

    public void openWeeklyCashFlowGraphic() {
        RequestContext context = RequestContext.getCurrentInstance();
        String data = new Gson().toJson(weeklyCashFlowList);

        context.execute("weeklycashflow(" + "'" + data + "'" + ")");
        context.execute("PF('dlg_weeklycash').show();");
        context.update("dlgWeeklyCash");
    }   

    public void createPricesVaryingProducts(String widgetName) {
        selectedPricesVaryingProducts = new ChartItem();
        tempProductVaryingList = widgetUserDataConService.getPricesVaryingProducts(isAllBranchesFromPricesVaryingProducts);
        runnedWidgets.add(widgetName);

    }

    /**
     * type 1 - en çok satışı yapılan stoklar type 2- en çok alım yapan
     * müşteriler type 3 - markalara göre aylık satışlar type 4 -kasiyere göre
     * satışlar 5-satış tipine göre satışlar 6-vadesi gelen ödemeler 7-pompacı
     * satışları 8-kategorilere göre satışlar 9-akaryakıt vardiya satışları
     */
    public void showBetweenDialog(int type) {
        typeSelectedInterval = type;
        switch (typeSelectedInterval) {
            case 1:
                mostSoldStockType = 4;
                break;
            case 2:
                customersBySellMontlyType = 4;
                break;
            case 3:
                monthlySalesBybBrandType = 4;
                break;
            case 4:
                salesByCashierType = 4;
                break;
            case 5:
                salesBySalesType = 4;
                break;
            case 6:
                duePayments = 4;
                break;
            case 7:
                salesByPumperType = 4;
                break;
            case 8:
                monthlySalesByCategorizationType = 4;
                break;
            case 9:
                fuelShiftSalesType = 4;
                break;
        }

        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.HOUR_OF_DAY, 23);
        cale.set(Calendar.MINUTE, 59);
        cale.set(Calendar.SECOND, 59);
        endDate = cale.getTime();

        cale.set(Calendar.HOUR_OF_DAY, 00);
        cale.set(Calendar.MINUTE, 00);
        cale.set(Calendar.SECOND, 00);
        beginDate = cale.getTime();

        RequestContext.getCurrentInstance().execute("PF('dlg_between').show();");
        RequestContext.getCurrentInstance().update("dlgBetween");
    }

    /**
     * Bu metot iki tarih arasında butonu tıklandıktan sınra type parametresini
     * gönderip widgeti güncellemek için kullanılır.
     */
    public void findSelectedInterval() {
        RequestContext.getCurrentInstance().execute("PF('dlg_between').hide();");
        switch (typeSelectedInterval) {
            case 1:
                createMostSoldStocks("productsales", 4);
                RequestContext.getCurrentInstance().update("productsales");
                break;

            case 2:
                createCustomersBySellMontly("customerpurchases", 4);
                RequestContext.getCurrentInstance().update("customerpurchases");
                break;

            case 3:
                createMonthlySalesByBrand("brandsales", 4);
                RequestContext.getCurrentInstance().update("brandsales");
                break;

            case 4:
                createSalesByCashier("cashiersales", 4);
                RequestContext.getCurrentInstance().update("cashiersales");
                break;

            case 5:
                createSalesBySaleType("salesbysaletype", 4);
                RequestContext.getCurrentInstance().update("salesbysaletype");
                break;

            case 6:
                createDuePayments("duepayments", 4);
                RequestContext.getCurrentInstance().update("duepayments");
                break;
            case 7:
                createSalesByPumper("pumpersales", 4);
                RequestContext.getCurrentInstance().update("pumpersales");
                break;

            case 8:
                createMonthlySalesByCategorization("monthlysalesbycategorization", monthlySalesCategoryType, 4);
                RequestContext.getCurrentInstance().update("monthlysalesbycategorization");
                break;

            case 9:
                createFuelShiftSales("fuelshiftsales", 4);
                RequestContext.getCurrentInstance().update("fuelshiftsales");
                break;

        }

    }

    public void setChartParameter(int dateSelect, String widgetName) {
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            isAllBranchesFromWelcome = true;
            isAllBranchesFromSalesByCategorization = true;
            isAllBranchesFromDecreasingStock = true;
            isAllBranchesFromReturnedStock = true;
            isAllBranchesFromRecoveries = true;
            isAllBranchesFromPayments = true;
            isAllBranchesFromWeeklyCashFlow = true;
            isAllBranchesFromPricesVaryingProducts = true;
            isAllBranchesFromPurchasePriceProducts = true;
            isAllBranchesFromProductProfitalibility = true;
            isAllBranchesFromDuePayments = true;
            isAllBranchesFromPumperSales = true;
            isAllBranchesFromFuelShiftSales = true;
            isAllBranchesFromFuelStock = true;

        }

        switch (widgetName) {
            case "productsales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromStockSale = true;
                }
                mostSoldStockType = dateSelect;
                break;
            case "customerpurchases":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromCustomerPurchase = true;
                }
                customersBySellMontlyType = dateSelect;
                break;
            case "brandsales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromBrandSales = true;
                }
                monthlySalesBybBrandType = dateSelect;
                break;
            case "cashiersales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromCashierSales = true;
                }
                salesByCashierType = dateSelect;
                break;
            case "salesbysaletype":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromSaleType = true;
                }
                salesBySalesType = dateSelect;
                break;
            case "duepayments":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromDuePayments = true;
                }
                duePayments = dateSelect;
                break;
            case "washingmachineprofitability":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromWashingMachineProfitability = true;
                }
                stationsBySalesPeriodForWashingMachicne = dateSelect;
                break;
            case "washingsystemproductsalesbyquantity":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromWashingMachineSalesByQuantity = true;
                }
                washingSalesByQuantiy = dateSelect;
                break;
            case "washingsystemproductsalesbyturnover":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromWashingMachineSalesByTurnover = true;
                }
                washingSalesByTurnover = dateSelect;
                break;
            case "washingsystemsales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromWashingSystemSales = true;
                }
                washingSales = dateSelect;
                break;

            case "pumpersales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromPumperSales = true;
                }
                salesByPumperType = dateSelect;
                break;

            case "monthlysalesbycategorization":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromSalesByCategorization = true;
                }
                monthlySalesByCategorizationType = dateSelect;
                break;
            case "fuelshiftsales":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromFuelShiftSales = true;
                }
                fuelShiftSalesType = dateSelect;
                break;

            case "fuelremainingstockinfo":
                if (sessionBean.getUser().getLastBranch().isIsCentral()) {
                    isAllBranchesFromFuelStock = true;
                }
                fuelStocktType = dateSelect;

            default:
                break;

        }
        runnedWidgets.remove(widgetName);
        RequestContext.getCurrentInstance().update(widgetName);
    }

    public void openDialog() {
        RequestContext.getCurrentInstance().execute("blockInput(); blockCommand();");
        categoryBookCheckboxFilterBean.getListOfCategorization().clear();
        if (!listOfCategorization.isEmpty()) {
            if (listOfCategorization.get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.isAll = true;
            } else {
                categoryBookCheckboxFilterBean.isAll = false;
            }

        }
        categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);

    }

    public void updateAllInformation(ActionEvent event) {
        listOfCategorization.clear();
        if (categoryBookCheckboxFilterBean.isAll) {
            Categorization s = new Categorization(0);
            if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
            }
        } else if (!categoryBookCheckboxFilterBean.isAll) {
            if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                }
            }
        }
        listOfCategorization.addAll(categoryBookCheckboxFilterBean.getListOfCategorization());
        if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
            categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
            categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
        }

        RequestContext.getCurrentInstance().update("txtCategory");

        createMonthlySalesByCategorization("monthlysalesbycategorization", monthlySalesCategoryType, monthlySalesByCategorizationType);
        RequestContext.getCurrentInstance().update("monthlysalesbycategorization");

    }

    public void findNotification(int type) {

        if (type == 0) {//okundu
            int result = 0;

            String notificationList = "";
            for (UserNotification notification : tempNotificationList) {
                notificationList = notificationList + "," + String.valueOf(notification.getId());
            }

            if (!notificationList.equals("")) {
                notificationList = notificationList.substring(1, notificationList.length());
            }

            result = userNotificationService.update(notificationList, isAllNotification);
        }

        RequestContext.getCurrentInstance().execute("PF('dlgNotificationVar').hide()");

    }

    public Date formatStringToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date processDate = null;
        try {
            processDate = formatter.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(DashboardBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processDate;

    }

    public Currency findCurrency(int currency_id) {
        Currency currency = new Currency(currency_id);
        return currency;

    }

    public Unit findUnit(int unit_id) {
        Unit unit = new Unit();
        for (Unit unt : listOfUnit) {
            if (unt.getCenterunit_id() == unit_id) {

                unit.setId(unt.getId());
                unit.setName(unt.getName());
                unit.setSortName(unt.getSortName());
                unit.setUnitRounding(unt.getUnitRounding());

            }

        }
        return unit;

    }

    /**
     * Bu metot fiyatı değişen ürünler widgetinde ikona tıklanıldığı zaman
     * listeyi ayrı bir dialogta açmak için kullanılır.
     */
    public void openProductVaryingPricesDialog() {

        lazyPricesVaryingProductsList = findAllPriceVarying();
        RequestContext.getCurrentInstance().update("dlgPricesVaryingProducts");
        RequestContext.getCurrentInstance().execute("PF('dlg_pricesVaryingProducts').show()");

    }

    public LazyDataModel<ChartItem> findAllPriceVarying() {
        return new CentrowizLazyDataModel<ChartItem>() {
            @Override
            public List<ChartItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                List<ChartItem> result = widgetUserDataConService.getLazyPricesVaryingProductsList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, isAllBranchesFromPricesVaryingProducts);
                int count = widgetUserDataConService.countPriceVarying(isAllBranchesFromPricesVaryingProducts);
                lazyPricesVaryingProductsList.setRowCount(count);
                return result;
            }
        };
    }

    public void openProductProfitalibilityDialog() {

        tempProductProfitalibility = findAll();
        RequestContext.getCurrentInstance().update("dlgProductProfitalibility");
        RequestContext.getCurrentInstance().execute("PF('dlg_productProfitalibility').show()");

    }

    public LazyDataModel<ChartItem> findAll() {
        return new CentrowizLazyDataModel<ChartItem>() {
            @Override
            public List<ChartItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                List<ChartItem> result = widgetUserDataConService.tempProductProfitalibility(first, pageSize, sortField, convertSortOrder(sortOrder), filters, isAllBranchesFromProductProfitalibility);
                int count = widgetUserDataConService.count(isAllBranchesFromProductProfitalibility);
                tempProductProfitalibility.setRowCount(count);
                return result;
            }
        };

    }

    /**
     * Bu metot ürün satışları widgetinde ikona tıklanıldığı zaman listeyi ayrı
     * bir dialogta açmak için kullanılır.
     */
    public void openProductSalesDialog(int type) {

        changeStock = 2;
        mostSoldStockType = type;
        tempStockSalesList = widgetUserDataConService.getMostSoldStocks(type, beginDate, endDate, isAllBranchesFromStockSale, changeStock);

        for (ChartItem tempStockSalesList : tempStockSalesList) {

            String s = tempStockSalesList.getName1();
            int index = tempStockSalesList.getName1().indexOf('-');
            String s1 = s.substring(index + 1, s.length());
            tempStockSalesList.setName1(s1);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_productSales').show()");
        RequestContext.getCurrentInstance().update("dlgProductSales");

        changeStock = 1;

    }

    /**
     * Bu metot Marka satışları widgetinde ikona tıklanıldığı zaman listeyi ayrı
     * bir dialogta açmak için kullanılır.
     */
    public void openBrandSalesDialog(int type) {

        changeBrand = 2;
        monthlySalesBybBrandType = type;
        tempSalesByBrandList = widgetUserDataConService.getSalesByBrand(type, beginDate, endDate, isAllBranchesFromBrandSales, changeBrand);

        for (ChartItem tempSalesByBrandList : tempSalesByBrandList) {

            String s = tempSalesByBrandList.getName1();
            int index = tempSalesByBrandList.getName1().indexOf('-');
            String s1 = s.substring(index + 1, s.length());
            tempSalesByBrandList.setName1(s1);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_brandSales').show()");
        RequestContext.getCurrentInstance().update("dlgBrandSales");

        changeBrand = 1;
    }

    /**
     * Bu metot Müşteri Alışları widgetinde ikona tıklanıldığı zaman listeyi
     * ayrı bir dialogta açmak için kullanılır.
     */
    public void openCustomerPurchasesDialog(int type) {

        changeCustomerPurchases = 2;
        customersBySellMontlyType = type;
        tempCustomerPurchasesList = widgetUserDataConService.getMostCustomersBySale(type, beginDate, endDate, isAllBranchesFromCustomerPurchase, changeCustomerPurchases);

        for (ChartItem tempCustomerPurchasesList : tempCustomerPurchasesList) {

            String s = tempCustomerPurchasesList.getName1();
            int index = tempCustomerPurchasesList.getName1().indexOf('-');
            String s1 = s.substring(index + 1, s.length());
            tempCustomerPurchasesList.setName1(s1);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_customerPurchases').show()");
        RequestContext.getCurrentInstance().update("dlgCustomerPurchases");

        changeCustomerPurchases = 1;
    }

    /**
     * Bu metot Pompacı bazlı satış widgetinde ikona tıklanıldığı zaman listeyi
     * ayrı bir dialogta açmak için kullanılır.
     */
    public void openPumperSalesDialog(int type) {

        changePumper = 2;
        salesByPumperType = type;
        tempPumperSalesList = widgetUserDataConService.getSalesByPumper(type, beginDate, endDate, isAllBranchesFromPumperSales, changePumper);

        RequestContext.getCurrentInstance().execute("PF('dlg_pumperSales').show()");
        RequestContext.getCurrentInstance().update("dlgPumperSales");

        changePumper = 1;
    }

    /**
     * Bu metot Kasiyer satışları widgetinde ikona tıklanıldığı zaman listeyi
     * ayrı bir dialogta açmak için kullanılır.
     */
    public void openCashierSalesDialog(int type) {

        changeCashier = 2;
        salesByCashierType = type;
        tempCashierSalesList = widgetUserDataConService.getSalesByCashier(type, beginDate, endDate, isAllBranchesFromCashierSales, changeCashier);

        for (ChartItem tempCashierSalesList : tempCashierSalesList) {

            String s = tempCashierSalesList.getName2();
            int index = tempCashierSalesList.getName2().indexOf('-');
            String s1 = s.substring(index + 1, s.length());
            tempCashierSalesList.setName2(s1);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_cashierSales').show()");
        RequestContext.getCurrentInstance().update("dlgCashierSales");

        changeCashier = 1;
    }

    public void createPurchasePriceHighProduct(String widgetName) {
        purchasePriceHighProductList = widgetUserDataConService.getPurchasePriceHighProducts(isAllBranchesFromPurchasePriceProducts);
        runnedWidgets.add(widgetName);
    }

    public void createProductProfitalibility(String widgetName) {
        productProfitalibilityList = widgetUserDataConService.getProductProfitalibility(isAllBranchesFromProductProfitalibility);
        runnedWidgets.add(widgetName);
    }

    public void goToPurchasePriceHighProduct() {
        String id;

        id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        List<Object> list = new ArrayList<>();

        Map<String, Object> filt = new HashMap<>();
        List<Stock> findAll = stockService.findAll(0, 1, "stck.id", "ASC", filt, " AND stck.id = " + Integer.parseInt(id));
        if (findAll.size() > 0) {
            list.add(findAll.get(0));
        }
     
        marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", list, 1, 12);
        RequestContext.getCurrentInstance().update("mainPanel");
    }

    public void goToPurchaseProductProfitalibility() {
        String id;

        id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        List<Object> list = new ArrayList<>();

        Map<String, Object> filt = new HashMap<>();
        List<Stock> findAll = stockService.findAll(0, 1, "stck.id", "ASC", filt, " AND stck.id = " + Integer.parseInt(id));
        if (findAll.size() > 0) {
            list.add(findAll.get(0));
        }
        marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", list, 1, 12);
        RequestContext.getCurrentInstance().update("mainPanel");
    }

    //yıkama Makinesi satışları istasyon bazında 
    public void createWashingMachicnesSales(String widgetName) {
        if (stationsBySalesPeriodForWashingMachicne == 4) {
            long diff = endDate.getTime() - beginDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (days < 0 || days > 31) {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.loc.getString("error"));
                message.setDetail(sessionBean.loc.getString("twodatesdiffcannotbemorethan31days"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
                runnedWidgets.add(widgetName);
                context.update("washingmachineprofitability");
                return;
            }
        }
        
        Calendar cal = GregorianCalendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        switch (stationsBySalesPeriodForWashingMachicne) {
            case 1: // Günlük İse
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 2:// Haftalık İse
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 3: // Aylık İse
                cal.set(Calendar.DAY_OF_MONTH, 01);
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 4://iki tarih aralığı seçecek
                RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashing').hide();");
                break;
            default:
                break;
        }
        List<ChartItem> list = widgetUserDataConService.getStationBySalesForWashingMachicne(beginDate, endDate, isAllBranchesFromWashingMachineProfitability);

        if (!list.isEmpty()) {
            ChartItem automatSaleReport = new ChartItem();
            automatSaleReport.setName(sessionBean.getLoc().getString("electricalexpense")); // elektrik gideri eklenir.
            automatSaleReport.setUnitName("kW");

            BigDecimal electricQuantity = BigDecimal.ZERO;
            int electricTime = 0, waterTime = 0;
            BigDecimal electricExpense = BigDecimal.ZERO;
            BigDecimal electricTotalWase = BigDecimal.ZERO;
            BigDecimal waterQuantity = BigDecimal.ZERO, waterExpense = BigDecimal.ZERO, waterTotalWase = BigDecimal.ZERO;

            for (ChartItem automatSaleReport1 : list) {
                electricQuantity = automatSaleReport1.getElectricQuantity().add(automatSaleReport1.getElectricQuantity());
                electricTime += automatSaleReport1.getElectricOperationTime().doubleValue();
                electricExpense = automatSaleReport1.getElectricExpense().add(automatSaleReport1.getElectricExpense());
                electricTotalWase = automatSaleReport1.getTotalElectricAmount().add(automatSaleReport1.getTotalElectricAmount());
                waterQuantity = automatSaleReport1.getWaterWorkingAmount().add(automatSaleReport1.getWaterWorkingAmount());
                waterTime += automatSaleReport1.getWaterWorkingTime();
                waterExpense = automatSaleReport1.getWaterExpense().add(automatSaleReport1.getWaterExpense());
                waterTotalWase = automatSaleReport1.getWaterWaste().add(automatSaleReport1.getWaterWaste());
            }

            automatSaleReport.setQuantity(electricQuantity);
            automatSaleReport.setOperationTime(electricTime);
            automatSaleReport.setTotalExpense(electricExpense);
            automatSaleReport.setWaste(electricTotalWase);
            automatSaleReport.setTotalIncome(BigDecimal.valueOf(0));
            int comp = electricExpense.compareTo(BigDecimal.ZERO);
            BigDecimal bd = comp == 0 ? BigDecimal.ZERO : electricExpense.multiply(BigDecimal.valueOf(-1));
            automatSaleReport.setTotalWinnings(bd);
            list.add(automatSaleReport);

            ChartItem automatSaleReportWater = new ChartItem();
            automatSaleReportWater.setName(sessionBean.getLoc().getString("waterexpense")); // su gideri eklenir.
            automatSaleReportWater.setUnitName("LT");
            automatSaleReportWater.setQuantity(waterQuantity);
            automatSaleReportWater.setOperationTime(waterTime);
            automatSaleReportWater.setTotalExpense(waterExpense);
            automatSaleReportWater.setWaste(waterTotalWase);
            automatSaleReportWater.setTotalIncome(BigDecimal.valueOf(0));
            int comp2 = waterExpense.compareTo(BigDecimal.ZERO);
            BigDecimal bd2 = comp2 == 0 ? BigDecimal.ZERO : waterExpense.multiply(BigDecimal.valueOf(-1));
            automatSaleReportWater.setTotalWinnings(bd2);
            list.add(automatSaleReportWater);
        }
        calculateTotal(list);
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempWashingMachineProfitability = isAllBranchesFromWashingMachineProfitability;
        }
        runnedWidgets.add(widgetName);
        stationBySalesForWashingmachicne.clear();
        stationBySalesForWashingmachicne.addAll(list);

    }

    public void calculateTotal(List<ChartItem> list) {
        BigDecimal total = BigDecimal.valueOf(0), totalIncome = BigDecimal.valueOf(0), totalExpense = BigDecimal.valueOf(0);
        for (ChartItem listOfObject : list) {
            total = total.add(listOfObject.getTotalWinnings());
            totalIncome = totalIncome.add(listOfObject.getTotalIncome());
            totalExpense = totalExpense.add(listOfObject.getTotalExpense());
        }
        totalNetExpense = totalExpense;
        totalNetIncome = totalIncome;
        netTotal = total;
    }

    public void createWashingMachicnesSalesByQuantity(String widgetName) {
        System.out.println("-------create sales by quantity---------");
        if (washingSalesByQuantiy == 4) {
            long diff = endDate.getTime() - beginDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (days < 0 || days > 31) {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.loc.getString("error"));
                message.setDetail(sessionBean.loc.getString("twodatesdiffcannotbemorethan31days"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
                runnedWidgets.add(widgetName);
                context.update("washingsystemproductsalesbyquantity");
                return;
            }
        }

        Calendar cal = GregorianCalendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        switch (washingSalesByQuantiy) {
            case 1: // Günlük İse
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 2:// Haftalık İse
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 3: // Aylık İse
                cal.set(Calendar.DAY_OF_MONTH, 01);
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 4://iki tarih aralığı seçecek
                RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashingbyquantity').hide();");
                break;
            default:
                break;
        }
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempWashingMachineSalesByQuantity = isAllBranchesFromWashingMachineSalesByQuantity;
        }
        List<ChartItem> list = widgetUserDataConService.getWashingSalesByQuantity(beginDate, endDate, isAllBranchesFromWashingMachineSalesByQuantity);

        runnedWidgets.add(widgetName);

        calcQantity(list);
        washingSalesByQuantityList.clear();
        washingSalesByQuantityList.addAll(list);

    }

    public void calcQantity(List<ChartItem> list) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChartItem chartItem : list) {
            total = total.add(chartItem.getQuantity());
        }
        totalQuantity = total;
    }

    public void createWashingMachicnesSalesByTurnover(String widgetName) {
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            tempWashingMachineSalesByTurnover = isAllBranchesFromWashingMachineSalesByTurnover;
        }
        if (washingSalesByTurnover == 4) {
            long diff = endDate.getTime() - beginDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (days < 0 || days > 31) {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.loc.getString("error"));
                message.setDetail(sessionBean.loc.getString("twodatesdiffcannotbemorethan31days"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
                runnedWidgets.add(widgetName);
                context.update("washingsystemproductsalesbyturnover");
                return;
            }
        }

        Calendar cal = GregorianCalendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        switch (washingSalesByTurnover) {
            case 1: // Günlük İse
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 2:// Haftalık İse
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 3: // Aylık İse
                cal.set(Calendar.DAY_OF_MONTH, 01);
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 4://iki tarih aralığı seçecek
                RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashingbyturnover').hide();");
                break;
            default:
                break;
        }
        List<ChartItem> list = widgetUserDataConService.getWashingSalesByTurnover(beginDate, endDate, isAllBranchesFromWashingMachineSalesByTurnover);

        runnedWidgets.add(widgetName);

        calcTotal(list);

        washingSalesByTurnoverList.clear();
        washingSalesByTurnoverList.addAll(list);
    }

    public void calcTotal(List<ChartItem> list) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChartItem chartItem : list) {
            total = total.add(chartItem.getTotalIncome());
        }
        totalSales = total;
    }

    /**
     * Yıkama satışları line grafiği verilerini hazırlamak için kullanılır.
     *
     * @param widgetName
     */
    public void createWashingSystemSales(String widgetName) {
        Calendar cal = GregorianCalendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        switch (washingSales) {
            case 2:// Haftalık İse
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.DAY_OF_MONTH, 1);
                beginDate = cal.getTime();
                endDate = null;
                break;
            case 3: // Aylık İse
                cal.add(Calendar.DAY_OF_MONTH, -15);
                beginDate = cal.getTime();
                endDate = null;
                break;
            default:
                break;
        }
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {

            tempWashingSystemSales = isAllBranchesFromWashingSystemSales;
        }
        List<ChartItem> list = widgetUserDataConService.getWashingSales(beginDate, endDate, washingSales, isAllBranchesFromWashingSystemSales);

        List<ChartItem> difList = new ArrayList<>();

        // Farklı stokları bulmak için yapıldı.,
        boolean isThere = false;
        for (ChartItem chartItem : list) {
            for (ChartItem difItem : difList) {
                isThere = false;
                if (difItem.getStockId() == chartItem.getStockId()) {
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                difList.add(chartItem);
            }
        }

        List<ChartItem> tempList = new ArrayList<>();
        BigDecimal firstMonth = BigDecimal.ZERO, secondMonth = BigDecimal.ZERO, thirdMonth = BigDecimal.ZERO, fourthMonth = BigDecimal.ZERO, fifthMonth = BigDecimal.ZERO, sixthMonth = BigDecimal.ZERO, seventhMonth = BigDecimal.ZERO, eightMonth = BigDecimal.ZERO, nineMonth = BigDecimal.ZERO, tenMonth = BigDecimal.ZERO, eleventhMonth = BigDecimal.ZERO, twelfthMonth = BigDecimal.ZERO, thirteenthMonth = BigDecimal.ZERO, fourteenthMonth = BigDecimal.ZERO, fifteenthMonth = BigDecimal.ZERO;
        JsonArray jsonArray = new JsonArray();

        for (ChartItem difItem : difList) {
            if (washingSales == 3) {

                // son 15 günün ayın kaçı olduğunu numareic olarak verir
                int day = 0;

                Calendar clc = GregorianCalendar.getInstance();
                //   clc.add(Calendar.DAY_OF_MONTH, -1);
                day = clc.get(Calendar.DAY_OF_MONTH);
                firstMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -1);
                day = clc.get(Calendar.DAY_OF_MONTH);
                secondMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -2);
                day = clc.get(Calendar.DAY_OF_MONTH);
                thirdMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -3);
                day = clc.get(Calendar.DAY_OF_MONTH);
                fourthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -4);
                day = clc.get(Calendar.DAY_OF_MONTH);
                fifthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -5);
                day = clc.get(Calendar.DAY_OF_MONTH);
                sixthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -6);
                day = clc.get(Calendar.DAY_OF_MONTH);
                seventhMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -7);
                day = clc.get(Calendar.DAY_OF_MONTH);
                eightMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -8);
                day = clc.get(Calendar.DAY_OF_MONTH);
                nineMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -9);
                day = clc.get(Calendar.DAY_OF_MONTH);
                tenMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -10);
                day = clc.get(Calendar.DAY_OF_MONTH);
                eleventhMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -11);
                day = clc.get(Calendar.DAY_OF_MONTH);
                twelfthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -12);
                day = clc.get(Calendar.DAY_OF_MONTH);
                thirteenthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -13);
                day = clc.get(Calendar.DAY_OF_MONTH);
                fourteenthMonth = getDailyQuantity(day, list, tempList, difItem);

                clc = GregorianCalendar.getInstance();
                clc.add(Calendar.DAY_OF_MONTH, -14);
                day = clc.get(Calendar.DAY_OF_MONTH);
                fifteenthMonth = getDailyQuantity(day, list, tempList, difItem);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -14);

                JsonObject jsonObject = new JsonObject();

                jsonObject = new JsonObject();
                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", fifteenthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();
                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -13);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", fourteenthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -12);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", thirteenthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -11);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", twelfthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -10);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", eleventhMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -9);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", tenMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -8);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", nineMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -7);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", eightMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -6);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", seventhMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -5);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", sixthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -4);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", fifthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -3);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", fourthMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -2);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", thirdMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", secondMonth);

                jsonArray.add(jsonObject);

                jsonObject = new JsonObject();

                calendar = GregorianCalendar.getInstance();
                calendar.get(Calendar.DAY_OF_MONTH);

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", String.valueOf(StaticMethods.convertToDateFormat("yyyy/MM/dd HH:mm:ss  ", calendar.getTime())));
                jsonObject.addProperty("quantity", firstMonth);

                jsonArray.add(jsonObject);
            } else {
                firstMonth = getMonthlyQuantity(1, list, tempList, difItem);
                secondMonth = getMonthlyQuantity(2, list, tempList, difItem);
                thirdMonth = getMonthlyQuantity(3, list, tempList, difItem);
                fourthMonth = getMonthlyQuantity(4, list, tempList, difItem);
                fifthMonth = getMonthlyQuantity(5, list, tempList, difItem);
                sixthMonth = getMonthlyQuantity(6, list, tempList, difItem);
                seventhMonth = getMonthlyQuantity(7, list, tempList, difItem);

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 1);
                jsonObject.addProperty("quantity", firstMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 2);
                jsonObject.addProperty("quantity", secondMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 3);
                jsonObject.addProperty("quantity", thirdMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 4);
                jsonObject.addProperty("quantity", fourthMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 5);
                jsonObject.addProperty("quantity", fifthMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 6);
                jsonObject.addProperty("quantity", sixthMonth);

                jsonArray.add(jsonObject);
                jsonObject = new JsonObject();

                jsonObject.addProperty("name", difItem.getName());
                jsonObject.addProperty("stock", difItem.getStockId());
                jsonObject.addProperty("month", 7);
                jsonObject.addProperty("quantity", seventhMonth);
                jsonArray.add(jsonObject);
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();

        runnedWidgets.add(widgetName);
        context.execute("washingsystemsales(" + "'" + jsonArray.toString() + "'," + washingSales + ")");
    }

    public BigDecimal getMonthlyQuantity(int month, List<ChartItem> list, List<ChartItem> tempList, ChartItem difItem) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChartItem chartItem : list) {//1
            if (difItem.getStockId() == chartItem.getStockId() && chartItem.getMonth() == month) {
                total = chartItem.getQuantity().add(chartItem.getQuantity());
            }
        }
        return total;
    }

    public BigDecimal getDailyQuantity(int month, List<ChartItem> list, List<ChartItem> tempList, ChartItem difItem) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChartItem chartItem : list) {//1           
            if (difItem.getStockId() == chartItem.getStockId() && chartItem.getMonth() == month) {
                total = chartItem.getQuantity();
            }
        }
        return total;
    }

    public void showBetweenDialogForWashing(int type) {

        if (type == 1) {
            stationsBySalesPeriodForWashingMachicne = 4;
        } else if (type == 2) {
            washingSalesByQuantiy = 4;
        } else if (type == 3) {
            washingSalesByTurnover = 4;
        } else if (type == 4) {
            //   fillSaleInfomation = 4;      
        }
        Calendar cale = Calendar.getInstance();

        cale.setTime(new Date());

        cale.set(Calendar.HOUR_OF_DAY, 23);
        cale.set(Calendar.MINUTE, 59);
        cale.set(Calendar.SECOND, 59);
        endDate = cale.getTime();

        cale.set(Calendar.HOUR_OF_DAY, 00);
        cale.set(Calendar.MINUTE, 00);
        cale.set(Calendar.SECOND, 00);
        cale.add(Calendar.DAY_OF_MONTH, -1);
        beginDate = cale.getTime();

        if (type == 1) {

            RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashing').show();");
            RequestContext.getCurrentInstance().update("dlgBetweenWashing");
        } else if (type == 2) {

            RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashingbyquantity').show();");
            RequestContext.getCurrentInstance().update("dlgBetweenWashingByQuantity");
        } else if (type == 3) {

            RequestContext.getCurrentInstance().execute("PF('dlg_betweenwashingbyturnover').show();");
            RequestContext.getCurrentInstance().update("dlgBetweenWashingByTurnover");
        } else if (type == 4) { // dolum ve satış bilgileri

            RequestContext.getCurrentInstance().execute("PF('dlg_betweenfillsaleinfo').show();");
            RequestContext.getCurrentInstance().update("dlgBetweenFillSaleInfo");
        }
    }

    public void onRowDoubleClick(SelectEvent event) {

        userNotification = new UserNotification();
        userNotification = (UserNotification) event.getObject();

        if (userNotification.getTypeId() == 102) {
            RequestContext.getCurrentInstance().execute("PF('dlgNotificationVar').hide();");
            RequestContext.getCurrentInstance().execute("goToOrderRecord();");
        }

    }

    public void selectedCheckbox(SelectEvent event) {
        UserNotification s = new UserNotification();
        s = (UserNotification) event.getObject();

        boolean isThere = false;
        for (UserNotification t : tempNotificationList) {
            if (s.getId() == t.getId()) {
                isThere = true;
                break;

            } else {
                isThere = false;
            }
        }

        if (!isThere) {
            tempNotificationList.add(s);
        }
    }

    public void unSelectedCheckbox(UnselectEvent event) {

        UserNotification us = new UserNotification();
        us = (UserNotification) event.getObject();

        for (Iterator<UserNotification> iterator = tempNotificationList.iterator(); iterator.hasNext();) {
            UserNotification next = iterator.next();

            if (next.getId() == us.getId()) {
                iterator.remove();
            }
        }
    }

    public void toggleSelectedCheckbox(ToggleSelectEvent event) {
        if (event.isSelected()) {
            tempNotificationList.addAll(selectedNotifications);
        } else {

            //  Silme işlemi
            for (Iterator<UserNotification> iterator = tempNotificationList.iterator(); iterator.hasNext();) {
                UserNotification next = iterator.next();
                for (UserNotification notUser : listNotUser) {
                    if (next.getId() == notUser.getId()) {
                        iterator.remove();
                        break;
                    }
                }
            }
            selectedNotifications.addAll(tempNotificationList);
        }

        RequestContext.getCurrentInstance().update("dlgNotificationDatatable");

    }

    /**
     * Tüm checkboxları tikler veya tikini kaldırır(Yani tüm elemanları seçer ya
     * da seçmez).
     *
     */
    public void changeIsAll() {

        RequestContext.getCurrentInstance().update("btnItIsRead");

    }

    public void goToOrderRecord() {
        List<Object> list = new ArrayList<>();
        if (userNotification.getTypeId() == 102) {
            list.add(userNotification.getListOfNotification().get(0));
            marwiz.goToPage("/pages/finance/order/order.xhtml", list, 1, 228);
        }
    }

    /**
     * Bildirim listesini lazy data modele göre çeker.
     *
     *
     * @param where
     * @param userData
     * @return
     */
    public LazyDataModel<UserNotification> findAll(String where, UserData userData) {
        return new CentrowizLazyDataModel<UserNotification>() {

            @Override
            public List<UserNotification> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                List<UserNotification> result = userNotificationService.findUserNotification(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, userData);
                listNotUser.clear();
                listNotUser.addAll(result);
                int count = userNotificationService.count(where, userData);
                findCheckNotification();
                listOfUserNotifications.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void showNotification() {

        if (listOfUserNotifications.getRowCount() > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlgNotificationVar').show()");
        }
    }

    public void findCheckNotification() {

        Boolean isThere = false;
        for (UserNotification t : tempNotificationList) {
            for (UserNotification s : selectedNotifications) {
                if (t.getId() == s.getId()) {
                    isThere = true;
                    break;
                } else {
                    isThere = false;
                }
            }
            if (!isThere) {
                selectedNotifications.add(t);
            }
        }
    }

}
