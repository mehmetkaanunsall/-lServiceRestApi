/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   24.01.2018 08:26:33
 */
package com.mepsan.marwiz.general.cashregister.dao;

import com.mepsan.marwiz.general.model.general.CashRegister;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CashRegisterMapper implements RowMapper<CashRegister> {

    @Override
    public CashRegister mapRow(ResultSet rs, int i) throws SQLException {
        CashRegister cashRegister = new CashRegister();

        cashRegister.setId(rs.getInt("cashid"));
        cashRegister.setName(rs.getString("cashname"));

        try {
            cashRegister.getBrand().setId(rs.getInt("cashbrand_id"));
            cashRegister.getBrand().setName(rs.getString("brname"));
            cashRegister.setModel(rs.getString("cashmodel"));
            cashRegister.setSerialNumber(rs.getString("cashserialnumber"));
            cashRegister.setVersion(rs.getString("cashversion"));
            cashRegister.setMacAddress(rs.getString("cashmacaddress"));
            cashRegister.setIpAddress(rs.getString("cashipaddress"));
            cashRegister.setPort(rs.getString("cashport"));
            cashRegister.setIsExternalEftPos(rs.getBoolean("cashisexternaleftpos"));

        } catch (Exception e) {

        }
        return cashRegister;

    }

}
