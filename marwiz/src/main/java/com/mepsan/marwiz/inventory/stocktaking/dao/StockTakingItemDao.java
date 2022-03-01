/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.02.2018 07:40:18
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockTakingItemDao extends JdbcDaoSupport implements IStockTakingItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int processStockTakingItem(int type, StockTaking stockTaking , boolean isReset) {

        String sql = " SELECT r_id FROM inventory.process_stocktakingitem(?, ?, ?, ? , ?);";
        Object[] param = {type, stockTaking.getJsonItems(), stockTaking.getId(), sessionBean.getUser().getId() , isReset};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Bu Fonksiyon Depo Sayımı sayfasında sayım farkını kapattıktan sonra
     * depoya giriş çıkış olmuş mu diye kontrol eder. Olanları liste şeklinde
     * döndürür.
     *
     * @param obj
     * @return
     */
    @Override
    public List<StockTakingItem> findAllSaleControlList(StockTaking obj) {

        String where = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm");
        if (obj.isIsRetrospective()) {
            where += " AND whr.processdate< '" + sd.format(obj.getEndDate()) + "' ";
        }

        String sql = " SELECT\n"
                + "        sti.id,\n"
                + "        sti.stock_id,\n"
                + "        stck.name,\n"
                + "        sti.systemquantity,\n"
                + "        sti.realquantity,\n"
                + "        SUM(CASE WHEN whm.is_direction=TRUE THEN whm.quantity ELSE 0 END) AS entry,\n"
                + "        SUM(CASE WHEN whm.is_direction=FALSE THEN whm.quantity ELSE 0 END) AS exit\n"
                + "\n"
                + "        FROM \n"
                + "        inventory.stocktakingitem sti\n"
                + "        LEFT JOIN inventory.stock stck ON (sti.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "        LEFT JOIN inventory.warehousemovement whm ON (whm.stock_id=stck.id AND whm.deleted=FALSE)\n"
                + "        LEFT JOIN inventory.warehousereceipt whr ON (whr.id=whm.warehousereceipt_id AND whr.deleted=FALSE)\n"
                + "        WHERE sti.stocktaking_id=? \n"
                + "        AND whr.is_canceled =FALSE\n"
                + "        AND sti.deleted=FALSE AND whr.processdate > sti.processdate AND whr.warehouse_id=?\n " + where
                + "        GROUP BY sti.id,sti.stock_id,stck.name,sti.systemquantity,sti.realquantity,sti.processdate;";

        Object[] param = {obj.getId(), obj.getWarehouse().getId()};

        List<StockTakingItem> result = getJdbcTemplate().query(sql, param, new StockTakingItemMapper());
        return result;
    }

    /**
     * Sayımda sayılmayan miktarı sıfırdan farklı olan stokları bulur.
     *
     * @param stockTaking
     * @return
     */
    @Override
    public List<StockTakingItem> findAllUncountedStocks(StockTaking stockTaking) {
        String where = "";
        if (stockTaking.getCategories() != null && !stockTaking.getCategories().equals("")) {
            where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + stockTaking.getCategories() + ") ) ";
        }

        String subQuery = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm");
        if (stockTaking.isIsRetrospective()) {
            subQuery = subQuery + " iwi.quantity+COALESCE((SELECT\n"
                    + "                    SUM(CASE WHEN whm.is_direction=TRUE THEN -whm.quantity ELSE whm.quantity END)\n"
                    + "                FROM \n"
                    + "                    inventory.warehousemovement whm\n"
                    + "                    LEFT JOIN inventory.warehousereceipt whr ON (whr.id=whm.warehousereceipt_id AND whr.deleted=FALSE)\n"
                    + "                WHERE \n"
                    + "                    whm.deleted=FALSE \n"
                    + "                    AND whm.stock_id=stck.id \n"
                    + "                    AND whr.is_canceled = FALSE\n"
                    + "                    AND whr.processdate > '" + sd.format(stockTaking.getBeginDate()) + "'\n"
                    + "                    AND whr.warehouse_id = " + stockTaking.getWarehouse().getId() + "),0) \n"
                    + " as iwiquantity \n";
        } else {
            subQuery = "iwi.quantity as iwiquantity \n";
        }
        String sql = "SELECT * FROM (SELECT \n"
                + "stck.id as stckid, \n"
                + "stck.name as stckname, \n"
                + "stck.code as stckcode, \n"
                + "stck.centerproductcode as stckcenterproductcode, \n"
                + "stck.barcode as stckbarcode,  \n"
                + "gunt.name as guntname, \n"
                + "gunt.sortname as guntsortname, \n"
                + "gunt.unitrounding as guntunitrounding, \n"
                + subQuery
                + "FROM inventory.warehouseitem iwi \n"
                + "LEFT JOIN inventory.stock stck ON (iwi.stock_id=stck.id and stck.deleted=false) \n"
                + "LEFT JOIN inventory.stocktakingitem sti ON (sti.stock_id=stck.id and sti.deleted=false and sti.stocktaking_id=?) \n"
                + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false) \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE iwi.warehouse_id=? AND iwi.deleted=FALSE AND stck.status_id=3 AND si.is_passive = FALSE \n"
                + "AND sti.id IS NULL  AND stck.is_service=FALSE  \n"
                + where + " ) t\n"
                + "WHERE t.iwiquantity <> 0 \n"
                + "ORDER BY t.stckname ASC";

        Object[] param = {stockTaking.getId(), sessionBean.getUser().getLastBranch().getId(), stockTaking.getWarehouse().getId()};
        List<StockTakingItem> result = getJdbcTemplate().query(sql, param, new StockTakingItemMapper());
        return result;
    }

    @Override
    public List<StockTakingItem> findAllMinusStocks(StockTaking stockTaking) {
        String sql = "select \n"
                + "sti.id as stiid,\n"
                + "sti.stock_id as stckid,\n"
                + "stck.name as stckname,\n"
                + "stck.code as stckcode,\n"
                + "stck.centerproductcode as stckcenterproductcode,\n"
                + "stck.barcode as stckbarcode,\n"
                + "sti.systemquantity as stisystemquantity,\n"
                + "sti.realquantity as stirealquantity,\n"
                + "sti.processdate as stiprocessdate\n"
                + "from inventory.stocktakingitem sti\n"
                + "LEFT JOIN inventory.stock stck ON (sti.stock_id=stck.id and stck.deleted=false)\n"
                + "where sti.stocktaking_id=? and sti.deleted=false and sti.realquantity<0";

        Object[] param = {stockTaking.getId()};
        List<StockTakingItem> result = getJdbcTemplate().query(sql, param, new StockTakingItemMapper());
        return result;
    }

    /**
     * Göderilen kategoride olmayan stokların Depo sayımı itemdaki kayıtlarını
     * çeker.
     *
     * @param obj
     * @param categories
     * @return
     */
    @Override
    public List<StockTakingItem> findWithoutCategorization(StockTaking obj, String categories) {
        String sql = "SELECT \n"
                + "sti.id as stiid\n"
                + "FROM inventory.stocktakingitem sti\n"
                + "WHERE sti.stocktaking_id=? and sti.deleted=FALSE\n"
                + "AND sti.stock_id NOT IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + categories + " ) )";

        Object[] param = {obj.getId()};
        List<StockTakingItem> result = getJdbcTemplate().query(sql, param, new StockTakingItemMapper());
        return result;
    }

    @Override
    public String importStockTakingItem(String json, StockTaking stockTaking) {
        String sql = "SELECT r_message FROM inventory.excel_stocktakingitem(?, ?, ?);";

        Object[] param = new Object[]{json, stockTaking.getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public String exportData(String where, StockTaking stockTaking) {
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid = TRUE  ";
        } else {
            where = where + " AND ist.is_otherbranch = TRUE  ";
        }
        String subQuery = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm");
        if (stockTaking.isIsRetrospective()) {
            subQuery = subQuery + " iwi.quantity+COALESCE((SELECT\n"
                    + "                    SUM(CASE WHEN whm.is_direction=TRUE THEN -whm.quantity ELSE whm.quantity END)\n"
                    + "                FROM \n"
                    + "                    inventory.warehousemovement whm\n"
                    + "                    LEFT JOIN inventory.warehousereceipt whr ON (whr.id=whm.warehousereceipt_id AND whr.deleted=FALSE)\n"
                    + "                WHERE \n"
                    + "                    whm.deleted=FALSE \n"
                    + "                    AND whm.stock_id=ist.id \n"
                    + "                    AND whr.is_canceled = FALSE\n"
                    + "                    AND whr.processdate > '" + sd.format(stockTaking.getBeginDate()) + "'\n"
                    + "                    AND whr.warehouse_id = " + stockTaking.getWarehouse().getId() + "),0) \n"
                    + " as iwiquantity ,\n";
        } else {
            subQuery = "iwi.quantity as iwiquantity,\n";
        }

        String sql = "SELECT \n"
                + "  ist.barcode AS istbarcode,\n"
                + "  ist.name AS istname,\n"
                + subQuery
                + "   gunt.unitrounding AS guntunitrounding \n"
                + "  FROM \n"
                + "  inventory.warehouseitem iwi\n"
                + "  INNER JOIN inventory.stock ist ON(ist.id = iwi.stock_id AND ist.deleted = FALSE)\n"
                + "  LEFT JOIN inventory.stockinfo si ON (si.stock_id=ist.id AND si.deleted=False AND si.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "   INNER JOIN general.unit gunt ON(gunt.id = ist.unit_id)"
                + "WHERE\n"
                + "	iwi.deleted = FALSE AND iwi.warehouse_id = " + stockTaking.getWarehouse().getId() + "  " + where;

        return sql;
    }

    @Override
    public int delete(String ids) {
        String sql = "UPDATE inventory.stocktakingitem set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id IN (" + ids + ") ";
        Object[] param = new Object[]{sessionBean.getUser().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateSaleControl(StockTaking stockTaking) {

        String where = " ";
        String processDate = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm");
        if (stockTaking.isIsRetrospective()) {
            where += " AND whr.processdate< '" + sd.format(stockTaking.getEndDate()) + "' ";
            processDate += " '" + sd.format(stockTaking.getEndDate()) + "'";
        } else {
            processDate += " now() ";
        }
        String sql = "UPDATE\n"
                + "inventory.stocktakingitem isi\n"
                + "SET\n"
                + "systemquantity = t.systemquantity + t.entry - t.exit,\n"
                + "realquantity = t.realquantity + t.entry - t.exit,\n"
                + "processdate = " + processDate + " ,\n"
                + "u_time =  now(),\n"
                + "u_id= ?\n"
                + "FROM\n"
                + "(\n"
                + "select\n"
                + "sti.id,\n"
                + "sti.stock_id,\n"
                + "stck.name,\n"
                + "sti.systemquantity,\n"
                + "sti.realquantity,\n"
                + "sti.processdate,\n"
                + "SUM(case when whm.is_direction=true THEN whm.quantity else 0 END) as entry,\n"
                + "SUM(case when whm.is_direction=false then whm.quantity else 0 END) as exit\n"
                + "\n"
                + "from \n"
                + "inventory.stocktakingitem sti\n"
                + "left join inventory.stock stck on (sti.stock_id=stck.id and stck.deleted=false)\n"
                + "left join inventory.warehousemovement whm on (whm.stock_id=stck.id and whm.deleted=false)\n"
                + "left join inventory.warehousereceipt whr on (whr.id=whm.warehousereceipt_id and whr.deleted=false)\n"
                + "where sti.stocktaking_id=? and whr.is_canceled =false\n"
                + "and sti.deleted=false and whr.processdate > sti.processdate " + where + " AND whr.warehouse_id=?\n"
                + "group by sti.id,sti.stock_id,stck.name,sti.systemquantity,sti.realquantity,sti.processdate,stck.barcode\n"
                + ") t\n"
                + "\n"
                + "WHERE isi.id = t.id";
        Object[] param = new Object[]{sessionBean.getUser().getId(), stockTaking.getId(), stockTaking.getWarehouse().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<StockTakingItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockTaking stockTaking) {

        String sql = "";
        Object[] param = null;

        if (sortField == null) {
            sortField = "stiid";
            sortOrder = " ASC ";
        } else if (sortField.equals("systemQuantity")) {
            if (stockTaking.getStatus().getId() == 15) {
                sortField = " iwiquantity ";
            } else if (stockTaking.getStatus().getId() == 16) {
                sortField = "stisystemquantity";
            }
        }

        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        if (stockTaking.getStatus().getId() == 15) { //açıksa o an depoda olan bütün stokları getiriyo
            if (stockTaking.getCategories() != null && !stockTaking.getCategories().equals("")) {
                where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + stockTaking.getCategories() + ") ) ";
            }
            sql = "WITH stock_temp AS (\n"
                    + "SELECT\n"
                    + "	whm.stock_id,\n"
                    + "	SUM(CASE WHEN whm.is_direction = TRUE THEN -whm.quantity ELSE whm.quantity END) as quantity\n"
                    + "FROM\n"
                    + "    inventory.warehousemovement whm\n"
                    + "    LEFT JOIN inventory.warehousereceipt whr ON (whr.id = whm.warehousereceipt_id AND whr.deleted = FALSE)\n"
                    + "WHERE\n"
                    + "   whm.deleted = FALSE \n"
                    + "   AND whr.is_canceled = FALSE\n"
                    + "   AND whr.processdate > ? \n"
                    + "   AND whr.warehouse_id = ?\n"
                    + "GROUP BY\n"
                    + "	whm.stock_id)\n"
                    + "\n"
                    + "SELECT *,\n"
                    + "CASE WHEN stocktakingitem.stiid IS NULL THEN (stocktakingitem.iwiquantity - stocktakingitem.stirealquantity) ELSE (stocktakingitem.stisystemquantity - stocktakingitem.stirealquantity) END as diff,\n"
                    + "CASE WHEN stocktakingitem.stiid IS NULL THEN (stocktakingitem.iwiquantity * stocktakingitem.price) ELSE (stocktakingitem.stisystemquantity * stocktakingitem.price) END as systemprice,\n"
                    + "stocktakingitem.stirealquantity * stocktakingitem.price AS realprice,\n"
                    + "CASE WHEN stocktakingitem.stiid IS NULL THEN ((stocktakingitem.iwiquantity - stocktakingitem.stirealquantity) * stocktakingitem.price) ELSE ((stocktakingitem.stisystemquantity - stocktakingitem.stirealquantity) * stocktakingitem.price) END as diffprice \n"
                    + "FROM\n"
                    + "(\n"
                    + "SELECT \n"
                    + "DISTINCT stck.id as stckid,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode as stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END as iwiquantity ,\n"
                    + "sti.id as stiid,\n"
                    + "sti.processdate as stiprocessdate,\n"
                    + "sti.realquantity as stirealquantity,\n"
                    + "sti.systemquantity as stisystemquantity,\n"
                    + "CASE WHEN prli.id IS NULL THEN 0\n"
                    + "WHEN st.is_taxincluded = TRUE THEN \n"
                    + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END\n"
                    + "ELSE\n"
                    + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END  \n"
                    + "\n"
                    + "END AS price,\n"
                    + "prli.currency_id AS prlicurrency_id,\n"
                    + "    sti.c_time as stic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername \n"
                    + "FROM inventory.warehouseitem iwi\n"
                    + "LEFT JOIN inventory.stock stck ON (iwi.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                    + "LEFT JOIN inventory.stocktakingitem sti ON (sti.stock_id=stck.id and sti.deleted=false and sti.stocktaking_id=?)\n"
                    + "LEFT JOIN inventory.stocktaking st ON (st.id=? and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE iwi.warehouse_id=? AND iwi.deleted=FALSE AND stck.status_id=3 AND stck.is_service=FALSE AND si.is_passive = FALSE\n"
                    + where + " \n"
                    + ")  stocktakingitem\n"
                    + "ORDER BY " + sortField + " " + sortOrder + " \n"
                    + " limit " + pageSize + " offset " + first;
            param = new Object[]{stockTaking.getBeginDate(), stockTaking.getWarehouse().getId(), sessionBean.getUser().getLastBranch().getId(), stockTaking.getId(), stockTaking.getId(), stockTaking.getWarehouse().getId()};
        } else if (stockTaking.getStatus().getId() == 16) { // kapalıysa sadece sayımda sayılan stokları getiriyor.
            sql = "SELECT \n"
                    + "DISTINCT stck.id as stckid,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode as stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "sti.id as stiid,\n"
                    + "sti.processdate as stiprocessdate,\n"
                    + "sti.realquantity as stirealquantity,\n"
                    + "sti.systemquantity as stisystemquantity,\n"
                    + "COALESCE(sti.currentpricelistprice,0) AS price,\n"
                    + "sti.currentpricelistcurrency_id AS prlicurrency_id\n,"
                    + "(sti.systemquantity - sti.realquantity) AS diff,\n"
                    + "(sti.systemquantity * COALESCE(sti.currentpricelistprice,0))  as systemprice,\n"
                    + "(sti.realquantity * COALESCE(sti.currentpricelistprice,0)) AS realprice,\n"
                    + "((sti.systemquantity - sti.realquantity) * COALESCE(sti.currentpricelistprice,0)) as diffprice,\n"
                    + " sti.c_time as stic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername \n"
                    + "FROM inventory.stocktakingitem sti\n"
                    + "LEFT JOIN inventory.stock stck ON (sti.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stocktaking st ON (sti.stocktaking_id=st.id and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE ) \n"
                    + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE sti.stocktaking_id=? AND sti.deleted=FALSE " + where + "\n"
                    + "ORDER BY " + sortField + " " + sortOrder + " \n"
                    + " limit " + pageSize + " offset " + first;

            param = new Object[]{stockTaking.getId()};
        }
        List<StockTakingItem> result = getJdbcTemplate().query(sql, param, new StockTakingItemMapper());
        return result;
    }

    @Override
    public int count(String where, StockTaking stockTaking
    ) {

        String sql = "";
        Object[] param = null;

        String whereSub = " ";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        if (stockTaking.getStatus().getId() == 15) { //açıksa o an depoda olan bütün stokları getiriyo

            if (stockTaking.getCategories() != null && !stockTaking.getCategories().equals("")) {
                where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + stockTaking.getCategories() + ") ) ";
            }
            sql = "WITH stock_temp AS (\n"
                    + "SELECT\n"
                    + "	whm.stock_id,\n"
                    + "	SUM(CASE WHEN whm.is_direction = TRUE THEN -whm.quantity ELSE whm.quantity END) as quantity\n"
                    + "FROM\n"
                    + "    inventory.warehousemovement whm\n"
                    + "    LEFT JOIN inventory.warehousereceipt whr ON (whr.id = whm.warehousereceipt_id AND whr.deleted = FALSE)\n"
                    + "WHERE\n"
                    + "   whm.deleted = FALSE \n"
                    + "   AND whr.is_canceled = FALSE\n"
                    + "   AND whr.processdate > ? \n"
                    + "   AND whr.warehouse_id = ?\n"
                    + "GROUP BY\n"
                    + "	whm.stock_id)\n"
                    + "\n"
                    + "SELECT \n"
                    + "COUNT(DISTINCT stck.id) as stckid\n"
                    + "FROM inventory.warehouseitem iwi\n"
                    + "LEFT JOIN inventory.stock stck ON (iwi.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                    + "LEFT JOIN inventory.stocktakingitem sti ON (sti.stock_id=stck.id and sti.deleted=false and sti.stocktaking_id=?)\n"
                    + "LEFT JOIN inventory.stocktaking st ON (st.id=? and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE iwi.warehouse_id=? AND iwi.deleted=FALSE AND stck.status_id=3 AND stck.is_service=FALSE AND si.is_passive = FALSE\n"
                    + where;
            param = new Object[]{stockTaking.getBeginDate(), stockTaking.getWarehouse().getId(), sessionBean.getUser().getLastBranch().getId(), stockTaking.getId(), stockTaking.getId(), stockTaking.getWarehouse().getId()};
        } else if (stockTaking.getStatus().getId() == 16) { // kapalıysa sadece sayımda sayılan stokları getiriyor.
            sql = "SELECT \n"
                    + "COUNT(DISTINCT stck.id) as stckid\n"
                    + "FROM inventory.stocktakingitem sti\n"
                    + "LEFT JOIN inventory.stock stck ON (sti.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stocktaking st ON (sti.stocktaking_id=st.id and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereSub + ")\n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE sti.stocktaking_id=? AND sti.deleted=FALSE " + where;

            param = new Object[]{stockTaking.getId()};
        }

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;

    }

    @Override
    public String exportData(List<StockTakingItem> listOfItemUpdate, StockTaking stockTaking
    ) {
        String sql = "";
        Object[] param = null;
        String where = "";
        String whereSub = " ";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereSub = whereSub + " AND sab.is_otherbranch = FALSE ";
        }

        if (stockTaking.getStatus().getId() == 15) { //açıksa o an depoda olan bütün stokları getiriyo

            if (stockTaking.getCategories() != null && !stockTaking.getCategories().equals("")) {
                where += " AND stck.id IN (SELECT scc.stock_id FROM inventory.stock_categorization_con scc WHERE scc.deleted=FALSE AND scc.categorization_id IN ( " + stockTaking.getCategories() + ") ) ";
            }

            sql = "WITH stock_temp AS (\n"
                    + "SELECT\n"
                    + "	whm.stock_id,\n"
                    + "	SUM(CASE WHEN whm.is_direction = TRUE THEN -whm.quantity ELSE whm.quantity END) as quantity\n"
                    + "FROM\n"
                    + "    inventory.warehousemovement whm\n"
                    + "    LEFT JOIN inventory.warehousereceipt whr ON (whr.id = whm.warehousereceipt_id AND whr.deleted = FALSE)\n"
                    + "WHERE\n"
                    + "   whm.deleted = FALSE \n"
                    + "   AND whr.is_canceled = FALSE\n"
                    + "   AND whr.processdate > '" + sd.format(stockTaking.getBeginDate()) + "' \n"
                    + "   AND whr.warehouse_id = " + stockTaking.getWarehouse().getId() + "\n"
                    + "GROUP BY\n"
                    + "	whm.stock_id)\n"
                    + "\n"
                    + "SELECT \n"
                    + "stck.id as stckid,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode as stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END as iwiquantity ,\n"
                    + "sti.id as stiid,\n"
                    + "sti.processdate as stiprocessdate,\n"
                    + "sti.realquantity as stirealquantity,\n"
                    + "sti.systemquantity as stisystemquantity,\n"
                    + "CASE WHEN prli.id IS NULL THEN 0\n"
                    + "WHEN st.is_taxincluded = TRUE THEN \n"
                    + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END\n"
                    + "ELSE\n"
                    + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END  \n"
                    + "\n"
                    + "END AS price,\n"
                    + "prli.currency_id AS prlicurrency_id,\n"
                    + " sti.c_time as stic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername \n"
                    + "FROM inventory.warehouseitem iwi\n"
                    + "LEFT JOIN inventory.stock stck ON (iwi.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                    + "LEFT JOIN inventory.stocktakingitem sti ON (sti.stock_id=stck.id and sti.deleted=false and sti.stocktaking_id=" + stockTaking.getId() + ")\n"
                    + "LEFT JOIN inventory.stocktaking st ON (st.id=" + stockTaking.getId() + " and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE iwi.warehouse_id=" + stockTaking.getWarehouse().getId() + " AND iwi.deleted=FALSE AND stck.status_id=3 AND stck.is_service=FALSE AND si.is_passive = FALSE\n"
                    + where + " \n"
                    + "ORDER BY sti.id ASC, stck.name ASC";

        } else if (stockTaking.getStatus().getId() == 16) { // kapalıysa sadece sayımda sayılan stokları getiriyor.
            sql = "SELECT \n"
                    + "stck.id as stckid,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode as stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "sti.id as stiid,\n"
                    + "sti.processdate as stiprocessdate,\n"
                    + "sti.realquantity as stirealquantity,\n"
                    + "sti.systemquantity as stisystemquantity,\n"
                    + "COALESCE(sti.currentpricelistprice,0) AS price,\n"
                    + "sti.currentpricelistcurrency_id AS prlicurrency_id,\n"
                    + " sti.c_time as stic_time,\n"
                    + "    usd.id as usdid,\n"
                    + "    usd.name as usdname,\n"
                    + "    usd.surname as usdsurname,\n"
                    + "    usd.username as usdusername \n"
                    + "FROM inventory.stocktakingitem sti\n"
                    + "LEFT JOIN inventory.stock stck ON (sti.stock_id=stck.id and stck.deleted=false)\n"
                    + "LEFT JOIN inventory.stocktaking st ON (sti.stocktaking_id=st.id and st.deleted=false)\n"
                    + "LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
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
                    + "LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + " LEFT JOIN general.userdata usd ON(usd.id=sti.c_id)\n"
                    + "WHERE sti.stocktaking_id=" + stockTaking.getId() + " AND sti.deleted=FALSE " + where + "\n"
                    + "ORDER BY stck.name ASC";

        }

        return sql;

    }

}
