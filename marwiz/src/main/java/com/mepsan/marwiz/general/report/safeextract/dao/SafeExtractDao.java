/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 16:55:37
 */
package com.mepsan.marwiz.general.report.safeextract.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SafeExtractDao extends JdbcDaoSupport implements ISafeExtractDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

  
    @Override
    public List<SafeMovement> findAll(String where) {
        String sql = "SELECT\n"
                  + "s.id as sid\n"
                  + ",s.name as sname\n"
                  + ",br.id AS brid\n"
                  + ",br.name AS brname\n"
                  + ",s.currency_id as scurrency_id\n"
                  + ",s.balance as sbalance\n"
                  + ",(\n"
                  + "    SELECT \n"
                  + "      COALESCE(SUM(sfm1.price),0)\n"
                  + "    FROM finance.safemovement sfm1 \n"
                  + "    WHERE sfm1.is_direction=true \n"
                  + "    AND sfm1.safe_id = s.id \n"
                  + "    AND sfm1.branch_id = br.id \n"
                  + "    AND sfm1.deleted = false \n"
                  + "  ) AS sumincoming\n"
                  + "  ,(\n"
                  + "    SELECT \n"
                  + "      COALESCE(SUM(sfm2.price),0)\n"
                  + "    FROM  finance.safemovement sfm2 \n"
                  + "    WHERE sfm2.is_direction=false \n"
                  + "    and sfm2.safe_id = s.id \n"
                  + "    AND sfm2.branch_id = br.id \n"
                  + "    AND sfm2.deleted = false \n"
                  + "  ) AS sumoutcoming\n"
                  + "FROM \n"
                  + "	finance.safemovement sm\n"
                  + "	INNER JOIN finance.safe s ON(s.id=sm.safe_id AND s.branch_id= sm.branch_id AND s.deleted = FALSE)\n"
                  + "   LEFT JOIN general.branch br ON(sm.branch_id = br.id AND br.deleted=FALSE)\n"
                  + "WHERE \n"
                  + "	sm.deleted = FALSE \n"
                  + where + " \n"
                  + "GROUP BY\n"
                  + "	 s.id,s.name,s.currency_id, s.balance,br.id,br.name\n";

        Object[] param = new Object[]{};
        List<SafeMovement> result = getJdbcTemplate().query(sql, param, new SafeExtractMapper());
        return result;
    }

}
