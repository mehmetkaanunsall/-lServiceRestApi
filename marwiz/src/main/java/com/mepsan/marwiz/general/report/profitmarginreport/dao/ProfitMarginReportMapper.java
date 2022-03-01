/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.03.2018 09:16:37
 */
package com.mepsan.marwiz.general.report.profitmarginreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

public class ProfitMarginReportMapper implements RowMapper<ProfitMarginReport> {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public ProfitMarginReport mapRow(ResultSet rs, int i) throws SQLException {
        ProfitMarginReport profitMarginReport = new ProfitMarginReport();

        try {
            profitMarginReport.setId(rs.getInt("ttrcount"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.getCurrency().setId(rs.getInt("currency_id"));
            profitMarginReport.getCurrency().setTag(sessionBean.currencySignOrCode(profitMarginReport.getCurrency().getId(), 0));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.getCategorization().setId(rs.getInt("gctid"));
            profitMarginReport.getCategorization().setName(rs.getString("gctname"));
            Categorization parentId = new Categorization();
            parentId.setId(rs.getInt("gctparent_id"));
            parentId.setName(rs.getString("parentname"));
            profitMarginReport.getCategorization().setParentId(parentId);

        } catch (Exception e) {
        }
        try {
            profitMarginReport.getStock().setName(rs.getString("name"));
            profitMarginReport.getStock().setCode(rs.getString("code"));
            profitMarginReport.getStock().setCenterProductCode(rs.getString("centerproductcode"));
            profitMarginReport.getStock().setBarcode(rs.getString("barcode"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.getStock().getUnit().setSortName(rs.getString("sortname"));
            profitMarginReport.getStock().getUnit().setUnitRounding(rs.getInt("unitrounding"));
        } catch (Exception e) {
        }
        try { // totals için buraya girer 
            profitMarginReport.setTotalPurchasePrice(rs.getBigDecimal("totalpurchaseprice"));
            profitMarginReport.setTotalSalesPrice(rs.getBigDecimal("totalsaleprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setQuantity(rs.getBigDecimal("salecount"));
        } catch (Exception e) {
            profitMarginReport.setQuantity(BigDecimal.valueOf(0));
        }
        try {
            profitMarginReport.setPurchaseQuantity(rs.getBigDecimal("purchasequantity"));
        } catch (Exception e) {
            profitMarginReport.setPurchaseQuantity(BigDecimal.valueOf(0));
        }
        try { //totals için buraya da girer
            profitMarginReport.setWarehouseStartQuantity(rs.getBigDecimal("warehousestartquantity"));
            profitMarginReport.setWarehouseEndQuantity(rs.getBigDecimal("warehouseendquantity"));
            profitMarginReport.setBeginToEndPurchaseQuantity(rs.getBigDecimal("endtobeginpurchasequantity"));
            profitMarginReport.setBeginToEndPurchasePrice(rs.getBigDecimal("endtobeginpurchaseprice"));
            profitMarginReport.setBeginToEndSalesQuantity(rs.getBigDecimal("salecount"));
            profitMarginReport.setBeginToEndSalesPrice(rs.getBigDecimal("totalsaleprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setWarehouseStartPrice(rs.getBigDecimal("warehousestartprice"));
            profitMarginReport.setWarehouseEndPrice(rs.getBigDecimal("warehouseendprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setOverallTotalPurchase(rs.getBigDecimal("overalltotalpurchase"));
            profitMarginReport.setOverallTotalSales(rs.getBigDecimal("overalltotalsales"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setOverallQuantity(rs.getBigDecimal("overallsalecount"));
            profitMarginReport.setOverallWarehouseStartQuantity(rs.getBigDecimal("overallwarehousestartquantity"));
            profitMarginReport.setOverallWarehouseEndQuantity(rs.getBigDecimal("overallwarehouseendquantity"));
            profitMarginReport.setOverallBeginToEndPurchaseQuantity(rs.getBigDecimal("overallendtobeginpurchasequantity"));
            profitMarginReport.setOverallBeginToEndPurchasePrice(rs.getBigDecimal("overallendtobeginpurchaseprice"));

        } catch (Exception e) {
        }
        try {

            profitMarginReport.setOverallBeginToEndSalesQuantity(rs.getBigDecimal("overallsalecount"));
            profitMarginReport.setOverallBeginToEndSalesPrice(rs.getBigDecimal("overalltotalsales"));

        } catch (Exception e) {
        }

        try {
            profitMarginReport.setOverallPurchaseQuantity(rs.getBigDecimal("overallpurchasequantity"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setOverallTotalWarehouseStartPrice(rs.getBigDecimal("overallwarehousestartprice"));
            profitMarginReport.setOverallTotalWarehouseEndPrice(rs.getBigDecimal("overallwarehouseendprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            profitMarginReport.getStock().getBrand().setName(rs.getString("brname"));
            profitMarginReport.getStock().getSupplier().setId(rs.getInt("supplier_id"));
            profitMarginReport.getStock().getSupplier().setName(rs.getString("accname"));
            profitMarginReport.getStock().getCentralSupplier().setId(rs.getInt("centralsupplier_id"));
            profitMarginReport.getStock().getCentralSupplier().setName(rs.getString("centralsuppliername"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }

        try {
            profitMarginReport.setStockTakingPrice(rs.getBigDecimal("stocktakingprice"));
            profitMarginReport.setStockTakingQuantity(rs.getBigDecimal("stocktakingquantity"));
        } catch (Exception e) {

        }
        try {
            profitMarginReport.setOverallStockTakingPrice(rs.getBigDecimal("overallstocktakingprice"));
            profitMarginReport.setOverallStockTakingQuantity(rs.getBigDecimal("overallstocktakingquantity"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setTempOverallTotalSales(rs.getBigDecimal("tempoveralltotalsales"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setBeginToEndPurchaseReturnQuantity(rs.getBigDecimal("endtobeginpurchasereturnquantity"));
            profitMarginReport.setBeginToEndPurchaseReturnPrice(rs.getBigDecimal("endtobeginpurchasereturnprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setOverallBeginToEndPurchaseReturnQuantity(rs.getBigDecimal("overallendtobeginpurchasereturnquantity"));
            profitMarginReport.setOverallBeginToEndPurchaseReturnPrice(rs.getBigDecimal("overallendtobeginpurchasereturnprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setDifferencePrice(rs.getBigDecimal("differenceprice"));
        } catch (Exception e) {
        }
        try {
            profitMarginReport.setOverallDifferencePrice(rs.getBigDecimal("overalldifferenceprice"));
        } catch (Exception e) {
        }
        try {
           profitMarginReport.setzSalesQuantity(rs.getBigDecimal("zsalesquantity"));
           profitMarginReport.setzSalesPrice(rs.getBigDecimal("zsalesprice"));
        } catch (Exception e) {
        }
        try {
           profitMarginReport.setOverallZSalesQuantity(rs.getBigDecimal("overallzsalesquantity"));
           profitMarginReport.setOverallZSalesPrice(rs.getBigDecimal("overallzsalesprice"));
        } catch (Exception e) {
        }

        return profitMarginReport;
    }

}
