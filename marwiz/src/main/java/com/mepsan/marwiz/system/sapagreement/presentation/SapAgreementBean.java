/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapagreement.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.PaymentType;
import com.mepsan.marwiz.general.model.general.ZSeries;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.paymenttype.business.IPaymentTypeService;
import com.mepsan.marwiz.system.sapagreement.business.ISapAgreementService;
import com.mepsan.marwiz.system.sapagreement.dao.SapAgreement;
import com.mepsan.marwiz.system.zseries.business.IZSeriesService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.json.JSONObject;
import org.primefaces.context.RequestContext;
import sun.font.EAttribute;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class SapAgreementBean extends GeneralDefinitionBean<SapAgreement> {

    @ManagedProperty(value = "#{sapAgreementService}")
    private ISapAgreementService sapAgreementService;

    @ManagedProperty(value = "#{zSeriesService}")
    private IZSeriesService zSeriesService;

    @ManagedProperty(value = "#{paymentTypeService}")
    private IPaymentTypeService paymentTypeService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private boolean isFind = false;
    private boolean isSend;
    private Date date;
    private Date beginDate;
    private Date endDate;
    private List<SapAgreement> listOfFuel;
    private List<SapAgreement> listOfFuelAll;
    private List<SapAgreement> listFuelTestSales;
    private List<SapAgreement> listOfExpense;
    private List<SapAgreement> listOfFuelOilZSeries;
    private List<SapAgreement> listOfMarketZSeries;
    private List<SapAgreement> listOfExchangeEntries;
    private List<SapAgreement> listOfTotals;
    private List<SapAgreement> listOfPaymentTypes;
    private List<SapAgreement> listOfPosSales;
    private List<SapAgreement> listOfPosSalesDetail;
    private List<SapAgreement> listOfBankGroupPosSales;
    private List<SapAgreement> listOfSafeTransfer;
    private List<SapAgreement> listOfSendToBank;
    private List<SapAgreement> listOfCurrency;
    private List<SapAgreement> listOfEndDay;
    private Map<String, SapAgreement> bankPosSale;

    private BigDecimal totalFuelLiter;
    private BigDecimal totalFuelTotalMoney;
    private BigDecimal totalExpense;
    private BigDecimal totalExchangeQuantity;
    private BigDecimal totalExchangeTotalMoney;
    private BigDecimal totalMarketZSeries;
    private BigDecimal totalPaymentTypes;
    private BigDecimal totalFuelZSeriesQuantity;
    private BigDecimal totalFuelZSeriesTotalMoney;
    private BigDecimal differenceFuel;
    private BigDecimal differenceMarket;
    private BigDecimal totalMarketSales;
    private BigDecimal totalPosSalesMoney;
    private BigDecimal totalSafeTransfer;
    private BigDecimal totalBankSend;
    private BigDecimal totalMarketSaleReturn;
    private SapAgreement selectedPosSales;
    private BigDecimal cashPayment;
    private BigDecimal nonPosCollection;//pos harici tahsilat
    private BigDecimal totalCollection;//Toplam tahsilat
    private BigDecimal saleCollection;//Satış-Tahsilat
    private BigDecimal automationSaleDifference;//Otomasyon satış Farkı
    private BigDecimal transferAutomationSaleDifference;//Devir Otomasyon satış Farkı
    private BigDecimal transferMarketSaleDifference;//Devir market satış Farkı
    private BigDecimal totalMarketReturnWithSale;
    private BigDecimal testSalesQuantityTotal;
    private BigDecimal testSalesTotalMoneyTotal;

    private boolean isThereInMarwiz;//Kayıtların marwiz loglarından mı web servisten mi alındığının bilgisi 
    private boolean isCatchReturnWithSale;
    private boolean isView;

    private SapAgreement sapAgreement;
    private Map<String, SapAgreement> fuelStockTotalsCollection;

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SapAgreement> getListOfFuel() {
        return listOfFuel;
    }

    public void setListOfFuel(List<SapAgreement> listOfFuel) {
        this.listOfFuel = listOfFuel;
    }

    public List<SapAgreement> getListOfExpense() {
        return listOfExpense;
    }

    public void setListOfExpense(List<SapAgreement> listOfExpense) {
        this.listOfExpense = listOfExpense;
    }

    public List<SapAgreement> getListOfFuelOilZSeries() {
        return listOfFuelOilZSeries;
    }

    public void setListOfFuelOilZSeries(List<SapAgreement> listOfFuelOilZSeries) {
        this.listOfFuelOilZSeries = listOfFuelOilZSeries;
    }

    public List<SapAgreement> getListOfMarketZSeries() {
        return listOfMarketZSeries;
    }

    public void setListOfMarketZSeries(List<SapAgreement> listOfMarketZSeries) {
        this.listOfMarketZSeries = listOfMarketZSeries;
    }

    public List<SapAgreement> getListOfExchangeEntries() {
        return listOfExchangeEntries;
    }

    public void setListOfExchangeEntries(List<SapAgreement> listOfExchangeEntries) {
        this.listOfExchangeEntries = listOfExchangeEntries;
    }

    public List<SapAgreement> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<SapAgreement> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public ISapAgreementService getSapAgreementService() {
        return sapAgreementService;
    }

    public void setSapAgreementService(ISapAgreementService sapAgreementService) {
        this.sapAgreementService = sapAgreementService;
    }

    public BigDecimal getTotalFuelLiter() {
        return totalFuelLiter;
    }

    public void setTotalFuelLiter(BigDecimal totalFuelLiter) {
        this.totalFuelLiter = totalFuelLiter;
    }

    public BigDecimal getTotalFuelTotalMoney() {
        return totalFuelTotalMoney;
    }

    public void setTotalFuelTotalMoney(BigDecimal totalFuelTotalMoney) {
        this.totalFuelTotalMoney = totalFuelTotalMoney;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
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

    public BigDecimal getTotalExchangeQuantity() {
        return totalExchangeQuantity;
    }

    public void setTotalExchangeQuantity(BigDecimal totalExchangeQuantity) {
        this.totalExchangeQuantity = totalExchangeQuantity;
    }

    public BigDecimal getTotalExchangeTotalMoney() {
        return totalExchangeTotalMoney;
    }

    public void setTotalExchangeTotalMoney(BigDecimal totalExchangeTotalMoney) {
        this.totalExchangeTotalMoney = totalExchangeTotalMoney;
    }

    public BigDecimal getTotalMarketZSeries() {
        return totalMarketZSeries;
    }

    public void setTotalMarketZSeries(BigDecimal totalMarketZSeries) {
        this.totalMarketZSeries = totalMarketZSeries;
    }

    public List<SapAgreement> getListOfPaymentTypes() {
        return listOfPaymentTypes;
    }

    public void setListOfPaymentTypes(List<SapAgreement> listOfPaymentTypes) {
        this.listOfPaymentTypes = listOfPaymentTypes;
    }

    public BigDecimal getTotalPaymentTypes() {
        return totalPaymentTypes;
    }

    public void setTotalPaymentTypes(BigDecimal totalPaymentTypes) {
        this.totalPaymentTypes = totalPaymentTypes;
    }

    public List<SapAgreement> getListOfPosSales() {
        return listOfPosSales;
    }

    public void setListOfPosSales(List<SapAgreement> listOfPosSales) {
        this.listOfPosSales = listOfPosSales;
    }

    public BigDecimal getTotalFuelZSeriesQuantity() {
        return totalFuelZSeriesQuantity;
    }

    public void setTotalFuelZSeriesQuantity(BigDecimal totalFuelZSeriesQuantity) {
        this.totalFuelZSeriesQuantity = totalFuelZSeriesQuantity;
    }

    public BigDecimal getTotalFuelZSeriesTotalMoney() {
        return totalFuelZSeriesTotalMoney;
    }

    public void setTotalFuelZSeriesTotalMoney(BigDecimal totalFuelZSeriesTotalMoney) {
        this.totalFuelZSeriesTotalMoney = totalFuelZSeriesTotalMoney;
    }

    public BigDecimal getDifferenceFuel() {
        return differenceFuel;
    }

    public void setDifferenceFuel(BigDecimal differenceFuel) {
        this.differenceFuel = differenceFuel;
    }

    public BigDecimal getTotalMarketSales() {
        return totalMarketSales;
    }

    public void setTotalMarketSales(BigDecimal totalMarketSales) {
        this.totalMarketSales = totalMarketSales;
    }

    public SapAgreement getSelectedPosSales() {
        return selectedPosSales;
    }

    public void setSelectedPosSales(SapAgreement selectedPosSales) {
        this.selectedPosSales = selectedPosSales;
    }

    public List<SapAgreement> getListOfPosSalesDetail() {
        return listOfPosSalesDetail;
    }

    public void setListOfPosSalesDetail(List<SapAgreement> listOfPosSalesDetail) {
        this.listOfPosSalesDetail = listOfPosSalesDetail;
    }

    public BigDecimal getTotalPosSalesMoney() {
        return totalPosSalesMoney;
    }

    public void setTotalPosSalesMoney(BigDecimal totalPosSalesMoney) {
        this.totalPosSalesMoney = totalPosSalesMoney;
    }

    public SapAgreement getSapAgreement() {
        return sapAgreement;
    }

    public void setSapAgreement(SapAgreement sapAgreement) {
        this.sapAgreement = sapAgreement;
    }

    public List<SapAgreement> getListOfSafeTransfer() {
        return listOfSafeTransfer;
    }

    public void setListOfSafeTransfer(List<SapAgreement> listOfSafeTransfer) {
        this.listOfSafeTransfer = listOfSafeTransfer;
    }

    public IZSeriesService getzSeriesService() {
        return zSeriesService;
    }

    public void setzSeriesService(IZSeriesService zSeriesService) {
        this.zSeriesService = zSeriesService;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SapAgreement> getListOfSendToBank() {
        return listOfSendToBank;
    }

    public void setListOfSendToBank(List<SapAgreement> listOfSendToBank) {
        this.listOfSendToBank = listOfSendToBank;
    }

    public Map<String, SapAgreement> getBankPosSale() {
        return bankPosSale;
    }

    public void setBankPosSale(Map<String, SapAgreement> bankPosSale) {
        this.bankPosSale = bankPosSale;
    }

    public List<SapAgreement> getListOfBankGroupPosSales() {
        return listOfBankGroupPosSales;
    }

    public void setListOfBankGroupPosSales(List<SapAgreement> listOfBankGroupPosSales) {
        this.listOfBankGroupPosSales = listOfBankGroupPosSales;
    }

    public BigDecimal getTotalSafeTransfer() {
        return totalSafeTransfer;
    }

    public void setTotalSafeTransfer(BigDecimal totalSafeTransfer) {
        this.totalSafeTransfer = totalSafeTransfer;
    }

    public BigDecimal getTotalBankSend() {
        return totalBankSend;
    }

    public void setTotalBankSend(BigDecimal totalBankSend) {
        this.totalBankSend = totalBankSend;
    }

    public List<SapAgreement> getListOfCurrency() {
        return listOfCurrency;
    }

    public void setListOfCurrency(List<SapAgreement> listOfCurrency) {
        this.listOfCurrency = listOfCurrency;
    }

    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    public BigDecimal getTotalMarketSaleReturn() {
        return totalMarketSaleReturn;
    }

    public void setTotalMarketSaleReturn(BigDecimal totalMarketSaleReturn) {
        this.totalMarketSaleReturn = totalMarketSaleReturn;
    }

    public BigDecimal getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(BigDecimal cashPayment) {
        this.cashPayment = cashPayment;
    }

    public BigDecimal getNonPosCollection() {
        return nonPosCollection;
    }

    public void setNonPosCollection(BigDecimal nonPosCollection) {
        this.nonPosCollection = nonPosCollection;
    }

    public BigDecimal getTotalCollection() {
        return totalCollection;
    }

    public void setTotalCollection(BigDecimal totalCollection) {
        this.totalCollection = totalCollection;
    }

    public BigDecimal getSaleCollection() {
        return saleCollection;
    }

    public void setSaleCollection(BigDecimal saleCollection) {
        this.saleCollection = saleCollection;
    }

    public BigDecimal getAutomationSaleDifference() {
        return automationSaleDifference;
    }

    public void setAutomationSaleDifference(BigDecimal automationSaleDifference) {
        this.automationSaleDifference = automationSaleDifference;
    }

    public List<SapAgreement> getListOfEndDay() {
        return listOfEndDay;
    }

    public void setListOfEndDay(List<SapAgreement> listOfEndDay) {
        this.listOfEndDay = listOfEndDay;
    }

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public BigDecimal getTransferAutomationSaleDifference() {
        return transferAutomationSaleDifference;
    }

    public void setTransferAutomationSaleDifference(BigDecimal transferAutomationSaleDifference) {
        this.transferAutomationSaleDifference = transferAutomationSaleDifference;
    }

    public boolean isIsView() {
        return isView;
    }

    public void setIsView(boolean isView) {
        this.isView = isView;
    }

    public BigDecimal getDifferenceMarket() {
        return differenceMarket;
    }

    public void setDifferenceMarket(BigDecimal differenceMarket) {
        this.differenceMarket = differenceMarket;
    }

    public BigDecimal getTransferMarketSaleDifference() {
        return transferMarketSaleDifference;
    }

    public void setTransferMarketSaleDifference(BigDecimal transferMarketSaleDifference) {
        this.transferMarketSaleDifference = transferMarketSaleDifference;
    }

    public boolean isIsThereInMarwiz() {
        return isThereInMarwiz;
    }

    public void setIsThereInMarwiz(boolean isThereInMarwiz) {
        this.isThereInMarwiz = isThereInMarwiz;
    }

    public List<SapAgreement> getListOfFuelAll() {
        return listOfFuelAll;
    }

    public void setListOfFuelAll(List<SapAgreement> listOfFuelAll) {
        this.listOfFuelAll = listOfFuelAll;
    }

    public List<SapAgreement> getListFuelTestSales() {
        return listFuelTestSales;
    }

    public void setListFuelTestSales(List<SapAgreement> listFuelTestSales) {
        this.listFuelTestSales = listFuelTestSales;
    }

    public BigDecimal getTestSalesQuantityTotal() {
        return testSalesQuantityTotal;
    }

    public void setTestSalesQuantityTotal(BigDecimal testSalesQuantityTotal) {
        this.testSalesQuantityTotal = testSalesQuantityTotal;
    }

    public BigDecimal getTestSalesTotalMoneyTotal() {
        return testSalesTotalMoneyTotal;
    }

    public void setTestSalesTotalMoneyTotal(BigDecimal testSalesTotalMoneyTotal) {
        this.testSalesTotalMoneyTotal = testSalesTotalMoneyTotal;
    }

    public Map<String, SapAgreement> getFuelStockTotalsCollection() {
        return fuelStockTotalsCollection;
    }

    public void setFuelStockTotalsCollection(Map<String, SapAgreement> fuelStockTotalsCollection) {
        this.fuelStockTotalsCollection = fuelStockTotalsCollection;
    }

    public BigDecimal getTotalMarketReturnWithSale() {
        return totalMarketReturnWithSale;
    }

    public void setTotalMarketReturnWithSale(BigDecimal totalMarketReturnWithSale) {
        this.totalMarketReturnWithSale = totalMarketReturnWithSale;
    }

    public boolean isIsCatchReturnWithSale() {
        return isCatchReturnWithSale;
    }

    public void setIsCatchReturnWithSale(boolean isCatchReturnWithSale) {
        this.isCatchReturnWithSale = isCatchReturnWithSale;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------SAP Agreement Process Bean------");
        listOfFuel = new ArrayList<>();
        listOfExpense = new ArrayList<>();
        listOfMarketZSeries = new ArrayList<>();
        listOfFuelOilZSeries = new ArrayList<>();
        listOfExchangeEntries = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        listOfPaymentTypes = new ArrayList<>();
        listOfPosSales = new ArrayList<>();
        selectedPosSales = new SapAgreement();
        listOfPosSalesDetail = new ArrayList<>();
        sapAgreement = new SapAgreement();
        listOfSafeTransfer = new ArrayList<>();
        listOfSendToBank = new ArrayList<>();
        bankPosSale = new HashMap<>();
        listOfBankGroupPosSales = new ArrayList<>();
        listOfCurrency = new ArrayList<>();
        listOfEndDay = new ArrayList<>();
        isThereInMarwiz = false;
        listOfFuelAll = new ArrayList<>();
        listFuelTestSales = new ArrayList<>();
        fuelStockTotalsCollection = new HashMap<>();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(calendar.getTime());
        setDate(calendar.getTime());

        isSend = false;
        isView = true;

        setListBtn(sessionBean.checkAuthority(new int[]{353}, 0));

    }

    public void find() {
        isFind = true;
        isSend = false;
        boolean negativeBalance = false;
        sapAgreement = new SapAgreement();
        listOfFuel = new ArrayList<>();
        listOfExpense = new ArrayList<>();
        listOfMarketZSeries = new ArrayList<>();
        listOfFuelOilZSeries = new ArrayList<>();
        listOfExchangeEntries = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        listOfPaymentTypes = new ArrayList<>();
        listOfPosSales = new ArrayList<>();
        selectedPosSales = new SapAgreement();
        listOfPosSalesDetail = new ArrayList<>();
        sapAgreement = new SapAgreement();
        listOfSafeTransfer = new ArrayList<>();
        listOfSendToBank = new ArrayList<>();
        bankPosSale = new HashMap<>();
        listOfBankGroupPosSales = new ArrayList<>();
        listOfCurrency = new ArrayList<>();
        listOfEndDay = new ArrayList<>();
        listOfFuelAll = new ArrayList<>();
        listFuelTestSales = new ArrayList<>();
        fuelStockTotalsCollection = new HashMap<>();

        totalMarketZSeries = BigDecimal.ZERO;
        totalExpense = BigDecimal.ZERO;
        totalPaymentTypes = BigDecimal.ZERO;
        totalMarketSales = BigDecimal.ZERO;
        totalFuelZSeriesQuantity = BigDecimal.ZERO;
        totalFuelZSeriesTotalMoney = BigDecimal.ZERO;
        totalExchangeQuantity = BigDecimal.ZERO;
        totalExchangeTotalMoney = BigDecimal.ZERO;
        totalFuelLiter = BigDecimal.ZERO;
        totalFuelTotalMoney = BigDecimal.ZERO;
        totalPosSalesMoney = BigDecimal.ZERO;
        totalMarketSaleReturn = BigDecimal.ZERO;
        cashPayment = BigDecimal.ZERO;
        nonPosCollection = BigDecimal.ZERO;
        totalCollection = BigDecimal.ZERO;
        saleCollection = BigDecimal.ZERO;
        automationSaleDifference = BigDecimal.ZERO;
        differenceFuel = BigDecimal.ZERO;
        differenceMarket = BigDecimal.ZERO;
        transferMarketSaleDifference = BigDecimal.ZERO;
        totalMarketReturnWithSale = BigDecimal.ZERO;
        int day = date.getDate() - 1;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.DAY_OF_MONTH, day);
        endDate = cal1.getTime();

        cal1.set(Calendar.DAY_OF_MONTH, 1);
        beginDate = cal1.getTime();

        sapAgreement = sapAgreementService.findall(beginDate, endDate, date);
        if (sapAgreement.getId() > 0) {
            isThereInMarwiz = true;
            if (sapAgreement.getIsSend()) {
                isSend = true;
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

        if (sapAgreement.getId() != 0) { //Veritabanında kayıtlı mutabakat verileri varsa
            listOfCurrency = sapAgreementService.listCurrency();
            isView = true;
            listOfFuel = sapAgreementService.listFuelData(sapAgreement);
            listFuelTestSales = sapAgreementService.listFuelTestData(sapAgreement);
            listOfPosSales = sapAgreementService.listPosSalesData(sapAgreement);
            listOfFuelOilZSeries = sapAgreementService.listFuelZSeriesData(sapAgreement);
            listOfMarketZSeries = sapAgreementService.listMarketZSeriesData(sapAgreement);
            listOfExpense = sapAgreementService.listExpenseData(sapAgreement);
            listOfExchangeEntries = sapAgreementService.listExchangeEntriesData(sapAgreement);
            listOfSafeTransfer = sapAgreementService.listSafeTransferData(sapAgreement);
            listOfSendToBank = sapAgreementService.listBankSendData(sapAgreement);
            listOfEndDay = sapAgreementService.listDailyEndData(sapAgreement);
            listOfPaymentTypes = sapAgreementService.listPaymentTypesData(sapAgreement);

            if (!listOfSendToBank.isEmpty()) {
                for (SapAgreement sap : listOfSendToBank) {
                    for (SapAgreement crr : listOfCurrency) {
                        if (crr.getCurrency().getId() == sap.getCurrency().getId()) {
                            sap.getCurrency().setInternationalCode(crr.getCurrency().getInternationalCode());
                        }
                    }
                }
            }

            if (!listOfPosSales.isEmpty()) {
                calculateBankPosSales();
            }

            calculateTotalJson();
            transferAutomationSaleDifference = sapAgreement.getTransferAutomationDiffAmount();
            transferMarketSaleDifference = sapAgreement.getTransferMarketDiffAmount();

            differenceFuel = totalFuelLiter.subtract(totalFuelZSeriesQuantity);
            if (!listOfFuelOilZSeries.isEmpty()) {
                if (listOfFuelOilZSeries.size() == 1) {
                    listOfFuelOilZSeries.get(0).setDifference(differenceFuel);
                    listOfFuelOilZSeries.get(0).setTotalFuelLiter(totalFuelLiter);
                } else if (listOfFuelOilZSeries.size() > 1) {
                    int avg = 0;
                    avg = listOfFuelOilZSeries.size() / 2;
                    listOfFuelOilZSeries.get(avg).setDifference(differenceFuel);
                    listOfFuelOilZSeries.get(avg).setTotalFuelLiter(totalFuelLiter);

                }
            }

            differenceMarket = totalMarketSales.add(totalMarketReturnWithSale).subtract(totalMarketZSeries);
            if (!listOfMarketZSeries.isEmpty()) {
                if (listOfMarketZSeries.size() == 1) {
                    listOfMarketZSeries.get(0).setDifference(differenceMarket);
                    listOfMarketZSeries.get(0).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));
                } else if (listOfMarketZSeries.size() > 1) {
                    int avg = 0;
                    avg = listOfMarketZSeries.size() / 2;
                    listOfMarketZSeries.get(avg).setDifference(differenceMarket);
                    listOfMarketZSeries.get(avg).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));

                }

            }

            if (isCatchReturnWithSale) {
                SapAgreement tempSapAgreement = new SapAgreement();
                tempSapAgreement = sapAgreementService.createJson(date, listOfFuel, listOfPosSales, listOfExpense, listOfExchangeEntries, listOfFuelOilZSeries, listOfMarketZSeries, listOfPaymentTypes,
                        totalFuelTotalMoney, totalMarketSales, cashPayment, totalExchangeTotalMoney, totalMarketSaleReturn, nonPosCollection, totalCollection, saleCollection,
                        automationSaleDifference, listOfSafeTransfer, listOfSendToBank, listOfEndDay, totalPaymentTypes, totalExpense, transferAutomationSaleDifference, totalFuelLiter,
                        totalPosSalesMoney, totalFuelZSeriesQuantity, totalFuelZSeriesTotalMoney, totalMarketZSeries, totalExchangeQuantity, differenceMarket, totalMarketReturnWithSale, testSalesQuantityTotal, testSalesTotalMoneyTotal, listFuelTestSales);

                sapAgreement.setPeriod(tempSapAgreement.getPeriod());
                sapAgreement.setProcessDate(tempSapAgreement.getProcessDate());
                sapAgreement.setAutomationDiffAmount(automationSaleDifference);
                sapAgreement.setAutomationDiffAmount(tempSapAgreement.getAutomationDiffAmount());
                sapAgreement.setAutomationJson(tempSapAgreement.getAutomationJson());
                sapAgreement.setPosSaleJson(tempSapAgreement.getPosSaleJson());
                sapAgreement.setBankTransferJson(tempSapAgreement.getBankTransferJson());
                sapAgreement.setExchangeJson(tempSapAgreement.getExchangeJson());
                sapAgreement.setExpenseJson(tempSapAgreement.getExpenseJson());
                sapAgreement.setFuelZJson(tempSapAgreement.getFuelZJson());
                sapAgreement.setMarketZJson(tempSapAgreement.getMarketZJson());
                sapAgreement.setTotalJson(tempSapAgreement.getTotalJson());
                sapAgreement.setSafeTransferJson(tempSapAgreement.getSafeTransferJson());
                sapAgreementService.insertOrUpdateLog(sapAgreement, automationSaleDifference, 1, differenceMarket);
            }

            for (SapAgreement sap : listOfEndDay) {
                if (sap.getTotalMoney() != null) {
                    if (sap.getTotalMoney().compareTo(BigDecimal.ZERO) < 0) {
                        negativeBalance = true;
                        break;
                    }
                }

            }

            if (negativeBalance) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("hasanegativeendofdaybalance")));
                RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");
            }

        } else { //Veritabanında kayıtlı mutabakat verileri yoksa webservisten alınır

            isThereInMarwiz = false;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDate = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            beginDate = cal.getTime();

            listOfCurrency = sapAgreementService.listCurrency();
            listOfSendToBank.addAll(listOfCurrency);
            for (SapAgreement sap : listOfCurrency) {
                SapAgreement sapCrr = new SapAgreement();
                sapCrr.getCurrency().setCode(sap.getCurrency().getCode());
                sapCrr.getCurrency().setId(sap.getCurrency().getId());
                sapCrr.setTotalMoney(sap.getTotalMoney());
                listOfSafeTransfer.add(sapCrr);
            }
            findPaymentType();
            sapAgreement = sapAgreementService.getDataSap(date);

            if (!sapAgreement.isIsSuccess()) {
                if ((sapAgreement.getMessageType() != null && sapAgreement.getMessageType().equalsIgnoreCase("E")) || sapAgreement.getMessageType() == null) {
                    isView = false;
                } else {
                    isView = true;

                }

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sapAgreement.getMessage()));
                RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");
            } else {
                isView = true;
                if (sapAgreement.getMessageType().equalsIgnoreCase("S")) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sapAgreement.getMessage()));
                    RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");
                }

            }

            listOfFuelAll = sapAgreementService.listOfFuel(sapAgreement);
            listOfFuel.addAll(listOfFuelAll);
            for (SapAgreement fuel : listOfFuelAll) {

                if (fuel.isIsTestSale()) {
                    listFuelTestSales.add(fuel);
                }
            }

            fuelStockTotalsCollection = groupByProductListOfFuel();

            listOfFuel = listOfFuelGroupByStock();

            listOfPaymentTypes = sapAgreementService.listOfPaymentTypes(sapAgreement, listOfPaymentTypes);
            listOfPosSales = sapAgreementService.listOfPosSales(sapAgreement);
            listOfSafeTransfer = sapAgreementService.listOfSafeTransfer(sapAgreement, listOfSafeTransfer);
            listOfExpense = sapAgreementService.listOfExpense();

            findZSeries();

            if (!listOfFuel.isEmpty()) {
                calculateTotals(listOfFuel, 1);
            }
            if (!listOfPosSales.isEmpty()) {
                calculateTotals(listOfPosSales, 3);
                calculateBankPosSales();
            }

            if (!listOfPaymentTypes.isEmpty()) {
                calculateTotals(listOfPaymentTypes, 5);
            }
            if (!listFuelTestSales.isEmpty()) {
                calculateTotals(listFuelTestSales, 6);
            }

            differenceFuel = totalFuelLiter.subtract(totalFuelZSeriesQuantity);
            if (!listOfFuelOilZSeries.isEmpty()) {
                if (listOfFuelOilZSeries.size() == 1) {
                    listOfFuelOilZSeries.get(0).setDifference(differenceFuel);
                    listOfFuelOilZSeries.get(0).setTotalFuelLiter(totalFuelLiter);

                } else if (listOfFuelOilZSeries.size() > 1) {
                    int avg = 0;
                    avg = listOfFuelOilZSeries.size() / 2;
                    listOfFuelOilZSeries.get(avg).setDifference(differenceFuel);
                    listOfFuelOilZSeries.get(avg).setTotalFuelLiter(totalFuelLiter);

                }

            }
            List<SapAgreement> marketTotalSalesList = new ArrayList<>();
            listOfExchangeEntries = sapAgreementService.findAllExchange(beginDate, endDate);
            totalMarketSales = sapAgreementService.findMarketSalesTotal(beginDate, endDate);
            marketTotalSalesList = sapAgreementService.findMarketSaleReturnTotal(sapAgreement, beginDate, endDate);
            if (!marketTotalSalesList.isEmpty()) {
                sapAgreement.setReturnSalesTotal(marketTotalSalesList.get(0).getReturnSalesTotal());
                sapAgreement.setReturnsWithSale(marketTotalSalesList.get(0).getReturnsWithSale());
            }
            totalMarketSaleReturn = sapAgreement.getReturnSalesTotal();
            totalMarketReturnWithSale = sapAgreement.getReturnsWithSale();
