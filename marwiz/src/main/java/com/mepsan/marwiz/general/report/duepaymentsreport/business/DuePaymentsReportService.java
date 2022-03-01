/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.duepaymentsreport.dao.DuePaymentsReport;
import com.mepsan.marwiz.general.report.duepaymentsreport.dao.IDuePaymentsReportDao;
import com.mepsan.marwiz.general.report.salesdetailreport.business.SalesDetailReportService;
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
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;

/**
 *
 * @author ebubekir.buker
 */
public class DuePaymentsReportService implements IDuePaymentsReportService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    IDuePaymentsReportDao duePaymentsReportDao;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IDuePaymentsReportDao getDuePaymentsReportDao() {
        return duePaymentsReportDao;
    }

    public void setDuePaymentsReportDao(IDuePaymentsReportDao duePaymentsReportDao) {
        this.duePaymentsReportDao = duePaymentsReportDao;
    }

    @Override
    public List<DuePaymentsReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, DuePaymentsReport duePaymentsReport) {
        return duePaymentsReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, whereBranch, duePaymentsReport);

    }

    @Override
    public List<DuePaymentsReport> totals(String where) {
        return duePaymentsReportDao.totals(where);

    }

    @Override
    public String createWhere(DuePaymentsReport obj) {
        String where = "";

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        where += " AND inv.duedate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

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
            where = where + " AND inv.account_id IN(" + accountList + ") ";
        }

        if (obj.getInvoiceType() == 0) {
            where = where + " AND inv.is_purchase = TRUE";
        } else if (obj.getInvoiceType() == 1) {
            where = where + " AND inv.is_purchase = FALSE";
        } else { //Hiçbir şey yapma

        }
        String branchList = "";
        for (BranchSetting branch : obj.getSelectedBranchList()) {
            branchList = branchList + "," + String.valueOf(branch.getBranch().getId());
            if (branch.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND inv.branch_id IN(" + branchList + ") ";
        }

        return where;
    }

    @Override
    public void exportPdf(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, String totalRemainingMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {

            int jRow = 0;
            connection = duePaymentsReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(duePaymentsReportDao.exportData(where, branchList, duePaymentsReport));

            ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + "HH:mm:ss");
            rs = prep.executeQuery();
            
            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("duepaymentsreport"), pdfDocument.getFontHeader()));

            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getEndDate()), pdfDocument.getFont()));
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

            String accountName = "";
            if (duePaymentsReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : duePaymentsReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("account") + " : " + accountName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String invoice = "";
            if (duePaymentsReport.getInvoiceType() == 1) {
                invoice = sessionBean.getLoc().getString("salesinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 0) {
                invoice = sessionBean.getLoc().getString("purchaseinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 2) {
                invoice = sessionBean.getLoc().getString("all");
            }
            //sessionBean.getLoc().getString("invoice") + " : " + invoice
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("invoice") + " : " + invoice, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmDuePaymentsReportDatatable:dtbDuePaymentsReport", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentserial") + rs.getString("invdocumentnumber"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invduedate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("invremainingmoney")) + sessionBean.currencySignOrCode(rs.getInt("crnid"), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("total") + " : " + totalRemainingMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            
            
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("duepaymentsreport"));

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
                Logger.getLogger(DuePaymentsReport.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

    }

    @Override
    public void exportExcel(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList ,String totalRemainingMoney) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = duePaymentsReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(duePaymentsReportDao.exportData(where, branchList, duePaymentsReport));

            rs = prep.executeQuery();
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("duepaymentsreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getEndDate()));

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

            String accountName = "";
            if (duePaymentsReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : duePaymentsReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            SXSSFRow accName = excelDocument.getSheet().createRow(jRow++);
            accName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("account") + " : " + accountName);

            //          String invoice;
//            if(duePaymentsReport.isIsPurchase()==true)
//            {
//                invoice=sessionBean.getLoc().getString("salesinvoice");
//            }
//            else
//            {
//                invoice =sessionBean.getLoc().getString("purchaseinvoice");
//            } 
            String invoice = "";
            if (duePaymentsReport.getInvoiceType() == 1) {
                invoice = sessionBean.getLoc().getString("salesinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 0) {
                invoice = sessionBean.getLoc().getString("purchaseinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 2) {
                invoice = sessionBean.getLoc().getString("all");
            }

            SXSSFRow invName = excelDocument.getSheet().createRow(jRow++);
            invName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("invoice") + " : " + invoice);

            SXSSFRow empty5 = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmDuePaymentsReportDatatable:dtbDuePaymentsReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));

                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));

                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("invdocumentserial") + rs.getString("invdocumentnumber"));

                }

                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate")));

                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invduedate")));

                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getBigDecimal("invremainingmoney").doubleValue());

                }

            }
            
            jRow++;
            SXSSFRow rowEmpty1 = excelDocument.getSheet().createRow(jRow++);

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(sessionBean.getLoc().getString("total") + " : " + totalRemainingMoney);
            cell.setCellStyle(stylefooter);

            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("duepaymentsreport"));
            } catch (IOException ex) {
                Logger.getLogger(DuePaymentsReport.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DuePaymentsReport.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(DuePaymentsReport.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList ,String totalRemainingMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        
         int numberOfColumns = 0;

        for (boolean b : toogleList) {
            if (b) {
                numberOfColumns++;
            }
        }


        try {
            connection = duePaymentsReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(duePaymentsReportDao.exportData(where, branchList, duePaymentsReport));
            rs = prep.executeQuery();

//             NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
//
//            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
//            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
//            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
//            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
//            decimalFormatSymbolsUnit.setCurrencySymbol("");
//            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);
            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), duePaymentsReport.getEndDate())).append(" </div> ");

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

            String accountName = "";
            if (duePaymentsReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : duePaymentsReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("account")).append(" : ").append(accountName).append(" </div> ");

//            String invoice;
//            if(duePaymentsReport.isIsPurchase()==true)
//            {
//                invoice=sessionBean.getLoc().getString("salesinvoice");
//            }
//            else
//            {
//              invoice =sessionBean.getLoc().getString("purchaseinvoice");
//            }   
            String invoice = "";
            if (duePaymentsReport.getInvoiceType() == 1) {
                invoice = sessionBean.getLoc().getString("salesinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 0) {
                invoice = sessionBean.getLoc().getString("purchaseinvoice");
            }
            if (duePaymentsReport.getInvoiceType() == 2) {
                invoice = sessionBean.getLoc().getString("all");
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("invoice")).append(" : ").append(invoice).append(" </div> ");

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

            StaticMethods.createHeaderPrint("frmDuePaymentsReportDatatable:dtbDuePaymentsReport", toogleList, "headerBlack", sb);
            sb.append(" </tr>  ");

            while (rs.next()) {
                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("invdocumentserial") + rs.getString("invdocumentnumber") == null ? "" : rs.getString("invdocumentserial") + rs.getString("invdocumentnumber")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getTimestamp("invinvoicedate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate"))).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getTimestamp("invduedate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invduedate"))).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("invremainingmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("crnid"), 0)).append("</td>");
                }
                sb.append(" </tr> ");
            }
            
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("total")).append(" ").append(" : ").append(totalRemainingMoney);
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
                Logger.getLogger(DuePaymentsReport.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

        return sb.toString();

    }

    @Override
    public List<DuePaymentsReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {

        return duePaymentsReportDao.count(where);
    }

}
