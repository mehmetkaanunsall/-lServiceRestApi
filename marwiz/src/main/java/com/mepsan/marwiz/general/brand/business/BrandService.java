/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 14:19:35
 */
package com.mepsan.marwiz.general.brand.business;

import com.mepsan.marwiz.general.brand.dao.IBrandDao;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BrandService implements IBrandService {

    @Autowired
    private IBrandDao brandDao;

    public void setBrandDao(IBrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    public List<Brand> findAll(Item item) {
        return brandDao.findAll(item);
    }

    @Override
    public int create(Brand obj) {
        return brandDao.create(obj);
    }

    @Override
    public int update(Brand obj) {
        return brandDao.update(obj);
    }

    @Override
    public String sendWhere(Item item) {
        return " and br.item_id= " + item.getId();
    }

    @Override
    public int delete(Brand obj) {
        return brandDao.delete(obj);
    }

    @Override
    public int testBeforeDelete(Brand obj) {
        return brandDao.testBeforeDelete(obj);
    }

    @Override
    public Brand findBrandAccordingToName(Brand obj) {
        return brandDao.findBrandAccordingToName(obj);
    }

    @Override
    public int deleteForOtherBranch(Brand obj) {
        return brandDao.deleteForOtherBranch(obj);
    }

    @Override
    public int updateAvailableBrand(int oldId, int newId) {
        return brandDao.updateAvailableBrand(oldId, newId);
    }
}
