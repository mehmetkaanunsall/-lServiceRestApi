/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.generalstationreport.dao.GeneralStationReportDao;
import com.mepsan.marwiz.general.report.generalstationreport.dao.IGeneralStationReportDao;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author m.duzoylum
 */
public class GeneralStationReportService implements IGeneralStationReportService {

    @Autowired
    IGeneralStationReportDao generalStationReportDao;

    @Autowired
    public SessionBean sessionBean;

    public IGeneralStationReportDao getGeneralStationReportDao() {
        return generalStationReportDao;
    }

    public void setGeneralStationReportDao(IGeneralStationReportDao generalStationReportDao) {
        this.generalStationReportDao = generalStationReportDao;
    }

    @Override
    public List<GeneralStation> findAll(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType) {
        return generalStationReportDao.findAll(beginDate, endDate, branchList, lastUnitPrice, typeOfTable, centralIntegrationIf, costType);
    }

    @Override
    public List<GeneralStation> findAllMarket(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int centralIntegrationIf, int costType) {
        return generalStationReportDao.findAllMarket(beginDate, endDate, branchList, lastUnitPrice, centralIntegrationIf, costType);
    }

    @Override
    public List<GeneralStation> findAllAutomat(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int costType) {
        return generalStationReportDao.findAllAutomat(beginDate, endDate, branchList, lastUnitPrice, costType);
    }

    @Override
    public List<GeneralStation> totals(String where, Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable, int centralIntegrationIf, int costType) {
        return generalStationReportDao.totals(where, beginDate, endDate, branchList, lastUnitPrice, typeOfTable, centralIntegrationIf, costType);
    }

    @Override
    public String createWhere(GeneralStation obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GeneralStation> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportPdf(Date beginDate, Date endDate, List<BranchSetting> selectedBranchList, int lastUnitPrice, List<Boolean> toogleList, List<GeneralStation> listFuel, List<GeneralStation> totalListFuel, Map<Integer, GeneralStation> currencyTotalsCollection,
            List<Boolean> toogleListMarket, List<GeneralStation> listMarket, List<Boolean> toogleListAutomat, List<GeneralStation> listAutomat, List<GeneralStation> totalListAutomat,
            BigDecimal totalPurchaseAmount, BigDecimal totalSalesAmount, BigDecimal totalProfitAmount, BigDecimal totalProfitRate, BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost, int reportType, int costType, List<GeneralStation> listOfTotalsCategory, HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated) {

        NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

        formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
        decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbolsUnit.setCurrencySymbol("");
        ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);
        BigDecimal bd = BigDecimal.ZERO;

        try {

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("generalstationreport"), pdfDocument.getFontHeader()));
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

