/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:26:49 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.business;

import com.lowagie.text.Anchor;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.business.MarketShiftService;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.dao.IStationSaleSummaryReportDao;
import com.mepsan.marwiz.general.report.stationsalessummaryreport.dao.StationSalesSummaryReport;
import com.mepsan.marwiz.general.report.stocktrackingreport.dao.StockTrackingReportDao;
import com.mepsan.marwiz.system.branch.dao.IBranchDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

public class StationSalesSummaryReportService implements IStationSalesSummaryReportService {

    @Autowired
    IStationSaleSummaryReportDao stationSalesSummaryReportDao;

    @Autowired
    IBranchDao branchDao;

    @Autowired
    SessionBean sessionBean;

    public void setStationSalesSummaryReportDao(IStationSaleSummaryReportDao stationSalesSummaryReportDao) {
        this.stationSalesSummaryReportDao = stationSalesSummaryReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchDao(IBranchDao branchDao) {
        this.branchDao = branchDao;
    }

    @Override
    public List<StationSalesSummaryReport> findFuelSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findFuelSales(beginDate, endDate, createWhere, selectedBranchList);
    }

    @Override
    public List<StationSalesSummaryReport> findFuelCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findFuelCollections(beginDate, endDate, createWhere, selectedBranchList);
    }

    @Override
    public List<StationSalesSummaryReport> findMarketSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findMarketSales(beginDate, endDate, createWhere, selectedBranchList);
    }

    @Override
    public List<StationSalesSummaryReport> findMarketCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findMarketCollections(beginDate, endDate, createWhere, selectedBranchList);
    }

    @Override
    public String createWhere(List<BranchSetting> selectedBranchList) {
        int branchId;
        List<Branch> list = new ArrayList<>();
        String branchList = "";
        if (selectedBranchList.size() == 1) {
            branchId = selectedBranchList.get(0).getBranch().getId();

        } else {

            branchId = -1;
        }
        for (BranchSetting branchSetting : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        String whereBranch = "";
        if (!selectedBranchList.isEmpty()) {
            whereBranch += " IN( " + branchList + " )";
        } else {
            list = branchDao.findUserAuthorizeBranch();
            for (Branch branchSetting : list) {
                branchList = branchList + "," + String.valueOf(branchSetting.getId());
                if (branchSetting.getId() == 0) {
                    branchList = "";
                    break;
                }
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
        }

        return whereBranch;
    }

    public CellStyle headerCss(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.index);

        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public void createExcel(Date beginDate, Date endDate, List<StationSalesSummaryReport> listOfFuelSales, List<StationSalesSummaryReport> listOfFuelCollection, List<StationSalesSummaryReport> listOfMarketSales, List<StationSalesSummaryReport> listOfMarketCollection, List<StationSalesSummaryReport> listOfTotalSales, List<StationSalesSummaryReport> listOfTotalCollection, BigDecimal fuelSaleTotalLiter, BigDecimal fuelSaleTotalMoney, BigDecimal fuelCollectionTotalMoney,
            BigDecimal marketSalesTotalPrice, BigDecimal marketCollectionTotalPrices, BigDecimal generalSalesTotal, BigDecimal generalCollectionTotal, List<BranchSetting> selectedBranchList, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketCollection,
            Map<Integer, StationSalesSummaryReport> currencyTotalsSales, Map<Integer, StationSalesSummaryReport> currencyTotalsCollection) {

        
            ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

            CellStyle stylesub = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("stationsalessummaryreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty0 = excelDocument.getSheet().createRow(1);

            SXSSFRow startdate = excelDocument.getSheet().createRow(2);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate));

            SXSSFRow enddate = excelDocument.getSheet().createRow(3);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate));

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
            SXSSFRow branch = excelDocument.getSheet().createRow(4);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            SXSSFRow empty4 = excelDocument.getSheet().createRow(5);

            SXSSFRow reportName = excelDocument.getSheet().createRow(6);
            SXSSFCell ceelheader = reportName.createCell((short) 0);
            ceelheader.setCellValue(sessionBean.getLoc().getString("fuelsales"));
            ceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFRow rowm = excelDocument.getSheet().createRow(7);
            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerDarkGray", excelDocument.getWorkbook());

            SXSSFCell celld0 = rowm.createCell((short) 0);
            celld0.setCellValue(sessionBean.getLoc().getString("branch"));
            celld0.setCellStyle(styleheader);

            SXSSFCell celld1 = rowm.createCell((short) 1);
            celld1.setCellValue(sessionBean.getLoc().getString("stockname"));
            celld1.setCellStyle(styleheader);

            SXSSFCell celld2 = rowm.createCell((short) 2);
            celld2.setCellValue(sessionBean.getLoc().getString("salesamount"));
            celld2.setCellStyle(styleheader);

            SXSSFCell celld3 = rowm.createCell((short) 3);
            celld3.setCellValue(sessionBean.getLoc().getString("unitprice"));
            celld3.setCellStyle(styleheader);

            SXSSFCell celld4 = rowm.createCell((short) 4);
            celld4.setCellValue(sessionBean.getLoc().getString("total"));
            celld4.setCellStyle(styleheader);

            /////////////////////////////////////////Akaryakıt Tahsilatları////////////////////
            SXSSFCell ceelheaderMarketCollection0 = reportName.createCell((short) 6);
            ceelheaderMarketCollection0.setCellValue(sessionBean.getLoc().getString("fuelcollections"));
            ceelheaderMarketCollection0.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFCell cellcollection1 = rowm.createCell((short) 6);
            cellcollection1.setCellValue(sessionBean.getLoc().getString("branch"));
            cellcollection1.setCellStyle(styleheader);

            SXSSFCell cellcollection2 = rowm.createCell((short) 7);
            cellcollection2.setCellValue(sessionBean.getLoc().getString("collectiontype"));
            cellcollection2.setCellStyle(styleheader);

            SXSSFCell cellcollection3 = rowm.createCell((short) 8);
            cellcollection3.setCellValue(sessionBean.getLoc().getString("total"));
            cellcollection3.setCellStyle(styleheader);

            int j = 8;
            SXSSFRow row = null;
            int rowSize = 0;
            if (listOfFuelSales.size() >= listOfFuelCollection.size()) {
                rowSize = listOfFuelSales.size();
            } else {
                rowSize = listOfFuelCollection.size();
            }
            for (int i = 0; i < rowSize; i++) {
                int b = 0;
                row = excelDocument.getSheet().createRow(j);
                if (listOfFuelSales.size() > i) {

                    row.createCell((short) b++).setCellValue((listOfFuelSales.get(i).getBranchSetting().getBranch().getName() == null ? "" : listOfFuelSales.get(i).getBranchSetting().getBranch().getName()));

                    row.createCell((short) b++).setCellValue((listOfFuelSales.get(i).getFuelStockName() == null ? "" : listOfFuelSales.get(i).getFuelStockName()));

                    SXSSFCell stockQuantity = row.createCell((short) b++);
                    stockQuantity.setCellValue(StaticMethods.round(listOfFuelSales.get(i).getFuelStockQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    SXSSFCell stockUnitPrice = row.createCell((short) b++);
                    stockUnitPrice.setCellValue(StaticMethods.round(listOfFuelSales.get(i).getFuelStockUnitPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    SXSSFCell stockTotal = row.createCell((short) b++);
                    stockTotal.setCellValue(StaticMethods.round(listOfFuelSales.get(i).getFuelStockSalesTotal().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                b = 6;
                if (listOfFuelCollection.size() > i) {

                    row.createCell((short) b++).setCellValue((listOfFuelCollection.get(i).getFuelCollectionName() == null ? "" : listOfFuelCollection.get(i).getBranchSetting().getBranch().getName()));

                    row.createCell((short) b++).setCellValue((listOfFuelCollection.get(i).getFuelCollectionName() == null ? "" : listOfFuelCollection.get(i).getFuelCollectionName()));

                    SXSSFCell collectionSalesTotal = row.createCell((short) b++);
                    collectionSalesTotal.setCellValue(StaticMethods.round(listOfFuelCollection.get(i).getFuelCollectionSalesTotal().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                j++;

            }

            Set setFuelSale = currencyTotalsFuelSales.entrySet();

            List<Integer> hashKeyFuelSale = new ArrayList<>();

            Iterator iFuelSale = setFuelSale.iterator();

            while (iFuelSale.hasNext()) {

                Map.Entry me = (Map.Entry) iFuelSale.next();

                hashKeyFuelSale.add((Integer) me.getKey());

            }

            Set setFuelCol = currencyTotalsFuelCollection.entrySet();

            List<Integer> hashKeyFuelcol = new ArrayList<>();

            Iterator iFuelCol = setFuelCol.iterator();

            while (iFuelCol.hasNext()) {

                Map.Entry me = (Map.Entry) iFuelCol.next();

                hashKeyFuelcol.add((Integer) me.getKey());

            }

            SXSSFRow rowTotalFuel = null;
            int rowSizeFuel = 0;
            if (currencyTotalsFuelSales.size() >= currencyTotalsFuelCollection.size()) {
                rowSizeFuel = currencyTotalsFuelSales.size();

            } else {
                rowSizeFuel = currencyTotalsFuelCollection.size();
            }

            for (int k = 0; k < rowSizeFuel; k++) {

                rowTotalFuel = excelDocument.getSheet().createRow(j);

                if (currencyTotalsFuelSales.size() > k) {

                    SXSSFCell cellsub0 = rowTotalFuel.createCell((short) 0);
                    cellsub0.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub0.setCellStyle(stylesub);

                    SXSSFCell cellsub1 = rowTotalFuel.createCell((short) 4);
                    cellsub1.setCellValue(StaticMethods.round((currencyTotalsFuelSales.get(hashKeyFuelSale.get(k)).getFuelStockSalesTotal()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsFuelSales.get(hashKeyFuelSale.get(k)).getBranchSetting().getBranch().getCurrency().getId(), 0));
                    cellsub1.setCellStyle(stylesub);

                }

                if (currencyTotalsFuelCollection.size() > k) {

                    SXSSFCell cellsub2 = rowTotalFuel.createCell((short) 6);
                    cellsub2.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub2.setCellStyle(stylesub);

                    SXSSFCell cellsub3 = rowTotalFuel.createCell((short) 8);
                    cellsub3.setCellValue(StaticMethods.round((currencyTotalsFuelCollection.get(hashKeyFuelcol.get(k)).getFuelCollectionSalesTotal()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsFuelCollection.get(hashKeyFuelcol.get(k)).getBranchSetting().getBranch().getCurrency().getId(), 0));
                    cellsub3.setCellStyle(stylesub);

                }
                j++;
            }

            j = j + 3;

            /////////////////////////////////////////Market Satışları////////////////////
            SXSSFRow reportNameMarket = excelDocument.getSheet().createRow(j - 1);
            SXSSFCell ceelheaderMArket = reportNameMarket.createCell((short) 0);
            ceelheaderMArket.setCellValue(sessionBean.getLoc().getString("marketsales"));
            ceelheaderMArket.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFRow rowMarketSal = excelDocument.getSheet().createRow(j++);

            SXSSFCell cellmarket0 = rowMarketSal.createCell((short) 0);
            cellmarket0.setCellValue(sessionBean.getLoc().getString("branch"));
            cellmarket0.setCellStyle(styleheader);

            SXSSFCell cellmarket1 = rowMarketSal.createCell((short) 1);
            cellmarket1.setCellValue(sessionBean.getLoc().getString("sales"));
            cellmarket1.setCellStyle(styleheader);

            SXSSFCell cellmarket2 = rowMarketSal.createCell((short) 2);
            cellmarket2.setCellValue(sessionBean.getLoc().getString("quantity"));
            cellmarket2.setCellStyle(styleheader);

            SXSSFCell cellmarket3 = rowMarketSal.createCell((short) 3);
            cellmarket3.setCellValue(sessionBean.getLoc().getString("total"));
            cellmarket3.setCellStyle(styleheader);

            SXSSFRow rowMarket = null;
            int rowSizeMarket = 0;
            if (listOfMarketSales.size() >= listOfMarketCollection.size()) {
                rowSizeMarket = listOfMarketSales.size();
            } else {
                rowSizeMarket = listOfMarketCollection.size();
            }

            int tj = j;

            for (int i = 0; i < rowSizeMarket; i++) {
                int b = 0;
                rowMarket = excelDocument.getSheet().createRow(j);


                if (listOfMarketSales.size() > i) {

                    rowMarket.createCell((short) b++).setCellValue((listOfMarketSales.get(i).getBranchSetting().getBranch().getName() == null ? "" : listOfMarketSales.get(i).getBranchSetting().getBranch().getName()));
                    rowMarket.createCell((short) b++).setCellValue((listOfMarketSales.get(i).getSalesTypeName() == null ? "" : listOfMarketSales.get(i).getSalesTypeName()));

//                    rowMarket.createCell((short) b++).setCellValue((listOfMarketSales.get(i).getMarketSalesQuantity()));

                    SXSSFCell marketSalesQuantity = rowMarket.createCell((short) b++);
                    marketSalesQuantity.setCellValue(StaticMethods.round(listOfMarketSales.get(i).getMarketSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    SXSSFCell marketSalesTotal = rowMarket.createCell((short) b++);
                    marketSalesTotal.setCellValue(StaticMethods.round(listOfMarketSales.get(i).getMarketSaleTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                b = 6;
                if (listOfMarketCollection.size() > i) {

                    rowMarket.createCell((short) b++).setCellValue((listOfMarketCollection.get(i).getBranchSetting().getBranch().getName() == null ? "" : listOfMarketCollection.get(i).getBranchSetting().getBranch().getName()));

                    rowMarket.createCell((short) b++).setCellValue((listOfMarketCollection.get(i).getMarketCollectionTypeName() == null ? "" : listOfMarketCollection.get(i).getMarketCollectionTypeName()));

                    SXSSFCell collectionSalesTotal = rowMarket.createCell((short) b++);
                    collectionSalesTotal.setCellValue(StaticMethods.round(listOfMarketCollection.get(i).getMarketCollectionTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                j++;

            }

            Set setMarketSale = currencyTotalsMarketSales.entrySet();

            List<Integer> hashKeyMarketSale = new ArrayList<>();

            Iterator iMarketSale = setMarketSale.iterator();

            while (iMarketSale.hasNext()) {

                Map.Entry me = (Map.Entry) iMarketSale.next();

                hashKeyMarketSale.add((Integer) me.getKey());

            }

            Set setMarketCol = currencyTotalsMarketCollection.entrySet();

            List<Integer> hashKeyMarketCol = new ArrayList<>();

            Iterator iMarketCol = setMarketCol.iterator();

            while (iMarketCol.hasNext()) {

                Map.Entry me = (Map.Entry) iMarketCol.next();

                hashKeyMarketCol.add((Integer) me.getKey());

            }

            SXSSFRow rowMarketTotal = null;
            int rowSizeMarketTotal = 0;
            if (currencyTotalsMarketSales.size() >= currencyTotalsMarketCollection.size()) {
                rowSizeMarketTotal = currencyTotalsMarketSales.size();

            } else {
                rowSizeMarketTotal = currencyTotalsMarketCollection.size();
            }

            for (int k = 0; k < rowSizeMarketTotal; k++) {

                int b = 0;
                rowMarketTotal = excelDocument.getSheet().createRow(j);

                if (currencyTotalsMarketSales.size() > k) {

                    SXSSFCell cellsub0 = rowMarketTotal.createCell((short) 0);
                    cellsub0.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub0.setCellStyle(stylesub);

                    SXSSFCell cellsub1 = rowMarketTotal.createCell((short) 3);
                    cellsub1.setCellValue(StaticMethods.round((currencyTotalsMarketSales.get(hashKeyMarketSale.get(k)).getMarketSaleTotalMoney()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsMarketSales.get(hashKeyMarketSale.get(k)).getCurrency().getId(), 0));
                    cellsub1.setCellStyle(stylesub);

                }

                b = 6;

                if (currencyTotalsMarketCollection.size() > k) {

                    SXSSFCell cellsub2 = rowMarketTotal.createCell((short) 6);
                    cellsub2.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub2.setCellStyle(stylesub);

                    SXSSFCell cellsub3 = rowMarketTotal.createCell((short) 8);
                    cellsub3.setCellValue(StaticMethods.round((currencyTotalsMarketCollection.get(hashKeyMarketCol.get(k)).getMarketCollectionTotalMoney()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsMarketCollection.get(hashKeyMarketCol.get(k)).getBranchSetting().getBranch().getCurrency().getId(), 0));
                    cellsub3.setCellStyle(stylesub);

                }
                j++;
            }

//            /////////////////////////////////////////Market Tahsilatları////////////////////
            SXSSFCell ceelheaderMarketCollectio = reportNameMarket.createCell((short) 6);
            ceelheaderMarketCollectio.setCellValue(sessionBean.getLoc().getString("marketcollections"));
            ceelheaderMarketCollectio.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFCell cellmarketCollection = rowMarketSal.createCell((short) 6);
            cellmarketCollection.setCellValue(sessionBean.getLoc().getString("branch"));
            cellmarketCollection.setCellStyle(styleheader);

            SXSSFCell cellmarketCollection1 = rowMarketSal.createCell((short) 7);
            cellmarketCollection1.setCellValue(sessionBean.getLoc().getString("collectiontype"));
            cellmarketCollection1.setCellStyle(styleheader);

            SXSSFCell cellmarketCollection2 = rowMarketSal.createCell((short) 8);
            cellmarketCollection2.setCellValue(sessionBean.getLoc().getString("total"));
            cellmarketCollection2.setCellStyle(styleheader);

            j = j + 3;

            ///////////////////////////////////Toplam Satışları////////////////////
            SXSSFRow reportNameTotal = excelDocument.getSheet().createRow(j - 1);
            SXSSFCell ceelheaderMarket = reportNameTotal.createCell((short) 0);
            ceelheaderMarket.setCellValue(sessionBean.getLoc().getString("totalsales"));
            ceelheaderMarket.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFRow rowTotal = excelDocument.getSheet().createRow(j++);

            SXSSFCell celltotal1 = rowTotal.createCell((short) 0);
            celltotal1.setCellValue(sessionBean.getLoc().getString("sales"));
            celltotal1.setCellStyle(styleheader);

            SXSSFCell celltotal2 = rowTotal.createCell((short) 1);
            celltotal2.setCellValue(sessionBean.getLoc().getString("total"));
            celltotal2.setCellStyle(styleheader);

            ///////////////////////////////////////Toplam Tahsilatlar////////////////////
            SXSSFCell ceelheaderTotal = reportNameTotal.createCell((short) 6);
            ceelheaderTotal.setCellValue(sessionBean.getLoc().getString("totalcollections"));
            ceelheaderTotal.setCellStyle(headerCss(excelDocument.getWorkbook()));

            SXSSFCell celltotalG0 = rowTotal.createCell((short) 6);
            celltotalG0.setCellValue(sessionBean.getLoc().getString("branch"));
            celltotalG0.setCellStyle(styleheader);

            SXSSFCell celltotalG1 = rowTotal.createCell((short) 7);
            celltotalG1.setCellValue(sessionBean.getLoc().getString("collectiontype"));
            celltotalG1.setCellStyle(styleheader);

            SXSSFCell celltotalG2 = rowTotal.createCell((short) 8);
            celltotalG2.setCellValue(sessionBean.getLoc().getString("total"));
            celltotalG2.setCellStyle(styleheader);

            SXSSFRow rowTotalG = null;
            int rowSizeTotal = 0;
            if (listOfTotalSales.size() >= listOfTotalCollection.size()) {
                rowSizeTotal = listOfTotalSales.size();
            } else {
                rowSizeTotal = listOfTotalCollection.size();
            }
            for (int i = 0; i < rowSizeTotal; i++) {
                int b = 0;
                rowTotalG = excelDocument.getSheet().createRow(j);
                if (listOfTotalSales.size() > i) {

                    rowTotalG.createCell((short) b++).setCellValue((listOfTotalSales.get(i).getTotalSalesName() == null ? "" : listOfTotalSales.get(i).getTotalSalesName()));

                    SXSSFCell marketSalesTotal = rowTotalG.createCell((short) b++);
                    marketSalesTotal.setCellValue(StaticMethods.round(listOfTotalSales.get(i).getTotalSalesPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }
                b = 6;
                if (listOfTotalCollection.size() > i) {

                    rowTotalG.createCell((short) b++).setCellValue((listOfTotalCollection.get(i).getBranchName() == null ? "" : listOfTotalCollection.get(i).getBranchName()));
                    rowTotalG.createCell((short) b++).setCellValue((listOfTotalCollection.get(i).getTotalCollectionName() == null ? "" : listOfTotalCollection.get(i).getTotalCollectionName()));

                    SXSSFCell collectionSalesTotal = rowTotalG.createCell((short) b++);
                    collectionSalesTotal.setCellValue(StaticMethods.round(listOfTotalCollection.get(i).getTotalCollectionPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                }

                j++;

            }

            Set setTotalSale = currencyTotalsSales.entrySet();

            List<Integer> hashKeyTotalSale = new ArrayList<>();

            Iterator iTotalSale = setTotalSale.iterator();

            while (iTotalSale.hasNext()) {

                Map.Entry me = (Map.Entry) iTotalSale.next();

                hashKeyTotalSale.add((Integer) me.getKey());

            }

            Set setTotalCol = currencyTotalsCollection.entrySet();

            List<Integer> hashKeyTotalCol = new ArrayList<>();

            Iterator iTotalCol = setTotalCol.iterator();

            while (iTotalCol.hasNext()) {

                Map.Entry me = (Map.Entry) iTotalCol.next();

                hashKeyTotalCol.add((Integer) me.getKey());

            }

            SXSSFRow rowGeneralTotal = null;
            int rowSizeGeneralTotal = 0;
            if (currencyTotalsSales.size() >= currencyTotalsCollection.size()) {
                rowSizeGeneralTotal = currencyTotalsSales.size();

            } else {
                rowSizeGeneralTotal = currencyTotalsCollection.size();
            }

            for (int k = 0; k < rowSizeGeneralTotal; k++) {

                int b = 0;
                rowGeneralTotal = excelDocument.getSheet().createRow(j);

                if (currencyTotalsSales.size() > k) {

                    SXSSFCell cellsub0 = rowGeneralTotal.createCell((short) 0);
                    cellsub0.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub0.setCellStyle(stylesub);

                    SXSSFCell cellsub1 = rowGeneralTotal.createCell((short) 1);
                    cellsub1.setCellValue(StaticMethods.round((currencyTotalsSales.get(hashKeyTotalSale.get(k)).getTotalSalesPrice()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsSales.get(hashKeyTotalSale.get(k)).getCurrency().getId(), 0));
                    cellsub1.setCellStyle(stylesub);

                }

                b = 6;

                if (currencyTotalsCollection.size() > k) {

                    SXSSFCell cellsub2 = rowGeneralTotal.createCell((short) 6);
                    cellsub2.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                    cellsub2.setCellStyle(stylesub);

                    SXSSFCell cellsub3 = rowGeneralTotal.createCell((short) 8);
                    cellsub3.setCellValue(StaticMethods.round((currencyTotalsCollection.get(hashKeyTotalCol.get(k)).getTotalCollectionPrice()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) + sessionBean.currencySignOrCode(currencyTotalsCollection.get(hashKeyTotalCol.get(k)).getCurrency().getId(), 0));
                    cellsub3.setCellStyle(stylesub);

                }
                j++;
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("stationsalessummaryreport"));
            } catch (IOException ex) {
                Logger.getLogger(MarketShiftService.class.getName()).log(Level.SEVERE, null, ex);

            }
        
    }

    @Override
    public void createPdf(Date beginDate, Date endDate,
            List<StationSalesSummaryReport> listOfFuelSales, List<StationSalesSummaryReport> listOfFuelCollection,
            List<StationSalesSummaryReport> listOfMarketSales, List<StationSalesSummaryReport> listOfMarketCollection,
            List<StationSalesSummaryReport> listOfTotalSales, List<StationSalesSummaryReport> listOfTotalCollection, BigDecimal fuelSaleTotalLiter, BigDecimal fuelSaleTotalMoney, BigDecimal fuelCollectionTotalMoney,
            BigDecimal marketSalesTotalPrice, BigDecimal marketCollectionTotalPrices, BigDecimal generalSalesTotal, BigDecimal generalCollectionTotal, List<BranchSetting> selectedBranchList, Map<Integer, StationSalesSummaryReport> currencyTotalsFuelSales,
            Map<Integer, StationSalesSummaryReport> currencyTotalsFuelCollection, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketSales, Map<Integer, StationSalesSummaryReport> currencyTotalsMarketCollection, Map<Integer, StationSalesSummaryReport> currencyTotalsSales,
            Map<Integer, StationSalesSummaryReport> currencyTotalsCollection) {

            PdfDocument pdfDocument = StaticMethods.preparePdf(Arrays.asList(true, true, true, true), 0);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stationsalessummaryreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate), pdfDocument.getFont()));
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

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

        try {
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
        } catch (DocumentException ex) {
            Logger.getLogger(StationSalesSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

            PdfDocument pdfDocumentFuelSales = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);
            PdfDocument pdfDocumentFuelConnections = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);

            PdfPTable fuelMainTable = new PdfPTable(2);
            fuelMainTable.setWidthPercentage(100.0f);

            PdfPCell firstTableCell = new PdfPCell();
            firstTableCell.setBorder(PdfPCell.NO_BORDER);

            pdfDocumentFuelSales.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fuelsales"), pdfDocument.getFontHeader()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getHeader());

            pdfDocumentFuelSales.getPdfTable().setSpacingAfter(15f);

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelSales, pdfDocumentFuelSales.getTableHeader());
            pdfDocumentFuelSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocumentFuelSales.getFontColumnTitle()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelSales, pdfDocumentFuelSales.getTableHeader());
            pdfDocumentFuelSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockname"), pdfDocumentFuelSales.getFontColumnTitle()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelSales, pdfDocumentFuelSales.getTableHeader());
            pdfDocumentFuelSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocumentFuelSales.getFontColumnTitle()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelSales, pdfDocumentFuelSales.getTableHeader());
            pdfDocumentFuelSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("unitprice"), pdfDocumentFuelSales.getFontColumnTitle()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelSales, pdfDocumentFuelSales.getTableHeader());
            pdfDocumentFuelSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentFuelSales.getFontColumnTitle()));
            pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getTableHeader());

            /*AKaryakıt Satışları */
            for (int i = 0; i < listOfFuelSales.size(); i++) {

                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(listOfFuelSales.get(i).getBranchSetting().getBranch().getName(), pdfDocumentFuelSales.getFont()));
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(listOfFuelSales.get(i).getFuelStockName(), pdfDocumentFuelSales.getFont()));
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfFuelSales.get(i).getFuelStockQuantity()), pdfDocumentFuelSales.getFont()));
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfFuelSales.get(i).getFuelStockUnitPrice())
                        + sessionBean.currencySignOrCode(listOfFuelSales.get(i).getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentFuelSales.getFont()));
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfFuelSales.get(i).getFuelStockSalesTotal())
                        + sessionBean.currencySignOrCode(listOfFuelSales.get(i).getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentFuelSales.getFont()));
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsFuelSales.entrySet()) {
                pdfDocumentFuelSales.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getFuelStockSalesTotal()) + sessionBean.currencySignOrCode(entry.getValue().getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentFuelSales.getFontHeader()));
                pdfDocumentFuelSales.getRightCell().setColspan(5);
                pdfDocumentFuelSales.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentFuelSales.getPdfTable().addCell(pdfDocumentFuelSales.getRightCell());

            }

            firstTableCell.addElement(pdfDocumentFuelSales.getPdfTable());
            fuelMainTable.addCell(firstTableCell);

            firstTableCell.addElement(pdfDocumentFuelSales.getPdfTable());

            pdfDocumentFuelConnections.getPdfTable().setSpacingAfter(15f);

            pdfDocumentFuelConnections.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("fuelcollections"), pdfDocument.getFontHeader()));
            pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getHeader());

            /* Akaryakıt Tahsilatları*/
            PdfPCell secondTableCell = new PdfPCell();
            secondTableCell.setBorder(PdfPCell.NO_BORDER);

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelConnections, pdfDocumentFuelConnections.getTableHeader());
            pdfDocumentFuelConnections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocumentFuelConnections.getFontColumnTitle()));
            pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelConnections, pdfDocumentFuelConnections.getTableHeader());
            pdfDocumentFuelConnections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("collectiontype"), pdfDocumentFuelConnections.getFontColumnTitle()));
            pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentFuelConnections, pdfDocumentFuelConnections.getTableHeader());
            pdfDocumentFuelConnections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentFuelConnections.getFontColumnTitle()));
            pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getTableHeader());

            for (int i = 0; i < listOfFuelCollection.size(); i++) {

                pdfDocumentFuelConnections.getDataCell().setPhrase(new Phrase(listOfFuelCollection.get(i).getBranchSetting().getBranch().getName(), pdfDocumentFuelConnections.getFont()));
                pdfDocumentFuelConnections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getDataCell());

                pdfDocumentFuelConnections.getDataCell().setPhrase(new Phrase(listOfFuelCollection.get(i).getFuelCollectionName(), pdfDocumentFuelConnections.getFont()));
                pdfDocumentFuelConnections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getDataCell());

                pdfDocumentFuelConnections.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfFuelCollection.get(i).getFuelCollectionSalesTotal())
                        + sessionBean.currencySignOrCode(listOfFuelCollection.get(i).getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentFuelConnections.getFont()));
                pdfDocumentFuelConnections.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsFuelCollection.entrySet()) {
                pdfDocumentFuelConnections.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getFuelCollectionSalesTotal()) + sessionBean.currencySignOrCode(entry.getValue().getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentFuelConnections.getFontHeader()));
                pdfDocumentFuelConnections.getRightCell().setColspan(3);
                pdfDocumentFuelConnections.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentFuelConnections.getPdfTable().addCell(pdfDocumentFuelConnections.getRightCell());

            }

            pdfDocumentFuelConnections.getPdfTable().setSpacingAfter(15f);
            pdfDocumentFuelSales.getPdfTable().setSpacingAfter(15f);

            secondTableCell.addElement(pdfDocumentFuelConnections.getPdfTable());
            fuelMainTable.addCell(secondTableCell);

            PdfDocument pdfDocumentMarketSales = StaticMethods.preparePdf(Arrays.asList(true, true, true, true), 0);
            PdfDocument pdfDocumentMarketCollections = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);
            ///////////////////////////////////Market Satışları//////////////////////
            PdfPTable marketMainTable = new PdfPTable(2);
            marketMainTable.setWidthPercentage(100.0f);

            PdfPCell firstMarketTableCell = new PdfPCell();
            firstMarketTableCell.setBorder(PdfPCell.NO_BORDER);

            pdfDocumentMarketSales.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("marketsales"), pdfDocument.getFontHeader()));
            pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketSales, pdfDocumentMarketSales.getTableHeader());
            pdfDocumentMarketSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocumentMarketSales.getFontColumnTitle()));
            pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketSales, pdfDocumentMarketSales.getTableHeader());
            pdfDocumentMarketSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("sales"), pdfDocumentMarketSales.getFontColumnTitle()));
            pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketSales, pdfDocumentMarketSales.getTableHeader());
            pdfDocumentMarketSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("quantity"), pdfDocumentMarketSales.getFontColumnTitle()));
            pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketSales, pdfDocumentMarketSales.getTableHeader());
            pdfDocumentMarketSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentMarketSales.getFontColumnTitle()));
            pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getTableHeader());

            for (int i = 0; i < listOfMarketSales.size(); i++) {

                pdfDocumentMarketSales.getDataCell().setPhrase(new Phrase(listOfMarketSales.get(i).getBranchSetting().getBranch().getName(), pdfDocumentMarketSales.getFont()));
                pdfDocumentMarketSales.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getDataCell());

                pdfDocumentMarketSales.getDataCell().setPhrase(new Phrase(listOfMarketSales.get(i).getSalesTypeName(), pdfDocumentMarketSales.getFont()));
                pdfDocumentMarketSales.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getDataCell());

                pdfDocumentMarketSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfMarketSales.get(i).getMarketSalesQuantity()), pdfDocumentMarketSales.getFont()));
                pdfDocumentMarketSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getRightCell());

                pdfDocumentMarketSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfMarketSales.get(i).getMarketSaleTotalMoney())
                        + sessionBean.currencySignOrCode(listOfMarketSales.get(i).getCurrency().getId(), 0), pdfDocumentMarketSales.getFont()));
                pdfDocumentMarketSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsMarketSales.entrySet()) {
                pdfDocumentMarketSales.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getMarketSaleTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocumentMarketSales.getFontHeader()));
                pdfDocumentMarketSales.getRightCell().setColspan(4);
                pdfDocumentMarketSales.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentMarketSales.getPdfTable().addCell(pdfDocumentMarketSales.getRightCell());

            }

            firstMarketTableCell.addElement(pdfDocumentMarketSales.getPdfTable());
            marketMainTable.addCell(firstMarketTableCell);

            ////////////////////////////Market Tahsilatları///////////////
            PdfPCell secondMarketTableCell = new PdfPCell();
            secondMarketTableCell.setBorder(PdfPCell.NO_BORDER);

            pdfDocumentMarketCollections.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("marketcollections"), pdfDocument.getFontHeader()));
            pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getHeader());

            pdfDocumentMarketCollections.getPdfTable().setSpacingAfter(15f);

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketCollections, pdfDocumentMarketCollections.getTableHeader());
            pdfDocumentMarketCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocumentMarketCollections.getFontColumnTitle()));
            pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketCollections, pdfDocumentMarketCollections.getTableHeader());
            pdfDocumentMarketCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("collectiontype"), pdfDocumentMarketCollections.getFontColumnTitle()));
            pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentMarketCollections, pdfDocumentMarketCollections.getTableHeader());
            pdfDocumentMarketCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentMarketCollections.getFontColumnTitle()));
            pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getTableHeader());

            for (int i = 0; i < listOfMarketCollection.size(); i++) {

                pdfDocumentMarketCollections.getDataCell().setPhrase(new Phrase(listOfMarketCollection.get(i).getBranchSetting().getBranch().getName(), pdfDocumentMarketCollections.getFont()));
                pdfDocumentMarketCollections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getDataCell());

                pdfDocumentMarketCollections.getDataCell().setPhrase(new Phrase(listOfMarketCollection.get(i).getMarketCollectionTypeName(), pdfDocumentMarketCollections.getFont()));
                pdfDocumentMarketCollections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getDataCell());

                pdfDocumentMarketCollections.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfMarketCollection.get(i).getMarketCollectionTotalMoney())
                        + sessionBean.currencySignOrCode(listOfMarketCollection.get(i).getBranchSetting().getBranch().getCurrency().getId(), 0), pdfDocumentMarketCollections.getFont()));
                pdfDocumentMarketCollections.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsMarketCollection.entrySet()) {
                pdfDocumentMarketCollections.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getMarketCollectionTotalMoney()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocumentMarketCollections.getFontHeader()));
                pdfDocumentMarketCollections.getRightCell().setColspan(3);
                pdfDocumentMarketCollections.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentMarketCollections.getPdfTable().addCell(pdfDocumentMarketCollections.getRightCell());

            }

            pdfDocumentMarketSales.getPdfTable().setSpacingAfter(15f);
            pdfDocumentMarketCollections.getPdfTable().setSpacingAfter(15f);

            secondMarketTableCell.addElement(pdfDocumentMarketCollections.getPdfTable());
            marketMainTable.addCell(secondMarketTableCell);

            PdfDocument pdfDocumentTotalSales = StaticMethods.preparePdf(Arrays.asList(true, true), 0);
            PdfDocument pdfDocumentTotalCollections = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);

            //////////////////////////////////////////////////////Toplamlar////////////////////////////
            PdfPTable totalMainTable = new PdfPTable(2);
            totalMainTable.setWidthPercentage(100.0f);

            PdfPCell firstTotalTableCell = new PdfPCell();
            firstTotalTableCell.setBorder(PdfPCell.NO_BORDER);

            pdfDocumentTotalSales.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalsales"), pdfDocument.getFontHeader()));
            pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentTotalSales, pdfDocumentTotalSales.getTableHeader());
            pdfDocumentTotalSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("sales"), pdfDocumentTotalSales.getFontColumnTitle()));
            pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getTableHeader());

            pdfDocumentTotalSales.getPdfTable().setSpacingAfter(15f);

            createCellStylePdf("headerWhiteBold", pdfDocumentTotalSales, pdfDocumentTotalSales.getTableHeader());
            pdfDocumentTotalSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentTotalSales.getFontColumnTitle()));
            pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getTableHeader());

            for (int i = 0; i < listOfTotalSales.size(); i++) {
                pdfDocumentTotalSales.getDataCell().setPhrase(new Phrase(listOfTotalSales.get(i).getTotalSalesName(), pdfDocumentTotalSales.getFont()));
                pdfDocumentTotalSales.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getDataCell());

                pdfDocumentTotalSales.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotalSales.get(i).getTotalSalesPrice())
                        + sessionBean.currencySignOrCode(listOfTotalSales.get(i).getCurrency().getId(), 0), pdfDocumentTotalSales.getFont()));
                pdfDocumentTotalSales.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsSales.entrySet()) {
                pdfDocumentTotalSales.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalSalesPrice()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocumentTotalSales.getFontHeader()));
                pdfDocumentTotalSales.getRightCell().setColspan(2);
                pdfDocumentTotalSales.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentTotalSales.getPdfTable().addCell(pdfDocumentTotalSales.getRightCell());

            }

            pdfDocumentTotalSales.getPdfTable().setSpacingAfter(15f);

            firstTotalTableCell.addElement(pdfDocumentTotalSales.getPdfTable());
            totalMainTable.addCell(firstTotalTableCell);

            PdfPCell secondTotalTableCell = new PdfPCell();
            secondTotalTableCell.setBorder(PdfPCell.NO_BORDER);

            pdfDocumentTotalCollections.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalcollections"), pdfDocument.getFontHeader()));
            pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getHeader());

            pdfDocumentTotalCollections.getPdfTable().setSpacingAfter(15f);

            createCellStylePdf("headerWhiteBold", pdfDocumentTotalCollections, pdfDocumentTotalCollections.getTableHeader());
            pdfDocumentTotalCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branch"), pdfDocumentTotalCollections.getFontColumnTitle()));
            pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentTotalCollections, pdfDocumentTotalCollections.getTableHeader());
            pdfDocumentTotalCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("collectiontype"), pdfDocumentTotalCollections.getFontColumnTitle()));
            pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getTableHeader());

            createCellStylePdf("headerWhiteBold", pdfDocumentTotalCollections, pdfDocumentTotalCollections.getTableHeader());
            pdfDocumentTotalCollections.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentTotalCollections.getFontColumnTitle()));
            pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getTableHeader());

            for (int i = 0; i < listOfTotalCollection.size(); i++) {

                pdfDocumentTotalCollections.getDataCell().setPhrase(new Phrase(listOfTotalCollection.get(i).getBranchName(), pdfDocumentTotalCollections.getFont()));
                pdfDocumentTotalCollections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getDataCell());

                pdfDocumentTotalCollections.getDataCell().setPhrase(new Phrase(listOfTotalCollection.get(i).getTotalCollectionName(), pdfDocumentTotalCollections.getFont()));
                pdfDocumentTotalCollections.getDataCell().setBackgroundColor(Color.WHITE);
                pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getDataCell());

                pdfDocumentTotalCollections.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(listOfTotalCollection.get(i).getTotalCollectionPrice())
                        + sessionBean.currencySignOrCode(listOfTotalCollection.get(i).getCurrency().getId(), 0), pdfDocumentTotalCollections.getFont()));
                pdfDocumentTotalCollections.getRightCell().setBackgroundColor(Color.WHITE);
                pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getRightCell());

            }

            for (Map.Entry<Integer, StationSalesSummaryReport> entry : currencyTotalsCollection.entrySet()) {
                pdfDocumentTotalCollections.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(entry.getValue().getTotalCollectionPrice()) + sessionBean.currencySignOrCode(entry.getValue().getCurrency().getId(), 0), pdfDocumentTotalCollections.getFontHeader()));
                pdfDocumentTotalCollections.getRightCell().setColspan(3);
                pdfDocumentTotalCollections.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocumentTotalCollections.getPdfTable().addCell(pdfDocumentTotalCollections.getRightCell());

            }

            pdfDocumentTotalCollections.getPdfTable().setSpacingAfter(15f);

            secondTotalTableCell.addElement(pdfDocumentTotalCollections.getPdfTable());
            totalMainTable.addCell(secondTotalTableCell);

        try {
            pdfDocument.getDocument().add(fuelMainTable);
        } catch (DocumentException ex) {
            Logger.getLogger(StationSalesSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pdfDocument.getDocument().add(marketMainTable);
        } catch (DocumentException ex) {
            Logger.getLogger(StationSalesSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pdfDocument.getDocument().add(totalMainTable);
        } catch (DocumentException ex) {
            Logger.getLogger(StationSalesSummaryReportService.class.getName()).log(Level.SEVERE, null, ex);
        }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("stationsalessummaryreport"));

        
    }

    @Override
    public List<StationSalesSummaryReport> findFuelSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findFuelSalesOutherMoney(beginDate, endDate, createWhere, selectedBranchList);
    }

    @Override
    public List<StationSalesSummaryReport> findMarketSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        return stationSalesSummaryReportDao.findMarketSalesOutherMoney(beginDate, endDate, createWhere, selectedBranchList);
    }

}
