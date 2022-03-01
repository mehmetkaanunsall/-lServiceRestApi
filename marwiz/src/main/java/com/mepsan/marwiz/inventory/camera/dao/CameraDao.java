/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 10:57:56
 */
package com.mepsan.marwiz.inventory.camera.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Camera;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CameraDao extends JdbcDaoSupport implements ICameraDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Camera> findAll() {
        String sql = "SELECT \n"
                + "  cam.id AS camid ,\n"
                + "  cam.branch_id AS cambranch_id,\n"
                + "  cam.url AS camurl,\n"
                + "  cam.port AS camport,\n"
                + "  cam.username AS camusername,\n"
                + "  cam.password AS campassword,\n"
                + "  cam.pumpno AS campumpno\n"
                + "FROM \n"
                + "  inventory.camera  cam\n"
                + "WHERE cam.deleted = FALSE AND cam.branch_id = ?;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<Camera> result = getJdbcTemplate().query(sql, param, new CameraMapper());
        return result;
    }

    @Override
    public int delete(Camera camera) {
        String sql = "UPDATE inventory.camera SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), camera.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int create(Camera obj) {
        String sql = "insert into inventory.camera\n"
                + "(branch_id,  url,  port,  username,  password,  pumpno,  c_id, u_id)\n"
                + "values(?,?,?,?,?,?,?,?)\n"
                + "returning id;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getIpAddress(), obj.getPort(), obj.getUsername(), obj.getPassword(), obj.getPumpNo(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Camera obj) {
        String sql = "UPDATE \n"
                + "  inventory.camera \n"
                + "SET \n"
                + "  url = ?,\n"
                + "  port = ?,\n"
                + "  username = ?,\n"
                + "  password = ?,\n"
                + "  pumpno = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?;";
        Object[] param = new Object[]{obj.getIpAddress(), obj.getPort(), obj.getUsername(), obj.getPassword(), obj.getPumpNo(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
