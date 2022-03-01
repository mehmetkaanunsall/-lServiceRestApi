/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class StockAlternativeUnitMapper implements RowMapper<StockUnitConnection>{

    @Override
    public StockUnitConnection mapRow(ResultSet rs, int i) throws SQLException {
        StockUnitConnection stockUnitConnection=new StockUnitConnection();
        stockUnitConnection.setId(rs.getInt("isucid"));
        stockUnitConnection.getStock().setId(rs.getInt("isucstock_id"));
        stockUnitConnection.getUnit().setId(rs.getInt("isucunit_id"));
        stockUnitConnection.getUnit().setName(rs.getString("guntname"));
        stockUnitConnection.getUnit().setSortName(rs.getString("guntsortname"));
        stockUnitConnection.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        stockUnitConnection.setQuantity(rs.getBigDecimal("isucquantity"));
        stockUnitConnection.setIsOtherBranch(rs.getBoolean("isucis_otherbranch"));
        return stockUnitConnection;
    }
    
}
