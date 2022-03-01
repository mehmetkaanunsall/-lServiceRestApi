/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 11:37:30
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankBranchDao extends JdbcDaoSupport implements IBankBranchDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * gelen bankanın durumu aktıf olan tum subeleri
     *
     * @param bank
     * @return
     */
    @Override
    public List<BankBranch> selectBankBranchForBank(Bank bank) {
        String sql = "SELECT\n"
                  + "	bkb.id AS bkbid,  \n"
                  + "	bkb.name AS bkbname,\n"
                  + "	bkb.code AS bkbcode\n"
                  + "FROM finance.bankbranch bkb \n"
                  + "where bkb.deleted=FALSE AND bkb.status_id=19 AND bkb.bank_id=?";
        Object[] param = new Object[]{bank.getId()};
        return getJdbcTemplate().query(sql, param, new BankBranchMapper());
    }

    @Override
    public int create(BankBranch obj) {
        String sql = "INSERT INTO finance.bankbranch (bank_id,name,code,status_id,phone,email,address,county_id,city_id,country_id,c_id) "
                  + "VALUES(?,?,?,?,?,?,?,?,?,?,?) RETURNING id ;";
        Object[] param = new Object[]{obj.getBank().getId(), obj.getName(), obj.getCode(), obj.getStatus().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(), obj.getCounty().getId(), obj.getCity().getId(), obj.getCountry().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(BankBranch obj) {
        String sql = "UPDATE finance.bankbranch "
                  + "SET "
                  + "bank_id= ? ,"
                  + "name= ?, "
                  + "code= ? ,"
                  + "status_id= ? ,"
                  + "phone= ? ,"
                  + "email= ? ,"
                  + "address= ? ,"
                  + "county_id= ? ,"
                  + "city_id= ? ,"
                  + "country_id= ? ,"
                  + "u_id= ? ,"
                  + "u_time= now() "
                  + "WHERE id= ? ";
        Object[] param = new Object[]{obj.getBank().getId(), obj.getName(), obj.getCode(), obj.getStatus().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(), obj.getCounty().getId(), obj.getCity().getId(), obj.getCountry().getId(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<BankBranch> selectBankBranch() {
        String sql = "SELECT\n"
                  + "     b.name as bname,"
                  + "	bkb.id AS bkbid,  \n"
                  + "	bkb.name AS bkbname,\n"
                  + "	bkb.code AS bkbcode\n"
                  + "FROM finance.bankbranch bkb \n"
                  + "INNER JOIN finance.bank b ON (b.id = bkb.bank_id AND b.deleted=FALSE)"
                  + "WHERE bkb.deleted=FALSE "
                  + "AND bkb.status_id=19 ";
        return getJdbcTemplate().query(sql, new BankBranchMapper());
    }

    @Override
    public int testBeforeDelete(BankBranch bankBranch) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT bankbranch_id FROM finance.bankaccount WHERE bankbranch_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{bankBranch.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(BankBranch bankBranch) {
        String sql = "UPDATE finance.bankbranch SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                  + "UPDATE general.address SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=FALSE AND responsible_id IN (SELECT rsp.id FROM general.responsible rsp WHERE rsp.deleted=FALSE AND rsp.bankbranch_id = ?);\n"
                  + "UPDATE general.phone SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=FALSE AND responsible_id IN (SELECT rsp.id FROM general.responsible rsp WHERE rsp.deleted=FALSE AND rsp.bankbranch_id = ?);\n"
                  + "UPDATE general.internet SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=FALSE AND responsible_id IN (SELECT rsp.id FROM general.responsible rsp WHERE rsp.deleted=FALSE AND rsp.bankbranch_id = ?);\n"
                  + "UPDATE general.responsible SET deleted=TRUE ,u_id=? , d_time=NOW() WHERE deleted=FALSE AND bankbranch_id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), bankBranch.getId(),
            sessionBean.getUser().getId(), bankBranch.getId(), sessionBean.getUser().getId(), bankBranch.getId(),
            sessionBean.getUser().getId(), bankBranch.getId(), sessionBean.getUser().getId(), bankBranch.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
