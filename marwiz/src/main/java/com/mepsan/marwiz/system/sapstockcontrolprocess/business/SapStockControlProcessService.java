/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.business;

import com.google.gson.JsonObject;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.sapstockcontrolprocess.dao.ISapStockControlProcessDao;
import com.mepsan.marwiz.system.sapstockcontrolprocess.dao.SapStockControlProcess;
import com.mepsan.marwiz.system.sapstockcontrolprocess.dao.SapStockControlProcessDao;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.parser.TokenType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class SapStockControlProcessService implements ISapStockControlProcessService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private ISapStockControlProcessDao sapStockControlProcessDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public ISapStockControlProcessDao getSapStockControlProcessDao() {
        return sapStockControlProcessDao;
    }

    public void setSapStockControlProcessDao(ISapStockControlProcessDao sapStockControlProcessDao) {
        this.sapStockControlProcessDao = sapStockControlProcessDao;
    }

    @Override
    public SapStockControlProcess getSapStockInfos(Date date) {

        SapStockControlProcess sapStockControl = new SapStockControlProcess();
        sapStockControl.setProcessDate(date);

        BranchSetting bs = sessionBean.getUser().getLastBranchSetting();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String result = null;
        String url = bs.getErpUrl();

        try {

            HttpPost httpPost = new HttpPost(url);

            try {

                HttpClient httpClient = WebServiceClient.createHttpClient_AcceptsUntrustedCerts();

                httpPost.addHeader("Operation", "Malzeme_Stok");

                RequestConfig rc = RequestConfig.DEFAULT;
                RequestConfig requestConfig
                        = RequestConfig
                                .copy(rc)
                                .setSocketTimeout(bs.getErpTimeout() * 1000)
                                .setConnectTimeout(bs.getErpTimeout() * 1000)
                                .setConnectionRequestTimeout(bs.getErpTimeout() * 1000)
                                .build();
                httpPost.setConfig(requestConfig);

                byte[] encodedAuth = Base64.getEncoder().encode((bs.getErpUsername() + ":" + bs.getErpPassword()).getBytes("UTF-8"));
                String authHeader = "Basic " + new String(encodedAuth);
                httpPost.addHeader("Authorization", authHeader);

                JsonObject jsonSendData = new JsonObject();
                jsonSendData.addProperty("IV_TARIH", sdf.format(date));
                jsonSendData.addProperty("IV_ISTASYON_KOD", bs.getBranch().getLicenceCode());

                String json = jsonSendData.toString();
                StringEntity requestEntity = new StringEntity(
                        json,
                        ContentType.APPLICATION_JSON);
                httpPost.setEntity(requestEntity);

                sapStockControl.setSendData(json);
                System.out.println("----json send data----" + jsonSendData.toString());
                HttpResponse httpResponse = httpClient.execute(httpPost);
                int returnCode = httpResponse.getStatusLine().getStatusCode();
                result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                sapStockControl.setGetData(result);

                if (returnCode == 200) {
                    System.out.println("RESULT : " + result);
                    if (!result.isEmpty()) {
                        JSONObject resJson = new JSONObject(result);

                        JSONObject resJsonResult = new JSONObject(resJson.get("ET_STOK").toString());

                        if (!resJsonResult.toString().isEmpty()) {

                            JSONArray jsonArrayItem = new JSONArray(resJsonResult.get("item").toString());
                            sapStockControl.setItemJson(jsonArrayItem.toString());

                        }

                    }

                    sapStockControl.setIsSuccess(true);

                } else {
                    System.out.println("HTTP STATUS : " + returnCode);
                    sapStockControl.setIsSuccess(false);
                    sapStockControl.setMessage("HTTP STATUS : " + returnCode);

                }

                sapStockControlProcessDao.insertOrUpdateLog(sapStockControl);

            } catch (Exception e) {
                System.out.println("Catch 1 : " + e.toString());
                sapStockControl.setIsSuccess(false);
                sapStockControl.setMessage("");

            } finally {
                try {
                    httpPost.releaseConnection();
                } catch (Exception fe) {
                    System.out.println("Catch 2 : " + fe.toString());
                    sapStockControl.setIsSuccess(false);
                    sapStockControl.setMessage("");

                }
            }
        } catch (Exception ex) {
            System.out.println("Catch 3 : " + ex.toString());
            sapStockControl.setIsSuccess(false);
            sapStockControl.setMessage("");
        }

        return sapStockControl;
    }

    @Override
    public List<SapStockControlProcess> compareStockInfos(SapStockControlProcess sapStock, Date date, int differenceReasonType) {
        return sapStockControlProcessDao.compareStockInfos(sapStock, date, differenceReasonType);
    }

    @Override
    public void exportExcel(List<Boolean> toogleList, List<SapStockControlProcess> listOfDifferentStocks, Date date, int differenceReasonType) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sdf1.toString());

        try {

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("sapstockcontrolprocess"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("date") + " : " + sdf1.format(date));

            String differenceReasonTypeName = "";
            if (differenceReasonType == 1) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productswithdifferentquantity");
            } else if (differenceReasonType == 2) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productsnotavailableonmarwiz");
            } else if (differenceReasonType == 3) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productsnotavailableonsap");
            } else {
                differenceReasonTypeName = sessionBean.getLoc().getString("all");
            }

            SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
            brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("marwizsapdifferencereason") + " : " + differenceReasonTypeName);

            SXSSFRow empty6 = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow rowm = excelDocument.getSheet().createRow(jRow++);

            CellStyle styleheader2 = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            styleheader2.setBorderRight(BorderStyle.MEDIUM);
            styleheader2.setRightBorderColor(IndexedColors.WHITE.index);

            CellStyle styleheader3 = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            styleheader3.setBorderLeft(BorderStyle.MEDIUM);
            styleheader3.setLeftBorderColor(IndexedColors.WHITE.index);

            CellStyle styleheader1 = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            SXSSFCell celld01 = rowm.createCell((short) 0);
            celld01.setCellValue("");
            celld01.setCellStyle(styleheader1);

            SXSSFCell celld11 = rowm.createCell((short) 1);
            celld11.setCellValue("SAP");
            celld11.setCellStyle(styleheader1);

            SXSSFCell celld21 = rowm.createCell((short) 2);
            celld21.setCellValue("");
            celld21.setCellStyle(styleheader2);

            SXSSFCell celld31 = rowm.createCell((short) 3);
            celld31.setCellValue("");
            celld31.setCellStyle(styleheader3);

            SXSSFCell celld41 = rowm.createCell((short) 4);
            celld41.setCellValue("");
            celld41.setCellStyle(styleheader1);

            SXSSFCell celld51 = rowm.createCell((short) 5);
            celld51.setCellValue("MARWIZ");
            celld51.setCellStyle(styleheader1);

            SXSSFCell celld61 = rowm.createCell((short) 6);
            celld61.setCellValue("");
            celld61.setCellStyle(styleheader1);

            SXSSFCell celld71 = rowm.createCell((short) 7);
            celld71.setCellValue("");
            celld71.setCellStyle(styleheader1);

            SXSSFCell celld81 = rowm.createCell((short) 8);
            celld81.setCellValue("");
            celld81.setCellStyle(styleheader1);

            SXSSFRow rowm1 = excelDocument.getSheet().createRow(jRow++);
            CellStyle styleheader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

            SXSSFCell celld0 = rowm1.createCell((short) 0);
            celld0.setCellValue(sessionBean.getLoc().getString("stockcode"));
            celld0.setCellStyle(styleheader);

            SXSSFCell celld1 = rowm1.createCell((short) 1);
            celld1.setCellValue(sessionBean.getLoc().getString("quantity"));
            celld1.setCellStyle(styleheader);

            SXSSFCell celld2 = rowm1.createCell((short) 2);
            celld2.setCellValue("MARWIZ" + sessionBean.getLoc().getString("centerstockcode"));
            celld2.setCellStyle(styleheader);

            SXSSFCell celld3 = rowm1.createCell((short) 3);
            celld3.setCellValue(sessionBean.getLoc().getString("barcode"));
            celld3.setCellStyle(styleheader);

            SXSSFCell celld4 = rowm1.createCell((short) 4);
            celld4.setCellValue(sessionBean.getLoc().getString("centerstockcode"));
            celld4.setCellStyle(styleheader);

            SXSSFCell celld5 = rowm1.createCell((short) 5);
            celld5.setCellValue(sessionBean.getLoc().getString("stockname"));
            celld5.setCellStyle(styleheader);

            SXSSFCell celld6 = rowm1.createCell((short) 6);
            celld6.setCellValue(sessionBean.getLoc().getString("quantity"));
            celld6.setCellStyle(styleheader);

            SXSSFCell celld8 = rowm1.createCell((short) 7);
            celld8.setCellValue(sessionBean.getLoc().getString("isservice"));
            celld8.setCellStyle(styleheader);

            SXSSFCell celld7 = rowm1.createCell((short) 8);
            celld7.setCellValue(sessionBean.getLoc().getString("description"));
            celld7.setCellStyle(styleheader);

            for (SapStockControlProcess stockControl : listOfDifferentStocks) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(stockControl.getSapStockCode());
                }

                if (toogleList.get(1)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -2) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(stockControl.getSapQuantity().doubleValue(), stockControl.getSapUnit().getUnitRounding()));
                    } else {
                        row.createCell((short) b++).setCellValue("");
                    }
                }

                if (toogleList.get(2)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -2) {
                        row.createCell((short) b++).setCellValue(stockControl.getCenterStockCode());
                    } else {
                        row.createCell((short) b++).setCellValue("");
                    }
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(stockControl.getStock().getBarcode());
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(stockControl.getStock().getId() > 0 ? stockControl.getCenterStockCode() : "");
                }
                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(stockControl.getStock().getName());
                }
                if (toogleList.get(6)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -3) {
                        row.createCell((short) b++).setCellValue(StaticMethods.round(stockControl.getMarwizQuantity() != null ? stockControl.getMarwizQuantity().doubleValue() : BigDecimal.ZERO.doubleValue(), stockControl.getStock().getUnit().getId() > 0 ? stockControl.getStock().getUnit().getUnitRounding() : sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    } else {
                        row.createCell((short) b++).setCellValue("");
                    }
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(stockControl.getStock().getId() > 0 ? stockControl.getStock().isIsService() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no") : "");
                }

                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(stockControl.getErrorCode() == -1 ? sessionBean.loc.getString("marwizsapproductquantitiesaredifferent") : stockControl.getErrorCode() == -2 ? sessionBean.loc.getString("productnotfoundonmarwiz") : sessionBean.loc.getString("productnotfoundonsap"));
                }

            }
            try {

                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("sapstockcontrolprocess"));
            } catch (IOException ex) {
                Logger.getLogger(SapStockControlProcessService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(SapStockControlProcessService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

            } catch (Exception ex1) {
                Logger.getLogger(SapStockControlProcessService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportPdf(List<Boolean> toogleList, List<SapStockControlProcess> listOfDifferentStocks, Date date, int differenceReasonType) {

        try {


            //Birim için
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());

            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 1);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("sapstockcontrolprocess"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("date") + " : " + sdf1.format(date), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String differenceReasonTypeName = "";
            if (differenceReasonType == 1) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productswithdifferentquantity");
            } else if (differenceReasonType == 2) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productsnotavailableonmarwiz");
            } else if (differenceReasonType == 3) {
                differenceReasonTypeName = sessionBean.getLoc().getString("productsnotavailableonsap");
            } else {
                differenceReasonTypeName = sessionBean.getLoc().getString("all");
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("marwizsapdifferencereason") + " : " + differenceReasonTypeName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setBackgroundColor(Color.BLACK);
            pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfDocument.getCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, 0, Color.WHITE));
            pdfDocument.getCell().setColspan(3);
            pdfDocument.getCell().setBorderColorRight(Color.WHITE);
            pdfDocument.getCell().setBorderWidthRight(1f);

            pdfDocument.getCell().setPhrase(new Phrase("SAP", pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setColspan(6);
            pdfDocument.getCell().setBorderColorLeft(Color.WHITE);
            pdfDocument.getCell().setBorderWidthLeft(1f);
            pdfDocument.getCell().setBorderWidthRight(0f);

            pdfDocument.getCell().setPhrase(new Phrase("MARWIZ", pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("quantity"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("centerstockcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("barcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("centerstockcode"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("stockname"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("quantity"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("isservice"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("description"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            for (SapStockControlProcess stockControl : listOfDifferentStocks) {

                if (toogleList.get(0)) {

                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getSapStockCode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -2) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(formatterUnit.format(stockControl.getSapQuantity() != null ? stockControl.getSapQuantity() : BigDecimal.ZERO) + stockControl.getSapUnit().getSortName(), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(2)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -2) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getCenterStockCode(), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getStock().getBarcode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getStock().getId() > 0 ? stockControl.getCenterStockCode() : " ", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getStock().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(6)) {
                    if (stockControl.getErrorCode() == -1 || stockControl.getErrorCode() == -3) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(formatterUnit.format(stockControl.getMarwizQuantity() != null ? stockControl.getMarwizQuantity() : BigDecimal.ZERO) + stockControl.getStock().getUnit().getSortName(), pdfDocument.getFont()));
                    } else if (stockControl.getErrorCode() == -2) {
                        pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getStock().getId() > 0 ? stockControl.getStock().isIsService() ? sessionBean.getLoc().getString("yes") : sessionBean.getLoc().getString("no") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(stockControl.getErrorCode() == -1 ? sessionBean.loc.getString("marwizsapproductquantitiesaredifferent") : stockControl.getErrorCode() == -2 ? sessionBean.loc.getString("productnotfoundonmarwiz") : sessionBean.loc.getString("productnotfoundonsap"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("sapstockcontrolprocess"));

        } catch (DocumentException e) {
        } finally {
        }
    }

}
