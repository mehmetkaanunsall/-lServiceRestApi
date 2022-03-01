/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 11:05:45
 */
package com.mepsan.marwiz.general.centralsupplier.dao;

import com.mepsan.marwiz.general.model.general.CentralSupplier;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CentralSupplierMapper implements RowMapper<CentralSupplier> {

    @Override
    public CentralSupplier mapRow(ResultSet rs, int i) throws SQLException {
        CentralSupplier centralSupplier = new CentralSupplier();
        centralSupplier.setId(rs.getInt("csppid"));
        centralSupplier.setName(rs.getString("csppname"));
        try {
            centralSupplier.setTagQuantity(rs.getInt("tagquantity"));
        } catch (Exception e) {
        }

        return centralSupplier;
    }

}
