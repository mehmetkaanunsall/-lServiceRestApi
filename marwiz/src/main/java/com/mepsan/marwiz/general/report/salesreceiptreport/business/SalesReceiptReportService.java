/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 01:27:13
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.business;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStyleExcel;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.salesreceiptreport.dao.SalesReport;
import com.mepsan.marwiz.general.report.salesreceiptreport.dao.SalesReceiptReportDao;
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
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.general.report.salesreceiptreport.dao.ISalesReceiptReportDao;

public class SalesReceiptReportService implements ISalesReceiptReportService {

    @Autowired
    private ISalesReceiptReportDao salesReportDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSalesReportDao(ISalesReceiptReportDao salesReportDao) {
        this.salesReportDao = salesReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String createWhere(SalesReport obj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String where = "";

        String saleTypeId = "";
        for (String type : obj.getSaleTypeList()) {
            saleTypeId = saleTypeId + "," + type;
            if ("0".equals(type)) {
                saleTypeId = "";
                break;
            }
        }
        if (!saleTypeId.equals("")) {
            saleTypeId = saleTypeId.substring(1, saleTypeId.length());
            where = where + "INNER JOIN (\n"
                      + "	SELECT \n"
                      + "        DISTINCT(slp.sale_id)\n"
                      + "    FROM \n"
                      + "    	general.salepayment slp WHERE slp.type_id IN(" + saleTypeId + ") \n"
                      + ") slpj ON (slpj.sale_id = sl.id)\n";
        }

        where = where + (" WHERE sl.processdate BETWEEN '" + dateFormat.format(obj.getBeginDate()) + "' AND '" + dateFormat.format(obj.getEndDate()) + "' ")
                  + ((obj.getMinSalesPrice() != null && obj.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) ? " AND (sl.totalmoney) >= " + obj.getMinSalesPrice() + " " : "")
                  + ((obj.getMaxSalesPrice() != null && obj.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) ? " AND (sl.totalmoney) <= " + obj.getMaxSalesPrice() + " " : "")
                  + ((obj.getAccount().getId() != 0) ? " AND sl.account_id = " + obj.getAccount().getId() + " " : "");

        if (obj.getShiftNo() != null) {
            where += ((!obj.getShiftNo().equals("")) ? " AND sl.shiftno = '" + obj.getShiftNo().replace("'", "") + "' " : "");
        }

        if (obj.getReceipt().getReceiptNo() != null) {
            where += ((!obj.getReceipt().getReceiptNo().equals("")) ? " AND (rcp.receiptno = '" + obj.getReceipt().getReceiptNo().replace("'", "") + "' OR inv.documentnumber = '" + obj.getReceipt().getReceiptNo().replace("'", "") + "')" : "");

        }

        String cashierList = "";
        for (UserData user : obj.getListOfCashier()) {
            cashierList = cashierList + "," + String.valueOf(user.getId());
            if (user.getId() == 0) {
                cashierList = "";
                break;
            }
        }
        if (!cashierList.equals("")) {
            cashierList = cashierList.substring(1, cashierList.length());
            where = where + " AND sl.userdata_id IN(" + cashierList + ") ";
        }

        if (obj.getCashRegisterReceipt() == 1) {//yazar kasa fişi olanlar
            where += " AND (rcp.receiptno not ilike 'IR%' AND rcp.receiptno not ilike 'CRT%' )";

        } else if (obj.getCashRegisterReceipt() == 2) {//yazar kasa fişi olmayanlar
            where += " AND (rcp.receiptno ilike 'IR%' OR rcp.receiptno ilike 'CRT%' )";

        }

        if (obj.getSaleType().getId() == 1) {
            where += " AND sl.saletype_id = 81 ";
        } else if (obj.getSaleType().getId() == 2) {
            where += " AND sl.saletype_id <> 81 ";
        }

        return where;
    }

    @Override
    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Object param, String branchList, SalesReport salesReport) {
        return salesReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, findFieldGroup(((SalesReport) param).getSubTotal()), branchList, salesReport);
    }

    @Override
    public int count(String where, Object param, String branchList, SalesReport salesReport) {
        return salesReportDao.count(where, findFieldGroup(((SalesReport) param).getSubTotal()), branchList, salesReport);
    }

    @Override
    public List<SaleItem> findSaleItem(SalesReport salesReport) {
        return salesReportDao.findSaleItem(salesReport);
    }

    @Override
    public List<SalePayment> findSalePayment(SalesReport salesReport) {
        return salesReportDao.findSalePayment(salesReport);
    }

    @Override
    public String findFieldGroup(int subTotalValue) {
        String subTotal = "";
        switch (subTotalValue) {
            case 1:
                subTotal = "sl.account_id";
                break;
            case 2:
                subTotal = "sl.processdate";
                break;
            default:
                break;
        }

        return subTotal;
    }

    private String getRowGroupName(int type, ResultSet rs) throws SQLException {
        switch (type) {
            case 1:
                return rs.getString("accname") == null ? "" : (rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"));
            case 2:
                return rs.getDate("saledate") == null ? "" : StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getDate("saledate"));
            default:
                return "";
        }
    }

    @Override
    public void exportPdf(String where, SalesReport salesReport, List<Boolean> toogleList, List<SalesReport> listOfTotals, String branchList, List<BranchSetting> selectedBranchList) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        try {
            connection = salesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal()), branchList, salesReport));
            rs = prep.executeQuery();

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }
            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("salesreceiptreport"), pdfDocument.getFontHeader()));
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

            String param3 = "";

            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                param3 += sessionBean.getLoc().getString("shiftno") + " : " + salesReport.getShiftNo() + "     ";
            }

            if (salesReport.getReceipt().getReceiptNo() != null && !salesReport.getReceipt().getReceiptNo().equals("")) {
                param3 += sessionBean.getLoc().getString("invoice") + " " + sessionBean.getLoc().getString("documentnumber") + " / " + sessionBean.getLoc().getString("receiptno") + " : " + salesReport.getReceipt().getReceiptNo();
            }

            if (!"".equals(param3)) {
                pdfDocument.getCell().setPhrase(new Phrase(param3, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String param4 = "";
            if (salesReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesReport.getAccount().getName());
            }

            pdfDocument.getCell().setPhrase(new Phrase(param4, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String param5 = "";
            if (salesReport.getSaleTypeList().isEmpty()) {
                param5 += sessionBean.getLoc().getString("salestype") + " : " + sessionBean.getLoc().getString("all");
            } else {
                String saletypename = "";
                for (String s : salesReport.getSaleTypeList()) {
                    for (Type type : sessionBean.getTypes(15)) {
                        if (type.getId() == Integer.valueOf(s)) {
                            saletypename += " , " + type.getNameMap().get(sessionBean.getLangId()).getName();
                        }
                    }

                }
                saletypename = saletypename.substring(3, saletypename.length());
                param5 += sessionBean.getLoc().getString("salestype") + " : " + saletypename;
            }

            pdfDocument.getCell().setPhrase(new Phrase(param5, pdfDocument.getFont()));
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

            String cashierName = "";
            if (salesReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cashier") + " : " + cashierName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String cashregisterreceipt = "";
            if (salesReport.getCashRegisterReceipt() == 0) {
                cashregisterreceipt = sessionBean.getLoc().getString("all");
            } else if (salesReport.getCashRegisterReceipt() == 1) {
                cashregisterreceipt = sessionBean.getLoc().getString("withcashregisterreceipt");
            } else if (salesReport.getCashRegisterReceipt() == 2) {
                cashregisterreceipt = sessionBean.getLoc().getString("withoutcashregisterreceipt");
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("cashregisterreceipt") + " : " + cashregisterreceipt, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String cardoperation = "";
            if (salesReport.getSaleType().getId() == 0) {
                cardoperation = sessionBean.getLoc().getString("all");
            } else if (salesReport.getSaleType().getId() == 1) {
                cardoperation = sessionBean.getLoc().getString("yes");
            } else if (salesReport.getSaleType().getId() == 2) {
                cardoperation = sessionBean.getLoc().getString("no");
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("isthecardoperation") + " : " + cardoperation, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            StaticMethods.createHeaderPdf("frmSalesReportDatatable:dtbSalesReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);
            Currency slcurrency = new Currency();
            while (rs.next()) {
                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getString("slaccount_id") == null ? "" : rs.getString("slaccount_id");
                        break;
                    case 2:
                        rowGroup = rs.getDate("saledate") == null ? "" : rs.getString("saledate");
                        break;
                    default:
                        break;
                }
                Currency currency = new Currency(rs.getInt("slcurrency_id"));
                if (i == 0) {//birinci kayit için
                    pdfDocument.getCell().setBorder(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.TOP);

                    if (salesReport.getSubTotal() != 0) {
                        pdfDocument.getCell().setPhrase(new Phrase(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                  + rs.getInt("slidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
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
                                  + rs.getInt("slidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                  + sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFontHeader()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

                    }
                }

                if (toogleList.get(0)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate")), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(1)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("brnname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(2)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(3)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("usrname") + " " + rs.getString("usrsurname"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(4)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("invdocumnetnumber"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("rcpreceiptno"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                }
                if (toogleList.get(5)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("yes"), pdfDocument.getFont()));
                    } else {
                        pdfDocument.getDataCell().setPhrase(new Phrase(sessionBean.getLoc().getString("no"), pdfDocument.getFont()));
                    }
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                }
                if (toogleList.get(6)) {
                    pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("sltransactionno"), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                }
                if (toogleList.get(7)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaldiscount")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));

                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                }
                if (toogleList.get(8)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(9)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaltax")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }
                if (toogleList.get(10)) {
                    pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney")) + sessionBean.currencySignOrCode(currency.getId(), 0), pdfDocument.getFont()));
                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                }

                i++;
                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                    slcurrency.setId(rs.getInt("slcurrency_id"));
                }
                pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            }

            if (salesReport.getSubTotal() != 0) {
                pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totalprice") + ":"
                          + sessionBean.getNumberFormat().format(sltotalmoney) + sessionBean.currencySignOrCode(slcurrency.getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            }
            int operationCount = 0;
            for (SalesReport t : listOfTotals) {
                operationCount = operationCount + t.getCardOperationCount();
            }

            pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("operationcountwithcard") + " : "
                      + operationCount + " " + sessionBean.getLoc().getString("piece"), pdfDocument.getFontHeader()));
            pdfDocument.getCell().setColspan(numberOfColumns);
            pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            for (SalesReport total : listOfTotals) {
                pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("totaldiscount") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " " + "(" + total.getDiscountCount() + sessionBean.getLoc().getString("piece") + ") " + sessionBean.getNumberFormat().format(total.getTotalDiscount()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getCell().setColspan(numberOfColumns);
                pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            for (SalesReport total : listOfTotals) {
                pdfDocument.getCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " " + sessionBean.getNumberFormat().format(total.getTotalMoney()) + sessionBean.currencySignOrCode(total.getCurrency().getId(), 0), pdfDocument.getFontHeader()));
                pdfDocument.getCell().setColspan(numberOfColumns);
                pdfDocument.getCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("salesreceiptreport"));

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
                Logger.getLogger(SalesReceiptReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public void exportExcel(String where, SalesReport salesReport, List<Boolean> toogleList, List<SalesReport> listOfTotals, String branchList, List<BranchSetting> selectedBranchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {
            connection = salesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal()), branchList, salesReport));
            rs = prep.executeQuery();

            CellStyle dateFormatStyle = excelDocument.getWorkbook().createCellStyle();
            CreationHelper createHelper = excelDocument.getWorkbook().getCreationHelper();
            short dateFormat = createHelper.createDataFormat().getFormat(sessionBean.getUser().getLastBranch().getDateFormat());
            dateFormatStyle.setDataFormat(dateFormat);
            dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);

            int j = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(0);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("salesreceiptreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            j++;

            SXSSFRow empty = excelDocument.getSheet().createRow(j);
            j++;

            String param1 = sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getBeginDate()) + "    "
                      + sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), salesReport.getEndDate());

            SXSSFRow startdate = excelDocument.getSheet().createRow(j);
            startdate.createCell((short) 0).setCellValue(param1);
            j++;

            String param2 = "";

            if (salesReport.getMinSalesPrice() != null && salesReport.getMinSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Min) : " + salesReport.getMinSalesPrice() + "     ";
            }
            if (salesReport.getMaxSalesPrice() != null && salesReport.getMaxSalesPrice().compareTo(BigDecimal.valueOf(0)) != 0) {
                param2 += sessionBean.getLoc().getString("salesprice") + "(Max) : " + salesReport.getMaxSalesPrice();
            }
            if (!param2.equals("")) {
                SXSSFRow param2Cell = excelDocument.getSheet().createRow(j);
                param2Cell.createCell((short) 0).setCellValue(param2);
                j++;
            }

            String param3 = "";

            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                param3 += sessionBean.getLoc().getString("shiftno") + " : " + salesReport.getShiftNo() + "     ";
            }
            if (salesReport.getReceipt().getReceiptNo() != null && !salesReport.getReceipt().getReceiptNo().equals("")) {
                param3 += sessionBean.getLoc().getString("invoice") + " " + sessionBean.getLoc().getString("documentnumber") + " / " + sessionBean.getLoc().getString("receiptno") + " : " + salesReport.getReceipt().getReceiptNo();
            }

            if (!param3.equals("")) {
                SXSSFRow param3Cell = excelDocument.getSheet().createRow(j);
                param3Cell.createCell((short) 0).setCellValue(param3);
                j++;
            }

            String param4 = "";
            if (salesReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesReport.getAccount().getName());
            }

            SXSSFRow param4Cell = excelDocument.getSheet().createRow(j);
            param4Cell.createCell((short) 0).setCellValue(param4);
            j++;

            String param5 = "";
            if (salesReport.getSaleTypeList().isEmpty()) {
                param5 += sessionBean.getLoc().getString("salestype") + " : " + sessionBean.getLoc().getString("all");
            } else {
                String saletypename = "";
                for (String s : salesReport.getSaleTypeList()) {
                    for (Type type : sessionBean.getTypes(15)) {
                        if (type.getId() == Integer.valueOf(s)) {
                            saletypename += " , " + type.getNameMap().get(sessionBean.getLangId()).getName();
                        }
                    }

                }
                saletypename = saletypename.substring(3, saletypename.length());
                param5 += sessionBean.getLoc().getString("salestype") + " : " + saletypename;
            }

            SXSSFRow param5Cell = excelDocument.getSheet().createRow(j);
            param5Cell.createCell((short) 0).setCellValue(param5);
            j++;

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

            SXSSFRow branch = excelDocument.getSheet().createRow(j);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);
            j++;

            String cashierName = "";
            if (salesReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }

            SXSSFRow current = excelDocument.getSheet().createRow(j);
            current.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cashier") + " : " + cashierName);
            j++;

            String cashregisterreceipt = "";
            if (salesReport.getCashRegisterReceipt() == 0) {
                cashregisterreceipt = sessionBean.getLoc().getString("all");
            } else if (salesReport.getCashRegisterReceipt() == 1) {
                cashregisterreceipt = sessionBean.getLoc().getString("withcashregisterreceipt");
            } else if (salesReport.getCashRegisterReceipt() == 2) {
                cashregisterreceipt = sessionBean.getLoc().getString("withoutcashregisterreceipt");
            }
            SXSSFRow cashregister = excelDocument.getSheet().createRow(j);
            cashregister.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("cashregisterreceipt") + " : " + cashregisterreceipt);
            j++;

            String cardoperation = "";
            if (salesReport.getSaleType().getId() == 0) {
                cardoperation = sessionBean.getLoc().getString("all");
            } else if (salesReport.getSaleType().getId() == 1) {
                cardoperation = sessionBean.getLoc().getString("yes");
            } else if (salesReport.getSaleType().getId() == 2) {
                cardoperation = sessionBean.getLoc().getString("no");
            }
            SXSSFRow cardoperationx = excelDocument.getSheet().createRow(j);
            cardoperationx.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("isthecardoperation") + " : " + cardoperation);
            j++;

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(j);
            j++;

            SXSSFRow rowh = excelDocument.getSheet().createRow(j);

            int a = 0;
            CellStyle cellStyle = createCellStyleExcel("headerBlack", excelDocument.getWorkbook());
            if (toogleList.get(0)) {
                SXSSFCell cell = rowh.createCell((short) a++);
                cell.setCellValue(sessionBean.getLoc().getString("date"));
                cell.setCellStyle(excelDocument.getStyleHeader());
                cell.setCellStyle(cellStyle);

                SXSSFCell cellhour = rowh.createCell((short) a++);
                cellhour.setCellValue(sessionBean.getLoc().getString("date") + " - " + sessionBean.getLoc().getString("hours"));
                cellhour.setCellStyle(excelDocument.getStyleHeader());
                cellhour.setCellStyle(cellStyle);

            }
            if (toogleList.get(1)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("branch"));
                cell1.setCellStyle(excelDocument.getStyleHeader());
                cell1.setCellStyle(cellStyle);

            }
            if (toogleList.get(2)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("customer"));
                cell1.setCellStyle(excelDocument.getStyleHeader());
                cell1.setCellStyle(cellStyle);

            }
            if (toogleList.get(3)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("cashier"));
                cell1.setCellStyle(excelDocument.getStyleHeader());
                cell1.setCellStyle(cellStyle);

            }
            if (toogleList.get(4)) {
                SXSSFCell cell2 = rowh.createCell((short) a++);
                cell2.setCellValue(sessionBean.getLoc().getString("invoice") + " " + sessionBean.getLoc().getString("documentnumber") + "/" + sessionBean.getLoc().getString("receiptno"));
                cell2.setCellStyle(excelDocument.getStyleHeader());
                cell2.setCellStyle(cellStyle);

            }
            if (toogleList.get(5)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("isthecardoperation"));
                cell1.setCellStyle(excelDocument.getStyleHeader());
                cell1.setCellStyle(cellStyle);

            }
            if (toogleList.get(6)) {
                SXSSFCell cell1 = rowh.createCell((short) a++);
                cell1.setCellValue(sessionBean.getLoc().getString("trxno"));
                cell1.setCellStyle(excelDocument.getStyleHeader());
                cell1.setCellStyle(cellStyle);

            }
            if (toogleList.get(7)) {
                SXSSFCell cell3 = rowh.createCell((short) a++);
                cell3.setCellValue(sessionBean.getLoc().getString("discountamount"));
                cell3.setCellStyle(excelDocument.getStyleHeader());
                cell3.setCellStyle(cellStyle);

            }
            if (toogleList.get(8)) {
                SXSSFCell cell4 = rowh.createCell((short) a++);
                cell4.setCellValue(sessionBean.getLoc().getString("sum"));
                cell4.setCellStyle(excelDocument.getStyleHeader());
                cell4.setCellStyle(cellStyle);

            }
            if (toogleList.get(9)) {
                SXSSFCell cell5 = rowh.createCell((short) a++);
                cell5.setCellValue(sessionBean.getLoc().getString("taxprice"));
                cell5.setCellStyle(excelDocument.getStyleHeader());
                cell5.setCellStyle(cellStyle);

            }
            if (toogleList.get(10)) {
                SXSSFCell cell6 = rowh.createCell((short) a++);
                cell6.setCellValue(sessionBean.getLoc().getString("overalltotal"));
                cell6.setCellStyle(excelDocument.getStyleHeader());
                cell6.setCellStyle(cellStyle);

            }

            j++;

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);

            int count = 0;

            while (rs.next()) {
                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getString("slaccount_id") == null ? "" : rs.getString("slaccount_id");
                        break;
                    case 2:
                        rowGroup = rs.getDate("saledate") == null ? "" : rs.getString("saledate");
                        break;
                    default:
                        break;
                }
                if (i == 0) {//birinci kayit için
                    if (salesReport.getSubTotal() != 0) {
                        SXSSFRow row = excelDocument.getSheet().createRow(j);
                        SXSSFCell cell = row.createCell((short) 0);
                        cell.setCellValue(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                  + rs.getInt("slidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                  + StaticMethods.round(rs.getBigDecimal("totalmoney"), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell.setCellStyle(excelDocument.getStyleHeader());

                        j++;
                    }
                } else if (salesReport.getSubTotal() != 0) {
                    if (!oldRowGroup.equals(rowGroup)) {
                        SXSSFRow row1 = excelDocument.getSheet().createRow(j);
                        SXSSFCell cell1 = row1.createCell((short) 0);
                        cell1.setCellValue(sessionBean.getLoc().getString("totalprice") + ":"
                                  + StaticMethods.round(sltotalmoney, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell1.setCellStyle(excelDocument.getStyleHeader());
                        j++;
                        SXSSFRow row2 = excelDocument.getSheet().createRow(j);
                        SXSSFCell cell = row2.createCell((short) 0);
                        cell.setCellValue(getRowGroupName(salesReport.getSubTotal(), rs) + " / " + sessionBean.getLoc().getString("salecount") + " : "
                                  + rs.getInt("slidcount") + " / " + sessionBean.getLoc().getString("totalprice") + " : "
                                  + StaticMethods.round(rs.getBigDecimal("totalmoney"), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        cell.setCellStyle(excelDocument.getStyleHeader());

                        j++;
                    }
                }
                int b = 0;

                SXSSFRow row = excelDocument.getSheet().createRow(j);
                if (toogleList.get(0)) {

                    SXSSFCell cell0 = row.createCell((short) b++);
                    cell0.setCellValue(rs.getTimestamp("slprocessdate"));
                    cell0.setCellStyle(dateFormatStyle);

                    SXSSFCell cell_0 = row.createCell((short) b++);
                    cell_0.setCellValue(rs.getTimestamp("slprocessdate"));
                    cell_0.setCellStyle(excelDocument.getDateFormatStyle());
                    if (count == 0) {
                        count = 1;
                    }

                }
                if (toogleList.get(1)) {
                    row.createCell((short) b++).setCellValue(rs.getString("brnname"));
                }
                if (toogleList.get(2)) {
                    row.createCell((short) b++).setCellValue(rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"));
                }
                if (toogleList.get(3)) {
                    row.createCell((short) b++).setCellValue(rs.getString("usrname") + " " + rs.getString("usrsurname"));
                }
                if (toogleList.get(4)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        row.createCell((short) b++).setCellValue(rs.getString("invdocumnetnumber"));
                    } else {
                        row.createCell((short) b++).setCellValue(rs.getString("rcpreceiptno"));
                    }

                }
                if (toogleList.get(5)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("yes"));
                    } else {
                        row.createCell((short) b++).setCellValue(sessionBean.getLoc().getString("no"));
                    }
                }
                if (toogleList.get(6)) {
                    row.createCell((short) b++).setCellValue(rs.getString("sltransactionno"));
                }
                if (toogleList.get(7)) {
                    SXSSFCell discount = row.createCell((short) b++);
                    discount.setCellValue(StaticMethods.round(rs.getBigDecimal("sltotaldiscount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(8)) {
                    SXSSFCell discount = row.createCell((short) b++);
                    discount.setCellValue(StaticMethods.round(rs.getBigDecimal("sltotalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(9)) {
                    SXSSFCell tax = row.createCell((short) b++);
                    tax.setCellValue(StaticMethods.round(rs.getBigDecimal("sltotaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }
                if (toogleList.get(10)) {
                    SXSSFCell overall = row.createCell((short) b++);
                    overall.setCellValue(StaticMethods.round(rs.getBigDecimal("sltotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                }

                j++;
                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                }
                i++;

            }

            if (salesReport.getSubTotal() != 0) {
                SXSSFRow row = excelDocument.getSheet().createRow(j);
                SXSSFCell cell = row.createCell((short) 0);
                cell.setCellValue(sessionBean.getLoc().getString("totalprice") + ":"
                          + StaticMethods.round(sltotalmoney, sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(excelDocument.getStyleHeader());
                j++;

            }
            int operationCount = 0;
            for (SalesReport total : listOfTotals) {
                operationCount = operationCount + total.getCardOperationCount();
            }

            SXSSFRow row1 = excelDocument.getSheet().createRow(j++);
            SXSSFCell cell1 = row1.createCell((short) 0);
            CellStyle cellStyle2 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
            cellStyle2.setAlignment(HorizontalAlignment.LEFT);
            cell1.setCellValue(sessionBean.getLoc().getString("operationcountwithcard") + " : " + operationCount + " " + sessionBean.getLoc().getString("piece"));
            cell1.setCellStyle(cellStyle2);

            for (SalesReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(j++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("totaldiscount") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " "
                          + "(" + total.getDiscountCount() + sessionBean.getLoc().getString("piece") + ") " + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }
            for (SalesReport total : listOfTotals) {
                SXSSFRow row = excelDocument.getSheet().createRow(j++);
                SXSSFCell cell = row.createCell((short) 0);
                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellValue(sessionBean.getLoc().getString("sum") + " : " + (listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "") + " "
                          + StaticMethods.round(total.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cell.setCellStyle(cellStyle1);
            }

            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("salesreceiptreport"));
            } catch (IOException ex) {
                Logger.getLogger(SalesReceiptReportService.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SalesReceiptReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public String exportPrinter(String where, SalesReport salesReport, List<Boolean> toogleList, List<SalesReport> listOfTotals, String branchList, List<BranchSetting> selectedBranchList) {
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        StringBuilder sb = new StringBuilder();

        try {
            connection = salesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(salesReportDao.exportData(where, findFieldGroup(salesReport.getSubTotal()), branchList,salesReport));
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
            String param3 = "";
            if (salesReport.getShiftNo() != null && !salesReport.getShiftNo().equals("")) {
                param3 += sessionBean.getLoc().getString("shiftno") + " : " + salesReport.getShiftNo() + "     ";
            }
            if (salesReport.getReceipt().getReceiptNo() != null && !salesReport.getReceipt().getReceiptNo().equals("")) {
                param3 += sessionBean.getLoc().getString("invoice") + " " + sessionBean.getLoc().getString("documentnumber") + " / " + sessionBean.getLoc().getString("receiptno") + " : " + salesReport.getReceipt().getReceiptNo();
            }
            if (!param3.equals("")) {
                sb.append(" <div style=\"font-family:sans-serif;\">").append(param3).append(" </div> ");
            }

            String param4 = "";
            if (salesReport.getAccount().getId() == 0) {
                param4 += sessionBean.getLoc().getString("customer") + " : " + sessionBean.getLoc().getString("all");
            } else {
                param4 += sessionBean.getLoc().getString("customer") + " : " + (salesReport.getAccount().getName());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param4).append(" </div> ");

            String param5 = "";
            if (salesReport.getSaleTypeList().isEmpty()) {
                param5 += sessionBean.getLoc().getString("salestype") + " : " + sessionBean.getLoc().getString("all");
            } else {
                String saletypename = "";
                for (String s : salesReport.getSaleTypeList()) {
                    for (Type type : sessionBean.getTypes(15)) {
                        if (type.getId() == Integer.valueOf(s)) {
                            saletypename += " , " + type.getNameMap().get(sessionBean.getLangId()).getName();
                        }
                    }

                }
                saletypename = saletypename.substring(3, saletypename.length());
                param5 += sessionBean.getLoc().getString("salestype") + " : " + saletypename;
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(param5).append(" </div> ");

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

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("branch")).append(" : ").append(branchName);

            String cashierName = "";
            if (salesReport.getListOfCashier().isEmpty()) {
                cashierName = sessionBean.getLoc().getString("all");
            } else if (salesReport.getListOfCashier().get(0).getId() == 0) {
                cashierName = sessionBean.getLoc().getString("all");
            } else {
                for (UserData s : salesReport.getListOfCashier()) {
                    cashierName += " , " + s.getName() + " " + s.getSurname();
                }
                cashierName = cashierName.substring(3, cashierName.length());
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("cashier")).append(" : ").append(cashierName);

            String cashregisterreceipt = "";
            if (salesReport.getCashRegisterReceipt() == 0) {
                cashregisterreceipt = sessionBean.getLoc().getString("all");
            } else if (salesReport.getCashRegisterReceipt() == 1) {
                cashregisterreceipt = sessionBean.getLoc().getString("withcashregisterreceipt");
            } else if (salesReport.getCashRegisterReceipt() == 2) {
                cashregisterreceipt = sessionBean.getLoc().getString("withoutcashregisterreceipt");
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("cashregisterreceipt")).append(" : ").append(cashregisterreceipt);

            String cardoperation = "";
            if (salesReport.getSaleType().getId() == 0) {
                cardoperation = sessionBean.getLoc().getString("all");
            } else if (salesReport.getSaleType().getId() == 1) {
                cardoperation = sessionBean.getLoc().getString("yes");
            } else if (salesReport.getSaleType().getId() == 2) {
                cardoperation = sessionBean.getLoc().getString("no");
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.getLoc().getString("isthecardoperation")).append(" : ").append(cardoperation);

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

            StaticMethods.createHeaderPrint("frmSalesReportDatatable:dtbSalesReport", toogleList, "headerBlack", sb);

            int i = 0;
            String rowGroup = "";
            String oldRowGroup = "";
            BigDecimal sltotalmoney = BigDecimal.valueOf(0);
            Currency slcurrency = new Currency();

            while (rs.next()) {
                Currency currency = new Currency(rs.getInt("slcurrency_id"));

                switch (salesReport.getSubTotal()) {
                    case 1:
                        rowGroup = rs.getString("slaccount_id") == null ? "" : rs.getString("slaccount_id");
                        break;
                    case 2:
                        rowGroup = rs.getDate("saledate") == null ? "" : rs.getString("saledate");
                        break;
                    default:
                        break;
                }
                if (i == 0) {//brinci kayit için
                    if (salesReport.getSubTotal() != 0) {
                        sb.append(" <tr> ");
                        sb.append("<td style=\"font-weight:bold;\" colspan=\"").append(numberOfColumns).append("\">").append(getRowGroupName(salesReport.getSubTotal(), rs)).append(" / ").append(sessionBean.getLoc().getString("salecount"))
                                  .append(" : ").append(rs.getInt("slidcount")).append(" / ").append(sessionBean.getLoc().getString("totalprice"))
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
                                  .append(" : ").append(rs.getInt("slidcount")).append(" / ").append(sessionBean.getLoc().getString("totalprice"))
                                  .append(" : ").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("totalmoney")))
                                  .append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                        sb.append(" </tr> ");
                    }
                }

                sb.append(" <tr> ");

                if (toogleList.get(0)) {
                    sb.append("<td>").append(rs.getTimestamp("slprocessdate") == null ? "" : StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), rs.getTimestamp("slprocessdate"))).append("</td>");
                }
                if (toogleList.get(1)) {
                    sb.append("<td>").append(rs.getString("brnname")).append("</td>");
                }
                if (toogleList.get(2)) {
                    sb.append("<td>").append(rs.getString("accname") == null ? "" : (rs.getBoolean("accis_employee") ? rs.getString("accname") + " " + rs.getString("acctitle") : rs.getString("accname"))).append("</td>");
                }
                if (toogleList.get(3)) {
                    sb.append("<td>").append(rs.getString("usrname") == null ? "" : rs.getString("usrname") + " " + rs.getString("usrsurname")).append("</td>");
                }
                if (toogleList.get(4)) {
                    if (rs.getInt("slreceipt_id") == 0) {
                        sb.append("<td>").append(rs.getString("invdocumnetnumber") == null ? "" : rs.getString("invdocumnetnumber")).append("</td>");
                    } else {
                        sb.append("<td>").append(rs.getString("rcpreceiptno") == null ? "" : rs.getString("rcpreceiptno")).append("</td>");
                    }

                }
                if (toogleList.get(5)) {
                    if (rs.getInt("slsaletype_id") == 81) {
                        sb.append("<td>").append(sessionBean.getLoc().getString("yes")).append("</td>");
                    } else {
                        sb.append("<td>").append(sessionBean.getLoc().getString("no")).append("</td>");
                    }
                }
                if (toogleList.get(6)) {
                    sb.append("<td>").append(rs.getString("sltransactionno") == null ? "" : rs.getString("sltransactionno")).append("</td>");
                }
                if (toogleList.get(7)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaldiscount"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }
                if (toogleList.get(8)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalprice"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }
                if (toogleList.get(9)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotaltax"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");

                }
                if (toogleList.get(10)) {
                    sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("sltotalmoney"))).append(sessionBean.currencySignOrCode(currency.getId(), 0)).append("</td>");
                }

                sb.append(" </tr> ");
                i++;
                if (salesReport.getSubTotal() != 0) {
                    oldRowGroup = rowGroup;
                    sltotalmoney = rs.getBigDecimal("totalmoney");
                    slcurrency.setId(rs.getInt("slcurrency_id"));
                }

            }

            if (salesReport.getSubTotal() != 0) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                          .append(sessionBean.getLoc().getString("totalprice")).append(" : ").append(sessionBean.getNumberFormat().format(sltotalmoney))
                          .append(sessionBean.currencySignOrCode(slcurrency.getId(), 0)).append("</td>");
                sb.append(" </tr> ");
            }

            int operationCount = 0;
            for (SalesReport total : listOfTotals) {
                operationCount = operationCount + total.getCardOperationCount();
            }
            sb.append(" <tr> ");
            sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("operationcountwithcard")).append(" : ")
                      .append(" ").append(operationCount).append(" ").append(sessionBean.getLoc().getString("piece"))
                      .append("</td>");
            sb.append(" </tr> ");

            for (SalesReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("totaldiscount")).append(" : ")
                          .append((listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "")).append(" ").append("(").append(total.getDiscountCount()).append(sessionBean.getLoc().getString("piece")).append(") ").append(sessionBean.getNumberFormat().format(total.getTotalDiscount()))
                          .append(sessionBean.currencySignOrCode(total.getCurrency().getId(), 0)).append("</td>");
                sb.append(" </tr> ");

            }
            for (SalesReport total : listOfTotals) {
                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">").append(sessionBean.getLoc().getString("sum")).append(" : ")
                          .append((listOfTotals.size() > 1 ? " ( " + total.getBranchSetting().getBranch().getName() + " ) " : "")).append(" ").append(sessionBean.getNumberFormat().format(total.getTotalMoney()))
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
                Logger.getLogger(SalesReceiptReportDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return sb.toString();
    }

    @Override
    public List<SalesReport> totals(String where, String branchList, SalesReport salesReport) {
        return salesReportDao.totals(where, branchList, salesReport);
    }

}
