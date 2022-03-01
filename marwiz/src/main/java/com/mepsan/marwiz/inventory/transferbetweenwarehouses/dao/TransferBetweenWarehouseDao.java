/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   09.02.2018 04:45:15
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TransferBetweenWarehouseDao extends JdbcDaoSupport implements ITransferBetweenWarehouseDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int save(Warehouse entry, Warehouse exit, String movements, int type, WarehouseTransfer warehouseTransfer) {
                
        String sql = " SELECT r_transfer_id FROM inventory.process_warehousetransfer(?, ?, ?, ?, ?, ?, ?,? );";
        Object[] param = {type, warehouseTransfer.getId(), entry.getId(), exit.getId(), warehouseTransfer.getReceiptNumber(), sessionBean.getUser().getId(), movements == null ? null : movements.equals("") ? null : movements, 0};

        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WarehouseTransfer> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {

        if (sortField == null) {
            sortField = "whr.processdate";
            sortOrder = "desc";
        }

        String sql = "SELECT \n"
                  + "	 wht.id as whtid,\n"
                  + "    wht.receiptnumber AS whtreceiptnumber,\n"
                  + "    wht.warehousereceipt_id AS whtwarehousereceipt_id,\n"
                  + "    wht.transferwarehousereceipt_id AS whttransferwarehousereceipt_id,\n"
                  + "    wht.processdate AS whtprocessdate,\n"
                  + "    whr.warehouse_id as whrwarehouse_id,\n"
                  + "	 wh.name as whname,\n"
                  + "    wh.branch_id AS whbranch_id,\n"
                  + "    whr2.warehouse_id AS whr2warehouse_id,\n"
                  + "    wh2.name AS wh2name,	\n"
                  + "    wh2.branch_id AS wh2branch_id\n"
                  + "FROM inventory.warehousetransfer wht\n"
                  + "INNER JOIN inventory.warehousereceipt whr ON(whr.id = wht.warehousereceipt_id AND whr.deleted=FALSE)\n"
                  + "INNER JOIN inventory.warehouse wh ON (wh.id = whr.warehouse_id and wh.deleted=false)\n"
                  + "INNER JOIN inventory.warehousereceipt whr2 ON(whr2.id = wht.transferwarehousereceipt_id AND whr2.deleted=FALSE)\n"
                  + "INNER JOIN inventory.warehouse wh2 ON (wh2.id = whr2.warehouse_id and wh2.deleted=false)\n"
                  + "WHERE wht.deleted=false AND (wh.branch_id = ? OR wh2.branch_id =?) " + where + "\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;
        Object[] param = {sessionBean.getUser().getLastBranch().getId(),sessionBean.getUser().getLastBranch().getId()};
        List<WarehouseTransfer> result = getJdbcTemplate().query(sql, param, new TransferBetweenWarehouseMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                  + "	 COUNT(wht.id) as whtid\n"
                  + "FROM inventory.warehousetransfer wht\n"
                  + "INNER JOIN inventory.warehousereceipt whr ON(whr.id = wht.warehousereceipt_id AND whr.deleted=FALSE)\n"
                  + "INNER JOIN inventory.warehouse wh ON (wh.id = whr.warehouse_id and wh.deleted=false)\n"
                  + "INNER JOIN inventory.warehousereceipt whr2 ON(whr2.id = wht.transferwarehousereceipt_id AND whr2.deleted=FALSE)\n"
                  + "INNER JOIN inventory.warehouse wh2 ON (wh2.id = whr2.warehouse_id and wh2.deleted=false)\n"
                  + "WHERE wht.deleted=false AND (wh.branch_id = ? OR wh2.branch_id =?) " + where + "\n";

        Object[] param = {sessionBean.getUser().getLastBranch().getId(),sessionBean.getUser().getLastBranch().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int delete(Warehouse entry, Warehouse exit, String movements, int type, WarehouseTransfer warehouseTransfer, WarehouseMovement warehouseMovement) {
        String sql = " SELECT r_transfer_id FROM inventory.process_warehousetransfer(?, ?, ?, ?, ?, ?, ?, ? );";
        Object[] param = {type, warehouseTransfer.getId(), entry.getId(), exit.getId(), warehouseTransfer.getReceiptNumber(), sessionBean.getUser().getId(), movements == null ? null : movements.equals("") ? null : movements, warehouseMovement.getStock().getId()};
        
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

      @Override
    public String jsonArrayForExcelUpload(String listItems, int exitWarehouseId, Warehouse entryWarehouse) {
                String sql = "SELECT r_result FROM inventory.excel_transferbetweenwarehousesitem(?, ?, ?, ?)";
        Object[] param = new Object[]{listItems, exitWarehouseId, entryWarehouse.getBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
         try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
            
            
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }



    

}
