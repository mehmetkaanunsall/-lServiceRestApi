/**
 * @author Mehmet ERGÜLCÜ
 * @date 01.03.2017 05:03:56
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.admin.DbObject;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DbObjectDao extends JdbcDaoSupport implements IDbObjectDao {

    @Override
    public List<DbObject> findAll() {
        String sql = "SELECT id "
                + ",name "
                + ",description "
                + ",type "
                + ",tag "
                + " FROM admin.object  with (NOLOCK) ";
        List<DbObject> dbObjects = getJdbcTemplate().query(sql, new DbObjectMapper());
        return dbObjects;
    }

    /**
     *
     * @param tag nesnenin kısaltması
     * @return tag gönderilen taga eşit olan nesne geri döner
     */
    @Override
    public DbObject findByTag(String tag) {
        String sql = "SELECT id "
                + ",name "
                + ",description "
                + ",type "
                + ",tag "
                + " FROM admin.object  with (NOLOCK) "
                + " where tag=?";
        Object[] param = new Object[]{tag};
        DbObject dbObject = getJdbcTemplate().queryForObject(sql, param, new DbObjectMapper());
        return dbObject;
    }

}
