/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   09.02.2018 04:53:50
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.business;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.io.InputStream;
import java.util.List;

public interface ITransferBetweenWarehouseService extends ILazyGridService<WarehouseTransfer> {

    public int save(Warehouse entry, Warehouse exit, List<WarehouseMovement> listOfWarehouseMovement, int type, WarehouseTransfer warehouseTransfer);

    public int delete(Warehouse entry, Warehouse exit, List<WarehouseMovement> listOfWarehouseMovement, int type, WarehouseTransfer warehouseTransfer, WarehouseMovement warehouseMovement);

    public String jsonArrayWarehouseMovements(List<WarehouseMovement> movements);

    public WarehouseTransfer find(WarehouseTransfer warehouseTransfer);

    public List<WarehouseMovement> createSampleList();
    
    public List<WarehouseMovement> processUploadFile(InputStream inputStream);
    
     public String jsonArrayForExcelUpload(List<WarehouseMovement> listItems, int exitWarehouseId, Warehouse entryWarehouse);
     
     

}
