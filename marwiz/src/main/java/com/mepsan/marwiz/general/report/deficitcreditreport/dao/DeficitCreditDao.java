package com.mepsan.marwiz.general.report.deficitcreditreport.dao;

import com.mepsan.marwiz.finance.credit.dao.CreditMapper;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class DeficitCreditDao extends JdbcDaoSupport implements IDeficitCreditDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String exportData(String where) {
        String sql = "SELECT \n"
                + "        	crdt.id as crdt_id,\n"
                + "        	crdt.processdate as crdtprocessdate,\n"
                + "        	crdt.account_id as crdtaccount_id,\n"
                + "             br.id AS brid,\n"
                + "             br.name AS brname,\n"
                + "        	acc.name as accname,\n"
                + "             acc.title AS acctitle,\n"
                + "             acc.is_employee AS accis_employee,\n"
                + "        	COALESCE(crdt.money,0) as crdtmoney,\n"
                + "        	crdt.currency_id as crdtcurrency_id,\n"
                + "        	cr.id as crcurrency_id,\n"
                + "             cr.code as crcode,\n"
                + "             crdt.duedate as  crdtduedate,\n"
                + "        	COALESCE(crdt.remainingmoney,0) as crdtremainingmoney,\n"
                + "        	crdt.is_paid as crdtis_paid,\n"
                + "             crdt.is_cancel as  crdtis_cancel,\n"
                + "             crdt.is_customer as crdtis_customer,\n"
                + "       	SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=true THEN crdt.money ELSE 0 END) OVER () AS totalCollection,\n"
                + "             SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=true THEN crdt.remainingmoney ELSE 0 END) OVER() AS totalCollectionRemaining,\n"
                + "             SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=false THEN crdt.money ELSE 0 END) OVER () AS totalPayment,\n"
                + "             SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=false THEN crdt.remainingmoney ELSE 0 END) OVER() AS totalPaymentRemaining\n"
                + "        FROM  finance.credit crdt \n"
                + "             INNER JOIN general.account acc ON(acc.id=crdt.account_id AND acc.deleted=FALSE)\n"
                + "             INNER JOIN system.currency cr ON(cr.id=crdt.currency_id)\n"
                + "             LEFT JOIN general.branch br ON(crdt.branch_id = br.id AND br.deleted=FALSE)\n"
                + "        WHERE crdt.deleted=FALSE"
                + where;

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<CreditReport> totals(String where) {
        String sql = "SELECT \n"
                + "	    count(*)as crdt_id,\n"
                + "         crdt.currency_id as crdtcurrency_id,\n"
                + "         COALESCE((sum(crdt.money)),0) as crdtmoney,\n"
                + "         COALESCE((sum(crdt.remainingmoney)),0) as crdtremainingmoney,\n"
                + "         COALESCE((sum((crdt.money)-(crdt.remainingmoney))),0)as paidmoney\n"
                + "	FROM  \n"
                + "    	finance.credit crdt \n"
                + "       	INNER JOIN general.account acc ON(acc.id=crdt.account_id AND acc.deleted=FALSE)\n"
                + "       	INNER JOIN system.currency cr ON(cr.id=crdt.currency_id)\n"
                + "       	INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                + "       	INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                + "	WHERE \n"
                + "  crdt.deleted=FALSE\n"
                + where + "\n"
                + "      GROUP BY crdt.currency_id";

        List<CreditReport> result = getJdbcTemplate().query(sql, new CreditMapper());
        return result;
    }

}
