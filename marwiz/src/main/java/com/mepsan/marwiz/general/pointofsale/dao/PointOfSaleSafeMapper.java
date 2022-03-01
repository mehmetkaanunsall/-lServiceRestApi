/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:42:08
 */
package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PointOfSaleSafeMapper implements RowMapper<PointOfSaleSafeConnection> {
    
    @Override
    public PointOfSaleSafeConnection mapRow(ResultSet rs, int i) throws SQLException {
        PointOfSaleSafeConnection possc = new PointOfSaleSafeConnection();
        possc.setId(rs.getInt("pscid"));
        possc.getSafe().setId(rs.getInt("pscsafe_id"));
        possc.getSafe().setName(rs.getString("sfname"));
        possc.getSafe().setCode(rs.getString("sfcode"));
        possc.getSafe().getCurrency().setId(rs.getInt("sfcurrency_id"));
        
        return possc;
    }
    
}
