/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 16:58:30
 */
package com.mepsan.marwiz.general.report.safeextract.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.safeextract.dao.ISafeExtractDao;
import org.apache.poi.ss.usermodel.CellStyle;

public class SafeExtractService implements ISafeExtractService {

    @Autowired
    private ISafeExtractDao safeReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeReportDao(ISafeExtractDao safeReportDao) {
        this.safeReportDao = safeReportDao;
    }

    @Override
    public List<SafeMovement> findAll(String where) {
        return safeReportDao.findAll(where);
    }

    /**
     * *
     *
     * @param selectedSafe Seçilen Kasalar
     * @return String Where Şartı
     */
    @Override
    public String createWhere(List<Safe> selectedSafe, List<Branch> selectedBranchList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        String sadeId = "";
        for (Safe safe : selectedSafe) {
            sadeId = sadeId + "," + safe.getId();
            if (0 == safe.getId()) {
                sadeId = "";
                break;
            }
        }
        if (!sadeId.equals("")) {
            sadeId = sadeId.substring(1, sadeId.length());
            where = where + " AND s.id IN(" + sadeId + ") ";
        }
        
        String branchList = "";
        for (Branch bs : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(bs.getId());
            if (bs.getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND sm.branch_id IN (" + branchList + ")";
        }

        return where;
    }

    @Override
    public void exportPdf(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance) {

        try {

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("safeextract"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
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
            
            String safeName = "";
            if (selectedSafe.isEmpty()) {
                safeName = sessionBean.getLoc().getString("all");
            } else {
                for (Safe s : selectedSafe) {
                    safeName += " , " + s.getName();
                }
                safeName = safeName.substring(3, safeName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase((sessionBean.getLoc().getString("safecodename")) + " : " + safeName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmSafeReportDatatableDetail:dtbSafeDetail", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            for (SafeMovement extract : listOfSafeExtract) {
                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(extract.getBranch().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(extract.getSafe().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("+" + sessionBean.getNumberFormat().format(extract.getTotalIncoming()) + sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase("-" + sessionBean.getNumberFormat().format(extract.getTotalOutcoming()) + sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(extract.getBalance()) + sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.createCellStylePdf("footer", pdfDocument, pdfDocument.getDataCell());
            if (toogleList.get(0)) {
                pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(1)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum"), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(2)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(subTotalIncome, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(3)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(subTotalOutcome, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            if (toogleList.get(4)) {
                pdfDocument.getDataCell().setPhrase(new Phrase(subTotalBalance, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
            }
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("safeextract"));

        } catch (DocumentException e) {
        } finally {

        }

    }

    @Override
    public void exportExcel(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance) {

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
        int jRow = 0;

        SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
        SXSSFCell cellheader = header.createCell((short) 0);
        cellheader.setCellValue(sessionBean.getLoc().getString("safeextract"));
        cellheader.setCellStyle(excelDocument.getStyleHeader());

        SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

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

        SXSSFRow brName = excelDocument.getSheet().createRow(jRow++);
        brName.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
        
        String safeName = "";
        if (selectedSafe.isEmpty()) {
            safeName = sessionBean.getLoc().getString("all");
        } else {
            for (Safe s : selectedSafe) {
                safeName += " , " + s.getName();
            }
            safeName = safeName.substring(3, safeName.length());
        }

        SXSSFRow current = excelDocument.getSheet().createRow(jRow++);
        current.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("safecodename") + " : " + safeName);

        SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

        StaticMethods.createHeaderExcel("frmSafeReportDatatableDetail:dtbSafeDetail", toogleList, "headerBlack", excelDocument.getWorkbook());
        jRow++;

        for (SafeMovement extract : listOfSafeExtract) {
            int b = 0;
            SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
            if (toogleList.get(0)) {
                row.createCell((short) b++).setCellValue(extract.getBranch().getName());
            }
            if (toogleList.get(1)) {
                row.createCell((short) b++).setCellValue(extract.getSafe().getName());
            }
            if (toogleList.get(2)) {
                row.createCell((short) b++).setCellValue(StaticMethods.round(extract.getTotalIncoming().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }
            if (toogleList.get(3)) {
                row.createCell((short) b++).setCellValue(StaticMethods.round(extract.getTotalOutcoming().multiply(BigDecimal.valueOf(-1)).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }
            if (toogleList.get(4)) {
                row.createCell((short) b++).setCellValue(StaticMethods.round(extract.getBalance().doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
            }
        }

        CellStyle stylefooter = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
        int c = 0;
        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
        if (toogleList.get(0)) {
            SXSSFCell cell0 = row.createCell((short) c++);
            cell0.setCellValue("");
            cell0.setCellStyle(stylefooter);

        }
        if (toogleList.get(1)) {
            SXSSFCell cell0 = row.createCell((short) c++);
            cell0.setCellValue(sessionBean.getLoc().getString("sum"));
            cell0.setCellStyle(stylefooter);

        }
        if (toogleList.get(2)) {
            SXSSFCell cell1 = row.createCell((short) c++);
            cell1.setCellValue(subTotalIncome);
            cell1.setCellStyle(stylefooter);
        }
        if (toogleList.get(3)) {
            SXSSFCell cell2 = row.createCell((short) c++);
            cell2.setCellValue(subTotalOutcome);
            cell2.setCellStyle(stylefooter);
        }
        if (toogleList.get(4)) {
            SXSSFCell cell3 = row.createCell((short) c++);
            cell3.setCellValue(subTotalBalance);
            cell3.setCellStyle(stylefooter);
        }
        try {
            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("safeextract"));
        } catch (IOException ex) {
            Logger.getLogger(SafeExtractService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String exportPrinter(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance) {

        StringBuilder sb = new StringBuilder();

        sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

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
        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

        
        String safeName = "";
        if (selectedSafe.isEmpty()) {
            safeName = sessionBean.getLoc().getString("all");
        } else {
            for (Safe s : selectedSafe) {
                safeName += " , " + s.getName();
            }
            safeName = safeName.substring(3, safeName.length());
        }

        sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("safecodename")).append(" : ").append(safeName).append(" </div> ");

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
                  + "    </style> <table>");

        StaticMethods.createHeaderPrint("frmSafeReportDatatableDetail:dtbSafeDetail", toogleList, "headerBlack", sb);

        for (SafeMovement extract : listOfSafeExtract) {

            sb.append(" <tr> ");

            if (toogleList.get(0)) {
                sb.append("<td>").append(extract.getBranch().getName() == null ? "" : extract.getBranch().getName()).append("</td>");
            }
            if (toogleList.get(1)) {
                sb.append("<td>").append(extract.getSafe().getName() == null ? "" : extract.getSafe().getName()).append("</td>");
            }
            if (toogleList.get(2)) {
                sb.append("<td style=\"text-align: right\">").append("+").append(sessionBean.getNumberFormat().format(extract.getTotalIncoming())).append(sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0)).append("</td>");
            }
            if (toogleList.get(3)) {
                sb.append("<td style=\"text-align: right\">").append("-").append(sessionBean.getNumberFormat().format(extract.getTotalOutcoming())).append(sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0)).append("</td>");
            }
            if (toogleList.get(4)) {
                sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(extract.getBalance())).append(sessionBean.currencySignOrCode(extract.getSafe().getCurrency().getId(), 0)).append("</td>");
            }

            sb.append(" </tr> ");

        }

        sb.append(" <tr> ");

        if (toogleList.get(0)) {
            sb.append("<td>").append("").append("</td>");
        }
        if (toogleList.get(1)) {
            sb.append("<td>").append(sessionBean.getLoc().getString("sum")).append("</td>");
        }
        if (toogleList.get(2)) {
            sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(subTotalIncome).append("</td>");
        }
        if (toogleList.get(3)) {
            sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(subTotalOutcome).append("</td>");
        }
        if (toogleList.get(4)) {
            sb.append("<td style=\"text-align: right; font-weight:bold;\">").append(subTotalBalance).append("</td>");
        }

        sb.append(" </tr> ");
        sb.append(" </table> ");

        return sb.toString();
    }

}
