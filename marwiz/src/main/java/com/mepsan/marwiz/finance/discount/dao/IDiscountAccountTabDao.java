/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:21:49
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IDiscountAccountTabDao extends ICrud<DiscountAccountConnection> {

    public List<DiscountAccountConnection> listofDiscountAccount(Discount obj, String where);

    public int testBeforeDelete(DiscountAccountConnection obj);

    public int delete(DiscountAccountConnection obj);
}
