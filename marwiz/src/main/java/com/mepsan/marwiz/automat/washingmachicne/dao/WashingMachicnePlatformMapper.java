/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:09:24 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WashingMachicnePlatformMapper implements RowMapper<WashingPlatform> {

    @Override
    public WashingPlatform mapRow(ResultSet rs, int i) throws SQLException {
        WashingPlatform platform = new WashingPlatform();

        platform.setId(rs.getInt("plfid"));
        platform.setPlatformNo(rs.getString("plfplatformno"));
        platform.setPort(rs.getString("plfport"));
        platform.setIsActive(rs.getBoolean("plfis_active"));
        try {

            platform.getWashingMachicne().setId(rs.getInt("plfwashingmachine_id"));
        } catch (Exception e) {
        }

        try {
            platform.setDescription(rs.getString("plfdescription"));
        } catch (Exception e) {
        }
        
        try {
            platform.setBarcodeAddress(rs.getString("plfbarcodeaddress"));
            platform.setBarcodePortNo(rs.getString("plfbarcodeport"));
            platform.setBarcodeTimeOut(rs.getInt("plfbarcodetimeout"));
        } catch (Exception e) {
        }
        
        try {
            platform.setIsActiveBarcode(rs.getBoolean("plfis_activebarcode"));
        } catch (Exception e) {
        }
        
        return platform;

    }
}
