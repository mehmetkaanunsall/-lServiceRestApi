/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 12:25:43
 */

package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IWarehouseShelfDao extends ICrud<WarehouseShelf> {
    
    public List<WarehouseShelf> findAll(Warehouse wareHouse);
    
    public List<WarehouseShelf> selectShelfWithoutCon(Warehouse warehouse,Stock stock);
    
    public int delete(WarehouseShelf warehouseShelf);

}
