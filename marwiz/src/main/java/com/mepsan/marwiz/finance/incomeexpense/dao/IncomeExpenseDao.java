/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Branch;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseDao extends JdbcDaoSupport implements IIncomeExpenseDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IncomeExpense> listofIncomeExpense(Branch branch) {
        String sql = "SELECT\n"
                  + "fie.id AS fieid,\n"
                  + "fie.parent_id AS fieparent_id,\n"
                  + "fie.balance AS fiebalance,\n"
                  + "fie.is_income AS fieis_income,\n"
                  + "fie.is_profitmarginreport AS fieis_profitmarginreport,\n"
                  + "fie.name AS fiename\n"
                  + "FROM finance.incomeexpense fie\n"
                  + "WHERE fie.deleted=FALSE AND fie.branch_id = ?";

        Object[] param = new Object[]{branch.getId()};

        List<IncomeExpense> result = getJdbcTemplate().query(sql, param, new IncomeExpenseMapper());
        return result;
    }

    @Override
    public int create(IncomeExpense obj) {
        String sql = "INSERT INTO \n"
                  + "finance.incomeexpense\n"
                  + "(name,parent_id,is_income,is_profitmarginreport,branch_id,c_id,u_id)\n"
                  + "VALUES\n"
                  + "(?,?,?,?,?,?,?) RETURNING id";

        Object[] param = new Object[]{obj.getName(), obj.getParentId() == null ? null : obj.getParentId().getId() == 0 ? null : obj.getParentId().getId(), obj.isIsIncome(), obj.isIsProfitMarginReport(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(IncomeExpense obj) {
        String sql = "UPDATE finance.incomeexpense SET name= ?, is_income = ?,is_profitmarginreport = ?, u_id = ?, u_time = now() WHERE id= ? ";
        Object[] param = new Object[]{obj.getName(), obj.isIsIncome(), obj.isIsProfitMarginReport(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(IncomeExpense incomeExpense) {
        String sql = "UPDATE finance.incomeexpense set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                  + "UPDATE finance.incomeexpensemovement SET deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND incomeexpense_id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), incomeExpense.getId(), sessionBean.getUser().getId(), incomeExpense.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(IncomeExpense incomeExpense) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT parent_id FROM finance.incomeexpense WHERE parent_id=? AND branch_id =? AND deleted=False) THEN 1 WHEN (SELECT COUNT(incomeexpense_id) FROM finance.incomeexpensemovement WHERE incomeexpense_id=? AND branch_id = ? AND deleted=False)>1 THEN 2 ELSE 0 END";
        Object[] param = new Object[]{incomeExpense.getId(), sessionBean.getUser().getLastBranch().getId(), incomeExpense.getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<IncomeExpense> selectIncomeExpense(boolean isIncome) {
        String where = "";
        if (isIncome) {
            where += " AND fie.is_income = TRUE";
        } else {
            where += " AND fie.is_income = FALSE";
        }
        String sql = "SELECT\n"
                  + "fie.id AS fieid,\n"
                  + "fie.parent_id AS fieparent_id,\n"
                  + "fie.balance AS fiebalance,\n"
                  + "fie.is_income AS fieis_income,\n"
                  + "fie.is_profitmarginreport AS fieis_profitmarginreport,\n"
                  + "fie.name AS fiename\n"
                  + "FROM finance.incomeexpense fie\n"
                  + "WHERE fie.deleted=FALSE AND fie.branch_id = ?"
                  + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<IncomeExpense> result = getJdbcTemplate().query(sql, param, new IncomeExpenseMapper());
        return result;
    }

    @Override
    public List<IncomeExpense> totalIncomeExpense(Date beginDate, Date endDate, String branchList) {
        String sql = "SELECT \n"
                  + "	 fie.id AS fieid,\n"
                  + "    fie.name AS fiename,\n"
                  + "    fie1.name AS fie1name,\n"
                  + "    fie.parent_id AS fieparent_id,\n"
                  + "    fie.is_income AS fieis_income,\n"
                  + "    COALESCE(SUM(fiem.price),0) AS fiemprice,\n"
                  + "    COALESCE(SUM(fiem.price*fiem.exchangerate),0) AS fiemexchangeprice\n"
                  + "FROM finance.incomeexpensemovement fiem\n"
                  + "	INNER JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id AND fie.deleted=False)\n"
                  + "	LEFT JOIN finance.incomeexpense fie1 ON(fie1.id = fie.parent_id AND fie1.deleted=False)\n"
                  + "WHERE fiem.deleted=False AND fie.is_profitmarginreport=True AND fiem.movementdate BETWEEN ? AND ? AND fiem.branch_id IN (" + branchList + ")\n"
                  + "GROUP BY fie.id, fie.name, fie.parent_id, fie.is_income, fie1.name\n"
                  + "ORDER BY fie.is_income DESC";

        Object[] param = new Object[]{beginDate, endDate};
        List<IncomeExpense> result = getJdbcTemplate().query(sql, param, new IncomeExpenseMapper());
        return result;
    }

}
