/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 03:04:03
 */
package com.mepsan.marwiz.general.report.marketshiftreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.accountextract.dao.AccountExtractDao;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftReportDao;
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
import com.mepsan.marwiz.general.report.marketshiftreport.dao.IMarketShiftReportDao;
import java.util.HashMap;

public class MarketShiftReportService implements IMarketShiftReportService {

    @Autowired
    public IMarketShiftReportDao marketShiftReportDao;

    @Autowired
    public SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarketShiftReportDao(IMarketShiftReportDao marketShiftReportDao) {
        this.marketShiftReportDao = marketShiftReportDao;
    }

    @Override
    public List<Shift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return marketShiftReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return marketShiftReportDao.count(where);
    }

    @Override
    public List<Sales> listOfSalePOS(Shift obj) {
        return marketShiftReportDao.listOfSalePOS(obj);
    }

    @Override
    public List<Sales> listOfSaleUser(Shift obj) {
        return marketShiftReportDao.listOfSaleUser(obj);
    }

    @Override
    public List<SalePayment> listOfSaleType(Shift obj) {
        return marketShiftReportDao.listOfSaleType(obj);
    }

    @Override
    public int create(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportPdf(String where, List<Boolean> toogleList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        List<Boolean> toogleListTemp = new ArrayList<>();
        int tempCount = 0;

        toogleListTemp.addAll(toogleList);
        toogleListTemp.set(toogleListTemp.size() - 1, Boolean.FALSE);
        toogleListTemp.set(toogleListTemp.size() - 2, Boolean.FALSE);

        for (Boolean b : toogleListTemp) {
            if (!b) {
                tempCount++;
            }
        }
        if (tempCount == toogleListTemp.size()) {
            toogleListTemp.set(toogleListTemp.size() - 1, true);
            toogleListTemp.set(toogleListTemp.size() - 2, true);
        }

        try {
            connection = marketShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(marketShiftReportDao.exportData(where));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleListTemp, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("marketshiftreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            String[] colums = new String[]{"shfshiftno", "shfbegindate", "shfenddate"};
            String[] extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};

            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    StaticMethods.pdfAddCell(pdfDocument, rs, toogleListTemp, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);

                    pdfDocument.getDocument().add(pdfDocument.getPdfTable());
                }
            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("marketshiftreport"));

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
                Logger.getLogger(MarketShiftReportDao.class.getName()).log(Level.SEVERE, null, ex1);
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
            connection = marketShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(marketShiftReportDao.exportData(where));
            rs = prep.executeQuery();

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("marketshiftreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

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
                toogleListTemp.set(toogleListTemp.size() - 2, true);
            }

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(1);
            StaticMethods.createHeaderExcel("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", excelDocument.getWorkbook());

            int i = 3;

            String[] colums = new String[]{"shfshiftno", "shfbegindate", "shfenddate"};
            String[] extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};

            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    SXSSFRow row = excelDocument.getSheet().createRow(i);
                    StaticMethods.excelAddCell(row, rs, toogleListTemp, colums, excelDocument.getDateFormatStyle(), excelDocument.getWorkbook().getCreationHelper(), sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    i++;
                }
            }

            StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("marketshiftreport"));

        } catch (SQLException | IOException e) {
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
                Logger.getLogger(MarketShiftReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    @Override
    public String exportPrinter(String where, List<Boolean> toogleList
    ) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();
        List<Boolean> toogleListTemp = new ArrayList<>();
        int tempCount = 0;

        try {
            connection = marketShiftReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(marketShiftReportDao.exportData(where));
            rs = prep.executeQuery();

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
                toogleListTemp.set(toogleListTemp.size() - 2, true);
            }

            StaticMethods.createHeaderPrint("frmShiftReport:dtbShiftReport", toogleListTemp, "headerBlack", sb);

            String[] colums = new String[]{"shfshiftno", "shfbegindate", "shfenddate"};
            String[] extension = new String[]{"", "HH:mm:ss", "HH:mm:ss"};
            if (tempCount != toogleListTemp.size()) {
                while (rs.next()) {
                    sb.append(" <tr> ");
                    StaticMethods.printAddCell(sb, rs, toogleListTemp, colums, sessionBean.getUser(), sessionBean.getNumberFormat(), extension);
                    sb.append(" </tr> ");
                }
            }
            sb.append(" </table> ");
        } catch (SQLException e) {
            Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, e);
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
                Logger.getLogger(AccountExtractDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();

    }


    @Override
    public Shift controlShiftPayment(Shift obj) {
        return marketShiftReportDao.controlShiftPayment(obj);
    }

    @Override
    public Shift findMarketShift(FinancingDocument financingDocument) {
        return marketShiftReportDao.findMarketShift(financingDocument);
    }
}
