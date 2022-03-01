package com.mepsan.marwiz.general.report.deficitcreditreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.credit.dao.ICreditDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import com.mepsan.marwiz.general.report.deficitcreditreport.dao.IDeficitCreditDao;
import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samet Dağ
 */
public class DeficitCreditService implements IDeficitCreditService {

    @Autowired
    private ICreditDao creditDao;

    @Autowired
    private IDeficitCreditDao deficitCreditReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setDeficitCreditReportDao(IDeficitCreditDao deficitCreditReportDao) {
        this.deficitCreditReportDao = deficitCreditReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCreditDao(ICreditDao creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    public List<CreditReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return creditDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public String createWhere(CreditReport obj) {
        String where = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String branchList = "";
        for (Branch bs : obj.getBranchList()) {
            branchList = branchList + "," + String.valueOf(bs.getId());
            if (bs.getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND crdt.branch_id IN (" + branchList + ")";
        }

        String accountList = "";
        for (Account account : obj.getAccountList()) {
            accountList = accountList + "," + String.valueOf(account.getId());
            if (account.getId() == 0) {
                accountList = "";
                break;
            }
        }
        if (!accountList.equals("")) {
            accountList = accountList.substring(1, accountList.length());
            where = where + " AND acc.id IN(" + accountList + ") ";
        }

        where = where + " AND crdt.is_invoice=FALSE AND crdt.is_cancel=FALSE\n "
                + " AND crdt.processdate BETWEEN '" + dateFormat.format(obj.getBeginDate())
                + "' AND '" + dateFormat.format(obj.getEndDate()) + "'";
        return where;
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = deficitCreditReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(deficitCreditReportDao.exportData(where));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("deficitcreditreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = " "; // Branch Üst Bilgisi
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch : selectedBranchList) {
                    branchName += " , " + branch.getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String accountName = " "; // Cari Üst bilgisi 
            if (deficitCredit.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (deficitCredit.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account : deficitCredit.getAccountList()) {
                    accountName += "," + account.getName();
                }
                accountName = accountName.substring(3, accountName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("account") + " : " + accountName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmDeficitCreditDatatable:dtbDeficitCredit", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                Currency currency = new Currency(rs.getInt("crcurrency_id"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("crdtprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("crdtis_customer") == true ? sessionBean.getLoc().getString("collection") : sessionBean.getLoc().getString("payment"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("crdtduedate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("" + sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("" + sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney").subtract(rs.getBigDecimal("crdtremainingmoney"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("" + sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtremainingmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("crdtis_cancel") == false ? (rs.getBoolean("crdtis_paid") == true ? sessionBean.getLoc().getString("itwaspaid") : sessionBean.getLoc().getString("unpaid")) : sessionBean.getLoc().getString("canceled"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalcollectionamount") + " : " + totalMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("amountcollectedontheball") + " : " + paidMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingcollectionamount") + " : " + remainingMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("deficitcreditreport"));

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
                ex1.printStackTrace();
            }
        }
    }

    @Override
    public void exportExcel(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + "HH:mm:ss");
        try {
            connection = deficitCreditReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(deficitCreditReportDao.exportData(where));
            rs = prep.executeQuery();

            CellStyle styleRight = excelDocument.getWorkbook().createCellStyle();
            styleRight.setAlignment(HorizontalAlignment.RIGHT);

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("deficitcreditreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            jRow++;

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            String branchName = "";//Branch Üst Bilgisi
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch : selectedBranchList) {
                    branchName += " , " + branch.getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String accountName = "";// Cari Üst Bilgisi
            if (deficitCredit.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (deficitCredit.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account : deficitCredit.getAccountList()) {
                    accountName += " , " + account.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            SXSSFRow accName = excelDocument.getSheet().createRow(jRow++);
            accName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("account") + " : " + accountName);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getEndDate()));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmDeficitCreditDatatable:dtbDeficitCredit", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("crdtprocessdate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("crdtis_customer") == true ? sessionBean.getLoc().getString("collection") : sessionBean.getLoc().getString("payment"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell1 = row.createCell((short) b++);
                    cell1.setCellValue(rs.getTimestamp("crdtduedate"));
                    cell1.setCellStyle(excelDocument.getDateFormatStyle());
                }

                if (toogleList.get(5)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney")));
                    cell2.setCellStyle(styleRight);
                }
                if (toogleList.get(6)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney").subtract(rs.getBigDecimal("crdtremainingmoney"))));
                    cell3.setCellStyle(styleRight);
                }
                if (toogleList.get(7)) {
                    SXSSFCell cell4 = row.createCell((short) b++);
                    cell4.setCellValue(sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtremainingmoney")));
                    cell4.setCellStyle(styleRight);
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("crdtis_cancel") == false ? (rs.getBoolean("crdtis_paid") == true
                            ? sessionBean.getLoc().getString("itwaspaid") : sessionBean.getLoc().getString("unpaid"))
                            : sessionBean.getLoc().getString("canceled"));
                }
            }

            jRow++;
            SXSSFRow rowEmpty1 = excelDocument.getSheet().createRow(jRow++);

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(sessionBean.getLoc().getString("totalcollectionamount") + " : " + totalMoney);
            cell.setCellStyle(stylefooter);

            SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell1 = row1.createCell((short) 0);
            cell1.setCellValue(sessionBean.getLoc().getString("amountcollectedontheball") + " : " + paidMoney);
            cell1.setCellStyle(stylefooter);

            SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell2 = row2.createCell((short) 0);
            cell2.setCellValue(sessionBean.getLoc().getString("remainingcollectionamount") + " : " + remainingMoney);
            cell2.setCellStyle(stylefooter);

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("deficitcreditreport"));
            } catch (IOException ex) {
                Logger.getLogger(DeficitCreditService.class.getName()).log(Level.SEVERE, null, ex);
            }
//            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("deficitcreditreport"));

        } catch (Exception e) {
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
            }
        }

    }

    @Override
    public String exportPrinter(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney) {
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
            connection = deficitCreditReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(deficitCreditReportDao.exportData(where));
            rs = prep.executeQuery();

            sb.append(" <style>"
                    + "        #printerDiv table {"
                    + "            font-family: arial, sans-serif;"
                    + "            border-collapse: collapse;"
                    + "            width: 100%;"
                    + "        }"
                    + "        #printerDiv table tr td, #printerDiv table tr th {"
                    + "            border: 1px solid #dddddd;"
                    + "            text-align: left;"
                    + "            padding: 2px;"
                    + "        }"
                    + "    </style> <table>");

            String branchName = "";// Branch Üst Bilgisi
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch : selectedBranchList) {
                    branchName += " , " + branch.getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String accountName = "";// Cari Üst Bilgisi
            if (deficitCredit.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (deficitCredit.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account : deficitCredit.getAccountList()) {
                    accountName += " , " + account.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("account")).append(" : ").append(accountName).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), deficitCredit.getEndDate())).append(" </div> ");

            StaticMethods.createHeaderPrint("frmDeficitCreditDatatable:dtbDeficitCredit", toogleList, "headerBlack", sb);

            while (rs.next()) {

                Currency currency = new Currency(rs.getInt("crcurrency_id"));
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("crdtprocessdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getBoolean("crdtis_customer") == true ? sessionBean.getLoc().getString("collection") : sessionBean.getLoc().getString("payment")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("crdtduedate"))).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append("" + sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append("" + sessionBean.getNumberFormat().format(rs.getBigDecimal("crdtmoney").subtract(rs.getBigDecimal("crdtremainingmoney"))) + sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append("" + sessionBean.  getNumberFormat().format(rs.getBigDecimal("crdtremainingmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getBoolean("crdtis_cancel") == false ? (rs.getBoolean("crdtis_paid") == true ? sessionBean.getLoc().getString("itwaspaid") : sessionBean.getLoc().getString("unpaid")) : sessionBean.getLoc().getString("canceled")).append("</td>");
                }
                sb.append(" </tr> ");
            }

            Currency currency1 = new Currency("crcurrency_id");
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalcollectionamount")).append(" ").append(" : ")
                    .append(totalMoney).append(sessionBean.currencySignOrCode(currency1.getId(), 0))
                    .append("</td>");
            sb.append(" </tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("amountcollectedontheball")).append(" ").append(" : ")
                    .append(paidMoney)
                    .append("</td>");
            sb.append(" </tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("remainingcollectionamount")).append(" ").append(" : ")
                    .append(remainingMoney)
                    .append("</td>");
            sb.append(" </tr> ");

            sb.append(" </table> ");
        } catch (SQLException e) {
            e.printStackTrace();
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
                ex1.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public List<CreditReport> totals(String where) {
        return deficitCreditReportDao.totals(where);
    }

}
