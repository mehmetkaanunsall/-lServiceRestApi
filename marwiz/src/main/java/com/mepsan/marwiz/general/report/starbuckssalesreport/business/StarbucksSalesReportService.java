/**
 *Bu class şubenin starbucks cihazının satışlarını listeye çekmek için şubeden web servis bilgilerini alır.web servise ulaşur ve satışları listeler.
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:16:33 PM
 */
package com.mepsan.marwiz.general.report.starbuckssalesreport.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.starbuckssalesreport.dao.StarbucksMachicneSales;
import com.mepsan.marwiz.system.branch.dao.IBranchSettingDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class StarbucksSalesReportService implements IStarbucksSalesReportService {

    @Autowired
    IBranchSettingDao branchSettingDao;

    @Autowired
    SessionBean sessionBean;

    public void setBranchSettingDao(IBranchSettingDao branchSettingDao) {
        this.branchSettingDao = branchSettingDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    private int pagingCount;
    private BranchSetting branchSetting;

    public int getPagingCount() {
        return pagingCount;
    }

    public void setPagingCount(int pagingCount) {
        this.pagingCount = pagingCount;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    @Override
    public List<StarbucksMachicneSales> listOfSale(int first, int pageSize, Date begin, Date end, List<StarbucksStock> listOfStarbucksStock) {
        branchSetting = new BranchSetting();
        branchSetting = branchSettingDao.findStarbucksMachicne();
        List<StarbucksMachicneSales> listOfsales = new ArrayList<>();

        String parameter = "from_timestamp=" + begin.getTime() / 1000 + "&to_timestamp=" + end.getTime() / 1000 + "";
        if ((branchSetting.getStarbucksApiKey() != null && !branchSetting.getStarbucksApiKey().isEmpty()) && (branchSetting.getStarbucksMachicneName() != null && !branchSetting.getStarbucksMachicneName().isEmpty()) && (branchSetting.getStarbucksWebServiceUrl() != null && !branchSetting.getStarbucksWebServiceUrl().isEmpty())) {
            parameter += "&machine_id=" + branchSetting.getStarbucksMachicneName() + "";

            parameter += "&offset=" + first + "&limit=" + pageSize + "";
            if ((!branchSetting.getStarbucksWebServiceUrl().isEmpty() && branchSetting.getStarbucksWebServiceUrl() != null) && (!branchSetting.getStarbucksApiKey().isEmpty() && branchSetting.getStarbucksApiKey() != null)) {
                String result = httpClientStarbucks(branchSetting.getStarbucksWebServiceUrl(), "/stats/vends?" + parameter);
                if (!result.equals("")) {
                    JSONObject jo = new JSONObject(result);
                    if (jo.getInt("code") == 200) {

                        pagingCount = jo.getJSONObject("paging").getInt("total");
                        Gson gson = new Gson();
                        Type deviceListType = new TypeToken<ArrayList<StarbucksMachicneSales>>() {
                        }.getType();
                        listOfsales = gson.fromJson(jo.getJSONArray("result").toString(), deviceListType);
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                        for (StarbucksMachicneSales listOfsale : listOfsales) { // gelen tüm tarih bilgilerini formatlar
                            Date date = new Date(new Long(listOfsale.getDatetime() * 1000L));
                            listOfsale.setLongTime(format.format(date));

                        }
                    }
                }
            }

            findStockName(listOfsales, listOfStarbucksStock);
            return listOfsales;
        } else {
            return new ArrayList<>();
        }

    }

    public String convertTimeWithTimeZome(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        return (cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.MONTH) + 1) + " "
                + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE));

    }

    public String httpClientStarbucks(String urls, String methodName) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls + methodName);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", "Token " + branchSetting.getStarbucksApiKey());
            connection.setDoOutput(true);
            connection.setAllowUserInteraction(false);

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder respo = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (Exception e) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public int count() {
        return pagingCount;
    }

    @Override
    public List<StarbucksMachicneSales> findSales(Date beginDate, Date endDate, List<StarbucksStock> listOfStarbucksStock) {
        branchSetting = new BranchSetting();
        branchSetting = branchSettingDao.findStarbucksMachicne();
        List<StarbucksMachicneSales> listOfsales = new ArrayList<>();

        String parameter = "from_timestamp=" + beginDate.getTime() / 1000 + "&to_timestamp=" + endDate.getTime() / 1000 + "";
        if ((branchSetting.getStarbucksApiKey() != null && !branchSetting.getStarbucksApiKey().isEmpty()) && (branchSetting.getStarbucksMachicneName() != null && !branchSetting.getStarbucksMachicneName().isEmpty()) && (branchSetting.getStarbucksWebServiceUrl() != null && !branchSetting.getStarbucksWebServiceUrl().isEmpty())) {
            parameter += "&machine_id=" + branchSetting.getStarbucksMachicneName() + "";

            if ((!branchSetting.getStarbucksWebServiceUrl().isEmpty() && branchSetting.getStarbucksWebServiceUrl() != null) && (!branchSetting.getStarbucksApiKey().isEmpty() && branchSetting.getStarbucksApiKey() != null)) {
                String result = httpClientStarbucks(branchSetting.getStarbucksWebServiceUrl(), "/stats/vends?" + parameter);
                if (!result.equals("")) {
                    JSONObject jo = new JSONObject(result);
                    if (jo.getInt("code") == 200) {

                        pagingCount = jo.getJSONObject("paging").getInt("total");
                        Gson gson = new Gson();
                        Type deviceListType = new TypeToken<ArrayList<StarbucksMachicneSales>>() {
                        }.getType();
                        listOfsales = gson.fromJson(jo.getJSONArray("result").toString(), deviceListType);

                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                        for (StarbucksMachicneSales listOfsale : listOfsales) { // gelen tüm tarih bilgilerini formatlar
                            Date date = new Date(new Long(listOfsale.getDatetime() * 1000L));
                            listOfsale.setLongTime(format.format(date));
                        }

                    }
                }
            }
            return listOfsales;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void exportPdf(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock) {

        listOfSales = findStockName(listOfSales, listOfStarbucksStock);

        try {
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("starbuckssalereport"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), endDate), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.createHeaderPdf("frmStarbucksSaleReportDatatable:dtbStarbucksSaleReport", toogleList, "headerBlack", pdfDocument);
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            int i = 0;
            while (i < listOfSales.size()) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(String.valueOf(listOfSales.get(i).getMachine_id()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(listOfSales.get(i).getLongTime(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(String.valueOf(listOfSales.get(i).getTransaction_id()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(listOfSales.get(i).getStockName() == null ? Integer.toString(listOfSales.get(i).getStock_id()) :  String.valueOf(listOfSales.get(i).getStockName()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(String.valueOf(listOfSales.get(i).getPrice()) + " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                i++;
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("starbuckssalereport"));
        } catch (Exception e) {
        }
    }

    @Override
    public void exportExcel(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock) {
       
       listOfSales= findStockName(listOfSales, listOfStarbucksStock);
        try {
            ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("starbuckssalereport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());
            jRow++;
            SXSSFRow empty0 = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate));

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), endDate));

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmStarbucksSaleReportDatatable:dtbStarbucksSaleReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;
            int i = 0;
            while (i < listOfSales.size()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(listOfSales.get(i).getMachine_id());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(listOfSales.get(i).getLongTime());
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(listOfSales.get(i).getTransaction_id());
                }
                if (toogleList.get(3)) {
                    String stockName="";
                    stockName=listOfSales.get(i).getStockName() == null ? Integer.toString(listOfSales.get(i).getStock_id()) : listOfSales.get(i).getStockName();
                    
                    row.createCell((short) b++).setCellValue(stockName );
                }
                if (toogleList.get(4)) {
                    CellStyle style1 = excelDocument.getWorkbook().createCellStyle();
                    SXSSFCell cell = row.createCell((short) b++);
                    cell.setCellValue(listOfSales.get(i).getPrice());
                    style1.setAlignment(HorizontalAlignment.RIGHT);
                    cell.setCellStyle(style1);
                }
                i++;
            }
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("starbuckssalereport"));
        } catch (Exception e) {
        }
    }

    @Override
    public String exportPrint(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock) {
        StringBuilder sb = new StringBuilder();
       listOfSales=findStockName(listOfSales, listOfStarbucksStock);
        
        try {
            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), beginDate)).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), endDate)).append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append("</div> ");

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
                    + "    </style> <table> <tr>");
            StaticMethods.createHeaderPrint("frmStarbucksSaleReportDatatable:dtbStarbucksSaleReport", toogleList, "headerBlack", sb);

            int i = 0;

            while (i < listOfSales.size()) {
                sb.append(" <tr> ");
                if (toogleList.get(0)) {
                    sb.append("<td>").append(listOfSales.get(i).getMachine_id()).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td style=\"text-align: right\">").append(listOfSales.get(i).getLongTime()).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(listOfSales.get(i).getTransaction_id()).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(listOfSales.get(i).getStockName() == null ? listOfSales.get(i).getStock_id() : listOfSales.get(i).getStockName()).append("</td>");
                }
                if (toogleList.get(4)) {
                    sb.append("<td style=\"text-align: right\">").append(listOfSales.get(i).getPrice()).append(" ").append(sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getId(), 0)).append("</td>");
                }
                sb.append(" </tr> ");
                i++;
            }
            sb.append(" </tr> ");
            sb.append(" </table> ");
        } catch (Exception e) {
        }

        return sb.toString();
    }

    public List<StarbucksMachicneSales> findStockName(List<StarbucksMachicneSales> listSales, List<StarbucksStock> listOfStarbucksStock) {

        for (StarbucksMachicneSales starbucksSale : listSales) {

            for (StarbucksStock starbucksStock : listOfStarbucksStock) {

                if (starbucksStock.getCode().equalsIgnoreCase(Integer.toString(starbucksSale.getStock_id()))) {

                    starbucksSale.setStockName(starbucksStock.getName());
                    break;
                }
            }
        }

        return listSales;

    }
}
