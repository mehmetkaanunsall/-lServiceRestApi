/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 03:45:28
 */
package com.mepsan.marwiz.inventory.warehousereceipt.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.inventory.warehousereceipt.dao.IWarehouseMovementDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseMovementService implements IWarehouseMovementService {

    @Autowired
    private IWarehouseMovementDao warehouseMovementDao;

    public void setWarehouseMovementDao(IWarehouseMovementDao warehouseMovementDao) {
        this.warehouseMovementDao = warehouseMovementDao;
    }

    @Override
    public List<WarehouseMovement> findAll(WarehouseReceipt warehouseReceipt) {
        return warehouseMovementDao.findAll(warehouseReceipt);
    }

    @Override
    public int create(WarehouseMovement obj) {

        List<WarehouseMovement> movements = new ArrayList<>();
        movements.add(obj);
        obj.getWarehouseReceipt().setJsonMovements(jsonArrayWarehouseMovements(movements));
        return warehouseMovementDao.create(obj.getWarehouseReceipt());
    }

    @Override
    public int update(WarehouseMovement obj) {
        List<WarehouseMovement> movements = new ArrayList<>();
        movements.add(obj);
        obj.getWarehouseReceipt().setJsonMovements(jsonArrayWarehouseMovements(movements));
        return warehouseMovementDao.update(obj.getWarehouseReceipt());
    }

    /**
     * Bu metot gelen depo hareketi listesini json array stringine dönüştürür.
     *
     * @param movements
     * @return
     */
    @Override
    public String jsonArrayWarehouseMovements(List<WarehouseMovement> movements) {
        JsonArray jsonArray = new JsonArray();
        for (WarehouseMovement obj : movements) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("stockcount", 1);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    

    @Override
    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt) {
        return warehouseMovementDao.testBeforeDelete(warehouseReceipt);
    }

    @Override
    public int delete(WarehouseMovement warehouseMovement) {
        return warehouseMovementDao.delete(warehouseMovement);
    }

    @Override
    public int createWasteInfo(WarehouseReceipt obj, WarehouseMovement warehouseMovement) {
        return warehouseMovementDao.createWasteInfo(obj, warehouseMovement);
    }

    @Override
    public int updateWasteInfo(WarehouseMovement warehouseMovement) {
        return warehouseMovementDao.updateWasteInfo(warehouseMovement);
    }

    @Override
    public int deleteWasteInfo( WarehouseMovement warehouseMovement) {
        return warehouseMovementDao.deleteWasteInfo( warehouseMovement);
    }

    @Override
    public int count(String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer) {
        return warehouseMovementDao.count(where, exitWarehouse, entryWarehouse, type, warehouseTransfer);
    }

    @Override
    public List<WarehouseMovement> findAllAccordingToWarehouse(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer) {
        return warehouseMovementDao.findAllAccordingToWarehouse(first, pageSize, sortField, sortOrder, filters, where, exitWarehouse, entryWarehouse, type, warehouseTransfer);
    }
    
    
    
   
    
}
