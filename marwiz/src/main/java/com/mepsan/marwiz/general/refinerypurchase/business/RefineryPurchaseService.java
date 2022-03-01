/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:03:14 PM
 */
package com.mepsan.marwiz.general.refinerypurchase.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.refinerypurchase.dao.IRefineryPurchaseDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class RefineryPurchaseService implements IRefineryPurchaseService {

    @Autowired
    IRefineryPurchaseDao refineryPurchaseDao;

    public void setRefineryPurchaseDao(IRefineryPurchaseDao refineryPurchaseDao) {
        this.refineryPurchaseDao = refineryPurchaseDao;
    }

    @Override
    public List<RefineryStockPrice> findAll() {
        return refineryPurchaseDao.findAll();
    }

    @Override
    public int testBeforeDelete(RefineryStockPrice obj) {
        return refineryPurchaseDao.testBeforeDelete(obj);
    }

    @Override
    public int delete(RefineryStockPrice obj) {
        return refineryPurchaseDao.delete(obj);
    }

    @Override
    public int create(RefineryStockPrice obj) {
        return refineryPurchaseDao.create(obj);
    }

    @Override
    public int update(RefineryStockPrice obj) {
        return refineryPurchaseDao.update(obj);
    }

    @Override
    public int findRefineryPrice(RefineryStockPrice obj) {
        return refineryPurchaseDao.findRefineryPrice(obj);
    }

    @Override
    public RefineryStockPrice findStockRefineryPrice(int stockId, Branch branch) {
        return refineryPurchaseDao.findStockRefineryPrice(stockId, branch);

    }

}
