/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.scheduledtaskprocesses.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ScheduledTaskProcesses;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class ScheduledTaskProcessesDao extends JdbcDaoSupport implements IScheduledTaskProcessesDao {

    @Autowired
    public SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ScheduledTaskProcesses> findAll() {

        String sql = "SELECT \n"
                + "sch.id AS schid,\n"
                + "sch.branch_id AS schbranch_id,\n"
                + "sch.type_id AS schtype_id,\n"
                + "sch.name AS schname,\n"
                + "sch.status_id AS schstatus_id,\n"
                + "sch.description AS schdescription,\n"
                + "sch.days AS schdays,\n"
                + "sch.daysdate AS schdaysdate,\n"
                + "sttd.name AS sttdname,\n"
                + "typd.name AS typdname\n"
                + "FROM general.scheduledjob sch\n"
                + "LEFT JOIN system.status_dict sttd ON (sttd.status_id = sch.status_id AND sttd.language_id = ?) \n"
                + "LEFT JOIN system.type_dict typd ON (typd.type_id = sch.type_id AND typd.language_id = ?)\n"
                + "WHERE sch.deleted=FALSE and sch.branch_id = ?";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new ScheduledTaskProcessesMapper());

    }

    @Override
    public int create(ScheduledTaskProcesses obj) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String sql = "INSERT INTO general.scheduledjob (branch_id,type_id,name,status_id,description,days,daysdate,c_id,u_id) VALUES (?,?,?,?,?,?,?,?,?) RETURNING id ;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getType().getId(), obj.getName(), obj.getStatus().getId(), obj.getDescription(), obj.getDays(), sdf.format(obj.getWorkingTime()), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(ScheduledTaskProcesses obj) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String sql = "UPDATE general.scheduledjob SET branch_id= ?, type_id = ?, status_id = ? , description=?, days=?, daysdate=?, u_id = ?, u_time = now() WHERE id= ? ";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getType().getId(), obj.getStatus().getId(), obj.getDescription(), obj.getDays(), sdf.format(obj.getWorkingTime()), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int delete(ScheduledTaskProcesses obj) {
        String sql = "UPDATE general.scheduledjob set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
