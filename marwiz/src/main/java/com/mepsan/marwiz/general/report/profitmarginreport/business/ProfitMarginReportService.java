/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 05:32:55
 */
package com.mepsan.marwiz.general.report.profitmarginreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.profitmarginreport.dao.IProfitMarginReportDao;
import com.mepsan.marwiz.general.report.profitmarginreport.dao.ProfitMarginReport;
import com.mepsan.marwiz.general.report.profitmarginreport.dao.ProfitMarginReportDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfitMarginReportService implements IProfitMarginReportService {

    @Autowired
    private IProfitMarginReportDao profitMarginReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setProfitMarginReportDao(IProfitMarginReportDao profitMarginReportDao) {
        this.profitMarginReportDao = profitMarginReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(ProfitMarginReport obj) {
        String where = " ";

        String stockList = "";
        for (Stock stock : obj.getStockList()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }

        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND p.stock_id IN(" + stockList + ") ";
        }

        String categoryList = "";
        for (Categorization category : obj.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            where = where + " AND p.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
        }

        return where;
    }

    @Override
    public List<ProfitMarginReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {
        return profitMarginReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, profitMarginReport, branchList, centralIngetrationInf);
    }

    public BigDecimal calculateProfit(int type, BigDecimal totalSale, BigDecimal totalPurchase, BigDecimal saleCount) {
        BigDecimal profit = BigDecimal.valueOf(0);
        switch (type) {
            case 0:
                //Profit Margin
                if (totalSale.compareTo(BigDecimal.valueOf(0)) != 0 && totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = ((totalSale.subtract(totalPurchase)).divide(totalSale, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                }
                break;
            case 1:
                //Profit Percentage
                if (totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = ((totalSale.subtract(totalPurchase)).divide(totalPurchase, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                }
                break;
            case 2:
                //Total Profit
                if (totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = totalSale.subtract(totalPurchase);
                }
                break;
            case 3:
                if (saleCount.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = totalPurchase.divide(saleCount, 4, RoundingMode.HALF_EVEN);
                }
                break;
            default:
                break;
        }

        return profit;
    }

    public BigDecimal calcTotalProfit(BigDecimal overallTotalSales, BigDecimal overallTotalPurchase) {
        return overallTotalSales.subtract(overallTotalPurchase);
    }

    @Override
    public void exportPdf(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            if (!profitMarginReport.isReportType()) {
                connection = profitMarginReportDao.getDatasource().getConnection();
                prep = connection.prepareStatement(profitMarginReportDao.exportData(profitMarginReport, where, branchList, centralIngetrationInf));

                String stockList = "";
                for (Stock stock : profitMarginReport.getStockList()) {
                    stockList = stockList + "," + String.valueOf(stock.getId());
                    if (stock.getId() == 0) {
                        stockList = "";
                        break;
                    }
                }

                if (!stockList.equals("")) {
                    stockList = stockList.substring(1, stockList.length());
                }

                String categoryList = "";
                for (Categorization category : profitMarginReport.getListOfCategorization()) {
                    categoryList = categoryList + "," + String.valueOf(category.getId());
                    if (category.getId() == 0) {
                        categoryList = "";
                        break;
                    }
                }
                if (!categoryList.equals("")) {
                    categoryList = categoryList.substring(1, categoryList.length());
                }

                if (profitMarginReport.getStockList().isEmpty()) {
                    prep.setNull(1, java.sql.Types.NULL);
                } else {
                    prep.setString(1, stockList);
                }
                if (profitMarginReport.getListOfCategorization().isEmpty()) {
                    prep.setNull(2, java.sql.Types.NULL);
                } else {
                    prep.setString(2, categoryList);
                }

                rs = prep.executeQuery();
            }

            //Birim İçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitlossreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getBeginDate()) + " - "
                      + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("calculationtype") + " : " + (profitMarginReport.isCalculationType() ? "FIFO" : sessionBean.getLoc().getString("weightedaverage")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("reporttype") + " : " + (profitMarginReport.isReportType() ? sessionBean.getLoc().getString("summary") : sessionBean.getLoc().getString("detail")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (profitMarginReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : profitMarginReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (!profitMarginReport.isReportType()) {
                String stockName = "";
                if (profitMarginReport.getStockList().isEmpty()) {
                    stockName = sessionBean.getLoc().getString("all");
                } else if (profitMarginReport.getStockList().get(0).getId() == 0) {
                    stockName = sessionBean.getLoc().getString("all");
                } else {
                    for (Stock s : profitMarginReport.getStockList()) {
                        stockName += " , " + s.getName();
                    }
                    stockName = stockName.substring(3, stockName.length());
                }
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String branchName = "";
            if (profitMarginReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : profitMarginReport.getSelectedBranchList()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stocksituation") + " : " + (profitMarginReport.isIsAllStock() ? sessionBean.getLoc().getString("all") : sessionBean.getLoc().getString("dontshowstockwithoutmovement")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("tax") + " : " + (profitMarginReport.isIsTaxIncluded() ? sessionBean.getLoc().getString("istaxincluded") : sessionBean.getLoc().getString("taxexcluding")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            if (!profitMarginReport.isReportType()) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("excludeservicestocks") + " : " + (profitMarginReport.isIsExcludingServiceStock() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no")), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("beginingofperiodandendofperiodiscalculatedfromstocktaking") + " : " + (profitMarginReport.isIsCalculateStockTaking() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            PdfDocument pdfDocumentTotal = StaticMethods.preparePdf(Arrays.asList(true, true), 0);

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(warehouseStartQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(warehouseStartPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantity"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndPurchaseQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("purchaseamount"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndPurchasePrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("returnamount"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndPurchaseReturnQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("returnprice"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndPurchaseReturnPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("includingZ") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalZSalesQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("includingZ") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalZSalesPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("excludingZ") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalExcludingZSalesQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("excludingZ") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalExcludingZSalesPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("sum") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndSalesQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("sum") + ")", pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(beginToEndSalesPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasecost"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalPurchasePrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalStockTakingQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalStockTakingPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("differenceprice"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalDifferencePrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("profitmargin"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());

            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(profitMargin, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentage"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());

            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(profitPercentage, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalstockprofit"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalProfit, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingamountinendofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(warehouseEndQuantity, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingpriceinendofperiod"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(warehouseEndPrice, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofincome"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(sessionBean.getNumberFormat().format(totalIncome) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofexpense"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(sessionBean.getNumberFormat().format(totalExpense) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocumentTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprofit"), pdfDocumentTotal.getFontHeader()));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getDataCell());
            pdfDocumentTotal.getRightCell().setPhrase(new Phrase(new Phrase(totalProfit, pdfDocumentTotal.getFont())));
            pdfDocumentTotal.getPdfTable().addCell(pdfDocumentTotal.getRightCell());

            pdfDocument.getDocument().add(pdfDocumentTotal.getPdfTable());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            if (!profitMarginReport.isReportType()) {

                StaticMethods.createHeaderPdf("frmProfitMarginDatatable:dtbProfitMargin", toogleList, "headerBlack", pdfDocument);

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                while (rs.next()) {
                    formatterUnit.setMaximumFractionDigits(rs.getInt("unitrounding"));
                    formatterUnit.setMinimumFractionDigits(rs.getInt("unitrounding"));

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(1)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("code"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(2)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("centerproductcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(3)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("barcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(4)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("name"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("centralsuppliername"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("warehousestartquantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(9)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("warehousestartprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(10)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("endtobeginpurchasequantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(11)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("endtobeginpurchaseprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("endtobeginpurchasereturnquantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(13)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("endtobeginpurchasereturnprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(14)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("zsalesquantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(15)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("zsalesprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(16)) {
                        BigDecimal t = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("salecount") != null && rs.getBigDecimal("zsalesquantity") != null) {
                            t = rs.getBigDecimal("salecount").subtract(rs.getBigDecimal("zsalesquantity"));
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(t) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(17)) {
                        BigDecimal t = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("totalsaleprice") != null && rs.getBigDecimal("zsalesprice") != null) {
                            t = rs.getBigDecimal("totalsaleprice").subtract(rs.getBigDecimal("zsalesprice"));
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(t) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(18)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("salecount")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(19)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalsaleprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(20)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalpurchaseprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(21)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("stocktakingquantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(22)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("stocktakingprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(23)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("differenceprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(24)) {
                        String param3 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param3 += "%";
                        }
                        param3 += sessionBean.getNumberFormat().format(calculateProfit(0, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")));
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param3 += "%";
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(param3, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(25)) {
                        String param4 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param4 += "%";
                        }
                        param4 += sessionBean.getNumberFormat().format(calculateProfit(1, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")));
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param4 += "%";
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(26)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateProfit(2, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount"))) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(27)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("warehouseendquantity")) + (rs.getString("sortname") == null ? "" : rs.getString("sortname")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(28)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("warehouseendprice")) + sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                }
                //Alt Toplam
                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                    if (toogleList.get(0)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(1)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(2)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(3)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(4)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallWarehouseStartQuantity()), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(9)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalWarehouseStartPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(10)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseQuantity()) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(11)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchasePrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseReturnQuantity()) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(13)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseReturnPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(14)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallZSalesQuantity()) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(15)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallZSalesPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(16)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallBeginToEndSalesQuantity() != null && listOfTotal.getOverallZSalesQuantity() != null) {
                            b = listOfTotal.getOverallBeginToEndSalesQuantity().subtract(listOfTotal.getOverallZSalesQuantity());
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(17)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallBeginToEndSalesPrice() != null && listOfTotal.getOverallZSalesPrice() != null) {
                            b = listOfTotal.getOverallBeginToEndSalesPrice().subtract(listOfTotal.getOverallZSalesPrice());
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(18)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndSalesQuantity()) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(19)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndSalesPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(20)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalPurchase()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(21)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallStockTakingQuantity()) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(22)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallStockTakingPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(23)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallDifferencePrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(24)) {
                        BigDecimal calc = calculateProfit(0, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());

                        String param4 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param4 += "%";
                        }
                        param4 += sessionBean.getNumberFormat().format(calc);
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param4 += "%";
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(25)) {
                        BigDecimal calc = calculateProfit(1, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calc) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(26)) {
                        BigDecimal calc = calcTotalProfit(listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase());
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calc) + "", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(27)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallWarehouseEndQuantity()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(28)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalWarehouseEndPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                    pdfDocument.getRightCell().setBackgroundColor(Color.WHITE);
                }

            } else { //Özet Kategori

                StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("maincategory"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("category"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantity"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchaseamount"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnamount"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnprice"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("includingZ") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("includingZ") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("excludingZ") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("excludingZ") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("sum") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("sum") + ")", pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasecost"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("differenceprice"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitmargin"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentage"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprofit"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingamountinendofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockexistingpriceinendofperiod"), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                for (ProfitMarginReport p : listCategory) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(p.getBranchSetting().getBranch().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    pdfDocument.getDataCell().setPhrase(new Phrase(p.getCategorization().getParentId().getId() == 0 ? "-" : p.getCategorization().getParentId().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    pdfDocument.getDataCell().setPhrase(new Phrase(p.getCategorization().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getWarehouseStartQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getWarehouseStartPrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndPurchasePrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseReturnQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseReturnPrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getzSalesQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getzSalesPrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesQuantity() != null && p.getzSalesQuantity() != null) {
                        b = p.getBeginToEndSalesQuantity().subtract(p.getzSalesQuantity());
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesPrice() != null && p.getzSalesPrice() != null) {
                        b1 = p.getBeginToEndSalesPrice().subtract(p.getzSalesPrice());
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b1) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndSalesQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getBeginToEndSalesPrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getTotalPurchasePrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getStockTakingQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getStockTakingPrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getDifferencePrice()) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    String param3 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param3 += "%";
                    }
                    param3 += sessionBean.getNumberFormat().format(calculateProfit(0, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()));
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param3 += "%";
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(param3, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    String param4 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param4 += "%";
                    }
                    param4 += sessionBean.getNumberFormat().format(calculateProfit(1, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()));
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param4 += "%";
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateProfit(2, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity())) + sessionBean.currencySignOrCode(p.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getWarehouseEndQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(p.getWarehouseEndPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                }

                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseStartQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseStartPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchasePrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseReturnQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseReturnPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getzSalesQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getzSalesPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesQuantity() != null && listOfTotal.getzSalesQuantity() != null) {
                        b = listOfTotal.getBeginToEndSalesQuantity().subtract(listOfTotal.getzSalesQuantity());
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesPrice() != null && listOfTotal.getzSalesPrice() != null) {
                        b1 = listOfTotal.getBeginToEndSalesPrice().subtract(listOfTotal.getzSalesPrice());
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(b1) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndSalesQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndSalesPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getTotalPurchasePrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getStockTakingQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getStockTakingPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getDifferencePrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    String param3 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param3 += "%";
                    }
                    param3 += sessionBean.getNumberFormat().format(listOfTotal.getProfitMargin());
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param3 += "%";
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(param3, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    String param4 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param4 += "%";
                    }
                    param4 += sessionBean.getNumberFormat().format(listOfTotal.getProfitPercentage());
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param4 += "%";
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getTotalProfit()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseEndQuantity()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseEndPrice()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }

                pdfDocument.getRightCell().setBackgroundColor(Color.WHITE);
            }
            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            PdfDocument pdfDocumentIncomeExpense = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);
            createCellStylePdf("headerBlack", pdfDocumentIncomeExpense, pdfDocumentIncomeExpense.getTableHeader());

            pdfDocumentIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomeexpense"), pdfDocumentIncomeExpense.getFontColumnTitle()));
            pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getTableHeader());

            pdfDocumentIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("type"), pdfDocumentIncomeExpense.getFontColumnTitle()));
            pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getTableHeader());

            pdfDocumentIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentIncomeExpense.getFontColumnTitle()));
            pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getTableHeader());

            for (IncomeExpense i : listOfIncomeExpense) {

                pdfDocumentIncomeExpense.getDataCell().setPhrase(new Phrase((i.getParentId().getId() == 0 ? "" : i.getParentId().getName()) + " - " + i.getName(), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getDataCell());

                pdfDocumentIncomeExpense.getDataCell().setPhrase(new Phrase(i.isIsIncome() ? sessionBean.getLoc().getString("income") : sessionBean.getLoc().getString("expense"), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getDataCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(i.getTotalExchagePrice()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

            }

            if (!listOfIncomeExpense.isEmpty()) {
                pdfDocumentIncomeExpense.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalincome"), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase("", pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateIncomeExpenseSubTotal(true, listOfIncomeExpense)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalexpense"), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase("", pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

                pdfDocumentIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateIncomeExpenseSubTotal(false, listOfIncomeExpense)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentIncomeExpense.getFont()));
                pdfDocumentIncomeExpense.getPdfTable().addCell(pdfDocumentIncomeExpense.getRightCell());

            }

            pdfDocument.getDocument().add(pdfDocumentIncomeExpense.getPdfTable());
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("profitlossreport"));

        } catch (SQLException | DocumentException e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                Logger.getLogger(ProfitMarginReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            if (!profitMarginReport.isReportType()) {
                connection = profitMarginReportDao.getDatasource().getConnection();
                prep = connection.prepareStatement(profitMarginReportDao.exportData(profitMarginReport, where, branchList, centralIngetrationInf));

                List<Stock> tempListOfStock = new ArrayList<>();
                List<Categorization> tempListOfCategorization = new ArrayList<>();

                String stockList = "";
                for (Stock stock : profitMarginReport.getStockList()) {
                    stockList = stockList + "," + String.valueOf(stock.getId());
                    tempListOfStock.add(stock);
                    if (stock.getId() == 0) {
                        stockList = "";
                        tempListOfStock.clear();
                        break;
                    }
                }

                if (!stockList.equals("")) {
                    stockList = stockList.substring(1, stockList.length());
                }

                String categoryList = "";
                for (Categorization category : profitMarginReport.getListOfCategorization()) {
                    categoryList = categoryList + "," + String.valueOf(category.getId());
                    tempListOfCategorization.add(category);
                    if (category.getId() == 0) {
                        categoryList = "";
                        tempListOfCategorization.clear();
                        break;
                    }
                }
                if (!categoryList.equals("")) {
                    categoryList = categoryList.substring(1, categoryList.length());
                }

                if (tempListOfStock.isEmpty()) {
                    prep.setNull(1, java.sql.Types.NULL);
                } else {
                    prep.setString(1, stockList);
                }
                if (tempListOfCategorization.isEmpty()) {
                    prep.setNull(2, java.sql.Types.NULL);
                } else {
                    prep.setString(2, categoryList);
                }

                rs = prep.executeQuery();
            }
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("profitlossreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getBeginDate())
                      + " - " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getEndDate()));

            SXSSFRow calculationType = excelDocument.getSheet().createRow(jRow++);
            calculationType.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("calculationtype") + " : " + (profitMarginReport.isCalculationType() ? "FIFO" : sessionBean.getLoc().getString("weightedaverage")));

            SXSSFRow reporttype = excelDocument.getSheet().createRow(jRow++);
            reporttype.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("reporttype") + " : " + (profitMarginReport.isReportType() ? sessionBean.getLoc().getString("summary") : sessionBean.getLoc().getString("detail")));

            String categoryName = "";
            if (profitMarginReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : profitMarginReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow stockcategory = excelDocument.getSheet().createRow(jRow++);
            stockcategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (!profitMarginReport.isReportType()) {
                String stockName = "";
                if (profitMarginReport.getStockList().isEmpty()) {
                    stockName = sessionBean.getLoc().getString("all");
                } else if (profitMarginReport.getStockList().get(0).getId() == 0) {
                    stockName = sessionBean.getLoc().getString("all");
                } else {
                    for (Stock s : profitMarginReport.getStockList()) {
                        stockName += " , " + s.getName();
                    }
                    stockName = stockName.substring(3, stockName.length());
                }

                SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
                stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);
            }

            String branchName = "";
            if (profitMarginReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting : profitMarginReport.getSelectedBranchList()) {
                    branchName += " , " + branchSetting.getBranch().getName();
                }
                branchName = branchName.substring(2, branchName.length());
            }
            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            SXSSFRow stocksituation = excelDocument.getSheet().createRow(jRow++);
            stocksituation.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stocksituation") + " : " + (profitMarginReport.isIsAllStock() ? sessionBean.getLoc().getString("all") : sessionBean.getLoc().getString("dontshowstockwithoutmovement")));

            SXSSFRow tax = excelDocument.getSheet().createRow(jRow++);
            tax.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("tax") + " : " + (profitMarginReport.isIsTaxIncluded() ? sessionBean.getLoc().getString("istaxincluded") : sessionBean.getLoc().getString("taxexcluding")));

            if (!profitMarginReport.isReportType()) {
                SXSSFRow servicestock = excelDocument.getSheet().createRow(jRow++);
                servicestock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("excludeservicestocks") + " : " + (profitMarginReport.isIsExcludingServiceStock() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no")));
            }

            SXSSFRow beginend = excelDocument.getSheet().createRow(jRow++);
            beginend.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("beginingofperiodandendofperiodiscalculatedfromstocktaking") + " : " + (profitMarginReport.isIsCalculateStockTaking() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no")));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow rowTotal = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellTotal = rowTotal.createCell((short) 0);
            cellTotal.setCellValue(sessionBean.getLoc().getString("sum"));
            cellTotal.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow rowWarehouseStartQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellWarehouseStartQuantity = rowWarehouseStartQuantity.createCell((short) 0);
            cellWarehouseStartQuantity.setCellValue(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod") + " : ");
            cellWarehouseStartQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowWarehouseStartQuantity.createCell((short) 1).setCellValue(warehouseStartQuantity);

            SXSSFRow rowWarehouseStartPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellWarehouseStartPrice = rowWarehouseStartPrice.createCell((short) 0);
            cellWarehouseStartPrice.setCellValue(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod") + " : ");
            cellWarehouseStartPrice.setCellStyle(excelDocument.getStyleHeader());
            rowWarehouseStartPrice.createCell((short) 1).setCellValue(warehouseStartPrice);

            SXSSFRow rowBeginToEndPurchaseQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndPurchaseQuantity = rowBeginToEndPurchaseQuantity.createCell((short) 0);
            cellBeginToEndPurchaseQuantity.setCellValue(sessionBean.getLoc().getString("purchasequantity") + " : ");
            cellBeginToEndPurchaseQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndPurchaseQuantity.createCell((short) 1).setCellValue(beginToEndPurchaseQuantity);

            SXSSFRow rowBeginToEndPurchasePrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndPurchasePrice = rowBeginToEndPurchasePrice.createCell((short) 0);
            cellBeginToEndPurchasePrice.setCellValue(sessionBean.getLoc().getString("purchaseamount") + " : ");
            cellBeginToEndPurchasePrice.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndPurchasePrice.createCell((short) 1).setCellValue(beginToEndPurchasePrice);

            SXSSFRow rowBeginToEndPurchaseReturnQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndPurchaseReturnQuantity = rowBeginToEndPurchaseReturnQuantity.createCell((short) 0);
            cellBeginToEndPurchaseReturnQuantity.setCellValue(sessionBean.getLoc().getString("returnamount") + " : ");
            cellBeginToEndPurchaseReturnQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndPurchaseReturnQuantity.createCell((short) 1).setCellValue(beginToEndPurchaseReturnQuantity);

            SXSSFRow rowBeginToEndPurchaseReturnPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndPurchaseReturnPrice = rowBeginToEndPurchaseReturnPrice.createCell((short) 0);
            cellBeginToEndPurchaseReturnPrice.setCellValue(sessionBean.getLoc().getString("returnprice") + " : ");
            cellBeginToEndPurchaseReturnPrice.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndPurchaseReturnPrice.createCell((short) 1).setCellValue(beginToEndPurchaseReturnPrice);

            SXSSFRow rowZSalesQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellZSalesQuantity = rowZSalesQuantity.createCell((short) 0);
            cellZSalesQuantity.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("includingZ") + ")" + " : ");
            cellZSalesQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowZSalesQuantity.createCell((short) 1).setCellValue(totalZSalesQuantity);

            SXSSFRow rowZSalesPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellZSalesPrice = rowZSalesPrice.createCell((short) 0);
            cellZSalesPrice.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("includingZ") + ")" + " : ");
            cellZSalesPrice.setCellStyle(excelDocument.getStyleHeader());
            rowZSalesPrice.createCell((short) 1).setCellValue(totalZSalesPrice);

            SXSSFRow rowExcZSalesQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellExcZSalesQuantity = rowExcZSalesQuantity.createCell((short) 0);
            cellExcZSalesQuantity.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("excludingZ") + ")" + " : ");
            cellExcZSalesQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowExcZSalesQuantity.createCell((short) 1).setCellValue(totalExcludingZSalesQuantity);

            SXSSFRow rowExcZSalesPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellExcZSalesPrice = rowExcZSalesPrice.createCell((short) 0);
            cellExcZSalesPrice.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("excludingZ") + ")" + " : ");
            cellExcZSalesPrice.setCellStyle(excelDocument.getStyleHeader());
            rowExcZSalesPrice.createCell((short) 1).setCellValue(totalExcludingZSalesPrice);

            SXSSFRow rowBeginToEndSalesQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndSalesQuantity = rowBeginToEndSalesQuantity.createCell((short) 0);
            cellBeginToEndSalesQuantity.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("sum") + ")" + " : ");
            cellBeginToEndSalesQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndSalesQuantity.createCell((short) 1).setCellValue(beginToEndSalesQuantity);

            SXSSFRow rowBeginToEndSalesPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellBeginToEndSalesPrice = rowBeginToEndSalesPrice.createCell((short) 0);
            cellBeginToEndSalesPrice.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("sum") + ")" + " : ");
            cellBeginToEndSalesPrice.setCellStyle(excelDocument.getStyleHeader());
            rowBeginToEndSalesPrice.createCell((short) 1).setCellValue(beginToEndSalesPrice);

            SXSSFRow rowTotalPurchasePrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellTotalPurchasePrice = rowTotalPurchasePrice.createCell((short) 0);
            cellTotalPurchasePrice.setCellValue(sessionBean.getLoc().getString("purchasecost") + " : ");
            cellTotalPurchasePrice.setCellStyle(excelDocument.getStyleHeader());
            rowTotalPurchasePrice.createCell((short) 1).setCellValue(totalPurchasePrice);

            SXSSFRow rowTotalStockTakingQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellStockTakingQuantity = rowTotalStockTakingQuantity.createCell((short) 0);
            cellStockTakingQuantity.setCellValue(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod") + " : ");
            cellStockTakingQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowTotalStockTakingQuantity.createCell((short) 1).setCellValue(totalStockTakingQuantity);

            SXSSFRow rowTotalStockTakingPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellStockTakingPrice = rowTotalStockTakingPrice.createCell((short) 0);
            cellStockTakingPrice.setCellValue(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod") + " : ");
            cellStockTakingPrice.setCellStyle(excelDocument.getStyleHeader());
            rowTotalStockTakingPrice.createCell((short) 1).setCellValue(totalStockTakingPrice);

            SXSSFRow rowTotalDifferencePrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellDifferencePrice = rowTotalDifferencePrice.createCell((short) 0);
            cellDifferencePrice.setCellValue(sessionBean.getLoc().getString("differenceprice") + " : ");
            cellDifferencePrice.setCellStyle(excelDocument.getStyleHeader());
            rowTotalDifferencePrice.createCell((short) 1).setCellValue(totalDifferencePrice);

            SXSSFRow rowProfitMargin = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellProfitMargin = rowProfitMargin.createCell((short) 0);
            cellProfitMargin.setCellValue(sessionBean.getLoc().getString("profitmargin") + " : ");
            cellProfitMargin.setCellStyle(excelDocument.getStyleHeader());
            rowProfitMargin.createCell((short) 1).setCellValue(profitMargin);

            SXSSFRow rowProfitPercentage = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellProfitPercentage = rowProfitPercentage.createCell((short) 0);
            cellProfitPercentage.setCellValue(sessionBean.getLoc().getString("profitpercentage") + " : ");
            cellProfitPercentage.setCellStyle(excelDocument.getStyleHeader());
            rowProfitPercentage.createCell((short) 1).setCellValue(profitPercentage);

            SXSSFRow rowProfit = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellProfit = rowProfit.createCell((short) 0);
            cellProfit.setCellValue(sessionBean.getLoc().getString("totalstockprofit") + " : ");
            cellProfit.setCellStyle(excelDocument.getStyleHeader());
            rowProfit.createCell((short) 1).setCellValue(totalStockProfit);

            SXSSFRow rowWarehouseEndQuantity = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellWarehouseEndQuantity = rowWarehouseEndQuantity.createCell((short) 0);
            cellWarehouseEndQuantity.setCellValue(sessionBean.getLoc().getString("stockexistingamountinendofperiod") + " : ");
            cellWarehouseEndQuantity.setCellStyle(excelDocument.getStyleHeader());
            rowWarehouseEndQuantity.createCell((short) 1).setCellValue(warehouseEndQuantity);

            SXSSFRow rowWarehouseEndPrice = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellWarehouseEndPrice = rowWarehouseEndPrice.createCell((short) 0);
            cellWarehouseEndPrice.setCellValue(sessionBean.getLoc().getString("stockexistingpriceinendofperiod") + " : ");
            cellWarehouseEndPrice.setCellStyle(excelDocument.getStyleHeader());
            rowWarehouseEndPrice.createCell((short) 1).setCellValue(warehouseEndPrice);

            SXSSFRow rowIncome = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellIncome = rowIncome.createCell((short) 0);
            cellIncome.setCellValue(sessionBean.getLoc().getString("totalofincome") + " : ");
            cellIncome.setCellStyle(excelDocument.getStyleHeader());
            rowIncome.createCell((short) 1).setCellValue(sessionBean.getNumberFormat().format(totalIncome) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));

            SXSSFRow rowExpense = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellExpense = rowExpense.createCell((short) 0);
            cellExpense.setCellValue(sessionBean.getLoc().getString("totalofexpense") + ": ");
            cellExpense.setCellStyle(excelDocument.getStyleHeader());
            rowExpense.createCell((short) 1).setCellValue(sessionBean.getNumberFormat().format(totalExpense) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));

            SXSSFRow rowProfit1 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellProfit1 = rowProfit1.createCell((short) 0);
            cellProfit1.setCellValue(sessionBean.getLoc().getString("totalprofit") + " : ");
            cellProfit1.setCellStyle(excelDocument.getStyleHeader());
            rowProfit1.createCell((short) 1).setCellValue(totalProfit);

            SXSSFRow rowEmpty1 = excelDocument.getSheet().createRow(jRow++);

            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            if (!profitMarginReport.isReportType()) {
                StaticMethods.createHeaderExcel("frmProfitMarginDatatable:dtbProfitMargin", toogleList, "headerBlack", excelDocument.getWorkbook());
                jRow++;

                while (rs.next()) {
                    int b = 0;

                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    if (toogleList.get(0)) {
                        row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                    }
                    if (toogleList.get(1)) {
                        row.createCell((short) b++).setCellValue(rs.getString("code"));
                    }
                    if (toogleList.get(2)) {
                        row.createCell((short) b++).setCellValue(rs.getString("centerproductcode"));
                    }
                    if (toogleList.get(3)) {
                        row.createCell((short) b++).setCellValue(rs.getString("barcode"));
                    }
                    if (toogleList.get(4)) {
                        row.createCell((short) b++).setCellValue(rs.getString("name"));
                    }
                    if (toogleList.get(5)) {
                        row.createCell((short) b++).setCellValue(rs.getString("centralsuppliername"));
                    }
                    if (toogleList.get(6)) {
                        row.createCell((short) b++).setCellValue(rs.getString("accname"));
                    }
                    if (toogleList.get(7)) {
                        SXSSFCell quantity = row.createCell((short) b++);
                        quantity.setCellValue(rs.getString("brname"));
                    }
                    if (toogleList.get(8)) {
                        SXSSFCell warehouseStartQuantitys = row.createCell((short) b++);
                        warehouseStartQuantitys.setCellValue(StaticMethods.round(rs.getBigDecimal("warehousestartquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(9)) {
                        SXSSFCell warehouseStartPrices = row.createCell((short) b++);
                        warehouseStartPrices.setCellValue(StaticMethods.round(rs.getBigDecimal("warehousestartprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(10)) {
                        SXSSFCell beginToEndPurchaseQuantitys = row.createCell((short) b++);
                        beginToEndPurchaseQuantitys.setCellValue(StaticMethods.round(rs.getBigDecimal("endtobeginpurchasequantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(11)) {
                        SXSSFCell beginToEndPurchasePrices = row.createCell((short) b++);
                        beginToEndPurchasePrices.setCellValue(StaticMethods.round(rs.getBigDecimal("endtobeginpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(12)) {
                        SXSSFCell beginToEndPurchaseReturnQuantitys = row.createCell((short) b++);
                        beginToEndPurchaseReturnQuantitys.setCellValue(StaticMethods.round(rs.getBigDecimal("endtobeginpurchasereturnquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(13)) {
                        SXSSFCell beginToEndPurchaseReturnPrices = row.createCell((short) b++);
                        beginToEndPurchaseReturnPrices.setCellValue(StaticMethods.round(rs.getBigDecimal("endtobeginpurchasereturnprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(14)) {
                        SXSSFCell zSalesQuantity = row.createCell((short) b++);
                        zSalesQuantity.setCellValue(StaticMethods.round(rs.getBigDecimal("zsalesquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(15)) {
                        SXSSFCell zSalesPrice = row.createCell((short) b++);
                        zSalesPrice.setCellValue(StaticMethods.round(rs.getBigDecimal("zsalesprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(16)) {
                        BigDecimal t = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("salecount") != null && rs.getBigDecimal("zsalesquantity") != null) {
                            t = rs.getBigDecimal("salecount").subtract(rs.getBigDecimal("zsalesquantity"));
                        }
                        SXSSFCell exclZQuantity = row.createCell((short) b++);
                        exclZQuantity.setCellValue(StaticMethods.round(t.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(17)) {
                        BigDecimal t = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("totalsaleprice") != null && rs.getBigDecimal("zsalesprice") != null) {
                            t = rs.getBigDecimal("totalsaleprice").subtract(rs.getBigDecimal("zsalesprice"));
                        }
                        SXSSFCell exclZPrice = row.createCell((short) b++);
                        exclZPrice.setCellValue(StaticMethods.round(t.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(18)) {
                        SXSSFCell beginToEndSalesQuantitys = row.createCell((short) b++);
                        beginToEndSalesQuantitys.setCellValue(StaticMethods.round(rs.getBigDecimal("salecount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(19)) {
                        SXSSFCell beginToEndSalesPrices = row.createCell((short) b++);
                        beginToEndSalesPrices.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(20)) {
                        SXSSFCell costOfStock = row.createCell((short) b++);
                        costOfStock.setCellValue(StaticMethods.round(rs.getBigDecimal("totalpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(21)) {
                        SXSSFCell stockTakingq = row.createCell((short) b++);
                        stockTakingq.setCellValue(StaticMethods.round(rs.getBigDecimal("stocktakingquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(22)) {
                        SXSSFCell stockTakingP = row.createCell((short) b++);
                        stockTakingP.setCellValue(StaticMethods.round(rs.getBigDecimal("stocktakingprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(23)) {
                        SXSSFCell diffprice = row.createCell((short) b++);
                        diffprice.setCellValue(StaticMethods.round(rs.getBigDecimal("differenceprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(24)) {
                        SXSSFCell inComing = row.createCell((short) b++);
                        inComing.setCellValue(StaticMethods.round(calculateProfit(0, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(25)) {
                        SXSSFCell inComing = row.createCell((short) b++);
                        inComing.setCellValue(StaticMethods.round(calculateProfit(1, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(26)) {
                        SXSSFCell inComing = row.createCell((short) b++);
                        inComing.setCellValue(StaticMethods.round(calculateProfit(2, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(27)) {
                        SXSSFCell warehouseEndQuantitys = row.createCell((short) b++);
                        warehouseEndQuantitys.setCellValue(StaticMethods.round(rs.getBigDecimal("warehouseendquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(28)) {
                        SXSSFCell warehouseEndPrices = row.createCell((short) b++);
                        warehouseEndPrices.setCellValue(StaticMethods.round(rs.getBigDecimal("warehouseendprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                }

                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    int f = 0;
                    SXSSFRow rowf = excelDocument.getSheet().createRow(jRow++);
                    if (toogleList.get(0)) {
                        SXSSFCell e1 = rowf.createCell((short) f++);
                        e1.setCellValue("");
                        e1.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(1)) {
                        SXSSFCell e2 = rowf.createCell((short) f++);
                        e2.setCellValue("");
                        e2.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(2)) {
                        SXSSFCell e3 = rowf.createCell((short) f++);
                        e3.setCellValue("");
                        e3.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(3)) {
                        SXSSFCell e4 = rowf.createCell((short) f++);
                        e4.setCellValue("");
                        e4.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(4)) {
                        SXSSFCell e4 = rowf.createCell((short) f++);
                        e4.setCellValue("");
                        e4.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(5)) {
                        SXSSFCell e4 = rowf.createCell((short) f++);
                        e4.setCellValue("");
                        e4.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(6)) {
                        SXSSFCell e4 = rowf.createCell((short) f++);
                        e4.setCellValue("");
                        e4.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(7)) {
                        SXSSFCell e4 = rowf.createCell((short) f++);
                        e4.setCellValue(sessionBean.getLoc().getString("sum"));
                        e4.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(8)) {
                        SXSSFCell warehouseStartQuantitys = rowf.createCell((short) f++);//overallWarehouseStartQuantity
                        warehouseStartQuantitys.setCellValue(StaticMethods.round(listOfTotal.getOverallWarehouseStartQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        warehouseStartQuantitys.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(9)) {
                        SXSSFCell warehouseStartPrices = rowf.createCell((short) f++);
                        warehouseStartPrices.setCellValue(StaticMethods.round(listOfTotal.getOverallTotalWarehouseStartPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        warehouseStartPrices.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(10)) {
                        SXSSFCell beginToEndPurchaseQuantitys = rowf.createCell((short) f++);
                        beginToEndPurchaseQuantitys.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndPurchaseQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndPurchaseQuantitys.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(11)) {
                        SXSSFCell beginToEndPurchasePrices = rowf.createCell((short) f++);
                        beginToEndPurchasePrices.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndPurchasePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndPurchasePrices.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(12)) {
                        SXSSFCell beginToEndPurchaseReturnQuantitys = rowf.createCell((short) f++);
                        beginToEndPurchaseReturnQuantitys.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndPurchaseReturnQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndPurchaseReturnQuantitys.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(13)) {
                        SXSSFCell beginToEndPurchaseReturnPrices = rowf.createCell((short) f++);
                        beginToEndPurchaseReturnPrices.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndPurchaseReturnPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndPurchaseReturnPrices.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(14)) {
                        SXSSFCell zSalesQuantity = rowf.createCell((short) f++);
                        zSalesQuantity.setCellValue(StaticMethods.round(listOfTotal.getOverallZSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        zSalesQuantity.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(15)) {
                        SXSSFCell zSalesPrice = rowf.createCell((short) f++);
                        zSalesPrice.setCellValue(StaticMethods.round(listOfTotal.getOverallZSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        zSalesPrice.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(16)) {
                        SXSSFCell excZSalesQuantity = rowf.createCell((short) f++);
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallZSalesQuantity() != null && listOfTotal.getOverallBeginToEndSalesQuantity() != null) {
                            b = listOfTotal.getOverallBeginToEndSalesQuantity().subtract(listOfTotal.getOverallZSalesQuantity());
                        }
                        excZSalesQuantity.setCellValue(StaticMethods.round(b.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        excZSalesQuantity.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(17)) {
                        SXSSFCell excZSalesPrice = rowf.createCell((short) f++);
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallZSalesPrice() != null && listOfTotal.getOverallBeginToEndSalesPrice() != null) {
                            b = listOfTotal.getOverallBeginToEndSalesPrice().subtract(listOfTotal.getOverallZSalesPrice());
                        }
                        excZSalesPrice.setCellValue(StaticMethods.round(b.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        excZSalesPrice.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(18)) {
                        SXSSFCell beginToEndSalesQuantitys = rowf.createCell((short) f++);
                        beginToEndSalesQuantitys.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndSalesQuantitys.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(19)) {
                        SXSSFCell beginToEndSalesPrices = rowf.createCell((short) f++);
                        beginToEndSalesPrices.setCellValue(StaticMethods.round(listOfTotal.getOverallBeginToEndSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        beginToEndSalesPrices.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(20)) {
                        SXSSFCell costOfStock = rowf.createCell((short) f++);
                        costOfStock.setCellValue(StaticMethods.round(listOfTotal.getOverallTotalPurchase().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        costOfStock.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(21)) {
                        SXSSFCell stockTakingQuantity = rowf.createCell((short) f++);
                        stockTakingQuantity.setCellValue(StaticMethods.round(listOfTotal.getOverallStockTakingQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        stockTakingQuantity.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(22)) {
                        SXSSFCell stockTakingPrice = rowf.createCell((short) f++);
                        stockTakingPrice.setCellValue(StaticMethods.round(listOfTotal.getOverallStockTakingPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        stockTakingPrice.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(23)) {
                        SXSSFCell diffPrice = rowf.createCell((short) f++);
                        diffPrice.setCellValue(StaticMethods.round(listOfTotal.getOverallDifferencePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        diffPrice.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(24)) {
                        SXSSFCell inComing = rowf.createCell((short) f++);

                        BigDecimal bigDecimal = calculateProfit(0, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());
                        inComing.setCellValue(StaticMethods.round(bigDecimal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        inComing.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(25)) {
                        SXSSFCell inComing = rowf.createCell((short) f++);
                        BigDecimal bigDecimal = calculateProfit(1, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());
                        inComing.setCellValue(StaticMethods.round(bigDecimal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        inComing.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(26)) {
                        SXSSFCell inComing = rowf.createCell((short) f++);

                        BigDecimal bigDecimal = calcTotalProfit(listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase());

                        inComing.setCellValue(StaticMethods.round(bigDecimal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        inComing.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(27)) {
                        SXSSFCell warehouseEndQuantitys = rowf.createCell((short) f++);
                        warehouseEndQuantitys.setCellValue(StaticMethods.round(listOfTotal.getOverallWarehouseEndQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        warehouseEndQuantitys.setCellStyle(cellStyle1);
                    }
                    if (toogleList.get(28)) {
                        SXSSFCell warehouseEndPrices = rowf.createCell((short) f++);
                        warehouseEndPrices.setCellValue(StaticMethods.round(listOfTotal.getOverallTotalWarehouseEndPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        warehouseEndPrices.setCellStyle(cellStyle1);
                    }
                }

            } else {

                int x = 0;
                SXSSFRow rowch = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellbranch = rowch.createCell((short) x++);
                cellbranch.setCellValue(sessionBean.getLoc().getString("branch"));
                cellbranch.setCellStyle(styleheader);

                SXSSFCell cell0 = rowch.createCell((short) x++);
                cell0.setCellValue(sessionBean.getLoc().getString("maincategory"));
                cell0.setCellStyle(styleheader);

                SXSSFCell cell1 = rowch.createCell((short) x++);
                cell1.setCellValue(sessionBean.getLoc().getString("category"));
                cell1.setCellStyle(styleheader);

                SXSSFCell cell7 = rowch.createCell((short) x++);
                cell7.setCellValue(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod"));
                cell7.setCellStyle(styleheader);

                SXSSFCell cell8 = rowch.createCell((short) x++);
                cell8.setCellValue(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod"));
                cell8.setCellStyle(styleheader);

                SXSSFCell cell9 = rowch.createCell((short) x++);
                cell9.setCellValue(sessionBean.getLoc().getString("purchasequantity"));
                cell9.setCellStyle(styleheader);

                SXSSFCell cell10 = rowch.createCell((short) x++);
                cell10.setCellValue(sessionBean.getLoc().getString("purchaseamount"));
                cell10.setCellStyle(styleheader);

                SXSSFCell cell23 = rowch.createCell((short) x++);
                cell23.setCellValue(sessionBean.getLoc().getString("returnamount"));
                cell23.setCellStyle(styleheader);

                SXSSFCell cell24 = rowch.createCell((short) x++);
                cell24.setCellValue(sessionBean.getLoc().getString("returnprice"));
                cell24.setCellStyle(styleheader);

                SXSSFCell cell11 = rowch.createCell((short) x++);
                cell11.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("includingZ") + ")");
                cell11.setCellStyle(styleheader);

                SXSSFCell cell12 = rowch.createCell((short) x++);
                cell12.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("includingZ") + ")");
                cell12.setCellStyle(styleheader);

                SXSSFCell cell19 = rowch.createCell((short) x++);
                cell19.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("excludingZ") + ")");
                cell19.setCellStyle(styleheader);

                SXSSFCell cell191 = rowch.createCell((short) x++);
                cell191.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("excludingZ") + ")");
                cell191.setCellStyle(styleheader);

                SXSSFCell cell192 = rowch.createCell((short) x++);
                cell192.setCellValue(sessionBean.getLoc().getString("salesamount") + " (" + sessionBean.getLoc().getString("sum") + ")");
                cell192.setCellStyle(styleheader);

                SXSSFCell cell193 = rowch.createCell((short) x++);
                cell193.setCellValue(sessionBean.getLoc().getString("salesprice") + " (" + sessionBean.getLoc().getString("sum") + ")");
                cell193.setCellStyle(styleheader);

                SXSSFCell cell15 = rowch.createCell((short) x++);
                cell15.setCellValue(sessionBean.getLoc().getString("purchasecost"));
                cell15.setCellStyle(styleheader);

                SXSSFCell cell151 = rowch.createCell((short) x++);
                cell151.setCellValue(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod"));
                cell151.setCellStyle(styleheader);

                SXSSFCell cell152 = rowch.createCell((short) x++);
                cell152.setCellValue(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod"));
                cell152.setCellStyle(styleheader);

                SXSSFCell cell153 = rowch.createCell((short) x++);
                cell153.setCellValue(sessionBean.getLoc().getString("differenceprice"));
                cell153.setCellStyle(styleheader);

                SXSSFCell cell4 = rowch.createCell((short) x++);
                cell4.setCellValue(sessionBean.getLoc().getString("profitmargin"));
                cell4.setCellStyle(styleheader);

                SXSSFCell cell5 = rowch.createCell((short) x++);
                cell5.setCellValue(sessionBean.getLoc().getString("profitpercentage"));
                cell5.setCellStyle(styleheader);

                SXSSFCell cell6 = rowch.createCell((short) x++);
                cell6.setCellValue(sessionBean.getLoc().getString("totalprofit"));
                cell6.setCellStyle(styleheader);

                SXSSFCell cell13 = rowch.createCell((short) x++);
                cell13.setCellValue(sessionBean.getLoc().getString("stockexistingamountinendofperiod"));
                cell13.setCellStyle(styleheader);

                SXSSFCell cell14 = rowch.createCell((short) x++);
                cell14.setCellValue(sessionBean.getLoc().getString("stockexistingpriceinendofperiod"));
                cell14.setCellStyle(styleheader);

                for (ProfitMarginReport p : listCategory) {

                    int y = 0;
                    SXSSFRow rowC2 = excelDocument.getSheet().createRow(jRow++);

                    rowC2.createCell((short) y++).setCellValue(p.getBranchSetting().getBranch().getName());

                    rowC2.createCell((short) y++).setCellValue(p.getCategorization().getParentId().getId() == 0 ? "" : p.getCategorization().getParentId().getName());

                    rowC2.createCell((short) y++).setCellValue(p.getCategorization().getName());

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getWarehouseStartQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getWarehouseStartPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndPurchaseQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndPurchasePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndPurchaseReturnQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndPurchaseReturnPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getzSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getzSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesQuantity() != null && p.getzSalesQuantity() != null) {
                        b = p.getBeginToEndSalesQuantity().subtract(p.getzSalesQuantity());
                    }
                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(b.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesPrice() != null && p.getzSalesPrice() != null) {
                        b1 = p.getBeginToEndSalesPrice().subtract(p.getzSalesPrice());
                    }
                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(b1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getBeginToEndSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getTotalPurchasePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getStockTakingQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getStockTakingPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getDifferencePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(calculateProfit(0, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(calculateProfit(1, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(calculateProfit(2, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getWarehouseEndQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    rowC2.createCell((short) y++).setCellValue(StaticMethods.round(p.getWarehouseEndPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());

                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    int f = 0;
                    SXSSFRow rowf = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell e0 = rowf.createCell((short) f++);
                    e0.setCellValue("");
                    e0.setCellStyle(cellStyle1);

                    SXSSFCell e00 = rowf.createCell((short) f++);
                    e00.setCellValue("");
                    e00.setCellStyle(cellStyle1);

                    SXSSFCell e1 = rowf.createCell((short) f++);
                    e1.setCellValue(sessionBean.getLoc().getString("sum"));
                    e1.setCellStyle(cellStyle1);

                    SXSSFCell warehouseStartQuantitys = rowf.createCell((short) f++);
                    warehouseStartQuantitys.setCellValue(StaticMethods.round(listOfTotal.getWarehouseStartQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartQuantitys.setCellStyle(cellStyle1);

                    SXSSFCell warehouseStartPrices = rowf.createCell((short) f++);
                    warehouseStartPrices.setCellValue(StaticMethods.round(listOfTotal.getWarehouseStartPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseStartPrices.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndPurchaseQuantitys = rowf.createCell((short) f++);
                    beginToEndPurchaseQuantitys.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndPurchaseQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndPurchaseQuantitys.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndPurchasePrices = rowf.createCell((short) f++);
                    beginToEndPurchasePrices.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndPurchasePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndPurchasePrices.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndPurchaseReturnQuantitys = rowf.createCell((short) f++);
                    beginToEndPurchaseReturnQuantitys.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndPurchaseReturnQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndPurchaseReturnQuantitys.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndPurchaseReturnPrices = rowf.createCell((short) f++);
                    beginToEndPurchaseReturnPrices.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndPurchaseReturnPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndPurchaseReturnPrices.setCellStyle(cellStyle1);

                    SXSSFCell zSalesQuantity = rowf.createCell((short) f++);
                    zSalesQuantity.setCellValue(StaticMethods.round(listOfTotal.getzSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    zSalesQuantity.setCellStyle(cellStyle1);

                    SXSSFCell zSalesPrice = rowf.createCell((short) f++);
                    zSalesPrice.setCellValue(StaticMethods.round(listOfTotal.getzSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    zSalesPrice.setCellStyle(cellStyle1);

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesQuantity() != null && listOfTotal.getzSalesQuantity() != null) {
                        b = listOfTotal.getBeginToEndSalesQuantity().subtract(listOfTotal.getzSalesQuantity());
                    }

                    SXSSFCell excZSalesQuantity = rowf.createCell((short) f++);
                    excZSalesQuantity.setCellValue(StaticMethods.round(b.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    excZSalesQuantity.setCellStyle(cellStyle1);

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesPrice() != null && listOfTotal.getzSalesPrice() != null) {
                        b1 = listOfTotal.getBeginToEndSalesPrice().subtract(listOfTotal.getzSalesPrice());
                    }

                    SXSSFCell excZSalesPrice = rowf.createCell((short) f++);
                    excZSalesPrice.setCellValue(StaticMethods.round(b1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    excZSalesPrice.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndSalesQuantitys = rowf.createCell((short) f++);
                    beginToEndSalesQuantitys.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndSalesQuantitys.setCellStyle(cellStyle1);

                    SXSSFCell beginToEndSalesPrices = rowf.createCell((short) f++);
                    beginToEndSalesPrices.setCellValue(StaticMethods.round(listOfTotal.getBeginToEndSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    beginToEndSalesPrices.setCellStyle(cellStyle1);

                    SXSSFCell costOfStock = rowf.createCell((short) f++);
                    costOfStock.setCellValue(StaticMethods.round(listOfTotal.getTotalPurchasePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    costOfStock.setCellStyle(cellStyle1);

                    SXSSFCell stockTakingQ = rowf.createCell((short) f++);
                    stockTakingQ.setCellValue(StaticMethods.round(listOfTotal.getStockTakingQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    stockTakingQ.setCellStyle(cellStyle1);

                    SXSSFCell stockTakingP = rowf.createCell((short) f++);
                    stockTakingP.setCellValue(StaticMethods.round(listOfTotal.getStockTakingPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    stockTakingP.setCellStyle(cellStyle1);

                    SXSSFCell diffPrice = rowf.createCell((short) f++);
                    diffPrice.setCellValue(StaticMethods.round(listOfTotal.getDifferencePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    diffPrice.setCellStyle(cellStyle1);

                    SXSSFCell inComing = rowf.createCell((short) f++);
                    inComing.setCellValue(StaticMethods.round(listOfTotal.getProfitMargin().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    inComing.setCellStyle(cellStyle1);

                    SXSSFCell profitpercentage = rowf.createCell((short) f++);
                    profitpercentage.setCellValue(StaticMethods.round(listOfTotal.getProfitPercentage().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    profitpercentage.setCellStyle(cellStyle1);

                    SXSSFCell totalp = rowf.createCell((short) f++);
                    totalp.setCellValue(StaticMethods.round(listOfTotal.getTotalProfit().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalp.setCellStyle(cellStyle1);

                    SXSSFCell warehouseEndQuantitys = rowf.createCell((short) f++);
                    warehouseEndQuantitys.setCellValue(StaticMethods.round(listOfTotal.getWarehouseEndQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseEndQuantitys.setCellStyle(cellStyle1);

                    SXSSFCell warehouseEndPrices = rowf.createCell((short) f++);
                    warehouseEndPrices.setCellValue(StaticMethods.round(listOfTotal.getWarehouseEndPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    warehouseEndPrices.setCellStyle(cellStyle1);
                }

            }

            SXSSFRow rowEmpty2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFRow rowEmpty3 = excelDocument.getSheet().createRow(jRow++);

            int c = 0;
            SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cell = rowh.createCell((short) c++);
            cell.setCellValue(sessionBean.getLoc().getString("incomeexpense"));
            cell.setCellStyle(styleheader);

            SXSSFCell cell11 = rowh.createCell((short) c++);
            cell11.setCellValue(sessionBean.getLoc().getString("type"));
            cell11.setCellStyle(styleheader);

            SXSSFCell cell12 = rowh.createCell((short) c++);
            cell12.setCellValue(sessionBean.getLoc().getString("total"));
            cell12.setCellStyle(styleheader);

            for (IncomeExpense i : listOfIncomeExpense) {

                int a = 0;
                SXSSFRow rowD2 = excelDocument.getSheet().createRow(jRow++);
                rowD2.createCell((short) a++).setCellValue((i.getParentId().getId() == 0 ? "" : i.getParentId().getName()) + " - " + i.getName());

                rowD2.createCell((short) a++).setCellValue(i.isIsIncome() ? sessionBean.getLoc().getString("income") : sessionBean.getLoc().getString("expense"));

                SXSSFCell totalPriceOfIncome = rowD2.createCell((short) a++);
                totalPriceOfIncome.setCellValue(StaticMethods.round(i.getTotalExchagePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

            }
            if (!listOfIncomeExpense.isEmpty()) {
                CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                SXSSFRow rowf1 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell e0 = rowf1.createCell((short) 0);
                e0.setCellValue(sessionBean.getLoc().getString("totalincome"));
                e0.setCellStyle(cellStyle2);

                SXSSFCell e3 = rowf1.createCell((short) 1);
                e3.setCellValue("");
                e3.setCellStyle(cellStyle2);

                SXSSFCell tre = rowf1.createCell((short) 2);
                tre.setCellValue(StaticMethods.round(calculateIncomeExpenseSubTotal(true, listOfIncomeExpense).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                tre.setCellStyle(cellStyle2);

                SXSSFRow rowf2 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell e1 = rowf2.createCell((short) 0);
                e1.setCellValue(sessionBean.getLoc().getString("totalexpense"));
                e1.setCellStyle(cellStyle2);

                SXSSFCell e4 = rowf2.createCell((short) 1);
                e4.setCellValue("");
                e4.setCellStyle(cellStyle2);

                SXSSFCell tre1 = rowf2.createCell((short) 2);
                tre1.setCellValue(StaticMethods.round(calculateIncomeExpenseSubTotal(false, listOfIncomeExpense).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                tre1.setCellStyle(cellStyle2);

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("profitlossreport"));
            } catch (IOException ex) {
                Logger.getLogger(ProfitMarginReportService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                Logger.getLogger(ProfitMarginReport.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, ProfitMarginReport profitMarginReport, List<Boolean> toogleList, BigDecimal totalIncome, BigDecimal totalExpense, ProfitMarginReport totalProfitMargin, List<IncomeExpense> listOfIncomeExpense, List<ProfitMarginReport> listCategory, String branchList, int centralIngetrationInf, List<ProfitMarginReport> listOfTotals, String warehouseStartQuantity, String warehouseStartPrice, String beginToEndPurchaseQuantity, String beginToEndPurchasePrice, String beginToEndPurchaseReturnQuantity, String beginToEndPurchaseReturnPrice, String beginToEndSalesQuantity, String beginToEndSalesPrice, String totalPurchasePrice, String profitMargin, String profitPercentage, String totalStockProfit, String warehouseEndQuantity, String warehouseEndPrice, String totalProfit, String totalStockTakingPrice, String totalStockTakingQuantity, String totalDifferencePrice, String totalZSalesPrice, String totalZSalesQuantity, String totalExcludingZSalesPrice, String totalExcludingZSalesQuantity) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (!profitMarginReport.isReportType()) {
                connection = profitMarginReportDao.getDatasource().getConnection();
                prep = connection.prepareStatement(profitMarginReportDao.exportData(profitMarginReport, where, branchList, centralIngetrationInf));

                String stockList = "";
                for (Stock stock : profitMarginReport.getStockList()) {
                    stockList = stockList + "," + String.valueOf(stock.getId());
                    if (stock.getId() == 0) {
                        stockList = "";
                        break;
                    }
                }

                if (!stockList.equals("")) {
                    stockList = stockList.substring(1, stockList.length());
                }

                String categoryList = "";
                for (Categorization category : profitMarginReport.getListOfCategorization()) {
                    categoryList = categoryList + "," + String.valueOf(category.getId());
                    if (category.getId() == 0) {
                        categoryList = "";
                        break;
                    }
                }
                if (!categoryList.equals("")) {
                    categoryList = categoryList.substring(1, categoryList.length());
                }

                if (profitMarginReport.getStockList().isEmpty()) {
                    prep.setNull(1, java.sql.Types.NULL);
                } else {
                    prep.setString(1, stockList);
                }
                if (profitMarginReport.getListOfCategorization().isEmpty()) {
                    prep.setNull(2, java.sql.Types.NULL);
                } else {
                    prep.setString(2, categoryList);
                }

                rs = prep.executeQuery();
            }
            //Birim İçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getBeginDate())).
                      append(" - ").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), profitMarginReport.getEndDate())).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("calculationtype")).append(" : ").append(profitMarginReport.isCalculationType() ? "FIFO" : sessionBean.loc.getString("weightedaverage")).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("reporttype")).append(" : ").append(profitMarginReport.isReportType() ? sessionBean.loc.getString("summary") : sessionBean.loc.getString("detail")).append(" </div> ");

            String categoryName = "";
            if (profitMarginReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : profitMarginReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (!profitMarginReport.isReportType()) {
                String stockName = "";
                if (profitMarginReport.getStockList().isEmpty()) {
                    stockName = sessionBean.getLoc().getString("all");
                } else if (profitMarginReport.getStockList().get(0).getId() == 0) {
                    stockName = sessionBean.getLoc().getString("all");
                } else {
                    for (Stock s : profitMarginReport.getStockList()) {
                        stockName += " , " + s.getName();
                    }
                    stockName = stockName.substring(3, stockName.length());
                }
                sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");
            }

            String branchName = "";
            if (profitMarginReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (profitMarginReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting : profitMarginReport.getSelectedBranchList()) {
                    branchName += " , " + branchSetting.getBranch().getName();
                }
                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.getLoc().getString("branch")).append(" ").append(" : ").append(branchName).append("</div>");

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("stocksituation")).append(" : ").append(profitMarginReport.isIsAllStock() ? sessionBean.loc.getString("all") : sessionBean.loc.getString("dontshowstockwithoutmovement")).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("tax")).append(" : ").append(profitMarginReport.isIsTaxIncluded() ? sessionBean.loc.getString("istaxincluded") : sessionBean.loc.getString("taxexcluding")).append(" </div> ");

            if (!profitMarginReport.isReportType()) {
                sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("excludeservicestocks")).append(" : ").append(profitMarginReport.isIsExcludingServiceStock() ? sessionBean.loc.getString("yes") : sessionBean.loc.getString("no")).append(" </div> ");
            }

            sb.append(" <div style=\"font-family:sans-serif; font-size: 10px;\">").append(sessionBean.loc.getString("beginingofperiodandendofperiodiscalculatedfromstocktaking")).append(" : ").append(profitMarginReport.isIsCalculateStockTaking() ? sessionBean.loc.getString("yes") : sessionBean.loc.getString("no")).append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <style>"
                      + "        #printerDiv table {"
                      + "            font-family: arial, sans-serif;"
                      + "            border-collapse: collapse;"
                      + "            width: 100%;"
                      + "        }"
                      + "        #printerDiv table tr td, #printerDiv table tr th {"
                      + "            border: 1px solid #dddddd;"
                      + "            text-align: left;"
                      + "            padding: 8px;"
                      + "            font-size: 10px;"
                      + "        }"
                      + "        #printerDiv .tblTotal{"
                      + "            width: 40%;"
                      + "        }"
                      + "   @page { size: landscape; }"
                      + "    </style> <table class=\"tblTotal;\"> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(warehouseStartQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(warehouseStartPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("purchasequantity")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndPurchaseQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("purchaseamount")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndPurchasePrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("returnamount")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndPurchaseReturnQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("returnprice")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndPurchaseReturnPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesamount")).append(" (").append(sessionBean.getLoc().getString("includingZ")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalZSalesQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesprice")).append(" (").append(sessionBean.getLoc().getString("includingZ")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalZSalesPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesamount")).append(" (").append(sessionBean.getLoc().getString("excludingZ")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalExcludingZSalesQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesprice")).append(" (").append(sessionBean.getLoc().getString("excludingZ")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalExcludingZSalesPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesamount")).append(sessionBean.getLoc().getString("sum")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndSalesQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("salesprice")).append(sessionBean.getLoc().getString("sum")).append(")").append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(beginToEndSalesPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("purchasecost")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalPurchasePrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalStockTakingQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalStockTakingPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("differenceprice")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(totalDifferencePrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("profitmargin")).append("</td>");

            sb.append("<td style=\"text-align: right;\">").append(profitMargin).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("profitpercentage")).append("</td>");

            sb.append("<td style=\"text-align: right;\">").append(profitPercentage).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("totalstockprofit")).append("</td>");
            sb.append("<td style=\"text-align: right; \">").append(totalStockProfit).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stockexistingamountinendofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(warehouseEndQuantity).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("stockexistingpriceinendofperiod")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(warehouseEndPrice).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\"> ").append(sessionBean.getLoc().getString("totalofincome")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(formatterUnit.format(totalIncome)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("totalofexpense")).append("</td>");
            sb.append("<td style=\"text-align: right;\">").append(formatterUnit.format(totalExpense)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight: bold;\">").append(sessionBean.getLoc().getString("totalprofit")).append("</td>");
            sb.append("<td style=\"text-align: right; \">").append(totalProfit).append("</td>");
            sb.append(" </tr> ");

            sb.append(" </table> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            if (!profitMarginReport.isReportType()) {
                sb.append(" <table> ");
                StaticMethods.createHeaderPrint("frmProfitMarginDatatable:dtbProfitMargin", toogleList, "headerBlack", sb);

                while (rs.next()) {
                    sb.append(" <tr> ");
                    formatterUnit.setMaximumFractionDigits(rs.getInt("unitrounding"));
                    formatterUnit.setMinimumFractionDigits(rs.getInt("unitrounding"));

                    if (toogleList.get(0)) {
                        sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                    }

                    if (toogleList.get(1)) {
                        sb.append("<td>").append(rs.getString("code") == null ? "" : rs.getString("code")).append("</td>");
                    }
                    if (toogleList.get(2)) {
                        sb.append("<td>").append(rs.getString("centerproductcode") == null ? "" : rs.getString("centerproductcode")).append("</td>");
                    }
                    if (toogleList.get(3)) {
                        sb.append("<td>").append(rs.getString("barcode") == null ? "" : rs.getString("barcode")).append("</td>");
                    }
                    if (toogleList.get(4)) {
                        sb.append("<td>").append(rs.getString("name") == null ? "" : rs.getString("name")).append("</td>");
                    }
                    if (toogleList.get(5)) {
                        sb.append("<td>").append(rs.getString("centralsuppliername") == null ? "" : rs.getString("centralsuppliername")).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                    }
                    if (toogleList.get(7)) {
                        sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                    }
                    if (toogleList.get(8)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("warehousestartquantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(9)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("warehousestartprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(10)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("endtobeginpurchasequantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(11)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("endtobeginpurchaseprice") == null ? 0 : rs.getBigDecimal("endtobeginpurchaseprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(12)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("endtobeginpurchasereturnquantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(13)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("endtobeginpurchasereturnprice") == null ? 0 : rs.getBigDecimal("endtobeginpurchasereturnprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(14)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("zsalesquantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(15)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("zsalesprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(16)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("salecount") != null && rs.getBigDecimal("zsalesquantity") != null) {
                            b = rs.getBigDecimal("salecount").subtract(rs.getBigDecimal("zsalesquantity"));
                        }
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(b)).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(17)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (rs.getBigDecimal("totalsaleprice") != null && rs.getBigDecimal("zsalesprice") != null) {
                            b = rs.getBigDecimal("totalsaleprice").subtract(rs.getBigDecimal("zsalesprice"));
                        }
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(b)).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(18)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("salecount"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(19)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalsaleprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(20)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalpurchaseprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(21)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("stocktakingquantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(22)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("stocktakingprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(23)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("differenceprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(24)) {
                        String param3 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param3 += "%";
                        }
                        param3 += sessionBean.getNumberFormat().format(calculateProfit(0, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")));
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param3 += "%";
                        }
                        sb.append("<td style=\"text-align: right\">").append(param3).append("</td>");
                    }
                    if (toogleList.get(25)) {
                        String param4 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param4 += "%";
                        }
                        param4 += sessionBean.getNumberFormat().format(calculateProfit(1, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")));
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param4 += "%";
                        }
                        sb.append("<td style=\"text-align: right\">").append(param4).append("</td>");
                    }
                    if (toogleList.get(26)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateProfit(2, rs.getBigDecimal("totalsaleprice"), rs.getBigDecimal("totalpurchaseprice"), rs.getBigDecimal("salecount")))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(27)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("warehouseendquantity"))).append((rs.getString("sortname") == null ? "" : rs.getString("sortname"))).append("</td>");
                    }
                    if (toogleList.get(28)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("warehouseendprice"))).append(sessionBean.currencySignOrCode(rs.getInt("currency_id"), 0)).append("</td>");
                    }

                    sb.append(" </tr> ");

                }
                //Alt toplam
                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    sb.append(" <tr> ");

                    if (toogleList.get(0)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(1)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(2)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(3)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(4)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }

                    if (toogleList.get(5)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");
                    }
                    if (toogleList.get(7)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getLoc().getString("sum")).append("</td>");
                    }

                    if (toogleList.get(8)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallWarehouseStartQuantity())).append("</td>");
                    }
                    if (toogleList.get(9)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalWarehouseStartPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(10)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseQuantity())).append("</td>");
                    }
                    if (toogleList.get(11)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchasePrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(12)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseReturnQuantity())).append("</td>");
                    }
                    if (toogleList.get(13)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndPurchaseReturnPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(14)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallZSalesQuantity())).append("</td>");
                    }
                    if (toogleList.get(15)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallZSalesPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(16)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallBeginToEndSalesQuantity() != null && listOfTotal.getOverallZSalesQuantity() != null) {
                            listOfTotal.getOverallBeginToEndSalesQuantity().subtract(listOfTotal.getOverallZSalesQuantity());
                        }
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(b)).append("</td>");
                    }
                    if (toogleList.get(17)) {
                        BigDecimal b = BigDecimal.valueOf(0);
                        if (listOfTotal.getOverallBeginToEndSalesPrice() != null && listOfTotal.getOverallZSalesPrice() != null) {
                            listOfTotal.getOverallBeginToEndSalesPrice().subtract(listOfTotal.getOverallZSalesPrice());
                        }
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(b)).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(18)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndSalesQuantity())).append("</td>");
                    }
                    if (toogleList.get(19)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallBeginToEndSalesPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(20)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalPurchase())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(21)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallStockTakingQuantity())).append("</td>");
                    }
                    if (toogleList.get(22)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallStockTakingPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(23)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallDifferencePrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(24)) {

                        BigDecimal bd = calculateProfit(0, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());

                        String param3 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param3 += "%";
                        }
                        param3 += sessionBean.getNumberFormat().format(bd);
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param3 += "%";
                        }
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(param3).append("</td>");
                    }
                    if (toogleList.get(25)) {

                        BigDecimal bigDecimal = calculateProfit(1, listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase(), listOfTotal.getOverallQuantity());

                        String param4 = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param4 += "%";
                        }
                        param4 += sessionBean.getNumberFormat().format(bigDecimal);
                        if (sessionBean.getUser().getLanguage().getId() != 1) {
                            param4 += "%";
                        }
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(param4).append("</td>");
                    }
                    if (toogleList.get(26)) {
                        BigDecimal bd = calcTotalProfit(listOfTotal.getTempOverallTotalSales(), listOfTotal.getOverallTotalPurchase());
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(bd)).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(27)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallWarehouseEndQuantity())).append("</td>");
                    }
                    if (toogleList.get(28)) {
                        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getOverallTotalWarehouseEndPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
                    }

                    sb.append(" </tr> ");
                }

                sb.append(" </table> ");
            } else {
                sb.append(" <table> ");
                sb.append(" <tr> ");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("branch")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("maincategory")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("category")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stockexistingamountinbeginningofperiod")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stockexistingpriceinbeginningofperiod")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("purchasequantity")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("purchaseamount")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("returnamount")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("returnprice")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesamount")).append(" (").append(sessionBean.getLoc().getString("includingZ")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesprice")).append(" (").append(sessionBean.getLoc().getString("includingZ")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesamount")).append(" (").append(sessionBean.getLoc().getString("excludingZ")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesprice")).append(" (").append(sessionBean.getLoc().getString("excludingZ")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesamount")).append(" (").append(sessionBean.getLoc().getString("sum")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("salesprice")).append(" (").append(sessionBean.getLoc().getString("sum")).append(")").append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("purchasecost")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stocktakingdifferencequantityincurrentofperiod")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stocktakingdifferencepriceincurrentofperiod")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("differenceprice")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("profitmargin")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("profitpercentage")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("totalprofit")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stockexistingamountinendofperiod")).append("</th>");

                sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("stockexistingpriceinendofperiod")).append("</th>");

                sb.append(" </tr> ");

                for (ProfitMarginReport p : listCategory) {
                    sb.append(" <tr> ");

                    sb.append("<td>").append(p.getBranchSetting().getBranch().getName() != null ? p.getBranchSetting().getBranch().getName() : "").append("</td>");

                    sb.append("<td>").append(p.getCategorization().getParentId().getId() != 0 ? p.getCategorization().getParentId().getName() : "").append("</td>");

                    sb.append("<td>").append(p.getCategorization().getName() != null ? p.getCategorization().getName() : "").append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getWarehouseStartQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getWarehouseStartPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndPurchasePrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseReturnQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndPurchaseReturnPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getzSalesQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getzSalesPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesQuantity() != null && p.getzSalesQuantity() != null) {
                        b = p.getBeginToEndSalesQuantity().subtract(p.getzSalesQuantity());
                    }
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(b)).append("</td>");

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (p.getBeginToEndSalesPrice() != null && p.getzSalesPrice() != null) {
                        b1 = p.getBeginToEndSalesPrice().subtract(p.getzSalesPrice());
                    }
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(b1)).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndSalesQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getBeginToEndSalesPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getTotalPurchasePrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getStockTakingQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getStockTakingPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getDifferencePrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    String param3 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param3 += "%";
                    }
                    param3 += sessionBean.getNumberFormat().format(calculateProfit(0, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()));
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param3 += "%";
                    }
                    sb.append("<td style=\"text-align: right\">").append(param3).append("</td>");

                    String param4 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param4 += "%";
                    }
                    param4 += sessionBean.getNumberFormat().format(calculateProfit(1, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()));
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param4 += "%";
                    }
                    sb.append("<td style=\"text-align: right\">").append(param4).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateProfit(2, p.getTempOverallTotalSales(), p.getTotalPurchasePrice(), p.getQuantity()))).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getWarehouseEndQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(p.getWarehouseEndPrice())).append(sessionBean.currencySignOrCode(p.getCurrency().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                }

                for (ProfitMarginReport listOfTotal : listOfTotals) {
                    sb.append(" <tr> ");
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getLoc().getString("sum")).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseStartQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseStartPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchasePrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseReturnQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndPurchaseReturnPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getzSalesQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getzSalesPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    BigDecimal b = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesQuantity() != null && listOfTotal.getzSalesQuantity() != null) {
                        b = listOfTotal.getBeginToEndSalesQuantity().subtract(listOfTotal.getzSalesQuantity());
                    }
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(b)).append("</td>");

                    BigDecimal b1 = BigDecimal.valueOf(0);
                    if (listOfTotal.getBeginToEndSalesPrice() != null && listOfTotal.getzSalesPrice() != null) {
                        b1 = listOfTotal.getBeginToEndSalesPrice().subtract(listOfTotal.getzSalesPrice());
                    }
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(b1)).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndSalesQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getBeginToEndSalesPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getTotalPurchasePrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getStockTakingQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getStockTakingPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getDifferencePrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    String param3 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param3 += "%";
                    }
                    param3 += sessionBean.getNumberFormat().format(listOfTotal.getProfitMargin());
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param3 += "%";
                    }
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(param3).append("</td>");

                    String param4 = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param4 += "%";
                    }
                    param4 += sessionBean.getNumberFormat().format(listOfTotal.getProfitPercentage());
                    if (sessionBean.getUser().getLanguage().getId() != 1) {
                        param4 += "%";
                    }
                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(param4).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getTotalProfit())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseEndQuantity())).append("</td>");

                    sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(listOfTotal.getWarehouseEndPrice())).append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");
                }

                sb.append(" </table> ");
            }

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <table> ");

            sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("incomeexpense")).append("</th>");

            sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("type")).append("</th>");

            sb.append("<th style=\"text-align: center\">").append(sessionBean.getLoc().getString("total")).append("</th>");

            for (IncomeExpense i : listOfIncomeExpense) {
                sb.append(" <tr> ");

                sb.append("<td>").append((i.getParentId().getId() == 0 ? "" : i.getParentId().getName())).append(" - ").append(i.getName() == null ? "" : i.getName()).append("</td>");

                sb.append("<td>").append(i.isIsIncome() ? sessionBean.getLoc().getString("income") : sessionBean.getLoc().getString("expense")).append("</td>");

                sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(i.getTotalExchagePrice())).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");

                sb.append(" </tr> ");

            }

            if (!listOfIncomeExpense.isEmpty()) {
                sb.append(" <tr> ");

                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getLoc().getString("totalincome")).append("</td>");

                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");

                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(calculateIncomeExpenseSubTotal(true, listOfIncomeExpense))).append("</td>");

                sb.append(" </tr> ");
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getLoc().getString("totalexpense")).append("</td>");
                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append("").append("</td>");

                sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.getNumberFormat().format(calculateIncomeExpenseSubTotal(false, listOfIncomeExpense))).append("</td>");

                sb.append(" </tr> ");
            }

            sb.append(" </table> ");

        } catch (SQLException e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prep != null) {
                    prep.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex1) {
                Logger.getLogger(ProfitMarginReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return sb.toString();
    }

    @Override
    public List<ProfitMarginReport> findAllCategory(ProfitMarginReport profitMarginReport, String where, String branchList, int centralIngetrationInf) {
        return profitMarginReportDao.findAllCategory(profitMarginReport, where, branchList, centralIngetrationInf);
    }

    public BigDecimal calculateIncomeExpenseSubTotal(boolean isIncome, List<IncomeExpense> listOfIncomeExpense) {
        BigDecimal t = BigDecimal.valueOf(0);
        for (IncomeExpense p : listOfIncomeExpense) {
            if (p.isIsIncome() == isIncome) {
                t = t.add(p.getTotalExchagePrice());
            }
        }
        return t;

    }

    @Override
    public List<ProfitMarginReport> totals(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {
        return profitMarginReportDao.totals(where, profitMarginReport, branchList, centralIngetrationInf);
    }

//    @Override
//    public List<ProfitMarginReport> totalsCategory(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {
//        return profitMarginReportDao.totalsCategory(where, profitMarginReport, branchList, centralIngetrationInf);
//    }
}
