/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 11:10:57
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.AccountNote;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountNoteMapper implements RowMapper<AccountNote> {

    @Override
    public AccountNote mapRow(ResultSet rs, int i) throws SQLException {
        AccountNote accountNote = new AccountNote();
        accountNote.setId(rs.getInt("accntid"));
        accountNote.setName(rs.getString("accntname"));
        accountNote.setDescription(rs.getString("accntdescription"));

        return accountNote;
    }

}