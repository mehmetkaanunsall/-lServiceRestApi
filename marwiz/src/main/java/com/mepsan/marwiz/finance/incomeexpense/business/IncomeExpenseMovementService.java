/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.finance.incomeexpense.dao.IIncomeExpenseMovementDao;
import com.mepsan.marwiz.finance.incomeexpense.dao.IncomeExpenseMovementDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseMovementService implements IIncomeExpenseMovementService {

    @Autowired
    private IIncomeExpenseMovementDao incomeExpenseMovementDao;

    @Autowired
    private SessionBean sessionBean;

    public void setIncomeExpenseMovementDao(IIncomeExpenseMovementDao incomeExpenseMovementDao) {
        this.incomeExpenseMovementDao = incomeExpenseMovementDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IncomeExpenseMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, IncomeExpense incomeExpense, Date beginDate, Date endDate) {
        return incomeExpenseMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, incomeExpense, beginDate, endDate);
    }

    @Override
    public List<IncomeExpenseMovement> totals(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate) {
        return incomeExpenseMovementDao.totals(where, incomeExpense, beginDate, endDate);
    }

    @Override
    public void exportPdf(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = incomeExpenseMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(incomeExpenseMovementDao.exportData(createWhere, incomeExpense, beginDate, endDate));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("movements"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("tbvMovement:frmMovementDataTable:dtbMovement", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fiemmovementdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    if (rs.getInt("fdocid") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("transferbalance"), pdfDocument.getFont()));
                    } else if (rs.getInt("fdocid") > 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdocumentnumber"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fdocdescription"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("typdname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    Currency currencyMovement = new Currency(rs.getInt("fiemcurrency_id"));
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("fiemprice")) + sessionBean.currencySignOrCode(currencyMovement.getId(), 0), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            for (IncomeExpenseMovement total : listOfTotals) {
                if (total.getCurrency().getId() != 0) {
                    pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(total.getPrice()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                    pdfDocument.getCell().setColspan(toogleList.size());
                    pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                }
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("movements"));

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
                Logger.getLogger(IncomeExpenseMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = incomeExpenseMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(incomeExpenseMovementDao.exportData(createWhere, incomeExpense, beginDate, endDate));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("movements"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            int i = 1;
            SXSSFRow empty = excelDocument.getSheet().createRow(i);
            i++;

            SXSSFRow startdate = excelDocument.getSheet().createRow(i);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate));
            i++;
            SXSSFRow enddate = excelDocument.getSheet().createRow(i);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), endDate));
            i++;    

            SXSSFRow rwempty = excelDocument.getSheet().createRow(i);
            i++;

            StaticMethods.createHeaderExcel("tbvMovement:frmMovementDataTable:dtbMovement", toogleList, "headerBlack", excelDocument.getWorkbook());

            i++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);
                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("fiemmovementdate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    if (rs.getInt("fdocid") == 0) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("transferbalance"));

                    } else if (rs.getInt("fdocid") > 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("fdocdocumentnumber"));

                    }
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("fdocdescription"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("typdname"));
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(rs.getBigDecimal("fiemprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                

                i++;
            }
            for (IncomeExpenseMovement total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getPrice(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }


            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("movements"));
            } catch (IOException ex) {
                Logger.getLogger(IncomeExpenseMovementService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(IncomeExpenseMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String createWhere, List<Boolean> toogleList, Date beginDate, Date endDate, IncomeExpense incomeExpense, List<IncomeExpenseMovement> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = incomeExpenseMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(incomeExpenseMovementDao.exportData(createWhere, incomeExpense, beginDate, endDate));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), endDate)).append(" </div> ");
           
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

            StaticMethods.createHeaderPrint("tbvMovement:frmMovementDataTable:dtbMovement", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            
            while (rs.next()) {
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("fiemmovementdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("fiemmovementdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    if (rs.getInt("fdocid") == 0) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("transferbalance")).append("</td>");
                    } else if (rs.getInt("fdocid") > 0) {
                        sb.append("<td>").append(rs.getString("fdocdocumentnumber") == null ? "" : rs.getString("fdocdocumentnumber")).append("</td>");
                    }
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("fdocdescription") == null ? "" : rs.getString("fdocdescription")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("typdname") == null ? "" : rs.getString("typdname")).append("</td>");                   
                }
                if (toogleList.get(4)) {
                    Currency currencyMovement = new Currency(rs.getInt("fiemcurrency_id"));
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("fiemprice"))).append(sessionBean.currencySignOrCode(currencyMovement.getId(), 0)).append("</td>");                  
                }

                sb.append(" </tr> ");

            }
            for (IncomeExpenseMovement total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(total.getPrice()))
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
                Logger.getLogger(IncomeExpenseMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public int create(IncomeExpenseMovement obj) {
        return incomeExpenseMovementDao.create(obj);
    }

    @Override
    public int update(IncomeExpenseMovement obj) {
        return incomeExpenseMovementDao.update(obj);
    }

}
