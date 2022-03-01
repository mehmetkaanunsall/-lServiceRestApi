/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 02:00:40
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBranchSettingService extends ICrudService<BranchSetting> {

    public BranchSetting find(BranchSetting obj);

    public BranchSetting findCentralIntegration();

    public BranchSetting findAutomationSetting(Branch obj);

    public BranchSetting findStarbucksMachicne();

    public List<BranchSetting> findUserAuthorizeBranch();

    public BranchSetting findBranchSetting(Branch branch);

    public List<BranchSetting> findUserAuthorizeBranchForInvoiceAuth();
    
    public int updateParoInformation(BranchSetting obj, List<String> pointOfSaleIntegrationList);

}
