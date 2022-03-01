/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:21:02
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.finance.discount.dao.IDiscountAccountTabDao;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscountAccountTabService implements IDiscountAccountTabService {

    @Autowired
    private IDiscountAccountTabDao discountAccountTabDao;

    public void setDiscountAccountTabDao(IDiscountAccountTabDao discountAccountTabDao) {
        this.discountAccountTabDao = discountAccountTabDao;
    }

    @Override
    public List<DiscountAccountConnection> listofDiscountAccount(Discount obj, String where) {
        return discountAccountTabDao.listofDiscountAccount(obj, where);
    }

    @Override
    public int create(DiscountAccountConnection obj) {
        return discountAccountTabDao.create(obj);
    }

    @Override
    public int update(DiscountAccountConnection obj) {
        return discountAccountTabDao.update(obj);
    }

    @Override
    public int testBeforeDelete(DiscountAccountConnection obj) {
        return discountAccountTabDao.testBeforeDelete(obj);
    }

    @Override
    public int delete(DiscountAccountConnection obj) {
        return discountAccountTabDao.delete(obj);
    }

}
