/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:51:44
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IBranchDao extends ICrud<Branch> {

    public List<Branch> findAll(String where);

    public List<Branch> selectBranchs();

    public List<Branch> findUserAuthorizeBranch();

    public List<Branch> findUserAuthorizeBranchForBankAccount();
    
    public int delete(Branch branch);

}
