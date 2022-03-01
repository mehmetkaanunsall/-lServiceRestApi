/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 07.02.2019 11:14:00
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TankItemMapper implements RowMapper<WarehouseItem> {

    @Override
    public WarehouseItem mapRow(ResultSet rs, int i) throws SQLException {

        WarehouseItem wareHouseItem = new WarehouseItem();

        wareHouseItem.getStock().setId(rs.getInt("stckid"));
        wareHouseItem.setId(rs.getInt("iwid"));
        wareHouseItem.getStock().setName(rs.getString("stckname"));
        wareHouseItem.getStock().setCode(rs.getString("stckcode"));
        wareHouseItem.getStock().setBarcode(rs.getString("stckbarcode"));
        wareHouseItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));

        return wareHouseItem;

    }
}
