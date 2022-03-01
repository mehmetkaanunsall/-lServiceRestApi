/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:39:35
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.inventory.pricelist.dao.IPriceListDao;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class PriceListService implements IPriceListService {

    @Autowired
    private IPriceListDao priceListDao;
    
    @Autowired
    public SessionBean sessionBean;


    public void setPriceListDao(IPriceListDao priceListDao) {
        this.priceListDao = priceListDao;
    }

    @Override
    public List<PriceList> listofPriceList() {
        return priceListDao.listofPriceList();
    }

    @Override
    public int create(PriceList obj) {
        return priceListDao.create(obj);
    }

    @Override
    public int update(PriceList obj) {
        return priceListDao.update(obj);
    }

    @Override
    public int delete(PriceList priceList) {
        return priceListDao.delete(priceList);
    }

    @Override
    public PriceList findDefaultPriceList(boolean isPurchase, Branch branch) {
        return priceListDao.findDefaultPriceList(isPurchase, branch);
    }

   
    
  

}
