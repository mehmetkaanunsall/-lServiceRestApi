/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2020 11:44:53
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomationDeviceMovementDao extends JdbcDaoSupport implements IAutomationDeviceMovementDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomationDeviceItemMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String sql = "SELECT \n"
                  + "    vmm.id AS vmmid,\n"
                  + "	 vmm.processdate AS vmmprocessdate,\n"
                  + "    vmm.type_id AS vmmtype_id, \n"
                  + "    vmm.stock_id AS vmmstock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    gunt.id AS guntid,\n"
                  + "    gunt.name AS guntname,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding as guntunitrounding,\n"
                  + "    vmm.vendingmachineitem_id AS vmmvendingmachineitem_id,\n"
                  + "    vmi.shelfno AS vmishelfno,\n"
                  + "    vmm.quantity AS vmmquantity,\n"
                  + "    vmm.is_direction AS vmmis_direction\n"
                  + "FROM inventory.vendingmachineitemmovement vmm \n"
                  + "LEFT JOIN inventory.stock stck ON(stck.id = vmm.stock_id)\n"
                  + "LEFT JOIN inventory.vendingmachineitem vmi ON(vmi.id = vmm.vendingmachineitem_id AND vmi.deleted=FALSE)\n"
                  + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                  + "WHERE vmm.deleted=FALSE\n"
                  + where + "\n"
                  + "ORDER BY vmm.processdate DESC\n"
                  + " limit " + pageSize + " offset " + first;

        return getJdbcTemplate().query(sql, new AutomationDeviceMovementMapper());
    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                  + "   COUNT(vmm.id) AS vmmcount\n"
                  + "FROM inventory.vendingmachineitemmovement vmm \n"
                  + "LEFT JOIN inventory.stock stck ON(stck.id = vmm.stock_id)\n"
                  + "LEFT JOIN inventory.vendingmachineitem vmi ON(vmi.id = vmm.vendingmachineitem_id AND vmi.deleted=FALSE)\n"
                  + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                  + "WHERE vmm.deleted=FALSE"
                  + where;
        int result = getJdbcTemplate().queryForObject(sql, Integer.class);
        return result;
    }

}
