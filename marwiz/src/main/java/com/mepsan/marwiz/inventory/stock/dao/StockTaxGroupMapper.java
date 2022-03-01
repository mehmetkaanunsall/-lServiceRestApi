/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * Created on 04.11.2016 08:24:17
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockTaxGroupMapper implements RowMapper<StockTaxGroupConnection> {

    @Override
    public StockTaxGroupConnection mapRow(ResultSet rs, int i) throws SQLException {
        StockTaxGroupConnection stgc = new StockTaxGroupConnection();
        
        stgc.setId(rs.getInt("stgcid"));
        stgc.getTaxGroup().setId(rs.getInt("txgid"));
        stgc.getTaxGroup().setName(rs.getString("txgname"));
        stgc.setIsPurchase(rs.getBoolean("stgcis_purchase"));
        stgc.getTaxGroup().setRate(rs.getBigDecimal("txgrate"));
        stgc.getTaxGroup().getType().setId(rs.getInt("txgtype_id"));
        UserData userData = new UserData();

        userData.setId(rs.getInt("usdid"));
        userData.setName(rs.getString("usdname"));
        userData.setSurname(rs.getString("usdsurname"));
        userData.setUsername(rs.getString("usdusername"));

        stgc.setUserCreated(userData);
        stgc.setDateCreated(rs.getTimestamp("stgcc_time"));
        return stgc;
    }

}
