/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Gozde Gursel
 */
public class UserDataNoteDao extends JdbcDaoSupport implements IUserDataNoteDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<UserDataNote> find() {
        String sql = "SELECT \n"
                + "	nt.id as ntid,\n"
                + "	nt.userdata_id AS ntuserdata_id,\n"
                + "	nt.description AS ntdescription,\n"
                + "     nt.c_time AS ntc_time, \n"
                + "     nt.u_time As ntu_time \n"
                + "FROM general.userdata_note nt \n"
                + "WHERE nt.deleted=FALSE AND nt.userdata_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        return getJdbcTemplate().query(sql, param, new UserDataNoteMapper());
    }

    @Override
    public int create(UserDataNote obj) {
        String sql = "INSERT INTO \n"
                + "  general.userdata_note\n"
                + "(\n"
                + "  userdata_id,\n"
                + "  description,\n"
                + "  c_id,\n"
                + "  c_time,\n"
                + "  u_id,\n"
                + "  u_time\n"
                + ")\n"
                + "VALUES (\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  now(),\n"
                + "  ?,\n"
                + "  now()\n"
                + ")";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getDescription(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(UserDataNote obj) {
        String sql = "UPDATE \n"
                + "          general.userdata_note \n"
                + "        SET \n"
                + "          description=? ,\n"
                + "          u_id = ? ,\n"
                + "          u_time = NOW() \n"
                + "        WHERE \n"
                + "          id = ?";

        Object[] param = new Object[]{obj.getDescription(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int deleteNote(int noteId) {
        String sql = "UPDATE \n"
                + "          general.userdata_note \n"
                + "        SET \n"
                + "          deleted = TRUE ,\n"
                + "          d_time = NOW() , \n"
                + "          u_id = ? ,\n"
                + "          u_time = NOW() \n"
                + "        WHERE \n"
                + "          id = ?";


        Object[] param = new Object[]{sessionBean.getUser().getId(), noteId};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
