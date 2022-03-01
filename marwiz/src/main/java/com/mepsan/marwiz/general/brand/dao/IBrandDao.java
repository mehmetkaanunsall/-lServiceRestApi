/**
 *
 * Bu sınıf, Brand sınıfına arayüz oluşturur.
 *
 * @author Ali Kurt
 *
 * Created on 26.10.2016 16:57:29
 */
package com.mepsan.marwiz.general.brand.dao;

import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBrandDao extends ICrud<Brand> {

    public List<Brand> findAll(Item item);

    public int testBeforeDelete(Brand obj);

    public int delete(Brand obj);

    public Brand findBrandAccordingToName(Brand obj);

    public int deleteForOtherBranch(Brand obj);

    public int updateAvailableBrand(int oldId, int newId);
}
