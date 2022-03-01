package com.mepsan.marwiz.general.salarypayment.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class SalaryPaymentDao extends JdbcDaoSupport implements ISalaryPaymentDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<EmployeeInfo> findAllEmployee() {

        String sql = "SELECT \n"
                  + "      ei.id as eiid,\n"
                  + "      ei.account_id as accid,\n"
                  + "      CONCAT(COALESCE(acc.name,'-'),' ',COALESCE(acc.title,'')) as accname,\n"
                  + "      COALESCE(ei.exactsalary,0) as eiexactsalary,\n"
                  + "      COALESCE(ei.agi,0) as eiagi,\n"
                  + "      SUM(CASE WHEN amv.is_direction = FALSE THEN -(COALESCE(amv.price,0)*amv.exchangerate) ELSE (COALESCE(amv.price,0)*amv.exchangerate) END) as amvdebt,\n"
                  + "      COALESCE(ei.exactsalary,0)+COALESCE(ei.agi,0)+(SUM(CASE WHEN amv.is_direction = FALSE THEN -(COALESCE(amv.price,0)*amv.exchangerate) ELSE (COALESCE(amv.price,0)*amv.exchangerate) END)) as eisalarytobepaid\n"
                  + "    FROM general.employeeinfo ei\n"
                  + "    INNER JOIN general.account acc ON (acc.id=ei.account_id AND acc.deleted=FALSE)\n"
                  + "    LEFT JOIN general.accountmovement amv ON(amv.account_id=acc.id AND amv.deleted=FALSE AND amv.branch_id = ?)\n"
                  + "    INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                  + "    WHERE ei.deleted=FALSE \n"
                  + "    AND acc.is_employee=TRUE \n"
                  + "    AND acc.status_id=5\n"
                  + "    AND ei.branch_id = ?\n"
                  + "    GROUP BY ei.id,acc.name,acc.title\n"
                  + "    ORDER BY accname";

        Object[] params = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<EmployeeInfo> result = getJdbcTemplate().query(sql, params, new SalaryPaymentMapper());
        return result;
    }

    /**
     * Bu metod maaşı ödenebilir olan personellerin maaşlarının ödenmesini borç
     * tahsil edilmesini veya alacaklının alacağının verilmesini sağlar.
     *
     * @param financingDocument
     * @param safe
     * @param bankAccount
     * @param incomeExpense
     * @param isDebt
     * @param listPayableOrDebt
     * @param listAccount
     * @return
     */
    @Override
    public int createFinancingDocument(FinancingDocument financingDocument, Safe safe, BankAccount bankAccount, List<EmployeeInfo> listPayableOrDebt, boolean isDebt, List<EmployeeInfo> listAccount, int whichAction) {

        String sql = "SELECT * FROM finance.insert_financingdocument(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    if (listPayableOrDebt.get(i).getAccountMovement().getBalance().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) < 0) {
                        listPayableOrDebt.get(i).getAccountMovement().setBalance(listPayableOrDebt.get(i).getAccountMovement().getBalance().multiply(new BigDecimal("-1")));
                    }

                    ps.setInt(1, financingDocument.getFinancingType().getId());

                    /* if (incomeExpense.getId() == 0) {
                        ps.setNull(2, java.sql.Types.NULL);
                    } else {
                        ps.setInt(2, incomeExpense.getId());
                    }*/
                    ps.setNull(2, java.sql.Types.NULL);

                    ps.setString(3, financingDocument.getDocumentNumber());

                    if (whichAction == 0 || (whichAction == 2 && safe == null && bankAccount == null)) {
                        ps.setBigDecimal(4, listAccount.get(i).getExactsalary().add(new BigDecimal(listAccount.get(i).getAgi())));
                    } else {
                        ps.setBigDecimal(4, isDebt == false ? listPayableOrDebt.get(i).getAccountMovement().getPrice() : listPayableOrDebt.get(i).getAccountMovement().getBalance());//price ?? her personelin ödenecek maaşı gelmeli
                    }

                    ps.setInt(5, sessionBean.getUser().getLastBranch().getCurrency().getId());
                    ps.setBigDecimal(6, BigDecimal.ONE);//exc rate
                    ps.setTimestamp(7, new Timestamp(financingDocument.getDocumentDate().getTime()));
                    ps.setString(8, financingDocument.getDescription());
                    ps.setBoolean(9, Boolean.FALSE);//chequebill mi 

                    // if ((safe == null && bankAccount == null)) {
                    //     ps.setNull(10, java.sql.Types.NULL);
                    //} 
                    // else {
                    ps.setInt(10, ((whichAction == 0 || whichAction == 2) && listAccount.size() > 0) ? listAccount.get(i).getAccount().getId()
                              : financingDocument.getFinancingType().getId() == 48
                              || financingDocument.getFinancingType().getId() == 56 ? listPayableOrDebt.get(i).getAccount().getId()
                              : (safe == null && bankAccount == null) ? null : safe.getId() != 0 ? safe.getId() : bankAccount.getId());//inmov
                    // }
                    if ((safe == null && bankAccount == null)) {
                        ps.setNull(11, java.sql.Types.NULL);
                    } else {
                        ps.setInt(11, financingDocument.getFinancingType().getId() == 48 || financingDocument.getFinancingType().getId() == 56 ? safe.getId() != 0 ? safe.getId() : bankAccount.getId() : listPayableOrDebt.get(i).getAccount().getId());//outmov
                    }
                    ps.setInt(12, sessionBean.getUser().getLastBranch().getId());//ekleyecek kisi
                    ps.setNull(13, java.sql.Types.NULL);
                    ps.setInt(14, sessionBean.getUser().getId());//ekleyecek kisi
                }

                @Override
                public int getBatchSize() {
                    return listPayableOrDebt.size();
                }
            });
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
        return 1;
    }

}
