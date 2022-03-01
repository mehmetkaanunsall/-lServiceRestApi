/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 05:20:28
 */
package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WarehouseReceiptDao extends JdbcDaoSupport implements IWarehouseReceiptDao {

    @Autowired
    private SessionBean sessionBean;

    @Override
    public List<WarehouseReceipt> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String column = "";

        if (sortField == null) {
            sortField = "whr.processdate";
            sortOrder = "desc";
        }
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranchSetting().getErpIntegrationId() == 1) {
            column = "(\n"
                    + "SELECT \n"
                    + "swh.is_send as is_send\n"
                    + "FROM integration.sap_warehousereceipt swh \n"
                    + "WHERE swh.deleted=FALSE AND swh.object_id = whr.id AND swh.branch_id = wh.branch_id\n"
                    + " ORDER BY swh.c_time ASC LIMIT 1) AS swhris_send,\n";
           
        }

        String sql = "select \n"
                + "whr.id as whrid,\n"
                + "whr.warehouse_id as whrwarehouse_id,\n"
                + "wh.name as whname,\n"
                + "whr.receiptnumber as whrreceiptnumber,\n"
                + "whr.is_direction as whris_direction,\n"
                + "whr.type_id as whrtype_id,\n"
                + "typd.name as typdname,\n"
                + column
                + "whr.processdate as whrprocessdate\n"
                + "from inventory.warehousereceipt whr\n"
                + "inner join inventory.warehouse wh on (whr.warehouse_id=wh.id and wh.deleted=false)\n"
                + "inner join system.type_dict typd on (typd.type_id=whr.type_id and typd.language_id=?)\n"
                + "where wh.branch_id=? and whr.deleted=false" + where + "\n"
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<WarehouseReceipt> result = getJdbcTemplate().query(sql, param, new WarehouseReceiptMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "select \n"
                + "count(whr.id) as whrid\n"
                + "from inventory.warehousereceipt whr\n"
                + "inner join inventory.warehouse wh on (whr.warehouse_id=wh.id and wh.deleted=false)\n"
                + "inner join system.type_dict typd on (typd.type_id=whr.type_id and typd.language_id=?)\n"
                + "where wh.branch_id=? and whr.deleted=false " + where;
        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int create(WarehouseReceipt obj) {

        String sql = " SELECT r_receipt_id FROM inventory.process_warehousereceipt(?, ?, ?, ?, ?, ?, ?, ? );";
        Object[] param = {0, obj.getId(), obj.getWarehouse().getId(), obj.getReceiptNumber(), obj.isIsDirection(), obj.getType().getId(), obj.getProcessDate(), sessionBean.getUser().getId()};

        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WarehouseReceipt obj) {

        String sql = " SELECT r_receipt_id FROM inventory.process_warehousereceipt(?, ?, ?, ?, ?, ?, ?, ? );";
        Object[] param = {1, obj.getId(), obj.getWarehouse().getId(), obj.getReceiptNumber(), obj.isIsDirection(), obj.getType().getId(), obj.getProcessDate(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {2, warehouseReceipt.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(WarehouseReceipt warehouseReceipt) {
        String sql = " SELECT r_receipt_id FROM inventory.process_warehousereceipt(?, ?, ?, ?, ?, ?, ?, ? );";
        Object[] param = {2, warehouseReceipt.getId(), warehouseReceipt.getWarehouse().getId(), warehouseReceipt.getReceiptNumber(), warehouseReceipt.isIsDirection(), warehouseReceipt.getType().getId(), warehouseReceipt.getProcessDate(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int sendWasteCenter(WarehouseReceipt warehouseReceipt) {

        String sql = "SELECT log.insertjson_waste(?,?,?);";
        Object[] param = new Object[]{warehouseReceipt.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int deleteWasteInfo(WarehouseReceipt warehouseReceipt) {
        String sql = "UPDATE inventory.wasteiteminfo SET deleted=TRUE, u_id=?, u_time=NOW() WHERE warehousemovement_id IN (SELECT whm.id AS whmid \n"
                + "FROM  inventory.warehousemovement whm  \n"
                + "WHERE whm.warehousereceipt_id=? )\n";
        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouseReceipt.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateLogSap(WarehouseReceipt warehouseReceipt) {

        String sql = "UPDATE \n"
                + "      	integration.sap_warehousereceipt\n"
                + "    SET\n"
                + "        is_send   		= FALSE,\n"
                + "        senddate  		= NULL,\n"
                + "        sendcount 		= NULL,\n"
                + "        response 		= NULL,\n"
                + "        receiptnumber	= NULL,\n"
                + "        sessionnumber        = NULL,\n"
                + "        u_id	  		= ?,\n"
                + "        u_time	  		= NOW()\n"
                + "    WHERE \n"
                + "        object_id = ? AND branch_id = ?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouseReceipt.getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }
}
