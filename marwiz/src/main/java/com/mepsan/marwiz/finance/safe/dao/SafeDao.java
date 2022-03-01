/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 10:06:00
 */
package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SafeDao extends JdbcDaoSupport implements ISafeDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Safe> findAll() {
        String sql = "SELECT  \n"
                  + "    sf.id AS sfid,  \n"
                  + "    sf.name AS sfname,  \n"
                  + "    sf.code AS sfcode,  \n"
                  + "    sf.currency_id AS sfcurrency_id,  \n"
                  + "    crrd.name AS crrdname,  \n"
                  + "    crr.sign AS crrsign,  \n"
                  + "    crr.code as crrcode,  \n"
                  + "    sf.status_id AS sfstatus_id,  \n"
                  + "    sf.is_mposmovement as sfis_mposmovement, \n"
                  + "    sf.balance AS sfbalance,  \n"
                  + "    sttd.name AS sttdname,  "
                  + "    sf.shiftmovementsafe_id as sfshiftmovsf_id \n"
                  + "FROM  \n"
                  + "    finance.safe sf\n"
                  + "    INNER JOIN system.currency crr ON (crr.id = sf.currency_id)  \n"
                  + "    INNER JOIN system.currency_dict crrd ON (crrd.currency_id = sf.currency_id AND crrd.language_id = ?)  \n"
                  + "    INNER JOIN system.status_dict sttd  ON (sttd.status_id = sf.status_id AND sttd.language_id = ?)  \n"
                  + "WHERE      \n"
                  + "     sf.deleted = false and sf.branch_id = ? ";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;
    }

    @Override
    public int create(Safe obj) {
        String sql = "SELECT r_safe_id FROM finance.insert_safe (?, ? , ? , ? , ? , ? , ? , ? , ? , ? , ?,?,?);";

        int a = obj.getBalance().compareTo(BigDecimal.valueOf(0));
        Object[] param = new Object[]{null, obj.getName(), obj.getCode(),
            obj.getCurrency().getId(), a == 1 ? obj.getBalance() : obj.getBalance().multiply(BigDecimal.valueOf(-1)), obj.getStatus().getId(),
            sessionBean.getUser().getLastBranch().getId(), a == 1 ? true : false, new Date(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getShiftmovementsafe_id() == 0 ? null : obj.getShiftmovementsafe_id(), obj.isIsMposMovement()};

        // System.out.println("---Arrays--" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Safe obj) {
        String sql = "UPDATE finance.safe \n"
                  + "   SET name = ? ,code = ?, currency_id = ?, status_id = ?, u_id = ?, u_time = NOW(),shiftmovementsafe_id = ?,is_mposmovement = ? \n"
                  + "   WHERE id = ?";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), obj.getCurrency().getId(), obj.getStatus().getId(),
            sessionBean.getUser().getId(), obj.getShiftmovementsafe_id() == 0 ? null : obj.getShiftmovementsafe_id(), obj.isIsMposMovement(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Safe> selectSafe() {
        String sql = "   \n"
                  + "   SELECT \n"
                  + "       sf.id AS sfid, \n"
                  + "       sf.currency_id AS sfcurrency_id,  \n"
                  + "       crr.code AS crrcode,  \n"
                  + "       crrd.name AS crrdname,  \n"
                  + "       sf.name AS sfname, "
                  + "       sf.shiftmovementsafe_id as sfshiftmovsf_id,\n"
                  + "       sf.is_mposmovement as sfis_mposmovement \n"
                  + "   FROM \n"
                  + "   finance.safe sf  \n"
                  + "   INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=sf.currency_id AND crrd.language_id=?)\n"
                  + "   INNER JOIN system.currency crr  ON (crr.id=sf.currency_id)\n"
                  + "   WHERE sf.deleted = false AND sf.status_id=23 AND sf.branch_id = ?";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;
    }

    /**
     * POS Tanımları sayfasında şubenin para birimine göre kasa seçme işlemi
     * için kullanılır.
     *
     * @param where
     * @return
     */
    @Override
    public List<Safe> findSafeByCurrency(String where) {
        String sql = "SELECT \n"
                  + "   sf.id AS sfid,\n"
                  + "   sf.name AS sfname,\n"
                  + "   sf.code AS sfcode,\n"
                  + "   sf.currency_id AS sfcurrency_id,\n"
                  + "   crr.code AS crrcode,\n"
                  + "   crrd.name AS crrdname,\n"
                  + "   sf.shiftmovementsafe_id as sfshiftmovsf_id,\n"
                  + "   sf.is_mposmovement as sfis_mposmovement \n"
                  + "FROM finance.safe sf\n"
                  + "   INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=sf.currency_id AND crrd.language_id=?)\n"
                  + "   INNER JOIN system.currency crr  ON (crr.id=sf.currency_id)\n"
                  + "WHERE sf.deleted=False AND sf.branch_id=?\n"
                  + where;

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;
    }

    @Override
    public int delete(Safe safe) {
        String sql = "UPDATE finance.safe set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                  + "UPDATE finance.safemovement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND safe_id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), safe.getId(), sessionBean.getUser().getId(), safe.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Safe> findSafeBalanceForDate(Safe safe) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                  + "COALESCE(SUM(CASE WHEN sfm.is_direction THEN 1 ELSE -1 END * COALESCE(sfm.price,0)),0)  as balance\n"
                  + "FROM finance.safe sf \n"
                  + "INNER JOIN finance.safemovement sfm ON(sfm.safe_id=sf.id AND sfm.deleted=FALSE)\n"
                  + "WHERE sf.deleted=FALSE AND sf.branch_id=?\n"
                  + "AND sf.id=? AND sfm.movementdate <= '" + sd.format(safe.getReportDate()) + "' ";

        Object[] param = {sessionBean.getUser().getLastBranch().getId(), safe.getId()};

        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;

    }

    @Override
    public List<Safe> selectSafe(Branch branch) {
        String sql = "   \n"
                  + "   SELECT \n"
                  + "       sf.id AS sfid, \n"
                  + "       sf.currency_id AS sfcurrency_id,  \n"
                  + "       crr.code AS crrcode,  \n"
                  + "       crrd.name AS crrdname,  \n"
                  + "       sf.name AS sfname, "
                  + "       sf.shiftmovementsafe_id as sfshiftmovsf_id,\n"
                  + "       sf.is_mposmovement as sfis_mposmovement, \n"
                  + "       sf.branch_id AS sfbranch_id\n"
                  + "   FROM \n"
                  + "   finance.safe sf  \n"
                  + "   INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=sf.currency_id AND crrd.language_id=?)\n"
                  + "   INNER JOIN system.currency crr  ON (crr.id=sf.currency_id)\n"
                  + "   WHERE sf.deleted = false AND sf.status_id=23 AND sf.branch_id = ?";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), branch.getId()};
        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;
    }

    @Override
    public List<Safe> selectSafe(List<Branch> branchList) {
        String where = "";
        String branchId = "";
        for (Branch bs : branchList) {
            branchId = branchId + "," + String.valueOf(bs.getId());
            if (bs.getId() == 0) {
                branchId = "";
                break;
            }
        }
        if (!branchId.equals("")) {
            branchId = branchId.substring(1, branchId.length());
            where = where + "AND sf.branch_id IN (" + branchId + ")";
        }

        String sql = "   \n"
                  + "   SELECT \n"
                  + "       sf.id AS sfid, \n"
                  + "       sf.currency_id AS sfcurrency_id,  \n"
                  + "       crr.code AS crrcode,  \n"
                  + "       crrd.name AS crrdname,  \n"
                  + "       sf.name AS sfname, "
                  + "       sf.shiftmovementsafe_id as sfshiftmovsf_id,\n"
                  + "       sf.is_mposmovement as sfis_mposmovement, \n"
                  + "       sf.branch_id AS sfbranch_id\n"
                  + "   FROM \n"
                  + "   finance.safe sf  \n"
                  + "   INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=sf.currency_id AND crrd.language_id=?)\n"
                  + "   INNER JOIN system.currency crr  ON (crr.id=sf.currency_id)\n"
                  + "   WHERE sf.deleted = false AND sf.status_id=23 \n"
                  + where;

        Object[] param = {sessionBean.getUser().getLanguage().getId()};
        List<Safe> result = getJdbcTemplate().query(sql, param, new SafeMapper());
        return result;
    }

}
