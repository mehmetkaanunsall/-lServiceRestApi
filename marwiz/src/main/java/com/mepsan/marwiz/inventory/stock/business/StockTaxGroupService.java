/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 10:25:20
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.inventory.stock.dao.IStockTaxGroupDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockTaxGroupService implements IStockTaxGroupService {

    @Autowired
    private IStockTaxGroupDao stockTaxGroupDao;

    public void setStockTaxGroupDao(IStockTaxGroupDao stockTaxGroupDao) {
        this.stockTaxGroupDao = stockTaxGroupDao;
    }

    /**
     * bu metot stokun vergi gruplarını listeler
     *
     * @param obj
     * @return vergi grubu connection listesi
     */
    @Override
    public List<StockTaxGroupConnection> listStokTaxGroup(Stock obj) {
        return stockTaxGroupDao.listStokTaxGroup(obj);
    }

    /**
     * comboboxa daha önce eklenmiş vergi gruplarının tipindeki vergi grupları
     * gelmicek.
     *
     * @param obj
     * @param isPurchase
     * @return vergi grubu listesi
     */
    @Override
    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase) {
        return stockTaxGroupDao.selectStokTaxGroup(obj, isPurchase);
    }

    /**
     * comboboxa daha önce eklenmiş vergi gruplarının tipindeki vergi grupları
     * gelmicek ama kendi tipindeki vergi grupları gelcek.
     *
     * @param obj
     * @param isPurchase
     * @param type
     * @return
     */
    @Override
    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase, int type) {
        return stockTaxGroupDao.selectStokTaxGroup(obj, isPurchase, type);
    }

    @Override
    public int create(StockTaxGroupConnection obj) {
        return stockTaxGroupDao.create(obj);
    }

    @Override
    public int update(StockTaxGroupConnection obj) {
        return stockTaxGroupDao.update(obj);
    }

    @Override
    public int delete(StockTaxGroupConnection stockTaxGroupConnection) {
        return stockTaxGroupDao.delete(stockTaxGroupConnection);
    }

}
