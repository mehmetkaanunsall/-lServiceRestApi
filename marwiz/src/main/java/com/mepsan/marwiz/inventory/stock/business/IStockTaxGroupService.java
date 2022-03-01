/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 10:24:28
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IStockTaxGroupService extends ICrudService<StockTaxGroupConnection> {

    public List<StockTaxGroupConnection> listStokTaxGroup(Stock obj);

    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase);

    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase, int type);

    public int delete(StockTaxGroupConnection stockTaxGroupConnection);

}
