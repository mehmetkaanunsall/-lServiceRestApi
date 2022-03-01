/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.07.2021 02:55:42
 */
package com.mepsan.marwiz.service.paro.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;

public interface ICallCampaignInfoService {

    public void executeWebServiceInfo(List<BranchSetting> branchSettings);

    public int updateParoInformation(BranchSetting obj, String pointOfSaleIntegrationList);

}
