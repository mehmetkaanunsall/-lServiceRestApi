/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.07.2020 09:41:53
 */

package com.mepsan.marwiz.service.branchinfo.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.List;


public interface IGetBranchInfoDao {
    
     public int callBranchInfo(int branchId, String response);
     
      public List<BranchSetting> findBranchSettingsForBranchInfo();

}