            String unitPrice = "";
            if (lastUnitPrice == 1) {
                unitPrice = sessionBean.getLoc().getString("lastsaleunitprice");
            } else {
                unitPrice = sessionBean.getLoc().getString("lastpurchaseunitprice");
            }
            Currency branchCurrency = new Currency();
            branchCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("setunitpriceforoutstandingbalance") + " : " + unitPrice, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String purchaseCost = "";
            if (costType == 1) {
                purchaseCost = sessionBean.getLoc().getString("fifo");
            } else {
                purchaseCost = sessionBean.getLoc().getString("weightedaverage");
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cost") + " : " + purchaseCost, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String reportTypeName = "";

            switch (reportType) {
                case 1: //Hepsi
                    reportTypeName = sessionBean.getLoc().getString("all");
                    break;
                case 2://Akaryakıt ürünleri
                    reportTypeName = sessionBean.getLoc().getString("fuelproduct");
                    break;
                case 3://Market kategorileri
                    reportTypeName = sessionBean.getLoc().getString("marketproduct");

                    break;
                case 4://Otomat
                    reportTypeName = sessionBean.getLoc().getString("automat");

                    break;
                default:
                    break;
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("reporttype") + " : " + reportTypeName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(
                    sessionBean.getLoc().getString("purchaseamount") + ": " + sessionBean.getNumberFormat().format(totalPurchaseAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0) + "  |  "
                    + sessionBean.getLoc().getString("salegiro") + ": " + sessionBean.getNumberFormat().format(totalSalesAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0) + "  |  "
                    + sessionBean.getLoc().getString("purchasecost") + ": " + sessionBean.getNumberFormat().format(totalPurchaseCost) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0) + "  |  "
                    + sessionBean.getLoc().getString("profitpercentage") + ": " + sessionBean.getNumberFormat().format(totalProfitRate) + "  |  "
                    + sessionBean.getLoc().getString("profitmargin") + ": " + sessionBean.getNumberFormat().format(totalProfitMargin) + "  |  "
                    + sessionBean.getLoc().getString("profitprice") + ": " + sessionBean.getNumberFormat().format(totalProfitAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            PdfDocument pdfDocument1 = StaticMethods.preparePdf(toogleList, 0);

            if (reportType == 1 || reportType == 2) {
                pdfDocument1.getCell().setPhrase(new Phrase(sessionBean.loc.getString("fuelproduct"), pdfDocument1.getFontHeader()));
                pdfDocument1.getPdfTable().addCell(pdfDocument1.getCell());
                StaticMethods.createHeaderPdf("dtbGeneralStationFuel", toogleList, "headerBlack", pdfDocument1);

                ////// List Fuel
                for (GeneralStation fuel : listFuel) {

                    formatterUnit.setMaximumFractionDigits(fuel.getStock().getUnit().getUnitRounding());
                    formatterUnit.setMinimumFractionDigits(fuel.getStock().getUnit().getUnitRounding());
                    branchCurrency.setId(fuel.getSaleCurrencyId().getId());

                    if (toogleList.get(0)) {
                        pdfDocument1.getDataCell().setPhrase(new Phrase(fuel.getBranchSetting().getBranch().getName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());
                    }
                    if (toogleList.get(1)) {
                        pdfDocument1.getDataCell().setPhrase(new Phrase(fuel.getStock().getName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());
                    }

                    if (toogleList.get(2)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(fuel.getStock().getCode(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(3)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(fuel.getStock().getBarcode(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(4)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(fuel.getStock().getCenterProductCode(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(formatterUnit.format(fuel.getTransferQuantity()) + fuel.getStock().getUnit().getSortName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuel.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(formatterUnit.format(fuel.getPurchaseQuantity()) + fuel.getStock().getUnit().getSortName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuel.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(9)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(formatterUnit.format(fuel.getSalesQuantity()) + fuel.getStock().getUnit().getSortName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(10)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuel.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(11)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(formatterUnit.format(fuel.getTransferQuantity().add(fuel.getPurchaseQuantity()).subtract(fuel.getSalesQuantity())) + fuel.getStock().getUnit().getSortName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(12)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuel.getTransferAmount().add(fuel.getPurchaseAmount()).subtract(fuel.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }

                    if (toogleList.get(13)) {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(fuel.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(14)) {

                        if (fuel.getCost() != null && fuel.getCost().compareTo(BigDecimal.ZERO) != 0) {
                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                        } else {
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                        }
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(15)) {

                        if (fuel.getSalesAmount() != null && fuel.getSalesAmount().compareTo(BigDecimal.ZERO) != 0) {
                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                        } else {
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                        }
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }
                    if (toogleList.get(16)) {
                        bd = (fuel.getSalesAmount().subtract(fuel.getPurchaseAmount()));
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());
                    }

                }
                /////// Fuel Total
                for (GeneralStation total : totalListFuel) {

                    pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                    pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                    pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                    pdfDocument1.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                    if (selectedBranchList.size() > 1) {
                        pdfDocument1.getDataCell().setPhrase(new Phrase(total.getBranchSetting().getBranch().getName(), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());
                    } else {
                        pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());
                    }

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity()), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseQuantity()), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesQuantity()), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity().add(total.getPurchaseQuantity()).subtract(total.getSalesQuantity())), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount().add(total.getPurchaseAmount()).subtract(total.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    if (total.getCost() != null && total.getCost().compareTo(BigDecimal.ZERO) != 0) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                    } else {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                    }
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                    } else {
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                    }
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    bd = (total.getSalesAmount().subtract(total.getPurchaseAmount()));
                    pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                    pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                }
                /////// Fuel Collection

                if (selectedBranchList.size() > 1) {
                    for (Map.Entry<Integer, GeneralStation> entry : currencyTotalsCollection.entrySet()) {

                        pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                        pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                        pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                        pdfDocument1.getDataCell().setPhrase(new Phrase("", pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                        pdfDocument1.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getDataCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getTransferQuantity()), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getPurchaseQuantity()), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getSalesQuantity()), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getTransferQuantity().add(entry.getValue().getPurchaseQuantity()).subtract(entry.getValue().getSalesQuantity())), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getTransferAmount().add(entry.getValue().getPurchaseAmount()).subtract(entry.getValue().getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entry.getValue().getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        if (entry.getValue().getCost().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getCost() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                        } else {
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                        }
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        if (entry.getValue().getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getSalesAmount() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument1.getFont()));
                        } else {
                            pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument1.getFont()));
                        }
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                        bd = (entry.getValue().getSalesAmount().subtract(entry.getValue().getPurchaseAmount()));
                        pdfDocument1.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument1.getFont()));
                        pdfDocument1.getPdfTable().addCell(pdfDocument1.getRightCell());

                    }
                }
                pdfDocument.getDocument().add(pdfDocument1.getPdfTable());

            }
            if (reportType == 1 || reportType == 3) {
                /////// Market List
                PdfDocument pdfDocument2 = StaticMethods.preparePdf(toogleListMarket, 0);
                pdfDocument2.getCell().setPhrase(new Phrase(sessionBean.loc.getString("marketproduct"), pdfDocument2.getFontHeader()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getCell());
                //StaticMethods.createHeaderPdf("dtbGeneralStationMarket", toogleListMarket, "headerBlack", pdfDocument2);

                StaticMethods.createCellStylePdf("headerBlack", pdfDocument2, pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branchname"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("maincategory"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("categoryname"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringamount"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantity"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchaseamount"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salegiro"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamount"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamountt"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasecost"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentage"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitmargin"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument2.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitprice"), pdfDocument2.getFontColumnTitle()));
                pdfDocument2.getPdfTable().addCell(pdfDocument2.getTableHeader());

                pdfDocument.getDocument().add(pdfDocument2.getPdfTable());

                for (GeneralStation lm : listMarket) {

                    branchCurrency.setId(lm.getSaleCurrencyId().getId());

                    if (toogleList.get(0)) {
                        pdfDocument2.getDataCell().setPhrase(new Phrase(lm.getBranchSetting().getBranch().getName(), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());
                    }

                    pdfDocument2.getDataCell().setPhrase(new Phrase((lm.getCategorization().getParentId().getName() == null || lm.getCategorization().getParentId().getName().isEmpty()) ? "-" : lm.getCategorization().getParentId().getName(), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());

                    if (toogleList.get(1)) {
                        pdfDocument2.getDataCell().setPhrase(new Phrase(lm.getCategorization().getName(), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());
                    }

                    if (toogleList.get(2)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getTransferQuantity()), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(3)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(4)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getPurchaseQuantity()), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(5)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(6)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getSalesQuantity()), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(7)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(8)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getTransferQuantity().add(lm.getPurchaseQuantity()).subtract(lm.getSalesQuantity())), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(9)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getTransferAmount().add(lm.getPurchaseAmount()).subtract(lm.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(10)) {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(lm.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(11)) {
                        if (lm.getCost().compareTo(BigDecimal.ZERO) != 0 && lm.getCost() != null) {
                            bd = ((lm.getSalesAmount().subtract(lm.getCost())).divide(lm.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument2.getFont()));
                        } else {
                            pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument2.getFont()));
                        }
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(12)) {
                        if (lm.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && lm.getSalesAmount() != null) {
                            bd = ((lm.getSalesAmount().subtract(lm.getCost())).divide(lm.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument2.getFont()));
                        } else {
                            pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument2.getFont()));
                        }
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                    if (toogleList.get(13)) {
                        bd = (lm.getSalesAmount().subtract(lm.getPurchaseAmount()));
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());
                    }

                }

                /////// Market Total
                for (GeneralStation total : listOfTotalsCategory) {

                    pdfDocument2.getDataCell().setPhrase(new Phrase("", pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());

                    pdfDocument2.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());

                    if (selectedBranchList.size() > 1) {
                        pdfDocument2.getDataCell().setPhrase(new Phrase(total.getBranchSetting().getBranch().getName(), pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());
                    } else {
                        pdfDocument2.getDataCell().setPhrase(new Phrase("", pdfDocument2.getFont()));
                        pdfDocument2.getPdfTable().addCell(pdfDocument2.getDataCell());
                    }

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity()), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseQuantity()), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesQuantity()), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity().add(total.getPurchaseQuantity()).subtract(total.getSalesQuantity())), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount().add(total.getPurchaseAmount()).subtract(total.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument2.getFont()));
                    } else {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument2.getFont()));
                    }
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument2.getFont()));
                    } else {
                        pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument2.getFont()));
                    }
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                    bd = (total.getSalesAmount().subtract(total.getPurchaseAmount()));
                    pdfDocument2.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument2.getFont()));
                    pdfDocument2.getPdfTable().addCell(pdfDocument2.getRightCell());

                }

                pdfDocument.getDocument().add(pdfDocument2.getPdfTable());
            }

            if (reportType == 1 || reportType == 4) {

                for (Map.Entry<String, List<GeneralStation>> entry : groupAutomatType.entrySet()) {

                    /////// otomat List
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() == -1) {
                        toogleListAutomat = Arrays.asList(true, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true);
                    } else {
                        toogleListAutomat = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false);
                    }

                    PdfDocument pdfDocument3 = StaticMethods.preparePdf(toogleListAutomat, 0);

                    pdfDocument3.getCell().setPhrase(new Phrase(entry.getKey(), pdfDocument3.getFontHeader()));
                    pdfDocument3.getPdfTable().addCell(pdfDocument3.getCell());

                    StaticMethods.createCellStylePdf("headerBlack", pdfDocument3, pdfDocument3.getTableHeader());

                    if (toogleListAutomat.get(0)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("branchname"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(1)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("automat"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(2)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringamount"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(3)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("transferringbalance"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }

                    if (toogleListAutomat.get(4)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasequantity"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(5)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(6)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salegiro"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(7)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamount"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());

                    }
                    if (toogleListAutomat.get(8)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamountt"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(9)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamountt"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(10)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasecost"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(11)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitpercentage"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(12)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitmargin"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(13)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("profitprice"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(14)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("quantity"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(15)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("waste"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(16)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalincome"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(17)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalexpense"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }
                    if (toogleListAutomat.get(18)) {
                        pdfDocument3.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("winnings"), pdfDocument3.getFontColumnTitle()));
                        pdfDocument3.getPdfTable().addCell(pdfDocument3.getTableHeader());
                    }

                    pdfDocument.getDocument().add(pdfDocument3.getPdfTable());

                    for (GeneralStation automat : entry.getValue()) {

                        branchCurrency.setId(automat.getSaleCurrencyId().getId());

                        if (toogleListAutomat.get(0)) {
                            pdfDocument3.getDataCell().setPhrase(new Phrase(automat.getBranchSetting().getBranch().getName(), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                        }

                        if (toogleListAutomat.get(1)) {
                            pdfDocument3.getDataCell().setPhrase(new Phrase(automat.getVendingMachine().getName(), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                        }

                        if (toogleListAutomat.get(2)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTransferQuantity()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(3)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(4)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getPurchaseQuantity()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(5)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(6)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getSalesQuantity()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(7)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(8)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTransferQuantity().add(automat.getPurchaseQuantity()).subtract(automat.getSalesQuantity())), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(9)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTransferAmount().add(automat.getPurchaseAmount()).subtract(automat.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(10)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(11)) {

                            if (automat.getCost().compareTo(BigDecimal.ZERO) != 0 && automat.getCost() != null) {
                                bd = ((automat.getSalesAmount().subtract(automat.getCost())).divide(automat.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument3.getFont()));
                            } else {
                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument3.getFont()));
                            }
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                        }

                        if (toogleListAutomat.get(12)) {

                            if (automat.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && automat.getSalesAmount() != null) {
                                bd = ((automat.getSalesAmount().subtract(automat.getCost())).divide(automat.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument3.getFont()));
                            } else {
                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument3.getFont()));
                            }
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                        }

                        if (toogleListAutomat.get(13)) {
                            bd = (automat.getSalesAmount().subtract(automat.getPurchaseAmount()));
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(14)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getSalesQuantity()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }
                        if (toogleListAutomat.get(15)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getWaste()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                        if (toogleListAutomat.get(16)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTotalIncome()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }
                        if (toogleListAutomat.get(17)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTotalExpense()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }
                        if (toogleListAutomat.get(18)) {
                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(automat.getTotalWinnings()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());
                        }

                    }

                    //pdfDocument.getDocument().add(pdfDocument3.getPdfTable());
                    ///// Otomat Total
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                        for (GeneralStation total : totalListAutomat) {

                            if (total.getVendingMachine().getDeviceType().getId() == entry.getValue().get(0).getVendingMachine().getDeviceType().getId()) {

                                pdfDocument3.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());

                                if (selectedBranchList.size() > 1) {
                                    pdfDocument3.getDataCell().setPhrase(new Phrase(total.getBranchSetting().getBranch().getName(), pdfDocument3.getFont()));
                                    pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                                } else {
                                    pdfDocument3.getDataCell().setPhrase(new Phrase("", pdfDocument3.getFont()));
                                    pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                                }

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity()), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseQuantity()), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesQuantity()), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferQuantity().add(total.getPurchaseQuantity()).subtract(total.getSalesQuantity())), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getTransferAmount().add(total.getPurchaseAmount()).subtract(total.getSalesAmount())) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(total.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                    pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument3.getFont()));
                                } else {
                                    pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument3.getFont()));
                                }
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                    pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd), pdfDocument3.getFont()));
                                } else {
                                    pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(BigDecimal.ZERO), pdfDocument3.getFont()));
                                }
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                                bd = (total.getSalesAmount().subtract(total.getPurchaseAmount()));
                                pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                            }

                        }

                    } else {

                        for (Map.Entry<Integer, GeneralStation> entryAutomat : groupVendingMachineCalculated.entrySet()) {

                            pdfDocument3.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());

                            if (selectedBranchList.size() > 1) {
                                pdfDocument3.getDataCell().setPhrase(new Phrase(entryAutomat.getValue().getBranchSetting().getBranch().getName(), pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                            } else {
                                pdfDocument3.getDataCell().setPhrase(new Phrase("", pdfDocument3.getFont()));
                                pdfDocument3.getPdfTable().addCell(pdfDocument3.getDataCell());
                            }

                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entryAutomat.getValue().getSalesQuantity()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entryAutomat.getValue().getWaste()), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalIncome()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalExpense()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                            pdfDocument3.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalWinnings()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0), pdfDocument3.getFont()));
                            pdfDocument3.getPdfTable().addCell(pdfDocument3.getRightCell());

                        }

                    }

                    pdfDocument.getDocument().add(pdfDocument3.getPdfTable());

                }

            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("generalstationreport"));

        } catch (DocumentException e) {
        }
    }

    @Override
    public void exportExcel(Date beginDate, Date endDate, List<BranchSetting> selectedBranchList, String branchList, int lastUnitPrice, List<Boolean> toogleList,
            List<GeneralStation> listFuel, List<GeneralStation> totalListFuel, Map<Integer, GeneralStation> currencyTotalsCollection, List<Boolean> toogleListMarket,
            List<GeneralStation> listMarket, List<GeneralStation> totalListMarket, List<Boolean> toogleListAutomat,
            List<GeneralStation> listAutomat, List<GeneralStation> totalListAutomat, Map<Integer, GeneralStation> currencyTotalsCollection2, int centralIntegrationIf,
            BigDecimal totalPurchaseAmount, BigDecimal totalSalesAmount, BigDecimal totalProfitAmount, BigDecimal totalProfitRate, BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost, int reportType, int costType, List<GeneralStation> listOfTotalsCategory, HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated) {

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());

        try {

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("generalstationreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(1);

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

            String unitPrice = "";
            if (lastUnitPrice == 1) {
                unitPrice = sessionBean.getLoc().getString("lastsaleunitprice");
            } else {
                unitPrice = sessionBean.getLoc().getString("lastpurchaseunitprice");
            }
            SXSSFRow unitPriceRow = excelDocument.getSheet().createRow(5);
            unitPriceRow.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("setunitpriceforoutstandingbalance") + " : " + unitPrice);

            String cost = "";
            if (costType == 1) {
                cost = sessionBean.getLoc().getString("fifo");
            } else {
                cost = sessionBean.getLoc().getString("weightedaverage");
            }
            SXSSFRow costRow = excelDocument.getSheet().createRow(6);
            costRow.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cost") + " : " + cost);

            String reportTypeName = "";

            switch (reportType) {
                case 1: //Hepsi
                    reportTypeName = sessionBean.getLoc().getString("all");
                    break;
                case 2://Akaryakıt ürünleri
                    reportTypeName = sessionBean.getLoc().getString("fuelproduct");
                    break;
                case 3://Market kategorileri
                    reportTypeName = sessionBean.getLoc().getString("marketproduct");

                    break;
                case 4://Otomat
                    reportTypeName = sessionBean.getLoc().getString("automat");

                    break;
                default:
                    break;
            }

            SXSSFRow reportTypeRow = excelDocument.getSheet().createRow(7);
            reportTypeRow.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("reporttype") + " : " + reportTypeName);

            SXSSFRow emptyRow = excelDocument.getSheet().createRow(8);
            emptyRow.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("sum"));

            SXSSFRow totalRow = excelDocument.getSheet().createRow(9);
            totalRow.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("purchaseamount"));
            totalRow.createCell((short) 1).setCellValue((StaticMethods.round(totalPurchaseAmount.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
            totalRow.createCell((short) 2).setCellValue(sessionBean.getLoc().getString("salegiro"));
            totalRow.createCell((short) 3).setCellValue((StaticMethods.round(totalSalesAmount.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
            totalRow.createCell((short) 4).setCellValue(sessionBean.getLoc().getString("purchasecost"));
            totalRow.createCell((short) 5).setCellValue((StaticMethods.round(totalPurchaseCost.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
            totalRow.createCell((short) 6).setCellValue(sessionBean.getLoc().getString("profitprice"));
            totalRow.createCell((short) 7).setCellValue((StaticMethods.round(totalProfitAmount.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
            totalRow.createCell((short) 8).setCellValue(sessionBean.getLoc().getString("profitpercentage"));
            totalRow.createCell((short) 9).setCellValue((StaticMethods.round(totalProfitRate.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
            totalRow.createCell((short) 10).setCellValue(sessionBean.getLoc().getString("profitmargin"));
            totalRow.createCell((short) 11).setCellValue((StaticMethods.round(totalProfitMargin.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));

            int j = 10;
            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerDarkGray", excelDocument.getWorkbook());

            BigDecimal bd = new BigDecimal(BigInteger.ZERO);

            if (reportType == 1 || reportType == 2) {

                SXSSFRow reportName1 = excelDocument.getSheet().createRow(j++);
                SXSSFCell ceelheader1 = reportName1.createCell((short) 0);
                ceelheader1.setCellValue(sessionBean.getLoc().getString("fuelproduct"));
                ceelheader1.setCellStyle(headerCss(excelDocument.getWorkbook()));

                SXSSFRow rowm1 = excelDocument.getSheet().createRow(j++);

                SXSSFCell celld0 = rowm1.createCell((short) 0);
                celld0.setCellValue(sessionBean.getLoc().getString("branchname"));
                celld0.setCellStyle(styleheader);

                SXSSFCell celld1 = rowm1.createCell((short) 1);
                celld1.setCellValue(sessionBean.getLoc().getString("stock"));
                celld1.setCellStyle(styleheader);

                SXSSFCell celld2 = rowm1.createCell((short) 2);
                celld2.setCellValue(sessionBean.getLoc().getString("stockcode"));
                celld2.setCellStyle(styleheader);

                SXSSFCell celld3 = rowm1.createCell((short) 3);
                celld3.setCellValue(sessionBean.getLoc().getString("stockbarcode"));
                celld3.setCellStyle(styleheader);

                SXSSFCell celld4 = rowm1.createCell((short) 4);
                celld4.setCellValue(sessionBean.getLoc().getString("centerstockcode"));
                celld4.setCellStyle(styleheader);

                SXSSFCell celld5 = rowm1.createCell((short) 5);
                celld5.setCellValue(sessionBean.getLoc().getString("transferringamount"));
                celld5.setCellStyle(styleheader);

                SXSSFCell celld6 = rowm1.createCell((short) 6);
                celld6.setCellValue(sessionBean.getLoc().getString("transferringbalance"));
                celld6.setCellStyle(styleheader);

                SXSSFCell celld7 = rowm1.createCell((short) 7);
                celld7.setCellValue(sessionBean.getLoc().getString("purchasequantity"));
                celld7.setCellStyle(styleheader);

                SXSSFCell celld8 = rowm1.createCell((short) 8);
                celld8.setCellValue(sessionBean.getLoc().getString("purchaseamount"));
                celld8.setCellStyle(styleheader);

                SXSSFCell celld9 = rowm1.createCell((short) 9);
                celld9.setCellValue(sessionBean.getLoc().getString("salesamount"));
                celld9.setCellStyle(styleheader);

                SXSSFCell celld10 = rowm1.createCell((short) 10);
                celld10.setCellValue(sessionBean.getLoc().getString("salegiro"));
                celld10.setCellStyle(styleheader);

                SXSSFCell celld11 = rowm1.createCell((short) 11);
                celld11.setCellValue(sessionBean.getLoc().getString("remainingamount"));
                celld11.setCellStyle(styleheader);

                SXSSFCell celld12 = rowm1.createCell((short) 12);
                celld12.setCellValue(sessionBean.getLoc().getString("remainingamountt"));
                celld12.setCellStyle(styleheader);

                SXSSFCell celld13 = rowm1.createCell((short) 13);
                celld13.setCellValue(sessionBean.getLoc().getString("purchasecost"));
                celld13.setCellStyle(styleheader);

                SXSSFCell celld14 = rowm1.createCell((short) 14);
                celld14.setCellValue(sessionBean.getLoc().getString("profitpercentage"));
                celld14.setCellStyle(styleheader);

                SXSSFCell celld15 = rowm1.createCell((short) 15);
                celld15.setCellValue(sessionBean.getLoc().getString("profitmargin"));
                celld15.setCellStyle(styleheader);

                SXSSFCell celld16 = rowm1.createCell((short) 16);
                celld16.setCellValue(sessionBean.getLoc().getString("profitprice"));
                celld16.setCellStyle(styleheader);

                j = 12;

                // Fuel List
                for (GeneralStation fuel : listFuel) {

                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(j);

                    if (toogleList.get(0)) {
                        row.createCell((short) b++).setCellValue(fuel.getBranchSetting().getBranch().getName());
                    }
                    if (toogleList.get(1)) {
                        row.createCell((short) b++).setCellValue(fuel.getStock().getName());
                    }
                    if (toogleList.get(2)) {
                        row.createCell((short) b++).setCellValue(fuel.getStock().getCode());
                    }
                    if (toogleList.get(3)) {
                        row.createCell((short) b++).setCellValue(fuel.getStock().getBarcode());
                    }
                    if (toogleList.get(4)) {
                        row.createCell((short) b++).setCellValue(fuel.getStock().getCenterProductCode());
                    }
                    if (toogleList.get(5)) {

                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getTransferQuantity().doubleValue(), fuel.getStock().getUnit().getUnitRounding())));
                    }
                    if (toogleList.get(6)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getTransferAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }
                    if (toogleList.get(7)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getPurchaseQuantity().doubleValue(), fuel.getStock().getUnit().getUnitRounding())));
                    }
                    if (toogleList.get(8)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getPurchaseAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }
                    if (toogleList.get(9)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getSalesQuantity().doubleValue(), fuel.getStock().getUnit().getUnitRounding())));
                    }
                    if (toogleList.get(10)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getSalesAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }
                    if (toogleList.get(11)) {
                        bd = fuel.getTransferQuantity().add(fuel.getPurchaseQuantity()).subtract(fuel.getSalesQuantity());
                        row.createCell((short) b++).setCellValue((StaticMethods.round(bd.doubleValue(), fuel.getStock().getUnit().getUnitRounding())));
                    }
                    if (toogleList.get(12)) {
                        bd = fuel.getTransferAmount().add(fuel.getPurchaseAmount()).subtract(fuel.getSalesAmount());
                        row.createCell((short) b++).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }
                    if (toogleList.get(13)) {
                        row.createCell((short) b++).setCellValue((StaticMethods.round(fuel.getCost().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }

                    if (toogleList.get(14)) {
                        if (fuel.getCost().compareTo(BigDecimal.ZERO) != 0 && fuel.getCost() != null) {

                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));

                            row.createCell((short) b++).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        } else {
                            row.createCell((short) b++).setCellValue((StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        }
                    }
                    if (toogleList.get(15)) {
                        if (fuel.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && fuel.getSalesAmount() != null) {

                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));

                            row.createCell((short) b++).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        } else {
                            row.createCell((short) b++).setCellValue((StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        }
                    }

                    if (toogleList.get(16)) {

                        bd = fuel.getSalesAmount().subtract(fuel.getPurchaseAmount());

                        row.createCell((short) b++).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }

                    j++;

                }

                // Fuel Total
                for (GeneralStation total : totalListFuel) {

                    SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                    if (selectedBranchList.size() > 1) {
                        SXSSFCell e0 = rowf1.createCell((short) 4);
                        e0.setCellValue(" ( " + total.getBranchSetting().getBranch().getName() + " ) ");
                        e0.setCellStyle(cellStyle2);
                    }

                    SXSSFCell e5 = rowf1.createCell((short) 5);
                    e5.setCellValue(StaticMethods.round((total.getTransferQuantity() != null ? total.getTransferQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e5.setCellStyle(cellStyle2);

                    SXSSFCell e6 = rowf1.createCell((short) 6);
                    e6.setCellValue(StaticMethods.round((total.getTransferAmount() != null ? total.getTransferAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e6.setCellStyle(cellStyle2);

                    SXSSFCell e7 = rowf1.createCell((short) 7);
                    e7.setCellValue(StaticMethods.round((total.getPurchaseQuantity() != null ? total.getPurchaseQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e7.setCellStyle(cellStyle2);

                    SXSSFCell e8 = rowf1.createCell((short) 8);
                    e8.setCellValue(StaticMethods.round((total.getPurchaseAmount() != null ? total.getPurchaseAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e8.setCellStyle(cellStyle2);

                    SXSSFCell e9 = rowf1.createCell((short) 9);
                    e9.setCellValue(StaticMethods.round((total.getSalesQuantity() != null ? total.getSalesQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e9.setCellStyle(cellStyle2);

                    SXSSFCell e10 = rowf1.createCell((short) 10);
                    e10.setCellValue(StaticMethods.round((total.getSalesAmount() != null ? total.getSalesAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e10.setCellStyle(cellStyle2);

                    SXSSFCell e11 = rowf1.createCell((short) 11);
                    bd = (total.getTransferQuantity().add(total.getPurchaseQuantity())).subtract(total.getSalesQuantity());
                    e11.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e11.setCellStyle(cellStyle2);

                    SXSSFCell e12 = rowf1.createCell((short) 12);
                    bd = (total.getTransferAmount().add(total.getPurchaseAmount())).subtract(total.getSalesAmount());
                    e12.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e12.setCellStyle(cellStyle2);

                    SXSSFCell e13 = rowf1.createCell((short) 13);
                    e13.setCellValue(StaticMethods.round((total.getCost() != null ? total.getCost() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e13.setCellStyle(cellStyle2);

                    SXSSFCell e14 = rowf1.createCell((short) 14);
                    if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        e14.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        e14.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    e14.setCellStyle(cellStyle2);

                    SXSSFCell e15 = rowf1.createCell((short) 15);
                    if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        e15.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        e15.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    e15.setCellStyle(cellStyle2);

                    SXSSFCell e16 = rowf1.createCell((short) 16);
                    bd = total.getSalesAmount().subtract(total.getPurchaseAmount());
                    e16.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e16.setCellStyle(cellStyle2);

                }

                // Fuel Collection
                if (selectedBranchList.size() > 1) {
                    for (Map.Entry<Integer, GeneralStation> entry : currencyTotalsCollection.entrySet()) {

                        SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                        SXSSFCell e0 = rowf1.createCell((short) 4);
                        e0.setCellValue(sessionBean.getLoc().getString("sum"));
                        e0.setCellStyle(cellStyle2);

                        SXSSFCell e1 = rowf1.createCell((short) 5);
                        e1.setCellValue(StaticMethods.round((entry.getValue().getTransferQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e1.setCellStyle(cellStyle2);

                        SXSSFCell e2 = rowf1.createCell((short) 6);
                        e2.setCellValue(StaticMethods.round((entry.getValue().getTransferAmount()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e2.setCellStyle(cellStyle2);

                        SXSSFCell e3 = rowf1.createCell((short) 7);
                        e3.setCellValue(StaticMethods.round((entry.getValue().getPurchaseQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e3.setCellStyle(cellStyle2);

                        SXSSFCell e4 = rowf1.createCell((short) 8);
                        e4.setCellValue(StaticMethods.round((entry.getValue().getPurchaseAmount()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e4.setCellStyle(cellStyle2);

                        SXSSFCell e5 = rowf1.createCell((short) 9);
                        e5.setCellValue(StaticMethods.round((entry.getValue().getSalesQuantity()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e5.setCellStyle(cellStyle2);

                        SXSSFCell e6 = rowf1.createCell((short) 10);
                        e6.setCellValue(StaticMethods.round((entry.getValue().getSalesAmount()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e6.setCellStyle(cellStyle2);

                        SXSSFCell e7 = rowf1.createCell((short) 11);
                        bd = (entry.getValue().getTransferQuantity().add(entry.getValue().getPurchaseQuantity())).subtract(entry.getValue().getSalesQuantity());
                        e7.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e7.setCellStyle(cellStyle2);

                        SXSSFCell e8 = rowf1.createCell((short) 12);
                        bd = (entry.getValue().getTransferAmount().add(entry.getValue().getPurchaseAmount())).subtract(entry.getValue().getSalesAmount());
                        e8.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e8.setCellStyle(cellStyle2);

                        SXSSFCell e9 = rowf1.createCell((short) 13);
                        e9.setCellValue(StaticMethods.round((entry.getValue().getCost()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e9.setCellStyle(cellStyle2);

                        SXSSFCell e10 = rowf1.createCell((short) 14);
                        if (entry.getValue().getCost().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getCost() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            e10.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        } else {
                            e10.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                        e10.setCellStyle(cellStyle2);

                        SXSSFCell e11 = rowf1.createCell((short) 15);
                        if (entry.getValue().getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getSalesAmount() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            e11.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        } else {
                            e11.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                        e11.setCellStyle(cellStyle2);

                        SXSSFCell e12 = rowf1.createCell((short) 16);
                        bd = entry.getValue().getSalesAmount().subtract(entry.getValue().getPurchaseAmount());
                        e12.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        e12.setCellStyle(cellStyle2);
                    }
                }

            }

            //Market kategorileri
            if (reportType == 1 || reportType == 3) {

                j = j + 2;

                SXSSFRow reportName = excelDocument.getSheet().createRow(j++);
                SXSSFCell ceelheader = reportName.createCell((short) 0);
                ceelheader.setCellValue(sessionBean.getLoc().getString("marketproduct"));
                ceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                SXSSFRow rowm = excelDocument.getSheet().createRow(j++);

                SXSSFCell celld01 = rowm.createCell((short) 0);
                celld01.setCellValue(sessionBean.getLoc().getString("branchname"));
                celld01.setCellStyle(styleheader);

                SXSSFCell celld111 = rowm.createCell((short) 1);
                celld111.setCellValue(sessionBean.getLoc().getString("categoryname"));
                celld111.setCellStyle(styleheader);

                SXSSFCell celld21 = rowm.createCell((short) 2);
                celld21.setCellValue(sessionBean.getLoc().getString("transferringamount"));
                celld21.setCellStyle(styleheader);

                SXSSFCell celld31 = rowm.createCell((short) 3);
                celld31.setCellValue(sessionBean.getLoc().getString("transferringbalance"));
                celld31.setCellStyle(styleheader);

                SXSSFCell celld41 = rowm.createCell((short) 4);
                celld41.setCellValue(sessionBean.getLoc().getString("purchasequantity"));
                celld41.setCellStyle(styleheader);

                SXSSFCell celld51 = rowm.createCell((short) 5);
                celld51.setCellValue(sessionBean.getLoc().getString("purchaseamount"));
                celld51.setCellStyle(styleheader);

                SXSSFCell celld61 = rowm.createCell((short) 6);
                celld61.setCellValue(sessionBean.getLoc().getString("salesamount"));
                celld61.setCellStyle(styleheader);

                SXSSFCell celld71 = rowm.createCell((short) 7);
                celld71.setCellValue(sessionBean.getLoc().getString("salegiro"));
                celld71.setCellStyle(styleheader);

                SXSSFCell celld81 = rowm.createCell((short) 8);
                celld81.setCellValue(sessionBean.getLoc().getString("remainingamount"));
                celld81.setCellStyle(styleheader);

                SXSSFCell celld91 = rowm.createCell((short) 9);
                celld91.setCellValue(sessionBean.getLoc().getString("remainingamountt"));
                celld91.setCellStyle(styleheader);

                SXSSFCell celldcost = rowm.createCell((short) 10);
                celldcost.setCellValue(sessionBean.getLoc().getString("purchasecost"));
                celldcost.setCellStyle(styleheader);

                SXSSFCell celld101 = rowm.createCell((short) 11);
                celld101.setCellValue(sessionBean.getLoc().getString("profitpercentage"));
                celld101.setCellStyle(styleheader);

                SXSSFCell celld011 = rowm.createCell((short) 12);
                celld011.setCellValue(sessionBean.getLoc().getString("profitmargin"));
                celld011.setCellStyle(styleheader);

                SXSSFCell celld121 = rowm.createCell((short) 13);
                celld121.setCellValue(sessionBean.getLoc().getString("profitprice"));
                celld121.setCellStyle(styleheader);

                // Market List
                for (GeneralStation gs : listMarket) {

                    SXSSFRow row = excelDocument.getSheet().createRow(j);

                    if (toogleList.get(0)) {
                        row.createCell((short) 0).setCellValue(gs.getBranchSetting().getBranch().getName());
                    }

                    if (toogleList.get(1)) {
                        row.createCell((short) 1).setCellValue(gs.getCategorization().getName());
                    }

                    if (toogleList.get(2)) {
                        row.createCell((short) 2).setCellValue(StaticMethods.round(gs.getTransferQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(3)) {
                        row.createCell((short) 3).setCellValue(StaticMethods.round(gs.getTransferAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(4)) {
                        row.createCell((short) 4).setCellValue(StaticMethods.round(gs.getPurchaseQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(5)) {
                        row.createCell((short) 5).setCellValue(StaticMethods.round(gs.getPurchaseAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(6)) {
                        row.createCell((short) 6).setCellValue(StaticMethods.round(gs.getSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(7)) {
                        row.createCell((short) 7).setCellValue(StaticMethods.round(gs.getSalesAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(8)) {
                        bd = gs.getTransferQuantity().add(gs.getPurchaseQuantity()).subtract(gs.getSalesQuantity());
                        row.createCell((short) 8).setCellValue(StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(9)) {
                        bd = gs.getTransferAmount().add(gs.getPurchaseAmount()).subtract(gs.getSalesAmount());
                        row.createCell((short) 9).setCellValue(StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(10)) {
                        row.createCell((short) 10).setCellValue(StaticMethods.round(gs.getCost().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(11)) {
                        if (gs.getCost().compareTo(BigDecimal.ZERO) != 0 && gs.getCost() != null) {
                            bd = ((gs.getSalesAmount().subtract(gs.getCost())).divide(gs.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            row.createCell((short) 11).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        } else {
                            row.createCell((short) 11).setCellValue(StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                    }

                    if (toogleList.get(12)) {
                        if (gs.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && gs.getSalesAmount() != null) {

                            bd = ((gs.getSalesAmount().subtract(gs.getCost())).divide(gs.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            row.createCell((short) 12).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                        } else {
                            row.createCell((short) 12).setCellValue(StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                    }

                    if (toogleList.get(13)) {
                        bd = gs.getSalesAmount().subtract(gs.getPurchaseAmount());
                        row.createCell((short) 13).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                    }

                    j++;

                }

                // Market Total
                for (GeneralStation total : listOfTotalsCategory) {

                    SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                    if (selectedBranchList.size() > 1) {
                        SXSSFCell e01 = rowf1.createCell((short) 1);
                        e01.setCellValue(" ( " + total.getBranchSetting().getBranch().getName() + " ) ");
                        e01.setCellStyle(cellStyle2);
                    }
                    SXSSFCell e0 = rowf1.createCell((short) 2);
                    e0.setCellValue(StaticMethods.round((total.getTransferQuantity() != null ? total.getTransferQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e0.setCellStyle(cellStyle2);

                    SXSSFCell e1 = rowf1.createCell((short) 3);
                    e1.setCellValue(StaticMethods.round((total.getTransferAmount() != null ? total.getTransferAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e1.setCellStyle(cellStyle2);

                    SXSSFCell e2 = rowf1.createCell((short) 4);
                    e2.setCellValue(StaticMethods.round((total.getPurchaseQuantity() != null ? total.getPurchaseQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e2.setCellStyle(cellStyle2);

                    SXSSFCell e3 = rowf1.createCell((short) 5);
                    e3.setCellValue(StaticMethods.round((total.getPurchaseAmount() != null ? total.getPurchaseAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e3.setCellStyle(cellStyle2);

                    SXSSFCell e4 = rowf1.createCell((short) 6);
                    e4.setCellValue(StaticMethods.round((total.getSalesQuantity() != null ? total.getSalesQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e4.setCellStyle(cellStyle2);

                    SXSSFCell e5 = rowf1.createCell((short) 7);
                    e5.setCellValue(StaticMethods.round((total.getSalesAmount() != null ? total.getSalesAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e5.setCellStyle(cellStyle2);

                    SXSSFCell e7 = rowf1.createCell((short) 8);
                    bd = (total.getTransferQuantity().add(total.getPurchaseQuantity())).subtract(total.getSalesQuantity());
                    e7.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e7.setCellStyle(cellStyle2);

                    SXSSFCell e8 = rowf1.createCell((short) 9);
                    bd = (total.getTransferAmount().add(total.getPurchaseAmount())).subtract(total.getSalesAmount());
                    e8.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e8.setCellStyle(cellStyle2);

                    SXSSFCell e6 = rowf1.createCell((short) 10);
                    e6.setCellValue(StaticMethods.round((total.getCost() != null ? total.getCost() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e6.setCellStyle(cellStyle2);

                    SXSSFCell e9 = rowf1.createCell((short) 11);
                    if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        e9.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        e9.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    e9.setCellStyle(cellStyle2);

                    SXSSFCell e10 = rowf1.createCell((short) 12);
                    if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        e10.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        e10.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    e10.setCellStyle(cellStyle2);

                    SXSSFCell e11 = rowf1.createCell((short) 13);
                    bd = total.getSalesAmount().subtract(total.getPurchaseAmount());
                    e11.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    e11.setCellStyle(cellStyle2);

                }

            }

            //Otomatlar
            if (reportType == 1 || reportType == 4) {

                for (Map.Entry<String, List<GeneralStation>> entry : groupAutomatType.entrySet()) {

                    /////// otomat List
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() == -1) {
                        toogleListAutomat = Arrays.asList(true, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true);
                    } else {
                        toogleListAutomat = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false);
                    }

                    j = j + 2;

                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                        SXSSFRow reportName3 = excelDocument.getSheet().createRow(j++);
                        SXSSFCell ceelheader3 = reportName3.createCell((short) 0);
                        ceelheader3.setCellValue(entry.getKey());
                        ceelheader3.setCellStyle(headerCss(excelDocument.getWorkbook()));

                        SXSSFRow rowm3 = excelDocument.getSheet().createRow(j++);

                        SXSSFCell celld01Automat = rowm3.createCell((short) 0);
                        celld01Automat.setCellValue(sessionBean.getLoc().getString("branchname"));
                        celld01Automat.setCellStyle(styleheader);

                        SXSSFCell celld111Automat = rowm3.createCell((short) 1);
                        celld111Automat.setCellValue(sessionBean.getLoc().getString("automat"));
                        celld111Automat.setCellStyle(styleheader);

                        SXSSFCell celld21Automat = rowm3.createCell((short) 2);
                        celld21Automat.setCellValue(sessionBean.getLoc().getString("transferringamount"));
                        celld21Automat.setCellStyle(styleheader);

                        SXSSFCell celld31Automat = rowm3.createCell((short) 3);
                        celld31Automat.setCellValue(sessionBean.getLoc().getString("transferringbalance"));
                        celld31Automat.setCellStyle(styleheader);

                        SXSSFCell celld41Automat = rowm3.createCell((short) 4);
                        celld41Automat.setCellValue(sessionBean.getLoc().getString("purchasequantity"));
                        celld41Automat.setCellStyle(styleheader);

                        SXSSFCell celld51Automat = rowm3.createCell((short) 5);
                        celld51Automat.setCellValue(sessionBean.getLoc().getString("purchaseamount"));
                        celld51Automat.setCellStyle(styleheader);

                        SXSSFCell celld61Automat = rowm3.createCell((short) 6);
                        celld61Automat.setCellValue(sessionBean.getLoc().getString("salesamount"));
                        celld61Automat.setCellStyle(styleheader);

                        SXSSFCell celld71Automat = rowm3.createCell((short) 7);
                        celld71Automat.setCellValue(sessionBean.getLoc().getString("salegiro"));
                        celld71Automat.setCellStyle(styleheader);

                        SXSSFCell celld81Automat = rowm3.createCell((short) 8);
                        celld81Automat.setCellValue(sessionBean.getLoc().getString("remainingamount"));
                        celld81Automat.setCellStyle(styleheader);

                        SXSSFCell celld91Automat = rowm3.createCell((short) 9);
                        celld91Automat.setCellValue(sessionBean.getLoc().getString("remainingamountt"));
                        celld91Automat.setCellStyle(styleheader);

                        SXSSFCell celldcost = rowm3.createCell((short) 10);
                        celldcost.setCellValue(sessionBean.getLoc().getString("purchasecost"));
                        celldcost.setCellStyle(styleheader);

                        SXSSFCell celld101Automat = rowm3.createCell((short) 11);
                        celld101Automat.setCellValue(sessionBean.getLoc().getString("profitpercentage"));
                        celld101Automat.setCellStyle(styleheader);

                        SXSSFCell celld011Automat = rowm3.createCell((short) 12);
                        celld011Automat.setCellValue(sessionBean.getLoc().getString("profitmargin"));
                        celld011Automat.setCellStyle(styleheader);

                        SXSSFCell celld121Automat = rowm3.createCell((short) 13);
                        celld121Automat.setCellValue(sessionBean.getLoc().getString("profitprice"));
                        celld121Automat.setCellStyle(styleheader);

                    } else {

                        SXSSFRow reportName3 = excelDocument.getSheet().createRow(j++);
                        SXSSFCell ceelheader3 = reportName3.createCell((short) 0);
                        ceelheader3.setCellValue(entry.getKey());
                        ceelheader3.setCellStyle(headerCss(excelDocument.getWorkbook()));

                        SXSSFRow rowm3 = excelDocument.getSheet().createRow(j++);

                        SXSSFCell celld01Automat = rowm3.createCell((short) 0);
                        celld01Automat.setCellValue(sessionBean.getLoc().getString("branchname"));
                        celld01Automat.setCellStyle(styleheader);

                        SXSSFCell celld111Automat = rowm3.createCell((short) 1);
                        celld111Automat.setCellValue(sessionBean.getLoc().getString("automat"));
                        celld111Automat.setCellStyle(styleheader);

                        SXSSFCell celldquantity = rowm3.createCell((short) 2);
                        celldquantity.setCellValue(sessionBean.getLoc().getString("quantity"));
                        celldquantity.setCellStyle(styleheader);

                        SXSSFCell celldWaste = rowm3.createCell((short) 3);
                        celldWaste.setCellValue(sessionBean.getLoc().getString("waste"));
                        celldWaste.setCellStyle(styleheader);

                        SXSSFCell celldincome = rowm3.createCell((short) 4);
                        celldincome.setCellValue(sessionBean.getLoc().getString("totalincome"));
                        celldincome.setCellStyle(styleheader);

                        SXSSFCell celldExpense = rowm3.createCell((short) 5);
                        celldExpense.setCellValue(sessionBean.getLoc().getString("totalexpense"));
                        celldExpense.setCellStyle(styleheader);

                        SXSSFCell celldwinnings = rowm3.createCell((short) 6);
                        celldwinnings.setCellValue(sessionBean.getLoc().getString("winnings"));
                        celldwinnings.setCellStyle(styleheader);

                    }

                    // Otomat List
                    for (GeneralStation gs : entry.getValue()) {
                        SXSSFRow row = excelDocument.getSheet().createRow(j);

                        if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                            row.createCell((short) 0).setCellValue(gs.getBranchSetting().getBranch().getName());

                            row.createCell((short) 1).setCellValue(gs.getVendingMachine().getName());

                            row.createCell((short) 2).setCellValue(StaticMethods.round(gs.getTransferQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 3).setCellValue(StaticMethods.round(gs.getTransferAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 4).setCellValue(StaticMethods.round(gs.getPurchaseQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 5).setCellValue(StaticMethods.round(gs.getPurchaseAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 6).setCellValue(StaticMethods.round(gs.getSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 7).setCellValue(StaticMethods.round(gs.getSalesAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            bd = gs.getTransferQuantity().add(gs.getPurchaseQuantity()).subtract(gs.getSalesQuantity());
                            row.createCell((short) 8).setCellValue(StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            bd = gs.getTransferAmount().add(gs.getPurchaseAmount()).subtract(gs.getSalesAmount());
                            row.createCell((short) 9).setCellValue(StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 10).setCellValue(StaticMethods.round(gs.getCost().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            if (gs.getCost().compareTo(BigDecimal.ZERO) != 0 && gs.getCost() != null) {
                                bd = ((gs.getSalesAmount().subtract(gs.getCost())).divide(gs.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                row.createCell((short) 11).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                            } else {
                                row.createCell((short) 11).setCellValue(StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            }

                            if (gs.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && gs.getSalesAmount() != null) {

                                bd = ((gs.getSalesAmount().subtract(gs.getCost())).divide(gs.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                row.createCell((short) 12).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));
                            } else {
                                row.createCell((short) 12).setCellValue(StaticMethods.round(BigDecimal.ZERO.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            }

                            bd = gs.getSalesAmount().subtract(gs.getPurchaseAmount());
                            row.createCell((short) 13).setCellValue((StaticMethods.round(bd.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding())));

                        } else {

                            row.createCell((short) 0).setCellValue(gs.getBranchSetting().getBranch().getName());

                            row.createCell((short) 1).setCellValue(gs.getVendingMachine().getName());

                            row.createCell((short) 2).setCellValue(StaticMethods.round(gs.getSalesQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 3).setCellValue(StaticMethods.round(gs.getWaste().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 4).setCellValue(StaticMethods.round(gs.getTotalIncome().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 5).setCellValue(StaticMethods.round(gs.getTotalExpense().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                            row.createCell((short) 6).setCellValue(StaticMethods.round(gs.getTotalWinnings().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                        }

                        j++;

                    }

                    ///// Otomat Total
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                        // Otomat Total
                        for (GeneralStation total : totalListAutomat) {

                            if (total.getVendingMachine().getDeviceType().getId() == entry.getValue().get(0).getVendingMachine().getDeviceType().getId()) {

                                SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                                if (selectedBranchList.size() > 1) {
                                    SXSSFCell e0 = rowf1.createCell((short) 1);
                                    e0.setCellValue(" ( " + total.getBranchSetting().getBranch().getName() + " ) ");
                                    e0.setCellStyle(cellStyle2);
                                }

                                SXSSFCell e1 = rowf1.createCell((short) 2);
                                e1.setCellValue(StaticMethods.round((total.getTransferQuantity() != null ? total.getTransferQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e1.setCellStyle(cellStyle2);

                                SXSSFCell e2 = rowf1.createCell((short) 3);
                                e2.setCellValue(StaticMethods.round((total.getTransferAmount() != null ? total.getTransferAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e2.setCellStyle(cellStyle2);

                                SXSSFCell e3 = rowf1.createCell((short) 4);
                                e3.setCellValue(StaticMethods.round((total.getPurchaseQuantity() != null ? total.getPurchaseQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e3.setCellStyle(cellStyle2);

                                SXSSFCell e4 = rowf1.createCell((short) 5);
                                e4.setCellValue(StaticMethods.round((total.getPurchaseAmount() != null ? total.getPurchaseAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e4.setCellStyle(cellStyle2);

                                SXSSFCell e5 = rowf1.createCell((short) 6);
                                e5.setCellValue(StaticMethods.round((total.getSalesQuantity() != null ? total.getSalesQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e5.setCellStyle(cellStyle2);

                                SXSSFCell e6 = rowf1.createCell((short) 7);
                                e6.setCellValue(StaticMethods.round((total.getSalesAmount() != null ? total.getSalesAmount() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e6.setCellStyle(cellStyle2);

                                SXSSFCell e7 = rowf1.createCell((short) 8);
                                bd = (total.getTransferQuantity().add(total.getPurchaseQuantity())).subtract(total.getSalesQuantity());
                                e7.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e7.setCellStyle(cellStyle2);

                                SXSSFCell e8 = rowf1.createCell((short) 9);
                                bd = (total.getTransferAmount().add(total.getPurchaseAmount())).subtract(total.getSalesAmount());
                                e8.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e8.setCellStyle(cellStyle2);

                                SXSSFCell ecost = rowf1.createCell((short) 10);
                                ecost.setCellValue(StaticMethods.round((total.getCost() != null ? total.getCost() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                ecost.setCellStyle(cellStyle2);

                                SXSSFCell e81 = rowf1.createCell((short) 11);
                                if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                    e81.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                } else {
                                    e81.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                }
                                e81.setCellStyle(cellStyle2);

                                SXSSFCell e9 = rowf1.createCell((short) 12);
                                if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                    e9.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                } else {
                                    e9.setCellValue(StaticMethods.round((BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                }
                                e9.setCellStyle(cellStyle2);

                                SXSSFCell e10 = rowf1.createCell((short) 13);
                                bd = total.getSalesAmount().subtract(total.getPurchaseAmount());
                                e10.setCellValue(StaticMethods.round((bd != null ? bd : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                                e10.setCellStyle(cellStyle2);

                            }

                        }

                    } else {

                        for (Map.Entry<Integer, GeneralStation> entryAutomat : groupVendingMachineCalculated.entrySet()) {

                            SXSSFRow rowf1 = excelDocument.getSheet().createRow(j++);

                            if (selectedBranchList.size() > 1) {
                                SXSSFCell e0 = rowf1.createCell((short) 1);
                                e0.setCellValue(" ( " + entryAutomat.getValue().getBranchSetting().getBranch().getName() + " ) ");
                                e0.setCellStyle(cellStyle2);
                            }

                            SXSSFCell e1 = rowf1.createCell((short) 2);
                            e1.setCellValue(StaticMethods.round((entryAutomat.getValue().getSalesQuantity() != null ? entryAutomat.getValue().getSalesQuantity() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            e1.setCellStyle(cellStyle2);

                            SXSSFCell e2 = rowf1.createCell((short) 3);
                            e2.setCellValue(StaticMethods.round((entryAutomat.getValue().getWaste() != null ? entryAutomat.getValue().getWaste() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            e2.setCellStyle(cellStyle2);

                            SXSSFCell e3 = rowf1.createCell((short) 4);
                            e3.setCellValue(StaticMethods.round((entryAutomat.getValue().getTotalIncome() != null ? entryAutomat.getValue().getTotalIncome() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            e3.setCellStyle(cellStyle2);

                            SXSSFCell e4 = rowf1.createCell((short) 5);
                            e4.setCellValue(StaticMethods.round((entryAutomat.getValue().getTotalExpense() != null ? entryAutomat.getValue().getTotalExpense() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            e4.setCellStyle(cellStyle2);

                            SXSSFCell e5 = rowf1.createCell((short) 6);
                            e5.setCellValue(StaticMethods.round((entryAutomat.getValue().getTotalWinnings() != null ? entryAutomat.getValue().getTotalWinnings() : BigDecimal.ZERO).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                            e5.setCellStyle(cellStyle2);

                        }

                    }

                }

            }

            j = j + 2;

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("generalstationreport"));
            } catch (IOException ex) {
                Logger.getLogger(GeneralStationReportService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
        }
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
    public String exportPrinter(Date beginDate, Date endDate, List<BranchSetting> selectedBranchList, int lastUnitPrice, List<Boolean> toogleList,
            List<GeneralStation> listFuel, List<GeneralStation> totalListFuel, Map<Integer, GeneralStation> currencyTotalsCollection, List<Boolean> toogleListMarket,
            List<GeneralStation> listMarket, List<Boolean> toogleListAutomat, List<GeneralStation> totalListAutomat, int centralIntegrationIf,
            BigDecimal totalPurchaseAmount, BigDecimal totalSalesAmount, BigDecimal totalProfitAmount, BigDecimal totalProfitRate, BigDecimal totalProfitMargin,
            BigDecimal totalPurchaseCost, int reportType, int costType, List<GeneralStation> listOfTotalsCategory, HashMap<String, List<GeneralStation>> groupAutomatType,
            HashMap<Integer, GeneralStation> groupVendingMachineCalculated) {

        StringBuilder sb = new StringBuilder();

        try {

            Currency branchCurrency = new Currency();
            branchCurrency.setId(sessionBean.getUser().getLastBranch().getCurrency().getId());

            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), endDate)).append(" </div> ");

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
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String unitPrice = "";
            if (lastUnitPrice == 1) {
                unitPrice = sessionBean.getLoc().getString("lastsaleunitprice");
            } else {
                unitPrice = sessionBean.getLoc().getString("lastpurchaseunitprice");
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("setunitpriceforoutstandingbalance")).append(" : ").append(unitPrice).append(" </div> ");

            String cost = "";
            if (costType == 1) {
                cost = sessionBean.getLoc().getString("fifo");
            } else {
                cost = sessionBean.getLoc().getString("weightedaverage");
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("cost")).append(" : ").append(cost).append(" </div> ");

            String reportTypeName = "";
            switch (reportType) {
                case 1: //Hepsi
                    reportTypeName = sessionBean.getLoc().getString("all");
                    break;
                case 2://Akaryakıt ürünleri
                    reportTypeName = sessionBean.getLoc().getString("fuelproduct");
                    break;
                case 3://Market kategorileri
                    reportTypeName = sessionBean.getLoc().getString("marketproduct");

                    break;
                case 4://Otomat
                    reportTypeName = sessionBean.getLoc().getString("automat");

                    break;
                default:
                    break;
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("reporttype")).append(" : ").append(reportTypeName).append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%;\">");
            sb.append("<b>" + sessionBean.getLoc().getString("sum") + "</b><br>");
            sb.append(sessionBean.getLoc().getString("purchaseamount") + ": " + (sessionBean.getNumberFormat().format(totalPurchaseAmount == null ? "" : totalPurchaseAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)));
            sb.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            sb.append(sessionBean.getLoc().getString("salegiro") + ": " + (sessionBean.getNumberFormat().format(totalSalesAmount == null ? "" : totalSalesAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)));
            sb.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            sb.append(sessionBean.getLoc().getString("purchasecost") + ": " + (sessionBean.getNumberFormat().format(totalPurchaseCost == null ? "" : totalPurchaseCost) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)));
            sb.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            sb.append(sessionBean.getLoc().getString("profitprice") + ": " + (sessionBean.getNumberFormat().format(totalProfitAmount == null ? "" : totalProfitAmount) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)));
            sb.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            sb.append(sessionBean.getLoc().getString("profitpercentage") + ": " + (sessionBean.getNumberFormat().format(totalProfitRate == null ? "" : totalProfitRate)));
            sb.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            sb.append(sessionBean.getLoc().getString("profitmargin") + ": " + (sessionBean.getNumberFormat().format(totalProfitMargin == null ? "" : totalProfitMargin)));
            sb.append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            BigDecimal bd = BigDecimal.ZERO;

            if (reportType == 1 || reportType == 2) {

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
                        + "    </style> <table>"
                        + "<tr><td colspan=\"17\" style=\"text-align:center;font-size:13pt;font-weight:bold\">" + sessionBean.getLoc().getString("fuelproduct") + "</td></tr>"
                        + " <tr>");

                sb.append(" </tr>  ");

                StaticMethods.createHeaderPrint("dtbGeneralStationFuel", toogleList, "headerBlack", sb);

                // Fuel List
                for (GeneralStation fuel : listFuel) {
                    branchCurrency.setId(fuel.getSaleCurrencyId().getId());

                    if (toogleList.get(0)) {
                        sb.append("<td>").append(fuel.getBranchSetting().getBranch().getName() == null ? "" : fuel.getBranchSetting().getBranch().getName()).append("</td>");
                    }

                    if (toogleList.get(1)) {
                        sb.append("<td>").append(fuel.getStock().getName() == null ? "" : fuel.getStock().getName()).append("</td>");
                    }

                    if (toogleList.get(2)) {
                        sb.append("<td>").append(fuel.getStock().getCode() == null ? "" : fuel.getStock().getCode()).append("</td>");
                    }

                    if (toogleList.get(3)) {
                        sb.append("<td>").append(fuel.getStock().getBarcode() == null ? "" : fuel.getStock().getBarcode()).append("</td>");
                    }

                    if (toogleList.get(4)) {
                        sb.append("<td>").append(fuel.getStock().getCenterProductCode() == null ? "" : fuel.getStock().getCenterProductCode()).append("</td>");
                    }

                    if (toogleList.get(5)) {
                        sb.append("<td style=\"text-align: right\"> ").append(sessionBean.getNumberFormat().format(fuel.getTransferQuantity() == null ? "" : fuel.getTransferQuantity())).append("</td>");
                    }

                    if (toogleList.get(6)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getTransferAmount() == null ? "" : fuel.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(7)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getPurchaseQuantity() == null ? "" : fuel.getPurchaseQuantity())).append("</td>");
                    }

                    if (toogleList.get(8)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getPurchaseAmount() == null ? "" : fuel.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(9)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getSalesQuantity() == null ? "" : fuel.getSalesQuantity())).append("</td>");
                    }

                    if (toogleList.get(10)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getSalesAmount() == null ? "" : fuel.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(11)) {
                        bd = fuel.getTransferQuantity().add(fuel.getPurchaseQuantity()).subtract(fuel.getSalesQuantity());
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd)).append("</td>");
                    }

                    if (toogleList.get(12)) {
                        bd = fuel.getTransferAmount().add(fuel.getPurchaseAmount()).subtract(fuel.getSalesAmount());
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(13)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(fuel.getCost() == null ? "" : fuel.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    if (toogleList.get(14)) {
                        if (fuel.getCost().compareTo(BigDecimal.ZERO) != 0 && fuel.getCost() != null) {
                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            bd = BigDecimal.ZERO;
                        }
                        sb.append("<td style=\"text-align: right\">").append(bd).append("</td>");
                    }

                    if (toogleList.get(15)) {
                        if (fuel.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && fuel.getSalesAmount() != null) {
                            bd = ((fuel.getSalesAmount().subtract(fuel.getCost())).divide(fuel.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            bd = BigDecimal.ZERO;
                        }
                        sb.append("<td style=\"text-align: right\">").append(bd).append("</td>");
                    }

                    if (toogleList.get(16)) {
                        bd = fuel.getSalesAmount().subtract(fuel.getPurchaseAmount());
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    }

                    sb.append(" </tr> ");

                }

                // Fuel Total
                for (GeneralStation total : totalListFuel) {

                    branchCurrency.setId(total.getSaleCurrencyId().getId());
                    sb.append(" <tr> ");

                    if (selectedBranchList.size() > 1) {
                        sb.append(" <td></td> <td></td> <td></td> <td></td> <td><b>").append(total.getBranchSetting().getBranch().getName()).append("</b></td>");
                    } else {
                        sb.append(" <td></td> <td></td> <td></td> <td></td> <td><b>").append("").append("</b></td>");
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getTransferQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getPurchaseQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getSalesQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    bd = total.getTransferQuantity().add(total.getPurchaseQuantity()).subtract(total.getSalesQuantity());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd)).append("</b></td>");

                    bd = total.getTransferAmount().add(total.getPurchaseAmount()).subtract(total.getSalesAmount());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                    if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                        bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                    bd = total.getSalesAmount().subtract(total.getPurchaseAmount());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append(" </tr> ");

                }

                // Fuel Collection
                if (selectedBranchList.size() > 1 && !currencyTotalsCollection.isEmpty()) {

                    sb.append(" <tr> ");
                    sb.append(" <td></td> <td></td> <td></td> <td></td> <td><b>").append(sessionBean.loc.getString("sum")).append("</b></td>");
                    for (Map.Entry<Integer, GeneralStation> entry : currencyTotalsCollection.entrySet()) {
                        branchCurrency.setId(entry.getValue().getSaleCurrencyId().getId());
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getTransferQuantity())).append("</b></td>");
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getPurchaseQuantity())).append("</b></td>");
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getSalesQuantity())).append("</b></td>");
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                        bd = entry.getValue().getTransferQuantity().add(entry.getValue().getPurchaseQuantity()).subtract(entry.getValue().getSalesQuantity());
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd)).append("</b></td>");

                        bd = entry.getValue().getTransferAmount().add(entry.getValue().getPurchaseAmount()).subtract(entry.getValue().getSalesAmount());
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entry.getValue().getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                        if (entry.getValue().getCost().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getCost() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            bd = BigDecimal.ZERO;
                        }
                        sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                        if (entry.getValue().getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && entry.getValue().getSalesAmount() != null) {
                            bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getCost())).divide(entry.getValue().getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            bd = BigDecimal.ZERO;
                        }
                        sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                        bd = entry.getValue().getSalesAmount().subtract(entry.getValue().getPurchaseAmount());
                        sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    }
                    sb.append(" </tr> ");
                }
            }

            //Market kategorileri
            if (reportType == 1 || reportType == 3) {

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
                        + "    </style> <table>"
                        + "<tr><td colspan=\"14\" style=\"text-align:center;font-size:13pt;font-weight:bold\">" + sessionBean.getLoc().getString("marketproduct") + "</td></tr>"
                        + " <tr>");

                sb.append(" </tr>  ");

                sb.append(" </table> ");
                sb.append("<br>");
                sb.append("<table> ");
                // sb.append("<tr><td colspan=\"14\" style=\"text-align:center;font-size:13pt;font-weight:bold\">" + sessionBean.getLoc().getString("marketproduct") + "</td></tr>");

                String[] columns;

                columns = new String[]{sessionBean.getLoc().getString("branchname"),
                    sessionBean.getLoc().getString("categoryname"),
                    sessionBean.getLoc().getString("transferringamount"),
                    sessionBean.getLoc().getString("transferringbalance"),
                    sessionBean.getLoc().getString("purchasequantity"),
                    sessionBean.getLoc().getString("purchaseamount"),
                    sessionBean.getLoc().getString("salesamount"),
                    sessionBean.getLoc().getString("salegiro"),
                    sessionBean.getLoc().getString("remainingamount"),
                    sessionBean.getLoc().getString("remainingamountt"),
                    sessionBean.getLoc().getString("purchasecost"),
                    sessionBean.getLoc().getString("profitpercentage"),
                    sessionBean.getLoc().getString("profitmargin"),
                    sessionBean.getLoc().getString("profitprice")};

                sb.append(" <tr>  ");
                for (int x = 0; x < columns.length; x++) {
                    if (toogleListMarket.get(x)) {
                        sb.append("<th>").append(columns[x]).append("</th>");
                    }
                }
                sb.append(" </tr>  ");
                sb.append(" </tr>  ");
                sb.append(" <tr>  ");

                // Market List
                for (GeneralStation gs1 : listMarket) {
                    branchCurrency.setId(gs1.getSaleCurrencyId().getId());
                    sb.append("<td>").append(gs1.getBranchSetting().getBranch().getName() == null ? "" : gs1.getBranchSetting().getBranch().getName()).append("</td>");
                    sb.append("<td>").append(gs1.getCategorization().getName() == null ? "" : gs1.getCategorization().getName()).append("</td>");
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getTransferQuantity() == null ? "" : gs1.getTransferQuantity())).append("</td>");
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getTransferAmount() == null ? "" : gs1.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getPurchaseQuantity() == null ? "" : gs1.getPurchaseQuantity())).append("</td>");
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getPurchaseAmount() == null ? "" : gs1.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getSalesQuantity() == null ? BigDecimal.ZERO : gs1.getSalesQuantity())).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getSalesAmount() == null ? BigDecimal.ZERO : gs1.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                    bd = gs1.getTransferQuantity().add(gs1.getPurchaseQuantity()).subtract(gs1.getSalesQuantity());
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd)).append("</td>");

                    bd = gs1.getTransferAmount().add(gs1.getPurchaseAmount()).subtract(gs1.getSalesAmount());
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs1.getCost() == null ? BigDecimal.ZERO : gs1.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                    if (gs1.getCost().compareTo(BigDecimal.ZERO) != 0 && gs1.getCost() != null) {
                        bd = ((gs1.getSalesAmount().subtract(gs1.getCost())).divide(gs1.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\">").append(bd == null ? BigDecimal.ZERO : bd).append("</td>");

                    if (gs1.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && gs1.getSalesAmount() != null) {
                        bd = ((gs1.getSalesAmount().subtract(gs1.getCost())).divide(gs1.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\">").append(bd == null ? BigDecimal.ZERO : bd).append("</td>");

                    bd = gs1.getSalesAmount().subtract(gs1.getPurchaseAmount());
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                    sb.append(" </tr>  ");
                }

                // Market Total
                for (GeneralStation total1 : listOfTotalsCategory) {
                    branchCurrency.setId(total1.getSaleCurrencyId().getId());
                    sb.append(" <tr> ");
                    if (selectedBranchList.size() > 1) {
                        sb.append(" <td></td>  <td><b>").append(total1.getBranchSetting().getBranch().getName()).append("</b></td>");
                    } else {
                        sb.append(" <td></td>  <td><b>").append("").append("</b></td>");
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getTransferQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getPurchaseQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getSalesQuantity())).append("</b></td>");
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    bd = total1.getTransferQuantity().add(total1.getPurchaseQuantity()).subtract(total1.getSalesQuantity());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd)).append("</b></td>");

                    bd = total1.getTransferAmount().add(total1.getPurchaseAmount()).subtract(total1.getSalesAmount());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total1.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                    if (total1.getCost().compareTo(BigDecimal.ZERO) != 0 && total1.getCost() != null) {
                        bd = ((total1.getSalesAmount().subtract(total1.getCost())).divide(total1.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                    if (total1.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total1.getSalesAmount() != null) {
                        bd = ((total1.getSalesAmount().subtract(total1.getCost())).divide(total1.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        bd = BigDecimal.ZERO;
                    }
                    sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                    bd = total1.getSalesAmount().subtract(total1.getPurchaseAmount());
                    sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                    sb.append(" </tr> ");
                }

            }

            //Otomatlar
            if (reportType == 1 || reportType == 4) {

                for (Map.Entry<String, List<GeneralStation>> entry : groupAutomatType.entrySet()) {
                    /////// otomat List
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() == -1) {
                        toogleListAutomat = Arrays.asList(true, true, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true);
                    } else {
                        toogleListAutomat = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false);
                    }
                    String append = "";

                    append = " <style>"
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
                            + "    </style> <table>";
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                        append = append + "<tr><td colspan=\"14\" style=\"text-align:center;font-size:13pt;font-weight:bold\">" + entry.getKey() + "</td></tr>"
                                + " <tr>";

                        sb.append(append);

                        sb.append(" </tr>  ");

                        sb.append(" </table> ");
                        sb.append("<br>");
                        sb.append("<table> ");

                        String[] columns;

                        columns = new String[]{sessionBean.getLoc().getString("branchname"),
                            sessionBean.getLoc().getString("automat"),
                            sessionBean.getLoc().getString("transferringamount"),
                            sessionBean.getLoc().getString("transferringbalance"),
                            sessionBean.getLoc().getString("purchasequantity"),
                            sessionBean.getLoc().getString("purchaseamount"),
                            sessionBean.getLoc().getString("salesamount"),
                            sessionBean.getLoc().getString("salegiro"),
                            sessionBean.getLoc().getString("remainingamount"),
                            sessionBean.getLoc().getString("remainingamountt"),
                            sessionBean.getLoc().getString("purchasecost"),
                            sessionBean.getLoc().getString("profitpercentage"),
                            sessionBean.getLoc().getString("profitmargin"),
                            sessionBean.getLoc().getString("profitprice")};

                        sb.append(" <tr>  ");
                        for (int x = 0; x < columns.length; x++) {
                            if (toogleListMarket.get(x)) {
                                sb.append("<th>").append(columns[x]).append("</th>");
                            }
                        }
                        sb.append(" </tr>  ");
                        sb.append(" </tr>  ");
                        sb.append(" <tr>  ");

                    } else {

                        append = append + "<tr><td colspan=\"7\" style=\"text-align:center;font-size:13pt;font-weight:bold\">" + entry.getKey() + "</td></tr>"
                                + " <tr>";

                        sb.append(append);

                        sb.append(" </tr>  ");

                        sb.append(" </table> ");
                        sb.append("<br>");
                        sb.append("<table> ");

                        String[] columns;

                        columns = new String[]{sessionBean.getLoc().getString("branchname"),
                            sessionBean.getLoc().getString("automat"),
                            sessionBean.getLoc().getString("quantity"),
                            sessionBean.getLoc().getString("waste"),
                            sessionBean.getLoc().getString("totalincome"),
                            sessionBean.getLoc().getString("totalexpense"),
                            sessionBean.getLoc().getString("winnings")};

                        sb.append(" <tr>  ");
                        for (int x = 0; x < columns.length; x++) {
                            if (toogleListMarket.get(x)) {
                                sb.append("<th>").append(columns[x]).append("</th>");
                            }
                        }
                        sb.append(" </tr>  ");
                        sb.append(" </tr>  ");
                        sb.append(" <tr>  ");

                    }

                    for (GeneralStation gs2 : entry.getValue()) {

                        branchCurrency.setId(gs2.getSaleCurrencyId().getId());
                        if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                            sb.append("<td>").append(gs2.getBranchSetting().getBranch().getName() == null ? "" : gs2.getBranchSetting().getBranch().getName()).append("</td>");
                            sb.append("<td>").append(gs2.getVendingMachine().getName() == null ? "" : gs2.getVendingMachine().getName()).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getTransferQuantity() == null ? BigDecimal.ZERO : gs2.getTransferQuantity())).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getTransferAmount() == null ? BigDecimal.ZERO : gs2.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getPurchaseQuantity() == null ? BigDecimal.ZERO : gs2.getPurchaseQuantity())).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getPurchaseAmount() == null ? BigDecimal.ZERO : gs2.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getSalesQuantity() == null ? BigDecimal.ZERO : gs2.getSalesQuantity())).append("</td>");

                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getSalesAmount() == null ? BigDecimal.ZERO : gs2.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                            bd = gs2.getTransferQuantity().add(gs2.getPurchaseQuantity()).subtract(gs2.getSalesQuantity());
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd)).append("</td>");

                            bd = gs2.getTransferAmount().add(gs2.getPurchaseAmount()).subtract(gs2.getSalesAmount());
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getCost() == null ? BigDecimal.ZERO : gs2.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                            if (gs2.getCost().compareTo(BigDecimal.ZERO) != 0 && gs2.getCost() != null) {
                                bd = ((gs2.getSalesAmount().subtract(gs2.getCost())).divide(gs2.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            } else {
                                bd = BigDecimal.ZERO;
                            }
                            sb.append("<td style=\"text-align: right\">").append(bd == null ? BigDecimal.ZERO : bd).append("</td>");

                            if (gs2.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && gs2.getSalesAmount() != null) {
                                bd = ((gs2.getSalesAmount().subtract(gs2.getCost())).divide(gs2.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                            } else {
                                bd = BigDecimal.ZERO;
                            }
                            sb.append("<td style=\"text-align: right\">").append(bd == null ? BigDecimal.ZERO : bd).append("</td>");

                            bd = gs2.getSalesAmount().subtract(gs2.getPurchaseAmount());
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(bd == null ? BigDecimal.ZERO : bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");

                            sb.append(" </tr>  ");

                        } else {

                            sb.append("<td>").append(gs2.getBranchSetting().getBranch().getName() == null ? "" : gs2.getBranchSetting().getBranch().getName()).append("</td>");
                            sb.append("<td>").append(gs2.getVendingMachine().getName() == null ? "" : gs2.getVendingMachine().getName()).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getSalesQuantity() == null ? BigDecimal.ZERO : gs2.getSalesQuantity())).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getWaste() == null ? BigDecimal.ZERO : gs2.getWaste()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getTotalIncome() == null ? BigDecimal.ZERO : gs2.getTotalIncome())).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getTotalExpense() == null ? BigDecimal.ZERO : gs2.getTotalExpense()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</td>");
                            sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(gs2.getTotalWinnings() == null ? BigDecimal.ZERO : gs2.getTotalWinnings())).append("</td>");

                            sb.append(" </tr>  ");

                        }

                    }

                    ///// Otomat Total
                    if (entry.getValue().get(0).getVendingMachine().getDeviceType().getId() != -1) {

                        // Otomat Total
                        for (GeneralStation total : totalListAutomat) {

                            if (total.getVendingMachine().getDeviceType().getId() == entry.getValue().get(0).getVendingMachine().getDeviceType().getId()) {

                                branchCurrency.setId(total.getSaleCurrencyId().getId());
                                sb.append(" <tr> ");
                                if (selectedBranchList.size() > 1) {
                                    sb.append(" <td></td>  <td><b>").append(total.getBranchSetting().getBranch().getName()).append("</b></td>");
                                } else {
                                    sb.append(" <td></td>  <td><b>").append("").append("</b></td>");
                                }

                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getTransferQuantity())).append("</b></td>");
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getTransferAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getPurchaseQuantity())).append("</b></td>");
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getPurchaseAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getSalesQuantity())).append("</b></td>");
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getSalesAmount()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                                bd = total.getTransferQuantity().add(total.getPurchaseQuantity()).subtract(total.getSalesQuantity());
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd)).append("</b></td>");

                                bd = total.getTransferAmount().add(total.getPurchaseAmount()).subtract(total.getSalesAmount());
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(total.getCost()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");

                                if (total.getCost().compareTo(BigDecimal.ZERO) != 0 && total.getCost() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                } else {
                                    bd = BigDecimal.ZERO;
                                }
                                sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                                if (total.getSalesAmount().compareTo(BigDecimal.ZERO) != 0 && total.getSalesAmount() != null) {
                                    bd = ((total.getSalesAmount().subtract(total.getCost())).divide(total.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                                } else {
                                    bd = BigDecimal.ZERO;
                                }
                                sb.append("<td style=\"text-align: right\"><b>").append(bd).append("</b></td>");

                                bd = total.getSalesAmount().subtract(total.getPurchaseAmount());
                                sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(bd) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                                sb.append(" </tr> ");

                            }

                        }

                    } else {

                        for (Map.Entry<Integer, GeneralStation> entryAutomat : groupVendingMachineCalculated.entrySet()) {

                            branchCurrency.setId(entryAutomat.getValue().getSaleCurrencyId().getId());
                            sb.append(" <tr> ");
                            if (selectedBranchList.size() > 1) {
                                sb.append(" <td></td>  <td><b>").append(entryAutomat.getValue().getBranchSetting().getBranch().getName()).append("</b></td>");
                            } else {
                                sb.append(" <td></td>  <td><b>").append("").append("</b></td>");
                            }

                            sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entryAutomat.getValue().getSalesQuantity())).append("</b></td>");
                            sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entryAutomat.getValue().getWaste()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                            sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalIncome())).append("</b></td>");
                            sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalExpense()) + sessionBean.currencySignOrCode(branchCurrency.getId(), 0)).append("</b></td>");
                            sb.append("<td style=\"text-align: right\"><b>").append(sessionBean.getNumberFormat().format(entryAutomat.getValue().getTotalWinnings())).append("</b></td>");
                            sb.append(" </tr> ");

                        }

                    }

                }

            }

            sb.append(" </table> ");

        } catch (Exception e) {

        }

        return sb.toString();
    }

}
