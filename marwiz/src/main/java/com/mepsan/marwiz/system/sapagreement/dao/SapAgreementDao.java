package com.mepsan.marwiz.system.sapagreement.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.dao.ExchangeMapper;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class SapAgreementDao extends JdbcDaoSupport implements ISapAgreementDao {

    @Autowired
    private SessionBean sessionBean;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Exchange> findAllExchange(Date beginDate, Date endDate) {

        String sql = "SELECT \n"
                + "                 	crd.id as crdid,\n"
                + "                    crd.code as crdcode,\n"
                + "                   (SELECT\n"
                + "                       AVG(exc.buying)       \n"
                + "                FROM finance.exchange exc\n"
                + "                where exc.deleted=FALSE AND exc.currency_id=crd.id AND exc.responsecurrency_id = ?  AND exc.c_time BETWEEN ? AND ? AND exc.exchangedate BETWEEN ? AND ? \n"
                + "                ) as excbuying\n"
                + "                FROM system.currency crd   \n"
                + "                WHERE  \n"
                + "                 crd.deleted = FALSE AND crd.id NOT IN(?,4)\n"
                + "                group by  crd.id;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getCurrency().getId(), beginDate, endDate, beginDate, endDate, sessionBean.getUser().getLastBranch().getCurrency().getId()};
        return getJdbcTemplate().query(sql, param, new ExchangeMapper(sessionBean.getUser().getLastBranch().getCurrency()));

    }

    @Override
    public List<SapAgreement> listCurrency() {

        String sql = "SELECT \n"
                + "    crd.id as crdid,\n"
                + "    crd.code as crdcode,\n"
                + "  crd.internationalcode as crdinternationalcode\n"
                + "FROM system.currency crd   \n"
                + "WHERE crd.deleted = FALSE AND crd.id NOT IN(4)";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());
    }

    @Override
    public List<SapAgreement> findMarketSalesTotal(Date beginDate, Date endDate) {

        String sql = "SELECT\n"
                + "          COALESCE( sum((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,1)),0) AS totalmoney\n"
                + "          \n"
                + "       FROM \n"
                + "          general.sale sl\n"
                + "           INNER JOIN general.saleitem sli ON(sli.sale_id = sl.id AND sli.deleted = FALSE)\n"
                + "           LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < ?)\n"
                + "       WHERE\n"
                + "          sl.is_return = FALSE AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN ? AND ? THEN FALSE ELSE TRUE END) AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "          AND sl.deleted = FALSE AND sli.processdate BETWEEN  ? AND ? \n"
                + "           AND sl.branch_id = ? ";

        Object[] param = new Object[]{endDate, beginDate, endDate, beginDate, endDate, sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());

    }

    @Override
    public List<SapAgreement> findMarketSaleReturnTotal(Date beginDate, Date endDate) {

        String sql = "SELECT \n"
                + "    COALESCE(sum((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,1)),0) AS totalreturnsales,\n"
                + "      COALESCE(sum(((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,1))\n"
                + "                   + (CASE WHEN sl.is_return = TRUE AND sl2.id IS NULL THEN -1 * (COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) ELSE 0 END )) ,0) AS returnsWithSale\n"
                + "FROM general.sale sl \n"
                + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                + "INNER JOIN general.saleitem sli ON(sl.id=sli.sale_id AND sli.deleted = False)\n"
                + "                LEFT JOIN general.sale sl2 ON(sl.returnparent_id = sl2.id AND sl2.deleted = FALSE AND sl2.processdate BETWEEN ? AND ? AND sl2.branch_id = sl.branch_id)\n"
                + "WHERE sl.is_return=True \n"
                + "AND sl.deleted=FALSE AND  sl.branch_id = ?\n"
                + " AND sl.processdate BETWEEN ? AND ? ";

        Object[] param = new Object[]{beginDate, endDate, sessionBean.getUser().getLastBranch().getId(), beginDate, endDate};

        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());
    }

    @Override
    public int save(String automationJson, String posSaleJson, String expenseJson, String exchangeJson, String fuelZJson, String marketZJson, String safeTransferJson, String bankSendJson, String totalJson, Date date, int dateint, BigDecimal automationSaleDifference, int type, String sendData, boolean isSend, Date sendDate, String response, BigDecimal marketSaleDifference) {
        String sql = "SELECT r_sapagreement_id FROM integration.process_sapagreement (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{automationJson, posSaleJson, expenseJson, exchangeJson, fuelZJson, marketZJson, safeTransferJson, bankSendJson, totalJson, date, dateint, sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), automationSaleDifference, type, sendData, isSend, sendDate, response, marketSaleDifference};
        try {
            int i = 0;
            i = getJdbcTemplate().queryForObject(sql, param, Integer.class);
            return i;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int insertOrUpdateLog(SapAgreement sap, BigDecimal automationSaleDifference, int type, BigDecimal marketSaleDifference) {
        String sql = "SELECT r_sapagreement_id FROM integration.process_sapagreement (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{sap.getAutomationJson(), sap.getPosSaleJson(), sap.getExpenseJson(), sap.getExchangeJson(), sap.getFuelZJson(), sap.getMarketZJson(), sap.getSafeTransferJson(), sap.getBankTransferJson(), sap.getTotalJson(), sap.getProcessDate(), sap.getPeriod(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), automationSaleDifference, type, sap.getSendData(), sap.getIsSend(), sap.getSendDate(), sap.getResponse(), marketSaleDifference};
        try {
            int i = 0;
            i = getJdbcTemplate().queryForObject(sql, param, Integer.class);
            return i;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<SapAgreement> findall(Date beginDate, Date endDate, Date date) {
        String sql = "SELECT \n"
                + "sap.id AS sapid,\n"
                + "sap.branch_id AS sapbranch_id,\n"
                + "sap.processdate AS sapprocessdate,\n"
                + "sap.automationjson AS sapautomationjson,\n"
                + "sap.automationdiffamount AS sapautomationdiffamount,\n"
                + "sap.possalejson AS sappossalejson,\n"
                + "sap.expensejson AS sapexpensejson,\n"
                + "sap.exchangejson AS sapexchangejson,\n"
                + "sap.fuelzjson AS sapfuelzjson,\n"
                + "sap.marketzjson AS sapmarketzjson,\n"
                + "sap.totaljson AS saptotaljson,\n"
                + "sap.safetransfer AS sapsafetransfer,\n"
                + "sap.banktransfer AS sapbanktransfer,\n"
                + "sap.senddata AS sapsenddata,\n"
                + "sap.is_send AS sapis_send,\n"
                + "sap.senddate AS sapsenddate,\n"
                + "sap.response AS sapresponse,\n"
                + "(SELECT \n"
                + "SUM(isap.automationdiffamount)\n"
                + "FROM integration.sap_agreement isap\n"
                + "WHERE isap.deleted =FALSE AND isap.branch_id = ? AND isap.processdate BETWEEN ? AND ?\n"
                + ") AS transferautomationdiffamount,\n"
                + "(SELECT \n"
                + "SUM(isap.marketdiffamount)\n"
                + "FROM integration.sap_agreement isap\n"
                + "WHERE isap.deleted =FALSE AND isap.branch_id = ? AND isap.processdate BETWEEN ? AND ?\n"
                + ") AS transfermarketdiffamount\n"
                + "FROM integration.sap_agreement sap\n"
                + "WHERE sap.deleted = FALSE AND sap.processdate = ? AND sap.branch_id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), beginDate, endDate, sessionBean.getUser().getLastBranch().getId(), beginDate, endDate, date, sessionBean.getUser().getLastBranch().getId()};

        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());
    }

//    @Override
//    public List<SapAgreement> findPaymentType(Date beginDate, Date endDate) {
//
//        String sql = "";
//
//        sql = "SELECT \n"
//                + "   tt.id AS id,\n"
//                + "   tt.name AS name,\n"
//                + "   tt.integrationcode AS integrationcode,\n"
//                + "   tt.paymenttype AS paymenttype,\n"
//                + "   COALESCE(wmsale.totalmoney,0) AS totalmoney\n"
//                + "FROM\n"
//                + "(\n"
//                + "            SELECT \n"
//                + "                payment.id AS id,\n"
//                + "                payment.name AS name,\n"
//                + "                payment.integrationcode AS integrationcode,\n"
//                + "                (CASE WHEN payment.integrationcode = '92' THEN 0 --TORA Nakit(Bozuk Para)\n"
//                + "                     WHEN payment.integrationcode = '93' THEN 1  --TORA Mobil Ödeme\n"
//                + "                     WHEN payment.integrationcode = '94' THEN 2  --TORA Opet Kart\n"
//                + "                     WHEN payment.integrationcode = '95' THEN 3  --TORA Kredi Kartı\n"
//                + "                 ELSE 4 END) AS paymenttype\n"
//                + "            FROM integration.sap_paymenttype payment\n"
//                + "            WHERE deleted = FALSE AND branch_id = ?\n"
//                + ") tt\n"
//                + "LEFT JOIN (\n"
//                + "         SELECT \n"
//                + "           SUM(COALESCE(wmss.totalmoney,0)) AS totalmoney,\n"
//                + "           wmss.paymenttype_id AS paymenttype_id\n"
//                + "         FROM wms.sale wmss \n"
//                + "         WHERE wmss.deleted = FALSE \n"
//                + "               AND wmss.washingtype_id = 2 \n"
//                + "               AND wmss.branch_id = ?\n"
//                + "               AND wmss.is_excise = TRUE"
//                + "               AND wmss.saledatetime BETWEEN ? AND ? \n"
//                + "        GROUP BY wmss.paymenttype_id  \n"
//                + ") wmsale ON (wmsale.paymenttype_id = tt.paymenttype)\n";
//
//        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), beginDate, endDate};
//        System.out.println("-----wms sale ---"+sql);
//        System.out.println("---param---"+Arrays.toString(param));
//        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());
//
//    }

    @Override
    public List<SapAgreement> calculateTransferSaleDiffAmount(Date beginDate, Date endDate) {
        String sql = " SELECT \n"
                + "                SUM(isap.automationdiffamount) as transferautomationdiffamount,\n"
                + "                SUM(isap.marketdiffamount) as transfermarketdiffamount\n"
                + "                FROM integration.sap_agreement isap\n"
                + "                WHERE isap.deleted =FALSE AND isap.branch_id = ? AND isap.processdate BETWEEN ? AND ?";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), beginDate, endDate};
        return getJdbcTemplate().query(sql, param, new SapAgreementMapper());
    }

    @Override
    public int delete(SapAgreement obj) {
        String sql = "UPDATE integration.sap_agreement set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND is_send=FALSE AND id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(SapAgreement obj) {
        String sql = "UPDATE integration.sap_agreement SET  is_send = FALSE, u_id = ?, u_time = now() WHERE id= ?";
        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
