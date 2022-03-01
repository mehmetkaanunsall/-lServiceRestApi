/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 02:06:01
 */
package com.mepsan.marwiz.general.report.productmovementreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
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
import com.mepsan.marwiz.general.report.productmovementreport.dao.IProductMovementReportDao;
import com.mepsan.marwiz.general.report.productmovementreport.dao.ProductMovementReport;
import com.mepsan.marwiz.general.report.productmovementreport.dao.ProductMovementReportDao;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductMovementReportService implements IProductMovementReportService {

    @Autowired
    private IProductMovementReportDao productMovementReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setProductMovementReportDao(IProductMovementReportDao productMovementReportDao) {
        this.productMovementReportDao = productMovementReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(ProductMovementReport obj, boolean isCentralIntegration, int supplierType) {
        String where = " ";
        if (obj.getMinSalesAmount() == null && obj.getMaxSalesAmount() != null) {
            where = " AND COALESCE((t.quantity),0) <= " + obj.getMaxSalesAmount() + "\n";
        } else if (obj.getMinSalesAmount() != null && obj.getMaxSalesAmount() == null) {
            where = " AND COALESCE((t.quantity),0) >= " + obj.getMinSalesAmount() + "\n";
        } else if (obj.getMinSalesAmount() == null && obj.getMaxSalesAmount() == null) {
            where = " ";
        } else if (obj.getMinSalesAmount() != null && obj.getMaxSalesAmount() != null) {
            where = " AND COALESCE((t.quantity),0) BETWEEN " + obj.getMinSalesAmount() + " and " + obj.getMaxSalesAmount() + "\n";
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
        return where;
    }

    @Override
    public List<ProductMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductMovementReport obj) {
        return productMovementReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj);
    }

    @Override
    public int count(String where, ProductMovementReport obj) {
        return productMovementReportDao.count(where, obj);
    }

    @Override
    public void exportPdf(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = productMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(productMovementReportDao.exportData(where, productMovementReport));
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

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("lessormoresoldstockreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (productMovementReport.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : productMovementReport.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String param = "";
            if (productMovementReport.getMinSalesAmount() != null) {
                param += sessionBean.getLoc().getString("salesamount") + "(Min)" + " : " + productMovementReport.getMinSalesAmount() + "  ";
            }
            if (productMovementReport.getMaxSalesAmount() != null) {
                param += sessionBean.getLoc().getString("salesamount") + "(Max)" + " : " + productMovementReport.getMaxSalesAmount();
            }

            if (!"".equals(param)) {
                pdfDocument.getCell().setPhrase(new Phrase(param, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String stockName = "";
            if (productMovementReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productMovementReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (productMovementReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : productMovementReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (productMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : productMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (productMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : productMovementReport.getListOfAccount()) {
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

            StaticMethods.createHeaderPdf("frmProductMovementReportDatatable:dtbProductMovementReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            String[] colums = new String[]{"brcname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", "", ""};

            String[] extension = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                formatter.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

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
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("quantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                Currency currency = new Currency(rs.getInt("currency_id"));
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }
            for (ProductMovementReport total : listOfTotals) {
                if (total.getCurrency().getId() != 0) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(total.getSalesPrice()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                    pdfDocument.getRightCell().setColspan(numberOfColumns);
                    pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("lessormoresoldstockreport"));
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
                Logger.getLogger(ProductMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = productMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(productMovementReportDao.exportData(where, productMovementReport));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("lessormoresoldstockreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            int i = 1;
            SXSSFRow empty = excelDocument.getSheet().createRow(i);
            i++;

            SXSSFRow startdate = excelDocument.getSheet().createRow(i);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getBeginDate()));
            i++;
            SXSSFRow enddate = excelDocument.getSheet().createRow(i);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getEndDate()));
            i++;

            String branchName = "";
            if (productMovementReport.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : productMovementReport.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(i);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
            i++;

            String param = "";
            if (productMovementReport.getMinSalesAmount() != null) {
                param += sessionBean.getLoc().getString("salesamount") + "(Min)" + " : " + productMovementReport.getMinSalesAmount() + "  ";
            }
            if (productMovementReport.getMaxSalesAmount() != null) {
                param += sessionBean.getLoc().getString("salesamount") + "(Max)" + " : " + productMovementReport.getMaxSalesAmount();
            }

            if (!param.equals("")) {
                SXSSFRow paramCell = excelDocument.getSheet().createRow(i);
                paramCell.createCell((short) 0).setCellValue(param);
                i++;
            }

            String stockName = "";
            if (productMovementReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productMovementReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(i);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);
            i++;

            String categoryName = "";
            if (productMovementReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : productMovementReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            SXSSFRow category = excelDocument.getSheet().createRow(i);
            category.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);
            i++;

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (productMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : productMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(i);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
                i++;
            } else {
                String supplierName = "";
                if (productMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : productMovementReport.getListOfAccount()) {
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

            StaticMethods.createHeaderExcel("frmProductMovementReportDatatable:dtbProductMovementReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            i++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brcname"));
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
                    SXSSFCell quantity = row.createCell((short) b++);
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("quantity").doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(10)) {
                    SXSSFCell unitprice = row.createCell((short) b++);
                    unitprice.setCellValue(StaticMethods.round(rs.getBigDecimal("unitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {
                    SXSSFCell unitprice = row.createCell((short) b++);
                    unitprice.setCellValue(StaticMethods.round(rs.getBigDecimal("totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                i++;
            }
            for (ProductMovementReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getSalesPrice(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("lessormoresoldstockreport"));
            } catch (IOException ex) {
                Logger.getLogger(ProductMovementReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ProductMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, ProductMovementReport productMovementReport, List<Boolean> toogleList, List<ProductMovementReport> listOfTotals, boolean isCentralSupplier) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = productMovementReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(productMovementReportDao.exportData(where, productMovementReport));
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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productMovementReport.getEndDate())).append(" </div> ");

            String branchName = "";
            if (productMovementReport.getListOfBranch().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : productMovementReport.getListOfBranch()) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(productMovementReport.getMinSalesAmount() != null ? sessionBean.loc.getString("salesamount") + "(Min) : " + productMovementReport.getMinSalesAmount() + "  " : "")
                    .append(productMovementReport.getMaxSalesAmount() != null ? sessionBean.loc.getString("salesamount") + "(Max) : " + productMovementReport.getMaxSalesAmount() : "").append(" </div> ");

            String stockName = "";
            if (productMovementReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productMovementReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (productMovementReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productMovementReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : productMovementReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (productMovementReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : productMovementReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (productMovementReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (productMovementReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : productMovementReport.getListOfAccount()) {
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

            StaticMethods.createHeaderPrint("frmProductMovementReportDatatable:dtbProductMovementReport", toogleList, "headerBlack", sb);

            sb.append(" </tr>  ");
            String[] colums = new String[]{"brcname", "stckcode", "stckcenterproductcode", "stckbarcode", "stckname", "", "", "", "", "", "", ""};
            String[] extensions = new String[]{"", "", "", "", "", "", "", "", "", "", "", ""};
            while (rs.next()) {
                formatter.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
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
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("quantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                Currency currency = new Currency(rs.getInt("currency_id"));
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("unitprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                sb.append(" </tr> ");

            }
            for (ProductMovementReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(total.getSalesPrice()))
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
                Logger.getLogger(ProductMovementReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<ProductMovementReport> totals(String where, ProductMovementReport obj) {
        return productMovementReportDao.totals(where, obj);
    }

}
