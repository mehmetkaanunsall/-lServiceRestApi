/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 01:49:13
 */
package com.mepsan.marwiz.general.report.fulltakingreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReport;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReportDao;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.IFullTakingReportDao;
import java.awt.Color;
import java.math.BigDecimal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class FullTakingReportService implements IFullTakingReportService {

    @Autowired
    private IFullTakingReportDao fullTakingReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setFullTakingReportDao(IFullTakingReportDao fullTakingReportDao) {
        this.fullTakingReportDao = fullTakingReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(FullTakingReport obj, int differentType) {
        String where = "";

        where += obj.getWarehouse().getId() != 0 ? " AND st.warehouse_id= " + obj.getWarehouse().getId() + "" : "";

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
            where = where + " AND sti.stock_id IN(" + stockList + ") ";
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
            where = where + " AND sti.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
        }

        if (differentType == 1) {
            where = where + " AND ( COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0)) <> 0 ";
        } else if (differentType == 2) {
            where = where + " AND ( COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0)) = 0 ";
        }

        return where;
    }

    @Override
    public List<FullTakingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FullTakingReport productInventoryReport) {
        return fullTakingReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, productInventoryReport);
    }

    @Override
    public int count(String where, FullTakingReport productInventoryReport) {
        return fullTakingReportDao.count(where, productInventoryReport);
    }

    @Override
    public void exportPdf(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList, int differentType, String totalPurchase, String totalSale) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = fullTakingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fullTakingReportDao.exportData(where));

            prep.setInt(1, productInventoryReport.getStockTaking().getId());

            rs = prep.executeQuery();

            //Birim iiçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fulltakingreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + productInventoryReport.getWarehouse().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktaking") + " : " + productInventoryReport.getStockTaking().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String diffValue = differentType == 1 ? sessionBean.getLoc().getString("thedifferentones") : (differentType == 2 ? sessionBean.getLoc().getString("nodifferent") : sessionBean.getLoc().getString("all"));

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("difference") + " : " + diffValue, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockname = "";
            if (productInventoryReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productInventoryReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockname, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (productInventoryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : productInventoryReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            StaticMethods.createHeaderPdf("frmProductInventoryReportDatatable:dtbProductInventoryReport", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitrounding"));

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcenterproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("parentcategories"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("subcategories"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("systemquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("quantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("different")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(rs.getInt("tef") == -1 ? sessionBean.getLoc().getString("minus") : (rs.getInt("tef") == 1 ? sessionBean.getLoc().getString("plus") : sessionBean.getLoc().getString("equal")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {//clmSystemPrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(11)) {//clmEnteredPrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(12)) {//clmDifferentPrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format((rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity"))).subtract(rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity")))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {//clmLastPurchasePrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(14)) {//clmLastPurchaseCost
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculatePurchaseTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different")), rs.getInt("purchasetaxgrouprate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(15)) {//clmLastPurchaseCost2
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different")))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(16)) {//clmPurchaseTaxRate
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getInt("purchasetaxgrouprate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(17)) {//clmLastSalePrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(18)) {//clmLastSaleCost
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different")))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(19)) {//clmLastSaleCost2
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateSaleTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different")), rs.getInt("salestaxgrouprate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }

                if (toogleList.get(20)) {//clmSalesTaxRate
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getInt("salestaxgrouprate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalpurchasecost") + " : " + totalPurchase
                    + "      " + sessionBean.getLoc().getString("totalsalecost") + " : " + totalSale, pdfDocument.getFontHeader()));

            pdfDocument.getCell().setColspan(numberOfColumns);
            pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("fulltakingreport"));

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
                Logger.getLogger(FullTakingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList, int differentType, String totalPurchase, String totalSale) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());
        try {
            connection = fullTakingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fullTakingReportDao.exportData(where));

            prep.setInt(1, productInventoryReport.getStockTaking().getId());

            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("fulltakingreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow warehouse = excelDocument.getSheet().createRow(jRow++);
            warehouse.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + productInventoryReport.getWarehouse().getName());

            SXSSFRow stocktaking = excelDocument.getSheet().createRow(jRow++);
            stocktaking.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktaking") + " : " + productInventoryReport.getStockTaking().getName());

            String diffValue = differentType == 1 ? sessionBean.getLoc().getString("thedifferentones") : (differentType == 2 ? sessionBean.getLoc().getString("nodifferent") : sessionBean.getLoc().getString("all"));
            SXSSFRow difvaluess = excelDocument.getSheet().createRow(jRow++);
            difvaluess.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("difference") + " : " + diffValue);

            String stockname = "";
            if (productInventoryReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productInventoryReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockname);

            String categoryName = "";
            if (productInventoryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : productInventoryReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow cate = excelDocument.getSheet().createRow(jRow++);
            cate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmProductInventoryReportDatatable:dtbProductInventoryReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcenterproductcode"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("parentcategories"));
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getString("subcategories"));
                }
                if (toogleList.get(6)) {
                    SXSSFCell quantity = row.createCell((short) b++);
                    quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("systemquantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(7)) {
                    SXSSFCell sysquantity = row.createCell((short) b++);
                    sysquantity.setCellValue(StaticMethods.round(rs.getBigDecimal("quantity").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(8)) {
                    SXSSFCell diff = row.createCell((short) b++);
                    diff.setCellValue(StaticMethods.round(rs.getBigDecimal("different").doubleValue(), rs.getInt("guntunitrounding")));
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(rs.getInt("tef") == -1 ? sessionBean.getLoc().getString("minus") : (rs.getInt("tef") == 1 ? sessionBean.getLoc().getString("plus") : sessionBean.getLoc().getString("equal")));
                }

                if (toogleList.get(10)) {//clmSystemPrice
                    SXSSFCell systemPrice = row.createCell((short) b++);
                    systemPrice.setCellValue(StaticMethods.round((rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity")).doubleValue()), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(11)) {//clmEnteredPrice
                    SXSSFCell systemPrice = row.createCell((short) b++);
                    systemPrice.setCellValue(StaticMethods.round((rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity")).doubleValue()), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                if (toogleList.get(12)) {//clmDifferentPrice
                    SXSSFCell systemPrice = row.createCell((short) b++);
                    systemPrice.setCellValue(StaticMethods.round((rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity")).subtract(rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity"))).doubleValue()), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(13)) {//clmLastPurchasePrice
                    SXSSFCell lastpurchase = row.createCell((short) b++);
                    lastpurchase.setCellValue(StaticMethods.round(rs.getBigDecimal("lastpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(14)) {//clmLastPurchaseCost
                    SXSSFCell lastpurchasecost2 = row.createCell((short) b++);
                    lastpurchasecost2.setCellValue(StaticMethods.round((calculatePurchaseTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different")), rs.getInt("purchasetaxgrouprate"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                if (toogleList.get(15)) {//clmLastPurchaseCost2
                    SXSSFCell lastpurchasecost = row.createCell((short) b++);
                    lastpurchasecost.setCellValue(StaticMethods.round((calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different")))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(16)) {//clmPurchaseTaxRate
                    SXSSFCell lastpurchasetaxrate = row.createCell((short) b++);
                    lastpurchasetaxrate.setCellValue(rs.getInt("purchasetaxgrouprate"));
                }
                if (toogleList.get(17)) {//clmLastSalePrice
                    SXSSFCell lastsaleprice = row.createCell((short) b++);
                    lastsaleprice.setCellValue(StaticMethods.round(rs.getBigDecimal("lastsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(18)) {//clmLastSaleCost
                    SXSSFCell lastsalecost = row.createCell((short) b++);
                    lastsalecost.setCellValue(StaticMethods.round(calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(19)) {//clmLastSaleCost2
                    SXSSFCell lastsalecost = row.createCell((short) b++);
                    lastsalecost.setCellValue(StaticMethods.round(calculateSaleTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different")), rs.getInt("salestaxgrouprate")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(20)) {//clmSalesTaxRate
                    SXSSFCell lastpurchasetaxrate = row.createCell((short) b++);
                    lastpurchasetaxrate.setCellValue(rs.getInt("salestaxgrouprate"));
                }

            }

            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell = row.createCell((short) 0);
            CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle1.setAlignment(HorizontalAlignment.LEFT);
            cell.setCellValue(sessionBean.getLoc().getString("totalpurchasecost") + " : "
                    + totalPurchase);
            cell.setCellStyle(cellStyle1);

            SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cell2 = row2.createCell((short) 0);
            CellStyle cellStyle3 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle3.setAlignment(HorizontalAlignment.LEFT);
            cell2.setCellValue(sessionBean.getLoc().getString("totalsalecost") + " : "
                    + totalSale);
            cell2.setCellStyle(cellStyle3);

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("fulltakingreport"));
            } catch (IOException ex) {
                Logger.getLogger(FullTakingReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(FullTakingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, FullTakingReport productInventoryReport, List<Boolean> toogleList, int differentType, String totalPurchase, String totalSale) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = fullTakingReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fullTakingReportDao.exportData(where));

            prep.setInt(1, productInventoryReport.getStockTaking().getId());

            rs = prep.executeQuery();

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehouse")).append(" : ").append(productInventoryReport.getWarehouse().getName()).append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktaking")).append(" : ").append(productInventoryReport.getStockTaking().getName()).append(" </div> ");

            String diffValue = differentType == 1 ? sessionBean.getLoc().getString("thedifferentones") : (differentType == 2 ? sessionBean.getLoc().getString("nodifferent") : sessionBean.getLoc().getString("all"));

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("difference")).append(" : ").append(diffValue).append(" </div> ");

            String stockname = "";
            if (productInventoryReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : productInventoryReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" : ").append(stockname);

            String categoryName = "";
            if (productInventoryReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (productInventoryReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : productInventoryReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.loc.getString("category")).append(" : ").append(categoryName).append(" </div> ");

            int numberOfColumns = 0;
            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
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
                    + "    </style> <table> ");

            StaticMethods.createHeaderPrint("frmProductInventoryReportDatatable:dtbProductInventoryReport", toogleList, "headerBlack", sb);

            while (rs.next()) {
                formatter.setMaximumFractionDigits(rs.getInt("guntunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitrounding"));
                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("stckcenterproductcode") == null ? "" : rs.getString("stckcenterproductcode")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("parentcategories") == null ? "" : rs.getString("parentcategories")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("subcategories") == null ? "" : rs.getString("subcategories")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("systemquantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("quantity"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("different"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td style=\"text-align: right\">").append(rs.getInt("tef") == -1 ? sessionBean.getLoc().getString("minus") : (rs.getInt("tef") == 1 ? sessionBean.getLoc().getString("plus") : sessionBean.getLoc().getString("equal"))).append("</td>");
                }
                if (toogleList.get(10)) {//clmSystemPrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(11)) {//clmEnteredPrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(12)) {//clmDifferentPrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format((rs.getBigDecimal("price").multiply(rs.getBigDecimal("quantity"))).subtract(rs.getBigDecimal("price").multiply(rs.getBigDecimal("systemquantity"))))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {//clmLastPurchasePrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {//clmLastPurchaseCost
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculatePurchaseTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different")), rs.getInt("purchasetaxgrouprate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                if (toogleList.get(15)) {//clmLastPurchaseCost2
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastpurchaseprice").multiply(rs.getBigDecimal("different"))))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }
                if (toogleList.get(16)) {//clmPurchaseTaxRate
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getInt("purchasetaxgrouprate"))).append("</td>");
                }
                if (toogleList.get(17)) {//clmLastSalePrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(18)) {//clmLastSaleCost
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateCost(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different"))))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(19)) {//clmLastSaleCost2
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateSaleTaxTotal(rs.getInt("tef"), rs.getBigDecimal("lastsaleprice").multiply(rs.getBigDecimal("different")), rs.getInt("salestaxgrouprate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(20)) {//clmSalesTaxRate
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getInt("salestaxgrouprate"))).append("</td>");
                }
                sb.append(" </tr> ");

            }

            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                    .append(sessionBean.getLoc().getString("totalpurchasecost")).append(" : ")
                    .append(totalPurchase)
                    .append("   ").append(sessionBean.getLoc().getString("totalsalecost")).append(" : ")
                    .append(totalSale)
                    .append("</td>");
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
                Logger.getLogger(FullTakingReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<FullTakingReport> totals(String where, FullTakingReport fullTakingReport) {
        return fullTakingReportDao.totals(where, fullTakingReport);
    }

    public BigDecimal calculatePurchaseTaxTotal(int diffStatus, BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.multiply(taxFactor);
        }
        return tot.multiply(BigDecimal.valueOf(diffStatus));
    }

    public BigDecimal calculateSaleTaxTotal(int diffStatus, BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.divide(taxFactor, RoundingMode.HALF_EVEN);
        }
        return tot.multiply(BigDecimal.valueOf(diffStatus));
    }

    private BigDecimal calculateCost(int diffStatus, BigDecimal total) {
        BigDecimal val = total.multiply(BigDecimal.valueOf(diffStatus));
        return val;
    }

}
