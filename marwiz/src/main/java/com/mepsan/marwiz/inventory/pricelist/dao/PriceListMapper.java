/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 12:09:26
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PriceListMapper implements RowMapper<PriceList> {

    @Override
    public PriceList mapRow(ResultSet rs, int i) throws SQLException {
        PriceList priceList = new PriceList();

        priceList.setId(rs.getInt("plid"));
        try {
            priceList.setName(rs.getString("plname"));
            priceList.setCode(rs.getString("plcode"));
            priceList.getStatus().setId(rs.getInt("plstatus_id"));
            priceList.getStatus().setTag(rs.getString("sttdname"));
            priceList.setIsDefault(rs.getBoolean("plis_default"));
            priceList.setIsPurchase(rs.getBoolean("plis_purchase"));

            priceList.setDateCreated(rs.getTimestamp("plc_time"));
            UserData userData = new UserData();
            userData.setId(rs.getInt("plc_id"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));

            priceList.setUserCreated(userData);
        } catch (Exception e) {
        }

        return priceList;
    }

}
