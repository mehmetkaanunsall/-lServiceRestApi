/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.CreditPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Gozde Gursel
 */
public class CreditPaymentDao extends JdbcDaoSupport implements ICreditPaymentDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<CreditPayment> listCreditPayment(CreditReport credit) {
        String sql = "SELECT"
                  + "    cpy.id AS cpyid, \n"
                  + "    cpy.credit_id AS cpycredit_id,\n"
                  + "    cpy.processdate AS cpyprocessdate,\n"
                  + "    cpy.price AS cpyprice,\n"
                  + "    cpy.is_direction AS cpyis_direction,\n"
                  + "    cpy.type_id AS cpytype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    cpy.currency_id AS cpycurrency_id,\n"
                  + "    cryd.name AS crydname,\n"
                  + "    cry.code AS crycode,\n"
                  + "    cpy.exchangerate AS cpyexchangerate,\n"
                  + "    fd.documentnumber AS fddocumentnumber,\n"
                  + "    fd.description AS fddescription,\n"
                  + "    bam.bankaccount_id AS bambankaccount_id,\n"
                  + "    bank.name AS bankname,\n"
                  + "    sfm.safe_id AS sfmsafe_id,\n"
                  + "    sf.name AS sfname,\n"
                  + "    chq.id as chqid,\n"
                  + "    chq.portfolionumber as chqportfolionumber,\n"
                  + "    chq.documentnumber as chqdocumentnumber,\n"
                  + "    chq.documentnumber_id as chqdocumentnumber_id,\n"
                  + "    chq.documentserial as chqdocumentserial,\n"
                  + "    chq.bankbranch_id as chqbankbranch_id,\n"
                  + "    chq.accountnumber as chqaccountnumber,\n"
                  + "    chq.ibannumber as chqibannumber,\n"
                  + "    chq.expirydate as chqexpirydate,\n"
                  + "    chq.status_id as chqstatus_id,\n"
                  + "    chq.paymentcity_id as chqpaymentcity_id,\n"
                  + "    chq.bill_collocationdate as chqbill_collocationdate,\n"
                  + "    chq.accountguarantor as chqaccountguarantor, \n"
                  + "    cty.country_id as ctycountry_id\n"
                  + "FROM \n"
                  + "    finance.creditpayment cpy\n"
                  + "    LEFT JOIN finance.financingdocument fd ON (fd.id = cpy.financingdocument_id)\n"
                  + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id = fd.id)\n"
                  + "    LEFT JOIN finance.bankaccount bank ON(bank.id = bam.bankaccount_id)\n"
                  + "    LEFT JOIN finance.safemovement sfm ON(sfm.financingdocument_id = fd.id)\n"
                  + "    LEFT JOIN finance.safe sf ON(sf.id = sfm.safe_id)\n"
                  + "    LEFT JOIN finance.chequebill chq ON (chq.id = cpy.chequebill_id)\n"
                  + "    LEFT JOIN system.city cty ON(cty.id = chq.paymentcity_id)\n"
                  + "    INNER JOIN system.type_dict typd ON(typd.type_id=cpy.type_id AND typd.language_id = ?)\n"
                  + "    INNER JOIN system.currency_dict cryd ON(cryd.currency_id=cpy.currency_id AND cryd.language_id = ?)\n"
                  + "    INNER JOIN system.currency cry ON(cry.id=cpy.currency_id)\n"
                  + "WHERE \n"
                  + "  cpy.deleted=FALSE AND cpy.credit_id = ? \n"
                  + "AND ((? AND cpy.is_direction = FALSE) OR (? AND cpy.is_direction = TRUE))";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), credit.getId(), !credit.isIsCustomer(), credit.isIsCustomer()};
        return getJdbcTemplate().query(sql, param, new CreditPaymentMapper());
    }

    @Override
    public int create(CreditPayment creditPayment, Currency currencys, String json) {

        String sql = " SELECT * FROM finance.process_creditpayment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{0, creditPayment.getCredit().getId(), creditPayment.getId(), creditPayment.getCredit().isIsCustomer(), creditPayment.getType().getId(), creditPayment.getProcessDate(), creditPayment.getCredit().getAccount().getId(),
            creditPayment.getSafe().getId(), creditPayment.getBankAccount().getId(), creditPayment.getPrice(), creditPayment.getCurrency().getId(), creditPayment.getExchangeRate(),
            creditPayment.getFinancingDocument().getDocumentNumber(), creditPayment.getFinancingDocument().getDescription(), json, sessionBean.getUser().getId(), creditPayment.getCredit().getBranchSetting().getBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(CreditPayment creditPayment) {

        String sql = " SELECT * FROM finance.process_creditpayment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{1, creditPayment.getCredit().getId(), creditPayment.getId(), creditPayment.getCredit().isIsCustomer(), creditPayment.getType().getId(), creditPayment.getProcessDate(), creditPayment.getCredit().getAccount().getId(),
            creditPayment.getSafe().getId(), creditPayment.getBankAccount().getId(), creditPayment.getPrice(), creditPayment.getCurrency().getId(), creditPayment.getExchangeRate(),
            creditPayment.getFinancingDocument().getDocumentNumber(), creditPayment.getFinancingDocument().getDescription(), null, sessionBean.getUser().getId(), creditPayment.getCredit().getBranchSetting().getBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(CreditPayment creditPayment) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {6, creditPayment.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(CreditPayment creditPayment) {
        String sql = "SELECT r_payment_id FROM finance.delete_payment_financingdocument (?, ?, ?);";

        Object[] param = new Object[]{3, creditPayment.getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
