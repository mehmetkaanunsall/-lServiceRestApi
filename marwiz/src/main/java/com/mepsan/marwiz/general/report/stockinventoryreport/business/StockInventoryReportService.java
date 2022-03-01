/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.report.stockinventoryreport.dao.IStockInventoryReportDao;
import com.mepsan.marwiz.general.report.stockinventoryreport.dao.StockInventoryReport;
import java.awt.Color;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class StockInventoryReportService implements IStockInventoryReportService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    IStockInventoryReportDao stockInventoryReportDao;

    private Currency purchaseCurrency;

    private Map<Integer, BigDecimal> currencyTotals = new HashMap<>();

    private Currency saleCurrency;

    public Map<Integer, BigDecimal> getCurrencyTotals() {
        return currencyTotals;
    }

    public void setCurrencyTotals(Map<Integer, BigDecimal> currencyTotals) {
        this.currencyTotals = currencyTotals;
    }

    public Currency getPurchaseCurrency() {
        return purchaseCurrency;
    }

    public void setPurchaseCurrency(Currency purchaseCurrency) {
        this.purchaseCurrency = purchaseCurrency;
    }

    public Currency getSaleCurrency() {
        return saleCurrency;
    }

    public void setSaleCurrency(Currency saleCurrency) {
        this.saleCurrency = saleCurrency;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockInventoryReportDao(IStockInventoryReportDao stockInventoryReportDao) {
        this.stockInventoryReportDao = stockInventoryReportDao;
    }

    @Override
    public String createWhere(StockInventoryReport obj, int centralIntegrationIf) {
        String where = " ";
        return where;
    }

    @Override
    public List<StockInventoryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockInventoryReport obj, String whereBranch, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier) {
        return stockInventoryReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj, whereBranch, centralIntegrationIf, isCentralBranch, supplierType, isCentralSupplier);
    }

    @Override
    public List<StockInventoryReport> totals(String where, StockInventoryReport obj, String whereBranch, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier) {
        return stockInventoryReportDao.totals(where, obj, whereBranch, centralIntegrationIf, isCentralBranch, supplierType, isCentralSupplier);
    }

    public NumberFormat getNumberFormat() {
        NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
        decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbolsUnit.setCurrencySymbol("");
        ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);
        return formatterUnit;
    }

    @Override
    public void exportPdf(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        int numberColumn = toogleList.size();
        try {
            connection = stockInventoryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockInventoryReportDao.exportData(where, stockInventoryReport, branchList, centralIngetrationInf, isCentralBranch, supplierType, isCentralSupplier));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("productinventoryreport"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("date") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), stockInventoryReport.getDate()), pdfDocument.getFont()));
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

            String stockname = "";
            if (stockInventoryReport.getListOfStock().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (stockInventoryReport.getListOfStock().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockInventoryReport.getListOfStock()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockname, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (stockInventoryReport.getReportType() == 2) {
                String categoryName = "";
                if (stockInventoryReport.getListOfStockCategorization().isEmpty()) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else if (stockInventoryReport.getListOfStockCategorization().get(0).getId() == 0) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else {
                    for (Categorization c : stockInventoryReport.getListOfStockCategorization()) {
                        categoryName += " , " + c.getName();
                    }
                    categoryName = categoryName.substring(3, categoryName.length());
                }
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            if (stockInventoryReport.getReportType() == 3) {
                if (isCentralSupplier) {
                    String centralSupplierName = "";
                    if (stockInventoryReport.getListOfCentralSupplier().isEmpty()) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else {
                        for (CentralSupplier s : stockInventoryReport.getListOfCentralSupplier()) {
                            centralSupplierName += " , " + s.getName();
                        }
                        centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                    }

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                } else {
                    String accountName = "";
                    if (stockInventoryReport.getListOfAccount().isEmpty()) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfAccount().get(0).getId() == 0) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else {
                        for (Account account : stockInventoryReport.getListOfAccount()) {
                            accountName += " , " + account.getName();
                        }
                        accountName = accountName.substring(3, accountName.length());
                    }
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("account") + " : " + accountName, pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                }

            }

            switch (stockInventoryReport.getCost()) {
                case 1:
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("lastprice"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                case 2:
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("fifo"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                default:
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("weightedaverage"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("ip.is_taxincluded") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isIsTax() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("retail") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isRetailStock() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("includethosewhoarefinishedinstock") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isZeroStock() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("givingminusproduct") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isMinusStock() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("showonlyproductsgivingminus") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isOnlyMinusStock() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("showonlyproductsnotforsale") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isOnlyNotForSaleStock() == true ? "yes" : "no"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmStockInventoryReportDataTable:dtbInventoryReport", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {
                if (stockInventoryReport.getCost() == 2 || stockInventoryReport.getCost() == 3) {
                    purchaseCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                } else {
                    purchaseCurrency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                }
                saleCurrency = new Currency(rs.getInt("sicurrentsalecurrency_id"));

                if (stockInventoryReport.getReportType() == 1 || stockInventoryReport.getReportType() == 2 || stockInventoryReport.getReportType() == 3) {
                    getNumberFormat().setMinimumFractionDigits(rs.getInt("guntunitrounding"));
                    getNumberFormat().setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                }

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
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        pdfDocument.getRightCell().setPhrase(new Phrase("%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("tgrate")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("tgrate")) + "%", pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                }

                if (toogleList.get(10)) {
                    if (stockInventoryReport.getReportType() == 4 || stockInventoryReport.getReportType() == 5) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(getNumberFormat().format(rs.getBigDecimal("quantity")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    } else {
                        pdfDocument.getRightCell().setPhrase(new Phrase(getNumberFormat().format(rs.getBigDecimal("quantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice")) + sessionBean.currencySignOrCode(purchaseCurrency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(12)) {

                    pdfDocument.getRightCell().setPhrase(new Phrase((rs.getBigDecimal("purchasecost") == null ? BigDecimal.valueOf(0) : sessionBean.getNumberFormat().format(rs.getBigDecimal("purchasecost"))) + sessionBean.currencySignOrCode(purchaseCurrency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentsaleprice")) + sessionBean.currencySignOrCode(saleCurrency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase((rs.getBigDecimal("salecost") == null ? BigDecimal.valueOf(0) : sessionBean.getNumberFormat().format(rs.getBigDecimal("salecost"))) + sessionBean.currencySignOrCode(saleCurrency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalpurchasecost") + " : " + totals(2, stockInventoryReport, listOfTotals), pdfDocument.getFont()));
            pdfDocument.getRightCell().setColspan(numberColumn);
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalsalecost") + " : " + totals(1, stockInventoryReport, listOfTotals), pdfDocument.getFont()));
            pdfDocument.getRightCell().setColspan(numberColumn);
            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("productinventoryreport"));

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
            }
        }
    }

    public String totals(int type, StockInventoryReport stockInventory, List<StockInventoryReport> listOfTotals) {
        String total = "";
        currencyTotals.clear();
        for (StockInventoryReport stockInventoryReport : listOfTotals) {

            if (type == 1) {
                if (stockInventoryReport.getLastSaleCurreny().getId() != 0) {
                    if (currencyTotals.containsKey(stockInventoryReport.getLastSaleCurreny().getId())) {
                        BigDecimal old = currencyTotals.get(stockInventoryReport.getLastSaleCurreny().getId());
                        currencyTotals.put(stockInventoryReport.getLastSaleCurreny().getId(), old.add(stockInventoryReport.getLastSaleCost()));
                    } else {
                        currencyTotals.put(stockInventoryReport.getLastSaleCurreny().getId(), stockInventoryReport.getLastSaleCost());

                    }
                }

            } else {

                if (stockInventory.getCost() == 2 || stockInventory.getCost() == 3) {
                    stockInventoryReport.getLastPurchaseCurreny().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
                }
                if (stockInventoryReport.getLastPurchaseCurreny().getId() != 0) {

                    if (currencyTotals.containsKey(stockInventoryReport.getLastPurchaseCurreny().getId())) {
                        BigDecimal old = currencyTotals.get(stockInventoryReport.getLastPurchaseCurreny().getId());
                        currencyTotals.put(stockInventoryReport.getLastPurchaseCurreny().getId(), old.add(stockInventoryReport.getLastPurchaseCost()));
                    } else {
                        currencyTotals.put(stockInventoryReport.getLastPurchaseCurreny().getId(), stockInventoryReport.getLastPurchaseCost());

                    }
                }
            }

        }
        for (Map.Entry<Integer, BigDecimal> entry : currencyTotals.entrySet()) {
            total += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0) + " + ";
        }
        if (total.isEmpty() || total.equals("")) {
            total = "0 " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        } else {
            total = total.substring(0, total.length() - 2);
        }
        return total;
    }

    @Override
    public void exportExcel(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = stockInventoryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockInventoryReportDao.exportData(where, stockInventoryReport, branchList, centralIngetrationInf, isCentralBranch, supplierType, isCentralSupplier));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("productinventoryreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            jRow++;
            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("date") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), stockInventoryReport.getDate()));

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting : selectedBranchList) {
                    branchName += " , " + branchSetting.getBranch().getName();
                }
                branchName = branchName.substring(2, branchName.length());
            }
            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String stockname = "";
            if (stockInventoryReport.getListOfStock().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (stockInventoryReport.getListOfStock().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockInventoryReport.getListOfStock()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockname);

            if (stockInventoryReport.getReportType() == 2) {
                String categoryName = "";
                if (stockInventoryReport.getListOfStockCategorization().isEmpty()) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else if (stockInventoryReport.getListOfStockCategorization().get(0).getId() == 0) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else {
                    for (Categorization c : stockInventoryReport.getListOfStockCategorization()) {
                        categoryName += " , " + c.getName();
                    }
                    categoryName = categoryName.substring(3, categoryName.length());
                }
                SXSSFRow stockCategory = excelDocument.getSheet().createRow(jRow++);
                stockCategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);
            }
            if (stockInventoryReport.getReportType() == 3) {
                if (isCentralSupplier) {
                    String centralSupplierName = "";
                    if (stockInventoryReport.getListOfCentralSupplier().isEmpty()) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else {
                        for (CentralSupplier s : stockInventoryReport.getListOfCentralSupplier()) {
                            centralSupplierName += " , " + s.getName();
                        }
                        centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                    }

                    SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                    centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
                } else {
                    String accountName = "";
                    if (stockInventoryReport.getListOfAccount().isEmpty()) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfAccount().get(0).getId() == 0) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else {
                        for (Account account : stockInventoryReport.getListOfAccount()) {
                            accountName += " , " + account.getName();
                        }
                        accountName = accountName.substring(3, accountName.length());
                    }
                    SXSSFRow account = excelDocument.getSheet().createRow(jRow++);
                    account.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("account") + " : " + accountName);
                }
            }

            switch (stockInventoryReport.getCost()) {
                case 1:
                    SXSSFRow cost = excelDocument.getSheet().createRow(jRow++);
                    cost.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("lastprice"));
                    break;
                case 2:
                    SXSSFRow cost1 = excelDocument.getSheet().createRow(jRow++);
                    cost1.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("fifo"));
                    break;
                default:
                    SXSSFRow cost2 = excelDocument.getSheet().createRow(jRow++);
                    cost2.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cost") + " : " + sessionBean.getLoc().getString("weightedaverage"));
                    break;
            }

            SXSSFRow isTaxIncluded = excelDocument.getSheet().createRow(jRow++);
            isTaxIncluded.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("ip.is_taxincluded") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isIsTax() == true ? "yes" : "no"));

            SXSSFRow isRetail = excelDocument.getSheet().createRow(jRow++);
            isRetail.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("retail") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isRetailStock() == true ? "yes" : "no"));

            SXSSFRow isIncludethosewhoarefinishedinstock = excelDocument.getSheet().createRow(jRow++);
            isIncludethosewhoarefinishedinstock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("includethosewhoarefinishedinstock") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isZeroStock() == true ? "yes" : "no"));

            SXSSFRow givingminusproduct = excelDocument.getSheet().createRow(jRow++);
            givingminusproduct.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("givingminusproduct") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isMinusStock() == true ? "yes" : "no"));

            SXSSFRow showonlyproductsgivingminus = excelDocument.getSheet().createRow(jRow++);
            showonlyproductsgivingminus.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("showonlyproductsgivingminus") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isOnlyMinusStock() == true ? "yes" : "no"));

            SXSSFRow showonlyproductsnotforsale = excelDocument.getSheet().createRow(jRow++);
            showonlyproductsnotforsale.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("showonlyproductsnotforsale") + " : " + sessionBean.getLoc().getString(stockInventoryReport.isOnlyNotForSaleStock() == true ? "yes" : "no"));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmStockInventoryReportDataTable:dtbInventoryReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

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
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("tgrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("quantity")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("sicurrentpurchaseprice")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("purchasecost") == null ? BigDecimal.valueOf(0).doubleValue() : rs.getBigDecimal("purchasecost").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round((rs.getBigDecimal("sicurrentsaleprice")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("salecost") == null ? BigDecimal.valueOf(0).doubleValue() : rs.getBigDecimal("salecost").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
            }
            CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow purchaseTotal = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell purchaseCell = purchaseTotal.createCell((short) 0);
            purchaseCell.setCellValue(sessionBean.getLoc().getString("totalpurchasecost") + " : " + totals(2, stockInventoryReport, listOfTotals));
            purchaseCell.setCellStyle(cellStyle);

            SXSSFRow saleTotal = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell saleCell = saleTotal.createCell((short) 0);
            saleCell.setCellValue(sessionBean.getLoc().getString("totalsalecost") + " : " + totals(1, stockInventoryReport, listOfTotals));
            saleCell.setCellStyle(cellStyle);
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("productinventoryreport"));
            } catch (IOException ex) {
                Logger.getLogger(StockInventoryReportService.class.getName()).log(Level.SEVERE, null, ex);
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
            }
        }
    }

    @Override
    public String exportPrinter(String where, StockInventoryReport stockInventoryReport, List<Boolean> toogleList, List<StockInventoryReport> listOfTotals, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList, boolean isCentralSupplier, boolean isCentralBranch, int supplierType) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        try {
            connection = stockInventoryReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockInventoryReportDao.exportData(where, stockInventoryReport, branchList, centralIngetrationInf, isCentralBranch, supplierType, isCentralSupplier));
            rs = prep.executeQuery();
            int numberOfColumns = 0;
            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("date")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), stockInventoryReport.getDate())).append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting : selectedBranchList) {
                    branchName += " , " + branchSetting.getBranch().getName();
                }
                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("branch")).append(" ").append(" : ").append(branchName).append("</div>");

            String stockname = "";
            if (stockInventoryReport.getListOfStock().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (stockInventoryReport.getListOfStock().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : stockInventoryReport.getListOfStock()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" : ").append(stockname);

            if (stockInventoryReport.getReportType() == 2) {
                String categoryName = "";
                if (stockInventoryReport.getListOfStockCategorization().isEmpty()) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else if (stockInventoryReport.getListOfStockCategorization().get(0).getId() == 0) {
                    categoryName = sessionBean.getLoc().getString("all");
                } else {
                    for (Categorization s : stockInventoryReport.getListOfStockCategorization()) {
                        categoryName += " , " + s.getName();
                    }
                    categoryName = categoryName.substring(3, categoryName.length());
                }
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append("</div>");
            }
            if (stockInventoryReport.getReportType() == 3) {
                if (isCentralSupplier) {
                    String centralSupplierName = "";
                    if (stockInventoryReport.getListOfCentralSupplier().isEmpty()) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfCentralSupplier().get(0).getId() == 0) {
                        centralSupplierName = sessionBean.getLoc().getString("all");
                    } else {
                        for (CentralSupplier s : stockInventoryReport.getListOfCentralSupplier()) {
                            centralSupplierName += " , " + s.getName();
                        }
                        centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                    }

                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");

                } else {
                    String accountName = "";
                    if (stockInventoryReport.getListOfAccount().isEmpty()) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else if (stockInventoryReport.getListOfAccount().get(0).getId() == 0) {
                        accountName = sessionBean.getLoc().getString("all");
                    } else {
                        for (Account account : stockInventoryReport.getListOfAccount()) {
                            accountName += " , " + account.getName();
                        }
                        accountName = accountName.substring(3, accountName.length());
                    }
                    sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("account")).append(" : ").append(accountName).append("</div>");
                }

            }

            switch (stockInventoryReport.getCost()) {
                case 1:
                    sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("cost")).append(" : ").append(sessionBean.getLoc().getString("lastprice")).append("</div>");
                    break;
                case 2:
                    sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("cost")).append(" : ").append(sessionBean.getLoc().getString("fifo")).append("</div>");
                    break;
                default:
                    sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("cost")).append(" : ").append(sessionBean.getLoc().getString("weightedaverage")).append("</div>");
                    break;
            }

            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("ip.is_taxincluded")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isIsTax() == true ? "yes" : "no")).append("</div>");
            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("retail")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isRetailStock() == true ? "yes" : "no")).append("</div>");
            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("includethosewhoarefinishedinstock")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isZeroStock() == true ? "yes" : "no")).append("</div>");
            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("givingminusproduct")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isMinusStock() == true ? "yes" : "no")).append("</div>");

            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("showonlyproductsgivingminus")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isOnlyMinusStock() == true ? "yes" : "no")).append("</div>");
            sb.append("<div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("showonlyproductsnotforsale")).append(" : ").append(sessionBean.getLoc().getString(stockInventoryReport.isOnlyNotForSaleStock() == true ? "yes" : "no")).append("</div>");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append("</div> ");

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

            StaticMethods.createHeaderPrint("frmStockInventoryReportDataTable:dtbInventoryReport", toogleList, "headerBlack", sb);

            while (rs.next()) {
                if (stockInventoryReport.getCost() == 2 || stockInventoryReport.getCost() == 3) {
                    purchaseCurrency = sessionBean.getUser().getLastBranch().getCurrency();
                } else {
                    purchaseCurrency = new Currency(rs.getInt("sicurrentpurchasecurrency_id"));
                }
                saleCurrency = new Currency(rs.getInt("sicurrentsalecurrency_id"));

                if (stockInventoryReport.getReportType() == 1 || stockInventoryReport.getReportType() == 2 || stockInventoryReport.getReportType() == 3) {
                    getNumberFormat().setMinimumFractionDigits(rs.getInt("guntunitrounding"));
                    getNumberFormat().setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                }

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getString("brnname") == null ? "" : rs.getString("brnname")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
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
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        sb.append("<td style=\"text-align: right\">").append("%").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("tgrate"))).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("tgrate"))).append("%").append("</td>");
                    }
                }
                if (toogleList.get(10)) {
                    if (stockInventoryReport.getReportType() == 4 || stockInventoryReport.getReportType() == 5) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("quantity"))).append("</td>");
                    } else {
                        sb.append("<td style=\"text-align: right\">").append(getNumberFormat().format(rs.getBigDecimal("quantity"))).append(rs.getString("guntsortname")).append("</td>");
                    }
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentpurchaseprice"))).append(sessionBean.currencySignOrCode(purchaseCurrency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append((rs.getBigDecimal("purchasecost") == null ? BigDecimal.valueOf(0) : sessionBean.getNumberFormat().format(rs.getBigDecimal("purchasecost")))).append(sessionBean.currencySignOrCode(purchaseCurrency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sicurrentsaleprice"))).append(sessionBean.currencySignOrCode(saleCurrency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("salecost") == null ? BigDecimal.valueOf(0).doubleValue() : rs.getBigDecimal("salecost").doubleValue())).append(sessionBean.currencySignOrCode(saleCurrency.getId(), 0)).append("</td>");
                }
            }

            sb.append(" <tr> ");
            sb.append("<td style=\"text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                      .append(sessionBean.getLoc().getString("totalpurchasecost")).append(" : ").append(totals(2, stockInventoryReport, listOfTotals))
                      .append("</td>");
            sb.append(" </tr> ");

            sb.append(" <tr> ");
            sb.append("<td style=\"text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                      .append(sessionBean.getLoc().getString("totalsalecost")).append(" : ").append(totals(1, stockInventoryReport, listOfTotals))
                      .append("</td>");
            sb.append(" </tr> ");
            sb.append(" </table> ");
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
            }
        }
        return sb.toString();
    }
}
