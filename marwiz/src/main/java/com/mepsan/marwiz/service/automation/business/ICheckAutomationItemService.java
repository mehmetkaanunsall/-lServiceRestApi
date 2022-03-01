/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 10:48:41
 */
package com.mepsan.marwiz.service.automation.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;

public interface ICheckAutomationItemService {

    public void listAutomationShift(BranchSetting branchSetting);

    public void listAutomationShiftAsync();

    public void executeListAutomationShift(List<BranchSetting> branchSettings);

}
