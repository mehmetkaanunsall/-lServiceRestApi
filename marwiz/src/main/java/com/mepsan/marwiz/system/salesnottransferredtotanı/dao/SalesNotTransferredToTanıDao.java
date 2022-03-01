/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author sinem.arslan
 */
public class SalesNotTransferredToTanıDao extends JdbcDaoSupport implements ISalesNotTransferredToTanıDao {

    @Autowired
    private SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SalesNotTransferredToTanı> listOfSalesCount() {
        String sql = "   SELECT \n"
                + "    		SUM(CASE WHEN sl.transactionno IS NULL THEN 1  ELSE 0  END) AS unsentsales,\n"
                + "           	SUM(CASE WHEN sl.transactionno IS NOT NULL THEN 1  ELSE 0  END) AS sentsales\n"
                + "            \n"
                + "        FROM general.sale sl where sl.deleted = FALSE ";

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new SalesNotTransferredToTanıMapper());

    }

    public int transferSales() {
        String sql = "SELECT * FROM log.process_notsendsaleto_paro()";

        Object[] param = new Object[]{};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
