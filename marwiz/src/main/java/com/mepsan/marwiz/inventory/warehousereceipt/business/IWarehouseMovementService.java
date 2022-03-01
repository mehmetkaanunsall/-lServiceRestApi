/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 03:45:03
 */
package com.mepsan.marwiz.inventory.warehousereceipt.business;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;
import java.util.Map;

public interface IWarehouseMovementService extends ICrudService<WarehouseMovement> {

    public List<WarehouseMovement> findAll(WarehouseReceipt warehouseReceipt);

    public String jsonArrayWarehouseMovements(List<WarehouseMovement> movements);


    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt);

    public int delete(WarehouseMovement warehouseMovement);

    public int createWasteInfo(WarehouseReceipt obj, WarehouseMovement warehouseMovement);

    public int updateWasteInfo(WarehouseMovement warehouseMovement);

    public int deleteWasteInfo(WarehouseMovement warehouseMovement);

    public int count(String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer);

    public List<WarehouseMovement> findAllAccordingToWarehouse(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer);

}
