/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.03.2021 12:06:10
 */
package com.mepsan.marwiz.service.hepsiburada.dao;

import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.system.branch.dao.BranchIntegrationMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class HepsiburadaDao extends JdbcDaoSupport implements IHepsiburadaDao {

    @Override
    public List<BranchIntegration> findBranchIntegration() {
        String sql
                  = "SELECT \n"
                  + "	 brint.host1 AS brinthost,\n"
                  + "    brint.parameter1 AS brintparameter,\n"
                  + "    brint.username1 AS brintusername,\n"
                  + "    brint.password1 AS brintpassword,\n"
                  + "    brint.branch_id AS brintbranch_id,\n"
                  + "    brint.timeout1 AS brinttimeout\n"
                  + "FROM general.branchintegration brint \n"
                  + "WHERE brint.deleted=FALSE AND brint.type_id = 1\n";

        List<BranchIntegration> result = getJdbcTemplate().query(sql, new BranchIntegrationMapper());
        return result;
    }

    @Override
    public int processHepsiburada(String stockList, BranchIntegration branchIntegration, int type, String updateResult, String updateControlResult, boolean isSuccess) {
        String sql = "";
        Object[] param;
        if (type == 1) {
            sql = "SELECT * FROM integration.process_hepsiburada(?, ?, ?, ?, ?, ?);";
            param = new Object[]{1, stockList, "", "", branchIntegration.getBranch().getId(), false};
        } else {
            sql = "SELECT * FROM integration.process_hepsiburada(?, ?, ?, ?, ?, ?);";
            param = new Object[]{2, stockList, updateResult, updateControlResult, branchIntegration.getBranch().getId(), isSuccess};
        }
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String findSendingHepsiburada(BranchIntegration branchIntegration) {

        String sql = "SELECT * FROM integration.findsendinghepsiburada(?);";
        Object[] param = new Object[]{branchIntegration.getBranch().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
