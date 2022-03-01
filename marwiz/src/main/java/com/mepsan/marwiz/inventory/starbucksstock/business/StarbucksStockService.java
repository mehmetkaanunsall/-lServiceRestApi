package com.mepsan.marwiz.inventory.starbucksstock.business;

import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.inventory.starbucksstock.dao.IStarbucksStockDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class StarbucksStockService implements IStarbucksStockService {

    @Autowired
    private IStarbucksStockDao starbucksStockDao;

    public void setStarbucksStockDao(IStarbucksStockDao starbucksStockDao) {
        this.starbucksStockDao = starbucksStockDao;
    }

    @Override
    public int create(StarbucksStock obj) {
        return starbucksStockDao.create(obj);
    }

    @Override
    public int update(StarbucksStock obj) {
        return starbucksStockDao.update(obj);
    }

    @Override
    public List<StarbucksStock> findAll() {
        return starbucksStockDao.findAll();
    }

    @Override
    public int delete(StarbucksStock obj) {
        return starbucksStockDao.delete(obj);
    }

    @Override
    public StarbucksStock findAccordingToCode(StarbucksStock stock) {
        return starbucksStockDao.findAccordingToCode(stock);
    }

    @Override
    public int deleteForOtherBranch(StarbucksStock stock) {
        return starbucksStockDao.deleteForOtherBranch(stock);
    }

    @Override
    public int updateAvailableStarbucksStock(int oldId, int newId) {
        return starbucksStockDao.updateAvailableStarbucksStock(oldId, newId);
    }

}
