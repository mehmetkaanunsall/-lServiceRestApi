/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   18.01.2018 04:47:21
 */

package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockCategorizationConnection;
import com.mepsan.marwiz.general.pattern.ICategorization;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IStockCategorizationDao extends ICrud<StockCategorizationConnection>,ICategorization<Stock>{

    public List<Categorization> listOfCategorization();
}