/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 14:01:15
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreementItemMapper;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseMapper;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class InvoiceItemDao extends JdbcDaoSupport implements IInvoiceItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<InvoiceItem> listInvoiceStocks(Invoice invoice, String type) {

        String quantity = "";
        String warehouseJoin = "";
        String groupByQuantity = "";
        String where = "";
        String fuelJoin = "";
        String fuelColumn = "";
        String fuelColumnGroup = "";

        if ((invoice.getType().getId() == 59 || invoice.getType().getId() == 23) && invoice.isIsFuel()) {

            fuelJoin = " LEFT JOIN(\n"
                      + "          SELECT \n"
                      + "          wic.invoice_id as wicinvoice_id,\n"
                      + "          wb.id as wbid,\n"
                      + "          whm.stock_id as whmstockid,\n"
                      + "          whi.stock_id as whistockid,\n"
                      + "          whr.warehouse_id as whid,\n"
                      + "          wh.name AS whname,\n"
                      + "          COALESCE(whi.quantity,0) AS availablequantity,\n"
                      + "           wb.branch_id as branch_id\n"
                      + "          FROM finance.waybill_invoice_con wic\n"
                      + "               LEFT JOIN finance.waybill wb ON(wic.waybill_id =wb.id AND wb.deleted = FALSE)\n"
                      + "               LEFT JOIN finance.waybill_warehousereceipt_con wwcon ON(wwcon.waybill_id =wb.id AND wwcon.deleted = FALSE)\n"
                      + "               LEFT JOIN inventory.warehousereceipt whr ON(wwcon.warehousereceipt_id = whr.id AND whr.deleted = FALSE)\n"
                      + "               LEFT JOIN inventory.warehousemovement whm ON(whm.warehousereceipt_id = whr.id AND whm.deleted=FALSE)\n"
                      + "               LEFT JOIN inventory.warehouse wh ON(wh.id=whr.warehouse_id AND wh.deleted=FALSE)\n"
                      + "               LEFT JOIN inventory.warehouseitem whi ON(whi.warehouse_id = wh.id AND whi.deleted=FALSE)\n"
                      + "                    WHERE wic.deleted=FALSE\n"
                      + "         ) fuelwrh ON(fuelwrh.wicinvoice_id=invi.invoice_id AND fuelwrh.whmstockid = stck.id  AND fuelwrh.whistockid = stck.id AND fuelwrh.branch_id=stcki.branch_id)\n";

            fuelColumn = "         fuelwrh.whid AS whid,\n"
                      + "            fuelwrh.whname AS whname,\n"
                      + "            fuelwrh.availablequantity as availablequantity,\n";
            fuelColumnGroup = " fuelwrh.whid,\n"
                      + "            fuelwrh.whname,\n"
                      + "            fuelwrh.availablequantity,\n ";
        } else {
            if (invoice.getWarehouseIdList() != null && !invoice.getWarehouseIdList().equals("")) {
                quantity = " COALESCE(wrh.quantity,0) AS availablequantity, \n";
                warehouseJoin = "LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id= " + Integer.parseInt(invoice.getWarehouseIdList()) + ")\n";
                groupByQuantity = "wrh.quantity, \n";
            }

        }

        if (type.equals("pricedifferentinvoice")) {
            where = " AND NOT EXISTS(SELECT * FROM finance.invoiceitem invi1 WHERE invi1.deleted=FALSE AND invi1.differentinvoiceitem_id = invi.id)\n";
        }

        String sql = "SELECT\n"
                  + "            invi.id as inviid,\n"
                  + "            invi.invoice_id as inviinvoiceid,\n"
                  + "            invi.is_service as inviis_service,\n"
                  + "            invi.stock_id as invistock_id,\n"
                  + "            stck.name as stckname,\n"
                  + "            stck.code as stckcode,\n"
                  + "            stck.barcode as stckbarcode,\n"
                  + "            stck.centerproductcode as stckcenterproductcode,\n"
                  + "            gunt.id AS guntid,\n"
                  + "            gunt.sortname AS guntsortname,\n"
                  + "            gunt.name as guntname,\n"
                  + "            gunt.unitrounding as guntunitrounding,\n"
                  + "            invi.unitprice as inviunitprice,\n"
                  + "            invi.quantity as inviquantity,\n"
                  + "            invi.totalprice as invitotalprice,\n"
                  + "            invi.taxrate as invitaxrate,\n"
                  + "            invi.totaltax as invitotaltax,\n"
                  + "            invi.is_discountrate as inviis_discountrate,\n"
                  + "            invi.discountrate as invidiscountrate,\n"
                  + "            invi.discountprice as invidiscountprice,\n"
                  + "            invi.is_discountrate2 as inviis_discountrate2,\n"
                  + "            invi.discountrate2 as invidiscountrate2,\n"
                  + "            invi.discountprice2 as invidiscountprice2,\n"
                  + "            invi.currency_id as invicurrency_id,\n"
                  + "            invi.exchangerate as inviexchangerate,\n"
                  + "            invi.totalmoney as invitotalmoney,\n"
                  + "            invi.description as invidescription,\n"
                  + "            invi.is_free as inviis_free,\n"
                  + "            crr.code as crrcode,\n"
                  + "            crrd.name as crrdname, \n"
                  + "            invi.c_id as invic_id,\n"
                  + "            usd.name as usdname,\n"
                  + "            usd.username as usdusername,\n"
                  + "            usd.surname as usdsurname, \n"
                  + "            invi.c_time as invic_time,\n"
                  + "    (SELECT \n"
                  + "       	STRING_AGG(COALESCE(wic.waybillitem_id,0)::TEXT,',') \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as wicwaybillitem_id,\n"
                  + "    (SELECT \n"
                  + "       	STRING_AGG(COALESCE(wic.quantity,0)::TEXT,',') \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as wicwaybillitem_quantity,\n"
                  + "    (SELECT \n"
                  + "       	STRING_AGG(COALESCE(wi.remainingquantity,0)::TEXT,',') \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "                     LEFT JOIN finance.waybillitem wi ON(wic.waybillitem_id=wi.id AND wi.deleted=false) \n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as wiquantity,\n"
                  + "    (SELECT \n"
                  + "       	STRING_AGG(CAST(COALESCE(owc.orderitem_id,0) as varchar),',') \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "                     INNER JOIN finance.waybillitem wi ON(wic.waybillitem_id=wi.id AND wi.deleted=false) \n"
                  + "       		INNER JOIN finance.orderitem_waybillitem_con owc ON (owc.waybillitem_id = wi.id AND owc.deleted=FALSE)\n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as orderitemids,\n"
                  + "    (SELECT \n"
                  + "       	STRING_AGG(CAST(COALESCE(foi.remainingquantity+owc.quantity,0) as varchar),',') \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "                     INNER JOIN finance.waybillitem wi ON(wic.waybillitem_id=wi.id AND wi.deleted=false) \n"
                  + "       		INNER JOIN finance.orderitem_waybillitem_con owc ON (owc.waybillitem_id = wi.id AND owc.deleted=FALSE)\n"
                  + "                     INNER JOIN finance.orderitem foi ON(foi.id=owc.orderitem_id AND foi.deleted=FALSE)\n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as orderitemquantitys,\n"
                  + "    (SELECT \n"
                  + "       	SUM(COALESCE(foi.remainingquantity+owc.quantity,0)) \n"
                  + "       	FROM\n"
                  + "       		finance.waybillitem_invoiceitem_con wic\n"
                  + "                     INNER JOIN finance.waybillitem wi ON(wic.waybillitem_id=wi.id AND wi.deleted=false) \n"
                  + "       		INNER JOIN finance.orderitem_waybillitem_con owc ON (owc.waybillitem_id = wi.id AND owc.deleted=FALSE)\n"
                  + "                     INNER JOIN finance.orderitem foi ON(foi.id=owc.orderitem_id AND foi.deleted=FALSE)\n"
                  + "            WHERE wic.invoiceitem_id=invi.id AND wic.deleted=false\n"
                  + "    ) as controlquantity,\n"
                  + "            stcki.purchasecontroldate as stckipurchasecontroldate,\n"
                  + "            stcki.is_fuel as stckiis_fuel,\n"
                  + "            stcki.currentsaleprice as stckicurrentsaleprice,\n"
                  + "            stcki.currentsalecurrency_id as stckicurrentsalecurrency_id,\n"
                  + "            stcki.salemandatoryprice as stckisalemandatoryprice,\n"
                  + "            stcki.salemandatorycurrency_id as stckisalemandatorycurrency_id,\n"
                  + "            invi.profitrate AS inviprofitrate,\n"
                  + quantity
                  + "            invi.profitprice AS inviprofitprice,\n"
                  + "            stcki.maxstocklevel AS stckimaxstocklevel,\n"
                  + "            COALESCE(stcki.balance,0) AS stckibalance, \n"
                  + "            stcki.is_minusstocklevel AS siis_minusstocklevel,\n "
                  + "            invi.differenttotalmoney AS invidifferenttotalmoney,\n"
                  + fuelColumn
                  + "            invi.differentinvoiceitem_id AS invidifferentinvoiceitem_id\n"
                  + "          FROM finance.invoiceitem invi \n"
                  + "          LEFT JOIN inventory.stock stck  ON(stck.id=invi.stock_id)\n"
                  + "          LEFT JOIN inventory.stockinfo stcki  ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE AND stcki.branch_id=?)\n"
                  + "          LEFT JOIN general.unit gunt ON (gunt.id = invi.unit_id)\n"
                  + "          INNER JOIN system.currency crr   ON (crr.id=invi.currency_id)\n"
                  + "          INNER JOIN system.currency_dict crrd ON(crrd.currency_id=invi.currency_id AND crrd.language_id = ?) \n"
                  + warehouseJoin
                  + "          LEFT JOIN general.userdata usd ON(usd.id=invi.c_id)\n"
                  + fuelJoin
                  + "          WHERE invi.invoice_id = ?\n"
                  + "          AND invi.deleted=false\n"
                  + where + "\n"
                  + "          GROUP BY \n"
                  + "            invi.id,\n"
                  + "            invi.invoice_id,\n"
                  + "            invi.is_service,\n"
                  + "            invi.stock_id,\n"
                  + "            stck.name,\n"
                  + "            stck.code,\n"
                  + "            stck.barcode,\n"
                  + "            stck.centerproductcode,\n"
                  + "            gunt.id,\n"
                  + "            gunt.sortname,\n"
                  + "            gunt.unitrounding,\n"
                  + "            invi.unitprice,\n"
                  + "            invi.quantity,\n"
                  + "            invi.totalprice,\n"
                  + "            invi.taxrate,\n"
                  + "            invi.totaltax,\n"
                  + "            invi.is_discountrate,\n"
                  + "            invi.discountrate,\n"
                  + "            invi.discountprice,\n"
                  + "            invi.is_discountrate2,\n"
                  + "            invi.discountrate2,\n"
                  + "            invi.discountprice2,\n"
                  + "            invi.currency_id,\n"
                  + "            invi.exchangerate,\n"
                  + "            invi.totalmoney,\n"
                  + "            invi.description,\n"
                  + "            invi.is_free,\n"
                  + "            crr.code,\n"
                  + "            crrd.name,\n"
                  + "            usd.name,\n"
                  + "            usd.surname,\n"
                  + "            usd.username,"
                  + "            stcki.is_fuel,\n"
                  + "            stcki.purchasecontroldate,\n"
                  + "            stcki.currentsalecurrency_id,\n"
                  + "            stcki.currentsaleprice,\n"
                  + "            stcki.salemandatoryprice,\n"
                  + "            stcki.salemandatorycurrency_id,\n"
                  + "            COALESCE(invi.profitrate,0),\n"
                  + groupByQuantity
                  + "            COALESCE(invi.profitprice,0),\n"
                  + "            stcki.maxstocklevel,\n"
                  + "            stcki.balance, \n"
                  + "            stcki.is_minusstocklevel,\n"
                  + fuelColumnGroup
                  + "            invi.differenttotalmoney,\n"
                  + "            invi.differentinvoiceitem_id\n"
                  + "          ORDER BY invi.id ASC";

        Object[] param = new Object[]{invoice.getBranchSetting().getBranch().getId(), sessionBean.getUser().getLanguage().getId(), invoice.getId()};
        return getJdbcTemplate().query(sql, param, new InvoiceItemMapper());
    }

    @Override
    public int create(InvoiceItem obj) {

        String sql = "SELECT r_invoice_id FROM finance.process_invoiceitem(?, ?, ?, ?);";

        Object[] param = new Object[]{0, obj.getInvoice().getId(), sessionBean.getUser().getId(), obj.getJsonItems()};
        System.out.println("param:" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(InvoiceItem obj) {
        String sql = "SELECT r_invoice_id FROM finance.process_invoiceitem(?, ?, ?, ?);";

        Object[] param = new Object[]{1, obj.getInvoice().getId(), sessionBean.getUser().getId(), obj.getJsonItems()};
        System.out.println("param:" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(InvoiceItem item) {
        String sql = "SELECT r_invoice_id FROM finance.process_invoiceitem(?, ?, ?, ?);";

        Object[] param = new Object[]{2, item.getInvoice().getId(), sessionBean.getUser().getId(), item.getJsonItems()};
        System.out.println("param:" + Arrays.toString(param));

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<InvoiceItem> findAllSaleItemForCredit(CustomerAgreements customerAgreements, String where) {
        if (customerAgreements.isChcCredit()) {
            String creditId = "";
            creditId += customerAgreements.getCreditIds();
            where += where + " and crdt.id IN(" + creditId + ")";//crdtid
        }

        String sql = "SELECT *, ROW_NUMBER () OVER (ORDER BY tt.slistock_id) AS sid FROM("
                  + "SELECT \n"
                  + "	 sli.stock_id AS slistock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.is_service AS stckis_service,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.barcode as stckbarcode,\n"
                  + "    gunt.id AS guntid,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.name AS guntname,\n"
                  + "    gunt.unitrounding as guntunitrounding,\n"
                  + "    sli.unitprice AS sliunitprice,\n"
                  + "    COALESCE(SUM(sli.quantity),0) AS sliquantity,\n"
                  + "    COALESCE(SUM(sli.totalprice),0) AS slitotalprice,\n"
                  + "	 sli.taxrate AS slitaxrate,\n"
                  + "    COALESCE(SUM(sli.totaltax),0) AS slitotaltax,\n"
                  + "    sli.discountrate AS slidiscountrate,\n"
                  + "    COALESCE(SUM(sli.discountprice),0) AS slidiscountprice,\n"
                  + "    sli.currency_id AS slicurrency,\n"
                  + "    sli.exchangerate AS sliexchangerate,\n"
                  + "    COALESCE(SUM(sli.totalmoney),0) AS slitotalmoney,\n"
                  + "    crr.code as crrcode,\n"
                  + "    crrd.name as crrdname,\n"
                  + "    stcki.currentsaleprice as stckicurrentsaleprice,\n"
                  + "    stcki.currentsalecurrency_id as stckicurrentsalecurrency_id\n"
                  + "FROM general.saleitem sli\n"
                  + "    LEFT JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON (gunt.id = sli.unit_id)\n"
                  + "    INNER JOIN system.currency crr   ON (crr.id=sli.currency_id)\n"
                  + "    INNER JOIN system.currency_dict crrd ON(crrd.currency_id=sli.currency_id AND crrd.language_id = ?) \n"
                  + "    LEFT JOIN inventory.stockinfo stcki ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE AND stcki.branch_id=" + customerAgreements.getBranchSetting().getBranch().getId() + ")"
                  + "WHERE sli.deleted=False\n"
                  + "     AND sli.sale_id IN (SELECT \n"
                  + "                             slp.sale_id\n"
                  + "                         FROM finance.credit crdt \n"
                  + "                           INNER JOIN general.salepayment slp ON(slp.credit_id=crdt.id AND slp.deleted = False)\n"
                  + "                       	INNER JOIN general.sale sl ON(sl.id=slp.sale_id AND sl.deleted = False)\n"
                  + "                       	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                  + " 			      WHERE crdt.deleted=False AND crdt.branch_id = " + customerAgreements.getBranchSetting().getBranch().getId() + "\n"
                  + "                        	AND crdt.is_invoice=False \n"
                  + "                       	AND crdt.account_id=?\n"
                  + where + "\n"
                  + "    		   	AND crdt.is_cancel=False AND slp.type_id=19 \n"
                  + "                           AND sl.is_return=False \n"
                  + "                       	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) \n"
                  + "     		        AND crdt.processdate BETWEEN ? AND ?\n"
                  + "                    	)\n"
                  + "GROUP BY \n"
                  + "	 sli.stock_id, stck.name, \n"
                  + "	 stck.is_service, stck.code, \n"
                  + "    gunt.id, gunt.sortname, \n"
                  + "    gunt.unitrounding, sli.taxrate, \n"
                  + "    sli.discountrate, sli.currency_id, \n"
                  + "    sli.exchangerate, crr.code, crrd.name, stck.barcode, gunt.name, stcki.currentsaleprice, stcki.currentsalecurrency_id,sli.unitprice \n"
                  + "    \n"
                  + "    UNION ALL\n"
                  + "\n"
                  + "SELECT \n"
                  + "	stck.id AS slistock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.is_service AS stckis_service,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.barcode as stckbarcode,\n"
                  + "    gunt.id AS guntid,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.name AS guntname,\n"
                  + "    gunt.unitrounding as guntunitrounding,\n"
                  + "    assl.price AS sliunitprice,\n"
                  + "    COALESCE(SUM(assl.liter),0) AS sliquantity,\n"
                  + "    COALESCE(SUM((assl.price/(1+(COALESCE(taxgroup.txgrate,0)/100)))*assl.liter),0) - COALESCE(SUM(assl.discounttotal),0) AS slitotalprice,\n"
                  + "    COALESCE(taxgroup.txgrate,0) AS slitaxrate,\n"
                  + "    (((COALESCE(SUM((assl.price/(1+(COALESCE(taxgroup.txgrate,0)/100)))*assl.liter),0) - COALESCE(SUM(assl.discounttotal),0)) * COALESCE(taxgroup.txgrate,0))/100) AS slitotaltax,\n"
                  + "    0 AS slidiscountrate,\n"
                  + "    COALESCE(SUM(assl.discounttotal),0) AS slidiscountprice,\n"
                  + "    " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AS slicurrency,\n"
                  + "    1 AS sliexchangerate,\n"
                  + "    COALESCE(SUM(assl.totalmoney),0) AS slitotalmoney,\n"
                  + "    '" + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) + "' as crrcode,\n"
                  + "    '" + sessionBean.getUser().getLastBranch().getCurrency().getNameMap() + "' as crrdname,\n"
                  + "    stcki.currentsaleprice as stckicurrentsaleprice,\n"
                  + "    stcki.currentsalecurrency_id as stckicurrentsalecurrency_id\n"
                  + "FROM automation.shiftsale assl\n"
                  + "	LEFT JOIN inventory.stock stck ON(stck.id=assl.stock_id)\n"
                  + "   LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id)\n"
                  + "   LEFT JOIN (SELECT \n"
                  + "                 			txg.rate AS txgrate,\n"
                  + "                            stc.stock_id AS stock_id \n"
                  + "           				FROM inventory.stock_taxgroup_con stc  \n"
                  + "              				INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "          	 			WHERE stc.deleted = false\n"
                  + "                 			  AND txg.type_id = 10 --kdv grubundan \n"
                  + "                 		      AND stc.is_purchase = FALSE) taxgroup ON(taxgroup.stock_id = stck.id)\n"
                  + "  LEFT JOIN inventory.stockinfo stcki ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE AND stcki.branch_id=" + customerAgreements.getBranchSetting().getBranch().getId() + ")"
                  + "WHERE assl.deleted=FALSE\n"
                  + "AND assl.id IN (SELECT \n"
                  + "			assl1.id\n"
                  + "		     FROM finance.credit crdt \n"
                  + "			INNER JOIN automation.shiftsale assl1 ON(assl1.credit_id=crdt.id AND assl1.deleted = False)\n"
                  + " 		     WHERE crdt.deleted=False AND crdt.branch_id =" + customerAgreements.getBranchSetting().getBranch().getId() + "\n"
                  + "                	AND crdt.is_invoice=False \n"
                  + "                   AND crdt.account_id=?\n"
                  + where + "\n"
                  + "    		AND crdt.is_cancel=False\n"
                  + "     		AND crdt.processdate BETWEEN ? AND ?\n"
                  + "                )\n"
                  + "GROUP BY \n"
                  + "	 stck.id, stck.name, \n"
                  + "	 stck.is_service, stck.code, \n"
                  + "    gunt.id, gunt.sortname, \n"
                  + "    gunt.unitrounding, stck.barcode, gunt.name, taxgroup.txgrate, stcki.currentsaleprice, stcki.currentsalecurrency_id,assl.price ) tt\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), customerAgreements.getAccount().getId(),
            customerAgreements.getBeginDate(), customerAgreements.getEndDate(),
            customerAgreements.getAccount().getId(),
            customerAgreements.getBeginDate(), customerAgreements.getEndDate()};

        return getJdbcTemplate().query(sql, param, new CustomerAgreementItemMapper());
    }

    @Override
    public InvoiceItem findStock(String barcode, Invoice obj, boolean isAlternativeBarcode, boolean isInvoiceStockSalePriceList) {

        String where = "";
        String join = "";
        String fuelJoin = "";
        String fuelColumn = "";
        if (obj.getBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE  ";
            if (isAlternativeBarcode) {
                where = where + " AND sab.is_otherbranch = FALSE ";
            }
        } else {
            where = where + " AND stck.is_otherbranch = TRUE  ";
        }

        if (!isAlternativeBarcode) {
            where = where + " AND stck.barcode = '" + barcode + "' ";
        } else {
            where = where + " AND sab.barcode = '" + barcode + "' ";
        }

        if (obj.isIsPurchase() && isInvoiceStockSalePriceList) { // satın alma faturasında satış fiyat listesindeki ürünler eklenebilsin mi durumu
            join = "INNER JOIN inventory.pricelistitem plii ON(plii.stock_id =stck.id AND plii.deleted=FALSE AND plii.pricelist_id IN(SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + obj.getBranchSetting().getBranch().getId() + " AND pl.is_default=TRUE AND pl.is_purchase=false AND pl.status_id=11  LIMIT 1))";
        }

        if (!obj.isIsFuel()) {
            fuelJoin = " LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id= " + Integer.parseInt(obj.getWarehouseIdList()) + ")\n";
            fuelColumn = " COALESCE(wrh.quantity,0) AS availablequantity,\n";

        }

        String sql = "SELECT \n"
                  + "               stck.id AS invistock_id,\n"
                  + "               stck.barcode as stckbarcode,\n"
                  + "               stck.centerproductcode AS stckcenterproductcode,\n"
                  + "               stck.name AS stckname,\n"
                  + "               stck.code AS stckcode,\n"
                  + "               sttd.status_id AS sttdid,\n"
                  + "               sttd.name AS sttdname,\n"
                  + "               si.recommendedprice as sirecommendedprice,\n"
                  + "               si.currency_id as sicurrency_id,\n"
                  + "               si.minprofitrate as siminprofitrate,\n"
                  + "               si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                  + "               si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                  + "               si.purchasecontroldate as sipurchasecontroldate,\n"
                  + "               si.salemandatoryprice as sisalemandatoryprice, \n"
                  + "               si.is_fuel as siis_fuel, \n"
                  + "               gunt.id AS guntid,\n"
                  + "               gunt.name AS guntname,\n"
                  + "               gunt.sortname AS guntsortname,\n"
                  + "               gunt.unitrounding as guntunitrounding,\n"
                  + "               gunt.centerunit_id as guntcenterunit_id,\n"
                  + "               stck.centerstock_id as stckcenterstock_id,\n"
                  + "               COALESCE(pli.price,0) as pliprice,\n"
                  + (isAlternativeBarcode ? "sab.quantity" : "1") + " as inviquantity,\n"
                  + "               COALESCE(pli.currency_id,0) as plicurrrency,\n"
                  + "               si.maxstocklevel as simaxstocklevel,\n"
                  + "               COALESCE(si.balance,0) as sibalance,\n"
                  + "               si.is_minusstocklevel AS siis_minusstocklevel,\n "
                  + "               COALESCE(si.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                  + fuelColumn
                  + "               si.purchasecurrency_id AS sipurchasecurrency_id,\n"
                  + "               si.is_delist AS siis_delist \n"
                  + "            FROM inventory.stock stck   \n"
                  + "            LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                  + "            INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                  + join + "\n"
                  + "            LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                  + "            LEFT JOIN inventory.pricelistitem pli ON(pli.stock_id =stck.id AND pli.deleted=FALSE AND pli.pricelist_id IN(SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=? AND pl.status_id=11  LIMIT 1))\n"
                  + "     LEFT JOIN inventory.stockalternativebarcode sab ON(sab.deleted = FALSE AND sab.stock_id = stck.id)\n"
                  + fuelJoin
                  + "     WHERE stck.deleted = false "
                  + "     AND stck.is_service = FALSE AND stck.status_id <> 4 AND si.is_passive = FALSE \n"
                  + where
                  + "LIMIT 1";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getBranchSetting().getBranch().getId(), obj.getBranchSetting().getBranch().getId(), obj.isIsPurchase()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new InvoiceItemMapper());
        } catch (EmptyResultDataAccessException e) {
            return new InvoiceItem();
        }
    }

    @Override
    public InvoiceItem totalQuantityForInvoice(Invoice obj, int stockId, Date begin, Date end, Branch branch) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                  + "SUM(COALESCE(invi.quantity,0)) as inviquantity\n"
                  + "FROM finance.invoice inv \n"
                  + "INNER JOIN finance.invoiceitem invi ON(inv.id=invi.invoice_id AND invi.deleted=FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id =invi.stock_id AND stck.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.branch_id=? AND stcki.is_fuel=FALSE)\n"
                  + "WHERE inv.account_id=?  AND invi.stock_id=? AND inv.is_purchase=TRUE AND inv.deleted=FALSE AND inv.invoicedate  BETWEEN " + " ' "
                  + simpleDateFormat.format(begin) + " ' "
                  + " AND "
                  + " ' "
                  + simpleDateFormat.format(end)
                  + " ' ";

        Object[] param = new Object[]{branch.getId(), obj.getAccount().getId(), stockId};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new InvoiceItemMapper());
        } catch (EmptyResultDataAccessException e) {
            return new InvoiceItem();
        }

    }

    @Override
    public String excelItemInsert(Invoice invoice, String json) {
        String sql = "SELECT r_message FROM finance.excel_invoiceitem(?, ?, ?);";

        Object[] param = new Object[]{json, invoice.getId(), sessionBean.getUser().getId()};
        System.out.println("param" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return ((SQLException) e.getCause()).getSQLState();
        }
    }

    @Override
    public List<InvoiceItem> findInvoiceItemLastPrice(String stockList, BranchSetting branchSetting) {
        String sql = "SELECT  \n"
                  + "	stcki.stock_id AS stckistock_id,\n"
                  + "	COALESCE(stcki.currentpurchaseprice,0) AS stckicurrentpurchaseprice,\n"
                  + "   COALESCE(stcki.currentpurchasecurrency_id,?) AS stckicurrentpurchasecurrency_id,\n"
                  + "	COALESCE(stcki.currentsaleprice,0) AS stckicurrentsaleprice,\n"
                  + "   COALESCE(stcki.currentsalecurrency_id,?) AS stckicurrentsalecurrency_id\n"
                  + "FROM inventory.stockinfo stcki \n"
                  + "WHERE stcki.deleted=FALSE AND stcki.branch_id =? AND stcki.stock_id IN (" + stockList + ")";

        Object[] param = new Object[]{branchSetting.getBranch().getCurrency().getId(), branchSetting.getBranch().getCurrency().getId(), branchSetting.getBranch().getId()};
        return getJdbcTemplate().query(sql, param, new InvoiceItemMapper());
    }

    @Override
    public List<CheckDelete> testBeforeDelete(InvoiceItem invoiceitem) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {13, invoiceitem.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int updateWaitedInvoiceJson(String jsonItem, Invoice invoice) {
        String sql = "UPDATE finance.invoice \n"
                  + "   SET waitinvoiceitemjson = ?, u_id = ?, u_time = NOW()\n"
                  + "   WHERE id = ?";
        Object[] param = new Object[]{jsonItem.equals("") || jsonItem.equals("[]") ? null : jsonItem, sessionBean.getUser().getId(), invoice.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem, Invoice inv) {
        String where = "";
        if (!invoiceItem.isIsService()) {
            where = "  AND iw.is_fuel= TRUE  ";
        }

        if (inv.isIsPurchase()) {
            where = where + " AND si.is_delist = FALSE \n";
        }

        String sql = "SELECT \n"
                  + "iw.id as iwid,\n"
                  + "iw.name as iwname,\n"
                  + "si.maxstocklevel as simaxstocklevel,\n"
                  + "COALESCE(si.balance,0) as sibalance,\n"
                  + "COALESCE(iwi.quantity,0) as availablequantity,\n"
                  + "si.is_minusstocklevel AS siis_minusstocklevel\n"
                  + "FROM inventory.warehouse iw\n"
                  + "                  INNER JOIN inventory.warehouseitem iwi ON(iwi.warehouse_id = iw.id AND iwi.deleted = FALSE) \n"
                  + "                  INNER JOIN inventory.stock stck ON(stck.id = iwi.stock_id AND stck.deleted = FALSE)\n"
                  + "                  INNER JOIN inventory.stockinfo si ON(si.stock_id =stck.id AND si.deleted = FALSE AND si.branch_id=?)"
                  + "                  LEFT JOIN system.status_dict std ON (iw.status_id=std.status_id and std.language_id= ? )\n"
                  + "                  WHERE iw.deleted=FALSE " + where + "  AND iw.branch_id= ? AND stck.id = ? AND iw.status_id = 13 ";

        Object[] params = new Object[]{inv.getBranchSetting().getBranch().getId(), sessionBean.getUser().getLanguage().getId(), inv.getBranchSetting().getBranch().getId(), invoiceItem.getStock().getId()};

        List<Warehouse> result = getJdbcTemplate().query(sql, params, new WarehouseMapper());
        return result;

    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranch() {
        String sql = "SELECT \n"
                  + "   brn.id as brid,\n"
                  + "   brn.name as brname,\n"
                  + "   brns.is_centralintegration as brsis_centralintegration,\n"
                  + "   brns.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                  + "   brn.currency_id AS brncurrency_id,\n"
                  + "   brn.is_central AS brnis_central,\n"
                  + "   brn.is_agency AS brnis_agency,\n"
                  + "   brn.concepttype_id AS brnconcepttype_id, \n"
                  + "   brns.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                  + "   brns.parourl AS brsparourl,\n"
                  + "   brns.paroaccountcode AS brsparoaccountcode,\n"
                  + "   brns.parobranchcode AS brsparobranchcode,\n"
                  + "   brns.paroresponsiblecode AS brsparoresponsiblecode\n"
                  + "FROM general.userdata usr\n"
                  + "INNER JOIN general.userdata_authorize_con usda ON(usr.id=usda.userdata_id AND usda.deleted=FALSE)\n"
                  + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=aut.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN general.branchsetting brns ON(brns.branch_id=brn.id AND brns.deleted=FALSE)\n"
                  + "WHERE usr.deleted=FALSE AND usr.id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());

        return result;

    }

    @Override
    public List<InvoiceItem> processUploadExcelItemsControl(String jsonExcelItems, Invoice inv) {
        String sql = "SELECT * FROM finance.excel_invoiceitem_stockcontrol(?,?,?,?,?,?,?,?,?);";

        Object[] param = {jsonExcelItems.toString(), inv.isIsPurchase(), inv.getBranchSetting().isIsCentralIntegration(), inv.getWarehouseIdList(), inv.getBranchSetting().getBranch().getId(),
            inv.getType().getId(), sessionBean.getUser().getLanguage().getId(), inv.getId(), inv.getBranchSetting().isIsInvoiceStockSalePriceList()};

        List<InvoiceItem> result = getJdbcTemplate().query(sql, param, new InvoiceItemMapper());
        return result;
    }

}
