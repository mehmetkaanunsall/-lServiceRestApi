/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:27:58 AM
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountCard;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountCardMapper implements RowMapper<AccountCard> {

    @Override
    public AccountCard mapRow(ResultSet rs, int i) throws SQLException {
        AccountCard accountCard = new AccountCard();

        accountCard.setId(rs.getInt("crdid"));
        accountCard.getAccount().setId(rs.getInt("crdaccountid"));
        accountCard.setRfNo(rs.getString("crdrfno"));
        accountCard.getStatus().setId(rs.getInt("crdstatus_id"));
        accountCard.getStatus().setTag(rs.getString("sttdname"));

        return accountCard;
    }

}
