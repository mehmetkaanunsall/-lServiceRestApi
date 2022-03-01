/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:59:23 PM
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomationDeviceCardDao extends JdbcDaoSupport implements IAutomationDeviceCardDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomationDeviceCard> listOfCard(AutomationDevice obj) {
        String sql = "SELECT \n"
                  + " ivm.id as ivmid,\n"
                  + " ivm.rfno as ivmrfno,\n"
                  + " ivm.name as ivmname,\n"
                  + " ivm.status_id as ivmstatus_id,\n"
                  + " ivm.vendingmachine_id as ivmvendingmachine_id,\n"
                  + " sttd.name as sttdname,\n"
                  + "ivm.type_id as ivmtype_id,\n"
                  + " typd.name as typdname\n"
                  + "FROM inventory.vendingmachinecard ivm\n"
                  + "INNER JOIN system.status_dict sttd ON (ivm.status_id=sttd.status_id AND sttd.language_id=?)\n"
                  + "LEFT JOIN system.type typd ON(typd.id=ivm.type_id)\n"
                  + "WHERE ivm.deleted=FALSE AND ivm.vendingmachine_id=?";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), obj.getId()};
        List<AutomationDeviceCard> result = getJdbcTemplate().query(sql, param, new AutomationDeviceCardMapper());
        return result;
    }

    @Override
    public int delete(AutomationDeviceCard automationDeviceCard) {
        String sql = "UPDATE \n"
                  + "         inventory.vendingmachinecard\n"
                  + "       SET \n"
                  + "         deleted = TRUE,\n"
                  + "         u_id = ?,\n"
                  + "         d_time = NOW()\n"
                  + "       WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getId(), automationDeviceCard.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int create(AutomationDeviceCard obj) {
        String sql = "   INSERT INTO \n"
                  + "  inventory.vendingmachinecard\n"
                  + "(\n"
                  + "  vendingmachine_id,\n"
                  + "  type_id,\n"
                  + "  name,\n"
                  + "  status_id,\n"
                  + "  rfno,\n"
                  + "  c_id,\n"
                  + "  u_id\n"
                  + ")\n"
                  + "VALUES (?,?,?,?,?,?,?)RETURNING id;";

        Object[] param = new Object[]{obj.getAutomationDevice().getId(), (obj.getType().getId() == 0 ? null : obj.getType().getId()), obj.getName(), obj.getStatus().getId(),
            obj.getRfNo(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AutomationDeviceCard obj) {
        String sql = "UPDATE \n"
                  + "  inventory.vendingmachinecard \n"
                  + "SET \n"
                  + "  vendingmachine_id = ?,\n"
                  + "  name = ?,\n"
                  + "  status_id = ?,\n"
                  + "  rfno = ?,\n"
                  + "  type_id = ?,\n"
                  + "  u_id = ?,\n"
                  + "  u_time = now()\n"
                  + "WHERE \n"
                  + "  id = ?;";
        Object[] param = new Object[]{obj.getAutomationDevice().getId(), obj.getName(),
            obj.getStatus().getId(),
            obj.getRfNo(), (obj.getType().getId() == 0 ? null : obj.getType().getId()),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
