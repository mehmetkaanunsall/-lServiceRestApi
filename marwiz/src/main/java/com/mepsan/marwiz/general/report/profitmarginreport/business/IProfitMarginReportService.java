/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 05:31:41
 */
package com.mepsan.marwiz.general.report.profitmarginreport.business;

import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.report.profitmarginreport.dao.ProfitMarginReport;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IProfitMarginReportService {

    public List<ProfitMarginReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);

    public String createWhere(ProfitMarginReport obj);

    public void exportPdf(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity);

    public void exportExcel(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity);

    public String exportPrinter(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity);

    public List<ProfitMarginReport> findAllCategory(ProfitMarginReport profitMarginReport, String where, String branchList, int centralIngetrationInf);

    public List<ProfitMarginReport> totals(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);

    // public List<ProfitMarginReport> totalsCategory(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf);
}
