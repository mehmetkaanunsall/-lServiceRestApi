/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 10:59:59
 */
package com.mepsan.marwiz.inventory.camera.dao;

import com.mepsan.marwiz.general.model.inventory.Camera;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CameraMapper implements RowMapper<Camera> {

    @Override
    public Camera mapRow(ResultSet rs, int i) throws SQLException {
        Camera c = new Camera();
        c.setId(rs.getInt("camid"));
        c.getBranch().setId(rs.getInt("cambranch_id"));
        c.setIpAddress(rs.getString("camurl"));
        c.setPort(rs.getString("camport"));
        c.setUsername(rs.getString("camusername"));
        c.setPassword(rs.getString("campassword"));
        c.setPumpNo(rs.getString("campumpno"));
        return c;
    }

}
