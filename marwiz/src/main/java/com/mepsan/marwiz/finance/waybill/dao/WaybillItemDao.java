/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseMapper;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WaybillItemDao extends JdbcDaoSupport implements IWaybillItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WaybillItem> listWaybillItem(Waybill waybill, String where) {

        String join = "";
        String column = "";
        String groupby = "";

        if (waybill.isIsFuel()) {
            join = "LEFT JOIN(\n"
                    + "          SELECT \n"
                    + "          wb.id as wbid,\n"
                    + "          whm.stock_id as whmstockid,\n"
                    + "          whi.stock_id as whistockid,\n"
                    + "          whr.warehouse_id as whid,\n"
                    + "          wh.name AS whname,\n"
                    + "          COALESCE(whi.quantity,0) AS availablequantity,\n"
                    + "          wb.branch_id as branch_id\n"
                    + "          FROM finance.waybill wb\n"
                    + "               LEFT JOIN finance.waybill_warehousereceipt_con wwcon ON(wwcon.waybill_id =wb.id AND wwcon.deleted = FALSE)\n"
                    + "               LEFT JOIN inventory.warehousereceipt whr ON(wwcon.warehousereceipt_id = whr.id AND whr.deleted = FALSE)\n"
                    + "               LEFT JOIN inventory.warehousemovement whm ON(whm.warehousereceipt_id = whr.id AND whm.deleted=FALSE)\n"
                    + "               LEFT JOIN inventory.warehouse wh ON(wh.id=whr.warehouse_id AND wh.deleted=FALSE)\n"
                    + "               LEFT JOIN inventory.warehouseitem whi ON(whi.warehouse_id = wh.id AND whi.deleted=FALSE)\n"
                    + "                    WHERE wb.deleted=FALSE\n"
                    + "         ) fuelwrh ON(fuelwrh.wbid=wbi.waybill_id AND fuelwrh.whmstockid = stck.id  AND fuelwrh.whistockid = stck.id AND fuelwrh.branch_id=si.branch_id)\n";

            column = "             fuelwrh.whid AS whid,\n"
                    + "            fuelwrh.whname AS whname,\n"
                    + "            fuelwrh.availablequantity as availablequantity,\n";

            groupby = " fuelwrh.whid ,\n"
                    + "            fuelwrh.whname ,\n"
                    + "            fuelwrh.availablequantity,\n ";
        } else {
            join = "     LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id= " + Integer.valueOf(waybill.getWarehouseIdList()) + ")\n";
            column = "    COALESCE(wrh.quantity,0) as availablequantity, \n";
            groupby = "wrh.quantity, ";
        }

        String sql = "SELECT \n"
                + "    wbi.id as wbiid,\n"
                + "    wbi.quantity as wbiquantity, \n"
                + "    wbi.remainingquantity as wbiremainingquantity,\n"
                + "    wbi.stock_id as wbistock_id, \n"
                + "    stck.name as stckname, \n"
                + "    stck.barcode as stckbarcode, \n"
                + "    stck.centerproductcode as stckcenterproductcode,\n"
                + "    stck.is_service AS stckis_service,\n"
                + "    wbi.description as wbidescription,\n"
                + "    gunt.id as guntid,\n"
                + "    gunt.name as guntname,\n"
                + "    gunt.sortname as guntsortname,\n"
                + "    gunt.unitrounding as guntunitrounding,\n"
                + "    COALESCE(si.balance,0) as sibalance, \n"
                + "    si.maxstocklevel as simaxstocklevel, \n"
                + "    COALESCE(si.currentsaleprice,0) as sicurrentsaleprice,\n"
                + "    si.currentsalecurrency_id as sicurrentsalecurrency_id,\n"
                + "    si.is_minusstocklevel as siis_minusstocklevel,\n"
                + "    stg.rate AS taxrate,\n"
                + "    price.price AS pricelistprice,\n"
                + "    price.currency_id AS pricelistcurrency,\n"
                + "    price.is_taxincluded AS pricelisttaxincluded,\n"
                + "    price.crrdname AS pricelistcurrencyname,\n"
                + "    COALESCE(si.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                + "    si.purchasecurrency_id AS sipurchasecurrency_id,\n"
                + "    cryd1.name AS cryd1name,\n"
                + column
                + "    (SELECT \n"
                + "       	STRING_AGG(CAST(COALESCE(owc.orderitem_id,0) as varchar),',') \n"
                + "       	FROM\n"
                + "       		finance.orderitem_waybillitem_con owc\n"
                + "            WHERE owc.deleted=FALSE AND owc.waybillitem_id = wbi.id\n"
                + "    ) as orderitemids,\n"
                + "    (SELECT \n"
                + "       	STRING_AGG(CAST(COALESCE(foi.remainingquantity+owc.quantity,0) as varchar),',') \n"
                + "       	FROM\n"
                + "       		finance.orderitem_waybillitem_con owc\n"
                + "            LEFT JOIN finance.orderitem foi ON(foi.id=owc.orderitem_id AND foi.deleted=FALSE)\n"
                + "            WHERE owc.deleted=FALSE AND owc.waybillitem_id = wbi.id\n"
                + "    ) as orderitemquantitys,\n"
                + "    (SELECT \n"
                + "       	SUM(COALESCE(foi.remainingquantity+owc.quantity,0)) \n"
                + "       	FROM\n"
                + "       		finance.orderitem_waybillitem_con owc\n"
                + "            LEFT JOIN finance.orderitem foi ON(foi.id=owc.orderitem_id AND foi.deleted=FALSE)\n"
                + "            WHERE owc.deleted=FALSE AND owc.waybillitem_id = wbi.id\n"
                + "    ) as controlquantity\n"
                + "FROM \n"
                + "	finance.waybillitem wbi  \n"
                + "	INNER JOIN inventory.stock stck ON (stck.id = wbi.stock_id AND stck.deleted = FALSE) \n"
                + "	INNER JOIN general.unit gunt ON (gunt.id = wbi.unit_id AND gunt.deleted = FALSE) \n"
                + "     LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + "   LEFT JOIN system.currency_dict cryd1 ON(cryd1.currency_id=si.purchasecurrency_id AND cryd1.language_id=?)\n"
                + " LEFT JOIN (SELECT \n"
                + "               txg.rate AS rate,\n"
                + "               stc.stock_id AS stock_id \n"
                + "           FROM inventory.stock_taxgroup_con stc  \n"
                + "           INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "           WHERE stc.deleted = false\n"
                + "           AND txg.type_id = 10 --kdv grubundan \n"
                + "           AND stc.is_purchase = ?\n"
                + ") stg ON(stg.stock_id = wbi.stock_id)\n"
                + " LEFT JOIN (SELECT\n"
                + "               pli.stock_id, \n"
                + "               pli.price,\n"
                + "               pli.is_taxincluded,\n"
                + "               pli.currency_id,\n"
                + "               cryd.name AS crrdname\n"
                + "            FROM inventory.pricelistitem pli\n"
                + "            INNER JOIN inventory.pricelist pl ON(pl.id = pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "            INNER JOIN system.currency_dict cryd ON(cryd.currency_id=pli.currency_id AND cryd.language_id=?)\n"
                + "            WHERE pli.deleted=FALSE AND pl.status_id = 11 AND pl.is_default=TRUE\n"
                + "            AND pl.is_purchase =? AND pl.branch_id = ? \n"
                + ") price ON(price.stock_id = wbi.stock_id)\n"
                + join
                + "WHERE \n"
                + "	wbi.deleted = FALSE \n"
                + "    AND wbi.waybill_id = ? \n"
                + where
                + "GROUP BY wbi.id ,wbi.quantity,wbi.remainingquantity , wbi.stock_id, stck.name, stck.barcode , wbi.description ,gunt.id , gunt.name ,gunt.sortname , gunt.unitrounding,"
                + groupby
                + "si.balance, si.maxstocklevel, si.currentsaleprice, si.currentsalecurrency_id, si.is_minusstocklevel, \n"
                + "stg.rate, price.price, price.currency_id, price.is_taxincluded, price.crrdname, COALESCE(si.purchaserecommendedprice,0), si.purchasecurrency_id, cryd1.name, stck.centerproductcode, stck.is_service \n"
                + "ORDER BY wbi.id ASC";
        Object[] param = new Object[]{waybill.getBranchSetting().getBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            waybill.isIsPurchase(), sessionBean.getUser().getLanguage().getId(), waybill.isIsPurchase(), waybill.getBranchSetting().getBranch().getId(),
            waybill.getId()};

        return getJdbcTemplate().query(sql, param, new WaybillItemMapper(waybill));
    }

    @Override
    public int create(WaybillItem obj) {
        String sql = "SELECT r_waybill_id FROM finance.process_waybillitem(?, ?, ?, ?);";

        Object[] param = new Object[]{0, obj.getWaybill().getId(), sessionBean.getUser().getId(), obj.getJsonItems()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WaybillItem obj) {
        String sql = "SELECT r_waybill_id FROM finance.process_waybillitem(?, ?, ?, ?);";

        Object[] param = new Object[]{1, obj.getWaybill().getId(), sessionBean.getUser().getId(), obj.getJsonItems()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot fatura tarafında irsaliyeden hazır stok aktarmak için
     * yazılmıştır.
     *
     * @param invoice fatura
     * @return irsaliye item listesi
     */
    @Override
    public List<WaybillItem> listWaybillItemForInvoice(Invoice invoice) {

        String where = " ";

        if (invoice.getType().getId() == 23) {//Normal Faturada İptal ve iade irsaliye itemları gelmesin diye 
            where = " AND wb.type_id <> 22 AND wb.status_id<>27 ";
        } else if (invoice.getType().getId() == 27) {//İade Faturada İptal ve Normal irsaliye itemları gelmesin diye 
            where = " AND wb.type_id <> 21 AND wb.status_id <> 27 ";
        }

        String sql = "SELECT\n"
                + "    wbi.id as wbiid,\n"
                + "    wb.id as wbid,"
                + "    wb.documentnumber as wbdocumentnumber,\n"
                + "    wb.documentserial as wbdocumentserial,\n"
                + "    wb.waybilldate as wbwaybilldate,\n"
                + "    wbi.stock_id as wbistock_id,\n"
                + "    stck.name as stckname,\n"
                + "    stck.barcode as stckbarcode, \n"
                + "    gunt.id as guntid,\n"
                + "    gunt.name as guntname,\n"
                + "    gunt.sortname as guntsortname,\n"
                + "    gunt.unitrounding as guntunitrounding,\n"
                + "    wbi.remainingquantity as wbiquantity,\n"//kalan miktarı miktar olarak çektik.
                + "    wbi.remainingquantity as wbiremainingquantity,\n"//cell edit te kalan miktar oynanacak. kontrolü yukarıdaki miktar üzerinden yapılacak
                + "    COALESCE(si.currentsaleprice,0) as sicurrentsaleprice,\n"
                + "    si.currentsalecurrency_id as sicurrentsalecurrency_id,\n"
                + "    stg.rate AS taxrate,\n"
                + "    price.price AS pricelistprice,\n"
                + "    price.currency_id AS pricelistcurrency,\n"
                + "    price.is_taxincluded AS pricelisttaxincluded,\n"
                + "    price.crrdname AS pricelistcurrencyname,\n"
                + "    COALESCE(si.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                + "    si.purchasecurrency_id AS sipurchasecurrency_id,\n"
                + "    cryd1.name AS cryd1name\n"
                + "FROM finance.waybillitem wbi\n"
                + "INNER JOIN finance.waybill wb ON (wb.id=wbi.waybill_id AND wb.deleted = FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (stck.id=wbi.stock_id)\n"
                + "INNER JOIN general.unit gunt ON (gunt.id=wbi.unit_id)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + "LEFT JOIN system.currency_dict cryd1 ON(cryd1.currency_id=si.purchasecurrency_id AND cryd1.language_id=?)\n"
                + " LEFT JOIN (SELECT \n"
                + "               txg.rate AS rate,\n"
                + "               stc.stock_id AS stock_id \n"
                + "           FROM inventory.stock_taxgroup_con stc  \n"
                + "           INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "           WHERE stc.deleted = false\n"
                + "           AND txg.type_id = 10 --kdv grubundan \n"
                + "           AND stc.is_purchase = ?\n"
                + ") stg ON(stg.stock_id = wbi.stock_id)\n"
                + " LEFT JOIN (SELECT\n"
                + "               pli.stock_id, \n"
                + "               pli.price,\n"
                + "               pli.is_taxincluded,\n"
                + "               pli.currency_id,\n"
                + "               cryd.name AS crrdname\n"
                + "            FROM inventory.pricelistitem pli\n"
                + "            INNER JOIN inventory.pricelist pl ON(pl.id = pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "            INNER JOIN system.currency_dict cryd ON(cryd.currency_id=pli.currency_id AND cryd.language_id=?)\n"
                + "            WHERE pli.deleted=FALSE AND pl.status_id = 11 AND pl.is_default=TRUE\n"
                + "            AND pl.is_purchase =? AND pl.branch_id = ? \n"
                + ") price ON(price.stock_id = wbi.stock_id)\n"
                + "WHERE wbi.deleted = FALSE\n"
                + "AND wb.is_purchase = ? \n"
                + "AND wb.account_id = ? AND wb.branch_id = ?\n"
                + where + "\n"
                + "AND wbi.remainingquantity > 0;";

        Object[] param = new Object[]{invoice.getBranchSetting().getBranch().getId(), sessionBean.getUser().getLanguage().getId(), invoice.isIsPurchase(), sessionBean.getUser().getLanguage().getId(),
            invoice.isIsPurchase(), invoice.getBranchSetting().getBranch().getId(),
            invoice.isIsPurchase(), invoice.getAccount().getId(), invoice.getBranchSetting().getBranch().getId()};
        return getJdbcTemplate().query(sql, param, new WaybillItemMapper());
    }

    @Override
    public CheckDelete testBeforeDelete(WaybillItem waybillItem) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {4, waybillItem.getId()};
        try {
            List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
            return result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int delete(WaybillItem waybillItem) {
        String sql = " SELECT r_waybill_id FROM finance.process_waybillitem(?, ?, ?, ?);";
        Object[] param = {2, waybillItem.getId(), sessionBean.getUser().getId(), null};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public WaybillItem findStock(String barcode, Waybill obj, boolean isAlternativeBarcode) {
        String where = "";
        String join = "";
        if (obj.getBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            if (isAlternativeBarcode) {
                where = where + " AND sab.is_otherbranch = FALSE ";
            }
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }

        if (!isAlternativeBarcode) {
            where = where + " AND stck.barcode = '" + barcode + "' ";
        } else {
            where = where + " AND sab.barcode = '" + barcode + "' ";
        }

        if (obj.isIsPurchase() && obj.getBranchSetting().isIsInvoiceStockSalePriceList()) { // satın alma faturasında satış fiyat listesindeki ürünler eklenebilsin mi durumu
            join = "INNER JOIN inventory.pricelistitem plii ON(plii.stock_id =stck.id AND plii.deleted=FALSE AND plii.pricelist_id IN(SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + obj.getBranchSetting().getBranch().getId() + " AND pl.is_default=TRUE AND pl.is_purchase=false AND pl.status_id=11  LIMIT 1))";
        }
        String sql = "SELECT \n"
                + "                       stck.id AS wbistock_id,\n"
                + "                       stck.barcode as stckbarcode,\n"
                + "                       stck.centerproductcode AS stckcenterproductcode,\n"
                + "                       stck.name AS stckname,\n"
                + "                       stck.code AS stckcode,\n"
                + "                       sttd.status_id AS sttdid,\n"
                + "                       sttd.name AS sttdname,\n"
                + "                       unt.id AS guntid,\n"
                + "                       COALESCE(wrh.quantity,0) as availablequantity, \n"
                + "                       si.maxstocklevel as simaxstocklevel,\n"
                + "                       COALESCE(si.balance,0) as sibalance, \n"
                + (isAlternativeBarcode ? "sab.quantity" : "1") + " as wbiquantity,\n"
                + "                       si.is_minusstocklevel as siis_minusstocklevel,\n"
                + "                       si.is_delist as siis_delist \n"
                + "                    FROM inventory.stock stck   \n"
                + "                    INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?) \n"
                + "                  LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + join + "\n"
                + "                  LEFT JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=false) \n"
                + "                  LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id=?)\n"
                + "                  LEFT JOIN inventory.stockalternativebarcode sab ON(sab.deleted = FALSE AND sab.stock_id = stck.id)\n"
                + "                    WHERE stck.deleted = false AND stck.status_id <> 4 AND si.is_passive = FALSE  " + where + "\n"
                + " LIMIT 1;";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getBranchSetting().getBranch().getId(), Integer.parseInt(obj.getWarehouseIdList())};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new WaybillItemMapper());
        } catch (EmptyResultDataAccessException e) {
            return new WaybillItem();
        }
    }

    @Override
    public String excelItemInsert(Waybill waybill, String json) {
        String sql = "SELECT r_message FROM finance.excel_waybillitem(?, ?, ?, ?, ?);";

        Object[] param = new Object[]{waybill.getId(), Integer.valueOf(waybill.getWarehouseIdList()), json, sessionBean.getUser().getId(), waybill.getBranchSetting().getBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return ((SQLException) e.getCause()).getSQLState();
        }
    }

    @Override
    public String findWaybillForInvoice(int stockId, int invoiceId) {
        String sql = "SELECT \n"
                + "       STRING_AGG(COALESCE(wic.waybillitem_id,0)::TEXT,',') as wicwaybillitem_id\n"
                + "       FROM finance.invoiceitem ivi \n"
                + "       LEFT JOIN finance.waybillitem_invoiceitem_con wic ON(wic.invoiceitem_id=ivi.id AND wic.deleted=false) \n"
                + "       WHERE ivi.deleted=FALSE AND ivi.stock_id=? AND ivi.invoice_id=?";

        Object[] param = new Object[]{stockId, invoiceId};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return ((SQLException) e.getCause()).getSQLState();
        }
    }

    @Override
    public int checkStockSalePriceList(String barcode, boolean isPurchase, BranchSetting branchSetting) {
        String where = " ";
        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND sab.is_otherbranch = FALSE ";
        }
        String sql = "                 \n"
                + "SELECT CASE WHEN EXISTS (\n"
                + "	   SELECT\n"
                + "          stck.id\n"
                + "       FROM inventory.stock  stck\n"
                + "       INNER JOIN inventory.pricelistitem plii ON(plii.stock_id =stck.id AND plii.deleted=FALSE AND plii.pricelist_id IN(SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11  LIMIT 1))\n"
                + "       LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE)\n"
                + "       WHERE stck.deleted=FALSE AND (stck.barcode= ? OR (sab.barcode = ? " + where + " ))\n"
                + "       					) \n"
                + "THEN 1 ELSE 0 END";

        Object[] param = {branchSetting.getBranch().getId(), barcode, barcode};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<Warehouse> findFuelStockWarehouse(WaybillItem waybillItem, Waybill waybill) {

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
                + "                  WHERE iw.deleted=FALSE AND iw.is_fuel= TRUE AND iw.branch_id= ? AND stck.id = ?  AND iw.status_id = 13 ";

        Object[] params = new Object[]{waybill.getBranchSetting().getBranch().getId(), sessionBean.getUser().getLanguage().getId(), waybill.getBranchSetting().getBranch().getId(), waybillItem.getStock().getId()};

        List<Warehouse> result = getJdbcTemplate().query(sql, params, new WarehouseMapper());
        return result;

    }
}
