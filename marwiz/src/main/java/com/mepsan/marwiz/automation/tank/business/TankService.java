/**
 * This class ...
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
import com.mepsan.marwiz.automation.tank.dao.ITankDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class TankService implements ITankService {

    @Autowired
    private ITankDao tankdao;

    public void setTankdao(ITankDao tankdao) {
        this.tankdao = tankdao;
    }

    @Override
    public List<WarehouseItem> selectListWareHouseItem(Warehouse warehouse) {
        return tankdao.selectListWareHouseItem(warehouse);
    }

    @Override
    public List<Warehouse> findAll() {
        return tankdao.findAll();
    }

    @Override
    public int create(Warehouse obj) {
        return tankdao.create(obj);
    }

    @Override
    public int update(Warehouse obj) {
        return tankdao.update(obj);
    }

    @Override
    public int delete(Warehouse warehouse) {
        return tankdao.delete(warehouse);
    }

    @Override
    public int testBeforeDelete(Warehouse warehouse) {
        return tankdao.testBeforeDelete(warehouse);
    }

    @Override
    public int createWareHouseItem(Stock stock, Warehouse warehouse) {
        return tankdao.createWareHouseItem(stock, warehouse);
    }

    @Override
    public int updateWareHouseItem(WarehouseItem warehouseItem, Warehouse warehouse) {
        return tankdao.updateWareHouseItem(warehouseItem, warehouse);
    }

    @Override
    public List<Warehouse> findTankList() {
        return tankdao.findTankList();
    }

}
