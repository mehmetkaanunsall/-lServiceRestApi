/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:08:41
 */
package com.mepsan.marwiz.general.pointofsale.business;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.PointOfSaleSafeConnection;
import com.mepsan.marwiz.general.pointofsale.dao.IPointOfSaleSafeDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PointOfSaleSafeService implements IPointOfSaleSafeService {

    @Autowired
    private IPointOfSaleSafeDao pointOfSaleSafeDao;

    public void setPointOfSaleSafeDao(IPointOfSaleSafeDao pointOfSaleSafeDao) {
        this.pointOfSaleSafeDao = pointOfSaleSafeDao;
    }

    @Override
    public List<PointOfSaleSafeConnection> listofPOSSafe(PointOfSale obj) {
        return pointOfSaleSafeDao.listofPOSSafe(obj);
    }

    @Override
    public int create(PointOfSaleSafeConnection pointOfSaleSafeConnection) {
        return pointOfSaleSafeDao.create(pointOfSaleSafeConnection);
    }

    @Override
    public int update(PointOfSaleSafeConnection pointOfSaleSafeConnection) {
        return pointOfSaleSafeDao.update(pointOfSaleSafeConnection);
    }

    @Override
    public int delete(PointOfSaleSafeConnection obj) {
        return pointOfSaleSafeDao.delete(obj);
    }

}
