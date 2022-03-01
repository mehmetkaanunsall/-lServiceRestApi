/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:55:09
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.finance.discount.dao.IDiscountDao;
import com.mepsan.marwiz.general.model.finance.Discount;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscountService implements IDiscountService {

    @Autowired
    private IDiscountDao discountDao;

    public void setDiscountDao(IDiscountDao discountDao) {
        this.discountDao = discountDao;
    }

    @Override
    public int create(Discount obj) {
        return discountDao.create(obj);
    }

    @Override
    public int update(Discount obj) {
        return discountDao.update(obj);
    }

    @Override
    public int testBeforeDelete(Discount discount) {
        return discountDao.testBeforeDelete(discount);
    }

    @Override
    public int delete(Discount obj) {
        return discountDao.delete(obj);
    }

    @Override
    public List findAll(int first, int pageSize, String sortField, String sortOrder, Map filters, String where) {
        return discountDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return discountDao.count(where);
    }

}
