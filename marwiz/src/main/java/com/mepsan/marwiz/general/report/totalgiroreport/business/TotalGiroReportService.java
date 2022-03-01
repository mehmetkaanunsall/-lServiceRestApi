/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.03.2018 09:04:12
 */
package com.mepsan.marwiz.general.report.totalgiroreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.ITotalGiroReportDao;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.TotalGiroReport;
import com.mepsan.marwiz.general.report.totalgiroreport.dao.TotalGiroReportDao;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class TotalGiroReportService implements ITotalGiroReportService {

    @Autowired
    private ITotalGiroReportDao totalGiroReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setTotalGiroReportDao(ITotalGiroReportDao totalGiroReportDao) {
        this.totalGiroReportDao = totalGiroReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(TotalGiroReport obj) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";
        return where;
    }

    @Override
    public List<TotalGiroReport> findAll(String where, String branchList) {
        return totalGiroReportDao.findAll(where, branchList);
    }

    @Override
    public List<TotalGiroReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportPdf(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList) {

        PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

        Currency c = new Currency();

        try {
            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("totalgiroreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getBeginDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getEndDate()), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (totalGiroReport.getSelectedBranchList().isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (totalGiroReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting s : totalGiroReport.getSelectedBranchList()) {
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
            StaticMethods.createHeaderPdf("frmTotalGiroDatatable:dtbTotalGiro", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);
            for (TotalGiroReport tgiro : listOfGiroReports) {
                Currency currency = new Currency(tgiro.getCurrency().getId());

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(tgiro.getBranch().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(tgiro.getType().getTag(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(tgiro.getPrice()) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                c = new Currency(tgiro.getCurrency().getId());
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getCell());
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("giro") + " : "
                    + totalGiro, pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("totalgiroreport"));
        } catch (DocumentException e) {
            Logger.getLogger(TotalGiroReportDao.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void exportExcel(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList) {

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        int jRow = 0;

        SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell cellheader = header.createCell((short) 0);
        cellheader.setCellValue(sessionBean.getLoc().getString("totalgiroreport"));
        cellheader.setCellStyle(excelDocument.getStyleHeader());

        SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

        SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
        startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getBeginDate()));

        SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
        enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getEndDate()));

        String branchName = "";
        if (totalGiroReport.getSelectedBranchList().isEmpty()) {
            branchName = sessionBean.getLoc().getString("all");
        } else if (totalGiroReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
            branchName = sessionBean.getLoc().getString("all");
        } else {
            for (BranchSetting s : totalGiroReport.getSelectedBranchList()) {
                branchName += " , " + s.getBranch().getName();
            }
            branchName = branchName.substring(3, branchName.length());
        }

        SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
        branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

        SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

        StaticMethods.createHeaderExcel("frmTotalGiroDatatable:dtbTotalGiro", toogleList, "headerBlack", excelDocument.getWorkbook());
        jRow++;

        Currency c = new Currency();
        for (TotalGiroReport tgiro : listOfGiroReports) {

            int b = 0;
            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

            if (toogleList.get(0)) {
                row.createCell((short) b++).setCellValue(tgiro.getBranch().getName());
            }

            if (toogleList.get(1)) {
                row.createCell((short) b++).setCellValue(tgiro.getType().getTag());
            }
            if (toogleList.get(2)) {
                row.createCell((short) b++).setCellValue(StaticMethods.round((tgiro.getPrice()).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }
            c = new Currency(tgiro.getCurrency().getId());
        }
        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
        CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
        stylefooter.setAlignment(HorizontalAlignment.LEFT);
        SXSSFCell cell = row.createCell((short) 0);
        cell.setCellValue(sessionBean.getLoc().getString("sum") + " " + sessionBean.getLoc().getString("giro") + " : "
                + totalGiro);
        cell.setCellStyle(stylefooter);

        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("totalgiroreport"));
        } catch (IOException ex) {
            Logger.getLogger(TotalGiroReportDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String exportPrinter(TotalGiroReport totalGiroReport, List<TotalGiroReport> listOfGiroReports, String totalGiro, List<Boolean> toogleList, String branchList) {

        int numberOfColumns = 0;

        for (boolean b : toogleList) {
            if (b) {
                numberOfColumns++;
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getBeginDate())).append(" </div> ");
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), totalGiroReport.getEndDate())).append(" </div> ");

        String branchName = "";
        if (totalGiroReport.getSelectedBranchList().isEmpty()) {
            branchName = sessionBean.getLoc().getString("all");
        } else if (totalGiroReport.getSelectedBranchList().get(0).getBranch().getId() == 0) {
            branchName = sessionBean.getLoc().getString("all");
        } else {
            for (BranchSetting s : totalGiroReport.getSelectedBranchList()) {
                branchName += " , " + s.getBranch().getName();
            }
            branchName = branchName.substring(3, branchName.length());
        }

        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("branch")).append(" : ").append(branchName);

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
                + "    </style> <table> <tr>");

        StaticMethods.createHeaderPrint("frmTotalGiroDatatable:dtbTotalGiro", toogleList, "headerBlack", sb);

        sb.append(" </tr>  ");

        Currency c = new Currency();
        for (TotalGiroReport tgiro : listOfGiroReports) {

            Currency currency = new Currency(tgiro.getCurrency().getId());

            sb.append(" <tr> ");
            if (toogleList.get(0)) {
                sb.append("<td>").append(tgiro.getBranch().getName()).append("</td>");
            }

            if (toogleList.get(1)) {
                sb.append("<td>").append(tgiro.getType().getTag()).append("</td>");
            }
            if (toogleList.get(2)) {
                sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(tgiro.getPrice())).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
            }

            c = new Currency(tgiro.getCurrency().getId());
            sb.append(" </tr> ");
        }
        sb.append(" <tr> ");
        sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" ").append(sessionBean.getLoc().getString("giro")).append(" : ")
                .append(totalGiro)
                .append("</td>");
        sb.append(" </tr> ");

        sb.append(" </table> ");

        return sb.toString();
    }

    @Override
    public String createWhereBranch(List<BranchSetting> listOfBranch) {
        String branchList = "";
        for (BranchSetting branchSetting : listOfBranch) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        return branchList;
    }

}
