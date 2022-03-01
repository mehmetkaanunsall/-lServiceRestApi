/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
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
public class ChequeBillDao extends JdbcDaoSupport implements IChequeBillDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ChequeBill> findAll(int chequeBillType, String where) {

        switch (chequeBillType) {
            case 1:
                // müşteri çeki
                where += " AND cqb.is_cheque = TRUE AND cqb.is_customer = TRUE \n";
                break;
            case 2:
                // borç çeki
                where += " AND cqb.is_cheque = TRUE AND cqb.is_customer = FALSE \n";
                break;
            case 3:
                // müşteri senedi
                where += " AND cqb.is_cheque = FALSE AND cqb.is_customer = TRUE \n";
                break;
            case 4:
                // borç senedi
                where += " AND cqb.is_cheque = FALSE AND cqb.is_customer = FALSE \n";
                break;
            default:
                break;
        }

        String sql = "SELECT \n"
                  + "    cqb.id AS cqbid,\n"
                  + "    cqb.account_id AS cqbaccount_id,\n"
                  + "    acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    cqb.accountnumber AS cqbaccountnumber,\n"
                  + "    cqb.bankbranch_id AS cqbbankbranch_id,\n"
                  + "    bkb.name AS bkbname,\n"
                  + "    cqb.bill_collocationdate AS cqbbill_collocationdate,\n"
                  + "    cqb.currency_id AS cqbcurrency_id,\n"
                  + "    cqb.documentnumber AS cqbdocumnetnumber,\n"
                  + "    cqb.documentnumber_id AS cqbdocumentnumber_id,\n"
                  + "    cqb.documentserial AS cqbdocumentserial,\n"
                  + "    cqb.exchangerate AS cqbexchangerate,\n"
                  + "    cqb.expirydate AS cqbexpirydate,\n"
                  + "    cqb.ibannumber AS cqbibannumber,\n"
                  + "    cqb.is_cheque AS cqbis_cheque,\n"
                  + "    cqb.is_customer AS cqbis_customer,\n"
                  + "    ctry.id AS ctryid,\n"
                  + "    cqb.paymentcity_id AS cqbpaymentcity_id,\n"
                  + "    ctyd.name AS ctydname,\n"
                  + "    cqb.portfolionumber AS cqbportfolionumber,\n"
                  + "    cqb.accountguarantor AS cqbaccountguarantor,\n"
                  + "    cqb.endorsedaccount_id AS cqbendorsedaccount_id,\n"
                  + "    acc2.name AS acc2name,\n"
                  + "    acc2.title AS acc2title,\n"
                  + "    acc2.is_employee AS acc2is_employee,\n"
                  + "    cqb.status_id AS cqbstatus_id,\n"
                  + "    sttd.name AS sttdname,\n"
                  + "    cqb.totalmoney AS cqbtotalmoney,\n"
                  + "    cqb.remainingmoney AS cqbremainingmoney,\n"
                  + "    cqb.branch_id AS cqbbranch_id,\n"
                  + "    br.name AS brname,\n"
                  + "    cqb.collectingbankaccount_id AS cqbcollectingbankaccount_id,\n"
                  + "    ba.name AS baname\n"
                  + "    \n"
                  + "FROM finance.chequebill cqb \n"
                  + "    LEFT JOIN general.account acc ON(acc.id = cqb.account_id AND acc.deleted = False)\n"
                  + "    LEFT JOIN finance.bankbranch bkb ON(bkb.id = cqb.bankbranch_id AND bkb.deleted = False)\n"
                  + "    LEFT JOIN system.city cty ON(cty.id = cqb.paymentcity_id AND cty.deleted = FALSE)\n"
                  + "    LEFT JOIN system.city_dict ctyd ON(ctyd.city_id = cqb.paymentcity_id AND ctyd.language_id = ?)\n"
                  + "    LEFT JOIN system.country ctry ON(ctry.id = cty.country_id AND ctry.deleted = FALSE)\n"
                  + "    LEFT JOIN general.account acc2 ON(acc2.id = cqb.endorsedaccount_id AND acc2.deleted = False)\n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = cqb.status_id AND sttd.language_id = ?)\n"
                  + "    INNER JOIN general.branch br ON (br.id = cqb.branch_id)\n"
                  + "    LEFT JOIN finance.bankaccount ba ON(ba.id = cqb.collectingbankaccount_id)\n"
                  + " WHERE cqb.deleted = FALSE \n"
                  + where
                  + " ORDER BY cqb.expirydate DESC ";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        List<ChequeBill> result = getJdbcTemplate().query(sql, param, new ChequeBillMapper());
        return result;

    }

    @Override
    public int create(ChequeBill obj) {

        String sql = "SELECT * FROM finance.process_chequebill (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?)";

        Object[] param = new Object[]{0, obj.getId(), obj.isIsCheque(), obj.isIsCustomer(), obj.getPortfolioNumber(), obj.getExpiryDate(), obj.getBankBranch().getId() == 0 ? null : obj.getBankBranch().getId(),
            obj.getEndorsedAccount().getId() == 0 ? null : obj.getEndorsedAccount().getId(), obj.getAccountGuarantor(), obj.getDocumentNumber().getId() == 0 ? null : obj.getDocumentNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNo(),
            obj.getPaymentCity().getId(), obj.getCurrency().getId(),
            obj.getExchangeRate(), obj.getTotalMoney(), obj.getAccount().getId(), obj.getAccountNumber(), obj.getIbanNumber(),
            obj.getStatus().getId(), obj.getBillCollocationDate(), obj.getCollectingBankAccount().getId() == 0 ? null : obj.getCollectingBankAccount().getId(), sessionBean.getUser().getId(), obj.getBranch().getId()};

//        System.out.println("------"+sql);
//        System.out.println("---------"+Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(ChequeBill obj) {
        String sql = "SELECT * FROM finance.process_chequebill (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{1, obj.getId(), obj.isIsCheque(), obj.isIsCustomer(), obj.getPortfolioNumber(), obj.getExpiryDate(), obj.getBankBranch().getId() == 0 ? null : obj.getBankBranch().getId(),
            obj.getEndorsedAccount().getId() == 0 ? null : obj.getEndorsedAccount().getId(), obj.getAccountGuarantor(), obj.getDocumentNumber().getId() == 0 ? null : obj.getDocumentNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNo(),
            obj.getPaymentCity().getId(), obj.getCurrency().getId(),
            obj.getExchangeRate(), obj.getTotalMoney(), obj.getAccount().getId(), obj.getAccountNumber(), obj.getIbanNumber(),
            obj.getStatus().getId(), obj.getBillCollocationDate(), obj.getCollectingBankAccount().getId() == 0 ? null : obj.getCollectingBankAccount().getId(), sessionBean.getUser().getId(), obj.getBranch().getId()};
//         System.out.println("------"+sql);
//        System.out.println("---------"+Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(ChequeBill chequeBill) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {8, chequeBill.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(ChequeBill obj) {
        String sql = "SELECT * FROM finance.process_chequebill (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{2, obj.getId(), obj.isIsCheque(), obj.isIsCustomer(), obj.getPortfolioNumber(), obj.getExpiryDate(), obj.getBankBranch().getId() == 0 ? null : obj.getBankBranch().getId(),
            obj.getEndorsedAccount().getId() == 0 ? null : obj.getEndorsedAccount().getId(), obj.getAccountGuarantor(), obj.getDocumentNumber().getId() == 0 ? null : obj.getDocumentNumber().getId(), obj.getDocumentSerial(), obj.getDocumentNo(),
            obj.getPaymentCity().getId(), obj.getCurrency().getId(),
            obj.getExchangeRate(), obj.getTotalMoney(), obj.getAccount().getId(), obj.getAccountNumber(), obj.getIbanNumber(),
            obj.getStatus().getId(), obj.getBillCollocationDate(), obj.getCollectingBankAccount().getId() == 0 ? null : obj.getCollectingBankAccount().getId(), sessionBean.getUser().getId(), obj.getBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
