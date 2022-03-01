/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class StockEInvoiceUnitConDao extends JdbcDaoSupport implements IStockEInvoiceUnitConDao {

    @Autowired
    private SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockEInvoiceUnitCon> findAll(StockEInvoiceUnitCon obj) {
        String sql = "SELECT \n"
                + "     seiu.id AS seiuid ,\n"
                + "     seiu.stock_id AS seiustockid ,\n"
                + "     seiu.stockintegrationcode AS seiustockintegrationcode ,\n"
                + "     seiu.quantity AS seiuquantity "
                + "  FROM inventory.stock_einvoice_unit_con seiu\n"
                + "  INNER JOIN inventory.stock stck ON(stck.id = seiu.stock_id AND stck.deleted = FALSE)\n"
                + "  LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE)\n"
                + " WHERE seiu.deleted = FALSE AND seiu.stock_id = ? AND seiu.branch_id = ? AND seiu.stockintegrationcode = ? ";

        Object[] param = new Object[]{obj.getStockId(), sessionBean.getUser().getLastBranch().getId(), obj.getStockIntegrationCode()};
        return getJdbcTemplate().query(sql, param, new StockEInvoiceUnitConMapper());

    }

    @Override
    public int create(StockEInvoiceUnitCon obj) {

        String sql = "INSERT INTO inventory.stock_einvoice_unit_con (branch_id,stock_id,stockintegrationcode,quantity,c_id,u_id) VALUES (?,?,?,?,?,?) RETURNING id ;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getStockId(), obj.getStockIntegrationCode(), obj.getQuantity(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(StockEInvoiceUnitCon obj) {
        String sql = "UPDATE inventory.stock_einvoice_unit_con SET stock_id= ?, stockintegrationcode = ?, quantity = ? , branch_id=?, u_id = ?, u_time = now() WHERE id= ?";
        Object[] param = new Object[]{obj.getStockId(), obj.getStockIntegrationCode(), obj.getQuantity(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(StockEInvoiceUnitCon obj) {
        String sql = "UPDATE inventory.stock_einvoice_unit_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
