/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:14:56
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.IPurchaseSummaryReportDao;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.PurchaseSummaryReport;
import com.mepsan.marwiz.general.report.purchasesummaryreport.dao.PurchaseSummaryReportDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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

public class PurchaseSummaryReportService implements IPurchaseSummaryReportService {

    @Autowired
    private IPurchaseSummaryReportDao purchaseSummaryReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setPurchaseSummaryReportDao(IPurchaseSummaryReportDao purchaseSummaryReportDao) {
        this.purchaseSummaryReportDao = purchaseSummaryReportDao;
    }

    @Override
    public String createWhere(PurchaseSummaryReport obj, boolean isCentralSupplier, int supplierType) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

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
            where = where + " AND invi.stock_id IN(" + stockList + ") ";
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

        if (isCentralSupplier) {
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

        where += " AND inv.invoicedate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

        where += " AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";

        return where;

    }

    @Override
    public List<PurchaseSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        return purchaseSummaryReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList);
    }

    @Override
    public int count(String where, String branchList) {
        return purchaseSummaryReportDao.count(where, branchList);
    }

    @Override
    public void exportPdf(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        int numberOfColumns = toogleList.size();
        try {
            connection = purchaseSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSummaryReportDao.exportData(where, branchList));
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

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasesummaryreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getEndDate()), pdfDocument.getFont()));
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
            if (purchaseSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (purchaseSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            StaticMethods.createHeaderPdf("frmPurchaseSummaryDatatable:dtbPurchaseSummary", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                Currency currency = new Currency(rs.getInt("invicurrency"));

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
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("inviquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    BigDecimal unitPriceWithOutTax = new BigDecimal(BigInteger.ZERO);

                    unitPriceWithOutTax = rs.getBigDecimal("inviunitprice").divide((BigDecimal.ONE.add(rs.getBigDecimal("invitaxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN);

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(unitPriceWithOutTax) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(11)) {

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("inviunitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("invitotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalcountbystock")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoneybystock")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(15)) {
                    if (rs.getBigDecimal("siturnoverpremium") != null) {
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("siturnoverpremium")), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("siturnoverpremium")) + "%", pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                        }
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                }

                if (toogleList.get(16)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("premiumamount") == null ? " " : sessionBean.getNumberFormat().format(rs.getBigDecimal("premiumamount")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            for (PurchaseSummaryReport total : listOfTotals) {
                pdfDocument.getRightCell().setPhrase(new Phrase((selectedBranchList.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " "
                          + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " "
                          + sessionBean.getLoc().getString("total") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " "
                          + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getTotalQuantityByStock()) + " "
                          + sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoneyByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " "
                          + sessionBean.getLoc().getString("premiumamount") + " : " + sessionBean.getNumberFormat().format(total.getPremiumAmount()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, PurchaseSummaryReport> entry : currencyTotalsCollection.entrySet()) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + "  "
                              + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getQuantity()) + " "
                              + sessionBean.getLoc().getString("total") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " "
                              + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalQuantityByStock()) + " "
                              + sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoneyByStock()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " "
                              + sessionBean.getLoc().getString("premiumamount") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getPremiumAmount()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getRightCell().setColspan(numberOfColumns);
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("purchasesummaryreport"));

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
                Logger.getLogger(PurchaseSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

        try {
            connection = purchaseSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSummaryReportDao.exportData(where, branchList));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("purchasesummaryreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            jRow++;

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getEndDate()));

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
            if (purchaseSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (purchaseSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmPurchaseSummaryDatatable:dtbPurchaseSummary", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            while (rs.next()) {
                Currency currency = new Currency(rs.getInt("invicurrency"));

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
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("inviquantity").doubleValue(), rs.getInt("guntunitsorting")));
                }

                if (toogleList.get(10)) {

                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("inviunitprice").divide((BigDecimal.ONE.add(rs.getBigDecimal("invitaxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("inviunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("invitotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalcountbystock").doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalmoneybystock").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(15)) {
                    if (rs.getBigDecimal("siturnoverpremium") != null) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("siturnoverpremium").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        row.createCell((short) b++).setCellValue("");
                    }

                }

                if (toogleList.get(16)) {
                    row.createCell((short) b++).setCellValue(rs.getBigDecimal("premiumamount") == null ? BigDecimal.ZERO.doubleValue() : StaticMethods.round(rs.getBigDecimal("premiumamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

            }

            for (PurchaseSummaryReport total : listOfTotals) {

                CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                SXSSFRow rowf1 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell e0 = rowf1.createCell((short) 0);
                if (selectedBranchList.size() > 1) {

                    e0.setCellValue(" ( " + total.getBranchSetting().getBranch().getName() + " ) ");

                } else {
                    e0.setCellValue(sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " ");

                }
                e0.setCellStyle(cellStyle2);

                SXSSFCell e1 = rowf1.createCell((short) 1);
                if (selectedBranchList.size() > 1) {

                    e1.setCellValue(sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " ");

                } else {
                    e1.setCellValue(sessionBean.getLoc().getString("total") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                }
                e1.setCellStyle(cellStyle2);

                SXSSFCell e2 = rowf1.createCell((short) 2);
                if (selectedBranchList.size() > 1) {

                    e2.setCellValue(sessionBean.getLoc().getString("total") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                } else {
                    e2.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getTotalQuantityByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                }
                e2.setCellStyle(cellStyle2);

                SXSSFCell e3 = rowf1.createCell((short) 3);
                if (selectedBranchList.size() > 1) {

                    e3.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(total.getTotalQuantityByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                } else {
                    e3.setCellValue(sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoneyByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");
                }
                e3.setCellStyle(cellStyle2);

                SXSSFCell e4 = rowf1.createCell((short) 4);
                if (selectedBranchList.size() > 1) {
                    e4.setCellValue(sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoneyByStock()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");
                } else {
                    e4.setCellValue(sessionBean.getLoc().getString("premiumamount") + " : " + sessionBean.getNumberFormat().format(total.getPremiumAmount()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                }
                e4.setCellStyle(cellStyle2);
                SXSSFCell e5 = rowf1.createCell((short) 5);
                if (selectedBranchList.size() > 1) {

                    e5.setCellValue(sessionBean.getLoc().getString("premiumamount") + " : " + sessionBean.getNumberFormat().format(total.getPremiumAmount()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                } else {
                    e5.setCellValue(" ");

                }
                e5.setCellStyle(cellStyle2);
            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, PurchaseSummaryReport> entry : currencyTotalsCollection.entrySet()) {

                    CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                    cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                    SXSSFRow rowf1 = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell e0 = rowf1.createCell((short) 0);
                    e0.setCellValue(sessionBean.getLoc().getString("sum"));
                    e0.setCellStyle(cellStyle2);

                    SXSSFCell e1 = rowf1.createCell((short) 1);
                    e1.setCellValue(sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getQuantity()) + " ");
                    e1.setCellStyle(cellStyle2);

                    SXSSFCell e2 = rowf1.createCell((short) 2);
                    e2.setCellValue(sessionBean.getLoc().getString("total") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " ");
                    e2.setCellStyle(cellStyle2);

                    SXSSFCell e3 = rowf1.createCell((short) 3);
                    e3.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("purchasedstockquantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalQuantityByStock()) + " ");
                    e3.setCellStyle(cellStyle2);

                    SXSSFCell e4 = rowf1.createCell((short) 4);
                    e4.setCellValue(sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoneyByStock()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " ");
                    e4.setCellStyle(cellStyle2);

                    SXSSFCell e5 = rowf1.createCell((short) 5);
                    e5.setCellValue(sessionBean.getLoc().getString("premiumamount") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getPremiumAmount()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " ");
                    e5.setCellStyle(cellStyle2);

                }

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("purchasesummaryreport"));
            } catch (IOException ex) {
                Logger.getLogger(PurchaseSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(PurchaseSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, PurchaseSummaryReport purchaseSummaryReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, List<PurchaseSummaryReport> listOfTotals, Map<Integer, PurchaseSummaryReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        int numberOfColumns = toogleList.size();

        try {
            connection = purchaseSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSummaryReportDao.exportData(where, branchList));
            rs = prep.executeQuery();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSummaryReport.getEndDate())).append(" </div> ");

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
            if (purchaseSummaryReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSummaryReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSummaryReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSummaryReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSummaryReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (purchaseSummaryReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSummaryReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSummaryReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("supplier")).append(" : ").append(supplierName).append(" </div> ");
            }

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
                      + "        }"
                      + "   @page { size: landscape; }"
                      + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmPurchaseSummaryDatatable:dtbPurchaseSummary", toogleList, "headerBlack", sb);

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                Currency currency = new Currency(rs.getInt("invicurrency"));

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
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("inviquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(10)) {

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("inviunitprice").divide((BigDecimal.ONE.add(rs.getBigDecimal("invitaxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("inviunitprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("invitotalmoney"))))
                              .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("totalcountbystock"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoneybystock"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                if (toogleList.get(15)) {
                    if (rs.getBigDecimal("siturnoverpremium") != null) {
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            sb.append("<td style=\"text-align: right\">").append("%").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("siturnoverpremium"))).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("siturnoverpremium"))).append("%").append("</td>");
                        }
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("").append("</td>");
                    }
                }

                if (toogleList.get(16)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getBigDecimal("premiumamount") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("premiumamount"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
            }

            for (PurchaseSummaryReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                          .append((selectedBranchList.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : ""))
                          .append(" ").append(sessionBean.getLoc().getString("purchasedstockquantity")).append(" : ").append(sessionBean.getNumberFormat().format(total.getQuantity()))
                          .append(" ").append(sessionBean.getLoc().getString("total")).append(" : ").append(sessionBean.getNumberFormat().format(total.getTotalMoney()))
                          .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0))
                          .append(" ").append(sessionBean.getLoc().getString("sum")).append(" ").append(sessionBean.getLoc().getString("purchasedstockquantity")).append(" : ").append(sessionBean.getNumberFormat().format(total.getTotalQuantityByStock()))
                          .append(" ").append(sessionBean.getLoc().getString("totalprice")).append(" : ").append(sessionBean.getNumberFormat().format(total.getTotalMoneyByStock()))
                          .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0))
                          .append(" ").append(sessionBean.getLoc().getString("premiumamount")).append(" : ").append(sessionBean.getNumberFormat().format(total.getPremiumAmount()))
                          .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0)).append("</td>");

                sb.append(" </tr> ");

            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, PurchaseSummaryReport> entry : currencyTotalsCollection.entrySet()) {

                    sb.append(" <tr> ");
                    sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                              .append(sessionBean.getLoc().getString("sum"))
                              .append(" ").append(sessionBean.getLoc().getString("purchasedstockquantity")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getQuantity()))
                              .append(" ").append(sessionBean.getLoc().getString("total")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()))
                              .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0))
                              .append(" ").append(sessionBean.getLoc().getString("sum")).append(" ").append(sessionBean.getLoc().getString("purchasedstockquantity")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalQuantityByStock()))
                              .append(" ").append(sessionBean.getLoc().getString("totalprice")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalMoneyByStock()))
                              .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0))
                              .append(" ").append(sessionBean.getLoc().getString("premiumamount")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getPremiumAmount()))
                              .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0)).append("</td>");

                    sb.append(" </tr> ");

                }
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
                Logger.getLogger(PurchaseSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<PurchaseSummaryReport> findAllDetail(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSummaryReport obj, String branchList) {
        return purchaseSummaryReportDao.findAllDetail(first, pageSize, sortField, sortOrder, filters, where, obj, branchList);
    }

    @Override
    public int countDetail(String where, PurchaseSummaryReport obj, String branchList) {
        return purchaseSummaryReportDao.countDetail(where, obj, branchList);
    }

    @Override
    public List<PurchaseSummaryReport> totals(String where, String branchList) {
        return purchaseSummaryReportDao.totals(where, branchList);
    }

}
