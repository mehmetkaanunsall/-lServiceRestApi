/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUnitConnection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class StockAlternativeUnitDao extends JdbcDaoSupport implements IStockAlternativeUnitDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockUnitConnection> findAll(Stock stock) {
        String where = " ";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND isuc.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + "  isuc.id AS isucid,\n"
                + "  isuc.stock_id AS isucstock_id,\n"
                + "  isuc.unit_id AS isucunit_id,\n"
                + "   isuc.is_otherbranch AS isucis_otherbranch,\n"
                + "  gunt.name AS guntname,\n"
                + "  gunt.sortname AS guntsortname,\n"
                + "  gunt.unitrounding AS guntunitrounding,\n"
                + "  isuc.quantity AS isucquantity\n"
                + "FROM \n"
                + "  inventory.stock_unit_con isuc \n"
                + "  INNER JOIN general.unit gunt ON (gunt.id = isuc.unit_id)"
                + "WHERE \n"
                + "  isuc.stock_id = ? AND isuc.deleted = FALSE\n"
                + where;
        Object[] param = new Object[]{stock.getId()};
        return getJdbcTemplate().query(sql, param, new StockAlternativeUnitMapper());
    }

    @Override
    public int create(StockUnitConnection obj) {
        String sql = "INSERT INTO \n"
                + "  inventory.stock_unit_con \n"
                + "(\n"
                + "  stock_id,\n"
                + "  unit_id,\n"
                + "  quantity,\n"
                + "  is_otherbranch,\n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES (\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?\n"
                + ") RETURNING id ;";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getUnit().getId(), obj.getQuantity(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockUnitConnection obj) {
        String sql = "UPDATE \n"
                + "  inventory.stock_unit_con \n"
                + "SET \n"
                + "  stock_id = ?, \n"
                + "  unit_id = ?,\n"
                + "  quantity = ? ,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ? ;";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getUnit().getId(), obj.getQuantity(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(StockUnitConnection stockUnitConnection) {
        String sql = "UPDATE inventory.stock_unit_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), stockUnitConnection.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<StockUnitConnection> findStockUnitConnection(Stock stock, BranchSetting branchSetting) {
        String where = " ";

        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND isuc.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + "  1 AS isucid,\n"
                + "  stck.id AS isucstock_id,\n"
                + "  stck.unit_id AS isucunit_id,\n"
                + "  gunt.is_otherbranch AS isucis_otherbranch,\n"
                + "  gunt.name AS guntname,\n"
                + "  gunt.sortname AS guntsortname,\n"
                + "  gunt.unitrounding AS guntunitrounding,\n"
                + "  NULL AS isucquantity\n"
                + "FROM inventory.stock stck\n"
                + "INNER JOIN general.unit gunt ON (gunt.id = stck.unit_id AND gunt.deleted =FALSE)\n"
                + "  WHERE \n"
                + "  stck.id = ? AND stck.deleted = FALSE\n"
                + "  \n"
                + "  UNION ALL\n"
                + "  \n"
                + "SELECT \n"
                + "  isuc.id AS isucid,\n"
                + "  isuc.stock_id AS isucstock_id,\n"
                + "  isuc.unit_id AS isucunit_id,\n"
                + "  isuc.is_otherbranch AS isucis_otherbranch,\n"
                + "  gunt.name AS guntname,\n"
                + "  gunt.sortname AS guntsortname,\n"
                + "  gunt.unitrounding AS guntunitrounding,\n"
                + "  isuc.quantity AS isucquantity\n"
                + "FROM \n"
                + "  inventory.stock_unit_con isuc \n"
                + "  INNER JOIN general.unit gunt ON (gunt.id = isuc.unit_id AND gunt.deleted = FALSE)\n"
                + "  WHERE \n"
                + "  isuc.stock_id = ? AND isuc.deleted = FALSE\n"
                + where;

        Object[] param = new Object[]{stock.getId(), stock.getId()};
        return getJdbcTemplate().query(sql, param, new StockAlternativeUnitMapper());

    }

}
