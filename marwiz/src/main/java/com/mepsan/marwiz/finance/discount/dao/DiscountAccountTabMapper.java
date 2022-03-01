/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:23:57
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DiscountAccountTabMapper implements RowMapper<DiscountAccountConnection> {

    @Override
    public DiscountAccountConnection mapRow(ResultSet rs, int i) throws SQLException {

        DiscountAccountConnection accountConnection = new DiscountAccountConnection();

        accountConnection.setId(rs.getInt("dacid"));
        accountConnection.getDiscount().setId(rs.getInt("dacdiscount_id"));
        accountConnection.getAccount().setId(rs.getInt("dacaccount_id"));
        accountConnection.getAccount().setName(rs.getString("accname"));
        accountConnection.getAccountCategorization().setId(rs.getInt("dacaccountcategorization_id"));
        accountConnection.getAccountCategorization().setName(rs.getString("ctgname"));
        accountConnection.setUserCreated(new UserData());
        accountConnection.getUserCreated().setUsername(rs.getString("usrusername"));
        accountConnection.setDateCreated(rs.getTimestamp("dacc_time"));
        accountConnection.getUserCreated().setName(rs.getString("usrname"));
        accountConnection.getUserCreated().setSurname(rs.getString("usrsurname"));

        return accountConnection;
    }

}
