/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 10:00:37
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftReportDetailDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.mepsan.marwiz.general.report.marketshiftreport.dao.IMarketShiftReportDetailDao;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class MarketShiftReportDetailService implements IMarketShiftReportDetailService {

    @Autowired
    public IMarketShiftReportDetailDao marketShiftReportDetailDao;

    @Autowired
    private SessionBean sessionBean;

    public void setMarketShiftReportDetailDao(IMarketShiftReportDetailDao marketShiftReportDetailDao) {
        this.marketShiftReportDetailDao = marketShiftReportDetailDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift) {
        return marketShiftReportDetailDao.findAll(first, pageSize, sortField, sortOrder, filters, where, shift);
    }

    @Override
    public int count(String where, Shift shift) {
        return marketShiftReportDetailDao.count(where, shift);
    }

    @Override
    public int create(Sales obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Sales obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SaleItem> find(Sales obj) {
        return marketShiftReportDetailDao.find(obj);
    }

    @Override
    public List<SalePayment> listOfSaleType(Sales sales) {
        return marketShiftReportDetailDao.listOfSaleType(sales);
    }

    @Override
    public void exportExcel(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        org.apache.poi.ss.usermodel.Font newFont = excelDocument.getWorkbook().createFont();
        newFont.setBold(true);
        newFont.setFontHeightInPoints((short) 10);
        excelDocument.getStyleHeader().setFont(newFont);

        CellStyle style = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());
        org.apache.poi.ss.usermodel.Font newFont2 = excelDocument.getWorkbook().createFont();
        newFont2.setBold(false);
        newFont2.setFontHeightInPoints((short) 10);
        style.setFont(newFont2);

        //market vardiyasından gelindiyse virgülden sonra shiftCurrencyRounding alanında yazan değer kadar hane gösterilmesi için
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        try {
            if (!isStockView) {
                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportData(shift));
                rs = prep.executeQuery();

            } else {
                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportDataStockDetail(shift));
                rs = prep.executeQuery();

            }

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("detailmarketshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow shiftNo = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellshiftno = shiftNo.createCell((short) 0);
            cellshiftno.setCellValue(sessionBean.getLoc().getString("shiftno") + " : " + shift.getShiftNo());
            cellshiftno.setCellStyle(style);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);
            if (!isStockView) {
                StaticMethods.createHeaderExcel("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            } else {
                StaticMethods.createHeaderExcel("frmShiftReportStockDetail:dtbShiftReportStockDetail", toogleList, "headerBlack", excelDocument.getWorkbook());
            }

            jRow++;

            int i = 0;

            if (!isStockView) { //Fiş görünümü ise

                while (rs.next()) {

                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    if (toogleList.get(0)) {
                        SXSSFCell cell0 = row.createCell((short) b++);
                        cell0.setCellValue(rs.getTimestamp("slprocessdate"));
                        cell0.setCellStyle(excelDocument.getDateFormatStyle());

                    }
                    if (toogleList.get(1)) {
                        SXSSFCell posname = row.createCell((short) b++);
                        posname.setCellValue(rs.getString("posname"));
                        posname.setCellStyle(style);

                    }
                    if (toogleList.get(2)) {
                        SXSSFCell usname = row.createCell((short) b++);
                        usname.setCellValue(rs.getString("usname") + " " + rs.getString("ussurname"));
                        usname.setCellStyle(style);

                    }
                    if (toogleList.get(3)) {
                        if (rs.getBoolean("accis_employee")) {
                            SXSSFCell accname = row.createCell((short) b++);
                            accname.setCellValue(rs.getString("accname") + " " + rs.getString("acctitle"));
                            accname.setCellStyle(style);

                        } else {
                            SXSSFCell accname1 = row.createCell((short) b++);
                            accname1.setCellValue(rs.getString("accname"));
                            accname1.setCellStyle(style);

                        }
                    }
                    if (toogleList.get(4)) {
                        SXSSFCell sltotalprice = row.createCell((short) b++);
                        sltotalprice.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalprice")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice")));
                        sltotalprice.setCellStyle(style);

                    }
                    if (toogleList.get(5)) {
                        SXSSFCell sltotaldiscount = row.createCell((short) b++);
                        sltotaldiscount.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotaldiscount")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaldiscount")));
                        sltotaldiscount.setCellStyle(style);

                    }
                    if (toogleList.get(6)) {
                        SXSSFCell sltotaltax = row.createCell((short) b++);
                        sltotaltax.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotaltax")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaltax")));
                        sltotaltax.setCellStyle(style);

                    }
                    if (toogleList.get(7)) {
                        SXSSFCell sltotalmoney = row.createCell((short) b++);
                        sltotalmoney.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney")));
                        sltotalmoney.setCellStyle(style);

                    }
                    i++;
                }

            } else { // Ürün görünümü ise

                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    if (toogleList.get(0)) {
                        SXSSFCell cell0 = row.createCell((short) b++);
                        cell0.setCellValue(rs.getTimestamp("sliprocessdate"));
                        cell0.setCellStyle(excelDocument.getDateFormatStyle());
                    }

                    if (toogleList.get(1)) {
                        SXSSFCell stckcode = row.createCell((short) b++);
                        stckcode.setCellValue(rs.getString("stckcode"));
                        stckcode.setCellStyle(style);

                    }
                    if (toogleList.get(2)) {
                        SXSSFCell stckcenterproductcode = row.createCell((short) b++);
                        stckcenterproductcode.setCellValue(rs.getString("stckcenterproductcode"));
                        stckcenterproductcode.setCellStyle(style);

                    }
                    if (toogleList.get(3)) {
                        SXSSFCell stckbarcode = row.createCell((short) b++);
                        stckbarcode.setCellValue(rs.getString("stckbarcode"));
                        stckbarcode.setCellStyle(style);

                    }
                    if (toogleList.get(4)) {
                        SXSSFCell stckname = row.createCell((short) b++);
                        stckname.setCellValue(rs.getString("stckname"));
                        stckname.setCellStyle(style);

                    }
                    if (toogleList.get(5)) {
                        SXSSFCell category = row.createCell((short) b++);
                        category.setCellValue(StaticMethods.findCategories(rs.getString("category")));
                        category.setCellStyle(style);

                    }
                    if (toogleList.get(6)) {
                        SXSSFCell csppname = row.createCell((short) b++);
                        csppname.setCellValue(rs.getString("csppname"));
                        csppname.setCellStyle(style);

                    }
                    if (toogleList.get(7)) {
                        SXSSFCell acc1name = row.createCell((short) b++);
                        acc1name.setCellValue(rs.getString("acc1name"));
                        acc1name.setCellStyle(style);

                    }

                    if (toogleList.get(8)) {
                        SXSSFCell accname = row.createCell((short) b++);
                        accname.setCellValue(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"));
                        accname.setCellStyle(style);

                    }

                    if (toogleList.get(9)) {
                        SXSSFCell brname = row.createCell((short) b++);
                        brname.setCellValue(rs.getString("brname"));
                        brname.setCellStyle(style);

                    }

                    if (toogleList.get(10)) {
                        if (rs.getInt("slreceipt_id") == 0) {
                            SXSSFCell invdocumentnumber = row.createCell((short) b++);
                            invdocumentnumber.setCellValue(rs.getString("invdocumentnumber"));
                            invdocumentnumber.setCellStyle(style);

                        } else {
                            SXSSFCell receiptno = row.createCell((short) b++);
                            receiptno.setCellValue(rs.getString("receiptno"));
                            receiptno.setCellStyle(style);

                        }
                    }
                    if (toogleList.get(11)) {
                        SXSSFCell sliunitprice = row.createCell((short) b++);
                        sliunitprice.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sliunitprice")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice")));
                        sliunitprice.setCellStyle(style);

                    }
                    if (toogleList.get(12)) {
                        SXSSFCell sliquantity = row.createCell((short) b++);
                        sliquantity.setCellValue(StaticMethods.round(rs.getBigDecimal("sliquantity").doubleValue(), oldId == 66 ? sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding() : rs.getInt("guntunitsorting")));
                        sliquantity.setCellStyle(style);

                    }
                    if (toogleList.get(13)) {
                        SXSSFCell slitotalmoney = row.createCell((short) b++);
                        slitotalmoney.setCellValue(oldId == 66 ? numberFormat.format(rs.getBigDecimal("slitotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney")));
                        slitotalmoney.setCellStyle(style);

                    }

                    i++;
                }

            }

            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            org.apache.poi.ss.usermodel.Font newFont1 = excelDocument.getWorkbook().createFont();
            newFont1.setFontHeightInPoints((short) 10);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setFont(newFont1);

            if (!isStockView) {
                for (SalePayment total : listOfTotals) {
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell cell = row.createCell((short) 0);
                    cell.setCellValue(total.getType().getTag() + " : "
                            + StaticMethods.round(total.getPrice(), oldId == 66 ? sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding() : sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(total.getSales().getCurrency().getId(), 0));
                    cell.setCellStyle(cellStyle);
                }
            } else {
                for (SalePayment total : listStockDetailOfTotals) {
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell cell = row.createCell((short) 0);
                    cell.setCellValue(total.getType().getTag() + " : "
                            + StaticMethods.round(total.getPrice(), oldId == 66 ? sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding() : sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(total.getSales().getCurrency().getId(), 0));
                    cell.setCellStyle(cellStyle);
                }
            }

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(sessionBean.getLoc().getString("sum") + " : " + totals);
            cell.setCellStyle(cellStyle);

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("detailmarketshiftreport"));
            } catch (IOException ex) {
                Logger.getLogger(MarketShiftReportDetailService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MarketShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId) {
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
            if (!isStockView) {
                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportData(shift));
                rs = prep.executeQuery();

            } else {
                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportDataStockDetail(shift));
                rs = prep.executeQuery();

            }
            //market vardiyasından gelindiyse virgülden sonra shiftCurrencyRounding alanında yazan değer kadar hane gösterilmesi için
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

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
                    + "            font-size: 10px;"
                    + "        }"
                    + "   @page { size: landscape; }"
                    + "    </style> <table>");

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("shiftno")).append(" : ").append(shift.getShiftNo()).append(" </div> ");
            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            if (!isStockView) {
                StaticMethods.createHeaderPrint("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", sb);
            } else {
                StaticMethods.createHeaderPrint("frmShiftReportStockDetail:dtbShiftReportStockDetail", toogleList, "headerBlack", sb);
            }

            Currency currency = new Currency();
            int i = 0;

            if (!isStockView) { //Fiş görünümü ise

                while (rs.next()) {
                    sb.append(" <tr> ");

                    currency = new Currency(rs.getInt("slcurrency_id"));

                    if (toogleList.get(0)) {
                        sb.append("<td>").append(rs.getTimestamp("slprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate"))).append("</td>");
                    }
                    if (toogleList.get(1)) {
                        sb.append("<td>").append(rs.getString("posname") == null ? "" : rs.getString("posname")).append("</td>");
                    }
                    if (toogleList.get(2)) {
                        sb.append("<td>").append(rs.getString("usname") == null ? "" : rs.getString("usname") + " " + rs.getString("ussurname")).append("</td>");
                    }
                    if (toogleList.get(3)) {
                        if (rs.getBoolean("accis_employee")) {
                            sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append(" ").append(rs.getString("acctitle") == null ? "" : rs.getString("acctitle")).append("</td>");
                        } else {
                            sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                        }
                    }
                    if (toogleList.get(4)) {

                        if (rs.getBoolean("slisreturn")) {
                            sb.append("<td style=\"text-align: right\">").append("-").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalprice")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append("+").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalprice")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        }
                    }
                    if (toogleList.get(5)) {
                        sb.append("<td style=\"text-align: right\">").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotaldiscount")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaldiscount"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td style=\"text-align: right\">").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotaltax")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaltax"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(7)) {
                        if (rs.getBoolean("slisreturn")) {
                            sb.append("<td style=\"text-align: right\">").append("-").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append("+").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sltotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        }
                    }

                    sb.append(" </tr> ");
                    i++;
                }

            } else { //Ürün görünümü ise

                while (rs.next()) {

                    if (oldId != 66) {
                        formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                        formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                    } else {
                        formatterUnit.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                        formatterUnit.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                    }

                    Currency currencyDetail = new Currency(rs.getInt("slicurrency_id"));

                    if (toogleList.get(0)) {
                        sb.append("<td>").append(rs.getTimestamp("sliprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("sliprocessdate"))).append("</td>");
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
                        sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                    }
                    if (toogleList.get(7)) {
                        sb.append("<td>").append(rs.getString("acc1name") == null ? "" : rs.getString("acc1name")).append("</td>");
                    }

                    if (toogleList.get(8)) {
                        sb.append("<td>").append(rs.getString("accname") == null ? "" : (rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"))).append("</td>");
                    }
                    if (toogleList.get(9)) {
                        sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                    }
                    if (toogleList.get(10)) {
                        if (rs.getInt("slreceipt_id") == 0) {
                            sb.append("<td>").append(rs.getString("invdocumentnumber") == null ? "" : rs.getString("invdocumentnumber")).append("</td>");
                        } else {
                            sb.append("<td>").append(rs.getString("receiptno") == null ? "" : rs.getString("receiptno")).append("</td>");
                        }
                    }
                    if (toogleList.get(11)) {
                        sb.append("<td style=\"text-align: right\">").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("sliunitprice")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice"))).append(sessionBean.currencySignOrCode(currencyDetail.getId(), 0)).append("</td>");
                    }
                    if (toogleList.get(12)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("sliquantity"))).append(rs.getString("guntsortname")).append("</td>");
                    }
                    if (toogleList.get(13)) {
                        if (rs.getBoolean("slisreturn")) {
                            sb.append("<td style=\"text-align: right\">").append("-").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("slitotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))).append(sessionBean.currencySignOrCode(currencyDetail.getId(), 0)).append("</td>");
                        } else {
                            sb.append("<td style=\"text-align: right\">").append("+").append(oldId == 66 ? numberFormat.format(rs.getBigDecimal("slitotalmoney")) : sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))).append(sessionBean.currencySignOrCode(currencyDetail.getId(), 0)).append("</td>");
                        }
                    }

                    sb.append(" </tr> ");

                }

            }

            if (!isStockView) {

                for (SalePayment listOfTotal : listOfTotals) {
                    sb.append(" <tr> ");
                    sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(listOfTotal.getType().getTag()).append(" : ")
                            .append(oldId == 66 ? numberFormat.format(listOfTotal.getPrice()) : sessionBean.getNumberFormat().format(listOfTotal.getPrice()))
                            .append(sessionBean.currencySignOrCode(listOfTotal.getSales().getCurrency().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }

            } else {
                for (SalePayment listOfTotal : listStockDetailOfTotals) {
                    sb.append(" <tr> ");
                    sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(listOfTotal.getType().getTag()).append(" : ")
                            .append(oldId == 66 ? numberFormat.format(listOfTotal.getPrice()) : sessionBean.getNumberFormat().format(listOfTotal.getPrice()))
                            .append(sessionBean.currencySignOrCode(listOfTotal.getSales().getCurrency().getId(), 0)).append("</td>");
                    sb.append(" </tr> ");
                }

            }

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                    .append(totals).append("</td>");
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
                Logger.getLogger(MarketShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();

    }

    @Override
    public List<SalePayment> totals(String where, Shift shift) {
        return marketShiftReportDetailDao.totals(where, shift);
    }

    @Override
    public List<SaleItem> findStockDetailList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift) {
        return marketShiftReportDetailDao.findStockDetailList(first, pageSize, sortField, sortOrder, filters, where, shift);
    }

    @Override
    public List<SalePayment> totalsStockDetailList(String where, Shift shift) {
        return marketShiftReportDetailDao.totalsStockDetailList(where, shift);
    }

    @Override
    public void exportPdf(Shift shift, List<Boolean> toogleList, List<SalePayment> listOfTotals, String totals, Boolean isStockView, List<SalePayment> listStockDetailOfTotals, int oldId) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        int numberOfColumns = toogleList.size();

        try {

            if (!isStockView) {

                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportData(shift));
                rs = prep.executeQuery();

            } else {
                connection = marketShiftReportDetailDao.getDatasource().getConnection();
                prep = connection.prepareStatement(marketShiftReportDetailDao.exportDataStockDetail(shift));
                rs = prep.executeQuery();

            }

            //market vardiyasından gelindiyse virgülden sonra shiftCurrencyRounding alanında yazan değer kadar hane gösterilmesi için
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
            pdfDocument.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9, Font.BOLD));
            pdfDocument.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8));
            pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8));

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("detailmarketshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + shift.getShiftNo(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            if (!isStockView) {
                StaticMethods.createHeaderPdf("frmShiftDetailReportDatatable:dtbShiftDetailReport", toogleList, "headerBlack", pdfDocument);
            } else {
                StaticMethods.createHeaderPdf("frmShiftReportStockDetail:dtbShiftReportStockDetail", toogleList, "headerBlack", pdfDocument);
            }

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            Currency currency = new Currency();
            int i = 0;

            if (!isStockView) {//Fiş görünümü ise

                while (rs.next()) {

                    currency = new Currency(rs.getInt("slcurrency_id"));

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(1)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("posname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(2)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("usname") + " " + rs.getString("ussurname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(3)) {
                        if (rs.getBoolean("accis_employee")) {
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname") + " " + rs.getString("acctitle"), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                        }
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(4)) {
                        if (rs.getBoolean("slisreturn")) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("-" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotalprice"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase("+" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotalprice"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        }
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase((oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotaldiscount"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaldiscount"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase((oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotaltax"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaltax"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(7)) {

                        if (rs.getBoolean("slisreturn")) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("-" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotalmoney"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase("+" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sltotalmoney"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        }
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    i++;

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }

            } else {//Ürün görünümü ise

                while (rs.next()) {
                    if (oldId != 66) {
                        formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                        formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                    } else {
                        formatterUnit.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                        formatterUnit.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                    }

                    Currency currency1 = new Currency(rs.getInt("slicurrency_id"));

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("sliprocessdate")), pdfDocument.getFont()));
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
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("acc1name"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    }

                    if (toogleList.get(9)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    }
                    if (toogleList.get(10)) {
                        if (rs.getInt("slreceipt_id") == 0) {
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentnumber"), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("receiptno"), pdfDocument.getFont()));
                        }
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    }

                    if (toogleList.get(11)) {

                        pdfDocument.getRightCell().setPhrase(new Phrase((oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("sliunitprice"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("sliunitprice"))) + sessionBean.currencySignOrCode(currency1.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    }
                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sliquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    }
                    if (toogleList.get(13)) {
                        if (rs.getBoolean("slisreturn")) {
                            pdfDocument.getRightCell().setPhrase(new Phrase("-" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("slitotalmoney"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))) + sessionBean.currencySignOrCode(currency1.getId(), 0), pdfDocument.getFont()));
                        } else {
                            pdfDocument.getRightCell().setPhrase(new Phrase("+" + (oldId == 66 ? String.valueOf(numberFormat.format(rs.getBigDecimal("slitotalmoney"))) : sessionBean.getNumberFormat().format(rs.getBigDecimal("slitotalmoney"))) + sessionBean.currencySignOrCode(currency1.getId(), 0), pdfDocument.getFont()));
                        }
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                }

            }

            if (!isStockView) {
                for (SalePayment listOfTotal : listOfTotals) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(listOfTotal.getType().getTag() + " : "
                            + (oldId == 66 ? String.valueOf(numberFormat.format(listOfTotal.getPrice())) : sessionBean.getNumberFormat().format(listOfTotal.getPrice())) + sessionBean.currencySignOrCode(listOfTotal.getSales().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getRightCell().setColspan(numberOfColumns);
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
            } else {
                for (SalePayment listOfTotal : listStockDetailOfTotals) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(listOfTotal.getType().getTag() + " : "
                            + (oldId == 66 ? String.valueOf(numberFormat.format(listOfTotal.getPrice())) : sessionBean.getNumberFormat().format(listOfTotal.getPrice())) + sessionBean.currencySignOrCode(listOfTotal.getSales().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getRightCell().setColspan(numberOfColumns);
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
            }

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : "
                    + totals, pdfDocument.getFont()));
            pdfDocument.getRightCell().setColspan(numberOfColumns);
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("detailmarketshiftreport"));

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
                Logger.getLogger(MarketShiftReportDetailDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

}
