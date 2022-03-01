/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.05.2020 09:38:49
 */

package com.mepsan.marwiz.service.backup.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;


public interface IGetBackUpParameterService {
    
    public int listBackUpParameters(BranchSetting branchSetting);

}