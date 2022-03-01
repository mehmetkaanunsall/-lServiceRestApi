/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 12:57:47
 */
package com.mepsan.marwiz.system.authorize.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AuthorizeDao extends JdbcDaoSupport implements IAuthorizeDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Authorize> findAll() {
        String sql = "select \n"
                + "auth.id as authid,\n"
                + "auth.name as authname, \n"
                + "auth.modules as authmodules, \n"
                + "auth.folders as authfolders, \n"
                + "auth.pages as authpages, \n"
                + "auth.tabs as authtabs, \n"
                + "auth.buttons as authbuttons, \n"
                + "auth.is_admin as authis_admin,\n"
                + "auth.c_time AS authc_time, \n"
                + "usd.name as usdname,\n"
                + "usd.surname as usdsurname,\n"
                + "auth.c_id AS authc_id \n"
                + "from general.authorize auth \n "
                + "LEFT JOIN general.userdata usd ON(usd.id=auth.c_id)\n"
                + "where auth.deleted=false and auth.branch_id=? \n"
                + "ORDER BY auth.id ";

        Object param[] = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<Authorize> result = getJdbcTemplate().query(sql, param, new AuthorizeMapper());
        return result;
    }

    @Override
    public int create(Authorize obj) {
        String sql = "INSERT INTO general.authorize (name,branch_id,c_id,u_id) VALUES (?,?,?,?) RETURNING id;";

        Object param[] = new Object[]{obj.getName(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Authorize obj) {
        String sql = "UPDATE general.authorize SET\n"
                + "name=?,\n"
                + "u_id=?,\n"
                + "u_time= now()\n"
                + "WHERE id=?";

        Object param[] = new Object[]{obj.getName(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<Authorize> selectAuthorize() {
        String sql = "select \n"
                + "auth.id as authid,\n"
                + "auth.name as authname \n"
                + "from general.authorize auth \n"
                + "where auth.deleted=false and auth.branch_id=?";

        Object param[] = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<Authorize> result = getJdbcTemplate().query(sql, param, new AuthorizeMapper());
        return result;
    }

    @Override
    public int updateModuleTab(Authorize authorize) {
        String sql = "UPDATE \n"
                + "  general.authorize  \n"
                + "SET \n"
                + "  modules = ? ,\n"
                + "  u_id = ? ,\n"
                + "  u_time=NOW()\n"
                + "WHERE \n"
                + "  id = ? and deleted=false ";
        Object[] param = new Object[]{
            authorize.getListOfModules().size() == 0 ? null : authorize.getListOfModules().toString().substring(1, authorize.getListOfModules().toString().length() - 1).replaceAll("\\s+", ""),
            sessionBean.getUser().getId(),
            authorize.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updatePageTab(Authorize authorize) {

        String sql = "UPDATE \n"
                + "  general.authorize  \n"
                + "SET \n"
                + "  folders = ? ,\n"
                + "  pages = ? ,\n"
                + "  tabs = ? ,\n"
                + "  buttons = ? ,\n"
                + "  u_id = ? ,\n"
                + "  u_time=NOW()\n"
                + "WHERE \n"
                + "  id = ? and deleted = false ";
        Object[] param = new Object[]{
            authorize.getListOfFolders().size() == 0 ? null : (authorize.getListOfFolders().toString().substring(1, authorize.getListOfFolders().toString().length() - 1).replaceAll("\\s+", "")),
            authorize.getListOfPages().size() == 0 ? null : (authorize.getListOfPages().toString().substring(1, authorize.getListOfPages().toString().length() - 1).replaceAll("\\s+", "")),
            authorize.getListOfTabs().size() == 0 ? null : (authorize.getListOfTabs().toString().substring(1, authorize.getListOfTabs().toString().length() - 1).replaceAll("\\s+", "")),
            authorize.getListOfButtons().size() == 0 ? null : (authorize.getListOfButtons().toString().substring(1, authorize.getListOfButtons().toString().length() - 1).replaceAll("\\s+", "")),
            sessionBean.getUser().getId(),
            authorize.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Authorize> selectAuthorizeToTheBranch(Branch branch) {

        String sql = "select \n"
                + "auth.id as authid,\n"
                + "auth.name as authname \n"
                + "from general.authorize auth \n"
                + "where auth.deleted=false and auth.branch_id=?";

        Object param[] = new Object[]{branch.getId()};
        List<Authorize> result = getJdbcTemplate().query(sql, param, new AuthorizeMapper());
        return result;

    }

    @Override
    public int delete(Authorize authorize) {
        String sql = "UPDATE general.authorize set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), authorize.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Authorize authorize) {

        String sql = "SELECT CASE WHEN EXISTS (SELECT id FROM general.userdata_authorize_con WHERE authorize_id=? AND deleted=FALSE) THEN 1 ELSE 0 END ;";

        Object param[] = new Object[]{authorize.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
