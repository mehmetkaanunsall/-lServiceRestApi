/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.11.2019 08:38:14
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBank;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountBankDao extends JdbcDaoSupport implements IAccountBankDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AccountBank> findAccountBank(Account account) {
        String sql = "SELECT\n"
                  + "	 accb.id AS accbid,\n"
                  + "    accb.name AS accbname,\n"
                  + "    accb.accountnumber AS accbaccountnumber,\n"
                  + "    accb.ibannumber AS accbibannumber,\n"
                  + "    accb.currency_id AS accbcurrency_id,\n"
                  + "    crrd.name AS crrdname,\n"
                  + "    accb.type_id AS accbtype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    accb.status_id AS accbstatus_id,\n"
                  + "    sttd.name AS sttdname,\n"
                  + "    accb.balance AS accbbalance,\n"
                  + "    bkb.code AS bkbcode,\n"
                  + "    bkb.bank_id AS bkbbank_id,\n"
                  + "    bnk.name AS bnkname,\n"
                  + "    accb.bankbranch_id AS accbbankbranch_id,\n"
                  + "    bkb.name AS bkbname\n"
                  + "FROM\n"
                  + "	general.accountbank accb \n"
                  + "    INNER JOIN finance.bankbranch bkb ON (bkb.id = accb.bankbranch_id AND bkb.deleted = False)\n"
                  + "    INNER JOIN finance.bank bnk ON (bnk.id = bkb.bank_id AND bnk.deleted = False)\n"
                  + "    INNER JOIN system.currency_dict crrd ON (crrd.currency_id = accb.currency_id AND crrd.language_id = ?)\n"
                  + "    INNER JOIN system.type_dict typd ON (typd.type_id = accb.type_id AND typd.language_id = ?)\n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = accb.status_id AND sttd.language_id = ?)\n"
                  + "WHERE accb.deleted=FALSE AND accb.account_id =?\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(),
            account.getId()};

        List<AccountBank> result = getJdbcTemplate().query(sql, param, new AccountBankMapper());
        return result;
    }

    @Override
    public int create(AccountBank obj) {
        String sql = "INSERT INTO general.accountbank (account_id, bankbranch_id, name, accountnumber, ibannumber, status_id, type_id, currency_id, balance, c_id, u_id)\n"
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

        Object[] param = new Object[]{obj.getAccount().getId(), obj.getBankBranch().getId(), obj.getName(),
            obj.getAccountNumber(), obj.getIbanNumber(), obj.getStatus().getId(), obj.getType().getId(), obj.getCurrency().getId(), obj.getBalance(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(AccountBank obj) {
        String sql = "UPDATE \n"
                  + "	general.accountbank \n"
                  + "SET \n"
                  + "	 bankbranch_id = ?,\n"
                  + "	 name = ?,\n"
                  + "	 accountnumber = ?,\n"
                  + "	 ibannumber = ?,\n"
                  + "	 status_id = ?,\n"
                  + "	 type_id = ?,\n"
                  + "	 currency_id = ?,\n"
                  + "	 balance = ?,\n"
                  + "    u_id = ?,\n"
                  + "    u_time = NOW()\n"
                  + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getBankBranch().getId(), obj.getName(), obj.getAccountNumber(), obj.getIbanNumber(), obj.getStatus().getId(), obj.getType().getId(),
            obj.getCurrency().getId(), obj.getBalance(),
            sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(AccountBank obj) {
        String sql = "UPDATE general.accountbank SET deleted = TRUE, u_id = ?, d_time = NOW() WHERE id= ? AND deleted=FALSE;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
