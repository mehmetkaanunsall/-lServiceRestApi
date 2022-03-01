/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:41:06
 */
package com.mepsan.marwiz.general.report.salestypestockreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SalesTypeStockReportDao extends JdbcDaoSupport implements ISalesTypeStockReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(SalesTypeStockReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(SalesTypeStockReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesTypeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String sql = " ";
        Object[] param = null;

        String whereBranch = "";
        if (!whereBranchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + whereBranchList + " ) ";
        }

        switch (salesTypeStockReport.getType().getId()) {
            case 0:
                //Hepsi Seçili ise
                sql = "SELECT "
                          + "    brn.id as brnid,\n"
                          + "    brn.name as brnname,\n"
                          + "    COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))*sli.exchangerate),0) AS totalmoney,\n"
                          + "    sli.stock_id AS slistock_id,\n"
                          + "    stck.name AS stckname,\n"
                          + "    stck.code AS stckcode,\n"
                          + "    stck.centerproductcode AS stckcenterproductcode,\n"
                          + "    stck.barcode AS stckbarcode,\n"
                          + "    sl.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(sli.quantity),0) AS totalquantity,\n"
                          + "    gunt.sortname AS guntsortname,\n"
                          + "    gunt.unitrounding AS guntunitrounding\n"
                          + "FROM general.saleitem sli \n"
                          + "INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False) \n"
                          + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id)\n"
                          + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE \n"
                          + " sli.deleted=False " + whereBranch + " AND sl.is_return=False AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "GROUP BY sli.stock_id,stck.name, sl.currency_id, stck.code,stck.centerproductcode, stck.barcode, gunt.sortname, gunt.unitrounding,brn.id,brn.name\n"
                          + "ORDER BY brn.id,brn.name,SUM(sli.totalmoney*sli.exchangerate) DESC\n"
                          + " LIMIT " + pageSize + " OFFSET " + first;
                param = new Object[]{};
                break;
            //Ortak
            case -1:
                sql = "SELECT \n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT "
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate,\n"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE  sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount > 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.branch_id,tt.branchname \n"
                          + "ORDER BY  tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n"
                          + " LIMIT " + pageSize + " OFFSET " + first;
                param = new Object[]{};
                break;
            //Açık
            case -2:
                sql = "SELECT\n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT \n"
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate,\n"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount < 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding ,tt.branch_id,tt.branchname \n"
                          + "ORDER BY tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n"
                          + " LIMIT " + pageSize + " OFFSET " + first;
                param = new Object[]{};
                break;
            default:
                sql = "SELECT\n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT \n"
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate\n,"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.salepayment slp ON (slp.sale_id = sli.sale_id AND slp.type_id = ? AND slp.deleted = False) \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE  sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount = 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding  ,tt.branch_id,tt.branchname\n"
                          + "ORDER BY tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n"
                          + " LIMIT " + pageSize + " OFFSET " + first;
                param = new Object[]{salesTypeStockReport.getType().getId()};
                break;

        }

        List<SalesTypeStockReport> result = getJdbcTemplate().query(sql, param, new SalesTypeStockReportMapper());
        return result;
    }

    @Override
    public List<SalesTypeStockReport> totals(String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String sql = " ";
        Object[] param = null;
        String whereBranch = "";
        if (!whereBranchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + whereBranchList + " ) ";
        }

        switch (salesTypeStockReport.getType().getId()) {
            case 0:
                //Hepsi Seçili ise
                sql = "SELECT "
                          + "  t.brnid as brnid,\n"
                          + "  t.brnname as brnname,\n"
                          + "COUNT(t.stock_id) AS slistock_id,\n"
                          + "COALESCE(SUM(t.totalmoney),0) totalmoney,\n"
                          + "t.slcurrency_id AS slcurrency_id\n"
                          + "FROM\n"
                          + "(\n"
                          + "SELECT "
                          + " brn.id as brnid,\n"
                          + "   brn.name as brnname,\n"
                          + "   sli.stock_id,\n"
                          + "   SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))*sli.exchangerate) AS totalmoney,\n"
                          + "   sl.currency_id AS slcurrency_id\n"
                          + "\n"
                          + "FROM general.saleitem sli \n"
                          + "INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id)\n"
                          + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False  " + whereBranch + "  AND sl.is_return=False AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END) \n"
                          + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "GROUP BY sli.stock_id,stck.name, sl.currency_id, stck.code,stck.centerproductcode, stck.barcode, gunt.sortname, gunt.unitrounding,brn.id, brn.name\n"
                          + ") t\n"
                          + "GROUP BY t.brnid,t.brnname,t.slcurrency_id";
                param = new Object[]{};
                break;
            //Ortak
            case -1:
                sql = "SELECT\n"
                          + "      COUNT(t.ttstock_id) AS slistock_id,\n"
                          + "      COALESCE(SUM(t.totalmoney),0) AS totalmoney,\n"
                          + "      t.ttcurrency_id AS slcurrency_id,\n"
                          + "      t.brnid as brnid,\n"
                          + "      t.brnname as brnname\n"
                          + "      FROM\n"
                          + "      (\n"
                          + "      \n"
                          + "          SELECT\n"
                          + "             tt.stock_id AS ttstock_id,\n"
                          + "             COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "             tt.currency_id AS ttcurrency_id,\n"
                          + "             tt.brnname as brnname,\n"
                          + "             tt.brnid as brnid\n"
                          + "          FROM\n"
                          + "          (    \n"
                          + "           SELECT \n"
                          + "                brn.id as brnid,\n"
                          + "                brn.name as brnname,\n"
                          + "                sli.stock_id,\n"
                          + "                CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "                   COALESCE(sli.differenttotalmoney,0)\n"
                          + "                ELSE\n"
                          + "                   (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "                stck.name,\n"
                          + "                stck.code,\n"
                          + "                stck.centerproductcode,\n"
                          + "                stck.barcode,\n"
                          + "                sli.exchangerate,\n"
                          + "                sl.currency_id,\n"
                          + "                sli.quantity,\n"
                          + "                gunt.sortname,\n"
                          + "                gunt.unitrounding,\n"
                          + "               (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "           FROM \n"
                          + "               general.saleitem sli   \n"
                          + "               INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "               INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "               LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "               INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "               LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False " + whereBranch + "  AND sl.is_return=False\n"
                          + "               AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "               AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "               AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "           ) tt\n"
                          + "           WHERE\n"
                          + "           tt.paymentcount > 1\n"
                          + "           GROUP BY tt.stock_id, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.brnid,tt.brnname\n"
                          + "      ) t\n"
                          + "      GROUP BY t.brnid,t.brnname,t.ttcurrency_id";
                param = new Object[]{};
                break;
            //Açık
            case -2:
                sql = "SELECT\n"
                          + "       COUNT(t.ttstock_id) AS slistock_id,\n"
                          + "       COALESCE(SUM(t.totalmoney),0) AS totalmoney,\n"
                          + "       t.ttcurrency_id AS slcurrency_id,\n"
                          + "       t.brnid as brnid,\n"
                          + "       t.brnname as brnname\n"
                          + "       FROM\n"
                          + "       (\n"
                          + "           SELECT\n"
                          + "              tt.stock_id AS ttstock_id,\n"
                          + "              COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "              tt.currency_id AS ttcurrency_id,\n"
                          + "              tt.brnid as brnid,\n"
                          + "              tt.brnname as brnname\n"
                          + "           FROM\n"
                          + "           (    \n"
                          + "            SELECT \n"
                          + "            	brn.id as brnid,\n"
                          + "                brn.name as brnname,\n"
                          + "                sli.stock_id,\n"
                          + "                CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "                   COALESCE(sli.differenttotalmoney,0)\n"
                          + "                ELSE\n"
                          + "                   (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "                stck.name,\n"
                          + "                stck.code,\n"
                          + "                stck.centerproductcode,\n"
                          + "                stck.barcode,\n"
                          + "                sli.exchangerate,\n"
                          + "                sl.currency_id,\n"
                          + "                sli.quantity,\n"
                          + "                gunt.sortname,\n"
                          + "                gunt.unitrounding,\n"
                          + "                (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "            FROM \n"
                          + "                general.saleitem sli \n"
                          + "                INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "                INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "                LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "                INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "                LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False  " + whereBranch + " AND sl.is_return=False\n"
                          + "                AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "                AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "               AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "           ) tt\n"
                          + "           WHERE\n"
                          + "           tt.paymentcount < 1\n"
                          + "           GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding ,tt.brnid, tt.brnname\n"
                          + "       ) t\n"
                          + "       GROUP BY t.brnid,t.brnname,t.ttcurrency_id";
                param = new Object[]{};
                break;
            default:
                sql = "SELECT\n"
                          + "       COUNT(t.ttstock_id) AS slistock_id,\n"
                          + "       COALESCE(SUM(t.totalmoney),0) AS totalmoney,\n"
                          + "       t.ttcurrency_id AS slcurrency_id,\n"
                          + "       t.brnid as brnid,\n"
                          + "       t.brnname as brnname\n"
                          + "       FROM\n"
                          + "       (\n"
                          + "           SELECT\n"
                          + "              tt.stock_id AS ttstock_id,\n"
                          + "              COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "              tt.currency_id AS ttcurrency_id,\n"
                          + "              tt.brnid as brnid,\n"
                          + "              tt.brnname as brnname\n"
                          + "           FROM\n"
                          + "           (    \n"
                          + "            SELECT \n"
                          + "            	brn.id as brnid,\n"
                          + "                brn.name as brnname,\n"
                          + "                sli.stock_id,\n"
                          + "                CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "                   COALESCE(sli.differenttotalmoney,0)\n"
                          + "                ELSE\n"
                          + "                   (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "                stck.name,\n"
                          + "                stck.code,\n"
                          + "                stck.centerproductcode,\n"
                          + "                stck.barcode,\n"
                          + "                sli.exchangerate,\n"
                          + "                sl.currency_id,\n"
                          + "                sli.quantity,\n"
                          + "                gunt.sortname,\n"
                          + "                gunt.unitrounding,\n"
                          + "                (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "            FROM \n"
                          + "                general.saleitem sli  \n"
                          + "                INNER JOIN general.salepayment slp ON (slp.sale_id = sli.sale_id AND slp.type_id = ? AND slp.deleted = False) \n"
                          + "                INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "                INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "                LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "                INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id)\n"
                          + "                LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False " + whereBranch + "  AND sl.is_return=False\n"
                          + "                AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "               AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "           ) tt\n"
                          + "           WHERE\n"
                          + "             tt.paymentcount = 1\n"
                          + "           GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.brnid ,tt.brnname\n"
                          + "       ) t\n"
                          + "       GROUP BY t.brnid,t.brnname,t.ttcurrency_id";
                param = new Object[]{salesTypeStockReport.getType().getId()};
                break;
        }

        List<SalesTypeStockReport> result = getJdbcTemplate().query(sql, param, new SalesTypeStockReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, SalesTypeStockReport salesTypeStockReport, String whereBranchList) {
        String sql = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!whereBranchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + whereBranchList + " ) ";
        }

        switch (salesTypeStockReport.getType().getId()) {
            case 0:
                //Hepsi Seçili ise
                sql = "SELECT \n"
                          + "    brn.id as brnid,\n"
                          + "    brn.name as brnname,\n"
                          + "    COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))*sli.exchangerate),0) AS totalmoney,\n"
                          + "    sli.stock_id AS slistock_id,\n"
                          + "    stck.name AS stckname,\n"
                          + "    stck.code AS stckcode,\n"
                          + "    stck.centerproductcode AS stckcenterproductcode,\n"
                          + "    stck.barcode AS stckbarcode,\n"
                          + "    sl.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(sli.quantity),0) AS totalquantity,\n"
                          + "    gunt.sortname AS guntsortname,\n"
                          + "    gunt.unitrounding AS guntunitrounding\n"
                          + "FROM general.saleitem sli \n"
                          + "INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id)\n"
                          + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False  " + whereBranch + "  AND sl.is_return=False AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + "GROUP BY sli.stock_id,stck.name, sl.currency_id, stck.code,stck.centerproductcode, stck.barcode, gunt.sortname, gunt.unitrounding,brn.id, brn.name\n"
                          + "ORDER BY brn.id,brn.name,SUM(sli.totalmoney*sli.exchangerate) DESC\n";
                break;
            //Ortak
            case -1:
                sql = "SELECT \n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT "
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate,\n"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE  sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount > 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.branch_id,tt.branchname \n"
                          + "ORDER BY tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n";
                break;
            //Açık
            case -2:
                sql = "SELECT \n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT "
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate,\n"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE  sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount < 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.branch_id,tt.branchname \n"
                          + "ORDER BY tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n";
                break;
            default:
                sql = "SELECT\n"
                          + "    tt.branch_id AS brnid,\n"
                          + "    tt.branchname AS brnname,\n"
                          + "    tt.stock_id AS slistock_id,\n"
                          + "    tt.name AS stckname,\n"
                          + "    tt.code AS stckcode,\n"
                          + "    tt.centerproductcode AS stckcenterproductcode,\n"
                          + "    tt.barcode AS stckbarcode,\n"
                          + "    COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS totalmoney,\n"
                          + "    tt.currency_id AS slcurrency_id,\n"
                          + "    COALESCE(SUM(tt.quantity),0) AS totalquantity,\n"
                          + "    tt.sortname AS guntsortname,\n"
                          + "    tt.unitrounding AS guntunitrounding\n"
                          + "FROM\n"
                          + "(    \n"
                          + "  SELECT \n"
                          + "      brn.id as branch_id,\n"
                          + "      brn.name as branchname,\n"
                          + "      sli.stock_id,\n"
                          + "      CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN\n"
                          + "           COALESCE(sli.differenttotalmoney,0)\n"
                          + "      ELSE\n"
                          + "           (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS totalmoney,\n"
                          + "      stck.name,\n"
                          + "      stck.code,\n"
                          + "      stck.centerproductcode,\n"
                          + "      stck.barcode,\n"
                          + "      sli.exchangerate\n,"
                          + "      sl.currency_id,\n"
                          + "      sli.quantity,\n"
                          + "      gunt.sortname,\n"
                          + "      gunt.unitrounding,\n"
                          + "      (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                          + "  FROM \n"
                          + "      general.saleitem sli   \n"
                          + "      INNER JOIN general.salepayment slp ON (slp.sale_id = sli.sale_id AND slp.type_id = " + salesTypeStockReport.getType().getId() + " AND slp.deleted = False) \n"
                          + "      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                          + "      INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                          + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                          + "      INNER JOIN inventory.stock stck ON (stck.id = sli.stock_id) \n"
                          + "      LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                          + "WHERE sli.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                          + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                          + "      AND sl.processdate BETWEEN '" + sd.format(salesTypeStockReport.getBeginDate()) + "' AND '" + sd.format(salesTypeStockReport.getEndDate()) + "' \n"
                          + ") tt\n"
                          + "WHERE\n"
                          + "	tt.paymentcount = 1\n"
                          + "GROUP BY tt.stock_id, tt.name, tt.currency_id, tt.code, tt.centerproductcode, tt.barcode, tt.sortname, tt.unitrounding,tt.branch_id,tt.branchname \n"
                          + "ORDER BY tt.branch_id,tt.branchname,SUM(tt.totalmoney*tt.exchangerate) DESC\n";
                break;
        }
        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
