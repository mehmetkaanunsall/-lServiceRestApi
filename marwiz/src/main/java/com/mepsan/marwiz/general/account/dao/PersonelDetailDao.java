package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class PersonelDetailDao extends JdbcDaoSupport implements IPersonelDetailDao {
    
    @Autowired
    public SessionBean sessionBean;
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    @Override
    public int update(String integrationcode, BigDecimal exactsalary, int agi, Date startDate, Date endDate, int accountId) {
        
        String sql = "UPDATE general.employeeinfo "
                + "SET "
                + "integrationcode=? , "
                + "exactsalary=? , "
                + "agi=? ,"
                + "startdate=? ,"
                + "enddate=? ,"
                + "c_id=? "
                + "WHERE account_id=? AND branch_id = ? AND deleted=false";
        
        Object[] param = new Object[]{integrationcode, exactsalary, agi, startDate,
            endDate, sessionBean.getUser().getId(), accountId,sessionBean.getUser().getLastBranch().getId()};
        
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
    
    @Override
    public EmployeeInfo find(int accountId) {
        
        String sql = " SELECT \n"
                + " ei.id eiid,\n"
                + " ei.integrationcode eiintegrationcode,\n"
                + " ei.exactsalary eiexactsalary,\n"
                + " ei.agi eiagi,\n"
                + " ei.startdate eistartdate,\n"
                + " ei.enddate eienddate\n"
                + "  from general.employeeinfo ei\n"
                + "  WHERE ei.account_id=? AND ei.branch_id = ? AND ei.deleted=FALSE";
        
        Object[] param = new Object[]{accountId,sessionBean.getUser().getLastBranch().getId()};
        
        List<EmployeeInfo> result = getJdbcTemplate().query(sql, param, new PersonelDetailMapper());
        return result.size() > 0 ? result.get(0) : new EmployeeInfo();
    }
    
}
