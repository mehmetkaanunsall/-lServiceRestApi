/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 10:30:02
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarketShiftReportDetailDao extends JdbcDaoSupport implements IMarketShiftReportDetailDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Sales obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Sales obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Sales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift) {
        String sql = "SELECT\n"
                  + "        sl.id AS slid,\n"
                  + "        sl.processdate AS slprocessdate,\n"
                  + "        sl.pointofsale_id AS slpointofsale_id,\n"
                  + "        pos.name AS posname,\n"
                  + "        sl.posmacaddress AS slposmacaddress,\n"
                  + "        sl.account_id AS slaccount_id,\n"
                  + "        acc.name AS accname,\n"
                  + "        acc.title AS acctitle,\n"
                  + "        acc.is_employee AS accis_employee,\n"
                  + "        sl.userdata_id sluserdata_id,\n"
                  + "        us.name AS usname,\n"
                  + "        us.surname AS ussurname,\n"
                  + "        COALESCE(sl.totalprice,0) AS sltotalprice,\n"
                  + "        COALESCE(sl.totalmoney,0) AS sltotalmoney,\n"
                  + "        COALESCE(sl.totaldiscount,0) AS sltotaldiscount,\n"
                  + "        COALESCE(sl.totaltax,0) AS sltotaltax,\n"
                  + "        COALESCE(sl.discountprice,0) AS sldiscountprice,\n"
                  + "        sl.currency_id AS slcurrency_id,\n"
                  + "        sl.is_return AS slisreturn,\n"
                  + "        sl.invoice_id AS slinvoice_id,\n"
                  + "        inv.documentnumber AS invdocumentnumber,\n"
                  + "        sl.receipt_id AS slreceipt_id,\n"
                  + "        rcp.receiptno AS rcpreceiptno\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id=sl.pointofsale_id)\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "LEFT JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=?\n"
                  + "AND sl.branch_id=? AND us.type_id = 2\n"
                  + where + "\n"
                  + "ORDER BY sl.processdate ASC, sl.id ASC\n"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        List<Sales> result = getJdbcTemplate().query(sql, param, new MarketShiftReportDetailMapper());
        return result;
    }

    @Override
    public String exportData(Shift shift) {
        String sql = "SELECT\n"
                  + "        sl.id AS slid,\n"
                  + "        sl.processdate AS slprocessdate,\n"
                  + "        sl.pointofsale_id AS slpointofsale_id,\n"
                  + "        pos.name AS posname,\n"
                  + "        sl.posmacaddress AS slposmacaddress,\n"
                  + "        sl.account_id AS slaccount_id,\n"
                  + "        acc.name AS accname,\n"
                  + "        acc.title AS acctitle,\n"
                  + "        acc.is_employee AS accis_employee,\n"
                  + "        sl.userdata_id sluserdata_id,\n"
                  + "        us.name AS usname,\n"
                  + "        us.surname AS ussurname,\n"
                  + "        COALESCE(sl.totalprice,0) AS sltotalprice,\n"
                  + "        COALESCE(sl.totalmoney,0) AS sltotalmoney,\n"
                  + "        COALESCE(sl.totaldiscount,0) AS sltotaldiscount,\n"
                  + "        COALESCE(sl.totaltax,0) AS sltotaltax,\n"
                  + "        COALESCE(sl.discountprice,0) AS sldiscountprice,\n"
                  + "        sl.currency_id AS slcurrency_id,\n"
                  + "        sl.is_return AS slisreturn,\n"
                  + "        sl.invoice_id AS slinvoice_id,\n"
                  + "        inv.documentnumber AS invdocumentnumber,\n"
                  + "        sl.receipt_id AS slreceipt_id,\n"
                  + "        rcp.receiptno AS rcpreceiptno\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id=sl.pointofsale_id)\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "LEFT JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=" + shift.getId() + "\n"
                  + "AND us.type_id = 2\n"
                  + "AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + "\n"
                  + "ORDER BY sl.processdate ASC , sl.id ASC";

        return sql;
    }

    @Override
    public int count(String where, Shift shift) {
        String sql = "SELECT \n"
                  + "	COUNT(sl.id) AS slid \n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id=sl.pointofsale_id)\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "LEFT JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=? AND us.type_id = 2\n"
                  + "AND sl.branch_id=?\n"
                  + where;

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<SaleItem> find(Sales sales) {
        String sql = "SELECT\n"
                  + "                  sli.id AS sliid,\n"
                  + "                  sli.processdate AS sliprocessdate,\n"
                  + "                  sli.unit_id AS sliunit_id,\n"
                  + "                  gunt.sortname AS guntsortname,\n"
                  + "                  gunt.unitrounding AS guntunitsorting,\n"
                  + "                  sli.quantity AS sliquantity,\n"
                  + "                  sli.unitprice AS sliunitprice,\n"
                  + "                 (sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)) AS slitotalprice,\n"
                  + "                 (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) AS slitotalmoney,\n"
                  + "                  sli.currency_id AS slicurrency_id,\n"
                  + "                  sli.stock_id AS slistock_id,\n"
                  + "                  stck.name AS stckname,\n"
                  + "                  stck.barcode AS stckbarcode,\n"
                  + "                  stck.code AS stckcode,\n"
                  + "                  stck.centerproductcode AS stckcenterproductcode,\n"
                  + "                  sli.is_managerdiscount AS sliis_managerdiscount,\n"
                  + "                  sli.manageruserdata_id AS slimanageruserdata_id,\n"
                  + "                  us1.name AS us1name,\n"
                  + "                  us1.surname AS us1surname\n"
                  + "FROM general.saleitem sli\n"
                  + "INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id = sli.unit_id)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.userdata us1 ON(us1.id=sli.manageruserdata_id)\n"
                  + "WHERE sli.deleted=False AND sli.sale_id=?\n"
                  + "ORDER BY sli.processdate ASC";

        Object[] param = new Object[]{sales.getId()};
        List<SaleItem> result = getJdbcTemplate().query(sql, param, new MarketShiftSaleItemMapper());
        return result;
    }

    @Override
    public List<SalePayment> listOfSaleType(Sales sales) {
        String sql = "SELECT\n"
                  + "           slp.type_id AS slptype_id,\n"
                  + "           typd.name AS typdname,\n"
                  + "           SUM(slp.price) AS slpprice,\n"
                  + "           slp.currency_id AS slpcurrency_id\n"
                  + "FROM general.salepayment slp\n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                  + "WHERE slp.sale_id=? AND slp.deleted=False\n"
                  + "GROUP BY slp.type_id, typd.name, slp.currency_id";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sales.getId()};
        List<SalePayment> result = getJdbcTemplate().query(sql, param, new MarketShiftSalePaymentMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<SalePayment> totals(String where, Shift shift) {

        String sql = "SELECT \n"
                  + "    sl.currency_id AS slcurrency_id,\n"
                  + "    COALESCE(SUM(case when sl.is_return = true then -(slp.price * slp.exchangerate) else (slp.price * slp.exchangerate) END),0)::NUMERIC(18,4) AS slpprice,\n"
                  + "    slp.type_id AS slptype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    COALESCE((SELECT COUNT(sl1.id) FROM general.sale sl1 LEFT JOIN general.userdata us ON(us.id=sl1.userdata_id) WHERE sl1.deleted=FALSE AND sl1.shift_id=? AND sl1.branch_id=? AND us.type_id = 2), 0) AS slid\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id=sl.pointofsale_id)\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "LEFT JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN general.salepayment slp ON(slp.sale_id = sl.id AND slp.deleted=FALSE)\n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=?\n"
                  + "AND sl.branch_id=? AND us.type_id = 2\n"
                  + where + "\n"
                  + "GROUP BY sl.currency_id, slp.type_id, typd.name";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getLanguage().getId(), shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        List<SalePayment> result = getJdbcTemplate().query(sql, param, new MarketShiftReportSalePaymentMapper());
        return result;
    }

    @Override
    public List<SaleItem> findStockDetailList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Shift shift) {
        if (sortField == null) {
            sortField = " sli.processdate ";
            sortOrder = "DESC";
        }

        String sql = "SELECT\n"
                  + "                              sli.id AS sliid,\n"
                  + "                              sli.processdate AS sliprocessdate,\n"
                  + "                              sli.unit_id AS sliunit_id,\n"
                  + "                              gunt.sortname AS guntsortname,\n"
                  + "                              gunt.unitrounding AS guntunitsorting,\n"
                  + "                              sli.quantity AS sliquantity,\n"
                  + "                              sli.unitprice AS sliunitprice,\n"
                  + "                             (sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)) AS slitotalprice,\n"
                  + "                             (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))::NUMERIC(18,4) AS slitotalmoney,\n"
                  + "                              sli.currency_id AS slicurrency_id,\n"
                  + "                              sli.stock_id AS slistock_id,\n"
                  + "                              stck.name AS stckname,\n"
                  + "                              stck.barcode AS stckbarcode,\n"
                  + "                              stck.code AS stckcode,\n"
                  + "                              stck.centerproductcode AS stckcenterproductcode,\n"
                  + "                              sli.is_managerdiscount AS sliis_managerdiscount,\n"
                  + "                              sli.manageruserdata_id AS slimanageruserdata_id,\n"
                  + "                              (SELECT general.find_category(sli.stock_id, 1,brn.id)) AS category,\n"
                  + "                              stck.supplier_id AS stcksupplier_id,\n"
                  + "                              acc1.name AS acc1name, \n"
                  + "                              stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "                              cspp.name AS csppname,\n"
                  + "                              acc.name AS accname,\n"
                  + "                              acc.title AS acctitle,\n"
                  + "                              acc.is_employee AS accis_employee,\n"
                  + "                              inv.documentnumber as invdocumentnumber,\n"
                  + "                              sl.receipt_id AS slreceipt_id,\n"
                  + "                              rcp.receiptno AS receiptno,\n"
                  + "                              br.id AS brid,\n"
                  + "                              br.name AS brname,\n"
                  + "                              us1.name AS us1name,\n"
                  + "                              us1.surname AS us1surname,\n"
                  + "                              sl.is_return AS slisreturn\n"
                  + "            FROM general.saleitem sli\n"
                  + "            INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE)\n"
                  + "            INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "            LEFT JOIN general.unit gunt ON(gunt.id = sli.unit_id)\n"
                  + "            INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id AND stck.deleted=false)\n"
                  + "            LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "            LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "            LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "            LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "            LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                  + "            LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "            INNER JOIN general.userdata us ON(us.id = sl.userdata_id)\n"
                  + "            LEFT JOIN general.userdata us1 ON(us1.id=sli.manageruserdata_id)\n"
                  + "            WHERE sli.deleted=False AND sl.shift_id=? AND us.type_id = 2\n"
                  + "             AND sl.branch_id=? \n"
                  + "            ORDER BY " + sortField + ", sli.id " + sortOrder + "\n"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};
        List<SaleItem> result = getJdbcTemplate().query(sql, param, new MarketShiftSaleItemMapper());
        return result;
    }

    @Override
    public List<SalePayment> totalsStockDetailList(String where, Shift shift) {
        String sql = "SELECT \n"
                  + "    sl.currency_id AS slcurrency_id,\n"
                  + "    COALESCE(SUM(case when sl.is_return = true then -(slp.price * slp.exchangerate) else (slp.price * slp.exchangerate) END),0)::NUMERIC(18,4) AS slpprice,\n"
                  + "    slp.type_id AS slptype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    COALESCE(( SELECT\n"
                  + "                                COUNT(sli.id)AS sliid \n"
                  + "                           FROM general.saleitem sli\n"
                  + "                           INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE)\n"
                  + "                           INNER JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "                           WHERE sli.deleted=False AND sl.shift_id=? AND us.type_id = 2\n"
                  + "                            AND sl.branch_id=? ), 0) AS slid\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id=sl.pointofsale_id)\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "INNER JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "LEFT JOIN general.salepayment slp ON(slp.sale_id = sl.id AND slp.deleted=FALSE)\n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=?\n"
                  + "AND sl.branch_id=? AND us.type_id = 2\n"
                  + where + "\n"
                  + "GROUP BY sl.currency_id, slp.type_id, typd.name";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getLanguage().getId(), shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        List<SalePayment> result = getJdbcTemplate().query(sql, param, new MarketShiftReportSalePaymentMapper());
        return result;
    }

    @Override
    public String exportDataStockDetail(Shift shift) {
        String sql = "SELECT\n"
                  + "                              sli.id AS sliid,\n"
                  + "                              sli.processdate AS sliprocessdate,\n"
                  + "                              sli.unit_id AS sliunit_id,\n"
                  + "                              gunt.sortname AS guntsortname,\n"
                  + "                              gunt.unitrounding AS guntunitsorting,\n"
                  + "                              sli.quantity AS sliquantity,\n"
                  + "                              sli.unitprice AS sliunitprice,\n"
                  + "                             (sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)) AS slitotalprice,\n"
                  + "                             (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) AS slitotalmoney,\n"
                  + "                              sli.currency_id AS slicurrency_id,\n"
                  + "                              sli.stock_id AS slistock_id,\n"
                  + "                              stck.name AS stckname,\n"
                  + "                              stck.barcode AS stckbarcode,\n"
                  + "                              stck.code AS stckcode,\n"
                  + "                              stck.centerproductcode AS stckcenterproductcode,\n"
                  + "                              sli.is_managerdiscount AS sliis_managerdiscount,\n"
                  + "                              sli.manageruserdata_id AS slimanageruserdata_id,\n"
                  + "                              (SELECT general.find_category(sli.stock_id, 1,brn.id)) AS category,\n"
                  + "                              stck.supplier_id AS stcksupplier_id,\n"
                  + "                              acc1.name AS acc1name, \n"
                  + "                              stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "                              cspp.name AS csppname,\n"
                  + "                              acc.name AS accname,\n"
                  + "                              acc.title AS acctitle,\n"
                  + "                              acc.is_employee AS accis_employee,\n"
                  + "                              inv.documentnumber as invdocumentnumber,\n"
                  + "                              sl.receipt_id AS slreceipt_id,\n"
                  + "                              rcp.receiptno AS receiptno,\n"
                  + "                              br.id AS brid,\n"
                  + "                              br.name AS brname,\n"
                  + "                              us1.name AS us1name,\n"
                  + "                              us1.surname AS us1surname,\n"
                  + "                              sl.is_return AS slisreturn\n"
                  + "            FROM general.saleitem sli\n"
                  + "            INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE)\n"
                  + "            INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "            LEFT JOIN general.unit gunt ON(gunt.id = sli.unit_id)\n"
                  + "            INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "            LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "            LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "            LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted = False)\n"
                  + "            LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                  + "            LEFT JOIN general.account acc1 ON (acc1.id = stck.supplier_id)\n"
                  + "            LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "            INNER JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "            LEFT JOIN general.userdata us1 ON(us1.id=sli.manageruserdata_id)\n"
                  + "            WHERE sli.deleted=False AND sl.shift_id=" + shift.getId() + " \n"
                  + "            AND us.type_id = 2\n"
                  + "             AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " \n"
                  + "            ORDER BY sli.processdate ,  sli.id \n";

        return sql;
    }

}
