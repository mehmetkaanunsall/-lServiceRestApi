/**
 * This interface ...
 *
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 08:24:00
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IStockTaxGroupDao extends ICrud<StockTaxGroupConnection> {

    public List<StockTaxGroupConnection> listStokTaxGroup(Stock obj);

    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase);

    public List<TaxGroup> selectStokTaxGroup(Stock obj, boolean isPurchase, int type);

    public int delete(StockTaxGroupConnection stockTaxGroupConnection);

}
