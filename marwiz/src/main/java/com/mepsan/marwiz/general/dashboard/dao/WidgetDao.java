/**
 * Bu sınıf Widget tablosunda updated, created, deleted ve listing işlemlerini yapar.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   07.09.2016 09:55
 * @edited Zafer Yaşar - insertUsersDashboard ,deleteUsersDashboard
 * ,findAllWotUserDashboard ,updateUsersDashboard ,findAllUsersDashboard
 * metotları eklendi.
 * @edited Zafer Yaşar - deleted ve aliaslar eklendi.
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.Widget;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WidgetDao extends JdbcDaoSupport implements IWidgetDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void test() {
        System.out.println("test");
    }

    @Override
    public List<Widget> findAll() {
        String sql = "SELECT \n"
                + "	wd.id,\n"
                + "	wd.name \n"
                + "FROM general.widget wd\n"
                + "WHERE wd.deleted=False\n"
                + " ORDER BY wd.id";

        List<Widget> dashboard = getJdbcTemplate().query(sql, new WidgetMapper());
        return dashboard;
    }

    @Override
    public int create(Widget obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Widget obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Widget find(Widget obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
