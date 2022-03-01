/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.03.2018 01:28:53
 */
package com.mepsan.marwiz.general.report.salesreceiptreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SalesReceiptReportDao extends JdbcDaoSupport implements ISalesReceiptReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String subTotal, String branchList, SalesReport salesReport) {
        String fields = "";
        String order = "";
        String orderTotal = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (!subTotal.isEmpty()) {
            if (subTotal.equals("sl.processdate")) {
                subTotal = " date(sl.processdate) ";
                order = "sl.processdate,brn.id,brn.name";
                orderTotal = "brn.id,brn.name,sl.processdate ";
            } else {
                order = subTotal + " ,sl.processdate,brn.id,brn.name ";
                orderTotal = subTotal + " DESC ,brn.id,brn.name,sl.processdate ";
            }
            //Alt Toplam Var İse Alanları Toplayarak Çek
            fields = "SUM(CASE WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = FALSE THEN COALESCE(-sl.differenttotalmoney,0)\n"
                    + "      WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = TRUE THEN COALESCE(sl.differenttotalmoney,0)\n"
                    + "      ELSE sl.totalmoney END) OVER(PARTITION BY " + subTotal + " ) AS totalmoney,\n"
                    + "	COUNT(sl.id) OVER(PARTITION BY " + subTotal + " ) AS slidcount,";
        } else {
            order = "brn.id,brn.name,sl.processdate ";
            orderTotal = "brn.id,brn.name,sl.processdate ";
        }

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "    sl.id AS slid,\n"
                + "    brn.id AS brnid,\n"
                + "    brn.name AS brnname,\n"
                + "    sl.processdate AS slprocessdate,\n"
                + "    sl.account_id AS slaccount_id,\n"
                + "    acc.name AS accname,\n"
                + "    acc.title AS acctitle,\n"
                + "    acc.is_employee AS accis_employee,\n"
                + "    sl.receipt_id AS slreceipt_id,\n"
                + "    rcp.receiptno AS rcpreceiptno,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE COALESCE(sl.totaldiscount, 0) END AS sltotaldiscount,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE sl.totaltax END AS sltotaltax,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN sl.differenttotalmoney ELSE sl.totalmoney END AS sltotalmoney,\n"
                + "    sl.shift_id AS slshift_id,\n"
                + "    sl.shiftno AS slshiftno,\n"
                + "    sl.saletype_id AS slsaletype_id,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE sl.discounttype_id END AS sldiscounttype_id,\n"
                + "    typd.name AS typdname,\n"
                + "    sl.transactionno AS sltransactionno,\n"
                + "    sl.pointofsale_id AS slpointofsale_id,\n"
                + "    sl.posmacaddress AS slposmacaddress,\n"
                + "    pos.name AS posname,\n"
                + "    sl.userdata_id AS sluserdata_id,\n"
                + "    usr.name AS usrname,\n"
                + "    usr.surname AS usrsurname,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN sl.differenttotalmoney ELSE sl.totalprice END AS sltotalprice,\n"
                + "    sl.invoice_id AS slinvoice_id,\n"
                + "    sl.is_online AS slis_online,\n"
                + "    COALESCE(sl.discountprice,0) AS sldiscountprice,\n"
                + "    inv.documentnumber AS invdocumnetnumber,\n"
                + "    date(sl.processdate) AS saledate,\n"
                + fields
                + "    sl.currency_id AS slcurrency_id\n"
                + "FROM general.sale sl \n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesReport.getEndDate()) + "')\n"
                + "INNER JOIN general.account acc ON(acc.id = sl.account_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN system.type_dict typd ON (typd.type_id = sl.discounttype_id AND typd.language_id = ?)\n"
                + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + where + "\n"
                + "AND sl.deleted=False AND sl.is_return=False" + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) \n"
                + "ORDER BY " + order + " limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<SalesReport> result = getJdbcTemplate().query(sql, param, new SalesReceiptReportMapper());
        return result;
    }

    public String exportData(String where, String subTotal, String branchList, SalesReport salesReport) {
        String fields = "";
        String order = "";
        String orderTotal = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (!subTotal.isEmpty()) {
            if (subTotal.equals("sl.processdate")) {
                subTotal = " date(sl.processdate) ";
                order = "sl.processdate ";
                orderTotal = "sl.processdate ";
            } else {
                order = subTotal + " ,sl.processdate ";
                orderTotal = subTotal + " DESC ,sl.processdate ";
            }
            //Alt Toplam Var İse Alanları Toplayarak Çek
            fields = "SUM(CASE WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = FALSE THEN COALESCE(-sl.differenttotalmoney,0)\n"
                    + "      WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = TRUE THEN COALESCE(sl.differenttotalmoney,0)\n"
                    + "      ELSE sl.totalmoney END) OVER(PARTITION BY " + subTotal + " ) AS totalmoney,\n"
                    + "	COUNT(sl.id) OVER(PARTITION BY " + subTotal + " ) AS slidcount,";
        } else {
            order = " sl.processdate ";
            orderTotal = " sl.processdate ";
        }

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "    sl.id AS slid,\n"
                + "    brn.id AS brnid,\n"
                + "    brn.name AS brnname,\n"
                + "    sl.processdate AS slprocessdate,\n"
                + "    sl.account_id AS slaccount_id,\n"
                + "    acc.name AS accname,\n"
                + "    acc.title AS acctitle,\n"
                + "    acc.is_employee AS accis_employee,\n"
                + "    sl.receipt_id AS slreceipt_id,\n"
                + "    rcp.receiptno AS rcpreceiptno,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE COALESCE(sl.totaldiscount,0) END AS sltotaldiscount,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE COALESCE(sl.totaltax,0) END AS sltotaltax,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN sl.differenttotalmoney ELSE COALESCE(sl.totalmoney,0) END AS sltotalmoney,\n"
                + "    sl.shift_id AS slshift_id,\n"
                + "    sl.shiftno AS slshiftno,\n"
                + "    sl.saletype_id AS slsaletype_id,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 ELSE sl.discounttype_id END AS sldiscounttype_id,\n"
                + "    typd.name AS typdname,\n"
                + "    sl.transactionno AS sltransactionno,\n"
                + "    sl.pointofsale_id AS slpointofsale_id,\n"
                + "    sl.posmacaddress AS slposmacaddress,\n"
                + "    pos.name AS posname,\n"
                + "    sl.userdata_id AS sluserdata_id,\n"
                + "    usr.name AS usrname,\n"
                + "    usr.surname AS usrsurname,\n"
                + "    CASE WHEN sl.differentsale_id IS NOT NULL THEN sl.differenttotalmoney ELSE sl.totalprice END AS sltotalprice,\n"
                + "    sl.invoice_id AS slinvoice_id,\n"
                + "    sl.is_online AS slis_online,\n"
                + "    COALESCE(sl.discountprice,0) AS sldiscountprice,\n"
                + "    inv.documentnumber AS invdocumnetnumber,\n"
                + "    date(sl.processdate) AS saledate,\n"
                + fields
                + "    sl.currency_id AS slcurrency_id\n"
                + "FROM general.sale sl \n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesReport.getEndDate()) + "')\n"
                + "INNER JOIN general.account acc ON(acc.id = sl.account_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                + "LEFT JOIN system.type_dict typd ON (typd.type_id = sl.discounttype_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + where + "\n"
                + "AND sl.deleted=False AND sl.is_return=False \n" + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "ORDER BY " + order + "\n";

        return sql;

    }

    @Override
    public int count(String where, String subTotal, String branchList, SalesReport salesReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT COUNT(sl.id) AS slid\n"
                + "FROM \n"
                + "    general.sale sl \n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesReport.getEndDate()) + "')\n"
                + "INNER JOIN general.account acc ON(acc.id = sl.account_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "LEFT JOIN system.type_dict typd ON (typd.type_id = sl.discounttype_id AND typd.language_id = ?)\n"
                + where + "\n"
                + "AND sl.deleted = False AND sl.is_return=False" + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;

    }

    @Override
    public List<SalesReport> totals(String where, String branchList, SalesReport salesReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                + "COUNT(sl.id) AS slid,\n"
                + "COALESCE(SUM(CASE WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = FALSE THEN COALESCE(-sl.differenttotalmoney,0)\n"
                + "             WHEN sl.differentsale_id IS NOT NULL AND sl.is_differentdirection = TRUE THEN COALESCE(sl.differenttotalmoney,0)\n"
                + "             ELSE COALESCE(sl.totalmoney,0) END),0) AS sltotalmoney,\n"
                + "COALESCE(SUM(CASE WHEN sl.differentsale_id IS NOT NULL THEN 0\n"
                + "             ELSE COALESCE(sl.totaldiscount,0) END),0) AS sltotaldiscount,\n"
                + "SUM(CASE WHEN sl.differentsale_id IS NOT NULL THEN 0 WHEN sl.totaldiscount>0 and sl.totaldiscount IS NOT NULL THEN 1 ELSE 0 END) AS discountcount,\n"
                + "SUM(CASE WHEN sl.saletype_id = 81 THEN 1 ELSE 0 END) AS cardoperationcount,\n"
                + "sl.currency_id AS  slcurrency_id,\n"
                + "brn.id as brnid,\n"
                + "brn.name as brnname\n"
                + "FROM \n"
                + "    general.sale sl \n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesReport.getEndDate()) + "')\n"
                + "INNER JOIN general.account acc ON(acc.id = sl.account_id)\n"
                + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id)\n"
                + "LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = False)\n"
                + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "LEFT JOIN system.type_dict typd ON (typd.type_id = sl.discounttype_id AND typd.language_id = ?)\n"
                + where + "\n"
                + "AND sl.deleted = False AND sl.is_return=False " + whereBranch + "\n"
                + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "group by brn.id,brn.name,sl.currency_id";
        
        System.out.println("satış fiş total--"+sql);
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<SalesReport> result = getJdbcTemplate().query(sql, param, new SalesReceiptReportMapper());

        return result;
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SaleItem> findSaleItem(SalesReport salesReport) {
        String sql = "SELECT\n"
                + "                  sli.id AS sliid,\n"
                + "                  sli.processdate AS sliprocessdate,\n"
                + "                  sli.unit_id AS sliunit_id,\n"
                + "                  gunt.sortname AS guntsortname,\n"
                + "                  gunt.unitrounding AS guntunitsorting,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN 0 ELSE sli.quantity END AS sliquantity,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN 0 ELSE sli.unitprice END AS sliunitprice,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN 0 ELSE COALESCE(sli.discountprice,0) END AS slidiscountprice,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN 0 ELSE COALESCE(sli.totaltax,0) END AS slitotaltax,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN COALESCE(sli.differenttotalmoney,0) ELSE (sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)) END AS slitotalprice,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN COALESCE(sli.differenttotalmoney,0) ELSE (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) END AS slitotalmoney,\n"
                + "                  sli.currency_id AS slicurrency_id,\n"
                + "                  sli.stock_id AS slistock_id,\n"
                + "                  stck.name AS stckname,\n"
                + "                  stck.code AS stckcode,\n"
                + "                  stck.centerproductcode AS stckcenterproductcode,\n"
                + "                  stck.barcode AS stckbarcode,\n"
                + "                  sli.is_managerdiscount AS sliis_managerdiscount,\n"
                + "                  CASE WHEN sli.differentsaleitem_id IS NOT NULL THEN 0 ELSE sli.manageruserdata_id END AS slimanageruserdata_id,\n"
                + "                  us1.name AS us1name,\n"
                + "                  us1.surname AS us1surname\n"
                + "FROM general.saleitem sli\n"
                + "INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id = sli.unit_id)\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                + "LEFT JOIN general.userdata us1 ON(us1.id=sli.manageruserdata_id)\n"
                + "WHERE sli.deleted=False AND sli.sale_id=?\n"
                + "ORDER BY sli.processdate ASC";

        Object[] param = new Object[]{salesReport.getId()};
        List<SaleItem> result = getJdbcTemplate().query(sql, param, new SalesItemReportMapper());
        return result;
    }

    @Override
    public List<SalePayment> findSalePayment(SalesReport salesReport) {
        String sql = "SELECT\n"
                + "           slp.type_id AS slptype_id,\n"
                + "           typd.name AS typdname,\n"
                + "           SUM(slp.price) AS slpprice,\n"
                + "           slp.currency_id AS slpcurrency_id\n"
                + "FROM general.salepayment slp\n"
                + "INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                + "WHERE slp.sale_id=? AND slp.deleted=False\n"
                + "GROUP BY slp.type_id, typd.name, slp.currency_id";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), salesReport.getId()};
        List<SalePayment> result = getJdbcTemplate().query(sql, param, new SalesTypeReportMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
