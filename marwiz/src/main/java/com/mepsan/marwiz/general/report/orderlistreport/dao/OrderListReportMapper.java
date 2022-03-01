/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:54:48
 */
package com.mepsan.marwiz.general.report.orderlistreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OrderListReportMapper implements RowMapper<OrderListReport> {

    @Override
    public OrderListReport mapRow(ResultSet rs, int i) throws SQLException {
        OrderListReport orderListReport = new OrderListReport();
        orderListReport.getStock().setId(rs.getInt("iwistock_id"));
        orderListReport.getStock().setName(rs.getString("stckname"));
        orderListReport.getStock().setCode(rs.getString("stckcode"));
        orderListReport.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        orderListReport.getStock().setBarcode(rs.getString("stckbarcode"));
        orderListReport.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        orderListReport.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        orderListReport.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        orderListReport.getStock().getStockInfo().setMinStockLevel(rs.getBigDecimal("stckiminstocklevel"));
        orderListReport.setQuantity(rs.getBigDecimal("sumquantity"));
        orderListReport.setCategory(rs.getString("category"));
        orderListReport.setCategory(StaticMethods.findCategories(orderListReport.getCategory()));
        orderListReport.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
        orderListReport.getStock().getBrand().setName(rs.getString("brname"));
        orderListReport.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
        orderListReport.getStock().getSupplier().setName(rs.getString("accname"));
        orderListReport.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        orderListReport.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        orderListReport.getBranch().setId(rs.getInt("brnid"));
        orderListReport.getBranch().setName(rs.getString("brnname"));

        return orderListReport;
    }

}
