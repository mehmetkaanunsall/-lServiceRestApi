/**
 * Bu sınıf, UserData ve UserConfig tablosunda updated ve listing işlemlerini yapar.
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date   21.09.2016 09:34:38
 */
package com.mepsan.marwiz.general.profile.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ProfileDao extends JdbcDaoSupport implements IProfileDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Bu metot, kullanıcının theme seçimini günceller.
     *
     * @param themeName temanın adını barındırır.
     * @return 
     */
    @Override
    public int themeChange(String themeName) {
        String sql = "UPDATE \n"
                + "  general.userdata  \n"
                + "SET \n"
                + "  lasttheme = ?,\n"
                + "  u_id = ? ,\n"
                + "  u_time = now()"
                + "WHERE \n"
                + "  id = ? \n"
                + " AND deleted = false ";


        Object[] param = new Object[]{
            themeName,
            sessionBean.getUser().getId(),
            sessionBean.getUser().getId()
        };

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
