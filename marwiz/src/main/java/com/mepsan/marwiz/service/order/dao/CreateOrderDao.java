/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.dao;

import java.sql.SQLException;
import java.util.Arrays;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class CreateOrderDao extends JdbcDaoSupport implements ICreateOrderDao{

    @Override
    public int createOrderJob(int branch_id) {
        String sql = "SELECT COALESCE(r_sequencee,0) FROM finance.create_job_order(?, ?);";

        Object[] param = new Object[]{branch_id,1};
        System.out.println("--param---" + Arrays.toString(param));

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }    
    }
    
}
