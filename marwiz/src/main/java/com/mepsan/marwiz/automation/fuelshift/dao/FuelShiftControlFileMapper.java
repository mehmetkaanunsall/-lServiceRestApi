/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   10.06.2019 01:39:29
 */

package com.mepsan.marwiz.automation.fuelshift.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class FuelShiftControlFileMapper implements RowMapper<FuelShiftControlFile>{

    @Override
    public FuelShiftControlFile mapRow(ResultSet rs, int i) throws SQLException {
        FuelShiftControlFile fuelShiftControlFile=new FuelShiftControlFile();
        fuelShiftControlFile.setShiftNo(rs.getString("r_shiftno"));
        fuelShiftControlFile.setFileName(rs.getString("r_filename"));
        
        return fuelShiftControlFile;
    }

}