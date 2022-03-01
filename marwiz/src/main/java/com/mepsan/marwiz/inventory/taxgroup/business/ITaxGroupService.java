/**
 *
 *  Bu sınıf, TaxGroupService sınıfına arayüz oluşturur.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 09:49:59
 */
package com.mepsan.marwiz.inventory.taxgroup.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ITaxGroupService extends ICrud<TaxGroup> {

    public List<TaxGroup> findAll();

    public List<TaxGroup> findTaxGroupsForStock(Stock stock);

    public TaxGroup findTaxGroupsKDV(Stock stock, boolean isPurchase, BranchSetting branchSetting);

    public int testBeforeDelete(TaxGroup taxGroup);

    public int delete(TaxGroup taxGroup);

    public TaxGroup findAccordingToTypeAndRate(TaxGroup taxGroup);

    public int deleteForOtherBranch(TaxGroup taxGroup);
    
    public int updateAvailableTaxGroup(int oldId, int newId);

}
