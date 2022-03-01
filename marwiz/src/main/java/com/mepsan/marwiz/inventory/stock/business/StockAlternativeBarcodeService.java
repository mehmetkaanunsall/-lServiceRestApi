/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 14.03.2018 09:03:11
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.inventory.stock.dao.IStockAlternativeBarcodeDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockAlternativeBarcodeService implements IStockAlternativeBarcodeService {

    @Autowired
    private IStockAlternativeBarcodeDao stockAlternativeBarcodeDao;

    public void setStockAlternativeBarcodeDao(IStockAlternativeBarcodeDao stockAlternativeBarcodeDao) {
        this.stockAlternativeBarcodeDao = stockAlternativeBarcodeDao;
    }

    @Override
    public int create(StockAlternativeBarcode obj) {
        return stockAlternativeBarcodeDao.create(obj);
    }

    @Override
    public int update(StockAlternativeBarcode obj) {
        return stockAlternativeBarcodeDao.update(obj);
    }

    @Override
    public List<StockAlternativeBarcode> findAll(Stock stock) {
        return stockAlternativeBarcodeDao.findAll(stock);
    }

    @Override
    public int delete(StockAlternativeBarcode stockAlternativeBarcode) {
        return stockAlternativeBarcodeDao.delete(stockAlternativeBarcode);
    }

}
