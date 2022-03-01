/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:16:40
 */
package com.mepsan.marwiz.general.report.orderlistreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseMapper;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class OrderListReportDao extends JdbcDaoSupport implements IOrderListReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchID) {
        String sql = "SELECT \n"
                + "    iwi.stock_id AS iwistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    COALESCE(stcki.minstocklevel,0) AS stckiminstocklevel,\n"
                + "    COALESCE(SUM(iwi.quantity),0) AS sumquantity,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitsorting,\n"
                + "    (SELECT general.find_category(iwi.stock_id, 1, ?)) AS category,\n"
                + "    stck.brand_id AS stckbrand_id,\n"
                + "    br.name AS brname,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    brn.id AS brnid,\n"
                + "    brn.name AS brnname\n"
                + "FROM inventory.warehouseitem iwi\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id AND stck.deleted = False)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=False AND stcki.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=False \n"
                + where + "\n"
                + "GROUP BY iwi.stock_id, stck.name, stcki.minstocklevel, stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.barcode, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name\n"
                + "HAVING SUM(iwi.quantity) < stcki.minstocklevel\n"
                + "ORDER BY  SUM(iwi.quantity)\n"
                + " limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<OrderListReport> result = getJdbcTemplate().query(sql, param, new OrderListReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, String branchID) {

        String sql = "SELECT \n"
                + "    iwi.stock_id AS iwistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    COALESCE(stcki.minstocklevel,0) AS stckiminstocklevel,\n"
                + "    COALESCE(SUM(iwi.quantity),0) AS sumquantity,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitsorting,\n"
                + "    (SELECT general.find_category(iwi.stock_id, 1, " + sessionBean.getUser().getLastBranch().getId() + ")) AS category,\n"
                + "    stck.brand_id AS stckbrand_id,\n"
                + "    br.name AS brname,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    brn.id AS brnid,\n"
                + "    brn.name AS brnname\n"
                + "FROM inventory.warehouseitem iwi\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id AND stck.deleted = False)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=False AND stcki.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=False \n"
                + where + "\n"
                + "GROUP BY iwi.stock_id, stck.name, stcki.minstocklevel, stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.barcode, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name\n"
                + "HAVING SUM(iwi.quantity) < stcki.minstocklevel\n"
                + "ORDER BY SUM(iwi.quantity)";
        return sql;
    }

    @Override
    public int count(String where, String branchID) {
        String sql = " SELECT	COUNT(u.iwistock_id) FROM \n"
                + " (SELECT COUNT(iwi.stock_id) as iwistock_id FROM  inventory.warehouseitem iwi  \n"
                + " INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id AND stck.deleted = False)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=False AND stcki.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=false " + where + " GROUP BY iwi.stock_id, stck.name, stcki.minstocklevel , stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.barcode, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name\n"
                + "HAVING SUM(iwi.quantity) < stcki.minstocklevel) u";

        Object[] param = new Object[]{};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<Warehouse> listWarehouse(String branchID) {
        String sql = "select \n"
                + "iw.id as iwid,\n"
                + "iw.name as iwname\n"
                + "from inventory.warehouse iw\n"
                + "where iw.deleted=false and iw.branch_id IN (" + branchID + ") ";

        Object[] param = {};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;

    }

    @Override
    public List<OrderListReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
