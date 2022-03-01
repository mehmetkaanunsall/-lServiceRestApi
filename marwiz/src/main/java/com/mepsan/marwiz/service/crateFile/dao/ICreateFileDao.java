/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.03.2020 05:26:10
 */
package com.mepsan.marwiz.service.crateFile.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.Date;
import java.util.List;

public interface ICreateFileDao {

    public String listOfSale(BranchSetting branchSetting, Date beginDate, Date endDate);

    public List<BranchSetting> findBranchSettingsForCreateFile();

}
