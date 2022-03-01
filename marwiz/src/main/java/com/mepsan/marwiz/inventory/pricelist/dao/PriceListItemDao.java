/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:38:29
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PriceListItemDao extends JdbcDaoSupport implements IPriceListItemDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PriceListItem> listofPriceListItem(PriceList obj, String where) {
        String whereSub = "";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND stcka.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + "                 pli.id AS pliid,\n"
                + "                 pli.stock_id AS plistock_id,\n"
                + "                 stck.name AS stckname,\n"
                + "                 stck.barcode AS stckbarcode,\n"
                + "                 si.recommendedprice AS sirecommendedprice,\n"
                + "                 si.currency_id AS sicurrency_id,\n"
                + "                 1 AS tagquantity,\n"
                + "                 crrd2.name AS crrd2name,\n"
                + "                 pli.currency_id AS plicurrency_id,\n"
                + "                 (SELECT \n"
                + "            	 string_agg(stcka.barcode,',')\n"
                + "            FROM inventory.stockalternativebarcode stcka\n"
                + "            WHERE stcka.deleted=FALSE AND stcka.stock_id=pli.stock_id " + whereSub + " ) as alternativebarcode,\n"
                + "                 crrd.name AS crrdname,\n"
                + "                 pli.price AS pliprice,\n"
                + "                 pli.is_taxincluded AS pliis_taxincluded,\n"
                + "                 pli.c_time as plic_time,\n"
                + "                 usd.id as usdid,\n"
                + "                 usd.name as usdname,\n"
                + "                 usd.surname as usdsurname,\n"
                + "                 usd.username as usdusername,"
                + "                 stck.country_id AS stckcountry_id,\n"
                + "                 cond.name AS condname,\n"
                + "                 si.minprofitrate as siminprofitrate,\n"
                + "                 si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                + "                 si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                + "                 COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                + "                 COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                + "                 si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                + "                 COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "                 COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                + "                 COALESCE(si.weight,0) AS siweight,\n"
                + "                 si.weightunit_id AS siweightunit_id,\n"
                + "                 wu.name AS wuname,\n"
                + "                 wu.sortname AS wusortname,\n"
                + "                 COALESCE(wu.mainweight,0) AS wumainweight,\n"
                + "                 wu.mainweightunit_id AS wumainweightunit_id,\n"
                + "                 mwu.name AS mwuname,\n"
                + "                 mwu.sortname AS mwusortname,\n"
                + "                 CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                + "                     CASE WHEN pl.is_purchase=TRUE THEN  pli.price ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                + "                 ELSE pli.price END AS pricewithtax\n"
                + "                 FROM \n"
                + "                 inventory.pricelistitem pli\n"
                + "                 INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "                 INNER JOIN inventory.stock stck ON (stck.id = pli.stock_id AND stck.deleted = False)\n"
                + "                 LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                + "                 INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)"
                + "                 LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)"
                + "                 INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                + "                 LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )  \n"
                + "                 LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                + "                 LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)"
                + "                  LEFT JOIN (SELECT \n"
                + "                       txg.id as id, \n"
                + "                          txg.rate AS rate,\n"
                + "                          stc.stock_id AS stock_id \n"
                + "                          FROM inventory.stock_taxgroup_con stc  \n"
                + "                          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "                          WHERE stc.deleted = false\n"
                + "                          AND txg.type_id = 10 --kdv grubundan \n"
                + "                            AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)"
                + "             WHERE \n"
                + "             	pli.pricelist_id = ? AND pli.deleted = false\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), obj.getId()};
        List<PriceListItem> result = getJdbcTemplate().query(sql, param, new PriceListItemMapper());
        return result;
    }

    @Override
    public int create(PriceListItem obj) {
        String sql = "INSERT INTO inventory.pricelistitem "
                + "(pricelist_id,stock_id,currency_id,price, is_taxincluded,c_id,u_id) "
                + "VALUES (?, ?, ?, ?, ?, ?,?)  RETURNING id ;";

        Object[] param = new Object[]{obj.getPriceList().getId(), obj.getStock().getId(), obj.getCurrency().getId(), obj.getPrice(), obj.isIs_taxIncluded(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot varsayılan SATIŞ fiyat listesine hızlı stok ekler.
     *
     * @param obj
     * @return
     */
    @Override
    public int createItem(PriceListItem obj, Branch branch) {
        String sql = "INSERT INTO inventory.pricelistitem "
                + "(pricelist_id,stock_id,currency_id,price, is_taxincluded,c_id,u_id) "
                + "VALUES ((SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.is_purchase = FALSE AND pl.is_default = TRUE AND pl.branch_id = ? LIMIT 1)\n"
                + ", ? "
                + ", ? "
                + ", ? "
                + ", ? "
                + ", ? "
                + ",? ) RETURNING id ;";

        Object[] param = new Object[]{branch.getId(), obj.getStock().getId(), obj.getCurrency().getId(), obj.getPrice(), obj.isIs_taxIncluded(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(PriceListItem obj) {
        String sql = "UPDATE inventory.pricelistitem SET stock_id = ?, currency_id = ?, price = ?,is_taxincluded=?, u_id = ?, u_time = now() WHERE id = ? ";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getCurrency().getId(), obj.getPrice(), obj.isIs_taxIncluded(),
            sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu metot gelen ürünün fiyatlistesindeki birim fiyatını getirir.
     *
     * @param stock
     * @param isPurchase
     * @return
     */
    @Override
    public PriceListItem findStockPrice(Stock stock, boolean isPurchase, Branch branch) {
        String sql = "SELECT \n"
                + "         pli.id AS pliid,\n"
                + "         pli.currency_id AS plicurrency_id,\n"
                + "         pli.price AS pliprice,\n"
                + "         cryd.name AS crrdname,\n"
                + "         pli.is_taxincluded AS pliis_taxincluded\n"
                + "       FROM inventory.pricelistitem pli\n"
                + "       INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted = FALSE)\n"
                + "       INNER JOIN system.currency_dict cryd ON(cryd.currency_id=pli.currency_id AND cryd.language_id=?)\n"
                + "       WHERE pli.deleted = FALSE\n"
                + "       AND pl.status_id = 11 \n"
                + "       AND pl.is_default = TRUE \n"
                + "       AND pl.is_purchase = ? \n"
                + "       AND pli.stock_id =? \n"
                + "       AND pl.branch_id = ? ";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), isPurchase, stock.getId(), branch.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new PriceListItemMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public int delete(PriceListItem obj) {
        String sql = "UPDATE inventory.pricelistitem set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String processStockPriceList(String json, int priceListId, Boolean isUpdate) {

        String sql = "SELECT r_message FROM inventory.process_pricelistitemaddupdate(?, ?, ?, ?)";
        Object[] param = new Object[]{json, priceListId, sessionBean.getUser().getId(), isUpdate};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<PriceListItem> listOfStock(int type, int priceListId, String where) {

        String sql = "";
        String wheree = "";
        Object[] param = null;
        if (type == 1) {//Kategori
            wheree = "AND ctc.categorization_id IN( " + where + " )";
            sql = "         \n"
                    + "SELECT \n"
                    + "    pli.id AS pliid,\n"
                    + "    pli.stock_id AS plistock_id,\n"
                    + "    stck.name AS stckname,\n"
                    + "    stck.barcode AS stckbarcode,\n"
                    + "    si.recommendedprice AS sirecommendedprice,\n"
                    + "    si.currency_id AS sicurrency_id,\n"
                    + "    1 AS tagquantity,\n "
                    + "    crrd2.name AS crrd2name,\n"
                    + "    pli.currency_id AS plicurrency_id,\n"
                    + "    crrd.name AS crrdname,\n"
                    + "    pli.price AS pliprice,\n"
                    + "    pli.is_taxincluded AS pliis_taxincluded,\n"
                    + "    pli.c_time as plic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername,\n"
                    + "    stck.country_id AS stckcountry_id,\n"
                    + "    ctc.categorization_id AS ctccategorization_id,\n"
                    + "    cond.name AS condname,\n"
                    + "    si.minprofitrate as siminprofitrate,\n"
                    + "    si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                    + "    si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                    + "    COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                    + "    COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                    + "    si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                    + "    COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                    + "    COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                    + "    COALESCE(si.weight,0) AS siweight,\n"
                    + "    si.weightunit_id AS siweightunit_id,\n"
                    + "    wu.name AS wuname,\n"
                    + "    wu.sortname AS wusortname,\n"
                    + "    COALESCE(wu.mainweight,0) AS wumainweight,\n"
                    + "    wu.mainweightunit_id AS wumainweightunit_id,\n"
                    + "    mwu.name AS mwuname,\n"
                    + "    mwu.sortname AS mwusortname,\n"
                    + "                 CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                    + "                     CASE WHEN pl.is_purchase=TRUE THEN  pli.price ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                    + "                 ELSE pli.price END AS pricewithtax\n"
                    + "FROM\n"
                    + "    inventory.pricelistitem pli\n"
                    + "                 INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                    + "    INNER JOIN inventory.stock_categorization_con ctc ON(ctc.stock_id = pli.stock_id AND  ctc.deleted = FALSE)\n"
                    + "    INNER JOIN general.categorization ct ON(ct.id = ctc.categorization_id  AND ct.deleted = FALSE)\n"
                    + "     INNER JOIN inventory.stock stck ON (stck.id = pli.stock_id AND stck.deleted = False)\n"
                    + "    LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                    + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                    + "    LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                    + "    INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                    + "    LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )\n"
                    + "    LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                    + "    LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)"
                    + "                  LEFT JOIN (SELECT \n"
                    + "                       txg.id as id, \n"
                    + "                          txg.rate AS rate,\n"
                    + "                          stc.stock_id AS stock_id \n"
                    + "                          FROM inventory.stock_taxgroup_con stc  \n"
                    + "                          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                    + "                          WHERE stc.deleted = false\n"
                    + "                          AND txg.type_id = 10 --kdv grubundan \n"
                    + "                            AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)"
                    + "WHERE    \n"
                    + " pli.pricelist_id  = ? AND pli.deleted = FALSE  \n" + wheree + "\n"
                    + " GROUP BY pli.id , pli.stock_id,stck.name ,stck.barcode ,si.recommendedprice, \n"
                    + "    si.currency_id,crrd2.name,pli.currency_id , crrd.name , pli.price, pli.is_taxincluded, pli.c_time , usd.id , usd.name , usd.surname,\n"
                    + "    usd.username, stck.country_id,cond.name  ,  si.currentsaleprice, si.currentsalecurrency_id ,si.lastsalepricechangedate,si.salemandatoryprice,si.weight,si.salemandatorycurrency_id,si.weightunit_id,wu.name,wu.sortname,wu.mainweight,wu.mainweightunit_id,mwu.name,mwu.sortname,si.minprofitrate, si.currentpurchaseprice, si.currentpurchasecurrency_id , \n"
                    + "    pl.is_purchase,stg.rate,ctc.categorization_id";

            param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), priceListId};
        } else if (type == 3) {//tedarikçi
            wheree = "AND  wb.account_id IN( " + where + " )";
            sql = "         \n"
                    + "SELECT \n"
                    + "   pli.id AS pliid,\n"
                    + "    pli.stock_id AS plistock_id,\n"
                    + "    stck.name AS stckname,\n"
                    + "    stck.barcode AS stckbarcode,\n"
                    + "    wb.account_id AS wbaccount_id,\n"
                    + "    si.recommendedprice AS sirecommendedprice,\n"
                    + "    si.currency_id AS sicurrency_id,\n"
                    + "    1 AS tagquantity,\n"
                    + "    crrd2.name AS crrd2name,\n"
                    + "    pli.currency_id AS plicurrency_id,\n"
                    + "    crrd.name AS crrdname,\n"
                    + "    pli.price AS pliprice,\n"
                    + "    pli.is_taxincluded AS pliis_taxincluded,\n"
                    + "    pli.c_time as plic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername,\n"
                    + "    stck.country_id AS stckcountry_id,\n"
                    + "    cond.name AS condname,\n"
                    + "    si.minprofitrate as siminprofitrate,\n"
                    + "    si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                    + "    si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                    + "    COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                    + "    COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                    + "    si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                    + "    COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                    + "    COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                    + "    COALESCE(si.weight,0) AS siweight,\n"
                    + "    si.weightunit_id AS siweightunit_id,\n"
                    + "    wu.name AS wuname,\n"
                    + "    wu.sortname AS wusortname,\n"
                    + "    COALESCE(wu.mainweight,0) AS wumainweight,\n"
                    + "    wu.mainweightunit_id AS wumainweightunit_id,\n"
                    + "    mwu.name AS mwuname,\n"
                    + "    mwu.sortname AS mwusortname,\n"
                    + "                 CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                    + "                     CASE WHEN pl.is_purchase=TRUE THEN  pli.price ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                    + "                 ELSE pli.price END AS pricewithtax\n"
                    + "FROM\n"
                    + "    finance.waybill wb\n"
                    + "    INNER JOIN finance.waybillitem wbi ON(wbi.waybill_id = wb.id AND wbi.deleted = FALSE)\n"
                    + "    INNER JOIN inventory.stock stck ON(stck.id = wbi.stock_id AND stck.deleted = FALSE)\n"
                    + "    INNER JOIN inventory.pricelistitem pli ON(pli.stock_id = stck.id)\n"
                    + "    INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                    + "    LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                    + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                    + "    LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                    + "    INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                    + "    LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )\n"
                    + "    LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                    + "    LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)"
                    + "                  LEFT JOIN (SELECT \n"
                    + "                       txg.id as id, \n"
                    + "                          txg.rate AS rate,\n"
                    + "                          stc.stock_id AS stock_id \n"
                    + "                          FROM inventory.stock_taxgroup_con stc  \n"
                    + "                          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                    + "                          WHERE stc.deleted = false\n"
                    + "                          AND txg.type_id = 10 --kdv grubundan \n"
                    + "                            AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)"
                    + "WHERE    \n"
                    + "    pli.pricelist_id = ? AND pli.deleted = FALSE  AND   wb.is_purchase = TRUE AND wb.deleted = FALSE AND\n"
                    + "      wb.status_id NOT IN (27) AND wb.type_id = 21 \n" + wheree + "\n"
                    + "      GROUP BY pli.id , pli.stock_id,stck.name ,stck.barcode ,si.recommendedprice ,\n"
                    + "    si.currency_id,crrd2.name,pli.currency_id , crrd.name , pli.price, pli.is_taxincluded, pli.c_time , usd.id , usd.name , usd.surname,\n"
                    + "    usd.username, stck.country_id,cond.name ,  si.currentsaleprice, si.currentsalecurrency_id ,si.lastsalepricechangedate,si.salemandatoryprice ,si.salemandatorycurrency_id,si.weight,si.weightunit_id,wu.name,wu.sortname,wu.mainweight,wu.mainweight,wu.mainweightunit_id ,mwu.name,mwu.sortname,si.minprofitrate, si.currentpurchaseprice, si.currentpurchasecurrency_id, \n"
                    + "   pl.is_purchase,stg.rate,wb.account_id"
                    + " ";

            param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), priceListId};

        } else if (type == 4 || type == 5) {//Ürünün tedarikçisi
            if (type == 4) {
                wheree = "AND stck.supplier_id IN( " + where + " )";
            } else if (type == 5) {
                wheree = "AND stck.centralsupplier_id IN( " + where + " )";
            }

            sql = "SELECT \n"
                    + "    pli.id AS pliid,\n"
                    + "    pli.stock_id AS plistock_id,\n"
                    + "    stck.name AS stckname,\n"
                    + "    stck.barcode AS stckbarcode,\n"
                    + "    stck.centralsupplier_id AS stckcentralsupplier_id,\n"
                    + "    stck.supplier_id AS stcksupplier_id,\n"
                    + "    si.recommendedprice AS sirecommendedprice,\n"
                    + "    si.currency_id AS sicurrency_id,\n"
                    + "    1 AS tagquantity,\n"
                    + "    crrd2.name AS crrd2name,\n"
                    + "    pli.currency_id AS plicurrency_id,\n"
                    + "    crrd.name AS crrdname,\n"
                    + "    pli.price AS pliprice,\n"
                    + "    pli.is_taxincluded AS pliis_taxincluded,\n"
                    + "    pli.c_time as plic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername,\n"
                    + "    stck.country_id AS stckcountry_id,\n"
                    + "    cond.name AS condname,\n"
                    + "    si.minprofitrate as siminprofitrate,\n"
                    + "    si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                    + "    si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                    + "    COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                    + "    COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                    + "    si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                    + "    COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                    + "    COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                    + "    COALESCE(si.weight,0) AS siweight,\n"
                    + "    si.weightunit_id AS siweightunit_id,\n"
                    + "    wu.name AS wuname,\n"
                    + "    wu.sortname AS wusortname,\n"
                    + "    COALESCE(wu.mainweight,0) AS wumainweight,\n"
                    + "    wu.mainweightunit_id AS wumainweightunit_id,\n"
                    + "    mwu.name AS mwuname,\n"
                    + "    mwu.sortname AS mwusortname,\n"
                    + "                 CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                    + "                     CASE WHEN pl.is_purchase=TRUE THEN  pli.price ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                    + "                 ELSE pli.price END AS pricewithtax\n"
                    + "FROM\n"
                    + "    inventory.pricelistitem pli\n"
                    + "    INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                    + "    INNER JOIN inventory.stock stck ON (stck.id = pli.stock_id AND stck.deleted = False)\n"
                    + "    LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                    + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                    + "    LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                    + "    INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                    + "    LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )\n"
                    + "    LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                    + "    LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)"
                    + "    LEFT JOIN (SELECT \n"
                    + "                 txg.id as id, \n"
                    + "                 txg.rate AS rate,\n"
                    + "                 stc.stock_id AS stock_id \n"
                    + "               FROM inventory.stock_taxgroup_con stc  \n"
                    + "               INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                    + "               WHERE stc.deleted = false\n"
                    + "                     AND txg.type_id = 10 --kdv grubundan \n"
                    + "                     AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)"
                    + "WHERE    \n"
                    + "pli.pricelist_id  = ? AND pli.deleted = FALSE  \n" + wheree + "\n"
                    + " GROUP BY pli.id , pli.stock_id,stck.name ,stck.barcode ,si.recommendedprice, \n"
                    + "    si.currency_id,crrd2.name,pli.currency_id , crrd.name , pli.price, pli.is_taxincluded, pli.c_time , usd.id , usd.name , usd.surname,\n"
                    + "    usd.username, stck.country_id,cond.name  ,  si.currentsaleprice, si.currentsalecurrency_id ,si.lastsalepricechangedate,si.salemandatoryprice,si.weight,si.salemandatorycurrency_id,si.weightunit_id,wu.name,wu.sortname,wu.mainweight,wu.mainweightunit_id,mwu.name,mwu.sortname,si.minprofitrate, si.currentpurchaseprice, si.currentpurchasecurrency_id , \n"
                    + "    pl.is_purchase,stg.rate,stck.centralsupplier_id,stck.supplier_id";

            param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), priceListId};
        }
        List<PriceListItem> result = getJdbcTemplate().query(sql, param, new PriceListItemMapper());
        return result;
    }

    @Override
    public List<PriceListItem> listOfUpdatingPriceStock(PriceList obj) {
        String where = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }

        String sql = "SELECT\n"
                + "	 stck.id AS stckid,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.name AS stckname,\n"
                + "    si.recommendedprice AS sirecommendedprice,\n"
                + "    COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "    si.salemandatorycurrency_id AS sisalemandatorycurrency_id , \n"
                + "    si.currency_id AS sicurrency_id,\n"
                + "    COALESCE(pli.price,0) AS pliprice,\n"
                + "    pli.currency_id AS plicurrency_id\n"
                + "FROM inventory.stock stck\n"
                + "	 LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "    LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.deleted=False)\n"
                + "    LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                + "WHERE stck.deleted = false \n"
                + "	 AND si.recommendedprice IS NOT NULL AND si.recommendedprice<> 0 \n"
                + "    AND CASE WHEN si.currency_id=pli.currency_id THEN si.recommendedprice<> COALESCE(pli.price,0) ELSE TRUE END\n"
                + "    AND pl.id=?\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getId()};
        List<PriceListItem> result = getJdbcTemplate().query(sql, param, new PriceListItemMapper());
        return result;
    }

    @Override
    public int updatingPriceStock(String listOfItem, PriceList priceList, Branch branch) {

        String sql = "SELECT r_pricelist_id FROM inventory.process_priceonrecommended (?,?,?,?);";

        Object[] param = new Object[]{priceList.getId(), listOfItem, sessionBean.getUser().getId(), branch.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<PriceListItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj) {

        if (sortField == null) {
            sortField = "stck.name";
            sortOrder = " asc ";
        }
        String whereSub = " ";
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND stcka.is_otherbranch = FALSE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + "   DISTINCT pli.id AS pliid,\n"
                + "   pli.stock_id AS plistock_id,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.barcode AS stckbarcode,\n"
                + "   si.recommendedprice AS sirecommendedprice,\n"
                + "   si.currency_id AS sicurrency_id,\n"
                + "   crrd2.name AS crrd2name,\n"
                + "   pli.currency_id AS plicurrency_id,\n"
                + "   1 AS tagquantity,\n"
                + "   (SELECT \n"
                + "       string_agg(stcka.barcode,',')\n"
                + "   FROM inventory.stockalternativebarcode stcka\n"
                + "   WHERE stcka.deleted=FALSE AND stcka.stock_id=pli.stock_id " + whereSub + " ) as alternativebarcode,\n"
                + "   crrd.name AS crrdname,\n"
                + "   pli.price AS pliprice,\n"
                + "   pli.is_taxincluded AS pliis_taxincluded,\n"
                + "   pli.c_time as plic_time,\n"
                + "   usd.id as usdid,\n"
                + "   usd.name as usdname,\n"
                + "   usd.surname as usdsurname,\n"
                + "   usd.username as usdusername,\n"
                + "   stck.country_id AS stckcountry_id,\n"
                + "   cond.name AS condname,\n"
                + "   si.minprofitrate as siminprofitrate,\n"
                + "   si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                + "   si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                + "   COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                + "   COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                + "   si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                + "   COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "   COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                + "   COALESCE(si.weight,0) AS siweight,\n"
                + "   si.weightunit_id AS siweightunit_id,\n"
                + "   wu.name AS wuname,\n"
                + "   wu.sortname AS wusortname,\n"
                + "   COALESCE(wu.mainweight,0) AS wumainweight,\n"
                + "   wu.mainweightunit_id AS wumainweightunit_id,\n"
                + "   mwu.name AS mwuname,\n"
                + "   mwu.sortname AS mwusortname,\n"
                + "   CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                + "       CASE WHEN pl.is_purchase=TRUE THEN  pli.price "
                + "       ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                + "   ELSE pli.price END AS pricewithtax\n"
                + "FROM \n"
                + "   inventory.pricelistitem pli\n"
                + "INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (stck.id = pli.stock_id AND stck.deleted = False)\n"
                + "LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                + "INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                + "LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                + "INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                + "LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )  \n"
                + "LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                + "LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN (SELECT \n"
                + "               txg.id as id, \n"
                + "               txg.rate AS rate,\n"
                + "               stc.stock_id AS stock_id \n"
                + "           FROM inventory.stock_taxgroup_con stc  \n"
                + "           INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "           WHERE stc.deleted = false\n"
                + "           AND txg.type_id = 10 --kdv grubundan \n"
                + "           AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "WHERE \n"
                + "pli.pricelist_id = ? AND pli.deleted = false\n"
                + where + "\n"
                + "order by " + sortField + " " + sortOrder + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), obj.getId()};

        List<PriceListItem> result = getJdbcTemplate().query(sql, param, new PriceListItemMapper());
        return result;
    }

    @Override
    public int count(String where, PriceList obj) {
        String whereAlter = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        }
        String sql = "SELECT \n"
                + "   COUNT(DISTINCT pli.id) as plicount\n"
                + "FROM \n"
                + "   inventory.pricelistitem pli\n"
                + "INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck ON (stck.id = pli.stock_id AND stck.deleted = False)\n"
                + "LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                + "INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                + "LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                + "INNER JOIN general.userdata usd ON(usd.id=pli.c_id)\n"
                + "LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )  \n"
                + "LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                + "LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)\n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN (SELECT \n"
                + "               txg.id as id, \n"
                + "               txg.rate AS rate,\n"
                + "               stc.stock_id AS stock_id \n"
                + "           FROM inventory.stock_taxgroup_con stc  \n"
                + "           INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "           WHERE stc.deleted = false\n"
                + "           AND txg.type_id = 10 --kdv grubundan \n"
                + "           AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "WHERE \n"
                + "pli.pricelist_id = ? AND pli.deleted = false\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), obj.getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<PriceListItem> findAllRecordedStock(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj, int type) {

        String lazy = "";
        if (type == 1) {
            if (sortField == null) {
                sortField = "stck.name";
                sortOrder = " asc ";
            }
            lazy = "order by " + sortField + " " + sortOrder + " limit " + pageSize + " offset " + first;

        }

        String whereSub = " ";
        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND stcka.is_otherbranch = FALSE ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + "	 DISTINCT pt.id AS ptid,\n"
                + "    pt.quantity AS ptquantity,\n"
                + "    pli.stock_id AS plistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    ( SELECT \n"
                + "     	string_agg(stcka.barcode,',')\n"
                + "      FROM inventory.stockalternativebarcode stcka\n"
                + "      WHERE stcka.deleted=FALSE AND stcka.stock_id=pt.stock_id " + whereSub + "\n"
                + "     ) as alternativebarcode,\n"
                + "     stck.country_id AS stckcountry_id,\n"
                + "     si.recommendedprice AS sirecommendedprice,\n"
                + "     si.currency_id AS sicurrency_id,\n"
                + "     si.minprofitrate as siminprofitrate,\n"
                + "     si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                + "     si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                + "     COALESCE(si.currentsaleprice,0) AS sicurrentsaleprice,\n"
                + "     COALESCE(si.currentsalecurrency_id,0) AS  sicurrentsalecurrency_id,\n"
                + "     si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                + "     COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "     COALESCE(si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id, \n"
                + "     COALESCE(si.weight,0) AS siweight,\n"
                + "     si.weightunit_id AS siweightunit_id,\n"
                + "     COALESCE(wu.mainweight,0) AS wumainweight,\n"
                + "     wu.mainweightunit_id AS wumainweightunit_id,\n"
                + "     crrd2.name AS crrd2name,\n"
                + "     pli.id AS pliid,\n"
                + "     pli.currency_id AS plicurrency_id,\n"
                + "     crrd.name AS crrdname,\n"
                + "     pli.price AS pliprice,\n"
                + "     pli.is_taxincluded AS pliis_taxincluded,\n"
                + "     cond.name AS condname,\n"
                + "     wu.name AS wuname,\n"
                + "     wu.sortname AS wusortname,\n"
                + "     mwu.name AS mwuname,\n"
                + "     mwu.sortname AS mwusortname,\n"
                + "     CASE WHEN pli.is_taxincluded=FALSE THEN \n"
                + "             CASE WHEN pl.is_purchase=TRUE THEN  pli.price \n"
                + "             ELSE (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) END\n"
                + "     ELSE pli.price END AS pricewithtax\n"
                + "FROM log.printtag pt \n"
                + "INNER JOIN inventory.stock stck ON(stck.id = pt.stock_id AND stck.deleted=FALSE)\n"
                + "LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                + "LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                + "INNER JOIN inventory.pricelistitem pli ON(pli.stock_id = pt.stock_id AND pli.deleted=FALSE)\n"
                + "INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                + "LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ?)\n"
                + "LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                + "LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)  \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN (SELECT \n"
                + "                     txg.id as id, \n"
                + "                     txg.rate AS rate,\n"
                + "                     stc.stock_id AS stock_id \n"
                + "                 FROM inventory.stock_taxgroup_con stc  \n"
                + "                 INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "                 WHERE stc.deleted = false\n"
                + "                 AND txg.type_id = 10 --kdv grubundan \n"
                + "                 AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "WHERE pt.branch_id = ? AND pl.id = ?\n"
                + where + "\n"
                + lazy;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getId()};

        List<PriceListItem> result = getJdbcTemplate().query(sql, param, new PriceListItemMapper());
        return result;
    }

    @Override
    public int countRecordedStock(String where, PriceList obj) {

        String whereAlter = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        }
        String sql = "SELECT \n"
                + "   COUNT(DISTINCT pt.id) as ptid\n"
                + "FROM log.printtag pt \n"
                + "INNER JOIN inventory.stock stck ON(stck.id = pt.stock_id AND stck.deleted=FALSE)\n"
                + "LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                + "LEFT JOIN system.currency_dict crrd2  ON (crrd2.currency_id = si.currency_id AND crrd2.language_id =?)\n"
                + "INNER JOIN inventory.pricelistitem pli ON(pli.stock_id = pt.stock_id AND pli.deleted=FALSE)\n"
                + "INNER JOIN inventory.pricelist pl ON (pl.id=pli.pricelist_id AND pl.deleted=FALSE)\n"
                + "INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = pli.currency_id AND crrd.language_id =?)\n"
                + "LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ?)\n"
                + "LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                + "LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)  \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + ")\n"
                + "LEFT JOIN (SELECT \n"
                + "                     txg.id as id, \n"
                + "                     txg.rate AS rate,\n"
                + "                     stc.stock_id AS stock_id \n"
                + "                 FROM inventory.stock_taxgroup_con stc  \n"
                + "                 INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "                 WHERE stc.deleted = false\n"
                + "                 AND txg.type_id = 10 --kdv grubundan \n"
                + "                 AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "WHERE pt.branch_id = ? AND pl.id = ?\n"
                + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(),
            obj.getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int deleteRecordedStock(String deleteList) {
        String where = "";
        if (!deleteList.equals("")) {
            where = where + " AND id IN(" + deleteList + ") ";
        }

        String sql = "DELETE FROM log.printtag WHERE branch_id = ? " + where + "\n";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
