/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:19:33
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IDiscountAccountTabService extends ICrudService<DiscountAccountConnection> {

    public List<DiscountAccountConnection> listofDiscountAccount(Discount obj,String where);
   public int testBeforeDelete(DiscountAccountConnection obj);

    public int delete(DiscountAccountConnection obj);
}
