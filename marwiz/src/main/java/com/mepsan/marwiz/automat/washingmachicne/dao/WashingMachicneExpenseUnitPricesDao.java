/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:15:39 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicneExpenseUnitPricesDao extends JdbcDaoSupport implements IWashingMachicneExpenseUnitPricesDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(ExpenseUnitPrice obj) {
        String sql = "        \n"
                + "INSERT INTO \n"
                + "  wms.expenseunitprice\n"
                + "(\n"
                + "  washingmachine_id,\n"
                + "  stock_id,\n"
                + "  unitprice,\n"
                + "  currency_id,\n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES (?,?,?,?,?,?)"
                + "RETURNING id ;";

        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.getStock().getId(), obj.getUnitPrice(), obj.getCurrency().getId(), sessionBean.getUser().getId(),
            sessionBean.getUser().getId()};


        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(ExpenseUnitPrice obj) {
        String sql = "UPDATE \n"
                + "  wms.expenseunitprice  \n"
                + "SET \n"
                + "  washingmachine_id = ?,\n"
                + "  stock_id = ?,\n"
                + "  unitprice = ?,\n"
                + "  currency_id = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?;";

        Object[] param = new Object[]{obj.getWashingMachicne().getId(), obj.getStock().getId(), obj.getUnitPrice(), obj.getCurrency().getId(), sessionBean.getUser().getId(),
            obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public List<ExpenseUnitPrice> findAll(WashingMachicne obj) {
        String sql = "SELECT\n"
                + "          eup.id as eupid,\n"
                + "          eup.washingmachine_id as eupwashingmachine_id,\n"
                + "          eup.stock_id as eupstock_id,\n"
                + "          eup.unitprice as eupunitprice,\n"
                + "          eup.currency_id as eupcurrency_id,\n"
                + "          stck.name as stckname,\n"
                + "          stck.unit_id as stckunit_id,\n"
                + "          unt.sortname as untsortname\n"
                + "          FROM wms.expenseunitprice eup \n"
                + "          INNER JOIN inventory.stock stck  ON(eup.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "          INNER JOIN wms.washingmachine wm ON(wm.id=eup.washingmachine_id AND wm.deleted=FALSE)\n"
                + "          INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "          WHERE eup.deleted=FALSE AND eup.washingmachine_id=?";

        Object[] param = new Object[]{obj.getId()};
        return getJdbcTemplate().query(sql, param, new WashingMachicneExpenseUnitPricesMapper());
    }

    @Override
    public int delete(ExpenseUnitPrice obj) {
        String sql = "UPDATE  wms.expenseunitprice SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
