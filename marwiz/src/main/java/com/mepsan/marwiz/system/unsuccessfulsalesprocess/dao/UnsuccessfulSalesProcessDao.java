package com.mepsan.marwiz.system.unsuccessfulsalesprocess.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class UnsuccessfulSalesProcessDao extends JdbcDaoSupport implements IUnsuccessfulSalesProcessDao {
    
    @Override
    public List<UnsuccessfulSalesProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        String whereBranch = "";
        if (!branchList.equals("") && !branchList.isEmpty()) {
            whereBranch = whereBranch + " AND ps.branch_id IN(" + branchList + ")";
        }
        
        System.out.println("---brachlist dao---" + branchList);
        String sql = "SELECT\n"
                + "   ps.id AS psid,\n"
                + "   ps.branch_id AS psbranch_id,\n"
                + "   brn.name AS brnname,\n"
                + "   ps.saleprocessdate AS pssaleprocessdate,\n"
                + "   ps.saleerrormessage AS pssaleerrormessage\n"
                + "   FROM log.possale ps\n"
                + "   LEFT JOIN general.branch brn ON(brn.id=ps.branch_id AND brn.deleted=FALSE)\n"
                + "    where ps.saleis_success = FALSE AND ps.cashregisteris_success = TRUE AND\n"
                + "     (SELECT count(*) FROM jsonb_object_keys(saledata::jsonb)) > 6 \n"
                + whereBranch
                + " limit " + pageSize + " offset " + first;
        
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new UnsuccessfulSalesProcessMapper());
    }
    
    @Override
    public int count(String branchList) {
        String whereBranch = "";
        if (!branchList.equals("") && !branchList.isEmpty()) {
            whereBranch = whereBranch + " AND ps.branch_id IN(" + branchList + ")";
        }
        
        String sql = " SELECT\n"
                + "   COUNT(ps.id) AS psid\n"
                + "   FROM log.possale ps\n"
                + "   LEFT JOIN general.branch brn ON(brn.id=ps.branch_id AND brn.deleted=FALSE)\n"
                + "    where ps.saleis_success = FALSE AND ps.cashregisteris_success = TRUE AND\n"
                + "     (SELECT count(*) FROM jsonb_object_keys(saledata::jsonb)) > 6 \n"
                + whereBranch;
        
        Object[] param = new Object[]{};
        
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }
    
    @Override
    public List<UnsuccessfulSalesProcess> sendIntegration(String branchlist) {
        String sql = "SELECT * FROM general.unsuccessful_pos_sale(?);";
        Object[] param = new Object[]{branchlist};
        
        System.out.println("----param---" + Arrays.toString(param));
        try {
            return getJdbcTemplate().query(sql, param, new UnsuccessfulSalesProcessMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }
    
}
