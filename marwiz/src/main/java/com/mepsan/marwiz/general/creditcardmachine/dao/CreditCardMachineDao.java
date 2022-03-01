/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 05:07:19
 */
package com.mepsan.marwiz.general.creditcardmachine.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CreditCardMachineDao extends JdbcDaoSupport implements ICreditCardMachineDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<CreditCardMachine> listOfCreditCardMachine() {
        String sql = "SELECT \n"
                + "    ccm.id AS ccmid,\n"
                + "    ccm.name AS ccmname,\n"
                + "    ccm.code AS ccmcode,\n"
                + "    ccm.bankaccount_id AS ccmbankaccount_id,\n"
                + "    bka.name AS bkaname,\n"
                + "    ccm.status_id AS ccmstatus_id,\n"
                + "    sttd.name AS sttdname\n"
                + "FROM general.creditcardmachine ccm\n"
                + "INNER JOIN finance.bankaccount bka ON(ccm.bankaccount_id=bka.id AND bka.deleted = False)\n"
                + "INNER JOIN system.status_dict sttd ON (sttd.status_id = ccm.status_id AND sttd.language_id = ?)\n"
                + "WHERE ccm.deleted=False AND ccm.branch_id=? ";//AND bka.type_id = 16;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<CreditCardMachine> result = getJdbcTemplate().query(sql, param, new CreditCardMachineMapper());
        return result;
    }

    @Override
    public int create(CreditCardMachine obj) {
        String sql = "INSERT INTO general.creditcardmachine\n"
                + "(branch_id, name, code, status_id, bankaccount_id, c_id, u_id) \n"
                + "VALUES(?, ?, ?, ?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getCode(),
            obj.getStatus().getId(), obj.getBankAccount().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(CreditCardMachine obj) {
        String sql = "UPDATE general.creditcardmachine "
                + "SET "
                + "name = ?, "
                + "code = ? ,"
                + "status_id = ? ,"
                + "bankaccount_id = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getCode(),
            obj.getStatus().getId(), obj.getBankAccount().getId(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(CreditCardMachine obj) {
        String sql = "UPDATE general.creditcardmachine set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
