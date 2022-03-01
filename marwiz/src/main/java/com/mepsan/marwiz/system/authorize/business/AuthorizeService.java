/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 01:16:19
 */
package com.mepsan.marwiz.system.authorize.business;

import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.system.authorize.dao.IAuthorizeDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthorizeService implements IAuthorizeService {

    @Autowired
    private IAuthorizeDao authorizeDao;

    public void setAuthorizeDao(IAuthorizeDao authorizeDao) {
        this.authorizeDao = authorizeDao;
    }

    @Override
    public List<Authorize> findAll() {
        return authorizeDao.findAll();
    }

    @Override
    public int create(Authorize obj) {
        return authorizeDao.create(obj);
    }

    @Override
    public int update(Authorize obj) {
        return authorizeDao.update(obj);
    }

    @Override
    public List<Authorize> selectAuthorize() {
        return authorizeDao.selectAuthorize();
    }

    @Override
    public int updateModuleTab(Authorize authorize) {
        return authorizeDao.updateModuleTab(authorize);
    }

    @Override
    public int updatePageTab(Authorize authorize) {
        return authorizeDao.updatePageTab(authorize);
    }

    @Override
    public List<Authorize> selectAuthorizeToTheBranch(Branch branch) {
        return authorizeDao.selectAuthorizeToTheBranch(branch);
    }

    @Override
    public int delete(Authorize authorize) {
        return authorizeDao.delete(authorize);
    }

    @Override
    public int testBeforeDelete(Authorize authorize) {
        return authorizeDao.testBeforeDelete(authorize);
    }

}
