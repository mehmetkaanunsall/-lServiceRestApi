/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:01:19 PM
 */
package com.mepsan.marwiz.general.refinerypurchase.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IRefineryPurchaseService extends ICrudService<RefineryStockPrice> {

    public List<RefineryStockPrice> findAll();

    public int testBeforeDelete(RefineryStockPrice obj);

    public int delete(RefineryStockPrice obj);

    public int findRefineryPrice(RefineryStockPrice obj);

    public RefineryStockPrice findStockRefineryPrice(int stockId, Branch branch);

}
