/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.stockaccountreceipt.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ali.kurt
 */
class OfficialBranchSettingMapper implements RowMapper<BranchSetting> {


    @Override
    public BranchSetting mapRow(ResultSet rs, int i) throws SQLException {
        BranchSetting bs = new BranchSetting();
        
        bs.getBranch().setId(rs.getInt("brid"));
        bs.setErpUrl(rs.getString("brserpurl"));
        
        return bs;
    }
    
}
