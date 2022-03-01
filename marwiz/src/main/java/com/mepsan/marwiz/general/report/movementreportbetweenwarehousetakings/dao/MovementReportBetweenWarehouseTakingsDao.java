/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:22:57
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.inventory.stocktaking.dao.StockTakingMapper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MovementReportBetweenWarehouseTakingsDao extends JdbcDaoSupport implements IMovementReportBetweenWarehouseTakingsDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String exportData(String where) {
        String sql = "SELECT\n"
                + "    sti.stock_id AS stistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    unt.sortname AS untsortname,\n"
                + "    unt.unitrounding AS untunitrounding,\n"
                + "    COALESCE(sti.realquantity,0) AS stirealquantity,\n"
                + "    COALESCE(t.entryamount,0) AS entryamount,\n"
                + "    COALESCE(t.exitamount,0) AS exitamount,\n"
                + "    COALESCE(sti2.realquantity,0) AS quantity2,\n"
                + "    CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti2.currentpurchaseprice,0) END AS lastpurchaseprice,\n"
                + "    CASE WHEN st2.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti2.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "    CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti2.currentsaleprice,0) END AS lastsaleprice,\n"
                + "    CASE WHEN st2.status_id = 15 THEN si.currentsalecurrency_id ELSE sti2.currentsalecurrency_id END AS lastsalecurrency_id,\n"
                + "    COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = TRUE LIMIT 1 ),0) AS purchasetaxgrouprate,\n"
                + "    COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = FALSE  LIMIT 1 ),0) AS salestaxgrouprate\n"
                + "FROM\n"
                + "    inventory.stocktakingitem sti\n"
                + "    INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "    INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN inventory.stocktakingitem sti2 ON(sti2.stocktaking_id =  ? AND sti2.stock_id = sti.stock_id)\n"
                + "    LEFT JOIN inventory.stocktaking st2 ON(st2.id = ? AND st2.deleted = FALSE)\n"
                + "    INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)\n"
                + "    LEFT JOIN(\n"
                + "    SELECT \n"
                + "    whm.stock_id AS whmstock_id,\n"
                + "    SUM(CASE WHEN whm.is_direction = TRUE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS entryamount,\n"
                + "    SUM(CASE WHEN whm.is_direction = FALSE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS exitamount\n"
                + "    FROM inventory.warehousemovement whm \n"
                + "    INNER JOIN inventory.warehousereceipt wr ON(wr.id = whm.warehousereceipt_id AND wr.deleted=FALSE)\n  "
                + "    WHERE whm.warehouse_id =? AND  whm.deleted = FALSE\n"
                + "    AND wr.processdate > ? AND wr.processdate < ?\n"
                + "    GROUP BY whm.stock_id\n"
                + "    ) t ON (t.whmstock_id = sti.stock_id)\n"
                + "WHERE\n"
                + "	sti.stocktaking_id = ? AND sti.deleted = FALSE " + where;

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<MovementReportBetweenWarehouseTakings> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings) {

        String sql = "SELECT\n"
                + "    sti.stock_id AS stistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.centerproductcode AS stckcenterproductcode,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    unt.sortname AS untsortname,\n"
                + "    unt.unitrounding AS untunitrounding,\n"
                + "    COALESCE(sti.realquantity,0) AS stirealquantity,\n"
                + "    COALESCE(t.entryamount,0) AS entryamount,\n"
                + "    COALESCE(t.exitamount,0) AS exitamount,\n"
                + "    COALESCE(sti2.realquantity,0) AS quantity2,\n"
                + "    CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti2.currentpurchaseprice,0) END AS lastpurchaseprice,\n"
                + "    CASE WHEN st2.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti2.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "    CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti2.currentsaleprice,0) END AS lastsaleprice,\n"
                + "    CASE WHEN st2.status_id = 15 THEN si.currentsalecurrency_id ELSE sti2.currentsalecurrency_id END AS lastsalecurrency_id,\n"
                + "    COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = TRUE LIMIT 1 ),0) AS purchasetaxgrouprate,\n"
                + "    COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = FALSE  LIMIT 1 ),0) AS salestaxgrouprate\n"
                + "FROM\n"
                + "    inventory.stocktakingitem sti \n"
                + "    INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "    INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN inventory.stocktakingitem sti2 ON(sti2.stocktaking_id =  ? AND sti2.stock_id = sti.stock_id)\n"
                + "    LEFT JOIN inventory.stocktaking st2 ON(st2.id = ? AND st2.deleted = FALSE)\n"
                + "    INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)\n"
                + "    LEFT JOIN(\n"
                + "    SELECT \n"
                + "    whm.stock_id AS whmstock_id,\n"
                + "    SUM(CASE WHEN whm.is_direction = TRUE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS entryamount,\n"
                + "    SUM(CASE WHEN whm.is_direction = FALSE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS exitamount\n"
                + "    FROM inventory.warehousemovement whm \n"
                + "    INNER JOIN inventory.warehousereceipt wr ON(wr.id = whm.warehousereceipt_id AND wr.deleted=FALSE)\n  "
                + "    WHERE whm.warehouse_id =? AND  whm.deleted = FALSE\n"
                + "    AND wr.processdate > ? AND wr.processdate < ?\n"
                + "    GROUP BY whm.stock_id\n"
                + "    ) t ON (t.whmstock_id = sti.stock_id)\n"
                + "WHERE\n"
                + "	sti.stocktaking_id = ? AND sti.deleted = FALSE " + where + "\n"
                + "limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{movementReportBetweenWarehouseTakings.getStockTaking2().getId(), movementReportBetweenWarehouseTakings.getStockTaking2().getId(), movementReportBetweenWarehouseTakings.getStockTaking1().getWarehouse().getId(),  movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() != null ? movementReportBetweenWarehouseTakings.getStockTaking1().getEndDate() : new Date(),movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() != null ? movementReportBetweenWarehouseTakings.getStockTaking2().getEndDate() : new Date(),movementReportBetweenWarehouseTakings.getStockTaking1().getId()};
        System.out.println("--param--" + Arrays.toString(param));
        List<MovementReportBetweenWarehouseTakings> result = getJdbcTemplate().query(sql, param, new MovementReportBetweenWarehouseTakingsMapper());

        return result;
    }

    @Override
    public List<MovementReportBetweenWarehouseTakings> totals(String where, MovementReportBetweenWarehouseTakings obj) {

        String sql = "SELECT\n"
                + "COUNT(tt.stistock_id) AS stistock_id,\n"
                + "SUM(tt.lastpurchaseprice*((COALESCE(tt.quantity2,0)-COALESCE(tt.stirealquantity,0))-(tt.entryamount-tt.exitamount))) AS lastpurchaseprice,\n"
                + "SUM(tt.lastsaleprice*((COALESCE(tt.quantity2,0)-COALESCE(tt.stirealquantity,0))-(tt.entryamount-tt.exitamount))) AS lastsaleprice,\n"
                + "tt.lastpurchasecurrency_id AS lastpurchasecurrency_id,\n"
                + "tt.lastsalecurrency_id AS lastsalecurrency_id\n"
                + "FROM\n"
                + "(\n"
                + "    SELECT\n"
                + "         sti.stock_id AS stistock_id, \n"
                + "         COALESCE(sti.realquantity,0) AS stirealquantity,\n"
                + "         COALESCE(sti2.realquantity,0) AS quantity2,\n"
                + "         COALESCE(t.entryamount,0) AS entryamount,\n"
                + "         COALESCE(t.exitamount,0) AS exitamount,\n"
                + "         CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti2.currentpurchaseprice,0) END AS lastpurchaseprice,\n"
                + "         CASE WHEN st2.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti2.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "         CASE WHEN st2.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti2.currentsaleprice,0) END AS lastsaleprice,\n"
                + "         CASE WHEN st2.status_id = 15 THEN si.currentsalecurrency_id ELSE sti2.currentsalecurrency_id END AS lastsalecurrency_id\n"
                + "     FROM  \n"
                + "         inventory.stocktakingitem sti   \n"
                + "         INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)  \n"
                + "         INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)  \n"
                + "         LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "         LEFT JOIN inventory.stocktakingitem sti2 ON(sti2.stocktaking_id = ? AND sti2.stock_id = sti.stock_id)\n"
                + "         LEFT JOIN inventory.stocktaking st2 ON(st2.id = ? AND st2.deleted = FALSE)\n"
                + "         INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)  \n"
                + "         LEFT JOIN(\n"
                + "         SELECT \n"
                + "         whm.stock_id AS whmstock_id,\n"
                + "         SUM(CASE WHEN whm.is_direction = TRUE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS entryamount,\n"
                + "         SUM(CASE WHEN whm.is_direction = FALSE THEN COALESCE(whm.quantity,0) ELSE 0 END) AS exitamount\n"
                + "         FROM inventory.warehousemovement whm \n"
                + "         INNER JOIN inventory.warehousereceipt wr ON(wr.id = whm.warehousereceipt_id AND wr.deleted=FALSE)\n  "
                + "         WHERE whm.warehouse_id =? AND  whm.deleted = FALSE\n"
                + "         AND wr.processdate > ? AND wr.processdate < ?\n"
                + "         GROUP BY whm.stock_id\n"
                + "         ) t ON (t.whmstock_id = sti.stock_id)\n"
                + "     WHERE  \n"
                + "      sti.stocktaking_id =? AND sti.deleted = FALSE " + where + "\n"
                + ") tt\n"
                + "GROUP BY tt.lastpurchasecurrency_id,tt.lastsalecurrency_id";

        Object[] param = new Object[]{obj.getStockTaking2().getId(), obj.getStockTaking2().getId(), obj.getStockTaking1().getWarehouse().getId(),  obj.getStockTaking1().getEndDate() != null ? obj.getStockTaking1().getEndDate() : new Date(),obj.getStockTaking2().getEndDate() != null ? obj.getStockTaking2().getEndDate() : new Date(),obj.getStockTaking1().getId()};
        List<MovementReportBetweenWarehouseTakings> result = getJdbcTemplate().query(sql, param, new MovementReportBetweenWarehouseTakingsMapper());

        return result;
    }

    @Override
    public int count(String where, MovementReportBetweenWarehouseTakings movementReportBetweenWarehouseTakings) {

        String sql = "SELECT \n"
                + "	COUNT(sti.stock_id) AS stistock_id\n"
                + "FROM\n"
                + "	inventory.stocktakingitem sti\n"
                + "    INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "    INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN inventory.stocktakingitem sti2 ON(sti2.stocktaking_id =  ? AND sti2.stock_id = sti.stock_id)\n"
                + "    LEFT JOIN inventory.stocktaking st2 ON(st2.id = ? AND st2.deleted = FALSE)\n"
                + "    INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)\n"
                + "WHERE\n"
                + "sti.stocktaking_id = ? AND sti.deleted = FALSE  " + where;

        Object[] param = new Object[]{movementReportBetweenWarehouseTakings.getStockTaking2().getId(), movementReportBetweenWarehouseTakings.getStockTaking2().getId(), movementReportBetweenWarehouseTakings.getStockTaking1().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<StockTaking> listOfTaking(StockTaking stockTaking) {
        String sql = "SELECT \n"
                + "ist.id as istid,\n"
                + "ist.name as istname,\n"
                + "ist.warehouse_id as istwarehouse_id,\n"
                + "iw.name as iwname,\n"
                + "ist.begindate as istbegindate,\n"
                + "ist.enddate as istenddate,\n"
                + "ist.status_id as iststatus_id,\n"
                + "sttd.name as sttdname,\n"
                + "ist.enddate as istenddate,\n"
                + "ist.description as istdescription\n"
                + "\n"
                + "FROM inventory.stocktaking ist \n"
                + "LEFT JOIN inventory.warehouse iw ON (ist.warehouse_id=iw.id and iw.deleted=false)\n"
                + "INNER JOIN system.status_dict sttd ON (sttd.status_id = ist.status_id AND sttd.language_id = ?)\n"
                + "WHERE ist.branch_id = ? AND ist.warehouse_id = ? AND ist.deleted = FALSE AND ist.begindate > ? AND ist.status_id = 16\n"
                + "ORDER BY ist.begindate DESC";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), stockTaking.getWarehouse().getId(), stockTaking.getBeginDate()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

}
