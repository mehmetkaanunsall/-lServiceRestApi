/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:27:36
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.fulltakingreport.business.FullTakingReportService;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReport;
import com.mepsan.marwiz.general.report.fulltakingreport.dao.FullTakingReportDao;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao.IMovementReportBetweenWarehouseTakingsDao;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao.MovementReportBetweenWarehouseTakings;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
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

public class MovementReportBetweenWarehouseTakingsService implements IMovementReportBetweenWarehouseTakingsService {

    @Autowired
    private IMovementReportBetweenWarehouseTakingsDao iMovementReportBetweenWarehouseTakingsDao;

    @Autowired
    SessionBean sessionBean;

    public void setiMovementReportBetweenWarehouseTakingsDao(IMovementReportBetweenWarehouseTakingsDao iMovementReportBetweenWarehouseTakingsDao) {
        this.iMovementReportBetweenWarehouseTakingsDao = iMovementReportBetweenWarehouseTakingsDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public void exportPdf(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale) {
        System.out.println("PDF Service");
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        
        Calendar cl=Calendar.getInstance();
        cl.setTime(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate());
        cl.add(Calendar.SECOND, 1);
        
        Calendar cl2=Calendar.getInstance();
        cl2.setTime(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate());
        cl2.add(Calendar.SECOND, -1);
        try {
            connection = iMovementReportBetweenWarehouseTakingsDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iMovementReportBetweenWarehouseTakingsDao.exportData(where));
            prep.setInt(1, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(2, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(3, movementReportBetweenWarehouseTakings.getStockTaking1().getWarehouse().getId());
            prep.setTimestamp(4, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null ? cl.getTime().getTime() : new Date().getTime()));
            prep.setTimestamp(5, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null ? cl2.getTime().getTime() : new Date().getTime()));
            prep.setInt(6, movementReportBetweenWarehouseTakings.getStockTaking1().getId());

            rs = prep.executeQuery();
            
            System.out.println("-----prep--"+prep.toString());
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("movementreportbetweenwarehousetakings"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingone") + "   " + sessionBean.getLoc().getString("name") + " : " + movementReportBetweenWarehouseTakings.getStockTaking1().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingone") + "  " + sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingone") + "  " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate()), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingone") + "  " + sessionBean.getLoc().getString("enddate") + " : ", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingtwo") + "   " + sessionBean.getLoc().getString("name") + " : " + movementReportBetweenWarehouseTakings.getStockTaking2().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingtwo") + "  " + sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingtwo") + "  " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate()), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            } else {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehousestocktakingtwo") + "  " + sessionBean.getLoc().getString("enddate") + " : ", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }
            
            String stockname = "";
            if (movementReportBetweenWarehouseTakings.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : movementReportBetweenWarehouseTakings.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockname, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (movementReportBetweenWarehouseTakings.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : movementReportBetweenWarehouseTakings.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            StaticMethods.createHeaderPdf("frmMovementReportBetweenWarehouseTakingsDatatable:dtbMovementReportBetweenWarehouseTakings", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("untunitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("untunitrounding"));
                BigDecimal movementdifference = rs.getBigDecimal("entryamount").subtract(rs.getBigDecimal("exitamount"));
                BigDecimal difftaking = rs.getBigDecimal("quantity2").subtract(rs.getBigDecimal("stirealquantity"));
                BigDecimal result = difftaking.subtract(movementdifference);
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("stirealquantity")) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("entryamount")) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("exitamount")) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(movementdifference) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("quantity2")) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(difftaking) + rs.getString("untsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(result), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(9)) {//clmLastPurchasePrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {//clmLastPurchaseCost

                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculatePurchaseTaxTotal(rs.getBigDecimal("lastpurchaseprice").multiply(result), rs.getInt("purchasetaxgrouprate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(11)) {//clmLastPurchaseCost2
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice").multiply(result)) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(12)) {//clmPurchaseTaxRate
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getInt("purchasetaxgrouprate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(13)) {//clmLastSalePrice
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {//clmLastSaleCost
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice").multiply(result)) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(15)) {//clmLastSaleCost2
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(calculateSaleTaxTotal(rs.getBigDecimal("lastsaleprice").multiply(result), rs.getInt("salestaxgrouprate"))) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(16)) {//clmSalesTaxRate
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getInt("salestaxgrouprate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());
            pdfDocument.getDataCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalpurchasecost") + " : " + totalPurchase
                    + "      " + sessionBean.getLoc().getString("totalsalecost") + " : " + totalSale));

            pdfDocument.getDataCell().setColspan(numberOfColumns);
            pdfDocument.getDataCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("movementreportbetweenwarehousetakings"));

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
    public void exportExcel(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        Calendar cl=Calendar.getInstance();
        cl.setTime(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate());
        cl.add(Calendar.SECOND, 1);
        
        Calendar cl2=Calendar.getInstance();
        cl2.setTime(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate());
        cl2.add(Calendar.SECOND, -1);
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());
        try {
            connection = iMovementReportBetweenWarehouseTakingsDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iMovementReportBetweenWarehouseTakingsDao.exportData(where));
            prep.setInt(1, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(2, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(3, movementReportBetweenWarehouseTakings.getStockTaking1().getWarehouse().getId());
            prep.setTimestamp(4, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null ? cl.getTime().getTime() : new Date().getTime()));
            prep.setTimestamp(5, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null ? cl2.getTime().getTime() : new Date().getTime()));
            prep.setInt(6, movementReportBetweenWarehouseTakings.getStockTaking1().getId());

            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("movementreportbetweenwarehousetakings"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow warehouse = excelDocument.getSheet().createRow(jRow++);
            warehouse.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingone") + " " + sessionBean.getLoc().getString("name") + " : " + movementReportBetweenWarehouseTakings.getStockTaking1().getName());

            SXSSFRow begindate = excelDocument.getSheet().createRow(jRow++);
            begindate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingone") + " " + sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            if (movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null) {
                enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingone") + " " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate()));
            } else {
                enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingone") + " " + sessionBean.getLoc().getString("enddate") + " : ");
            }

            SXSSFRow empty2 = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow stocktaking = excelDocument.getSheet().createRow(jRow++);
            stocktaking.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingtwo") + " : " + movementReportBetweenWarehouseTakings.getStockTaking2().getName());

            SXSSFRow begindate2 = excelDocument.getSheet().createRow(jRow++);
            begindate2.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingtwo") + " " + sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getBeginDate()));

            SXSSFRow enddate2 = excelDocument.getSheet().createRow(jRow++);
            if (movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null) {
                enddate2.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingtwo") + " " + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate()));
            } else {
                enddate2.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehousestocktakingtwo") + " " + sessionBean.getLoc().getString("enddate") + " : ");
            }
            
             String stockname = "";
            if (movementReportBetweenWarehouseTakings.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : movementReportBetweenWarehouseTakings.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockname);

            String categoryName = "";
            if (movementReportBetweenWarehouseTakings.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : movementReportBetweenWarehouseTakings.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow cate = excelDocument.getSheet().createRow(jRow++);
            cate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);


            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmMovementReportBetweenWarehouseTakingsDatatable:dtbMovementReportBetweenWarehouseTakings", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                BigDecimal movementdifference = rs.getBigDecimal("entryamount").subtract(rs.getBigDecimal("exitamount"));
                BigDecimal difftaking = rs.getBigDecimal("quantity2").subtract(rs.getBigDecimal("stirealquantity"));
                BigDecimal result = difftaking.subtract(movementdifference);

                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(2)) {
                    SXSSFCell takingone = row.createCell((short) b++);
                    takingone.setCellValue(StaticMethods.round(rs.getBigDecimal("stirealquantity").doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(3)) {
                    SXSSFCell entry = row.createCell((short) b++);
                    entry.setCellValue(StaticMethods.round(rs.getBigDecimal("entryamount").doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(4)) {
                    SXSSFCell exit = row.createCell((short) b++);
                    exit.setCellValue(StaticMethods.round(rs.getBigDecimal("exitamount").doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(5)) {
                    SXSSFCell cmovementdifference = row.createCell((short) b++);
                    cmovementdifference.setCellValue(StaticMethods.round(movementdifference.doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(6)) {
                    SXSSFCell takingtwo = row.createCell((short) b++);
                    takingtwo.setCellValue(StaticMethods.round(rs.getBigDecimal("quantity2").doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(7)) {
                    SXSSFCell cdifftaking = row.createCell((short) b++);
                    cdifftaking.setCellValue(StaticMethods.round(difftaking.doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(8)) {//clmLastPurchasePrice
                    SXSSFCell cresult = row.createCell((short) b++);
                    cresult.setCellValue(StaticMethods.round(result.doubleValue(), rs.getInt("untunitrounding")));
                }
                if (toogleList.get(9)) {//clmLastPurchasePrice
                    SXSSFCell lastpurchase = row.createCell((short) b++);
                    lastpurchase.setCellValue(StaticMethods.round(rs.getBigDecimal("lastpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(10)) {//clmLastPurchaseCost
                    SXSSFCell lastpurchasecost2 = row.createCell((short) b++);
                    lastpurchasecost2.setCellValue(StaticMethods.round((calculatePurchaseTaxTotal(rs.getBigDecimal("lastpurchaseprice").multiply(result), rs.getInt("purchasetaxgrouprate"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(11)) {//clmLastPurchaseCost2
                    SXSSFCell lastpurchasecost = row.createCell((short) b++);
                    lastpurchasecost.setCellValue(StaticMethods.round((rs.getBigDecimal("lastpurchaseprice").multiply(result)).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(12)) {//clmPurchaseTaxRate
                    SXSSFCell lastpurchasetaxrate = row.createCell((short) b++);
                    lastpurchasetaxrate.setCellValue(rs.getInt("purchasetaxgrouprate"));
                }
                if (toogleList.get(13)) {//clmLastSalePrice
                    SXSSFCell lastsaleprice = row.createCell((short) b++);
                    lastsaleprice.setCellValue(StaticMethods.round(rs.getBigDecimal("lastsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(14)) {//clmLastSaleCost
                    SXSSFCell lastsalecost = row.createCell((short) b++);
                    lastsalecost.setCellValue(StaticMethods.round(calculateSaleTaxTotal(rs.getBigDecimal("lastsaleprice").multiply(result), rs.getInt("salestaxgrouprate")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(15)) {//clmLastSaleCost2
                    SXSSFCell lastsalecost = row.createCell((short) b++);
                    lastsalecost.setCellValue(StaticMethods.round(rs.getBigDecimal("lastsaleprice").multiply(result).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(16)) {//clmSalesTaxRate
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
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("movementreportbetweenwarehousetakings"));
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
    public String exportPrinter(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings, List<Boolean> toogleList,String totalPurchase, String totalSale) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        
        Calendar cl=Calendar.getInstance();
        cl.setTime(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate());
        cl.add(Calendar.SECOND, 1);
        
        Calendar cl2=Calendar.getInstance();
        cl2.setTime(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate());
        cl2.add(Calendar.SECOND, -1);

        try {
            connection = iMovementReportBetweenWarehouseTakingsDao.getDatasource().getConnection();
            prep = connection.prepareStatement(iMovementReportBetweenWarehouseTakingsDao.exportData(where));
            prep.setInt(1, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(2, movementReportBetweenWarehouseTakings.getStockTaking2().getId());
            prep.setInt(3, movementReportBetweenWarehouseTakings.getStockTaking1().getWarehouse().getId());
            prep.setTimestamp(4, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null ? cl.getTime().getTime() : new Date().getTime()));
            prep.setTimestamp(5, new Timestamp(movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null ? cl2.getTime().getTime() : new Date().getTime()));
            prep.setInt(6, movementReportBetweenWarehouseTakings.getStockTaking1().getId());

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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingone")).append("  ").append(sessionBean.loc.getString("name")).append(" : ").append(movementReportBetweenWarehouseTakings.getStockTaking1().getName()).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingone")).append(" ").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getBeginDate())).append(" </div> ");
            if (movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingone")).append(" ").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate())).append(" </div> ");
            } else {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingone")).append(" ").append(sessionBean.loc.getString("enddate")).append(" : ").append(" </div> ");
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append("  ").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingtwo")).append("  ").append(sessionBean.loc.getString("name")).append(" : ").append(movementReportBetweenWarehouseTakings.getStockTaking2().getName()).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingtwo")).append(" ").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getBeginDate())).append(" </div> ");
            if (movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingtwo")).append(" ").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate())).append(" </div> ");
            } else {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehousestocktakingtwo")).append(" ").append(sessionBean.loc.getString("enddate")).append(" : ").append(" </div> ");
            }
            
             String stockname = "";
            if (movementReportBetweenWarehouseTakings.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : movementReportBetweenWarehouseTakings.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" : ").append(stockname);
            
              String categoryName = "";
            if (movementReportBetweenWarehouseTakings.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (movementReportBetweenWarehouseTakings.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : movementReportBetweenWarehouseTakings.getListOfCategorization()) {
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

            StaticMethods.createHeaderPrint("frmMovementReportBetweenWarehouseTakingsDatatable:dtbMovementReportBetweenWarehouseTakings", toogleList, "headerBlack", sb);

            while (rs.next()) {

                formatter.setMaximumFractionDigits(rs.getInt("untunitrounding"));
                formatter.setMinimumFractionDigits(rs.getInt("untunitrounding"));
                BigDecimal movementdifference = rs.getBigDecimal("entryamount").subtract(rs.getBigDecimal("exitamount"));
                BigDecimal difftaking = rs.getBigDecimal("quantity2").subtract(rs.getBigDecimal("stirealquantity"));
                BigDecimal result = difftaking.subtract(movementdifference);

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("stirealquantity"))).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("entryamount"))).append(rs.getString("untsortname")).append("</td>");

                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("exitamount"))).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(movementdifference)).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("quantity2"))).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(difftaking)).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(result)).append(rs.getString("untsortname")).append("</td>");
                }
                if (toogleList.get(9)) {//clmLastPurchasePrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(10)) {//clmLastPurchaseCost
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculatePurchaseTaxTotal(rs.getBigDecimal("lastpurchaseprice").multiply(result), rs.getInt("purchasetaxgrouprate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }

                if (toogleList.get(11)) {//clmLastPurchaseCost2
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastpurchaseprice").multiply(result))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }
                if (toogleList.get(12)) {//clmPurchaseTaxRate
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getInt("purchasetaxgrouprate"))).append("</td>");
                }
                if (toogleList.get(13)) {//clmLastSalePrice
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {//clmLastSaleCost
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(calculateSaleTaxTotal(rs.getBigDecimal("lastsaleprice").multiply(result), rs.getInt("salestaxgrouprate")))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(15)) {//clmLastSaleCost2
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("lastsaleprice").multiply(result))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(16)) {//clmSalesTaxRate
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getInt("salestaxgrouprate"))).append("</td>");
                }

                sb.append(" </tr> ");

            }
            
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                    .append(sessionBean.getLoc().getString("totalpurchasecost")).append(" : ")
                    .append(totalPurchase)
                    .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("   ").append(sessionBean.getLoc().getString("totalsalecost")).append(" : ")
                    .append(totalSale)
                    .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
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
    public String createWhere(MovementReportBetweenWarehouseTakings obj) {
        String where = "";

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

        return where;
    }

    @Override
    public List<MovementReportBetweenWarehouseTakings> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings) {
        return iMovementReportBetweenWarehouseTakingsDao.findAll(first, pageSize, sortField, sortOrder, filters, where, movementReportBetweenWarehouseTakings);
    }

    @Override
    public List<MovementReportBetweenWarehouseTakings> totals(String where, MovementReportBetweenWarehouseTakings obj) {
        return iMovementReportBetweenWarehouseTakingsDao.totals(where, obj);
    }
    
    @Override
    public int count(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings) {
        return iMovementReportBetweenWarehouseTakingsDao.count(where, movementReportBetweenWarehouseTakings);
    }

    @Override
    public List<StockTaking> listOfTaking(StockTaking stockTaking) {
        return iMovementReportBetweenWarehouseTakingsDao.listOfTaking(stockTaking);
    }
    
    public BigDecimal calculatePurchaseTaxTotal(BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.multiply(taxFactor);
        }
        return tot;
    }

    public BigDecimal calculateSaleTaxTotal(BigDecimal total, int taxRate) {
        BigDecimal tax = BigDecimal.valueOf(taxRate);
        BigDecimal taxFactor = tax.movePointLeft(2).add(BigDecimal.ONE);//1,08
        BigDecimal tot = BigDecimal.ZERO;
        if (taxRate == 0) {
            tot = total;
        } else {
            tot = total.divide(taxFactor, RoundingMode.HALF_EVEN);
        }
        return tot;
    }

}
