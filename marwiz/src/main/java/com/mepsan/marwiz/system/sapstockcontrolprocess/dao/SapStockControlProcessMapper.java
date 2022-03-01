/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class SapStockControlProcessMapper implements RowMapper<SapStockControlProcess> {
    
    @Override
    public SapStockControlProcess mapRow(ResultSet rs, int i) throws SQLException {
        SapStockControlProcess stockControl = new SapStockControlProcess();
        try {
            stockControl.setId(rs.getInt("r_id"));
            stockControl.getStock().setId(rs.getInt("r_stockid"));
            stockControl.setSapStockCode(rs.getString("r_sapstockcode"));
            stockControl.setSapQuantity(rs.getBigDecimal("r_sapquantity"));
            stockControl.setCenterStockCode(rs.getString("r_centerstockcode"));
            stockControl.getStock().setBarcode(rs.getString("r_barcode"));
            stockControl.getStock().setName(rs.getString("r_stockname"));
            stockControl.setErrorCode(rs.getInt("r_errorcode"));
            stockControl.setMarwizQuantity(rs.getBigDecimal("r_marwizquantity"));
            stockControl.getSapUnit().setSortName(rs.getString("r_sapunitcode"));
            
            stockControl.getSapUnit().setUnitRounding(rs.getInt("r_unitrounding"));
            
            stockControl.getStock().getUnit().setId(rs.getInt("r_guntid"));
            stockControl.getStock().getUnit().setSortName(rs.getString("r_guntsortname"));
            stockControl.getStock().getUnit().setUnitRounding(rs.getInt("r_guntunitrounding"));
            stockControl.getStock().setIsService(rs.getBoolean("r_isservice"));
            
        } catch (Exception e) {
        }
        
        return stockControl;
        
    }
    
}
