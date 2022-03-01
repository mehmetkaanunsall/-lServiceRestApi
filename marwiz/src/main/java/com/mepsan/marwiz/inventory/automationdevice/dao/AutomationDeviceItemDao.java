/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 02:05:17
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomationDeviceItemDao extends JdbcDaoSupport implements IAutomationDeviceItemDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomationDeviceItem> listOfShelf(AutomationDevice obj) {
        String sql = "SELECT\n"
                  + "   vmi.id as vmiid,\n"
                  + "   stck.id as stckid,\n"
                  + "   stck.name as stckname,\n"
                  + "   stck.barcode as stckbarcode,\n"
                  + "   vmi.shelfno as vmishelfno,\n"
                  + "   vmi.maxstocklevel as vmimaxstocklevel,\n"
                  + "   vmi.type_id AS vmitype_id,\n"
                  + "   typd.name AS typdname\n"
                  + "FROM inventory.vendingmachineitem vmi\n"
                  + "INNER JOIN inventory.vendingmachine vm ON(vm.id = vmi.vendingmachine_id AND vm.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.stock stck on (vmi.stock_id=stck.id and stck.deleted=false)\n"
                  + "LEFT JOIN system.type_dict typd ON(typd.type_id = vmi.type_id AND typd.language_id = ?)\n"
                  + "where vmi.deleted=false and vmi.vendingmachine_id=?\n"
                  + "order by vmi.shelfno";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};
        List<AutomationDeviceItem> result = getJdbcTemplate().query(sql, param, new AutomationDeviceItemMapper());
        return result;
    }

    @Override
    public List<AutomationDeviceItem> listOfShelfOnlyWithProduct(AutomationDevice obj) {
        String sql = "SELECT\n"
                  + "   vmi.id as vmiid,\n"
                  + "   stck.id as stckid,\n"
                  + "   stck.name as stckname,\n"
                  + "   stck.barcode as stckbarcode,\n"
                  + "   vmi.shelfno as vmishelfno,\n"
                  + "   vmi.maxstocklevel as vmimaxstocklevel,\n"
                  + "   vmi.type_id AS vmitype_id,\n"
                  + "   typd.name AS typdname,\n"
                  + "   vmi.balance AS vmibalance,\n"
                  + "   (SELECT wi.quantity FROM inventory.warehouseitem wi WHERE wi.deleted=FALSE AND wi.stock_id = stck.id AND wi.warehouse_id = vm.warehouse_id limit 1) AS warehousequantity\n"
                  + "FROM inventory.vendingmachineitem vmi\n"
                  + "INNER JOIN inventory.vendingmachine vm ON(vm.id = vmi.vendingmachine_id AND vm.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.stock stck on (vmi.stock_id=stck.id and stck.deleted=false)\n"
                  + "LEFT JOIN system.type_dict typd ON(typd.type_id = vmi.type_id AND typd.language_id = ?)\n"
                  + "where vmi.deleted=false and vmi.vendingmachine_id=? and vmi.stock_id IS NOT NULL\n"
                  + "order by vmi.shelfno";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};
        List<AutomationDeviceItem> result = getJdbcTemplate().query(sql, param, new AutomationDeviceItemMapper());
        return result;
    }

    @Override
    public int create(AutomationDeviceItem obj) {
        String sql = "insert into inventory.vendingmachineitem\n"
                  + "(vendingmachine_id,stock_id,shelfno,c_id,u_id)\n"
                  + "values\n"
                  + "(?,?,?,?,?)\n"
                  + "returning id;";
        Object[] param = new Object[]{obj.getAutomationDevice().getId(), obj.getStock().getId() == 0 ? null : obj.getStock().getId(), obj.getShelfNo(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AutomationDeviceItem obj, boolean isStockChange) {
        String sql = "";
        Object[] param;
        if (obj.getAutomationDevice().getDeviceType().getId() != 111) {
            if (isStockChange) {

                if (obj.getStock().getId() == 0) {
                    sql = "update inventory.vendingmachineitem\n"
                              + "set\n"
                              + "stock_id=?,\n"
                              + "type_id = ?\n,"
                              + "maxstocklevel = ?,\n"
                              + "u_id=?,\n"
                              + "u_time=now()\n"
                              + "where id = ?;"
                              + "UPDATE inventory.warehouseshelf_stock_con SET deleted = TRUE , u_id = ?, d_time=NOW() WHERE deleted=False AND warehouseshelf_id IN (SELECT ws.id FROM inventory.warehouseshelf ws WHERE ws.deleted=FALSE AND ws.warehouse_id = ? AND cast(ws.code AS INTEGER) = ?);";
                    param = new Object[]{null, obj.getType().getId(), obj.getMaxStockLevel(),
                        sessionBean.getUser().getId(), obj.getId(),
                        sessionBean.getUser().getId(), obj.getAutomationDevice().getWarehouse().getId(), obj.getShelfNo()};
                } else {
                    sql = "update inventory.vendingmachineitem\n"
                              + "set\n"
                              + "stock_id=?,\n"
                              + "type_id = ?\n,"
                              + "maxstocklevel = ?,\n"
                              + "u_id=?,\n"
                              + "u_time=now()\n"
                              + "where id = ?;"
                              + "UPDATE inventory.warehouseshelf_stock_con SET deleted = TRUE , u_id = ?, d_time=NOW() WHERE deleted=False AND warehouseshelf_id IN (SELECT ws.id FROM inventory.warehouseshelf ws WHERE ws.deleted=FALSE AND ws.warehouse_id = ? AND cast(ws.code AS INTEGER) = ?);"
                              + "INSERT INTO inventory.warehouseshelf_stock_con (warehouseshelf_id, stock_id, c_id, u_id) VALUES ((SELECT ws.id FROM inventory.warehouseshelf ws WHERE ws.deleted=FALSE AND ws.warehouse_id = ? AND cast(ws.code AS INTEGER) = ?), ?, ?,?);";
                    param = new Object[]{obj.getStock().getId(), obj.getType().getId(), obj.getMaxStockLevel(),
                        sessionBean.getUser().getId(), obj.getId(),
                        sessionBean.getUser().getId(), obj.getAutomationDevice().getWarehouse().getId(), obj.getShelfNo(),
                        obj.getAutomationDevice().getWarehouse().getId(), obj.getShelfNo(), obj.getStock().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
                }
            } else {
                sql = "update inventory.vendingmachineitem\n"
                          + "set\n"
                          + "stock_id=?,\n"
                          + "type_id = ?\n,"
                          + "maxstocklevel = ?,\n"
                          + "u_id=?,\n"
                          + "u_time=now()\n"
                          + "where id = ?;";
                param = new Object[]{obj.getStock().getId() == 0 ? null : obj.getStock().getId(), obj.getType().getId(), obj.getMaxStockLevel(),
                    sessionBean.getUser().getId(), obj.getId()};
            }
        } else {
            sql = "update inventory.vendingmachineitem\n"
                      + "set\n"
                      + "stock_id=?,\n"
                      + "shelfno= ?,\n"
                      + "u_id=?,\n"
                      + "u_time=now()\n"
                      + "where id = ?;";
            param = new Object[]{obj.getStock().getId(), obj.getShelfNo(),
                sessionBean.getUser().getId(), obj.getId()};
        }

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(AutomationDeviceItem automationDeviceItem) {

        String sql = "UPDATE inventory.vendingmachineitem SET deleted=TRUE, u_id=? , d_time=NOW() WHERE deleted=False AND id=?;";

        Object[] param = new Object[]{sessionBean.getUser().getId(), automationDeviceItem.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createMovement(AutomationDeviceItemMovement automationDeviceItemMovement) {

        String sql = "INSERT INTO\n"
                  + "   inventory.vendingmachineitemmovement\n"
                  + "   (vendingmachineitem_id, stock_id, quantity, is_direction, type_id, processdate, c_id, u_id)\n"
                  + "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)  RETURNING id ;";

        Object[] param = new Object[]{automationDeviceItemMovement.getAutomationDeviceItem().getId(), automationDeviceItemMovement.getAutomationDeviceItem().getStock().getId(),
            automationDeviceItemMovement.getQuantity(), automationDeviceItemMovement.isIsDirection(), automationDeviceItemMovement.getType(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AutomationDeviceItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
