package com.mepsan.marwiz.inventory.starbucksstock.dao;

import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class StarbucksStockMapper implements RowMapper<StarbucksStock> {

    @Override
    public StarbucksStock mapRow(ResultSet rs, int i) throws SQLException {
        StarbucksStock starbucksStock = new StarbucksStock();

        starbucksStock.setId(rs.getInt("issid"));
        starbucksStock.setName(rs.getString("issname"));
        starbucksStock.setCode(rs.getString("isscode"));
        starbucksStock.setCenterStarbucksStock_id(rs.getInt("isscenterstarbucksstock_id"));

        return starbucksStock;

    }

}
