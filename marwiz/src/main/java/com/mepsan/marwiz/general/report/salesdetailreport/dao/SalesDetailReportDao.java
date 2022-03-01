/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 12:08:27
 */
package com.mepsan.marwiz.general.report.salesdetailreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SalesDetailReportDao extends JdbcDaoSupport implements ISalesDetailReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String exportData(String where, String branchList, SalesDetailReport salesDetailReport) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {

            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "        brn.id AS brnid,\n"
                + "        brn.name AS brnname,\n"
                + "        sli.id AS sliid,\n"
                + "        sli.stock_id AS slistock_id,\n"
                + "        stck.name AS stckname,\n"
                + "        sli.processdate AS sliprocessdate,\n"
                + "        stck.barcode AS stckbarcode,\n"
                + "        stck.code AS stckcode,\n"
                + "        stck.centerproductcode AS stckcenterproductcode,\n"
                + "        sli.quantity AS sliquantity,\n"
                + "        sl.receipt_id AS slreceipt_id,\n"
                + "        rcp.receiptno AS rcpreceiptno,\n"
                + "        sl.invoice_id AS slinvoice_id,\n"
                + "        sl.saletype_id AS slsaletype_id,\n"
                + "        sl.discounttype_id AS sldiscounttype_id,\n"
                + "        sl.transactionno AS sltransactionno,\n"
                + "        inv.documentnumber AS invdocumentnumber,\n"
                + "        COALESCE(sli.unitprice,0) AS sliunitprice,\n"
                + "        COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalmoney,\n"
                + "        sli.currency_id AS slicurrency_id,\n"
                + "        sl.account_id AS slaccount_id,\n"
                + "        acc.name AS accname,\n"
                + "        acc.title AS acctitle,\n"
                + "        acc.is_employee AS accis_employee,\n"
                + "        gunt.sortname AS guntsortname,\n"
                + "        gunt.unitrounding AS guntunitsorting,\n"
                + "       (SELECT general.find_category(sli.stock_id, 1,brn.id)) AS category,\n"
                + "        stck.brand_id AS stckbrand_id,\n"
                + "        br.name AS brname,\n"
                + "        stck.supplier_id AS stcksupplier_id,\n"
                + "        acc1.name AS acc1name, \n"
                + "        stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "        cspp.name AS csppname,\n"
                + "        us.name AS usname,\n"
                + "        us.surname AS ussurname \n"
                + "FROM general.saleitem sli\n"
                + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesDetailReport.getEndDate()) + "')\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN general.userdata us ON (us.id = sl.userdata_id AND us.deleted = FALSE) \n"
                + "WHERE  sl.is_return=False AND sli.deleted=FALSE " + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + where + "\n"
                + "ORDER BY brn.id, sli.processdate DESC\n";

        return sql;
    }

    @Override
    public int count(String where, String branchList, SalesDetailReport salesDetailReport) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {

            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = " SELECT \n"
                + "	COUNT(sli.id)\n"
                + "FROM general.saleitem sli\n"
                + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesDetailReport.getEndDate()) + "')\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN general.userdata us ON (us.id = sl.userdata_id AND us.deleted = FALSE) \n"
                + "WHERE  sl.is_return=False AND sli.deleted=FALSE " + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + where;

        Object[] param = new Object[]{};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, SalesDetailReport salesDetailReport) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (sortField == null) {
            sortField = " brn.id, sli.processdate ";
            sortOrder = "DESC";
        }

        String whereBranch = "";
        if (!branchList.isEmpty()) {

            whereBranch += "  AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "          brn.id AS brnid,\n"
                + "          brn.name AS brnname, \n  "
                + "        sli.id AS sliid,\n"
                + "        sli.stock_id AS slistock_id,\n"
                + "        stck.name AS stckname,\n"
                + "        sli.processdate AS sliprocessdate,\n"
                + "        stck.barcode AS stckbarcode,\n"
                + "        stck.code AS stckcode,\n"
                + "        stck.centerproductcode AS stckcenterproductcode,\n"
                + "        sli.quantity AS sliquantity,\n"
                + "        sl.receipt_id AS slreceipt_id,\n"
                + "        rcp.receiptno AS rcpreceiptno,\n"
                + "        sl.invoice_id AS slinvoice_id,\n"
                + "        sl.saletype_id AS slsaletype_id,\n"
                + "        sl.discounttype_id AS sldiscounttype_id,\n"
                + "        sl.transactionno AS sltransactionno,\n"
                + "        inv.documentnumber AS invdocumentnumber,\n"
                + "        COALESCE(sli.unitprice,0) AS sliunitprice,\n"
                + "        COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalmoney,\n"
                + "        sli.currency_id AS slicurrency_id,\n"
                + "        sl.account_id AS slaccount_id,\n"
                + "        acc.name AS accname,\n"
                + "        acc.title AS acctitle,\n"
                + "        acc.is_employee AS accis_employee,\n"
                + "        gunt.sortname AS guntsortname,\n"
                + "        gunt.unitrounding AS guntunitsorting,\n"
                + "        (SELECT general.find_category(sli.stock_id, 1, brn.id)) AS category,\n"
                + "        stck.brand_id AS stckbrand_id,\n"
                + "        br.name AS brname,\n"
                + "        stck.supplier_id AS stcksupplier_id,\n"
                + "        acc1.name AS acc1name, \n"
                + "        stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "        cspp.name AS csppname,\n"
                + "        us.name AS usname,\n"
                + "        us.surname AS ussurname \n"
                + "FROM general.saleitem sli\n"
                + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesDetailReport.getEndDate()) + "')\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN general.userdata us ON (us.id = sl.userdata_id AND us.deleted = FALSE) \n"
                + "WHERE  sl.is_return=False AND sli.deleted=FALSE " + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + where + "\n"
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{};
        List<SalesDetailReport> result = getJdbcTemplate().query(sql, param, new SalesDetailReportMapper());
        return result;
    }

    @Override
    public List<SalesDetailReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param where
     * @return
     */
    @Override
    public int count(String where) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public List<SalesDetailReport> totals(String where, String branchList, SalesDetailReport salesDetailReport) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {

            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "	COUNT(sli.id) as sliid,\n"
                + "    sli.unit_id AS sliunit_id,\n"
                + "    sli.currency_id as slicurrency_id,\n"
                + "    sum(sli.quantity) as sliquantity,\n"
                + "    COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) *sli.exchangerate),0) as slitotalmoney,\n"
                + "    gunt.sortname as guntsortname\n"
                + "FROM general.saleitem sli\n"
                + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesDetailReport.getEndDate()) + "')\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN general.userdata us ON (us.id = sl.userdata_id AND us.deleted = FALSE) \n"
                + "WHERE  sl.is_return=False AND sli.deleted=FALSE " + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + where + "\n"
                + " GROUP BY  sli.unit_id , sli.currency_id , gunt.sortname";

        List<SalesDetailReport> result = getJdbcTemplate().query(sql,new SalesDetailReportMapper());
        return result;
    }

}
