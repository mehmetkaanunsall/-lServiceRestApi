/**
 *
 * Bu sınıf, Tax Group nesnesini oluşturur ve özelliklerini set eder.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 16:57:09
 */
package com.mepsan.marwiz.inventory.taxgroup.dao;

import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TaxGroupMapper implements RowMapper<TaxGroup> {

    @Override
    public TaxGroup mapRow(ResultSet rs, int i) throws SQLException {

        TaxGroup taxGroup = new TaxGroup();

        taxGroup.setId(rs.getInt("txgid"));

        try {
            taxGroup.setName(rs.getString("txgname"));
        } catch (Exception e) {
        }
        try {
            taxGroup.setRate(rs.getBigDecimal("txgrate"));
        } catch (Exception e) {
        }
        try {
            taxGroup.getType().setId(rs.getInt("txgtype_id"));
        } catch (Exception e) {
        }
        try {
            taxGroup.getType().setId(rs.getInt("typdid"));
            taxGroup.getType().setTag(rs.getString("typdname"));
        } catch (Exception e) {
        }
        
        try {
            taxGroup.setCentertaxgroup_id(rs.getInt("txgcentertaxgroup_id"));
        } catch (Exception e) {
        }

        return taxGroup;

    }

}
