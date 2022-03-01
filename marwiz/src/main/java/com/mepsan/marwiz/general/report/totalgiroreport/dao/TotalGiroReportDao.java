/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.03.2018 09:14:33
 */
package com.mepsan.marwiz.general.report.totalgiroreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TotalGiroReportDao extends JdbcDaoSupport implements ITotalGiroReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<TotalGiroReport> findAll(String where, String branchList) {
        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch = "AND sl.branch_id IN(" + branchList + ")";
        }

        String sql = "SELECT \n"
                + "      COALESCE(SUM(slp.price * slp.exchangerate),0) AS totalmoney,\n"
                + "      slp.type_id AS slptype_id,\n"
                + "      typd.name AS typdname,\n"
                + "      sl.currency_id AS slcurrency_id,\n"
                + "      brn.id as brnid,\n"
                + "      brn.name as brnname\n"
                + " 	FROM \n"
                + "      general.salepayment slp\n"
                + "      INNER JOIN general.sale sl ON (sl.id = slp.sale_id AND sl.deleted = False)\n"
                + "      INNER JOIN general.branch brn ON(brn.id =sl.branch_id AND brn.deleted=FALSE)\n"
                + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                + "      INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                + "      WHERE slp.deleted=False " + whereBranch + " AND sl.is_return=False\n"
                + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + where + "\n"
                + "GROUP BY slp.type_id, typd.name, sl.currency_id,brn.id, brn.name\n"
                + "ORDER BY brn.id, brn.name,typd.name";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<TotalGiroReport> result = getJdbcTemplate().query(sql, param, new TotalGiroReportMapper());
        return result;
    }

    @Override
    public int create(TotalGiroReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(TotalGiroReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
