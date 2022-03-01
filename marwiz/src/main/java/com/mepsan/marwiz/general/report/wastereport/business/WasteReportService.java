/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.wastereport.dao.IWasteReportDao;
import com.mepsan.marwiz.general.report.wastereport.dao.WasteReport;
import com.mepsan.marwiz.general.report.wastereport.dao.WasteReportDao;
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
public class WasteReportService implements IWasteReportService {

    @Autowired
    private IWasteReportDao wasteReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setWasteReportDao(IWasteReportDao wasteReportDao) {
        this.wasteReportDao = wasteReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(WasteReport obj) {
        String where = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND iwr.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

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
            where = where + " AND stck.id IN(" + stockList + ") ";
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
            where = where + " AND stck.id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
        }

        if (obj.getWasteReason().getId() != 0) {

            where = where + " AND wr.id = " + obj.getWasteReason().getId() + "";
        }
        return where;
    }

    @Override
    public List<WasteReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WasteReport obj, String branchList) {
        return wasteReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj, branchList);
    }

    @Override
    public List<WasteReport> totals(String where, String branchList) {
        return wasteReportDao.totals(where, branchList);
    }

    @Override
    public void exportPdf(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            connection = wasteReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(wasteReportDao.exportData(where, wasteReport, branchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("wastereport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getEndDate()), pdfDocument.getFont()));
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

                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (wasteReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : wasteReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (wasteReport.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : wasteReport.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String wasteReasonName = "";
            if (wasteReport.getWasteReason().getId() != 0) {
                wasteReasonName = wasteReport.getWasteReason().getName();
            } else {
                wasteReasonName = sessionBean.getLoc().getString("all");
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("wastereason") + " : " + wasteReasonName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmWasteReportDatatable:dtbWasteReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            String[] colums = new String[]{"iwrprocessdate", "brnname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", ""};

            String[] extension = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                Currency currency = new Currency();

                currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                formatter.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

                StaticMethods.pdfAddCell(pdfDocument, rs, toogleList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(rs.getString("wsidescription"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(rs.getString("wrname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(9)) {
                    Date dt = rs.getTimestamp("wsiexpirationdate");
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dt), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("wsiquantity") == null ? BigDecimal.ZERO : rs.getBigDecimal("wsiquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(12)) {
                    String param = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("taxrate"));
                    } else {
                        param = sessionBean.getNumberFormat().format(rs.getBigDecimal("taxrate")) + "%";
                    }
                    pdfDocument.getRightCell().setPhrase(new Phrase(param, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(13)) {
                    BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                    bd = bd = rs.getBigDecimal("taxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
                    bd = rs.getBigDecimal("unitprice").subtract(rs.getBigDecimal("unitprice").divide((new BigDecimal(1).add(bd)), 4, RoundingMode.HALF_EVEN)).multiply(rs.getBigDecimal("wsiquantity"));

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(14)) {
                    if (rs.getBigDecimal("unitprice") != null && rs.getBigDecimal("wsiquantity") != null) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice").multiply(rs.getBigDecimal("wsiquantity"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }
            for (WasteReport total : listOfTotals) {
                formatter.setMaximumFractionDigits(total.getStock().getUnit().getUnitRounding());
                formatter.setMinimumFractionDigits(total.getStock().getUnit().getUnitRounding());

                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : "
                        + formatter.format(total.getQuantity()) + total.getStock().getUnit().getSortName()
                        + " - "
                        + sessionBean.getNumberFormat().format(total.getTotal())
                        + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("wastereport"));
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
                Logger.getLogger(WasteReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = wasteReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(wasteReportDao.exportData(where, wasteReport, branchList));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("wastereport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            int i = 1;
            SXSSFRow empty = excelDocument.getSheet().createRow(i);
            i++;

            SXSSFRow startdate = excelDocument.getSheet().createRow(i);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getBeginDate()));
            i++;
            SXSSFRow enddate = excelDocument.getSheet().createRow(i);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getEndDate()));
            i++;
            
            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(i);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
            i++;
            
            String stockName = "";
            if (wasteReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : wasteReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(i);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);
            i++;

            String categoryName = "";
            if (wasteReport.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : wasteReport.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            SXSSFRow category = excelDocument.getSheet().createRow(i);
            category.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);
            i++;

            String wasteReasonName = "";
            if (wasteReport.getWasteReason().getId() != 0) {
                wasteReasonName = wasteReport.getWasteReason().getName();
            } else {
                wasteReasonName = sessionBean.getLoc().getString("all");
            }

            SXSSFRow wasteReason = excelDocument.getSheet().createRow(i);
            wasteReason.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("wastereason") + " : " + wasteReasonName);
            i++;

            SXSSFRow rwempty = excelDocument.getSheet().createRow(i);
            i++;

            StaticMethods.createHeaderExcel("frmWasteReportDatatable:dtbWasteReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            i++;

            String[] colums = new String[]{"iwrprocessdate", "brnname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", ""};

            String[] extension = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

                StaticMethods.excelAddCell(row, rs, toogleList, colums, excelDocument.getDateFormatStyle(), excelDocument.getWorkbook().getCreationHelper(), sessionBean.getUser(), sessionBean.getNumberFormat(), extension);

                if (toogleList.get(6)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(7)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("wsidescription"));
                }
                if (toogleList.get(8)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("wrname"));
                }
                if (toogleList.get(9)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("wsiexpirationdate")));
                }
                if (toogleList.get(10)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("wsiquantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(11)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("unitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(12)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("taxrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(13)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                    bd = bd = rs.getBigDecimal("taxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
                    bd = rs.getBigDecimal("unitprice").subtract(rs.getBigDecimal("unitprice").divide((new BigDecimal(1).add(bd)), 4, RoundingMode.HALF_EVEN)).multiply(rs.getBigDecimal("wsiquantity"));

                    quantity.setCellValue(StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                if (toogleList.get(14)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    if (rs.getBigDecimal("unitprice") != null && rs.getBigDecimal("wsiquantity") != null) {
                        quantity.setCellValue(StaticMethods.round((rs.getBigDecimal("unitprice").multiply(rs.getBigDecimal("wsiquantity"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        quantity.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                }

                i++;
            }

            for (WasteReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getQuantity(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + total.getStock().getUnit().getSortName()
                        + " - "
                        + StaticMethods.round(total.getTotal(), sessionBean.getUser().getLastBranch().getCurrencyrounding())
                        + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0));
                cell.setCellStyle(cellStyle1);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("wastereport"));
            } catch (IOException ex) {
                Logger.getLogger(WasteReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(WasteReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, WasteReport wasteReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, List<WasteReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = wasteReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(wasteReportDao.exportData(where, wasteReport, branchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), wasteReport.getEndDate())).append(" </div> ");
            
            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String stockName = "";
            if (wasteReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : wasteReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (wasteReport.getCategorizationList().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (wasteReport.getCategorizationList().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : wasteReport.getCategorizationList()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName);

            String wasteReasonName = "";
            if (wasteReport.getWasteReason().getId() != 0) {
                wasteReasonName = wasteReport.getWasteReason().getName();
            } else {
                wasteReasonName = sessionBean.getLoc().getString("all");
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("wastereason")).append(" : ").append(wasteReasonName).append(" </div> ");

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

            StaticMethods.createHeaderPrint("frmWasteReportDatatable:dtbWasteReport", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            String[] colums = new String[]{"iwrprocessdate", "brnname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", ""};

            String[] extensions = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                Currency currency = new Currency();
                currency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

                formatter.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitrounding"));
                sb.append(" <tr> ");

                StaticMethods.printAddCell(sb, rs, toogleList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extensions);

                if (toogleList.get(6)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("wsidescription") != null ? rs.getString("wsidescription") : " ").append("</td>");
                }

                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("wrname") != null ? rs.getString("wrname") : " ").append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getTimestamp("wsiexpirationdate") != null ? StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("wsiexpirationdate")) : " ").append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("wsiquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                if (toogleList.get(12)) {
                    String param = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("taxrate"));
                    } else {
                        param = sessionBean.getNumberFormat().format(rs.getBigDecimal("taxrate")) + "%";
                    }
                    sb.append("<td style=\"text-align: right\">").append(param).append("</td>");
                }

                if (toogleList.get(13)) {
                    BigDecimal bd = new BigDecimal(BigInteger.ZERO);
                    bd = bd = rs.getBigDecimal("taxrate").divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN);
                    bd = rs.getBigDecimal("unitprice").subtract(rs.getBigDecimal("unitprice").divide((new BigDecimal(1).add(bd)), 4, RoundingMode.HALF_EVEN)).multiply(rs.getBigDecimal("wsiquantity"));
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd)).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }

                if (toogleList.get(14)) {
                    if (rs.getBigDecimal("unitprice") != null && rs.getBigDecimal("wsiquantity") != null) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice").multiply(rs.getBigDecimal("wsiquantity")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(0)).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }

                }

                sb.append(" </tr> ");

            }
            for (WasteReport total : listOfTotals) {

                formatter.setMaximumFractionDigits(total.getStock().getUnit().getUnitRounding());
                formatter.setMinimumFractionDigits(total.getStock().getUnit().getUnitRounding());

                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"")
                        .append(numberOfColumns).append("\">")
                        .append(sessionBean.getLoc().getString("sum"))
                        .append(" : ").append(formatter.format(total.getQuantity()))
                        .append(total.getStock().getUnit().getSortName()).append(" - ")
                        .append(sessionBean.getNumberFormat().format(total.getTotal()))
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
                Logger.getLogger(WasteReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

}
