/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.10.2018 08:11:03
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lowagie.text.Phrase;
import com.mepsan.marwiz.general.common.StaticMethods;
import static com.mepsan.marwiz.general.common.StaticMethods.createCellStylePdf;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import com.mepsan.marwiz.general.report.profitmarginreport.business.ProfitMarginReportService;
import com.mepsan.marwiz.general.report.purchasesalesreport.dao.IPurchaseSalesReportDao;
import com.mepsan.marwiz.general.report.purchasesalesreport.dao.PurchaseSalesReport;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

public class PurchaseSalesReportService implements IPurchaseSalesReportService {

    @Autowired
    private IPurchaseSalesReportDao purchaseSalesReportDao;

    @Autowired
    SessionBean sessionBean;
    private List<PurchaseSalesReport> listOfPurchaseSaleReports;
    private List<PurchaseSalesReport> exportDataList;

    public void setPurchaseSalesReportDao(IPurchaseSalesReportDao purchaseSalesReportDao) {
        this.purchaseSalesReportDao = purchaseSalesReportDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<PurchaseSalesReport> getListOfPurchaseSaleReports() {
        return listOfPurchaseSaleReports;
    }

    public void setListOfPurchaseSaleReports(List<PurchaseSalesReport> listOfPurchaseSaleReports) {
        this.listOfPurchaseSaleReports = listOfPurchaseSaleReports;
    }

    public List<PurchaseSalesReport> getExportDataList() {
        return exportDataList;
    }

    public void setExportDataList(List<PurchaseSalesReport> exportDataList) {
        this.exportDataList = exportDataList;
    }

    @Override
    public List<PurchaseSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf, List<BranchSetting> selectedBranchList) {
        listOfPurchaseSaleReports = new ArrayList<>();
        listOfPurchaseSaleReports = purchaseSalesReportDao.findAll(first, pageSize, sortField, sortOrder, filters, where, obj, branchList, centralIngetrationInf);
        return listOfPurchaseSaleReports;
    }

