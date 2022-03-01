/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:01:39 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.productreportsoldtogether.dao.IProdutReportSoldTogetherDao;
import com.mepsan.marwiz.general.report.productreportsoldtogether.dao.ProductReportSoldTogether;
import com.mepsan.marwiz.general.report.productreportsoldtogether.dao.ProdutReportSoldTogetherDao;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;

public class ProdutReportSoldTogetherService implements IProdutReportSoldTogetherService {

    @Autowired
    IProdutReportSoldTogetherDao produtReportSoldTogetherDao;

    @Autowired
    SessionBean sessionBean;

    public void setProdutReportSoldTogetherDao(IProdutReportSoldTogetherDao produtReportSoldTogetherDao) {
        this.produtReportSoldTogetherDao = produtReportSoldTogetherDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(ProductReportSoldTogether obj) {
        //     System.out.println("****createWhere****");
        String where = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("MM.dd.yyyy");
        switch (obj.getTimeInterval()) {
            case 0://İki Tarih Arasında
                where += " AND msl.processdate BETWEEN '" + dateFormat.format(obj.getBeginDate()) + "' AND '" + dateFormat.format(obj.getEndDate()) + "' ";
                break;
            case 1://Saatlik
                where += " AND msl.processdate BETWEEN '" + dateFormatWithoutTime.format(obj.getBeginDate()) + " 00:00:00 " + "' AND '" + dateFormatWithoutTime.format(obj.getEndDate()) + " 23:59:59 " + "'  ";

                if (obj.getTimezone() != 0 && !"0".equals(obj.getHourInterval().getId())) {
                    String first = "";
                    String end = "";
                    first = obj.getHourInterval().getId().substring(0, 2);
                    end = obj.getHourInterval().getId().substring(2, 4);
                    where += " AND date_part('hour' ,msl.processdate::TIMESTAMP)  BETWEEN " + first + " AND " + end + "";
                }
                break;
            case 2://Günlük
                where += " AND msl.processdate BETWEEN '" + dateFormatWithoutTime.format(obj.getBeginDate()) + " 00:00:00 " + "' AND '" + dateFormatWithoutTime.format(obj.getEndDate()) + " 23:59:59 " + "'  ";
                break;
            case 3://Haftalık
                where += " AND msl.processdate BETWEEN '" + dateFormatWithoutTime.format(obj.getBeginDate()) + " 00:00:00 " + "' AND '" + dateFormatWithoutTime.format(obj.getEndDate()) + " 23:59:59 " + "'  ";
                if (obj.getWeekDay() == 1) {
                    where += " AND ((extract(dow from msl.processdate))<>6 AND (extract(dow from msl.processdate))<>7)";
                } else if (obj.getWeekDay() == 2) {
                    where += " AND ((extract(dow from msl.processdate))=6 OR (extract(dow from msl.processdate))=7)";
                }
                break;
            case 4://Aylık
                if (obj.getSelectedYear() != 0) {
                    where += "AND date_part('year',msl.processdate)='" + obj.getSelectedYear() + "'";
                }
                break;
            case 5://Yillık
                break;
            case 6://Vardiya Dönemi
                if (obj.getShiftNo() != null) {
                    where += ((!obj.getShiftNo().equals("")) ? " AND msl.shiftno = '" + obj.getShiftNo().replace("'", "") + "' " : "");
                }
                break;
            default:
                break;
        }

        return where;
    }

    @Override
    public List<ProductReportSoldTogether> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductReportSoldTogether obj, BranchSetting selectedBranch) {
        return produtReportSoldTogetherDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj, selectedBranch);
    }

    @Override
    public int count(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch) {
        return produtReportSoldTogetherDao.count(where, obj, selectedBranch);
    }

    @Override
    public void exportPdf(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        try {
            connection = produtReportSoldTogetherDao.getDatasource().getConnection();
            prep = connection.prepareStatement(produtReportSoldTogetherDao.exportData(where, productReportSoldTogether, selectedBranch));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("productreportsoldtogether"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            /////TIME INTERVAL
            switch (productReportSoldTogether.getTimeInterval()) {
                case 0: {
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                case 1: {
                    //Saatlik
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("hourinterval") + " : " + ("0".equals(productReportSoldTogether.getHourInterval().getId()) ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getHourInterval().getName()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                case 2: {
                    //Günlük
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                case 3: {
                    //Haftalık
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("week") + " : " + (productReportSoldTogether.getWeekDay() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getWeekDay() == 1 ? sessionBean.getLoc().getString("weeekdays") : sessionBean.getLoc().getString("weekends")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                case 4: {
                    //Aylık
                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("year") + " : " + (productReportSoldTogether.getSelectedYear() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getSelectedYear()), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                case 6: {
                    //Vardiya

                    pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + productReportSoldTogether.getShiftNo(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                    break;
                }
                default:
                    break;
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + selectedBranch.getBranch().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + productReportSoldTogether.getStock1().getName(), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmProductReportSoldTogetherDatatable:dtbProductReportSoldTogether", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            while (rs.next()) {

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(selectedBranch.getBranch().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("acc1integrationcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stationintegrationcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    switch (productReportSoldTogether.getReportType()) {
                        case 2:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("ctydname"), pdfDocument.getFont()));
                            break;
                        case 3:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("dicname"), pdfDocument.getFont()));
                            break;
                        case 4:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("regname"), pdfDocument.getFont()));
                            break;
                        case 5:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stname"), pdfDocument.getFont()));
                            break;
                        default:
                            pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                            break;
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    switch (productReportSoldTogether.getTimeInterval()) {
                        case 1:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("hour"), pdfDocument.getFont()));
                            break;
                        case 2:
                            pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("day")), pdfDocument.getFont()));
                            break;
                        case 3:
                            String row = "";
                            row += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("firstWeekDay"));
                            row += " - ";
                            row += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("endWeekDay"));
                            pdfDocument.getDataCell().setPhrase(new Phrase(row, pdfDocument.getFont()));
                            break;
                        case 4:
                            pdfDocument.getDataCell().setPhrase(new Phrase(bringSaleMonthName(rs.getInt("month")), pdfDocument.getFont()));
                            break;
                        case 5:
                            pdfDocument.getDataCell().setPhrase(new Phrase(String.valueOf(rs.getInt("year")), pdfDocument.getFont()));
                            break;
                        case 6:
                            pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("mslshiftno"), pdfDocument.getFont()));
                            break;
                        default:
                            pdfDocument.getDataCell().setPhrase(new Phrase("", pdfDocument.getFont()));
                            break;
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getCode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getSupplierProductCode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getBarcode(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getCentralSupplier().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(productReportSoldTogether.getStock1().getSupplier().getName(), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(12)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stcksupplierproductcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(13)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckbarcode"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("stckname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(15)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("csppname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(16)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(17)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getDouble("quantity")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("productreportsoldtogether"));

        } catch (DocumentException | SQLException e) {
            //   System.out.println("----eee----" + e);
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
                Logger.getLogger(ProdutReportSoldTogetherDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat());

        try {
            connection = produtReportSoldTogetherDao.getDatasource().getConnection();
            prep = connection.prepareStatement(produtReportSoldTogetherDao.exportData(where, productReportSoldTogether, selectedBranch));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("productreportsoldtogether"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            /////TIME INTERVAL
            switch (productReportSoldTogether.getTimeInterval()) {
                case 0: {

                    SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
                    startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()));

                    SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
                    enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()));
                    break;
                }
                case 1: {
                    //Saatlik
                    SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
                    startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()));

                    SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
                    enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()));

                    SXSSFRow hourinterval = excelDocument.getSheet().createRow(jRow++);
                    hourinterval.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("hourinterval") + " : " + ("0".equals(productReportSoldTogether.getHourInterval().getId()) ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getHourInterval().getName()));

                    break;
                }
                case 2: {
                    //Günlük
                    SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
                    startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()));

                    SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
                    enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()));
                    break;
                }
                case 3: {
                    //Haftalık
                    SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
                    startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate()));

                    SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
                    enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate()));

                    SXSSFRow week = excelDocument.getSheet().createRow(jRow++);
                    week.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("week") + " : " + (productReportSoldTogether.getWeekDay() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getWeekDay() == 1 ? sessionBean.getLoc().getString("weeekdays") : sessionBean.getLoc().getString("weekends")));

                    break;
                }
                case 4: {
                    //Aylık
                    SXSSFRow monthly = excelDocument.getSheet().createRow(jRow++);
                    monthly.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("year") + " : " + (productReportSoldTogether.getSelectedYear() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getSelectedYear()));
                    break;
                }
                case 6: {
                    SXSSFRow shift = excelDocument.getSheet().createRow(jRow++);
                    shift.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("shiftno") + " : " + productReportSoldTogether.getShiftNo());
                    break;
                }
                default:
                    break;
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + selectedBranch.getBranch().getName());

            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + productReportSoldTogether.getStock1().getName());

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);
            StaticMethods.createHeaderExcel("frmProductReportSoldTogetherDatatable:dtbProductReportSoldTogether", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                int b = 0;
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (toogleList.get(0)) {
                    row.createCell((short) b++).setCellValue(selectedBranch.getBranch().getName());
                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("acc1integrationcode"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stationintegrationcode"));
                }
                if (toogleList.get(3)) {
                    switch (productReportSoldTogether.getReportType()) {
                        case 2:
                            row.createCell((short) b++).setCellValue(rs.getString("ctydname"));
                            break;
                        case 3:
                            row.createCell((short) b++).setCellValue(rs.getString("dicname"));
                            break;
                        case 4:
                            row.createCell((short) b++).setCellValue(rs.getString("regname"));
                            break;
                        case 5:
                            row.createCell((short) b++).setCellValue(rs.getString("stname"));
                            break;
                        default:
                            row.createCell((short) b++).setCellValue("");
                            break;
                    }
                }
                if (toogleList.get(4)) {
                    switch (productReportSoldTogether.getTimeInterval()) {
                        case 1:
                            row.createCell((short) b++).setCellValue(rs.getString("hour"));
                            break;
                        case 2:
                            SXSSFCell cell1 = row.createCell((short) b++);
                            cell1.setCellValue(rs.getDate("day"));
                            cell1.setCellStyle(excelDocument.getDateFormatStyle());
                            break;
                        case 3:
                            String a = "";
                            a += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("firstWeekDay"));
                            a += " - ";
                            a += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("endWeekDay"));
                            row.createCell((short) b++).setCellValue(a);
                            break;
                        case 4:
                            row.createCell((short) b++).setCellValue(bringSaleMonthName(rs.getInt("month")));
                            break;
                        case 5:
                            row.createCell((short) b++).setCellValue(rs.getInt("year"));
                            break;
                        case 6:
                            row.createCell((short) b++).setCellValue(rs.getString("mslshiftno"));
                            break;
                        default:
                            break;
                    }
                }

                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getCode());
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getSupplierProductCode());
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getBarcode());
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getName());
                }
                if (toogleList.get(9)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getCentralSupplier().getName());
                }
                if (toogleList.get(10)) {
                    row.createCell((short) b++).setCellValue(productReportSoldTogether.getStock1().getSupplier().getName());
                }
                if (toogleList.get(11)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckcode"));
                }
                if (toogleList.get(12)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stcksupplierproductcode"));
                }
                if (toogleList.get(13)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckbarcode"));
                }
                if (toogleList.get(14)) {
                    row.createCell((short) b++).setCellValue(rs.getString("stckname"));
                }
                if (toogleList.get(15)) {
                    row.createCell((short) b++).setCellValue(rs.getString("csppname"));
                }
                if (toogleList.get(16)) {
                    row.createCell((short) b++).setCellValue(rs.getString("accname"));
                }
                if (toogleList.get(17)) {
                    SXSSFCell quantity = row.createCell((short) b++);
                    quantity.setCellValue(StaticMethods.round(rs.getDouble("quantity"), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    quantity.setCellType(CellType.NUMERIC);
                }

            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("productreportsoldtogether"));
            } catch (IOException ex) {
                Logger.getLogger(ProdutReportSoldTogetherService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
            //  System.out.println("***e****" + e);
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
                Logger.getLogger(ProdutReportSoldTogetherDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, ProductReportSoldTogether productReportSoldTogether, List<Boolean> toogleList, BranchSetting selectedBranch) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = produtReportSoldTogetherDao.getDatasource().getConnection();
            prep = connection.prepareStatement(produtReportSoldTogetherDao.exportData(where, productReportSoldTogether, selectedBranch));
            rs = prep.executeQuery();

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            /////TIME INTERVAL
            switch (productReportSoldTogether.getTimeInterval()) {
                case 0: {
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate())).append(" </div> ");
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate())).append(" </div> ");
                    break;
                }
                case 1: {
                    //Saatlik
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate())).append(" </div> ");
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate())).append(" </div> ");

                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("hourinterval")).append(" : ").append(("0".equals(productReportSoldTogether.getHourInterval().getId()) ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getHourInterval().getName())).append(" </div> ");

                    break;
                }
                case 2: {
                    //Günlük
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate())).append(" </div> ");
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate())).append(" </div> ");
                    break;
                }
                case 3: {
                    //Haftalık
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getBeginDate())).append(" </div> ");
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), productReportSoldTogether.getEndDate())).append(" </div> ");

                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("week")).append(" : ").append((productReportSoldTogether.getWeekDay() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getWeekDay() == 1 ? sessionBean.getLoc().getString("weeekdays") : sessionBean.getLoc().getString("weekends"))).append(" </div> ");

                    break;
                }
                case 4: {
                    //Aylık
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("year")).append(" : ").append((productReportSoldTogether.getSelectedYear() == 0 ? sessionBean.getLoc().getString("all") : productReportSoldTogether.getSelectedYear())).append(" </div> ");
                    break;
                }
                case 6: {
                    //Vardiya
                    sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("shiftno")).append(" : ").append(productReportSoldTogether.getShiftNo()).append(" </div> ");

                    break;
                }
                default:
                    break;
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("branch")).append(" : ").append(selectedBranch.getBranch().getName());

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock")).append(" : ").append(productReportSoldTogether.getStock1().getName());

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
                    + "   @page { size: landscape; }"
                    + "    </style> <table> <tr>");

            StaticMethods.createHeaderPrint("frmProductReportSoldTogetherDatatable:dtbProductReportSoldTogether", toogleList, "headerBlack", sb);

            while (rs.next()) {

                sb.append(" <tr> ");

                 if (toogleList.get(0)) {
                    sb.append("<td>").append(selectedBranch.getBranch().getName() == null ? "" : selectedBranch.getBranch().getName()).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("acc1integrationcode") == null ? "" : rs.getString("acc1integrationcode")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("stationintegrationcode") == null ? "" : rs.getString("stationintegrationcode")).append("</td>");
                }
                if (toogleList.get(3)) {
                    switch (productReportSoldTogether.getReportType()) {
                        case 2:
                            sb.append("<td>").append(rs.getString("ctydname") == null ? "" : rs.getString("ctydname")).append("</td>");
                            break;
                        case 3:
                            sb.append("<td>").append(rs.getString("dicname") == null ? "" : rs.getString("dicname")).append("</td>");
                            break;
                        case 4:
                            sb.append("<td>").append(rs.getString("regname") == null ? "" : rs.getString("regname")).append("</td>");
                            break;
                        case 5:
                            sb.append("<td>").append(rs.getString("stname") == null ? "" : rs.getString("stname")).append("</td>");
                            break;
                        default:
                            sb.append("<td>").append("").append("</td>");
                            break;
                    }
                }
                if (toogleList.get(4)) {
                    switch (productReportSoldTogether.getTimeInterval()) {
                        case 1:
                            sb.append("<td>").append(rs.getString("hour") == null ? "" : rs.getString("hour")).append("</td>");
                            break;
                        case 2:
                            sb.append("<td>").append(rs.getDate("day") == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("day"))).append("</td>");
                            break;
                        case 3:
                            String a = "";
                            a += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("firstWeekDay"));
                            a += " - ";
                            a += StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("endWeekDay"));
                            sb.append("<td>").append(a == null ? "" : a).append("</td>");
                            break;
                        case 4:
                            sb.append("<td>").append(bringSaleMonthName(rs.getInt("month"))).append("</td>");
                            break;
                        case 5:
                            sb.append("<td>").append(rs.getInt("year")).append("</td>");
                            break;
                        case 6:
                            sb.append("<td>").append(rs.getString("mslshiftno") == null ? "" : rs.getString("mslshiftno")).append("</td>");
                            break;
                        default:
                            break;
                    }

                }

                if (toogleList.get(5)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getCode() == null ? "" : productReportSoldTogether.getStock1().getCode()).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getSupplierProductCode() == null ? "" : productReportSoldTogether.getStock1().getSupplierProductCode()).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getBarcode() == null ? "" : productReportSoldTogether.getStock1().getBarcode()).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getName() == null ? "" : productReportSoldTogether.getStock1().getName()).append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getCentralSupplier().getName() == null ? "" : productReportSoldTogether.getStock1().getCentralSupplier().getName()).append("</td>");
                }
                if (toogleList.get(10)) {
                    sb.append("<td>").append(productReportSoldTogether.getStock1().getSupplier().getName() == null ? "" : productReportSoldTogether.getStock1().getSupplier().getName()).append("</td>");
                }
                if (toogleList.get(11)) {
                    sb.append("<td>").append(rs.getString("stckcode") == null ? "" : rs.getString("stckcode")).append("</td>");
                }
                if (toogleList.get(12)) {
                    sb.append("<td>").append(rs.getString("stcksupplierproductcode") == null ? "" : rs.getString("stcksupplierproductcode")).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td>").append(rs.getString("stckbarcode") == null ? "" : rs.getString("stckbarcode")).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td>").append(rs.getString("stckname") == null ? "" : rs.getString("stckname")).append("</td>");
                }
                if (toogleList.get(15)) {
                    sb.append("<td>").append(rs.getString("csppname") == null ? "" : rs.getString("csppname")).append("</td>");
                }
                if (toogleList.get(16)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : rs.getString("accname")).append("</td>");
                }
                if (toogleList.get(17)) {
                    sb.append("<td style=\"text-align: right\" >").append(sessionBean.getNumberFormat().format(rs.getDouble("quantity"))).append("</td>");
                }

                sb.append(" </tr> ");
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
                Logger.getLogger(ProdutReportSoldTogetherDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    public String bringSaleMonthName(int month) {

        String name = "";
        switch (month) {
            case 1:
                name = sessionBean.getLoc().getString("january");
                break;
            case 2:
                name = sessionBean.getLoc().getString("february");
                break;
            case 3:
                name = sessionBean.getLoc().getString("march");
                break;
            case 4:
                name = sessionBean.getLoc().getString("april");
                break;
            case 5:
                name = sessionBean.getLoc().getString("may");
                break;
            case 6:
                name = sessionBean.getLoc().getString("june");
                break;
            case 7:
                name = sessionBean.getLoc().getString("july");
                break;
            case 8:
                name = sessionBean.getLoc().getString("august");
                break;
            case 9:
                name = sessionBean.getLoc().getString("september");
                break;
            case 10:
                name = sessionBean.getLoc().getString("october");
                break;
            case 11:
                name = sessionBean.getLoc().getString("november");
                break;
            case 12:
                name = sessionBean.getLoc().getString("december");
                break;
            default:
                break;
        }
        return name;

    }

}
