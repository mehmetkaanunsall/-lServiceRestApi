/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:56:14
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;

public interface IDiscountDao extends ILazyGrid<Discount> {

    public int create(Discount obj);

    public int update(Discount obj);

    public int testBeforeDelete(Discount discount);

    public int delete(Discount obj);
}
