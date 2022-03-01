/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:20:11
 */
package com.mepsan.marwiz.general.report.orderlistreport.business;

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
import com.mepsan.marwiz.general.report.orderlistreport.dao.IOrderListReportDao;
import com.mepsan.marwiz.general.report.orderlistreport.dao.OrderListReport;
import com.mepsan.marwiz.general.report.orderlistreport.dao.OrderListReportDao;
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
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderListReportService implements IOrderListReportService {

    @Autowired
    private IOrderListReportDao orderListReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setOrderListReportDao(IOrderListReportDao orderListReportDao) {
        this.orderListReportDao = orderListReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(OrderListReport obj, List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier) {
        String where = " ";
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
            where = where + " AND iwi.warehouse_id IN(" + warehouseList + ") ";
        } else {//Depo hepsi seçildiyse branch id ye göre deponun itemları listelenir
            where = where + " AND iw.branch_id IN (" + branchID + ") ";
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
            where = where + " AND iwi.stock_id IN(" + stockList + ") ";
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
            where = where + " AND iwi.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
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
    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchID) {
        return orderListReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchID);
    }

    @Override
    public int count(String where, String branchID) {
        return orderListReportDao.count(where, branchID);
    }

    @Override
    public void exportPdf(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = orderListReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(orderListReportDao.exportData(where, branchID));
            rs = prep.executeQuery();

            //Birim İçin
            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("decreasingstockreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brSetting : selectedBranchList) {
                    branchName += " , " + (brSetting.getBranch().getName());
                }
                branchName = branchName.substring(3, branchName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String warehouseName = "";
            if (orderListReport.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : orderListReport.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockname = "";
            if (orderListReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : orderListReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockname, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (orderListReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : orderListReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (orderListReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : orderListReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (orderListReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : orderListReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }
            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmOrderListDatatable:dtbOrderList", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            while (rs.next()) {

                formatter.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

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
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("stckiminstocklevel")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatter.format(rs.getBigDecimal("sumquantity")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("decreasingstockreport"));

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
                Logger.getLogger(OrderListReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public void exportExcel(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        try {
            connection = orderListReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(orderListReportDao.exportData(where, branchID));
            rs = prep.executeQuery();
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("decreasingstockreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brSetting : selectedBranchList) {
                    branchName += " , " + (brSetting.getBranch().getName());
                }
                branchName = branchName.substring(3, branchName.length());
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String warehouseName = "";
            if (orderListReport.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : orderListReport.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }

            SXSSFRow station = excelDocument.getSheet().createRow(jRow++);
            station.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("warehouse") + " : " + warehouseName);

            String stockname = "";
            if (orderListReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : orderListReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockname);

            String categoryName = "";
            if (orderListReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : orderListReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow stockcategory = excelDocument.getSheet().createRow(jRow++);
            stockcategory.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (orderListReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : orderListReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (orderListReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : orderListReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmOrderListDatatable:dtbOrderList", toogleList, "headerBlack", excelDocument.getWorkbook());

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
                    SXSSFCell outComing = row.createCell((short) b++);
                    outComing.setCellValue(StaticMethods.round(rs.getBigDecimal("stckiminstocklevel").doubleValue(), rs.getInt("guntunitsorting")));
                }
                if (toogleList.get(10)) {
                    SXSSFCell inComing = row.createCell((short) b++);
                    inComing.setCellValue(StaticMethods.round(rs.getBigDecimal("sumquantity").doubleValue(), rs.getInt("guntunitsorting")));
                }

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("decreasingstockreport"));
            } catch (IOException ex) {
                Logger.getLogger(OrderListReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(OrderListReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, OrderListReport orderListReport, List<Boolean> toogleList, boolean isCentralSupplier, String branchID, List<BranchSetting> selectedBranchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = orderListReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(orderListReportDao.exportData(where, branchID));
            rs = prep.executeQuery();

            NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatter.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting brSetting : selectedBranchList) {
                    branchName += " , " + (brSetting.getBranch().getName());
                }
                branchName = branchName.substring(3, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String warehouseName = "";
            if (orderListReport.getWarehouseList().isEmpty()) {
                warehouseName = sessionBean.getLoc().getString("all");
            } else {
                for (Warehouse warehouse : orderListReport.getWarehouseList()) {
                    warehouseName += " , " + (warehouse.getName());
                }
                warehouseName = warehouseName.substring(3, warehouseName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("warehouse")).append(" : ").append(warehouseName).append(" </div> ");

            String stockname = "";
            if (orderListReport.getStockList().isEmpty()) {
                stockname = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getStockList().get(0).getId() == 0) {
                stockname = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : orderListReport.getStockList()) {
                    stockname += " , " + s.getName();
                }
                stockname = stockname.substring(3, stockname.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" : ").append(stockname);

            String categoryName = "";
            if (orderListReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (orderListReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization c : orderListReport.getListOfCategorization()) {
                    categoryName += " , " + c.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.getLoc().getString("category")).append(" : ").append(categoryName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (orderListReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : orderListReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (orderListReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (orderListReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : orderListReport.getListOfAccount()) {
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
                    + "    </style> <table> ");

            StaticMethods.createHeaderPrint("frmOrderListDatatable:dtbOrderList", toogleList, "headerBlack", sb);

            while (rs.next()) {
                formatter.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatter.setMinimumFractionDigits(rs.getInt("guntunitsorting"));
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
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("stckiminstocklevel"))).append(rs.getString("guntsortname")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(formatter.format(rs.getBigDecimal("sumquantity"))).append(rs.getString("guntsortname")).append("</td>");
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
                Logger.getLogger(OrderListReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<Warehouse> listWarehouse(String branchList) {
        return orderListReportDao.listWarehouse(branchList);
    }

    @Override
    public String createWhere(OrderListReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
