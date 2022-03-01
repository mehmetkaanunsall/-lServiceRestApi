/**
 * This interface ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   06.02.2019 08:35:05
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ITankDao extends ICrud<Warehouse> {

    public List<Warehouse> findAll();

    public List<Warehouse> findTankList();

    public List<WarehouseItem> selectListWareHouseItem(Warehouse warehouse);

    public int createWareHouseItem(Stock stock, Warehouse warehouse);

    public int updateWareHouseItem(WarehouseItem warehouseItem, Warehouse warehouse);

    public int delete(Warehouse warehouse);

    public int testBeforeDelete(Warehouse warehouse);
}
