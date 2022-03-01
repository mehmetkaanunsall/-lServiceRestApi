/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automation.report.fuelsalesreport.dao.IFuelSalesReportDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.io.IOException;
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
 * @author ebubekir.buker
 */
public class FuelSalesReportService implements IFuelSalesReportService {

    @Autowired
    private IFuelSalesReportDao fuelSalesReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setFuelServiceDao(IFuelSalesReportDao fuelSalesReportDao) {
        this.fuelSalesReportDao = fuelSalesReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FuelSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, FuelSalesReport fuelSalesReport) {
        return fuelSalesReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList, fuelSalesReport);

    }

    @Override
    public int count(String where) {
        return fuelSalesReportDao.count(where);
    }

    @Override
    public String createWhere(FuelSalesReport obj) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        where += "AND shf.processdate BETWEEN'" + dateFormat.format(obj.getBeginDate()) + "'AND'" + dateFormat.format(obj.getEndDate()) + "'";

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
            where = where + " AND acc.id IN(" + accountList + ") ";
        }

        String branchList = "";
        for (Branch branch : obj.getSelectedBranchList()) {
            branchList = branchList + "," + String.valueOf(branch.getId());
            if (branch.getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND ash.branch_id IN(" + branchList + ") ";
        }

        String pumperList = "";

        for (Account pumper : obj.getListOfPumper()) {
            pumperList = pumperList + "," + String.valueOf(pumper.getId());
            if (pumper.getId() == 0) {
                pumperList = "";
                break;
            }
        }
        if (!pumperList.equals("")) {
            pumperList = pumperList.substring(1, pumperList.length());
            where = where + " AND shf.attendant_id IN(" + pumperList + ")";
        }

        String saleTypeList = "";

        for (FuelSaleType saleType : obj.getSelectedFuelSaleTypeList()) {
            saleTypeList = saleTypeList + "," + String.valueOf(saleType.getId());
            if (saleType.getId() == 0) {
                saleTypeList = "";
                break;
            }
        }
        if (!saleTypeList.equals("")) {
            saleTypeList = saleTypeList.substring(1, saleTypeList.length());        
            where = where + " AND fls.id IN(" + saleTypeList + ")";
        }

        if (obj.getMinSalesPrice() != null) {
            where = where + " AND shf.totalmoney >= " + obj.getMinSalesPrice();
        }
        if (obj.getMaxSalesPrice() != null) {
            where = where + " AND shf.totalmoney <= " + obj.getMaxSalesPrice();
        }
        if (obj.getFuelShift().toString() != null) {
            where = where + " AND ash.shiftno ILIKE '%" + obj.getFuelShift().getShiftNo() + "%'";
        }
        if (obj.getReceiptNo() != null) {
            where = where + " AND shf.receiptno ILIKE '%" + obj.getReceiptNo() + "%'";
        }
      
        if (obj.getPlate() != null) {
            where = where + " AND shf.plate ILIKE '%" + obj.getPlate() + "%'";
        }

        return where;
    }

    @Override
    public void exportPdf(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, String branchList, List<Branch> selectedBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            connection = fuelSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelSalesReportDao.exportData(where, branchList, fuelSalesReport));

            rs = prep.executeQuery();

            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fuelsalesreport"), pdfDocument.getFontHeader()));

            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {

                branchName = sessionBean.getLoc().getString("all");

            } else {
                for (Branch branch1 : selectedBranchList) {
                    branchName += " , " + branch1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String accountName = "";
            if (fuelSalesReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfAccount().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : fuelSalesReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("customer") + " : " + accountName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String pumperList = "";

            if (fuelSalesReport.getListOfPumper().isEmpty()) {
                pumperList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfPumper().get(0).getId() == 0) {
                pumperList = sessionBean.getLoc().getString("all");
            } else {
                for (Account pumperList1 : fuelSalesReport.getListOfPumper()) {
                    pumperList += " , " + pumperList1.getName();
                }

                pumperList = pumperList.substring(2, pumperList.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("pumpper") + " : " + pumperList, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            /**/
            String saleTypeList = "";

            if (fuelSalesReport.getSelectedFuelSaleTypeList().isEmpty()) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getSelectedFuelSaleTypeList().get(0).getId() == 0) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else {
                for (FuelSaleType fuelSaleType : fuelSalesReport.getSelectedFuelSaleTypeList()) {
                    saleTypeList += " , " + fuelSaleType.getName();
                }
                saleTypeList = saleTypeList.substring(2, saleTypeList.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("saletype") + " : " + saleTypeList, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (fuelSalesReport.getMinSalesPrice() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("minimumsaleamount") + " : " + fuelSalesReport.getMinSalesPrice(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            if (fuelSalesReport.getMaxSalesPrice() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("maximumsaleamount") + " : " + fuelSalesReport.getMaxSalesPrice(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }

            if (fuelSalesReport.getFuelShift().toString() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + fuelSalesReport.getFuelShift().toString(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            if (fuelSalesReport.getReceiptNo() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("receiptno") + " : " + fuelSalesReport.getReceiptNo(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }

            if (fuelSalesReport.getPlate() != null) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("plate") + " : " + fuelSalesReport.getPlate(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            /**/
            StaticMethods.createHeaderPdf("frmFuelSalesReportDatatable:dtbFuelSalesReport", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                Currency currency = new Currency(rs.getInt("crnid"));
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("shfprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ashshiftno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("shfplate"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stccode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stccentralproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stcbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stcname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("shfreceiptno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("attendantname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("flsname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("shfliter")) + rs.getString("gunsortname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("shfprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("shfdiscountotal")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(15)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("shftotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("fuelsalesreport"));
        } catch (DocumentException | SQLException e) {
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
                Logger.getLogger(FuelSalesReport.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

    }

    @Override
    public void exportExcel(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, List<FuelSalesReport> listOfTotals, String branchList, List<Branch> selectedBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = fuelSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelSalesReportDao.exportData(where, branchList, fuelSalesReport));

            rs = prep.executeQuery();
            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("fuelsalesreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getBeginDate()));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getEndDate()));

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String accountName = "";
            if (fuelSalesReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfAccount().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : fuelSalesReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            SXSSFRow accName = excelDocument.getSheet().createRow(jRow++);
            accName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("account") + " : " + accountName);

            String pumperList = "";

            if (fuelSalesReport.getListOfPumper().isEmpty()) {
                pumperList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfPumper().get(0).getId() == 0) {
                pumperList = sessionBean.getLoc().getString("all");
            } else {
                for (Account pumperList1 : fuelSalesReport.getListOfPumper()) {
                    pumperList += " , " + pumperList1.getName();
                }

                pumperList = pumperList.substring(2, pumperList.length());
            }

            SXSSFRow pumpName = excelDocument.getSheet().createRow(jRow++);
            pumpName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("pumpper") + " : " + pumperList);

            String saleTypeList = "";

            if (fuelSalesReport.getSelectedFuelSaleTypeList().isEmpty()) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getSelectedFuelSaleTypeList().get(0).getId() == 0) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else {
                for (FuelSaleType fuelSaleType : fuelSalesReport.getSelectedFuelSaleTypeList()) {
                    saleTypeList += " , " + fuelSaleType.getName();
                }
                saleTypeList = saleTypeList.substring(2, saleTypeList.length());
            }

            SXSSFRow saleType = excelDocument.getSheet().createRow(jRow++);
            saleType.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("saletype") + " : " + saleTypeList);

            if (fuelSalesReport.getMinSalesPrice() != null) {
                SXSSFRow minSales = excelDocument.getSheet().createRow(jRow++);
                minSales.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("minimumsaleamount") + " : " + fuelSalesReport.getMinSalesPrice());
            }

            if (fuelSalesReport.getMaxSalesPrice() != null) {
                SXSSFRow maxSales = excelDocument.getSheet().createRow(jRow++);
                maxSales.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("maximumsaleamount") + " : " + fuelSalesReport.getMaxSalesPrice());

            }

            if (fuelSalesReport.getFuelShift().toString() != null) {
                SXSSFRow fuelShift = excelDocument.getSheet().createRow(jRow++);
                fuelShift.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("shiftno") + " : " + fuelSalesReport.getFuelShift().getShiftNo());
            }

            if (fuelSalesReport.getReceiptNo() != null) {
                SXSSFRow recipt = excelDocument.getSheet().createRow(jRow++);
                recipt.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("receiptno") + " : " + fuelSalesReport.getReceiptNo());

            }

            if (fuelSalesReport.getPlate() != null) {
                SXSSFRow plate = excelDocument.getSheet().createRow(jRow++);
                plate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("plate") + " : " + fuelSalesReport.getPlate());
            }

            SXSSFRow empty5 = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmFuelSalesReportDatatable:dtbFuelSalesReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brname"));

                }

                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("shfprocessdate")));

                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("ashshiftno"));

                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));

                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("shfplate"));

                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stccode"));

                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stccentralproductcode"));

                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stcbarcode"));

                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stcname"));

                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(rs.getString("shfreceiptno"));

                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(rs.getString("attendantname"));

                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(rs.getString("flsname"));

                }

                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("shfliter").doubleValue(), rs.getInt("gununitrounding")));

                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("shfprice").doubleValue(), rs.getInt("gununitrounding")));

                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("shfdiscountotal").doubleValue(), rs.getInt("gununitrounding")));

                }
                if (toogleList.get(15)) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("shftotalmoney").doubleValue(), rs.getInt("gununitrounding")));

                }

            }
            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("fuelsalesreport"));
            } catch (IOException ex) {
                Logger.getLogger(FuelSalesReport.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(FuelSalesReport.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(FuelSalesReport.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, FuelSalesReport fuelSalesReport, List<Boolean> toogleList, List<FuelSalesReport> listOfTotalst, String branchList, List<Branch> selectedBranchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = fuelSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelSalesReportDao.exportData(where, branchList, fuelSalesReport));
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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getBeginDate())).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelSalesReport.getEndDate())).append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (Branch branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String accountName = "";
            if (fuelSalesReport.getListOfAccount().isEmpty()) {
                accountName = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfAccount().get(0).getId() == 0) {
                accountName = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : fuelSalesReport.getListOfAccount()) {
                    accountName += " , " + account1.getName();
                }

                accountName = accountName.substring(2, accountName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("customer")).append(" : ").append(accountName).append(" </div> ");

            String pumperList = "";

            if (fuelSalesReport.getListOfPumper().isEmpty()) {
                pumperList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getListOfPumper().get(0).getId() == 0) {
                pumperList = sessionBean.getLoc().getString("all");
            } else {
                for (Account account1 : fuelSalesReport.getListOfPumper()) {
                    pumperList += " , " + account1.getName();
                }

                pumperList = pumperList.substring(2, pumperList.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("pumpper")).append(" : ").append(pumperList).append(" </div> ");

            /**/
            String saleTypeList = "";

            if (fuelSalesReport.getSelectedFuelSaleTypeList().isEmpty()) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else if (fuelSalesReport.getSelectedFuelSaleTypeList().get(0).getId() == 0) {
                saleTypeList = sessionBean.getLoc().getString("all");
            } else {
                for (FuelSaleType fuelSaleType : fuelSalesReport.getSelectedFuelSaleTypeList()) {
                    saleTypeList += " , " + fuelSaleType.getName();
                }
                saleTypeList = saleTypeList.substring(2, saleTypeList.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("saletype")).append(" : ").append(saleTypeList).append(" </div> ");

            if (fuelSalesReport.getMinSalesPrice() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("minimumsaleamount")).append(" : ").append(fuelSalesReport.getMinSalesPrice()).append(" </div> ");
            }

            if (fuelSalesReport.getMaxSalesPrice() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("maximumsaleamount")).append(" : ").append(fuelSalesReport.getMaxSalesPrice()).append(" </div> ");
            }

            if (fuelSalesReport.getFuelShift().toString() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("shiftno")).append(" : ").append(fuelSalesReport.getFuelShift().toString()).append(" </div> ");
            }

            if (fuelSalesReport.getReceiptNo() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("receiptno")).append(" : ").append(fuelSalesReport.getReceiptNo()).append(" </div> ");

            }

            if (fuelSalesReport.getPlate() != null) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("plate")).append(" : ").append(fuelSalesReport.getPlate()).append(" </div> ");
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
                    + "             font-size: 10px;"
                    + "        }"
                    + "   @page { size: landscape; }"
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmFuelSalesReportDatatable:dtbFuelSalesReport", toogleList, "headerBlack", sb);
            sb.append(" </tr>  ");

            while (rs.next()) {

                formatterUnit.setMaximumFractionDigits(rs.getInt("gununitrounding"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("gununitrounding"));
                Currency currency = new Currency(rs.getInt("crnid"));

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getString("brname") == null ? "" : rs.getString("brname")).append("</td>");
                }

                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getTimestamp("shfprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("shfprocessdate"))).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("ashshiftno") == null ? "" : rs.getString("ashshiftno")).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("shfplate") == null ? "" : rs.getString("shfplate")).append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getString("stccode") == null ? "" : rs.getString("stccode")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("stccentralproductcode") == null ? "" : rs.getString("stccentralproductcode")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("stcbarcode") == null ? "" : rs.getString("stcbarcode")).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("stcname") == null ? "" : rs.getString("stcname")).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(rs.getString("shfreceiptno") == null ? "" : rs.getString("shfreceiptno")).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(rs.getString("attendantname") == null ? "" : rs.getString("attendantname")).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td>").append(rs.getString("flsname") == null ? "" : rs.getString("flsname")).append("</td>");
                }
                if (toogleList.get(12)) {//shfliter
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("shfliter"))).append(rs.getString("gunsortname")).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("shfprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("shfdiscountotal"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(15)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("shftotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
            }
            sb.append(" </table> ");

            /**/
        } catch (Exception e) {
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
                Logger.getLogger(FuelSalesReport.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

        return sb.toString();
    }

}
