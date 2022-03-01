/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.03.2021 02:05:08
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.system.branch.dao.IBranchIntegrationDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BranchIntegrationService implements IBranchIntegrationService {

    @Autowired
    private IBranchIntegrationDao branchIntegrationDao;

    public void setBranchIntegrationDao(IBranchIntegrationDao branchIntegrationDao) {
        this.branchIntegrationDao = branchIntegrationDao;
    }

    @Override
    public List<BranchIntegration> listOfIntegration(Branch obj) {
        return branchIntegrationDao.listOfIntegration(obj);
    }

    @Override
    public int create(BranchIntegration obj) {
        return branchIntegrationDao.create(obj);
    }

    @Override
    public int update(BranchIntegration obj) {
        return branchIntegrationDao.update(obj);
    }

    @Override
    public int delete(BranchIntegration obj) {
        return branchIntegrationDao.delete(obj);
    }

    @Override
    public BranchIntegration findBranchIntegration() {
        return branchIntegrationDao.findBranchIntegration();
    }

}