    @Override
    public List<PurchaseSalesReport> count(PurchaseSalesReport obj, PurchaseSalesReport selectedObject) {
        listOfPurchaseSaleReports = new ArrayList<>();

        if (obj.getStringResult() != null) {
            try {
                listOfPurchaseSaleReports = convertJsonToObject(obj.getStringResult(), selectedObject);
            } catch (ParseException ex) {
                Logger.getLogger(PurchaseSalesReportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listOfPurchaseSaleReports;

    }

    @Override
    public String createWhere(PurchaseSalesReport obj, List<BranchSetting> branchList, int supplierType, boolean isCentralSupplier) {
        String where = " ";
        boolean isCentralIntegration = false;
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        for (BranchSetting brs : branchList) {
            if (brs.isIsCentralIntegration()) {
                isCentralIntegration = true;
                break;
            }
        }

        if (obj.getStocktaxgroup() != null) {
            if (obj.getStocktaxgroup().getId() > 0) {
                for (TaxGroup taxGroup : obj.getTaxGroupList()) {
                    if (obj.getStocktaxgroup().getId() == taxGroup.getId()) {
                        if (obj.isIsPurchase()) {
                            where = where + " AND invi.taxrate = " + taxGroup.getRate() + " ";
                        } else {
                            where = where + " AND sli.taxrate = " + taxGroup.getRate() + " ";

                        }
                        break;
                    }

                }
            }
        }

        if (obj.getAccount() != null) {
            if (obj.getAccount().getId() != 0) {
                if (obj.isIsPurchase()) {
                    where = where + " AND inv.account_id = " + obj.getAccount().getId() + " ";
                } else {
                    where = where + " AND sl.account_id = " + obj.getAccount().getId() + " ";
                }
            }
        }

        String stockList = "";
        for (Stock stock : obj.getStockList()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            if (obj.isIsPurchase()) {
                where = where + " AND invi.stock_id IN(" + stockList + ") ";
            } else {
                where = where + " AND sli.stock_id IN(" + stockList + ") ";
            }

        }

        String categoryList = "";
        for (Categorization category : obj.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }

        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            if (obj.isIsPurchase()) {
                where = where + " AND invi.stock_id  IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
            } else {
                where = where + " AND sli.stock_id IN (SELECT gsct.stock_id FROM inventory.stock_categorization_con gsct WHERE gsct.deleted=False AND gsct.categorization_id IN (" + categoryList + ") )";
            }
        }

        String accountList = "";
        for (Account account : obj.getListOfAccount()) {
            accountList = accountList + "," + String.valueOf(account.getId());
            if (account.getId() == 0) {
                accountList = "";
                break;
            }
        }
        if (!accountList.equals("")) {
            accountList = accountList.substring(1, accountList.length());
            where = where + " AND stck.supplier_id IN(" + accountList + ") ";
        }

        if (isCentralIntegration) {
            String centralSupplierList = "";
            for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                if (centralSupplier.getId() == 0) {
                    centralSupplierList = "";
                    break;
                }
            }
            if (!centralSupplierList.equals("")) {
                centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                where = where + " AND stck.centralsupplier_id IN(" + centralSupplierList + ") ";
            } else {

                if (isCentralSupplier) {
                    if (supplierType == 0) {
                        where = where + " AND (cspp.centersuppliertype_id != 2 OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 1) {
                        where = where + " AND (cspp.centersuppliertype_id NOT IN (1,2) OR cspp.centersuppliertype_id IS NULL) ";
                    } else if (supplierType == 2) {
                        where = where + " AND cspp.centersuppliertype_id = 1 ";
                    }
                }
            }
        }

//        if (obj.isIsPurchase()) {
//            where += " AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";
//        } else {
//            where += " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";
//        }
        return where;
    }

    @Override
    public List<TaxGroup> listOfTaxGroup(int type, List<BranchSetting> branchList) {
        return purchaseSalesReportDao.listOfTaxGroup(type, branchList);
    }

    @Override
    public String exportPrinter(String where, PurchaseSalesReport purchaseSalesReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, int centralIngetrationInf, boolean isCentralSupplier) {

        List<PurchaseSalesReport> listOfSubTotal = new ArrayList<>();
        String totalCount = "";

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        StringBuilder sb = new StringBuilder();
        BigDecimal totalMoney = BigDecimal.valueOf(0);
        try {

            connection = purchaseSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSalesReportDao.exportData(where, purchaseSalesReport, branchList, centralIngetrationInf));
            rs = prep.executeQuery();

            //Birim İçin
            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
            formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
            decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
            decimalFormatSymbolsUnit.setCurrencySymbol("");
            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

            Currency currency = new Currency(sessionBean.getUser().getLastBranch().getCurrency().getId());

            sb.append(" <div style=\"display:block; width:100%; height:10px; overflow:hidden;\">").append(" </div> ");

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("type")).append(" : ").append(purchaseSalesReport.isIsPurchase() ? sessionBean.loc.getString("purchase") : sessionBean.loc.getString("sales")).append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("startdate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getBeginDate())).append(" 00:00:00").append(" </div> ");
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("enddate")).append(" : ").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getEndDate())).append(" 23:59:59").append(" </div> ");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("branch")).append(" : ").append(branchName).append(" </div> ");

            String stockName = "";
            if (purchaseSalesReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSalesReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" : ").append(stockName).append(" </div> ");

            String categoryName = "";
            if (purchaseSalesReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : purchaseSalesReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("stock")).append(" ").append(sessionBean.loc.getString("category")).append(" : ").append(categoryName).append(" </div> ");

            String accountName = sessionBean.loc.getString("stock");
            if (purchaseSalesReport.getAccount() != null) {
                if (purchaseSalesReport.getAccount().getId() != 0) {
                    accountName = purchaseSalesReport.getAccount().getName();
                }
            }

            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("account")).append(" : ").append(accountName).append(" </div> ");

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSalesReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSalesReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("centralsupplier")).append(" : ").append(centralSupplierName).append(" </div> ");
            } else {
                String supplierName = "";
                if (purchaseSalesReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSalesReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("supplier")).append(" : ").append(supplierName).append(" </div> ");
            }

            String taxName = sessionBean.loc.getString("all");
            if (purchaseSalesReport.getStocktaxgroup() != null) {
                if (purchaseSalesReport.getStocktaxgroup().getId() != 0) {
                    taxName = purchaseSalesReport.getStocktaxgroup().getName() + " " + purchaseSalesReport.getStocktaxgroup().getRate().intValue();
                }
            }
            sb.append(" <div style=\"font-family:sans-serif;\">").append(sessionBean.loc.getString("taxgroup")).append(" : ").append(taxName).append(" </div> ");

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
                    + "    </style> <table> ");

            StaticMethods.createHeaderPrint("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport", toogleList, "headerBlack", sb);

            int numberOfColumns = 0;

            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            while (rs.next()) {
                totalCount = rs.getString("r_countresult");

                if (purchaseSalesReport.isIsPurchase()) {
                    totalMoney = rs.getBigDecimal("r_salestotalmoney");
                } else {
                    totalMoney = rs.getBigDecimal("r_totalmoney");
                }

                sb.append(" <tr> ");
                formatterUnit.setMaximumFractionDigits(rs.getInt("r_guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("r_guntunitsorting"));

                if (purchaseSalesReport.isIsPurchase()) {
                    if (toogleList.get(0)) {
                        sb.append("<td>").append(rs.getString("r_brnname")).append("</td>");
                    }
                    if (toogleList.get(1)) {
                        sb.append("<td>").append(rs.getString("r_stckcode")).append("</td>");
                    }
                    if (toogleList.get(2)) {
                        sb.append("<td>").append(rs.getString("r_stckcenterproductcode")).append("</td>");
                    }
                    if (toogleList.get(3)) {
                        sb.append("<td>").append(rs.getString("r_stckbarcode")).append("</td>");
                    }
                    if (toogleList.get(4)) {
                        sb.append("<td>").append(rs.getString("r_stckname")).append("</td>");
                    }
                    if (toogleList.get(5)) {
                        sb.append("<td>").append(StaticMethods.findCategories(rs.getString("r_category"))).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td>").append(rs.getString("r_csppname")).append("</td>");
                    }
                    if (toogleList.get(7)) {
                        sb.append("<td>").append(rs.getString("r_accname")).append("</td>");
                    }
                    if (toogleList.get(8)) {
                        sb.append("<td>").append(rs.getString("r_brname")).append("</td>");
                    }
                    if (toogleList.get(9)) {
                        String param = "";
                        if (rs.getBigDecimal("r_taxrate") != null) {
                            if (sessionBean.getUser().getLanguage().getId() == 1) {
                                param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("r_taxrate"));
                            } else {
                                param = sessionBean.getNumberFormat().format(rs.getBigDecimal("r_taxrate")) + "%";
                            }
                        }
                        sb.append("<td style=\"text-align: right\">").append(param).append("</td>");
                    }

                    if (toogleList.get(10)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("r_quantity"))).append(rs.getString("r_guntsortname")).append("</td>");
                    }
                    if (toogleList.get(11)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgsaleunitprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(12)) {

                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgpurchaseunitprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");

                    }
                    if (toogleList.get(13)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastsaleprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(14)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastpurchaseprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(15)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_purchasecost"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(16)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_salestotalmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(17)) {

                        BigDecimal result;
                        String param1 = "";
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param1 = "%" + sessionBean.getNumberFormat().format(result);
                        } else {
                            param1 = sessionBean.getNumberFormat().format(result) + "%";
                        }

                        sb.append("<td style=\"text-align: right\">").append(param1).append("</td>");
                    }
                    if (toogleList.get(18)) {
                        BigDecimal result2;
                        String param2 = "";
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result2 = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result2 = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param2 = "%" + sessionBean.getNumberFormat().format(result2);
                        } else {
                            param2 = sessionBean.getNumberFormat().format(result2) + "%";
                        }

                        sb.append("<td style=\"text-align: right\">").append(param2).append("</td>");
                    }

                    if (toogleList.get(19)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(20)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaltax"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(21)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaldiscount"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(22)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }

                } else {

                    if (toogleList.get(0)) {
                        sb.append("<td>").append(rs.getString("r_brnname")).append("</td>");
                    }

                    if (toogleList.get(1)) {
                        sb.append("<td>").append(rs.getString("r_stckcode")).append("</td>");
                    }
                    if (toogleList.get(2)) {
                        sb.append("<td>").append(rs.getString("r_stckcenterproductcode")).append("</td>");
                    }
                    if (toogleList.get(3)) {
                        sb.append("<td>").append(rs.getString("r_stckbarcode")).append("</td>");
                    }
                    if (toogleList.get(4)) {
                        sb.append("<td>").append(rs.getString("r_stckname")).append("</td>");
                    }
                    if (toogleList.get(5)) {
                        sb.append("<td>").append(StaticMethods.findCategories(rs.getString("r_category"))).append("</td>");
                    }
                    if (toogleList.get(6)) {
                        sb.append("<td>").append(rs.getString("r_csppname")).append("</td>");
                    }
                    if (toogleList.get(7)) {
                        sb.append("<td>").append(rs.getString("r_accname")).append("</td>");
                    }
                    if (toogleList.get(8)) {
                        sb.append("<td>").append(rs.getString("r_brname")).append("</td>");
                    }
                    if (toogleList.get(9)) {
                        String param = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param = "%" + (rs.getInt("r_taxrate"));
                        } else {
                            param = sessionBean.getNumberFormat().format(rs.getInt("r_taxrate")) + "%";
                        }
                        sb.append("<td style=\"text-align: right\">").append(param).append("</td>");
                    }

                    if (toogleList.get(10)) {
                        sb.append("<td style=\"text-align: right\">").append(formatterUnit.format(rs.getBigDecimal("r_quantity"))).append(rs.getString("r_guntsortname")).append("</td>");
                    }
                    if (toogleList.get(11)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgsaleunitprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(12)) {

                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgpurchaseunitprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");

                    }
                    if (toogleList.get(13)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastsaleprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(14)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastpurchaseprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(15)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_purchasecost"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }

                    if (toogleList.get(16)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_salestotalmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(17)) {
                        BigDecimal result;
                        String param1 = "";
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param1 = "%" + sessionBean.getNumberFormat().format(result);
                        } else {
                            param1 = sessionBean.getNumberFormat().format(result) + "%";
                        }

                        sb.append("<td style=\"text-align: right\">").append(param1).append("</td>");
                    }
                    if (toogleList.get(18)) {

                        BigDecimal result2;
                        String param2 = "";
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result2 = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result2 = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param2 = "%" + sessionBean.getNumberFormat().format(result2);
                        } else {
                            param2 = sessionBean.getNumberFormat().format(result2) + "%";
                        }

                        sb.append("<td style=\"text-align: right\">").append(param2).append("</td>");
                    }

                    if (toogleList.get(19)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalprice"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(20)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaltax"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(21)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaldiscount"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }
                    if (toogleList.get(22)) {
                        sb.append("<td style=\"text-align: right\">").append(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalmoney"))).append(sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0)).append("</td>");
                    }

                }

                sb.append(" </tr> ");

            }

            listOfSubTotal = convertJsonToObject(totalCount, purchaseSalesReport);

            for (PurchaseSalesReport pslr : listOfSubTotal) {
                String param1 = "";
                if (sessionBean.getUser().getLanguage().getId() == 1) {
                    param1 = "%" + sessionBean.getNumberFormat().format(pslr.getProfitpercentage());
                } else {
                    param1 = sessionBean.getNumberFormat().format(pslr.getProfitpercentage()) + "%";
                }

                sb.append(" <tr> ");
                sb.append("<td style=\"font-weight:bold; text-align: right;\" colspan=\"").append(numberOfColumns).append("\">")
                        .append(sessionBean.getLoc().getString("quantity")).append(" : ")
                        .append(StaticMethods.round(pslr.getQuantity(), sessionBean.getUser().getLastBranch().getCurrencyrounding()))
                        .append("   ")
                        .append(sessionBean.getLoc().getString("purchasecost")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getCost()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)).append("   ")
                        .append(sessionBean.getLoc().getString("profitprice")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getProfitAmount()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)).append("   ")
                        .append(sessionBean.getLoc().getString("profitpercentage")).append(" : ")
                        .append(param1)
                        .append("   ")
                        .append(sessionBean.getLoc().getString("sum")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getTotalPrice()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)).append("   ").append(sessionBean.getLoc().getString("taxprice")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getTotalTax()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)).append("   ").append(sessionBean.getLoc().getString("totaldiscount")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getTotalDiscount()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)).append("   ").append(sessionBean.getLoc().getString("overalltotal")).append(" : ")
                        .append(sessionBean.getNumberFormat().format(pslr.getTotalMoney()))
                        .append(sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0))
                        .append("</td>");
                sb.append(" </tr> ");
            }

            sb.append(" </table> ");

        } catch (Exception ex) {
        }

        return sb.toString();
    }

    @Override
    public void exportPdf(String where, PurchaseSalesReport purchaseSalesReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList, int centralIngetrationInf, List<PurchaseSalesReport> listOfPurchaseSaleReports, boolean isCentralSupplier) {
        List<PurchaseSalesReport> listOfSubTotal = new ArrayList<>();
        String totalCount = "";

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatterUnit.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
        decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbolsUnit.setCurrencySymbol("");
        ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);

        try {

            connection = purchaseSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSalesReportDao.exportData(where, purchaseSalesReport, branchList, centralIngetrationInf));
            rs = prep.executeQuery();

            PdfDocument pdfDocument = StaticMethods.preparePdf(toogleList, 0);

            pdfDocument.getHeader().setPhrase(new Phrase(sessionBean.getLoc().getString("purchasesalesreport"), pdfDocument.getFontHeader()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getHeader());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("type") + " : " + (purchaseSalesReport.isIsPurchase() ? sessionBean.getLoc().getString("purchase") : sessionBean.getLoc().getString("sales")), pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getBeginDate()) + " 00:00:00", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getEndDate()) + " 23:59:59", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("branch") + " : " + branchName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String stockName = "";
            if (purchaseSalesReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSalesReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " : " + stockName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String categoryName = "";
            if (purchaseSalesReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : purchaseSalesReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            String accountName = sessionBean.loc.getString("all");
            if (purchaseSalesReport.getAccount() != null) {
                if (purchaseSalesReport.getAccount().getId() != 0) {
                    accountName = purchaseSalesReport.getAccount().getName();
                }
            }

            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("currentname") + " : " + accountName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSalesReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSalesReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            } else {
                String supplierName = "";
                if (purchaseSalesReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSalesReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("supplier") + " : " + supplierName, pdfDocument.getFont()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getCell());
            }

            String taxName = sessionBean.loc.getString("all");
            if (purchaseSalesReport.getStocktaxgroup() != null) {
                if (purchaseSalesReport.getStocktaxgroup().getId() != 0) {
                    taxName = purchaseSalesReport.getStocktaxgroup().getName() + " " + purchaseSalesReport.getStocktaxgroup().getRate().intValue();
                }
            }
            pdfDocument.getCell().setPhrase(new Phrase(sessionBean.getLoc().getString("taxgroup") + " : " + taxName, pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
            pdfDocument.getPdfTable().addCell(pdfDocument.getCell());

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            int numberOfColumns = 0;
            for (boolean b : toogleList) {
                if (b) {
                    numberOfColumns++;
                }
            }

            //başlıkları ekledik
            StaticMethods.createHeaderPdf("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport", toogleList, "headerBlack", pdfDocument);

            pdfDocument.getDocument().add(pdfDocument.getPdfTable());

            System.out.println("----while begin----");

            while (rs.next()) {
                System.out.println("------while 11111111111111111");
                totalCount = rs.getString("r_countresult");
                BigDecimal totalMoney = BigDecimal.valueOf(0);

                if (purchaseSalesReport.isIsPurchase()) {
                    totalMoney = rs.getBigDecimal("r_salestotalmoney");
                } else {
                    totalMoney = rs.getBigDecimal("r_totalmoney");
                }
//                Currency currency = new Currency(exportDataList.get(i).getCurrency().getId());
                System.out.println("------while 22222222222222");

                formatterUnit.setMaximumFractionDigits(rs.getInt("r_guntunitsorting"));
                formatterUnit.setMinimumFractionDigits(rs.getInt("r_guntunitsorting"));
                System.out.println("------while 3333333333333333");

                if (purchaseSalesReport.isIsPurchase()) {
                    System.out.println("------while 444444444444444");

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_brnname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    System.out.println("------while 555555555555555");

                    if (toogleList.get(1)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(2)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckcenterproductcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(3)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckbarcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(4)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(5)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("r_category")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(6)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_csppname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(7)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_accname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(8)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_brname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    System.out.println("------while 66666666666666");
                    System.out.println("---taxrate---" + rs.getBigDecimal("r_taxrate"));
                    if (toogleList.get(9)) {
                        String param = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            if (rs.getBigDecimal("r_taxrate") != null) {
                                param = "%" + sessionBean.getNumberFormat().format(rs.getBigDecimal("r_taxrate"));
                            }

                        } else {
                            if (rs.getBigDecimal("r_taxrate") != null) {
                                param = sessionBean.getNumberFormat().format(rs.getBigDecimal("r_taxrate")) + "%";
                            }
                        }
                        pdfDocument.getDataCell().setPhrase(new Phrase(param, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    System.out.println("------while 77777777777777");

                    if (toogleList.get(10)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_quantity") == null ? "" : formatterUnit.format(rs.getBigDecimal("r_quantity")) + " " + rs.getString("r_guntsortname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 888888888888888888");

                    if (toogleList.get(11)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_avgsaleunitprice") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgsaleunitprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 9999999999999");

                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_avgpurchaseunitprice") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgpurchaseunitprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 10");

                    if (toogleList.get(13)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_lastsaleprice") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastsaleprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 11");

                    if (toogleList.get(14)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_lastpurchaseprice") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastpurchaseprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 12");

                    if (toogleList.get(15)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_cost") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_cost")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 13");

                    if (toogleList.get(16)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_salestotalmoney") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_salestotalmoney")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 14");

                    if (toogleList.get(17)) {

                        BigDecimal result;
                        String param1 = "";
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param1 = "%" + sessionBean.getNumberFormat().format(result);
                        } else {
                            param1 = sessionBean.getNumberFormat().format(result) + "%";
                        }

                        pdfDocument.getRightCell().setPhrase(new Phrase(param1, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 15");

                    if (toogleList.get(18)) {
                        BigDecimal result2;
                        String param2 = "";
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result2 = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result2 = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param2 = "%" + sessionBean.getNumberFormat().format(result2);
                        } else {
                            param2 = sessionBean.getNumberFormat().format(result2) + "%";
                        }

                        pdfDocument.getRightCell().setPhrase(new Phrase(param2, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 16");

                    if (toogleList.get(19)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_totalprice") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 17");

                    if (toogleList.get(20)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_totaltax") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaltax")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 18");

                    if (toogleList.get(21)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_totaldiscount") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaldiscount")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 19");

                    if (toogleList.get(22)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(rs.getBigDecimal("r_totalmoney") == null ? "" : sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalmoney")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    System.out.println("------while 20");

                } else {

                    if (toogleList.get(0)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_brnname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }

                    if (toogleList.get(1)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(2)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckcenterproductcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(3)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckbarcode"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(4)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_stckname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(5)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(StaticMethods.findCategories(rs.getString("r_category")), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(6)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_csppname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(7)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_accname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(8)) {
                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString("r_brname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                    }
                    if (toogleList.get(9)) {
                        String param = "";
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            if (rs.getBigDecimal("r_taxrate") != null) {
                                param = "%" + (rs.getBigDecimal("r_taxrate"));
                            }
                        } else {
                            if (rs.getBigDecimal("r_taxrate") != null) {
                                param = sessionBean.getNumberFormat().format(rs.getBigDecimal("r_taxrate")) + "%";
                            }
                        }
                        pdfDocument.getRightCell().setPhrase(new Phrase(param, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    if (toogleList.get(10)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(formatterUnit.format(rs.getBigDecimal("r_quantity")) + " " + rs.getString("r_guntsortname"), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(11)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgsaleunitprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(12)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_avgpurchaseunitprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                    }
                    if (toogleList.get(13)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastsaleprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(14)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_lastpurchaseprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    if (toogleList.get(15)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_cost")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    if (toogleList.get(16)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_salestotalmoney")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(17)) {
                        BigDecimal result;
                        String param1 = "";
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param1 = "%" + sessionBean.getNumberFormat().format(result);
                        } else {
                            param1 = sessionBean.getNumberFormat().format(result) + "%";
                        }

                        pdfDocument.getRightCell().setPhrase(new Phrase(param1, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(18)) {

                        BigDecimal result2;
                        String param2 = "";
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result2 = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result2 = BigDecimal.ZERO;
                        }
                        if (sessionBean.getUser().getLanguage().getId() == 1) {
                            param2 = "%" + sessionBean.getNumberFormat().format(result2);
                        } else {
                            param2 = sessionBean.getNumberFormat().format(result2) + "%";
                        }

                        pdfDocument.getRightCell().setPhrase(new Phrase(param2, pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                    if (toogleList.get(19)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalprice")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(20)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaltax")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(21)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totaldiscount")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }
                    if (toogleList.get(22)) {
                        pdfDocument.getRightCell().setPhrase(new Phrase(sessionBean.getNumberFormat().format(rs.getBigDecimal("r_totalmoney")) + " " + sessionBean.currencySignOrCode(rs.getInt("r_currency_id"), 0), pdfDocument.getFont()));
                        pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                    }

                }

            }

            System.out.println("----while end----");

//            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
            listOfSubTotal = convertJsonToObject(totalCount, purchaseSalesReport);
            System.out.println("----for total begin----");

            for (PurchaseSalesReport pslr : listOfSubTotal) {
                String param2 = "";
                if (sessionBean.getUser().getLanguage().getId() == 1) {
                    param2 = "%" + sessionBean.getNumberFormat().format(pslr.getProfitpercentage());
                } else {
                    param2 = sessionBean.getNumberFormat().format(pslr.getProfitpercentage()) + "%";
                }
                pdfDocument.getRightCell().setPhrase(new Phrase(
                        sessionBean.getLoc().getString("quantity") + " : " + sessionBean.getNumberFormat().format(pslr.getQuantity())
                        + "    " + sessionBean.getLoc().getString("purchasecost") + " : " + sessionBean.getNumberFormat().format(pslr.getCost()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)
                        + "    " + sessionBean.getLoc().getString("profitprice") + " : " + sessionBean.getNumberFormat().format(pslr.getProfitAmount()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)
                        + "    " + sessionBean.getLoc().getString("profitpercentage") + " : " + param2, pdfDocument.getFontHeader()));
                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());

                pdfDocument.getRightCell().setPhrase(new Phrase(
                        sessionBean.getLoc().getString("sum") + " : " + sessionBean.getNumberFormat().format(pslr.getTotalPrice()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)
                        + "    " + sessionBean.getLoc().getString("taxprice") + " : " + sessionBean.getNumberFormat().format(pslr.getTotalTax()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)
                        + "    " + sessionBean.getLoc().getString("totaldiscount") + " : " + sessionBean.getNumberFormat().format(pslr.getTotalDiscount()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0)
                        + "    " + sessionBean.getLoc().getString("overalltotal") + " : " + sessionBean.getNumberFormat().format(pslr.getTotalMoney()) + sessionBean.currencySignOrCode(pslr.getCurrency().getId(), 0), pdfDocument.getFontHeader()));

                pdfDocument.getRightCell().setColspan(numberOfColumns);
                pdfDocument.getRightCell().setBackgroundColor(Color.LIGHT_GRAY);
                pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
            }
            StaticMethods.writePDFToResponse(pdfDocument, sessionBean.getLoc().getString("purchasesalesreport"));
            System.out.println("----for total end----");

        } catch (Exception ex) {

            System.out.println("----catch pdf --" + ex.getMessage());
        }

    }

    @Override
    public void exportExcel(String where, PurchaseSalesReport purchaseSalesReport,
            List<Boolean> toogleList,
            String branchList, List<BranchSetting> selectedBranchList,
            int centralIngetrationInf, boolean isCentralSupplier
    ) {

        List<PurchaseSalesReport> listOfSubTotal = new ArrayList<>();
        String totalCount = "";

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;

        ExcelDocument excelDocument = StaticMethods.prepareExcel(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");

        try {

            connection = purchaseSalesReportDao.getDatasource().getConnection();
            prep = connection.prepareStatement(purchaseSalesReportDao.exportData(where, purchaseSalesReport, branchList, centralIngetrationInf));
            rs = prep.executeQuery();

            int jRow = 0;

            SXSSFRow header = excelDocument.getSheet().createRow(jRow++);
            SXSSFCell cellheader = header.createCell((short) 0);
            cellheader.setCellValue(sessionBean.getLoc().getString("purchasesalesreport"));
            cellheader.setCellStyle(excelDocument.getStyleHeader());

            SXSSFRow empty = excelDocument.getSheet().createRow(jRow++);

            SXSSFRow type = excelDocument.getSheet().createRow(jRow++);
            type.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("type") + " : " + (purchaseSalesReport.isIsPurchase() ? sessionBean.getLoc().getString("purchase") : sessionBean.getLoc().getString("sales")));

            SXSSFRow startdate = excelDocument.getSheet().createRow(jRow++);
            startdate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("startdate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getBeginDate()) + " 00:00:00");

            SXSSFRow enddate = excelDocument.getSheet().createRow(jRow++);
            enddate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("enddate") + " : " + StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), purchaseSalesReport.getEndDate()) + " 23:59:59");

            String branchName = "";
            if (selectedBranchList.isEmpty()) {
                branchName = sessionBean.getLoc().getString("all");
            } else if (selectedBranchList.get(0).getBranch().getId() == 0) {
                branchName = sessionBean.getLoc().getString("all");
            } else {
                for (BranchSetting branchSetting1 : selectedBranchList) {
                    branchName += " , " + branchSetting1.getBranch().getName();
                }

                branchName = branchName.substring(2, branchName.length());
            }

            SXSSFRow branch = excelDocument.getSheet().createRow(jRow++);
            branch.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("branch") + " : " + branchName);

            String stockName = "";
            if (purchaseSalesReport.getStockList().isEmpty()) {
                stockName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getStockList().get(0).getId() == 0) {
                stockName = sessionBean.getLoc().getString("all");
            } else {
                for (Stock s : purchaseSalesReport.getStockList()) {
                    stockName += " , " + s.getName();
                }
                stockName = stockName.substring(3, stockName.length());
            }
            SXSSFRow stock = excelDocument.getSheet().createRow(jRow++);
            stock.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " : " + stockName);

            String categoryName = "";
            if (purchaseSalesReport.getListOfCategorization().isEmpty()) {
                categoryName = sessionBean.getLoc().getString("all");
            } else if (purchaseSalesReport.getListOfCategorization().get(0).getId() == 0) {
                categoryName = sessionBean.getLoc().getString("all");
            } else {
                for (Categorization s : purchaseSalesReport.getListOfCategorization()) {
                    categoryName += " , " + s.getName();
                }
                categoryName = categoryName.substring(3, categoryName.length());
            }
            SXSSFRow cate = excelDocument.getSheet().createRow(jRow++);
            cate.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("stock") + " " + sessionBean.getLoc().getString("category") + " : " + categoryName);

            String accountName = sessionBean.loc.getString("all");
            if (purchaseSalesReport.getAccount() != null) {
                if (purchaseSalesReport.getAccount().getId() != 0) {
                    accountName = purchaseSalesReport.getAccount().getName();
                }
            }

            SXSSFRow acc = excelDocument.getSheet().createRow(jRow++);
            acc.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("currentname") + " : " + accountName);

            String taxName = sessionBean.loc.getString("all");
            if (purchaseSalesReport.getStocktaxgroup() != null) {
                if (purchaseSalesReport.getStocktaxgroup().getId() != 0) {
                    taxName = purchaseSalesReport.getStocktaxgroup().getName() + " " + purchaseSalesReport.getStocktaxgroup().getRate().intValue();
                }
            }

            if (isCentralSupplier) {
                String centralSupplierName = "";
                if (purchaseSalesReport.getListOfCentralSupplier().isEmpty()) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfCentralSupplier().get(0).getId() == 0) {
                    centralSupplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (CentralSupplier s : purchaseSalesReport.getListOfCentralSupplier()) {
                        centralSupplierName += " , " + s.getName();
                    }
                    centralSupplierName = centralSupplierName.substring(3, centralSupplierName.length());
                }

                SXSSFRow centralSupplierNamer = excelDocument.getSheet().createRow(jRow++);
                centralSupplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("centralsupplier") + " : " + centralSupplierName);
            } else {
                String supplierName = "";
                if (purchaseSalesReport.getListOfAccount().isEmpty()) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else if (purchaseSalesReport.getListOfAccount().get(0).getId() == 0) {
                    supplierName = sessionBean.getLoc().getString("all");
                } else {
                    for (Account s : purchaseSalesReport.getListOfAccount()) {
                        supplierName += " , " + s.getName();
                    }
                    supplierName = supplierName.substring(3, supplierName.length());
                }

                SXSSFRow supplierNamer = excelDocument.getSheet().createRow(jRow++);
                supplierNamer.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("supplier") + " : " + supplierName);
            }

            SXSSFRow tax = excelDocument.getSheet().createRow(jRow++);
            tax.createCell((short) 0).setCellValue(sessionBean.getLoc().getString("taxgroup") + " : " + taxName);

            SXSSFRow rowEmpty = excelDocument.getSheet().createRow(jRow++);

            StaticMethods.createHeaderExcel("frmPurchaseSalesReportDatatable:dtbPurchaseSalesReport", toogleList, "headerBlack", excelDocument.getWorkbook());
            jRow++;

            while (rs.next()) {
                totalCount = rs.getString("r_countresult");
                int b = 0;
                BigDecimal totalMoney = BigDecimal.valueOf(0);
                if (purchaseSalesReport.isIsPurchase()) {
                    totalMoney = rs.getBigDecimal("r_salestotalmoney");
                } else {
                    totalMoney = rs.getBigDecimal("r_totalmoney");
                }

                SXSSFRow row = excelDocument.getSheet().createRow(jRow++);

                if (purchaseSalesReport.isIsPurchase()) {
                    if (toogleList.get(0)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_brnname"));
                    }

                    if (toogleList.get(1)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckcode"));
                    }

                    if (toogleList.get(2)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckcenterproductcode"));
                    }
                    if (toogleList.get(3)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckbarcode"));
                    }
                    if (toogleList.get(4)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckname"));

                    }
                    if (toogleList.get(5)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("r_category")));
                    }
                    if (toogleList.get(6)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_csppname"));
                    }
                    if (toogleList.get(7)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_accname"));
                    }
                    if (toogleList.get(8)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_brname"));
                    }
                    if (toogleList.get(9)) {
                        SXSSFCell taxrate = row.createCell((short) b++);
                        if (rs.getBigDecimal("r_taxrate") != null) {
                            taxrate.setCellValue(StaticMethods.round((rs.getBigDecimal("r_taxrate")).doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }

                    }

                    if (toogleList.get(10)) {
                        SXSSFCell quantity = row.createCell((short) b++);
                        quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("r_quantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(11)) {
                        SXSSFCell quantity = row.createCell((short) b++);
                        quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("r_avgsaleunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(12)) {
                        SXSSFCell purchase = row.createCell((short) b++);
                        purchase.setCellValue(StaticMethods.round(rs.getBigDecimal("r_avgpurchaseunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(13)) {
                        SXSSFCell lastsale = row.createCell((short) b++);
                        lastsale.setCellValue(StaticMethods.round(rs.getBigDecimal("r_lastsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                    if (toogleList.get(14)) {
                        SXSSFCell lastpurchaseprice = row.createCell((short) b++);
                        lastpurchaseprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_lastpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(15)) {
                        SXSSFCell purchaseCost = row.createCell((short) b++);
                        purchaseCost.setCellValue(StaticMethods.round(rs.getBigDecimal("r_purchasecost").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(16)) {
                        SXSSFCell salesTotalMoney = row.createCell((short) b++);
                        salesTotalMoney.setCellValue(StaticMethods.round(rs.getBigDecimal("r_salestotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(17)) {
                        BigDecimal result;
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        SXSSFCell margin = row.createCell((short) b++);
                        margin.setCellValue(StaticMethods.round(result.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(18)) {
                        BigDecimal result;
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        SXSSFCell margin = row.createCell((short) b++);
                        margin.setCellValue(StaticMethods.round(result.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(19)) {
                        SXSSFCell totprice = row.createCell((short) b++);
                        totprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(20)) {
                        SXSSFCell totalttax = row.createCell((short) b++);
                        totalttax.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(21)) {
                        SXSSFCell totaldiscount = row.createCell((short) b++);
                        totaldiscount.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totaldiscount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(22)) {
                        SXSSFCell totalmoney = row.createCell((short) b++);
                        totalmoney.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                } else {

                    if (toogleList.get(0)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_brnname"));
                    }

                    if (toogleList.get(1)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckcode"));
                    }
                    if (toogleList.get(2)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckcenterproductcode"));
                    }
                    if (toogleList.get(3)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckbarcode"));
                    }
                    if (toogleList.get(4)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_stckname"));

                    }
                    if (toogleList.get(5)) {
                        row.createCell((short) b++).setCellValue(StaticMethods.findCategories(rs.getString("r_category")));
                    }
                    if (toogleList.get(6)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_csppname"));
                    }
                    if (toogleList.get(7)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_accname"));
                    }
                    if (toogleList.get(8)) {
                        row.createCell((short) b++).setCellValue(rs.getString("r_brname"));
                    }
                    if (toogleList.get(9)) {
                        SXSSFCell taxrate = row.createCell((short) b++);
                        if (rs.getBigDecimal("r_taxrate") != null) {
                            taxrate.setCellValue(StaticMethods.round(rs.getBigDecimal("r_taxrate").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                        }
                    }

                    if (toogleList.get(10)) {
                        SXSSFCell quantity = row.createCell((short) b++);
                        quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("r_quantity").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(11)) {
                        SXSSFCell quantity = row.createCell((short) b++);
                        quantity.setCellValue(StaticMethods.round(rs.getBigDecimal("r_avgsaleunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(12)) {
                        SXSSFCell purchase = row.createCell((short) b++);
                        purchase.setCellValue(StaticMethods.round(rs.getBigDecimal("r_avgpurchaseunitprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(13)) {
                        SXSSFCell lastsale = row.createCell((short) b++);
                        lastsale.setCellValue(StaticMethods.round(rs.getBigDecimal("r_lastsaleprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(14)) {
                        SXSSFCell lastpurchaseprice = row.createCell((short) b++);
                        lastpurchaseprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_lastpurchaseprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(15)) {
                        SXSSFCell lastpurchaseprice = row.createCell((short) b++);
                        lastpurchaseprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_purchasecost").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(16)) {
                        SXSSFCell lastpurchaseprice = row.createCell((short) b++);
                        lastpurchaseprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_salestotalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(17)) {

                        BigDecimal result;
                        String param1 = "";
                        if (totalMoney.compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(totalMoney, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }

                        SXSSFCell margin = row.createCell((short) b++);
                        margin.setCellValue(StaticMethods.round(result.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(18)) {
                        BigDecimal result;
                        if (rs.getBigDecimal("r_purchasecost").compareTo(BigDecimal.valueOf(0)) != 0) {
                            result = ((totalMoney.subtract(rs.getBigDecimal("r_purchasecost"))).divide(rs.getBigDecimal("r_purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                        } else {
                            result = BigDecimal.ZERO;
                        }
                        SXSSFCell margin = row.createCell((short) b++);
                        margin.setCellValue(StaticMethods.round(result.doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }

                    if (toogleList.get(19)) {
                        SXSSFCell totprice = row.createCell((short) b++);
                        totprice.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totalprice").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }
                    if (toogleList.get(20)) {
                        SXSSFCell totalttax = row.createCell((short) b++);
                        totalttax.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totaltax").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(21)) {
                        SXSSFCell totaldiscount = row.createCell((short) b++);
                        totaldiscount.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totaldiscount").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));

                    }
                    if (toogleList.get(22)) {
                        SXSSFCell totalmoney = row.createCell((short) b++);
                        totalmoney.setCellValue(StaticMethods.round(rs.getBigDecimal("r_totalmoney").doubleValue(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                    }

                }

            }
            SXSSFRow blank1 = excelDocument.getSheet().createRow(jRow++);

            listOfSubTotal = convertJsonToObject(totalCount, purchaseSalesReport);

            for (PurchaseSalesReport prchs : listOfSubTotal) {

                CellStyle cellStyle1 = StaticMethods.createCellStyleExcel("footer", excelDocument.getWorkbook());
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);

                SXSSFRow quantityrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cellquantity = quantityrow.createCell((short) 0);
                cellquantity.setCellValue(sessionBean.getLoc().getString("quantity") + " : "
                        + StaticMethods.round(prchs.getQuantity(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellquantity.setCellStyle(cellStyle1);

                SXSSFRow purchasecostrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cellpurchasecost = purchasecostrow.createCell((short) 0);
                cellpurchasecost.setCellValue(sessionBean.getLoc().getString("purchasecost") + " : "
                        + StaticMethods.round(prchs.getCost(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellpurchasecost.setCellStyle(cellStyle1);

                SXSSFRow profitprice = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell profitpricecell = profitprice.createCell((short) 0);
                profitpricecell.setCellValue(sessionBean.getLoc().getString("profitprice") + " : "
                        + StaticMethods.round(prchs.getProfitAmount(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                profitpricecell.setCellStyle(cellStyle1);

                SXSSFCell cellcurrency = profitprice.createCell((short) 1);
                cellcurrency.setCellValue(sessionBean.currencySignOrCode(prchs.getCurrency().getId(), 0));
                cellcurrency.setCellStyle(cellStyle1);

                SXSSFRow profitpercentage = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell profitpercentagecell = profitpercentage.createCell((short) 0);
                profitpercentagecell.setCellValue(sessionBean.getLoc().getString("profitpercentage") + " : " + "%"
                        + StaticMethods.round(prchs.getProfitpercentage(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                profitpercentagecell.setCellStyle(cellStyle1);

                SXSSFRow sumrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell cellsumm = sumrow.createCell((short) 0);
                cellsumm.setCellValue(sessionBean.getLoc().getString("sum") + " : "
                        + StaticMethods.round(prchs.getTotalPrice(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                cellsumm.setCellStyle(cellStyle1);

                SXSSFCell cellcurrency1 = sumrow.createCell((short) 1);
                cellcurrency1.setCellValue(sessionBean.currencySignOrCode(prchs.getCurrency().getId(), 0));
                cellcurrency1.setCellStyle(cellStyle1);

                SXSSFRow taxpricerow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell celltaxprice = taxpricerow.createCell((short) 0);
                celltaxprice.setCellValue(sessionBean.getLoc().getString("taxprice") + " : "
                        + StaticMethods.round(prchs.getTotalTax(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                celltaxprice.setCellStyle(cellStyle1);

                SXSSFCell cellcurrency2 = taxpricerow.createCell((short) 1);
                cellcurrency2.setCellValue(sessionBean.currencySignOrCode(prchs.getCurrency().getId(), 0));
                cellcurrency2.setCellStyle(cellStyle1);

                SXSSFRow discountrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell celldiscount = discountrow.createCell((short) 0);
                celldiscount.setCellValue(sessionBean.getLoc().getString("totaldiscount") + " : "
                        + StaticMethods.round(prchs.getTotalDiscount(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                celldiscount.setCellStyle(cellStyle1);

                SXSSFCell cellcurrency3 = discountrow.createCell((short) 1);
                cellcurrency3.setCellValue(sessionBean.currencySignOrCode(prchs.getCurrency().getId(), 0));
                cellcurrency3.setCellStyle(cellStyle1);

                SXSSFRow overraltotalrow = excelDocument.getSheet().createRow(jRow++);
                SXSSFCell celloverraltotal = overraltotalrow.createCell((short) 0);
                celloverraltotal.setCellValue(sessionBean.getLoc().getString("overalltotal") + " : "
                        + StaticMethods.round(prchs.getTotalMoney(), sessionBean.getUser().getLastBranch().getCurrencyrounding()));
                celloverraltotal.setCellStyle(cellStyle1);

                SXSSFCell cellcurrency4 = overraltotalrow.createCell((short) 1);
                cellcurrency4.setCellValue(sessionBean.currencySignOrCode(prchs.getCurrency().getId(), 0));
                cellcurrency4.setCellStyle(cellStyle1);

                SXSSFRow blank = excelDocument.getSheet().createRow(jRow++);

            }
            try {
                StaticMethods.writeExcelToResponse(excelDocument.getWorkbook(), sessionBean.getLoc().getString("purchasesalesreport"));
            } catch (IOException ex) {
                Logger.getLogger(ProfitMarginReportService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {

            System.out.println("----excel catch---" + ex.getMessage());
        }
    }

    @Override
    public List<PurchaseSalesReport> stockDetail(String where, PurchaseSalesReport obj,
            String branchList
    ) {
        return purchaseSalesReportDao.stockDetail(where, obj, branchList);
    }

    public List<PurchaseSalesReport> convertJsonToObject(String result, PurchaseSalesReport obj) throws ParseException {
        List<PurchaseSalesReport> listResult = new ArrayList<>();
        JSONArray jsonArr = new JSONArray(result);
        for (int m = 0; m < jsonArr.length(); m++) {

            PurchaseSalesReport item = new PurchaseSalesReport();

            try {
                item.getStock().setId(jsonArr.getJSONObject(m).getInt("stock_id"));
                item.setQuantity(jsonArr.getJSONObject(m).getBigDecimal("quantity"));
                item.setTotalMoney(jsonArr.getJSONObject(m).getBigDecimal("totalmoney"));
                item.setTotalTax(jsonArr.getJSONObject(m).getBigDecimal("totaltax"));
                item.setTotalPrice(jsonArr.getJSONObject(m).getBigDecimal("totalprice"));
                item.setTotalDiscount(jsonArr.getJSONObject(m).getBigDecimal("totaldiscount"));
                item.getCurrency().setId(jsonArr.getJSONObject(m).getInt("currency_id"));
                item.setCost(jsonArr.getJSONObject(m).getBigDecimal("cost"));

            } catch (Exception e) {
            }

            try {
                item.setProfitAmount(jsonArr.getJSONObject(m).getBigDecimal("profitamount"));

                if (jsonArr.getJSONObject(m).getBigDecimal("purchasecost").compareTo(BigDecimal.valueOf(0)) == 1) {
                    BigDecimal percentage = BigDecimal.valueOf(0);
                    if (obj.isIsPurchase()) {
                        percentage = ((jsonArr.getJSONObject(m).getBigDecimal("salestotalmoney").subtract(jsonArr.getJSONObject(m).getBigDecimal("purchasecost"))).divide(jsonArr.getJSONObject(m).getBigDecimal("purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                    } else {
                        percentage = ((jsonArr.getJSONObject(m).getBigDecimal("totalmoney").subtract(jsonArr.getJSONObject(m).getBigDecimal("purchasecost"))).divide(jsonArr.getJSONObject(m).getBigDecimal("purchasecost"), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));

                    }

                    item.setProfitpercentage(percentage);
                } else {

                    item.setProfitpercentage(BigDecimal.ZERO);

                }

            } catch (Exception e) {
            }

            listResult.add(item);
        }

        return listResult;

    }

}
