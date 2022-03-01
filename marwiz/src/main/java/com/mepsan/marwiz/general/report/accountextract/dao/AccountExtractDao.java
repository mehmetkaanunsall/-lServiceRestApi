/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.03.2018 11:50:55
 */
package com.mepsan.marwiz.general.report.accountextract.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountExtractDao extends JdbcDaoSupport implements IAccountExtractDao {

    @Override
    public String exportData(String where, String sortField, String sortOrder, AccountExtract accountExtract) {

        String havingString = "";

        if (sortField == null) {
            sortField = " acc.name ";
            sortOrder = " ASC ";
        } else if (sortField.equals("name")) {
            sortField = " CASE WHEN acc.is_employee THEN CONCAT(acc.name,acc.title) ELSE acc.name END ";
        } else if (sortField.equals("outComing")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN FALSE THEN accm.price*accm.exchangerate ELSE 0 END),0) ";
        } else if (sortField.equals("inComing")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE 0 END),0) ";
        } else if (sortField.equals("balance")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) ";
        }

        if (accountExtract.getBalance() != null) {
            havingString = " HAVING COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) > " + accountExtract.getBalance() + "";
        }

        String sql = "SELECT\n"
                + "    abc.id AS abcid\n"
                + "    ,acc.id AS accid\n"
                + "    ,acc.is_person as accis_person\n"
                + "    ,acc.title as acctitle\n"
                + "    ,acc.name as accname\n"
                + "    ,acc.is_employee AS accisemployee\n"
                + "    ,br.id AS brid\n"
                + "    ,br.name AS brname\n"
                + "   ,COALESCE(SUM(CASE accm.is_direction WHEN FALSE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS dept--borçlandı\n"
                + "   ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS credit --alacak\n"
                + "   ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) AS balance\n "
                + "FROM general.account acc\n"
                + "LEFT JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.deleted=FALSE)\n"
                + "LEFT JOIN general.accountmovement accm ON (accm.account_id = acc.id AND accm.branch_id = abc.branch_id AND accm.deleted = FALSE)\n"
                + "LEFT JOIN general.branch br ON(abc.branch_id = br.id AND br.deleted=FALSE)\n"
                + "WHERE acc.deleted = FALSE\n"
                + where
                + "GROUP BY abc.id, acc.id, acc.name,acc.is_person,acc.title,br.id,br.name \n"
                + havingString + "\n"
                + "ORDER BY " + sortField + " " + sortOrder + " ";

        return sql;
    }

    @Override
    public List<AccountExtract> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AccountExtract accountExtract) {

        String havingString = "";

        if (sortField == null) {
            sortField = " acc.name ";
            sortOrder = " ASC ";
        } else if (sortField.equals("name")) {
            sortField = " CASE WHEN acc.is_employee THEN CONCAT(acc.name,acc.title) ELSE acc.name END ";
        } else if (sortField.equals("outComing")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN FALSE THEN accm.price*accm.exchangerate ELSE 0 END),0) ";
        } else if (sortField.equals("inComing")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE 0 END),0) ";
        } else if (sortField.equals("balance")) {
            sortField = " COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) ";
        }

        if (accountExtract.getBalance() != null) {
            //Küçüktür
            havingString = " HAVING COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) > " + accountExtract.getBalance() + "";
        }

        String sql = "SELECT\n"
                + "    abc.id AS abcid\n"
                + "    ,acc.id AS accid\n"
                + "    ,acc.is_person as accis_person\n"
                + "    ,acc.title as acctitle\n"
                + "    ,acc.name as accname\n"
                + "    ,acc.is_employee AS accisemployee\n"
                + "    ,br.id AS brid\n"
                + "    ,br.name AS brname\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN FALSE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS dept--borçlandı\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS credit --alacak\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) AS balance\n "
                + "FROM general.account acc\n"
                + "LEFT JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.deleted=FALSE)\n"
                + "LEFT JOIN general.accountmovement accm ON (accm.account_id = acc.id AND accm.branch_id = abc.branch_id AND accm.deleted = FALSE)\n"
                + "LEFT JOIN general.branch br ON(abc.branch_id = br.id AND br.deleted=FALSE)\n"
                + "WHERE acc.deleted = FALSE\n"
                + where
                + "GROUP BY abc.id, acc.id, acc.name,acc.is_person,acc.title,br.id,br.name \n"
                + havingString + "\n"
                + "ORDER BY " + sortField + " " + sortOrder + "  OFFSET " + first + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY \n";
        Object[] param = new Object[]{};
        List<AccountExtract> result = getJdbcTemplate().query(sql, param, new AccountExtractMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    /**
     * verilen parametrelere uygun cari sayısına bakar
     * @param accountExtract
     * @param pageId
     * @return 
     */
    @Override
    public int findAccountCount(AccountExtract accountExtract, int pageId) {
        String categoryList = "";
        for (Categorization category : accountExtract.getCategorizationList()) {
            categoryList = categoryList + "," + String.valueOf(category.getId());
            if (category.getId() == 0) {
                categoryList = "";
                break;
            }
        }

        String sqlCategory = " ";
        if (!categoryList.equals("")) {
            categoryList = categoryList.substring(1, categoryList.length());
            if (pageId == 102) {
                sqlCategory = " EXISTS (SELECT ect.account_id FROM general.employee_categorization_con ect WHERE ect.deleted=False AND ect.account_id = "+accountExtract.getAccountList().get(0).getId()+" AND ect.categorization_id IN (" + categoryList + ")) AND ";
            } else {
                sqlCategory = " EXISTS (SELECT act.account_id FROM general.account_categorization_con act WHERE act.deleted=False AND act.account_id = "+accountExtract.getAccountList().get(0).getId()+" AND act.categorization_id IN (" + categoryList + ")) AND ";
            }
        }

        String branchList = "";
        for (Branch bs : accountExtract.getBranchList()) {
            branchList = branchList + "," + String.valueOf(bs.getId());
            if (bs.getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        String sql = "";
        if (pageId == 102) {
            sql = "SELECT CASE WHEN "+sqlCategory+" (SELECT count(*) FROM general.account_branch_con abc WHERE abc.deleted=FALSE AND abc.account_id = ? AND abc.branch_id IN (" + branchList + "))=1 THEN 1 ELSE 0 END";
        } else {
            sql = "SELECT CASE WHEN "+sqlCategory+" (SELECT count(*) FROM general.account_branch_con abc WHERE abc.deleted=FALSE AND abc.account_id = ? AND abc.branch_id IN (" + branchList + "))=1 THEN 1 ELSE 0 END";

        }

        Object[] param = new Object[]{accountExtract.getAccountList().get(0).getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<AccountExtract> totals(String where, AccountExtract accountExtract) {
        String havingString = "";

        if (accountExtract.getBalance() != null) {
            havingString = " HAVING COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) > " + accountExtract.getBalance() + "";
        }

        String sql = "SELECT\n"
                + "	COUNT(tt.accid) AS accid\n"
                + "    ,COALESCE(SUM(tt.dept),0) AS dept--borçlandı\n"
                + "    ,COALESCE(SUM(tt.credit),0) AS credit --alacak\n"
                + "    ,COALESCE(SUM(tt.balance),0) AS balance\n "
                + "FROM(\n"
                + "SELECT\n"
                + "	acc.id AS accid\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN FALSE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS dept--borçlandı\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE 0 END),0) AS credit --alacak\n"
                + "    ,COALESCE(SUM(CASE accm.is_direction WHEN TRUE THEN accm.price*accm.exchangerate ELSE -accm.price*accm.exchangerate END),0) AS balance\n "
                + "FROM general.account acc\n"
                + "LEFT JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.deleted=FALSE)\n"
                + "LEFT JOIN general.accountmovement accm ON (accm.account_id = acc.id AND accm.branch_id = abc.branch_id AND accm.deleted = FALSE)\n"
                + "LEFT JOIN general.branch br ON(abc.branch_id = br.id AND br.deleted=FALSE)\n"
                + "WHERE acc.deleted = FALSE\n"
                + where
                + "GROUP BY abc.id, acc.id, acc.name,acc.is_person,acc.title,br.id,br.name \n"
                + havingString + "\n"
                + ") as tt";
        Object[] param = new Object[]{};
        List<AccountExtract> result = getJdbcTemplate().query(sql, param, new AccountExtractMapper());
        return result;
    }

}
