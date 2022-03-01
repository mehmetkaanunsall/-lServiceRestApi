/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:13:33 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.automat.report.incomeexpensereport.dao.IIncomeExpenseReportDao;
import com.mepsan.marwiz.automat.report.incomeexpensereport.dao.IncomeExpenseReportDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.math.BigDecimal;
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
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.primefaces.component.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;

public class IncomeExpenseReportService implements IIncomeExpenseReportService {

    @Autowired
    IIncomeExpenseReportDao incomeExpenseReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setIncomeExpenseReportDao(IIncomeExpenseReportDao incomeExpenseReportDao) {
        this.incomeExpenseReportDao = incomeExpenseReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomatSalesReport> findAll(String where) {
        return incomeExpenseReportDao.findAll(where);
    }

    @Override
    public List<AutomatSalesReport> listOfSaleWaste(String where) {
        return incomeExpenseReportDao.listOfSaleWaste(where);
    }

    @Override
    public List<AutomatSalesReport> listOfIncomeExpense(String where) {
        return incomeExpenseReportDao.listOfIncomeExpense(where);
    }

    @Override
    public List<AutomatSalesReport> listOfDetail(String where) {
        return incomeExpenseReportDao.listOfDetail(where);
    }

    @Override
    public int count(String where) {
        return incomeExpenseReportDao.count(where);
    }

    @Override
    public String createWhere(AutomatSalesReport obj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String where = (" AND sl.saledatetime BETWEEN '" + dateFormat.format(obj.getBeginDate()) + "' AND '" + dateFormat.format(obj.getEndDate()) + "' ");

        if (obj.getShiftNo() != null) {
            where += ((!obj.getShiftNo().equals("")) ? " AND sl.shiftno = '" + obj.getShiftNo().replace("'", "") + "' " : "");
        }

        if (obj.getListOfStock().size() != 0) { // Seçili stoklar için
            String stockId = "";
            for (Stock stock : obj.getListOfStock()) {
                stockId = stockId + "," + String.valueOf(stock.getId());
                if (stock.getId() == 0) {
                    stockId = "";
                    break;
                }
            }
            if (!stockId.equals("")) {
                stockId = stockId.substring(1, stockId.length());
                where = where + " AND stck.id IN(" + stockId + ") ";

            }
        }
        return where;
    }

    @Override
    public String exportData(String where) {
        return incomeExpenseReportDao.exportData(where);
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        int numberOfColumns = 0;

        for (boolean b : toogleList) {
            if (b) {
                numberOfColumns++;
            }
        }
        try {
            connection = incomeExpenseReportDao.getDatasource().getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(IncomeExpenseReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            prep = connection.prepareStatement(incomeExpenseReportDao.exportData(where));
        } catch (SQLException ex) {
            Logger.getLogger(IncomeExpenseReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            rs = prep.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(IncomeExpenseReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        AutomatSalesReport automatSaleReport = (AutomatSalesReport) param;
        PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
        pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incomeexpensereport"), pdfDocument.getFontHeader()));
        pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());
        pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
        String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getBeginDate()) + "    " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getEndDate());
        if (automatSaleReport.getShiftNo() != null && !automatSaleReport.getShiftNo().equals("")) {
            param1 += "     " + sessionBean.getLoc().getString("shiftno") + " : " + automatSaleReport.getShiftNo();
        }
        String param2 = "";
        if (!automatSaleReport.getListOfStock().isEmpty()) {
            String stockName = "";
            for (Stock stock : automatSaleReport.getListOfStock()) {
                stockName += " , " + stock.getName();
            }
            stockName = stockName.substring(3, stockName.length());
            param2 += sessionBean.getLoc().getString("stock") + " : " + stockName;
        } else {
            param2 += sessionBean.getLoc().getString("stock") + " : " + sessionBean.getLoc().getString("all");
        }
        pdfDocument.getCell().setPhrase(new Phrase(param1, pdfDocument.getFont()));
        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

        pdfDocument.getCell().setPhrase(new Phrase(param2, pdfDocument.getFont()));
        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

        pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

        try {
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
        } catch (DocumentException ex) {
            Logger.getLogger(IncomeExpenseReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

        //başlıkları ekledik
        StaticMethods.createHeaderPdf("frmIncomeExpenseReportDatatable:dtbIncomeExpense", toogleList, "headerBlack", pdfDocument);

        try {
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
        } catch (DocumentException ex) {
            Logger.getLogger(IncomeExpenseReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            for (AutomatSalesReport report : list) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(report.getStock().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(String.valueOf(report.getQuantitiy()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(report.getOperationTime() + " " + sessionBean.getLoc().getString("sec"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(String.valueOf(report.getWaste() == null ? 0 : formatter.format(report.getWaste())) + " " + (report.getStock().getUnit().getSortName() == null ? "" : report.getStock().getUnit().getSortName()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((report.getTotalIncome().compareTo(BigDecimal.ZERO) == 0 ? "" : formatter.format(report.getTotalIncome())) + " " + (report.getTotalIncome().compareTo(BigDecimal.ZERO) == 0 ? "-" : sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }    
                if (toogleList.get(5)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((report.getTotalExpense().compareTo(BigDecimal.ZERO) == 0 || report.getTotalExpense() == null ? BigDecimal.valueOf(0) : formatter.format(report.getTotalExpense())) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(report.getTotalWinnings()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
            }
            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getRightCell());
            pdfDocument.getRightCell().setColspan(numberOfColumns);
            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumincome") + " : " + StaticMethods.round(automatSaleReport.getTotalNetIncome(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) 
                    + " " + sessionBean.getLoc().getString("sumexpense") + " : " + StaticMethods.round(automatSaleReport.getTotalNetExpense(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) 
                    + " " + sessionBean.getLoc().getString("sum") + " : " + StaticMethods.round(automatSaleReport.getNetTotal(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("incomeexpensereport"));
        } catch (DocumentException e) {
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
                Logger.getLogger(IncomeExpenseReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }
    }

    @Override
    public void exportExcel(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = incomeExpenseReportDao.getDatasource().getConnection();
            AutomatSalesReport automatSaleReport = (AutomatSalesReport) param;
            prep = connection.prepareStatement(incomeExpenseReportDao.exportData(where));
            rs = prep.executeQuery();

            CellStyle dateFormatStyle = excelDocument.getWorkbook().createCellStyle();
            CreationHelper createHelper = excelDocument.getWorkbook().getCreationHelper();
            short dateFormat = createHelper.createDataFormat().getFormat(sessionBean.getUser().getLastBranch().getDateFormat());
            dateFormatStyle.setDataFormat(dateFormat);
            dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            int jRow = 0;

            CellStyle cellStyle = excelDocument.getWorkbook().createCellStyle();
            Font font = excelDocument.getWorkbook().createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index);
            cellStyle.setFont(font);
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("incomeexpensereport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getBeginDate()) + "    " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getEndDate());
            if (automatSaleReport.getShiftNo() != null && !automatSaleReport.getShiftNo().equals("")) {
                param1 += "     " + sessionBean.getLoc().getString("shiftno") + " : " + automatSaleReport.getShiftNo();
            }

            SXSSFRow param1Cell = excelDocument.getSheet().createRow(jRow++);
            param1Cell.createCell((short) 0).setCellValue(param1);
            String param2 = "";
            if (!automatSaleReport.getListOfStock().isEmpty()) {
                String stockName = "";
                for (Stock stock : automatSaleReport.getListOfStock()) {
                    stockName += " , " + stock.getName();
                }
                stockName = stockName.substring(3, stockName.length());
                param2 += sessionBean.getLoc().getString("stock") + " : " + stockName;
            } else {
                param2 += sessionBean.getLoc().getString("stock") + " : " + sessionBean.getLoc().getString("all");

                SXSSFRow param2Cell = excelDocument.getSheet().createRow(jRow++);
                param2Cell.createCell((short) 0).setCellValue(param2);

                SXSSFRow rowc = excelDocument.getSheet().createRow(jRow++);

                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmIncomeExpenseReportDatatable:dtbIncomeExpense");
                int a = 0;
                SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);
                for (int x = 0; x < toogleList.size(); x++) {
                    if (toogleList.get(x)) {

                        SXSSFCell cell1 = rowh.createCell((short) a++);
                        cell1.setCellValue(dataTable.getColumns().get(x).getHeaderText());
                        cell1.setCellStyle(cellStyle);
                    }
                }
                CellStyle cellStyleCell = excelDocument.getWorkbook().createCellStyle();
                cellStyleCell.setAlignment(HorizontalAlignment.RIGHT);

                try {
                    for (AutomatSalesReport report : list) {
                        int b = 0;
                        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                        if (toogleList.get(0)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue(report.getStock().getName());
                        }
                        if (toogleList.get(1)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue(report.getQuantitiy().intValue());
                            cell.setCellStyle(cellStyleCell);

                        }
                        if (toogleList.get(2)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue(report.getOperationTime() + " " + sessionBean.getLoc().getString("sec"));
                            cell.setCellStyle(cellStyleCell);

                        }
                        if (toogleList.get(3)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue((report.getWaste() == null ? 0 : StaticMethods.round(report.getWaste().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                            cell.setCellStyle(cellStyleCell);

                        }
                        if (toogleList.get(4)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue(StaticMethods.round(report.getTotalIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            cell.setCellStyle(cellStyleCell);
                        }
                        if (toogleList.get(5)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue((report.getTotalExpense() == null ? 0 : StaticMethods.round(report.getTotalExpense().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                            cell.setCellStyle(cellStyleCell);
                        }
                        if (toogleList.get(6)) {
                            SXSSFCell cell = row.createCell((short) b++);
                            cell.setCellValue(StaticMethods.round(report.getTotalWinnings().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            cell.setCellStyle(cellStyleCell);
                        }
                    }
                    CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                    stylefooter.setAlignment(HorizontalAlignment.LEFT);
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell cell = row.createCell((short) 0);
                    SXSSFCell cell1 = row.createCell((short) 1);
                    SXSSFCell cell2 = row.createCell((short) 2);
                    cell.setCellValue((sessionBean.getLoc().getString("sumincome") + " : " + StaticMethods.round(automatSaleReport.getTotalNetIncome(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)));
                    cell.setCellStyle(stylefooter);
                    cell1.setCellValue(" " + sessionBean.getLoc().getString("sumexpense") + " : " + StaticMethods.round(automatSaleReport.getTotalNetExpense(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));
                    cell1.setCellStyle(stylefooter);
                    cell2.setCellValue(" " + sessionBean.getLoc().getString("sum") + " : " + StaticMethods.round(automatSaleReport.getNetTotal(), 2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));
                    cell2.setCellStyle(stylefooter);

                    StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("incomeexpensereport"));
                } catch (Exception e) {
                }
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
                Logger.getLogger(IncomeExpenseReportDao.class
                        .getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, List<Boolean> toogleList, Object param, int pageId, List<AutomatSalesReport> list) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        try {
            connection = incomeExpenseReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(incomeExpenseReportDao.exportData(where));
            AutomatSalesReport automatSaleReport = (AutomatSalesReport) param;
            rs = prep.executeQuery();

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            Currency currency = new Currency();
            currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            currency.setCode(sessionBean.currencySignOrCode(currency.getId(), 0));//kodu aldık

            Currency cTotalPrice = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getBeginDate()) + "    " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), automatSaleReport.getEndDate());
            if (automatSaleReport.getShiftNo() != null && !automatSaleReport.getShiftNo().equals("")) {
                param1 += "     " + sessionBean.getLoc().getString("shiftno") + " : " + automatSaleReport.getShiftNo();
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(param1).append(" </div> ");

            String param2 = "";
            if (!automatSaleReport.getListOfStock().isEmpty()) {
                String stockName = "";
                for (Stock stock : automatSaleReport.getListOfStock()) {
                    stockName += " , " + stock.getName();
                }
                stockName = stockName.substring(3, stockName.length());
                param2 += sessionBean.getLoc().getString("stock") + " : " + stockName;
            } else {
                param2 += sessionBean.getLoc().getString("stock") + " : " + sessionBean.getLoc().getString("all");
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param2).append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(
                    " <style>"
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
                    + "   @media print {"
                    + "     html, body {"
                    + "    width: 210mm;"
                    + "    height: 297mm;"
                    + "     }}"
                    + "    </style> <table> <tr>");

            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmIncomeExpenseReportDatatable:dtbIncomeExpense");
            for (int x = 0; x < toogleList.size(); x++) {
                if (toogleList.get(x)) {
                    sb.append("<th>").append(dataTable.getColumns().get(x).getHeaderText()).append("</th>");
                }
            }
            sb.append(" </tr>  ");
            int totalColumnIndex = 0;
            try {
                for (AutomatSalesReport report : list) {
                    sb.append(" <tr> ");

                    if (toogleList.get(0)) {
                        totalColumnIndex++;
                        sb.append("<td>").append(report.getStock().getName()).append("</td>");
                    }
                    if (toogleList.get(1)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(report.getQuantitiy().intValue()).append("</td>");
                    }
                    if (toogleList.get(2)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(report.getOperationTime()).append(" ").append(sessionBean.getLoc().getString("sec")).append("</td>");
                    }
                    if (toogleList.get(3)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(report.getWaste() == null ? 0 : formatter.format(report.getWaste())).append(" ").append(report.getStock().getUnit().getSortName() == null ? "" : report.getStock().getUnit().getSortName()).append("</td>");
                    }
                    if (toogleList.get(4)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(report.getTotalIncome().compareTo(BigDecimal.ZERO) == 0 ? "" : formatter.format(report.getTotalIncome())).append(" ").append(report.getTotalIncome().compareTo(BigDecimal.ZERO) == 0 ? "-" : currency.getCode()).append("</td>");
                    }
                    if (toogleList.get(5)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(report.getTotalExpense() == null ? BigDecimal.valueOf(0) : (formatter.format(report.getTotalExpense()))).append(" ").append(currency.getCode()).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        totalColumnIndex++;
                        sb.append("<td style=\"text-align: right\">").append(formatter.format(report.getTotalWinnings())).append(" ").append(currency.getCode()).append("</td>");
                    }
                }
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sumincome")).append(" : ")
                        .append(StaticMethods.round(automatSaleReport.getTotalNetIncome(), 2))
                        .append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append(" ").append(sessionBean.getLoc().getString("sumexpense")).append(" : ")
                        .append(StaticMethods.round(automatSaleReport.getTotalNetExpense(), 2))
                        .append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append(" ").append(sessionBean.getLoc().getString("total")).append(" : ")
                        .append(StaticMethods.round(automatSaleReport.getNetTotal(), 2))
                        .append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append(" ").append("</td>");
                sb.append(" </tr> ");

                sb.append(" </table> ");
            } catch (Exception e) {
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
                Logger.getLogger(IncomeExpenseReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

}
