/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 14:19:19
 */
package com.mepsan.marwiz.general.brand.business;

import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBrandService extends ICrudService<Brand> {

    public List<Brand> findAll(Item item);

    public String sendWhere(Item item);

    public int testBeforeDelete(Brand obj);

    public int delete(Brand obj);

    public Brand findBrandAccordingToName(Brand obj);

    public int deleteForOtherBranch(Brand obj);

    public int updateAvailableBrand(int oldId, int newId);
}
