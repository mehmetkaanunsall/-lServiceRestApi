/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   09.02.2018 04:52:13
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.dao;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.ILazyGrid;

public interface ITransferBetweenWarehouseDao extends ILazyGrid<WarehouseTransfer> {

    public int save(Warehouse entry, Warehouse exit, String movements, int type, WarehouseTransfer warehouseTransfer);

    public int delete(Warehouse entry, Warehouse exit, String movements, int type, WarehouseTransfer warehouseTransfer,WarehouseMovement warehouseMovement);
    
    public String jsonArrayForExcelUpload(String listItems, int exitWarehouseId, Warehouse entryWarehouse);

}
