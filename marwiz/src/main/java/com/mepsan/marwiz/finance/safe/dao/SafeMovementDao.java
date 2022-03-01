/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 07:41:43
 */
package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SafeMovementDao extends JdbcDaoSupport implements ISafeMovementDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SafeMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId) {
        String sql;

        String branchList = "";

        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }
        
        sql = " SELECT * FROM finance.list_safemovement(?, ?, ?, ?, ?, ? ,? ,?, ?, ?)";

        Object[] param = new Object[]{safeString, branchList, beginDate, endDate, sessionBean.getUser().getLanguage().getId(),pageSize, first, opType,1, financingTypeId};
        List<SafeMovement> result = getJdbcTemplate().query(sql, param, new SafeMovementMapper());
        return result;
        
    }

    @Override
    public List<SafeMovement> count(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId) {
        String sql;

        String branchList = "";

        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

       
        sql = " SELECT * FROM finance.count_safemovement(?, ?, ? ,? ,?, ?, ?)";

        Object[] params = new Object[]{safeString, branchList, beginDate, endDate, sessionBean.getUser().getLanguage().getId(),opType,financingTypeId};
        List<SafeMovement> result = getJdbcTemplate().query(sql, params, new SafeMovementMapper());
        return result;
        
    }

    @Override
    public int count(String where, Safe safe, Branch branch) {
        String sql = "SELECT COUNT(sfm.id)\n"
                  + "FROM \n"
                  + "    finance.safemovement sfm  \n"
                  + "    INNER JOIN finance.safe sf  ON(sf.id=sfm.safe_id) \n"
                  + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = sfm.financingdocument_id AND fdoc.deleted=False)  \n"
                  + "WHERE \n"
                  + "	sfm.safe_id=? AND sfm.deleted = false AND sfm.branch_id = ? " + where;

        Object[] params = new Object[]{safe.getId(), branch.getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    @Override
    public List<SafeMovement> exportData(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId) {
        String sql;

        String branchList = "";

        for (Branch br : listOfBranch) {
            branchList = branchList + "," + String.valueOf(br.getId());
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

      
       
        sql = " SELECT * FROM finance.list_safemovement(?, ?, ?, ?, ?, ? ,? ,?, ?, ?)";

        Object[] params = new Object[]{safeString, branchList, beginDate, endDate, sessionBean.getUser().getLanguage().getId(),0, 0, opType,3, financingTypeId};
        List<SafeMovement> result = getJdbcTemplate().query(sql, params, new SafeMovementMapper());
        return result;
        
        
    }

   

}
