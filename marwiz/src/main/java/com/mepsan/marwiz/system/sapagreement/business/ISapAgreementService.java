package com.mepsan.marwiz.system.sapagreement.business;

import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.system.sapagreement.dao.SapAgreement;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author elif.mart
 */
public interface ISapAgreementService {

    public SapAgreement getDataSap(Date date);

    public List<SapAgreement> listOfFuel(SapAgreement sap);

    public List<SapAgreement> listOfPaymentTypes(SapAgreement sap, List<SapAgreement> listOfPaymentTypes);

    public List<SapAgreement> listOfPosSales(SapAgreement sap);

    public List<SapAgreement> listOfSafeTransfer(SapAgreement sap, List<SapAgreement> listOfSafeTransfer);

    public List<SapAgreement> listOfExpense();

    public List<SapAgreement> listCurrency();

    public List<SapAgreement> findAllExchange(Date beginDate, Date endDate);

    public SapAgreement sendIntegration(List<SapAgreement> listOfExchangeEntries, List<SapAgreement> listOfExpense, List<SapAgreement> listOfFuelOilZSeries, List<SapAgreement> listOfMarketZSeries, List<SapAgreement> listOfSendToBank, BigDecimal totalFuelLiter, BigDecimal difference, List<SapAgreement> listOfPaymentTypes,
            Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes, BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference,
            BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales);

    public BigDecimal findMarketSalesTotal(Date beginDate, Date endDate);

    public List<SapAgreement> findMarketSaleReturnTotal(SapAgreement sapAgreement, Date beginDate, Date endDate);

    public int save(Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales, List<SapAgreement> listOfExpense, List<SapAgreement> listOfExchangeEntries,
            List<SapAgreement> listOfFuelZ, List<SapAgreement> ListOfMarketZ, List<SapAgreement> listOfPaymentType,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfBankSend, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes, BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference,
            BigDecimal totalFuelLiter, BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales);

    public int insertOrUpdateLog(SapAgreement sap, BigDecimal automationSaleDifference, int type, BigDecimal marketSaleDifference);

    public SapAgreement findall(Date beginDate, Date endDate, Date date);

    public List<SapAgreement> listFuelData(SapAgreement obj);

    public List<SapAgreement> listPosSalesData(SapAgreement obj);

    public List<SapAgreement> listFuelZSeriesData(SapAgreement obj);

    public List<SapAgreement> listMarketZSeriesData(SapAgreement obj);

    public List<SapAgreement> listExpenseData(SapAgreement obj);

    public List<SapAgreement> listExchangeEntriesData(SapAgreement obj);

    public List<SapAgreement> listSafeTransferData(SapAgreement obj);

    public List<SapAgreement> listBankSendData(SapAgreement obj);

    public List<SapAgreement> listDailyEndData(SapAgreement obj);

    public List<SapAgreement> listPaymentTypesData(SapAgreement obj);

    public List<SapAgreement> calculateTransferSaleDiffAmount(Date beginDate, Date endDate);

    public List<SapAgreement> listFuelTestData(SapAgreement obj);

    public int delete(SapAgreement obj);

    public SapAgreement createJson(Date date, List<SapAgreement> listOfFuel, List<SapAgreement> listOfPosSales, List<SapAgreement> listOfExpense, List<SapAgreement> listOfExchangeEntries,
            List<SapAgreement> listOfFuelZ, List<SapAgreement> ListOfMarketZ, List<SapAgreement> listOfPaymentType,
            BigDecimal totalFuelTotalMoney, BigDecimal totalMarketSales, BigDecimal cashPayment, BigDecimal totalExchangeTotalMoney,
            BigDecimal totalMarketSaleReturn, BigDecimal nonPosCollection, BigDecimal totalCollection, BigDecimal saleCollection,
            BigDecimal automationSaleDifference,
            List<SapAgreement> listOfSafeTransfer, List<SapAgreement> listOfBankSend, List<SapAgreement> listOfEndDay, BigDecimal totalPaymentTypes,
            BigDecimal totalExpense, BigDecimal transferAutomationSaleDifference, BigDecimal totalFuelLiter, BigDecimal totalPosSalesMoney, BigDecimal totalFuelZSeriesQuantity, BigDecimal totalFuelZSeriesTotalMoney, BigDecimal totalMarketZSeries,
            BigDecimal totalExchangeQuantity, BigDecimal differenceMarket, BigDecimal totalMarketReturnWithSale, BigDecimal testSalesQuantityTotal, BigDecimal testSalesTotalMoneyTotal, List<SapAgreement> listFuelTestSales);

    public int update(SapAgreement obj);

//    public List<SapAgreement> findPaymentType(Date beginDate, Date endDate);

}
