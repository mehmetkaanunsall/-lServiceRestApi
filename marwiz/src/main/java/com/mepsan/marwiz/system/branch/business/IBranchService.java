/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:51:20
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IBranchService extends ICrudService<Branch> {

    public List<Branch> findAll(String where);

    public List<Branch> selectBranchs();

    public List<Branch> findUserAuthorizeBranch();

    public List<Branch> findUserAuthorizeBranchForBankAccount();

    public int delete(Branch branch);

    public Branch findBranch(Branch branch);

}
