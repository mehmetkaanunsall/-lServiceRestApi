/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:51:35
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.system.branch.dao.IBranchDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BranchService implements IBranchService {

    @Autowired
    private IBranchDao branchDao;

    public void setBranchDao(IBranchDao branchDao) {
        this.branchDao = branchDao;
    }

    @Override
    public List<Branch> findAll(String where) {
        return branchDao.findAll(where);
    }

    @Override
    public int create(Branch obj) {
        return branchDao.create(obj);
    }

    @Override
    public int update(Branch obj) {
        return branchDao.update(obj);
    }

    @Override
    public List<Branch> selectBranchs() {
        return branchDao.selectBranchs();
    }

    @Override
    public List<Branch> findUserAuthorizeBranch() {
        return branchDao.findUserAuthorizeBranch();
    }

    @Override
    public List<Branch> findUserAuthorizeBranchForBankAccount() {
        return branchDao.findUserAuthorizeBranchForBankAccount();
    }

    @Override
    public int delete(Branch branch) {
        return branchDao.delete(branch);
    }

    @Override
    public Branch findBranch(Branch branch) {
        List<Branch> list = branchDao.findAll(" AND br.id = " + branch.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new Branch();
        }
    }

}
