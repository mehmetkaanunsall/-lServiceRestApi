/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 10:54:35
 */
package com.mepsan.marwiz.system.userdata.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserDataAuthorizeConnectionDao extends JdbcDaoSupport implements IUserDataAuthorizeConnectionDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<UserDataAuthorizeConnection> findAllUserAuthorize(String where) {
        String sql = "SELECT\n"
                + " autc.id AS autcid,\n"
                + " b.id AS bid,\n"
                + " b.name AS bname,\n"
                + " autc.authorize_id AS autcauthorize_id,\n"
                + " auth.name AS authname,\n"
                + " autc.userdata_id AS autcuserdata_id,\n"
                + " us.name AS usname,\n"
                + " us.surname AS ussurname\n"
                + "FROM\n"
                + "general.userdata_authorize_con autc \n"
                + "LEFT JOIN general.userdata us ON (us.id=autc.userdata_id and us.deleted=false)\n"
                + "LEFT JOIN general.authorize auth ON (auth.id=autc.authorize_id and auth.deleted=false)\n"
                + "LEFT JOIN general.branch b ON (b.id=auth.branch_id and b.deleted=false)\n"
                + "WHERE\n"
                + "autc.deleted=FALSE "+where;

        List<UserDataAuthorizeConnection> result = getJdbcTemplate().query(sql, new UserDataAuthorizeConnectionMapper());
        return result;
    }

    @Override
    public int create(UserDataAuthorizeConnection obj) {
        String sql = "INSERT INTO general.userdata_authorize_con\n"
                + "(userdata_id,authorize_id,c_id,u_id)\n"
                + "VALUES\n"
                + "(?,?,?,?) RETURNING id ;";
        Object param[] = new Object[]{obj.getUserData().getId(), obj.getAuthorize().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(UserDataAuthorizeConnection obj) {
        String sql = "UPDATE general.userdata_authorize_con\n"
                + "SET\n"
                + "userdata_id = ?,\n"
                + "authorize_id = ?,\n"
                + "u_id = ?,\n"
                + "u_time = now()\n"
                + "WHERE id=?";

        Object param[] = new Object[]{obj.getUserData().getId(),obj.getAuthorize().getId(), sessionBean.getUser().getId(),obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(UserDataAuthorizeConnection obj) {
        String sql = "UPDATE general.userdata_authorize_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
