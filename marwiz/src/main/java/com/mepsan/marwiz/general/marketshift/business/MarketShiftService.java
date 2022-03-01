/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:39:37
 */
package com.mepsan.marwiz.general.marketshift.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.dao.ErrorOfflinePos;
import com.mepsan.marwiz.general.marketshift.dao.IMarketShiftDao;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftDao;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPreview;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class MarketShiftService implements IMarketShiftService {

    @Autowired
    public IMarketShiftDao marketShiftDao;

    @Autowired
    private SessionBean sessionBean;

    private List<MarketShiftPreview> listOfSalesList, listOfCurrencyTotal, listOfCashDelivery, listOfCreditCardDelivery, listOfCreditDelivery, listOfStockGroupList,
            listOfAccountGroupList, listOfSafeTransfer;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarketShiftDao(IMarketShiftDao marketShiftDao) {
        this.marketShiftDao = marketShiftDao;
    }

    @Override
    public List<Shift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return marketShiftDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return marketShiftDao.count(where);
    }

    @Override
    public int create(Shift obj, Boolean isOfflineControl) {

        return marketShiftDao.create(obj, isOfflineControl);
    }

    @Override
    public int update(Shift obj) {
        return marketShiftDao.update(obj);
    }

    @Override
    public Shift controlHaveOpenShift() {
        return marketShiftDao.controlHaveOpenShift();
    }

    @Override
    public int delete(Shift shift) {
        return marketShiftDao.delete(shift);
    }

    @Override
    public int updateShift(Shift shift) {
        return marketShiftDao.updateShift(shift);
    }

    public CellStyle headerCss(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.index);

        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public void createPdfFile(Shift shift, String totalShiftAmount, List<String> selectedOptions) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        BigDecimal totalColumn1 = BigDecimal.ZERO, totalColumn2 = BigDecimal.ZERO, totalColumn3 = BigDecimal.ZERO, totalColumn4 = BigDecimal.ZERO, totalColumn5 = BigDecimal.ZERO;
        listOfSalesList = new ArrayList<>();
        listOfCurrencyTotal = new ArrayList<>();
        listOfCashDelivery = new ArrayList<>();
        listOfCreditCardDelivery = new ArrayList<>();
        listOfCreditDelivery = new ArrayList<>();
        listOfStockGroupList = new ArrayList<>();
        listOfAccountGroupList = new ArrayList<>();
        listOfSafeTransfer = new ArrayList<>();

        try {
            ////Satış Listesi//////
            connection = marketShiftDao.getDatasource().getConnection();
            prep = connection.prepareStatement(marketShiftDao.shiftStockDetail(shift));
            rs = prep.executeQuery();
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');

            ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            //Birim iiçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            Currency currencyBranch = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            formatterUnit.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            formatterUnit.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            PdfDocument pdfDocument = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true, true, true), 0);
            pdfDocument.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

            createCellStylePdf("headerDarkRedBold", pdfDocument, pdfDocument.getTableHeader());
            pdfDocument.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8, Font.BOLD, new Color(113, 0, 0)));

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftsummary"), pdfDocument.getFontHeader()));

            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + (shift.getShiftNo() == null ? "" : shift.getShiftNo()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftname") + " : " + (shift.getName() == null ? "" : shift.getName()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), shift.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), shift.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("countofremovedstock") + " : " + StaticMethods.round(shift.getSumOfRemovedStock(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalsalesamount") + " : " + totalShiftAmount, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            if (selectedOptions.contains("1")) {
                /*Satış Listesi  */
                PdfDocument pdfDocumentSales = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true, true, true, true), 0);
                pdfDocumentSales.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                pdfDocumentSales.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("saleslist"), pdfDocument.getFontHeader()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getHeader());

                pdfDocumentSales.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                pdfDocumentSales.getHeader().setPhrase(new Phrase("", pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getHeader());

                createCellStylePdf("headerDarkRed", pdfDocumentSales, pdfDocumentSales.getTableHeader());
                pdfDocumentSales.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockbarcode"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockname"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnamount"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnprice"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("taxrate"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totaltaxprice"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                pdfDocumentSales.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("unitprice"), pdfDocumentSales.getFontColumnTitle()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getTableHeader());

                while (rs.next()) {

                    pdfDocumentSales.getDataCell().setPhrase(new Phrase((rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getDataCell());

                    pdfDocumentSales.getDataCell().setPhrase(new Phrase((rs.getString("stckname") == null ? "" : rs.getString("stckname")).length() > 15 ? rs.getString("stckname").substring(0, 15) : rs.getString("stckname"), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getDataCell());

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalecount")) + rs.getString("guntsortname"), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturncount")) + rs.getString("guntsortname"), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    Currency currency = new Currency(rs.getInt("currency_id"));
                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalemoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturnmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    String taxSalesList = "";
                    if (sessionBean.getUser().getLanguage().getId() == 1) {
                        taxSalesList = "%" + formatterUnit.format(rs.getBigDecimal("slitaxrate"));
                    } else {
                        taxSalesList = formatterUnit.format(rs.getBigDecimal("slitaxrate")) + "%";
                    }

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(taxSalesList, pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("sliunitprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocumentSales.getFont()));
                    pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                    totalColumn3 = totalColumn3.add(rs.getBigDecimal("totalsalemoney"));
                    totalColumn4 = totalColumn4.add(rs.getBigDecimal("totalreturnmoney"));
                    totalColumn5 = totalColumn5.add(rs.getBigDecimal("totaltax"));

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.getStock().setId(rs.getInt("stckid"));
                    mp.getStock().setName(rs.getString("stckname"));
                    mp.getStock().setBarcode(rs.getString("stckbarcode"));
                    mp.getStock().getUnit().setId(rs.getInt("guntunitrounding"));
                    mp.getStock().getUnit().setSortName(rs.getString("guntsortname"));
                    mp.setSaleAmount(rs.getBigDecimal("totalsalecount"));
                    mp.setReturnAmount(rs.getBigDecimal("totalreturncount"));
                    mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
                    mp.setReturnPrice(rs.getBigDecimal("totalreturnmoney"));
                    mp.setTaxRate(rs.getBigDecimal("slitaxrate"));
                    mp.setTotalTaxPrice(rs.getBigDecimal("totaltax"));
                    mp.setUnitPrice(rs.getBigDecimal("sliunitprice"));
                    mp.getCurrency().setId(rs.getInt("currency_id"));
                    listOfSalesList.add(mp);

                }
                //Alt Toplam Satış Listesi
                StaticMethods.createCellStylePdf("footer", pdfDocumentSales, pdfDocumentSales.getRightCell());
                pdfDocumentSales.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(9);

                pdfDocumentSales.getRightCell().setPhrase(new Phrase("", pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(clcSubTotalSalesListUnit(false), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(clcSubTotalSalesListUnit(true), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn3) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn4) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase("", pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn5) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocumentSales.getRightCell().setPhrase(new Phrase("", pdfDocumentSales.getFont()));
                pdfDocumentSales.getPdfTable().addCell(pdfDocumentSales.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentSales.getPdfTable());
            }
            if (selectedOptions.contains("2")) {

                /* Satış/İade Kategori Toplam Dökümü  */
//                prep = connection.prepareStatement(marketShiftDao.shiftStockGroupDetail(shift));
//                rs = prep.executeQuery();
                List<MarketShiftPreview> resultList = marketShiftDao.shiftStockGroupDetail(shift);
                PdfDocument pdfDocumentCategory = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true, true, true, true, true, true), 0);
                pdfDocumentCategory.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                pdfDocumentCategory.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                pdfDocumentCategory.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesreturncategorytotaldump"), pdfDocument.getFontHeader()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getHeader());

                pdfDocumentCategory.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                pdfDocumentCategory.getHeader().setPhrase(new Phrase("", pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getHeader());

                createCellStylePdf("headerDarkRed", pdfDocumentCategory, pdfDocumentCategory.getTableHeader());
                pdfDocumentCategory.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("category"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("previoussaleamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("previoussaleprice"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("previousamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("previousprice"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("entryamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exitamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamount"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                pdfDocumentCategory.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("remainingamountt"), pdfDocumentCategory.getFontColumnTitle()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getTableHeader());

                for (MarketShiftPreview obj : resultList) {

                    pdfDocumentCategory.getDataCell().setPhrase(new Phrase(obj.getDescription(), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getDataCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getPreviousSaleAmount()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    Currency currencyCategory = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());
                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getPreviousSalePrice()) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getPreviousAmount()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getPreviousPrice()) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getTotalOfInComing()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getTotalOfOutGoing()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getSaleAmount()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getSalePrice()) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getRemainingQuantity()), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getRemainingPrice()) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    listOfStockGroupList.add(obj);

                }
                prep = connection.prepareStatement(marketShiftDao.shiftStockGroupDetailWithoutCategories(shift));
                rs = prep.executeQuery();

                while (rs.next()) {
                    pdfDocumentCategory.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stockswithoutcategory"), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getDataCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previoussalequantity")), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    Currency currencyCategory = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());
                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previoussaletotal")) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previousamountbeforeshift")), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previouspricebeforeshift")) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("girismiktar")), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("cikismiktar")), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("salesquantity")), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("salestotal")) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("previousamountbeforeshift").add(rs.getBigDecimal("girismiktar")).subtract(rs.getBigDecimal("cikismiktar"))), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    pdfDocumentCategory.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("remainingPrice").add(rs.getBigDecimal("previouspricebeforeshift"))) + sessionBean.currencySignOrCode(currencyCategory.getId(), 0), pdfDocumentCategory.getFont()));
                    pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.setPreviousPrice(rs.getBigDecimal("previouspricebeforeshift"));
                    mp.setPreviousAmount(rs.getBigDecimal("previousamountbeforeshift"));
                    mp.setTotalOfInComing(rs.getBigDecimal("girismiktar"));
                    mp.setTotalOfOutGoing(rs.getBigDecimal("cikismiktar"));
                    mp.setSalePrice(rs.getBigDecimal("salestotal"));
                    mp.setSaleAmount(rs.getBigDecimal("salesquantity"));
                    mp.setDescription(sessionBean.getLoc().getString("stockswithoutcategory"));
                    mp.setRemainingQuantity(rs.getBigDecimal("previousamountbeforeshift").add(rs.getBigDecimal("girismiktar")).subtract(rs.getBigDecimal("cikismiktar")));
                    mp.setRemainingPrice(rs.getBigDecimal("remainingPrice").add(rs.getBigDecimal("previouspricebeforeshift")));
                    mp.setPreviousSaleAmount(rs.getBigDecimal("previoussalequantity"));
                    mp.setPreviousSalePrice(rs.getBigDecimal("previoussaletotal"));
                    listOfStockGroupList.add(mp);
                }

                //Alt Toplam Kategori Toplam Dökümü
                StaticMethods.createCellStylePdf("footer", pdfDocumentCategory, pdfDocumentCategory.getRightCell());
                pdfDocumentCategory.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(11);

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(1), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(2), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(3), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(4), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase("", pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase("", pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(5), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(6), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(7), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocumentCategory.getRightCell().setPhrase(new Phrase(clcSubTotalSalesCategoryListUnit(8), pdfDocumentCategory.getFont()));
                pdfDocumentCategory.getPdfTable().addCell(pdfDocumentCategory.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentCategory.getPdfTable());
            }

            if (selectedOptions.contains("3")) {

                List<MarketShiftPreview> resultList = marketShiftDao.shiftAccountGroupList(shift);

                PdfDocument pdfDocumentForeignCurrency = StaticMethods.preparePdf(Arrays.asList(true, true), 0);
                pdfDocumentForeignCurrency.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                pdfDocumentForeignCurrency.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("accounttotaldump"), pdfDocument.getFontHeader()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                pdfDocumentForeignCurrency.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase("", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                createCellStylePdf("headerDarkRed", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getTableHeader());
                pdfDocumentForeignCurrency.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("account"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprice"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                for (MarketShiftPreview obj : resultList) {

                    pdfDocumentForeignCurrency.getDataCell().setPhrase(new Phrase((obj.getAccount().getName() == null ? "" : obj.getAccount().getName()), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getDataCell());

                    pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getSalePrice()) + sessionBean.currencySignOrCode(obj.getCurrency().getId(), 0), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                    listOfAccountGroupList.add(obj);

                }

                //Alt Toplam Cari Toplam Dökümü
                StaticMethods.createCellStylePdf("footer", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getRightCell());
                pdfDocumentForeignCurrency.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(2);
                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(clcSubTotalAccountGrouplist(), pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentForeignCurrency.getPdfTable());

            }

            if (selectedOptions.contains("4")) {

                /*Para Birimi */
                prep = connection.prepareStatement(marketShiftDao.shiftCurrencyDetail(shift));
                rs = prep.executeQuery();

                PdfDocument pdfDocumentForeignCurrency = StaticMethods.preparePdf(Arrays.asList(true, true), 0);
                pdfDocumentForeignCurrency.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                pdfDocumentForeignCurrency.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("foreigncurrencytotals"), pdfDocument.getFontHeader()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                pdfDocumentForeignCurrency.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase("", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                createCellStylePdf("headerDarkRed", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getTableHeader());
                pdfDocumentForeignCurrency.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("currency"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprice"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                while (rs.next()) {

                    pdfDocumentForeignCurrency.getDataCell().setPhrase(new Phrase((rs.getString("crdname") == null ? "" : rs.getString("crdname")), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getDataCell());

                    Currency currency2 = new Currency(rs.getInt("slcurrency_id"));
                    pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalemoney")) + sessionBean.currencySignOrCode(currency2.getId(), 0), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.getCurrency().setId(rs.getInt("slcurrency_id"));
                    mp.getCurrency().setTag(rs.getString("crdname"));
                    mp.getCurrency().setCode(rs.getString("crcode"));
                    mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
                    listOfCurrencyTotal.add(mp);

                }

                //Alt Toplam Dövizli Toplamlar
                StaticMethods.createCellStylePdf("footer", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getRightCell());
                pdfDocumentForeignCurrency.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(2);
                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(clcSubTotalCurrencyTotal(), pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentForeignCurrency.getPdfTable());

            }
            if (selectedOptions.contains("5")) {
                /*KDV  Gruplu*/
                prep = connection.prepareStatement(marketShiftDao.shiftTaxRateDetail(shift));
                rs = prep.executeQuery();

                PdfDocument kdvGroup = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true), 0);
                kdvGroup.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                kdvGroup.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                kdvGroup.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("kdv") + " " + sessionBean.getLoc().getString("totalmoney"), pdfDocument.getFontHeader()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getHeader());

                kdvGroup.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                kdvGroup.getHeader().setPhrase(new Phrase("", kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getHeader());

                createCellStylePdf("headerDarkRed", kdvGroup, kdvGroup.getTableHeader());
                kdvGroup.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("kdv") + " %", kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());

                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesamount"), kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());

                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnamount"), kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());
                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesprice"), kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());

                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("returnprice"), kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());

                kdvGroup.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totaltaxprice"), kdvGroup.getFontColumnTitle()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getTableHeader());
                totalColumn1 = totalColumn2 = totalColumn3 = totalColumn4 = totalColumn5 = BigDecimal.ZERO;
                while (rs.next()) {

                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getInt("slitaxrate")), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    Currency currency = new Currency(rs.getInt("currency_id"));
                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalecount")), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturncount")), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalemoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturnmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), kdvGroup.getFont()));
                    kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("totalsalecount"));
                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("totalreturncount"));
                    totalColumn3 = totalColumn3.add(rs.getBigDecimal("totalsalemoney"));
                    totalColumn4 = totalColumn4.add(rs.getBigDecimal("totalreturnmoney"));
                    totalColumn5 = totalColumn5.add(rs.getBigDecimal("totaltax"));

                }

                //Alt Toplam Kdv Gruplu
                StaticMethods.createCellStylePdf("footer", kdvGroup, kdvGroup.getRightCell());
                kdvGroup.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(5);

                kdvGroup.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn1), kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn2), kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn3) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn4) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                kdvGroup.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn5) + sessionBean.currencySignOrCode(currencyBranch.getId(), 0), kdvGroup.getFont()));
                kdvGroup.getPdfTable().addCell(kdvGroup.getRightCell());

                pdfDocument.getDocument().add(kdvGroup.getPdfTable());

            }
            if (selectedOptions.contains("6")) {
                /*Açık Fazla Peronel Borç Alacak*/
                prep = connection.prepareStatement(marketShiftDao.shiftDeficitGiveMoneyEmployee(shift));
                rs = prep.executeQuery();

                PdfDocument deficitEmployee = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);
                deficitEmployee.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                deficitEmployee.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                deficitEmployee.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("deficit") + "-" + sessionBean.getLoc().getString("surplus"), pdfDocument.getFontHeader()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getHeader());

                deficitEmployee.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                deficitEmployee.getHeader().setPhrase(new Phrase("", deficitEmployee.getFont()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getHeader());

                createCellStylePdf("headerDarkRed", deficitEmployee, deficitEmployee.getTableHeader());
                deficitEmployee.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 6, 0, new Color(113, 0, 0)));

                deficitEmployee.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("employee"), deficitEmployee.getFontColumnTitle()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getTableHeader());

                deficitEmployee.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incoming1"), deficitEmployee.getFontColumnTitle()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getTableHeader());

                deficitEmployee.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("outgoing"), deficitEmployee.getFontColumnTitle()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getTableHeader());
                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {

                    deficitEmployee.getDataCell().setPhrase(new Phrase(rs.getString("accountname") + " " + rs.getString("accountsurname"), deficitEmployee.getFont()));
                    deficitEmployee.getPdfTable().addCell(deficitEmployee.getDataCell());

                    deficitEmployee.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("fazla").doubleValue()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitEmployee.getFont()));
                    deficitEmployee.getPdfTable().addCell(deficitEmployee.getRightCell());

                    deficitEmployee.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("borc").doubleValue()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitEmployee.getFont()));
                    deficitEmployee.getPdfTable().addCell(deficitEmployee.getRightCell());

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("fazla"));
                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("borc"));
                }

                //Alt Toplam Açık Fazla Personel
                StaticMethods.createCellStylePdf("footer", deficitEmployee, deficitEmployee.getRightCell());
                deficitEmployee.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(3);

                deficitEmployee.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", deficitEmployee.getFont()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getRightCell());

                deficitEmployee.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn1) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitEmployee.getFont()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getRightCell());

                deficitEmployee.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitEmployee.getFont()));
                deficitEmployee.getPdfTable().addCell(deficitEmployee.getRightCell());

                pdfDocument.getDocument().add(deficitEmployee.getPdfTable());
            }
            if (selectedOptions.contains("7")) {
                /*Açık Fazla Gelir Gider*/
                prep = connection.prepareStatement(marketShiftDao.shiftDeficitGiveMoney(shift));
                rs = prep.executeQuery();

                PdfDocument deficitIncomeExpense = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);
                deficitIncomeExpense.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                deficitIncomeExpense.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                deficitIncomeExpense.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("deficit") + "-" + sessionBean.getLoc().getString("surplus"), pdfDocument.getFontHeader()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getHeader());

                deficitIncomeExpense.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                deficitIncomeExpense.getHeader().setPhrase(new Phrase("", deficitIncomeExpense.getFont()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getHeader());

                createCellStylePdf("headerDarkRed", deficitIncomeExpense, deficitIncomeExpense.getTableHeader());
                deficitIncomeExpense.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                deficitIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("income") + " - " + sessionBean.getLoc().getString("expense"), deficitIncomeExpense.getFontColumnTitle()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getTableHeader());

                deficitIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incoming1"), deficitIncomeExpense.getFontColumnTitle()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getTableHeader());

                deficitIncomeExpense.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("outgoing"), deficitIncomeExpense.getFontColumnTitle()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getTableHeader());
                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {

                    deficitIncomeExpense.getDataCell().setPhrase(new Phrase(rs.getString("name"), deficitIncomeExpense.getFont()));
                    deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getDataCell());

                    deficitIncomeExpense.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("income").doubleValue()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitIncomeExpense.getFont()));
                    deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getRightCell());

                    deficitIncomeExpense.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("expense").doubleValue()) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitIncomeExpense.getFont()));
                    deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getRightCell());

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("income"));
                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("expense"));

                }

                //Alt Toplam Açık Fazla Gelir Gider
                StaticMethods.createCellStylePdf("footer", deficitIncomeExpense, deficitIncomeExpense.getRightCell());
                deficitIncomeExpense.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(3);

                deficitIncomeExpense.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", deficitIncomeExpense.getFont()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getRightCell());

                deficitIncomeExpense.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn1) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitIncomeExpense.getFont()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getRightCell());

                deficitIncomeExpense.getRightCell().setPhrase(new Phrase(formatterUnit.format(totalColumn2) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), deficitIncomeExpense.getFont()));
                deficitIncomeExpense.getPdfTable().addCell(deficitIncomeExpense.getRightCell());

                pdfDocument.getDocument().add(deficitIncomeExpense.getPdfTable());
            }
            if (selectedOptions.contains("8")) {
                /*Personel Nakit Teslimat*/
                prep = connection.prepareStatement(marketShiftDao.shiftCashierPaymentCashDetail(shift));
                rs = prep.executeQuery();

                PdfDocument cashierPaymentCashDetail = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);
                cashierPaymentCashDetail.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                cashierPaymentCashDetail.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                cashierPaymentCashDetail.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("cashdelivery"), pdfDocument.getFontHeader()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getHeader());

                cashierPaymentCashDetail.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra
                cashierPaymentCashDetail.getHeader().setPhrase(new Phrase("", cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getHeader());

                createCellStylePdf("headerDarkRed", cashierPaymentCashDetail, cashierPaymentCashDetail.getTableHeader());
                cashierPaymentCashDetail.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                cashierPaymentCashDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("employee"), cashierPaymentCashDetail.getFontColumnTitle()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getTableHeader());

                cashierPaymentCashDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalmoney"), cashierPaymentCashDetail.getFontColumnTitle()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getTableHeader());

                cashierPaymentCashDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("casecodename"), cashierPaymentCashDetail.getFontColumnTitle()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getTableHeader());

                cashierPaymentCashDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangerate"), cashierPaymentCashDetail.getFontColumnTitle()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getTableHeader());

                cashierPaymentCashDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangeprice"), cashierPaymentCashDetail.getFontColumnTitle()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getTableHeader());

                while (rs.next()) {

                    cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(rs.getString("accname") + " " + rs.getString("acctitle"), cashierPaymentCashDetail.getFont()));
                    cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                    Currency currency = new Currency(rs.getInt("shpcurrency_id"));
                    cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("accualprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCashDetail.getFont()));
                    cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                    cashierPaymentCashDetail.getDataCell().setPhrase(new Phrase(rs.getString("sfcode") + "-" + rs.getString("sfname"), cashierPaymentCashDetail.getFont()));
                    cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getDataCell());

                    cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("shpexchangerate")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCashDetail.getFont()));
                    cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                    cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("shpaccualprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCashDetail.getFont()));
                    cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.setId(rs.getInt("shpid"));
                    mp.getAccount().setId(rs.getInt("shpaccount_id"));
                    mp.getAccount().setName(rs.getString("accname"));
                    mp.getAccount().setTitle(rs.getString("accname") + " " + rs.getString("acctitle"));
                    mp.setSalePrice(rs.getBigDecimal("accualprice"));
                    mp.setExchangePrice(rs.getBigDecimal("shpaccualprice"));
                    mp.getSafe().setId(rs.getInt("shpsafe_id"));
                    mp.getSafe().setName(rs.getString("sfname"));
                    mp.getSafe().setCode(rs.getString("sfcode"));
                    mp.getSafe().getCurrency().setId(rs.getInt("shpcurrency_id"));
                    mp.getSafe().getCurrency().setCode(rs.getString("crcode"));
                    mp.setExchangeRate(rs.getBigDecimal("shpexchangerate"));
                    listOfCashDelivery.add(mp);

                }

                //Alt Toplam Nakit Teslimat
                StaticMethods.createCellStylePdf("footer", cashierPaymentCashDetail, cashierPaymentCashDetail.getRightCell());
                cashierPaymentCashDetail.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(5);
                cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(1, false), cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase("", cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase("", cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                cashierPaymentCashDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(1, true), cashierPaymentCashDetail.getFont()));
                cashierPaymentCashDetail.getPdfTable().addCell(cashierPaymentCashDetail.getRightCell());

                pdfDocument.getDocument().add(cashierPaymentCashDetail.getPdfTable());

            }
            if (selectedOptions.contains("9")) {
                /*Personel Kredi Kartı*/
                prep = connection.prepareStatement(marketShiftDao.shiftCashierPaymentBankDetail(shift));
                rs = prep.executeQuery();

                PdfDocument cashierPaymentCreditCardDetail = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true), 0);
                cashierPaymentCreditCardDetail.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                cashierPaymentCreditCardDetail.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                cashierPaymentCreditCardDetail.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("creditcarddelivery"), pdfDocument.getFontHeader()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getHeader());

                cashierPaymentCreditCardDetail.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra
                cashierPaymentCreditCardDetail.getHeader().setPhrase(new Phrase("", cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getHeader());

                createCellStylePdf("headerDarkRed", cashierPaymentCreditCardDetail, cashierPaymentCreditCardDetail.getTableHeader());
                cashierPaymentCreditCardDetail.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getTableHeader());

                cashierPaymentCreditCardDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalmoney"), cashierPaymentCreditCardDetail.getFontColumnTitle()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getTableHeader());

                cashierPaymentCreditCardDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("bankcodename"), cashierPaymentCreditCardDetail.getFontColumnTitle()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getTableHeader());

                cashierPaymentCreditCardDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangerate"), cashierPaymentCreditCardDetail.getFontColumnTitle()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getTableHeader());

                cashierPaymentCreditCardDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangeprice"), cashierPaymentCreditCardDetail.getFontColumnTitle()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getTableHeader());

                while (rs.next()) {

                    cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(rs.getString("accname") + " " + rs.getString("acctitle"), cashierPaymentCreditCardDetail.getFont()));
                    cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                    Currency currency = new Currency(rs.getInt("shpcurrency_id"));
                    cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("accualprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditCardDetail.getFont()));
                    cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                    cashierPaymentCreditCardDetail.getDataCell().setPhrase(new Phrase(rs.getString("bnkcode") + "-" + rs.getString("baname"), cashierPaymentCreditCardDetail.getFont()));
                    cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getDataCell());

                    cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("shpexchangerate")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditCardDetail.getFont()));
                    cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                    cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("shpaccualprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditCardDetail.getFont()));
                    cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.setId(rs.getInt("shpid"));
                    mp.getAccount().setId(rs.getInt("shpaccount_id"));
                    mp.getAccount().setName(rs.getString("accname"));
                    mp.getAccount().setTitle(rs.getString("accname") + " " + rs.getString("acctitle"));
                    mp.setSalePrice(rs.getBigDecimal("accualprice"));
                    mp.setExchangePrice(rs.getBigDecimal("shpaccualprice"));
                    mp.getBankAccount().setId(rs.getInt("shpbankaccount_id"));
                    mp.getBankAccount().setName(rs.getString("baname"));
                    mp.getBankAccount().getBankBranch().getBank().setName(rs.getString("bnkname"));
                    mp.getBankAccount().getBankBranch().getBank().setCode(rs.getString("bnkcode"));
                    mp.getBankAccount().getCurrency().setId(rs.getInt("shpcurrency_id"));
                    mp.getBankAccount().getCurrency().setCode(rs.getString("crcode"));
                    mp.setExchangeRate(rs.getBigDecimal("shpexchangerate"));
                    listOfCreditCardDelivery.add(mp);

                }

                //Alt Toplam Kredi Kartı Teslimatı
                StaticMethods.createCellStylePdf("footer", cashierPaymentCreditCardDetail, cashierPaymentCreditCardDetail.getRightCell());
                cashierPaymentCreditCardDetail.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(5);
                cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(2, false), cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase("", cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase("", cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                cashierPaymentCreditCardDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(2, true), cashierPaymentCreditCardDetail.getFont()));
                cashierPaymentCreditCardDetail.getPdfTable().addCell(cashierPaymentCreditCardDetail.getRightCell());

                pdfDocument.getDocument().add(cashierPaymentCreditCardDetail.getPdfTable());

            }
            if (selectedOptions.contains("10")) {
                /*Personel Veresiye Teslimatı*/
                prep = connection.prepareStatement(marketShiftDao.shiftCreditPaymentDetail(shift));
                rs = prep.executeQuery();

                PdfDocument cashierPaymentCreditDetail = StaticMethods.preparePdf(Arrays.asList(true, true, true, true), 0);
                cashierPaymentCreditDetail.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                cashierPaymentCreditDetail.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                cashierPaymentCreditDetail.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("creditdelivery"), pdfDocument.getFontHeader()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getHeader());

                cashierPaymentCreditDetail.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra
                cashierPaymentCreditDetail.getHeader().setPhrase(new Phrase("", cashierPaymentCreditDetail.getFont()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getHeader());

                createCellStylePdf("headerDarkRed", cashierPaymentCreditDetail, cashierPaymentCreditDetail.getTableHeader());
                cashierPaymentCreditDetail.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                cashierPaymentCreditDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("account"), cashierPaymentCreditDetail.getFontColumnTitle()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getTableHeader());

                cashierPaymentCreditDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalmoney"), cashierPaymentCreditDetail.getFontColumnTitle()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getTableHeader());

                cashierPaymentCreditDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangerate"), cashierPaymentCreditDetail.getFontColumnTitle()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getTableHeader());

                cashierPaymentCreditDetail.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("exchangeprice"), cashierPaymentCreditDetail.getFontColumnTitle()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getTableHeader());

                while (rs.next()) {

                    cashierPaymentCreditDetail.getDataCell().setPhrase(new Phrase(rs.getString("name"), cashierPaymentCreditDetail.getFont()));
                    cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getDataCell());

                    Currency currency = new Currency(rs.getInt("crdcurrency_id"));
                    cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("price")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditDetail.getFont()));
                    cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                    cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("exchangerate")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditDetail.getFont()));
                    cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                    cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), cashierPaymentCreditDetail.getFont()));
                    cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                    MarketShiftPreview mp = new MarketShiftPreview();
                    mp.getAccount().setId(rs.getInt("id"));
                    mp.getAccount().setTitle(rs.getString("name"));
                    mp.setSalePrice(rs.getBigDecimal("price"));
                    mp.setExchangePrice(rs.getBigDecimal("totalprice"));
                    mp.setExchangeRate(rs.getBigDecimal("exchangerate"));
                    mp.getCurrency().setId(rs.getInt("crdcurrency_id"));
                    mp.getCurrency().setCode(rs.getString("crcode"));
                    listOfCreditDelivery.add(mp);

                }
                //Alt Toplam Veresiye Teslimatı
                StaticMethods.createCellStylePdf("footer", cashierPaymentCreditDetail, cashierPaymentCreditDetail.getRightCell());
                cashierPaymentCreditDetail.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(4);
                cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", cashierPaymentCreditDetail.getFont()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(3, false), cashierPaymentCreditDetail.getFont()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase("", cashierPaymentCreditDetail.getFont()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                cashierPaymentCreditDetail.getRightCell().setPhrase(new Phrase(clcCashierPaymentTotal(3, true), cashierPaymentCreditDetail.getFont()));
                cashierPaymentCreditDetail.getPdfTable().addCell(cashierPaymentCreditDetail.getRightCell());

                pdfDocument.getDocument().add(cashierPaymentCreditDetail.getPdfTable());
            }
            //Kasa Devirleri
            if (selectedOptions.contains("11")) {
                List< MarketShiftPreview> resultList = marketShiftDao.shiftSafeTransferList(shift);

                PdfDocument pdfDocumentForeignCurrency = StaticMethods.preparePdf(Arrays.asList(true, true), 0);
                pdfDocumentForeignCurrency.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                pdfDocumentForeignCurrency.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safetransfers"), pdfDocument.getFontHeader()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                pdfDocumentForeignCurrency.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra

                pdfDocumentForeignCurrency.getHeader().setPhrase(new Phrase("", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getHeader());

                createCellStylePdf("headerDarkRed", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getTableHeader());
                pdfDocumentForeignCurrency.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safe"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                pdfDocumentForeignCurrency.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("total"), pdfDocumentForeignCurrency.getFontColumnTitle()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getTableHeader());

                for (MarketShiftPreview obj : resultList) {

                    pdfDocumentForeignCurrency.getDataCell().setPhrase(new Phrase((obj.getSafe().getName() == null ? "" : obj.getSafe().getName()), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getDataCell());

                    pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(formatterUnit.format(obj.getSafe().getBalance()) + sessionBean.currencySignOrCode(obj.getSafe().getCurrency().getId(), 0), pdfDocumentForeignCurrency.getFont()));
                    pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                    listOfSafeTransfer.add(obj);

                }

                //Alt Toplam Cari Toplam Dökümü
                StaticMethods.createCellStylePdf("footer", pdfDocumentForeignCurrency, pdfDocumentForeignCurrency.getRightCell());
                pdfDocumentForeignCurrency.getRightCell().setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                pdfDocument.getRightCell().setColspan(2);
                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + ":", pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocumentForeignCurrency.getRightCell().setPhrase(new Phrase(clcSubTotalSafeTransferlist(), pdfDocumentForeignCurrency.getFont()));
                pdfDocumentForeignCurrency.getPdfTable().addCell(pdfDocumentForeignCurrency.getRightCell());

                pdfDocument.getDocument().add(pdfDocumentForeignCurrency.getPdfTable());

            }

            if (selectedOptions.contains("12")) {
                /*Vardiya Genel*/
                prep = connection.prepareStatement(marketShiftDao.shiftGeneral(shift));
                rs = prep.executeQuery();

                PdfDocument shiftGeneral = StaticMethods.preparePdf(Arrays.asList(true, true, true, true, true, true, true), 0);
                shiftGeneral.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                shiftGeneral.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                shiftGeneral.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftgeneral"), pdfDocument.getFontHeader()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getHeader());

                shiftGeneral.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra
                shiftGeneral.getHeader().setPhrase(new Phrase("", shiftGeneral.getFont()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getHeader());

                createCellStylePdf("headerDarkRed", shiftGeneral, shiftGeneral.getTableHeader());
                shiftGeneral.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("sale"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("cash"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("creditcard"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("credit1"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("open"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                shiftGeneral.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("return"), shiftGeneral.getFontColumnTitle()));
                shiftGeneral.getPdfTable().addCell(shiftGeneral.getTableHeader());

                while (rs.next()) {

                    shiftGeneral.getDataCell().setPhrase(new Phrase(rs.getString("name"), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getDataCell());

                    Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalemoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("nakit")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("banka")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("veresiye")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("acık")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                    shiftGeneral.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturnmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftGeneral.getFont()));
                    shiftGeneral.getPdfTable().addCell(shiftGeneral.getRightCell());

                }
                pdfDocument.getDocument().add(shiftGeneral.getPdfTable());
            }
            if (selectedOptions.contains("13")) {
                /*Vardiya Özet*/
                prep = connection.prepareStatement(marketShiftDao.shiftSummary(shift));
                rs = prep.executeQuery();

                PdfDocument shiftSummary = StaticMethods.preparePdf(Arrays.asList(true, true, true), 0);
                shiftSummary.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));
                shiftSummary.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5));

                shiftSummary.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftsummary") + ":", pdfDocument.getFontHeader()));
                shiftSummary.getPdfTable().addCell(shiftSummary.getHeader());

                shiftSummary.getPdfTable().setSpacingAfter(15f);//Boşluk Bırak  Pdf Oluşturulduktan Sonra
                shiftSummary.getHeader().setPhrase(new Phrase("", shiftSummary.getFont()));
                shiftSummary.getPdfTable().addCell(shiftSummary.getHeader());

                System.out.println("shiftSummary.getFont()=" + shiftSummary.getFont());

                createCellStylePdf("headerDarkRed", shiftSummary, shiftSummary.getTableHeader());
                shiftSummary.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5, 0, new Color(113, 0, 0)));

                shiftSummary.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), shiftSummary.getFontColumnTitle()));
                shiftSummary.getPdfTable().addCell(shiftSummary.getTableHeader());

                shiftSummary.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("incoming"), shiftSummary.getFontColumnTitle()));
                shiftSummary.getPdfTable().addCell(shiftSummary.getTableHeader());

                shiftSummary.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("outgoing"), shiftSummary.getFontColumnTitle()));
                shiftSummary.getPdfTable().addCell(shiftSummary.getTableHeader());

                while (rs.next()) {
                    Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

                    //////Satış İade Tutarı
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("salesreturnprice"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalsalemoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("totalreturnmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Nakit Toplamı
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofcash"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("nakittahsilat")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Kredi Kartı Teslimat
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofpos"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("bankatahsilat")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Veresiye Satış
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofpostpaid"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("veresiye")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Açık Satış
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofopen"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("acık")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Gelir
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofincome"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("gelir")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Gider
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofexpense"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(0) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("gider")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Personel Toplamı
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalofemployee"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("employeeincome")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("employeeexpense")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Ara Toplam
                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("subtotal"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("girentoplam")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("cıkantoplam")) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    //Fark Toplamı
                    BigDecimal girentoplam = StaticMethods.round(rs.getBigDecimal("girentoplam"), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                    BigDecimal cıkantoplam = StaticMethods.round(rs.getBigDecimal("cıkantoplam"), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
                    BigDecimal diffin = BigDecimal.ZERO;
                    BigDecimal diffout = BigDecimal.ZERO;
                    if (girentoplam.compareTo(cıkantoplam) > 0) {
                        diffin = BigDecimal.ZERO;
                        diffout = girentoplam.subtract(cıkantoplam);
                    } else if (cıkantoplam.compareTo(girentoplam) > 0) {
                        diffout = BigDecimal.ZERO;
                        diffin = cıkantoplam.subtract(girentoplam);
                    }

                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("difference"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(diffin) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(diffout) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    ////////////////////////Genel Toplam ////////////////
                    BigDecimal overralIn = diffin.add(rs.getBigDecimal("girentoplam"));
                    BigDecimal overralOut = diffout.add(rs.getBigDecimal("cıkantoplam"));

                    shiftSummary.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("overalltotal"), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getDataCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(overralIn) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    shiftSummary.getRightCell().setPhrase(new Phrase(formatterUnit.format(overralOut) + sessionBean.currencySignOrCode(currency.getId(), 0), shiftSummary.getFont()));
                    shiftSummary.getPdfTable().addCell(shiftSummary.getRightCell());

                    pdfDocument.getDocument().add(shiftSummary.getPdfTable());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("shiftsummary"));

        } catch (DocumentException | SQLException e) {
            System.out.println("Ex=" + e.toString());
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
                Logger.getLogger(MarketShiftDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public NumberFormat unitNumberFormat(int currencyRounding) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(currencyRounding);
        formatter.setMinimumFractionDigits(currencyRounding);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

    /**
     * Satış listesi Tablosu İçin Alt Toplam Hesaplaması Yapar.
     *
     * @return
     */
    public String clcSubTotalSalesListUnit(boolean isRetunCount) {

        String total = "";
        HashMap<Integer, Unit> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfSalesList) {
            hm.put(listOfObject.getStock().getUnit().getId(), listOfObject.getStock().getUnit());
        }

        for (Map.Entry<Integer, Unit> entry : hm.entrySet()) {
            Unit value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfSalesList) {
                if (listOfObject.getStock().getUnit().getId() == entry.getKey()) {
                    if (isRetunCount) {
                        totalValue = totalValue.add(listOfObject.getReturnAmount());
                    } else {
                        totalValue = totalValue.add(listOfObject.getSaleAmount());
                    }

                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getSortName() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Satış İade Kategori Toplam Dökümü Tablosunda Toplam Alt Toplam
     * Hesaplamalarını Yapar
     *
     * @param columnId
     * @return
     */
    public String clcSubTotalSalesCategoryListUnit(int columnId) {
        BigDecimal total = BigDecimal.ZERO;
        String currency = "";
        String result = "";
        for (MarketShiftPreview marketShiftPreview : listOfStockGroupList) {
            switch (columnId) {
                case 1:
                    total = total.add(marketShiftPreview.getPreviousSaleAmount());
                    break;
                case 2:
                    total = total.add(marketShiftPreview.getPreviousSalePrice());
                    break;
                case 3:
                    total = total.add(marketShiftPreview.getPreviousAmount());
                    break;
                case 4:
                    total = total.add(marketShiftPreview.getPreviousPrice());
                    break;
                case 5:
                    total = total.add(marketShiftPreview.getSaleAmount());
                    break;
                case 6:
                    total = total.add(marketShiftPreview.getSalePrice());
                    break;

                case 7:
                    total = total.add(marketShiftPreview.getRemainingQuantity());
                    break;
                case 8:
                    total = total.add(marketShiftPreview.getRemainingPrice());
                    break;
                default:
                    total = total.add(BigDecimal.ZERO);
                    break;
            }
        }
        if (columnId == 2 || columnId == 4) {
            currency = sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        }

        result = String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(total)) + " " + currency;
        return result;
    }

    /**
     * Dövizli Toplamlar Alt Toplam Hesaplaması Yapar.
     *
     * @return
     */
    public String clcSubTotalCurrencyTotal() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfCurrencyTotal) {
            hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfCurrencyTotal) {
                if (listOfObject.getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSalePrice());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Cari Toplam Tablosu İçin Alt Toplam Hesaplaması Yapar.
     */
    public String clcSubTotalAccountGrouplist() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfAccountGroupList) {
            hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfAccountGroupList) {
                if (listOfObject.getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSalePrice());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Kasa Devirleri Tablosu İçin Alt Toplam Hesaplaması Yapar.
     */
    public String clcSubTotalSafeTransferlist() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfSafeTransfer) {
            hm.put(listOfObject.getSafe().getCurrency().getId(), listOfObject.getSafe().getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfSafeTransfer) {
                if (listOfObject.getSafe().getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSafe().getBalance());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Kasiyer Nakit Teslimat,Kredi Kartı Teslimat ve Veresiye Teslimat
     * Tablolarının Alt Toplam İşlemlerini Yapar
     *
     * @param type
     * @param isTotalExchange
     * @return
     */
    public String clcCashierPaymentTotal(int type, boolean isTotalExchange) {
        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();
        if (type == 1) {
            for (MarketShiftPreview listOfObject : listOfCashDelivery) {
                hm.put(listOfObject.getSafe().getCurrency().getId(), listOfObject.getSafe().getCurrency());
            }
        } else if (type == 2) {
            for (MarketShiftPreview listOfObject : listOfCreditCardDelivery) {
                hm.put(listOfObject.getBankAccount().getCurrency().getId(), listOfObject.getBankAccount().getCurrency());
            }
        } else if (type == 3) {
            for (MarketShiftPreview listOfObject : listOfCreditDelivery) {
                hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
            }
        }
        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            if (type == 1) {
                for (MarketShiftPreview listOfObject : listOfCashDelivery) {
                    if (listOfObject.getSafe().getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());

                        }
                    }
                }
            } else if (type == 2) {
                for (MarketShiftPreview listOfObject : listOfCreditCardDelivery) {
                    if (listOfObject.getBankAccount().getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());

                        }
                    }
                }
            } else if (type == 3) {
                for (MarketShiftPreview listOfObject : listOfCreditDelivery) {
                    if (listOfObject.getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());
                        }
                    }
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    @Override
    public List<Invoice> controlOpenAmountInvoice(Shift shift) {
        return marketShiftDao.controlOpenAmountInvoice(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftStockDetailList(Shift shift) {
        return marketShiftDao.shiftStockDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftTaxRateDetailList(Shift shift) {
        return marketShiftDao.shiftTaxRateDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftCurrencyDetailList(Shift shift) {
        return marketShiftDao.shiftCurrencyDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftCashierPaymentCashDetailList(Shift shift) {
        return marketShiftDao.shiftCashierPaymentCashDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftCashierPaymentBankDetailList(Shift shift) {
        return marketShiftDao.shiftCashierPaymentBankDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftDeficitGiveMoneyList(Shift shift) {
        return marketShiftDao.shiftDeficitGiveMoneyList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftDeficitGiveMoneyEmployeeList(Shift shift) {
        return marketShiftDao.shiftDeficitGiveMoneyEmployeeList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftSummaryList(Shift shift) {
        return marketShiftDao.shiftSummaryList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftGeneralList(Shift shift) {
        return marketShiftDao.shiftGeneralList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftCreditPaymentDetailList(Shift shift) {
        return marketShiftDao.shiftCreditPaymentDetailList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftStockGroupList(Shift shift) {
        return marketShiftDao.shiftStockGroupList(shift);
    }

    @Override
    public Shift findShift(Shift obj) {
        Map<String, Object> filt = new HashMap<>();

        List<Shift> list = marketShiftDao.findAll(0, 10, "shf.id", "ASC", filt, " AND shf.id = " + obj.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Shift();
        }
    }

    @Override
    public int controlReopenShift(Shift shift) {
        return marketShiftDao.controlReopenShift(shift);
    }

    @Override
    public int reopenShift(Shift shift) {
        return marketShiftDao.reopenShift(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftStockGroupListWithoutCategories(Shift shift) {
        return marketShiftDao.shiftStockGroupListWithoutCategories(shift);
    }

    @Override
    public List<Shift> shiftBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        return marketShiftDao.shiftBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int shiftBookCount(String where, String type, List<Object> param) {
        return marketShiftDao.shiftBookCount(where, type, param);
    }

    @Override
    public List<PointOfSale> listPointOfSale() {
        return marketShiftDao.listPointOfSale();
    }

    //Offline posların aktarılmayan şatışlarını kontrol eder 
    public List<ErrorOfflinePos> controlOfflinePos(List<PointOfSale> listOfPos) {
        List<ErrorOfflinePos> listErrorPos = new ArrayList<>();
        for (PointOfSale pos : listOfPos) {
            ErrorOfflinePos errorPos = new ErrorOfflinePos();
            errorPos = transferOfflineSales(pos);
            listErrorPos.add(errorPos);
        }
        return listErrorPos;
    }

    //Vardiya kapatılırken offline çalışan pos varsa satışlarını aktarır 
    public ErrorOfflinePos transferOfflineSales(PointOfSale pos) {

        String result = null;
        String posIp = pos.getLocalIpAddress();
        ErrorOfflinePos epos = new ErrorOfflinePos();
        epos.setId(pos.getId());
        epos.setCode(pos.getCode());
        epos.setName(pos.getName());
        epos.setLocalIpAddress(pos.getLocalIpAddress());
        System.out.println("POS IP : " + epos.getLocalIpAddress());
        PostMethod httpPost = new PostMethod("http://" + posIp + ":8081/offlineSendSales");
        try {
            HttpClient httpClient = new HttpClient();

            String entity = "1";
            httpPost.setRequestEntity(new StringRequestEntity(entity));
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(180000);

            BufferedReader br = null;
            int returnCode = httpClient.executeMethod(httpPost);
            System.out.println("RETURN CODE-- : " + returnCode);
            if (returnCode == 200) {
                br = new BufferedReader(new InputStreamReader(httpPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
                result = sb.toString();
                System.out.println("RESULT-- : " + result);
                try {
                    if (result != null) {
                        JSONObject resJson = new JSONObject(result);
                        epos.setIsSuccessful(resJson.getBoolean("result"));
                        epos.setNotSendCount(resJson.getInt("notSendCount"));
                        epos.setIsAccessed(true);
                    }
                } catch (Exception e) {
                }
            } else {
                epos.setIsSuccessful(false);
                epos.setIsAccessed(false);
            }
        } catch (Exception e) {
            System.out.println("cath 1-- : " + e.toString());
            epos.setIsSuccessful(false);
            epos.setIsAccessed(false);
        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                epos.setIsSuccessful(false);
                epos.setIsAccessed(false);
                System.out.println("catch 2-- : " + fe.getMessage());
            }
        }
        return epos;
    }

    //Vardiya açılırken Mpos, şube ayarları ve vardiya bilgilerini aktarır 
    @Override
    public void transferShiftAndPos(List<PointOfSale> listOfPos) {
        for (PointOfSale pos : listOfPos) {
            transferShift(pos);
        }
    }

    //Vardiya açılırken Mpos, şube ayarları ve vardiya bilgilerini aktarır 
    public void transferShift(PointOfSale pos) {

        String result = null;
        String posIp = pos.getLocalIpAddress();
        ErrorOfflinePos epos = new ErrorOfflinePos();
        epos.setId(pos.getId());
        epos.setCode(pos.getCode());
        epos.setName(pos.getName());
        epos.setLocalIpAddress(pos.getLocalIpAddress());
        System.out.println("POS IP : " + epos.getLocalIpAddress());
        PostMethod httpPost = new PostMethod("http://" + posIp + ":8081/offlineSendSales");
        try {
            HttpClient httpClient = new HttpClient();
            String params = "2";
            httpPost.setRequestEntity(new StringRequestEntity(params));
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(180000);

            BufferedReader br = null;
            int returnCode = httpClient.executeMethod(httpPost);
            System.out.println("RETURN CODE : " + returnCode);
            if (returnCode == 200) {
                br = new BufferedReader(new InputStreamReader(httpPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
                result = sb.toString();
                System.out.println("RESULT : " + result);
            }
        } catch (Exception e) {
            System.out.println("catch 1 : " + e.getMessage());
        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                System.out.println("catch 2 : " + fe.getMessage());
            }
        }

    }

    @Override
    public int create(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String lastUserShiftReport() {
        return marketShiftDao.lastUserShiftReport();
    }

    @Override
    public String updateLastUserShiftReport(String str) {
        return marketShiftDao.updateLastUserShiftReport(str);
    }

    @Override
    public void createExcelFile(Shift shift, String totalShiftAmount, List<String> selectedOptions) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        org.apache.poi.ss.usermodel.Font newFont = excelDocument.getWorkbook().createFont();
        newFont.setBold(true);
        newFont.setFontHeightInPoints((short) 9);
        excelDocument.getStyleHeader().setFont(newFont);

        CellStyle style = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());
        org.apache.poi.ss.usermodel.Font newFont2 = excelDocument.getWorkbook().createFont();
        newFont2.setBold(false);
        newFont2.setFontHeightInPoints((short) 9);
        style.setFont(newFont2);

        org.apache.poi.ss.usermodel.Font newFont1 = excelDocument.getWorkbook().createFont();
        newFont1.setBold(true);
        newFont1.setColor(IndexedColors.DARK_RED.index);
        newFont1.setFontHeightInPoints((short) 9);

        try {
            connection = marketShiftDao.getDatasource().getConnection();
            prep = connection.prepareStatement(marketShiftDao.shiftStockDetail(shift));
            rs = prep.executeQuery();

            BigDecimal totalColumn1, totalColumn2, totalColumn3, totalColumn4, totalColumn5, totalColumn6, totalColumn7, totalColumn8, totalColumn9;//Alt Toplamları İfade Eder
            CellStyle stylesub = StaticMethods.createCellStyleExcel("footerBlack", excelDocument.getWorkbook());
            org.apache.poi.ss.usermodel.Font newFont3 = excelDocument.getWorkbook().createFont();
            newFont3.setBold(true);
            newFont3.setColor(IndexedColors.BLACK.index);
            newFont3.setFontHeightInPoints((short) 9);
            stylesub.setFont(newFont3);

            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerDarkRed", excelDocument.getWorkbook());
            styleheader.setFont(newFont1);

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("shiftsummary"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty0 = excelDocument.getSheet().createRow(jRow++);
            /////////////////////////
            SXSSFRow branchnamerow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell branchname = branchnamerow.createCell((short) 0);
            branchname.setCellValue(sessionBean.getLoc().getString("branchname") + " : ");
            branchname.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell branchnameVal = branchnamerow.createCell((short) 1);
            branchnameVal.setCellValue(sessionBean.getUser().getLastBranch().getName());
            branchnameVal.setCellStyle(style);
            ///////////////
            SXSSFRow shiftNoRow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell shiftNoDesc = shiftNoRow.createCell((short) 0);
            shiftNoDesc.setCellValue(sessionBean.getLoc().getString("shiftno") + " : ");
            shiftNoDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell shiftNoVal = shiftNoRow.createCell((short) 1);
            shiftNoVal.setCellValue((shift.getShiftNo() == null ? "" : shift.getShiftNo()));
            shiftNoVal.setCellStyle(style);

            ////////////
            SXSSFRow shiftnamerow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell shiftnameDesc = shiftnamerow.createCell((short) 0);
            shiftnameDesc.setCellValue(sessionBean.getLoc().getString("shiftname") + " : ");
            shiftnameDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell shiftnameVal = shiftnamerow.createCell((short) 1);
            shiftnameVal.setCellValue((shift.getName() == null ? "" : shift.getName()));
            shiftnameVal.setCellStyle(style);

            /////////
            SXSSFRow startdaterow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell startdateDesc = startdaterow.createCell((short) 0);
            startdateDesc.setCellValue(sessionBean.getLoc().getString("startdate") + " : ");
            startdateDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell startdateVal = startdaterow.createCell((short) 1);
            startdateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), shift.getBeginDate()));
            startdateVal.setCellStyle(style);

            /////////
            SXSSFRow enddateRow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell enddateDesc = enddateRow.createCell((short) 0);
            enddateDesc.setCellValue(sessionBean.getLoc().getString("enddate") + " : ");
            enddateDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell enddateVal = enddateRow.createCell((short) 1);
            enddateVal.setCellValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), shift.getEndDate()));
            enddateVal.setCellStyle(style);

            //enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), shift.getEndDate()));
            SXSSFRow removedStockrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell removedStockDesc = removedStockrow.createCell((short) 0);
            removedStockDesc.setCellValue(sessionBean.getLoc().getString("countofremovedstock") + " : ");
            removedStockDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell removedStockVal = removedStockrow.createCell((short) 1);
            removedStockVal.setCellValue("" + StaticMethods.round(shift.getSumOfRemovedStock(), 4));
            removedStockVal.setCellStyle(style);

            //removedStock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("countofremovedstock") + " : " + StaticMethods.round(shift.getSumOfRemovedStock(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            SXSSFRow totalamountrow = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell totalamountDesc = totalamountrow.createCell((short) 0);
            totalamountDesc.setCellValue(sessionBean.getLoc().getString("totalsalesamount") + " : ");
            totalamountDesc.setCellStyle(excelDocument.getStyleHeader());

            SXSSFCell totalamountVal = totalamountrow.createCell((short) 1);
            totalamountVal.setCellValue(totalShiftAmount);
            totalamountVal.setCellStyle(style);

            //* ****************************Satış Listesi*************************
            if (selectedOptions.contains("1")) {

                jRow++;
                //otalamount.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("totalsalesamount") + " : " + totalShiftAmount);
                SXSSFRow empty4 = excelDocument.getSheet().createRow(jRow++);

                totalColumn1 = BigDecimal.ZERO;
                totalColumn2 = totalColumn3 = totalColumn4 = totalColumn5 = totalColumn1;
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 7));
                SXSSFRow reportName = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell ceelheader = reportName.createCell((short) 0);
                ceelheader.setCellValue(sessionBean.getLoc().getString("saleslist"));
                ceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x = 0;
                SXSSFRow rowm = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell celld1 = rowm.createCell((short) x++);
                celld1.setCellValue(sessionBean.getLoc().getString("stockbarcode"));
                celld1.setCellStyle(styleheader);

                SXSSFCell celld2 = rowm.createCell((short) x++);
                celld2.setCellValue(sessionBean.getLoc().getString("stockname"));
                celld2.setCellStyle(styleheader);

                SXSSFCell celld3 = rowm.createCell((short) x++);
                celld3.setCellValue(sessionBean.getLoc().getString("salesamount"));
                celld3.setCellStyle(styleheader);

                SXSSFCell celld4 = rowm.createCell((short) x++);
                celld4.setCellValue(sessionBean.getLoc().getString("returnamount"));
                celld4.setCellStyle(styleheader);

                SXSSFCell celld5 = rowm.createCell((short) x++);
                celld5.setCellValue(sessionBean.getLoc().getString("salesprice"));
                celld5.setCellStyle(styleheader);

                SXSSFCell celld6 = rowm.createCell((short) x++);
                celld6.setCellValue(sessionBean.getLoc().getString("returnprice"));
                celld6.setCellStyle(styleheader);

                SXSSFCell celld7 = rowm.createCell((short) x++);
                celld7.setCellValue(sessionBean.getLoc().getString("taxrate"));
                celld7.setCellStyle(styleheader);

                SXSSFCell celld8 = rowm.createCell((short) x++);
                celld8.setCellValue(sessionBean.getLoc().getString("totaltaxprice"));
                celld8.setCellStyle(styleheader);

                SXSSFCell celld9 = rowm.createCell((short) x++);
                celld9.setCellValue(sessionBean.getLoc().getString("unitprice"));
                celld9.setCellStyle(styleheader);

                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    row.createCell((short) b++).setCellValue((rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")));
                    row.createCell((short) b++).setCellValue((rs.getString("stckname") == null ? "" : rs.getString("stckname")).length() > 15 ? rs.getString("stckname").substring(0, 15) : rs.getString("stckname"));
                    SXSSFCell totalsalecount = row.createCell((short) b++);
                    totalsalecount.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalecount").doubleValue(), rs.getInt("guntunitrounding")));
                    totalsalecount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("totalsalecount"));

                    SXSSFCell totalreturncount = row.createCell((short) b++);
                    totalreturncount.setCellValue(StaticMethods.round(rs.getBigDecimal("totalreturncount").doubleValue(), rs.getInt("guntunitrounding")));
                    totalreturncount.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("totalreturncount"));

                    SXSSFCell totalsalemoney = row.createCell((short) b++);
                    totalsalemoney.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalemoney").doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    totalsalemoney.setCellStyle(style);

                    totalColumn3 = totalColumn3.add(rs.getBigDecimal("totalsalemoney"));

                    SXSSFCell totalreturnmoney = row.createCell((short) b++);
                    totalreturnmoney.setCellValue(StaticMethods.round(rs.getBigDecimal("totalreturnmoney").doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    totalreturnmoney.setCellStyle(style);

                    totalColumn4 = totalColumn4.add(rs.getBigDecimal("totalreturnmoney"));

                    SXSSFCell slitaxrate = row.createCell((short) b++);
                    slitaxrate.setCellValue(rs.getInt("slitaxrate"));
                    slitaxrate.setCellStyle(style);

                    SXSSFCell totaltax = row.createCell((short) b++);
                    totaltax.setCellValue(StaticMethods.round(rs.getBigDecimal("totaltax").doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    totaltax.setCellStyle(style);

                    totalColumn5 = totalColumn5.add(rs.getBigDecimal("totaltax"));

                    SXSSFCell unitprice = row.createCell((short) b++);
                    unitprice.setCellValue(StaticMethods.round(rs.getBigDecimal("sliunitprice").doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    unitprice.setCellStyle(style);

                }
                SXSSFRow rowsub1 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub0 = rowsub1.createCell((short) 1);
                cellsub0.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub0.setCellStyle(stylesub);

                SXSSFCell cellsub1 = rowsub1.createCell((short) 2);
                cellsub1.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                cellsub1.setCellStyle(stylesub);

                SXSSFCell cellsub2 = rowsub1.createCell((short) 3);
                cellsub2.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                cellsub2.setCellStyle(stylesub);

                SXSSFCell cellsub3 = rowsub1.createCell((short) 4);
                cellsub3.setCellValue(StaticMethods.round(totalColumn3.doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                cellsub3.setCellStyle(stylesub);

                SXSSFCell cellsub4 = rowsub1.createCell((short) 5);
                cellsub4.setCellValue(StaticMethods.round(totalColumn4.doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                cellsub4.setCellStyle(stylesub);

                SXSSFCell cellsub5 = rowsub1.createCell((short) 7);
                cellsub5.setCellValue(StaticMethods.round(totalColumn5.doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                cellsub5.setCellStyle(stylesub);
            }

            //* ****************************Satış/İade Toplam Grup Dökümü*************************
            if (selectedOptions.contains("2")) {
                jRow++;
                jRow++;

                List<MarketShiftPreview> resultList = marketShiftDao.shiftStockGroupDetail(shift);

                totalColumn2 = totalColumn3 = totalColumn4 = totalColumn5 = totalColumn6 = totalColumn1 = totalColumn8 = totalColumn9 = BigDecimal.ZERO;
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 8));
                SXSSFRow reportNameGroup = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell ceelheaderGroup = reportNameGroup.createCell((short) 0);
                ceelheaderGroup.setCellValue(sessionBean.getLoc().getString("salesreturncategorytotaldump"));
                ceelheaderGroup.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int xGroup = 0;
                SXSSFRow rowmgroup = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheaderGroup = StaticMethods.createCellStyleExcel("headerDarkRed", excelDocument.getWorkbook());
                styleheaderGroup.setFont(newFont1);

                SXSSFCell celld1Group = rowmgroup.createCell((short) xGroup++);
                celld1Group.setCellValue(sessionBean.getLoc().getString("category"));
                celld1Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld2Group = rowmgroup.createCell((short) xGroup++);
                celld2Group.setCellValue(sessionBean.getLoc().getString("previoussaleamount"));
                celld2Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld3Group = rowmgroup.createCell((short) xGroup++);
                celld3Group.setCellValue(sessionBean.getLoc().getString("previoussaleprice"));
                celld3Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld10Group = rowmgroup.createCell((short) xGroup++);
                celld10Group.setCellValue(sessionBean.getLoc().getString("previousamount"));
                celld10Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld11Group = rowmgroup.createCell((short) xGroup++);
                celld11Group.setCellValue(sessionBean.getLoc().getString("previousprice"));
                celld11Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld4Group = rowmgroup.createCell((short) xGroup++);
                celld4Group.setCellValue(sessionBean.getLoc().getString("entryamount"));
                celld4Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld5Group = rowmgroup.createCell((short) xGroup++);
                celld5Group.setCellValue(sessionBean.getLoc().getString("exitamount"));
                celld5Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld6Group = rowmgroup.createCell((short) xGroup++);
                celld6Group.setCellValue(sessionBean.getLoc().getString("salesamount"));
                celld6Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld7Group = rowmgroup.createCell((short) xGroup++);
                celld7Group.setCellValue(sessionBean.getLoc().getString("salesprice"));
                celld7Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld8Group = rowmgroup.createCell((short) xGroup++);
                celld8Group.setCellValue(sessionBean.getLoc().getString("remainingamount"));
                celld8Group.setCellStyle(styleheaderGroup);

                SXSSFCell celld9Group = rowmgroup.createCell((short) xGroup++);
                celld9Group.setCellValue(sessionBean.getLoc().getString("remainingamountt"));
                celld9Group.setCellStyle(styleheaderGroup);

                for (MarketShiftPreview obj : resultList) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell category = row.createCell((short) b++);
                    category.setCellValue(obj.getDescription());
                    category.setCellStyle(style);

                    SXSSFCell previoussalequantity = row.createCell((short) b++);
                    previoussalequantity.setCellValue(StaticMethods.round(obj.getPreviousSaleAmount().doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    previoussalequantity.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(obj.getPreviousSaleAmount());

                    SXSSFCell previoussaletotal = row.createCell((short) b++);
                    previoussaletotal.setCellValue(StaticMethods.round(obj.getPreviousSalePrice().doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    previoussaletotal.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(obj.getPreviousSalePrice());

                    SXSSFCell previousquantity = row.createCell((short) b++);
                    previousquantity.setCellValue(StaticMethods.round(obj.getPreviousAmount().doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    previousquantity.setCellStyle(style);

                    totalColumn8 = totalColumn8.add(obj.getPreviousAmount());

                    SXSSFCell previoustotal = row.createCell((short) b++);
                    previoustotal.setCellValue(StaticMethods.round(obj.getPreviousPrice().doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    previoustotal.setCellStyle(style);

                    totalColumn9 = totalColumn9.add(obj.getPreviousPrice());

                    SXSSFCell totalsalemoney = row.createCell((short) b++);
                    totalsalemoney.setCellValue(StaticMethods.round(obj.getTotalOfInComing().doubleValue(), sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()));
                    totalsalemoney.setCellStyle(style);

                    SXSSFCell totalreturnmoney = row.createCell((short) b++);
                    totalreturnmoney.setCellValue(StaticMethods.round(obj.getTotalOfOutGoing().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalreturnmoney.setCellStyle(style);

                    SXSSFCell totaltax = row.createCell((short) b++);
                    totaltax.setCellValue(StaticMethods.round(obj.getSaleAmount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totaltax.setCellStyle(style);

                    totalColumn3 = totalColumn3.add(obj.getSaleAmount());

                    SXSSFCell unitprice = row.createCell((short) b++);
                    unitprice.setCellValue(StaticMethods.round(obj.getSalePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    unitprice.setCellStyle(style);

                    totalColumn4 = totalColumn4.add(obj.getSalePrice());

                    SXSSFCell totalremainingquantity = row.createCell((short) b++);
                    totalremainingquantity.setCellValue(StaticMethods.round(obj.getRemainingQuantity().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalremainingquantity.setCellStyle(style);

                    totalColumn5 = totalColumn5.add(obj.getRemainingQuantity());

                    SXSSFCell unitremainingprice = row.createCell((short) b++);
                    unitremainingprice.setCellValue(StaticMethods.round(obj.getRemainingPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    unitremainingprice.setCellStyle(style);

                    totalColumn6 = totalColumn6.add(obj.getRemainingPrice());

                }
                //Kategorisiz Stoklar için

                prep = connection.prepareStatement(marketShiftDao.shiftStockGroupDetailWithoutCategories(shift));
                rs = prep.executeQuery();
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell stockswithoutcategory = row.createCell((short) b++);
                    stockswithoutcategory.setCellValue(sessionBean.getLoc().getString("stockswithoutcategory"));
                    stockswithoutcategory.setCellStyle(style);

                    SXSSFCell previoussalequantity = row.createCell((short) b++);
                    previoussalequantity.setCellValue(StaticMethods.round(rs.getBigDecimal("previoussalequantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    previoussalequantity.setCellStyle(style);
                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("previoussalequantity"));

                    SXSSFCell previoussaletotal = row.createCell((short) b++);
                    previoussaletotal.setCellValue(StaticMethods.round(rs.getBigDecimal("previoussaletotal").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    previoussaletotal.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("previoussaletotal"));

                    SXSSFCell previousquantity = row.createCell((short) b++);
                    previousquantity.setCellValue(StaticMethods.round(rs.getBigDecimal("previousamountbeforeshift").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    previousquantity.setCellStyle(style);

                    totalColumn8 = totalColumn8.add(rs.getBigDecimal("previousamountbeforeshift"));

                    SXSSFCell previoustotal = row.createCell((short) b++);
                    previoustotal.setCellValue(StaticMethods.round(rs.getBigDecimal("previouspricebeforeshift").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    previoustotal.setCellStyle(style);

                    totalColumn9 = totalColumn9.add(rs.getBigDecimal("previouspricebeforeshift"));

                    SXSSFCell totalsalemoney = row.createCell((short) b++);
                    totalsalemoney.setCellValue(StaticMethods.round(rs.getBigDecimal("girismiktar").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalemoney.setCellStyle(style);

                    SXSSFCell totalreturnmoney = row.createCell((short) b++);
                    totalreturnmoney.setCellValue(StaticMethods.round(rs.getBigDecimal("cikismiktar").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalreturnmoney.setCellStyle(style);

                    SXSSFCell totaltax = row.createCell((short) b++);
                    totaltax.setCellValue(StaticMethods.round(rs.getBigDecimal("salesquantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totaltax.setCellStyle(style);

                    totalColumn3 = totalColumn3.add(rs.getBigDecimal("salesquantity"));

                    SXSSFCell unitprice = row.createCell((short) b++);
                    unitprice.setCellValue(StaticMethods.round(rs.getBigDecimal("salestotal").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    unitprice.setCellStyle(style);

                    totalColumn4 = totalColumn4.add(rs.getBigDecimal("salestotal"));

                    SXSSFCell totalremainingquantity = row.createCell((short) b++);
                    totalremainingquantity.setCellValue(StaticMethods.round((rs.getBigDecimal("previousamountbeforeshift").add(rs.getBigDecimal("girismiktar")).subtract(rs.getBigDecimal("cikismiktar"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalremainingquantity.setCellStyle(style);

                    totalColumn5 = totalColumn5.add(rs.getBigDecimal("previousamountbeforeshift").add(rs.getBigDecimal("girismiktar")).subtract(rs.getBigDecimal("cikismiktar")));

                    SXSSFCell unitremainingprice = row.createCell((short) b++);
                    unitremainingprice.setCellValue(StaticMethods.round((rs.getBigDecimal("remainingPrice").add(rs.getBigDecimal("previouspricebeforeshift"))).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    unitremainingprice.setCellStyle(style);

                    totalColumn6 = totalColumn6.add(rs.getBigDecimal("remainingPrice").add(rs.getBigDecimal("previouspricebeforeshift")));

                }

                SXSSFRow rowsub1Quantity = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub0Group = rowsub1Quantity.createCell((short) 0);
                cellsub0Group.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub0Group.setCellStyle(stylesub);

                SXSSFCell cellsub1Group = rowsub1Quantity.createCell((short) 1);
                cellsub1Group.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub1Group.setCellStyle(stylesub);

                SXSSFCell cellsub2Group = rowsub1Quantity.createCell((short) 2);
                cellsub2Group.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub2Group.setCellStyle(stylesub);

                SXSSFCell cellsub7Group = rowsub1Quantity.createCell((short) 3);
                cellsub7Group.setCellValue(StaticMethods.round(totalColumn8.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub7Group.setCellStyle(stylesub);

                SXSSFCell cellsub8Group = rowsub1Quantity.createCell((short) 4);
                cellsub8Group.setCellValue(StaticMethods.round(totalColumn9.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub8Group.setCellStyle(stylesub);

                SXSSFCell cellsub3Group = rowsub1Quantity.createCell((short) 7);
                cellsub3Group.setCellValue(StaticMethods.round(totalColumn3.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub3Group.setCellStyle(stylesub);

                SXSSFCell cellsub4Group = rowsub1Quantity.createCell((short) 8);
                cellsub4Group.setCellValue(StaticMethods.round(totalColumn4.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub4Group.setCellStyle(stylesub);

                SXSSFCell cellsub5Group = rowsub1Quantity.createCell((short) 9);
                cellsub5Group.setCellValue(StaticMethods.round(totalColumn5.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub5Group.setCellStyle(stylesub);

                SXSSFCell cellsub6Group = rowsub1Quantity.createCell((short) 10);
                cellsub6Group.setCellValue(StaticMethods.round(totalColumn6.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub6Group.setCellStyle(stylesub);
            }

            // *****************************Cari Toplam Dökümü*************************
            if (selectedOptions.contains("3")) {
                jRow++;
                jRow++;
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 1));
                List<MarketShiftPreview> resultList = marketShiftDao.shiftAccountGroupList(shift);

                SXSSFRow currency = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell currencyceelheader = currency.createCell((short) 0);
                currencyceelheader.setCellValue(sessionBean.getLoc().getString("accounttotaldump"));
                currencyceelheader.setCellStyle(excelDocument.getStyleHeader());
                currencyceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x1 = 0;
                SXSSFRow rowm1 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadercurrency = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
                styleheadercurrency.setFont(newFont1);
                SXSSFCell celld21 = rowm1.createCell((short) x1++);
                celld21.setCellValue(sessionBean.getLoc().getString("account"));
                celld21.setCellStyle(styleheader);

                SXSSFCell celld22 = rowm1.createCell((short) x1++);
                celld22.setCellValue(sessionBean.getLoc().getString("totalprice"));
                celld22.setCellStyle(styleheader);

                totalColumn1 = BigDecimal.ZERO;
                for (MarketShiftPreview obj : resultList) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell crdname = row.createCell((short) b++);
                    crdname.setCellValue(obj.getAccount().getName());
                    crdname.setCellStyle(style);

                    SXSSFCell totalsalecount = row.createCell((short) b++);
                    totalsalecount.setCellValue(StaticMethods.round(obj.getSalePrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalecount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(obj.getSalePrice());

                }
                SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub20 = rowsub2.createCell((short) 0);
                cellsub20.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub20.setCellStyle(stylesub);

                SXSSFCell cellsub21 = rowsub2.createCell((short) 1);
                cellsub21.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub21.setCellStyle(stylesub);
            }

            // *****************************Para Birimi*************************
            if (selectedOptions.contains("4")) {
                jRow++;
                jRow++;
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 1));

                prep = connection.prepareStatement(marketShiftDao.shiftCurrencyDetail(shift));
                rs = prep.executeQuery();

                SXSSFRow currency = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell currencyceelheader = currency.createCell((short) 0);
                currencyceelheader.setCellValue(sessionBean.getLoc().getString("foreigncurrencytotals"));
                currencyceelheader.setCellStyle(excelDocument.getStyleHeader());
                currencyceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x1 = 0;
                SXSSFRow rowm1 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadercurrency = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
                styleheadercurrency.setFont(newFont1);
                SXSSFCell celld21 = rowm1.createCell((short) x1++);
                celld21.setCellValue(sessionBean.getLoc().getString("currency"));
                celld21.setCellStyle(styleheader);

                SXSSFCell celld22 = rowm1.createCell((short) x1++);
                celld22.setCellValue(sessionBean.getLoc().getString("totalprice"));
                celld22.setCellStyle(styleheader);

                totalColumn1 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell crdname = row.createCell((short) b++);
                    crdname.setCellValue(rs.getString("crdname"));
                    crdname.setCellStyle(style);

                    SXSSFCell totalsalecount = row.createCell((short) b++);
                    totalsalecount.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalemoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalecount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("totalsalemoney"));

                }
                SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub20 = rowsub2.createCell((short) 0);
                cellsub20.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub20.setCellStyle(stylesub);

                SXSSFCell cellsub21 = rowsub2.createCell((short) 1);
                cellsub21.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub21.setCellStyle(stylesub);
            }

            //****************************KDV  Gruplu********************************
            if (selectedOptions.contains("5")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftTaxRateDetail(shift));
                rs = prep.executeQuery();
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 5));

                SXSSFRow tax = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell taxceelheader = tax.createCell((short) 0);
                taxceelheader.setCellValue(sessionBean.getLoc().getString("kdv") + " " + sessionBean.getLoc().getString("totalmoney"));
                taxceelheader.setCellStyle(excelDocument.getStyleHeader());
                taxceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x2 = 0;
                SXSSFRow rowm2 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadertax = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld31 = rowm2.createCell((short) x2++);
                celld31.setCellValue(sessionBean.getLoc().getString("kdv") + " %");
                celld31.setCellStyle(styleheader);

                SXSSFCell celld32 = rowm2.createCell((short) x2++);
                celld32.setCellValue(sessionBean.getLoc().getString("salesamount"));
                celld32.setCellStyle(styleheader);

                SXSSFCell celld33 = rowm2.createCell((short) x2++);
                celld33.setCellValue(sessionBean.getLoc().getString("returnamount"));
                celld33.setCellStyle(styleheader);

                SXSSFCell celld34 = rowm2.createCell((short) x2++);
                celld34.setCellValue(sessionBean.getLoc().getString("salesprice"));
                celld34.setCellStyle(styleheader);

                SXSSFCell cell35 = rowm2.createCell((short) x2++);
                cell35.setCellValue(sessionBean.getLoc().getString("returnprice"));
                cell35.setCellStyle(styleheader);

                SXSSFCell celld36 = rowm2.createCell((short) x2++);
                celld36.setCellValue(sessionBean.getLoc().getString("totaltaxprice"));
                celld36.setCellStyle(styleheader);

                totalColumn2 = totalColumn3 = totalColumn4 = totalColumn4 = totalColumn5 = totalColumn1 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell slitaxrate = row.createCell((short) b++);
                    slitaxrate.setCellValue(rs.getInt("slitaxrate"));
                    slitaxrate.setCellStyle(style);

                    SXSSFCell totalsalecount = row.createCell((short) b++);
                    totalsalecount.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalecount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalecount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("totalsalecount"));

                    SXSSFCell totalreturncount = row.createCell((short) b++);
                    totalreturncount.setCellValue(StaticMethods.round(rs.getBigDecimal("totalreturncount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalreturncount.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("totalreturncount"));

                    SXSSFCell totalsalemoney = row.createCell((short) b++);
                    totalsalemoney.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalemoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalemoney.setCellStyle(style);

                    totalColumn3 = totalColumn3.add(rs.getBigDecimal("totalsalemoney"));

                    SXSSFCell totalreturnmoney = row.createCell((short) b++);
                    totalreturnmoney.setCellValue(StaticMethods.round(rs.getBigDecimal("totalreturnmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalreturnmoney.setCellStyle(style);

                    totalColumn4 = totalColumn4.add(rs.getBigDecimal("totalreturnmoney"));

                    SXSSFCell totaltax = row.createCell((short) b++);
                    totaltax.setCellValue(StaticMethods.round(rs.getBigDecimal("totaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totaltax.setCellStyle(style);

                    totalColumn5 = totalColumn5.add(rs.getBigDecimal("totaltax"));

                }
                ///Alt Toplam
                SXSSFRow rowsub3 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub30 = rowsub3.createCell((short) 0);
                cellsub30.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub30.setCellStyle(stylesub);

                SXSSFCell cellsub31 = rowsub3.createCell((short) 1);
                cellsub31.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub31.setCellStyle(stylesub);

                SXSSFCell cellsub32 = rowsub3.createCell((short) 2);
                cellsub32.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub32.setCellStyle(stylesub);

                SXSSFCell cellsub33 = rowsub3.createCell((short) 3);
                cellsub33.setCellValue(StaticMethods.round(totalColumn3.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub33.setCellStyle(stylesub);

                SXSSFCell cellsub34 = rowsub3.createCell((short) 4);
                cellsub34.setCellValue(StaticMethods.round(totalColumn4.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub34.setCellStyle(stylesub);

                SXSSFCell cellsub35 = rowsub3.createCell((short) 5);
                cellsub35.setCellValue(StaticMethods.round(totalColumn5.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub35.setCellStyle(stylesub);

            }

            //****************************Açık Fazla Borç Alacak********************************
            if (selectedOptions.contains("6")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftDeficitGiveMoneyEmployee(shift));
                rs = prep.executeQuery();

                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 2));

                SXSSFRow deficit = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell deficitceelheader = deficit.createCell((short) 0);
                deficitceelheader.setCellValue(sessionBean.getLoc().getString("deficit") + "-" + sessionBean.getLoc().getString("surplus"));
                deficitceelheader.setCellStyle(excelDocument.getStyleHeader());
                deficitceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));
                int x3 = 0;
                SXSSFRow rowm3 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheaderdeficit = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld41 = rowm3.createCell((short) x3++);
                celld41.setCellValue(sessionBean.getLoc().getString("employee"));
                celld41.setCellStyle(styleheader);

                SXSSFCell celld42 = rowm3.createCell((short) x3++);
                celld42.setCellValue(sessionBean.getLoc().getString("incoming1"));
                celld42.setCellStyle(styleheader);

                SXSSFCell celld43 = rowm3.createCell((short) x3++);
                celld43.setCellValue(sessionBean.getLoc().getString("outgoing"));
                celld43.setCellStyle(styleheader);

                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell accountname = row.createCell((short) b++);
                    accountname.setCellValue(rs.getString("accountname") + " " + rs.getString("accountsurname"));
                    accountname.setCellStyle(style);

                    SXSSFCell totalreturncount = row.createCell((short) b++);
                    totalreturncount.setCellValue(StaticMethods.round(rs.getBigDecimal("fazla").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalreturncount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("fazla"));

                    SXSSFCell out = row.createCell((short) b++);
                    out.setCellValue(StaticMethods.round(rs.getBigDecimal("borc").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("borc"));

                }
                SXSSFRow rowsub4 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub40 = rowsub4.createCell((short) 0);
                cellsub40.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub40.setCellStyle(stylesub);

                SXSSFCell cellsub41 = rowsub4.createCell((short) 1);
                cellsub41.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub41.setCellStyle(stylesub);

                SXSSFCell cellsub42 = rowsub4.createCell((short) 2);
                cellsub42.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub42.setCellStyle(stylesub);
            }

            //****************************Açık Fazla Gelir Gider********************************
            if (selectedOptions.contains("7")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftDeficitGiveMoney(shift));
                rs = prep.executeQuery();

                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 2));

                SXSSFRow deficit1 = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell deficitceelheader1 = deficit1.createCell((short) 0);
                deficitceelheader1.setCellValue(sessionBean.getLoc().getString("deficit") + "-" + sessionBean.getLoc().getString("surplus"));
                deficitceelheader1.setCellStyle(excelDocument.getStyleHeader());
                deficitceelheader1.setCellStyle(headerCss(excelDocument.getWorkbook()));
                int x8 = 0;
                SXSSFRow rowm8 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheaderdeficit1 = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld81 = rowm8.createCell((short) x8++);
                celld81.setCellValue(sessionBean.getLoc().getString("income") + " - " + sessionBean.getLoc().getString("expense"));
                celld81.setCellStyle(styleheader);

                SXSSFCell celld82 = rowm8.createCell((short) x8++);
                celld82.setCellValue(sessionBean.getLoc().getString("incoming1"));
                celld82.setCellStyle(styleheader);

                SXSSFCell celld83 = rowm8.createCell((short) x8++);
                celld83.setCellValue(sessionBean.getLoc().getString("outgoing"));
                celld83.setCellStyle(styleheader);

                totalColumn1 = totalColumn2 = BigDecimal.ZERO;

                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    Double inComing = 0.0;
                    Double outGoing = 0.0;

                    inComing = StaticMethods.round(rs.getBigDecimal("income").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("income"));

                    outGoing = StaticMethods.round(rs.getBigDecimal("expense").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("expense"));

                    SXSSFCell name = row.createCell((short) b++);
                    name.setCellValue(rs.getString("name"));
                    name.setCellStyle(style);

                    SXSSFCell in = row.createCell((short) b++);
                    in.setCellValue(inComing);
                    in.setCellStyle(style);

                    SXSSFCell out = row.createCell((short) b++);
                    out.setCellValue(outGoing);
                    out.setCellStyle(style);

                }
                SXSSFRow rowsub8 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub80 = rowsub8.createCell((short) 0);
                cellsub80.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub80.setCellStyle(stylesub);

                SXSSFCell cellsub81 = rowsub8.createCell((short) 1);
                cellsub81.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub81.setCellStyle(stylesub);

                SXSSFCell cellsub82 = rowsub8.createCell((short) 2);
                cellsub82.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub82.setCellStyle(stylesub);
            }

            //****************************Personel Nakit Teslimat********************************
            if (selectedOptions.contains("8")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftCashierPaymentCashDetail(shift));
                rs = prep.executeQuery();

                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 4));
                SXSSFRow cashierpayment = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cashierpaymentheader = cashierpayment.createCell((short) 0);
                cashierpaymentheader.setCellValue(sessionBean.getLoc().getString("cashdelivery"));
                cashierpaymentheader.setCellStyle(excelDocument.getStyleHeader());
                cashierpaymentheader.setCellStyle(headerCss(excelDocument.getWorkbook()));
                int x4 = 0;
                SXSSFRow rowm4 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheaderpayment = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld51 = rowm4.createCell((short) x4++);
                celld51.setCellValue(sessionBean.getLoc().getString("employee"));
                celld51.setCellStyle(styleheader);

                SXSSFCell celld52 = rowm4.createCell((short) x4++);
                celld52.setCellValue(sessionBean.getLoc().getString("totalmoney"));
                celld52.setCellStyle(styleheader);

                SXSSFCell celld53 = rowm4.createCell((short) x4++);
                celld53.setCellValue(sessionBean.getLoc().getString("casecodename"));
                celld53.setCellStyle(styleheader);

                SXSSFCell celld54 = rowm4.createCell((short) x4++);
                celld54.setCellValue(sessionBean.getLoc().getString("exchangerate"));
                celld54.setCellStyle(styleheader);

                SXSSFCell celld55 = rowm4.createCell((short) x4++);
                celld55.setCellValue(sessionBean.getLoc().getString("exchangeprice"));
                celld55.setCellStyle(styleheader);

                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell accname = row.createCell((short) b++);
                    accname.setCellValue(rs.getString("accname") + " " + rs.getString("acctitle"));
                    accname.setCellStyle(style);

                    SXSSFCell accualprice = row.createCell((short) b++);
                    accualprice.setCellValue(StaticMethods.round(rs.getBigDecimal("accualprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    accualprice.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("accualprice"));

                    SXSSFCell sfcode = row.createCell((short) b++);
                    sfcode.setCellValue(rs.getString("sfcode") + "-" + rs.getString("sfname"));
                    sfcode.setCellStyle(style);

                    SXSSFCell exchangerate = row.createCell((short) b++);
                    exchangerate.setCellValue(StaticMethods.round(rs.getBigDecimal("shpexchangerate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    exchangerate.setCellStyle(style);

                    SXSSFCell shpaccualprice = row.createCell((short) b++);
                    shpaccualprice.setCellValue(StaticMethods.round(rs.getBigDecimal("shpaccualprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    shpaccualprice.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("shpaccualprice"));

                }
                SXSSFRow rowsub5 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub50 = rowsub5.createCell((short) 0);
                cellsub50.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub50.setCellStyle(stylesub);

                SXSSFCell cellsub51 = rowsub5.createCell((short) 1);
                cellsub51.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub51.setCellStyle(stylesub);

                SXSSFCell cellsub52 = rowsub5.createCell((short) 4);
                cellsub52.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub52.setCellStyle(stylesub);
            }

            //****************************Personel Kreti Kartı Teslimat********************************
            if (selectedOptions.contains("9")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftCashierPaymentBankDetail(shift));
                rs = prep.executeQuery();
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 4));
                SXSSFRow cashierpaymentcreditcart = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell creditcart = cashierpaymentcreditcart.createCell((short) 0);
                creditcart.setCellValue(sessionBean.getLoc().getString("creditcarddelivery"));
                creditcart.setCellStyle(excelDocument.getStyleHeader());
                creditcart.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x5 = 0;
                SXSSFRow rowm5 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheaderpaymentcreditcard = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld61 = rowm5.createCell((short) x5++);
                celld61.setCellValue(sessionBean.getLoc().getString("employee"));
                celld61.setCellStyle(styleheader);

                SXSSFCell celld62 = rowm5.createCell((short) x5++);
                celld62.setCellValue(sessionBean.getLoc().getString("totalmoney"));
                celld62.setCellStyle(styleheader);

                SXSSFCell celld63 = rowm5.createCell((short) x5++);
                celld63.setCellValue(sessionBean.getLoc().getString("bankcodename"));
                celld63.setCellStyle(styleheader);

                SXSSFCell celld64 = rowm5.createCell((short) x5++);
                celld64.setCellValue(sessionBean.getLoc().getString("exchangerate"));
                celld64.setCellStyle(styleheader);

                SXSSFCell celld65 = rowm5.createCell((short) x5++);
                celld65.setCellValue(sessionBean.getLoc().getString("exchangeprice"));
                celld65.setCellStyle(styleheader);
                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell accname = row.createCell((short) b++);
                    accname.setCellValue(rs.getString("accname") + " " + rs.getString("acctitle"));
                    accname.setCellStyle(style);

                    SXSSFCell accualprice = row.createCell((short) b++);
                    accualprice.setCellValue(StaticMethods.round(rs.getBigDecimal("accualprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    accualprice.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("accualprice"));

                    SXSSFCell sfcode = row.createCell((short) b++);
                    sfcode.setCellValue(rs.getString("bnkcode") + "-" + rs.getString("baname"));
                    sfcode.setCellStyle(style);

                    SXSSFCell exchangerate = row.createCell((short) b++);
                    exchangerate.setCellValue(StaticMethods.round(rs.getBigDecimal("shpexchangerate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    exchangerate.setCellStyle(style);

                    SXSSFCell shpaccualprice = row.createCell((short) b++);
                    shpaccualprice.setCellValue(StaticMethods.round(rs.getBigDecimal("shpaccualprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    shpaccualprice.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("shpaccualprice"));

                }

                SXSSFRow rowsub6 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub60 = rowsub6.createCell((short) 0);
                cellsub60.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub60.setCellStyle(stylesub);

                SXSSFCell cellsub61 = rowsub6.createCell((short) 1);
                cellsub61.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub61.setCellStyle(stylesub);

                SXSSFCell cellsub62 = rowsub6.createCell((short) 4);
                cellsub62.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub62.setCellStyle(stylesub);
            }

            //****************************Veresiye Teslimatı********************************
            if (selectedOptions.contains("10")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftCreditPaymentDetail(shift));
                rs = prep.executeQuery();
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 3));
                SXSSFRow creditpayment = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell creditpaymentcell = creditpayment.createCell((short) 0);
                creditpaymentcell.setCellValue(sessionBean.getLoc().getString("creditdelivery"));
                creditpaymentcell.setCellStyle(excelDocument.getStyleHeader());
                creditpaymentcell.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x10 = 0;
                SXSSFRow rowm10 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell celld101 = rowm10.createCell((short) x10++);
                celld101.setCellValue(sessionBean.getLoc().getString("account"));
                celld101.setCellStyle(styleheader);

                SXSSFCell celld102 = rowm10.createCell((short) x10++);
                celld102.setCellValue(sessionBean.getLoc().getString("totalmoney"));
                celld102.setCellStyle(styleheader);

                SXSSFCell celld103 = rowm10.createCell((short) x10++);
                celld103.setCellValue(sessionBean.getLoc().getString("exchangerate"));
                celld103.setCellStyle(styleheader);

                SXSSFCell celld104 = rowm10.createCell((short) x10++);
                celld104.setCellValue(sessionBean.getLoc().getString("exchangeprice"));
                celld104.setCellStyle(styleheader);
                totalColumn1 = totalColumn2 = BigDecimal.ZERO;
                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell name = row.createCell((short) b++);
                    name.setCellValue(rs.getString("name"));
                    name.setCellStyle(style);

                    SXSSFCell accualprice = row.createCell((short) b++);
                    accualprice.setCellValue(StaticMethods.round(rs.getBigDecimal("price").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    accualprice.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(rs.getBigDecimal("price"));

                    SXSSFCell exchangerate = row.createCell((short) b++);
                    exchangerate.setCellValue(StaticMethods.round(rs.getBigDecimal("exchangerate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    exchangerate.setCellStyle(style);

                    SXSSFCell totalprice = row.createCell((short) b++);
                    totalprice.setCellValue(StaticMethods.round(rs.getBigDecimal("totalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalprice.setCellStyle(style);

                    totalColumn2 = totalColumn2.add(rs.getBigDecimal("totalprice"));

                }

                SXSSFRow rowsub10 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub100 = rowsub10.createCell((short) 0);
                cellsub100.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub100.setCellStyle(stylesub);

                SXSSFCell cellsub101 = rowsub10.createCell((short) 3);
                cellsub101.setCellValue(StaticMethods.round(totalColumn2.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub101.setCellStyle(stylesub);
            }

            // *****************************Kasa Devirleri*************************
            if (selectedOptions.contains("11")) {
                jRow++;
                jRow++;
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 1));
                List<MarketShiftPreview> resultList = marketShiftDao.shiftSafeTransferList(shift);

                SXSSFRow currency = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell currencyceelheader = currency.createCell((short) 0);
                currencyceelheader.setCellValue(sessionBean.getLoc().getString("safetransfers"));
                currencyceelheader.setCellStyle(excelDocument.getStyleHeader());
                currencyceelheader.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x1 = 0;
                SXSSFRow rowm1 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadercurrency = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
                styleheadercurrency.setFont(newFont1);
                SXSSFCell celld21 = rowm1.createCell((short) x1++);
                celld21.setCellValue(sessionBean.getLoc().getString("safe"));
                celld21.setCellStyle(styleheader);

                SXSSFCell celld22 = rowm1.createCell((short) x1++);
                celld22.setCellValue(sessionBean.getLoc().getString("total"));
                celld22.setCellStyle(styleheader);

                totalColumn1 = BigDecimal.ZERO;
                for (MarketShiftPreview obj : resultList) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    SXSSFCell crdname = row.createCell((short) b++);
                    crdname.setCellValue(obj.getSafe().getName());
                    crdname.setCellStyle(style);

                    SXSSFCell totalsalecount = row.createCell((short) b++);
                    totalsalecount.setCellValue(StaticMethods.round(obj.getSafe().getBalance().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    totalsalecount.setCellStyle(style);

                    totalColumn1 = totalColumn1.add(obj.getSafe().getBalance());

                }
                SXSSFRow rowsub2 = excelDocument.getSheet().createRow(jRow++);

                SXSSFCell cellsub20 = rowsub2.createCell((short) 0);
                cellsub20.setCellValue(sessionBean.getLoc().getString("sum") + ":");
                cellsub20.setCellStyle(stylesub);

                SXSSFCell cellsub21 = rowsub2.createCell((short) 1);
                cellsub21.setCellValue(StaticMethods.round(totalColumn1.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsub21.setCellStyle(stylesub);
            }

            //****************************Vardiya Genel********************************
            if (selectedOptions.contains("12")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftGeneral(shift));
                rs = prep.executeQuery();
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 6));
                SXSSFRow shiftGeneralRow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell shiftgeneral = shiftGeneralRow.createCell((short) 0);
                shiftgeneral.setCellValue(sessionBean.getLoc().getString("shiftgeneral"));
                shiftgeneral.setCellStyle(excelDocument.getStyleHeader());
                shiftgeneral.setCellStyle(headerCss(excelDocument.getWorkbook()));

                int x9 = 0;
                SXSSFRow rowm9 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadershiftgeneral = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld91 = rowm9.createCell((short) x9++);
                celld91.setCellValue(sessionBean.getLoc().getString("description"));
                celld91.setCellStyle(styleheader);

                SXSSFCell celld92 = rowm9.createCell((short) x9++);
                celld92.setCellValue(sessionBean.getLoc().getString("sale"));
                celld92.setCellStyle(styleheader);

                SXSSFCell celld93 = rowm9.createCell((short) x9++);
                celld93.setCellValue(sessionBean.getLoc().getString("cash"));
                celld93.setCellStyle(styleheader);

                SXSSFCell celld94 = rowm9.createCell((short) x9++);
                celld94.setCellValue(sessionBean.getLoc().getString("creditcard"));
                celld94.setCellStyle(styleheader);

                SXSSFCell celld95 = rowm9.createCell((short) x9++);
                celld95.setCellValue(sessionBean.getLoc().getString("credit1"));
                celld95.setCellStyle(styleheader);

                SXSSFCell celld96 = rowm9.createCell((short) x9++);
                celld96.setCellValue(sessionBean.getLoc().getString("open"));
                celld96.setCellStyle(styleheader);

                SXSSFCell celld97 = rowm9.createCell((short) x9++);
                celld97.setCellValue(sessionBean.getLoc().getString("return"));
                celld97.setCellStyle(styleheader);

                while (rs.next()) {
                    int b = 0;
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                    SXSSFCell name = row.createCell((short) b++);
                    name.setCellValue(rs.getString("name"));
                    name.setCellStyle(style);

                    SXSSFCell sales = row.createCell((short) b++);
                    sales.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalemoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    sales.setCellStyle(style);

                    SXSSFCell cash = row.createCell((short) b++);
                    cash.setCellValue(StaticMethods.round(rs.getBigDecimal("nakit").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cash.setCellStyle(style);

                    SXSSFCell banka = row.createCell((short) b++);
                    banka.setCellValue(StaticMethods.round(rs.getBigDecimal("banka").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    banka.setCellStyle(style);

                    SXSSFCell credit = row.createCell((short) b++);
                    credit.setCellValue(StaticMethods.round(rs.getBigDecimal("veresiye").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    credit.setCellStyle(style);

                    SXSSFCell acık = row.createCell((short) b++);
                    acık.setCellValue(StaticMethods.round(rs.getBigDecimal("acık").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    acık.setCellStyle(style);

                    SXSSFCell returns = row.createCell((short) b++);
                    returns.setCellValue(StaticMethods.round(rs.getBigDecimal("totalreturnmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    returns.setCellStyle(style);

                }
            }

            //****************************Vardiya ÖZet********************************
            if (selectedOptions.contains("13")) {
                jRow++;
                jRow++;
                prep = connection.prepareStatement(marketShiftDao.shiftSummary(shift));
                rs = prep.executeQuery();
                excelDocument.getSheet().addMergedRegion(new CellRangeAddress(jRow, jRow, 0, 2));

                SXSSFRow shiftsummary = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell shiftsummarycell = shiftsummary.createCell((short) 0);
                shiftsummarycell.setCellValue(sessionBean.getLoc().getString("shiftsummary"));
                shiftsummarycell.setCellStyle(excelDocument.getStyleHeader());
                shiftsummarycell.setCellStyle(headerCss(excelDocument.getWorkbook()));
                int x6 = 0;
                SXSSFRow rowm6 = excelDocument.getSheet().createRow(jRow++);
                CellStyle styleheadershiftsummary = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

                SXSSFCell celld71 = rowm6.createCell((short) x6++);
                celld71.setCellValue(sessionBean.getLoc().getString("description"));
                celld71.setCellStyle(styleheader);

                SXSSFCell celld72 = rowm6.createCell((short) x6++);
                celld72.setCellValue(sessionBean.getLoc().getString("incoming1"));
                celld72.setCellStyle(styleheader);

                SXSSFCell celld73 = rowm6.createCell((short) x6++);
                celld73.setCellValue(sessionBean.getLoc().getString("outgoing"));
                celld73.setCellStyle(styleheader);
                while (rs.next()) {

                    SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);

                    int b = 0;
                    SXSSFCell name1 = row1.createCell((short) b++);
                    name1.setCellValue(sessionBean.getLoc().getString("salesreturnprice"));
                    name1.setCellStyle(style);

                    SXSSFCell in1 = row1.createCell((short) b++);
                    in1.setCellValue(StaticMethods.round(rs.getBigDecimal("totalsalemoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in1.setCellStyle(style);

                    SXSSFCell out1 = row1.createCell((short) b++);
                    out1.setCellValue(StaticMethods.round(StaticMethods.round(rs.getBigDecimal("totalreturnmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out1.setCellStyle(style);

                    /*   //////////////////////Nakit Satış///////////////////////////////////
                SXSSFRow row5 = excelDocument.getSheet().createRow(j++);

                b = 0;
                SXSSFCell name5 = row5.createCell((short) b++);
                name5.setCellValue(sessionBean.getLoc().getString("cashprice"));

                SXSSFCell in5 = row5.createCell((short) b++);
                in5.setCellValue(StaticMethods.round(rs.getBigDecimal("nakit").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFCell out5 = row5.createCell((short) b++);
                out5.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                ////////////////////////Kredi Kartı Satış/////////////////////////////////
                SXSSFRow row4 = excelDocument.getSheet().createRow(j++);

                b = 0;
                SXSSFCell name4 = row4.createCell((short) b++);
                name4.setCellValue(sessionBean.getLoc().getString("creditcardprice"));

                SXSSFCell in4 = row4.createCell((short) b++);
                in4.setCellValue(StaticMethods.round(rs.getBigDecimal("banka").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                SXSSFCell out4 = row4.createCell((short) b++);
                out4.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
             
                     */
                    /////////////////////////Nakit Teslimat////////////////////////////////
                    SXSSFRow row6 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name6 = row6.createCell((short) b++);
                    name6.setCellValue(sessionBean.getLoc().getString("totalofcash"));
                    name6.setCellStyle(style);

                    SXSSFCell in6 = row6.createCell((short) b++);
                    in6.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in6.setCellStyle(style);

                    SXSSFCell out6 = row6.createCell((short) b++);
                    out6.setCellValue(StaticMethods.round(rs.getBigDecimal("nakittahsilat").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out6.setCellStyle(style);

                    /////////////////////////Kredi Kartı Teslimat/////////////////////////////////////
                    SXSSFRow row7 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name7 = row7.createCell((short) b++);
                    name7.setCellValue(sessionBean.getLoc().getString("totalofpos"));
                    name7.setCellStyle(style);

                    SXSSFCell in7 = row7.createCell((short) b++);
                    in7.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in7.setCellStyle(style);

                    SXSSFCell out7 = row7.createCell((short) b++);
                    out7.setCellValue(StaticMethods.round(rs.getBigDecimal("bankatahsilat").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out7.setCellStyle(style);

                    ////////////////////Veresiye Satış////////////////////
                    SXSSFRow row8 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name8 = row8.createCell((short) b++);
                    name8.setCellValue(sessionBean.getLoc().getString("totalofpostpaid"));
                    name8.setCellStyle(style);

                    SXSSFCell in8 = row8.createCell((short) b++);
                    in8.setCellValue(0);
                    in8.setCellStyle(style);

                    SXSSFCell out8 = row8.createCell((short) b++);
                    out8.setCellValue(StaticMethods.round(rs.getBigDecimal("veresiye").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out8.setCellStyle(style);

                    ////////////////////Açık Satış////////////////////
                    SXSSFRow row9 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name9 = row9.createCell((short) b++);
                    name9.setCellValue(sessionBean.getLoc().getString("totalofopen"));
                    name9.setCellStyle(style);

                    SXSSFCell in9 = row9.createCell((short) b++);
                    in9.setCellValue(0);
                    in9.setCellStyle(style);

                    SXSSFCell out9 = row9.createCell((short) b++);
                    out9.setCellValue(StaticMethods.round(rs.getBigDecimal("acık").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out9.setCellStyle(style);

                    ///////////////////////////Gelir//////////////////////////////
                    SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);

                    b = 0;
                    SXSSFCell name2 = row2.createCell((short) b++);
                    name2.setCellValue(sessionBean.getLoc().getString("totalofincome"));
                    name2.setCellStyle(style);

                    SXSSFCell in2 = row2.createCell((short) b++);
                    in2.setCellValue(StaticMethods.round(rs.getBigDecimal("gelir").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in2.setCellStyle(style);

                    SXSSFCell out2 = row2.createCell((short) b++);
                    out2.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out2.setCellStyle(style);

                    ///////////////////////////Gider//////////////////////////////
                    SXSSFRow row3 = excelDocument.getSheet().createRow(jRow++);

                    b = 0;
                    SXSSFCell name3 = row3.createCell((short) b++);
                    name3.setCellValue(sessionBean.getLoc().getString("totalofexpense"));
                    name3.setCellStyle(style);

                    SXSSFCell in3 = row3.createCell((short) b++);
                    in3.setCellValue(StaticMethods.round(0, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in3.setCellStyle(style);

                    SXSSFCell out3 = row3.createCell((short) b++);
                    out3.setCellValue(StaticMethods.round(rs.getBigDecimal("gider").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out3.setCellStyle(style);

                    ////////////////////////Personel Toplamı////////////////
                    SXSSFRow row10 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name10 = row10.createCell((short) b++);
                    name10.setCellValue(sessionBean.getLoc().getString("totalofemployee"));
                    name10.setCellStyle(style);

                    SXSSFCell in10 = row10.createCell((short) b++);
                    in10.setCellValue(StaticMethods.round(rs.getBigDecimal("employeeincome").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in10.setCellStyle(style);

                    SXSSFCell out10 = row10.createCell((short) b++);
                    out10.setCellValue(StaticMethods.round(rs.getBigDecimal("employeeexpense").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out10.setCellStyle(style);

                    ////////////////////////Ara Toplam ////////////////
                    SXSSFRow row13 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name13 = row13.createCell((short) b++);
                    name13.setCellValue(sessionBean.getLoc().getString("subtotal"));
                    name13.setCellStyle(style);

                    SXSSFCell in13 = row13.createCell((short) b++);
                    in13.setCellValue(StaticMethods.round(rs.getBigDecimal("girentoplam").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in13.setCellStyle(style);

                    SXSSFCell out13 = row13.createCell((short) b++);
                    out13.setCellValue(StaticMethods.round(rs.getBigDecimal("cıkantoplam").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out13.setCellStyle(style);

                    ////////////////////////Fark Toplam ////////////////
                    SXSSFRow row11 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name11 = row11.createCell((short) b++);
                    name11.setCellValue(sessionBean.getLoc().getString("difference"));
                    name11.setCellStyle(style);

                    BigDecimal girentoplam = StaticMethods.round(rs.getBigDecimal("girentoplam"), sessionBean.getUser().getLastBranch().getCurrencyrounding());
                    BigDecimal cıkantoplam = StaticMethods.round(rs.getBigDecimal("cıkantoplam"), sessionBean.getUser().getLastBranch().getCurrencyrounding());

                    BigDecimal diffin = BigDecimal.ZERO;
                    BigDecimal diffout = BigDecimal.ZERO;
                    if (girentoplam.compareTo(cıkantoplam) > 0) {
                        diffin = BigDecimal.ZERO;
                        diffout = girentoplam.subtract(cıkantoplam);
                    } else if (cıkantoplam.compareTo(girentoplam) > 0) {
                        diffout = BigDecimal.ZERO;
                        diffin = cıkantoplam.subtract(girentoplam);
                    }

                    SXSSFCell in11 = row11.createCell((short) b++);
                    in11.setCellValue(StaticMethods.round(diffin.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in11.setCellStyle(style);

                    SXSSFCell out11 = row11.createCell((short) b++);
                    out11.setCellValue(StaticMethods.round(diffout.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out11.setCellStyle(style);

                    ////////////////////////Genel Toplam ////////////////
                    SXSSFRow row12 = excelDocument.getSheet().createRow(jRow++);
                    b = 0;
                    SXSSFCell name12 = row12.createCell((short) b++);
                    name12.setCellValue(sessionBean.getLoc().getString("overalltotal"));
                    name12.setCellStyle(style);

                    SXSSFCell in12 = row12.createCell((short) b++);
                    BigDecimal overralIn = diffin.add(rs.getBigDecimal("girentoplam"));
                    BigDecimal overralOut = diffout.add(rs.getBigDecimal("cıkantoplam"));
                    in12.setCellValue(StaticMethods.round(overralIn.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    in12.setCellStyle(style);

                    SXSSFCell out12 = row12.createCell((short) b++);
                    out12.setCellValue(StaticMethods.round(overralOut.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    out12.setCellStyle(style);

                }

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("shiftsummary"));
            } catch (IOException ex) {
                Logger.getLogger(MarketShiftService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            System.out.println("Ex" + ex.toString());
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
                Logger.getLogger(MarketShiftDao.class
                        .getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String createWhere(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        where = " AND ( shf.begindate >= '" + sd.format(beginDate) + "' AND (shf.enddate <= '" + sd.format(endDate) + "' OR  shf.enddate IS NULL ) ) \n";

        return where;
    }

    @Override
    public List<MarketShiftPreview> shiftAccountGroupList(Shift shift) {
        return marketShiftDao.shiftAccountGroupList(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftSafeTransferList(Shift shift) {

        return marketShiftDao.shiftSafeTransferList(shift);

    }

    @Override
    public boolean controlIsCheck() {
        return marketShiftDao.controlIsCheck();
    }

    @Override
    public String updateIsCheck(Shift shift) {
        return marketShiftDao.updateIsCheck(shift);
    }

}
