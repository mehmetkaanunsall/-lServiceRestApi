/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 05:30:26
 */
package com.mepsan.marwiz.general.report.profitmarginreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ProfitMarginReportDao extends JdbcDaoSupport implements IProfitMarginReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ProfitMarginReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {

        Object[] param = null;

        String sql = "";
        List<Stock> tempListOfStock = new ArrayList<>();
        List<Categorization> tempListOfCategorization = new ArrayList<>();

        String stockList = "";
        for (Stock stock : profitMarginReport.getStockList()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            tempListOfStock.add(stock);
            if (stock.getId() == 0) {
                stockList = "";
                tempListOfStock.clear();
                break;
            }
        }

        String categoryList = "";
        for (Categorization category : profitMarginReport.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            tempListOfCategorization.add(category);
            if (category.getId() == 0) {
                categoryList = "";
                tempListOfCategorization.clear();
                break;
            }
        }

        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
        }

        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
        }

        if (profitMarginReport.isCalculationType()) {//fifo
            sql = " SELECT r_stock_id,\n"
                      + "r_name AS name,\n"
                      + "r_code AS code,\n"
                      + "r_centerproductcode AS centerproductcode,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_barcode AS barcode,\n"
                      + "r_brnname AS brnname,\n"
                      + "r_sortname AS sortname,\n"
                      + "r_unitrounding AS unitrounding,\n"
                      + "r_stckbrand_id AS stckbrand_id,\n"
                      + "r_brname AS brname,\n"
                      + "r_supplier_id AS supplier_id,\n"
                      + "r_accname AS accname,\n"
                      + "r_centersuppliername AS centralsuppliername,\n"
                      + "r_centersupplier_id AS centralsupplier_id,\n"
                      + "r_totalpurchasemoney AS totalpurchaseprice,\n"
                      + "r_totalsalemoney AS totalsaleprice,\n"
                      + "r_totalsalecount AS salecount,\n"
                      + "r_remainingpurchasecount AS purchasequantity,\n"
                      + "r_warehousestartquantity AS warehousestartquantity,\n"
                      + "r_warehouseendquantity AS warehouseendquantity,\n"
                      + "r_begintoendpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "r_begintoendpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "r_warehousestartprice AS warehousestartprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN 0 ELSE r_warehouseendprice END) AS warehouseendprice,\n"
                      + "r_stocktakingprice AS stocktakingprice,\n"
                      + "r_stocktakingquantity AS stocktakingquantity,\n"
                      + "r_endtobeginpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "r_endtobeginpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN (r_warehouseendprice * -1) ELSE 0 END) AS differenceprice,\n"
                      + "r_zsalesquantity AS zsalesquantity,\n"
                      + "r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitloss_fifo(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";

            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(), profitMarginReport.isIsAllStock(), String.valueOf(first), String.valueOf(pageSize),
                tempListOfStock.isEmpty() ? null : stockList, tempListOfCategorization.isEmpty() ? null : categoryList,
                profitMarginReport.isIsCalculateStockTaking(), centralIngetrationInf, false, profitMarginReport.isIsExcludingServiceStock()};

        } else if (!profitMarginReport.isCalculationType()) {//weightaverage
            sql = " SELECT r_stock_id,\n"
                      + "r_name AS name,\n"
                      + "r_code AS code,\n"
                      + "r_centerproductcode AS centerproductcode,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_barcode AS barcode,\n"
                      + "r_brnname AS brnname,\n"
                      + "r_sortname AS sortname,\n"
                      + "r_unitrounding AS unitrounding,\n"
                      + "r_stckbrand_id AS stckbrand_id,\n"
                      + "r_brname AS brname,\n"
                      + "r_supplier_id AS supplier_id,\n"
                      + "r_accname AS accname,\n"
                      + "r_centersuppliername AS centralsuppliername,\n"
                      + "r_centersupplier_id AS centralsupplier_id,\n"
                      + "r_totalpurchaseprice AS totalpurchaseprice,\n"
                      + "r_salecount AS salecount,\n"
                      + "r_totalsaleprice AS totalsaleprice,\n"
                      + "r_warehousestartquantity AS warehousestartquantity,\n"
                      + "r_warehouseendquantity AS warehouseendquantity,\n"
                      + "r_endtobeginpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "r_endtobeginpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "r_endtobeginpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "r_endtobeginpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "r_stocktakingprice AS stocktakingprice,\n"
                      + "r_stocktakingquantity AS stocktakingquantity,\n"
                      + "r_warehousestartprice AS warehousestartprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN 0 ELSE r_warehouseendprice END) AS warehouseendprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN (r_warehouseendprice * -1) ELSE 0 END) AS differenceprice,\n"
                      + "r_zsalesquantity AS zsalesquantity,\n"
                      + "r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitloss_weightaverage(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";

            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(), profitMarginReport.isIsAllStock(), String.valueOf(first), String.valueOf(pageSize),
                tempListOfStock.isEmpty() ? null : stockList, tempListOfCategorization.isEmpty() ? null : categoryList,
                profitMarginReport.isIsCalculateStockTaking(), centralIngetrationInf, false, profitMarginReport.isIsExcludingServiceStock()};
        }

        List<ProfitMarginReport> result = getJdbcTemplate().query(sql, param, new ProfitMarginReportMapper());
        return result;
    }

    @Override
    public String exportData(ProfitMarginReport profitMarginReport, String where, String branchList, int centralIngetrationInf) {

        String sql = "";

        String stockList = "";
        for (Stock stock : profitMarginReport.getStockList()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }

        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
        }

        String categoryList = "";
        for (Categorization category : profitMarginReport.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
        }

        if (profitMarginReport.isCalculationType()) {//fifo
            //Detay--Stok

            sql = " SELECT r_stock_id,\n"
                      + "r_name AS name,\n"
                      + "r_code AS code,\n"
                      + "r_centerproductcode AS centerproductcode,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_barcode AS barcode,\n"
                      + "r_brnname AS brnname,\n"
                      + "r_sortname AS sortname,\n"
                      + "r_unitrounding AS unitrounding,\n"
                      + "r_stckbrand_id AS stckbrand_id,\n"
                      + "r_brname AS brname,\n"
                      + "r_supplier_id AS supplier_id,\n"
                      + "r_accname AS accname,\n"
                      + "r_centersuppliername AS centralsuppliername,\n"
                      + "r_centersupplier_id AS centralsupplier_id,\n"
                      + "r_totalpurchasemoney AS totalpurchaseprice,\n"
                      + "r_totalsalemoney AS totalsaleprice,\n"
                      + "r_totalsalecount AS salecount,\n"
                      + "r_remainingpurchasecount AS purchasequantity,\n"
                      + "r_warehousestartquantity AS warehousestartquantity,\n"
                      + "r_warehouseendquantity AS warehouseendquantity,\n"
                      + "r_begintoendpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "r_begintoendpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "r_warehousestartprice AS warehousestartprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN 0 ELSE r_warehouseendprice END) AS warehouseendprice,\n"
                      + "r_stocktakingprice AS stocktakingprice,\n"
                      + "r_stocktakingquantity AS stocktakingquantity,\n"
                      + "r_endtobeginpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "r_endtobeginpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN (r_warehouseendprice * -1) ELSE 0 END) AS differenceprice,\n"
                      + "r_zsalesquantity AS zsalesquantity,\n"
                      + "r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitloss_fifo('" + branchList + "', '" + profitMarginReport.getBeginDate() + "', '" + profitMarginReport.getEndDate() + "', " + profitMarginReport.isIsTaxIncluded() + ", " + profitMarginReport.isIsAllStock() + ", '" + 0 + "','" + 0 + "', ?, ? , " + profitMarginReport.isIsCalculateStockTaking() + ", " + centralIngetrationInf + ", true, " + profitMarginReport.isIsExcludingServiceStock() + ")";

        } else if (!profitMarginReport.isCalculationType()) {//weightaverage

            sql = " SELECT r_stock_id,\n"
                      + "r_name AS name,\n"
                      + "r_code AS code,\n"
                      + "r_centerproductcode AS centerproductcode,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_barcode AS barcode,\n"
                      + "r_brnname AS brnname,\n"
                      + "r_sortname AS sortname,\n"
                      + "r_unitrounding AS unitrounding,\n"
                      + "r_stckbrand_id AS stckbrand_id,\n"
                      + "r_brname AS brname,\n"
                      + "r_supplier_id AS supplier_id,\n"
                      + "r_accname AS accname,\n"
                      + "r_centersuppliername AS centralsuppliername,\n"
                      + "r_centersupplier_id AS centralsupplier_id,\n"
                      + "r_totalpurchaseprice AS totalpurchaseprice,\n"
                      + "r_salecount AS salecount,\n"
                      + "r_totalsaleprice AS totalsaleprice,\n"
                      + "r_warehousestartquantity AS warehousestartquantity,\n"
                      + "r_warehouseendquantity AS warehouseendquantity,\n"
                      + "r_endtobeginpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "r_endtobeginpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "r_endtobeginpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "r_endtobeginpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "r_stocktakingprice AS stocktakingprice,\n"
                      + "r_stocktakingquantity AS stocktakingquantity,\n"
                      + "r_warehousestartprice AS warehousestartprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN 0 ELSE r_warehouseendprice END) AS warehouseendprice,\n"
                      + "(CASE WHEN r_warehouseendquantity = 0 AND r_warehouseendprice <> 0 THEN (r_warehouseendprice * -1) ELSE 0 END) AS differenceprice,\n"
                      + "r_zsalesquantity AS zsalesquantity,\n"
                      + "r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitloss_weightaverage('" + branchList + "', '" + profitMarginReport.getBeginDate() + "', '" + profitMarginReport.getEndDate() + "', " + profitMarginReport.isIsTaxIncluded() + ", " + profitMarginReport.isIsAllStock() + ", '" + 0 + "','" + 0 + "', ?, ? , " + profitMarginReport.isIsCalculateStockTaking() + ", " + centralIngetrationInf + ", true, " + profitMarginReport.isIsExcludingServiceStock() + ")";

        }

        return sql;
    }

    @Override
    public List<ProfitMarginReport> findAllCategory(ProfitMarginReport profitMarginReport, String where, String branchList, int centralIngetrationInf) {
        String sql = "";
        Object[] param = null;

        List<Categorization> tempListOfCategorization = new ArrayList<>();

        String categoryList = "";
        for (Categorization category : profitMarginReport.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            tempListOfCategorization.add(category);
            if (category.getId() == 0) {
                categoryList = "";
                tempListOfCategorization.clear();
                break;
            }
        }

        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
        }

        if (profitMarginReport.isCalculationType()) {//FIFO

            sql = "SELECT \n"
                      + "   r_categoryname AS  gctname,\n"
                      + "   r_category_id AS gctid,\n"
                      + "   r_currency_id AS currency_id,\n"
                      + "   r_parent_id AS  gctparent_id,\n"
                      + "   r_parentname AS parentname,\n"
                      + "   r_branchname AS brnname,\n"
                      + "   r_totalpurchaseprice AS totalpurchaseprice,\n"
                      + "   r_salecount AS salecount,\n"
                      + "   r_tempoveralltotalsales AS tempoveralltotalsales,\n"
                      + "   r_totalsaleprice AS totalsaleprice,\n"
                      + "   r_warehousestartquantity AS warehousestartquantity,\n"
                      + "   r_warehouseendquantity AS warehouseendquantity,\n"
                      + "   r_endtobeginpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "   r_endtobeginpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "   r_stocktakingprice AS stocktakingprice,\n"
                      + "   r_stocktakingquantity AS stocktakingquantity,\n"
                      + "   r_warehousestartprice AS warehousestartprice,\n"
                      + "   r_warehouseendprice AS warehouseendprice,\n"
                      + "   r_begintoendpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "   r_begintoendpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "   r_differenceprice AS differenceprice,\n"
                      + "   r_purchasequantity AS purchasequantity,\n"
                      + "   r_zsalesquantity AS zsalesquantity,\n"
                      + "   r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitmargin_fifo__category(?, ?, ?, ?, ?, ?,?)";

            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(),
                tempListOfCategorization.isEmpty() ? null : categoryList, profitMarginReport.isIsCalculateStockTaking(),
                centralIngetrationInf};

        } else {

            sql = "SELECT \n"
                      + "   r_categoryname AS  gctname,\n"
                      + "   r_category_id AS gctid,\n"
                      + "   r_currency_id AS currency_id,\n"
                      + "   r_parent_id AS  gctparent_id,\n"
                      + "   r_parentname AS parentname,\n"
                      + "   r_branchname AS brnname,\n"
                      + "   r_totalpurchaseprice AS totalpurchaseprice,\n"
                      + "   r_salecount AS salecount,\n"
                      + "   r_tempoveralltotalsales AS tempoveralltotalsales,\n"
                      + "   r_totalsaleprice AS totalsaleprice,\n"
                      + "   r_warehousestartquantity AS warehousestartquantity,\n"
                      + "   r_warehouseendquantity AS warehouseendquantity,\n"
                      + "   r_endtobeginpurchasequantity AS endtobeginpurchasequantity,\n"
                      + "   r_endtobeginpurchaseprice AS endtobeginpurchaseprice,\n"
                      + "   r_stocktakingprice AS stocktakingprice,\n"
                      + "   r_stocktakingquantity AS stocktakingquantity,\n"
                      + "   r_warehousestartprice AS warehousestartprice,\n"
                      + "   r_warehouseendprice AS warehouseendprice,\n"
                      + "   r_begintoendpurchasereturnquantity AS endtobeginpurchasereturnquantity,\n"
                      + "   r_begintoendpurchasereturnprice AS endtobeginpurchasereturnprice,\n"
                      + "   r_differenceprice AS differenceprice,\n"
                      + "   r_zsalesquantity AS zsalesquantity,\n"
                      + "   r_zsalesprice AS zsalesprice\n"
                      + " FROM general.rpt_profitmargin_weightaverage__category(?, ?, ?, ?, ?, ?,?)";
            ;
            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(), profitMarginReport.isIsCalculateStockTaking(),
                tempListOfCategorization.isEmpty() ? null : categoryList, centralIngetrationInf};
        }

        //  System.out.println("---sql Category---" + sql);
        List<ProfitMarginReport> result = getJdbcTemplate().query(sql, param, new ProfitMarginReportMapper());
        return result;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<ProfitMarginReport> totals(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {

        String sql = "";
        Object[] param = null;

        List<Stock> tempListOfStock = new ArrayList<>();
        List<Categorization> tempListOfCategorization = new ArrayList<>();

        String stockList = "";
        for (Stock stock : profitMarginReport.getStockList()) {
            tempListOfStock.add(stock);
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                tempListOfStock.clear();
                break;
            }
        }

        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
        }

        String categoryList = "";
        for (Categorization category : profitMarginReport.getListOfCategorization()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            tempListOfCategorization.add(category);
            if (category.getId() == 0) {
                categoryList = "";
                tempListOfCategorization.clear();
                break;
            }
        }
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
        }

        if (profitMarginReport.isCalculationType()) {//fifo

            sql = " SELECT r_count AS ttrcount,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_overalltotalpurchase AS overalltotalpurchase,\n"
                      + "r_tempoveralltotalsales AS tempoveralltotalsales,\n"
                      + "r_overalltotalsales AS overalltotalsales,\n"
                      + "r_overallsalecount AS overallsalecount,\n"
                      + "r_overallwarehousestartquantity AS overallwarehousestartquantity,\n"
                      + "r_overallwarehouseendquantity AS overallwarehouseendquantity,\n"
                      + "r_overallendtobeginpurchasequantity AS overallendtobeginpurchasequantity,\n"
                      + "r_overallendtobeginpurchaseprice AS overallendtobeginpurchaseprice,\n"
                      + "r_overallwarehousestartprice AS overallwarehousestartprice,\n"
                      + "r_overallwarehouseendprice AS overallwarehouseendprice,\n"
                      + "r_overallstocktakingprice AS overallstocktakingprice,\n"
                      + "r_overallstocktakingquantity AS overallstocktakingquantity,\n"
                      + "r_overallendtobeginpurchasereturnquantity AS overallendtobeginpurchasereturnquantity,\n"
                      + "r_overallendtobeginpurchasereturnprice AS overallendtobeginpurchasereturnprice,\n"
                      + "r_overalldifferenceprice AS overalldifferenceprice,\n"
                      + "r_overallzsalesquantity AS overallzsalesquantity,\n"
                      + "r_overallzsalesprice AS overallzsalesprice\n"
                      + " FROM general.rpt_profitloss_fifo_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?)";

            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(), profitMarginReport.isIsAllStock(),
                tempListOfStock.isEmpty() ? null : stockList, tempListOfCategorization.isEmpty() ? null : categoryList,
                profitMarginReport.isIsCalculateStockTaking(), centralIngetrationInf, profitMarginReport.isIsExcludingServiceStock()};

        } else { // ağırlıklı ortalama
            //Detay--Stok

            sql = " SELECT r_count AS ttrcount,\n"
                      + "r_currency_id AS currency_id,\n"
                      + "r_overalltotalpurchase AS overalltotalpurchase,\n"
                      + "r_tempoveralltotalsales AS tempoveralltotalsales,\n"
                      + "r_overalltotalsales AS overalltotalsales,\n"
                      + "r_overallsalecount AS overallsalecount,\n"
                      + "r_overallwarehousestartquantity AS overallwarehousestartquantity,\n"
                      + "r_overallwarehouseendquantity AS overallwarehouseendquantity,\n"
                      + "r_overallendtobeginpurchasequantity AS overallendtobeginpurchasequantity,\n"
                      + "r_overallendtobeginpurchaseprice AS overallendtobeginpurchaseprice,\n"
                      + "r_overallstocktakingprice AS overallstocktakingprice,\n"
                      + "r_overallstocktakingquantity AS overallstocktakingquantity,\n"
                      + "r_overallwarehousestartprice AS overallwarehousestartprice,\n"
                      + "r_overallwarehouseendprice AS overallwarehouseendprice,\n"
                      + "r_overallendtobeginpurchasereturnquantity AS overallendtobeginpurchasereturnquantity,\n"
                      + "r_overallendtobeginpurchasereturnprice AS overallendtobeginpurchasereturnprice,\n"
                      + "r_overalldifferenceprice AS overalldifferenceprice,\n"
                      + "r_overallzsalesquantity AS overallzsalesquantity,\n"
                      + "r_overallzsalesprice AS overallzsalesprice\n"
                      + " FROM general.rpt_profitloss_weightaverage_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?)";

            param = new Object[]{branchList, profitMarginReport.getBeginDate(), profitMarginReport.getEndDate(), profitMarginReport.isIsTaxIncluded(), profitMarginReport.isIsAllStock(),
                tempListOfStock.isEmpty() ? null : stockList, tempListOfCategorization.isEmpty() ? null : categoryList,
                profitMarginReport.isIsCalculateStockTaking(), centralIngetrationInf, profitMarginReport.isIsExcludingServiceStock()};

        }
        List<ProfitMarginReport> result = getJdbcTemplate().query(sql, param, new ProfitMarginReportMapper());
        return result;
    }

