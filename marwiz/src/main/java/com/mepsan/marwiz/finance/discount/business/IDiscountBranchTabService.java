/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 08:25:54
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IDiscountBranchTabService extends ICrudService<DiscountBranchConnection> {

    public List<DiscountBranchConnection> listOfDiscountBranch(Discount obj);

    public int testBeforeDelete(DiscountBranchConnection obj);

    public int delete(DiscountBranchConnection obj);

}
