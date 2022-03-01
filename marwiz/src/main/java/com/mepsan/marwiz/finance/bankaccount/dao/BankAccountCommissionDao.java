/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.08.2020 08:48:15
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankAccountCommissionDao extends JdbcDaoSupport implements IBankAccountCommissionDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int createCommission(BankAccountCommission bankAccountCommission, String jsonFinancingDocument, String jsonCommissionFinancingDocument) {
        String sql = "SELECT r_bankaccountcommission_id FROM finance.process_bankaccountcommission (?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{0, bankAccountCommission.getId(), bankAccountCommission.getTotalMoney(), bankAccountCommission.getCommissionRate(),
            bankAccountCommission.getCommissionMoney(), jsonFinancingDocument, jsonCommissionFinancingDocument, sessionBean.getUser().getId()};

        System.out.println("-------Arraty" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateCommission(BankAccountCommission bankAccountCommission, String jsonFinancingDocument, String jsonCommissionFinancingDocument) {
        String sql = "SELECT r_bankaccountcommission_id FROM finance.process_bankaccountcommission (?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{1, bankAccountCommission.getId(), bankAccountCommission.getTotalMoney(), bankAccountCommission.getCommissionRate(),
            bankAccountCommission.getCommissionMoney(), jsonFinancingDocument, jsonCommissionFinancingDocument, sessionBean.getUser().getId()};

        System.out.println("----update---Array" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int deleteCommission(BankAccountCommission bankAccountCommission) {
        String sql = "SELECT r_bankaccountcommission_id FROM finance.process_bankaccountcommission (?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{2, bankAccountCommission.getId(), bankAccountCommission.getTotalMoney(), bankAccountCommission.getCommissionRate(),
            bankAccountCommission.getCommissionMoney(), null, null, sessionBean.getUser().getId()};

        System.out.println("----delete---Array" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public BankAccountCommission findBankAccountCommission(int bankAccountCommissionId) {
        String sql = "SELECT \n"
                  + "	 bac.id AS bacid,\n"
                  + "    bac.financingdocument_id AS bacfinancingdocument_id,\n"
                  + "    fdoc.type_id AS fdoctype_id,\n"
                  + "    fdoc.documentnumber AS fdocdocumentnumber,\n"
                  + "    fdoc.price AS fdocprice,\n"
                  + "    fdoc.currency_id AS fdoccurrency_id,\n"
                  + "    fdoc.exchangerate AS fdocexchangerate,\n"
                  + "    fdoc.documentdate AS fdocdocumentdate,\n"
                  + "    fdoc.description AS fdocdescription,\n"
                  + "    bam.bankaccount_id AS bambankaccount_id,--in\n"
                  + "    bam2.bankaccount_id AS bam2bankaccount_id,--out\n"
                  + "    fdoc.branch_id AS fdocbranch_id,\n"
                  + "    fdoc.transferbranch_id AS fdoctransferbranch_id,\n"
                  + "    bac.commissionfinancingdocument_id AS baccommissionfinancingdocument_id,\n"
                  + "    fdoc2.type_id AS fdoc2type_id,\n"
                  + "    fdoc2.documentnumber AS fdoc2documentnumber,\n"
                  + "    fdoc2.price AS fdoc2price,\n"
                  + "    fdoc2.currency_id AS fdoc2currency_id,\n"
                  + "    fdoc2.exchangerate AS fdoc2exchangerate,\n"
                  + "    fdoc2.documentdate AS fdoc2documentdate,\n"
                  + "    fdoc2.description AS fdoc2description,\n"
                  + "    inxm.incomeexpense_id AS inxmincomeexpense_id, --in\n"
                  + "    bam3.bankaccount_id AS bam3bankaccount_id, --out\n"
                  + "    fdoc2.branch_id AS fdoc2branch_id,\n"
                  + "    fdoc2.transferbranch_id AS fdoc2transferbranch_id,\n"
                  + "    bac.totalmoney AS bactotalmoney,\n"
                  + "    bac.commissionrate AS baccommissionrate,\n"
                  + "    bac.commissionmoney AS baccommissionmoney\n"
                  + "FROM finance.bankaccountcommission bac\n"
                  + "INNER JOIN finance.financingdocument fdoc ON(fdoc.id = bac.financingdocument_id AND fdoc.deleted=FALSE)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam  ON(bam.financingdocument_id=fdoc.id AND bam.is_direction=TRUE)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam2  ON(bam2.financingdocument_id=fdoc.id AND bam2.is_direction=FALSE)\n"
                  + "INNER JOIN finance.financingdocument fdoc2 ON(fdoc2.id = bac.commissionfinancingdocument_id AND fdoc2.deleted=FALSE)\n"
                  + "LEFT JOIN finance.incomeexpensemovement inxm ON(inxm.financingdocument_id = fdoc2.id AND inxm.deleted=False)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam3  ON(bam3.financingdocument_id=fdoc2.id AND bam3.is_direction=FALSE)\n"
                  + "WHERE bac.deleted=FALSE AND bac.id = ?";

        Object[] param = {bankAccountCommissionId};
        List<BankAccountCommission> result = getJdbcTemplate().query(sql, param, new BankAccountCommissionMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BankAccountCommission();
        }
    }

}
