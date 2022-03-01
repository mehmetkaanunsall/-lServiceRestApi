/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 03:21:30
 */
package com.mepsan.marwiz.general.report.dailysalesreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.DailySalesReport;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.IDailySalesReportDao;
import com.mepsan.marwiz.general.report.dailysalesreport.dao.SubPivot;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class DailySalesReportService implements IDailySalesReportService {

    @Autowired
    private IDailySalesReportDao dailySalesReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDailySalesReportDao(IDailySalesReportDao dailySalesReportDao) {
        this.dailySalesReportDao = dailySalesReportDao;
    }

    @Override
    public DailySalesReport findAll(DailySalesReport dailySalesReport, String branchList, String sortBy) {
        return dailySalesReportDao.findAll(dailySalesReport, branchList, sortBy);
    }

    @Override
    public void exportPdf(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate, int sorting, boolean sortby) {

        List<SubPivot> subAnalysisPivotsList = null;
        int numberOfColumns = 0;
        List<Boolean> toogleList = new ArrayList<>();
        if (!listOfObjects.isEmpty()) {
            subAnalysisPivotsList = listOfObjects.get(0).getSubList();
            numberOfColumns = subAnalysisPivotsList.size() + 4;
        }
        for (int i = 0; i < numberOfColumns; i++) {
            toogleList.add(true);
        }

        try {
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("dailysalesreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : selectedBranchList) {
                    branchName += " , " + s.getBranch().getName();
                }
                branchName = branchName.substring(3, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            createCellStylePdf("headerBlack", pdfDocument, pdfDocument.getTableHeader());
            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.loc.getString("branch"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.loc.getString("processdate"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            for (SubPivot subAnalysisPivot : subAnalysisPivotsList) {
                pdfDocument.getTableHeader().setPhrase(new Phrase(subAnalysisPivot.getType().getTag(), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());
            }

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.loc.getString("totaldiscount"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getTableHeader().setPhrase(new Phrase(sessionBean.loc.getString("totalgiro"), pdfDocument.getFontColumnTitle()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            int i = 0, count = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sumPrice = new BigDecimal(BigInteger.ZERO);
            double totalLiter = 0;
            int totalCount = 0;

            pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

            if (sorting == 2 && sortby) {
                Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
                Collections.reverse(listSaleProcessDate);
            } else if (sorting == 2 && !sortby) {
                Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
            } else {
                Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
                Collections.reverse(listSaleProcessDate);
            }

            for (DailySalesReport dailySale : listSaleProcessDate) {

                rowGroup = "";
                rowGroup += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySale.getProcessDate());

                sumPrice = BigDecimal.ZERO;
                count = 0;
                for (DailySalesReport report : listSaleProcessDate) {
                    if (report.getProcessDate().equals(dailySale.getProcessDate())) {
                        sumPrice = sumPrice.add(report.getTotalMoney());

                    }
                }

                if (i == 0) {//birinci kayit için
                    pdfDocument.getCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySale.getProcessDate()) + " / " + sessionBean.loc.getString("salesprice") + " : "
                            + sessionBean.getNumberFormat().format(sumPrice) + sessionBean.currencySignOrCode(dailySale.getCurrency().getId(), 0),
                            pdfDocument.getFontHeader()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                } else if (!oldRowGroup.equals(rowGroup)) {
                    pdfDocument.getCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySale.getProcessDate()) + " / " + sessionBean.loc.getString("salesprice") + " : "
                            + sessionBean.getNumberFormat().format(sumPrice) + sessionBean.currencySignOrCode(dailySale.getCurrency().getId(), 0),
                            pdfDocument.getFontHeader()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                }

                for (DailySalesReport dsr : listOfObjects) {

                    if (dsr.getProcessDate() != null) {

                        if (dsr.getProcessDate().equals(dailySale.getProcessDate())) {

                            Currency currency = new Currency(dsr.getCurrency().getId());

                            pdfDocument.getDataCell().setPhrase(new Phrase(dsr.getBranchSetting().getBranch().getName(), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                            pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dsr.getProcessDate()), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                            for (SubPivot subAnalysisPivot : dsr.getSubList()) {
                                pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(subAnalysisPivot.getTotalPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                            }

                            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(dsr.getTotalDiscount()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(dsr.getTotalMoney()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

                        }

                    }

                }
                i++;
                oldRowGroup = rowGroup;

            }
//            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getRightCell().setPhrase(new Phrase("", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.loc.getString("sum"), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            for (int k = 0; k < numberOfColumns - 4; k++) {
                pdfDocument.getRightCell().setPhrase(new Phrase(calcTotalDynamicColumn(listOfObjects, k), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            }

            pdfDocument.getRightCell().setPhrase(new Phrase(totalDailySalesReport.getOverallTotalDiscount(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getRightCell().setPhrase(new Phrase(totalDailySalesReport.getOverallTotalGiro(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("dailysalesreport"));
        } catch (DocumentException e) {
        }

    }

    @Override
    public void exportExcel(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate, int sorting, boolean sortby) {

        List<SubPivot> subAnalysisPivotsList = null;
        int numberOfColumns = 0;
        int jRow = 0;
        if (!listOfObjects.isEmpty()) {
            subAnalysisPivotsList = listOfObjects.get(0).getSubList();
            numberOfColumns = subAnalysisPivotsList.size() + 4;
        }

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

        CellStyle styleheader = StaticMethods.createCellStyleExcel("headerBlack", excelDocument.getWorkbook());

        SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell cellheader = header.createCell((short) 0);
        cellheader.setCellValue(sessionBean.getLoc().getString("dailysalesreport"));
        cellheader.setCellStyle(excelDocument.getStyleHeader());
        jRow++;

        excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getBeginDate()));

        excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getEndDate()));

        String branchName = "";
        if (selectedBranchList.isEmpty()) {
            branchName = sessionBean.getLoc().getString("all");
        } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
            branchName = sessionBean.getLoc().getString("all");
        } else {
            for (BranchSetting s : selectedBranchList) {
                branchName += " , " + s.getBranch().getName();
            }
            branchName = branchName.substring(3, branchName.length());
        }

        SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
        branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

        SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

        int a = 0;
        SXSSFRow rowh = excelDocument.getSheet().createRow(jRow++);

        SXSSFCell cell = rowh.createCell((short) a++);
        cell.setCellValue(sessionBean.getLoc().getString("branch"));
        cell.setCellStyle(styleheader);

        SXSSFCell cell1 = rowh.createCell((short) a++);
        cell1.setCellValue(sessionBean.getLoc().getString("processdate"));
        cell1.setCellStyle(styleheader);

        SXSSFCell celld;
        for (SubPivot subAnalysisPivot : subAnalysisPivotsList) {

            celld = rowh.createCell((short) a++);
            celld.setCellValue(subAnalysisPivot.getType().getTag());
            celld.setCellStyle(styleheader);
        }

        SXSSFCell cell2 = rowh.createCell((short) a++);
        cell2.setCellValue(sessionBean.getLoc().getString("totaldiscount"));
        cell2.setCellStyle(styleheader);

        SXSSFCell cell3 = rowh.createCell((short) a++);
        cell3.setCellValue(sessionBean.getLoc().getString("totalgiro"));
        cell3.setCellStyle(styleheader);

        int i = 0, count = 0;
        String rowGroup = "";
        String oldRowGroup = "";
        BigDecimal sumPrice = new BigDecimal(BigInteger.ZERO);
        double totalLiter = 0;
        int totalCount = 0;

        excelDocument.getStyleHeader().setAlignment(HorizontalAlignment.LEFT);

        if (sorting == 2 && sortby) {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
            Collections.reverse(listSaleProcessDate);
        } else if (sorting == 2 && !sortby) {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
        } else {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
            Collections.reverse(listSaleProcessDate);
        }

        for (DailySalesReport dailySaleReport : listSaleProcessDate) {

            rowGroup = "";
            rowGroup += dailySaleReport.getProcessDate();

            sumPrice = BigDecimal.ZERO;
            count = 0;
            for (DailySalesReport report : listSaleProcessDate) {
                if (report.getProcessDate().equals(dailySaleReport.getProcessDate())) {
                    sumPrice = sumPrice.add(report.getTotalMoney());

                }
            }

            if (i == 0) {//brinci kayit için

                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell4 = row.createCell((short) 0);
                cell4.setCellValue(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySaleReport.getProcessDate()) + " / " + sessionBean.loc.getString("salesprice") + " : "
                        + sessionBean.getNumberFormat().format(sumPrice) + sessionBean.currencySignOrCode(dailySaleReport.getCurrency().getId(), 0));
                cell4.setCellStyle(excelDocument.getStyleHeader());

            } else if (!oldRowGroup.equals(rowGroup)) {

                SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell5 = row2.createCell((short) 0);
                cell5.setCellValue(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySaleReport.getProcessDate()) + " / " + sessionBean.loc.getString("salesprice") + " : "
                        + sessionBean.getNumberFormat().format(sumPrice) + sessionBean.currencySignOrCode(dailySaleReport.getCurrency().getId(), 0));
                cell5.setCellStyle(excelDocument.getStyleHeader());

            }

            int b = 0;
            for (DailySalesReport dsr : listOfObjects) {
                b = 0;
                if (dsr.getProcessDate() != null) {

                    if (dsr.getProcessDate().equals(dailySaleReport.getProcessDate())) {

                        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                        row.createCell((short) b++).setCellValue(dsr.getBranchSetting().getBranch().getName());

                        SXSSFCell celldate = row.createCell((short) b++);
                        celldate.setCellValue(dsr.getProcessDate());
                        celldate.setCellStyle(excelDocument.getDateFormatStyle());

                        for (SubPivot subAnalysisPivot : dsr.getSubList()) {
                            row.createCell((short) b++).setCellValue(StaticMethods.round(subAnalysisPivot.getTotalPrice().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }

                        row.createCell((short) b++).setCellValue(StaticMethods.round(dsr.getTotalDiscount().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        row.createCell((short) b++).setCellValue(StaticMethods.round(dsr.getTotalMoney().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                }

            }

            oldRowGroup = rowGroup;

        }

        int b = 0;
        CellStyle cellStyle = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);

        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

        SXSSFCell cells1 = row.createCell((short) b++);
        cells1.setCellValue("");
        cells1.setCellStyle(cellStyle);

        SXSSFCell cells2 = row.createCell((short) b++);
        cells2.setCellValue(sessionBean.loc.getString("sum"));
        cells2.setCellStyle(cellStyle);

        for (int j = 0; j < numberOfColumns - 4; j++) {
            SXSSFCell cell0 = row.createCell((short) b++);
            cell0.setCellValue(calcTotalDynamicColumn(listOfObjects, j));
            cell0.setCellStyle(cellStyle);
        }

        SXSSFCell cells3 = row.createCell((short) b++);
        cells3.setCellValue(totalDailySalesReport.getOverallTotalDiscount());
        cells3.setCellStyle(cellStyle);

        SXSSFCell cells4 = row.createCell((short) b++);
        cells4.setCellValue(totalDailySalesReport.getOverallTotalGiro());
        cells4.setCellStyle(cellStyle);

        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("dailysalesreport"));
        } catch (IOException ex) {
            Logger.getLogger(DailySalesReportService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String exportPrinter(DailySalesReport dailySalesReport, List<BranchSetting> selectedBranchList, List<DailySalesReport> listOfObjects, DailySalesReport totalDailySalesReport, List<DailySalesReport> listSaleProcessDate,int sorting, boolean  sortby) {
        StringBuilder sb = new StringBuilder();

        int numberOfColumns = 0;
        List<SubPivot> subAnalysisPivotsList = null;
        if (!listOfObjects.isEmpty()) {
            subAnalysisPivotsList = listOfObjects.get(0).getSubList();
            numberOfColumns = subAnalysisPivotsList.size() + 4;
        }

        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getBeginDate())).append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySalesReport.getEndDate())).append(" </div> ");

        String branchName = "";
        if (selectedBranchList.isEmpty()) {
            branchName = sessionBean.getLoc().getString("all");
        } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
            branchName = sessionBean.getLoc().getString("all");
        } else {
            for (BranchSetting s : selectedBranchList) {
                branchName += " , " + s.getBranch().getName();
            }
            branchName = branchName.substring(3, branchName.length());
        }
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

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
                + "            font-size: 10px;"
                + "        }"
                + "   @page { size: landscape; }"
                + "    </style> <table> <tr>");

        sb.append("<th>").append(sessionBean.getLoc().getString("branch")).append("</th>");

        sb.append("<th>").append(sessionBean.getLoc().getString("processdate")).append("</th>");

        for (SubPivot subAnalysisPivot : subAnalysisPivotsList) {
            sb.append("<th>").append(subAnalysisPivot.getType().getTag()).append("</th>");
        }

        sb.append("<th>").append(sessionBean.getLoc().getString("totaldiscount")).append("</th>");

        sb.append("<th>").append(sessionBean.getLoc().getString("totalgiro")).append("</th>");

        sb.append("</tr>");

        int i = 0, count = 0;
        String rowGroup = "";
        String oldRowGroup = "";
        BigDecimal sumPrice = new BigDecimal(BigInteger.ZERO);
        double totalLiter = 0;
        int totalCount = 0;

        if (sorting == 2 && sortby) {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
            Collections.reverse(listSaleProcessDate);
        } else if (sorting == 2 && !sortby) {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
        } else {
            Collections.sort(listSaleProcessDate, (o1, o2) -> o2.getProcessDate().compareTo(o1.getProcessDate()));
            Collections.reverse(listSaleProcessDate);
        }

        for (DailySalesReport dailySaleReport : listSaleProcessDate) {
            Currency currency = new Currency(dailySaleReport.getCurrency().getId());
            rowGroup = "";
            rowGroup += dailySaleReport.getProcessDate();

            sumPrice = BigDecimal.ZERO;
            count = 0;
            for (DailySalesReport report : listOfObjects) {
                if (report.getProcessDate() != null) {
                    if (report.getProcessDate().equals(dailySaleReport.getProcessDate())) {
                        sumPrice = sumPrice.add(report.getTotalMoney());
                    }
                }
            }
            if (i == 0) {//birinci kayit için
                sb.append(" <tr> ");
                sb.append("<td colspan=\"3\" style=\"font-weight:bold\">").append(dailySaleReport.getProcessDate() == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySaleReport.getProcessDate())).append(" / ")
                        .append(sessionBean.loc.getString("salesprice")).append(" : ").append(sessionBean.getNumberFormat().format(sumPrice))
                        .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");

            } else if (!oldRowGroup.equals(rowGroup)) {
                sb.append(" <tr> ");
                sb.append("<td colspan=\"3\" style=\"font-weight:bold\">").append(dailySaleReport.getProcessDate() == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dailySaleReport.getProcessDate())).append(" / ")
                        .append(sessionBean.loc.getString("salesprice")).append(" : ").append(sessionBean.getNumberFormat().format(sumPrice))
                        .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");
            }

            sb.append(" <tr> ");

            for (DailySalesReport dsr : listOfObjects) {

                if (dsr.getProcessDate() != null) {

                    if (dsr.getProcessDate().equals(dailySaleReport.getProcessDate())) {
                        Currency currency1 = new Currency(dsr.getCurrency().getId());
                        sb.append("<tr>");

                        sb.append("<td>").append(dsr.getBranchSetting().getBranch().getName()).append("</td>");

                        sb.append("<td>").append(dsr.getProcessDate() == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), dsr.getProcessDate())).append("</td>");

                        for (SubPivot subAnalysisPivot : dsr.getSubList()) {
                            sb.append("<td style=\"text-align: right\">").append(subAnalysisPivot.getTotalPrice() != null ? sessionBean.getNumberFormat().format(subAnalysisPivot.getTotalPrice()) : BigDecimal.ZERO).append(sessionBean.currencySignOrCode(currency1.getId(), 0)).append("</td>");
                        }

                        sb.append("<td style=\"text-align: right\">").append(dsr.getTotalDiscount() != null ? sessionBean.getNumberFormat().format(dsr.getTotalDiscount()) : BigDecimal.ZERO).append(sessionBean.currencySignOrCode(currency1.getId(), 0)).append("</td>");

                        sb.append("<td style=\"text-align: right\">").append(dsr.getTotalMoney() != null ? sessionBean.getNumberFormat().format(dsr.getTotalMoney()) : BigDecimal.ZERO).append(sessionBean.currencySignOrCode(currency1.getId(), 0)).append("</td>");

                        sb.append("</tr>");

                    }

                }

            }

            sb.append(" </tr> ");
            i++;
            oldRowGroup = rowGroup;
        }

        sb.append("<tr>");

        sb.append("<td style=\"font-weight:bold;\">").append("").append("</th>");

        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(sessionBean.loc.getString("sum")).append("</th>");

        for (int j = 0; j < numberOfColumns - 4; j++) {
            sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(calcTotalDynamicColumn(listOfObjects, j)).append("</th>");
        }

        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(totalDailySalesReport.getOverallTotalDiscount()).append("</th>");

        sb.append("<td style=\"font-weight:bold; text-align: right;\">").append(totalDailySalesReport.getOverallTotalGiro()).append("</th>");

        sb.append("</tr>");

        sb.append(" </table> ");

        return sb.toString();

    }

    public String calcTotalDynamicColumn(List<DailySalesReport> listOfObjects, int index) {
        HashMap<Integer, BigDecimal> groupCurrencyTotalGiro = new HashMap<>();
        if (listOfObjects.size() > 1) {
            for (int i = 1; i < listOfObjects.size(); i++) {
                if (index != listOfObjects.get(0).getSubList().size()) {
                    if (groupCurrencyTotalGiro.containsKey(listOfObjects.get(i).getCurrency().getId())) {
                        BigDecimal old = groupCurrencyTotalGiro.get(listOfObjects.get(i).getCurrency().getId());
                        groupCurrencyTotalGiro.put(listOfObjects.get(i).getCurrency().getId(), old.add(listOfObjects.get(i).getSubList().get(index).getTotalPrice()));
                    } else {
                        groupCurrencyTotalGiro.put(listOfObjects.get(i).getCurrency().getId(), listOfObjects.get(i).getSubList().get(index).getTotalPrice());
                    }
                }
            }
        }

        int temp = 0;
        String saleTotal = "";

        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotalGiro.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTotal += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTotal += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (saleTotal.equals("")) {
            saleTotal = "0.0";
        }
        return saleTotal;
    }

}
