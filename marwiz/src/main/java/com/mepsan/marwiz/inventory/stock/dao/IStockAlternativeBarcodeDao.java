/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 14.03.2018 09:00:53
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IStockAlternativeBarcodeDao extends ICrud<StockAlternativeBarcode> {

    public List<StockAlternativeBarcode> findAll(Stock stock);

    public int delete(StockAlternativeBarcode stockAlternativeBarcode);
}
