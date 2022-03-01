/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.03.2021 02:04:44
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BranchIntegrationDao extends JdbcDaoSupport implements IBranchIntegrationDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    @Override
    public List<BranchIntegration> listOfIntegration(Branch obj) {
        String sql = "SELECT \n"
                + "brint.name as brintname,\n"
                + "brint.id as brintid, \n"
                + "brint.type_id as brinttype_id,\n"
                + "brint.password1 as brintpassword,\n"
                + "brint.parameter1 as brintparameter,\n"
                + "brint.username1 as  brintusername,\n"
                + "brint.host1 as brinthost,\n"
                + "brint.description as brintdescription,\n"
                + "brint.host2 as brinthost2,\n"
                + "brint.parameter2 as brintparameter2,\n"
                + "brint.parameter3 as brintparameter3,\n"
                + "brint.parameter4 as brintparameter4,\n"
                + "brint.parameter5 as brintparameter5,\n"
                + "brint.password2 as brintpassword2,\n"
                + "brint.timeout1 as brinttimeout,\n"
                + "brint.timeout2 as brinttimeout2,\n"
                + "brint.username2 as brintusername2,\n"
                + "brint.branch_id as brintbranch_id\n"
                + "FROM general.branchintegration brint\n"
                + "WHERE brint.deleted = FALSE AND brint.branch_id = ?  \n"
                + "\n";

        Object[] param = new Object[]{obj.getId()};
        return getJdbcTemplate().query(sql, param, new BranchIntegrationMapper());
    }

    @Override
    public int create(BranchIntegration obj) {
        String sql = "INSERT INTO \n"
                + "general.branchintegration (\n"
                + "branch_id,name,type_id,description,host1,host2,username1,username2,password1,password2,parameter1,parameter2,parameter3,\n"
                + "parameter4,parameter5,timeout1,timeout2,c_id,u_id\n"
                + ")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";

        Object[] param = new Object[]{obj.getBranch().getId(),obj.getName(), obj.getIntegrationtype(), obj.getDescription(), obj.getHost1(), obj.getHost2(), obj.getUsername1(), obj.getUsername2(), obj.getPassword1(),
            obj.getPassword2(), obj.getParameter1(), obj.getParameter2(), obj.getParameter3(), obj.getParameter4(), obj.getParameter5(), obj.getTimeout1(), obj.getTimeout2(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(BranchIntegration obj) {
        String sql = "UPDATE general.branchintegration \n"
                + "SET \n"
                + "name = ?,\n"
                + "type_id = ?,\n"
                + "description = ?,\n"
                + "host1= ?,\n"
                + "host2 = ?,\n"
                + "username1 = ?,\n"
                + "username2 = ?,\n"
                + "password1 = ?,\n"
                + "password2 = ?,\n"
                + "parameter1 = ?,\n"
                + "parameter2 = ?,\n"
                + "parameter3 = ?,\n"
                + "parameter4 = ?,\n"
                + "parameter5 = ?,\n"
                + "timeout1 = ?,\n"
                + "timeout2 = ?,\n"
                + "u_id = ?,\n"
                + "u_time = now()\n"
                + "WHERE id = ? AND deleted = FALSE";

        Object[] param = new Object[]{obj.getName(), obj.getIntegrationtype(), obj.getDescription(), obj.getHost1(), obj.getHost2(), obj.getUsername1(), obj.getUsername2(), obj.getPassword1(),
            obj.getPassword2(), obj.getParameter1(), obj.getParameter2(), obj.getParameter3(), obj.getParameter4(), obj.getParameter5(), obj.getTimeout1(), obj.getTimeout2(),
            sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int delete(BranchIntegration obj) {

        String sql = "UPDATE general.branchintegration SET deleted= TRUE, u_id=?,d_time = now() WHERE deleted=FALSE AND id=?";
        
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public BranchIntegration findBranchIntegration() {
        String sql
                  = "SELECT\n"
                  + "	 brint.host1 AS brinthost,\n"
                  + "    brint.parameter1 AS brintparameter,\n"
                  + "    brint.username1 AS brintusername,\n"
                  + "    brint.password1 AS brintpassword,\n"
                  + "    brint.branch_id AS brintbranch_id,\n"
                  + "    brint.timeout1 AS brinttimeout\n"
                  + "FROM general.branchintegration brint \n"
                  + "WHERE brint.deleted=FALSE AND brint.type_id = 1 AND brint.branch_id=? LIMIT 1\n";
        
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<BranchIntegration> result = getJdbcTemplate().query(sql, param, new BranchIntegrationMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BranchIntegration();
        }
    }

}
