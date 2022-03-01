/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:36:25 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductReportSoldTogetherMapper implements RowMapper<ProductReportSoldTogether> {

    @Override
    public ProductReportSoldTogether mapRow(ResultSet rs, int i) throws SQLException {

        ProductReportSoldTogether productReportSoldTogether = new ProductReportSoldTogether();
        productReportSoldTogether.getStock2().setId(rs.getInt("mslistock_id"));
        productReportSoldTogether.getStock2().setName(rs.getString("stckname"));
        productReportSoldTogether.getStock2().setCode(rs.getString("stckcode"));
        productReportSoldTogether.getStock2().setBarcode(rs.getString("stckbarcode"));
        productReportSoldTogether.getStock2().setSupplierProductCode(rs.getString("stcksupplierproductcode"));
        productReportSoldTogether.getStock2().getSupplier().setId(rs.getInt("stcksupplier_id"));
        productReportSoldTogether.getStock2().getSupplier().setName(rs.getString("accname"));
        productReportSoldTogether.getStock2().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        productReportSoldTogether.getStock2().getCentralSupplier().setName(rs.getString("csppname"));
        productReportSoldTogether.setQuantity(rs.getBigDecimal("quantity"));
        try {
            productReportSoldTogether.setHour(rs.getString("hour"));
        } catch (Exception e) {
        }
        try {
            productReportSoldTogether.setMonth(rs.getInt("month"));
        } catch (Exception e) {
        }
        try {
            productReportSoldTogether.setYear(rs.getInt("year"));
        } catch (Exception e) {
        }
        try {
            productReportSoldTogether.setProcessDate(rs.getDate("day"));
        } catch (Exception e) {
        }

        try {
            productReportSoldTogether.setEndWeekDay(rs.getDate("endWeekDay"));
            productReportSoldTogether.setFirstWeekDay(rs.getDate("firstWeekDay"));
        } catch (Exception e) {
        }

        try {
            productReportSoldTogether.setShiftNo(rs.getString("mslshiftno"));
        } catch (Exception e) {
        }

        return productReportSoldTogether;
    }

}
