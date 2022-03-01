/**
 * Bu sınıf UserData ve UserConfig nesnesini oluşturur ve özelliklerini set eder.
 * 
 * 
 * @author Cihat Kucukbagriacik
 *
 * @date   21.09.2016 09:26:16
 */
package com.mepsan.marwiz.general.profile.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProfileMapper implements RowMapper<UserData> {

    @Override
    public UserData mapRow(ResultSet rs, int i) throws SQLException {
        UserData user = new UserData();        
        
      //  UserConfig userConfig = new UserConfig();
//        
//        userConfig.setTheme(rs.getString("theme"));
//              
//        
//        user.setUserConfig(userConfig);
//        
        
        return user;
    }       
  
}
