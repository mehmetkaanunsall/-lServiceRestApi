/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:03:15
 */
package com.mepsan.marwiz.general.report.salessummaryreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salessummaryreport.dao.ISalesSummaryReportDao;
import com.mepsan.marwiz.general.report.salessummaryreport.dao.SalesSummaryReport;
import com.mepsan.marwiz.general.report.salessummaryreport.dao.SalesSummaryReportDao;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class SalesSummaryReportService implements ISalesSummaryReportService {

    @Autowired
    private ISalesSummaryReportDao salesSummaryReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSalesSummaryReportDao(ISalesSummaryReportDao salesSummaryReportDao) {
        this.salesSummaryReportDao = salesSummaryReportDao;
    }

    @Override
    public String createWhere(SalesSummaryReport obj, List<BranchSetting> branchList, boolean isCentralSupplier, int supplierType) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        boolean isCentralIntegration = false;

        for (BranchSetting brs : branchList) {
            if (brs.isIsCentralIntegration()) {
                isCentralIntegration = true;
                break;
            }

        }

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
            where = where + " AND sli.stock_id IN(" + stockList + ") ";
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
            where = where + " AND sli.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
        }
        String accountList = "";
        for (Account account : obj.getListOfAccount()) {
            accountList = accountList + "," + String.valueOf(account.getId());
            if (account.getId() == 0) {
                accountList = "";
                break;
            }
        }
        if (!accountList.equals("")) {
            accountList = accountList.substring(1, accountList.length());
            where = where + " AND stck.supplier_id IN(" + accountList + ") ";
        }

        if (isCentralIntegration) {
            String centralSupplierList = "";
            for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                if (centralSupplier.getId() == 0) {
                    centralSupplierList = "";
                    break;
                }
            }
            if (!centralSupplierList.equals("")) {
                centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                where = where + " AND stck.centralsupplier_id IN(" + centralSupplierList + ") ";
            } else {
                if (isCentralSupplier) {
                    System.out.println("----supplier type---"+supplierType);
                    if (supplierType == 0) {
                        where = where + " AND (cspp.centersuppliertype_id != 2 OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 1) {
                        where = where + " AND (cspp.centersuppliertype_id NOT IN (1,2) OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 2) {
                        where = where + " AND cspp.centersuppliertype_id = 1 ";
                    }
                }
            }
        }

        if (obj.getAccount().getId() != 0) {
            where = where + " AND sl.account_id = " + obj.getAccount().getId();
        }

        where += " AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

        where += " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";

        return where;
    }

    @Override
    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return salesSummaryReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where, String branchList, SalesSummaryReport salesSummaryReport) {
        return salesSummaryReportDao.count(where, branchList, salesSummaryReport);
    }

    @Override
    public void exportPdf(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        int numberOfColumns = toogleList.size();
        try {
            connection = salesSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesSummaryReportDao.exportData(where, branchList, salesSummaryReport));
            rs = prep.executeQuery();

            //Birim İçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salessummaryreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : selectedBranchList) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (salesSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (salesSummaryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesSummaryReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (salesSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String param4 = "";
            if (salesSummaryReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesSummaryReport.getAccount().getName());
            }

            pdfDocument.getCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            StaticMethods.createHeaderPdf("frmSalesSummaryDatatable:dtbSalesSummary", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                Currency currency = new Currency(rs.getInt("slcurrency_id"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("countQuantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("giro")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalcountbystock")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    if (rs.getBigDecimal("totalcountbystock").compareTo(BigDecimal.valueOf(0)) == 0) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalgirobystock").divide(rs.getBigDecimal("totalcountbystock"), RoundingMode.HALF_EVEN)) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalgirobystock")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            for (SalesSummaryReport total : listOfTotals) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " "
                        + sessionBean.getNumberFormat().format(total.getTotalGiroByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("salessummaryreport"));

        } catch (DocumentException | SQLException e) {
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
                Logger.getLogger(SalesSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = salesSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesSummaryReportDao.exportData(where, branchList, salesSummaryReport));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("salessummaryreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            jRow++;
            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getEndDate()));

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : selectedBranchList) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String stockName = "";
            if (salesSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String categoryName = "";
            if (salesSummaryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesSummaryReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow stockcategory = excelDocument.getSheet().createRow(jRow++);
            stockcategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (salesSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            String param4 = "";
            if (salesSummaryReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesSummaryReport.getAccount().getName());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(param4);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmSalesSummaryDatatable:dtbSalesSummary", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                }

                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("countQuantity").doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("sliunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("giro")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("totalcountbystock")).doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(13)) {
                    if (rs.getBigDecimal("totalcountbystock").compareTo(BigDecimal.valueOf(0)) == 0) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("totalgirobystock").divide(rs.getBigDecimal("totalcountbystock"), RoundingMode.HALF_EVEN)).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("totalgirobystock")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

            }

            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            for (SalesSummaryReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " "
                        + StaticMethods.round(total.getTotalGiroByStock(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0));
                cell.setCellStyle(cellStyle);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("salessummaryreport"));
            } catch (IOException ex) {
                Logger.getLogger(SalesSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
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
                Logger.getLogger(SalesSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, SalesSummaryReport salesSummaryReport, List<Boolean> toogleList, List<SalesSummaryReport> listOfTotals, List<BranchSetting> selectedBranchList, String branchList, boolean isCentralSupplier) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = salesSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesSummaryReportDao.exportData(where, branchList, salesSummaryReport));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }
            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), salesSummaryReport.getEndDate())).append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : selectedBranchList) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String stockName = "";
            if (salesSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (salesSummaryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesSummaryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesSummaryReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (salesSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("supplier")).append(" : ").append(supplierName).append(" </div> ");
            }

            String param4 = "";
            if (salesSummaryReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesSummaryReport.getAccount().getName());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param4).append(" </div> ");

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
                    + "   @page { size: landscape; }"
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmSalesSummaryDatatable:dtbSalesSummary", toogleList, "headerBlack", sb);

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                Currency currency = new Currency(rs.getInt("slcurrency_id"));

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }

                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("countQuantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("giro"))))
                            .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("totalcountbystock"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(13)) {
                    if (rs.getBigDecimal("totalcountbystock").compareTo(BigDecimal.valueOf(0)) == 0) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(0))
                                .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("totalgirobystock").divide(rs.getBigDecimal("totalcountbystock"), RoundingMode.HALF_EVEN))))
                                .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }

                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("totalgirobystock"))))
                            .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
            }
            for (SalesSummaryReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append((listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "")).append(" ").append(sessionBean.getNumberFormat().format(total.getTotalGiroByStock()))
                        .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0)).append("</td>");
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
                Logger.getLogger(SalesSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();

    }

    @Override
    public List<SalesSummaryReport> totals(String where, String branchList, SalesSummaryReport salesSummaryReport) {
        return salesSummaryReportDao.totals(where, branchList, salesSummaryReport);
    }

    @Override
    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, SalesSummaryReport salesSummaryReport) {
        return salesSummaryReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, whereBranch, salesSummaryReport);
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhere(SalesSummaryReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
