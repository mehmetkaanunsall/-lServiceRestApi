/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:32:17
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.model.finance.DiscountItem;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.User;

public class DiscountItemMapper implements RowMapper<DiscountItem> {

    @Override
    public DiscountItem mapRow(ResultSet rs, int i) throws SQLException {
        DiscountItem discountItem = new DiscountItem();

        discountItem.setId(rs.getInt("dsiid"));

        discountItem.getDiscount().setId(rs.getInt("dsidiscount_id"));
        discountItem.getDiscount().setName(rs.getString("dscname"));

        discountItem.getStock().setId(rs.getInt("dsistock_id"));
        discountItem.getStock().setName(rs.getString("stckname"));

        discountItem.getBrand().setId(rs.getInt("dsibrand_id"));
        discountItem.getBrand().setName(rs.getString("brndname"));

        discountItem.getPriceList().setId(rs.getInt("dsipricelist_id"));
        discountItem.getPriceList().setName(rs.getString("prlname"));

        discountItem.setDiscountRate(rs.getBigDecimal("dsidiscountrate"));
        discountItem.setDiscountAmount(rs.getBigDecimal("dsidiscountamount"));
        discountItem.setIsTaxIncluded(rs.getBoolean("dsiis_taxincluded"));
        discountItem.setSaleCount(rs.getInt("dsisalecount"));
        discountItem.setBeginDate(rs.getTimestamp("dsibegindate"));
        discountItem.setEndDate(rs.getTimestamp("dsienddate"));
        discountItem.setBeginPrice(rs.getBigDecimal("dsibeginprice"));
        discountItem.setEndPrice(rs.getBigDecimal("dsiendprice"));
        discountItem.setBeginTime(rs.getTime("dsibegintime"));
        discountItem.setEndTime(rs.getTime("dsiendtime"));
        discountItem.setSpecialDay(rs.getString("dsispecialday"));
        discountItem.setSpecialMonth(rs.getString("dsispecialmonth"));
        discountItem.setSpecialMonthDay(rs.getString("dsispecialmonthday"));
        discountItem.setIsDiscountCode(rs.getBoolean("dsiis_discountcode"));
        discountItem.setNecessaryStocks(rs.getString("dsinecessarystocks"));
        discountItem.setPromotionStocks(rs.getString("dsipromotionstocks"));
        discountItem.setNecessaryBrands(rs.getString("dsinecessarybrands"));
        discountItem.setPromotionBrands(rs.getString("dsipromotionbrands"));

        try {
            discountItem.setUserCreated(new UserData());
            discountItem.getUserCreated().setId(rs.getInt("dsic_id"));
            discountItem.getUserCreated().setUsername(rs.getString("usrusername"));
            discountItem.setDateCreated(rs.getTimestamp("dsic_time"));
            discountItem.getUserCreated().setName(rs.getString("usrname"));
            discountItem.getUserCreated().setSurname(rs.getString("usrsurname"));
        } catch (Exception e) {

        }

        return discountItem;

    }
}
