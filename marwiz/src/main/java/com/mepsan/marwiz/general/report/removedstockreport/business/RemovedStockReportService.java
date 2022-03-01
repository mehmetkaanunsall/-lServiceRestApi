/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 09:14:00
 */
package com.mepsan.marwiz.general.report.removedstockreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salesreturnreport.business.SalesReturnReportService;
import com.mepsan.marwiz.general.report.removedstockreport.dao.IRemovedStockReportDao;
import com.mepsan.marwiz.general.report.removedstockreport.dao.RemovedStockReportDao;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedStockReport;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class RemovedStockReportService implements IRemovedStockReportService {

    @Autowired
    private IRemovedStockReportDao removedStockReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setRemovedStockReportDao(IRemovedStockReportDao removedStockReportDao) {
        this.removedStockReportDao = removedStockReportDao;
    }

    @Override
    public List<RemovedStockReport> listOfMonthlyLog(Date date, String branchList) {
        return removedStockReportDao.listOfMonthlyLog(date, branchList);
    }

    @Override
    public List<RemovedStock> listOfLog(Date beginDate, Date endDate, UserData userData, Branch branch) {
        return removedStockReportDao.listOfLog(beginDate, endDate, userData, branch);
    }

    @Override
    public List<ChartItem> yearlyRemovedStock(Date date, String branchList) {
        return removedStockReportDao.yearlyRemovedStock(date,branchList);
    }

    @Override
    public List<ChartItem> monthlyRemovedStock(Date beginDate, Date EndDate, String branchList) {
        return removedStockReportDao.monthlyRemovedStock(beginDate, EndDate,branchList);
    }

    @Override
    public List<Shift> listOfShift(Date date) {
        return removedStockReportDao.listOfShift(date);
    }

    @Override
    public List<ChartItem> dailyRemovedStock(String branchList) {
        return removedStockReportDao.dailyRemovedStock(branchList);
    }

    @Override
    public List<ChartItem> weeklyRemovedStock(Date beginDate, Date EndDate, String branchList) {
        return removedStockReportDao.weeklyRemovedStock(beginDate, EndDate, branchList);
    }

    @Override
    public void exportPdf(Date beginDate, Date endDate, UserData userData, List<Boolean> toggleList, Branch branch) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = removedStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(removedStockReportDao.exportData());
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
            prep.setTimestamp(2, new Timestamp(beginDate.getTime()));
            prep.setTimestamp(3, new Timestamp(endDate.getTime()));
            prep.setInt(4, userData.getId());
            prep.setInt(5, branch.getId());
            rs = prep.executeQuery();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toggleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("removedstockreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("employee") + " : " + userData.getFullName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmInfo:dtbRemoveStock", toggleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);
            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("untunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("untunitrounding"));

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("usname") == null ? "" : rs.getString("usname") + " " + rs.getString("ussurname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("shfshiftno") == null ? "" : rs.getString("shfshiftno"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("rsprocessdate")), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode") == null ? "" : rs.getString("stckcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname") == null ? "" : rs.getString("stckname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("rsoldvalue")) + rs.getString("untsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("rsnewvalue")) + rs.getString("untsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("rsremovedvalue")) + rs.getString("untsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                Currency currencyStock = new Currency(rs.getInt("rscurrency_id"));
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("rsunitprice")) + sessionBean.currencySignOrCode(currencyStock.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("rsremovedtotalprice")) + sessionBean.currencySignOrCode(currencyStock.getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("removedstockreport"));
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
                Logger.getLogger(RemovedStockReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(Date beginDate, Date endDate, UserData userData, List<Boolean> toggleList, Branch branch) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = removedStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(removedStockReportDao.exportData());
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
            prep.setTimestamp(2, new Timestamp(beginDate.getTime()));
            prep.setTimestamp(3, new Timestamp(endDate.getTime()));
            prep.setInt(4, userData.getId());
            prep.setInt(5, branch.getId());

            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("removedstockreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate));

            SXSSFRow employe = excelDocument.getSheet().createRow(4);
            employe.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("employee") + " : " + userData.getFullName());

            SXSSFRow empty4 = excelDocument.getSheet().createRow(5);

            StaticMethods.createHeaderExcel("frmInfo:dtbRemoveStock", toggleList, "headerBlack", excelDocument.getWorkbook());

            int j = 7;

            while (rs.next()) {

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(j);

                row.createCell((short) b++).setCellValue(rs.getString("usname") == null ? "" : rs.getString("usname") + " " + rs.getString("ussurname"));

                row.createCell((short) b++).setCellValue(rs.getString("shfshiftno") == null ? "" : rs.getString("shfshiftno"));

                SXSSFCell cell0 = row.createCell((short) b++);
                cell0.setCellValue(rs.getTimestamp("rsprocessdate"));
                cell0.setCellStyle(excelDocument.getDateFormatStyle());

                row.createCell((short) b++).setCellValue(rs.getString("stckcode"));

                row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));

                row.createCell((short) b++).setCellValue(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode"));

                row.createCell((short) b++).setCellValue(rs.getString("stckname") == null ? "" : rs.getString("stckname"));

                row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));

                if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }

                row.createCell((short) b++).setCellValue(rs.getString("accname"));

                row.createCell((short) b++).setCellValue(rs.getString("brname"));

                SXSSFCell oldvalue = row.createCell((short) b++);
                oldvalue.setCellValue(StaticMethods.round(rs.getBigDecimal("rsoldvalue").doubleValue(), rs.getInt("untunitrounding")));

                SXSSFCell newvalue = row.createCell((short) b++);
                newvalue.setCellValue(StaticMethods.round(rs.getBigDecimal("rsnewvalue").doubleValue(), rs.getInt("untunitrounding")));

                SXSSFCell removedvalue = row.createCell((short) b++);
                removedvalue.setCellValue(StaticMethods.round(rs.getBigDecimal("rsremovedvalue").doubleValue(), rs.getInt("untunitrounding")));

                SXSSFCell unitprice = row.createCell((short) b++);
                unitprice.setCellValue(StaticMethods.round(rs.getBigDecimal("rsunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFCell totalprice = row.createCell((short) b++);
                totalprice.setCellValue(StaticMethods.round(rs.getBigDecimal("rsremovedtotalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                j++;

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("removedstockreport"));
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
                Logger.getLogger(RemovedStockReportDao.class
                          .getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(Date beginDate, Date endDate, UserData userData, List<Boolean> toggleList, Branch branch) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        StringBuilder sb = new StringBuilder();
        try {
            connection = removedStockReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(removedStockReportDao.exportData());
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
            prep.setTimestamp(2, new Timestamp(beginDate.getTime()));
            prep.setTimestamp(3, new Timestamp(endDate.getTime()));
            prep.setInt(4, userData.getId());
            prep.setInt(5, branch.getId());
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
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("employee")).append(" : ").append(userData.getFullName()).append(" </div> ");

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

            StaticMethods.createHeaderPrint("frmInfo:dtbRemoveStock", toggleList, "headerBlack", sb);
            sb.append(" </tr>  ");

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("untunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("untunitrounding"));

                sb.append(" <tr> ");
                sb.append("<td>").append(rs.getString("usname") == null ? "" : rs.getString("usname") + " " + rs.getString("ussurname")).append("</td>");

                sb.append("<td>").append(rs.getString("shfshiftno") == null ? "" : rs.getString("shfshiftno")).append("</td>");

                sb.append("<td>").append(rs.getTimestamp("rsprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("rsprocessdate"))).append("</td>");

                sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");

                sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");

                sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");

                sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");

                sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");

                if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }

                sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");

                sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");

                sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("rsoldvalue"))).append(rs.getString("untsortname")).append("</td>");

                sb.append("<td style=\"text-align: right\" >").append(formatterUnit.format(rs.getBigDecimal("rsnewvalue"))).append(rs.getString("untsortname")).append("</td>");

                sb.append("<td style=\"text-align: right\" >").append(formatterUnit.format(rs.getBigDecimal("rsremovedvalue"))).append(rs.getString("untsortname")).append("</td>");

                Currency currencyStock = new Currency(rs.getInt("rscurrency_id"));
                sb.append("<td style=\"text-align: right\" >").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("rsunitprice"))).append(sessionBean.currencySignOrCode(currencyStock.getId(), 0)).append("</td>");

                sb.append("<td style=\"text-align: right\" >").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("rsremovedtotalprice"))).append(sessionBean.currencySignOrCode(currencyStock.getId(), 0)).append("</td>");

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
                Logger.getLogger(RemovedStockReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<RemovedStock> listOfRemovedStockForMarketShift(Shift shift) {
        return removedStockReportDao.listOfRemovedStockForMarketShift(shift);
    }

}
