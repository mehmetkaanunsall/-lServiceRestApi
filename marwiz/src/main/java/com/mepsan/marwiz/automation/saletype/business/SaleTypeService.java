package com.mepsan.marwiz.automation.saletype.business;

import com.mepsan.marwiz.automation.saletype.dao.ISaleTypeDao;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Samet Dağ
 */
public class SaleTypeService implements ISaleTypeService {

    @Autowired
    public ISaleTypeDao saleTypeDao;

    @Override
    public List<FuelSaleType> findAll() {
        return saleTypeDao.findAll();
    }

    @Override
    public int create(FuelSaleType obj) {
        return saleTypeDao.create(obj);
    }

    @Override
    public int update(FuelSaleType obj) {
        return saleTypeDao.update(obj);
    }

    @Override
    public int delete(FuelSaleType obj) {
        return saleTypeDao.delete(obj);
    }

    @Override
    public List<FuelSaleType> findSaleTypeForBranch(String where) {
        return saleTypeDao.findSaleTypeForBranch(where);
    }

}
