/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 02:48:06
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WarehouseMapper implements RowMapper<Warehouse> {

    @Override
    public Warehouse mapRow(ResultSet rs, int i) throws SQLException {
        Warehouse wareHouse = new Warehouse();
        wareHouse.setId(rs.getInt("iwid"));
        wareHouse.setName(rs.getString("iwname"));
        try {
            wareHouse.setCode(rs.getString("iwcode"));
            wareHouse.setDescription(rs.getString("iwdescription"));
            wareHouse.getStatus().setId(rs.getInt("iwstatus_id"));
            wareHouse.getStatus().setTag(rs.getString("stdname"));
        } catch (Exception e) {
        }
        try{
            wareHouse.setIsAutomat(rs.getBoolean("isautomat"));
        }catch (Exception e) {
        }
        
        try{
            wareHouse.getBranch().setId(rs.getInt("iwbranch_id"));
        }catch (Exception e) {
        }
        
        try {
            wareHouse.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            wareHouse.getStock().getStockInfo().setBalance(rs.getBigDecimal("sibalance"));
            wareHouse.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            wareHouse.getStock().getStockInfo().setIsMinusStockLevel(rs.getBoolean("siis_minusstocklevel"));
        } catch (Exception e) {
        }
        return wareHouse;

        
        
    }
}
