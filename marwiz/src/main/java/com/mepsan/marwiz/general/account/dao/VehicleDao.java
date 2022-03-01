/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:34:48
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class VehicleDao extends JdbcDaoSupport implements IVehicleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Vehicle> findVehicle(Account account) {
        String sql = "SELECT \n"
                  + "	vhc.id AS vhcid,\n"
                  + "	vhc.plate AS vhcplate\n"
                  + "FROM\n"
                  + "general.vehicle vhc \n"
                  + "WHERE vhc.deleted=FALSE AND vhc.account_id =?";

        Object[] param = new Object[]{account.getId()};

        List<Vehicle> result = getJdbcTemplate().query(sql, param, new VehicleMapper());
        return result;
    }

    @Override
    public int create(Vehicle obj) {
        String sql = "INSERT INTO general.vehicle (account_id, plate, c_id, u_id) VALUES (?, ?, ?, ?) RETURNING id;";

        Object[] param = new Object[]{obj.getAccount().getId(), obj.getPlate(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(Vehicle obj) {
        String sql = "UPDATE \n"
                  + "	general.vehicle \n"
                  + "SET \n"
                  + "	 plate = ?,\n"
                  + "    u_id = ?,\n"
                  + "    u_time = NOW()\n"
                  + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getPlate(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Vehicle obj) {
        String sql = "UPDATE \n"
                  + "	general.vehicle \n"
                  + "SET \n"
                  + "	deleted = TRUE,\n"
                  + "   u_id = ?,\n"
                  + "   d_time = NOW()\n"
                  + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
