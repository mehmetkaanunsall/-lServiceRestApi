package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.automat.report.incomeexpensereport.dao.IncomeExpenseReportDao;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date 22.01.2018 11:27:47
 */
public class WarehouseItemDao extends JdbcDaoSupport implements IWarehouseItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WarehouseItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse wareHouse) {
        if (sortField == null) {
            sortField = "iwi.id";
            sortOrder = " asc ";
        }

        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        String sql = "select \n"
                + "DISTINCT iwi.id as iwid,\n"
                + "iwi.stock_id as iwistock_id,\n"
                + "stck.name as stckname,\n"
                + "stck.code as stckcode,\n"
                + "stck.centerproductcode AS stckcenterproductcode,\n"
                + "stck.barcode AS stckbarcode,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "iwi.quantity as iwiquantity, \n"
                + "iwi.minstocklevel as iwiminstocklevel \n"
                + "from inventory.warehouseitem iwi\n"
                + "left join inventory.stock stck on (iwi.stock_id=stck.id and stck.deleted=false)\n"
                + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                + "left join inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
                + "where iwi.warehouse_id=? and iwi.deleted=false " + where + "\n"
                + "order by " + sortField + " " + sortOrder + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{wareHouse.getId()};
        List<WarehouseItem> result = getJdbcTemplate().query(sql, param, new WareHouseItemMapper());
        return result;
    }

    @Override
    public int count(String where, Warehouse wareHouse) {

        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        String sql = "select \n"
                + "count (DISTINCT iwi.id)\n"
                + "from inventory.warehouseitem iwi\n"
                + "left join inventory.stock stck on (iwi.stock_id=stck.id and stck.deleted=false)\n"
                + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                + "left join inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
                + "where iwi.warehouse_id=? and iwi.deleted=false " + where + "\n";

        Object[] param = new Object[]{wareHouse.getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int addStock(Warehouse warehouse, String where) {

        String sql = "INSERT INTO\n"
                + "inventory.warehouseitem\n"
                + "(\n"
                + "warehouse_id,\n"
                + "stock_id,\n"
                + "quantity,\n"
                + "c_id,\n"
                + "u_id\n"
                + ")\n"
                + "SELECT\n"
                + "?,\n"
                + "stck.id,\n"
                + "0,\n"
                + "?,\n"
                + "?\n"
                + "FROM\n"
                + "inventory.stock stck\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE \n"
                + where + " AND stck.status_id <> 4 AND si.is_passive = FALSE ";
        Object[] param = {warehouse.getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};

        System.out.println("----addstock sql ----" + sql);

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WarehouseItem obj
    ) {
        String sql = "update inventory.warehouseitem\n"
                + "set\n"
                + "minstocklevel=?,\n"
                + "u_id=?,\n"
                + "u_time=now()\n"
                + "where id = ?";
        Object[] param = new Object[]{obj.getMinStockLevel(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String exportData(String where
    ) {

        String sortField = "";
        String sortOrder = "";

        sortField = "iwi.id";
        sortOrder = " asc ";

        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        String sql = "select \n"
                + "DISTINCT iwi.id as iwid,\n"
                + "iwi.stock_id as iwistock_id,\n"
                + "stck.name as stckname,\n"
                + "stck.code as stckcode,\n"
                + "stck.centerproductcode AS stckcenterproductcode,\n"
                + "stck.barcode AS stckbarcode,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "iwi.quantity as iwiquantity, \n"
                + "iwi.minstocklevel as iwiminstocklevel \n"
                + "from inventory.warehouseitem iwi\n"
                + "left join inventory.stock stck on (iwi.stock_id=stck.id and stck.deleted=false)\n"
                + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                + "left join inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
                + "where iwi.warehouse_id=? and iwi.deleted=false " + where + "\n"
                + "order by " + sortField + " " + sortOrder;

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
