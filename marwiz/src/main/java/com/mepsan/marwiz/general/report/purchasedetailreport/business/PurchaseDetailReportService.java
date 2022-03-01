/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.purchasedetailreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.IPurchaseDetailReportDao;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.PurchaseDetailReport;
import com.mepsan.marwiz.general.report.purchasedetailreport.dao.PurchaseDetailReportDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class PurchaseDetailReportService implements IPurchaseDetailReportService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    private IPurchaseDetailReportDao purchaseDetailReportDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setPurchaseDetailReportDao(IPurchaseDetailReportDao purchaseDetailReportDao) {
        this.purchaseDetailReportDao = purchaseDetailReportDao;
    }

    @Override
    public void exportPdf(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalPurchaseQuantity, String subTotalMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = purchaseDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseDetailReportDao.exportData(where, branchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            //Birim İçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasedetailreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getEndDate()), pdfDocument.getFont()));
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

            String stockName = "";
            if (purchaseDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (purchaseDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : purchaseDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (purchaseDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmPurchaseDetailDatatable:dtbPurchaseDetail", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            try {
                while (rs.next()) {
                    formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                    formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                    Currency currency = new Currency(rs.getInt("invicurrency"));

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(1)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(2)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(3)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(4)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(9)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(10)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invoiceaccount"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(11)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumentserial") + rs.getString("invdocumentnumber"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("inviunitprice") != null ? sessionBean.getNumberFormat().format(rs.getBigDecimal("inviunitprice")) : " " + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(13)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("inviquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    if (toogleList.get(14)) {
                        String param = "";
                        if (rs.getBigDecimal("invitaxrate") != null) {
                            if (sessionBean.getUser().getLanguage().getId() == 1) {
                                param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate"));
                            } else {
                                param = sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate")) + "%";
                            }
                        }
                        pdfDocument.getDataCell().setPhrase(new Phrase(param, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                    }
                    if (toogleList.get(15)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("invitotaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(16)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("invitotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalpurchasequantity") + " : " + subTotalPurchaseQuantity, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalgiro") + " : " + subTotalMoney, pdfDocument.getFont()));
            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("purchasedetailreport"));

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
                Logger.getLogger(PurchaseDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalPurchaseQuantity, String subTotal) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {

            connection = purchaseDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseDetailReportDao.exportData(where, branchList));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("purchasedetailreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            jRow++;

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getEndDate()));

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

            String stockName = "";
            if (purchaseDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(jRow++);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String categoryName = "";
            if (purchaseDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : purchaseDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow stockcategory = excelDocument.getSheet().createRow(jRow++);
            stockcategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (purchaseDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmPurchaseDetailDatatable:dtbPurchaseDetail", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            try {
                while (rs.next()) {
                    Currency currency = new Currency(rs.getInt("invicurrency"));

                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    if (toogleList.get(0)) {
                        SXSSFCell cell0 = row.createCell((short) b++);
                        cell0.setCellValue(rs.getTimestamp("invinvoicedate"));
                        cell0.setCellStyle(excelDocument.getDateFormatStyle());
                    }
                    if (toogleList.get(1)) {
                        row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                    }
                    if (toogleList.get(2)) {
                        row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                    }
                    if (toogleList.get(3)) {
                        row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));
                    }
                    if (toogleList.get(4)) {
                        row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                    }
                    if (toogleList.get(5)) {
                        row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                    }
                    if (toogleList.get(6)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));
                    }
                    if (toogleList.get(7)) {
                        row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                    }
                    if (toogleList.get(8)) {
                        row.createCell((short) b++).setCellValue(rs.getString("accname"));
                    }
                    if (toogleList.get(9)) {
                        row.createCell((short) b++).setCellValue(rs.getString("brname"));
                    }
                    if (toogleList.get(10)) {
                        row.createCell((short) b++).setCellValue(rs.getString("invoiceaccount"));
                    }

                    if (toogleList.get(11)) {
                        row.createCell((short) b++).setCellValue(rs.getString("invdocumentserial") + rs.getString("invdocumentnumber"));
                    }
                    if (toogleList.get(12)) {
                        row.createCell((short) b++).setCellValue(rs.getBigDecimal("inviunitprice") != null ? StaticMethods.round(rs.getBigDecimal("inviunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) : StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(13)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("inviquantity").doubleValue(), rs.getInt("guntunitsorting")));
                    }

                    if (toogleList.get(14)) {
                        String param = "";
                        if (rs.getBigDecimal("invitaxrate") != null) {
                            if (sessionBean.getUser().getLanguage().getId() == 1) {
                                param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate"));
                            } else {
                                param = sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate")) + "%";
                            }
                        }

                        row.createCell((short) b++).setCellValue((param));
                    }

                    if (toogleList.get(15)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("invitotaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(16)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("invitotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            jRow++;
            SXSSFRow rowEmpty1 = excelDocument.getSheet().createRow(jRow++);

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(sessionBean.getLoc().getString("totalpurchasequantity") + " " + subTotalPurchaseQuantity);
            cell.setCellStyle(stylefooter);

            SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell2 = row2.createCell((short) 0);
            cell2.setCellValue(sessionBean.getLoc().getString("totalprice") + " " + subTotal);
            cell2.setCellStyle(stylefooter);

            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("purchasedetailreport"));
            } catch (IOException ex) {
                Logger.getLogger(PurchaseDetailReportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseDetailReportService.class.getName()).log(Level.SEVERE, null, ex);

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
                Logger.getLogger(PurchaseDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, PurchaseDetailReport purchaseDetailReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, String subTotalPurchaseQuantity, String subTotalMoney) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = purchaseDetailReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseDetailReportDao.exportData(where, branchList));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseDetailReport.getEndDate())).append(" </div> ");

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

            String stockName = "";
            if (purchaseDetailReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseDetailReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (purchaseDetailReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseDetailReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : purchaseDetailReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseDetailReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseDetailReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (purchaseDetailReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseDetailReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseDetailReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("supplier")).append(" : ").append(supplierName).append(" </div> ");
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
                    + "   @page { size: landscape; }"
                    + "   @media print {"
                    + "     html, body {"
                    + "    width: 210mm;"
                    + "    height: 297mm;"
                    + "     }}"
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmPurchaseDetailDatatable:dtbPurchaseDetail", toogleList, "headerBlack", sb);
            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                Currency currency = new Currency(rs.getInt("invicurrency"));

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("invinvoicedate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("invinvoicedate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(rs.getString("invoiceaccount") == null ? "" : rs.getString("invoiceaccount")).append("</td>");
                }

                if (toogleList.get(11)) {
                    sb.append("<td>").append(rs.getString("invdocumentnumber") == null ? "" : rs.getString("invdocumentserial") + rs.getString("invdocumentnumber")).append("</td>");
                }

                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getBigDecimal("inviunitprice") != null ? sessionBean.getNumberFormat().format(rs.getBigDecimal("inviunitprice")) : sessionBean.getNumberFormat().format(BigDecimal.ZERO)).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("inviquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(14)) {
                    String param = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate") == null ? 0 : rs.getBigDecimal("invitaxrate"));
                    } else {
                        param = sessionBean.getNumberFormat().format(rs.getBigDecimal("invitaxrate") == null ? 0 : rs.getBigDecimal("invitaxrate")) + "%";
                    }
                    sb.append("<td style=\"text-align: right\">").append(param).append("</td>");

                }
                if (toogleList.get(15)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("invitotaltax"))))
                            .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                if (toogleList.get(16)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("invitotalmoney"))))
                            .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
            }

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalpurchasequantity")).append(" ").append(" : ")
                    .append(subTotalPurchaseQuantity).append("</td>");
            sb.append(" </tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalgiro")).append(" ").append(" : ")
                    .append(subTotalMoney).append("</td>");
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
                Logger.getLogger(PurchaseDetailReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return sb.toString();
    }

    @Override
    public String createWhere(PurchaseDetailReport obj, boolean isCentralSupplier, int supplierType) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

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
            where = where + " AND invi.stock_id IN(" + stockList + ") ";
        }

        String categoryList = "";
        for (Categorization category : obj.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            where = where + " AND invi.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
        }

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
            where = where + " AND stck.supplier_id IN(" + accountList + ") ";
        }

        if (isCentralSupplier) {
            String centralSupplierList = "";
            for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                if (centralSupplier.getId() == 0) {
                    centralSupplierList = "";
                    break;
                }
            }
            if (!centralSupplierList.equals("")) {
                centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                where = where + " AND stck.centralsupplier_id IN(" + centralSupplierList + ") ";
            } else {
                if (isCentralSupplier) {
                    if (supplierType == 0) {
                        where = where + " AND (cspp.centersuppliertype_id != 2 OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 1) {
                        where = where + " AND (cspp.centersuppliertype_id NOT IN (1,2) OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 2) {
                        where = where + " AND cspp.centersuppliertype_id = 1 ";
                    }
                }
            }
        }

        where += " AND inv.invoicedate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";
        where += " AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";

        return where;
    }

    @Override
    public List<PurchaseDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        return purchaseDetailReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList);
    }

    @Override
    public int count(String where, String branchList) {
        return purchaseDetailReportDao.count(where, branchList);
    }

    @Override
    public List<PurchaseDetailReport> totals(String where, String branchList) {
        return purchaseDetailReportDao.totals(where, branchList);
    }

}
