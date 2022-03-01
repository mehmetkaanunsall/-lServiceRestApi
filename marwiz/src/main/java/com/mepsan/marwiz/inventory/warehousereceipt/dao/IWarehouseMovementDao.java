/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 03:38:15
 */
package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;

public interface IWarehouseMovementDao extends ICrud<WarehouseReceipt> {

    public List<WarehouseMovement> findAll(WarehouseReceipt warehouseReceipt);

    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt);

    public int delete(WarehouseMovement warehouseMovement);

    public int createWasteInfo(WarehouseReceipt obj, WarehouseMovement warehouseMovement);

    public int updateWasteInfo(WarehouseMovement warehouseMovement);

    public int deleteWasteInfo(WarehouseMovement warehouseMovement);

    public List<WarehouseMovement> findAllAccordingToWarehouse(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer);

    public int count(String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer);
}
