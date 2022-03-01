/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 03:53:26
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.pattern.ICommunicationDao;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ResponsibleInternetDao extends JdbcDaoSupport implements ICommunicationDao<Internet<Responsible>, Responsible> {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Internet<Responsible> obj) {
        String sql = "INSERT INTO \n"
                  + "general.internet(responsible_id,type_id,tag,is_default,c_id,u_id) "
                  + "VALUES ( ?, ?, ?, ?, ?, ? ) RETURNING id";

        Object[] param = new Object[]{obj.getObject().getId(),
            obj.getInternetType().getId(), obj.getTag(), obj.isDefaultValue(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Internet<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.internet  \n"
                  + "SET \n"
                  + "type_id = ? ,\n"
                  + "tag = ? ,\n"
                  + "is_default = ? ,\n"
                  + "u_id = ? ,\n"
                  + "u_time=NOW()\n"
                  + "WHERE \n"
                  + "id = ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getInternetType().getId(), obj.getTag(),
            obj.isDefaultValue(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Internet<Responsible> obj) {
        String sql = "UPDATE \n"
                  + "general.internet  \n"
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
