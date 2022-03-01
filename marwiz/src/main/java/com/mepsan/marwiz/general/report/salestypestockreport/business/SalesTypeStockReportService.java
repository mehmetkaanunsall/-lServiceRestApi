/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:41:27
 */
package com.mepsan.marwiz.general.report.salestypestockreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salestypestockreport.dao.SalesTypeStockReport;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.salestypestockreport.dao.ISalesTypeStockReportDao;
import com.mepsan.marwiz.general.report.salestypestockreport.dao.SalesTypeStockReportDao;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;

public class SalesTypeStockReportService implements ISalesTypeStockReportService {

    @Autowired
    private ISalesTypeStockReportDao salesTypeStockReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setSalesTypeStockReportDao(ISalesTypeStockReportDao salesTypeStockReportDao) {
        this.salesTypeStockReportDao = salesTypeStockReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(SalesTypeStockReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesTypeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList) {
        return salesTypeStockReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, salesTypeStockReport, whereBranchList);
    }

    @Override
    public List<SalesTypeStockReport> totals(String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList) {
        return salesTypeStockReportDao.totals(where, salesTypeStockReport, whereBranchList);
    }

    @Override
    public List<SalesTypeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportPdf(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {

            connection = salesTypeStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesTypeStockReportDao.exportData(where, salesTypeStockReport, whereBranchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("paymenttypereport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("paymenttype") + " : " + (salesTypeStockReport.getType().getId() == 0 ? sessionBean.getLoc().getString("all") : salesTypeStockReport.getType().getId() == -1 ? sessionBean.getLoc().getString("common") : salesTypeStockReport.getType().getId() == -2 ? sessionBean.getLoc().getString("open") : salesTypeStockReport.getType().getTag()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (salesTypeStockReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (salesTypeStockReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : salesTypeStockReport.getSelectedBranchList()) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmCreditCardReportDatatable:dtbCreditCardReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //Birim iiçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

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
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")) + sessionBean.currencySignOrCode(rs.getInt("slcurrency_id"), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            for (SalesTypeStockReport total : listOfTotals) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranch().getName() + " ) " : "") + " " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("paymenttypereport"));
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
                Logger.getLogger(SalesTypeStockReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = salesTypeStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesTypeStockReportDao.exportData(where, salesTypeStockReport, whereBranchList));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("paymenttypereport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getEndDate()));

            SXSSFRow salestype = excelDocument.getSheet().createRow(4);
            salestype.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("paymenttype") + " : " + (salesTypeStockReport.getType().getId() == 0 ? sessionBean.getLoc().getString("all") : salesTypeStockReport.getType().getId() == -1 ? sessionBean.getLoc().getString("common") : salesTypeStockReport.getType().getId() == -2 ? sessionBean.getLoc().getString("open") : salesTypeStockReport.getType().getTag()));

            String branchName = "";
            if (salesTypeStockReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (salesTypeStockReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : salesTypeStockReport.getSelectedBranchList()) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(4);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            SXSSFRow rwempty = excelDocument.getSheet().createRow(5);

            StaticMethods.createHeaderExcel("frmCreditCardReportDatatable:dtbCreditCardReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            int i = 7;

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

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
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalquantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                i++;
            }

            for (SalesTypeStockReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranch().getName() + " ) " : "") + " "
                        + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("paymenttypereport"));
            } catch (IOException ex) {
                Logger.getLogger(SalesTypeStockReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SalesTypeStockReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, SalesTypeStockReport salesTypeStockReport, List<Boolean> toogleList, List<SalesTypeStockReport> listOfTotals, String whereBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = salesTypeStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesTypeStockReportDao.exportData(where, salesTypeStockReport, whereBranchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithMinute(sessionBean.getUser().getLastBranch().getDateFormat(), salesTypeStockReport.getEndDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("paymenttype")).append(" : ").append(salesTypeStockReport.getType().getId() == 0 ? sessionBean.getLoc().getString("all") : salesTypeStockReport.getType().getId() == -1 ? sessionBean.getLoc().getString("common") : salesTypeStockReport.getType().getId() == -2 ? sessionBean.getLoc().getString("open") : salesTypeStockReport.getType().getTag()).append(" </div> ");

            String branchName = "";
            if (salesTypeStockReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (salesTypeStockReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : salesTypeStockReport.getSelectedBranchList()) {
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
                    + "    </style> <table> ");

            StaticMethods.createHeaderPrint("frmCreditCardReportDatatable:dtbCreditCardReport", toogleList, "headerBlack", sb);

            //Birim iiçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

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
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("totalquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("slcurrency_id"), 0)).append("</td>");
                }

                sb.append(" </tr> ");

            }
            for (SalesTypeStockReport total : listOfTotals) {

                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append((listOfTotals.size() > 1 ? " (" + total.getBranch().getName() + ") " : "")).append(" ").append(sessionBean.getNumberFormat().format(total.getTotalMoney()))
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
                Logger.getLogger(SalesTypeStockReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public String createWhereForBranch(List<BranchSetting> listOfBranch) {
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

}
