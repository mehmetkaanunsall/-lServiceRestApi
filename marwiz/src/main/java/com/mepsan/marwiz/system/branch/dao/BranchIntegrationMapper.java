/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.branch.dao;

/**
 *
 * @author asli.can
 */
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BranchIntegrationMapper implements RowMapper<BranchIntegration> {

    @Override
    public BranchIntegration mapRow(ResultSet rs, int i) throws SQLException {

        BranchIntegration branchintegration = new BranchIntegration();
        try {
            branchintegration.setId(rs.getInt("brintid"));
            branchintegration.setName(rs.getString("brintname"));
            branchintegration.setIntegrationtype(rs.getInt("brinttype_id"));
            branchintegration.setUsername2(rs.getString("brintusername2"));
            branchintegration.setDescription(rs.getString("brintdescription"));
            branchintegration.setHost2(rs.getString("brinthost2"));
            branchintegration.setParameter2(rs.getString("brintparameter2"));
            branchintegration.setParameter3(rs.getString("brintparameter3"));
            branchintegration.setParameter4(rs.getString("brintparameter4"));
            branchintegration.setParameter5(rs.getString("brintparameter5"));
            branchintegration.setPassword2(rs.getString("brintpassword2"));
            branchintegration.setTimeout2(rs.getInt("brinttimeout2"));
        } catch (Exception e) {
        }

        branchintegration.setHost1(rs.getString("brinthost"));
        branchintegration.setParameter1(rs.getString("brintparameter"));
        branchintegration.setUsername1(rs.getString("brintusername"));
        branchintegration.setPassword1(rs.getString("brintpassword"));
        branchintegration.setTimeout1(rs.getInt("brinttimeout"));
        branchintegration.getBranch().setId(rs.getInt("brintbranch_id"));

        return branchintegration;
    }

}
