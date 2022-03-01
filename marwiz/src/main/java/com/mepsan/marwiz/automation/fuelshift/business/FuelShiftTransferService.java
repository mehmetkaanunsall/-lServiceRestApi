/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 03:41:15
 */
package com.mepsan.marwiz.automation.fuelshift.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftControlFile;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftPreview;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftTransferDao;
import com.mepsan.marwiz.automation.fuelshift.dao.IFuelShiftTransferDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.unit.dao.IUnitDao;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

public class FuelShiftTransferService implements IFuelShiftTransferService {

    @Autowired
    private IFuelShiftTransferDao fuelShiftTransferDao;

    @Autowired
    private IUnitDao unitDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFuelShiftTransferDao(IFuelShiftTransferDao fuelShiftTransferDao) {
        this.fuelShiftTransferDao = fuelShiftTransferDao;
    }

    public void setUnitDao(IUnitDao unitDao) {
        this.unitDao = unitDao;
    }

    @Override
    public List<FuelShiftSales> findAllAttendant(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftTransferDao.findAllAttendant(fuelShift, branchSetting);
    }

    @Override
    public List<ShiftPayment> findAllShiftPayment(FuelShift fuelShift, Account account, int type) {
        return fuelShiftTransferDao.findAllShiftPayment(fuelShift, account, type);
    }

