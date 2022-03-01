/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 10:06:39
 */
package com.mepsan.marwiz.general.categorization.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CategorizationMapper implements RowMapper<Categorization> {

    @Override
    public Categorization mapRow(ResultSet rs, int i) throws SQLException {
        Categorization categorization = new Categorization();

        try {
            categorization.setId(rs.getInt("gctid"));
            categorization.setName(rs.getString("gctname"));
        } catch (Exception e) {
        }

        try {

            categorization.setChecked(rs.getBoolean("gctchecked"));
        } catch (Exception e) {
        }

        try {
            Categorization parentId = new Categorization();
            parentId.setId(rs.getInt("gctparent_id"));
            categorization.setParentId(parentId);
        } catch (Exception e) {
        }

        try {
            Categorization parentCategorization = new Categorization();
            parentCategorization.setId(rs.getInt("gctparent_id"));
            parentCategorization.setName(rs.getString("gctparentname"));

            categorization.setParentId(parentCategorization);
        } catch (Exception e) {
        }
        
        try {
            categorization.setTagQuantity(rs.getInt("tagquantity"));
        } catch (Exception e) {
        }

        return categorization;
    }

}
