/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:39:52
 */
package com.mepsan.marwiz.general.report.salesreturnreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SalesReturnReportDao extends JdbcDaoSupport implements ISalesReturnReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch) {
        String whereB = "";
        if (!whereBranch.isEmpty()) {
            whereB = "AND  sl.branch_id IN( " + whereBranch + " )";
        }

        String sql = "SELECT \n"
                  + "    sl.id  AS slid,\n"
                  + "    rcp.id AS rcpid,"
                  + "    brn.id AS brnid,\n"
                  + "    brn.name AS brnname,\n"
                  + "    rcp.receiptno AS rcpreceiptno,\n"
                  + "    inv.id AS invid,\n"
                  + "    COALESCE(inv.documentserial,'') || inv.documentnumber AS invdocumentno,\n"
                  + "    sl.processdate AS slprocessdate,\n"
                  + "    sli.stock_id AS slistock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalmoney,\n"
                  + "    sli.currency_id AS slicurrency_id,\n"
                  + "    sli.quantity AS sliquantity,\n"
                  + "    sli.unit_id AS stckunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting,\n"
                  + "    sli.totalprice AS slitotalprice,\n"
                  + "    stg.rate AS stgrate\n"
                  + "FROM general.sale sl \n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id = sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                  + "INNER JOIN general.saleitem sli ON(sl.id=sli.sale_id AND sli.deleted = False)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN (SELECT \n"
                  + "          txg.rate AS rate,\n"
                  + "          stc.stock_id AS stock_id \n"
                  + "          FROM inventory.stock_taxgroup_con stc  \n"
                  + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "          WHERE stc.deleted = false\n"
                  + "          AND txg.type_id = 10 --kdv grubundan \n"
                  + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = sli.stock_id)\n"
                  + "WHERE sl.is_return=True AND sl.invoice_id IS NULL AND sl.deleted=FALSE " + whereB + "\n"
                  + where + "\n"
                  + "ORDER BY brn.id,brn.name,sl.processdate,brn.id, sli.stock_id \n"
                  + " limit " + pageSize + " offset " + first;
        List<ReceiptReturnReport> result = getJdbcTemplate().query(sql, new ReceiptReturnReportMapper());
        return result;
        
       
    }

    @Override
    public String exportData(String where, String whereBranch) {
        String whereB = "";
        if (!whereBranch.isEmpty()) {
            whereB = "AND  sl.branch_id IN( " + whereBranch + " )";
        }

        String sql = "SELECT \n"
                  + "    sl.id  AS slid,\n"
                  + "    rcp.id AS rcpid,\n"
                  + "    brn.id AS brnid,\n"
                  + "    brn.name AS brnname,\n"
                  + "    rcp.receiptno AS rcpreceiptno,\n"
                  + "    inv.id AS invid,\n"
                  + "    COALESCE(inv.documentserial,'') || inv.documentnumber AS invdocumentno,\n"
                  + "    sl.processdate AS slprocessdate,\n"
                  + "    sli.stock_id AS slistock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalmoney,\n"
                  + "    sli.currency_id AS slicurrency_id,\n"
                  + "    sli.quantity AS sliquantity,\n"
                  + "    sli.unit_id AS stckunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting,\n"
                  + "    sli.totalprice AS slitotalprice,\n"
                  + "    stg.rate AS stgrate\n"
                  + "FROM general.sale sl\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id = sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                  + "INNER JOIN general.saleitem sli ON(sl.id=sli.sale_id AND sli.deleted = False)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN (SELECT \n"
                  + "          txg.rate AS rate,\n"
                  + "          stc.stock_id AS stock_id \n"
                  + "          FROM inventory.stock_taxgroup_con stc  \n"
                  + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "          WHERE stc.deleted = false\n"
                  + "          AND txg.type_id = 10 --kdv grubundan \n"
                  + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = sli.stock_id)\n"
                  + "WHERE sl.is_return=True AND sl.invoice_id IS NULL AND sl.deleted=FALSE " + whereB + "\n"
                  + where + "\n"
                  + "ORDER BY brn.id,brn.name,sl.processdate,brn.id, sli.stock_id\n";
        return sql;
    }

    @Override
    public int count(String where, String whereBranch) {

        String whereB = "";
        if (!whereBranch.isEmpty()) {
            whereB = "AND  sl.branch_id IN( " + whereBranch + " )";
        }
        String sql = " SELECT \n"
                  + "	COUNT(sli.id)\n"
                  + "FROM general.sale sl\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id = sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                  + "INNER JOIN general.saleitem sli ON(sl.id=sli.sale_id AND sli.deleted = False)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN (SELECT \n"
                  + "          txg.rate AS rate,\n"
                  + "          stc.stock_id AS stock_id \n"
                  + "          FROM inventory.stock_taxgroup_con stc  \n"
                  + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "          WHERE stc.deleted = false\n"
                  + "          AND txg.type_id = 10 --kdv grubundan \n"
                  + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = sli.stock_id)\n"
                  + "WHERE sl.is_return=True AND sl.invoice_id IS NULL AND sl.deleted=FALSE " + whereB + "\n"
                  + where + "\n";

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<ReceiptReturnReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ReceiptReturnReport> totals(String where, String whereBranch) {

        String whereB = "";
        if (!whereBranch.isEmpty()) {
            whereB = "AND  sl.branch_id IN( " + whereBranch + " )";
        }

        String sql = "SELECT \n"
                  + "    COUNT(sl.id)  AS slid,\n"
                  + "    brn.id AS brnid,\n"
                  + "    brn.name AS brnname,\n"
                  + "    SUM(COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0)) AS slitotalmoney,\n"
                  + "    sli.currency_id AS slicurrency_id,\n"
                  + "   SUM(sli.quantity) AS sliquantity,\n"
                  + "   SUM(sli.totalprice) AS slitotalprice\n"
                  + "FROM general.sale sl \n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id = sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                  + "INNER JOIN general.saleitem sli ON(sl.id=sli.sale_id AND sli.deleted = False)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN (SELECT \n"
                  + "          txg.rate AS rate,\n"
                  + "          stc.stock_id AS stock_id \n"
                  + "          FROM inventory.stock_taxgroup_con stc  \n"
                  + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "          WHERE stc.deleted = false\n"
                  + "          AND txg.type_id = 10 --kdv grubundan \n"
                  + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = sli.stock_id)\n"
                  + "WHERE sl.is_return=True AND sl.invoice_id IS NULL AND sl.deleted=FALSE " + whereB + "\n"
                  + where + "\n"
                  + "GROUP BY brn.id,brn.name, sli.currency_id\n"
                  + "ORDER BY brn.id,brn.name, sli.currency_id\n";
        Object[] param = new Object[]{};
        List<ReceiptReturnReport> result = getJdbcTemplate().query(sql, param, new ReceiptReturnReportMapper());
        return result;
    }

}
