/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.10.2018 08:15:58
 */
package com.mepsan.marwiz.general.report.purchasesalesreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.inventory.taxgroup.dao.TaxGroupMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PurchaseSalesReportDao extends JdbcDaoSupport implements IPurchaseSalesReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PurchaseSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf) {
        String sql = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (obj.isIsPurchase()) {
            where += " AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";
        } else {
            where += " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END) ";
        }

        sql = " SELECT * FROM general.rpt_purchasesalesreport(?, ?, ?, ?, ?, ? ,? ,?, ?, ?)";

        Object[] param = new Object[]{obj.getBeginDate(), obj.getEndDate(), sessionBean.getUser().getLastBranch().getId(), where, obj.isIsPurchase(), branchList, String.valueOf(first), String.valueOf(pageSize), 1, obj.getCostType()};
        try {
            return getJdbcTemplate().query(sql, param, new PurchaseSalesReportMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }

    }

    @Override
    public List<PurchaseSalesReport> count(String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String first = "";
        String page = "";

        String sql = "";
        Object[] param;
        if (obj.isIsPurchase()) {

            sql = " SELECT r_result FROM general.rpt_purchasesalesreport(?, ?, ?, ?, ?, ? ,? ,?, ?, ?)";

            param = new Object[]{obj.getBeginDate(), obj.getEndDate(), sessionBean.getUser().getLastBranch().getId(), where, obj.isIsPurchase(), branchList, first, page, 3, obj.getCostType()};

        } else {
            sql = " SELECT r_result FROM general.rpt_purchasesalesreport(?, ?, ?, ?, ?, ? ,? ,?, ?, ?)";
            param = new Object[]{obj.getBeginDate(), obj.getEndDate(), sessionBean.getUser().getLastBranch().getId(), where, obj.isIsPurchase(), branchList, first, page, 3, obj.getCostType()};

        }
        List<PurchaseSalesReport> list = getJdbcTemplate().query(sql, param, new PurchaseSalesReportMapper());

        return list;
    }

    @Override
    public String exportData(String where, PurchaseSalesReport obj, String branchList, int centralIngetrationInf) {
        String value = "";
        String sql = "";

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (obj.isIsPurchase()) {
            where += " AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN ''" + sd.format(obj.getBeginDate()) + "'' AND ''" + sd.format(obj.getEndDate()) + "'' THEN FALSE ELSE TRUE END) ";
        } else {
            where += " AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN ''" + sd.format(obj.getBeginDate()) + "'' AND ''" + sd.format(obj.getEndDate()) + "'' THEN FALSE ELSE TRUE END) ";
        }
        sql = " SELECT "
                + "r_brnid,\n"
                + "r_brnname,\n"
                + "r_stock_id,\n"
                + "r_stckcode,\n"
                + "r_stckname,\n"
                + "r_stckbarcode,\n"
                + "r_stckcenterproductcode,\n"
                + "r_stckunit_id,\n"
                + "r_guntsortname,\n"
                + "r_guntunitsorting,\n"
                + "r_taxrate,\n"
                + "r_currency_id,\n"
                + "r_quantity,\n"
                + "r_totalmoney,\n"
                + "r_totaltax,\n"
                + "r_totalprice,\n"
                + "r_totaldiscount,\n"
                + "r_avgpurchaseunitprice,\n"
                + "r_lastsaleprice,\n"
                + "r_lastpurchaseprice,\n"
                + "r_salestotalmoney,\n"
                + "r_avgsaleunitprice,\n"
                + "r_purchasecost,\n"
                + "r_cost,\n"
                + "r_category,\n"
                + "r_stckbrand_id,\n"
                + "r_brname,\n"
                + "r_supplier_id,\n"
                + "r_accname,\n"
                + "r_stckcentralsupplier_id,\n"
                + "r_csppname,\n"
                + "r_countresult\n"
                + " FROM general.rpt_purchasesalesreport('"
                + sd.format(obj.getBeginDate()) + "' , ' " + sd.format(obj.getEndDate()) + "' ," + sessionBean.getUser().getLastBranch().getId() + ", ' " + where + " ' ,"
                + "" + obj.isIsPurchase() + ", '" + branchList + "' , '" + value + " ' , ' " + value + " ' , " + 2 + " , " + obj.getCostType() + ")";

        return sql;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<TaxGroup> listOfTaxGroup(int type, List<BranchSetting> branchList) {
        String where = "";
        int count = 0;
        for (BranchSetting branchSetting : branchList) {
            if (branchSetting.isIsCentralIntegration()) {
                count++;
            }
        }

        if (!branchList.isEmpty()) {
            if (count >= 1 && count < branchList.size()) {
                where = " ";
            } else if (count == branchList.size()) {
                where = " AND txg.centertaxgroup_id IS NOT NULL ";
            } else if (count == 0) {
                where = " AND txg.is_otherbranch = TRUE ";
            }
        }

        String sql = "SELECT\n"
                + "    txg.id AS txgid,\n"
                + "    txg.name AS txgname,\n"
                + "    txg.rate AS txgrate,\n"
                + "    txg.type_id AS txgtype_id\n"
                + "FROM\n"
                + "    inventory.taxgroup txg  \n"
                + "WHERE \n"
                + "	txg.deleted = false AND txg.type_id = ?\n"
                + where + " \n"
                + "ORDER BY txg.rate ASC \n"
                + " \n";
        Object[] param = new Object[]{type};
        List<TaxGroup> result = getJdbcTemplate().query(sql, param, new TaxGroupMapper());
        return result;

    }

    @Override
    public List<PurchaseSalesReport> stockDetail(String where, PurchaseSalesReport obj, String branchList) {

        String whereBranch = "";
        String whereBranchInv = "";
        if (!branchList.isEmpty()) {
            whereBranch = "AND  sl.branch_id " + "IN( " + branchList + " )";
            whereBranchInv = "AND  inv.branch_id " + "IN( " + branchList + " )";

        }

        String sql = "";
        Object[] param;
        if (obj.isIsPurchase()) {

            sql = "SELECT\n"
                    + "  invi.stock_id AS r_stock_id,\n"
                    + "  stck.code AS r_stckcode,\n"
                    + "   stck.name AS r_stckname,\n"
                    + "   stck.barcode AS r_stckbarcode,\n"
                    + "   stck.centerproductcode AS r_stckcenterproductcode,\n"
                    + "   stck.unit_id AS r_stckunit_id ,\n"
                    + "   gunt.sortname AS r_guntsortname, \n"
                    + "   COALESCE(gunt.unitrounding,0) AS r_guntunitsorting, \n"
                    + "   invi.taxrate AS r_taxrate,\n"
                    + "   inv.currency_id AS r_currency_id,\n"
                    + "   COALESCE(invi.quantity) AS r_quantity,\n"
                    + "   invi.unitprice AS r_unitprice,\n"
                    + "   COALESCE(invi.totalmoney*COALESCE(invi.exchangerate,1)) AS r_totalmoney,\n"
                    + "   COALESCE(invi.totaltax*COALESCE(invi.exchangerate,1)) AS r_totaltax,\n"
                    + "   COALESCE(invi.totalprice*COALESCE(invi.exchangerate,1)) AS r_totalprice,\n"
                    + "   COALESCE((COALESCE(invi.discountprice,0) + COALESCE(invi.discountprice2,0)) *COALESCE(invi.exchangerate,1)) AS r_totaldiscount\n"
                    + "       \n"
                    + "FROM\n"
                    + "   finance.invoice inv \n"
                    + "   INNER JOIN finance.invoiceitem invi ON(invi.invoice_id=inv.id AND invi.deleted=False and invi.quantity>0)\n"
                    + "   INNER JOIN inventory.stock stck ON(stck.id = invi.stock_id )\n"
                    + "   INNER JOIN general.branch brn ON(brn.id = inv.branch_id )\n"
                    + "   INNER JOIN system.currency cr ON (cr.id = inv.currency_id)\n"
                    + "   LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                    + "   LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                    + "WHERE\n"
                    + "inv.deleted = FALSE AND inv.type_id  <> 27 AND inv.status_id <> 30 AND inv.is_purchase = TRUE AND inv.invoicedate BETWEEN  ? AND  ?   AND stck.id = ? \n" + where + whereBranchInv
                    + "ORDER BY \n"
                    + "stck.name";
            param = new Object[]{obj.getBeginDate(), obj.getEndDate(), obj.getStock().getId()};

        } else {
            sql = "  SELECT \n"
                    + "    sli.stock_id AS r_stock_id, \n"
                    + "    stck.code AS r_stckcode,\n"
                    + "    stck.name AS r_stckname,\n"
                    + "    stck.barcode AS r_stckbarcode,\n"
                    + "    stck.centerproductcode AS r_stckcenterproductcode,\n"
                    + "    stck.unit_id AS r_stckunit_id ,\n"
                    + "    gunt.sortname AS r_guntsortname, \n"
                    + "    COALESCE(gunt.unitrounding,0) AS r_guntunitsorting, \n"
                    + "    sli.taxrate AS r_taxrate,\n"
                    + "    sli.unitprice as r_unitprice,\n"
                    + "    sl.currency_id AS r_currency_id,\n"
                    + "    COALESCE(sli.quantity) AS r_quantity,\n"
                    + "    COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,1)) AS r_totalmoney,\n"
                    + "    COALESCE(sli.totaltax* COALESCE(sli.exchangerate,1)) AS r_totaltax,\n"
                    + "    COALESCE((sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,1)) AS r_totalprice,\n"
                    + "    COALESCE(COALESCE(sli.discountprice,0)* COALESCE(sli.exchangerate,1)) AS r_totaldiscount\n"
                    + "FROM \n"
                    + "   general.sale sl\n"
                    + "    INNER JOIN general.saleitem sli ON(sli.sale_id = sl.id AND sli.deleted = FALSE)\n"
                    + "    INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id)\n"
                    + "    INNER JOIN system.currency cr ON (cr.id = sl.currency_id)\n"
                    + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                    + "    LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                    + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                    + "WHERE\n"
                    + "   sl.is_return = FALSE AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                    + "   AND sl.deleted = FALSE AND sl.processdate BETWEEN  ? AND ? " + whereBranch + "  AND stck.id = ? \n" + where
                    + "ORDER BY \n"
                    + "   stck.name ASC  \n";

            param = new Object[]{obj.getBeginDate(), obj.getEndDate(), obj.getStock().getId()};

        }
        List<PurchaseSalesReport> result = getJdbcTemplate().query(sql, param, new PurchaseSalesReportMapper());
        return result;
    }

}
