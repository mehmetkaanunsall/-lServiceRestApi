/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 02:35:05
 */
package com.mepsan.marwiz.inventory.warehouse.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWarehouseDao extends ICrud<Warehouse> {

    public List<Warehouse> findAll();

    public List<Warehouse> selectListWarehouse(String where);

    public List<Warehouse> selectListWarehouse(Stock stock, String where, Branch branch);

    public List<Warehouse> selectListAllWarehouse(String where);

    public int delete(Warehouse warehouse);

    public int testBeforeDelete(Warehouse warehouse);

    public List<Warehouse> selectListWarehouseForBranch(Branch branch, String where);
}
