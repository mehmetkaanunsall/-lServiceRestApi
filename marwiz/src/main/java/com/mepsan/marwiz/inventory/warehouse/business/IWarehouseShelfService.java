/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 12:32:06
 */
package com.mepsan.marwiz.inventory.warehouse.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseShelf;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IWarehouseShelfService extends ICrudService<WarehouseShelf> {

    public List<WarehouseShelf> findAll(Warehouse warehouse);

    public List<WarehouseShelf> selectShelfWithoutCon(Warehouse warehouse, Stock stock);

    public int delete(WarehouseShelf warehouseShelf);
}
