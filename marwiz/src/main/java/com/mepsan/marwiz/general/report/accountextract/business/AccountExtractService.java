/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 12:01:19
 */
package com.mepsan.marwiz.general.report.accountextract.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtract;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtractDao;
import com.mepsan.marwiz.general.report.accountextract.dao.IAccountExtractDao;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountExtractService implements IAccountExtractService {

    @Autowired
    private IAccountExtractDao accountExtractDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountExtractDao(IAccountExtractDao accountExtractDao) {
        this.accountExtractDao = accountExtractDao;
    }

    @Override
    public String createWhere(AccountExtract obj, int pageId) {
        String where = "";

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
            where = where + " AND abc.branch_id IN (" + branchList + ")";
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

        String categoryList = "";
        for (Categorization category : obj.getCategorizationList()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }

        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            if (pageId == 102) {
                where = where + " AND acc.id IN (SELECT ect.account_id FROM general.employee_categorization_con ect WHERE ect.deleted=False AND ect.categorization_id IN (" + categoryList + ") )";
            } else {
                where = where + " AND acc.id IN (SELECT act.account_id FROM general.account_categorization_con act WHERE act.deleted=False AND act.categorization_id IN (" + categoryList + ") )";
            }
        }

        if (pageId == 102) {//Personel Ekstre Sayfası İçin
            where += " AND acc.is_employee = True ";
        } else {// Cari Ekstre Sayfası İçin
            where += " AND acc.is_employee = False ";
        }

        return where;
    }

    @Override
    public List<AccountExtract> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AccountExtract accountExtract) {
        return accountExtractDao.findAll(first, pageSize, sortField, sortOrder, filters, where, accountExtract);
    }

    @Override
    public void exportPdf(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = accountExtractDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountExtractDao.exportData(where, sortField, sortOrder, accountExtract));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(pageId == 72 ? sessionBean.getLoc().getString("currentextract") : sessionBean.getLoc().getString("employeeextract"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (accountExtract.getBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getBranchList().get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch1 : accountExtract.getBranchList()) {
                    branchName += " , " + branch1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String accountName = "";
            if (accountExtract.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account s : accountExtract.getAccountList()) {
                    accountName += " , " + s.getName();
                }
                accountName = accountName.substring(3, accountName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase((pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")) + " : " + accountName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (accountExtract.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : accountExtract.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase((pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")) + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (accountExtract.getBalance() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("balance") + "(Min) : " + sessionBean.getNumberFormat().format(accountExtract.getBalance()), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmAccountExtractDatatable:dtbExtract", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("accisemployee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("dept")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("credit")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("balance")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());
            for (AccountExtract accountExtract1 : listOfTotals) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(accountExtract1.getOutComing()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(accountExtract1.getInComing()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(accountExtract1.getBalance()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, pageId == 72 ? sessionBean.getLoc().getString("currentextract") : sessionBean.getLoc().getString("employeeextract"));

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
                Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = accountExtractDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountExtractDao.exportData(where, sortField, sortOrder, accountExtract));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(pageId == 72 ? sessionBean.getLoc().getString("currentextract") : sessionBean.getLoc().getString("employeeextract"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            String branchName = "";
            if (accountExtract.getBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getBranchList().get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch1 : accountExtract.getBranchList()) {
                    branchName += " , " + branch1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }

            SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String accountName = "";
            if (accountExtract.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account s : accountExtract.getAccountList()) {
                    accountName += " , " + s.getName();
                }
                accountName = accountName.substring(3, accountName.length());
            }

            SXSSFRow current = excelDocument.getSheet().createRow(jRow++);
            current.createCell((short) 0).setCellValue((pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")) + " : " + accountName);

            String categoryName = "";
            if (accountExtract.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : accountExtract.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            SXSSFRow category = excelDocument.getSheet().createRow(jRow++);
            category.createCell((short) 0).setCellValue((pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")) + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (accountExtract.getBalance() != null) {
                SXSSFRow copm1 = excelDocument.getSheet().createRow(jRow++);
                copm1.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("balance") + "(Min) : " + StaticMethods.round(accountExtract.getBalance(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            jRow++;
            StaticMethods.createHeaderExcel("frmAccountExtractDatatable:dtbExtract", toogleList, "headerBlack", excelDocument.getWorkbook());

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("accisemployee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"));
                }
                if (toogleList.get(2)) {
                    SXSSFCell cell1 = row.createCell((short) b++);
                    cell1.setCellValue(StaticMethods.round(rs.getBigDecimal("dept").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(3)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(StaticMethods.round(rs.getBigDecimal("credit").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(rs.getBigDecimal("balance").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
            }

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            for (AccountExtract accountExtract1 : listOfTotals) {
                int c = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) c++);
                    cell0.setCellValue("");
                    cell0.setCellStyle(stylefooter);

                }
                if (toogleList.get(1)) {
                    SXSSFCell cell0 = row.createCell((short) c++);
                    cell0.setCellValue(sessionBean.getLoc().getString("sum"));
                    cell0.setCellStyle(stylefooter);

                }
                if (toogleList.get(2)) {
                    SXSSFCell cell1 = row.createCell((short) c++);
                    cell1.setCellValue(StaticMethods.round(accountExtract1.getOutComing().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell1.setCellStyle(stylefooter);
                }
                if (toogleList.get(3)) {
                    SXSSFCell cell2 = row.createCell((short) c++);
                    cell2.setCellValue(StaticMethods.round(accountExtract1.getInComing().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell2.setCellStyle(stylefooter);
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell3 = row.createCell((short) c++);
                    cell3.setCellValue(StaticMethods.round(accountExtract1.getBalance().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell3.setCellStyle(stylefooter);
                }
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), pageId == 72 ? sessionBean.getLoc().getString("currentextract") : sessionBean.getLoc().getString("employeeextract"));
            } catch (IOException ex) {
                Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, AccountExtract accountExtract, List<Boolean> toogleList, int pageId, List<AccountExtract> listOfTotals, String sortField, String sortOrder) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = accountExtractDao.getDatasource().getConnection();
            prep = connection.prepareStatement(accountExtractDao.exportData(where, sortField, sortOrder, accountExtract));
            rs = prep.executeQuery();

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            String branchName = "";
            if (accountExtract.getBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getBranchList().get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branch1 : accountExtract.getBranchList()) {
                    branchName += " , " + branch1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String accountName = "";
            if (accountExtract.getAccountList().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getAccountList().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account s : accountExtract.getAccountList()) {
                    accountName += " , " + s.getName();
                }
                accountName = accountName.substring(3, accountName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")).append(" : ").append(accountName).append(" </div> ");

            String categoryName = "";
            if (accountExtract.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (accountExtract.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : accountExtract.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(pageId == 72 ? sessionBean.getLoc().getString("current") : sessionBean.getLoc().getString("employee")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (accountExtract.getBalance() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("balance")).append("(Min) : ").append(sessionBean.getNumberFormat().format(accountExtract.getBalance())).append(" </div> ");
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
                    + "    </style> <table>");

            StaticMethods.createHeaderPrint("frmAccountExtractDatatable:dtbExtract", toogleList, "headerBlack", sb);

            while (rs.next()) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getBoolean("accisemployee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("dept"))).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("credit"))).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("balance"))).append("</td>");
                }

                sb.append(" </tr> ");

            }

            for (AccountExtract accountExtract1 : listOfTotals) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append("").append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(sessionBean.getLoc().getString("sum")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(sessionBean.getNumberFormat().format(accountExtract1.getOutComing())).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(sessionBean.getNumberFormat().format(accountExtract1.getInComing())).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(sessionBean.getNumberFormat().format(accountExtract1.getBalance())).append("</td>");
                }

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
                Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public int findAccountCount(AccountExtract accountExtract, int pageId) {
        return accountExtractDao.findAccountCount(accountExtract, pageId);

    }

    @Override
    public List<AccountExtract> totals(String where, AccountExtract accountExtract) {
        return accountExtractDao.totals(where, accountExtract);
    }

}
