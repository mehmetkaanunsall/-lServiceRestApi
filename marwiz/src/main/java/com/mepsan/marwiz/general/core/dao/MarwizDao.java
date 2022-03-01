/**
 * This class ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 05:26:56
 */
package com.mepsan.marwiz.general.core.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.login.dao.LoginMapper;
import com.mepsan.marwiz.general.model.general.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarwizDao extends JdbcDaoSupport implements IMarwizDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    
    
    @Override
    public UserData updateBranch(String username, int groupBranchId) {
       String sql = "select * from  general.get_userdata( ?,?,?,? )" ;
        Object[] param = new Object[]{username,true,sessionBean.getUser().getId(),groupBranchId};
        UserData result = getJdbcTemplate().queryForObject(sql, param, new LoginMapper());
        return result;
    }

}
