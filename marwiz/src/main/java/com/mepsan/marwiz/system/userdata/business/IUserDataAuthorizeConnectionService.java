/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   02.02.2018 10:55:16
 */

package com.mepsan.marwiz.system.userdata.business;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataAuthorizeConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;


public interface IUserDataAuthorizeConnectionService extends ICrudService<UserDataAuthorizeConnection> {

    public List<UserDataAuthorizeConnection> findAllUserAuthorize(String where, UserDataAuthorizeConnection userDataAuthorizeConnection);
    
    public int delete(UserDataAuthorizeConnection obj);
}
