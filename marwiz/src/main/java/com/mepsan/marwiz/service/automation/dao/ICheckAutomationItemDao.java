/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 11:39:06
 */
package com.mepsan.marwiz.service.automation.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckAutomationItem;
import java.util.Date;
import java.util.List;

public interface ICheckAutomationItemDao {

    public List<BranchSetting> findAutomationIntegratedBranchSettings();

    public int insertAutomationItem(CheckAutomationItem obj, BranchSetting branchSetting);

    public Date getMaxProcessDateByType(int type, BranchSetting branchSetting);

}
