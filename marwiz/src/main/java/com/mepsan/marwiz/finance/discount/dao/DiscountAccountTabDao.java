/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:22:52
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DiscountAccountTabDao extends JdbcDaoSupport implements IDiscountAccountTabDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DiscountAccountConnection> listofDiscountAccount(Discount obj, String where) {
        String sql = "SELECT \n"
                + "    dac.id AS dacid,\n"
                + "    dac.discount_id AS dacdiscount_id,\n"
                + "    dac.account_id AS dacaccount_id,\n"
                + "    acc.name AS accname,\n"
                + "    dac.accountcategorization_id AS dacaccountcategorization_id, \n"
                + "    ctg.name AS  ctgname , \n"
                + "    usr.username as usrusername, \n"
                + "    dac.c_time as dacc_time,\n"
                + "    usr.name AS usrname,\n"
                + "    usr.surname AS usrsurname \n"
                + "FROM  \n"
                + "    finance.discount_account_con dac\n"
                + "    LEFT JOIN general.account acc ON(acc.id = dac.account_id AND acc.deleted = FALSE)\n"
                + "    LEFT JOIN general.categorization ctg ON(ctg.id = dac.accountcategorization_id AND ctg.deleted = FALSE)\n"
                + "    INNER JOIN general.userdata usr   ON (usr.id = dac.c_id) \n"
                + "WHERE\n"
                + "	dac.deleted = FALSE AND dac.discount_id = ?    " + where;
        Object[] param = new Object[]{obj.getId()};
        List<DiscountAccountConnection> result = getJdbcTemplate().query(sql, param, new DiscountAccountTabMapper());
        return result;
    }

    @Override
    public int create(DiscountAccountConnection obj) {
        String sql = "INSERT INTO \n"
                + "  finance.discount_account_con\n"
                + "  (\n"
                + "    discount_id,\n"
                + "    account_id,\n"
                + "    accountcategorization_id,\n"
                + "    c_id,\n"
                + "    u_id\n"
                + "  )\n"
                + "  VALUES (?,  ?,  ?,  ?,  ?) RETURNING id;\n";

        Object[] param = new Object[]{(obj.getDiscount().getId() == 0 ? null : obj.getDiscount().getId()), (obj.getAccount().getId() == 0 ? null : obj.getAccount().getId()), (obj.getAccountCategorization().getId() == 0 ? null : obj.getAccountCategorization().getId()), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(DiscountAccountConnection obj) {
        String sql = "UPDATE \n"
                + "  finance.discount_account_con \n"
                + "SET \n"
                + "  account_id = ?,\n"
                + "  accountcategorization_id = ?,\n"
                + "  u_id = ?,\n"
                + "  u_time = NOW()\n"
                + "WHERE \n"
                + "  id = ?; \n";

        Object[] param = new Object[]{(obj.getAccount().getId() == 0 ? null : obj.getAccount().getId()), (obj.getAccountCategorization().getId() == 0 ? null : obj.getAccountCategorization().getId()), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(DiscountAccountConnection obj) {
        String sql = "\n";

        Object[] param = new Object[]{obj.getId(), obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(DiscountAccountConnection obj) {
        String sql = "UPDATE finance.discount_account_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
