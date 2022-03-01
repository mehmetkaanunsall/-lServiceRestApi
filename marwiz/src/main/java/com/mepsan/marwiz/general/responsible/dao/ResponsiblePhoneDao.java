/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 03:54:36
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.Phone;
import com.mepsan.marwiz.general.pattern.ICommunicationDao;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ResponsiblePhoneDao extends JdbcDaoSupport implements ICommunicationDao<Phone<Responsible>, Responsible> {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Phone<Responsible> obj) {
        String sql = "INSERT INTO \n"
                  + "general.phone \n"
                  + "( \n"
                  + "responsible_id,\n"
                  + "type_id,\n"
                  + "tag,\n"
                  + "country_id,\n"
                  + "is_default,\n"
                  + "c_id,\n"
                  + "u_id \n"
                  + ")\n"
                  + "VALUES ( ?, ?, ?, ?, ?, ?, ? )  RETURNING id;";

        Object[] param = new Object[]{obj.getObject().getId(), obj.getPhoneType().getId(), obj.getTag(),
            obj.getCountry().getId(), obj.isDefaultValue(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;

    }

    @Override
    public int update(Phone<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.phone  \n"
                  + "SET \n"
                  + "type_id =  ? ,\n"
                  + "tag =  ? ,\n"
                  + "country_id =  ? ,\n"
                  + "is_default =  ? ,\n"
                  + "u_id =  ? ,\n"
                  + "u_time = NOW()\n"
                  + "WHERE \n"
                  + "id =  ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getPhoneType().getId(), obj.getTag(), obj.getCountry().getId(),
            obj.isDefaultValue(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int delete(Phone<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.phone  \n"
                  + "SET \n"
                  + "deleted = True ,\n"
                  + "u_id = ? ,\n"
                  + "d_time=NOW()\n"
                  + "WHERE \n"
                  + "id =  ? AND deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

}
