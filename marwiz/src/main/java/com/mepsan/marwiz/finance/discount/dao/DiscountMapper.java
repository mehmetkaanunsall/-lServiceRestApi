/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:58:25
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DiscountMapper implements RowMapper<Discount> {

    @Override
    public Discount mapRow(ResultSet rs, int i) throws SQLException {
        Discount discount = new Discount();

        discount.setId(rs.getInt("dscid"));
        discount.setName(rs.getString("dscname"));

        discount.setBeginDate(rs.getTimestamp("dscbegindate"));
        discount.setEndDate(rs.getTimestamp("dscenddate"));
        discount.getStatus().setId(rs.getInt("dscstatus_id"));
        discount.getStatus().setTag(rs.getString("sttdname"));
        discount.setIsAllCustomer(rs.getBoolean("dscis_allcustomer"));
        discount.setIsInvoice(rs.getBoolean("dscis_invoice"));
        discount.setIsAllBranch(rs.getBoolean("dscis_allbranch"));
        discount.setIsRetailCustomer(rs.getBoolean("dscis_retailcustomer"));
        discount.setDescription(rs.getString("dscdescription"));
        discount.setCentercampaign_id(rs.getInt("dsccentercampaign_id"));
        discount.setUserCreated(new UserData());
        discount.getUserCreated().setUsername(rs.getString("usrusername"));
        discount.setDateCreated(rs.getTimestamp("dscc_time"));
        discount.getUserCreated().setName(rs.getString("usrname"));
        discount.getUserCreated().setSurname(rs.getString("usrsurname"));
        
                return discount;

    }

}
