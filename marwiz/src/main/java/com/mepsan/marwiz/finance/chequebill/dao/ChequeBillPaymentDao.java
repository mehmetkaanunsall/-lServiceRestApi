/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.ChequeBillPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBillPaymentDao extends JdbcDaoSupport implements IChequeBillPaymentDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ChequeBillPayment> listChequeBillPayment(ChequeBill chequeBill) {
        String sql = "SELECT\n"
                  + "cqbp.id AS cqbpid,\n"
                  + "cqbp.processdate AS cqbpprocessdate,\n"
                  + "cqbp.price AS cqbpprice, \n"
                  + "cqbp.type_id AS cqbptype_id, \n"
                  + "typd.name AS typdname, \n"
                  + "cqbp.currency_id AS cqbpcurrency_id, \n"
                  + "cryd.name AS crydname, \n"
                  + "cry.code AS crycode, \n"
                  + "cqbp.exchangerate AS cqbpexchangerate, \n"
                  + "fd.documentnumber AS fddocumentnumber, \n"
                  + "fd.description AS fddescription, \n"
                  + "bam.bankaccount_id AS bambankaccount_id, \n"
                  + "bank.name AS bankname, \n"
                  + "sfm.safe_id AS sfmsafe_id, \n"
                  + "sf.name AS sfname \n"
                  + "FROM finance.chequebillpayment cqbp\n"
                  + "LEFT JOIN finance.financingdocument fd ON (fd.id = cqbp.financingdocument_id) \n"
                  + "LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id = fd.id) \n"
                  + "LEFT JOIN finance.bankaccount bank ON(bank.id = bam.bankaccount_id) \n"
                  + "LEFT JOIN finance.safemovement sfm ON(sfm.financingdocument_id = fd.id) \n"
                  + "LEFT JOIN finance.safe sf ON(sf.id = sfm.safe_id) \n"
                  + "INNER JOIN system.type_dict typd ON(typd.type_id=cqbp.type_id AND typd.language_id = ?) \n"
                  + "INNER JOIN system.currency_dict cryd ON(cryd.currency_id=cqbp.currency_id AND cryd.language_id = ?) \n"
                  + "INNER JOIN system.currency cry ON(cry.id=cqbp.currency_id) \n"
                  + "WHERE \n"
                  + "cqbp.chequebill_id=? AND cqbp.deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), chequeBill.getId()};
        return getJdbcTemplate().query(sql, param, new ChequeBillPaymentMapper());
    }

    @Override
    public int create(ChequeBillPayment chequeBillPayment) {
        String sql = "SELECT * FROM finance.process_chequebillpayment (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{0, chequeBillPayment.getChequeBill().getId(), chequeBillPayment.getId(), chequeBillPayment.getChequeBill().isIsCustomer(), chequeBillPayment.getType().getId(), chequeBillPayment.getProcessDate(), chequeBillPayment.getChequeBill().getAccount().getId(),
            chequeBillPayment.getSafe().getId(), chequeBillPayment.getBankAccount().getId(), chequeBillPayment.getPrice(), chequeBillPayment.getCurrency().getId(), chequeBillPayment.getExchangeRate(),
            chequeBillPayment.getFinancingDocument().getDocumentNumber(), chequeBillPayment.getFinancingDocument().getDescription(), sessionBean.getUser().getId(), chequeBillPayment.getChequeBill().getBranch().getId()};
        System.out.println("--" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(ChequeBillPayment chequeBillPayment) {
        String sql = "SELECT * FROM finance.process_chequebillpayment (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{1, chequeBillPayment.getChequeBill().getId(), chequeBillPayment.getId(), chequeBillPayment.getChequeBill().isIsCustomer(), chequeBillPayment.getType().getId(), chequeBillPayment.getProcessDate(), chequeBillPayment.getChequeBill().getAccount().getId(),
            chequeBillPayment.getSafe().getId(), chequeBillPayment.getBankAccount().getId(), chequeBillPayment.getPrice(), chequeBillPayment.getCurrency().getId(), chequeBillPayment.getExchangeRate(),
            chequeBillPayment.getFinancingDocument().getDocumentNumber(), chequeBillPayment.getFinancingDocument().getDescription(), sessionBean.getUser().getId(), chequeBillPayment.getChequeBill().getBranch().getId()};
        System.out.println("--" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(ChequeBillPayment chequeBillPayment) {
        String sql = "SELECT r_payment_id FROM finance.delete_payment_financingdocument (?, ?, ?);";

        Object[] param = new Object[]{2, chequeBillPayment.getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(ChequeBillPayment chequeBillPayment) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {10, chequeBillPayment.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

}
