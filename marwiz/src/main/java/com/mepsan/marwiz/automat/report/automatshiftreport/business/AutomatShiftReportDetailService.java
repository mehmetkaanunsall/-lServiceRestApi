/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:40:05
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReportDetailDao;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.IAutomatShiftReportDetailDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomatShiftReportDetailService implements IAutomatShiftReportDetailService {

    @Autowired
    public IAutomatShiftReportDetailDao automatShiftReportDetailDao;

    @Autowired
    private SessionBean sessionBean;

    public void setAutomatShiftReportDetailDao(IAutomatShiftReportDetailDao automatShiftReportDetailDao) {
        this.automatShiftReportDetailDao = automatShiftReportDetailDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomatSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AutomatShift shift) {
        return automatShiftReportDetailDao.findAll(first, pageSize, sortField, sortOrder, filters, where, shift);
    }

    @Override
    public List<AutomatSales> totals(String where, AutomatShift shift) {
        return automatShiftReportDetailDao.totals(where, shift);
    }

    @Override
    public List<AutomatSales> find(AutomatSales obj) {
        return automatShiftReportDetailDao.find(obj);
    }

    @Override
    public void exportPdf(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        int numberOfColumns = toogleList.size();

        try {
            connection = automatShiftReportDetailDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDetailDao.exportData(shift, where));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("detailshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + shift.getShiftNo(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            Currency currency = new Currency();

            while (rs.next()) {

                currency = new Currency(rs.getInt("aslcurrency_id"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("aslsaledatetime")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslplatformno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("wshname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslmacaddress"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    if (rs.getInt("aslpaymenttype_id") == 1) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cash"), pdfDocument.getFont()));
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("barcode"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("mobilepayment"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaldiscount")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            for (AutomatSales listOfTotal : listOfTotals) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : "
                        + sessionBean.getNumberFormat().format(listOfTotal.getTotalMoney()) + sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("detailshiftreport"));

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
                Logger.getLogger(AutomatShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = automatShiftReportDetailDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDetailDao.exportData(shift, where));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("detailshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow shiftNo = excelDocument.getSheet().createRow(jRow++);
            shiftNo.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("shiftno") + " : " + shift.getShiftNo());
            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;
            CellStyle cellStyleNumber = excelDocument.getWorkbook().createCellStyle();
            cellStyleNumber.setAlignment(HorizontalAlignment.RIGHT);
            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("aslsaledatetime"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }

                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslplatformno"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("wshname"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslmacaddress"));
                }

                if (toogleList.get(4)) {
                    if (rs.getInt("aslpaymenttype_id") == 1) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("cash"));
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("barcode"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("mobilepayment"));
                    }
                }
                if (toogleList.get(5)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotaldiscount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(rs.getInt("aslcurrency_id"), 0));
                    cell0.setCellStyle(cellStyleNumber);
                }
                if (toogleList.get(6)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(rs.getInt("aslcurrency_id"), 0));
                    cell0.setCellStyle(cellStyleNumber);

                }
                if (toogleList.get(7)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(rs.getInt("aslcurrency_id"), 0));
                    cell0.setCellStyle(cellStyleNumber);

                }
                if (toogleList.get(8)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(rs.getInt("aslcurrency_id"), 0));
                    cell0.setCellStyle(cellStyleNumber);

                }

            }

            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            for (AutomatSales total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0));
                cell.setCellStyle(cellStyle);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("detailshiftreport"));
            } catch (IOException ex) {
                Logger.getLogger(AutomatShiftReportDetailService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(AutomatShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(AutomatShift shift, List<Boolean> toogleList, List<AutomatSales> listOfTotals, String where) {
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
            connection = automatShiftReportDetailDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDetailDao.exportData(shift, where));
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
                    + "            padding: 8px;"
                    + "        }"
                    + "    </style> <table>");

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("shiftno")).append(" : ").append(shift.getShiftNo()).append(" </div> ");
            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            StaticMethods.createHeaderPrint("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", sb);

            Currency currency = new Currency();
            int i = 0;

            while (rs.next()) {
                sb.append(" <tr> ");

                currency = new Currency(rs.getInt("aslcurrency_id"));

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("aslsaledatetime") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("aslsaledatetime"))).append("</td>");
                }

                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("aslplatformno") == null ? "" : rs.getString("aslplatformno")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("wshname") == null ? "" : rs.getString("wshname")).append("</td>");
                }

                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("aslmacaddress") == null ? "" : rs.getString("aslmacaddress")).append("</td>");
                }
                if (toogleList.get(4)) {
                    if (rs.getInt("aslpaymenttype_id") == 1) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("cash")).append("</td>");
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("barcode")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("mobilepayment")).append("</td>");
                    }
                }
                if (toogleList.get(5)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaldiscount"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaltax"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
                i++;
            }

            for (AutomatSales listOfTotal : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(listOfTotal.getTotalMoney()))
                        .append(sessionBean.currencySignOrCode(listOfTotal.getCurrency().getId(), 0)).append("</td>");
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
                Logger.getLogger(AutomatShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

}
