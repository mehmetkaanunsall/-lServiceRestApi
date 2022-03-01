/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.07.2020 09:42:52
 */
package com.mepsan.marwiz.service.branchinfo.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;

public interface IGetBranchInfoService {

    public int listBranchInfo(BranchSetting branchSetting);
    
     public void executeCreateList(List<BranchSetting> branchSettings);
}
