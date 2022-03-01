/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.cardtype.dao;

import com.mepsan.marwiz.general.model.automation.FuelCardType;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author asli.can
 */
public class CardTypeMapper implements RowMapper<FuelCardType> {

    @Override
    public FuelCardType mapRow(ResultSet rs, int i) throws SQLException {
        FuelCardType cardType = new FuelCardType();

        cardType.setId(rs.getInt("fctid"));
        cardType.setName(rs.getString("fctname"));
        cardType.setTypeNo(rs.getInt("fcttypeno"));
        cardType.getSaleType().setName(rs.getString("fstname"));
        cardType.getSaleType().setTypeno(rs.getInt("fsttypeno"));
        cardType.getAccount().setId(rs.getInt("fctaccount_id"));
        cardType.getBankacount().setId(rs.getInt("fctbankaccoun_id"));
        cardType.getSaleType().setId(rs.getInt("fctfuelsaletype_id"));
        cardType.getAccount().setName(rs.getString("accname"));

        return cardType;
    }

}
