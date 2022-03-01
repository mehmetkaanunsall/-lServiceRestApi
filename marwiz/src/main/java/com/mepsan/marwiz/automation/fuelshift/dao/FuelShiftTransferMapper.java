/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 04:17:37
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FuelShiftTransferMapper implements RowMapper<FuelShiftSales> {
    
    @Override
    public FuelShiftSales mapRow(ResultSet rs, int i) throws SQLException {
        FuelShiftSales fuelShiftSales = new FuelShiftSales();
        
        try {
            fuelShiftSales.setAttendant(rs.getString("ssattendant"));
            fuelShiftSales.getAccount().setId(rs.getInt("empiaccount_id"));
            fuelShiftSales.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            fuelShiftSales.getAccount().setName(rs.getString("accname"));
            fuelShiftSales.getAccount().setTitle(rs.getString("acctitle"));
        } catch (Exception e) {
        }
        try {
            fuelShiftSales.setAttendantCode(rs.getString("ssattendantcode"));
        } catch (Exception e) {
        }
        try {
            fuelShiftSales.setTotalMoney(rs.getBigDecimal("ssltotalmoney"));
        } catch (Exception e) {
        }
        
        try {
            fuelShiftSales.getFuelSaleType().setTypeno(rs.getInt("sslsaletype"));
            fuelShiftSales.getFuelSaleType().setName(rs.getString("fstname"));
        } catch (Exception e) {
        }
        try {
            fuelShiftSales.setProcessDate(rs.getTimestamp("sslprocessdate"));
            fuelShiftSales.setStockName(rs.getString("stckname"));
            fuelShiftSales.setPrice(rs.getBigDecimal("sslprice"));
            fuelShiftSales.setLiter(rs.getBigDecimal("sslliter"));
            fuelShiftSales.setId(rs.getInt("sslid"));
            fuelShiftSales.getCredit().getAccount().setId(rs.getInt("accid"));
            fuelShiftSales.getCredit().getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            fuelShiftSales.getCredit().getAccount().setName(rs.getString("accname"));
            fuelShiftSales.getCredit().getAccount().setTitle(rs.getString("acctitle"));
        } catch (Exception e) {
        }
        try {
            fuelShiftSales.setPlate(rs.getString("sslplate"));
        } catch (Exception e) {
        }
        try {
            fuelShiftSales.setReceiptNo(rs.getString("sslreceiptno"));
        } catch (Exception e) {
        }
        
        return fuelShiftSales;
    }
    
}
