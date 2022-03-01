/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 12:26:04
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WarehouseShelfDao extends JdbcDaoSupport implements IWarehouseShelfDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WarehouseShelf> findAll(Warehouse warehouse) {
        String sql = "select \n"
                  + "iws.id as iwsid,\n"
                  + "iws.name as iwsname,\n"
                  + "iws.code as iwscode\n"
                  + "from inventory.warehouseshelf iws \n"
                  + "where iws.deleted=false and iws.warehouse_id=?";
        Object[] param = new Object[]{warehouse.getId()};
        return getJdbcTemplate().query(sql, param, new WarehouseShelfMapper());
    }

    @Override
    public int create(WarehouseShelf obj) {
        String sql = "insert into inventory.warehouseshelf\n"
                  + "(warehouse_id,name,code,c_id,u_id)\n"
                  + "values\n"
                  + "(?,?,?,?,?) returning id;";
        Object[] param = {obj.getWareHouse().getId(), obj.getName(), obj.getCode(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WarehouseShelf obj) {
        String sql = "update inventory.warehouseshelf set\n"
                  + "name=?,\n"
                  + "code=?,\n"
                  + "u_id=?\n"
                  + "where id=?";

        Object[] param = {obj.getName(), obj.getCode(), sessionBean.getUser().getId(), obj.getId()};

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WarehouseShelf> selectShelfWithoutCon(Warehouse warehouse, Stock stock) {
        String sql = "select \n"
                  + "iws.id as iwsid,\n"
                  + "iws.name as iwsname,\n"
                  + "iws.code as iwscode\n"
                  + "from inventory.warehouseshelf iws \n"
                  + "where iws.deleted=false and iws.warehouse_id=?\n"
                  + "and iws.id not in (select wssc.warehouseshelf_id from inventory.warehouseshelf_stock_con wssc where wssc.deleted=false and wssc.stock_id=? )";
        Object[] param = new Object[]{warehouse.getId(), stock.getId()};
        System.out.println("" + Arrays.toString(param));
        return getJdbcTemplate().query(sql, param, new WarehouseShelfMapper());
    }

    @Override
    public int delete(WarehouseShelf warehouseShelf) {
        String sql = "UPDATE inventory.warehouseshelf SET deleted=TRUE, u_id=? , d_time=NOW() WHERE deleted=False AND id=?;\n"
                  + "UPDATE inventory.warehouseshelf_stock_con SET deleted=TRUE, u_id=? , d_time=NOW() WHERE deleted=False AND warehouseshelf_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouseShelf.getId(), sessionBean.getUser().getId(), warehouseShelf.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
