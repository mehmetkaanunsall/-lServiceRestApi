/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 02:36:44
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WarehouseDao extends JdbcDaoSupport implements IWarehouseDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Warehouse> findAll() {
        String sql = "select \n"
                  + "iw.id as iwid,\n"
                  + "iw.name as iwname,\n"
                  + "iw.code as iwcode,\n"
                  + "iw.description as iwdescription,\n"
                  + "iw.status_id as iwstatus_id,\n"
                  + "std.name as stdname,\n"
                  + "(SELECT CASE WHEN EXISTS (SELECT id FROM inventory.vendingmachine WHERE warehouse_id=iw.id AND deleted=False) THEN TRUE ELSE FALSE END) AS isautomat\n"
                  + "from inventory.warehouse iw\n"
                  + "left join system.status_dict std on (iw.status_id=std.status_id and std.language_id=?)\n"
                  + "where iw.deleted=false and iw.is_fuel=FALSE and iw.branch_id= ?";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;
    }

    @Override
    public int create(Warehouse obj) {
        String sql = "insert into inventory.warehouse\n"
                  + "(name,code,branch_id,status_id,description,c_id,u_id)\n"
                  + "values(?,?,?,?,?,?,?)"
                  + " RETURNING id ;";

        Object[] param = {obj.getName(), obj.getCode(), sessionBean.getUser().getLastBranch().getId(), obj.getStatus().getId(), obj.getDescription(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Warehouse obj) {
        String sql = "update inventory.warehouse\n"
                  + "set\n"
                  + "name=?,\n"
                  + "code=?,\n"
                  + "status_id=?,\n"
                  + "description=?,\n"
                  + "u_id=?,\n"
                  + "u_time= now()"
                  + "where id=?;";

        Object[] param = {obj.getName(), obj.getCode(), obj.getStatus().getId(), obj.getDescription(), sessionBean.getUser().getId(), obj.getId()};

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Warehouse> selectListWarehouse(String where) {
        String sql = "select \n"
                  + "iw.id as iwid,\n"
                  + "iw.name as iwname\n"
                  + "from inventory.warehouse iw\n"
                  + "where iw.deleted=false and iw.is_fuel = FALSE and iw.branch_id= ? " + where;
        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;
    }

    /**
     * Bu metot gelen stok un bulunduğu depoları ceker
     *
     * @param stock
     * @param where
     * @param branch
     * @return
     */
    @Override
    public List<Warehouse> selectListWarehouse(Stock stock, String where, Branch branch) {
        String sql = "SELECT \n"
                  + "  iw.id as iwid,\n"
                  + "  iw.name as iwname\n"
                  + "FROM inventory.warehouse iw\n"
                  + "INNER JOIN inventory.warehouseitem wi ON (wi.warehouse_id = iw.id AND wi.deleted = FALSE)\n"
                  + "WHERE iw.deleted = FALSE\n"
                  + "AND iw.branch_id = ?\n"
                  + "AND wi.stock_id  = ? " + where;

        Object[] param = {branch.getId(), stock.getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;
    }

    @Override
    public List<Warehouse> selectListAllWarehouse(String where) {
        String sql = "select \n"
                  + "iw.id as iwid,\n"
                  + "iw.branch_id as iwbranch_id,\n"
                  + "iw.name as iwname\n"
                  + "from inventory.warehouse iw\n"
                  + "where iw.deleted=false " + where;

        List<Warehouse> result = getJdbcTemplate().query(sql, new WarehouseMapper());
        return result;
    }

    @Override
    public int testBeforeDelete(Warehouse warehouse) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT warehouse_id FROM inventory.warehousemovement WHERE warehouse_id=? AND deleted=False) THEN 1\n"
                  + " WHEN EXISTS (SELECT warehouse_id FROM inventory.warehouseitem WHERE warehouse_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{warehouse.getId(), warehouse.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Warehouse warehouse) {
        String sql = "UPDATE inventory.warehouse SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouse.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Warehouse> selectListWarehouseForBranch(Branch branch, String where) {
        String sql = "select \n"
                  + "iw.id as iwid,\n"
                  + "iw.name as iwname\n"
                  + "from inventory.warehouse iw\n"
                  + "where iw.deleted=false and iw.is_fuel =false and iw.branch_id= ? \n"
                  + where;

        Object[] param = {branch.getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;
    }

}