//    @Override
//    public List<ProfitMarginReport> totalsCategory(String where, ProfitMarginReport profitMarginReport, String branchList, int centralIngetrationInf) {
//
//        String sql = "";
//
//        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
//
//        String whereAll = "";
//        String whereAllWaste = "";
//        String join = "";
//
//        String whereBranch = "";
//        String whereBranchWareHouse = "";
//
//        if (!branchList.isEmpty()) {
//            whereBranch = " AND p.branch_id IN(" + branchList + ")";
//            whereBranchWareHouse = " AND iw.branch_id IN(" + branchList + ")";
//
//        }
//
//        whereAll = " HAVING (SUM((p.quantity * CASE WHEN p.is_return THEN -1 ELSE 1 END)*( CASE WHEN p.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' then 1 else 0 end))>0)\n";
//        whereAllWaste = " HAVING (SUM((p.quantity)*( CASE WHEN iwr.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END))>0)\n";
//        join = " INNER JOIN  inventory.stock s on(s.id=t.stock_id)\n";
//
//        if (profitMarginReport.isCalculationType()) {//FIFO
//            sql = "SELECT \n"
//                      + "   ttr.currency_id as currency_id,\n"
//                      + "   SUM(ttr.totalpurchaseprice) as totalpurchaseprice,\n"
//                      + "   SUM(ttr.totalsaleprice) as totalsaleprice,\n"
//                      + "   SUM(ttr.tempoveralltotalsales) as tempoveralltotalsales,\n"
//                      + "   SUM(ttr.salecount) as salecount,\n"
//                      + "   SUM(ttr.purchasequantity) as purchasequantity,\n"
//                      + "   SUM(ttr.warehousestartquantity) as warehousestartquantity,\n"
//                      + "   SUM(ttr.warehouseendquantity) as warehouseendquantity,\n"
//                      + "   SUM(ttr.endtobeginpurchasequantity) as endtobeginpurchasequantity,\n"
//                      + "   SUM(ttr.endtobeginpurchaseprice) as endtobeginpurchaseprice,\n"
//                      + "   SUM(ttr.warehousestartprice) as warehousestartprice,\n"
//                      + "   SUM(ttr.warehouseendprice) as warehouseendprice,\n"
//                      + "   SUM(ttr.stocktakingprice) as stocktakingprice,\n"
//                      + "   SUM(ttr.stocktakingquantity) as stocktakingquantity,\n"
//                      + "   SUM(ttr.endtobeginpurchasereturnquantity) as endtobeginpurchasereturnquantity,\n"
//                      + "   SUM(ttr.endtobeginpurchasereturnprice) as endtobeginpurchasereturnprice,\n"
//                      + "   SUM(ttr.differenceprice) as differenceprice\n"
//                      + "FROM(SELECT \n"
//                      + "	profit.currency_id,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) * (CASE WHEN CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) <> 0 THEN 1 ELSE 0 END)),0) AS totalpurchaseprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',2), ''), '0') as NUMERIC(18,4)) * (CASE WHEN CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) <> 0 THEN 1 ELSE 0 END)),0) AS tempoveralltotalsales,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',2), ''), '0') as NUMERIC(18,4))),0) AS totalsaleprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',3), ''), '0') as NUMERIC(18,4))),0) AS salecount,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',4), ''), '0') as NUMERIC(18,4))),0) AS purchasequantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',5), ''), '0') as NUMERIC(18,4))),0) AS warehousestartquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',6), ''), '0') as NUMERIC(18,4))),0) AS warehouseendquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',7), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasequantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',8), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchaseprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',9), ''), '0') as NUMERIC(18,4))),0) AS warehousestartprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',10), ''), '0') as NUMERIC(18,4))),0) AS warehouseendprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',11), ''), '0') as NUMERIC(18,4))),0) AS stocktakingprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',12), ''), '0') as NUMERIC(18,4))),0) AS stocktakingquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',13), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasereturnquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',14), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasereturnprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',15), ''), '0') as NUMERIC(18,4))),0) AS differenceprice\n"
//                      + "FROM\n"
//                      + "(\n"
//                      + "WITH recursive ctTree AS(\n"
//                      + "SELECT t.calculation, gct.id AS gctid, gct.name AS gctname, COALESCE(gct.parent_id,0) AS gctparent_id,t.brnname ,t.currency_id\n"
//                      + "FROM\n"
//                      + "(\n"
//                      + "   SELECT \n"
//                      + "   tt.stock_id,\n"
//                      + "   tt.currency_id,\n"
//                      + "   tt.brnid as brnid,\n"
//                      + "   tt.brnname as brnname,\n"
//                      + "   general.rpt_profitmargin_fifo(  \n"
//                      + "   tt.stock_id,  \n"
//                      + "   COALESCE(SUM(tt.newquantity),0) ,  \n"
//                      + "   COALESCE(SUM(tt.price),0),  \n"
//                      + "   COALESCE(SUM(tt.oldquantity),0) ,  \n"
//                      + "   '" + sd.format(profitMarginReport.getEndDate()) + "',\n"
//                      + "   '" + sd.format(profitMarginReport.getBeginDate()) + "',\n"
//                      + "   tt.brnid,\n"
//                      + "    " + profitMarginReport.isIsTaxIncluded() + ",\n"
//                      + "    " + profitMarginReport.isIsCalculateStockTaking() + " ) as calculation\n"
//                      + "   FROM\n"
//                      + "   (\n"
//                      + "       SELECT p.stock_id,\n"
//                      + "              p.currency_id,\n"
//                      + "              p.branch_id as brnid,\n"
//                      + "              p.branchname as brnname,\n"
//                      + "              COALESCE(SUM((p.quantity * CASE WHEN p.is_return THEN -1 ELSE 1 END)*( CASE WHEN p.processdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END) * (CASE WHEN p.is_calcincluded = TRUE AND p.saledifferentdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 0 ELSE 1 END)),0) AS newquantity,\n"
//                      + "              COALESCE(SUM(((CASE WHEN " + profitMarginReport.isIsTaxIncluded() + " THEN (p.totalmoney - ((p.totalmoney * COALESCE(p.salediscountrate, 0))/100)) ELSE (p.totalprice - ((p.totalprice * COALESCE(p.salediscountrate, 0))/100)) END) * p.exchangerate  * CASE WHEN p.is_return THEN -1 ELSE 1 END) *( CASE WHEN p.processdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END) * (CASE WHEN p.is_calcincluded = TRUE AND p.saledifferentdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 0 ELSE 1 END)),0) AS price,\n"
//                      + "              COALESCE(SUM((p.quantity * CASE WHEN p.is_return   THEN -1 ELSE 1 END)*( CASE WHEN p.processdate <  '" + sd.format(profitMarginReport.getBeginDate()) + "'  then 1 else 0 end) * (CASE WHEN p.is_calcincluded = TRUE AND p.saledifferentdate <  '" + sd.format(profitMarginReport.getBeginDate()) + "' THEN 0 ELSE 1 END)),0) AS oldquantity  \n"
//                      + "       FROM general.saleitem_view_profit p  \n"
//                      + "       LEFT JOIN general.unit gunt on(gunt.id = p.unit_id)  \n"
//                      + "       WHERE  p.processdate <'" + sd.format(profitMarginReport.getEndDate()) + "' " + whereBranch + "   " + where + "\n"
//                      + "       GROUP BY p.stock_id ,p.currency_id,p.branch_id, p.branchname\n"
//                      + whereAll + "\n"
//                      + "       \n"
//                      + "       UNION ALL\n"
//                      + "       \n"
//                      + "       SELECT \n"
//                      + "              p.stock_id,\n"
//                      + "              brn.currency_id,\n"
//                      + "              brn.id as brnid,\n"
//                      + "              brn.name as brnname,\n"
//                      + "              COALESCE(SUM((p.quantity)*( CASE WHEN iwr.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END)),0) AS newquantity,\n"
//                      + "              COALESCE(SUM((CASE WHEN " + profitMarginReport.isIsTaxIncluded() + " = TRUE THEN (wi.totalmoney * (1+(wi.taxrate/100))) ELSE wi.totalmoney END)*( CASE WHEN iwr.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END)),0) AS price,\n"
//                      + "              COALESCE(SUM((p.quantity)*( CASE WHEN iwr.processdate <  '" + sd.format(profitMarginReport.getBeginDate()) + "'  then 1 else 0 end)),0) AS oldquantity  \n"
//                      + "\n"
//                      + "       FROM inventory.warehousemovement p \n"
//                      + "       INNER JOIN inventory.warehouse iw ON (p.warehouse_id=iw.id " + whereBranchWareHouse + " AND iw.deleted=FALSE) \n"
//                      + "       INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
//                      + "       INNER JOIN inventory.warehousereceipt iwr ON (p.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE) \n"
//                      + "       INNER JOIN inventory.wasteiteminfo wi ON(wi.warehousemovement_id = p.id AND wi.deleted=FALSE)\n"
//                      + "       INNER JOIN inventory.stock stck ON (p.stock_id=stck.id AND stck.deleted=FALSE)\n"
//                      + "       LEFT JOIN general.unit gunt on(gunt.id = stck.unit_id)  \n"
//                      + "       WHERE p.deleted=FALSE AND iwr.type_id = 76 AND iwr.processdate < '" + sd.format(profitMarginReport.getEndDate()) + "' " + where + "\n"
//                      + "       GROUP BY p.stock_id,brn.currency_id,brn.id,brn.name\n"
//                      + whereAllWaste + "\n"
//                      + "    ) tt\n"
//                      + "    GROUP BY tt.stock_id,tt.currency_id,tt.brnid,tt.brnname\n"
//                      + ") as t\n"
//                      + join + "\n"
//                      + "LEFT JOIN general.categorization gct ON(gct.id=(SELECT scac.categorization_id FROM inventory.stock_categorization_con scac WHERE scac.stock_id=s.id AND scac.deleted=False order by scac.categorization_id DESC limit 1) AND gct.deleted=False )\n"
//                      + "UNION ALL\n"
//                      + "SELECT      ct.calculation AS calculation, \n"
//                      + "           gct1.id AS gctid, gct1.name AS gctname, COALESCE(gct1.parent_id,0) AS gctparent_id, brn.name as brnname,brn.currency_id\n"
//                      + "          \n"
//                      + "from general.categorization gct1\n"
//                      + "INNER JOIN general.branch brn ON(brn.id =gct1.branch_id AND brn.deleted=FALSE)\n"
//                      + "JOIN ctTree AS ct ON ct.gctparent_id = gct1.id\n"
//                      + ")    \n"
//                      + "SELECT \n"
//                      + "    *\n"
//                      + "FROM \n"
//                      + "  ctTree ctr\n"
//                      + ") as profit\n"
//                      + "LEFT JOIN general.categorization gctp ON(gctp.id=profit.gctparent_id AND gctp.deleted=False )\n"
//                      + "GROUP BY profit.gctname, profit.gctid, profit.gctparent_id, gctp.name,profit.brnname,profit.currency_id\n"
//                      + "ORDER BY profit.gctname )ttr\n"
//                      + "GROUP BY ttr.currency_id";
//        } else {
//            sql = "SELECT \n"
//                      + "   ttr.currency_id as currency_id,\n"
//                      + "   SUM(ttr.totalpurchaseprice) as totalpurchaseprice,\n"
//                      + "   SUM(ttr.totalsaleprice) as totalsaleprice,\n"
//                      + "   SUM(ttr.tempoveralltotalsales) as tempoveralltotalsales,\n"
//                      + "   SUM(ttr.salecount) as salecount,\n"
//                      + "   SUM(ttr.warehousestartquantity) as warehousestartquantity,\n"
//                      + "   SUM(ttr.warehouseendquantity) as warehouseendquantity,\n"
//                      + "   SUM(ttr.endtobeginpurchasequantity) as endtobeginpurchasequantity,\n"
//                      + "   SUM(ttr.endtobeginpurchaseprice) as endtobeginpurchaseprice,\n"
//                      + "   SUM(ttr.stocktakingprice) as stocktakingprice,\n"
//                      + "   SUM(ttr.stocktakingquantity) as stocktakingquantity,\n"
//                      + "   SUM(ttr.warehousestartprice) as warehousestartprice,\n"
//                      + "   SUM(ttr.warehouseendprice) as warehouseendprice,\n"
//                      + "   SUM(ttr.endtobeginpurchasereturnquantity) as endtobeginpurchasereturnquantity,\n"
//                      + "   SUM(ttr.endtobeginpurchasereturnprice) as endtobeginpurchasereturnprice,\n"
//                      + "   SUM(ttr.differenceprice) as differenceprice\n"
//                      + " \n"
//                      + "FROM(SELECT \n"
//                      + "	profit.currency_id,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) * (CASE WHEN CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) <> 0 THEN 1 ELSE 0 END)),0) AS totalpurchaseprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',2), ''), '0') as NUMERIC(18,4))),0) AS salecount,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',3), ''), '0') as NUMERIC(18,4)) * (CASE WHEN CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',1), ''), '0') as NUMERIC(18,4)) <> 0 THEN 1 ELSE 0 END)),0) AS tempoveralltotalsales,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',3), ''), '0') as NUMERIC(18,4))),0) AS totalsaleprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',4), ''), '0') as NUMERIC(18,4))),0) AS warehousestartquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',5), ''), '0') as NUMERIC(18,4))),0) AS warehouseendquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',6), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasequantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',7), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchaseprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',8), ''), '0') as NUMERIC(18,4))),0) AS stocktakingprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',9), ''), '0') as NUMERIC(18,4))),0) AS stocktakingquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',10), ''), '0') as NUMERIC(18,4))),0) AS warehousestartprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',11), ''), '0') as NUMERIC(18,4))),0) AS warehouseendprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',12), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasereturnquantity,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',13), ''), '0') as NUMERIC(18,4))),0) AS endtobeginpurchasereturnprice,\n"
//                      + "    COALESCE(SUM(CAST(COALESCE(nullif(split_part(replace(replace(cast(profit.calculation AS text), '(', ''), ')',''), ',',14), ''), '0') as NUMERIC(18,4))),0) AS differenceprice\n"
//                      + "FROM\n"
//                      + "(\n"
//                      + "WITH recursive ctTree AS(\n"
//                      + "SELECT t.calculation, gct.id AS gctid, gct.name AS gctname, COALESCE(gct.parent_id,0) AS gctparent_id ,t.brnname,t.currency_id  \n"
//                      + "FROM \n"
//                      + "(\n"
//                      + "   SELECT \n"
//                      + "   tt.stock_id,\n"
//                      + "   tt.currency_id,\n"
//                      + "   tt.brnid as brnid,\n"
//                      + "   tt.brnname as brnname,\n"
//                      + "   general.rpt_profitmargin_weightaverage(  \n"
//                      + "   tt.stock_id,  \n"
//                      + "   COALESCE(SUM(tt.quantity),0) ,  \n"
//                      + "   COALESCE(SUM(tt.price),0),  \n"
//                      + "   '" + sd.format(profitMarginReport.getEndDate()) + "',\n"
//                      + "   '" + sd.format(profitMarginReport.getBeginDate()) + "',\n"
//                      + "   tt.brnid,\n"
//                      + "    " + profitMarginReport.isIsTaxIncluded() + ",\n"
//                      + "    " + profitMarginReport.isIsCalculateStockTaking() + " ) as calculation\n"
//                      + "   FROM\n"
//                      + "   (\n"
//                      + "       SELECT p.stock_id,\n"
//                      + "              p.currency_id,\n"
//                      + "	             p.branch_id as brnid,\n"
//                      + "              p.branchname as brnname,\n"
//                      + "              COALESCE(SUM((p.quantity * CASE WHEN p.is_return THEN -1 ELSE 1 END)*( CASE WHEN p.processdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END) * (CASE WHEN p.is_calcincluded = TRUE AND p.saledifferentdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 0 ELSE 1 END)),0) AS quantity,\n"
//                      + "              COALESCE(SUM(((CASE WHEN " + profitMarginReport.isIsTaxIncluded() + " THEN (p.totalmoney - ((p.totalmoney * COALESCE(p.salediscountrate, 0))/100)) ELSE (p.totalprice - ((p.totalprice * COALESCE(p.salediscountrate, 0))/100)) END) * p.exchangerate  * CASE WHEN p.is_return THEN -1 ELSE 1 END) *( CASE WHEN p.processdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END) * (CASE WHEN p.is_calcincluded = TRUE AND p.saledifferentdate BETWEEN  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 0 ELSE 1 END)),0) AS price\n"
//                      + "       FROM general.saleitem_view_profit p  \n"
//                      + "       LEFT JOIN general.unit gunt on(gunt.id = p.unit_id)  \n"
//                      + "       WHERE  p.processdate <'" + sd.format(profitMarginReport.getEndDate()) + "'  " + whereBranch + "   " + where + "\n"
//                      + "       GROUP BY p.stock_id,p.currency_id,p.branch_id, p.branchname \n"
//                      + whereAll + "\n"
//                      + "       \n"
//                      + "       UNION ALL\n"
//                      + "       \n"
//                      + "       SELECT \n"
//                      + "              p.stock_id,\n"
//                      + "              brn.currency_id,\n"
//                      + "              brn.id as brnid,\n"
//                      + "              brn.name as brnname,\n"
//                      + "              COALESCE(SUM((p.quantity)*( CASE WHEN iwr.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END)),0) AS quantity,\n"
//                      + "              COALESCE(SUM((CASE WHEN " + profitMarginReport.isIsTaxIncluded() + " = TRUE THEN (wi.totalmoney * (1+(wi.taxrate/100))) ELSE wi.totalmoney END)*( CASE WHEN iwr.processdate between  '" + sd.format(profitMarginReport.getBeginDate()) + "' AND '" + sd.format(profitMarginReport.getEndDate()) + "' THEN 1 ELSE 0 END)),0) AS price\n"
//                      + "       FROM inventory.warehousemovement p \n"
//                      + "       INNER JOIN inventory.warehouse iw ON (p.warehouse_id=iw.id " + whereBranchWareHouse + " AND iw.deleted=FALSE) \n"
//                      + "       INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
//                      + "       INNER JOIN inventory.warehousereceipt iwr ON (p.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE) \n"
//                      + "       INNER JOIN inventory.wasteiteminfo wi ON(wi.warehousemovement_id = p.id AND wi.deleted=FALSE)\n"
//                      + "       INNER JOIN inventory.stock stck ON (p.stock_id=stck.id AND stck.deleted=FALSE)\n"
//                      + "       LEFT JOIN general.unit gunt on(gunt.id = stck.unit_id)  \n"
//                      + "       WHERE p.deleted=FALSE AND iwr.type_id = 76 AND iwr.processdate < '" + sd.format(profitMarginReport.getEndDate()) + "' " + where + "\n"
//                      + "       GROUP BY p.stock_id,brn.currency_id,brn.id,brn.name\n"
//                      + whereAllWaste + "\n"
//                      + "    ) tt\n"
//                      + "    GROUP BY tt.stock_id,tt.brnid,tt.brnname,tt.currency_id\n"
//                      + ") as t\n"
//                      + join + "\n"
//                      + "LEFT JOIN general.categorization gct ON(gct.id=(SELECT scac.categorization_id FROM inventory.stock_categorization_con scac WHERE scac.stock_id=s.id AND scac.deleted=False order by scac.categorization_id DESC limit 1) AND gct.deleted=False )\n"
//                      + "UNION ALL\n"
//                      + "SELECT     ct.calculation AS calculation, \n"
//                      + "            gct1.id AS gctid, gct1.name AS gctname, COALESCE(gct1.parent_id,0) AS gctparent_id, brn.name as brnname,brn.currency_id\n"
//                      + "          \n"
//                      + "from general.categorization gct1 --where  gct1.deleted=False\n"
//                      + "INNER JOIN general.branch brn ON(brn.id =gct1.branch_id AND brn.deleted=FALSE)\n"
//                      + "\n"
//                      + "JOIN ctTree AS ct ON ct.gctparent_id = gct1.id\n"
//                      + ")    \n"
//                      + "SELECT \n"
//                      + "    *\n"
//                      + "FROM \n"
//                      + "  ctTree ctr\n"
//                      + ") as profit\n"
//                      + "LEFT JOIN general.categorization gctp ON(gctp.id=profit.gctparent_id AND gctp.deleted=False )\n"
//                      + "GROUP BY profit.gctname,profit.currency_id, profit.gctid, profit.gctparent_id, gctp.name,profit.brnname\n"
//                      + "ORDER BY profit.gctname)ttr\n"
//                      + "GROUP BY ttr.currency_id";
//        }
//
//        //System.out.println("---sql Category- Totals--" + sql);
//        List<ProfitMarginReport> result = getJdbcTemplate().query(sql, new ProfitMarginReportMapper());
//        return result;
//    }
}
