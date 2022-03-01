package com.mepsan.marwiz.system.userdata.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 10:53:34
 */


public interface IUserDataAuthorizeConnectionDao extends ICrud<UserDataAuthorizeConnection> {

    public List<UserDataAuthorizeConnection> findAllUserAuthorize(String where);
    
    public int delete(UserDataAuthorizeConnection obj);
}
