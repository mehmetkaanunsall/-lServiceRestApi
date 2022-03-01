/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:29:08
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IDiscountItemDao extends ICrud<DiscountItem> {

    public List<DiscountItem> listofDiscountItem(Discount obj);
    
     public int testBeforeDelete(DiscountItem discount);

    public int delete(DiscountItem obj);

}
