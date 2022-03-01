/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:52:42
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IDiscountService extends ILazyGridService {

    public int create(Discount obj);

    public int update(Discount obj);

    public int testBeforeDelete(Discount discount);

    public int delete(Discount obj);

}
