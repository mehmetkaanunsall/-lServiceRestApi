/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 17:15:43 
 */

package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.inventory.stock.dao.StockAnalysis;
import java.util.List;


public interface IStockAnalysisService {

    public StockAnalysis selectStockAnalysis(Stock stock, Branch branch);

    public List<StockAnalysis> listOfMonthAverage(Stock stock, Branch branch);

    public List<StockAnalysis> listOfThreeMonthAverage(Stock stock, Branch branch);
    
    public List<StockAnalysis> listOfYearAverage(Stock stock, Branch branch);
    

}

