/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 10:55:35
 */

package com.mepsan.marwiz.system.userdata.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import com.mepsan.marwiz.system.userdata.dao.IUserDataAuthorizeConnectionDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


public class UserDataAuthorizeConnectionService implements IUserDataAuthorizeConnectionService{

    @Autowired
    private IUserDataAuthorizeConnectionDao userDataAuthorizeConnectionDao;

    public void setUserDataAuthorizeConnectionDao(IUserDataAuthorizeConnectionDao userDataAuthorizeConnectionDao) {
        this.userDataAuthorizeConnectionDao = userDataAuthorizeConnectionDao;
    }  
    
    @Override
    public List<UserDataAuthorizeConnection> findAllUserAuthorize(String where, UserDataAuthorizeConnection userDataAuthorizeConnection) {
        if(where.equals("userPage")){
            where=" AND autc.userdata_id = "+userDataAuthorizeConnection.getUserData().getId();
        }else if (where.equals("authorizePage")){
            where=" AND autc.authorize_id = "+userDataAuthorizeConnection.getAuthorize().getId();
        }
               
        return userDataAuthorizeConnectionDao.findAllUserAuthorize(where);
    }

    @Override
    public int create(UserDataAuthorizeConnection obj) {
        return userDataAuthorizeConnectionDao.create(obj);
    }

    @Override
    public int update(UserDataAuthorizeConnection obj) {
        return userDataAuthorizeConnectionDao.update(obj);
    }

    @Override
    public int delete(UserDataAuthorizeConnection obj) {
        return userDataAuthorizeConnectionDao.delete(obj);
    }

}
