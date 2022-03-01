/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 09:02:00
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockTakingItemMapper implements RowMapper<StockTakingItem> {

    @Override
    public StockTakingItem mapRow(ResultSet rs, int i) throws SQLException {
        StockTakingItem stockTakingItem = new StockTakingItem();
        try {
            stockTakingItem.setId(rs.getInt("stiid"));
        } catch (Exception e) {

        }
        try {
            stockTakingItem.getStock().setId(rs.getInt("stckid"));
        } catch (Exception e) {

        }

        try {
            stockTakingItem.getStock().setName(rs.getString("stckname"));
            stockTakingItem.getStock().setCode(rs.getString("stckcode"));
            stockTakingItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            stockTakingItem.getStock().setBarcode(rs.getString("stckbarcode"));
        } catch (Exception e) {

        }

        try {
            stockTakingItem.getStock().getUnit().setName(rs.getString("guntname"));
            stockTakingItem.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            stockTakingItem.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        } catch (Exception e) {

        }

        try {
            stockTakingItem.setProcessDate(rs.getTimestamp("stiprocessdate"));
        } catch (Exception e) {

        }

        try {
            if (stockTakingItem.getId() == 0) {
                try {
                    stockTakingItem.setSystemQuantity(rs.getBigDecimal("iwiquantity"));
                } catch (Exception e) {

                }
            } else {
                stockTakingItem.setSystemQuantity(rs.getBigDecimal("stisystemquantity"));
            }

        } catch (Exception e) {

        }

        try {
            stockTakingItem.setRealQuantity(rs.getBigDecimal("stirealquantity"));
        } catch (Exception e) {

        }
        try {

            stockTakingItem.setPrice(rs.getBigDecimal("price"));
            stockTakingItem.getCurrency().setId(rs.getInt("prlicurrency_id"));
        } catch (Exception e) {

        }

        try {
            stockTakingItem.setId(rs.getInt("id"));
            stockTakingItem.getStock().setId(rs.getInt("stock_id"));
            stockTakingItem.getStock().setName(rs.getString("name"));
            stockTakingItem.setRealQuantity(rs.getBigDecimal("realquantity"));
            stockTakingItem.setEntryQuantity(rs.getBigDecimal("entry"));
            stockTakingItem.setExitQuantity(rs.getBigDecimal("exit"));
            stockTakingItem.setSystemQuantity(rs.getBigDecimal("systemquantity").subtract((stockTakingItem.getExitQuantity().subtract(stockTakingItem.getEntryQuantity()))));
            stockTakingItem.setCurrentQuantity(stockTakingItem.getRealQuantity().subtract((stockTakingItem.getExitQuantity().subtract(stockTakingItem.getEntryQuantity()))));
        } catch (Exception e) {

        }

        try {
            UserData userData = new UserData();

            stockTakingItem.setDateCreated(rs.getTimestamp("stic_time"));
            userData.setId(rs.getInt("usdid"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            stockTakingItem.setUserCreated(userData);
        } catch (Exception e) {
        }

        return stockTakingItem;
    }

}
