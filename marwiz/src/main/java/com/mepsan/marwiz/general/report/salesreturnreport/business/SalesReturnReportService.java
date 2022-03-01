/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:40:24
 */
package com.mepsan.marwiz.general.report.salesreturnreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.ReceiptReturnReport;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.SalesReturnReportDao;
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
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.salesreturnreport.dao.ISalesReturnReportDao;
import java.awt.Color;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class SalesReturnReportService implements ISalesReturnReportService {

    @Autowired
    private ISalesReturnReportDao receiptReturnReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setReceiptReturnReportDao(ISalesReturnReportDao receiptReturnReportDao) {
        this.receiptReturnReportDao = receiptReturnReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(ReceiptReturnReport obj) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";
        return where;
    }

    @Override
    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String createBranchWhere) {
        return receiptReturnReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, createBranchWhere);
    }

    @Override
    public int count(String where, String createWhereBranch) {
        return receiptReturnReportDao.count(where, createWhereBranch);
    }

    @Override
    public void exportPdf(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals, Map<Integer, ReceiptReturnReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        int numberOfColumns = toogleList.size();
        try {
            connection = receiptReturnReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(receiptReturnReportDao.exportData(where, whereBranchList));
            rs = prep.executeQuery();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesreceiptreturnreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getEndDate()), pdfDocument.getFont()));
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

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmReceiptReturnDatatable:dtbReceiptReturn", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);
            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("rcpid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("rcpreceiptno"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentno"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sliquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                if (toogleList.get(8)) {
                    if (rs.getBigDecimal("stgrate") != null) {
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("stgrate")), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("stgrate")) + "%", pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                        }

                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    }
                }

                if (toogleList.get(9)) {
                    Currency currencyStock = new Currency(rs.getInt("slicurrency_id"));
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalprice")) + sessionBean.currencySignOrCode(currencyStock.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    Currency currencyStock = new Currency(rs.getInt("slicurrency_id"));
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney")) + sessionBean.currencySignOrCode(currencyStock.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            for (ReceiptReturnReport total : listOfTotals) {
                pdfDocument.getRightCell().setPhrase(new Phrase((selectedBranchList.size() > 1 ? " ( " + total.getBranch().getName() + " ) " : "") + " "
                        + sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " "
                        + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withouttax") + " : " + sessionBean.getNumberFormat().format(total.getTotalPrice()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " "
                        + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withtax") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, ReceiptReturnReport> entry : currencyTotalsCollection.entrySet()) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + "  "
                            + sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getQuantity()) + " "
                            + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withouttax") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalPrice()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " "
                            + sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withtax") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getRightCell().setColspan(numberOfColumns);
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("salesreceiptreturnreport"));
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
                Logger.getLogger(SalesReturnReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals, Map<Integer, ReceiptReturnReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = receiptReturnReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(receiptReturnReportDao.exportData(where, whereBranchList));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("salesreceiptreturnreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getEndDate()));

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

            SXSSFRow branch = excelDocument.getSheet().createRow(4);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            SXSSFRow empty4 = excelDocument.getSheet().createRow(5);

            StaticMethods.createHeaderExcel("frmReceiptReturnDatatable:dtbReceiptReturn", toogleList, "headerBlack", excelDocument.getWorkbook());

            int j = 7;

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(j);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("slprocessdate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("rcpid") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("rcpreceiptno"));
                    } else {
                        row.createCell((short) b++).setCellValue(rs.getString("invdocumentno"));
                    }
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(7)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(StaticMethods.round(rs.getBigDecimal("sliquantity").doubleValue(), rs.getInt("guntunitsorting")));
                }

                if (toogleList.get(8)) {
                    SXSSFCell total = row.createCell((short) b++);
                    if (rs.getBigDecimal("stgrate") != null) {
                        total.setCellValue(StaticMethods.round(rs.getBigDecimal("stgrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        total.setCellValue("");
                    }
                }
                if (toogleList.get(9)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(StaticMethods.round(rs.getBigDecimal("slitotalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(10)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(StaticMethods.round(rs.getBigDecimal("slitotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                j++;

            }

            for (ReceiptReturnReport total : listOfTotals) {

                CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                SXSSFCell e0 = rowf1.createCell((short) 0);
                if (selectedBranchList.size() > 1) {

                    e0.setCellValue(" ( " + total.getBranch().getName() + " ) ");

                } else {
                    e0.setCellValue(sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " ");

                }
                e0.setCellStyle(cellStyle2);

                SXSSFCell e1 = rowf1.createCell((short) 1);
                if (selectedBranchList.size() > 1) {

                    e1.setCellValue(sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(total.getQuantity()) + " ");

                } else {
                    e1.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withouttax") + " : " + sessionBean.getNumberFormat().format(total.getTotalPrice()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                }
                e1.setCellStyle(cellStyle2);

                SXSSFCell e2 = rowf1.createCell((short) 2);
                if (selectedBranchList.size() > 1) {

                    e2.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withouttax") + " : " + sessionBean.getNumberFormat().format(total.getTotalPrice()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                } else {
                    e2.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withtax") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                }
                e2.setCellStyle(cellStyle2);

                SXSSFCell e3 = rowf1.createCell((short) 3);
                if (selectedBranchList.size() > 1) {

                    e3.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withtax") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0) + " ");

                } else {
                    e3.setCellValue(" ");

                }
                e3.setCellStyle(cellStyle2);

            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, ReceiptReturnReport> entry : currencyTotalsCollection.entrySet()) {

                    CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                    cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                    SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                    SXSSFCell e0 = rowf1.createCell((short) 0);
                    e0.setCellValue(sessionBean.getLoc().getString("sum"));
                    e0.setCellStyle(cellStyle2);

                    SXSSFCell e1 = rowf1.createCell((short) 1);
                    e1.setCellValue(sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getQuantity()) + " ");
                    e1.setCellStyle(cellStyle2);

                    SXSSFCell e2 = rowf1.createCell((short) 2);
                    e2.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withouttax") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalPrice()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " ");
                    e2.setCellStyle(cellStyle2);

                    SXSSFCell e3 = rowf1.createCell((short) 3);
                    e3.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("withtax") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " ");
                    e3.setCellStyle(cellStyle2);

                }

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("salesreceiptreturnreport"));
            } catch (IOException ex) {
                Logger.getLogger(SalesReturnReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SalesReturnReportDao.class
                        .getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, ReceiptReturnReport receiptReturnReport, List<Boolean> toogleList, String whereBranchList, List<BranchSetting> selectedBranchList, List<ReceiptReturnReport> listOfTotals, Map<Integer, ReceiptReturnReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        int numberOfColumns = toogleList.size();
        StringBuilder sb = new StringBuilder();
        try {

            connection = receiptReturnReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(receiptReturnReportDao.exportData(where, whereBranchList));
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
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), receiptReturnReport.getEndDate())).append(" </div> ");

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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("branch")).append(" : ").append(branchName);

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

            StaticMethods.createHeaderPrint("frmReceiptReturnDatatable:dtbReceiptReturn", toogleList, "headerBlack", sb);
            sb.append(" </tr>  ");

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("slprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    if (rs.getInt("rcpid") > 0) {
                        sb.append("<td>").append(rs.getString("rcpreceiptno") == null ? "" : rs.getString("rcpreceiptno")).append("</td>");
                    } else {
                        sb.append("<td>").append(rs.getString("invdocumentno") == null ? "" : rs.getString("invdocumentno")).append("</td>");
                    }
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("sliquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(8)) {

                    if (rs.getBigDecimal("stgrate") != null) {
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            sb.append("<td style=\"text-align: right\">").append("%").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("stgrate"))).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("stgrate"))).append("%").append("</td>");
                        }
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("").append("</td>");

                    }

                }

                if (toogleList.get(9)) {
                    Currency currencyStock = new Currency(rs.getInt("slicurrency_id"));
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalprice"))).append(sessionBean.currencySignOrCode(currencyStock.getId(), 0)).append("</td>");
                }

                if (toogleList.get(10)) {
                    Currency currencyStock = new Currency(rs.getInt("slicurrency_id"));
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))).append(sessionBean.currencySignOrCode(currencyStock.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");

            }

            for (ReceiptReturnReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                        .append((selectedBranchList.size() > 1 ? " ( " + total.getBranch().getName() + " ) " : ""))
                        .append(" ").append(sessionBean.getLoc().getString("quantity")).append(" : ").append(sessionBean.getNumberFormat().format(total.getQuantity()))
                        .append(" ").append(sessionBean.getLoc().getString("sum")).append(sessionBean.getLoc().getString("withouttax")).append(" : ").append(sessionBean.getNumberFormat().format(total.getTotalPrice()))
                        .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0))
                        .append(" ").append(sessionBean.getLoc().getString("sum")).append(sessionBean.getLoc().getString("withtax")).append(" : ").append(sessionBean.getNumberFormat().format(total.getTotalMoney()))
                        .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0)).append("</td>");

                sb.append(" </tr> ");

            }

            if (selectedBranchList.size() > 1) {
                for (Map.Entry<Integer, ReceiptReturnReport> entry : currencyTotalsCollection.entrySet()) {

                    sb.append(" <tr> ");
                    sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                            .append(sessionBean.getLoc().getString("sum"))
                            .append(" ").append(sessionBean.getLoc().getString("quantity")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getQuantity()))
                            .append(" ").append(sessionBean.getLoc().getString("sum")).append(sessionBean.getLoc().getString("withouttax")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalPrice()))
                            .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0))
                            .append(" ").append(sessionBean.getLoc().getString("sum")).append(sessionBean.getLoc().getString("withtax")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()))
                            .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0));

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
                Logger.getLogger(SalesReturnReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public String createWhereBranch(List<BranchSetting> listOfBranch) {
        String branchList = "";
        for (BranchSetting branchSetting : listOfBranch) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        return branchList;
    }

    @Override
    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ReceiptReturnReport> totals(String where, String whereBranch) {
        return receiptReturnReportDao.totals(where, whereBranch);
    }
}
