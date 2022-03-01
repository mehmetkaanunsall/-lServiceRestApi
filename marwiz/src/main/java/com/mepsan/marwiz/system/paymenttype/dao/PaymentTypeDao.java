/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.paymenttype.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.PaymentType;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author m.duzoylum
 */
public class PaymentTypeDao extends JdbcDaoSupport implements IPaymentTypeDao {

    @Autowired
    SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PaymentType> listofPayment(int branchId) {
        String sql = "SELECT "
                + "id,"
                + "name,"
                + "integrationcode "
                + "FROM integration.sap_paymenttype "
                + "WHERE deleted=false and branch_id=?";
        Object[] param = new Object[]{branchId};
        List<PaymentType> result = getJdbcTemplate().query(sql, param, new PaymentTypeMapper());
        return result;
    }

    @Override
    public int create(PaymentType obj) {
        String sql = "INSERT INTO integration.sap_paymenttype\n"
                + "(branch_id,name,integrationcode,c_id,u_id) \n"
                + "VALUES(?, ?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{
            sessionBean.getUser().getLastBranch().getId(),
            obj.getEntegrationname(),
            obj.getEntegrationcode(),
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
    public int update(PaymentType obj) {

        String sql = "UPDATE integration.sap_paymenttype "
                + "SET "
                + "name = ?, "
                + "integrationcode = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{
            obj.getEntegrationname(),
            obj.getEntegrationcode(),
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
    public int delete(PaymentType obj) {
        String sql = "UPDATE integration.sap_paymenttype set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
