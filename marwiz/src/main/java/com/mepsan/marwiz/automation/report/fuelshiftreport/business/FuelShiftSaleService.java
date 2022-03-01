package com.mepsan.marwiz.automation.report.fuelshiftreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.IFuelShiftSaleDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtractDao;
import com.mepsan.marwiz.general.unit.dao.IUnitDao;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @author Samet Dağ
 */
public class FuelShiftSaleService implements IFuelShiftSaleService {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Autowired
    private IFuelShiftSaleDao fuelShiftSaleDao;

    public void setFuelShiftSaleDao(IFuelShiftSaleDao fuelShiftSaleDao) {
        this.fuelShiftSaleDao = fuelShiftSaleDao;
    }

    @Override
    public List<FuelShiftSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FuelShift fuelShift) {
        return fuelShiftSaleDao.findAll(first, pageSize, sortField, sortOrder, filters, where, fuelShift);
    }

    @Override
    public int count(String where, FuelShift fuelShift) {
        return fuelShiftSaleDao.count(where, fuelShift);
    }

    @Override
    public List<FuelShiftSales> listPrintRecords(FuelShift fuelShift) {
        return fuelShiftSaleDao.listPrintRecords(fuelShift);
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        int numberOfColumns = toogleList.size();
        HashMap<Unit, BigDecimal> groupTotal = new HashMap<>();

        try {

            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            connection = fuelShiftSaleDao.getDatasource().getConnection();

            prep = connection.prepareStatement(fuelShiftSaleDao.exportData(where, fuelShift));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fuelshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + fuelShift.getShiftNo(), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmFuelShiftSaleReport:dtbFuelShiftSaleReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("ssprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("sspumpno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssnozzleno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssaccountcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssstckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssstockcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssplate"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssattendant"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ssattendantcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("fstname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(Integer.toString(rs.getInt("sspaymenttype")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssprice")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssdistotal")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sstotalmoney")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("ssliter")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            BigDecimal totalMoney = BigDecimal.valueOf(0);
            for (FuelShiftSales s : listOfTotals) {

                totalMoney = totalMoney.add(s.getTotalMoney());

                Unit keyUnit = new Unit();
                keyUnit.setId(s.getUnit().getId());
                keyUnit.setSortName(s.getUnit().getSortName());
                keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
                if (groupTotal.containsKey(keyUnit)) {
                    BigDecimal old = groupTotal.get(keyUnit);
                    groupTotal.put(keyUnit, old.add(s.getLiter()));
                } else {
                    groupTotal.put(keyUnit, s.getLiter());
                }
            }
            String liter = "";
            int temp = 0;
            for (Map.Entry<Unit, BigDecimal> entry : groupTotal.entrySet()) {
                formatterUnit.setMaximumFractionDigits(entry.getKey().getUnitRounding());
                formatterUnit.setMinimumFractionDigits(entry.getKey().getUnitRounding());
                if (temp == 0) {
                    temp = 1;
                    liter += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                } else {
                    liter += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                }

            }

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : "
                      + sessionBean.getNumberFormat().format(totalMoney) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) + " - "
                      + liter, pdfDocument.getFont()));
            pdfDocument.getRightCell().setColspan(numberOfColumns);
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);

            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("fuelshiftreport"));

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
    public void exportExcel(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        HashMap<Unit, BigDecimal> groupTotal = new HashMap<>();

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {

            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            connection = fuelShiftSaleDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftSaleDao.exportData(where, fuelShift));
            rs = prep.executeQuery();

            CellStyle styleRight = excelDocument.getWorkbook().createCellStyle();
            styleRight.setAlignment(HorizontalAlignment.RIGHT);

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("fuelshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(1);
            SXSSFCell celltableheader = rowEmpty.createCell((short) 0);
            celltableheader.setCellValue(sessionBean.getLoc().getString("shiftno") + " : " + fuelShift.getShiftNo());
            celltableheader.setCellStyle(excelDocument.getStyleHeader());

            StaticMethods.createHeaderExcel("frmFuelShiftSaleReport:dtbFuelShiftSaleReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            int i = 3;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("ssprocessdate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("sspumpno"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssnozzleno"));
                }

                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssaccountcode"));
                }

                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssstckname"));
                }

                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssstockcode"));
                }

                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssplate"));

                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssattendant"));
                }

                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ssattendantcode"));
                }

                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(rs.getString("fstname"));

                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(rs.getInt("sspaymenttype"));
                }

                if (toogleList.get(11)) {
                    SXSSFCell cell2 = row.createCell((short) b++);
                    cell2.setCellValue(StaticMethods.round(rs.getBigDecimal("ssprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell2.setCellStyle(styleRight);
                }
                if (toogleList.get(12)) {
                    SXSSFCell cell3 = row.createCell((short) b++);
                    cell3.setCellValue(StaticMethods.round(rs.getBigDecimal("ssdistotal").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell3.setCellStyle(styleRight);
                }
                if (toogleList.get(13)) {
                    SXSSFCell cell4 = row.createCell((short) b++);
                    cell4.setCellValue(StaticMethods.round(rs.getBigDecimal("sstotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell4.setCellStyle(styleRight);
                }

                if (toogleList.get(14)) {
                    SXSSFCell cell5 = row.createCell((short) b++);
                    cell5.setCellValue(StaticMethods.round(rs.getBigDecimal("ssliter").doubleValue(), rs.getInt("guntunitrounding")));
                    cell5.setCellStyle(styleRight);
                }
                i++;
            }

            BigDecimal totalMoney = BigDecimal.valueOf(0);
            for (FuelShiftSales s : listOfTotals) {

                totalMoney = totalMoney.add(s.getTotalMoney());

                Unit keyUnit = new Unit();
                keyUnit.setId(s.getUnit().getId());
                keyUnit.setSortName(s.getUnit().getSortName());
                keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
                if (groupTotal.containsKey(keyUnit)) {
                    BigDecimal old = groupTotal.get(keyUnit);
                    groupTotal.put(keyUnit, old.add(s.getLiter()));
                } else {
                    groupTotal.put(keyUnit, s.getLiter());
                }
            }
            String liter = "";
            int temp = 0;
            for (Map.Entry<Unit, BigDecimal> entry : groupTotal.entrySet()) {
                formatterUnit.setMaximumFractionDigits(entry.getKey().getUnitRounding());
                formatterUnit.setMinimumFractionDigits(entry.getKey().getUnitRounding());
                if (temp == 0) {
                    temp = 1;
                    liter += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                } else {
                    liter += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                }

            }

            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row = excelDocument.getSheet().createRow(i);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue("" + sessionBean.getLoc().getString("sum") + ":" + StaticMethods.round(totalMoney, sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) + " - " + liter);
            cell.setCellStyle(cellStyle);

            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("fuelshiftreport"));

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
    public String exportPrinter(String where, List<Boolean> toogleList, FuelShift fuelShift, List<FuelShiftSales> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        HashMap<Unit, BigDecimal> groupTotal = new HashMap<>();

        try {
            connection = fuelShiftSaleDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftSaleDao.exportData(where, fuelShift));
            rs = prep.executeQuery();

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
                      + "        }"
                      + "   @page { size: landscape; }"
                      + "    </style> <table>");

            StaticMethods.createHeaderPrint("frmFuelShiftSaleReport:dtbFuelShiftSaleReport", toogleList, "headerBlack", sb);

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("ssprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("ssprocessdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("sspumpno") == null ? "" : rs.getString("sspumpno")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("ssnozzleno") == null ? "" : rs.getString("ssnozzleno")).append("</td>");
                }

                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("ssaccountcode") == null ? "" : rs.getString("ssaccountcode")).append("</td>");
                }

                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("ssstckname") == null ? "" : rs.getString("ssstckname")).append("</td>");
                }

                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("ssstockcode") == null ? "" : rs.getString("ssstockcode")).append("</td>");
                }

                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("ssplate") == null ? "" : rs.getString("ssplate")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("ssattendant") == null ? "" : rs.getString("ssattendant")).append("</td>");
                }

                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("ssattendantcode") == null ? "" : rs.getString("ssattendantcode")).append("</td>");
                }

                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("fstname") == null ? "" : rs.getString("fstname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(rs.getInt("sspaymenttype")).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssprice"))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssdistotal"))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sstotalmoney"))).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("ssliter"))).append(rs.getString("guntsortname")).append("</td>");
                }
                sb.append(" </tr> ");
            }

            BigDecimal totalMoney = BigDecimal.valueOf(0);
            for (FuelShiftSales s : listOfTotals) {

                totalMoney = totalMoney.add(s.getTotalMoney());

                Unit keyUnit = new Unit();
                keyUnit.setId(s.getUnit().getId());
                keyUnit.setSortName(s.getUnit().getSortName());
                keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
                if (groupTotal.containsKey(keyUnit)) {
                    BigDecimal old = groupTotal.get(keyUnit);
                    groupTotal.put(keyUnit, old.add(s.getLiter()));
                } else {
                    groupTotal.put(keyUnit, s.getLiter());
                }
            }
            String liter = "";
            int temp = 0;
            for (Map.Entry<Unit, BigDecimal> entry : groupTotal.entrySet()) {
                formatterUnit.setMaximumFractionDigits(entry.getKey().getUnitRounding());
                formatterUnit.setMinimumFractionDigits(entry.getKey().getUnitRounding());
                if (temp == 0) {
                    temp = 1;
                    liter += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                } else {
                    liter += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        liter += " " + entry.getKey().getSortName();
                    }
                }

            }

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(toogleList.size()).append("\">").append("").append(sessionBean.getLoc().getString("sum")).append(":").append(sessionBean.getNumberFormat().format(totalMoney)).append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0)).append(" - ").append(liter)
                      .append("</td>");
            sb.append(" </tr> ");

            sb.append(" </table> ");
        } catch (SQLException e) {
            Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, e);
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
    public List<FuelShiftSales> totals(String where, FuelShift fuelShift) {
        return fuelShiftSaleDao.totals(where, fuelShift);
    }

}
