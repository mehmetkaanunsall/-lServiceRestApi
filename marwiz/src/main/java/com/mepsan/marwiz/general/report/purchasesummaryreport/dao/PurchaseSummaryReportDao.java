/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.06.2018 02:12:12
 */
package com.mepsan.marwiz.general.report.purchasesummaryreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PurchaseSummaryReportDao extends JdbcDaoSupport implements IPurchaseSummaryReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PurchaseSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                + "    ROW_NUMBER () OVER (ORDER BY invi.stock_id) AS id,\n"
                + "    invi.stock_id AS invistock_id,\n"
                + "    brn.name AS brnname,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    invi.unit_id AS stckunit_id,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitsorting,\n"
                + "    COALESCE(invi.unitprice,0) AS inviunitprice,\n"
                + "    SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS invitotalmoney,\n"
                + "    invi.currency_id AS invicurrency,\n"
                + "    SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS inviquantity,\n"
                + "    COALESCE(SUM(SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalmoneybystock,\n"
                + "    COALESCE(SUM(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalcountbystock,\n"
                + "    (SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                + "    stck.brand_id AS stckbrand_id,\n"
                + "    br.name AS brname,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    si.turnoverpremium as siturnoverpremium,\n"
                + "    COALESCE(invi.taxrate,0) AS invitaxrate,\n"
                + "    COALESCE(invi.unitprice,0)*(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)))*(si.turnoverpremium/100) AS premiumamount\n"
                + "FROM finance.invoiceitem invi \n"
                + "	 INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False\n"
                + "    AND inv.is_purchase = True " + whereBranch + "\n"
                + where + "\n"
                + "GROUP BY \n"
                + "	inv.branch_id, brn.name, invi.stock_id, stck.name, stck.barcode,\n"
                + "    invi.unit_id, gunt.sortname, gunt.unitrounding,\n"
                + "    invi.unitprice, invi.currency_id,stck.code, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name,\n"
                + "    si.turnoverpremium, invi.taxrate \n"
                + "ORDER BY invi.stock_id\n"
                + " limit " + pageSize + " offset " + first;
        
        List<PurchaseSummaryReport> result = getJdbcTemplate().query(sql, new PurchaseSummaryReportMapper());
        return result;

    }

    @Override
    public int count(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT COUNT(u.inviid) FROM\n"
                + "(SELECT\n"
                + "   COUNT(invi.id) AS inviid\n"
                + "FROM finance.invoiceitem invi \n"
                + "	 INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False\n"
                + "    AND inv.is_purchase = True " + whereBranch + "\n"
                + where + "\n"
                + "GROUP BY \n"
                + "	inv.branch_id, invi.stock_id, stck.name, stck.barcode, invi.unit_id,\n"
                + "    gunt.sortname, gunt.unitrounding, invi.unitprice,\n"
                + "    invi.currency_id,stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name,\n"
                + "     si.turnoverpremium, invi.taxrate ) u";
        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                + "    ROW_NUMBER () OVER (ORDER BY invi.stock_id) AS id,\n"
                + "    invi.stock_id AS invistock_id,\n"
                + "    brn.name AS brnname,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    invi.unit_id AS stckunit_id,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitsorting,\n"
                + "    COALESCE(invi.unitprice,0) AS inviunitprice,\n"
                + "    SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS invitotalmoney,\n"
                + "    invi.currency_id AS invicurrency,\n"
                + "    SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS inviquantity,\n"
                + "    COALESCE(SUM(SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalmoneybystock,\n"
                + "    COALESCE(SUM(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalcountbystock,\n"
                + "    (SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                + "    stck.brand_id AS stckbrand_id,\n"
                + "    br.name AS brname,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    si.turnoverpremium as siturnoverpremium,\n"
                + "    COALESCE(invi.taxrate,0) AS invitaxrate,\n"
                + "    COALESCE(invi.unitprice,0)*(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)))*(si.turnoverpremium/100) AS premiumamount\n"
                + "FROM finance.invoiceitem invi \n"
                + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False \n"
                + "    AND inv.is_purchase = True " + whereBranch + "\n"
                + where + "\n"
                + "GROUP BY \n"
                + "	inv.branch_id, brn.name, invi.stock_id, stck.name, stck.barcode,\n"
                + "    invi.unit_id, gunt.sortname, gunt.unitrounding,\n"
                + "    invi.unitprice, invi.currency_id, stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name,\n"
                + "    si.turnoverpremium, invi.taxrate \n"
                + "ORDER BY invi.stock_id";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<PurchaseSummaryReport> findAllDetail(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSummaryReport obj, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                + "    inv.invoicedate AS invinvoicedate,\n"
                + "    inv.status_id AS invstatus_id,\n"
                + "    inv.type_id AS invtype_id,\n"
                + "    invi.stock_id AS invistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    invi.unit_id AS stckunit_id,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitsorting,\n"
                + "    invi.unitprice AS inviunitprice,\n"
                + "    invi.totalmoney AS invitotalmoney,\n"
                + "    invi.currency_id AS invicurrency,\n"
                + "    invi.quantity AS inviquantity,\n"
                + "    stck.brand_id AS stckbrand_id,\n"
                + "    br.name AS brname,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    si.turnoverpremium as siturnoverpremium,\n"
                + "    invi.taxrate AS invitaxrate\n"
                + "FROM finance.invoiceitem invi \n"
                + "    INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False \n"
                + "   AND invi.unit_id = ? AND invi.currency_id = ? AND invi.stock_id=? AND invi.unitprice = ?\n"
                + "   AND inv.is_purchase = True " + whereBranch + "\n"
                + where + "\n"
                + "ORDER BY inv.invoicedate\n"
                + " limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{obj.getStock().getUnit().getId(), obj.getCurrency().getId(), obj.getStock().getId(), obj.getUnitPriceWithTax()};
                
        List<PurchaseSummaryReport> result = getJdbcTemplate().query(sql, param, new PurchaseSummaryReportMapper());
        return result;
    }

    @Override
    public int countDetail(String where, PurchaseSummaryReport obj, String branchList) {

        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT\n"
                + "    COUNT(invi.stock_id)\n"
                + "FROM finance.invoiceitem invi \n"
                + "	INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False \n"
                + "   AND invi.unit_id = ? AND invi.currency_id = ? AND invi.stock_id=? AND invi.unitprice = ?\n"
                + "   AND inv.is_purchase = True " + whereBranch + "\n"
                + where;

        Object[] param = new Object[]{obj.getStock().getUnit().getId(), obj.getCurrency().getId(), obj.getStock().getId(), obj.getUnitPriceWithTax()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<PurchaseSummaryReport> totals(String where, String branchList) {
        String whereBranch = "";

        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT COUNT(u.inviid) AS id,\n"
                + "       u.brnname AS brnname,\n"
                + "       SUM(u.inviquantity) AS inviquantity,\n"
                + "       SUM(u.invitotalmoney) AS invitotalmoney,\n"
                + "       SUM(COALESCE(u.premiumamount,0)) AS premiumamount,\n"
                + "       SUM(u.totalmoneybystock) AS totalmoneybystock,\n"
                + "       SUM(u.totalcountbystock) AS totalcountbystock,\n"
                + "       u.invicurrency_id AS invicurrency\n"
                + "       \n"
                + " FROM\n"
                + "(SELECT\n"
                + "   COUNT(invi.id) AS inviid,\n"
                + "   inv.branch_id as invbranch_id,\n"
                + "   brn.name AS brnname,\n"
                + "   invi.currency_id AS invicurrency_id,\n"
                + "   SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS inviquantity,\n"
                + "   SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)) AS invitotalmoney,\n"
                + "   COALESCE(invi.unitprice,0)*(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END)))*(COALESCE(si.turnoverpremium,0)/100) AS premiumamount,\n"
                + "   COALESCE(SUM(SUM(invi.totalmoney*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalmoneybystock,\n"
                + "   COALESCE(SUM(SUM(invi.quantity*(CASE WHEN inv.status_id = 30 OR inv.type_id = 27 THEN -1 ELSE 1 END))) OVER(PARTITION BY inv.branch_id, invi.stock_id, invi.currency_id),0) as totalcountbystock\n"
                + "FROM finance.invoiceitem invi \n"
                + "	 INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                + "    INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "    INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id=stck.id AND si.deleted=FALSE AND si.branch_id=inv.branch_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE\n"
                + "	invi.deleted = False\n"
                + "    AND inv.is_purchase = True " + whereBranch + "\n"
                + where + "\n"
                + "GROUP BY \n"
                + "	inv.branch_id, brn.name, invi.stock_id, stck.name, stck.barcode,\n"
                + "    invi.unit_id, gunt.sortname, gunt.unitrounding,\n"
                + "    invi.unitprice, invi.currency_id,stck.code, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name,\n"
                + "    si.turnoverpremium, invi.taxrate \n"
                + "     ) u GROUP BY u.invbranch_id, u.brnname, u.invicurrency_id";

        List<PurchaseSummaryReport> result = getJdbcTemplate().query(sql, new PurchaseSummaryReportMapper());
        return result;
    }

}
