/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 08:06:58
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IDiscountBranchTabDao extends ICrud<DiscountBranchConnection> {

    public List<DiscountBranchConnection> listOfDiscountBranch(Discount obj);

    public int testBeforeDelete(DiscountBranchConnection obj);

    public int delete(DiscountBranchConnection obj);
}
