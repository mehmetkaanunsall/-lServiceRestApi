/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 05.02.2019 18:20:19
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TankMovementTabDao extends JdbcDaoSupport implements ITankMovementTabDao {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

    @Override
    public List<TankMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, Warehouse warehouse) {
        String where2 = "";
        if (opType == 1) {//in
            where = " AND wm.is_direction = TRUE \n";
            where2 = " AND wm2.is_direction = TRUE \n";
        } else if (opType == 2) {//out
            where = " AND wm.is_direction = FALSE \n";
            where2 = " AND wm2.is_direction = FALSE \n";
        }

        String sql = "SELECT\n"
                  + "wm.id as wmid \n"
                  + ",wr.processdate as movedate \n"
                  + ",wm.is_direction as wmis_direction \n"
                  + ",wm.quantity as wmquantity \n"
                  + ",wm.stock_id AS wmstock_id\n"
                  + ",stck.name AS  stckname\n"
                  + ",stck.code AS stckcode\n"
                  + ",stck.barcode AS  stckbarcode\n"
                  + ",wm.warehouse_id AS wmwarehouse_id\n"
                  + ",wh.name AS whname\n"
                  + ",wh.code AS whcode\n"
                  + ",wy.id AS waybillid\n"
                  + ",wm.warehousereceipt_id AS wmwarehousereceipt_id \n"
                  + ",wr.receiptnumber\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "      shs.price\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN ? AND ? THEN \n"
                  + "          ((invi2.totalprice/invi2.quantity)* COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       ELSE\n"
                  + "          ((invii.totalprice/invii.quantity)* COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS unitpricewithouttaxrate\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "      shs.price\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN ? AND ? THEN \n"
                  + "    	((invi2.totalmoney/invi2.quantity) * COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       ELSE\n"
                  + "    	((invii.totalmoney/invii.quantity) * COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS unitpricewithtaxrate\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "     THEN \n"
                  + "    	 shs.totalmoney\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "     THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN ? AND ? THEN \n"
                  + "		(invi2.totalprice* COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1))\n"
                  + "       ELSE\n"
                  + "		(invii.totalprice* COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1))\n"
                  + "       END\n"
                  + "	ELSE 0 END)   AS totalprice\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "       shs.totalmoney\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN ? AND ? THEN \n"
                  + "           (invi2.totalmoney * COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1) )\n"
                  + "       ELSE\n"
                  + "           (invii.totalmoney * COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1) )\n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS totalmoney\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "       0\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN ? AND ? THEN \n"
                  + "           invi2.taxrate\n"
                  + "       ELSE\n"
                  + "           invii.taxrate\n"
                  + "       END\n"
                  + "    ELSE 0 END) AS taxrate\n"
                  + ",wm.c_id AS wmc_id\n"
                  + ",usd.name AS usdname\n"
                  + ",usd.surname AS usdsurname \n"
                  + ",stck.unit_id AS stckunit_id\n"
                  + ",gunt.name AS guntname \n"
                  + ",gunt.sortname AS guntsortname ,(CASE WHEN shs.id IS NOT NULL THEN 1 WHEN invi.id IS NOT NULL  THEN 2  ELSE 0 END) AS type  \n"
                  + ",gunt.unitrounding as guntunitrounding \n"
                  + ",(\n"
                  + "     SELECT \n"
                  + "         COALESCE(SUM (subtable.quantity), 0) \n"
                  + "     FROM ( \n"
                  + "         SELECT \n"
                  + "                 CASE WHEN wm2.is_direction = false THEN  -wm2.quantity ELSE wm2.quantity END as quantity \n"
                  + "         FROM  inventory.warehousemovement AS wm2 \n"
                  + "         INNER JOIN inventory.warehouse wh2 ON(wh2.id = wm2.warehouse_id AND wh2.deleted=FALSE)\n"
                  + "         INNER JOIN inventory.warehousereceipt wr2 ON(wr2.id = wm2.warehousereceipt_id AND wr2.deleted = FALSE)\n"
                  + "         WHERE  \n"
                  + "         wm2.deleted = FALSE\n"
                  + "         AND wh2.id = ?\n"
                  + "         AND wm2.stock_id = wm.stock_id\n"
                  + "        AND wr2.processdate <= ? \n" + " " + where2 + " \n"
                  + "  \n"
                  + "         ORDER BY  wr2.processdate DESC , wm2.id DESC\n"
                  + "       ) subtable \n"
                  + "     )+\n"
                  + "	SUM(CASE WHEN wm.is_direction = false THEN  -wm.quantity ELSE wm.quantity END) \n"
                  + "        OVER(PARTITION BY wm.stock_id ORDER BY wr.processdate  ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS lastquantity \n"
                  + "FROM  inventory.warehousemovement wm \n"
                  + "INNER JOIN inventory.warehouse wh ON (wh.id = wm.warehouse_id)\n"
                  + "INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "----Fatura\n"
                  + "LEFT JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.warehousereceipt_id=wm.warehousereceipt_id AND wwhrc.deleted=FALSE)\n"
                  + "LEFT JOIN finance.waybill wy ON(wwhrc.waybill_id=wy.id AND wy.deleted = FALSE)\n"
                  + "LEFT JOIN finance.waybill_invoice_con wyic ON(wyic.waybill_id = wy.id AND wyic.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoice invi ON(invi.id = wyic.invoice_id AND invi.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = invi.id AND invii.stock_id = wm.stock_id   AND invii.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invi2 ON(invi2.differentinvoiceitem_id = invii.id AND invi2.deleted=FALSE)\n"
                  + "---Shift Sale\n"
                  + "LEFT JOIN automation.shiftsale shs ON(shs.warehousereceipt_id = wr.id AND shs.deleted = FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(wm.stock_id=stck.id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE) \n"
                  + "INNER JOIN general.userdata usd ON(usd.id = wm.c_id)\n"
                  + "WHERE wm.deleted = FALSE \n"
                  + "AND wm.warehouse_id =  ? AND  wm.stock_id =  ?\n"
                  + "AND wr.processdate BETWEEN ? AND ?\n"
                  + where
                  + " ORDER BY wr.processdate DESC LIMIT " + pageSize + " offset " + first;

        Object[] param = new Object[]{begin, end, begin, end, begin, end, begin, end, begin, end, warehouse.getId(), begin, warehouse.getId(), warehouse.getStock().getId(), begin, end};
        return getJdbcTemplate().query(sql, param, new TankMovementTabMapper());

    }

    @Override
    public int count(String where, int opType, Date begin, Date end, Warehouse warehouse) {

        if (opType == 1) {//in
            where = " AND wm.is_direction = TRUE \n";
        } else if (opType == 2) {//out
            where = " AND wm.is_direction = FALSE \n";
        }

        String sql = "SELECT\n"
                  + "  COUNT(wm.id) AS stckid\n"
                  + "  FROM  inventory.warehousemovement wm \n"
                  + "INNER JOIN inventory.warehouse wh ON (wh.id = wm.warehouse_id)\n"
                  + "INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "----Fatura\n"
                  + "LEFT JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.warehousereceipt_id=wm.warehousereceipt_id AND wwhrc.deleted=FALSE)\n"
                  + "LEFT JOIN finance.waybill wy ON(wy.id = wwhrc.waybill_id AND wy.deleted = FALSE)\n"
                  + "LEFT JOIN finance.waybill_invoice_con wyic ON(wyic.waybill_id = wy.id AND wyic.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoice invi ON(invi.id = wyic.invoice_id AND invi.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = invi.id AND invii.stock_id = wm.stock_id   AND invii.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invi2 ON(invi2.differentinvoiceitem_id = invii.id AND invi2.deleted=FALSE)\n"
                  + "\n"
                  + "---Shift Sale\n"
                  + "LEFT JOIN automation.shiftsale shs ON(shs.warehousereceipt_id = wr.id AND shs.deleted = FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(wm.stock_id=stck.id)\n"
                  + "INNER JOIN general.userdata usd ON(usd.id = wm.c_id)\n"
                  + "  WHERE wm.deleted = FALSE\n"
                  + "  AND wm.warehouse_id =?   AND  wm.stock_id =  ? \n"
                  + "  AND wr.processdate BETWEEN " + " ' "
                  + simpleDateFormat.format(begin) + " ' "
                  + " AND "
                  + " ' "
                  + simpleDateFormat.format(end)
                  + " ' "
                  + where;

        Object[] param = new Object[]{warehouse.getId(), warehouse.getStock().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where, int opType, Date begin, Date end, Warehouse warehouse) {

        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String where2 = "";
        if (opType == 1) {//in
            where = " AND wm.is_direction = TRUE \n";
            where2 = " AND wm2.is_direction = TRUE \n";
        } else if (opType == 2) {//out
            where = " AND wm.is_direction = FALSE \n";
            where2 = " AND wm2.is_direction = FALSE \n";
        }

        String sql = "SELECT\n"
                  + "wm.id as wmid \n"
                  + ",wr.processdate as movedate \n"
                  + ",wm.is_direction as wmis_direction \n"
                  + ",wm.quantity as wmquantity \n"
                  + ",wm.stock_id AS wmstock_id\n"
                  + ",stck.name AS  stckname\n"
                  + ",stck.code AS stckcode\n"
                  + ",stck.barcode AS  stckbarcode\n"
                  + ",wm.warehouse_id AS wmwarehouse_id\n"
                  + ",wh.name AS whname\n"
                  + ",wh.code AS whcode\n"
                  + ",wy.id AS waybillid\n"
                  + ",wm.warehousereceipt_id AS wmwarehousereceipt_id \n"
                  + ",wr.receiptnumber AS receiptnumber\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "      shs.price\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN '" + format.format(begin) + "' AND '" + format.format(end) + "' THEN \n"
                  + "          ((invi2.totalprice/invi2.quantity)* COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       ELSE\n"
                  + "          ((invii.totalprice/invii.quantity)* COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS unitpricewithouttaxrate\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "      shs.price\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN '" + format.format(begin) + "' AND '" + format.format(end) + "' THEN \n"
                  + "    	((invi2.totalmoney/invi2.quantity) * COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       ELSE\n"
                  + "    	((invii.totalmoney/invii.quantity) * COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1)) \n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS unitpricewithtaxrate\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "     THEN \n"
                  + "    	 shs.totalmoney\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "     THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN '" + format.format(begin) + "' AND '" + format.format(end) + "' THEN \n"
                  + "		(invi2.totalprice* COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1))\n"
                  + "       ELSE\n"
                  + "		(invii.totalprice* COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1))\n"
                  + "       END\n"
                  + "	ELSE 0 END)   AS totalprice\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "       shs.totalmoney\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN '" + format.format(begin) + "' AND '" + format.format(end) + "' THEN \n"
                  + "           (invi2.totalmoney * COALESCE(invi2.exchangerate,1)* COALESCE(invi.exchangerate,1) )\n"
                  + "       ELSE\n"
                  + "           (invii.totalmoney * COALESCE(invii.exchangerate,1)* COALESCE(invi.exchangerate,1) )\n"
                  + "       END\n"
                  + "    ELSE 0 END)   AS totalmoney\n"
                  + ",(CASE \n"
                  + "    WHEN  (shs.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "       0\n"
                  + "    WHEN  (invi.id) IS NOT NULL \n"
                  + "      THEN \n"
                  + "        CASE WHEN invii.is_calcincluded = TRUE AND invi.differentdate BETWEEN '" + format.format(begin) + "' AND '" + format.format(end) + "' THEN \n"
                  + "           invi2.taxrate\n"
                  + "       ELSE\n"
                  + "           invii.taxrate\n"
                  + "       END\n"
                  + "    ELSE 0 END) AS taxrate\n"
                  + ",wm.c_id AS wmc_id\n"
                  + ",usd.name AS usdname\n"
                  + ",usd.surname AS usdsurname \n"
                  + ",stck.unit_id AS stckunit_id\n"
                  + ",gunt.name AS guntname \n"
                  + ",gunt.sortname AS guntsortname "
                  + ",(CASE WHEN shs.id IS NOT NULL THEN 1 WHEN invi.id IS NOT NULL  THEN 2  ELSE 0 END) AS type  \n"
                  + ",gunt.unitrounding as guntunitrounding \n"
                  + ",(\n"
                  + "     SELECT \n"
                  + "         COALESCE(SUM (subtable.quantity), 0) \n"
                  + "     FROM ( \n"
                  + "         SELECT \n"
                  + "                 CASE WHEN wm2.is_direction = false THEN  -wm2.quantity ELSE wm2.quantity END as quantity \n"
                  + "         FROM  inventory.warehousemovement AS wm2 \n"
                  + "         INNER JOIN inventory.warehouse wh2 ON(wh2.id = wm2.warehouse_id AND wh2.deleted=FALSE)\n"
                  + "         INNER JOIN inventory.warehousereceipt wr2 ON(wr2.id = wm2.warehousereceipt_id AND wr2.deleted = FALSE)\n"
                  + "         WHERE  \n"
                  + "         wm2.deleted = FALSE\n"
                  + "         AND wh2.id = " + warehouse.getId() + "\n"
                  + "         AND wm2.stock_id = wm.stock_id\n"
                  + "         AND wr2.processdate <= '" + format.format(begin) + "' \n" + " " + where2 + " \n"
                  + "         ORDER BY wr2.processdate DESC, wm2.id DESC\n"
                  + "       ) subtable \n"
                  + "     )+\n"
                  + "	SUM(CASE WHEN wm.is_direction = false THEN  -wm.quantity ELSE wm.quantity END) \n"
                  + "        OVER(PARTITION BY wm.stock_id ORDER BY wr.processdate  ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS lastquantity \n"
                  + "FROM  inventory.warehousemovement wm \n"
                  + "INNER JOIN inventory.warehouse wh ON (wh.id = wm.warehouse_id)\n"
                  + "INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "----Fatura\n"
                  + "LEFT JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.warehousereceipt_id=wm.warehousereceipt_id AND wwhrc.deleted=FALSE)\n"
                  + "LEFT JOIN finance.waybill wy ON(wy.id = wwhrc.waybill_id AND wy.deleted = FALSE)\n"
                  + "LEFT JOIN finance.waybill_invoice_con wyic ON(wyic.waybill_id = wy.id AND wyic.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoice invi ON(invi.id = wyic.invoice_id AND invi.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = invi.id  AND invii.stock_id = wm.stock_id  AND invii.deleted = FALSE)\n"
                  + "LEFT JOIN finance.invoiceitem invi2 ON(invi2.differentinvoiceitem_id = invii.id AND invi2.deleted=FALSE)\n"
                  + "\n"
                  + "---Shift Sale\n"
                  + "LEFT JOIN automation.shiftsale shs ON(shs.warehousereceipt_id = wr.id AND shs.deleted = FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(wm.stock_id=stck.id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE) \n"
                  + "INNER JOIN general.userdata usd ON(usd.id = wm.c_id)\n"
                  + "WHERE wm.deleted = FALSE \n"
                  + "AND wm.warehouse_id =  " + warehouse.getId() + " AND  wm.stock_id = " + warehouse.getStock().getId() + " \n"
                  + "AND wr.processdate BETWEEN  '" + format.format(begin) + "' AND '" + format.format(end) + "'\n"
                  + where + ""
                  + "ORDER BY wr.processdate DESC ";

        return sql;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
