package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.stock.dao.StockMovement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class WarehouseMovementTabDao extends JdbcDaoSupport implements IWarehouseMovementTabDao {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

    @Override
    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, List<Stock> stock, Warehouse warehouse) {
        String stocks = "";
        String where2 = "";
        if (opType == 1) {//in
            where = " AND wm.is_direction = TRUE \n";
            where2 = " AND wm2.is_direction = TRUE \n";
        } else if (opType == 2) {//out
            where = " AND wm.is_direction = FALSE \n";
            where2 = " AND wm2.is_direction = FALSE \n";
        }

        for (int i = 0; i < stock.size(); i++) {
            stocks += "" + stock.get(i).getId();
            stocks += ",";
            if (stock.get(i).getId() == 0) {
                stocks = "";
                break;
            }
        }
        if (stocks.length() > 0) {
            stocks = stocks.substring(0, stocks.length() - 1);
        }
        if (!stocks.equals("")) {
            where += "AND wm.stock_id IN ( "
                    + stocks
                    + " ) ";
        }

        String sql = "SELECT		\n"
                + "        stck.name stckname,\n"
                + "        wm.id as wmid\n"
                + "        ,wr.processdate as movedate\n"
                + "        ,wm.is_direction as wmis_direction\n"
                + "        ,wm.quantity as wmquantity\n"
                + "        ,wh.id as whid\n"
                + "        ,wh.name as whname\n"
                + "        ,wm.stock_id AS  wmstock_id \n"
                + "        ,stck.unit_id AS stckunit_id\n"
                + "         ,gunt.name AS guntname \n"
                + "         ,gunt.sortname AS guntsortname "
                + "         ,gunt.unitrounding as guntunitrounding \n"
                + "        ,(\n"
                + "        SELECT \n"
                + "            COALESCE(SUM (SS.price), 0) \n"
                + "        FROM ( \n"
                + "            SELECT \n"
                + "                    CASE WHEN wm2.is_direction = false THEN  -wm2.quantity ELSE wm2.quantity END as price \n"
                + "            FROM  inventory.warehousemovement AS wm2 \n"
                + "            INNER JOIN inventory.warehousereceipt wr2 ON(wr2.id = wm2.warehousereceipt_id )\n  "
                + "            INNER JOIN inventory.warehouse wh2 ON(wh2.id = wm2.warehouse_id)\n"
                + "            WHERE  \n"
                + "            wm2.deleted = FALSE\n"
                + "            AND wh2.id = ?\n"
                + "            AND wm2.stock_id = wm.stock_id\n"
                + "            AND wr2.processdate < ? \n"
                + where2
                + "            ORDER BY wr2.processdate DESC, wm2.id DESC\n"
                + "          ) SS \n"
                + "        )+"
                + "        SUM(CASE WHEN wm.is_direction = false THEN  -wm.quantity ELSE wm.quantity END) \n"
                + "        OVER(PARTITION BY wm.stock_id ORDER BY wr.processdate  ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS lastquantity\n"
                + "  FROM  inventory.warehousemovement wm\n"
                + "  INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted=FALSE)\n  "
                + "  INNER JOIN inventory.warehouse wh ON (wh.id = wm.warehouse_id AND wh.deleted=FALSE)\n"
                + "  INNER JOIN inventory.stock stck ON(wm.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "  LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE) \n"
                + "  WHERE wm.deleted = FALSE\n"
                + "  AND wh.id=? \n"
                + "  AND wr.processdate BETWEEN ? AND ? \n"
                + where
                + " ORDER BY wr.processdate DESC LIMIT " + pageSize + " offset " + first;

        Object[] param = new Object[]{warehouse.getId(), begin, warehouse.getId(), begin, end};

        return getJdbcTemplate().query(sql, param, new WarehouseMovementTabMapper());

    }

    @Override
    public int count(String where, List<Stock> stock, int opType, Date begin, Date end, Warehouse warehouse) {

        String stocks = "";

        if (opType == 1) {//in
            where = " AND wm.is_direction = TRUE \n";
        } else if (opType == 2) {//out
            where = " AND wm.is_direction = FALSE \n";
        }

        for (int i = 0; i < stock.size(); i++) {
            stocks += "" + stock.get(i).getId();
            stocks += ",";
            if (stock.get(i).getId() == 0) {
                stocks = "";
                break;
            }
        }
        if (stocks.length() > 0) {
            stocks = stocks.substring(0, stocks.length() - 1);
        }

        if (!stocks.equals("")) {
            where += "AND wm.stock_id IN ( "
                    + stocks
                    + " ) ";
        }

        String sql = "SELECT\n"
                + "  COUNT(wm.id) AS stckid\n"
                + "  FROM  inventory.warehousemovement wm\n"
                + "  INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted=FALSE)\n  "
                + "  INNER JOIN inventory.warehouse wh ON (wh.id = wm.warehouse_id AND wh.deleted=FALSE)\n"
                + "  INNER JOIN inventory.stock stck ON(wm.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "  WHERE wm.deleted = FALSE\n"
                + "  AND wh.id=? \n"
                + "  AND wr.processdate BETWEEN " + " ' "
                + simpleDateFormat.format(begin) + " ' "
                + " AND "
                + " ' "
                + simpleDateFormat.format(end)
                + " ' "
                + where;

        Object[] param = new Object[]{warehouse.getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

}