    @Override
    public List<FuelShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, boolean isCheckDeleted) {
        return fuelShiftTransferDao.findAll(first, pageSize, sortField, sortOrder, filters, where, isCheckDeleted);
    }

    @Override
    public List<FuelShift> count(String where, boolean isCheckDeleted) {
        return fuelShiftTransferDao.count(where, isCheckDeleted);
    }

    @Override
    public int update(FuelShift fuelShift) {
        return fuelShiftTransferDao.update(fuelShift);
    }

    @Override
    public int create(FuelShift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<FuelShiftSales> findAllAttendantSale(FuelShiftSales fuelShiftSales) {
        return fuelShiftTransferDao.findAllAttendantSale(fuelShiftSales);
    }

    @Override
    public int delete(FuelShift fuelShift) {
        return fuelShiftTransferDao.delete(fuelShift);
    }

    @Override
    public int createFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList) {
        return fuelShiftTransferDao.createFinDocAndShiftPayment(processType, shiftPayment, accountList);
    }

    @Override
    public int delete(ShiftPayment shiftPayment) {
        return fuelShiftTransferDao.delete(shiftPayment);
    }

    @Override
    public String jsonArrayAccounts(List<AccountMovement> accountMovements) {
        JsonArray jsonArray = new JsonArray();
        for (AccountMovement obj : accountMovements) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("account_id", obj.getAccount().getId());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public List<FuelShiftSales> findAllSaleForShift(FuelShift fuelShift) {
        return fuelShiftTransferDao.findAllSaleForShift(fuelShift);
    }

    @Override
    public List<FuelShiftSales> findAllSale(FuelShift fuelShift) {
        return fuelShiftTransferDao.findAllSale(fuelShift);
    }

    @Override
    public List<FuelShiftSales> findAllCreditSales(FuelShift fuelShift, FuelShiftSales fuelShiftSales, BranchSetting branchSetting, boolean isAllSales) {
        return fuelShiftTransferDao.findAllCreditSales(fuelShift, fuelShiftSales, branchSetting, isAllSales);
    }

    @Override
    public String jsonArrayShiftSale(List<FuelShiftSales> listOfFuelShiftSale) {
        JsonArray jsonArray = new JsonArray();
        for (FuelShiftSales obj : listOfFuelShiftSale) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("account_id", obj.getCredit().getAccount().getId());
            jsonObject.addProperty("sale_id", obj.getId());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public void createExcelFile(FuelShift fuelShift, BranchSetting branchSetting) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = fuelShiftTransferDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftTransferDao.findSalesAccordingToStockForExcel(fuelShift));
            rs = prep.executeQuery();
            int jRow = 0;

            CellStyle stylesub = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());

            BigDecimal totalColumn1, totalColumn2, totalColumn3, totalColumn4;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("detailfuelshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty0 = excelDocument.getSheet().createRow(jRow++);
            /////////////////////////
            SXSSFRow branchnamerow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell branchname = branchnamerow.createCell((short) 0);
            branchname.setCellValue(sessionBean.getLoc().getString("branchname") + " : ");
            branchname.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell branchnameVal = branchnamerow.createCell((short) 1);
            branchnameVal.setCellValue(sessionBean.getUser().getLastBranch().getName());
            ///////////////
            SXSSFRow shiftNoRow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell shiftNoDesc = shiftNoRow.createCell((short) 0);
            shiftNoDesc.setCellValue(sessionBean.getLoc().getString("shiftno") + " : ");
            shiftNoDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell shiftNoVal = shiftNoRow.createCell((short) 1);
            shiftNoVal.setCellValue((fuelShift.getShiftNo() == null ? "" : fuelShift.getShiftNo()));
            /////////
            SXSSFRow startdaterow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell startdateDesc = startdaterow.createCell((short) 0);
            startdateDesc.setCellValue(sessionBean.getLoc().getString("startdate") + " : ");
            startdateDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell startdateVal = startdaterow.createCell((short) 1);
            startdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShift.getBeginDate()));
            /////////
            SXSSFRow enddateRow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell enddateDesc = enddateRow.createCell((short) 0);
            enddateDesc.setCellValue(sessionBean.getLoc().getString("enddate") + " : ");
            enddateDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell enddateVal = enddateRow.createCell((short) 1);
            enddateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShift.getEndDate()));
            /////////
            SXSSFRow totalamountrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell totalamountDesc = totalamountrow.createCell((short) 0);
            totalamountDesc.setCellValue(sessionBean.getLoc().getString("totalsalesamount") + " : ");
            totalamountDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell totalamountVal = totalamountrow.createCell((short) 1);
            totalamountVal.setCellValue(StaticMethods.round(fuelShift.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0));
            ///

            SXSSFRow empty4 = excelDocument.getSheet().createRow(jRow++);

            //* ****************************Stok Miktarlarına Göre*************************
            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);
            totalColumn3 = BigDecimal.valueOf(0);
            totalColumn4 = BigDecimal.valueOf(0);
            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 5));
            SXSSFRow reportName = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader1 = reportName.createCell((short) 0);
            cellheader1.setCellValue(sessionBean.getLoc().getString("accordingtostockquantity"));
            cellheader1.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x = 0;
            SXSSFRow rowm = excelDocument.getSheet().createRow(jRow++);
            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerDarkRed", excelDocument.getWorkbook());

            SXSSFCell celld1 = rowm.createCell((short) x++);
            celld1.setCellValue(sessionBean.getLoc().getString("stockbarcode") + " - " + sessionBean.getLoc().getString("stockname"));
            celld1.setCellStyle(styleheader);

            SXSSFCell celld2 = rowm.createCell((short) x++);
            celld2.setCellValue(sessionBean.getLoc().getString("previousamount"));
            celld2.setCellStyle(styleheader);

            SXSSFCell celld3 = rowm.createCell((short) x++);
            celld3.setCellValue(sessionBean.getLoc().getString("salesamount"));
            celld3.setCellStyle(styleheader);

            SXSSFCell celld4 = rowm.createCell((short) x++);
            celld4.setCellValue(sessionBean.getLoc().getString("unitprice"));
            celld4.setCellStyle(styleheader);

            SXSSFCell celld5 = rowm.createCell((short) x++);
            celld5.setCellValue(sessionBean.getLoc().getString("salesprice"));
            celld5.setCellStyle(styleheader);

            SXSSFCell celld6 = rowm.createCell((short) x++);
            celld6.setCellValue(sessionBean.getLoc().getString("remainingamount"));
            celld6.setCellStyle(styleheader);

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("sslliter"));
                totalColumn2 = totalColumn2.add(rs.getBigDecimal("ssltotalmoney"));
                totalColumn3 = totalColumn3.add(rs.getBigDecimal("previousamount"));
                totalColumn4 = totalColumn4.add(rs.getBigDecimal("remainingamount"));

                row.createCell((short) b++).setCellValue((rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")) + " - " + (rs.getString("stckname") == null ? "" : rs.getString("stckname")));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("previousamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("sslliter").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                if (rs.getBigDecimal("sslliter") != null && rs.getBigDecimal("sslliter").compareTo(BigDecimal.valueOf(0)) != 0) {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("ssltotalmoney").divide(rs.getBigDecimal("sslliter"), 4, RoundingMode.HALF_EVEN).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                } else {
                    row.createCell((short) b++).setCellValue(StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("ssltotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("remainingamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

            }
            //Toplam 
            SXSSFRow rowsub1 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellsub0 = rowsub1.createCell((short) 0);
            cellsub0.setCellValue(sessionBean.getLoc().getString("sum") + ":");
            cellsub0.setCellStyle(stylesub);

            SXSSFCell cellsub1 = rowsub1.createCell((short) 1);
            cellsub1.setCellValue(StaticMethods.round(totalColumn3.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsub1.setCellStyle(stylesub);

            SXSSFCell cellsub2 = rowsub1.createCell((short) 2);
            cellsub2.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsub2.setCellStyle(stylesub);

            SXSSFCell cellsub3 = rowsub1.createCell((short) 4);
            cellsub3.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsub3.setCellStyle(stylesub);

            SXSSFCell cellsub4 = rowsub1.createCell((short) 5);
            cellsub4.setCellValue(StaticMethods.round(totalColumn4.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsub4.setCellStyle(stylesub);

            SXSSFRow empty5 = excelDocument.getSheet().createRow(jRow++);

            //* ****************************Nakit Teslimat*************************
            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCashDetail(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);

            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 4));
            SXSSFRow reportName1 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader2 = reportName1.createCell((short) 0);
            cellheader2.setCellValue(sessionBean.getLoc().getString("cashdelivery"));
            cellheader2.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x1 = 0;
            SXSSFRow rowm1 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell celld11 = rowm1.createCell((short) x1++);
            celld11.setCellValue(sessionBean.getLoc().getString("safecode") + " - " + sessionBean.getLoc().getString("safename"));
            celld11.setCellStyle(styleheader);

            SXSSFCell celld12 = rowm1.createCell((short) x1++);
            celld12.setCellValue(sessionBean.getLoc().getString("currency"));
            celld12.setCellStyle(styleheader);

            SXSSFCell celld13 = rowm1.createCell((short) x1++);
            celld13.setCellValue(sessionBean.getLoc().getString("total"));
            celld13.setCellStyle(styleheader);

            SXSSFCell celld14 = rowm1.createCell((short) x1++);
            celld14.setCellValue(sessionBean.getLoc().getString("exchangerate"));
            celld14.setCellStyle(styleheader);

            SXSSFCell celld15 = rowm1.createCell((short) x1++);
            celld15.setCellValue(sessionBean.getLoc().getString("exchangeprice"));
            celld15.setCellStyle(styleheader);

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                totalColumn1 = totalColumn1.add(rs.getBigDecimal("price"));

                row.createCell((short) b++).setCellValue((rs.getString("sfcode") == null ? "" : rs.getString("sfcode")) + " - " + (rs.getString("sfname") == null ? "" : rs.getString("sfname")));
                row.createCell((short) b++).setCellValue(sessionBean.currencySignOrCode(rs.getInt("currency"), 0));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("price").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("exchangerate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("price").multiply(rs.getBigDecimal("exchangerate")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

            }

            //Toplam 
            SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellsub01 = rowsub2.createCell((short) 0);
            cellsub01.setCellValue(sessionBean.getLoc().getString("sum") + ":");
            cellsub01.setCellStyle(stylesub);

            SXSSFCell cellsub11 = rowsub2.createCell((short) 1);
            cellsub11.setCellValue("");
            cellsub11.setCellStyle(stylesub);

            SXSSFCell cellsub12 = rowsub2.createCell((short) 2);
            cellsub12.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsub12.setCellStyle(stylesub);

            SXSSFRow empty6 = excelDocument.getSheet().createRow(jRow++);

            //* ****************************POS Toplam*************************
            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCreditCardDetail(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);

            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 1));
            SXSSFRow reportName4 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader5 = reportName4.createCell((short) 0);
            cellheader5.setCellValue(sessionBean.getLoc().getString("creditcarddelivery"));
            cellheader5.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x4 = 0;
            SXSSFRow rowmCreditCard = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell celld41 = rowmCreditCard.createCell((short) x4++);
            celld41.setCellValue(sessionBean.getLoc().getString("bankname"));
            celld41.setCellStyle(styleheader);

            SXSSFCell celld42 = rowmCreditCard.createCell((short) x4++);
            celld42.setCellValue(sessionBean.getLoc().getString("total"));
            celld42.setCellStyle(styleheader);

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("price").multiply(rs.getBigDecimal("exchangerate")));

                row.createCell((short) b++).setCellValue((rs.getString("bkaname") == null ? "" : rs.getString("bkaname")));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("price").doubleValue() * rs.getBigDecimal("exchangerate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }

            //Toplam 
            SXSSFRow rowsubCreditCard = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellsubCreditCard = rowsubCreditCard.createCell((short) 0);
            cellsubCreditCard.setCellValue(sessionBean.getLoc().getString("sum") + ":");
            cellsubCreditCard.setCellStyle(stylesub);

            SXSSFCell cellsubCreditCard1 = rowsubCreditCard.createCell((short) 1);
            cellsubCreditCard1.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsubCreditCard1.setCellStyle(stylesub);

            SXSSFRow empty9 = excelDocument.getSheet().createRow(jRow++);

            //* ****************************Veresiye Toplam*************************
            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCredit(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);

            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 2));
            SXSSFRow reportName2 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader3 = reportName2.createCell((short) 0);
            cellheader3.setCellValue(sessionBean.getLoc().getString("creditdelivery"));
            cellheader3.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x2 = 0;
            SXSSFRow rowm2 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell celld21 = rowm2.createCell((short) x2++);
            celld21.setCellValue(sessionBean.getLoc().getString("accountcode") + " - " + sessionBean.getLoc().getString("currentname"));
            celld21.setCellStyle(styleheader);

            SXSSFCell celld22 = rowm2.createCell((short) x2++);
            celld22.setCellValue(sessionBean.getLoc().getString("receiptcount"));
            celld22.setCellStyle(styleheader);

            SXSSFCell celld23 = rowm2.createCell((short) x2++);
            celld23.setCellValue(sessionBean.getLoc().getString("totalofpostpaid"));
            celld23.setCellStyle(styleheader);

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("ssltotalmoney"));
                totalColumn2 = totalColumn2.add(BigDecimal.valueOf(rs.getInt("salecount")));

                if (rs.getBoolean("accis_employee")) {
                    row.createCell((short) b++).setCellValue((rs.getString("acccode") == null ? "" : rs.getString("acccode")) + " - " + (rs.getString("accname") == null ? "" : rs.getString("accname")) + " " + (rs.getString("acctitle") == null ? "" : rs.getString("acctitle")));
                } else {
                    row.createCell((short) b++).setCellValue((rs.getString("acccode") == null ? "" : rs.getString("acccode")) + " - " + (rs.getString("accname") == null ? "" : rs.getString("accname")));
                }

                row.createCell((short) b++).setCellValue(rs.getInt("salecount"));

                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("ssltotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }

            //Toplam 
            SXSSFRow rowsub3 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellsubCredit = rowsub3.createCell((short) 0);
            cellsubCredit.setCellValue(sessionBean.getLoc().getString("sum") + ":");
            cellsubCredit.setCellStyle(stylesub);

            SXSSFCell cellsubCredit1 = rowsub3.createCell((short) 1);
            cellsubCredit1.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsubCredit1.setCellStyle(stylesub);

            SXSSFCell cellsubCredit2 = rowsub3.createCell((short) 2);
            cellsubCredit2.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsubCredit2.setCellStyle(stylesub);

            SXSSFRow empty7 = excelDocument.getSheet().createRow(jRow++);
            //* ****************************Cari Tahsilat Toplam*************************
            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentDeficitExcess(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);

            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 5));
            SXSSFRow reportName5 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader6 = reportName5.createCell((short) 0);
            cellheader6.setCellValue(sessionBean.getLoc().getString("accountrecoveries"));
            cellheader6.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x5 = 0;
            SXSSFRow rowmAccount = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell celld51 = rowmAccount.createCell((short) x5++);
            celld51.setCellValue(sessionBean.getLoc().getString("accountcode") + " - " + sessionBean.getLoc().getString("currentname"));
            celld51.setCellStyle(styleheader);

            SXSSFCell celld52 = rowmAccount.createCell((short) x5++);
            celld52.setCellValue(sessionBean.getLoc().getString("documentno"));
            celld52.setCellStyle(styleheader);

            SXSSFCell celld53 = rowmAccount.createCell((short) x5++);
            celld53.setCellValue(sessionBean.getLoc().getString("description"));
            celld53.setCellStyle(styleheader);

            SXSSFCell celld54 = rowmAccount.createCell((short) x5++);
            celld54.setCellValue(sessionBean.getLoc().getString("entryttotal"));
            celld54.setCellStyle(styleheader);

            SXSSFCell celld55 = rowmAccount.createCell((short) x5++);
            celld55.setCellValue(sessionBean.getLoc().getString("exittotal"));
            celld55.setCellStyle(styleheader);

            SXSSFCell celld56 = rowmAccount.createCell((short) x5++);
            celld56.setCellValue(sessionBean.getLoc().getString("operationtype"));
            celld56.setCellStyle(styleheader);

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("givenemployee"));
                totalColumn2 = totalColumn2.add(rs.getBigDecimal("employeedebt"));

                if (rs.getBoolean("accis_employee")) {
                    row.createCell((short) b++).setCellValue((rs.getString("acccode") == null ? "" : rs.getString("acccode") + " - ") + (rs.getString("accname") == null ? "" : rs.getString("accname")) + " " + (rs.getString("acctitle") == null ? "" : rs.getString("acctitle")));
                } else {
                    row.createCell((short) b++).setCellValue((rs.getString("acccode") == null ? "" : rs.getString("acccode") + " - ") + (rs.getString("accname") == null ? "" : rs.getString("accname")));
                }

                row.createCell((short) b++).setCellValue((rs.getString("fdocdocumnetnumber") == null ? "" : rs.getString("fdocdocumnetnumber")));
                row.createCell((short) b++).setCellValue((rs.getString("fdocdescription") == null ? "" : rs.getString("fdocdescription")));

                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("givenemployee").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row.createCell((short) b++).setCellValue(StaticMethods.round(rs.getBigDecimal("employeedebt").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                if (rs.getBoolean("accis_employee")) {
                    if (rs.getInt("fdoctype_id") == 49) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("shiftdeficit"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("shiftexcess"));
                    }
                } else {

                    if (rs.getInt("fdoctype_id") == 49) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("accountcollection"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("accountpayment"));
                    }
                }

            }
            //Toplam 
            SXSSFRow rowsub6 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell cellsubShift = rowsub6.createCell((short) 0);
            cellsubShift.setCellValue(sessionBean.getLoc().getString("sum") + ":");
            cellsubShift.setCellStyle(stylesub);

            SXSSFCell cellsubShift1 = rowsub6.createCell((short) 3);
            cellsubShift1.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsubShift1.setCellStyle(stylesub);

            SXSSFCell cellsubShift2 = rowsub6.createCell((short) 4);
            cellsubShift2.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            cellsubShift2.setCellStyle(stylesub);

            SXSSFRow empty11 = excelDocument.getSheet().createRow(jRow++);

            //* ****************************Vardiya Genel Toplam*************************
            prep = connection.prepareStatement(fuelShiftTransferDao.shiftGeneralTotal(fuelShift, branchSetting));
            rs = prep.executeQuery();

            excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 2));
            SXSSFRow reportName3 = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader4 = reportName3.createCell((short) 0);
            cellheader4.setCellValue(sessionBean.getLoc().getString("shiftgeneraltotals"));
            cellheader4.setCellStyle(headerCss(excelDocument.getWorkbook()));

            int x3 = 0;
            SXSSFRow rowm3 = excelDocument.getSheet().createRow(jRow++);

            SXSSFCell celld31 = rowm3.createCell((short) x3++);
            celld31.setCellValue(sessionBean.getLoc().getString("description"));
            celld31.setCellStyle(styleheader);

            SXSSFCell celld32 = rowm3.createCell((short) x3++);
            celld32.setCellValue(sessionBean.getLoc().getString("entryttotal"));
            celld32.setCellStyle(styleheader);

            SXSSFCell celld33 = rowm3.createCell((short) x3++);
            celld33.setCellValue(sessionBean.getLoc().getString("exittotal"));
            celld33.setCellStyle(styleheader);

            while (rs.next()) {
                SXSSFRow row0 = excelDocument.getSheet().createRow(jRow++);
                row0.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("saleamountwithfuelcounter"));
                row0.createCell((short) 1).setCellValue(StaticMethods.round(fuelShift.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row0.createCell((short) 2).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);
                row1.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofpostpaid"));
                row1.createCell((short) 1).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row1.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("creditamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
                row2.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("automationamount"));
                row2.createCell((short) 1).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row2.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("automationsale").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row3 = excelDocument.getSheet().createRow(jRow++);
                row3.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofpos"));
                row3.createCell((short) 1).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row3.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("creditcardamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row4 = excelDocument.getSheet().createRow(jRow++);
                row4.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cashdeliveryamount"));
                row4.createCell((short) 1).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row4.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("cashamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row5 = excelDocument.getSheet().createRow(jRow++);
                row5.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofincome"));
                row5.createCell((short) 1).setCellValue(StaticMethods.round(rs.getBigDecimal("incomeamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row5.createCell((short) 2).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row6 = excelDocument.getSheet().createRow(jRow++);
                row6.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofexpense"));
                row6.createCell((short) 1).setCellValue(StaticMethods.round(0.0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row6.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("expenseamount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row7 = excelDocument.getSheet().createRow(jRow++);
                row7.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofemployee"));
                row7.createCell((short) 1).setCellValue(StaticMethods.round(rs.getBigDecimal("givenemployee").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row7.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("employeedebt").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row8 = excelDocument.getSheet().createRow(jRow++);
                row8.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalofaccount"));
                row8.createCell((short) 1).setCellValue(StaticMethods.round(rs.getBigDecimal("accountcollection").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row8.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("accountpayment").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row9 = excelDocument.getSheet().createRow(jRow++);
                row9.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("subtotal"));
                row9.createCell((short) 1).setCellValue(StaticMethods.round(rs.getBigDecimal("entrysubtotal").doubleValue() + fuelShift.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row9.createCell((short) 2).setCellValue(StaticMethods.round(rs.getBigDecimal("exitsubtotal").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row10 = excelDocument.getSheet().createRow(jRow++);
                row10.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("difference"));

                BigDecimal entryttotal = StaticMethods.round(rs.getBigDecimal("entrysubtotal").add(fuelShift.getTotalMoney()), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                BigDecimal exittotal = StaticMethods.round(rs.getBigDecimal("exitsubtotal"), sessionBean.getUser().getLastBranch().getCurrencyrounding());

                BigDecimal diffin = BigDecimal.ZERO;
                BigDecimal diffout = BigDecimal.ZERO;
                if (entryttotal.compareTo(exittotal) > 0) {
                    diffin = BigDecimal.ZERO;
                    diffout = entryttotal.subtract(exittotal);
                } else if (exittotal.compareTo(entryttotal) > 0) {
                    diffout = BigDecimal.ZERO;
                    diffin = exittotal.subtract(entryttotal);
                }

                row10.createCell((short) 1).setCellValue(StaticMethods.round(diffin.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row10.createCell((short) 2).setCellValue(StaticMethods.round(diffout.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFRow row11 = excelDocument.getSheet().createRow(jRow++);
                row11.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("overalltotal"));
                row11.createCell((short) 1).setCellValue(StaticMethods.round(diffin.doubleValue() + entryttotal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                row11.createCell((short) 2).setCellValue(StaticMethods.round(diffout.doubleValue() + exittotal.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("detailfuelshiftreport"));
            } catch (IOException ex) {
                Logger.getLogger(FuelShiftTransferService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(FuelShiftTransferDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void createPdfFile(FuelShift fuelShift, BranchSetting branchSetting) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        List<Boolean> toogleList;
        BigDecimal totalColumn1, totalColumn2, totalColumn3, totalColumn4;

        try {
            connection = fuelShiftTransferDao.getDatasource().getConnection();
            prep = connection.prepareStatement(fuelShiftTransferDao.findSalesAccordingToStockForExcel(fuelShift));
            rs = prep.executeQuery();

            toogleList = Arrays.asList(true, true, true, true, true, true);
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("detailfuelshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branchname") + " : " + sessionBean.getUser().getLastBranch().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + (fuelShift.getShiftNo() == null ? "" : fuelShift.getShiftNo()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShift.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), fuelShift.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalsalesamount") + " : " + sessionBean.getNumberFormat().format(fuelShift.getTotalMoney()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //* ****************************Stok Miktarlarına Göre*************************
            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);
            totalColumn3 = BigDecimal.valueOf(0);
            totalColumn4 = BigDecimal.valueOf(0);

            List<Unit> unitList = new ArrayList<>();
            unitList = unitDao.findAll();
            HashMap<Integer, BigDecimal> groupTotal1 = new HashMap<>();
            HashMap<Integer, BigDecimal> groupTotal2 = new HashMap<>();
            HashMap<Integer, BigDecimal> groupTotal3 = new HashMap<>();

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocument, pdfDocument.getTableHeader());
            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocument, pdfDocument.getHeader());

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accordingtostockquantity"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getHeader().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockbarcode") + " - " + sessionBean.getLoc().getString("stockname"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("previousamount"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("unitprice"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamount"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                totalColumn2 = totalColumn2.add(rs.getBigDecimal("ssltotalmoney"));

                formatterUnit.setMaximumFractionDigits(rs.getInt("guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("guntunitsorting"));

                pdfDocument.getDataCell().setPhrase(new Phrase((rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")) + " - " + (rs.getString("stckname") == null ? "" : rs.getString("stckname")), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previousamount")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sslliter")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                if (rs.getBigDecimal("sslliter") != null && rs.getBigDecimal("sslliter").compareTo(BigDecimal.valueOf(0)) != 0) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssltotalmoney").divide(rs.getBigDecimal("sslliter"), 4, RoundingMode.HALF_EVEN)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                } else {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0.0), pdfDocument.getFont()));
                }
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssltotalmoney")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("remainingamount")) + rs.getString("guntsortname"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                if (groupTotal1.containsKey(rs.getInt("stckunit_id"))) {
                    BigDecimal old = groupTotal1.get(rs.getInt("stckunit_id"));
                    groupTotal1.put(rs.getInt("stckunit_id"), old.add(rs.getBigDecimal("previousamount")));
                } else {
                    groupTotal1.put(rs.getInt("stckunit_id"), rs.getBigDecimal("previousamount"));
                }

                if (groupTotal2.containsKey(rs.getInt("stckunit_id"))) {
                    BigDecimal old = groupTotal2.get(rs.getInt("stckunit_id"));
                    groupTotal2.put(rs.getInt("stckunit_id"), old.add(rs.getBigDecimal("sslliter")));
                } else {
                    groupTotal2.put(rs.getInt("stckunit_id"), rs.getBigDecimal("sslliter"));
                }

                if (groupTotal3.containsKey(rs.getInt("stckunit_id"))) {
                    BigDecimal old = groupTotal3.get(rs.getInt("stckunit_id"));
                    groupTotal3.put(rs.getInt("stckunit_id"), old.add(rs.getBigDecimal("remainingamount")));
                } else {
                    groupTotal3.put(rs.getInt("stckunit_id"), rs.getBigDecimal("remainingamount"));
                }
            }

            String previous = "";
            int temp = 0;
            for (Map.Entry<Integer, BigDecimal> entry : groupTotal1.entrySet()) {
                if (temp == 0) {
                    temp = 1;
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }

                    previous += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        previous += " " + unit.getSortName();
                    }
                } else {
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }
                    previous += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        previous += " " + unit.getSortName();
                    }
                }

            }

            String amount = "";
            int temp1 = 0;
            for (Map.Entry<Integer, BigDecimal> entry : groupTotal2.entrySet()) {

                if (temp1 == 0) {
                    temp1 = 1;
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }

                    amount += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        amount += " " + unit.getSortName();
                    }
                } else {
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }
                    amount += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        amount += " " + unit.getSortName();
                    }
                }
            }

            String remaining = "";
            int temp2 = 0;
            for (Map.Entry<Integer, BigDecimal> entry : groupTotal3.entrySet()) {

                if (temp2 == 0) {
                    temp2 = 1;
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }

                    remaining += String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        remaining += " " + unit.getSortName();
                    }
                } else {
                    Unit unit = new Unit();
                    for (Unit unit1 : unitList) {
                        if (unit1.getId() == entry.getKey()) {
                            unit.setSortName(unit1.getSortName());
                            unit.setUnitRounding(unit1.getUnitRounding());
                            formatterUnit.setMaximumFractionDigits(unit.getUnitRounding());
                            formatterUnit.setMinimumFractionDigits(unit.getUnitRounding());
                            break;
                        }
                    }
                    remaining += " + " + String.valueOf(formatterUnit.format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        remaining += " " + unit.getSortName();
                    }
                }

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getRightCell());
            pdfDocument.getRightCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : ", pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(previous, pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(amount, pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(remaining, pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //* ****************************Nakit Teslimat*************************
            PdfDocument pdfDocumentCash = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);

            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCashDetail(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);

            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocumentCash, pdfDocumentCash.getTableHeader());
            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocumentCash, pdfDocumentCash.getHeader());

            pdfDocumentCash.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("cashdelivery"), pdfDocumentCash.getFontHeader()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getHeader());

            pdfDocumentCash.getHeader().setPhrase(new Phrase("", pdfDocumentCash.getFont()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getHeader());

            pdfDocumentCash.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("casecodename"), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getTableHeader());

            pdfDocumentCash.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("currency"), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getTableHeader());

            pdfDocumentCash.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getTableHeader());

            pdfDocumentCash.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangerate"), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getTableHeader());

            pdfDocumentCash.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangeprice"), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getTableHeader());

            pdfDocument.getDocument().add(pdfDocumentCash.getPdfTable());

            while (rs.next()) {

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("price"));

                pdfDocumentCash.getDataCell().setPhrase(new Phrase((rs.getString("sfcode") == null ? "" : rs.getString("sfcode")) + " - " + (rs.getString("sfname") == null ? "" : rs.getString("sfname")), pdfDocumentCash.getFont()));
                pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getDataCell());

                pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.currencySignOrCode(rs.getInt("currency"), 0), pdfDocumentCash.getFont()));
                pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

                pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("price")), pdfDocumentCash.getFont()));
                pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

                pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("exchangerate")), pdfDocumentCash.getFont()));
                pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

                pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("exchangerate"))), pdfDocumentCash.getFont()));
                pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentCash.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocumentCash, pdfDocumentCash.getRightCell());
            pdfDocumentCash.getRightCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : ", pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

            pdfDocumentCash.getRightCell().setPhrase(new Phrase("", pdfDocumentCash.getFont()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

            pdfDocumentCash.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn1), pdfDocumentCash.getFontColumnTitle()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

            pdfDocumentCash.getRightCell().setPhrase(new Phrase("", pdfDocumentCash.getFont()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

            pdfDocumentCash.getRightCell().setPhrase(new Phrase("", pdfDocumentCash.getFont()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getRightCell());

            pdfDocumentCash.getCell().setPhrase(new Phrase(" ", pdfDocumentCash.getFont()));
            pdfDocumentCash.getPdfTable().addCell(pdfDocumentCash.getCell());

            pdfDocument.getDocument().add(pdfDocumentCash.getPdfTable());

            //* ****************************POS Toplam*************************
            PdfDocument pdfDocumentCreditCard = StaticMethods.preparePdf(Arrays.asList(true, true), 0);

            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCreditCardDetail(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);

            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocumentCreditCard, pdfDocumentCreditCard.getTableHeader());
            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocumentCreditCard, pdfDocumentCreditCard.getHeader());

            pdfDocumentCreditCard.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("creditcarddelivery"), pdfDocumentCreditCard.getFontHeader()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getHeader());

            pdfDocumentCreditCard.getHeader().setPhrase(new Phrase("", pdfDocumentCreditCard.getFont()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getHeader());

            pdfDocumentCreditCard.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("bankname"), pdfDocumentCreditCard.getFontColumnTitle()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getTableHeader());

            pdfDocumentCreditCard.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentCreditCard.getFontColumnTitle()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getTableHeader());

            pdfDocument.getDocument().add(pdfDocumentCreditCard.getPdfTable());

            while (rs.next()) {

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("price").multiply(rs.getBigDecimal("exchangerate")));

                pdfDocumentCreditCard.getDataCell().setPhrase(new Phrase((rs.getString("bkaname") == null ? "" : rs.getString("bkaname")), pdfDocumentCreditCard.getFont()));
                pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getDataCell());

                pdfDocumentCreditCard.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("price").multiply(rs.getBigDecimal("exchangerate"))) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentCreditCard.getFont()));
                pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentCreditCard.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocumentCreditCard, pdfDocumentCreditCard.getRightCell());
            pdfDocumentCreditCard.getRightCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            pdfDocumentCreditCard.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : ", pdfDocumentCreditCard.getFontColumnTitle()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getRightCell());

            pdfDocumentCreditCard.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn1) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentCreditCard.getFontColumnTitle()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getRightCell());

            pdfDocumentCreditCard.getCell().setPhrase(new Phrase(" ", pdfDocumentCreditCard.getFont()));
            pdfDocumentCreditCard.getPdfTable().addCell(pdfDocumentCreditCard.getCell());

            pdfDocument.getDocument().add(pdfDocumentCreditCard.getPdfTable());

            //* ****************************Veresiye Toplam*************************
            PdfDocument pdfDocumentCredit = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);

            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentCredit(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);

            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocumentCredit, pdfDocumentCredit.getHeader());
            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocumentCredit, pdfDocumentCredit.getTableHeader());

            pdfDocumentCredit.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("creditdelivery"), pdfDocumentCredit.getFontHeader()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getHeader());

            pdfDocumentCredit.getHeader().setPhrase(new Phrase("", pdfDocumentCredit.getFont()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getHeader());

            pdfDocumentCredit.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accountcode") + " - " + sessionBean.getLoc().getString("currentname"), pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getTableHeader());

            pdfDocumentCredit.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("receiptcount"), pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getTableHeader());

            pdfDocumentCredit.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofpostpaid"), pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getTableHeader());

            pdfDocument.getDocument().add(pdfDocumentCredit.getPdfTable());

            while (rs.next()) {

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("ssltotalmoney"));
                totalColumn2 = totalColumn2.add(BigDecimal.valueOf(rs.getInt("salecount")));

                if (rs.getBoolean("accis_employee")) {
                    pdfDocumentCredit.getDataCell().setPhrase(new Phrase((rs.getString("acccode") == null ? "" : rs.getString("acccode")) + " - " + (rs.getString("accname") == null ? "" : rs.getString("accname")) + " " + (rs.getString("acctitle") == null ? "" : rs.getString("acctitle")), pdfDocumentCredit.getFont()));
                } else {
                    pdfDocumentCredit.getDataCell().setPhrase(new Phrase((rs.getString("acccode") == null ? "" : rs.getString("acccode")) + " - " + (rs.getString("accname") == null ? "" : rs.getString("accname")), pdfDocumentCredit.getFont()));
                }
                pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getDataCell());

                pdfDocumentCredit.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getInt("salecount")), pdfDocumentCredit.getFont()));
                pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getRightCell());

                pdfDocumentCredit.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("ssltotalmoney")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentCredit.getFont()));
                pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentCredit.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocumentCredit, pdfDocumentCredit.getRightCell());
            pdfDocumentCredit.getRightCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            pdfDocumentCredit.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : ", pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getRightCell());

            pdfDocumentCredit.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn2), pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getRightCell());

            pdfDocumentCredit.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn1) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentCredit.getFontColumnTitle()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getRightCell());

            pdfDocumentCredit.getCell().setPhrase(new Phrase(" ", pdfDocumentCredit.getFont()));
            pdfDocumentCredit.getPdfTable().addCell(pdfDocumentCredit.getCell());

            pdfDocument.getDocument().add(pdfDocumentCredit.getPdfTable());

            //* ****************************Cari Tahsilat Toplam*************************
            PdfDocument pdfDocumentAccountRecovery = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true), 0);

            prep = connection.prepareStatement(fuelShiftTransferDao.shiftPaymentDeficitExcess(fuelShift));
            rs = prep.executeQuery();

            totalColumn1 = BigDecimal.valueOf(0);
            totalColumn2 = BigDecimal.valueOf(0);

            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocumentAccountRecovery, pdfDocumentAccountRecovery.getHeader());
            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocumentAccountRecovery, pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accountrecoveries"), pdfDocumentAccountRecovery.getFontHeader()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getHeader());

            pdfDocumentAccountRecovery.getHeader().setPhrase(new Phrase("", pdfDocumentAccountRecovery.getFont()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accountcode") + " - " + sessionBean.getLoc().getString("currentname"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("documentno"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("entryttotal"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exittotal"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocumentAccountRecovery.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("operationtype"), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getTableHeader());

            pdfDocument.getDocument().add(pdfDocumentAccountRecovery.getPdfTable());

            while (rs.next()) {

                totalColumn1 = totalColumn1.add(rs.getBigDecimal("givenemployee"));
                totalColumn2 = totalColumn2.add(rs.getBigDecimal("employeedebt"));

                if (rs.getBoolean("accis_employee")) {
                    pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase((rs.getString("acccode") == null ? "" : rs.getString("acccode") + " - ") + (rs.getString("accname") == null ? "" : rs.getString("accname")) + " " + (rs.getString("acctitle") == null ? "" : rs.getString("acctitle")), pdfDocumentAccountRecovery.getFont()));
                } else {
                    pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase((rs.getString("acccode") == null ? "" : rs.getString("acccode") + " - ") + (rs.getString("accname") == null ? "" : rs.getString("accname")), pdfDocumentAccountRecovery.getFont()));
                }
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getDataCell());

                pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(rs.getString("fdocdocumnetnumber"), pdfDocumentAccountRecovery.getFont()));
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getDataCell());

                pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(rs.getString("fdocdescription"), pdfDocumentAccountRecovery.getFont()));
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getDataCell());

                pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("givenemployee")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentAccountRecovery.getFont()));
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

                pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("employeedebt")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentAccountRecovery.getFont()));
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());
                if (rs.getBoolean("accis_employee")) {
                    if (rs.getInt("fdoctype_id") == 49) {
                        pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftdeficit"), pdfDocumentAccountRecovery.getFont()));
                    } else {
                        pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftexcess"), pdfDocumentAccountRecovery.getFont()));
                    }
                } else {
                    if (rs.getInt("fdoctype_id") == 49) {
                        pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("accountcollection"), pdfDocumentAccountRecovery.getFont()));
                    } else {
                        pdfDocumentAccountRecovery.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("accountpayment"), pdfDocumentAccountRecovery.getFont()));
                    }
                }
                pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getDataCell());

                pdfDocument.getDocument().add(pdfDocumentAccountRecovery.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocumentAccountRecovery, pdfDocumentAccountRecovery.getRightCell());
            pdfDocumentAccountRecovery.getRightCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : ", pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase("", pdfDocumentAccountRecovery.getFont()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase("", pdfDocumentAccountRecovery.getFont()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn1) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(totalColumn2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentAccountRecovery.getFontColumnTitle()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getRightCell().setPhrase(new Phrase("", pdfDocumentAccountRecovery.getFont()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getRightCell());

            pdfDocumentAccountRecovery.getCell().setPhrase(new Phrase(" ", pdfDocumentAccountRecovery.getFont()));
            pdfDocumentAccountRecovery.getPdfTable().addCell(pdfDocumentAccountRecovery.getCell());

            pdfDocument.getDocument().add(pdfDocumentAccountRecovery.getPdfTable());

            //* ****************************Vardiya Genel Toplam*************************
            PdfDocument pdfDocumentGeneralTotal = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);

            prep = connection.prepareStatement(fuelShiftTransferDao.shiftGeneralTotal(fuelShift, branchSetting));
            rs = prep.executeQuery();

            StaticMethods.createCellStylePdf("headerDarkRedBold", pdfDocumentGeneralTotal, pdfDocumentGeneralTotal.getHeader());
            StaticMethods.createCellStylePdf("headerDarkRed", pdfDocumentGeneralTotal, pdfDocumentGeneralTotal.getTableHeader());

            pdfDocumentGeneralTotal.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftgeneraltotals"), pdfDocumentGeneralTotal.getFontHeader()));
            pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getHeader());

            pdfDocumentGeneralTotal.getHeader().setPhrase(new Phrase("", pdfDocumentGeneralTotal.getFont()));
            pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getHeader());

            pdfDocumentGeneralTotal.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), pdfDocumentGeneralTotal.getFontColumnTitle()));
            pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getTableHeader());

            pdfDocumentGeneralTotal.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("entryttotal"), pdfDocumentGeneralTotal.getFontColumnTitle()));
            pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getTableHeader());

            pdfDocumentGeneralTotal.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exittotal"), pdfDocumentGeneralTotal.getFontColumnTitle()));
            pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getTableHeader());

            pdfDocument.getDocument().add(pdfDocumentGeneralTotal.getPdfTable());

            while (rs.next()) {

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("saleamountwithfuelcounter"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuelShift.getTotalMoney()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofpostpaid"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("creditamount")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("automationamount"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("automationsale")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofpos"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("creditcardamount")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cashdeliveryamount"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("cashamount")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofincome"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("incomeamount")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofexpense"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(0) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("expenseamount")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofemployee"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("givenemployee")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("employeedebt")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofaccount"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accountcollection")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("accountpayment")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("subtotal"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("entrysubtotal").add(fuelShift.getTotalMoney())) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("exitsubtotal")) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("difference"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());

                BigDecimal entryttotal = StaticMethods.round(rs.getBigDecimal("entrysubtotal").add(fuelShift.getTotalMoney()), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                BigDecimal exittotal = StaticMethods.round(rs.getBigDecimal("exitsubtotal"), sessionBean.getUser().getLastBranch().getCurrencyrounding());

                BigDecimal diffin = BigDecimal.ZERO;
                BigDecimal diffout = BigDecimal.ZERO;
                if (entryttotal.compareTo(exittotal) > 0) {
                    diffin = BigDecimal.ZERO;
                    diffout = entryttotal.subtract(exittotal);
                } else if (exittotal.compareTo(entryttotal) > 0) {
                    diffout = BigDecimal.ZERO;
                    diffin = exittotal.subtract(entryttotal);
                }

                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(diffin) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(diffout) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocumentGeneralTotal.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("overalltotal"), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getDataCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(diffin.add(entryttotal)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());
                pdfDocumentGeneralTotal.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(diffout.add(exittotal)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocumentGeneralTotal.getFont()));
                pdfDocumentGeneralTotal.getPdfTable().addCell(pdfDocumentGeneralTotal.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentGeneralTotal.getPdfTable());
            }

            //////////////////////
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("detailfuelshiftreport"));

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
                Logger.getLogger(FuelShiftTransferDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public CellStyle headerCss(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 18);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.index);

        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public String findSalesAccordingToStockForExcel(FuelShift fuelShift) {
        return fuelShiftTransferDao.findSalesAccordingToStockForExcel(fuelShift);
    }

    @Override
    public String shiftPaymentCashDetail(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCashDetail(fuelShift);
    }

    @Override
    public String shiftPaymentCredit(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCredit(fuelShift);
    }

    @Override
    public String shiftGeneralTotal(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftTransferDao.shiftGeneralTotal(fuelShift, branchSetting);
    }

    @Override
    public String shiftPaymentCreditCardDetail(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCreditCardDetail(fuelShift);
    }

    @Override
    public String shiftPaymentDeficitExcess(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentDeficitExcess(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> findSalesAccordingToStockForPreview(FuelShift fuelShift) {
        return fuelShiftTransferDao.findSalesAccordingToStockForPreview(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCashDetailForPreview(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCashDetailForPreview(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCreditCardDetailForPreview(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCreditCardDetailForPreview(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCreditForPreview(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentCreditForPreview(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentDeficitExcessForPreview(FuelShift fuelShift) {
        return fuelShiftTransferDao.shiftPaymentDeficitExcessForPreview(fuelShift);
    }

    @Override
    public List<FuelShiftPreview> shiftGeneralTotalForPreview(FuelShift fuelShift, BranchSetting branchSetting) {
        return fuelShiftTransferDao.shiftGeneralTotalForPreview(fuelShift, branchSetting);
    }

    @Override
    public FuelShift findShift(FuelShift obj) {
        Map<String, Object> filt = new HashMap<>();

        List<FuelShift> list = fuelShiftTransferDao.findAll(0, 10, "shf.id", "ASC", filt, " AND shf.id = " + obj.getId(), false);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new FuelShift();
        }
    }

    @Override
    public int updateFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList) {
        return fuelShiftTransferDao.updateFinDocAndShiftPayment(processType, shiftPayment, accountList);
    }

    @Override
    public List<FuelShift> nonTransferableShift() {
        return fuelShiftTransferDao.nonTransferableShift();
    }

    @Override
    public int reSendErrorShift() {
        return fuelShiftTransferDao.reSendErrorShift();
    }

    @Override
    public String jsonArrayShiftControl(List<FuelShiftControlFile> shiftControlList) {
        JsonArray jsonArray = new JsonArray();
        for (FuelShiftControlFile f : shiftControlList) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("shiftno", f.getShiftNo());
            jsonObject.addProperty("filename", f.getFileName());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public List<FuelShiftControlFile> controlShiftNo(String shiftList) {
        return fuelShiftTransferDao.controlShiftNo(shiftList);
    }

    @Override
    public int controlVehicleAccountCon(FuelShiftSales fuelShiftSales) {
        return fuelShiftTransferDao.controlVehicleAccountCon(fuelShiftSales);
    }

    @Override
    public String createWhere(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";
        where = " AND ( shf.begindate >= '" + sd.format(beginDate) + "' AND (shf.enddate <= '" + sd.format(endDate) + "' OR  shf.enddate IS NULL ) ) \n";

        return where;
    }

}
