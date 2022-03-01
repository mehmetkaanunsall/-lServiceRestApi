/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:50:55 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReportDao;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.IAutomatShiftReportDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomatShiftReportService implements IAutomatShiftReportService {

    @Autowired
    IAutomatShiftReportDao automatShiftReportDao;

    @Autowired
    SessionBean sessionBean;

    public void setAutomatShiftReportDao(IAutomatShiftReportDao automatShiftReportDao) {
        this.automatShiftReportDao = automatShiftReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        List<Boolean> toogleListTemp = new ArrayList<>();
        int tempCount = 0;

        try {
            connection = automatShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDao.exportData(where));
            rs = prep.executeQuery();

            toogleListTemp.addAll(toogleList);
            toogleListTemp.set(toogleListTemp.size() - 1, false);
            toogleListTemp.set(toogleListTemp.size() - 2, false);
            for (Boolean b : toogleListTemp) {
                if (!b) {
                    tempCount++;
                }
            }
            if (tempCount == toogleListTemp.size()) {
                toogleListTemp.set(toogleListTemp.size() - 1, true);
                toogleListTemp.set(toogleListTemp.size() - 2, false);

            }

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleListTemp, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            String[] colums;
            String[] extension;

            colums = new String[]{"shfshitfno", "shfbegindate", "shfenddate"};
            extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};

            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    StaticMethods.pdfAddCell(pdfDocument, rs, toogleListTemp, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("shiftreport"));

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
                Logger.getLogger(AutomatShiftReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, List<Boolean> toogleList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        List<Boolean> toogleListTemp = new ArrayList<>();
        int tempCount = 0;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = automatShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDao.exportData(where));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("shiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);
            toogleListTemp.addAll(toogleList);
            toogleListTemp.set(toogleListTemp.size() - 1, false);
            toogleListTemp.set(toogleListTemp.size() - 2, false);

            for (Boolean b : toogleListTemp) {
                if (!b) {
                    tempCount++;
                }
            }
            if (tempCount == toogleListTemp.size()) {
                toogleListTemp.set(toogleListTemp.size() - 1, true);
                toogleListTemp.set(toogleListTemp.size() - 2, false);

            }

            StaticMethods.createHeaderExcel("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            String[] colums;
            String[] extension;

            colums = new String[]{"shfshitfno", "shfbegindate", "shfenddate"};
            extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};
            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                    StaticMethods.excelAddCell(row, rs, toogleListTemp, colums, excelDocument.getDateFormatStyle(), excelDocument.getWorkbook().getCreationHelper(), sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                }
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("shiftreport"));
            } catch (IOException ex) {
                Logger.getLogger(AutomatShiftReportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
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
                Logger.getLogger(AutomatShiftReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, List<Boolean> toogleList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        List<Boolean> toogleListTemp = new ArrayList<>();
        int tempCount = 0;
        try {
            connection = automatShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatShiftReportDao.exportData(where));
            rs = prep.executeQuery();

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

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

            toogleListTemp.addAll(toogleList);
            toogleListTemp.set(toogleListTemp.size() - 1, false);
            toogleListTemp.set(toogleListTemp.size() - 2, false);

            for (Boolean b : toogleListTemp) {
                if (!b) {
                    tempCount++;
                }
            }
            if (tempCount == toogleListTemp.size()) {
                toogleListTemp.set(toogleListTemp.size() - 1, true);
                toogleListTemp.set(toogleListTemp.size() - 2, false);

            }
            StaticMethods.createHeaderPrint("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", sb);

            String[] colums;
            String[] extension;

            colums = new String[]{"shfshitfno", "shfbegindate", "shfenddate"};
            extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};
            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    sb.append(" <tr> ");
                    StaticMethods.printAddCell(sb, rs, toogleListTemp, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    sb.append(" </tr> ");
                }
            }

            sb.append(" </table> ");
        } catch (SQLException e) {
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
                Logger.getLogger(AutomatShiftReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public int count(String where) {
        return automatShiftReportDao.count(where);
    }

    @Override
    public int create(AutomatShiftReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(AutomatShiftReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AutomatShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return automatShiftReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public List<AutomatSales> listOfSaleStock(AutomatShiftReport obj) {
        return automatShiftReportDao.listOfSaleStock(obj);
    }

    @Override
    public List<AutomatSales> listOfSalePlatform(AutomatShiftReport obj) {
        return automatShiftReportDao.listOfSalePlatform(obj);
    }

    @Override
    public List<AutomatSales> listOfSalePaymentType(AutomatShiftReport obj) {
        return automatShiftReportDao.listOfSalePaymentType(obj);
    }

}
