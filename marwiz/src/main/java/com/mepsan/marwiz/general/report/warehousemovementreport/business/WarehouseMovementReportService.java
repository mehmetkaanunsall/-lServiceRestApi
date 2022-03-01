/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.warehousemovementreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.warehousemovementreport.dao.IWarehouseMovementReportDao;
import com.mepsan.marwiz.general.report.warehousemovementreport.dao.WarehouseMovementReport;
import com.mepsan.marwiz.general.report.warehousemovementreport.dao.WarehouseMovementReportDao;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class WarehouseMovementReportService implements IWarehouseMovementReportService {

    @Autowired
    IWarehouseMovementReportDao iWarehouseMovementReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setiWarehouseMovementReportDao(IWarehouseMovementReportDao iWarehouseMovementReportDao) {
        this.iWarehouseMovementReportDao = iWarehouseMovementReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(WarehouseMovementReport obj, int supplierType, boolean isCentralSupplier) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String stockList = "";
        for (Stock stock : obj.getSelectedStocks()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND iwm.stock_id IN(" + stockList + ") ";
        }

        String warehouseList = "";
        for (Warehouse warehouse : obj.getSelectedWarehouses()) {
            warehouseList = warehouseList + "," + String.valueOf(warehouse.getId());
            if (warehouse.getId() == 0) {
                warehouseList = "";
                break;
            }
        }
        if (!warehouseList.equals("")) {
            warehouseList = warehouseList.substring(1, warehouseList.length());
            where = where + " AND iwm.warehouse_id IN(" + warehouseList + ") ";
        }

        if (obj.getIsDirection() == 1) {
            where = where + " AND iwm.is_direction IS TRUE  ";
        } else if (obj.getIsDirection() == 2) {
            where = where + " AND iwm.is_direction IS FALSE ";
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

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
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

        if (obj.getType().getId() != 0) {
            where = where + " AND iwr.type_id = " + obj.getType().getId() + " ";
        }

        where += " AND iwr.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";
        return where;
    }

    @Override
    public List<WarehouseMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WarehouseMovementReport obj) {
        return iWarehouseMovementReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj);
    }

    @Override
    public void exportPdf(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        int numberOfColumns = toogleList.size();
        try {
            connection = iWarehouseMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iWarehouseMovementReportDao.exportData(where, warehouseMovementReport));
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousemovementreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String warehouseName = "";
            if (warehouseMovementReport.getSelectedWarehouses().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : warehouseMovementReport.getSelectedWarehouses()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String typeName = "";
            if (warehouseMovementReport.getType().getId() == 0) {
                typeName = sessionBean.getLoc().getString("all");
            } else {
                typeName = typeName + warehouseMovementReport.getType().getTag();

            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("type") + " : " + typeName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String entryexit = warehouseMovementReport.getIsDirection() == 1 ? sessionBean.getLoc().getString("entry") : warehouseMovementReport.getIsDirection() == 2 ? sessionBean.getLoc().getString("exit") : sessionBean.getLoc().getString("all");
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("entry") + " / " + sessionBean.getLoc().getString("exit") + " : " + entryexit, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (warehouseMovementReport.getSelectedStocks().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (warehouseMovementReport.getSelectedStocks().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : warehouseMovementReport.getSelectedStocks()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (warehouseMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : warehouseMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (warehouseMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : warehouseMovementReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmWarehouseMovementReportDatatable:dtbWarehouseMovementReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                Currency currencyPrice = new Currency(rs.getInt("currency_id"));
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("iwrprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("iwname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("iwmis_direction") ? sessionBean.getLoc().getString("entry") : sessionBean.getLoc().getString("exit"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("category")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("iwmquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((rs.getBigDecimal("price") == null ? BigDecimal.ZERO : sessionBean.getNumberFormat().format(rs.getBigDecimal("price"))) + (currencyPrice.getId() == 0 ? sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) : sessionBean.currencySignOrCode(currencyPrice.getId(), 0)), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((rs.getBigDecimal("totaltax") == null ? BigDecimal.ZERO : sessionBean.getNumberFormat().format(rs.getBigDecimal("totaltax"))) + sessionBean.currencySignOrCode(currencyPrice.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((rs.getBigDecimal("totalmoney") == null ? BigDecimal.ZERO : sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney"))) + sessionBean.currencySignOrCode(currencyPrice.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            for (Map.Entry<Integer, WarehouseMovementReport> entry : currencyTotalsCollection.entrySet()) {
                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + "  "
                        + sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getQuantity()) + " "
                        + sessionBean.getLoc().getString("taxprice") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalTax()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0) + " "
                        + sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("warehousemovementreport"));

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
                Logger.getLogger(WarehouseMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = iWarehouseMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iWarehouseMovementReportDao.exportData(where, warehouseMovementReport));
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("warehousemovementreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getEndDate()));

            String warehouseName = "";
            if (warehouseMovementReport.getSelectedWarehouses().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : warehouseMovementReport.getSelectedWarehouses()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            SXSSFRow warehouse = excelDocument.getSheet().createRow(4);
            warehouse.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName);

            String typeName = "";
            if (warehouseMovementReport.getType().getId() == 0) {
                typeName = sessionBean.getLoc().getString("all");
            } else {
                typeName = typeName + warehouseMovementReport.getType().getTag();

            }
            SXSSFRow type = excelDocument.getSheet().createRow(5);
            type.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("type") + " : " + typeName);

            String entryexit = warehouseMovementReport.getIsDirection() == 1 ? sessionBean.getLoc().getString("entry") : warehouseMovementReport.getIsDirection() == 2 ? sessionBean.getLoc().getString("exit") : sessionBean.getLoc().getString("all");
            SXSSFRow entry = excelDocument.getSheet().createRow(6);
            entry.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("entry") + " / " + sessionBean.getLoc().getString("exit") + " : " + entryexit);

            String stockName = "";
            if (warehouseMovementReport.getSelectedStocks().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (warehouseMovementReport.getSelectedStocks().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : warehouseMovementReport.getSelectedStocks()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(6);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (warehouseMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : warehouseMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(7);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (warehouseMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : warehouseMovementReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(7);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow empty5 = excelDocument.getSheet().createRow(8);

            StaticMethods.createHeaderExcel("frmWarehouseMovementReportDatatable:dtbWarehouseMovementReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            int i = 10;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);
                if (toogleList.get(0)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getTimestamp("iwrprocessdate"));
                    cell.setCellStyle(excelDocument.getDateFormatStyle());
                }
                if (toogleList.get(1)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getString("iwname"));
                }
                if (toogleList.get(2)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getBoolean("iwmis_direction") ? sessionBean.getLoc().getString("entry") : sessionBean.getLoc().getString("exit"));
                }
                if (toogleList.get(3)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(4)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getString("stckcenterproductcode"));
                }
                if (toogleList.get(5)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(6)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(11)) {
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(StaticMethods.round(rs.getDouble("iwmquantity"), rs.getInt("guntunitsorting")));
                }

                if (toogleList.get(12)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(rs.getBigDecimal("price") == null ? BigDecimal.ZERO.doubleValue() : StaticMethods.round(rs.getBigDecimal("price").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                if (toogleList.get(13)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(rs.getBigDecimal("totaltax") == null ? BigDecimal.ZERO.doubleValue() : StaticMethods.round(rs.getBigDecimal("totaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(14)) {
                    SXSSFCell total = row.createCell((short) b++);
                    total.setCellValue(rs.getBigDecimal("totalmoney") == null ? BigDecimal.ZERO.doubleValue() : StaticMethods.round(rs.getBigDecimal("totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                i++;
            }

            for (Map.Entry<Integer, WarehouseMovementReport> ent : currencyTotalsCollection.entrySet()) {

                CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle2.setAlignment(HorizontalAlignment.LEFT);
                SXSSFRow rowf1 = excelDocument.getSheet().createRow(i++);

                SXSSFCell e0 = rowf1.createCell((short) 0);
                e0.setCellValue(sessionBean.getLoc().getString("sum"));
                e0.setCellStyle(cellStyle2);

                SXSSFCell e1 = rowf1.createCell((short) 1);
                e1.setCellValue(sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(ent.getValue().getQuantity()) + " ");
                e1.setCellStyle(cellStyle2);

                SXSSFCell e2 = rowf1.createCell((short) 2);
                e2.setCellValue(sessionBean.getLoc().getString("taxprice") + " : " + sessionBean.getNumberFormat().format(ent.getValue().getTotalTax()) + sessionBean.currencySignOrCode(ent.getValue().getCurrency().getId(), 0) + " ");
                e2.setCellStyle(cellStyle2);

                SXSSFCell e3 = rowf1.createCell((short) 3);
                e3.setCellValue(sessionBean.getLoc().getString("totalprice") + " : " + sessionBean.getNumberFormat().format(ent.getValue().getTotalMoney()) + sessionBean.currencySignOrCode(ent.getValue().getCurrency().getId(), 0) + " ");
                e3.setCellStyle(cellStyle2);

            }

            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("warehousemovementreport"));
            } catch (IOException ex) {
                Logger.getLogger(WarehouseMovementReportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(WarehouseMovementReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(WarehouseMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, WarehouseMovementReport warehouseMovementReport, List<Boolean> toogleList, boolean isCentralSupplier, Map<Integer, WarehouseMovementReport> currencyTotalsCollection) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        int numberOfColumns = toogleList.size();

        try {
            connection = iWarehouseMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iWarehouseMovementReportDao.exportData(where, warehouseMovementReport));
            prep.setInt(1, sessionBean.getUser().getLastBranch().getId());
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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), warehouseMovementReport.getEndDate())).append(" </div> ");

            String warehouseName = "";
            if (warehouseMovementReport.getSelectedWarehouses().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : warehouseMovementReport.getSelectedWarehouses()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehouse")).append(" : ").append(warehouseName).append(" </div> ");

            String typeName = "";
            if (warehouseMovementReport.getType().getId() == 0) {
                typeName = sessionBean.getLoc().getString("all");
            } else {
                typeName = typeName + warehouseMovementReport.getType().getTag();

            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("type")).append(" : ").append(typeName).append(" </div> ");

            String entryexit = warehouseMovementReport.getIsDirection() == 1 ? sessionBean.getLoc().getString("entry") : warehouseMovementReport.getIsDirection() == 2 ? sessionBean.getLoc().getString("exit") : sessionBean.getLoc().getString("all");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("entry")).append(" / ").append(sessionBean.getLoc().getString("exit")).append(" : ").append(entryexit).append(" </div> ");

            String stockName = "";
            if (warehouseMovementReport.getSelectedStocks().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (warehouseMovementReport.getSelectedStocks().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : warehouseMovementReport.getSelectedStocks()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");
            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (warehouseMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : warehouseMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (warehouseMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (warehouseMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : warehouseMovementReport.getListOfAccount()) {
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
                    + "            font-size: 14px;"
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

            StaticMethods.createHeaderPrint("frmWarehouseMovementReportDatatable:dtbWarehouseMovementReport", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            while (rs.next()) {
                Currency currencyPrice = new Currency(rs.getInt("currency_id"));
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("iwrprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("iwrprocessdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("iwname") == null ? "" : rs.getString("iwname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getBoolean("iwmis_direction") ? sessionBean.getLoc().getString("entry") : sessionBean.getLoc().getString("exit")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(StaticMethods.findCategories(rs.getString("category"))).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("iwmquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }

                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getBigDecimal("price") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("price"))).append(sessionBean.currencySignOrCode(currencyPrice.getId(), 0)).append("</td>");
                }

                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getBigDecimal("totaltax") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("totaltax"))).append(sessionBean.currencySignOrCode(currencyPrice.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getBigDecimal("totalmoney") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney"))).append(sessionBean.currencySignOrCode(currencyPrice.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");

            }

            for (Map.Entry<Integer, WarehouseMovementReport> entry : currencyTotalsCollection.entrySet()) {

                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                        .append(sessionBean.getLoc().getString("sum"))
                        .append(" ").append(sessionBean.getLoc().getString("quantity")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getQuantity()))
                        .append(" ").append(sessionBean.getLoc().getString("taxprice")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalTax()))
                        .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0))
                        .append(" ").append(sessionBean.getLoc().getString("totalprice")).append(" : ").append(sessionBean.getNumberFormat().format(entry.getValue().getTotalMoney()))
                        .append(sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0));

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
                Logger.getLogger(WarehouseMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<WarehouseMovementReport> totals(String where, WarehouseMovementReport obj) {
        return iWarehouseMovementReportDao.totals(where, obj);
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<WarehouseMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhere(WarehouseMovementReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
