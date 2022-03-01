/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.10.2016 11:04:38
 */
package com.mepsan.marwiz.general.pcrdmymenu.business;

import com.mepsan.marwiz.general.core.business.BreadCrumbService;
import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import com.mepsan.marwiz.general.pcrdmymenu.dao.IPcrdMyMenuDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PcdMyMenuService implements IPcrdMyMenuService {

    @Autowired
    private IPcrdMyMenuDao pcrdMyMenuDao;

    @Autowired
    private BreadCrumbService breadCrumb;

    public void setBreadCrumb(BreadCrumbService breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public void setPcrdMyMenuDao(IPcrdMyMenuDao pcrdMyMenuDao) {
        this.pcrdMyMenuDao = pcrdMyMenuDao;
    }

    @Override
    public List<UserDataMenuConnection> findMyModules() {
        return pcrdMyMenuDao.findMyModules();
    }

    @Override
    public int create(UserDataMenuConnection obj) {
        return pcrdMyMenuDao.create(obj);
    }

    @Override
    public int update(UserDataMenuConnection obj) {
        return pcrdMyMenuDao.update(obj);
    }

    @Override
    public int delete(UserDataMenuConnection obj) {
        return pcrdMyMenuDao.delete(obj);
    }

    @Override
    public int reOrder(List<UserDataMenuConnection> list) {
        return pcrdMyMenuDao.reOrder(list);
    }

}
