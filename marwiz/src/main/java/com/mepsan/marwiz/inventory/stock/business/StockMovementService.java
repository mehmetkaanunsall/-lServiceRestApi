/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:59:26
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.inventory.stock.dao.IStockMovementDao;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import com.mepsan.marwiz.inventory.stock.dao.StockMovementDao;
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
import java.util.ArrayList;
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

public class StockMovementService implements IStockMovementService {

    @Autowired
    private IStockMovementDao stockMovementDao;

    @Autowired
    private SessionBean sessionBean;

    public void setStockMovementDao(IStockMovementDao stockMovementDao) {
        this.stockMovementDao = stockMovementDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockMovement> listOfWarehouseAvailability(Stock stock, Warehouse warehouse, List<Branch> listOfBranch) {
        return stockMovementDao.listOfWarehouseAvailability(stock, warehouse, listOfBranch);

    }

    @Override
    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch) {
        return stockMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where, stock, opType, begin, end, warehouse, listOfBranch);
    }

    @Override
    public StockMovement count(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch) {
        return stockMovementDao.count(where, stock, opType, begin, end, warehouse, listOfBranch);
    }

    @Override
    public void exportPdf(String where, StockMovement stockMovement, List<Boolean> toogleList, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal remainingAmount, BigDecimal transferAmount) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }

            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }

            connection = stockMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockMovementDao.exportData(where, stock, opType, begin, end, warehouse, branchList));
            rs = prep.executeQuery();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("movements"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), begin), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), end), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (stock.getName() != null) {
                stockName = stock.getName();
            } else {
                stockName = "";
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String processType = "";
            if (opType != 0) {
                switch (opType) {
                    case 1:
                        processType = sessionBean.getLoc().getString("entry");
                        break;
                    case 2:
                        processType = sessionBean.getLoc().getString("exit");
                        break;
                    case 3:
                        processType = sessionBean.getLoc().getString("all");
                        break;
                }
            } else {
                processType = sessionBean.getLoc().getString("all");
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("processtype") + " : " + processType, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (listOfBranch.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch br : listOfBranch) {
                    branchName += " , " + br.getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String warehouseName = "";
            if (warehouse.getId() != 0) {

                warehouseName = warehouse.getName();

            } else {
                warehouseName = sessionBean.getLoc().getString("all");
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            StaticMethods.createHeaderPdf("tbvStokProc:frmMovements:dtbItems", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            formatterUnit.setMaximumFractionDigits(stock.getUnit().getUnitRounding());
            formatterUnit.setMinimumFractionDigits(stock.getUnit().getUnitRounding());

            while (rs.next()) {

                Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("movedate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("whname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("wrreceiptnumber"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getInt("processtype") == 1 ? sessionBean.getLoc().getString("consumption") : rs.getInt("processtype") == 2 ? sessionBean.getLoc().getString("countdifference") : rs.getInt("processtype") == 3 ? sessionBean.getLoc().getString("trasferbetweenwarehouses") : rs.getInt("processtype") == 4 ? sessionBean.getLoc().getString("other") : rs.getInt("processtype") == 5 ? sessionBean.getLoc().getString("returnreceipt") : rs.getInt("processtype") == 6 ? sessionBean.getLoc().getString("receipt") : rs.getInt("processtype") == 7 ? sessionBean.getLoc().getString("invoice") : rs.getInt("processtype") == 8 ? sessionBean.getLoc().getString("waybill") : rs.getInt("processtype") == 9 ? sessionBean.getLoc().getString("refuse") : " ", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBigDecimal("price") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("price")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("wmis_direction") ? sessionBean.getLoc().getString("entry") : sessionBean.getLoc().getString("exit"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBigDecimal("wmquantity") == null ? "" : formatterUnit.format(rs.getBigDecimal("wmquantity")) + stock.getUnit().getSortName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBigDecimal("lastquantity") == null ? "" : formatterUnit.format(rs.getBigDecimal("lastquantity")) + stock.getUnit().getSortName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("overturnamount") + " : " + formatterUnit.format(transferAmount) + " " + stock.getUnit().getSortName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (opType == 3 || opType == 1) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofincoming") + " : " + formatterUnit.format(totalIncoming) + " " + stock.getUnit().getSortName(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            if (opType == 2 || opType == 3) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sumofoutcoming") + " : " + formatterUnit.format(totalOutcoming) + " " + stock.getUnit().getSortName(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }


            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamount") + " : " + formatterUnit.format(remainingAmount) + " " + stock.getUnit().getSortName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("movements"));

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
                Logger.getLogger(StockMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, StockMovement stockMovement,
            List<Boolean> toogleList, Stock stock,
            int opType, Date begin,
            Date end, Warehouse warehouse, List<Branch> listOfBranch, BigDecimal totalIncoming, BigDecimal totalOutcoming, BigDecimal remainingAmount, BigDecimal transferAmount
    ) {


        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            String branchList = "";
            for (Branch br : listOfBranch) {
                branchList = branchList + "," + String.valueOf(br.getId());
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
            connection = stockMovementDao.getDatasource().getConnection();
            prep = connection.prepareStatement(stockMovementDao.exportData(where, stock, opType, begin, end, warehouse, branchList));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);

            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("movements"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), begin));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), end));

            String stockName = "";
            if (stock.getName() != null) {
                stockName = stock.getName();
            } else {
                stockName = "";
            }

            SXSSFRow stokName = excelDocument.getSheet().createRow(4);
            stokName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String processType = "";
            if (opType != 0) {
                switch (opType) {
                    case 1:
                        processType = sessionBean.getLoc().getString("entry");
                        break;
                    case 2:
                        processType = sessionBean.getLoc().getString("exit");
                        break;
                    case 3:
                        processType = sessionBean.getLoc().getString("all");
                        break;
                }
            } else {
                processType = sessionBean.getLoc().getString("all");
            }

            SXSSFRow processTypeName = excelDocument.getSheet().createRow(5);
            processTypeName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("processtype") + " : " + processType);

            String branchName = "";
            if (listOfBranch.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch br : listOfBranch) {
                    branchName += " , " + br.getName();
                }

                branchName = branchName.substring(3, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(6);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String warehouseName = "";
            if (warehouse.getId() != 0) {
                warehouseName = warehouse.getName();
            } else {
                warehouseName = sessionBean.getLoc().getString("all");
            }

            SXSSFRow wrName = excelDocument.getSheet().createRow(7);
            wrName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName);

            SXSSFRow empty5 = excelDocument.getSheet().createRow(8);

            StaticMethods.createHeaderExcel("tbvStokProc:frmMovements:dtbItems", toogleList, "headerBlack", excelDocument.getWorkbook());

            int i = 10;
            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(i);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));
                }
                if (toogleList.get(1)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("movedate"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }

                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("whname"));
                }

                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("wrreceiptnumber"));
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getInt("processtype") == 1 ? sessionBean.getLoc().getString("consumption") : rs.getInt("processtype") == 2 ? sessionBean.getLoc().getString("countdifference") : rs.getInt("processtype") == 3 ? sessionBean.getLoc().getString("trasferbetweenwarehouses") : rs.getInt("processtype") == 4 ? sessionBean.getLoc().getString("other") : rs.getInt("processtype") == 5 ? sessionBean.getLoc().getString("returnreceipt") : rs.getInt("processtype") == 6 ? sessionBean.getLoc().getString("receipt") : rs.getInt("processtype") == 7 ? sessionBean.getLoc().getString("invoice") : rs.getInt("processtype") == 8 ? sessionBean.getLoc().getString("waybill") : rs.getInt("processtype") == 9 ? sessionBean.getLoc().getString("refuse") : " ");
                }

                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("price") == null ? BigDecimal.ZERO.doubleValue() : rs.getBigDecimal("price").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("wmis_direction") ? sessionBean.getLoc().getString("entry") : sessionBean.getLoc().getString("exit"));
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("wmquantity") == null ? BigDecimal.ZERO.doubleValue() : rs.getBigDecimal("wmquantity").doubleValue(), stock.getUnit().getUnitRounding()));
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("lastquantity") == null ? BigDecimal.ZERO.doubleValue() : rs.getBigDecimal("lastquantity").doubleValue(), stock.getUnit().getUnitRounding()));

                }

                i++;
            }

            CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            stylefooter.setAlignment(HorizontalAlignment.LEFT);

            SXSSFRow row4 = excelDocument.getSheet().createRow(i++);
            SXSSFCell cellbalance3 = row4.createCell((short) 0);
            cellbalance3.setCellValue(sessionBean.getLoc().getString("overturnamount") + " : " + StaticMethods.round(transferAmount, stock.getUnit().getUnitRounding()) + " " + stock.getUnit().getSortName());
            cellbalance3.setCellStyle(stylefooter);

            if (opType == 1 || opType == 3) {

                SXSSFRow row1 = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row1.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sumofincoming") + " : " + StaticMethods.round(totalIncoming, stock.getUnit().getUnitRounding()) + " " + stock.getUnit().getSortName());
                cell.setCellStyle(stylefooter);
            }
            if (opType == 2 || opType == 3) {
                SXSSFRow row2 = excelDocument.getSheet().createRow(i++);
                SXSSFCell cell = row2.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("sumofoutcoming") + " : " + StaticMethods.round(totalOutcoming, stock.getUnit().getUnitRounding()) + " " + stock.getUnit().getSortName());
                cell.setCellStyle(stylefooter);
            }
            SXSSFRow row3 = excelDocument.getSheet().createRow(i++);
            SXSSFCell cellbalance = row3.createCell((short) 0);
            cellbalance.setCellValue(sessionBean.getLoc().getString("remainingamount") + " : " + StaticMethods.round(remainingAmount, stock.getUnit().getUnitRounding()) + " " + stock.getUnit().getSortName());
            cellbalance.setCellStyle(stylefooter);
            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("movements"));
            } catch (IOException ex) {
                Logger.getLogger(StockMovementService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(StockMovementDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

}
