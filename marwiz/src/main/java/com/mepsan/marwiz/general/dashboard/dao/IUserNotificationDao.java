/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:43:37
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import java.util.Map;

public interface IUserNotificationDao {

    public List<UserNotification> findUserNotification(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, UserData userData);

    public int count(String where, UserData userData);

    public int update(String notificationList, boolean isAllNotification);

}
