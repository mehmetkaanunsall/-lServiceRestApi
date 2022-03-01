/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.03.2021 02:04:50
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBranchIntegrationDao extends ICrud<BranchIntegration> {

    public List<BranchIntegration> listOfIntegration(Branch obj);

    public int delete(BranchIntegration obj);

    public BranchIntegration findBranchIntegration();
}
