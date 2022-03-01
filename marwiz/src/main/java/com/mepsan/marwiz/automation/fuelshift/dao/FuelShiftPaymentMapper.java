/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   24.01.2019 02:21:40
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FuelShiftPaymentMapper implements RowMapper<ShiftPayment> {
    
    @Override
    public ShiftPayment mapRow(ResultSet rs, int i) throws SQLException {
        ShiftPayment shiftPayment = new ShiftPayment();
        
        shiftPayment.setId(rs.getInt("spid"));
        shiftPayment.setProcessDate(rs.getTimestamp("spprocessdate"));
        
        shiftPayment.getFinancingDocument().setId(rs.getInt("fdocid"));
        shiftPayment.getFinancingDocument().setPrice(rs.getBigDecimal("spprice"));
        shiftPayment.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
        
        shiftPayment.getCredit().setId(rs.getInt("spcredit_id"));
        
        shiftPayment.getSafe().setId(rs.getInt("spsafeid"));
        shiftPayment.getSafe().setName(rs.getString("sfname"));
        
        shiftPayment.getBankAccount().setId(rs.getInt("spbankaccountid"));
        shiftPayment.getBankAccount().setName(rs.getString("baname"));
        
        shiftPayment.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        shiftPayment.getAccount().setName(rs.getString("accname"));
        shiftPayment.getAccount().setTitle(rs.getString("acctitle"));
        shiftPayment.getAccount().setId(rs.getInt("spaccountid"));
        
        shiftPayment.getCredit().getAccount().setIsEmployee(rs.getBoolean("acc1is_employee"));
        shiftPayment.getCredit().getAccount().setName(rs.getString("acc1name"));
        shiftPayment.getCredit().getAccount().setTitle(rs.getString("acc1title"));
        shiftPayment.getCredit().getAccount().setId(rs.getInt("acc1id"));
        shiftPayment.getCredit().setDueDate(rs.getTimestamp("crduedate"));
        
        shiftPayment.getFinancingDocument().getAccount().setIsEmployee(rs.getBoolean("acc2is_employee"));
        shiftPayment.getFinancingDocument().getAccount().setName(rs.getString("acc2name"));
        shiftPayment.getFinancingDocument().getAccount().setTitle(rs.getString("acc2title"));
        
        shiftPayment.getFinancingDocument().getIncomeExpense().setId(rs.getInt("fiemincomeexpense_id"));
        shiftPayment.getFinancingDocument().getIncomeExpense().setName(rs.getString("fiename"));
        shiftPayment.getFinancingDocument().getIncomeExpense().setIsIncome(rs.getBoolean("fieis_income"));
        
        shiftPayment.getFuelSaleType().setId(rs.getInt("spfueltype_id"));
        shiftPayment.getFuelSaleType().setName(rs.getString("fstname"));
        shiftPayment.getFuelSaleType().setTypeno(rs.getInt("fsttypeno"));
        
        shiftPayment.setIsAutomation(rs.getBoolean("spisautomation"));
        
        try {
            shiftPayment.getFinancingDocument().getAccount().setId(rs.getInt("acc2id"));
        } catch (Exception e) {
        }
        
        return shiftPayment;
    }
    
}
