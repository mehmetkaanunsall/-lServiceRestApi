/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.07.2021 03:01:15
 */
package com.mepsan.marwiz.service.paro.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;

public interface ICallCampaignInfoDao {

    public List<BranchSetting> findBranchSettingsForCampaignInfo();

    public int updateParoInformation(BranchSetting obj, String pointOfSaleIntegrationList);

}
