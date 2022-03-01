/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:19:05
 */
package com.mepsan.marwiz.general.pointofsale.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pointofsale.dao.IPointOfSaleDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PointOfSaleService implements IPointOfSaleService {

    @Autowired
    private IPointOfSaleDao pointOfSaleDao;

    public void setPointOfSaleDao(IPointOfSaleDao pointOfSaleDao) {
        this.pointOfSaleDao = pointOfSaleDao;
    }

    @Override
    public List<PointOfSale> listOfPointOfSale() {
        return pointOfSaleDao.listOfPointOfSale();
    }

    @Override
    public int create(PointOfSale obj) {
        return pointOfSaleDao.create(obj);
    }

    @Override
    public int update(PointOfSale obj) {
        return pointOfSaleDao.update(obj);
    }

    @Override
    public int delete(PointOfSale obj) {
        return pointOfSaleDao.delete(obj);
    }

    @Override
    public List<PointOfSale> listIntegrationPointOfSale(Branch branch, String where) {
       return pointOfSaleDao.listIntegrationPointOfSale(branch, where);
    }

    @Override
    public int updateIntegrationCode(PointOfSale obj) {
        return pointOfSaleDao.updateIntegrationCode(obj);
    }

}
