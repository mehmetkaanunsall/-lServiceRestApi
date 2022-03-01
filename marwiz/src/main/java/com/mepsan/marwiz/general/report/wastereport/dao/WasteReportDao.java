/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.wastereport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class WasteReportDao extends JdbcDaoSupport implements IWasteReportDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WasteReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, WasteReport obj, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND iw.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "whm.id AS whmid,\n"
                + "whm.stock_id AS whmstock_id,\n"
                + "iwr.processdate AS iwrprocessdate,\n"
                + "brn.name AS brnname,\n"
                + "stck.name AS stckname,\n"
                + "stck.barcode AS stckbarcode,\n"
                + "stck.code AS stckcode,\n"
                + "stck.centerproductcode AS stckcenterproductcode,\n"
                + "(SELECT general.find_category(stck.id, 1, iw.branch_id)) AS category,\n"
                + "gunt.name AS guntname,\n"
                + "gunt.sortname AS guntsortname,\n"
                + "gunt.unitrounding AS guntunitrounding,\n"
                + "COALESCE(wsi.alternativeunitquantity, 0) AS wsiquantity,\n"
                + "wsi.description AS wsidescription,\n"
                + "wsi.expirationdate AS wsiexpirationdate,\n"
                + "COALESCE(wsi.currentprice, 0) * COALESCE(wsi.exchangerate, 1) AS unitprice,\n"
                + "COALESCE(wsi.currency_id, 0) AS currency_id,\n"
                + "COALESCE(wsi.taxrate, 0) AS taxrate,\n"
                + "wr.name AS wrname,\n"
                + "wr.id AS wrid\n"
                + "FROM inventory.wasteiteminfo wsi \n"
                + "INNER JOIN inventory.warehousemovement whm ON(whm.id=wsi.warehousemovement_id AND whm.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehouse iw ON (whm.warehouse_id=iw.id AND iw.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehousereceipt iwr ON (whm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (whm.stock_id=stck.id and stck.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt ON (gunt.id = wsi.unit_id and gunt.deleted=FALSE)\n"
                + "LEFT JOIN inventory.wastereason wr ON(wr.id=wsi.wastereason_id and wr.deleted=false)\n"
                + "WHERE wsi.deleted=FALSE AND iwr.type_id = 76 " + whereBranch + "\n"
                + where + " ORDER BY iwr.processdate \n"
                + " LIMIT " + pageSize + " OFFSET " + first;

        List<WasteReport> result = getJdbcTemplate().query(sql, new WasteReportMapper());
        return result;
    }

    @Override
    public List<WasteReport> totals(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND iw.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "	 COUNT(t.whmid) AS whmid,\n"
                + "    SUM(t.wsiquantity) AS wsiquantity,\n"
                + "    SUM(t.total) AS total,\n"
                + "    t.currency_id AS currency_id,\n"
                + "    t.guntname AS guntname, \n"
                + "    t.guntsortname AS guntsortname,\n"
                + "    t.guntunitrounding AS guntunitrounding\n"
                + "FROM\n"
                + "(\n"
                + "SELECT \n"
                + "	whm.id AS whmid,\n"
                + "     wsi.alternativeunitquantity AS wsiquantity,\n"
                + "    ((COALESCE(wsi.currentprice,0)  * COALESCE(wsi.exchangerate, 1)) * wsi.alternativeunitquantity) AS total,\n"
                + "   " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AS currency_id,\n"
                + "    gunt.name AS guntname,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitrounding\n"
                + "FROM inventory.wasteiteminfo wsi \n"
                + "INNER JOIN inventory.warehousemovement whm ON(whm.id=wsi.warehousemovement_id AND whm.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehouse iw ON (whm.warehouse_id=iw.id AND iw.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehousereceipt iwr ON (whm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (whm.stock_id=stck.id and stck.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt ON (gunt.id = wsi.unit_id and gunt.deleted=FALSE)\n"
                + "LEFT JOIN inventory.wastereason wr ON(wr.id=wsi.wastereason_id and wr.deleted=false)\n"
                + "WHERE wsi.deleted=FALSE AND iwr.type_id = 76 " + whereBranch + "\n"
                + where
                + ") as t\n"
                + "GROUP BY t.currency_id,\n"
                + "    	 t.guntname, \n"
                + "    	 t.guntsortname,\n"
                + "    	 t.guntunitrounding";

        List<WasteReport> result = getJdbcTemplate().query(sql, new WasteReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, WasteReport obj, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND iw.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "whm.id AS whmid,\n"
                + "whm.stock_id AS whmstock_id,\n"
                + "iwr.processdate AS iwrprocessdate,\n"
                + "brn.name AS brnname,\n"
                + "stck.name AS stckname,\n"
                + "stck.barcode AS stckbarcode,\n"
                + "stck.code AS stckcode,\n"
                + "stck.centerproductcode AS stckcenterproductcode,\n"
                + "(SELECT general.find_category(stck.id, 1, iw.branch_id)) AS category,\n"
                + "gunt.name AS guntname,\n"
                + "gunt.sortname AS guntsortname,\n"
                + "gunt.unitrounding AS guntunitrounding,\n"
                + "COALESCE(wsi.alternativeunitquantity,0) AS wsiquantity,\n"
                + "wsi.description AS wsidescription,\n"
                + "wsi.expirationdate AS wsiexpirationdate,\n"
                + "COALESCE(wsi.currentprice, 0) * COALESCE(wsi.exchangerate, 1) AS unitprice,\n"
                + "COALESCE(wsi.currency_id, 0) AS currency_id,\n"
                + "COALESCE(wsi.taxrate, 0) AS taxrate,\n"
                + "wr.name AS wrname,\n"
                + "wr.id AS wrid\n"
                + "FROM inventory.wasteiteminfo wsi\n"
                + "INNER JOIN inventory.warehousemovement whm ON(whm.id=wsi.warehousemovement_id AND whm.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehouse iw ON (whm.warehouse_id=iw.id AND iw.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN inventory.warehousereceipt iwr ON (whm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (whm.stock_id=stck.id and stck.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt ON (gunt.id = wsi.unit_id and gunt.deleted=FALSE)\n"
                + "LEFT JOIN inventory.wastereason wr ON(wr.id=wsi.wastereason_id and wr.deleted=false)\n"
                + "WHERE wsi.deleted=FALSE AND iwr.type_id = 76 " + whereBranch + "\n"
                + where + " ORDER BY iwr.processdate";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
