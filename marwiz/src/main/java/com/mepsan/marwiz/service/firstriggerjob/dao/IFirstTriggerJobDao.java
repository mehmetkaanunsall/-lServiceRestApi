/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.07.2021 01:33:25
 */
package com.mepsan.marwiz.service.firstriggerjob.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;

public interface IFirstTriggerJobDao {

    public String callFirstTriggerJob();

    public int updateSystemParameters();
    
     public BranchSetting findTopCentralIntegratedBranchSetting();
}
