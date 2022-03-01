/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:56:58
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.core.RowMapper;

public class UserNotificationMapper implements RowMapper<UserNotification> {
    
    @Override
    public UserNotification mapRow(ResultSet rs, int i) throws SQLException {
        UserNotification userNotification = new UserNotification();
        userNotification.setDescription(rs.getString("ntfdescription"));
        userNotification.setId(rs.getInt("usnfid"));
        userNotification.setCenterWarningTypeId(rs.getInt("ntfcenterwarningtype_id"));
        userNotification.setTypeId(rs.getInt("ntftype_id"));
        userNotification.setIsCenter(rs.getBoolean("ntfis_center"));
        Gson gson = new Gson();
        
        Type modelClassType = new TypeToken<ArrayList<NotificationRecommendedPrice>>() {
        }.getType();
        userNotification.getListOfNotification().addAll(gson.fromJson(userNotification.getDescription(), modelClassType));
        
        
        
        
        return userNotification;
    }
    
}
