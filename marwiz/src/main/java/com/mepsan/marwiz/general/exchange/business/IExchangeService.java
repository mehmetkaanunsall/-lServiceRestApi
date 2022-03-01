/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 17.02.2017 14:11:09
 */
package com.mepsan.marwiz.general.exchange.business;

import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.List;

public interface IExchangeService {

    public Exchange updateExchange();

    public BigDecimal bringExchangeRate(Currency currency, Currency responseCurrency, UserData userdata);

    public List<Exchange> findAll(UserData userdata);

    public List<Exchange> controlRecurringRecord();

}
