/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:24:20 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicneNozzleDao extends JdbcDaoSupport implements IWashingMachicneNozzleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WashingNozzle> findAll(WashingPlatform obj) {
        String sql = "   SELECT\n"
                + "                           nz.id as nzid,\n"
                + "                           nz.nozzleno as nznozzleno,\n"
                + "                           nz.platform_id as nzplatformid,\n"
                + "                           nz.tank_id as nztankid,\n"
                + "                           tnk.tankno as tnktankno ,\n"
                + "         	      	      COALESCE(nz.unitprice,0) as nzunitprice,\n"
                + "                           nz.currency_id as nzcurrency_id,\n"
                + "                           cry.code as crycode,\n"
                + "         	       	      COALESCE(nz.operationamount,0) as nzoperationamount,\n"
                + "                           COALESCE(nz.operationtime,0) as nzoperationtime,\n"
                + "                           COALESCE(nz.electricamount,0) as nzelectricamount,\n"
                + "                           COALESCE(nz.wateramount,0) as nzwateramount,\n"
                + "                           stck.name as stckname,\n"
                + "                           stck.unit_id as stckunitid,\n"
                + "                           unt.sortname as untsortname,\n"
                + "                           unt.unitrounding as untunitrounding,\n"
                + "                           unt.name as untname\n"
                + "                           FROM wms.nozzle nz \n"
                + "                           INNER JOIN wms.tank tnk ON(tnk.id=nz.tank_id AND tnk.deleted=FALSE)\n"
                + "                           INNER JOIN inventory.stock stck ON(stck.id=tnk.stock_id AND stck.deleted=FALSE)\n"
                + "                           INNER JOIN general.unit unt ON(stck.unit_id=unt.id AND unt.deleted=FALSE)\n"
                + "                           LEFT JOIN system.currency cry ON(cry.id=nz.currency_id )\n"
                + "                           WHERE nz.deleted=FALSE AND nz.platform_id=?";

        Object[] param = new Object[]{obj.getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicneNozzleMapper());
    }

    @Override
    public int create(WashingNozzle obj) {
        String sql = "INSERT INTO \n"
                + "  wms.nozzle\n"
                + "(\n"
                + "  platform_id,\n"
                + "  tank_id,\n"
                + "  nozzleno,\n"
                + "  unitprice,\n"
                + "  currency_id,\n"
                + "  operationtime,\n"
                + "  operationamount,\n"
                + "  electricamount,\n"
                + "  wateramount,\n"
                + "  c_id,\n"
                + "  u_id \n"
                + ")\n"
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)\n"
                + "RETURNING id ;";

        Object[] param = new Object[]{obj.getWashingMachicnePlatform().getId(), obj.getWashingMachicneTank().getId(), obj.getNozzleNo(),
            obj.getUnitPrice(), obj.getCurrency().getId(), obj.getOperationTime(), obj.getOperationAmount(), obj.getElectricAmount(), obj.getWaterAmount(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(WashingNozzle obj) {
        String sql = "UPDATE \n"
                + "  wms.nozzle  \n"
                + "SET \n"
                + "  tank_id = ?,\n"
                + "  nozzleno = ?,\n"
                + "  unitprice = ?,\n"
                + "  currency_id = ?,\n"
                + "  operationtime = ?,\n"
                + "  operationamount = ?,\n"
                + "  electricamount = ?,\n"
                + "  wateramount = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?;";

        Object[] param = new Object[]{obj.getWashingMachicneTank().getId(), obj.getNozzleNo(), obj.getUnitPrice(), obj.getCurrency().getId(),
            obj.getOperationTime(), obj.getOperationAmount(), obj.getElectricAmount(), obj.getWaterAmount(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public List<WashingNozzle> findNozzleForPlatform(String where) {
        String sql = "SELECT\n"
                + "     nz.id as nzid,\n"
                + "     nz.nozzleno as nznozzleno\n"
                + "FROM wms.nozzle nz\n"
                + " WHERE nz.deleted=FALSE\n" + where;

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new WashingMachicneNozzleMapper());
    }

    @Override
    public int delete(WashingNozzle obj) {
        String sql = "UPDATE  wms.nozzle SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
