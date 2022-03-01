/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:23:57
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.model.finance.DiscountBranchConnection;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DiscountBranchTabMapper implements RowMapper<DiscountBranchConnection> {

    @Override
    public DiscountBranchConnection mapRow(ResultSet rs, int i) throws SQLException {

        DiscountBranchConnection branchConnection = new DiscountBranchConnection();
        branchConnection.setId(rs.getInt("dbcid"));
        branchConnection.getDiscount().setId(rs.getInt("dbcdiscount_id"));
        branchConnection.getDiscount().setName(rs.getString("dscname"));
        branchConnection.getBranch().setId(rs.getInt("dbcbranch_id"));
        branchConnection.getBranch().setName(rs.getString("brname"));
        branchConnection.setUserCreated(new UserData());
        branchConnection.getUserCreated().setUsername(rs.getString("usrusername"));
        branchConnection.setDateCreated(rs.getTimestamp("dbcc_time"));
        branchConnection.getUserCreated().setName(rs.getString("usrname"));
        branchConnection.getUserCreated().setSurname(rs.getString("usrsurname"));
        return branchConnection;
    }

}
