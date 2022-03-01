/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.starbucksmachine.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StarbucksMachine;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ebubekir.buker
 */
public class StarbucksMachineDao extends JdbcDaoSupport implements IStarbucksMachineDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StarbucksMachine> listOfStarbucksMachine() {
        String sql = "SELECT \n"
                  + " id as id,\n"
                  + " name as name,\n"
                  + " code as code,\n"
                  + " machinebarcode as machinebarcode,\n"
                  + " pubkey as pubkey\n"
                  + "from inventory.starbucksmachine\n"
                  + "where deleted=FALSE AND branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<StarbucksMachine> result = getJdbcTemplate().query(sql, param, new StarbucksMachineMapper());
        return result;
    }

    @Override
    public int delete(StarbucksMachine obj) {
        String sql = "UPDATE inventory.starbucksmachine SET deleted = true, u_id=?, d_time=now() WHERE deleted=false AND id=?";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int create(StarbucksMachine obj) {

        String sql = "INSERT INTO inventory.starbucksmachine\n"
                  + "(branch_id,name,code,machinebarcode,pubkey,c_id,u_id)\n"
                  + "VALUES\n"
                  + "(?,?,?,?,?,?,?)\n"
                  + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getCode(), obj.getMachineBarcode(), obj.getPubKey(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(StarbucksMachine obj) {
        String sql = "UPDATE inventory.starbucksmachine\n"
                  + "SET\n"
                  + "name=?,\n"
                  + "code=?,\n"
                  + "machinebarcode=?,\n"
                  + "pubkey = ?,\n"
                  + "u_id=?,\n"
                  + "u_time =now()\n"
                  + "where id=? AND deleted=FALSE";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), obj.getMachineBarcode(), obj.getPubKey(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
