/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:36:30
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Vehicle;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class VehicleMapper implements RowMapper<Vehicle> {
    
    @Override
    public Vehicle mapRow(ResultSet rs, int i) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("vhcid"));
        vehicle.setPlate(rs.getString("vhcplate"));
        
        return vehicle;
    }
    
}
