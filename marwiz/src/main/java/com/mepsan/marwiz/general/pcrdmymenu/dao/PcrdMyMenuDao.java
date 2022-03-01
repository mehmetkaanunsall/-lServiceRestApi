/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.10.2016 11:06:48
 */
package com.mepsan.marwiz.general.pcrdmymenu.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PcrdMyMenuDao extends JdbcDaoSupport implements IPcrdMyMenuDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<UserDataMenuConnection> findMyModules() {
        String sql = "select umc.id,umc.icon,umc.color,umc.order,umc.page_id ,p.url,pd.name\n"
                + "               from general.userdata_menu_con umc  \n"
                + "               left join system.page p on (p.id=umc.page_id)\n"
                + "               left join system.page_dict pd on (pd.page_id=p.id and pd.language_id=?)\n"
                + "               where umc.userdata_id=? and umc.deleted=false order by " + '"' + "order" + '"';
        Object[] param = new Object[]{sessionBean.getLangId(), sessionBean.getUser().getId()};
        List<UserDataMenuConnection> result = getJdbcTemplate().query(sql, param, new UserDataConnectionMapper());

        return result;

    }

    @Override
    public int create(UserDataMenuConnection obj) {
        String sql = "INSERT INTO general.userdata_menu_con (userdata_id,page_id,color,icon," + '"' + "order" + '"' + ",c_id) VALUES (?,?,?,?,?,?) returning id";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getPage().getId(),
            obj.getColor(), obj.getIcon(), obj.getOrder(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(UserDataMenuConnection obj) {
        String sql = "update general.userdata_menu_con set color=?,icon=?,u_id=?,u_time=current_timestamp where page_id = ?   and userdata_id = ? and deleted=false ;";
        Object[] param = new Object[]{obj.getColor(),obj.getIcon(),sessionBean.getUser().getId(),obj.getPage().getId(), sessionBean.getUser().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(UserDataMenuConnection obj) {
        String sql = "update general.userdata_menu_con set deleted=true,d_time=current_timestamp where page_id = ?   and userdata_id = ? and deleted=false ;";
        Object[] param = new Object[]{obj.getPage().getId(), sessionBean.getUser().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int reOrder(List<UserDataMenuConnection> list) {
        String sql = "";
        int id = 0;
        for (UserDataMenuConnection udmc : list) {
            sql = sql + "update general.userdata_menu_con set \"order\" =" + id + "  where  page_id=" + udmc.getPage().getId() + " and userdata_id=" + sessionBean.getUser().getId() + " and deleted=false;\n";
            id = id + 1;
        }

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
