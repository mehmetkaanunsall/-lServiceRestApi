/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.03.2021 02:04:59
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBranchIntegrationService extends ICrudService<BranchIntegration> {

    public BranchIntegration findBranchIntegration();

    public List<BranchIntegration> listOfIntegration(Branch obj);

    public int delete(BranchIntegration obj);

}
