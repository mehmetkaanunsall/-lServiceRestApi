/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 05:07:33
 */
package com.mepsan.marwiz.general.creditcardmachine.dao;

import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CreditCardMachineMapper implements RowMapper<CreditCardMachine> {

    @Override
    public CreditCardMachine mapRow(ResultSet rs, int i) throws SQLException {
        CreditCardMachine creditCardMachine = new CreditCardMachine();
        creditCardMachine.setId(rs.getInt("ccmid"));
        creditCardMachine.setName(rs.getString("ccmname"));
        creditCardMachine.setCode(rs.getString("ccmcode"));
        creditCardMachine.getBankAccount().setId(rs.getInt("ccmbankaccount_id"));
        creditCardMachine.getBankAccount().setName(rs.getString("bkaname"));
        creditCardMachine.getStatus().setId(rs.getInt("ccmstatus_id"));
        creditCardMachine.getStatus().setTag(rs.getString("sttdname"));

        return creditCardMachine;
    }

}