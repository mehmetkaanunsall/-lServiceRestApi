/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 11:33:07
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomationDeviceDao extends JdbcDaoSupport implements IAutomationDeviceDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomationDevice> findAll(String where) {
        String sql = "select\n"
                  + "vm.id AS vmid,\n"
                  + "vm.name AS vmname,\n"
                  + "vm.ipadress AS vmipadress,\n"
                  + "vm.port AS vmport,\n"
                  + "vm.macaddress AS vmmacaddress,\n"
                  + "vm.description AS vmdescription, \n"
                  + "vm.warehouse_id AS vmwarehouse_id,\n"
                  + "vm.brand_id AS vmbrand_id,\n"
                  + "brd.name AS brdname,\n"
                  + "vm.protocol_id AS vmprotocol_id,\n"
                  + "prt.name AS prtname,\n"
                  + "prt.protocolno AS prtprotocolno,\n"
                  + "vm.type_id AS vmtype_id,\n"
                  + "typd.name AS typdname\n"
                  + "from inventory.vendingmachine vm\n"
                  + "LEFT JOIN inventory.protocol prt ON(prt.id = vm.protocol_id AND prt.deleted = FALSE)\n"
                  + "LEFT JOIN general.brand brd ON(brd.id = vm.brand_id AND brd.deleted = FALSE)\n"
                  + "LEFT JOIN system.type_dict typd ON(typd.type_id = vm.type_id AND typd.language_id = ?)\n"
                  + "where vm.deleted=false and vm.branch_id=?\n"
                  + where;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<AutomationDevice> result = getJdbcTemplate().query(sql, param, new AutomationDeviceMapper());
        return result;
    }

    @Override
    public int create(AutomationDevice obj) {
        String sql = "SELECT r_vendingmachine_id FROM inventory.process_vendingmachine (?, ?, ? , ? , ?, ? , ? , ? , ? , ? , ? , ?);";

        Object[] param = new Object[]{0, obj.getId(), sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getIpadress(), obj.getPort(), obj.getMacAddress(), obj.getDescription(),
            (obj.getProtocol().getId() == 0 ? null : obj.getProtocol().getId()), obj.getBrand().getId(), obj.getDeviceType().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AutomationDevice obj) {

        String sql = "SELECT r_vendingmachine_id FROM inventory.process_vendingmachine (?, ?, ? , ? , ?, ? , ? , ? , ? , ? , ? , ?);";

        Object[] param = new Object[]{1, obj.getId(), sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getIpadress(), obj.getPort(), obj.getMacAddress(), obj.getDescription(),
            (obj.getProtocol().getId() == 0 ? null : obj.getProtocol().getId()), obj.getBrand().getId(), obj.getDeviceType().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(AutomationDevice automationDevice) {
        String sql = "UPDATE inventory.vendingmachine SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                  + "UPDATE inventory.vendingmachineitem SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND vendingmachine_id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), automationDevice.getId(), sessionBean.getUser().getId(), automationDevice.getId()};

        System.out.println("---arry" + Arrays.toString(param));
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String configureDetail(AutomationDevice automationDevice) {
        String sql = "";
        if (automationDevice.getDeviceType().getId() == 111) {
            sql = "SELECT * FROM inventory.configure_vendingmachinecoffee(?) ";
        } else {
            sql = "SELECT * FROM inventory.configure_vendingmachine(?) ";
        }

        Object[] param = new Object[]{automationDevice.getId()};
        List<String> result = getJdbcTemplate().query(sql, param, new AutomationConfigureMapper());

        if (result.size() > 0) {
            return result.get(0);
        } else {
            return "";
        }

    }

    @Override
    public int controlAutomationDevice() {

        String sql = "SELECT CASE WHEN EXISTS (SELECT vm.id FROM inventory.vendingmachine vm WHERE vm.branch_id=? AND vm.deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
