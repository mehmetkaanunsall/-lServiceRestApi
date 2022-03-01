/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 01:29:21
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class WareHouseItemMapper implements RowMapper<WarehouseItem>{

    @Override
    public WarehouseItem mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseItem wareHouseItem=new WarehouseItem();
        wareHouseItem.setId(rs.getInt("iwid"));
        wareHouseItem.getStock().setId(rs.getInt("iwistock_id"));
        wareHouseItem.getStock().setName(rs.getString("stckname"));
        wareHouseItem.getStock().setCode(rs.getString("stckcode"));
        wareHouseItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        wareHouseItem.getStock().setBarcode(rs.getString("stckbarcode"));
        wareHouseItem.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        wareHouseItem.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        wareHouseItem.setQuantity(rs.getBigDecimal("iwiquantity"));
        wareHouseItem.setMinStockLevel(rs.getBigDecimal("iwiminstocklevel"));

        return wareHouseItem;
    }

}
