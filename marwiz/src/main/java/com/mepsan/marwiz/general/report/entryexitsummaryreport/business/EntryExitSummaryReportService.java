/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.dao.EntryExitSummary;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.dao.EntryExitSummaryReportDao;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.dao.IEntryExitSummaryReportDao;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class EntryExitSummaryReportService implements IEntryExitSummaryReportService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IEntryExitSummaryReportDao entryExitSummaryReportDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setEntryExitSummaryReportDao(IEntryExitSummaryReportDao entryExitSummaryReportDao) {
        this.entryExitSummaryReportDao = entryExitSummaryReportDao;
    }

    @Override
    public List<EntryExitSummary> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, EntryExitSummary obj) {
        return entryExitSummaryReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj);

    }

    @Override
    public String createWhere(EntryExitSummary obj, boolean isCentralIntegration, int supplierType) {
        String where = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND iwr.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

        String warehouseList = "";
        for (Warehouse warehouse : obj.getWarehouseList()) {
            warehouseList = warehouseList + "," + String.valueOf(warehouse.getId());
            if (warehouse.getId() == 0) {
                warehouseList = "";
                break;
            }
        }
        if (!warehouseList.equals("")) {
            warehouseList = warehouseList.substring(1, warehouseList.length());
            where = where + " AND iw.id IN(" + warehouseList + ") ";
        }

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
        for (Categorization category : obj.getListOfCategorization()) {
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

        if (isCentralIntegration) {
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
                if (isCentralIntegration) {
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

        String branchList = "";
        for (BranchSetting branchSetting : obj.getListOfBranch()) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND iw.branch_id IN(" + branchList + ") ";
        }
        return where;
    }

    @Override
    public int count(String where, EntryExitSummary obj) {
        return entryExitSummaryReportDao.count(where, obj);
    }

    @Override
    public void exportPdf(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = entryExitSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(entryExitSummaryReportDao.exportData(where, entryExitSummary));
            rs = prep.executeQuery();

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("entryexitsummaryreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (entryExitSummary.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : entryExitSummary.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String warehouseName = "";
            if (entryExitSummary.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : entryExitSummary.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (entryExitSummary.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : entryExitSummary.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (entryExitSummary.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : entryExitSummary.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (entryExitSummary.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : entryExitSummary.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (entryExitSummary.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : entryExitSummary.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmEntyExitSummaryReportDatatable:dtbEntyExitSummaryReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            String[] colums = new String[]{"brcname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", "", "", ""};

            String[] extension = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {

                formatter.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

                StaticMethods.pdfAddCell(pdfDocument, rs, toogleList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("iwname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"))) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("entry")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("exit")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("lastquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(14)) {
                    //Stok devir hızı=Satılan ürün miktarı/((Devir miktarı+Kalan miktar) / 2)
                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(result), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                if (toogleList.get(15)) {

                    // Stok tutma süresi=İki tarih arası gün sayısı / stok devir hızı
                    Long dayCount; //gün farkı 
                    Long difference;

                    difference = entryExitSummary.getEndDate().getTime() - entryExitSummary.getBeginDate().getTime();
                    dayCount = (Long) (difference / (1000 * 60 * 60 * 24));

                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }

                    BigDecimal stockHoldingTime = new BigDecimal(BigInteger.ZERO);
                    try {

                        stockHoldingTime = (BigDecimal.valueOf(dayCount).divide(result, 4, RoundingMode.HALF_EVEN));
                    } catch (Exception e) {
                    }

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(stockHoldingTime), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("entryexitsummaryreport"));
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
                Logger.getLogger(EntryExitSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = entryExitSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(entryExitSummaryReportDao.exportData(where, entryExitSummary));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("entryexitsummaryreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            int i = 1;
            SXSSFRow empty = excelDocument.getSheet().createRow(i);
            i++;

            SXSSFRow startdate = excelDocument.getSheet().createRow(i);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getBeginDate()));
            i++;
            SXSSFRow enddate = excelDocument.getSheet().createRow(i);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getEndDate()));
            i++;

            String branchName = "";
            if (entryExitSummary.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : entryExitSummary.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(i);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
            i++;

            String warehouseName = "";
            if (entryExitSummary.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : entryExitSummary.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            SXSSFRow warehouse = excelDocument.getSheet().createRow(i);
            warehouse.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName);
            i++;

            String stockName = "";
            if (entryExitSummary.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : entryExitSummary.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(i);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);
            i++;

            String categoryName = "";
            if (entryExitSummary.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : entryExitSummary.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            SXSSFRow category = excelDocument.getSheet().createRow(i);
            category.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);
            i++;

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (entryExitSummary.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : entryExitSummary.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(i);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
                i++;
            } else {
                String supplierName = "";
                if (entryExitSummary.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : entryExitSummary.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(i);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
                i++;
            }

            SXSSFRow rwempty = excelDocument.getSheet().createRow(i);
            i++;

            StaticMethods.createHeaderExcel("frmEntyExitSummaryReportDatatable:dtbEntyExitSummaryReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            i++;

            String[] colums = new String[]{"brcname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", "", "", ""};

            String[] extension = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

                StaticMethods.excelAddCell(row, rs, toogleList, colums, excelDocument.getDateFormatStyle(), excelDocument.getWorkbook().getCreationHelper(), sessionBean.getUser(), sessionBean.getNumberFormat(), extension);

                if (toogleList.get(5)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(6)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(7)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(8)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(9)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(rs.getString("iwname"));
                }

                if (toogleList.get(10)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round((rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"))).doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(11)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("entry").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(12)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("exit").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(13)) {
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("lastquantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(14)) {
                    //Stok devir hızı=Satılan ürün miktarı/((Devir miktarı+Kalan miktar) / 2)
                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }
                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(result.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                if (toogleList.get(15)) {

                    // Stok tutma süresi=İki tarih arası gün sayısı / stok devir hızı
                    Long dayCount; //gün farkı 
                    Long difference;

                    difference = entryExitSummary.getEndDate().getTime() - entryExitSummary.getBeginDate().getTime();
                    dayCount = (Long) (difference / (1000 * 60 * 60 * 24));

                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }

                    BigDecimal stockHoldingTime = new BigDecimal(BigInteger.ZERO);
                    try {

                        stockHoldingTime = (BigDecimal.valueOf(dayCount).divide(result, 4, RoundingMode.HALF_EVEN));
                    } catch (Exception e) {
                    }

                    SXSSFCell quantity = row.createCell((short) row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
                    quantity.setCellValue(StaticMethods.round(stockHoldingTime.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                i++;
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("entryexitsummaryreport"));
            } catch (IOException ex) {
                Logger.getLogger(EntryExitSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(EntryExitSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, EntryExitSummary entryExitSummary, List<Boolean> toogleList, boolean isCentralSupplier) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = entryExitSummaryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(entryExitSummaryReportDao.exportData(where, entryExitSummary));
            rs = prep.executeQuery();

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), entryExitSummary.getEndDate())).append(" </div> ");

            String branchName = "";
            if (entryExitSummary.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : entryExitSummary.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String warehouseName = "";
            if (entryExitSummary.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : entryExitSummary.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehouse")).append(" : ").append(warehouseName).append(" </div> ");

            String stockName = "";
            if (entryExitSummary.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : entryExitSummary.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (entryExitSummary.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (entryExitSummary.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : entryExitSummary.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (entryExitSummary.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : entryExitSummary.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (entryExitSummary.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (entryExitSummary.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : entryExitSummary.getListOfAccount()) {
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
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmEntyExitSummaryReportDatatable:dtbEntyExitSummaryReport", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            String[] colums = new String[]{"brcname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", "", "", "", ""};
            String[] extensions = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                formatter.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitrounding"));
                sb.append(" <tr> ");

                StaticMethods.printAddCell(sb, rs, toogleList, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extensions);

                if (toogleList.get(5)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("iwname") == null ? "" : rs.getString("iwname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry")))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("entry"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("exit"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("lastquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }

                if (toogleList.get(14)) {
                    //Stok devir hızı=Satılan ürün miktarı/((Devir miktarı+Kalan miktar) / 2)
                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(result)).append("</td>");

                }

                if (toogleList.get(15)) {

                    // Stok tutma süresi=İki tarih arası gün sayısı / stok devir hızı
                    Long dayCount; //gün farkı 
                    Long difference;

                    difference = entryExitSummary.getEndDate().getTime() - entryExitSummary.getBeginDate().getTime();
                    dayCount = (Long) (difference / (1000 * 60 * 60 * 24));

                    BigDecimal transferringBalance = new BigDecimal(BigInteger.ZERO);
                    transferringBalance = rs.getBigDecimal("exit").add(rs.getBigDecimal("lastquantity")).subtract(rs.getBigDecimal("entry"));

                    BigDecimal result = new BigDecimal(BigInteger.ZERO);
                    try {
                        result = rs.getBigDecimal("exit").divide((transferringBalance.add(rs.getBigDecimal("lastquantity")).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);
                    } catch (Exception e) {
                    }

                    BigDecimal stockHoldingTime = new BigDecimal(BigInteger.ZERO);
                    try {

                        stockHoldingTime = (BigDecimal.valueOf(dayCount).divide(result, 4, RoundingMode.HALF_EVEN));
                    } catch (Exception e) {
                    }

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(stockHoldingTime)).append("</td>");
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
                Logger.getLogger(EntryExitSummaryReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

}
