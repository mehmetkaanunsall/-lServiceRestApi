/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.purchasedetailreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class PurchaseDetailReportDao extends JdbcDaoSupport implements IPurchaseDetailReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String exportData(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                  + "    invi.id AS inviid,\n"
                  + "    inv.documentnumber_id as invdocumentnumber_id,\n"
                  + "    inv.documentserial as invdocumentserial,\n"
                  + "    inv.documentnumber as invdocumentnumber,\n"
                  + "    acc2.name as invoiceaccount,\n"
                  + "    inv.invoicedate AS invinvoicedate,\n"
                  + "    inv.status_id AS invstatus_id,\n"
                  + "    inv.type_id AS invtype_id,\n"
                  + "    invi.stock_id AS invistock_id,\n"
                  + "    brn.name AS brnname,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    invi.unit_id AS stckunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting,\n"
                  + "    (COALESCE(invi.unitprice,0) / (1 + COALESCE(invi.taxrate,0) / 100)) * COALESCE(invi.exchangerate,1) AS inviunitprice,\n"
                  + "    invi.totalmoney AS invitotalmoney,\n"
                  + "    invi.currency_id AS invicurrency,\n"
                  + "    invi.quantity AS inviquantity,\n"
                  + "	(SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    stck.supplier_id AS stcksupplier_id,\n"
                  + "    acc.name AS accname, \n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    invi.taxrate AS invitaxrate,\n"
                  + "    invi.totaltax AS invitotaltax\n"
                  + "FROM finance.invoiceitem invi \n"
                  + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                  + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "    LEFT JOIN general.account acc2 ON(acc2.id = inv.account_id)\n"
                  + "WHERE \n"
                  + "	invi.deleted = False\n"
                  + "   AND inv.is_purchase = True " + whereBranch + " AND inv.status_id <> 30 AND inv.type_id <> 27 " + where;
        return sql;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<PurchaseDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                  + "    invi.id AS inviid,\n"
                  + "    inv.documentnumber_id as invdocumentnumber_id,\n"
                  + "    inv.documentserial as invdocumentserial,\n"
                  + "    inv.documentnumber as invdocumentnumber,\n"
                  + "    acc2.name as invoiceaccount,\n"
                  + "    inv.invoicedate AS invinvoicedate,\n"
                  + "    inv.status_id AS invstatus_id,\n"
                  + "    inv.type_id AS invtype_id,\n"
                  + "    invi.stock_id AS invistock_id,\n"
                  + "    brn.name AS brnname,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    invi.unit_id AS stckunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting,\n"
                  + "    (COALESCE(invi.unitprice,0) / (1 + COALESCE(invi.taxrate,0) / 100)) * COALESCE(invi.exchangerate,1) AS inviunitprice,\n"
                  + "    invi.totalmoney AS invitotalmoney,\n"
                  + "    invi.currency_id AS invicurrency,\n"
                  + "    invi.quantity AS inviquantity,\n"
                  + "	(SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    stck.supplier_id AS stcksupplier_id,\n"
                  + "    acc.name AS accname, \n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    invi.taxrate AS invitaxrate,\n"
                  + "    invi.totaltax AS invitotaltax\n"
                  + "FROM finance.invoiceitem invi \n"
                  + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                  + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "    LEFT JOIN general.account acc2 ON(acc2.id = inv.account_id)\n"
                  + "WHERE \n"
                  + "	invi.deleted = False\n"
                  + "   AND inv.is_purchase = True " + whereBranch + " AND inv.status_id <> 30 AND inv.type_id <> 27\n"
                  + where + "\n"
                  + "ORDER BY inv.invoicedate\n"
                  + " limit " + pageSize + " offset " + first;
                
        List<PurchaseDetailReport> result = getJdbcTemplate().query(sql, new PurchaseDetailReportMapper());

        return result;
    }

    @Override
    public int count(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                  + "	\n"
                  + "\n"
                  + "COUNT(invi.id) AS id\n"
                  + "FROM finance.invoiceitem invi \n"
                  + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                  + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "    LEFT JOIN general.account acc2 ON(acc2.id = inv.account_id)\n"
                  + "WHERE\n"
                  + "	invi.deleted = False\n"
                  + "   AND inv.is_purchase = True " + whereBranch + " AND inv.status_id <> 30 AND inv.type_id <> 27 " + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public List<PurchaseDetailReport> totals(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                  + "	\n"
                  + "COUNT(invi.id) AS inviid,\n"
                  + "invi.currency_id as invicurrency,\n"
                  + "invi.unit_id as stckunit_id,\n"
                  + "sum(invi.quantity) as inviquantity,\n"
                  + "sum(invi.totalmoney) as invitotalmoney,\n"
                  + "gunt.sortname AS guntsortname\n"
                  + "FROM finance.invoiceitem invi \n"
                  + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                  + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "    LEFT JOIN general.account acc2 ON(acc2.id = inv.account_id)\n"
                  + "    INNER JOIN system.currency cr ON(cr.id=invi.currency_id)\n"
                  + "WHERE\n"
                  + "	invi.deleted = False\n"
                  + "   AND inv.is_purchase = True " + whereBranch + " AND inv.status_id <> 30 AND inv.type_id <> 27"
                  + where + "\n"
                  + "   GROUP BY invi.currency_id,invi.unit_id,gunt.sortname ";

        List<PurchaseDetailReport> result = getJdbcTemplate().query(sql, new PurchaseDetailReportMapper());
        return result;
    }

}
