/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:57:12 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicneDao extends JdbcDaoSupport implements IWashingMachicneDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WashingMachicne> findAll(String where) {
        String sql = "SELECT\n"
                + "              wm.id as wmid,\n"
                + "              wm.code as wmcode,\n"
                + "              wm.name as wmname,\n"
                + "              wm.ipaddress as wmipaddress,\n"
                + "              wm.macaddress as wmmacaddress,\n"
                + "              wm.version as wmversion,\n"
                + "              wm.description as wmdescription,\n"
                + "              wm.port as wmport,\n"
                + "              wm.electricunitprice as wmelectricunitprice,\n"
                + "              wm.waterunitprice as wmwaterunitprice,\n"
                + "              wm.status_id as wmstatus_id,\n"
                + "              sttd.name as sttdname\n"
                + "            FROM wms.washingmachine wm\n"
                + "            LEFT JOIN system.status_dict sttd ON(sttd.status_id=wm.status_id AND sttd.language_id=?)\n"
                + "            WHERE wm. deleted=FALSE AND wm.branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicneMapper());
    }

    @Override
    public int create(WashingMachicne obj) {
        String sql = "INSERT INTO \n"
                + "  wms.washingmachine\n"
                + "(\n"
                + "  branch_id,\n"
                + "  code,\n"
                + "  name,\n"
                + "  status_id,\n"
                + "  macaddress,\n"
                + "  ipaddress,\n"
                + "  port,\n"
                + "  version,\n"
                + "  description,\n"
                + "  electricunitprice,\n"
                + "  waterunitprice,\n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)\n"
                + "RETURNING id ;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getCode(), obj.getName(), obj.getStatus().getId(),
            obj.getMacAddress(), obj.getIpAddress(), obj.getPort(), obj.getVersion(), obj.getDescription(), obj.getElectricUnitPrice(), obj.getWaterUnitPrice(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(WashingMachicne obj) {
        String sql = "UPDATE \n"
                + "  wms.washingmachine  \n"
                + "SET \n"
                + "  branch_id = ?,\n"
                + "  code = ?,\n"
                + "  name = ?,\n"
                + "  status_id = ?,\n"
                + "  macaddress = ?,\n"
                + "  ipaddress = ?,\n"
                + "  port = ?,\n"
                + "  version = ?,\n"
                + "  description = ?,\n"
                + " electricunitprice = ?,\n"
                + "  waterunitprice = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getCode(), obj.getName(), obj.getStatus().getId(),
            obj.getMacAddress(), obj.getIpAddress(), obj.getPort(), obj.getVersion(), obj.getDescription(), obj.getElectricUnitPrice(), obj.getWaterUnitPrice(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int delete(WashingMachicne obj) {
        String sql = "UPDATE  wms.washingmachine SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(WashingMachicne obj) {
        String sql = "SELECT CASE WHEN EXISTS (\n"
                + "SELECT washingmachine_id FROM wms.platform WHERE washingmachine_id=?  AND deleted=False) THEN 1\n"
                + "                   WHEN EXISTS (SELECT washingmachine_id FROM wms.tank WHERE washingmachine_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{obj.getId(), obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    public List<WashingMachicne> selectWashinMachine(String where) {
        String sql = "SELECT\n"
                + "  wm.id as wmid,\n"
                + "  wm.code as wmcode,\n"
                + "  wm.name as wmname\n"
                + "FROM wms.washingmachine wm\n"
                + "   WHERE wm.deleted=FALSE AND wm.branch_id=?\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicneMapper());
    }

    @Override
    public String sendConfiguration(WashingMachicne obj) {
        String sql = "SELECT  to_json(configuration)  \"Configuration\"\n"
                + "       FROM (\n"
                + "        SELECT  array_to_json(array_agg(to_json(subconfiguration) )) \"Configurations\"\n"
                + "               FROM (\n"
                + "                SELECT \n"
                + "                      (SELECT \n"
                + "                                concat(brchs.applicationserverurl,'?wsdl')\n"
                + "                       FROM \n"
                + "                          wms.washingmachine wm \n"
                + "                          INNER JOIN general.branchsetting brchs ON (brchs.branch_id = wm.branch_id AND brchs.deleted = FALSE)\n"
                + "                       WHERE \n"
                + "                         wm.deleted = FALSE AND wm.id =? LIMIT 1\n"
                + "                 ) AS \"ApplicationURL\",\n"
                + "                 subtableplatform.\"PeronInfo\"\n"
                + "                 FROM(\n"
                + "                      SELECT  array_to_json(array_agg(to_json(subplatforminfo) )) \"PeronInfo\"\n"
                + "                                  FROM (\n"
                + "                                  SELECT\n"
                + "                                   wmp.platformno as \"PeronNo\",\n"
                + "                                   to_char(CURRENT_DATE,'MM/dd/YYYY') as \"Date\",\n"
                + "                                   to_char(CURRENT_TIMESTAMP ,'HH24:MI:SS') as \"Time\",\n"
                + "                                    wmp.port as \"PortNo\",\n"
                + "                                   (CASE WHEN wmp.is_active=TRUE THEN 1 ELSE 0 END) as \"Status\",\n"
                + "                                   (CASE WHEN wmp.barcodeaddress IS NOT NULL AND wmp.barcodeport IS NOT NULL AND wmp.barcodetimeout IS NOT NULL THEN wmp.barcodeaddress ELSE '' END) as \"BarcodeAddress\",\n"
                + "                                   (CASE WHEN wmp.barcodeaddress IS NOT NULL AND wmp.barcodeport IS NOT NULL AND wmp.barcodetimeout IS NOT NULL THEN wmp.barcodeport ELSE '' END)  as \"BarcodePortNo\",\n"
                + "                                   (CASE WHEN wmp.barcodeaddress IS NOT NULL AND wmp.barcodeport IS NOT NULL AND wmp.barcodetimeout IS NOT NULL THEN CAST(wmp.barcodetimeout AS VARCHAR) ELSE ''  END)  as \"BarcodeTimeout\",\n"
                + "                                   (CASE WHEN wmp.is_activebarcode=TRUE THEN 1 ELSE 0 END) as \"BarcodeStatus\",\n"
                + "                                    (\n"
                + "                                          SELECT \n"
                + "                                              array_agg(row_to_json(subproductinfo)) as \"ProductInfo\"\n"
                + "                                          FROM	\n"
                + "                                          ( \n"
                + "                                               SELECT \n"
                + "                                                  stck.id as \"ProductNo\",\n"
                + "                                                  stck.name as \"Name\",\n"
                + "                                                  CAST(wmn.unitprice AS INT) as \"Price\",\n"
                + "                                                  wmn.operationtime as \"Time\"\n"
                + "                                               FROM wms.nozzle wmn\n"
                + "                                                INNER JOIN wms.tank wmt ON(wmt.id=wmn.tank_id AND wmt.deleted=FALSE)\n"
                + "                                                INNER JOIN inventory.stock stck ON(stck.id=wmt.stock_id AND stck.deleted=FALSE)\n"
                + "                                               WHERE wmn.deleted=FALSE AND  wmn.platform_id=wmp.id\n"
                + "                                               ORDER BY wmn.id\n"
                + "                                           ) subproductinfo\n"
                + "                                        )\n"
                + "                                                \n"
                + "                                  FROM wms.platform wmp\n"
                + "                               WHERE wmp.deleted=FALSE AND wmp.washingmachine_id=?\n"
                + "                               ORDER BY wmp.id\n"
                + "                        ) subplatforminfo\n"
                + "                 ) subtableplatform                         	\n"
                + "         )subconfiguration        \n"
                + ")configuration";


        Object[] param = new Object[]{obj.getId(), obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return ((SQLException) e.getCause()).getSQLState();
        }
    }
}
