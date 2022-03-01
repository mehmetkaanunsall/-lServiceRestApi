/**
 * This interface ...
 *
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   04.02.2019 10:58:39
 */
package com.mepsan.marwiz.automation.tank.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface ITankService extends ICrudService<Warehouse> {

    public List<Warehouse> findAll();
    
        public List<Warehouse> findTankList();


    public List<WarehouseItem> selectListWareHouseItem(Warehouse warehouse);

    public int createWareHouseItem(Stock stock, Warehouse warehouse);

    public int updateWareHouseItem(WarehouseItem warehouseItem, Warehouse warehouse);

    public int delete(Warehouse warehouse);

    public int testBeforeDelete(Warehouse warehouse);

}
