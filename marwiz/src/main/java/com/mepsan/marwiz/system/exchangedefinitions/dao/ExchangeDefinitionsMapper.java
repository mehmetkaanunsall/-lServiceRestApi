/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.dao;

import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author sinem.arslan
 */
public class ExchangeDefinitionsMapper implements RowMapper<Currency> {

    @Override
    public Currency mapRow(ResultSet rs, int i) throws SQLException {

        Currency currency = new Currency();

        currency.setCode(rs.getString("crycode"));
        currency.setSign(rs.getString("crysign"));
        currency.setInternationalCode(rs.getString("cryinternationalcode"));
        currency.setConversionRate(rs.getBigDecimal("cryconversionrate"));
        currency.setLimitUp(rs.getBigDecimal("crylimitup"));
        currency.setId(rs.getInt("cryid"));

        return currency;
    }

}
