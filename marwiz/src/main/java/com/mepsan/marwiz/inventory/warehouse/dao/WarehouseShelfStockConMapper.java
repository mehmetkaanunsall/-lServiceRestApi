/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 04:05:29
 */

package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class WarehouseShelfStockConMapper implements RowMapper<WarehouseShelfStockCon>{

    @Override
    public WarehouseShelfStockCon mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseShelfStockCon warehouseShelfStockCon=new WarehouseShelfStockCon();
        warehouseShelfStockCon.setId(rs.getInt("wsscid"));
        warehouseShelfStockCon.getStock().setId(rs.getInt("wsscstock_id"));
        warehouseShelfStockCon.getStock().setName(rs.getString("stckname"));
        warehouseShelfStockCon.getStock().setCode(rs.getString("stckcode"));
        warehouseShelfStockCon.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        warehouseShelfStockCon.getWarehouseShelf().setId(rs.getInt("wsscwarehouseshelf_id"));
        warehouseShelfStockCon.getWarehouseShelf().setName(rs.getString("wsname"));
        warehouseShelfStockCon.getWarehouseShelf().setCode(rs.getString("wscode"));
        
        return warehouseShelfStockCon;
    }

}
