/**
 * Bu sınıf LoginService sınıfının veritabanı işlemlerini gerçekleştirir.
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   23.09.2016 16:07
 *
 * @edited Ali Kurt - insertFailed, captchaStatuIp ve captchaStatuUsername metotları eklendi.
 * @edited Ali Kurt - getUserLockedTime,updateUserLockedTime,captchaStatuUsername,icaptchaStatuIp ve insertUserDataLogin metotları eklendi.
 * @edited Cihat Küçükbağrıaçık - findAllLoginInfo metot eklendi.
 * @edited Mehmet Ergülcü - checkUser metodu eklendi.

 */
package com.mepsan.marwiz.general.login.dao;

import com.mepsan.marwiz.general.common.HashPassword;
import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.Arrays;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class LoginDao extends JdbcDaoSupport implements ILoginDao {

    private UserData user;

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    @Override
    public UserData findByUserName(String username, String password, UserDataLogin userDataLogin) {
        String sql = "select * from  general.get_userdata( ?,?,?,? )" ;
        Object[] param = new Object[]{username,null,null,null};
        try {
            UserData result = getJdbcTemplate().queryForObject(sql, param, new LoginMapper(password));
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

      
    }

    /**
     * eger kullanıcı hatalı giriş yaptı ise bu metot calısır. hata sayısını dondurur.
     *
     * @param username
     * @param password
     * @param userDataLogin
     * @return
     */
    @Override
    public UserData failedLogin(String username, String password, UserDataLogin userDataLogin) {
        String sql = "EXEC general.login ?,?,?,?,?,?";
        Object[] param = new Object[]{
            1,//hatalı giriş için
            username,
            userDataLogin.getIpAddress(),
            userDataLogin.getLocation(),
            userDataLogin.getBrowser(),
            userDataLogin.getDeviceType()
        };

        UserData result = getJdbcTemplate().queryForObject(sql, param, new LoginMapper(password));

        return result;
    }

    @Override
    public String passwordResetRequest(String email, String link) {
        String sql = "EXEC general.forgotpassword ?,?";
        Object[] param = new Object[]{
            email, link
        };
        String result = getJdbcTemplate().queryForObject(sql, param, String.class);

        return result;
    }

    @Override
    public boolean checkUser(String username, String password) {
        String sql
                = "SELECT\n"
                + "password\n"
                + "FROM\n"
                + "general.userdata ud\n"
                + "WHERE\n"
                + "ud.username=?\n"
                + "AND ud.deleted=FALSE\n"
                + "AND ud.status_id=1\n";
        Object[] param = new Object[]{username};
        try {
            String hashedPassword = getJdbcTemplate().queryForObject(sql, param, String.class);
            HashPassword hashPassword = new HashPassword();
            return hashPassword.passwordMatches(password, hashedPassword);
             
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
