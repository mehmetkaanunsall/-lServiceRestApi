/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 08.03.2018 15:18:30
 */
package com.mepsan.marwiz.general.report.bankextract.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankExtractDao extends JdbcDaoSupport implements IBankExtractDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<BankAccountMovement> findAll(Date beginDate, Date endDate, String where) {
        String sql = "SELECT\n"
                  + "    bbc.id as bbcid,\n"
                  + "    ba.id as baid,\n"
                  + "    ba.name as baname,\n"
                  + "    bbc.balance AS bbcbalance,\n"
                  + "    br.id AS brid,\n"
                  + "    br.name AS brname,\n"
                  + "    crr.id as crrid,\n"
                  + "    crr.code as crrcode,\n"
                  + "    (SELECT \n"
                  + "       COALESCE(SUM(bkam1.price),0)\n"
                  + "     FROM\n"
                  + "       finance.bankaccountmovement bkam1 \n"
                  + "     WHERE bkam1.is_direction=true\n"
                  + "           AND bkam1.bankaccount_id=ba.id AND bkam1.branch_id = br.id AND bkam1.deleted = false\n"
                  + "    ) AS sumincoming,\n"
                  + "    (SELECT \n"
                  + "       COALESCE(SUM(bkam2.price),0)\n"
                  + "     FROM\n"
                  + "       finance.bankaccountmovement bkam2 \n"
                  + "     WHERE bkam2.is_direction=false\n"
                  + "           AND bkam2.bankaccount_id=ba.id AND bkam2.branch_id = br.id AND bkam2.deleted = false\n"
                  + "    ) AS sumoutcoming\n"
                  + "FROM\n"
                  + "	 finance.bankaccountmovement bam\n"
                  + "    INNER JOIN finance.bankaccount ba ON(ba.id=bam.bankaccount_id AND ba.deleted = FALSE)\n"
                  + "    INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = ba.id AND bbc.branch_id = bam.branch_id AND bbc.deleted=FALSE)\n"
                  + "    LEFT JOIN general.branch br ON(bbc.branch_id = br.id AND br.deleted=FALSE)\n"  
                  + "    INNER JOIN system.currency crr ON(crr.id=ba.currency_id)\n"
                  + "WHERE\n"
                  + "	 bam.deleted = FALSE \n"
                  + where + " \n"
                  + "GROUP BY \n"
                  + "	bbc.id,ba.id,ba.name,ba.balance,crr.id,crr.code,br.id,br.name\n";

        Object[] param = new Object[]{};
        List<BankAccountMovement> result = getJdbcTemplate().query(sql, param, new BankExtractMapper());
        return result;
    }

}
