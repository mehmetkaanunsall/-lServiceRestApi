/**
 * Bu sınıf USerDetailService ve LoginBean arasında bağlantıyı sağlar.
 * Oturum açma, oturum kapatma ve gerekli kontrollerin yapıldığı sınıftır.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   10.08.2016 11:01:16
 *
 * @edited Ali Kurt - insertFailed ve captchaStatu metotları eklendi
 * @edited Ali Kurt - getCaptchaStatu ve insertUserDataLogin metotları eklendi.
 * @edited Ali Kurt - findAllLoginInfo metodu eklendi
 */
package com.mepsan.marwiz.general.login.business;

import com.mepsan.marwiz.general.login.dao.ILoginDao;
import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.pattern.HttpSessionCollector;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class LoginService implements ILoginService, Serializable {
    
    private UserDetailService userDetailService;
    private AuthenticationManager authenticationManager;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private SessionRegistry sessionRegistry;
    private HttpServletResponse httpResponse;
    private HttpServletRequest httpRequest;
    private Authentication authentication;
    private UserData userData;
    
    @Autowired
    private ILoginDao loginDao;
    

    
    public String host;
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setLoginDao(ILoginDao loginDao) {
        this.loginDao = loginDao;
    }
    
    public UserData getUser() {
        return userData;
    }
    
    public UserDetailService getUserDetailService() {
        return userDetailService;
    }
    
    public void setUserDetailService(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }
    
    public SessionAuthenticationStrategy getSessionAuthenticationStrategy() {
        return sessionAuthenticationStrategy;
    }
    
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }
    
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
    
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }
    
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }
    
    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    /**
     * Bu metot username ve password girdisine göre oturum açma kontrolünü yapar.
     *
     * @param username
     * @param password
     * @param userDataLogin
     * @return 0: username veya password alanı boş 1: oturum açıldı -1: kullanıcı başka cihazda oturum açmış -2: kullanıcı adı veya şifre
     * yanlıs -4: hatalı işlem
     */
    @Override
    public int doLogin(String username, String password, UserDataLogin userDataLogin) {
        
       try {
            if (username.isEmpty() || password.isEmpty()) {
                return 0;
            }
            
            userData = loginDao.findByUserName(username, password, userDataLogin);
            if(userData==null){
                return -2;
            }
            
            userDetailService.setUser(userData);
            
            Authentication request = new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            httpResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            sessionAuthenticationStrategy.onAuthentication(authentication, httpRequest, httpResponse);
            
            List<SessionInformation> t = getMySessionsInfo(username);
            if (t.size() == 2) {
                t.get(0).expireNow();
                sessionRegistry.removeSessionInformation(t.get(0).getSessionId());
                sessionRegistry.refreshLastRequest(t.get(0).getSessionId());
                HttpSessionCollector.find(t.get(0).getSessionId()).invalidate();
            }
            
        } catch (AuthenticationException e) {
            if (e instanceof SessionAuthenticationException) {//baska cıhazda oturum acılmıs
                SecurityContextHolder.clearContext();
                return -1;
            } else if (e instanceof BadCredentialsException) {//şifre yanlıs
                return -2;
            } else if (e instanceof InternalAuthenticationServiceException) {//kullanıcı yok
                return -3;
            } else {//dıger hatalar
                return -4;
            }
            
        }
        return 1;
    }

 
 

    /**
     * Bu metot oturum açmış tüm kullanıcıların session bilgilerini listeler.
     *
     * @return Oturum bilgileri listesi
     */
    @Override
    public List<SessionInformation> getAllSessionInfo() {
        List<SessionInformation> listOfSessionInfo = new ArrayList<>();
        listOfSessionInfo.clear();
        sessionRegistry.getAllPrincipals().stream().forEach((principal) -> {
            sessionRegistry.getAllSessions(principal, false).stream().forEach((si) -> {
                listOfSessionInfo.add(si);
            });
        });
        return listOfSessionInfo;
    }

    /**
     * Bu metot kullanıcının tüm oturum bilgilerini listeler
     *
     * @param username
     * @return kullanıcıya ait oturum bilgileri listesi
     */
    @Override
    public List<SessionInformation> getMySessionsInfo(String username) {
        List<SessionInformation> listOfSessionInfo = new ArrayList<>();
        
        sessionRegistry.getAllPrincipals().stream().forEach((principal) -> {
            sessionRegistry.getAllSessions(principal, false).stream().filter((si) -> (username.equals(((UserDetails) si.getPrincipal()).getUsername()))).forEach((si) -> {
                listOfSessionInfo.add(si);
            });
        });
        return listOfSessionInfo;
    }

    /**
     * This method finds the session of the id and remove it from session information.
     *
     * @param id is session id
     */
    @Override
    public void expireSession(String id) {
        sessionRegistry.getAllPrincipals().stream().forEach((principal) -> {
            sessionRegistry.getAllSessions(principal, false).stream().filter((si) -> (si.getSessionId().equals(id))).map((si) -> {
                si.expireNow();
                return si;
            }).forEach((_item) -> {
                sessionRegistry.removeSessionInformation(id);
            });
        });
        
    }
    
    @Override
    public UserData failedLogin(String username, String password, UserDataLogin userDataLogin) {
        return loginDao.failedLogin(username, password, userDataLogin);
    }
    
  
  
}
