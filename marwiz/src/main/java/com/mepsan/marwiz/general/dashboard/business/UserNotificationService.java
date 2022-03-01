/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:44:02
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.dashboard.dao.IUserNotificationDao;
import com.mepsan.marwiz.general.dashboard.dao.UserNotification;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class UserNotificationService implements IUserNotificationService {

    @Autowired
    private IUserNotificationDao userNotificationDao;

    @Override
    public int update(String notificationList, boolean isAllNotification) {
        return userNotificationDao.update(notificationList, isAllNotification);
    }

    @Override
    public List<UserNotification> findUserNotification(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, UserData userData) {
        return userNotificationDao.findUserNotification(first, pageSize, sortField, sortOrder, filters, where, userData);
    }

    @Override
    public int count(String where, UserData userData) {
       return userNotificationDao.count(where, userData);     
    }
}
