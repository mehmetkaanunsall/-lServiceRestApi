/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 09:50:12
 */
package com.mepsan.marwiz.inventory.taxgroup.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.inventory.taxgroup.dao.ITaxGroupDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class TaxGroupService implements ITaxGroupService {

    @Autowired
    private ITaxGroupDao taxGroupDao;

    public void setTaxGroupDao(ITaxGroupDao taxGroupDao) {
        this.taxGroupDao = taxGroupDao;
    }

    @Override
    public int create(TaxGroup obj) {
        return taxGroupDao.create(obj);
    }

    @Override
    public int update(TaxGroup obj) {
       return taxGroupDao.update(obj);
    }


    @Override
    public List<TaxGroup> findAll() {
       return taxGroupDao.findAll();
    }

    /**
     * bu metot gelen stok objesinin vergi grublarını getirir.
     * @param stock
     * @return 
     */
    @Override
    public List<TaxGroup> findTaxGroupsForStock(Stock stock) {
        return taxGroupDao.findTaxGroupsForStock(stock);
    }

    @Override
    public TaxGroup findTaxGroupsKDV(Stock stock, boolean isPurchase, BranchSetting branchSetting) {
        return taxGroupDao.findTaxGroupsKDV(stock, isPurchase, branchSetting);
    }

    @Override
    public int testBeforeDelete(TaxGroup taxGroup) {
        return taxGroupDao.testBeforeDelete(taxGroup);
    }

    @Override
    public int delete(TaxGroup taxGroup) {
        return taxGroupDao.delete(taxGroup);
    }

    @Override
    public TaxGroup findAccordingToTypeAndRate(TaxGroup taxGroup) {
        return taxGroupDao.findAccordingToTypeAndRate(taxGroup);
    }

    @Override
    public int deleteForOtherBranch(TaxGroup taxGroup) {
        return taxGroupDao.deleteForOtherBranch(taxGroup);
    }

    @Override
    public int updateAvailableTaxGroup(int oldId, int newId) {
        return taxGroupDao.updateAvailableTaxGroup(oldId, newId);
    }

}
