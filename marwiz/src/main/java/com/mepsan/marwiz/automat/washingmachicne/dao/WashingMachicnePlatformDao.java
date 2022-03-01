/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:05:27 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicnePlatformDao extends JdbcDaoSupport implements IWashingMachicnePlatformDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WashingPlatform> findAll(WashingMachicne obj) {
        String sql = "SELECT\n"
                + "                plf.id as plfid,\n"
                + "                plf.platformno as plfplatformno,\n"
                + "                plf.washingmachine_id as plfwashingmachine_id,\n"
                + "                plf.port as plfport,\n"
                + "                plf.barcodeaddress as plfbarcodeaddress,\n"
                + "                plf.barcodeport as plfbarcodeport,\n"
                + "                plf.barcodetimeout as plfbarcodetimeout,\n"
                + "                plf.is_active as plfis_active,\n"
                + "                plf.is_activebarcode as plfis_activebarcode,\n"
                + "                plf.description as plfdescription\n"
                + "                FROM wms.platform plf \n"
                + "                INNER JOIN wms.washingmachine wm  ON(wm.id=plf.washingmachine_id AND wm.deleted=FALSE)\n"
                + "                WHERE plf.deleted=FALSE AND plf.washingmachine_id=?;";
        Object[] param = new Object[]{obj.getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicnePlatformMapper());
    }

    @Override
    public int create(WashingPlatform obj) {
        String sql = "INSERT INTO \n"
                + "  wms.platform\n"
                + "(\n"
                + "  washingmachine_id,\n"
                + "  is_active,\n"
                + "  platformno,\n"
                + "  port,\n"
                + "  description,\n"
                + "  barcodeaddress,\n"
                + "  barcodeport,\n"
                + "  barcodetimeout,\n"
                + "  c_id,\n"
                + "  u_id,\n"
                + "  is_activebarcode\n"
                + ") \n"
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)"
                + "RETURNING id ;";

        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.isIsActive(), obj.getPlatformNo(), obj.getPort(), obj.getDescription(), obj.getBarcodeAddress(), obj.getBarcodePortNo().equals("0") ? null : obj.getBarcodePortNo(), obj.getBarcodeTimeOut() == 0 ? null : obj.getBarcodeTimeOut(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.isIsActiveBarcode()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(WashingPlatform obj) {
        String sql = "UPDATE \n"
                + "  wms.platform  \n"
                + "SET \n"
                + "  washingmachine_id = ?,\n"
                + "  platformno = ?,\n"
                + "  description = ?,\n"
                + " barcodeaddress = ?,\n"
                + "  barcodeport = ?,\n"
                + "  barcodetimeout = ?,\n"
                + "  is_activebarcode = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now(),\n"
                + "  port = ? ,\n"
                + "  is_active =?\n"
                + "WHERE \n"
                + "  id = ?;";
        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.getPlatformNo(), obj.getDescription(), obj.getBarcodeAddress(), obj.getBarcodePortNo().equals("0") ? null : obj.getBarcodePortNo(), obj.getBarcodeTimeOut() == 0 ? null : obj.getBarcodeTimeOut(), obj.isIsActiveBarcode(), sessionBean.getUser().getId(), obj.getPort(), obj.isIsActive(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }

    }

    @Override
    public int delete(WashingPlatform obj) {
        String sql = "UPDATE  wms.platform SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(WashingPlatform obj) {
        String sql = "SELECT CASE WHEN EXISTS (\n"
                + "SELECT platform_id FROM wms.nozzle WHERE platform_id=?  AND deleted=False) THEN 1  ELSE 0 END";

        Object[] param = new Object[]{obj.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WashingPlatform> findPlatformForWashingMachicne(String where) {
        String sql = "SELECT\n"
                + "	plf.id as plfid,\n"
                + "	plf.platformno as plfplatformno,\n"
                + "     plf.port as plfport,\n"
                + "     plf.barcodeaddress as plfbarcodeaddress,\n"
                + "     plf.barcodeport as plfbarcodeport,\n"
                + "     plf.is_activebarcode as plfis_activebarcode,\n"
                + "     plf.barcodetimeout as plfbarcodetimeout,\n"
                + "     plf.is_active as plfis_active, \n"
                + "FROM wms.platform plf\n"
                + "WHERE plf.deleted=FALSE " + where;

        return getJdbcTemplate().query(sql, new WashingMachicnePlatformMapper());

    }

}
