/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 08:08:38
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DiscountBranchTabDao extends JdbcDaoSupport implements IDiscountBranchTabDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DiscountBranchConnection> listOfDiscountBranch(Discount obj) {
        String sql = "SELECT \n"
                + "    dbc.id AS dbcid,\n"
                + "    dbc.discount_id AS dbcdiscount_id,\n"
                + "    dsc.name  AS dscname,\n"
                + "    dbc.branch_id AS dbcbranch_id,\n"
                + "    br.name AS brname, \n"
                + "    usr.username as usrusername, \n"
                + "    dbc.c_time as dbcc_time,\n"
                + "    usr.name AS usrname,\n"
                + "    usr.surname AS usrsurname \n"
                + "FROM \n"
                + "  	finance.discount_branch_con dbc\n"
                + "	INNER JOIN finance.discount dsc ON(dsc.id = dbc.discount_id AND dsc.deleted = FALSE)\n"
                + "    INNER JOIN general.branch br ON(br.id = dbc.branch_id AND br.deleted = FALSE)\n"
                + "    INNER JOIN general.userdata usr   ON (usr.id = dbc.c_id) \n"
                + "WHERE\n"
                + "	dbc.deleted = FALSE AND dbc.discount_id = ?   ";
        Object[] param = new Object[]{obj.getId()};
        List<DiscountBranchConnection> result = getJdbcTemplate().query(sql, param, new DiscountBranchTabMapper());
        return result;
    }

    @Override
    public int create(DiscountBranchConnection obj) {
        String sql = "   \n"
                + "INSERT INTO \n"
                + "finance.discount_branch_con\n"
                + "(\n"
                + "  discount_id,\n"
                + "  branch_id,\n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES ( ?,?,?,?) RETURNING id;\n";

        Object[] param = new Object[]{(obj.getDiscount().getId() == 0 ? null : obj.getDiscount().getId()), (obj.getBranch().getId() == 0 ? null : obj.getBranch().getId()), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(DiscountBranchConnection obj) {
        String sql = "UPDATE \n"
                + "  finance.discount_branch_con \n"
                + "SET \n"
                + "  discount_id = ?,\n"
                + "  branch_id = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = now()\n"
                + "WHERE \n"
                + "  id = ?; \n";

        Object[] param = new Object[]{(obj.getDiscount().getId() == 0 ? null : obj.getDiscount().getId()), (obj.getBranch().getId() == 0 ? null : obj.getBranch().getId()), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(DiscountBranchConnection obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(DiscountBranchConnection obj) {
    String sql = "UPDATE finance.discount_branch_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }    }

}
