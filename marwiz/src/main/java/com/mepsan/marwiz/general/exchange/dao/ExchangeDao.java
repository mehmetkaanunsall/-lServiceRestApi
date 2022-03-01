/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 17.02.2017 14:10:25
 */
package com.mepsan.marwiz.general.exchange.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ExchangeDao extends JdbcDaoSupport implements IExchangeDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * gelen xml verisini veritabanında exchange tablosuna ekler.
     *
     * @param xml
     */
    @Override
    public void updateExchange(String xml) {
        String sql = "SELECT r_exchange_id FROM finance.insert_exchange (? ) ";

        Object[] param = new Object[]{xml};
        try {
            getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
        }
    }

    /**
     * bu metot gelen iki currency arasındaki kuru döndürür.
     *
     * @param currency
     * @param responseCurrency
     * @param userdata
     * @return
     */
    @Override
    public Exchange bringExchangeRate(Currency currency, Currency responseCurrency, UserData userdata) {
        String sql = "SELECT\n"
                + "  exc.id as excid,\n"
                + "  exc.exchangedate as excexchangedate,\n"
                + "  COALESCE(exc.buying,1) as excbuying,\n"
                + "  COALESCE(exc.selling,1) as excsales\n"
                + "FROM finance.exchange exc \n"
                + "WHERE exc.currency_id = ? AND exc.responsecurrency_id = ? AND deleted=FALSE\n"
                + "ORDER BY exc.c_time DESC\n"
                + "LIMIT 1 ";

        Object[] param = new Object[]{currency.getId(), responseCurrency.getId()};
        List<Exchange> list = getJdbcTemplate().query(sql, param, new ExchangeMapper(currency, responseCurrency));

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return new Exchange();
        }
    }

    @Override
    public List<Exchange> findAll(UserData userdata) {

        String sql = "SELECT \n"
                + " 	exc.id as excid,\n"
                + "    exc.exchangedate as excexchangedate,\n"
                + "    exc.c_time as excc_time,\n"
                + "    exc.currency_id as exccurrency_id,\n"
                + "    crrd.name as crrdname,\n"
                + "    crd.sign as crdsign,\n"
                + "    exc.buying as excbuying,\n"
                + "    exc.selling as excsales\n"
                + "FROM finance.exchange exc   \n"
                + "INNER JOIN system.currency crd  ON (crd.id=exc.currency_id) \n"
                + "INNER JOIN system.currency_dict crrd  ON (crrd.currency_id = exc.currency_id AND crrd.language_id = ?) \n"
                + "WHERE  exc.responsecurrency_id = ? \n"
                + "AND exc.deleted = FALSE \n"
                + "ORDER BY exc.c_time DESC";

        Object[] param = new Object[]{userdata.getLanguage().getId(), userdata.getLastBranch().getCurrency().getId()};

        return getJdbcTemplate().query(sql, param, new ExchangeMapper(userdata.getLastBranch().getCurrency()));

    }

    @Override
    public List<Exchange> controlRecurringRecord() {

        String sql = "SELECT CASE WHEN EXISTS (SELECT excg.id FROM finance.exchange excg \n"
                + "    WHERE excg.deleted=FALSE AND TO_CHAR(excg.exchangedate, 'YYYYMMdd') = TO_CHAR(now(), 'YYYYMMdd'))\n"
                + "    THEN 1 ELSE 0 END AS therenow,\n"
                + "  (SELECT exc.exchangedate FROM finance.exchange exc WHERE exc.deleted = FALSE ORDER BY exc.exchangedate DESC LIMIT 1 ) AS excexchangedate";

        Object[] param = new Object[]{};

        return getJdbcTemplate().query(sql, param, new ExchangeMapper(sessionBean.getUser().getLastBranch().getCurrency()));

    }

}
