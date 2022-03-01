/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 03:44:53
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.pattern.ICommunicationDao;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ResponsibleAddressDao extends JdbcDaoSupport implements ICommunicationDao<Address<Responsible>, Responsible> {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Address<Responsible> obj) {
        String sql = "INSERT INTO \n"
                  + "general.address\n"
                  + "(\n"
                  + "responsible_id,\n"
                  + "country_id,\n"
                  + "city_id,\n"
                  + "county_id,\n"
                  + "fulladdress,\n"
                  + "is_default,\n"
                  + "c_id,\n"
                  + "u_id\n"
                  + ")\n"
                  + "VALUES ( ? , ? , ? , ? , ? , ? , ?, ? ) RETURNING id;";
        Object[] param = new Object[]{obj.getObject().getId(), obj.getCountry().getId(), obj.getCity().getId(),
            obj.getCounty().getId(), obj.getFulladdress(), obj.isDefaultValue(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Address<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.address  \n"
                  + "SET \n"
                  + "country_id = ? ,\n"
                  + "city_id = ? ,\n"
                  + "county_id = ? ,\n"
                  + "fulladdress = ? ,\n"
                  + "is_default = ? ,\n"
                  + "u_id = ? ,\n"
                  + "u_time = NOW() \n"
                  + "WHERE \n"
                  + "id = ? AND deleted=FALSE";
        String adress = obj.getFulladdress();
        Object[] param = new Object[]{obj.getCountry().getId(), obj.getCity().getId(),
            obj.getCounty().getId(), adress, obj.isDefaultValue(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Address<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.address  \n"
                  + "SET \n"
                  + "deleted = True ,\n"
                  + "u_id = ? ,\n"
                  + "d_time=NOW()\n"
                  + "WHERE \n"
                  + "id = ? AND deleted=FALSE";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
