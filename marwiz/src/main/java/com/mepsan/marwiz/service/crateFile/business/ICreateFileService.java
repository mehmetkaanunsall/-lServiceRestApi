/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.03.2020 05:21:24
 */
package com.mepsan.marwiz.service.crateFile.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.Document;
import java.util.List;

public interface ICreateFileService {

    public void createSalesFile(BranchSetting branchSetting);

    public Document createSaleDetailDocument(String sale, int extentionType);

    public void executeCreateList(List<BranchSetting> branchSettings);

}
