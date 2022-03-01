/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:26:17 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.dao.StationSalesSummaryReport;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IStationSalesSummaryReportService {

    public List<StationSalesSummaryReport> findFuelSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findFuelCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public String createWhere(List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findFuelSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public List<StationSalesSummaryReport> findMarketSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList);

    public void createExcel(Date beginDate, Date endDate,
            List<StationSalesSummaryReport> listOfFuelSales, List<StationSalesSummaryReport> listOfFuelCollection,
            List<StationSalesSummaryReport> listOfMarketSales, List<StationSalesSummaryReport> listOfMarketCollection,
            List<StationSalesSummaryReport> listOfTotalSales, List<StationSalesSummaryReport> listOfTotalCollection, BigDecimal fuelSaleTotalLiter, BigDecimal fuelSaleTotalMoney, BigDecimal fuelCollectionTotalMoney,
            BigDecimal marketSalesTotalPrice, BigDecimal marketCollectionTotalPrices, BigDecimal generalSalesTotal, BigDecimal generalCollectionTotal, List<BranchSetting> selectedBranchList, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales,
            Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales,   Map<Integer, StationSalesSummaryReport>  currencyTotalsMarketCollection,Map<Integer, StationSalesSummaryReport> currencyTotalsSales  ,  Map<Integer, StationSalesSummaryReport> currencyTotalsCollection);

    public void createPdf(Date beginDate, Date endDate, List<StationSalesSummaryReport> listOfFuelSales, List<StationSalesSummaryReport> listOfFuelCollection, List<StationSalesSummaryReport> listOfMarketSales, List<StationSalesSummaryReport> listOfMarketCollection,
            List<StationSalesSummaryReport> listOfTotalSales, List<StationSalesSummaryReport> listOfTotalCollection, BigDecimal fuelSaleTotalLiter, BigDecimal fuelSaleTotalMoney, BigDecimal fuelCollectionTotalMoney,
            BigDecimal marketSalesTotalPrice, BigDecimal marketCollectionTotalPrices, BigDecimal generalSalesTotal, BigDecimal generalCollectionTotal, List<BranchSetting> selectedBranchList, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales,
            Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection, Map<Integer, StationSalesSummaryReport> currencyTotalsSales, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketCollection,
            Map<Integer, StationSalesSummaryReport> currencyTotalsCollection);

}
