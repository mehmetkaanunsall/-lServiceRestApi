/**
 * 
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:59:54 PM
 */

package com.mepsan.marwiz.general.refinerypurchase.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IRefineryPurchaseDao extends ICrud<RefineryStockPrice>{
    
    public List<RefineryStockPrice> findAll();

    public int testBeforeDelete(RefineryStockPrice obj);

    public int delete(RefineryStockPrice obj);

    public int findRefineryPrice (RefineryStockPrice obj);
    
    public RefineryStockPrice findStockRefineryPrice(int stockId, Branch branch);
}