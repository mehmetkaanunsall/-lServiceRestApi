/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 11:07:25
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountNote;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountNoteDao extends JdbcDaoSupport implements IAccountNoteDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AccountNote> findAccountNote(Account account) {
        String sql = "SELECT \n"
                  + "	 accnt.id AS accntid,\n"
                  + "    accnt.name AS accntname,\n"
                  + "    accnt.description AS accntdescription\n"
                  + "FROM\n"
                  + "	general.accountnote accnt \n"
                  + "WHERE accnt.deleted=FALSE AND accnt.account_id =?";

        Object[] param = new Object[]{account.getId()};

        List<AccountNote> result = getJdbcTemplate().query(sql, param, new AccountNoteMapper());
        return result;
    }

    @Override
    public int create(AccountNote obj) {
        String sql = "INSERT INTO general.accountnote (account_id, name, description, c_id, u_id) VALUES (?, ?, ?, ?, ?) RETURNING id;";

        Object[] param = new Object[]{obj.getAccount().getId(), obj.getName(), obj.getDescription(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AccountNote obj) {
        String sql = "UPDATE \n"
                  + "	general.accountnote \n"
                  + "SET \n"
                  + "	 name = ?,\n"
                  + "	 description = ?,\n"
                  + "    u_id = ?,\n"
                  + "    u_time = NOW()\n"
                  + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getName(), obj.getDescription(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(AccountNote obj) {
        String sql = "UPDATE general.accountnote SET deleted = TRUE, u_id = ?, d_time = NOW() WHERE id= ? AND deleted=FALSE;";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
