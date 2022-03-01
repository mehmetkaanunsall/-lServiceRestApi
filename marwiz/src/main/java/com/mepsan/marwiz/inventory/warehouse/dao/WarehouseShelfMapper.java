/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 12:49:11
 */

package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class WarehouseShelfMapper implements RowMapper<WarehouseShelf>{

    @Override
    public WarehouseShelf mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseShelf warehouseShelf=new WarehouseShelf();
        warehouseShelf.setId(rs.getInt("iwsid"));
        warehouseShelf.setName(rs.getString("iwsname"));
        warehouseShelf.setCode(rs.getString("iwscode"));
        
        return warehouseShelf;
    }

}
