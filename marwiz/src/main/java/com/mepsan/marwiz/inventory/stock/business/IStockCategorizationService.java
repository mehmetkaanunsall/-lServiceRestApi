/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   18.01.2018 05:06:53
 */

package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockCategorizationConnection;
import com.mepsan.marwiz.general.pattern.ICategorizationService;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;


public interface IStockCategorizationService extends ICrudService<StockCategorizationConnection>, ICategorizationService<Stock, StockCategorizationConnection>{

    public List<Categorization> listOfCategorization();
}