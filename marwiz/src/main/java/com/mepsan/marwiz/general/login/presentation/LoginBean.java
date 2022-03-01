/**
 * Bu sınıf, kullanıcı girşi yapar, giriş bilgilerini loglar, giriş yapan kullanıcı yetkilerini çeker.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   09.08.2016 11:01:16
 *
 * @edited Ali Kurt - addUserFailers,controlIp,getCaptchaStatu,getLocation ve initialize metotları eklendi
 */
package com.mepsan.marwiz.general.login.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.login.business.LoginService;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import com.mepsan.marwiz.general.model.wot.UserFailer;
import eu.bitwalker.useragentutils.UserAgent;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginBean implements Serializable {
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private ApplicationBean applicationBean;

    DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private String username;
    private String password;
    private boolean isCaptchaSolved;
    private String latitude;
    private String longitude;
    private String lockedTime;
    private String ip;
    private boolean language_isTR;
    private String sessionId;
    private UserDataLogin userDataLogin;
    private UserData user;
    private Parameter parameter;
    private String captchaValue;
    private boolean renderCaptcha;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public String getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(String lockedTime) {
        this.lockedTime = lockedTime;
    }

    public boolean getIsCaptchaSolved() {
        return isCaptchaSolved;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public boolean isLanguage_isTR() {
        return language_isTR;
    }

    public void setLanguage_isTR(boolean language_isTR) {
        this.language_isTR = language_isTR;
    }

    public String getCaptchaValue() {
        return captchaValue;
    }

    public void setCaptchaValue(String captchaValue) {
        this.captchaValue = captchaValue;
    }

    public boolean isRenderCaptcha() {
        return renderCaptcha;
    }

    public void setRenderCaptcha(boolean renderCaptcha) {
        this.renderCaptcha = renderCaptcha;
    }

    public void initialize() {
        System.out.println("login bean");
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(applicationBean.getParameterMap().get("language_code").getValue().toLowerCase().trim()));
        renderCaptcha = false;
        //default dili aldık
        String code = applicationBean.getParameterMap().get("language_code").getValue();
        language_isTR = code.contains("tr") || code.contains("TR");

        FacesContext fCtx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
        sessionId = session.getId();
        userDataLogin = new UserDataLogin();
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            UserAgent u = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            userDataLogin.setBrowser(u.getBrowser().getName() + "-" + u.getBrowserVersion().getVersion());
            userDataLogin.setDeviceType(u.getOperatingSystem().getDeviceType().getName());
            
            ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = request.getRemoteAddr();
            }

            if (ip.equals("0:0:0:0:0:0:0:1")) {//localde
                InetAddress inetAddress = null;

                inetAddress = InetAddress.getByName(ip);  // performans duşuk bakılacak

                for (NetworkInterface networks : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (networks.isUp() && !networks.isVirtual() && !networks.isLoopback()) {
                        if (ip.equals("0:0:0:0:0:0:0:1")) {//localde
                            ip = networks.getInetAddresses().nextElement().getHostAddress();
                        } else {
                            ip = inetAddress.getHostAddress();
                        }
                        break;
                    }
                }

            }
        } catch (Exception e) {
            ip = "";
            userDataLogin.setBrowser("");
        }

        userDataLogin.setIpAddress(ip);
        getCaptchaStatu(0);

        setParameter(applicationBean.getParameterMap().get("logo"));

    }

    /**
     * Bu metot son yarım saatteki yanlış giriş sayısına göre captcha durumu ve
     * kilitlenme durumunu update eder.
     *
     * @param loginResult fonksiyon çalışmadan önce girien kullanıcı adı ve
     * şifre doğrumu? (0,1,-1..)
     * @return kullanıcı veya ip adresinin son yarım saatteki yanlış giriş
     * deneme sayısı
     */
    public int getCaptchaStatu(int loginResult) {
        int failedCount = 0;
        UserDataLogin usdl = null;

        if (loginResult == 0) {//sayfa yenılenınce
            failedCount = controlIp();//application bean listeye bak
        } else {//login butonundan
            //usdl = loginService.failedLogin(username, password, userDataLogin).getUserDataLogin();
            //  failedCount = usdl.getFailedLoginSize();
        }

        if (failedCount > 2) {
            isCaptchaSolved = true;
        }
        if (failedCount == -1) {

            isCaptchaSolved = true;
            List<UserFailer> failers = applicationBean.getMap().get(ip);
            if (loginResult == 0) {//sayfa yenılenınce
                lockedTime = dateFormatDay.format(failers.get(failers.size() - 1).getFailTime());
            } else {//butona tıklanınca
                lockedTime = dateFormatDay.format(usdl.getLastFailedLoginTime());
            }
        }
        return failedCount;
    }

    public String login() {
        FacesMessage facesMsg;
        if (latitude != null) {
            userDataLogin.setLocation(latitude + "-" + longitude);
        } else {
            userDataLogin.setLocation("0-0");
        }
        int result = loginService.doLogin(username, password, userDataLogin);

        user = loginService.getUser();
        System.out.println("---------RESULT------" + result);
        switch (result) {
            case 0: //  username veya passowrd bos
                facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        language_isTR ? "Kullanıcı Adı ve Şifre Boş Bırakılamaz!" : "Username and password cannot be left blank!", "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                captchaValue = "";
                renderCaptcha = true;
                return null;
            case 1: //doğru giriş
                /*if (loginService.getUser().getUserDataLogin().getFailedLoginSize() == -1) {
                        Calendar lastFailedLoginTime = Calendar.getInstance();
                        lastFailedLoginTime.setTime(loginService.getUser().getUserDataLogin().getLastFailedLoginTime());
                        lastFailedLoginTime.add(Calendar.MINUTE, 30);
                        loginService.getUser().getUserDataLogin().setLastFailedLoginTime(lastFailedLoginTime.getTime());
                        lockedTime = dateFormatDay.format(loginService.getUser().getUserDataLogin().getLastFailedLoginTime());
                        facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                lockedTime + (language_isTR ? " Kadar Oturumunuz Kilitlendi!" : " Your session is locked!"), "");
                        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                        return null;
                    } else {*/
                //applicationbean listesindeki bu ip den olan yanlış girişleri sil
                //  removeUserFailers(loginService.getUser().getUserDataLogin().getLastFailedLoginTime());
                renderCaptcha = false;
                boolean isCaptchaCorrect = false;
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
               

                if (captchaValue != null && !captchaValue.isEmpty()) {
                  
                    if (isCaptchaCorrect) {
                        if (userDataLogin.getDeviceType().equals("Computer")) {
                            loginService.getUser().setGridRowSelect("rowDblselect");
                            loginService.getUser().setIsMobile(false);
                        } else {
                            loginService.getUser().setGridRowSelect("rowSelect");
                            loginService.getUser().setIsMobile(true);
                        }
                        return "/pages/marwiz?faces-redirect=true";
                        

                    }

                } else {
                    facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            language_isTR ? "Doğrulama Kodunu Giriniz!"
                                    : "Enter The Verification Code", "");
                    FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                    return null;
                }
            //}
            case -1:// bu kullanıcı baska cıhazda oturum acmıs
                facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        language_isTR ? "Bu kullanıcı başka bir cihazda oturum açmış durumda! Lütfen admin ile irtibata geçiniz."
                                : "This user is signed in on another device! Please contact with admin.", language_isTR ? "Max Kullanıcı!" : "Max User!");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                renderCaptcha = true;
                return null;
            case -2:// şifre yanlıs

                int failedSize = getCaptchaStatu(1);//butona tıklandıgında hata durumunu cek
                if (failedSize == -1) {
                    facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, lockedTime + (language_isTR ? " Kadar Oturumunuz Kilitlendi!" : " Your session is locked!"), "");
                    FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                    captchaValue = "";
                    renderCaptcha = true;
                    return null;
                } else {
                    facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, language_isTR ? "Kullanıcı Adı veya Şifre yanlış!" : "Username or password is wrong!", "");
                    FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                    captchaValue = "";
                    renderCaptcha = true;
                    //hatalı girişapplication bean listesine eklendi
                    //addUserFailers(loginService.getUser().getUserDataLogin().getLastFailedLoginTime());
                    return null;
                }
            case -3://kullanıcı adı yok ise
                facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, language_isTR ? "Kullanıcı Adı veya Şifre yanlış!" : "Username or password is wrong!", "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                captchaValue = "";
                renderCaptcha = true;
                return null;
            case -4: // hataa
                facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, language_isTR ? "Hata !" : "Error!", "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                return null;
            default:
                break;
        }
        return null;
    }

    /**
     * ip adresinden ne kadar yanlış girildiğini kontrol eder
     *
     * @return girilen ip uzerınden son yarım saatteki yanlış giriş sayısı
     */
    public int controlIp() {
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        List<UserFailer> failers = applicationBean.getMap().get(ip);
        if (failers != null) {
            for (UserFailer failer : failers) {
                if (failer.getFailTime().after(calendar.getTime())) {//15:40
                    count++;
                }
            }
        }
        if (count >= 5) {
            count = -1;
        }
        return count;
    }

    /**
     * yanlış giriş yapılan ip bilgilerini application bean deki listeye ekler.
     *
     * @param date şimdiki zaman
     */
    public void addUserFailers(Date date) {
        List<UserFailer> userFailers = applicationBean.getMap().get(ip);
        if (userFailers == null) {
            userFailers = new ArrayList<>();
        }

        UserFailer userFailer = new UserFailer();
        userFailer.setIp(ip);
        Date date2 = date;
        //   date2.setMinutes(date2.getMinutes() + 30);
        userFailer.setFailTime(date2);
        userFailers.add(userFailer);
        applicationBean.getMap().put(ip, userFailers);
    }

    public void removeUserFailers(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -30);
        List<UserFailer> userFailers = applicationBean.getMap().get(ip);
        if (userFailers != null) {

            for (int i = 0; i < userFailers.size(); i++) {
                if (userFailers.get(i).getFailTime().before(calendar.getTime())) {
                    userFailers.remove(i);
                }
            }
        }
    }

    public void reset() {
        username = "";
        password = "";

    }
    
    public void refreshCaptcha(){
    
        System.out.println("------REFRESHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
    RequestContext.getCurrentInstance().execute("refreshCpt();");
    }

}