//            totalMarketSaleReturn = sapAgreementService.findMarketSaleReturnTotal(sapAgreement, beginDate, endDate);
            cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
            nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
            totalCollection = nonPosCollection.add(totalPosSalesMoney);
            saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
            automationSaleDifference = totalFuelTotalMoney.subtract(totalFuelZSeriesTotalMoney);
            endDay();

            differenceMarket = totalMarketSales.add(totalMarketReturnWithSale).subtract(totalMarketZSeries);
            if (!listOfMarketZSeries.isEmpty()) {
                if (listOfMarketZSeries.size() == 1) {
                    listOfMarketZSeries.get(0).setDifference(differenceMarket);
                    listOfMarketZSeries.get(0).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));
                } else if (listOfMarketZSeries.size() > 1) {
                    int avg = 0;
                    avg = listOfMarketZSeries.size() / 2;
                    listOfMarketZSeries.get(avg).setDifference(differenceMarket);
                    listOfMarketZSeries.get(avg).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));

                }

            }

            int day1 = date.getDate() - 1;

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date);
            cal2.set(Calendar.DAY_OF_MONTH, day);
            endDate = cal2.getTime();

            cal2.set(Calendar.DAY_OF_MONTH, 1);
            beginDate = cal2.getTime();
            List<SapAgreement> transferSaleDiff = new ArrayList<>();
            transferSaleDiff = sapAgreementService.calculateTransferSaleDiffAmount(beginDate, endDate);

            if (!transferSaleDiff.isEmpty()) {

                transferAutomationSaleDifference = transferSaleDiff.get(0).getTransferAutomationDiffAmount() != null ? transferSaleDiff.get(0).getTransferAutomationDiffAmount() : BigDecimal.ZERO;
                transferMarketSaleDifference = transferSaleDiff.get(0).getTransferMarketDiffAmount() != null ? transferSaleDiff.get(0).getTransferMarketDiffAmount() : BigDecimal.ZERO;

            }

        }
        RequestContext.getCurrentInstance().update("pgrSapAgreementProcess");

    }

    public Map<String, SapAgreement> groupByProductListOfFuel() {

        if (!fuelStockTotalsCollection.isEmpty()) {
            fuelStockTotalsCollection.clear();
        }
        for (SapAgreement total : listOfFuelAll) {

            if (fuelStockTotalsCollection.containsKey(total.getStock().getCode())) {

                SapAgreement old = new SapAgreement();
                old.getStock().setCode(fuelStockTotalsCollection.get(total.getStock().getCode()).getStock().getCode());

                old.setTotalMoney(fuelStockTotalsCollection.get(total.getStock().getCode()).getTotalMoney());
                old.setQuantity(fuelStockTotalsCollection.get(total.getStock().getCode()).getQuantity());
                old.setUnitPrice(fuelStockTotalsCollection.get(total.getStock().getCode()).getUnitPrice());
                old.getStock().setName(fuelStockTotalsCollection.get(total.getStock().getCode()).getStock().getName());

                old.setTotalMoney(old.getTotalMoney().add(total.getTotalMoney()));
                old.setQuantity(old.getQuantity().add(total.getQuantity()));

                fuelStockTotalsCollection.put(total.getStock().getCode(), old);

            } else {

                SapAgreement oldNew = new SapAgreement();
                oldNew.getStock().setCode(total.getStock().getCode());
                oldNew.setTotalMoney(total.getTotalMoney());
                oldNew.setQuantity(total.getQuantity());
                oldNew.getStock().setName(total.getStock().getName());
                oldNew.setUnitPrice(total.getUnitPrice());

                fuelStockTotalsCollection.put(total.getStock().getCode(), oldNew);
            }

        }

        return fuelStockTotalsCollection;

    }

    public List<SapAgreement> listOfFuelGroupByStock() {
        listOfFuel.clear();
        if (!fuelStockTotalsCollection.isEmpty()) {
            for (Map.Entry<String, SapAgreement> entry : fuelStockTotalsCollection.entrySet()) {
                SapAgreement sap = new SapAgreement();
                sap.getStock().setName(entry.getValue().getStock().getName());
                sap.getStock().setCode(entry.getKey());
                sap.setUnitPrice(entry.getValue().getUnitPrice());
                sap.setTotalMoney(entry.getValue().getTotalMoney());
                sap.setQuantity(entry.getValue().getQuantity());
                listOfFuel.add(sap);
            }
        }
        return listOfFuel;
    }

    public void calculateTotalJson() {

        JSONObject jsonTotal = new JSONObject(sapAgreement.getTotalJson());

        if (!jsonTotal.toString().isEmpty() && jsonTotal.toString() != null) {

            totalFuelTotalMoney = jsonTotal.getBigDecimal("AUTOMATIONSALESTOTAL");
            totalMarketSales = jsonTotal.getBigDecimal("MARKETSALESTOTAL");
            cashPayment = jsonTotal.getBigDecimal("CASHPAYMENT");
            totalExchangeTotalMoney = jsonTotal.getBigDecimal("EXCHANGETOTAL");
            totalMarketSaleReturn = jsonTotal.getBigDecimal("MARKETSALERETURNTOTAL");//tüm iadeler
            totalCollection = jsonTotal.getBigDecimal("SALECOLLECTION");
            nonPosCollection = jsonTotal.getBigDecimal("NONPOSCOLLECTION");
            automationSaleDifference = jsonTotal.getBigDecimal("AUTOMATIONSALEDIFFERENCE");
            totalCollection = jsonTotal.getBigDecimal("TOTALCOLLECTION");
            totalPaymentTypes = jsonTotal.getBigDecimal("TOTALPAYMENTTYPES");
            totalExpense = jsonTotal.getBigDecimal("TOTALEXPENSE");
            transferAutomationSaleDifference = jsonTotal.getBigDecimal("TRANSFERAUTOMATIONDIFFAMOUNT");
            totalFuelLiter = jsonTotal.getBigDecimal("TOTALFUELLITER");
            totalPosSalesMoney = jsonTotal.getBigDecimal("TOTALPOSSALESMONEY");
            totalFuelZSeriesQuantity = jsonTotal.getBigDecimal("TOTALFUELZSERIESQUANTITY");
            totalFuelZSeriesTotalMoney = jsonTotal.getBigDecimal("TOTALFUELZSERIESTOTALMONEY");
            totalMarketZSeries = jsonTotal.getBigDecimal("TOTALMARKETZSERIES");
            totalExchangeQuantity = jsonTotal.getBigDecimal("TOTALEXCHANGEQUANTITY");
            totalExchangeTotalMoney = jsonTotal.getBigDecimal("TOTALEXCHANGETOTALMONEY");
            differenceMarket = jsonTotal.getBigDecimal("MARKETSALEDIFFERENCE");//marwiz ve market z farkı	
            try {
                totalMarketReturnWithSale = jsonTotal.getBigDecimal("TOTALMARKETRETURNWİTHSALE");
                isCatchReturnWithSale = false;
            } catch (Exception e) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date);
                cal1.set(Calendar.HOUR_OF_DAY, 00);
                cal1.set(Calendar.MINUTE, 00);
                cal1.set(Calendar.SECOND, 00);
                isCatchReturnWithSale = true;
                List<SapAgreement> marketTotalSalesList = new ArrayList<>();
                totalMarketSales = sapAgreementService.findMarketSalesTotal(cal1.getTime(), cal.getTime());
                marketTotalSalesList = sapAgreementService.findMarketSaleReturnTotal(sapAgreement, cal1.getTime(), cal.getTime());
                if (!marketTotalSalesList.isEmpty()) {
                    sapAgreement.setReturnSalesTotal(marketTotalSalesList.get(0).getReturnSalesTotal());
                    sapAgreement.setReturnsWithSale(marketTotalSalesList.get(0).getReturnsWithSale());
                }
                totalMarketSaleReturn = sapAgreement.getReturnSalesTotal();
                totalMarketReturnWithSale = sapAgreement.getReturnsWithSale();
                differenceMarket = totalMarketSales.add(totalMarketReturnWithSale).subtract(totalMarketZSeries);

            }

            try {
                testSalesQuantityTotal = jsonTotal.getBigDecimal("TESTSALESQUANTITYTOTAL");
                testSalesTotalMoneyTotal = jsonTotal.getBigDecimal("TESTSALESTOTALMONEYTOTAL");
            } catch (Exception e) {
            }

        }
    }

    public void findZSeries() {

        List<ZSeries> listZSeries = new ArrayList<>();
        listZSeries = zSeriesService.listofZseries(sessionBean.getUser().getLastBranch().getId());

        for (ZSeries zSeries : listZSeries) {
            SapAgreement obj = new SapAgreement();
            obj.setzSeries(zSeries);
            if (zSeries.getType() == 1) {
                listOfFuelOilZSeries.add(obj);
            } else if (zSeries.getType() == 2) {
                listOfMarketZSeries.add(obj);
            }
        }

    }

    public void findPaymentType() {

        List<PaymentType> paymentTypeList = new ArrayList<>();
        paymentTypeList = paymentTypeService.listofPayment(sessionBean.getUser().getLastBranch().getId());

        for (PaymentType paymentType : paymentTypeList) {
            SapAgreement obj = new SapAgreement();
            obj.setPaymentType(paymentType);
            listOfPaymentTypes.add(obj);
        }

    }

    public void goToShiftDetail() {
        listOfPosSalesDetail.clear();

        for (SapAgreement sap : listOfPosSales) {
            if (sap.getBankName().equalsIgnoreCase(selectedPosSales.getBankName())) {
                listOfPosSalesDetail.add(sap);
            }
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgPosSalesDetail");
        context.execute("PF('dlg_possalesdetail').show();");

    }

    public void calculateTotals(List<SapAgreement> list, int type) { //Alt toplamları hesaplar
        if (type == 1) { //Akarkayıt
            totalFuelLiter = BigDecimal.ZERO;
            totalFuelTotalMoney = BigDecimal.ZERO;

            for (SapAgreement sap : list) {
                totalFuelLiter = totalFuelLiter.add(sap.getQuantity());
                totalFuelTotalMoney = totalFuelTotalMoney.add(sap.getTotalMoney());

            }
            if (listOfFuelOilZSeries.size() == 1) {
                listOfFuelOilZSeries.get(0).setDifference(differenceFuel);
                listOfFuelOilZSeries.get(0).setTotalFuelLiter(totalFuelLiter);
            } else if (listOfFuelOilZSeries.size() > 1) {
                int avg = 0;
                avg = listOfFuelOilZSeries.size() / 2;
                listOfFuelOilZSeries.get(avg).setDifference(differenceFuel);
                listOfFuelOilZSeries.get(avg).setTotalFuelLiter(totalFuelLiter);
            }

        } else if (type == 2) { //Masraf
            totalExpense = BigDecimal.ZERO;
            for (SapAgreement sap : list) {
                if (sap.getTotalMoney() != null) {
                    totalExpense = totalExpense.add(sap.getTotalMoney());
                }
            }

        } else if (type == 3) { //Pos Satışları
            totalPosSalesMoney = BigDecimal.ZERO;
            for (SapAgreement sap : list) {
                if (sap.getPosDailyEnd() != null) {
                    totalPosSalesMoney = totalPosSalesMoney.add(sap.getPosDailyEnd());
                }
            }

        } else if (type == 5) { //Ödeme Tipleri
            totalPaymentTypes = BigDecimal.ZERO;
            for (SapAgreement sap : list) {
                if (sap.getTotalMoney() != null) {
                    totalPaymentTypes = totalPaymentTypes.add(sap.getTotalMoney());
                }
            }

        } else if (type == 6) {

            testSalesQuantityTotal = BigDecimal.ZERO;
            testSalesTotalMoneyTotal = BigDecimal.ZERO;

            for (SapAgreement sap : list) {
                if (sap.getTotalMoney() != null) {
                    testSalesTotalMoneyTotal = testSalesTotalMoneyTotal.add(sap.getTotalMoney());
                }
                if (sap.getQuantity() != null) {
                    testSalesQuantityTotal = testSalesQuantityTotal.add(sap.getQuantity());
                }
            }

        }
    }

    public void calculateBankPosSales() {

        for (SapAgreement pos : listOfPosSales) {

            if (pos.getPosDailyEnd() != null) {
                if (bankPosSale.containsKey(pos.getBankName())) {

                    SapAgreement old = new SapAgreement();
                    old.setBankName(bankPosSale.get(pos.getBankName()).getBankName());

                    old.setPosDailyEnd(bankPosSale.get(pos.getBankName()).getPosDailyEnd());

                    old.setPosDailyEnd(old.getPosDailyEnd().add(pos.getPosDailyEnd()));

                    bankPosSale.put(pos.getBankName(), old);

                } else {

                    SapAgreement oldNew = new SapAgreement();
                    oldNew.setBankName(pos.getBankName());
                    oldNew.setPosDailyEnd(pos.getPosDailyEnd());

                    bankPosSale.put(pos.getBankName(), oldNew);
                }

            }

        }
        listOfBankGroupPosSales.clear();

        for (Map.Entry<String, SapAgreement> entry : bankPosSale.entrySet()) {

            SapAgreement sapPos = new SapAgreement();
            sapPos.setBankName(entry.getKey());
            sapPos.setPosDailyEnd(entry.getValue().getPosDailyEnd());
            listOfBankGroupPosSales.add(sapPos);
        }
    }

    public void cellEditExpense(SapAgreement sapExpense) {//Masraf celledit
        totalExpense = BigDecimal.ZERO;

        for (SapAgreement sap : listOfExpense) {
            if (sap.getId() == sapExpense.getId()) {
                sap.setTotalMoney(sapExpense.getTotalMoney());

                break;
            }
        }
        for (SapAgreement sap : listOfExpense) {
            if (sap.getTotalMoney() != null) {
                totalExpense = totalExpense.add(sap.getTotalMoney());
            }

        }
        cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
        nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
        totalCollection = nonPosCollection.add(totalPosSalesMoney);
        saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
        endDay();

    }

    public void cellEditExchange(SapAgreement sapExchange) { //Döviz
        totalExchangeQuantity = BigDecimal.ZERO;
        totalExchangeTotalMoney = BigDecimal.ZERO;

//        for (SapAgreement sap : listOfExchangeEntries) {
//            if (sap.getExchange().getCurrency().getId() == sapExchange.getExchange().getCurrency().getId()) {
//                if (sapExchange.getExchange().getBuying() != null && sapExchange.getQuantity() != null) {
//
//                    sap.setTotalMoney(sapExchange.getExchange().getBuying().multiply(sapExchange.getQuantity()));
//
//                }
//
//            }
//
//        }
        for (SapAgreement sap : listOfExchangeEntries) {

            if (sap.getExchange().getCurrency().getId() == sapExchange.getExchange().getCurrency().getId()) {
                if (sapExchange.getTotalMoney() != null) {

                    sap.setTotalMoney(sapExchange.getTotalMoney());
                }
                if (sapExchange.getQuantity() != null) {

                    sap.setQuantity(sapExchange.getQuantity());
                }
                if (sapExchange.getExchange().getBuying() != null) {

                    sap.getExchange().setBuying(sapExchange.getExchange().getBuying());
                }
                break;
            }
        }
        for (SapAgreement sap : listOfExchangeEntries) {
            if (sap.getTotalMoney() != null) {
                totalExchangeTotalMoney = totalExchangeTotalMoney.add(sap.getTotalMoney());
            }
            if (sap.getQuantity() != null) {
                totalExchangeQuantity = totalExchangeQuantity.add(sap.getQuantity());
            }

        }

        cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
        nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
        totalCollection = nonPosCollection.add(totalPosSalesMoney);
        saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
        endDay();
    }

    public void cellEditMarketZSeries(SapAgreement sapMarketZ) {//MarketZ
        totalMarketZSeries = BigDecimal.ZERO;

        for (SapAgreement sap : listOfMarketZSeries) {
            if (sap.getzSeries().getId() == sapMarketZ.getzSeries().getId()) {
                sap.setTotalMoney(sapMarketZ.getTotalMoney());
                break;
            }
        }
        for (SapAgreement sap : listOfMarketZSeries) {
            if (sap.getTotalMoney() != null) {
                totalMarketZSeries = totalMarketZSeries.add(sap.getTotalMoney());
            }

        }

        cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
        nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
        totalCollection = nonPosCollection.add(totalPosSalesMoney);
        saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
        endDay();

        differenceMarket = totalMarketSales.add(totalMarketReturnWithSale).subtract(totalMarketZSeries);
        if (!listOfMarketZSeries.isEmpty()) {
            if (listOfMarketZSeries.size() == 1) {
                listOfMarketZSeries.get(0).setDifference(differenceMarket);
                listOfMarketZSeries.get(0).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));
            } else if (listOfMarketZSeries.size() > 1) {
                int avg = 0;
                avg = listOfMarketZSeries.size() / 2;
                listOfMarketZSeries.get(avg).setDifference(differenceMarket);
                listOfMarketZSeries.get(avg).setTotalMarket(totalMarketSales.add(totalMarketReturnWithSale));

            }

        }

    }

    public void cellEditBankSend(SapAgreement sapBankSend) {//Bankaya gönderilen
        totalBankSend = BigDecimal.ZERO;

        for (SapAgreement sap : listOfSendToBank) {

            if (sap.getCurrency().getId() == sapBankSend.getCurrency().getId()) {
                sap.setTotalMoney(sapBankSend.getTotalMoney());
                break;
            }
        }
        for (SapAgreement sap : listOfSendToBank) {
            if (sap.getTotalMoney() != null) {
                totalBankSend = totalBankSend.add(sap.getTotalMoney());
            }

        }
        endDay();
    }

    public void cellEditFuelZSeries(SapAgreement sapFuelZ) {

        totalFuelZSeriesQuantity = BigDecimal.ZERO;
        totalFuelZSeriesTotalMoney = BigDecimal.ZERO;

        for (SapAgreement sap : listOfFuelOilZSeries) {
            if (sap.getzSeries().getId() == sapFuelZ.getzSeries().getId()) {
                if (sapFuelZ.getTotalMoney() != null) {
                    sap.setTotalMoney(sapFuelZ.getTotalMoney());
                }
                if (sapFuelZ.getQuantity() != null) {
                    sap.setQuantity(sapFuelZ.getQuantity());
                }

                break;
            }
        }
        for (SapAgreement sap : listOfFuelOilZSeries) {
            if (sap.getTotalMoney() != null) {
                totalFuelZSeriesTotalMoney = totalFuelZSeriesTotalMoney.add(sap.getTotalMoney());
            }
            if (sap.getQuantity() != null) {
                totalFuelZSeriesQuantity = totalFuelZSeriesQuantity.add(sap.getQuantity());
            }

        }
        differenceFuel = totalFuelLiter.subtract(totalFuelZSeriesQuantity);
        cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
        nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
        totalCollection = nonPosCollection.add(totalPosSalesMoney);
        saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
        automationSaleDifference = totalFuelTotalMoney.subtract(totalFuelZSeriesTotalMoney);
        if (listOfFuelOilZSeries.size() == 1) {
            listOfFuelOilZSeries.get(0).setDifference(differenceFuel);
        } else if (listOfFuelOilZSeries.size() > 1) {
            int avg = 0;
            avg = listOfFuelOilZSeries.size() / 2;
            listOfFuelOilZSeries.get(avg).setDifference(differenceFuel);
        }

        endDay();

        if (differenceFuel.compareTo(BigDecimal.ZERO) < 0) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thedifferenceamountoffuelzreportshasdecreasedtominuspleasecheckyourdata")));
            RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");
        }

    }

    public void cellEditPaymentTypes(SapAgreement sapPaymentType) {//Ödeme Tipleri

        totalPaymentTypes = BigDecimal.ZERO;

        for (SapAgreement sap : listOfPaymentTypes) {
            if (sap.getPaymentType().getId() == sapPaymentType.getPaymentType().getId()) {
                sap.setTotalMoney(sapPaymentType.getTotalMoney());
                break;
            }
        }
        for (SapAgreement sap : listOfPaymentTypes) {
            if (sap.getTotalMoney() != null) {
                totalPaymentTypes = totalPaymentTypes.add(sap.getTotalMoney());
            }

        }
        cashPayment = (totalMarketZSeries.add(totalFuelZSeriesTotalMoney)).subtract(totalExchangeTotalMoney.add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense)).subtract(totalPosSalesMoney);
        nonPosCollection = cashPayment.add(totalExchangeTotalMoney).add(totalMarketSaleReturn).add(totalPaymentTypes).add(totalExpense);
        totalCollection = nonPosCollection.add(totalPosSalesMoney);
        saleCollection = totalMarketZSeries.add(totalFuelZSeriesTotalMoney).subtract(totalCollection);
        endDay();

    }

    public void endDay() {
        boolean negativeBalance = false;
        listOfEndDay.clear();
        for (SapAgreement sap : listOfSafeTransfer) {
            BigDecimal endDayTotal = BigDecimal.ZERO;
            SapAgreement endDayObject = new SapAgreement();
            endDayObject.setCurrency(sap.getCurrency());
            if (sap.getTotalMoney() != null) {
                endDayTotal = endDayTotal.add(sap.getTotalMoney());
            }

            listOfEndDay.add(endDayObject);

            if (endDayObject.getCurrency().getCode().equalsIgnoreCase("TL")) {
                if (sap.getTotalMoney() != null) {
                    endDayTotal = cashPayment.add(sap.getTotalMoney());

                }
            } else {

                for (SapAgreement sapExchange : listOfExchangeEntries) {
                    if (sapExchange.getExchange().getCurrency().getId() == sap.getCurrency().getId()) {
                        if (sap.getTotalMoney() != null && sapExchange.getQuantity() != null) {
                            endDayTotal = sap.getTotalMoney().add(sapExchange.getQuantity());
                        } else if (sap.getTotalMoney() == null && sapExchange.getQuantity() != null) {

                            endDayTotal = sapExchange.getQuantity();
                        } else if (sapExchange.getQuantity() == null && sap.getTotalMoney() != null) {

                            endDayTotal = sap.getTotalMoney();

                        }
                        break;
                    }
                }
            }

            for (SapAgreement sapBankSend : listOfSendToBank) {

                if (sap.getCurrency().getId() == sapBankSend.getCurrency().getId()) {
                    if (sapBankSend.getTotalMoney() != null) {

                        endDayTotal = endDayTotal.subtract(sapBankSend.getTotalMoney());
                    }

                    break;
                }
            }
            endDayObject.setEndDayTotal(endDayTotal);

            boolean isThere = false;
            for (SapAgreement sapEndDay : listOfEndDay) {

                if (sapEndDay.getCurrency().getId() == endDayObject.getCurrency().getId()) {
                    sapEndDay.setEndDayTotal(endDayObject.getEndDayTotal());
                    isThere = true;
                    break;
                }

            }
            if (!isThere) {
                listOfEndDay.add(endDayObject);
            }

        }

        if (listOfEndDay != null) {
            for (SapAgreement sap : listOfEndDay) {
                if (sap.getEndDayTotal().compareTo(BigDecimal.ZERO) < 0) {
                    negativeBalance = true;
                    break;
                }
            }
        }

        if (negativeBalance) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("hasanegativeendofdaybalance")));
            RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");
        }

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        RequestContext.getCurrentInstance().update("dlgConfirmDayEnd");
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmDayEnd').show()");

    }

    @Override
    public List<SapAgreement> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendIntegration() {//Mutabakat ekranı verilerini web servise gönderir
        SapAgreement sap = new SapAgreement();
        sap = sapAgreementService.sendIntegration(listOfExchangeEntries, listOfExpense, listOfFuelOilZSeries, listOfMarketZSeries, listOfSendToBank, totalFuelLiter, differenceFuel, listOfPaymentTypes,
                date, listOfFuel, listOfPosSales, totalFuelTotalMoney, totalMarketSales, cashPayment, totalExchangeTotalMoney,
                totalMarketSaleReturn, nonPosCollection, totalCollection, saleCollection, automationSaleDifference, listOfSafeTransfer, listOfEndDay, totalPaymentTypes, totalExpense, transferAutomationSaleDifference,
                totalPosSalesMoney, totalFuelZSeriesQuantity, totalFuelZSeriesTotalMoney, totalMarketZSeries,
                totalExchangeQuantity, differenceMarket, totalMarketReturnWithSale, testSalesQuantityTotal, testSalesTotalMoneyTotal, listFuelTestSales);

        if (sap.getIsSend()) {
            isSend = true;
            RequestContext.getCurrentInstance().update("pgrSapAgreementProcess");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation") + " " + sap.getMessage()));
            RequestContext.getCurrentInstance().update("grwSapAgreementProcessMessage");

        }
        sapAgreementService.insertOrUpdateLog(sap, automationSaleDifference, 2, differenceMarket);

    }

    public void deleteConfirm() {

        RequestContext.getCurrentInstance().update("dlgConfirm");
        RequestContext.getCurrentInstance().execute("PF('dlg_Confirm').show()");
    }

    public void delete() {

        int result = 0;
        result = sapAgreementService.delete(sapAgreement);
        if (result > 0) {
            sapAgreement = new SapAgreement();
            find();
            RequestContext.getCurrentInstance().update("pgrSapAgreementProcess");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void goToFuelTestSalesDetail() {

        RequestContext.getCurrentInstance().update("dlgFuelTestSalesDetail");
        RequestContext.getCurrentInstance().execute("PF('dlg_fueltestsalesdetail').show()");

    }

    public void dayEndYes() {

        int result = sapAgreementService.save(date, listOfFuel, listOfPosSales, listOfExpense, listOfExchangeEntries,
                listOfFuelOilZSeries, listOfMarketZSeries, listOfPaymentTypes,
                totalFuelTotalMoney, totalMarketSales, cashPayment, totalExchangeTotalMoney,
                totalMarketSaleReturn, nonPosCollection, totalCollection, saleCollection,
                automationSaleDifference, listOfSafeTransfer, listOfSendToBank, listOfEndDay, totalPaymentTypes, totalExpense, transferAutomationSaleDifference,
                totalFuelLiter, totalPosSalesMoney, totalFuelZSeriesQuantity, totalFuelZSeriesTotalMoney, totalMarketZSeries,
                totalExchangeQuantity, differenceMarket, totalMarketReturnWithSale, testSalesQuantityTotal, testSalesTotalMoneyTotal, listFuelTestSales);

        if (result > 0) {
            isThereInMarwiz = true;
            RequestContext.getCurrentInstance().update("pgrSapAgreementProcess");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void canceltransfer() {
        RequestContext.getCurrentInstance().update("dlgConfirmCancel");
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmCancel').show()");

    }

    public void cancelTransferYes() {

        int result = sapAgreementService.update(sapAgreement);

        if (result > 0) {
            sapAgreement.setIsSend(false);
            isSend = false;
            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmCancel').hide()");
            RequestContext.getCurrentInstance().update("pgrSapAgreementProcess");

        }
        sessionBean.createUpdateMessage(result);

    }

}
