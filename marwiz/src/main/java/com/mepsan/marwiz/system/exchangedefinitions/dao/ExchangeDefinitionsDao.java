/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author sinem.arslan
 */
public class ExchangeDefinitionsDao extends JdbcDaoSupport implements IExchangeDefinitionsDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Currency> findAll() {

        String sql = "SELECT \n"
                + "cry.id as cryid,\n"
                + "cry.code as crycode,\n"
                + "cry.internationalcode as cryinternationalcode,\n"
                + "cry.sign as crysign,\n"
                + "cry.conversionrate as cryconversionrate,\n"
                + "cry.limitup as crylimitup\n"
                + "FROM system.currency cry";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new ExchangeDefinitionsMapper());
    }

    @Override
    public int create(Currency obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Currency obj) {
        String sql = "UPDATE system.currency SET conversionrate= ?, limitup = ?, u_id = ?, u_time = now() WHERE id= ?";
        Object[] param = new Object[]{obj.getConversionRate(), obj.getLimitUp(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

   
}
