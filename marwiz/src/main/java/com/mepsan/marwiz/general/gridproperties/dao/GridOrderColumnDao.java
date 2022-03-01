/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   04.01.2019 14:09:47
 */
package com.mepsan.marwiz.general.gridproperties.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GridOrderColumnDao extends JdbcDaoSupport implements IGridOrderColumnDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int reorder(int pageId, String gridId, String reorder) {
        String sql = "UPDATE general.userdata_gridproperties SET columnsetting= ? WHERE userdata_id = ? and page_id = ? and grid_id = ? and deleted=false;\n"
                + "INSERT INTO general.userdata_gridproperties (userdata_id, page_id, grid_id, columnsetting, c_id, u_id)\n"
                + "       SELECT ? , ? , ? , ? , ? , ? \n"
                + "       WHERE NOT EXISTS (SELECT 1 FROM general.userdata_gridproperties WHERE userdata_id = ? and page_id = ? and grid_id = ? and deleted=false);";
        Object[] param = new Object[]{reorder,sessionBean.getUser().getId(), pageId, gridId, sessionBean.getUser().getId(), pageId, gridId, reorder, sessionBean.getUser().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), pageId, gridId};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String bringOrder(int pageId, String gridId) {
        String sql = "SELECT  columnsetting FROM  general.userdata_gridproperties  where page_id= ? and grid_id = ? and userdata_id = ? and deleted=false ;";

        Object[] param = new Object[]{pageId, gridId, sessionBean.getUser().getId()};

        System.out.println("---- " + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (Exception e) {
        }
        return null;

    }

    @Override
    public int update(int pageId, String gridId, String reorder) {
        String sql = "UPDATE \n"
                + "  general.userdata_gridproperties \n"
                + "SET \n"
                + "  columnsetting = ?  \n"
                + "WHERE \n"
                + "  userdata_id = ? and page_id = ? and grid_id = ? ";

        Object[] param = new Object[]{reorder, sessionBean.getUser().getId(), pageId, gridId};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
