/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 17.02.2017 14:10:07
 */
package com.mepsan.marwiz.general.exchange.dao;

import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import java.util.Date;
import java.util.List;

public interface IExchangeDao {

    public void updateExchange(String xml);

    public Exchange bringExchangeRate(Currency currency, Currency responseCurrency, UserData userdata);

    public List<Exchange> findAll(UserData userdata);

    public List<Exchange> controlRecurringRecord();

}
