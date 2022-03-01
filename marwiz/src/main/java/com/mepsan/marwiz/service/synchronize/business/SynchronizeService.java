/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.09.2020 11:24:09
 */
package com.mepsan.marwiz.service.synchronize.business;

import com.mepsan.marwiz.general.login.dao.ILoginDao;
import org.springframework.beans.factory.annotation.Autowired;

public class SynchronizeService implements ISynchronizeService {

    @Autowired
    ILoginDao loginDao;

    public void setLoginDao(ILoginDao loginDao) {
        this.loginDao = loginDao;
    }

    @Override
    public boolean checkUser(String username, String password) {
        return loginDao.checkUser(username, password);
    }

}
