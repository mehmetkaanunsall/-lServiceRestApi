/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.dao;

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
public class EntryExitSummaryReportDao extends JdbcDaoSupport implements IEntryExitSummaryReportDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<EntryExitSummary> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, EntryExitSummary obj) {

        String sql = "SELECT\n"
                  + "stck.id AS stckid,\n"
                  + "stck.name AS stckname,\n"
                  + "stck.code AS stckcode,\n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.barcode AS stckbarcode,\n"
                  + "iw.id AS iwid,\n"
                  + "iw.name AS iwname,\n"
                  + "gunt.sortname AS guntsortname,\n"
                  + "gunt.unitrounding AS guntunitrounding,\n"
                  + "COALESCE(SUM(CASE WHEN wm.is_direction=TRUE THEN wm.quantity ELSE 0 END),0) AS entry,\n"
                  + "COALESCE(SUM(CASE WHEN wm.is_direction=FALSE THEN wm.quantity ELSE 0 END),0) AS exit,\n"
                  + "COALESCE((\n"
                  + "   SELECT \n"
                  + "   SUM(CASE WHEN wm1.is_direction=TRUE THEN wm1.quantity ELSE -wm1.quantity END)\n"
                  + "   FROM inventory.warehousemovement wm1\n"
                  + "   LEFT JOIN inventory.warehousereceipt iwr1 ON (wm1.warehousereceipt_id=iwr1.id AND iwr1.deleted=FALSE) \n"
                  + "   WHERE wm1.deleted=FALSE AND wm1.warehouse_id=iw.id AND wm1.stock_id=stck.id\n"
                  + "   AND iwr1.processdate <= ? \n"
                  + "),0) AS lastquantity,\n"
                  + "(SELECT general.find_category(stck.id, 1, ?)) AS category,\n"
                  + "stck.brand_id AS stckbrand_id,\n"
                  + "br.name AS brname,\n"
                  + "stck.supplier_id AS stcksupplier_id,\n"
                  + "acc.name AS accname, \n"
                  + "stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "cspp.name AS csppname,\n"
                  + "iw.branch_id AS iwbranch_id,\n"
                  + "brc.name AS brcname\n"
                  + "FROM\n"
                  + "inventory.warehousemovement wm\n"
                  + "LEFT JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.warehouse iw ON (wm.warehouse_id=iw.id)\n"
                  + "LEFT JOIN inventory.stock stck ON (wm.stock_id=stck.id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = iw.branch_id AND brc.deleted=FALSE)\n"
                  + "WHERE wm.deleted=FALSE \n"
                  + where + "\n"
                  + "GROUP BY stck.id,stck.name,stck.code,stck.centerproductcode,stck.barcode,iw.id,iw.name,gunt.sortname,gunt.unitrounding, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, iw.branch_id, brc.name \n"
                  + "ORDER BY stck.name,iw.name\n"
                  + " LIMIT " + pageSize + " OFFSET " + first;
        Object[] param = new Object[]{obj.getEndDate(), sessionBean.getUser().getLastBranch().getId()};
        List<EntryExitSummary> result = getJdbcTemplate().query(sql, param, new EntryExitSummaryReportMapper());
        return result;
    }

    @Override
    public int count(String where, EntryExitSummary obj) {
        String sql = ""
                  + "SELECT \n"
                  + "COUNT (t.stckid)\n"
                  + "FROM\n"
                  + "("
                  + "SELECT\n"
                  + "stck.id AS stckid,\n"
                  + "stck.name AS stckname,\n"
                  + "stck.code AS stckcode,\n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.barcode AS stckbarcode,\n"
                  + "gunt.sortname AS guntsortname,\n"
                  + "gunt.unitrounding AS guntunitrounding,\n"
                  + "iw.id AS iwid,\n"
                  + "iw.name AS iwname,\n"
                  + "SUM(CASE WHEN wm.is_direction=TRUE THEN wm.quantity ELSE 0 END) AS entry,\n"
                  + "SUM(CASE WHEN wm.is_direction=FALSE THEN wm.quantity ELSE 0 END) AS exit\n"
                  + "FROM\n"
                  + "inventory.warehousemovement wm\n"
                  + "LEFT JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.warehouse iw ON (wm.warehouse_id=iw.id)\n"
                  + "LEFT JOIN inventory.stock stck ON (wm.stock_id=stck.id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = iw.branch_id AND brc.deleted=FALSE)\n"
                  + "WHERE wm.deleted=FALSE \n"
                  + where + "\n"
                  + "GROUP BY stck.id,stck.name,stck.code,stck.centerproductcode,stck.barcode,iw.id,iw.name,gunt.sortname,gunt.unitrounding, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, iw.branch_id, brc.name  \n"
                  + ") AS t";

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where, EntryExitSummary obj) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT\n"
                  + "stck.id AS stckid,\n"
                  + "stck.name AS stckname,\n"
                  + "stck.code AS stckcode,\n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.barcode AS stckbarcode,\n"
                  + "iw.id AS iwid,\n"
                  + "iw.name AS iwname,\n"
                  + "gunt.sortname AS guntsortname,\n"
                  + "gunt.unitrounding AS guntunitrounding,\n"
                  + "COALESCE(SUM(CASE WHEN wm.is_direction=TRUE THEN wm.quantity ELSE 0 END),0) AS entry,\n"
                  + "COALESCE(SUM(CASE WHEN wm.is_direction=FALSE THEN wm.quantity ELSE 0 END),0) AS exit,\n"
                  + "COALESCE((\n"
                  + "   SELECT \n"
                  + "   SUM(CASE WHEN wm1.is_direction=TRUE THEN wm1.quantity ELSE -wm1.quantity END)\n"
                  + "   FROM inventory.warehousemovement wm1\n"
                  + "   LEFT JOIN inventory.warehousereceipt iwr1 ON (wm1.warehousereceipt_id=iwr1.id AND iwr1.deleted=FALSE) \n"
                  + "   WHERE wm1.deleted=FALSE AND wm1.warehouse_id=iw.id AND wm1.stock_id=stck.id\n"
                  + "   AND iwr1.processdate <= '" + sd.format(obj.getEndDate()) + "' \n"
                  + "),0) AS lastquantity,\n"
                  + "(SELECT general.find_category(stck.id, 1, " + sessionBean.getUser().getLastBranch().getId() + ")) AS category,\n"
                  + "stck.brand_id AS stckbrand_id,\n"
                  + "br.name AS brname,\n"
                  + "stck.supplier_id AS stcksupplier_id,\n"
                  + "acc.name AS accname, \n"
                  + "stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "cspp.name AS csppname,\n"
                  + "iw.branch_id AS iwbranch_id,\n"
                  + "brc.name AS brcname\n"
                  + "FROM\n"
                  + "inventory.warehousemovement wm\n"
                  + "LEFT JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.warehouse iw ON (wm.warehouse_id=iw.id)\n"
                  + "LEFT JOIN inventory.stock stck ON (wm.stock_id=stck.id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = iw.branch_id AND brc.deleted=FALSE)\n"
                  + "WHERE wm.deleted=FALSE \n"
                  + where + "\n"
                  + "GROUP BY stck.id,stck.name,stck.code,stck.centerproductcode,stck.barcode,iw.id,iw.name,gunt.sortname,gunt.unitrounding, stck.brand_id, br.name,  stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, iw.branch_id, brc.name \n"
                  + "ORDER BY stck.name,iw.name\n";
        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
