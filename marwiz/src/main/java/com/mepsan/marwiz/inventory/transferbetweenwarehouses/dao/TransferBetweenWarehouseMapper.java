/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.05.2020 11:06:26
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TransferBetweenWarehouseMapper implements RowMapper<WarehouseTransfer> {

    @Override
    public WarehouseTransfer mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseTransfer warehouseTransfer = new WarehouseTransfer();
        warehouseTransfer.setId(rs.getInt("whtid"));
        warehouseTransfer.setReceiptNumber(rs.getString("whtreceiptnumber"));
        warehouseTransfer.getWarehouseReceipt().setId(rs.getInt("whtwarehousereceipt_id"));
        warehouseTransfer.getTransferWarehouseReceipt().setId(rs.getInt("whttransferwarehousereceipt_id"));
        warehouseTransfer.setProcessDate(rs.getTimestamp("whtprocessdate"));
        warehouseTransfer.getWarehouseReceipt().getWarehouse().setId(rs.getInt("whrwarehouse_id"));
        warehouseTransfer.getWarehouseReceipt().getWarehouse().setName(rs.getString("whname"));
        warehouseTransfer.getWarehouseReceipt().getWarehouse().getBranch().setId(rs.getInt("whbranch_id"));
        warehouseTransfer.getTransferWarehouseReceipt().getWarehouse().setId(rs.getInt("whr2warehouse_id"));
        warehouseTransfer.getTransferWarehouseReceipt().getWarehouse().setName(rs.getString("wh2name"));
        warehouseTransfer.getTransferWarehouseReceipt().getWarehouse().getBranch().setId(rs.getInt("wh2branch_id"));

        return warehouseTransfer;
    }

}
