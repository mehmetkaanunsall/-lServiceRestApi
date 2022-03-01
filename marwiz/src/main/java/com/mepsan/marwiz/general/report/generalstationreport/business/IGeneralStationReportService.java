/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import com.mepsan.marwiz.general.pattern.IReportService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author m.duzoylum
 */
public interface IGeneralStationReportService extends IReportService<GeneralStation> {

    public List<GeneralStation> findAll(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType);

    public List<GeneralStation> findAllMarket(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int centralIntegrationIf, int costType);

    public List<GeneralStation> findAllAutomat(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int costType);

    public List<GeneralStation> totals(String where, Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType);

    public void exportPdf(
            Date beginDate,
            Date endDate,
            List<BranchSetting> selectedBranchList,
            int lastUnitPrice,
            List<Boolean> toogleList,
            List<GeneralStation> listFuel,
            List<GeneralStation> totalListFuel,
            Map<Integer, GeneralStation> currencyTotalsCollection,
            List<Boolean> toogleListMarket,
            List<GeneralStation> listMarket,
            List<Boolean> toogleListAutomat,
            List<GeneralStation> listAutomat,
            List<GeneralStation> totalListAutomat,
            BigDecimal totalPurchaseAmount,
            BigDecimal totalSalesAmount,
            BigDecimal totalProfitAmount,
            BigDecimal totalProfitRate,
            BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost,
            int reportType,
            int costType,
            List<GeneralStation> listOfTotalsCategory,
            HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated
    );

    public void exportExcel(
            Date beginDate,
            Date endDate,
            List<BranchSetting> selectedBranchList,
            String branchList,
            int lastUnitPrice,
            List<Boolean> toogleList,
            List<GeneralStation> listFuel,
            List<GeneralStation> totalListFuel,
            Map<Integer, GeneralStation> currencyTotalsCollection,
            List<Boolean> toogleListMarket,
            List<GeneralStation> listMarket,
            List<GeneralStation> totalListMarket,
            List<Boolean> toogleListAutomat,
            List<GeneralStation> listAutomat,
            List<GeneralStation> totalListAutomat,
            Map<Integer, GeneralStation> currencyTotalsCollection2,
            int centralIntegrationIf,
            BigDecimal totalPurchaseAmount,
            BigDecimal totalSalesAmount,
            BigDecimal totalProfitAmount,
            BigDecimal totalProfitRate,
            BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost,
            int reportType,
            int costType, 
            List<GeneralStation> listOfTotalsCategory,
            HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated
    );

    public String exportPrinter(
            Date beginDate,
            Date endDate,
            List<BranchSetting> selectedBranchList,
            int lastUnitPrice,
            List<Boolean> toogleList,
            List<GeneralStation> listFuel,
            List<GeneralStation> totalListFuel,
            Map<Integer, GeneralStation> currencyTotalsCollection,
            List<Boolean> toogleListMarket,
            List<GeneralStation> listMarket,
            List<Boolean> toogleListAutomat,
            List<GeneralStation> totalListAutomat,
            int centralIntegrationIf,
            BigDecimal totalPurchaseAmount,
            BigDecimal totalSalesAmount,
            BigDecimal totalProfitAmount,
            BigDecimal totalProfitRate,
            BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost,
            int reportType,
            int costType,
            List<GeneralStation> listOfTotalsCategory,
            HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated
    );

}
