/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 12:10:31
 */
package com.mepsan.marwiz.general.report.salesdetailreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salesdetailreport.dao.ISalesDetailReportDao;
import com.mepsan.marwiz.general.report.salesdetailreport.dao.SalesDetailReport;
import com.mepsan.marwiz.general.report.salesdetailreport.dao.SalesDetailReportDao;
import java.awt.Color;
import java.io.IOException;
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

public class SalesDetailReportService implements ISalesDetailReportService {

    @Autowired
    private ISalesDetailReportDao salesDetailReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setSalesDetailReportDao(ISalesDetailReportDao salesDetailReportDao) {
        this.salesDetailReportDao = salesDetailReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(SalesDetailReport obj, List<BranchSetting> branchList, boolean isCentralSupplier, int supplierType) {
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

        //Kasiyer filtresi
        String cashierList = "";
        for (UserData userdata : obj.getListOfCashier()) {
            cashierList = cashierList + "," + String.valueOf(userdata.getId());
            if (userdata.getId() == 0) {
                cashierList = "";
                break;
            }
        }
        if (!cashierList.equals("")) {
            cashierList = cashierList.substring(1, cashierList.length());
            where = where + " AND sl.userdata_id IN(" + cashierList + ") ";
        }

        where += " AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

        where += " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";

        return where;
    }

    @Override
    public int count(String where) {
        return salesDetailReportDao.count(where);
    }

    @Override
    public void exportPdf(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalSalesQuantity, String subTotalMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = salesDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesDetailReportDao.exportData(where, branchList, salesDetailReport));

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
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesdetailreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (salesDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (salesDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (salesDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String param4 = "";
            if (salesDetailReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesDetailReport.getAccount().getName());
            }

            pdfDocument.getCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String cashierName = "";
            if (salesDetailReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesDetailReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cashier") + " : " + cashierName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmSalesDetailDatatable:dtbSalesDetail", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                Currency currency = new Currency(rs.getInt("slicurrency_id"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("sliprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("acc1name"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(10)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentnumber"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("rcpreceiptno"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sliquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(15)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("usname")+ " " + rs.getString("ussurname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(16)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("yes"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("no"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(17)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(18)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("sltransactionno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(19)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(20)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalsalequantity") + " : " + subTotalSalesQuantity, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalgiro") + " : " + subTotalMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());


            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("salesdetailreport"));

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
                Logger.getLogger(SalesDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalSalesQuantity, String subTotalMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = salesDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesDetailReportDao.exportData(where, branchList, salesDetailReport));

            rs = prep.executeQuery();
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("salesdetailreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getEndDate()));

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String stockName = "";
            if (salesDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(jRow++);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String categoryName = "";
            if (salesDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow stockcategory = excelDocument.getSheet().createRow(jRow++);
            stockcategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (salesDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            String param4 = "";
            if (salesDetailReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesDetailReport.getAccount().getName());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(param4);

            String cashierName = "";
            if (salesDetailReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesDetailReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }

            SXSSFRow cashier = excelDocument.getSheet().createRow(jRow++);
            cashier.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cashier") + " : " + cashierName);

            SXSSFRow empty5 = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmSalesDetailDatatable:dtbSalesDetail", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("sliprocessdate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }

                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                }

                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("acc1name"));
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }

                if (toogleList.get(10)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("invdocumentnumber"));
                    } else {
                        row.createCell((short) b++).setCellValue(rs.getString("rcpreceiptno"));
                    }
                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("sliunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("sliquantity").doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("slitotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"));
                }
                if (toogleList.get(15)) {
                    row.createCell((short) b++).setCellValue(rs.getString("usname")+ " " + rs.getString("ussurname"));
                }
                if (toogleList.get(16)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("yes"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("no"));
                    }
                }
                if (toogleList.get(17)) {
                    row.createCell((short) b++).setCellValue("");
                }
                if (toogleList.get(18)) {
                    row.createCell((short) b++).setCellValue(rs.getString("sltransactionno"));
                }
                if (toogleList.get(19)) {
                    row.createCell((short) b++).setCellValue("");
                }
                if (toogleList.get(20)) {
                    row.createCell((short) b++).setCellValue("");
                }
                
            }

            jRow++;
            SXSSFRow rowEmpty1 = excelDocument.getSheet().createRow(jRow++);

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(sessionBean.getLoc().getString("totalsalequantity") + " " + subTotalSalesQuantity);
            cell.setCellStyle(stylefooter);

            SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell2 = row2.createCell((short) 0);
            cell2.setCellValue(sessionBean.getLoc().getString("totalgiro") + " " + subTotalMoney);
            cell2.setCellStyle(stylefooter);
            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("salesdetailreport"));
            } catch (IOException ex) {
                Logger.getLogger(SalesDetailReportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesDetailReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SalesDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, SalesDetailReport salesDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalSalesQuantity, String subTotalMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = salesDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesDetailReportDao.exportData(where, branchList, salesDetailReport));
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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesDetailReport.getEndDate())).append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String stockName = "";
            if (salesDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : salesDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (salesDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : salesDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (salesDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : salesDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (salesDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (salesDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : salesDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("supplier")).append(" : ").append(supplierName).append(" </div> ");
            }
            String param4 = "";
            if (salesDetailReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesDetailReport.getAccount().getName());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param4).append(" </div> ");

            String cashierName = "";
            if (salesDetailReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesDetailReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesDetailReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("cashier")).append(" : ").append(cashierName).append(" </div> ");

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
                    + "             font-size: 10px;"
                    + "        }"
                    + "   @page { size: landscape; }"
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmSalesDetailDatatable:dtbSalesDetail", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                Currency currency = new Currency(rs.getInt("slicurrency_id"));

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("sliprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("sliprocessdate"))).append("</td>");
                }

                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }

                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("acc1name") == null ? "" : rs.getString("acc1name")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        sb.append("<td>").append(rs.getString("invdocumentnumber") == null ? "" : rs.getString("invdocumentnumber")).append("</td>");
                    } else {
                        sb.append("<td>").append(rs.getString("rcpreceiptno") == null ? "" : rs.getString("rcpreceiptno")).append("</td>");
                    }
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("sliquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : (rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"))).append("</td>");
                }
                if (toogleList.get(15)) {
                    sb.append("<td>").append(rs.getString("usname") == null ? " " : rs.getString("usname")).append(rs.getString("ussurname") == null ? " " : rs.getString("ussurname")).append("</td>");
                }
                if (toogleList.get(16)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("yes")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("no")).append("</td>");
                    }
                }
                if (toogleList.get(17)) {
                    sb.append("<td>").append("").append("</td>");
                }
                if (toogleList.get(18)) {
                    sb.append("<td>").append(rs.getString("sltransactionno") == null ? "" : rs.getString("sltransactionno")).append("</td>");
                }
                if (toogleList.get(19)) {
                    sb.append("<td>").append("").append("</td>");
                }
                if (toogleList.get(20)) {
                    sb.append("<td>").append("").append("</td>");
                }
                

                sb.append(" </tr> ");
            }

            
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalsalequantity")).append(" ").append(" : ")
                    .append(subTotalSalesQuantity).append("</td>");
            sb.append(" </tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalgiro")).append(" ").append(" : ")
                    .append(subTotalMoney).append("</td>");
            sb.append(" </tr> ");


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
                Logger.getLogger(SalesDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, SalesDetailReport salesDetailReport) {
        return salesDetailReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList, salesDetailReport);
    }

    @Override
    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where, String branchList, SalesDetailReport salesDetailReport) {
        return salesDetailReportDao.count(where, branchList, salesDetailReport);
    }

    @Override
    public String createWhere(SalesDetailReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesDetailReport> totals(String where, String branchList, SalesDetailReport salesDetailReport) {
        return salesDetailReportDao.totals(where, branchList, salesDetailReport);
    }

}
