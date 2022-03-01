/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:08:07
 */
package com.mepsan.marwiz.general.profile.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.HotKey;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class HotKeyDao extends JdbcDaoSupport implements IHotKeyDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<HotKey> listHotKeys() {
        String sql = "SELECT "
                + "uhk.id AS uhkid, "
                + "uhk.page_id AS uhkpage_id, "
                + "uhk.hotkey AS uhkhotkey, \n"
                + "pge.url as pgeurl,"
                + "pged.name AS pgedname \n"
                + "   FROM general.userdata_hotkey uhk  with (NOLOCK) \n"
                + " INNER JOIN admin.page pge  with (NOLOCK) ON (pge.id=uhk.page_id)\n"
                + " INNER JOIN admin.page_dict pged  with (NOLOCK) ON (pged.page_id=uhk.page_id AND pged.language_id = ?)\n"
                + "WHERE uhk.userdata_id = ? AND uhk.deleted = 0 ";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getId()};
        return getJdbcTemplate().query(sql, param, new HotKeyMapper());
    }

    @Override
    public int create(HotKey obj) {
        String sql = "INSERT INTO general.userdata_hotkey (userdata_id,hotkey,page_id,c_id,u_id) OUTPUT inserted.id VALUES (?,?,?,?,?)";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getHotkey(),
            obj.getPage().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(HotKey obj) {
        String sql = "UPDATE general.userdata_hotkey SET hotkey= ?,page_id=?, u_id = ?, u_time = GETDATE() WHERE id = ? ";
        Object[] param = new Object[]{obj.getHotkey(), obj.getPage().getId(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(HotKey hotKey) {
        String sql = "update general.userdata_hotkey set deleted=1 ,u_id=? , d_time=GETDATE()  where id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), hotKey.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
