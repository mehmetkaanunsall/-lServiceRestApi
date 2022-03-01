/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:43:55
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.dashboard.dao.UserNotification;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import java.util.Map;

public interface IUserNotificationService {

    public List<UserNotification> findUserNotification(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, UserData userData);

    public int count(String where, UserData userData);

    public int update(String notificationList, boolean isAllNotification);

}
