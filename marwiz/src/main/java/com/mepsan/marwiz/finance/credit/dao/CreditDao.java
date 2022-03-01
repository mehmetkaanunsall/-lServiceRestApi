/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Gozde Gursel
 */
public class CreditDao extends JdbcDaoSupport implements ICreditDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<CreditReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "crdt.id";
            sortOrder = "desc";
        }

        String sql = "SELECT\n"
                  + "               crdt.id as crdt_id,\n"
                  + "               crdt.processdate as crdtprocessdate,\n"
                  + "               crdt.account_id as crdtaccount_id,\n"
                  + "               acc.name as accname,\n"
                  + "               acc.title AS acctitle,\n"
                  + "               acc.is_employee AS accis_employee, \n"
                  + "               acc.email As accemail,\n"
                  + "               acc.phone As accphone,\n"
                  + "               acc.dueday as accdueday,\n"
                  + "               acc.address As accaddress,\n"
                  + "               COALESCE(crdt.money,0) as crdtmoney,\n"
                  + "               crdt.currency_id as crdtcurrency_id,\n"
                  + "               cr.id as crcurrency_id,\n"
                  + "               cr.code as crcode,\n"
                  + "               crdt.duedate as  crdtduedate,\n"
                  + "               COALESCE(crdt.remainingmoney,0) as crdtremainingmoney,\n"
                  + "               crdt.is_paid as crdtis_paid,\n"
                  + "               crdt.is_cancel as  crdtis_cancel,\n"
                  + "               crdt.is_invoice as crdtis_invoice,\n"
                  + "               crdt.is_customer as crdtis_customer,\n"
                  + "               crdt.branch_id as crdtbranch_id,\n"
                  + "               br.id AS brid,\n"
                  + "               br.name AS brname,\n"
                  + "               brs.is_centralintegration AS brsis_centralintegration,\n"
                  + "               brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                  + "               br.currency_id AS brcurrency_id,\n"
                  + "               br.is_agency AS bris_agency,\n"
                  + "               brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                  + "               COALESCE(SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=true THEN (crdt.money*COALESCE(crdt.exchangerate,1)) ELSE 0 END) OVER (),0) AS totalCollection,\n"
                  + "               COALESCE(SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=true THEN (crdt.remainingmoney*COALESCE(crdt.exchangerate,1)) ELSE 0 END) OVER(),0) AS totalCollectionRemaining,\n"
                  + "               COALESCE(SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=false THEN (crdt.money*COALESCE(crdt.exchangerate,1)) ELSE 0 END) OVER (),0) AS totalPayment,\n"
                  + "               COALESCE(SUM(CASE WHEN crdt.is_cancel=false and crdt.is_customer=false THEN (crdt.remainingmoney*COALESCE(crdt.exchangerate,1)) ELSE 0 END) OVER(),0) AS totalPaymentRemaining\n"
                  + "               FROM  finance.credit crdt \n"
                  + "                    INNER JOIN general.account acc ON(acc.id=crdt.account_id AND acc.deleted=FALSE)\n"
                  + "                    INNER JOIN system.currency cr ON(cr.id=crdt.currency_id)\n"
                  + "                    INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                  + "                    INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                  + "               WHERE crdt.deleted=FALSE"
                  + where + "\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;

        Object[] params = new Object[]{};
        return getJdbcTemplate().query(sql, params, new CreditMapper());

    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                  + "	count(*)"
                  + "FROM  finance.credit crdt \n"
                  + "INNER JOIN general.account acc ON(acc.id=crdt.account_id AND acc.deleted=FALSE)\n"
                  + "INNER JOIN system.currency cr ON(cr.id=crdt.currency_id)\n"
                  + "INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                  + "INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                  + "WHERE crdt.deleted=FALSE \n"
                  + where;
        int result = getJdbcTemplate().queryForObject(sql, Integer.class);
        return result;
    }

    @Override
    public List<CheckDelete> testBeforeDelete(CreditReport credit) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {9, credit.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(CreditReport credit) {
        String sql = "SELECT r_credit_id FROM finance.delete_credit(?, ?);";

        Object[] param = {credit.getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CreditReport> findShiftCredit(Date beginDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String sql = "SELECT \n"
                  + "      COALESCE(slp.price,0)* COALESCE(slp.exchangerate,1) moneysub\n"
                  + "      FROM general.salepayment slp \n"
                  + "      INNER JOIN general.sale sl ON(slp.sale_id=sl.id AND sl.branch_id=? AND sl.deleted=FALSE)\n"
                  + "      INNER JOIN general.shift shf ON(shf.id=sl.shift_id AND shf.deleted=FALSE AND shf.branch_id=?)\n"
                  + "      WHERE slp.type_id=19 AND slp.deleted=FALSE  AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'";

        Object[] params = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        //   System.out.println("---credit---" + sql);
        return getJdbcTemplate().query(sql, params, new CreditMapper());
    }

}
