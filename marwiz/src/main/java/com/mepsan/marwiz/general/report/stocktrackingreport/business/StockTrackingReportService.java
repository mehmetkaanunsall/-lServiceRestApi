/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 04:03:45
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.stocktrackingreport.dao.IStockTrackingReportDao;
import com.mepsan.marwiz.general.report.stocktrackingreport.dao.StockTrackingReport;
import com.mepsan.marwiz.general.report.stocktrackingreport.dao.StockTrackingReportDao;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class StockTrackingReportService implements IStockTrackingReportService {

    @Autowired
    private IStockTrackingReportDao stockTrackingReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setStockTrackingReportDao(IStockTrackingReportDao stockTrackingReportDao) {
        this.stockTrackingReportDao = stockTrackingReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(StockTrackingReport obj, List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier) {
        String where = "";
        String branchID = "";
        boolean isCentralIntegration = false;

        for (BranchSetting brs : branchList) {
            branchID = branchID + " , " + String.valueOf(brs.getBranch().getId());
            if (brs.isIsCentralIntegration()) {
                isCentralIntegration = true;
            }
            if (brs.getBranch().getId() == 0) {
                branchID = "";
                break;
            }
        }

        if (!branchID.equals("")) {
            branchID = branchID.substring(3, branchID.length());
        }

        String stockList = "";
        for (Stock stock : obj.getListOfStock()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND iwi.stock_id IN(" + stockList + ") ";
        }

        String warehouseList = "";
        for (Warehouse warehouse : obj.getListOfWarehouse()) {
            warehouseList = warehouseList + "," + String.valueOf(warehouse.getId());
            if (warehouse.getId() == 0) {
                warehouseList = "";
                break;
            }
        }
        if (!warehouseList.equals("")) {
            warehouseList = warehouseList.substring(1, warehouseList.length());
            where = where + " AND iwi.warehouse_id IN(" + warehouseList + ") ";
        } else {//Depo hepsi seçildiyse branch id ye göre deponun itemları listelenir
            where = where + " AND iw.branch_id IN(" + branchID + ") ";
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

        return where;
    }

    @Override
    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        return stockTrackingReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList);
    }

    @Override
    public int count(String where, String branchList) {
        return stockTrackingReportDao.count(where, branchList);
    }

    @Override
    public void exportPdf(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = stockTrackingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTrackingReportDao.exportData(where, branchID));

            rs = prep.executeQuery();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stocklastsituationreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            String branchName = "";
            if (branchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brs : branchList) {
                    branchName += " , " + brs.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (stockTrackingReport.getListOfStock().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (stockTrackingReport.getListOfStock().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockTrackingReport.getListOfStock()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String warehouseName = "";
            if (stockTrackingReport.getListOfWarehouse().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : stockTrackingReport.getListOfWarehouse()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (stockTrackingReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : stockTrackingReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (stockTrackingReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : stockTrackingReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmStockTracking:dtbStockTracking", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
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
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    Currency currency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                    if (rs.getBigDecimal("sicurrentpurchaseprice") != null) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));

                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));

                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    Currency currency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                    if (rs.getBigDecimal("sicurrentpurchasepricewithtax") != null) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchasepricewithtax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));

                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));

                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(11)) {
                    Currency currency = new Currency(rs.getInt("pllcurrency_id"));
                    if (rs.getBigDecimal("pllpricewithouttax") != null) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("pllpricewithouttax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));

                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));

                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {
                    Currency currency = new Currency(rs.getInt("pllcurrency_id"));
                    if (rs.getBigDecimal("pllprice") != null) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("pllprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));

                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase("-", pdfDocument.getFont()));

                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sumquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("stocklastsituationreport"));

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
                Logger.getLogger(StockTrackingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = stockTrackingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTrackingReportDao.exportData(where, branchID));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("stocklastsituationreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            String branchName = "";
            if (branchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brs : branchList) {
                    branchName += " , " + brs.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(2);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String stockName = "";
            if (stockTrackingReport.getListOfStock().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (stockTrackingReport.getListOfStock().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockTrackingReport.getListOfStock()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(3);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String warehouseName = "";
            if (stockTrackingReport.getListOfWarehouse().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : stockTrackingReport.getListOfWarehouse()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            SXSSFRow warehouse = excelDocument.getSheet().createRow(4);
            warehouse.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (stockTrackingReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : stockTrackingReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(5);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (stockTrackingReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : stockTrackingReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(5);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow emptyRow = excelDocument.getSheet().createRow(6);

            StaticMethods.createHeaderExcel("frmStockTracking:dtbStockTracking", toogleList, "headerBlack", excelDocument.getWorkbook());

            int i = 8;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));

                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));

                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));

                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));

                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("category")));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }

                if (toogleList.get(9)) {
                    SXSSFCell price = row.createCell((short) b++);
                    if (rs.getBigDecimal("sicurrentpurchaseprice") != null) {
                        price.setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        price.setCellValue(StaticMethods.round(0, rs.getInt("guntunitsorting")));
                    }
                }
                if (toogleList.get(10)) {
                    SXSSFCell price = row.createCell((short) b++);
                    if (rs.getBigDecimal("sicurrentpurchasepricewithtax") != null) {
                        price.setCellValue(StaticMethods.round(rs.getBigDecimal("sicurrentpurchasepricewithtax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        price.setCellValue(StaticMethods.round(0, rs.getInt("guntunitsorting")));
                    }
                }

                if (toogleList.get(11)) {
                    SXSSFCell price = row.createCell((short) b++);
                    if (rs.getBigDecimal("pllpricewithouttax") != null) {
                        price.setCellValue(StaticMethods.round(rs.getBigDecimal("pllpricewithouttax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        price.setCellValue(StaticMethods.round(0, rs.getInt("guntunitsorting")));
                    }
                }
                if (toogleList.get(12)) {
                    SXSSFCell price = row.createCell((short) b++);
                    if (rs.getBigDecimal("pllprice") != null) {
                        price.setCellValue(StaticMethods.round(rs.getBigDecimal("pllprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        price.setCellValue(StaticMethods.round(0, rs.getInt("guntunitsorting")));
                    }
                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("sumquantity").doubleValue(), rs.getInt("guntunitsorting")));
                }

                i++;
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("stocklastsituationreport"));
            } catch (IOException ex) {
                Logger.getLogger(StockTrackingReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(StockTrackingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, StockTrackingReport stockTrackingReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> branchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = stockTrackingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockTrackingReportDao.exportData(where, branchID));
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

            String branchName = "";
            if (branchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brs : branchList) {
                    branchName += " , " + brs.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String stockName = "";
            if (stockTrackingReport.getListOfStock().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (stockTrackingReport.getListOfStock().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockTrackingReport.getListOfStock()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String warehouseName = "";
            if (stockTrackingReport.getListOfWarehouse().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : stockTrackingReport.getListOfWarehouse()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehouse")).append(" : ").append(warehouseName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (stockTrackingReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : stockTrackingReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (stockTrackingReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (stockTrackingReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : stockTrackingReport.getListOfAccount()) {
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

            StaticMethods.createHeaderPrint("frmStockTracking:dtbStockTracking", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");

            while (rs.next()) {
                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
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
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    Currency currency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                    if (rs.getBigDecimal("sicurrentpurchaseprice") != null) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append("</td>");

                    }

                }
                if (toogleList.get(10)) {
                    Currency currency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                    if (rs.getBigDecimal("sicurrentpurchasepricewithtax") != null) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append("</td>");

                    }

                }

                if (toogleList.get(11)) {
                    Currency currency = new Currency(rs.getInt("pllcurrency_id"));
                    if (rs.getBigDecimal("pllpricewithouttax") != null) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("pllpricewithouttax"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append("</td>");

                    }

                }
                if (toogleList.get(12)) {
                    Currency currency = new Currency(rs.getInt("pllcurrency_id"));
                    if (rs.getBigDecimal("pllprice") != null) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("pllprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append("-").append("</td>");

                    }

                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("sumquantity"))).append(rs.getString("guntsortname")).append("</td>");
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
                Logger.getLogger(StockTrackingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<Warehouse> listWarehouse(String branchList) {
        return stockTrackingReportDao.listWarehouse(branchList);
    }

    @Override
    public String createWhere(StockTrackingReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
