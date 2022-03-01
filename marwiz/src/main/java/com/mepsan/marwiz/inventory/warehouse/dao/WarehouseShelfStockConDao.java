/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 03:56:19
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelfStockCon;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WarehouseShelfStockConDao extends JdbcDaoSupport implements IWarehouseShelfStockConDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WarehouseShelfStockCon> findAll(String where) {
        String sql = "select \n"
                  + "wssc.id as wsscid,\n"
                  + "wssc.stock_id as wsscstock_id,\n"
                  + "wssc.warehouseshelf_id as wsscwarehouseshelf_id,\n"
                  + "ws.name as wsname,\n"
                  + "ws.code as wscode,\n"
                  + "stck.name as stckname,\n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.code as stckcode\n"
                  + "from inventory.warehouseshelf_stock_con wssc\n"
                  + "inner join inventory.warehouseshelf ws on (wssc.warehouseshelf_id=ws.id and ws.deleted=false)\n"
                  + "inner join inventory.stock stck on (stck.id=wssc.stock_id and stck.deleted=false)\n"
                  + "where wssc.deleted=false " + where;
        List<WarehouseShelfStockCon> result = getJdbcTemplate().query(sql, new WarehouseShelfStockConMapper());
        return result;
    }

    @Override
    public int create(WarehouseShelfStockCon obj) {
        String sql = "insert into inventory.warehouseshelf_stock_con \n"
                  + "(warehouseshelf_id,stock_id,c_id,u_id)\n"
                  + "values(?,?,?,?) returning id;";
        Object[] param = {obj.getWarehouseShelf().getId(), obj.getStock().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WarehouseShelfStockCon obj) {
        String sql = "update inventory.warehouseshelf_stock_con set\n"
                  + "warehouseshelf_id=?,\n"
                  + "stock_id=?,\n"
                  + "u_id=?\n"
                  + "where id=?";
        Object[] param = {obj.getWarehouseShelf().getId(), obj.getStock().getId(), sessionBean.getUser().getId(), obj.getId()};

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(WarehouseShelfStockCon warehouseShelfStockCon) {
        String sql = "UPDATE inventory.warehouseshelf_stock_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouseShelfStockCon.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
