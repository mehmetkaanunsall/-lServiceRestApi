/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.03.2018 05:23:40
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import com.mepsan.marwiz.general.model.general.SaleItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SalesItemReportMapper implements RowMapper<SaleItem> {

    @Override
    public SaleItem mapRow(ResultSet rs, int i) throws SQLException {
        SaleItem saleItem = new SaleItem();
        saleItem.setId(rs.getInt("sliid"));
        saleItem.setProcessDate(rs.getTimestamp("sliprocessdate"));
        saleItem.getUnit().setId(rs.getInt("sliunit_id"));
        saleItem.getUnit().setSortName(rs.getString("guntsortname"));
        saleItem.getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        saleItem.setQuantity(rs.getBigDecimal("sliquantity"));
        saleItem.setUnitPrice(rs.getBigDecimal("sliunitprice"));
        saleItem.setDiscountPrice(rs.getBigDecimal("slidiscountprice"));
        saleItem.setTotalTax(rs.getBigDecimal("slitotaltax"));
        saleItem.setTotalPrice(rs.getBigDecimal("slitotalprice"));
        saleItem.setTotalMoney(rs.getBigDecimal("slitotalmoney"));
        saleItem.getCurrency().setId(rs.getInt("slicurrency_id"));
        saleItem.getStock().setId(rs.getInt("slistock_id"));
        saleItem.getStock().setName(rs.getString("stckname"));
        saleItem.getStock().setCode(rs.getString("stckcode"));
        saleItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        saleItem.getStock().setBarcode(rs.getString("stckbarcode"));
        saleItem.setIsManagerDiscount(rs.getBoolean("sliis_managerdiscount"));
        saleItem.getManagerUserData().setId(rs.getInt("slimanageruserdata_id"));
        saleItem.getManagerUserData().setName(rs.getString("us1name"));
        saleItem.getManagerUserData().setSurname(rs.getString("us1surname"));
        return saleItem;
    }
}
