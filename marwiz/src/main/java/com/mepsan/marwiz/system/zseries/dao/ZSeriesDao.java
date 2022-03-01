/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.zseries.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ZSeries;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author m.duzoylum
 */
public class ZSeriesDao extends JdbcDaoSupport implements IZSeriesDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ZSeries> listofZseries(int branchId) {
        String sql = "SELECT "
                + "id,"
                + "type_id,"
                + "number "
                + "FROM integration.sap_zseries "
                + "WHERE deleted=false and branch_id=?";
        Object[] param = new Object[]{branchId};
        List<ZSeries> result = getJdbcTemplate().query(sql, param, new ZSeriesMapper());
        return result;
    }

    @Override
    public int create(ZSeries obj) {
        String sql = "INSERT INTO integration.sap_zseries\n"
                + "(branch_id,type_id,number,c_id,u_id) \n"
                + "VALUES(?, ?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{
            sessionBean.getUser().getLastBranch().getId(),
            obj.getType(),
            obj.getNumber(),
            sessionBean.getUser().getId(),
            sessionBean.getUser().getId()
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(ZSeries obj) {
        String sql = "UPDATE integration.sap_zseries "
                + "SET "
                + "type_id = ?, "
                + "number = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{
            obj.getType(),
            obj.getNumber(),
            sessionBean.getUser().getId(),
            obj.getId()
        };
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(ZSeries obj) {
        String sql = "UPDATE integration.sap_zseries set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
