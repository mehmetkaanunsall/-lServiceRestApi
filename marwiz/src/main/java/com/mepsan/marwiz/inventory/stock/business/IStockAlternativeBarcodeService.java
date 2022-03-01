/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 14.03.2018 09:02:44
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IStockAlternativeBarcodeService extends ICrudService<StockAlternativeBarcode> {

    public List<StockAlternativeBarcode> findAll(Stock stock);

    public int delete(StockAlternativeBarcode stockAlternativeBarcode);

}
