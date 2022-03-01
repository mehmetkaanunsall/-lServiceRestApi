/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 01:52:21
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReportDao;
import com.mepsan.marwiz.automat.report.automatsalesreport.dao.IAutomatSalesReportDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

public class AutomatSalesReportService implements IAutomatSalesReportService {

    @Autowired
    private IAutomatSalesReportDao automatSalesReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setAutomatSalesReportDao(IAutomatSalesReportDao automatSalesReportDao) {
        this.automatSalesReportDao = automatSalesReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(AutomatSalesReport obj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where = "";

        where = where + ("AND asl.saledatetime BETWEEN '" + dateFormat.format(obj.getBeginDate()) + "' AND '" + dateFormat.format(obj.getEndDate()) + "' ")
                + ((obj.getMinSalesPrice() != null && obj.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) ? " AND (asl.totalmoney) >= " + obj.getMinSalesPrice() + " " : "")
                + ((obj.getMaxSalesPrice() != null && obj.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) ? " AND (asl.totalmoney) <= " + obj.getMaxSalesPrice() + " " : "");

        if (obj.getShiftNo() != null) {
            where += ((!obj.getShiftNo().equals("")) ? " AND asl.shiftno = '" + obj.getShiftNo().replace("'", "") + "' " : "");
        }

        String tankList = "";
        for (WashingTank tank : obj.getListOfTank()) {
            tankList = tankList + "," + String.valueOf(tank.getId());
            if (tank.getId() == 0) {
                tankList = "";
                break;
            }
        }
        if (!tankList.equals("")) {
            tankList = tankList.substring(1, tankList.length());
            where = where + " AND asl.tank_id IN(" + tankList + ") ";
        }

        String stockList = "";
        for (Stock stock : obj.getListOfStock()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND asl.stock_id IN(" + stockList + ") ";
        }

        String nozzleList = "";
        for (WashingNozzle nozzle : obj.getListOfNozzle()) {
            nozzleList = nozzleList + "," + String.valueOf(nozzle.getId());
            if (nozzle.getId() == 0) {
                nozzleList = "";
                break;
            }
        }
        if (!nozzleList.equals("")) {
            nozzleList = nozzleList.substring(1, nozzleList.length());
            where = where + " AND asl.nozzle_id IN(" + nozzleList + ") ";
        }

        String platformList = "";
        for (WashingPlatform washingPlatform : obj.getListOfPlatform()) {
            platformList = platformList + "," + String.valueOf(washingPlatform.getId());
            if (washingPlatform.getId() == 0) {
                platformList = "";
                break;
            }
        }
        if (!platformList.equals("")) {
            platformList = platformList.substring(1, platformList.length());
            where = where + " AND asl.platform_id IN(" + platformList + ") ";
        }

        String paymentList = "";
        for (String i : obj.getListOfPaymentType()) {
            paymentList = paymentList + "," + i;
        }
        if (!paymentList.equals("")) {
            paymentList = paymentList.substring(1, paymentList.length());
            where = where + " AND asl.paymenttype_id IN(" + paymentList + ") ";
        }
        String washingmachicneList = "";
        for (WashingMachicne washingmachicne : obj.getListOfWashingMachine()) {
            washingmachicneList = washingmachicneList + "," + String.valueOf(washingmachicne.getId());
            if (washingmachicne.getId() == 0) {
                washingmachicneList = "";
                break;
            }
        }
        if (!washingmachicneList.equals("")) {
            washingmachicneList = washingmachicneList.substring(1, washingmachicneList.length());
            where = where + " AND asl.washingmachine_id IN(" + washingmachicneList + ") ";
        }

        return where;
    }

    @Override
    public List<AutomatSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Object param) {
        return automatSalesReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, findFieldGroup(((AutomatSalesReport) param).getSubTotal()));
    }

    @Override
    public List<AutomatSalesReport> totals(String where) {
        return automatSalesReportDao.totals(where);
    }

    @Override
    public String findFieldGroup(int subTotalValue) {
        String subTotal = "";
        switch (subTotalValue) {
            case 1:
                subTotal = "asl.paymenttype_id";
                break;
            case 2:
                subTotal = "asl.saledatetime";
                break;
            default:
                break;
        }

        return subTotal;
    }

    private String getRowGroupName(int type, ResultSet rs) throws SQLException {
        switch (type) {
            case 1:
                return rs.getInt("aslpaymenttype_id") == 0 ? "" : rs.getInt("aslpaymenttype_id") == 1 ? sessionBean.getLoc().getString("cash")
                        : rs.getInt("aslpaymenttype_id") == 2 ? sessionBean.getLoc().getString("creditcard")
                        : rs.getInt("aslpaymenttype_id") == 3 ? sessionBean.getLoc().getString("mobilepayment") : "";
            case 2:
                return rs.getDate("aslsaledate") == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("aslsaledate"));
            default:
                return "";
        }
    }

    @Override
    public void exportPdf(String where, AutomatSalesReport salesReport, List<Boolean> toogleList, List<AutomatSalesReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            connection = automatSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatSalesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal())));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getBeginDate()) + "    "
                    + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getEndDate());

            pdfDocument.getCell().setPhrase(new Phrase(param1, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String param2 = "";

            if (salesReport.getMinSalesPrice() != null && salesReport.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Min) : " + salesReport.getMinSalesPrice() + "     ";
            }
            if (salesReport.getMaxSalesPrice() != null && salesReport.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Max) : " + salesReport.getMaxSalesPrice();
            }

            if (!"".equals(param2)) {
                pdfDocument.getCell().setPhrase(new Phrase(param2, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("shiftno") + " : " + salesReport.getShiftNo(), pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String paymentName = "";
            if (salesReport.getListOfPaymentType().isEmpty()) {
                paymentName = sessionBean.getLoc().getString("all");
            } else {
                for (String t : salesReport.getListOfPaymentType()) {
                    if (t.equals("0")) {
                        paymentName += " , " + sessionBean.getLoc().getString("cash");
                    } else if (t.equals("1")) {
                        paymentName += " , " + sessionBean.getLoc().getString("mobilepayment");
                    } else if (t.equals("2")) {
                        paymentName += " , " + sessionBean.getLoc().getString("barcode");
                    } else if (t.equals("3")) {
                        paymentName += " , " + "OPT";
                    }

                }
                paymentName = paymentName.substring(3, paymentName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("paymenttype") + " : " + paymentName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String washingMachineName = "";
            if (salesReport.getListOfWashingMachine().isEmpty()) {
                washingMachineName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingMachicne w : salesReport.getListOfWashingMachine()) {
                    washingMachineName += " , " + w.getName();
                }
                washingMachineName = washingMachineName.substring(3, washingMachineName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("washingmachicne") + " : " + washingMachineName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String platformName = "";
            if (salesReport.getListOfPlatform().isEmpty()) {
                platformName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingPlatform p : salesReport.getListOfPlatform()) {
                    platformName += " , " + p.getPlatformNo();
                }
                platformName = platformName.substring(3, platformName.length());
            }

            String nozzleName = "";
            if (salesReport.getListOfNozzle().isEmpty()) {
                nozzleName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingNozzle n : salesReport.getListOfNozzle()) {
                    nozzleName += " , " + n.getNozzleNo();
                }
                nozzleName = nozzleName.substring(3, nozzleName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("platform") + " : " + platformName
                    + " " + sessionBean.getLoc().getString("nozzle") + " : " + nozzleName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String tankName = "";
            if (salesReport.getListOfTank().isEmpty()) {
                tankName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingTank t : salesReport.getListOfTank()) {
                    tankName += " , " + t.getTankNo();
                }
                tankName = tankName.substring(3, tankName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("tank") + " : " + tankName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockList = "";
            if (salesReport.getListOfStock().isEmpty()) {
                stockList = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfStock().get(0).getId() == 0) {
                stockList = sessionBean.getLoc().getString("all");
            } else {
                for (Stock w : salesReport.getListOfStock()) {
                    stockList += " , " + w.getName();
                }
                stockList = stockList.substring(3, stockList.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockList, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmAutomatSalesReportDatatable:dtbAutomatSalesReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);
            Currency slcurrency = new Currency();
            while (rs.next()) {
                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getInt("aslpaymenttype_id") == 0 ? "" : Integer.toString(rs.getInt("aslpaymenttype_id"));
                        break;
                    case 2:
                        rowGroup = rs.getDate("aslsaledate") == null ? "" : rs.getString("aslsaledate");
                        break;
                    default:
                        break;
                }
                Currency currency = new Currency(rs.getInt("aslcurrency_id"));
                if (i == 0) {//birinci kayit için
                    pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

                    if (salesReport.getSubTotal() != 0) {
                        pdfDocument.getCell().setPhrase(new Phrase(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                + rs.getInt("aslidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                + sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFontHeader()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    }
                } else if (salesReport.getSubTotal() != 0) {
                    pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);
                    if (!oldRowGroup.equals(rowGroup)) {
                        pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprice") + ":"
                                + sessionBean.getNumberFormat().format(sltotalmoney) + sessionBean.currencySignOrCode(slcurrency.getId(), 0), pdfDocument.getFontHeader()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
                        pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                        pdfDocument.getCell().setPhrase(new Phrase(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                + rs.getInt("aslidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                + sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFontHeader()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    }
                }

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("aslsaledatetime")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslshiftno") != null ? rs.getString("aslshiftno") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslplatformno") != null ? rs.getString("aslplatformno") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("wshname") != null ? rs.getString("wshname") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslmacaddress") != null ? rs.getString("aslmacaddress") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(5)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getInt("aslstock_id") != 0 ? rs.getString("stckname") : sessionBean.getLoc().getString("forgottenmoney"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }

                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslbarcodeno") != null ? rs.getString("aslbarcodeno") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslcustomerrfid") != null ? rs.getString("aslcustomerrfid") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(8)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("aslmobileno") != null ? rs.getString("aslmobileno") : "", pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(String.valueOf(rs.getInt("asloperationtime")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    if (rs.getInt("aslpaymenttype_id") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cash"), pdfDocument.getFont()));
                    } else if (rs.getInt("aslpaymenttype_id") == 1) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("mobilepayment"), pdfDocument.getFont()));
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("barcode"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase("OPT", pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(11)) {
                    if (rs.getBoolean("aslis_online")) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("online"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("offline"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                }
                if (toogleList.get(12)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaldiscount")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                if (toogleList.get(13)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(14)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(15)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                i++;
                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                    slcurrency.setId(rs.getInt("aslcurrency_id"));
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }
            if (salesReport.getSubTotal() != 0) {
                pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprice") + ":"
                        + sessionBean.getNumberFormat().format(sltotalmoney) + sessionBean.currencySignOrCode(slcurrency.getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }

            for (AutomatSalesReport total : listOfTotals) {
                pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getCell().setColspan(numberOfColumns);
                pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("salesreport"));

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
                Logger.getLogger(AutomatSalesReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, AutomatSalesReport salesReport, List<Boolean> toogleList, List<AutomatSalesReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = automatSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatSalesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal())));
            rs = prep.executeQuery();

            int jRow = 0;
            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("salesreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow type = excelDocument.getSheet().createRow(jRow++);

            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getBeginDate()) + "    "
                    + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getEndDate());

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(param1);

            String param2 = "";

            if (salesReport.getMinSalesPrice() != null && salesReport.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Min) : " + salesReport.getMinSalesPrice() + "     ";
            }
            if (salesReport.getMaxSalesPrice() != null && salesReport.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Max) : " + salesReport.getMaxSalesPrice();
            }
            if (!param2.equals("")) {
                SXSSFRow param2Cell = excelDocument.getSheet().createRow(jRow++);
                param2Cell.createCell((short) 0).setCellValue(param2);
            }

            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                SXSSFRow param2Cell = excelDocument.getSheet().createRow(jRow++);
                param2Cell.createCell((short) 0).setCellValue(salesReport.getShiftNo());
            }

            String paymentName = "";
            if (salesReport.getListOfPaymentType().isEmpty()) {
                paymentName = sessionBean.getLoc().getString("all");
            } else {
                for (String t : salesReport.getListOfPaymentType()) {
                    if (t.equals("0")) {
                        paymentName += " , " + sessionBean.getLoc().getString("cash");
                    } else if (t.equals("1")) {
                        paymentName += " , " + sessionBean.getLoc().getString("mobilepayment");
                    } else if (t.equals("2")) {
                        paymentName += " , " + sessionBean.getLoc().getString("barcode");
                    } else if (t.equals("3")) {
                        paymentName += " , " + "OPT";
                    }

                }
                paymentName = paymentName.substring(3, paymentName.length());
            }
            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("paymenttype") + " : " + paymentName);

            String washingMachineName = "";
            if (salesReport.getListOfWashingMachine().isEmpty()) {
                washingMachineName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingMachicne w : salesReport.getListOfWashingMachine()) {
                    washingMachineName += " , " + w.getName();
                }
                washingMachineName = washingMachineName.substring(3, washingMachineName.length());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("washingmachicne") + " : " + washingMachineName);

            String platformName = "";
            if (salesReport.getListOfPlatform().isEmpty()) {
                platformName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingPlatform p : salesReport.getListOfPlatform()) {
                    platformName += " , " + p.getPlatformNo();
                }
                platformName = platformName.substring(3, platformName.length());
            }

            String nozzleName = "";
            if (salesReport.getListOfNozzle().isEmpty()) {
                nozzleName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingNozzle n : salesReport.getListOfNozzle()) {
                    nozzleName += " , " + n.getNozzleNo();
                }
                nozzleName = nozzleName.substring(3, nozzleName.length());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("platform") + " : " + platformName
                    + " " + sessionBean.getLoc().getString("nozzle") + " : " + nozzleName);

            String tankName = "";
            if (salesReport.getListOfTank().isEmpty()) {
                tankName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingTank t : salesReport.getListOfTank()) {
                    tankName += " , " + t.getTankNo();
                }
                tankName = tankName.substring(3, tankName.length());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("tank") + " : " + tankName);

            String stockList = "";
            if (salesReport.getListOfStock().isEmpty()) {
                stockList = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfStock().get(0).getId() == 0) {
                stockList = sessionBean.getLoc().getString("all");
            } else {
                for (Stock w : salesReport.getListOfStock()) {
                    stockList += " , " + w.getName();
                }
                stockList = stockList.substring(3, stockList.length());
            }

            excelDocument.getSheet().createRow(jRow++).createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockList);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmAutomatSalesReportDatatable:dtbAutomatSalesReport", toogleList, "headerBlack", excelDocument.getWorkbook());

            jRow++;

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);

            while (rs.next()) {

                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getInt("aslpaymenttype_id") == 0 ? "" : Integer.toString(rs.getInt("aslpaymenttype_id"));
                        break;
                    case 2:
                        rowGroup = rs.getDate("aslsaledate") == null ? "" : rs.getString("aslsaledate");
                        break;
                    default:
                        break;
                }
                if (i == 0) {//birinci kayit için
                    if (salesReport.getSubTotal() != 0) {
                        SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                        SXSSFCell cell = row.createCell((short) 0);
                        cell.setCellValue(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                + rs.getInt("aslidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                + StaticMethods.round(rs.getBigDecimal("totalmoney"), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell.setCellStyle(excelDocument.getStyleHeader());

                    }
                } else if (salesReport.getSubTotal() != 0) {
                    if (!oldRowGroup.equals(rowGroup)) {
                        SXSSFRow row1 = excelDocument.getSheet().createRow(jRow++);
                        SXSSFCell cell1 = row1.createCell((short) 0);
                        cell1.setCellValue(sessionBean.getLoc().getString("totalprice") + ":"
                                + StaticMethods.round(sltotalmoney, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell1.setCellStyle(excelDocument.getStyleHeader());

                        SXSSFRow row2 = excelDocument.getSheet().createRow(jRow++);
                        SXSSFCell cell = row2.createCell((short) 0);
                        cell.setCellValue(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                + rs.getInt("aslidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                + StaticMethods.round(rs.getBigDecimal("totalmoney"), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell.setCellStyle(excelDocument.getStyleHeader());

                    }
                }
                int b = 0;

                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                if (toogleList.get(0)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("aslsaledatetime"));
                    cell0.setCellStyle(excelDocument.getDateFormatStyle());
                }
                CellStyle cellStyleSpecial = excelDocument.getWorkbook().createCellStyle();
                cellStyleSpecial.setAlignment(HorizontalAlignment.RIGHT);
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslshiftno") != null ? rs.getString("aslshiftno") : "");
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslplatformno") != null ? rs.getString("aslplatformno") : "");
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("wshname") != null ? rs.getString("wshname") : "");
                }
                if (toogleList.get(4)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslmacaddress") != null ? rs.getString("aslmacaddress") : "");
                }

                if (toogleList.get(5)) {
                    row.createCell((short) b++).setCellValue(rs.getInt("aslstock_id") != 0 ? rs.getString("stckname") : sessionBean.getLoc().getString("forgottenmoney"));
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslbarcodeno") != null ? rs.getString("aslbarcodeno") : "");
                }
                if (toogleList.get(7)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslcustomerrfid") != null ? rs.getString("aslcustomerrfid") : "");
                }
                if (toogleList.get(8)) {
                    row.createCell((short) b++).setCellValue(rs.getString("aslmobileno") != null ? rs.getString("aslmobileno") : "");
                }
                if (toogleList.get(9)) {
                    SXSSFCell totality = row.createCell((short) b++);
                    totality.setCellValue(rs.getInt("asloperationtime"));
                    totality.setCellType(CellType.NUMERIC);
                }
                if (toogleList.get(10)) {
                    if (rs.getInt("aslpaymenttype_id") == 0) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("cash"));
                    } else if (rs.getInt("aslpaymenttype_id") == 1) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("mobilepayment"));
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("barcode"));
                    } else {
                        row.createCell((short) b++).setCellValue("OPT");
                    }
                }
                if (toogleList.get(11)) {
                    if (rs.getBoolean("aslis_online")) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("online"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("offline"));
                    }
                }
                if (toogleList.get(12)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotaldiscount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell0.setCellStyle(cellStyleSpecial);
                }
                if (toogleList.get(13)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()) );
                    cell0.setCellStyle(cellStyleSpecial);

                }
                if (toogleList.get(14)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell0.setCellStyle(cellStyleSpecial);

                }
                if (toogleList.get(15)) {
                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(StaticMethods.round(rs.getBigDecimal("asltotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    cell0.setCellStyle(cellStyleSpecial);

                }

                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                }
                i++;

            }

            if (salesReport.getSubTotal() != 0) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("totalprice") + ":"
                        + StaticMethods.round(sltotalmoney, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(excelDocument.getStyleHeader());
            }

            for (AutomatSalesReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("salesreport"));
            } catch (IOException ex) {
                Logger.getLogger(AutomatSalesReportService.class.getName()).log(Level.SEVERE, null, ex);
                
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
                Logger.getLogger(AutomatSalesReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, AutomatSalesReport salesReport, List<Boolean> toogleList, List<AutomatSalesReport> listOfTotals) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = automatSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(automatSalesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal())));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getBeginDate()) + "    "
                    + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getEndDate());

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param1).append(" </div> ");
            String param2 = "";

            if (salesReport.getMinSalesPrice() != null && salesReport.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Min) : " + salesReport.getMinSalesPrice() + "     ";
            }
            if (salesReport.getMaxSalesPrice() != null && salesReport.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Max) : " + salesReport.getMaxSalesPrice();
            }

            if (!param2.equals("")) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(param2).append(" </div> ");
            }

            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("shiftno")).append(" : ").append(salesReport.getShiftNo()).append(" </div> ");
            }

            String paymentName = "";
            if (salesReport.getListOfPaymentType().isEmpty()) {
                paymentName = sessionBean.getLoc().getString("all");
            } else {
                for (String t : salesReport.getListOfPaymentType()) {
                    if (t.equals("0")) {
                        paymentName += " , " + sessionBean.getLoc().getString("cash");
                    } else if (t.equals("1")) {
                        paymentName += " , " + sessionBean.getLoc().getString("mobilepayment");
                    } else if (t.equals("2")) {
                        paymentName += " , " + sessionBean.getLoc().getString("barcode");
                    } else if (t.equals("3")) {
                        paymentName += " , " + "OPT";
                    }

                }
                paymentName = paymentName.substring(3, paymentName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("paymenttype") + " : " + paymentName).append(" </div> ");

            String washingMachineName = "";
            if (salesReport.getListOfWashingMachine().isEmpty()) {
                washingMachineName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingMachicne w : salesReport.getListOfWashingMachine()) {
                    washingMachineName += " , " + w.getName();
                }
                washingMachineName = washingMachineName.substring(3, washingMachineName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("washingmachicne") + " : " + washingMachineName).append(" </div> ");

            String platformName = "";
            if (salesReport.getListOfPlatform().isEmpty()) {
                platformName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingPlatform p : salesReport.getListOfPlatform()) {
                    platformName += " , " + p.getPlatformNo();
                }
                platformName = platformName.substring(3, platformName.length());
            }

            String nozzleName = "";
            if (salesReport.getListOfNozzle().isEmpty()) {
                nozzleName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingNozzle n : salesReport.getListOfNozzle()) {
                    nozzleName += " , " + n.getNozzleNo();
                }
                nozzleName = nozzleName.substring(3, nozzleName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("platform") + " : " + platformName
                    + " " + sessionBean.getLoc().getString("nozzle") + " : " + nozzleName).append(" </div> ");

            String tankName = "";
            if (salesReport.getListOfTank().isEmpty()) {
                tankName = sessionBean.getLoc().getString("all");
            } else {
                for (WashingTank t : salesReport.getListOfTank()) {
                    tankName += " , " + t.getTankNo();
                }
                tankName = tankName.substring(3, tankName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("tank") + " : " + tankName).append(" </div> ");

            String stockList = "";
            if (salesReport.getListOfStock().isEmpty()) {
                stockList = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfStock().get(0).getId() == 0) {
                stockList = sessionBean.getLoc().getString("all");
            } else {
                for (Stock w : salesReport.getListOfStock()) {
                    stockList += " , " + w.getName();
                }
                stockList = stockList.substring(3, stockList.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("stock") + " : " + stockList).append(" </div> ");

            sb.append(" <div style=\"display:block; width:100%; height:20px; overflow:hidden;\">").append(" </div> ");

            sb.append(
                    " <style>"
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

            StaticMethods.createHeaderPrint("frmAutomatSalesReportDatatable:dtbAutomatSalesReport", toogleList, "headerBlack", sb);

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);
            Currency slcurrency = new Currency();

            while (rs.next()) {
                Currency currency = new Currency(rs.getInt("aslcurrency_id"));

                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getInt("aslpaymenttype_id") == 0 ? "" : Integer.toString(rs.getInt("aslpaymenttype_id"));
                        break;
                    case 2:
                        rowGroup = rs.getDate("aslsaledate") == null ? "" : rs.getString("aslsaledate");
                        break;
                    default:
                        break;
                }
                if (i == 0) {//brinci kayit için
                    if (salesReport.getSubTotal() != 0) {
                        sb.append(" <tr> ");
                        sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(getRowGroupName(salesReport.getSubTotal(), rs)).append(" / ").append(sessionBean.getLoc().getString("salecount"))
                                .append(" : ").append(rs.getInt("aslidcount")).append(" / ").append(sessionBean.getLoc().getString("totalprice"))
                                .append(" : ").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")))
                                .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        sb.append(" </tr> ");
                    }
                } else if (salesReport.getSubTotal() != 0) {
                    if (!oldRowGroup.equals(rowGroup)) {

                        sb.append(" <tr> ");
                        sb.append("<td style=\"font-weight:bold; text-align:right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totalprice")).append(" : ")
                                .append(sessionBean.getNumberFormat().format(sltotalmoney)).append(sessionBean.currencySignOrCode(slcurrency.getId(), 0)).append("</td>");
                        sb.append(" </tr> ");

                        sb.append(" <tr> ");
                        sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(getRowGroupName(salesReport.getSubTotal(), rs)).append(" / ").append(sessionBean.getLoc().getString("salecount"))
                                .append(" : ").append(rs.getInt("aslidcount")).append(" / ").append(sessionBean.getLoc().getString("totalprice"))
                                .append(" : ").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")))
                                .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        sb.append(" </tr> ");
                    }
                }

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("aslsaledatetime") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("aslsaledatetime"))).append("</td>");
                }

                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("aslshiftno") != null ? rs.getString("aslshiftno") : "").append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("aslplatformno") != null ? rs.getString("aslplatformno") : "").append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("wshname") != null ? rs.getString("wshname") : "").append("</td>");
                }

                if (toogleList.get(4)) {
                    sb.append("<td>").append(rs.getString("aslmacaddress") != null ? rs.getString("aslmacaddress") : "").append("</td>");
                }
                if (toogleList.get(5)) {
                    sb.append("<td>").append(rs.getInt("aslstock_id") != 0 ? rs.getString("stckname") : sessionBean.getLoc().getString("forgottenmoney")).append("</td>");
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("aslbarcodeno") != null ? rs.getString("aslbarcodeno") : "").append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td>").append(rs.getString("aslcustomerrfid") != null ? rs.getString("aslcustomerrfid") : "").append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td>").append(rs.getString("aslmobileno") != null ? rs.getString("aslmobileno") : "").append("</td>");
                }
                if (toogleList.get(9)) {
                    sb.append("<td style=\"text-align: right\">").append(String.valueOf(rs.getInt("asloperationtime"))).append("</td>");
                }
                if (toogleList.get(10)) {
                    if (rs.getInt("aslpaymenttype_id") == 0) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("cash")).append("</td>");
                    } else if (rs.getInt("aslpaymenttype_id") == 1) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("mobilepayment")).append("</td>");
                    } else if (rs.getInt("aslpaymenttype_id") == 2) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("barcode")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("mobilepayment")).append("</td>");
                    }
                }
                if (toogleList.get(11)) {
                    if (rs.getBoolean("aslis_online")) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("online")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("offline")).append("</td>");
                    }
                }
                if (toogleList.get(12)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaldiscount"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(13)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(14)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotaltax"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(15)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("asltotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
                i++;
                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                    slcurrency.setId(rs.getInt("aslcurrency_id"));
                }

            }

            if (salesReport.getSubTotal() != 0) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                        .append(sessionBean.getLoc().getString("totalprice")).append(" : ").append(sessionBean.getNumberFormat().format(sltotalmoney))
                        .append(sessionBean.currencySignOrCode(slcurrency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");
            }

            for (AutomatSalesReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(total.getTotalMoney()))
                        .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0)).append("</td>");
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
                Logger.getLogger(AutomatSalesReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

}
