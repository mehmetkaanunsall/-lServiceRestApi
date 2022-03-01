/**
 * Bu sınıf, LoginDao sınıfına arayüz oluşturur.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   4.08.2016 10:01:16
 * 
 * @edited Ali Kurt - findAllLoginInfo metotları eklendi.
 * @edited Zafer Yaşar - findalllogininfo geriye dönüş tipi değişti.
 */
package com.mepsan.marwiz.general.login.dao;

import com.mepsan.marwiz.general.model.log_general.UserDataLogin;
import com.mepsan.marwiz.general.model.general.UserData;

public interface ILoginDao {

    public UserData findByUserName(String username,String password,UserDataLogin userDataLogin);
    
    public UserData failedLogin(String username,String password,UserDataLogin userDataLogin);
    
    public String passwordResetRequest(String email,String link);
    
    public boolean checkUser(String username,String password);
    
}
