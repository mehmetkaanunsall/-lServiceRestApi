/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 02:02:26
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBranchSettingDao extends ICrud<BranchSetting> {

    public BranchSetting find(BranchSetting obj);

    public BranchSetting findCentralIntegration();

    public BranchSetting findAutomationSetting(Branch obj);

    public BranchSetting findLicanseCode();

    public BranchSetting findStarbucksMachicne();

    public List<BranchSetting> findUserAuthorizeBranch();

    public BranchSetting findBranchSetting(Branch branch);

    public List<BranchSetting> findUserAuthorizeBranchForInvoiceAuth();
    
    public int updateParoInformation(BranchSetting obj,  List<String> pointOfSaleIntegrationList);
}
