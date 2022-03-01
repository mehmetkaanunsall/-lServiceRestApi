/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:54:22
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockDao extends JdbcDaoSupport implements IStockDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Stock> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {

        if (sortField == null) {
            sortField = "tt.stckid";
        }
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }

        String sql = "WITH recursive ctTree AS(\n"
                + "    SELECT \n"
                + "        gct.id,\n"
                + "        scac.stock_id,\n"
                + "        gct.name, \n"
                + "        COALESCE(gct.parent_id,0) AS parent_id, \n"
                + "        1 AS depth\n"
                + "    FROM \n"
                + "        inventory.stock_categorization_con scac\n"
                + "        INNER JOIN general.categorization gct ON(scac.categorization_id = gct.id AND gct.deleted = FALSE)\n"
                + "	WHERE\n"
                + "        scac.deleted = FALSE\n"
                + "	UNION ALL\n"
                + "   SELECT     	\n"
                + "        gct.id, \n"
                + "        ct.stock_id,\n"
                + "        gct.name,\n"
                + "        COALESCE(gct.parent_id,0) AS parent_id, \n"
                + "        ct.depth+1 AS depth\n"
                + "    FROM \n"
                + "        general.categorization gct\n"
                + "        JOIN ctTree ct ON ct.parent_id = gct.id\n"
                + "    WHERE\n"
                + "       gct.deleted = FALSE\n"
                + ")\n"
                + "SELECT tt.*,\n"
                + "   (\n"
                + "    SELECT \n"
                + "       xmlelement(\n"
                + "       name \"categories\",\n"
                + "       xmlagg(\n"
                + "       xmlelement(\n"
                + "                  name \"category\",\n"
                + "                  xmlforest (\n"
                + "                       ctr.id AS \"id\",\n"
                + "                       COALESCE(ctr.name, '') AS \"name\",\n"
                + "                       COALESCE(ctr.parent_id, 0) AS \"parent_id\",\n"
                + "                       ctr.depth AS \"depth\"\n"
                + "                   )\n"
                + "       )\n"
                + "       )\n"
                + "       )\n"
                + "      FROM \n"
                + "         ctTree ctr \n"
                + "      WHERE \n"
                + "      ctr.stock_id = tt.stckid\n"
                + "   ) AS category\n"
                + "FROM\n"
                + "(SELECT \n"
                + "   DISTINCT stck.id AS stckid,\n"
                + "   stck.barcode as stckbarcode,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.code AS stckcode,\n"
                + "   stck.centerproductcode AS stckcenterproductcode,\n"
                + "   stck.country_id as stckcountry_id, \n"
                + "   ctryd.name as ctrydname, \n"
                + "   sttd.status_id AS sttdid,\n"
                + "   sttd.name AS sttdname,\n"
                + "   stck.supplier_id AS stcksupplier_id,\n"
                + "   acc.name AS accname, \n"
                + "   stck.supplierproductcode AS stcksupplierproductcode, \n"
                + "   stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "   cspp.name AS csppname,\n"
                + "   stck.centralsupplierproductcode AS stckcentralsupplierproductcode, \n"
                + "   stck.type_id AS stcktype_id, \n"
                + "   gunt.id AS guntid,\n"
                + "   gunt.name AS guntname,\n"
                + "   gunt.sortname AS guntsortname,\n"
                + "   gunt.unitrounding as guntunitrounding,\n"
                + "   br.id AS brid,\n"
                + "   br.name AS brname,        \n"
                + "   si.minstocklevel as siminstocklevel,\n"
                + "   COALESCE(si.recommendedprice,0) as sirecommendedprice,\n"
                + "   si.is_quicksale as siis_quicksale,\n"
                + "   si.currency_id as sicurrency_id,\n"
                + "   COALESCE(si.purchaserecommendedprice,0) as sipurchaserecommendedprice,\n"
                + "   si.purchasecurrency_id as sipurchasecurrency_id,\n"
                + "   si.minprofitrate as siminprofitrate,\n"
                + "   si.purchasecontroldate as sipurchasecontroldate,\n"
                + "   si.is_fuel as siis_fuel,\n"
                + "   si.fuelintegrationcode as sifuelintegrationcode,\n"
                + "   si.weight as siweight,\n"
                + "   si.weightunit_id as siweightunit_id,\n"
                + "   si.mainweight as simainweight,\n"
                + "   si.mainweightunit_id as simainweightunit_id,\n"
                + "   COALESCE(si.salemandatoryprice,0) as sisalemandatoryprice,\n"
                + "   COALESCE(si.salemandatorycurrency_id,0) as sisalemandatorycurrency_id,\n"
                + "   COALESCE(si.currentpurchaseprice,0) as sicurrentpurchaseprice, \n"
                + "   COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)) as sicurrentpurchasepricewithkdv, \n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100))) as availablepurchasepricewithkdv,\n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentpurchaseprice,0)) as availablepurchasepricewithoutkdv,\n"
                + "   si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id, \n"
                + "   COALESCE(si.currentsaleprice,0) as sicurrentsaleprice, \n"
                + "   COALESCE(si.currentsaleprice,0)/(1+(COALESCE(stg.rate,0)/100)) as sicurrentsalepricewithoutkdv,\n"
                + "   COALESCE(si.balance,0)*COALESCE(si.currentsaleprice,0) as availablesalepricewithkdv, \n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentsaleprice,0)/(1+(COALESCE(stg.rate,0)/100))) as availablesalepricewithoutkdv, \n"
                + "   si.currentsalecurrency_id as sicurrentsalecurrency_id, \n"
                + "   COALESCE(si.salecount,0) as sisalecount,\n"
                + "   COALESCE(si.purchasecount,0) as sipurchasecount,\n"
                + "   CASE WHEN COALESCE(si.currentsaleprice,0)=0 OR COALESCE(si.currentpurchaseprice,0) = 0 THEN 0\n"
                + "   ELSE (COALESCE(si.currentsaleprice,0) - (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100))))/(COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)))*100 END AS profitpercentage,\n"
                + "   pli.price as purchaseprice,\n"
                + "   pli.currency_id as purchasecurrency_id ,\n"
                + "   pli.is_taxincluded as purchaseis_taxincluded,\n"
                + "   pli2.price as saleprice,\n"
                + "   pli2.currency_id as salecurrency_id,\n"
                + "   pli2.is_taxincluded as saleis_taxincluded,\n"
                + "   cryd.name as crydname,\n"
                + "   stck.is_service as stckis_service,\n"
                + "   stck.is_get AS stckis_get,\n"
                + "   stck.c_id AS stckc_id,\n"
                + "   stck.c_time AS stckc_time,\n"
                + "   COALESCE(stg.rate,0) salekdv,\n"
                + "   COALESCE(ptg.rate,0) purchasekdv,\n"
                + "   COALESCE(si.balance,0) AS availablequantity,\n"
                + "   COALESCE(si.balance,0)-(COALESCE(si.purchasecount,0)-COALESCE(si.salecount,0)) AS otherquantity,\n"
                + "   usr.username AS usrusername,\n"
                + "   usr.name as usrname,\n"
                + "   usr.surname as usrsurname,\n"
                + "   stck.description as stckdescription, \n"
                + "   gunt.centerunit_id as guntcenterunit_id,\n"
                + "   stck.centerstock_id as stckcenterstock_id,\n"
                + "   si.maxstocklevel as simaxstocklevel,\n "
                + "   COALESCE(si.balance,0) as sibalance,\n"
                + "   si.taxdepartment_id AS sitaxdepartment_id,\n"
                + "   si.is_minusstocklevel AS siis_minusstocklevel,\n"
                + "   si.turnoverpremium AS siturnoverpremium,\n"
                + "   stck.boxquantity AS stckboxquantity,\n"
                + "   si.shelfquantity AS sishelfquantity,\n"
                + "   si.stockenoughday AS  sistockenoughday,\n"
                + "   si.minfactorvalue AS siminfactorvalue,\n"
                + "   si.maxfactorvalue AS simaxfactorvalue,\n"
                + "   si.warehousestockdivisorvalue AS siwarehousestockdivisorvalue,\n"
                + "   si.incomeexpense_id AS siincomeexpense_id,\n"
                + "   fie.is_income AS fieis_income,\n"
                + "   fie.name AS fiename,\n"
                + "   pl2.id AS pl2id,\n"
                + "   pli2.id AS pli2id,\n"
                + "   si.einvoiceintegrationcode as sieinvoiceintegrationcode, \n"
                + "   si.is_passive AS sii_passive,\n"
                + "   si.is_delist AS stckiis_delist, \n"
                + "   si.is_campaign AS siis_campaign, \n"
                + "   si.orderdeliveryrate AS siorderdeliveryrate, \n"
                + "   txd.name AS txdname,\n"
                + "   si.orderdeliverysaleprice AS siorderdeliverysaleprice, \n"
                + "   si.orderdeliverysalecurrency_id AS siorderdeliverysalecurrency_id \n"
                + "FROM inventory.stock stck   \n"
                + "LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.brand br   ON (br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN system.country_dict ctryd ON(ctryd.country_id = stck.country_id AND ctryd.language_id = ?)\n"
                + "INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + "LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=si.currency_id AND cryd.language_id = ?) \n"
                + "LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=TRUE AND pl.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                + "LEFT JOIN inventory.pricelist pl2 ON (pl2.branch_id=? AND pl2.is_default=TRUE AND pl2.is_purchase=FALSE AND pl2.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli2 ON (pli2.stock_id=stck.id AND pli2.pricelist_id=pl2.id AND pli2.deleted=False)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN finance.incomeexpense fie ON(fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id=si.branch_id) \n"
                + "LEFT JOIN inventory.taxdepartment txd ON(txd.id=si.taxdepartment_id AND txd.deleted=FALSE) \n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "INNER JOIN general.userdata usr  ON (usr.id = stck.c_id)\n"
                + "WHERE stck.deleted = false " + where + "\n"
                + ") tt\n"
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new StockMapper());
    }

    @Override
    public int count(String where) {
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT \n"
                + "	COUNT(DISTINCT stck.id) AS stckid \n"
                + "FROM  inventory.stock stck  \n"
                + "LEFT JOIN general.unit gunt  ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.brand br  ON (br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN system.country_dict ctryd ON(ctryd.country_id = stck.country_id AND ctryd.language_id = ?)\n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=si.currency_id AND cryd.language_id = ?) \n"
                + "LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=TRUE AND pl.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                + "LEFT JOIN inventory.pricelist pl2 ON (pl2.branch_id=? AND pl2.is_default=TRUE AND pl2.is_purchase=FALSE AND pl2.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli2 ON (pli2.stock_id=stck.id AND pli2.pricelist_id=pl2.id AND pli2.deleted=False)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN finance.incomeexpense fie ON(fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id=si.branch_id) \n"
                + "LEFT JOIN inventory.taxdepartment txd ON(txd.id=si.taxdepartment_id AND txd.deleted=FALSE) \n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "INNER JOIN general.userdata usr  ON (usr.id = stck.c_id)\n"
                + "WHERE stck.deleted = false " + where;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    /**
     * Bu methot stok sayfasındaki alt toplamları hesaplar.
     *
     * @param where
     * @return
     */
    @Override
    public List<Stock> totals(String where) {
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT \n"
                + "	 COUNT(tt.stckid) AS stckid,\n"
                + "    COALESCE((SUM(COALESCE(tt.sipurchasecount,0))),0) AS sipurchasecount,\n"
                + "    COALESCE((SUM(COALESCE(tt.sisalecount,0))),0) AS sisalecount,\n"
                + "    COALESCE(SUM(tt.availablequantity),0) AS availablequantity,\n"
                + "    SUM(COALESCE(tt.availablequantity,0)-(COALESCE(tt.sipurchasecount,0)-COALESCE(tt.sisalecount,0))) AS otherquantity,\n"
                + "    SUM(COALESCE(tt.sicurrentpurchasepricewithoutkdv,0)) as sicurrentpurchasepricewithoutkdv,\n"
                + "    SUM(COALESCE(tt.sicurrentpurchasepricewithoutkdv,0)*(1+(COALESCE(tt.ptgrate,0)/100))) as sicurrentpurchasepricewithkdv, \n"
                + "    SUM(COALESCE(tt.availablequantity,0)*(COALESCE(tt.sicurrentpurchasepricewithoutkdv,0)*(1+(COALESCE(tt.ptgrate,0)/100)))) as availablepurchasepricewithkdv,\n"
                + "    SUM(COALESCE(tt.availablequantity,0)*(COALESCE(tt.sicurrentpurchasepricewithoutkdv,0))) as availablepurchasepricewithoutkdv,\n"
                + "    SUM(COALESCE(tt.sicurrentsalepricewithkdv,0)) as sicurrentsalepricewithkdv, \n"
                + "    SUM(COALESCE(tt.sicurrentsalepricewithkdv,0)/(1+(COALESCE(tt.stgrate,0)/100))) as sicurrentsalepricewithoutkdv,\n"
                + "    SUM(COALESCE(tt.availablequantity,0)*COALESCE(tt.sicurrentsalepricewithkdv,0)) as availablesalepricewithkdv, \n"
                + "    SUM(COALESCE(tt.availablequantity,0)*(COALESCE(tt.sicurrentsalepricewithkdv,0)/(1+(COALESCE(tt.stgrate,0)/100)))) as availablesalepricewithoutkdv, \n"
                + "    tt.guntid AS guntid,\n"
                + "    tt.guntsotname AS guntsotname,\n"
                + "    tt.sicurrentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                + "    tt.sicurrentsalecurrency_id AS sicurrentsalecurrency_id\n"
                + " FROM\n"
                + " (SELECT \n"
                + "   DISTINCT stck.id AS stckid, \n"
                + "   COALESCE(si.purchasecount, 0) AS sipurchasecount,\n"
                + "   COALESCE(si.salecount,0) AS sisalecount,\n"
                + "   COALESCE(si.balance) AS availablequantity, \n"
                + "   COALESCE(si.currentpurchaseprice,0) as sicurrentpurchasepricewithoutkdv,\n"
                + "   COALESCE(ptg.rate,0) AS ptgrate, \n"
                + "   COALESCE(si.currentsaleprice, 0) AS sicurrentsalepricewithkdv,\n"
                + "   COALESCE(stg.rate,0) AS stgrate,\n"
                + "   stck.unit_id AS guntid,\n"
                + "   gunt.sortname AS guntsotname,\n"
                + "   si.currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                + "   si.currentsalecurrency_id AS sicurrentsalecurrency_id\n"
                + "FROM  inventory.stock stck  \n"
                + "LEFT JOIN general.unit gunt  ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.brand br  ON (br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN system.country_dict ctryd ON(ctryd.country_id = stck.country_id AND ctryd.language_id = ?)\n"
                + "INNER JOIN system.status_dict sttd  ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=si.currency_id AND cryd.language_id = ?) \n"
                + "LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=TRUE AND pl.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                + "LEFT JOIN inventory.pricelist pl2 ON (pl2.branch_id=? AND pl2.is_default=TRUE AND pl2.is_purchase=FALSE AND pl2.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli2 ON (pli2.stock_id=stck.id AND pli2.pricelist_id=pl2.id AND pli2.deleted=False)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN finance.incomeexpense fie ON(fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id=si.branch_id) \n"
                + "LEFT JOIN inventory.taxdepartment txd ON(txd.id=si.taxdepartment_id AND txd.deleted=FALSE) \n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "INNER JOIN general.userdata usr  ON (usr.id = stck.c_id)\n"
                + "WHERE stck.deleted = false " + where + "\n"
                + ") tt\n"
                + "GROUP BY tt.guntid, tt.guntsotname, tt.sicurrentpurchasecurrency_id, tt.sicurrentsalecurrency_id";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        return getJdbcTemplate().query(sql, param, new StockMapper());
    }

    @Override
    public int create(Stock obj, boolean isAvailableStock) {

        String sql = " SELECT r_stock_id FROM inventory.process_stock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? , ?);";
//        String sql = "INSERT INTO \n"
//                + "  inventory.stock\n"
//                + "(\n"
//                + "  barcode,\n"
//                + "  name,\n"
//                + "  code,\n"
//                + "  centerproductcode,\n"
//                + "  status_id,\n"
//                + "  unit_id,\n"
//                + "  brand_id,\n"
//                + "  minstocklevel,\n"
//                + "  recommendedprice,\n"
//                + "  is_service,\n"
//                + "  is_quicksale,\n"
//                + "  description, \n"
//                + "  c_id\n"
//                + "  ordereliverysaleprice ,\n"
//                + "  orderdeliverycurrency_id ,\n"
//                + ")\n"
//                + "VALUES (\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?,\n"
//                + "  ?, \n"
//                + "  ?\n"
//                + ") RETURNING id ;";

        Object[] param = new Object[]{0, obj.getId(), obj.getBarcode(), obj.getName(), obj.getCode(), obj.getStatus().getId(), obj.getUnit().getId(),
            obj.getBrand().getId() == 0 ? null : obj.getBrand().getId(), obj.getSupplier().getId() == 0 ? null : obj.getSupplier().getId(), obj.getSupplierProductCode(), obj.isIsService(), obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getDescription(), obj.getCenterProductCode(), sessionBean.getUser().getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getStockInfo().getRecommendedPrice(), obj.getStockInfo().getCurrency().getId() == 0 ? null : obj.getStockInfo().getCurrency().getId(), obj.getStockInfo().getPurchaseRecommendedPrice(), obj.getStockInfo().getPurchaseCurrency().getId() == 0 ? null : obj.getStockInfo().getPurchaseCurrency().getId(), obj.getStockInfo().getMinProfitRate(), obj.getStockInfo().isIsQuickSale(), obj.getStockInfo().getMinStockLevel(),
            obj.getStockInfo().getMaxStockLevel(), obj.getStockInfo().getPurchaseControlDate(), obj.getStockInfo().isIsFuel(), "".equals(obj.getStockInfo().getFuelIntegrationCode()) ? null : obj.getStockInfo().getFuelIntegrationCode(),
            obj.getStockInfo().getWeight(), obj.getStockInfo().getWeightUnit().getId() == 0 ? null : obj.getStockInfo().getWeightUnit().getId(), obj.getStockInfo().getMainWeight(), obj.getStockInfo().getMainWeightUnit().getId() == 0 ? null : obj.getStockInfo().getMainWeightUnit().getId(), obj.getStockInfo().getTaxDepartment().getId(), obj.getStockInfo().isIsMinusStockLevel(), isAvailableStock, obj.getStockInfo().getTurnoverPremium(),
            obj.getStockInfo().getIncomeExpense().getId() == 0 ? null : obj.getStockInfo().getIncomeExpense().getId(), obj.getStockType_id(), obj.getStockInfo().geteInvoiceIntegrationCode(),
            obj.getStockInfo().getOrderDeliverySalePrice(), obj.getStockInfo().getOrderDeliverySaleCurrency().getId() == 0 ? null : obj.getStockInfo().getOrderDeliverySaleCurrency().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Stock obj, boolean isAvailableStock) {
//        String sql = "UPDATE \n"
//                + "  inventory.stock \n"
//                + "SET \n"
//                + "  barcode = ?,\n"
//                + "  name = ?,\n"
//                + "  code = ?,\n"
//                + "  centerproductcode = ?,\n"
//                + "  status_id = ?,\n"
//                + "  unit_id = ?,\n"
//                + "  brand_id = ?,\n"
//                + "  minstocklevel = ?,\n"
//                + "  recommendedprice = ?,\n"
//                + "  is_service = ?,\n"
//                + "  is_quicksale = ?,\n"
//                + "  description= ? ,\n"
//                + "  u_id = ?,\n"
//                + "  u_time = now()\n"
//                + "  orderdeliverysaleprice = ?,\n"
//                + "  orderdeliverycurrency_id = ?,\n"
//                + "WHERE \n"
//                + "  id = ? ;";

        String sql = " SELECT r_stock_id FROM inventory.process_stock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?);";

        Object[] param = new Object[]{1, obj.getId(), obj.getBarcode(), obj.getName(), obj.getCode(), obj.getStatus().getId(), obj.getUnit().getId(),
            obj.getBrand().getId() == 0 ? null : obj.getBrand().getId(), obj.getSupplier().getId() == 0 ? null : obj.getSupplier().getId(), obj.getSupplierProductCode(), obj.isIsService(), obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getDescription(), obj.getCenterProductCode(), sessionBean.getUser().getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getStockInfo().getRecommendedPrice(), obj.getStockInfo().getCurrency().getId() == 0 ? null : obj.getStockInfo().getCurrency().getId(), obj.getStockInfo().getPurchaseRecommendedPrice(), obj.getStockInfo().getPurchaseCurrency().getId() == 0 ? null : obj.getStockInfo().getPurchaseCurrency().getId(), obj.getStockInfo().getMinProfitRate(), obj.getStockInfo().isIsQuickSale(), obj.getStockInfo().getMinStockLevel(),
            obj.getStockInfo().getMaxStockLevel(), obj.getStockInfo().getPurchaseControlDate(), obj.getStockInfo().isIsFuel(), "".equals(obj.getStockInfo().getFuelIntegrationCode()) ? null : obj.getStockInfo().getFuelIntegrationCode(),
            obj.getStockInfo().getWeight(), obj.getStockInfo().getWeightUnit().getId() == 0 ? null : obj.getStockInfo().getWeightUnit().getId(), obj.getStockInfo().getMainWeight(), obj.getStockInfo().getMainWeightUnit().getId() == 0 ? null : obj.getStockInfo().getMainWeightUnit().getId(), obj.getStockInfo().getTaxDepartment().getId(), obj.getStockInfo().isIsMinusStockLevel(), isAvailableStock, obj.getStockInfo().getTurnoverPremium(),
            obj.getStockInfo().getIncomeExpense().getId() == 0 ? null : obj.getStockInfo().getIncomeExpense().getId(), obj.getStockType_id(), obj.getStockInfo().geteInvoiceIntegrationCode(),
            obj.getStockInfo().getOrderDeliverySalePrice(), obj.getStockInfo().getOrderDeliverySaleCurrency().getId() == 0 ? null : obj.getStockInfo().getOrderDeliverySaleCurrency().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateDetail(Stock obj) {

        String sql = " SELECT r_stock_id FROM inventory.process_stock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? , ?);";

        Object[] param = new Object[]{2, obj.getId(), obj.getBarcode(), obj.getName(), obj.getCode(), obj.getStatus().getId(), obj.getUnit().getId(),
            obj.getBrand().getId() == 0 ? null : obj.getBrand().getId(), obj.getSupplier().getId() == 0 ? null : obj.getSupplier().getId(), obj.getSupplierProductCode(), obj.isIsService(), obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getDescription(), obj.getCenterProductCode(), sessionBean.getUser().getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getStockInfo().getRecommendedPrice(), obj.getStockInfo().getCurrency().getId() == 0 ? null : obj.getStockInfo().getCurrency().getId(), obj.getStockInfo().getPurchaseRecommendedPrice(), obj.getStockInfo().getPurchaseCurrency().getId() == 0 ? null : obj.getStockInfo().getPurchaseCurrency().getId(), obj.getStockInfo().getMinProfitRate(), obj.getStockInfo().isIsQuickSale(), obj.getStockInfo().getMinStockLevel(),
            obj.getStockInfo().getMaxStockLevel(), obj.getStockInfo().getPurchaseControlDate(), obj.getStockInfo().isIsFuel(), "".equals(obj.getStockInfo().getFuelIntegrationCode()) ? null : obj.getStockInfo().getFuelIntegrationCode(),
            obj.getStockInfo().getWeight(), obj.getStockInfo().getWeightUnit().getId() == 0 ? null : obj.getStockInfo().getWeightUnit().getId(), obj.getStockInfo().getMainWeight(), obj.getStockInfo().getMainWeightUnit().getId() == 0 ? null : obj.getStockInfo().getMainWeightUnit().getId(), obj.getStockInfo().getTaxDepartment().getId(), obj.getStockInfo().isIsMinusStockLevel(), false,
            obj.getStockInfo().getTurnoverPremium(), obj.getStockInfo().getIncomeExpense().getId() == 0 ? null : obj.getStockInfo().getIncomeExpense().getId(), obj.getStockType_id(), obj.getStockInfo().geteInvoiceIntegrationCode(),
            obj.getStockInfo().getOrderDeliverySalePrice(), obj.getStockInfo().getOrderDeliverySaleCurrency().getId() == 0 ? null : obj.getStockInfo().getOrderDeliverySaleCurrency().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Stock> stockBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        List<Stock> result = new ArrayList<>();
        String whereBranch = "";
        int warehouseId = 0;
        String join = "";
        String column = "";
        String whereAlter = " ";
        Boolean isPurchase;
        String joinIncome = "";

        BranchSetting branchSetting = new BranchSetting();
        if (type.equals("waybillstock") || type.equals("waybillservice") || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")) {
            branchSetting.setIsCentralIntegration(((Waybill) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Waybill) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Waybill) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("salereturn")) {
            branchSetting.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());
            branchSetting.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
        } else if (type.equals("stock") || type.equals("service") || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")) {
            branchSetting.setIsCentralIntegration(((Invoice) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Invoice) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Invoice) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("einvoicestock")) {
            branchSetting.setIsCentralIntegration(((EInvoice) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((EInvoice) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((EInvoice) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("ordermanuel")) {
            branchSetting.setIsCentralIntegration(((Order) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Order) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Order) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("invoicePage") || type.equals("orderCheckBox")) {
            int count = 0;
            String branchID = "";
            for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                if (((List<BranchSetting>) param.get(0)).get(i).isIsCentralIntegration()) {
                    count++;
                }
                branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
            }
            branchID = branchID.substring(3, branchID.length());
            whereBranch = "AND si.branch_id =" + sessionBean.getUser().getLastBranch().getId() + "";

            if (((List<BranchSetting>) param.get(0)).size() > 0) {
                if (count >= 1 && count < ((List<BranchSetting>) param.get(0)).size()) {
                    where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                            + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                            + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                            + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) ";
                } else if (count == ((List<BranchSetting>) param.get(0)).size()) {
                    where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                            + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                            + "AND  si1.is_valid  =TRUE) ";
                    whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                } else if (count == 0) {
                    where = where + " AND stck.is_otherbranch = TRUE ";
                }
            }
        } else if (type.equals("reportproductsoldtogether")) {
            branchSetting.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());
            branchSetting.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
        } else {
            branchSetting.setIsCentralIntegration(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(sessionBean.getUser().getLastBranch().getId());
        }

        if (!type.equals("reportcheckboxwithbranch") && !type.equals("invoicePage") && !type.equals("orderCheckBox")) {
            if (branchSetting.isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE ";
                whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
            } else {
                where = where + " AND stck.is_otherbranch = TRUE ";
            }
            whereBranch = "AND si.branch_id=" + branchSetting.getBranch().getId() + "";
        }

        if (type.equals("stock") || type.equals("service") || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")) {
            if (type.equals("stock") || type.equals("service")) {
                if (!((Invoice) param.get(0)).isIsFuel()) {
                    warehouseId = Integer.valueOf(((Invoice) param.get(0)).getWarehouseIdList());
                    join = "LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id = " + warehouseId + ") \n";
                    column = "   COALESCE(wrh.quantity,0) as availablequantity,\n"
                            + "   si.is_minusstocklevel AS siis_minusstocklevel,\n";

                }
            }
            if (type.equals("stock") && ((Invoice) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            isPurchase = ((Invoice) param.get(0)).isIsPurchase();
            if (isPurchase) {
                joinIncome = joinIncome + " LEFT JOIN finance.incomeexpense fie ON (fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id = si.branch_id ) \n";
                where = where + " AND (CASE WHEN si.incomeexpense_id IS NOT NULL THEN fie.is_income =FALSE ELSE TRUE END)\n";
                where = where + " AND si.is_delist = FALSE \n";

            } else {
                joinIncome = joinIncome + " LEFT JOIN finance.incomeexpense fie ON (fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id = si.branch_id ) \n";
                where = where + " AND (CASE WHEN si.incomeexpense_id IS NOT NULL THEN fie.is_income =TRUE ELSE TRUE END)\n";

            }

            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((Invoice) param.get(0)).isIsPurchase()) {
                    if (type.equals("stock")) {
                        where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                                + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                                + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";
                    }

                }

            }

        } else if (type.equals("waybillstock") || type.equals("waybillservice") || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")) {
            if (type.equals("waybillstock") || type.equals("waybillservice")) {
                if (!((Waybill) param.get(0)).isIsFuel()) {
                    warehouseId = Integer.valueOf(((Waybill) param.get(0)).getWarehouseIdList());
                    join = "LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id = " + warehouseId + ") \n";
                    column = "   COALESCE(wrh.quantity,0) as availablequantity,\n"
                            + "   si.is_minusstocklevel AS siis_minusstocklevel,\n";
                }
            }
            if (((Waybill) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((Waybill) param.get(0)).isIsPurchase()) {
                    if (type.equals("waybillstock")) {
                        where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                                + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                                + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";
                    }
                }
            }

            if (((Waybill) param.get(0)).isIsPurchase()) {
                where = where + " AND si.is_delist = FALSE \n";
            }

        } else if (type.equals("warehousereceipt")) {
            warehouseId = ((WarehouseReceipt) param.get(0)).getWarehouse().getId();
            join = "LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id = " + warehouseId + ") \n";
            column = "   COALESCE(wrh.quantity,0) as availablequantity,\n"
                    + "   si.is_minusstocklevel AS siis_minusstocklevel,\n";

            if (((WarehouseReceipt) param.get(0)).isIsDirection() == true) {
                where = where + " AND si.is_delist = FALSE \n";
            }

        } else if (type.equals("einvoicestock")) {
            String whereUnitCode = "";
            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((EInvoice) param.get(0)).isIsPurchase()) {
                    where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                            + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                            + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";

                }
            }
            if (((Invoice) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            if (((String) param.get(1)) != null && !((String) param.get(1)).isEmpty()) {
                whereUnitCode = " AND seiuc.stockintegrationcode =  '" + ((String) param.get(1)) + "' ";
            }

            where = where + " AND si.is_delist = FALSE \n";

            join = join + " LEFT JOIN inventory.stock_einvoice_unit_con seiuc ON(seiuc.stock_id = stck.id AND seiuc.deleted = FALSE " + whereUnitCode + " ) \n";
            column = column + "    seiuc.id as seiucid,\n"
                    + "    seiuc.stockintegrationcode as seiucstockintegrationcode,\n"
                    + "    seiuc.quantity as seiucquantity,\n"
                    + "    seiuc.stock_id as seiucstock_id, \n"
                    + "    gunt.internationalcode as guntinternationalcode, \n"
                    + "    si.einvoiceintegrationcode as sieinvoiceintegrationcode, \n";

        }

        //Pasif ürünler gelmesin 
        if (type.equals("stock") || type.equals("service") || type.equals("waybillstock") || type.equals("waybillservice")
                || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")
                || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")
                || type.equals("automationdevice") || type.equals("pricelist") || type.equals("stockpricerequest")
                || type.equals("warehouseshelf") || type.equals("einvoicestock")
                || type.equals("discountitem") || type.equals("stockbatchupdate") || type.equals("warehouseitem") || type.equals("automationdeviceforcoffee")) {
            where = where + " AND stck.status_id <> 4 AND si.is_passive = FALSE ";
        }

        switch (type) {
            case "warehouse":
                where = where + " and stck.id NOT IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " and iwi.deleted=false)";
                break;
            case "warehouseitem":
                where = where + " and stck.id NOT IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " and iwi.deleted=false)";
                break;
            case "warehouseshelf":
                where = where + " and stck.id IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((WarehouseShelf) param.get(0)).getWareHouse().getId() + " and iwi.deleted=false)"
                        + " and stck.id NOT IN (select wssc.stock_id from inventory.warehouseshelf_stock_con wssc where wssc.warehouseshelf_id = " + ((WarehouseShelf) param.get(0)).getId() + " and wssc.deleted=false)  ";
                break;
            case "warehousereceipt":
                where = where + " and stck.id NOT IN ( SELECT whm.stock_id FROM inventory.warehousemovement whm WHERE whm.warehousereceipt_id= " + ((WarehouseReceipt) param.get(0)).getId() + " AND whm.deleted = FALSE )";
                if (((WarehouseReceipt) param.get(0)).isIsDirection() == false) {

                    where = where + " and stck.id IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((WarehouseReceipt) param.get(0)).getWarehouse().getId() + " and iwi.deleted=false)";
                    column = column + "   COALESCE(ptg.rate,0) as taxrate,\n";
                    join = join + "left join (SELECT \n"
                            + "                              txg.rate AS rate,\n"
                            + "                              stc.stock_id AS stock_id \n"
                            + "                              FROM inventory.stock_taxgroup_con stc  \n"
                            + "                              INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                            + "                              WHERE stc.deleted = false\n"
                            + "                              AND txg.type_id = 10 --kdv grubundan \n"
                            + "                              AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id) \n";
                }
                break;
            case "service": //sadece hizmet stokları
            case "waybillservice":
                where = where + " AND stck.is_service = TRUE ";
                break;
            case "stock": //sadece hizmet olmayan normal ürünler
            case "waybillstock":
                where = where + " AND stck.is_service = FALSE ";
                break;
            case "ordermanuel":
                where = where + " AND stck.is_service = FALSE AND si.is_delist = FALSE ";
                where = where + " AND stck.supplier_id = " + ((Order) param.get(0)).getAccount().getId() + " ";
                where = where + " AND COALESCE(cspp.is_autoordercreate,FALSE) = TRUE ";

                String orderIds = "";
                for (OrderItem orderItem : (List<OrderItem>) param.get(1)) {
                    orderIds = orderIds + orderItem.getStock().getId() + ",";
                }
                if (!orderIds.equals("")) {
                    orderIds = orderIds.substring(0, orderIds.length() - 1);
                    where = where + " AND stck.id NOT IN(" + orderIds + ") ";
                }

                break;
            case "einvoicestock": //sadece hizmet olmayan normal ürünler
                where = where + " AND stck.is_service = FALSE ";
                break;
            case "service_warehouse":
                where = where + " AND stck.is_service = TRUE AND stck.id IN (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " AND iwi.deleted = FALSE )";
                break;
            case "stock_warehouse":
                where = where + " AND stck.is_service = FALSE AND stck.id IN (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " AND iwi.deleted = FALSE )";
                break;
            case "pricelist":
                where = where + "and stck.id NOT IN (SELECT pli.stock_id FROM inventory.pricelistitem pli WHERE pli.pricelist_id = " + ((PriceList) param.get(0)).getId() + " AND pli.deleted = FALSE)";
                break;
            case "stockpricerequest":
                where = where + "AND COALESCE( si.salemandatoryprice ,0)>0 ";
                break;
            case "stockbatchupdate":
                if ((boolean) param.get(1)) {

                    List<Categorization> list = new ArrayList<>();
                    list = (ArrayList) param.get(0);
                    if (!list.isEmpty()) {
                        if (list.get(0).getId() != 0) {
                            String categories = "";
                            for (Categorization categorization : list) {
                                categories = categories + "," + String.valueOf(categorization.getId());
                            }

                            if (!categories.equals("")) {
                                categories = categories.substring(1, categories.length());
                                where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + categories + ") ) ";
                            }

                        }
                    }
                }
                break;
            case "reportcheckboxwithbranch":
                BranchSetting branch = null;
                whereBranch = "";
                if (param.size() > 0) {
                    if (param.get(0) instanceof ArrayList) {
                        int count = 0;
                        String branchID = "";
                        for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                            if (((List<BranchSetting>) param.get(0)).get(i).isIsCentralIntegration()) {
                                count++;
                            }
                            branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                        }
                        branchID = branchID.substring(3, branchID.length());
                        whereBranch = "AND si.branch_id =" + sessionBean.getUser().getLastBranch().getId() + "";

                        if (((List<BranchSetting>) param.get(0)).size() > 0) {
                            if (count >= 1 && count < ((List<BranchSetting>) param.get(0)).size()) {
                                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                        + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                                        + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                        + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) ";
                            } else if (count == ((List<BranchSetting>) param.get(0)).size()) {
                                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                        + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                        + "AND  si1.is_valid  =TRUE) ";
                                whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                            } else if (count == 0) {
                                where = where + " AND stck.is_otherbranch = TRUE ";
                            }
                        }

                    } else if (param.get(0) instanceof BranchSetting) {
                        branch = new BranchSetting();
                        branch.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
                        branch.getBranch().setName(((BranchSetting) param.get(0)).getBranch().getName());
                        branch.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());

                        if (branch.isIsCentralIntegration()) {
                            where = where + " AND si.is_valid = TRUE ";
                            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                        } else {
                            where = where + " AND stck.is_otherbranch = TRUE ";
                        }
                        whereBranch = "AND si.branch_id=" + branch.getBranch().getId() + "";

                    }

                }

                break;
            case "automationdeviceforcoffee":
                where = where + "AND stck.type_id = 3 ";
                break;
            case "stockCreateInvFromOrder":
            case "stockCreateWaybillFromOrder":
                where = where + " AND stck.is_service = FALSE AND si.is_campaign = TRUE ";
                break;
            case "serviceCreateInvFromOrder":
            case "serviceCreateWaybillFromOrder":
                where = where + " AND stck.is_service = TRUE ";
                break;
            case "washingmachineservice":
                where = where + " AND stck.is_service = TRUE AND stck.status_id <> 4 AND si.is_passive = FALSE";
                break;
            default:
                break;
        }

        String sql = "SELECT \n"
                + "   DISTINCT stck.id AS stckid,\n"
                + "   stck.barcode as stckbarcode,\n"
                + "   stck.centerproductcode AS stckcenterproductcode,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.code AS stckcode,\n"
                + "   stck.supplier_id AS stcksupplier_id,\n"
                + "   acc.name AS accname, \n"
                + "   stck.supplierproductcode AS stcksupplierproductcode, \n"
                + "   stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "   cspp.name AS csppname,\n"
                + "   stck.centralsupplierproductcode AS stckcentralsupplierproductcode, \n"
                + "   sttd.status_id AS sttdid,\n"
                + "   sttd.name AS sttdname,\n"
                + "   si.recommendedprice as sirecommendedprice,\n"
                + "   si.currency_id as sicurrency_id,\n"
                + "   si.minprofitrate as siminprofitrate,\n"
                + "   si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                + "   si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                + "   COALESCE(si.currentsaleprice,0) as sicurrentsaleprice,\n"
                + "   si.currentsalecurrency_id as sicurrentsalecurrency_id,\n"
                + "   si.purchasecontroldate as sipurchasecontroldate,\n "
                + "  COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "  COALESCE(  si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id,\n"
                + "   si.is_fuel as siis_fuel,\n"
                + "   COALESCE(si.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                + "   si.purchasecurrency_id AS sipurchasecurrency_id,\n"
                + "   gunt.id AS guntid,\n"
                + "   gunt.name AS guntname,\n"
                + "   gunt.sortname AS guntsortname,\n"
                + "   gunt.unitrounding as guntunitrounding,\n"
                + "   gunt.centerunit_id as guntcenterunit_id,\n"
                + "   stck.centerstock_id as stckcenterstock_id,\n"
                + column + "\n"
                + "   si.maxstocklevel as simaxstocklevel,\n "
                + "   COALESCE(si.balance,0) as sibalance \n"
                + "FROM inventory.stock stck   \n"
                + "LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + join + "\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False " + whereBranch + ")\n"
                + joinIncome + "\n"
                + "WHERE stck.deleted = false " + where + "\n"
                + "ORDER BY stck.name \n"
                + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};
        result = getJdbcTemplate().query(sql, params, new StockMapper());
        return result;
    }

    @Override
    public int stockBookCount(String where, String type, List<Object> param) {
        String whereBranch = "";
        String whereAlter = " ";
        Boolean isPurchase;
        String joinIncome = "";
        String join = "";
        BranchSetting branchSetting = new BranchSetting();
        if (type.equals("waybillstock") || type.equals("waybillservice") || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")) {
            branchSetting.setIsCentralIntegration(((Waybill) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Waybill) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Waybill) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("salereturn")) {
            branchSetting.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());
            branchSetting.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
        } else if (type.equals("stock") || type.equals("service") || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")) {
            branchSetting.setIsCentralIntegration(((Invoice) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Invoice) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Invoice) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("ordermanuel")) {
            branchSetting.setIsCentralIntegration(((Order) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((Order) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((Order) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("einvoicestock")) {
            branchSetting.setIsCentralIntegration(((EInvoice) param.get(0)).getBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(((EInvoice) param.get(0)).getBranchSetting().getBranch().getId());
            branchSetting.setIsInvoiceStockSalePriceList(((EInvoice) param.get(0)).getBranchSetting().isIsInvoiceStockSalePriceList());
        } else if (type.equals("invoicePage") || type.equals("orderCheckBox")) {
            int count = 0;
            String branchID = "";
            for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                if (((List<BranchSetting>) param.get(0)).get(i).isIsCentralIntegration()) {
                    count++;
                }
                branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
            }
            branchID = branchID.substring(3, branchID.length());
            whereBranch = "AND si.branch_id =" + sessionBean.getUser().getLastBranch().getId() + "";

            if (((List<BranchSetting>) param.get(0)).size() > 0) {
                if (count >= 1 && count < ((List<BranchSetting>) param.get(0)).size()) {
                    where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                            + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                            + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                            + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) ";
                } else if (count == ((List<BranchSetting>) param.get(0)).size()) {
                    where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                            + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                            + "AND  si1.is_valid  =TRUE) ";
                    whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                } else if (count == 0) {
                    where = where + " AND stck.is_otherbranch = TRUE ";
                }
            }
        } else if (type.equals("reportproductsoldtogether")) {
            branchSetting.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());
            branchSetting.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
        } else {
            branchSetting.setIsCentralIntegration(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration());
            branchSetting.getBranch().setId(sessionBean.getUser().getLastBranch().getId());
        }

        if (!type.equals("reportcheckboxwithbranch") && !type.equals("invoicePage") && !type.equals("orderCheckBox")) {
            if (branchSetting.isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE ";
                whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
            } else {
                where = where + " AND stck.is_otherbranch = TRUE ";
            }
            whereBranch = "AND si.branch_id=" + branchSetting.getBranch().getId() + "";
        }
        if (type.equals("stock") || type.equals("service") || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")) {
            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((Invoice) param.get(0)).isIsPurchase()) {
                    if (type.equals("stock")) {
                        where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                                + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                                + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";
                    }

                }

            }

            if (type.equals("stock") && ((Invoice) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            isPurchase = ((Invoice) param.get(0)).isIsPurchase();
            if (isPurchase) {
                joinIncome = joinIncome + " LEFT JOIN finance.incomeexpense fie ON (fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id = si.branch_id ) \n";
                where = where + " AND (CASE WHEN si.incomeexpense_id IS NOT NULL THEN fie.is_income =FALSE ELSE TRUE END)\n";
                where = where + " AND si.is_delist = FALSE  \n";
            } else {
                joinIncome = joinIncome + " LEFT JOIN finance.incomeexpense fie ON (fie.id=si.incomeexpense_id AND fie.deleted=FALSE AND fie.branch_id = si.branch_id ) \n";
                where = where + " AND (CASE WHEN si.incomeexpense_id IS NOT NULL THEN fie.is_income =TRUE ELSE TRUE END)\n";

            }

        } else if (type.equals("waybillstock") || type.equals("waybillservice") || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")) {
            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((Waybill) param.get(0)).isIsPurchase()) {
                    if (type.equals("waybillstock")) {
                        where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                                + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                                + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";
                    }
                }
            }
            if (((Waybill) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            if (((Waybill) param.get(0)).isIsPurchase()) {
                where = where + " AND si.is_delist = FALSE \n";
            }

        } else if (type.equals("einvoicestock")) {
            if (branchSetting.isIsInvoiceStockSalePriceList()) {
                if (((EInvoice) param.get(0)).isIsPurchase()) {
                    where = where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                            + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + branchSetting.getBranch().getId() + " AND\n"
                            + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))  ";

                }
            }

            if (((Invoice) param.get(0)).isIsFuel()) {
                where = where + " AND si.is_fuel=TRUE \n";
            }

            where = where + " AND si.is_delist = FALSE \n";

        }

        //Pasif ürünler gelmesin 
        if (type.equals("stock") || type.equals("service") || type.equals("waybillstock") || type.equals("waybillservice")
                || type.equals("stockCreateInvFromOrder") || type.equals("serviceCreateInvFromOrder")
                || type.equals("stockCreateWaybillFromOrder") || type.equals("serviceCreateWaybillFromOrder")
                || type.equals("automationdevice") || type.equals("pricelist") || type.equals("stockpricerequest")
                || type.equals("warehouseshelf") || type.equals("einvoicestock")
                || type.equals("discountitem") || type.equals("stockbatchupdate") || type.equals("warehouseitem") || type.equals("automationdeviceforcoffee")) {
            where = where + " AND stck.status_id <> 4 AND si.is_passive = FALSE ";
        }

        switch (type) {
            case "warehouse":
                where = where + " and stck.id NOT IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " and iwi.deleted=false)";
                break;
            case "warehouseitem":
                where = where + " and stck.id NOT IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " and iwi.deleted=false)";
                break;
            case "warehouseshelf":
                where = where + " and stck.id IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((WarehouseShelf) param.get(0)).getWareHouse().getId() + " and iwi.deleted=false)"
                        + " and stck.id NOT IN (select wssc.stock_id from inventory.warehouseshelf_stock_con wssc where wssc.warehouseshelf_id = " + ((WarehouseShelf) param.get(0)).getId() + " and wssc.deleted=false)  ";
                break;
            case "warehousereceipt":
                where = where + " and stck.id NOT IN ( SELECT whm.stock_id FROM inventory.warehousemovement whm WHERE whm.warehousereceipt_id= " + ((WarehouseReceipt) param.get(0)).getId() + " AND whm.deleted = FALSE )";
                if (((WarehouseReceipt) param.get(0)).isIsDirection() == false) {
                    where = where + " and stck.id IN (select iwi.stock_id from inventory.warehouseitem iwi where iwi.warehouse_id = " + ((WarehouseReceipt) param.get(0)).getWarehouse().getId() + " and iwi.deleted=false)";
                }
                if (((WarehouseReceipt) param.get(0)).isIsDirection() == true) {
                    where = where + " AND si.is_delist = FALSE \n";
                }
                break;
            case "service": //sadece hizmet stokları
            case "waybillservice":
                where = where + " AND stck.is_service = TRUE ";
                break;
            case "stock": //sadece hizmet olmayan normal ürünler
            case "waybillstock":
                where = where + " AND stck.is_service = FALSE ";
                break;
            case "einvoicestock": //sadece hizmet olmayan normal ürünler
                where = where + " AND stck.is_service = FALSE ";
                break;
            case "ordermanuel":
                where = where + " AND stck.is_service = FALSE AND si.is_delist = FALSE ";
                where = where + " AND stck.supplier_id = " + ((Order) param.get(0)).getAccount().getId() + " ";
                where = where + " AND COALESCE(cspp.is_autoordercreate,FALSE) = TRUE ";
                join = join + " LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n";

                String orderIds = "";
                for (OrderItem orderItem : (List<OrderItem>) param.get(1)) {
                    orderIds = orderIds + orderItem.getStock().getId() + ",";
                }
                if (!orderIds.equals("")) {
                    orderIds = orderIds.substring(0, orderIds.length() - 1);
                    where = where + " AND stck.id NOT IN(" + orderIds + ") ";
                }

                break;
            case "service_warehouse":
                where = where + " AND stck.is_service = TRUE AND stck.id IN (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " AND iwi.deleted = FALSE )";
                break;
            case "stock_warehouse":
                where = where + " AND stck.is_service = FALSE AND stck.id IN (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = " + ((Warehouse) param.get(0)).getId() + " AND iwi.deleted = FALSE )";
                break;
            case "pricelist":
                where = where + "and stck.id NOT IN (SELECT pli.stock_id FROM inventory.pricelistitem pli WHERE pli.pricelist_id = " + ((PriceList) param.get(0)).getId() + " AND pli.deleted = FALSE)";
                break;
            case "stockpricerequest":
                where = where + "AND COALESCE( si.salemandatoryprice ,0)>0 ";
                break;
            case "stockbatchupdate":
                if ((boolean) param.get(1)) {
                    List<Categorization> list = new ArrayList<>();
                    list = (ArrayList) param.get(0);
                    if (!list.isEmpty()) {
                        if (list.get(0).getId() != 0) {
                            String categories = "";
                            for (Categorization categorization : list) {
                                categories = categories + "," + String.valueOf(categorization.getId());
                            }

                            if (!categories.equals("")) {
                                categories = categories.substring(1, categories.length());
                                where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + categories + ") ) ";
                            }

                        }
                    }
                }
                break;
            case "reportcheckboxwithbranch":
                BranchSetting branch = null;
                whereBranch = "";
                if (param.size() > 0) {
                    if (param.get(0) instanceof ArrayList) {
                        int count = 0;
                        String branchID = "";
                        for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                            if (((List<BranchSetting>) param.get(0)).get(i).isIsCentralIntegration()) {
                                count++;
                            }
                            branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                        }
                        branchID = branchID.substring(3, branchID.length());
                        whereBranch = "AND si.branch_id =" + sessionBean.getUser().getLastBranch().getId() + "";

                        if (((List<BranchSetting>) param.get(0)).size() > 0) {
                            if (count >= 1 && count < ((List<BranchSetting>) param.get(0)).size()) {
                                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                        + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                                        + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                        + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) ";
                            } else if (count == ((List<BranchSetting>) param.get(0)).size()) {
                                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                        + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                        + "AND  si1.is_valid  =TRUE) ";
                                whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                            } else if (count == 0) {
                                where = where + " AND stck.is_otherbranch = TRUE ";
                            }
                        }

                    } else if (param.get(0) instanceof BranchSetting) {
                        branch = new BranchSetting();
                        branch.getBranch().setId(((BranchSetting) param.get(0)).getBranch().getId());
                        branch.getBranch().setName(((BranchSetting) param.get(0)).getBranch().getName());
                        branch.setIsCentralIntegration(((BranchSetting) param.get(0)).isIsCentralIntegration());

                        if (branch.isIsCentralIntegration()) {
                            where = where + " AND si.is_valid = TRUE ";
                            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
                        } else {
                            where = where + " AND stck.is_otherbranch = TRUE ";
                        }
                        whereBranch = "AND si.branch_id=" + branch.getBranch().getId() + "";

                    }

                }

                break;
            case "automationdeviceforcoffee":
                where = where + "AND stck.type_id = 3 ";
                break;
            case "stockCreateInvFromOrder":
            case "stockCreateWaybillFromOrder":
                where = where + " AND stck.is_service = FALSE AND si.is_campaign = TRUE ";
                break;
            case "serviceCreateInvFromOrder":
            case "serviceCreateWaybillFromOrder":
                where = where + " AND stck.is_service = TRUE ";
                break;
            case "washingmachineservice":
                where = where + " AND stck.is_service = TRUE AND stck.status_id <> 4 AND si.is_passive = FALSE ";
                break;
            default:
                break;
        }

        String sql = "SELECT \n"
                + "	COUNT(DISTINCT stck.id) AS stckid \n"
                + "FROM  inventory.stock stck \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False " + whereBranch + ")\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + join + "\n"
                + joinIncome + "\n"
                + "WHERE stck.deleted = false " + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public int stockBarcodeControl(Stock stock
    ) {
        int id;
        String where = " ";
        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";

        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT\n"
                + "COALESCE(stck.id,0)\n"
                + "FROM inventory.stock stck \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON (stck.id = sab.stock_id AND sab.deleted = FALSE)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE stck.deleted=FALSE AND\n"
                + "((UPPER(LTRIM(RTRIM(stck.barcode))) = ? AND stck.id <> ?) or (UPPER(LTRIM(RTRIM(sab.barcode))) = ? " + whereSub + "))"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), stock.getBarcode().toUpperCase().trim(), stock.getId(), stock.getBarcode().toUpperCase().trim()};
        List<Integer> list = getJdbcTemplate().queryForList(sql, param, Integer.class);
        if (list.size() > 0) {
            id = list.get(0);
        } else {
            id = 0;
        }
        return id;
    }

    @Override
    public int stockBarcodeControlRequest(Stock stock
    ) {
        int id;
        String where = " ";
        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT\n"
                + "COALESCE(stck.id,0)\n"
                + "FROM inventory.stock stck \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON (stck.id = sab.stock_id AND sab.deleted = FALSE)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE stck.deleted=FALSE AND stck.status_id <> 4 AND si.is_passive = FALSE AND\n"
                + "((UPPER(LTRIM(RTRIM(stck.barcode))) = ? AND stck.id <> ?) or (UPPER(LTRIM(RTRIM(sab.barcode))) = ? " + whereSub + "))"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), stock.getBarcode().toUpperCase().trim(), stock.getId(), stock.getBarcode().toUpperCase().trim()};
        List<Integer> list = getJdbcTemplate().queryForList(sql, param, Integer.class);
        if (list.size() > 0) {
            id = list.get(0);
        } else {
            id = 0;
        }
        return id;
    }

    @Override
    public int stockBarcodeControl(StockAlternativeBarcode stockAlternativeBarcode
    ) {
        int id;
        String where = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT\n"
                + "COALESCE(stck.id,0)\n"
                + "FROM inventory.stock stck\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE stck.deleted=FALSE AND\n"
                + "UPPER(LTRIM(RTRIM(stck.barcode))) = ?" + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), stockAlternativeBarcode.getBarcode().toUpperCase().trim()};
        List<Integer> list = getJdbcTemplate().queryForList(sql, param, Integer.class);
        if (list.size() > 0) {
            id = list.get(0);
        } else {
            id = 0;
        }
        return id;
    }

    @Override
    public int updateUnit(Stock stock
    ) {
        String sql = "UPDATE \n"
                + "  inventory.stock \n"
                + "SET \n"
                + "  unit_id = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ? ;";

        Object[] param = new Object[]{stock.getUnit().getId(), sessionBean.getUser().getId(), stock.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Stock stock
    ) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT stock_id FROM inventory.warehousemovement WHERE stock_id=? AND deleted=False AND warehouse_id IN(SELECT wr.id FROM inventory.warehouse wr WHERE wr.deleted = FALSE AND wr.branch_id IN(SELECT brst.branch_id FROM general.branchsetting brst WHERE brst.deleted=FALSE AND brst.is_centralintegration = FALSE))) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{stock.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Stock stock
    ) {
        String sql = "";
        Object[] param = null;

        if (stock.getCenterstock_id() > 0) {
            sql = "UPDATE inventory.stock SET is_otherbranch=FALSE, u_id=? , u_time=NOW() WHERE deleted=False AND id=?;\n"
                    + "UPDATE inventory.stockinfo SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=? AND branch_id IN (SELECT brst.branch_id FROM general.branchsetting brst WHERE brst.deleted=FALSE AND brst.is_centralintegration = FALSE);\n"
                    + "UPDATE inventory.stockalternativebarcode SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=? AND is_otherbranch = TRUE;\n"
                    + "UPDATE inventory.stock_unit_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=? AND is_otherbranch = TRUE;\n";
            param = new Object[]{sessionBean.getUser().getId(), stock.getId(),
                sessionBean.getUser().getId(), stock.getId(),
                sessionBean.getUser().getId(), stock.getId(), sessionBean.getUser().getId(), stock.getId()};
        } else {
            sql = "UPDATE inventory.stock SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                    + "UPDATE inventory.stock_taxgroup_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=?;\n"
                    + "UPDATE inventory.stock_unit_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=?;\n"
                    + "UPDATE inventory.stockalternativebarcode SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=?;\n"
                    + "UPDATE inventory.stock_categorization_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=?;\n"
                    + "UPDATE inventory.stockinfo SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND stock_id=? AND branch_id IN (SELECT brst.branch_id FROM general.branchsetting brst WHERE brst.deleted=FALSE AND brst.is_centralintegration = FALSE);\n";

            param = new Object[]{sessionBean.getUser().getId(), stock.getId(), sessionBean.getUser().getId(), stock.getId(),
                sessionBean.getUser().getId(), stock.getId(), sessionBean.getUser().getId(), stock.getId(),
                sessionBean.getUser().getId(), stock.getId(), sessionBean.getUser().getId(), stock.getId()
            };
        }

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String importProductList(String json
    ) {
        String sql = "SELECT r_message FROM inventory.process_stockaddupdate(?, ?, ?)";

        Object[] param = new Object[]{json, sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String importProductListForCentral(String json
    ) {
        String sql = "SELECT r_message FROM inventory.process_stockupdateforcentralentegration(?, ?, ?)";

        Object[] param = new Object[]{json, sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {

            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int batchUpdate(String where, int changeField, Stock stock
    ) {

        String sql = "";
        Object[] param = null;

        switch (changeField) {
            case 1:
                //birim
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "unit_id = ?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + "\n"
                        + "AND centerstock_id IS NULL;";
                param = new Object[]{stock.getUnit().getId() == 0 ? null : stock.getUnit().getId(), sessionBean.getUser().getId()};
                break;
            case 2:
                //marka
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "brand_id = ?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + "\n"
                        + "AND centerstock_id IS NULL;";
                param = new Object[]{stock.getBrand().getId() == 0 ? null : stock.getBrand().getId(), sessionBean.getUser().getId()};
                break;
            case 3:
                //üretim yeri
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "country_id=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + " ;";
                param = new Object[]{stock.getCountry().getId() == 0 ? null : stock.getCountry().getId(), sessionBean.getUser().getId()};
                break;
            case 4:
                //tedarikçi
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "supplier_id=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + " ;";
                param = new Object[]{stock.getSupplier().getId() == 0 ? null : stock.getSupplier().getId(), sessionBean.getUser().getId()};
                break;
            case 5:
                //statü
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "status_id=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + "\n"
                        + "AND centerstock_id IS NULL;";
                param = new Object[]{stock.getStatus().getId() == 0 ? null : stock.getStatus().getId(), sessionBean.getUser().getId()};
                break;
            case 6:
                //servis mi
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "is_service=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + "\n"
                        + "AND centerstock_id IS NULL;";
                param = new Object[]{stock.isIsService(), sessionBean.getUser().getId()};
                break;
            case 7:
                //stok eksiye düşebilir mi
                sql = "  UPDATE \n"
                        + "inventory.stockinfo si\n"
                        + "SET \n"
                        + "is_minusstocklevel=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE\n"
                        + where + "\n"
                        + "AND branch_id =" + sessionBean.getUser().getLastBranch().getId();

                param = new Object[]{stock.getStockInfo().isIsMinusStockLevel(), sessionBean.getUser().getId()};
                break;
            case 8:
                //departman
                sql = "  UPDATE \n"
                        + "inventory.stockinfo si\n"
                        + "SET \n"
                        + "taxdepartment_id=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE\n"
                        + where + "\n"
                        + "AND branch_id =" + sessionBean.getUser().getLastBranch().getId();
                param = new Object[]{stock.getStockInfo().getTaxDepartment().getId(), sessionBean.getUser().getId()};
                break;
            case 9:
                //ürün tipi
                sql = "  UPDATE \n"
                        + "inventory.stock stck\n"
                        + "SET \n"
                        + "type_id=?,\n"
                        + "u_id = ?,\n"
                        + "u_time = now()\n"
                        + "WHERE \n"
                        + where + "\n"
                        + "AND centerstock_id IS NULL;";
                param = new Object[]{stock.getStockType_id(), sessionBean.getUser().getId()};
                break;
            default:
                break;
        }
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * bu metot ürünün son alış ve son satış fiyatını kurla çarpıp getirir.
     *
     * @param stockId
     * @param branchSetting
     * @return
     */
    @Override
    public Stock findStockLastPrice(int stockId, BranchSetting branchSetting
    ) {

        String where = "";
        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND stcki.is_valid = TRUE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT \n"
                + "COALESCE(stcki.currentpurchasecurrency_id,0) AS sicurrentpurchasecurrency_id,\n"
                + "COALESCE(stcki.currentpurchaseprice,0) AS sicurrentpurchaseprice,\n"
                + "COALESCE(stcki.currentsalecurrency_id,0) AS sicurrentsalecurrency_id,\n"
                + "COALESCE(stcki.currentsaleprice,0) AS sicurrentsaleprice,\n"
                + "COALESCE(ptg.rate,0) AS purchasekdv,\n"
                + "stcki.currentsaleprice AS tempsicurrentsaleprice\n"
                + "FROM inventory.stock stck\n"
                + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=FALSE AND stcki.branch_id=?)\n"
                + "LEFT JOIN (SELECT \n"
                + "               txg.rate AS rate,\n"
                + "               stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "WHERE stck.deleted=FALSE AND stck.id=? \n"
                + where;

        Object[] param = new Object[]{branchSetting.getBranch().getId(), stockId};
        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Stock();
        }
    }

    @Override
    public List<Stock> findFuelStock() {
        String sql = "SELECT \n"
                + "stck.id as stckid,\n"
                + "stck.name as stckname\n"
                + "FROM inventory.stock stck\n"
                + "INNER JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.branch_id=? AND stcki.is_fuel=TRUE AND stcki.deleted=FALSE)\n"
                + "WHERE stck.deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());

        return result;
    }

    @Override
    public Stock findStokcUnit(String barcode, Invoice obj,
            boolean isAlternativeBarcode, BranchSetting branchSetting
    ) {
        String where = "";
        String join = "";
        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND stcki.is_valid = TRUE ";
            if (isAlternativeBarcode) {
                where = where + " AND sab.is_otherbranch = FALSE ";
            }
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }

        if (obj.isIsPurchase() && branchSetting.isIsInvoiceStockSalePriceList()) { // satın alma faturasında satış fiyat listesindeki ürünler eklenebilsin mi durumu
            join = "INNER JOIN inventory.pricelistitem plii ON(plii.stock_id =stck.id AND plii.deleted=FALSE AND plii.pricelist_id IN(SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND pl.is_default=TRUE AND pl.is_purchase=false AND pl.status_id=11  LIMIT 1))";
        }

        if (!isAlternativeBarcode) {
            where = where + " AND stck.barcode = '" + barcode + "' ";
        } else {
            where = where + " AND sab.barcode = '" + barcode + "' ";
        }

        String sql = "SELECT \n"
                + "       stck.id as stckid,\n"
                + "       stck.unit_id as stckunitid,\n"
                + "       unt.sortname as untsortname,\n"
                + "       unt.unitrounding as untunitrounding,\n"
                + "       stcki.is_fuel as stckiisfuel,\n"
                + "       COALESCE(wrh.quantity,0) as availablequantity,\n"
                + "       stcki.maxstocklevel as stckimaxstocklevel,\n"
                + "       COALESCE(stcki.balance,0) as stckibalance,\n"
                + (isAlternativeBarcode ? "sab.quantity" : "1") + " as sabquantity,\n"
                + "       stcki.is_minusstocklevel AS siis_minusstocklevel,\n "
                + "       COALESCE(stcki.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                + "       stcki.purchasecurrency_id AS sipurchasecurrency_id,\n"
                + "       stcki.is_delist AS stckiis_delist \n"
                + "       FROM inventory.stock stck\n"
                + "       INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "       LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=FALSE AND stcki.branch_id=?)\n"
                + "       LEFT JOIN inventory.warehouseitem wrh ON (wrh.stock_id=stck.id AND wrh.deleted=FALSE AND wrh.warehouse_id=?)\n"
                + "       LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE)\n"
                + join + "\n"
                + "       WHERE stck.deleted=FALSE AND stck.status_id <> 4 AND stcki.is_passive = FALSE " + where + "\n"
                + "       LIMIT 1";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), Integer.parseInt(obj.getWarehouseIdList())};
        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Stock();
        }
    }

    @Override
    public Stock findSaleMandatoryPrice(int stockId, BranchSetting branchSetting
    ) {
        String where = "";
        if (branchSetting.isIsCentralIntegration()) {
            where = where + " AND stcki.is_valid = TRUE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = "SELECT \n"
                + "COALESCE(stcki.salemandatoryprice,0) as stckisalemandatoryprice,\n"
                + "COALESCE(stcki.salemandatorycurrency_id,0) as stckisalemandatorycurrency_id\n"
                + "FROM inventory.stock stck \n"
                + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.branch_id=? AND stcki.deleted=FALSE)\n"
                + "\n"
                + "WHERE stck.deleted=FALSE AND stck.id=? \n"
                + where;

        Object[] param = new Object[]{branchSetting.getBranch().getId(), stockId};
        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Stock();
        }
    }

    @Override
    public Stock findStockBarcode(String barcode) {
        String where = "";
        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND stcki.is_valid = TRUE ";
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        String sql = " SELECT \n"
                + "       stck.id as stckid,\n"
                + "       stck.barcode as stckbarcode\n"
                + "    FROM \n"
                + "       inventory.stock stck \n"
                + "    LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.branch_id=? AND stcki.deleted=FALSE)\n"
                + "    WHERE \n"
                + "       stck.deleted = FALSE \n"
                + where + "\n"
                + "    AND UPPER(LTRIM(RTRIM(stck.barcode))) = ? or ? IN (\n"
                + "    SELECT UPPER(LTRIM(RTRIM(sab.barcode))) FROM inventory.stockalternativebarcode sab WHERE sab.stock_id=stck.id AND sab.deleted=FALSE " + whereSub + "\n"
                + "    );";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), barcode.toUpperCase().trim(), barcode.toUpperCase().trim()};
        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Stock();
        }
    }

    @Override
    public String exportData(String where
    ) {

        String sortField = "";
        sortField = "tt.stckid";
        String whereSub = " ";
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }

        String sql = "WITH recursive ctTree AS(\n"
                + "    SELECT \n"
                + "        gct.id,\n"
                + "        scac.stock_id,\n"
                + "        gct.name, \n"
                + "        COALESCE(gct.parent_id,0) AS parent_id, \n"
                + "        1 AS depth\n"
                + "    FROM \n"
                + "        inventory.stock_categorization_con scac\n"
                + "        INNER JOIN general.categorization gct ON(scac.categorization_id = gct.id AND gct.deleted = FALSE)\n"
                + "	WHERE\n"
                + "        scac.deleted = FALSE\n"
                + "	UNION ALL\n"
                + "   SELECT     	\n"
                + "        gct.id, \n"
                + "        ct.stock_id,\n"
                + "        gct.name,\n"
                + "        COALESCE(gct.parent_id,0) AS parent_id, \n"
                + "        ct.depth+1 AS depth\n"
                + "    FROM \n"
                + "        general.categorization gct\n"
                + "        JOIN ctTree ct ON ct.parent_id = gct.id\n"
                + "    WHERE\n"
                + "       gct.deleted = FALSE\n"
                + ")\n"
                + "SELECT tt.*,\n"
                + "   (\n"
                + "    SELECT \n"
                + "       xmlelement(\n"
                + "       name \"categories\",\n"
                + "       xmlagg(\n"
                + "       xmlelement(\n"
                + "                  name \"category\",\n"
                + "                  xmlforest (\n"
                + "                       ctr.id AS \"id\",\n"
                + "                       COALESCE(ctr.name, '') AS \"name\",\n"
                + "                       COALESCE(ctr.parent_id, 0) AS \"parent_id\",\n"
                + "                       ctr.depth AS \"depth\"\n"
                + "                   )\n"
                + "       )\n"
                + "       )\n"
                + "       )\n"
                + "      FROM \n"
                + "         ctTree ctr \n"
                + "      WHERE \n"
                + "      ctr.stock_id = tt.stckid\n"
                + "   ) AS category,\n"
                + "   (SELECT \n"
                + "       STRING_AGG(CAST(sab.barcode as varchar),',') \n"
                + "       FROM\n"
                + "       inventory.stockalternativebarcode sab\n"
                + "       WHERE sab.deleted=FALSE AND sab.stock_id = tt.stckid " + whereSub + "\n"
                + "   ) as alternativebarcodes\n"
                + "FROM\n"
                + "(SELECT \n"
                + "   DISTINCT stck.id AS stckid,\n"
                + "   stck.barcode as stckbarcode,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.code AS stckcode,\n"
                + "   stck.centerproductcode AS stckcenterproductcode,\n"
                + "   stck.country_id as stckcountry_id, \n"
                + "   ctryd.name as ctrydname, \n"
                + "   sttd.status_id AS sttdid,\n"
                + "   sttd.name AS sttdname,\n"
                + "   stck.supplier_id AS stcksupplier_id,\n"
                + "   acc.name AS accname, \n"
                + "   stck.supplierproductcode AS stcksupplierproductcode, \n"
                + "   stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "   cspp.name AS csppname,\n"
                + "   stck.centralsupplierproductcode AS stckcentralsupplierproductcode, \n"
                + "   gunt.id AS guntid,\n"
                + "   gunt.name AS guntname,\n"
                + "   gunt.sortname AS guntsortname,\n"
                + "   gunt.unitrounding as guntunitrounding,\n"
                + "   br.id AS brid,\n"
                + "   br.name AS brname,        \n"
                + "   si.minstocklevel as siminstocklevel,\n"
                + "   COALESCE(si.recommendedprice,0) as sirecommendedprice,\n"
                + "   si.is_quicksale as siis_quicksale,\n"
                + "   si.currency_id as sicurrency_id,\n"
                + "   COALESCE(si.purchaserecommendedprice,0) as sipurchaserecommendedprice,\n"
                + "   si.purchasecurrency_id as sipurchasecurrency_id,\n"
                + "   si.minprofitrate as siminprofitrate,\n"
                + "   si.purchasecontroldate as sipurchasecontroldate,\n"
                + "   si.is_fuel as siis_fuel,\n"
                + "   si.fuelintegrationcode as sifuelintegrationcode,\n"
                + "   si.weight as siweight,\n"
                + "   si.weightunit_id as siweightunit_id,\n"
                + "   si.mainweight as simainweight,\n"
                + "   si.mainweightunit_id as simainweightunit_id,\n"
                + "   COALESCE(si.salemandatoryprice,0) as sisalemandatoryprice,\n"
                + "   COALESCE(si.salemandatorycurrency_id,0) as sisalemandatorycurrency_id,\n"
                + "   COALESCE(si.currentpurchaseprice,0) as sicurrentpurchaseprice, \n"
                + "   COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)) as sicurrentpurchasepricewithkdv, \n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100))) as availablepurchasepricewithkdv,\n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentpurchaseprice,0)) as availablepurchasepricewithoutkdv,\n"
                + "   si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id, \n"
                + "   COALESCE(si.currentsaleprice,0) as sicurrentsaleprice, \n"
                + "   COALESCE(si.currentsaleprice,0)/(1+(COALESCE(stg.rate,0)/100)) as sicurrentsalepricewithoutkdv,\n"
                + "   COALESCE(si.balance,0)*COALESCE(si.currentsaleprice,0) as availablesalepricewithkdv, \n"
                + "   COALESCE(si.balance,0)*(COALESCE(si.currentsaleprice,0)/(1+(COALESCE(stg.rate,0)/100))) as availablesalepricewithoutkdv, \n"
                + "   si.currentsalecurrency_id as sicurrentsalecurrency_id, \n"
                + "   COALESCE(si.salecount,0) as sisalecount,\n"
                + "   COALESCE(si.purchasecount,0) as sipurchasecount,\n"
                + "   CASE WHEN COALESCE(si.currentsaleprice,0)=0 OR COALESCE(si.currentpurchaseprice,0) = 0 THEN 0\n"
                + "   ELSE (COALESCE(si.currentsaleprice,0) - (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100))))/(COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)))*100 END AS profitpercentage,\n"
                + "   pli.price as purchaseprice,\n"
                + "   pli.currency_id as purchasecurrency_id ,\n"
                + "   pli.is_taxincluded as purchaseis_taxincluded,\n"
                + "   pli2.price as saleprice,\n"
                + "   pli2.currency_id as salecurrency_id,\n"
                + "   pli2.is_taxincluded as saleis_taxincluded,\n"
                + "   cryd.name as crydname,\n"
                + "   stck.is_service as stckis_service,\n"
                + "   stck.c_id AS stckc_id,\n"
                + "   stck.c_time AS stckc_time,\n"
                + "   COALESCE(stg.rate,0) salekdv,\n"
                + "   COALESCE(ptg.rate,0) purchasekdv,\n"
                + "   COALESCE(si.balance,0) AS availablequantity,\n"
                + "   COALESCE(si.balance,0)-(COALESCE(si.purchasecount,0)-COALESCE(si.salecount,0)) AS otherquantity,\n"
                + "   usr.username AS usrusername,\n"
                + "   usr.name as usrname,\n"
                + "   usr.surname as usrsurname,\n"
                + "   stck.description as stckdescription, \n"
                + "   gunt.centerunit_id as guntcenterunit_id,\n"
                + "   stck.centerstock_id as stckcenterstock_id,\n"
                + "   si.maxstocklevel as simaxstocklevel,\n "
                + "   COALESCE(si.balance,0) as sibalance,\n"
                + "   si.taxdepartment_id AS sitaxdepartment_id,\n"
                + "   si.is_minusstocklevel AS siis_minusstocklevel,\n"
                + "   si.is_passive AS sii_passive,\n"
                + "   txd.name AS txdname\n"
                + "FROM inventory.stock stck   \n"
                + "LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.brand br   ON (br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "LEFT JOIN system.country_dict ctryd ON(ctryd.country_id = stck.country_id AND ctryd.language_id = ?)\n"
                + "INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + "LEFT JOIN system.currency_dict cryd ON(cryd.currency_id=si.currency_id AND cryd.language_id = ?) \n"
                + "LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=TRUE AND pl.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                + "LEFT JOIN inventory.pricelist pl2 ON (pl2.branch_id=? AND pl2.is_default=TRUE AND pl2.is_purchase=FALSE AND pl2.deleted=False)\n"
                + "LEFT JOIN inventory.pricelistitem pli2 ON (pli2.stock_id=stck.id AND pli2.pricelist_id=pl2.id AND pli2.deleted=False)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN inventory.taxdepartment txd ON(txd.id=si.taxdepartment_id AND txd.deleted=FALSE) \n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "INNER JOIN general.userdata usr  ON (usr.id = stck.c_id)\n"
                + "WHERE stck.deleted = false " + where + "\n"
                + ") tt\n"
                + "ORDER BY " + sortField + " \n";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public Stock findStockAccordingToBarcode(Stock stock
    ) {
        String where = " ";
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND stck.is_otherbranch = TRUE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.centerstock_id IS NOT NULL  ";
        }
        String sql = "SELECT\n"
                + "   DISTINCT stck.id AS stckid,\n"
                + "   stck.barcode as stckbarcode,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.code AS stckcode,\n"
                + "   stck.country_id as stckcountry_id, \n"
                + "   ctryd.name as ctrydname, \n"
                + "   sttd.status_id AS sttdid,\n"
                + "   sttd.name AS sttdname,\n"
                + "   stck.supplier_id AS stcksupplier_id,\n"
                + "   acc.name AS accname, \n"
                + "   stck.supplierproductcode AS stcksupplierproductcode, \n"
                + "   stck.centerproductcode AS stckcenterproductcode, \n"
                + "   gunt.id AS guntid,\n"
                + "   gunt.name AS guntname,\n"
                + "   gunt.sortname AS guntsortname,\n"
                + "   gunt.unitrounding as guntunitrounding,\n"
                + "   br.id AS brid,\n"
                + "   br.name AS brname,        \n"
                + "   stck.is_service as stckis_service,\n"
                + "   stck.description as stckdescription, \n"
                + "   stck.centerstock_id as stckcenterstock_id,\n"
                + "   stck.type_id AS stcktype_id, \n"
                + "   si.taxdepartment_id AS sitaxdepartment_id\n"
                + "FROM inventory.stock stck \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?) \n"
                + "LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.brand br   ON (br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN system.country_dict ctryd ON(ctryd.country_id = stck.country_id AND ctryd.language_id = ?)\n"
                + "INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE)\n"
                + "WHERE stck.deleted=FALSE AND\n"
                + "((LOWER(LTRIM(RTRIM(stck.barcode))) = ? AND stck.id <> ?) or (LOWER(LTRIM(RTRIM(sab.barcode))) = ? " + whereAlter + "))\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            stock.getBarcode().toLowerCase().trim(), stock.getId(), stock.getBarcode().toLowerCase().trim()};

        List<Stock> result = getJdbcTemplate().query(sql, param, new StockMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Stock();
        }
    }

}
