/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:31:32 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicneTankDao extends JdbcDaoSupport implements IWashingMachicneTankDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(WashingTank obj) {
        String sql = "INSERT INTO \n"
                + "  wms.tank\n"
                + "("
                + "  washingmachine_id,\n"
                + "  stock_id,\n"
                + "  tankno,\n"
                + "  mincapacity,\n"
                + "  capacity,\n"
                + "  balance,\n"
                + "  c_id,\n"
                + "  u_id \n"
                + ")\n"
                + "VALUES (?,?,?,?,?,?,?,?)\n"
                + "RETURNING id ;";
        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.getStock().getId(), obj.getTankNo(), obj.getMinCapacity(), obj.getCapacity(), obj.getBalance(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(WashingTank obj) {
        String sql = "UPDATE \n"
                + "  wms.tank \n"
                + "SET \n"
                + "  washingmachine_id = ?,\n"
                + "  stock_id = ?,\n"
                + "  tankno = ?,\n"
                + "  mincapacity = ?,\n"
                + "  capacity = ?,\n"
                + "  balance = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?;";

        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.getStock().getId(), obj.getTankNo(), obj.getMinCapacity(), obj.getCapacity(), obj.getBalance(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public List<WashingTank> findAll(WashingMachicne obj) {
        String sql = "SELECT \n"
                + "                   tnk.id as tnkid,\n"
                + "                   tnk.balance as tnkbalance,\n"
                + "                   tnk.capacity as tnkcapacity,\n"
                + "                   tnk.stock_id as tnkstockid,\n"
                + "                   tnk.tankno as tnktankno,\n"
                + "                   tnk.mincapacity as tnkmincapacity,\n"
                + "                   stck.name as stckname,\n"
                + "                   tnk.washingmachine_id as tnkwashingmachine_id,\n"
                + "                   stck.unit_id as stckunit_id,\n"
                + "                   unt.unitrounding as untunitrounding,\n"
                + "                   unt.sortname as untsortname\n"
                + "                   FROM wms.tank tnk \n"
                + "                   INNER JOIN wms.washingmachine wm  ON (wm.id=tnk.washingmachine_id AND wm.deleted=FALSE)\n"
                + "                   INNER JOIN inventory.stock stck ON(stck.id=tnk.stock_id AND stck.deleted=FALSE)\n"
                + "                   INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "                   WHERE tnk.deleted=FALSE AND tnk.washingmachine_id=?";

        Object[] param = new Object[]{obj.getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicneTankMapper());
    }

    @Override
    public int delete(WashingTank obj) {
        String sql = "UPDATE  wms.tank SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(WashingTank obj) {
        String sql = "SELECT CASE WHEN EXISTS (\n"
                + "               SELECT tank_id FROM wms.nozzle WHERE tank_id=?  AND deleted=False) THEN 1  ELSE 0 END";
        
          Object[] param = new Object[]{obj.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
