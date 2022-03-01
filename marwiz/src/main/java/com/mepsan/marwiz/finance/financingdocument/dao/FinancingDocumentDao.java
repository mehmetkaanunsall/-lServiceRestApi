/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 04:29:37
 */
package com.mepsan.marwiz.finance.financingdocument.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FinancingDocumentDao extends JdbcDaoSupport implements IFinancingDocumentDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FinancingDocument> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "fdoc.id";
            sortOrder = "desc";
        }

        String sql = "SELECT \n"
                  + "    fdoc.id as fdocid,  \n"
                  + "    fdoc.type_id as fdoctype_id,  \n"
                  + "    typd.name as typdname,  \n"
                  + "    fdoc.currency_id as fdoccurrency_id,  \n"
                  + "    fdoc.documentnumber as fdocdocumentnumber, \n"
                  + "    fdoc.price as fdocprice, \n"
                  + "    fdoc.exchangerate as fdocexchangerate, \n"
                  + "    fdoc.documentdate as fdocdocumentdate, \n"
                  + "    fdoc.description as fdocdescription, \n"
                  + "    inx.id as inxid, \n"
                  + "    inx.name as inxname,\n"
                  + "    usd.id AS usid, \n"
                  + "    usd.name AS name,\n"
                  + "    usd.surname AS surname,\n"
                  + "    CASE \n"
                  + "            WHEN bam.bankaccount_id>0 THEN bam.bankaccount_id \n"
                  + "            WHEN sfm.safe_id>0 THEN sfm.safe_id \n"
                  + "            ELSE acm.account_id \n"
                  + "        END as inmovementid, \n"
                  + "    CASE \n"
                  + "            WHEN bam2.bankaccount_id>0 THEN bam2.bankaccount_id \n"
                  + "            WHEN sfm2.safe_id>0 THEN sfm2.safe_id \n"
                  + "            ELSE acm.account_id \n"
                  + "        END as outmovementid, \n"
                  + "COALESCE(acc.is_person,cacc.is_person) as accis_person, \n"
                  + "COALESCE(acc.name,cacc.name) as accname, \n"
                  + "COALESCE(acc.id,cacc.id) as accid,\n"
                  + "COALESCE(acc.taxoffice,cacc.taxoffice) as acctaxoffice,\n"
                  + "COALESCE(acc.taxno,cacc.taxno) as acctaxno,\n"
                  + "COALESCE(acc.phone,cacc.phone) as accphone,\n"
                  + "COALESCE(acc.title,cacc.title) as acctitle,\n"
                  + "COALESCE(acc.is_employee,cacc.is_employee) as accis_employee,\n"
                  + "fdoc.branch_id AS fdocbranch_id,\n"
                  + "fdoc.transferbranch_id AS fdoctransferbranch_id,\n"
                  + "bac.id AS bacid\n"
                  + "FROM finance.financingdocument fdoc \n"
                  + "    LEFT JOIN finance.chequebillpayment cp ON(cp.financingdocument_id = fdoc.id AND cp.deleted = FALSE)\n"
                  + "    LEFT JOIN finance.chequebill cb ON(cb.id = cp.chequebill_id)  \n"
                  + "    LEFT JOIN general.account cacc  ON (cacc.id=cb.account_id)  \n"
                  + "    LEFT JOIN finance.incomeexpensemovement inxm ON (inxm.financingdocument_id = fdoc.id)  \n"
                  + "    LEFT JOIN finance.incomeexpense inx ON (inx.id = inxm.incomeexpense_id)  \n"
                  + "    INNER JOIN system.type_dict typd   ON (typd.type_id=fdoc.type_id AND typd.language_id=?)  \n"
                  + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=fdoc.currency_id AND crrd.language_id=?)  \n"
                  + "    LEFT JOIN (\n"
                  + "               SELECT\n"
                  + "                yy.*,\n"
                  + "                COUNT(yy.financingdocument_id) OVER (PARTITION BY yy.financingdocument_id) AS cnt\n"
                  + "            FROM\n"
                  + "                general.accountmovement yy) acm\n"
                  + "           ON (acm.financingdocument_id = fdoc.id AND acm.account_id <> CASE WHEN acm.cnt > 1 THEN 1 ELSE 0 END ) \n"
                  + "    LEFT JOIN finance.safemovement sfm   ON(sfm.financingdocument_id=fdoc.id AND sfm.is_direction=true) \n"
                  + "    LEFT JOIN finance.safemovement sfm2 ON(sfm2.financingdocument_id=fdoc.id AND sfm2.is_direction=FALSE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id=fdoc.id AND bam.is_direction=TRUE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam2 ON(bam2.financingdocument_id=fdoc.id AND bam2.is_direction=false) \n"
                  + "    LEFT JOIN general.account acc  ON (acc.id=acm.account_id) \n"
                  + "     LEFT JOIN general.userdata usd ON(usd.id = fdoc.c_id)\n"
                  + "    LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                  + " WHERE fdoc.deleted =  false " + where
                  + " ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, param, new FinancingDocumentMapper());
    }

    @Override
    public int count(String where) {
        String sql = " SELECT \n"
                  + "    count(fdoc.id) as fdocid  \n"
                  + "   \n"
                  + " FROM   \n"
                  + "   finance.financingdocument fdoc \n"
                  + "    LEFT JOIN finance.chequebillpayment cp ON(cp.financingdocument_id = fdoc.id AND cp.deleted = FALSE)\n"
                  + "    LEFT JOIN finance.chequebill cb ON(cb.id = cp.chequebill_id)  \n"
                  + "    LEFT JOIN general.account cacc  ON (cacc.id=cb.account_id)  \n"
                  + "    LEFT JOIN finance.incomeexpensemovement inxm ON (inxm.financingdocument_id = fdoc.id)  \n"
                  + "    LEFT JOIN finance.incomeexpense inx ON (inx.id = inxm.incomeexpense_id)  \n"
                  + "    INNER JOIN system.type_dict typd   ON (typd.type_id=fdoc.type_id AND typd.language_id=?)  \n"
                  + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=fdoc.currency_id AND crrd.language_id=?)  \n"
                  + "      LEFT JOIN (\n"
                  + "               SELECT\n"
                  + "                yy.*,\n"
                  + "                COUNT(yy.financingdocument_id) OVER (PARTITION BY yy.financingdocument_id) AS cnt\n"
                  + "            FROM\n"
                  + "                general.accountmovement yy) acm\n"
                  + "           ON (acm.financingdocument_id = fdoc.id AND acm.account_id <> CASE WHEN acm.cnt > 1 THEN 1 ELSE 0 END ) \n"
                  + "    LEFT JOIN finance.safemovement sfm   ON(sfm.financingdocument_id=fdoc.id AND sfm.is_direction=true) \n"
                  + "    LEFT JOIN finance.safemovement sfm2 ON(sfm2.financingdocument_id=fdoc.id AND sfm2.is_direction=FALSE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id=fdoc.id AND bam.is_direction=TRUE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam2 ON(bam2.financingdocument_id=fdoc.id AND bam2.is_direction=false) \n"
                  + "    LEFT JOIN general.account acc  ON (acc.id=acm.account_id) \n"
                  + "     LEFT JOIN general.userdata usd ON(usd.id = fdoc.c_id) \n"
                  + " WHERE fdoc.deleted =  false " + where;
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return result;
    }

    @Override
    public FinancingDocument findFinancingDocument(FinancingDocument fd) {
        String sql = " SELECT \n"
                  + "    fdoc.id as fdocid,  \n"
                  + "    fdoc.type_id as fdoctype_id,  \n"
                  + "    typd.name as typdname,  \n"
                  + "    fdoc.currency_id as fdoccurrency_id,  \n"
                  + "    fdoc.documentnumber as fdocdocumentnumber, \n"
                  + "    fdoc.price as fdocprice, \n"
                  + "    fdoc.exchangerate as fdocexchangerate, \n"
                  + "    fdoc.documentdate as fdocdocumentdate, \n"
                  + "    fdoc.description as fdocdescription, \n"
                  + "    inx.id as inxid, \n"
                  + "    inx.name as inxname,\n"
                  + "    CASE \n"
                  + "            WHEN bam.bankaccount_id>0 THEN bam.bankaccount_id \n"
                  + "            WHEN sfm.safe_id>0 THEN sfm.safe_id \n"
                  + "            ELSE acm.account_id \n"
                  + "        END as inmovementid, \n"
                  + "    CASE \n"
                  + "            WHEN bam2.bankaccount_id>0 THEN bam2.bankaccount_id \n"
                  + "            WHEN sfm2.safe_id>0 THEN sfm2.safe_id \n"
                  + "            ELSE acm.account_id \n"
                  + "        END as outmovementid, \n"
                  + "    acc.is_person as accis_person, \n"
                  + "    acc.name as accname, \n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    acc.id as accid, \n"
                  + "    fdoc.branch_id AS fdocbranch_id,\n"
                  + "    fdoc.transferbranch_id AS fdoctransferbranch_id,\n"
                  + "    bac.id AS bacid\n"
                  + " FROM   \n"
                  + "    finance.financingdocument fdoc  \n"
                  + "    INNER JOIN system.type_dict typd  ON (typd.type_id=fdoc.type_id AND typd.language_id=?)  \n"
                  + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=fdoc.currency_id AND crrd.language_id=?)  \n"
                  + "    LEFT JOIN (\n"
                  + "               SELECT\n"
                  + "                yy.*,\n"
                  + "                COUNT(yy.financingdocument_id) OVER (PARTITION BY yy.financingdocument_id) AS cnt\n"
                  + "            FROM\n"
                  + "                general.accountmovement yy) acm\n"
                  + "           ON (acm.financingdocument_id = fdoc.id AND acm.account_id <> CASE WHEN acm.cnt > 1 THEN 1 ELSE 0 END )\n"
                  + "    LEFT JOIN finance.safemovement sfm   ON(sfm.financingdocument_id=fdoc.id AND sfm.is_direction=TRUE) \n"
                  + "    LEFT JOIN finance.safemovement sfm2  ON(sfm2.financingdocument_id=fdoc.id AND sfm2.is_direction=FALSE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam  ON(bam.financingdocument_id=fdoc.id AND bam.is_direction=TRUE) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam2  ON(bam2.financingdocument_id=fdoc.id AND bam2.is_direction=FALSE) \n"
                  + "    LEFT JOIN finance.incomeexpensemovement inxm ON(inxm.financingdocument_id = fdoc.id AND inxm.deleted=False)\n "
                  + "    LEFT JOIN finance.incomeexpense inx ON(inx.id = inxm.incomeexpense_id)\n "
                  + "    LEFT JOIN general.account acc  ON (acc.id=acm.account_id) "
                  + "    LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                  + " WHERE  fdoc.deleted = false AND fdoc.id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), fd.getId()};
        return getJdbcTemplate().queryForObject(sql, param, new FinancingDocumentMapper());
    }

    @Override
    public int create(FinancingDocument obj, int inmovementId, int outmovementId) {
        String sql = "SELECT * FROM finance.insert_financingdocument (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{obj.getFinancingType().getId(), obj.getIncomeExpense().getId() == 0 ? null : obj.getIncomeExpense().getId(), obj.getDocumentNumber(),
            obj.getPrice(), obj.getCurrency().getId(), obj.getExchangeRate(), obj.getDocumentDate(), obj.getDescription(), false,
            outmovementId, inmovementId, obj.getBranch().getId(), obj.getTransferBranch().getId() == 0 ? null : obj.getTransferBranch().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(FinancingDocument obj, int inmovementId, int outmovementId) {

        String sql = "SELECT * FROM finance.update_financingdocument (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{obj.getFinancingType().getId(), obj.getId(), obj.getIncomeExpense().getId() == 0 ? false : true, false, obj.getDocumentNumber(),
            obj.getPrice(), obj.getCurrency().getId(), obj.getExchangeRate(), obj.getDocumentDate(), obj.getDescription(),
            outmovementId, inmovementId, sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<FinancingDocumentVoucher> listOfVancourDetail(FinancingDocument obj) {
        String sql = " SELECT \n"
                  + "fdoc.id as fdocid,  \n"
                  + "fdoc.type_id as fdoctype_id,  \n"
                  + "fdoc.currency_id as fdoccurrency_id,  \n"
                  + "fdoc.documentnumber as fdocdocumentnumber, \n"
                  + "fdoc.price as fdocprice, \n"
                  + "fdoc.exchangerate as fdocexchangerate, \n"
                  + "fdoc.documentdate as fdocdocumentdate, \n"
                  + "fdoc.description as fdocdescription, \n"
                  + " bam.bankaccount_id as bambankaccount_id,\n"
                  + "ba.name AS baname,\n"
                  + "ba.bankbranch_id AS babankbranch_id,\n"
                  + "ba.accountnumber AS baaccountnumber, \n"
                  + "bb.name AS bbname ,\n"
                  + "bb.bank_id AS bankabank_id,\n"
                  + "bnk.name AS bankaname,\n"
                  + "COALESCE(acc.is_person,cacc.is_person) as accis_person, \n"
                  + "COALESCE(acc.name,cacc.name) as accname, \n"
                  + "COALESCE(acc.id,cacc.id) as accid,\n"
                  + "COALESCE(acc.taxoffice,cacc.taxoffice) as acctaxoffice,\n"
                  + "COALESCE(acc.taxno,cacc.taxno) as acctaxno,\n"
                  + "COALESCE(acc.phone,cacc.phone) as accphone,\n"
                  + "cb.id AS cbid,\n"
                  + "cb.portfolionumber AS cbportfolionumber,\n"
                  + "cb.expirydate AS cbexpirydate,\n"
                  + "COALESCE(fdoc.price* COALESCE(fdoc.exchangerate,1),0) AS cpprice,\n"
                  + "cb.accountnumber AS cbaccountnumber,\n"
                  + "usd.id AS usid, \n"
                  + "usd.name AS name,\n"
                  + "usd.surname AS surname ,"
                  + "cb.documentnumber as cbdocumentnumber ,\n"
                  + "cb.documentserial as documentserial,\n"
                  + "fdoc.branch_id AS fdocbranch_id,\n"
                  + "fdoc.transferbranch_id AS fdoctransferbranch_id,\n"
                  + "bac.id AS bacid\n"
                  + "FROM \n"
                  + "    finance.financingdocument fdoc \n"
                  + "    LEFT JOIN finance.chequebillpayment cp ON(cp.financingdocument_id = fdoc.id AND cp.deleted = FALSE)\n"
                  + "    LEFT JOIN finance.chequebill cb ON(cb.id = cp.chequebill_id)  \n"
                  + "    LEFT JOIN general.account cacc  ON (cacc.id=cb.account_id)  \n"
                  + "    INNER JOIN system.currency_dict crrd  ON (crrd.currency_id=fdoc.currency_id AND crrd.language_id=?)  \n"
                  + "    LEFT JOIN general.accountmovement acm   ON(acm.financingdocument_id=fdoc.id) \n"
                  + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id=fdoc.id)\n"
                  + "    LEFT JOIN finance.bankaccount ba ON(ba.id =bam.bankaccount_id  ) \n"
                  + "    LEFT JOIN finance.bankbranch bb ON(bb.id =ba.bankbranch_id  ) \n"
                  + "    LEFT JOIN finance.bank bnk ON(bnk.id =bb.bank_id) \n"
                  + "    LEFT JOIN general.account acc  ON (acc.id=acm.account_id)  \n"
                  + "    LEFT JOIN general.userdata usd ON(usd.id = fdoc.c_id)  \n "
                  + "    LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                  + "WHERE\n"
                  + "\n"
                  + "fdoc.id = ? AND (fdoc.branch_id = ? OR fdoc.transferbranch_id = ?)";
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId(), obj.getBranch().getId(), obj.getBranch().getId()};
        return getJdbcTemplate().query(sql, params, new FinancingDocumentVoucherMapper());

    }

    @Override
    public List<CheckDelete> testBeforeDelete(FinancingDocument financingDocument) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {1, financingDocument.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(FinancingDocument financingDocument) {
        String sql = "UPDATE finance.financingdocument SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                  + "UPDATE general.shift_transfer_con SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND financingdocument_id = ?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), financingDocument.getId(), sessionBean.getUser().getId(), financingDocument.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
