/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 08:32:03
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.finance.discount.dao.IDiscountBranchTabDao;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscountBranchTabService implements IDiscountBranchTabService {

    @Autowired
    private IDiscountBranchTabDao discountBranchTabDao;

    @Override
    public List<DiscountBranchConnection> listOfDiscountBranch(Discount obj) {
        return discountBranchTabDao.listOfDiscountBranch(obj);
    }

    @Override
    public int create(DiscountBranchConnection obj) {
        return discountBranchTabDao.create(obj);
    }

    @Override
    public int update(DiscountBranchConnection obj) {
        return discountBranchTabDao.update(obj);
    }

    @Override
    public int testBeforeDelete(DiscountBranchConnection obj) {
      return discountBranchTabDao.testBeforeDelete(obj);
    }

    @Override
    public int delete(DiscountBranchConnection obj) {
        return discountBranchTabDao.delete(obj);
    }

}
