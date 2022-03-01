/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 14.03.2018 09:01:26
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockAlternativeBarcodeDao extends JdbcDaoSupport implements IStockAlternativeBarcodeDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockAlternativeBarcode> findAll(Stock stock) {

        String where = " ";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND sab.is_otherbranch = FALSE ";
        }
        String sql = "SELECT \n"
                  + "   sab.id AS sabid,\n"
                  + "   sab.barcode as sabbarcode,\n"
                  + "   sab.is_otherbranch AS sabis_otherbranch,\n"
                  + "   sab.quantity AS sabquantity\n"
                  + "FROM inventory.stockalternativebarcode sab   \n"
                  + "WHERE sab.deleted = FALSE \n"
                  + "AND sab.stock_id = ?\n"
                  + where;

        Object[] param = new Object[]{stock.getId()};
        return getJdbcTemplate().query(sql, param, new StockAlternativeBarcodeMapper());
    }

    @Override
    public int create(StockAlternativeBarcode obj) {
        String sql = "INSERT INTO \n"
                  + "  inventory.stockalternativebarcode\n"
                  + "(\n"
                  + "  stock_id,\n"
                  + "  barcode,\n"
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

        Object[] param = new Object[]{obj.getStock().getId(), obj.getBarcode(), obj.getQuantity(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockAlternativeBarcode obj) {
        String sql = "UPDATE \n"
                  + "  inventory.stockalternativebarcode \n"
                  + "SET \n"
                  + "  stock_id = ?, \n"
                  + "  barcode = ?,\n"
                  + "  quantity = ? ,\n"
                  + "  u_id = ?,\n"
                  + "  u_time = now()\n"
                  + "WHERE \n"
                  + "  id = ? ;";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getBarcode(), obj.getQuantity(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(StockAlternativeBarcode stockAlternativeBarcode) {
        String sql = "UPDATE inventory.stockalternativebarcode SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), stockAlternativeBarcode.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
