/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.starbucksmachine.dao;

import com.mepsan.marwiz.general.model.inventory.StarbucksMachine;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ebubekir.buker
 */
public class StarbucksMachineMapper implements RowMapper<StarbucksMachine>{

    @Override
    public StarbucksMachine mapRow(ResultSet rs, int i) throws SQLException {
        StarbucksMachine starbucksMachine =new StarbucksMachine();
        starbucksMachine.setId(rs.getInt("id"));
        starbucksMachine.setName(rs.getString("name"));
        starbucksMachine.setCode(rs.getString("code"));
        starbucksMachine.setMachineBarcode(rs.getString("machinebarcode"));
        starbucksMachine.setPubKey(rs.getString("pubkey"));
        
        
        return starbucksMachine;
    }
    
}
