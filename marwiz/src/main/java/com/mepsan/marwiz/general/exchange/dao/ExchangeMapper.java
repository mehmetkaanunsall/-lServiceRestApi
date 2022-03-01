/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 20.02.2017 12:04:52
 */
package com.mepsan.marwiz.general.exchange.dao;

import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ExchangeMapper implements RowMapper<Exchange> {

    Currency currency, resCurrency;

    public ExchangeMapper(Currency currency, Currency resCurrency) {
        this.currency = currency;
        this.resCurrency = resCurrency;
    }

    public ExchangeMapper(Currency resCurrency) {
        this.resCurrency = resCurrency;
    }

    @Override
    public Exchange mapRow(ResultSet rs, int i) throws SQLException {
        Exchange exchange = new Exchange();

        exchange.setResponseCurrency(resCurrency);
        try {
            exchange.setId(rs.getInt("excid"));
            exchange.setSales(rs.getBigDecimal("excsales"));
        } catch (Exception e) {
        }

        try {
            exchange.setExchangeDate(rs.getTimestamp("excexchangedate"));
        } catch (Exception e) {
        }

        try {
            exchange.setBuying(rs.getBigDecimal("excbuying"));
        } catch (Exception e) {
        }

        try {
            exchange.getCurrency().setId(rs.getInt("exccurrency_id"));
            exchange.getCurrency().setSign(rs.getString("crdsign"));
            exchange.getCurrency().setTag(rs.getString("crrdname"));
            exchange.setDateCreated(rs.getTimestamp("excc_time"));
        } catch (Exception e) {
        }
        try {
            exchange.getCurrency().setCode(rs.getString("crdcode"));
        } catch (Exception e) {
        }
        try {
            exchange.getCurrency().setId(rs.getInt("crdid"));
        } catch (Exception e) {
        }
        if (exchange.getCurrency().getId() == 0) {
            exchange.setCurrency(currency);
        }
        
        try {
            exchange.setIsThereNowDate(rs.getInt("therenow"));
        } catch (Exception e) {
        }

        return exchange;

    }
}
