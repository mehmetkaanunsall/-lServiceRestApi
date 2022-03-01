/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class IncomeExpenseMovementDao extends JdbcDaoSupport implements IIncomeExpenseMovementDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IncomeExpenseMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, IncomeExpense incomeExpense, Date beginDate, Date endDate) {
        String sql = "SELECT \n"
                  + "fiem.id as fiemid, \n"
                  + "fdoc.id as fdocid, \n"
                  + "fdoc.documentnumber as fdocdocumentnumber,\n"
                  + "fdoc.description as fdocdescription,\n"
                  + "fdoc.type_id as fdoctype_id,\n"
                  + "typd.name AS typdname,\n"
                  + "fiem.price as fiemprice, \n"
                  + "fiem.exchangerate as fiemexchangerate,\n"
                  + "fiem.movementdate as fiemmovementdate,\n"
                  + "fiem.currency_id as fiemcurrency_id, \n"
                  + "crr.code as crrcode, \n"
                  + "crr.sign as crrsign, \n"
                  + "bac.id AS bacid\n"
                  + "FROM finance.incomeexpensemovement fiem\n"
                  + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = fiem.financingdocument_id AND fdoc.deleted = False)\n"
                  + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.currency crr ON (crr.id = fiem.currency_id)   \n"
                  + "LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                  + "WHERE      \n"
                  + "fiem.incomeexpense_id=? AND fiem.deleted=false AND fiem.movementdate BETWEEN ?  AND ?  AND fiem.branch_id = ?\n"
                  + where + "\n"
                  + "ORDER BY fiem.movementdate DESC, fiem.id DESC  \n"
                  + " limit " + pageSize + " offset " + first;

        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), incomeExpense.getId(), beginDate, endDate, sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, params, new IncomeExpenseMovementMapper());
    }

    @Override
    public List<IncomeExpenseMovement> totals(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate) {
        String sql = "SELECT \n"
                  + "COUNT(fiem.id)as fiemid, \n"
                  + "SUM(fiem.price) as fiemprice, \n"
                  + "fiem.currency_id as fiemcurrency_id \n"
                  + "FROM finance.incomeexpensemovement fiem\n"
                  + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = fiem.financingdocument_id AND fdoc.deleted = False)\n"
                  + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.currency crr ON (crr.id = fiem.currency_id)   \n"
                  + "WHERE      \n"
                  + "fiem.incomeexpense_id=? AND fiem.deleted=false AND fiem.movementdate BETWEEN ?  AND ?  AND fiem.branch_id = ?\n"
                  + where + "\n"
                  + "GROUP BY fiem.currency_id";
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), incomeExpense.getId(), beginDate, endDate, sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, params, new IncomeExpenseMovementMapper());
    }

    @Override
    public String exportData(String where, IncomeExpense incomeExpense, Date beginDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                  + "fiem.id as fiemid, \n"
                  + "fdoc.id as fdocid, \n"
                  + "fdoc.documentnumber as fdocdocumentnumber,\n"
                  + "fdoc.description as fdocdescription,\n"
                  + "fdoc.type_id as fdoctype_id,\n"
                  + "typd.name AS typdname,\n"
                  + "fiem.price as fiemprice, \n"
                  + "fiem.exchangerate as fiemexchangerate,\n"
                  + "fiem.movementdate as fiemmovementdate,\n"
                  + "fiem.currency_id as fiemcurrency_id, \n"
                  + "crr.code as crrcode, \n"
                  + "crr.sign as crrsign \n"
                  + "FROM finance.incomeexpensemovement fiem\n"
                  + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = fiem.financingdocument_id AND fdoc.deleted = False)\n"
                  + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                  + "LEFT JOIN system.currency crr ON (crr.id = fiem.currency_id)   \n"
                  + "WHERE      \n"
                  + "fiem.incomeexpense_id=" + incomeExpense.getId() + " AND fiem.deleted=false AND fiem.branch_id =" + sessionBean.getUser().getLastBranch().getId() + " AND fiem.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'  \n"
                  + where + "\n"
                  + "ORDER BY fiem.movementdate DESC, fiem.id DESC  \n";
        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public int create(IncomeExpenseMovement obj) {
        String sql = "INSERT INTO \n"
                  + "finance.incomeexpensemovement\n"
                  + "(\n"
                  + "    incomeexpense_id,\n"
                  + "    financingdocument_id,\n"
                  + "    is_direction,\n"
                  + "    price,\n"
                  + "    movementdate,\n"
                  + "    currency_id,\n"
                  + "    exchangerate,\n"
                  + "    branch_id,\n"
                  + "    c_id,\n"
                  + "    u_id\n"
                  + ")\n"
                  + "VALUES (\n"
                  + "    ?,\n"
                  + "    null,\n"
                  + "    ?,\n"
                  + "    ?,\n"
                  + "    NOW()::TIMESTAMP,\n"
                  + "    ?,\n"
                  + "    1,\n"
                  + "     ?,\n"
                  + "    ?,\n"
                  + "    ?\n"
                  + ") RETURNING id";

        Object[] param = new Object[]{obj.getIncomeExpense().getId(), obj.getIncomeExpense().isIsIncome(), obj.getPrice(), sessionBean.getUser().getLastBranch().getCurrency().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(IncomeExpenseMovement obj) {
        String sql = "UPDATE finance.incomeexpensemovement SET price= ?, u_id = ?, u_time = now() WHERE id= ? AND deleted = false";
        Object[] param = new Object[]{obj.getPrice(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
