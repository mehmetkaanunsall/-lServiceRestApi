/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.02.2018 11:47:06
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockMovementDao extends JdbcDaoSupport implements IStockMovementDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch) {
        String where2 = "";

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        //Sıralama için
        if (sortField == null) {
            sortField = " tt.movedate ";
            sortOrder = " DESC ";
        } else if (sortField.equals("warehouseReceipt.receiptNumber")) {
            sortField = " (CASE WHEN tt.wrtype_id  = 61 AND (tt.rcid IS NOT NULL AND tt.rcid > 0) THEN tt.receiptno\n"
                    + "                        WHEN tt.wrtype_id  = 61 AND (tt.invid IS NOT NULL AND tt.invid > 0) THEN COALESCE(tt.invdocumentserial,''' || v_documentnumber || ''') || COALESCE(tt.invdocumentnumber,''' || v_documentnumber || ''') \n"
                    + "                        WHEN tt.wrtype_id  = 61 AND (tt.wbid IS NOT NULL AND tt.wbid > 0)  THEN COALESCE(tt.wbdocumentserial,''' || v_documentnumber || ''') || COALESCE(tt.wbdocumentnumber,''' || v_documentnumber || ''') \n"
                    + "                        ELSE tt.wrreceiptnumber END) ";

        } else if (sortField.equals("processType")) {
            sortField = " (CASE WHEN tt.wrtype_id = 6  THEN 1 WHEN tt.wrtype_id =  7 THEN  2 WHEN tt.wrtype_id  = 8  THEN 3\n"
                    + "                        WHEN tt.wrtype_id = 9  THEN 4 WHEN tt.wrtype_id = 60 THEN  5 \n"
                    + "                        WHEN tt.wrtype_id  = 61 AND (tt.rcid IS NOT NULL AND tt.rcid > 0) THEN 6 \n"
                    + "                        WHEN tt.wrtype_id  = 61 AND (tt.invid IS NOT NULL AND tt.invid > 0)  THEN 7\n"
                    + "                        WHEN tt.wrtype_id  = 61 AND (tt.wbid IS NOT NULL AND tt.wbid > 0)  THEN 8\n"
                    + "                        WHEN tt.wrtype_id  = 76 THEN 9\n"
                    + "                        ELSE 0 END) ";

        } else if (sortField.equals("unitPrice")) {
            sortField = " ( CASE WHEN (tt.wrtype_id = 61 OR tt.wrtype_id = 60) AND (tt.rcid IS NOT NULL AND tt.rcid > 0)\n"
                    + "                    THEN\n"
                    + "                      ( SELECT \n"
                    + "                           sl.price\n"
                    + "                 		FROM tempTableSale sl \n"
                    + "                 		WHERE  sl.receipt_id = tt.rcid  AND sl.stock_id = tt.stock_id LIMIT 1)\n"
                    + "                     WHEN (tt.wrtype_id = 61 OR tt.wrtype_id = 60 ) AND (tt.invid IS NOT NULL AND tt.invid > 0)\n"
                    + "                       THEN  \n"
                    + "                           ( SELECT \n"
                    + "                           (CASE WHEN  tt.stck_unitid <> invii.unit_id THEN invii.stucquantity ELSE 1 END) *  --alternatif birim ise\n"
                    + "                            invii.price *\n"
                    + "                             (CASE WHEN  invii.differentinvoice = TRUE  THEN tt.invexchangerate ELSE 1 END)\n"
                    + "                          FROM tempTableInvoice invii\n"
                    + "                          WHERE invii.invoice_id = tt.invid AND invii.stock_id = tt.stock_id  LIMIT 1)\n"
                    + "                     WHEN tt.wrtype_id = 8 \n"
                    + "                       THEN\n"
                    + "                         ( SELECT \n"
                    + "                           wti.price\n"
                    + "                           FROM tempTablewarehousetransferiteminfo wti\n"
                    + "                           WHERE wti.warehousemovement_id = tt.wmid LIMIT 1)\n"
                    + "                      ELSE \n"
                    + "                       0::NUMERIC(18,4)\n"
                    + "                      END ) ";

        };

        String branchList = "";
        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());

        }

        String sql = " Select * from inventory.list_stockmovement (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] param = new Object[]{opType, where, warehouse.getId(), branchList, stock.getId(), begin, end, sortField, sortOrder, String.valueOf(pageSize), String.valueOf(first), 1};
       
        try {
            return getJdbcTemplate().query(sql, param, new StockMovementMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }

    }

    @Override
    public StockMovement count(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, List<Branch> listOfBranch) {

        String branchList = "";
        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());

        }

        String sql = " Select * from inventory.list_stockmovement_count (?, ?, ?, ?, ?, ?, ?)";

        Object[] param = new Object[]{where, stock.getId(), opType, begin, end, warehouse.getId(), branchList};

        List<StockMovement> result = getJdbcTemplate().query(sql, param, new StockMovementMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new StockMovement();
        }
    }

    @Override
    public List<StockMovement> listOfWarehouseAvailability(Stock stock, Warehouse warehouse, List<Branch> listOfBranch) {

        String where = "";
        if (warehouse.getId() > 0) {
            where = " AND wh.id = " + warehouse.getId();
        }

        String branchList = "";
        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            where = where + " AND wh.branch_id IN(" + branchList + ") ";
        }

        String sql = "SELECT \n"
                + "      wh.id as whid\n"
                + "     ,wh.name as whname\n"
                + "      ,whi.quantity as whiquantity\n"
                + "FROM  inventory.warehouse wh \n"
                + "INNER JOIN inventory.warehouseitem whi ON (whi.warehouse_id = wh.id AND whi.deleted=false)\n"
                + "WHERE wh.deleted = FALSE\n"
                + "AND whi.stock_id =  ? "
                + where;

        Object[] param = new Object[]{stock.getId()};
        return getJdbcTemplate().query(sql, param, new StockMovementMapper());
    }

    @Override
    public String exportData(String where, Stock stock, int opType, Date begin, Date end, Warehouse warehouse, String branchList) {

        String sortField = "";
        String sortOrder = "";
        String value = "";
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        sortField = " tt.movedate ";
        sortOrder = " DESC ";

        String sql = " Select "
                + "  wmid as wmid,\n"
                + "  movedate as movedate,\n"
                + "  wmis_direction as wmis_direction,\n"
                + "  wmquantity as wmquantity,\n"
                + "  whid as whid,\n"
                + "  whname as whname,\n"
                + "  wrreceiptnumber  AS wrreceiptnumber,\n"
                + "  invid AS invid ,\n"
                + "  slid AS slid, \n"
                + "  stktid AS stktid, \n"
                + "  processtype AS processtype, \n"
                + "  price AS price,      \n"
                + "  lastquantity AS lastquantity,\n"
                + "  brname AS brname,\n"
                + "  brid AS brid\n"
                + " from inventory.list_stockmovement ("
                + opType
                + ",'"
                + where
                + "' ,"
                + warehouse.getId()
                + ", '"
                + branchList
                + "' ,"
                + stock.getId()
                + " , '"
                + format.format(begin)
                + "' , '"
                + format.format(end)
                + "' , '"
                + sortField
                + "' , '"
                + sortOrder
                + "' , '"
                + value
                + "' , '"
                + value
                + "' ,2)";

        return sql;

        
        
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }
}
