/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:39:16
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.List;

public interface IPriceListService extends ICrudService<PriceList> {

    public List<PriceList> listofPriceList();

    public int delete(PriceList priceList);

    public PriceList findDefaultPriceList(boolean isPurchase, Branch branch);
     

}
