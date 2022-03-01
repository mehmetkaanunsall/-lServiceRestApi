/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.09.2018 09:43:21
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserNotificationDao extends JdbcDaoSupport implements IUserNotificationDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int update(String notificationList, boolean isAllNotification) {
        String sql = "";
        Object[] param = null;

        if (isAllNotification) {
            sql = "     UPDATE general.userdata_notification_con ll\n"
                    + "                SET is_read= True,\n"
                    + "                u_id = ?,\n"
                    + "                u_time = now(),\n"
                    + "                readdate = now()\n"
                    + "                FROM\n"
                    + "                (\n"
                    + "                	SELECT \n"
                    + "                    	 \n"
                    + "                        usnf2.id,\n"
                    + "                        usnf2.is_read\n"
                    + "                	FROM \n"
                    + "                    	general.userdata_notification_con usnf2\n"
                    + "                		INNER JOIN general.notification ntf  ON(ntf.id=usnf2.notification_id AND ntf.deleted=False)\n"
                    + "                	WHERE \n"
                    + "                    	usnf2.deleted=False \n"
                    + "                        AND usnf2.is_read=False \n"
                    + "                        AND ntf.branch_id=? \n"
                    + "                        AND usnf2.userdata_id=?\n"
                    + "                )kk\n"
                    + "                WHERE \n"
                    + "                ll.id = kk. id;";
            param = new Object[]{sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};

        } else {
            sql = "UPDATE general.userdata_notification_con\n"
                    + "SET is_read= True,\n"
                    + "u_id = ?,\n"
                    + "u_time = now(),\n"
                    + "readdate = now()\n"
                    + "WHERE id IN(" + notificationList + ")";
            param = new Object[]{sessionBean.getUser().getId()};
        }

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<UserNotification> findUserNotification(int first, int pageSize, String sortField,
            String sortOrder, Map<String, Object> filters,
            String where, UserData userData
    ) {
        String sql = "";
        Object[] param = null;

        sql = "SELECT\n"
                + "     usnf.id AS usnfid,\n"
                + "     ntf.description AS ntfdescription,\n"
                + "     ntf.centerwarningtype_id AS ntfcenterwarningtype_id,\n"
                + "     ntf.is_center AS ntfis_center,\n"
                + "     ntf.type_id AS ntftype_id\n"
                + "FROM general.userdata_notification_con usnf \n"
                + "	INNER JOIN general.notification ntf  ON(ntf.id=usnf.notification_id AND ntf.deleted=False)\n"
                + "WHERE usnf.deleted=False AND usnf.is_read=False AND ntf.branch_id=? AND usnf.userdata_id=?\n"
                + "ORDER BY usnf.id DESC\n"
                + " limit " + pageSize + " offset " + first;
        param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};
        List<UserNotification> widgetUserDataCon = getJdbcTemplate().query(sql, param, new UserNotificationMapper());
        return widgetUserDataCon;
    }

    @Override
    public int count(String where, UserData userData
    ) {
        String sql = "";
        Object[] param = null;

        sql = "SELECT\n"
                + "    count(DISTINCT  usnf.id ) as usnfidcount\n"
                + "FROM general.userdata_notification_con usnf \n"
                + "	INNER JOIN general.notification ntf  ON(ntf.id=usnf.notification_id AND ntf.deleted=False)\n"
                + "WHERE usnf.deleted=False AND usnf.is_read=False AND ntf.branch_id=? AND usnf.userdata_id=?";
        param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

}
