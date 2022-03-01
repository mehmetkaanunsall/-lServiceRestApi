/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:41:26
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IPriceListDao extends ICrud<PriceList> {

    public List<PriceList> listofPriceList();

    public int delete(PriceList priceList);

    public PriceList findDefaultPriceList(boolean isPurchase, Branch branch);

}
