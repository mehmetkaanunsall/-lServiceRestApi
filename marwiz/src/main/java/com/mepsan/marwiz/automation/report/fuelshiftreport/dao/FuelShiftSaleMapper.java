package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samet.dag
 */
public class FuelShiftSaleMapper implements RowMapper<FuelShiftSales> {

    @Override
    public FuelShiftSales mapRow(ResultSet rs, int i) throws SQLException {
        FuelShiftSales fs = new FuelShiftSales();

        fs.setTotalMoney(rs.getBigDecimal("sstotalmoney"));
        
        try {
            fs.setLiter(rs.getBigDecimal("sstotalquantity"));
        } catch (Exception e) {
        }

        try {
            fs.setAttendant(rs.getString("ssattendant"));
        } catch (Exception e) {

        }

        try {
            fs.setStockName(rs.getString("ssstckname"));
        } catch (Exception e) {

        }

        try {
            fs.getFuelSaleType().setName(rs.getString("fstname"));
        } catch (Exception e) {

        }
        try {
            fs.getFuelSaleType().setId(rs.getInt("fstid"));
            fs.getFuelSaleType().setTypeno(rs.getInt("fsttypneno"));
            fs.setSaleCount(rs.getInt("salecount"));
        } catch (Exception e) {
        }
        try {
            fs.setLiter(rs.getBigDecimal("ssliter"));
        } catch (Exception e) {
        }
        try {
            fs.setAttendantCode(rs.getString("ssattendantcode"));
        } catch (Exception e) {
        }
        try {
             fs.setId(rs.getInt("ssid"));
        } catch (Exception e) {
        }
        try {

            fs.getFuelShift().setShiftNo(rs.getString("ssshiftno"));
            fs.setProcessDate(rs.getTimestamp("ssprocessdate"));
            fs.setPumpno(rs.getString("sspumpno"));
            fs.setNozzleNo(rs.getString("ssnozzleno"));
            fs.setPrice(rs.getBigDecimal("ssprice"));
            fs.setDiscountTotal(rs.getBigDecimal("ssdistotal"));
            fs.setPlate(rs.getString("ssplate"));
            fs.setPaymentType(rs.getInt("sspaymenttype"));
            fs.setStockCode(rs.getString("ssstockcode"));
            fs.setAccountCode(rs.getString("ssaccountcode"));
        } catch (Exception e) {

        }
        try {
            fs.getUnit().setId(rs.getInt("guntid"));
            fs.getUnit().setSortName(rs.getString("guntsortname"));
            fs.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        } catch (Exception e) {
        }
        return fs;
    }

}