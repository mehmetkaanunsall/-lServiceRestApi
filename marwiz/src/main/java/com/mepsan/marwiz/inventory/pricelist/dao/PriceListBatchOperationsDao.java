/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 14.09.2018 08:44:30
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PriceListBatchOperationsDao extends JdbcDaoSupport implements IPriceListBatchOperationsDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int updateStocks(int processType,int priceListId,boolean isRate, BigDecimal price,String where) {
        String sql = "SELECT * FROM inventory.process_pricelistitemchange(?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{processType,priceListId,isRate,price, where,sessionBean.getUser().getId()};
       // Arrays.toString(param);

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {//Varsa default değeri -1 döndürmek için 
                return -1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        }
    }

}
