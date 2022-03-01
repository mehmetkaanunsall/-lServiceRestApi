/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ebubekir.buker
 */
public class DuePaymentsReportDao extends JdbcDaoSupport implements IDuePaymentsReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DuePaymentsReport> totals(String where) {
        
        String sql = "SELECT \n"
                + "  COUNT(inv.id) as invid,\n"
                + "  sum(inv.remainingmoney) as invremainingmoney,\n"
                + "  inv.currency_id as crnid\n"
                + "FROM finance.invoice inv \n"
                + "INNER JOIN general.account acc ON(acc.id=inv.account_id AND acc.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN system.currency cr ON(cr.id=inv.currency_id)\n"
                + "WHERE inv.deleted=FALSE AND inv.remainingmoney > 0 " + where +  "\n"
                + "GROUP BY inv.currency_id ";
        
        List<DuePaymentsReport> result = getJdbcTemplate().query(sql, new DuePaymentsReportMapper());
        return result;
    }

    @Override
    public List<DuePaymentsReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, DuePaymentsReport duePaymentsReport) {

        String sql = "SELECT \n"
                + "  inv.id as invid,\n"
                + "  crn.code, \n"
                + "  crn.id as crnid,\n"
                + "  inv.branch_id as invbranch_id,\n"
                + "  brn.name as brnname,\n"
                + "  inv.is_purchase as invispurchase,\n"
                + "  inv.account_id as invaccount_id,\n"
                + "  acc.name as accname,\n"
                + "  inv.documentnumber as invdocumentnumber,\n"
                + "  inv.documentserial as invdocumentserial,\n"
                + "  inv.invoicedate as invinvoicedate,\n"
                + "  inv.duedate as invduedate,\n"
                + "  inv.totalmoney as invtotalmoney,\n"
                + "  inv.remainingmoney as invremainingmoney\n"
                + "FROM finance.invoice inv\n"
                + "INNER JOIN general.account acc ON(acc.id=inv.account_id AND acc.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN system.currency crn ON(crn.id=inv.currency_id) \n"
                + "WHERE inv.deleted=FALSE AND inv.remainingmoney > 0 " + where + "\n"
                + "ORDER BY acc.name \n"
                + " LIMIT " + pageSize + " OFFSET " + first;

        List<DuePaymentsReport> result = getJdbcTemplate().query(sql, new DuePaymentsReportMapper());
        return result;
    }

    @Override
    public List<DuePaymentsReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {//xhtml paginator toplam sayfa sayısını ayarlıyor.
        String sql = "SELECT \n"
                + "  COUNT(inv.id) as invid\n"
                + "FROM finance.invoice inv\n"
                + "INNER JOIN general.account acc ON(acc.id=inv.account_id AND acc.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "WHERE inv.deleted=FALSE AND inv.remainingmoney > 0 " + where + "\n";

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;

    }

    @Override
    public DataSource getDatasource() {

        return getDataSource();

    }

    @Override
    public String exportData(String where, String branchList, DuePaymentsReport duePaymentsReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                + "  inv.id as invid,\n"
                + "  crn.code, \n"
                + "  crn.id as crnid,\n"
                + "  inv.branch_id as invbranch_id,\n"
                + "  brn.name as brnname,\n"
                + "  inv.is_purchase as invispurchase,\n"
                + "  inv.account_id as invaccount_id,\n"
                + "  acc.name as accname,\n"
                + "  inv.documentserial as invdocumentserial,\n"
                + "  inv.documentnumber as invdocumentnumber,\n"
                + "  inv.invoicedate as invinvoicedate,\n"
                + "  inv.duedate as invduedate,\n"
                + "  inv.totalmoney as invtotalmoney,\n"
                + "  inv.remainingmoney as invremainingmoney\n"
                + "FROM finance.invoice inv\n"
                + "INNER JOIN general.account acc ON(acc.id=inv.account_id AND acc.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN system.currency crn ON(crn.id=inv.currency_id) \n"
                + "WHERE inv.deleted=FALSE AND inv.remainingmoney > 0 \n" + where + "\n"
                + "ORDER BY acc.name \n";

        return sql;

    }

}
