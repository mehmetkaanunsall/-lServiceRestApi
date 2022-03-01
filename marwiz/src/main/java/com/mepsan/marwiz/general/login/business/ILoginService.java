/**
 * Bu interface LoginService sınıfına arayüz oluşturur.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   10.08.2016 17:01:16
 *
 * @edited Ali Kurt - findAllLoginInfo metotları eklendi
 */
package com.mepsan.marwiz.general.login.business;

import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import org.springframework.security.core.session.SessionInformation;

public interface ILoginService {

    public int doLogin(String username, String password, UserDataLogin userDataLogin);

    public UserData failedLogin(String username, String password, UserDataLogin userDataLogin);


    public List<SessionInformation> getAllSessionInfo();

    public List<SessionInformation> getMySessionsInfo(String username);

    public void expireSession(String id);

    
}
