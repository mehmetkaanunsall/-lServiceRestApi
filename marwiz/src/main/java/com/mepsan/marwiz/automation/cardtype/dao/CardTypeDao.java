/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.cardtype.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelCardType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author asli.can
 */
public class CardTypeDao extends JdbcDaoSupport implements ICardTypeDao {

    @Autowired
    SessionBean sessionBean;

    @Override
    public int create(FuelCardType obj) {
        String sql = "INSERT INTO\n"
                + "                automation.fuelcardtype(branch_id,name,typeno,account_id, bankaccount_id , fuelsaletype_id, c_id,u_id) \n"
                + "                 VALUES(?,?,?,?,?,?,?,?) RETURNING id ";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getTypeNo(), obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(),
            obj.getBankacount().getId() == 0 ? null : obj.getBankacount().getId() , obj.getSaleType().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        System.out.println("-----Arrays param----" + Arrays.toString(param));

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(FuelCardType obj) {
        String sql = "UPDATE automation.fuelcardtype \n"
                + "SET\n"
                + "name =?,\n"
                + "typeno = ?,\n"
                + "fuelsaletype_id =?,\n"
                + "account_id = ?,\n"
                + "bankaccount_id = ?,\n"
                + "u_id = ?,\n"
                + "u_time = now()\n"
                + "WHERE id = ? AND deleted= FALSE";

        Object[] param = new Object[]{obj.getName(), obj.getTypeNo(), obj.getSaleType().getId(),
            obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(), obj.getBankacount().getId() == 0 ? null : obj.getBankacount().getId(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<FuelCardType> findAll() {
        String sql = "SELECT \n"
                + "fct.id AS fctid,\n"
                + "fct.name AS fctname,\n"
                + "fct.typeno AS fcttypeno,\n"
                + "fst.name as fstname,\n"
                + "fst.typeno AS fsttypeno,\n"
                + "fct.account_id as fctaccount_id,\n"
                + "fct.bankaccount_id as fctbankaccoun_id, \n"
                + "fct.fuelsaletype_id as fctfuelsaletype_id,\n"
                + "acc.name as accname\n"
                + "FROM automation.fuelcardtype fct\n"
                + "LEFT JOIN automation.fuelsaletype fst ON (fst.id = fct.fuelsaletype_id AND fst.deleted = FALSE)\n"
                + "LEFT JOIN general.account acc ON (acc.id = fct.account_id AND acc.deleted = FALSE)\n"
                + "LEFT JOIN finance.bankaccount fb ON (fb.id = fct.bankaccount_id AND fb.deleted = FALSE)\n"
                + "WHERE fct.deleted = FALSE AND fct.branch_id = ?";

        Object param[] = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<FuelCardType> result = getJdbcTemplate().query(sql, param, new CardTypeMapper());
        return result;
    }

    @Override
    public int delete(FuelCardType obj) {
        String sql = "UPDATE automation.fuelcardtype \n"
                + "SET \n"
                + " deleted = true, \n"
                + " u_id = ?, \n"
                + " d_time = now() \n"
                + " WHERE id= ? AND deleted= FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
