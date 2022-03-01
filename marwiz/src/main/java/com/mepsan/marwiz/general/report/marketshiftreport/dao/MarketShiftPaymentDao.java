/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.10.2018 09:12:45
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.dashboard.dao.ChartItemMapper;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPaymentFinancingDocumentCon;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarketShiftPaymentDao extends JdbcDaoSupport implements IMarketShiftPaymentDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<MarketShiftPayment> listOfShiftPayment(Shift shift, String where) {
        String sql = "SELECT\n"
                  + "	shp.id AS shpid,\n"
                  + "    shp.account_id AS shpaccount_id,\n"
                  + "    acc.id AS accid,\n"
                  + "    acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    shp.actualprice AS shpaccualprice,\n"
                  + "    shp.bankaccount_id AS shpbankaccount_id,\n"
                  + "    ba.name AS baname,\n"
                  + "     ba.type_id AS batype_id,\n"
                  + "    shp.currency_id AS shpcurrency_id,\n"
                  + "    shp.exchangerate AS shpexchangerate,\n"
                  + "    shp.is_check AS shpis_check,\n"
                  + "    shp.safe_id AS shpsafe_id,\n"
                  + "    sf.name AS sfname,\n"
                  + "    shp.saleprice AS shpsaleprice,\n"
                  + "    shp.saletype_id AS shpsaletype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    shp.inheritedmoney AS shpinheritedmoney,\n"
                  + "    shp.shift_id AS shpshift_id, \n"
                  + "    (SELECT CASE WHEN COUNT(shpcon.id) > 0 THEN TRUE ELSE FALSE END FROM general.shiftpayment_financingdocument_con shpcon WHERE shpcon.deleted=FALSE AND shpcon.is_inherited = FALSE AND shpcon.shiftpayment_id =shp.id) AS isAvailableFinancingDoc\n"
                  + "FROM general.shiftpayment shp\n"
                  + "	INNER JOIN general.account acc ON(acc.id=shp.account_id)\n"
                  + "    INNER JOIN system.type_dict typd ON (typd.type_id = shp.saletype_id AND typd.language_id = ?)\n"
                  + "    LEFT JOIN finance.bankaccount ba ON(ba.id=shp.bankaccount_id)\n"
                  + "    LEFT JOIN finance.safe sf ON(sf.id=shp.safe_id)\n"
                  + "WHERE shp.deleted=False AND shp.shift_id=? AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id)\n"
                  + where;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), shift.getId()};

        List<MarketShiftPayment> result = getJdbcTemplate().query(sql, param, new MarketShiftPaymentMapper());
        return result;
    }

    @Override
    public int update(MarketShiftPayment obj) {
        String sql = "UPDATE general.shiftpayment\n"
                  + "SET\n"
                  + "processdate=now(), "
                  + "actualprice=?, "
                  + "is_check=?, "
                  + "exchangerate=?, "
                  + "u_id=?, "
                  + "u_time=now() \n"
                  + "WHERE id=?";

        Object[] param = new Object[]{obj.getActualSalesPrice(),
            obj.isIs_check(), obj.getExchangeRate(),
            sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(MarketShiftPaymentFinancingDocumentCon marketShiftPaymentCon) {
        String sql = "SELECT r_payment_id FROM finance.delete_payment_financingdocument (?, ?, ?);";

        Object[] param = new Object[]{1, marketShiftPaymentCon.getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int controlOpenShiftPayment() {
        String sql = "  SELECT\n"
                  + "       CASE WHEN SUM(CASE WHEN sfp.is_check=FALSE THEN 1 ELSE 0 END) > 0 THEN 0 ELSE 1 END\n"
                  + "   FROM\n"
                  + "       general.shift sf \n"
                  + "   INNER JOIN general.shiftpayment sfp ON (sfp.shift_id = sf.id AND sfp.deleted = FALSE)\n"
                  + "   WHERE\n"
                  + "       sf.deleted = FALSE\n"
                  + "       AND sf.branch_id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<ChartItem> chartListForShiftPayment(Shift shift) {
        String sql = "SELECT\n"
                  + "    crr.code AS crrcode,\n"
                  + "    COALESCE(SUM(shp.saleprice),0) AS shpsaleprice,\n"
                  + "    shp.saletype_id AS shpsaletype_id,\n"
                  + "    typd.name AS typdname\n"
                  + "FROM general.shiftpayment shp\n"
                  + "    INNER JOIN system.type_dict typd ON (typd.type_id = shp.saletype_id AND typd.language_id = ?)\n"
                  + "    INNER JOIN system.currency crr ON(crr.id=shp.currency_id)\n"
                  + "WHERE shp.deleted=False AND shp.shift_id=?\n"
                  + "GROUP BY crr.code,\n"
                  + "         shp.saletype_id,\n"
                  + "         typd.name";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), shift.getId()};

        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> chartListForPreviousCompare(Shift shift) {
        String sql = "SELECT\n"
                  + "    crr.code AS crrcode,\n"
                  + "    COALESCE(SUM(shp.saleprice),0) AS shpsaleprice,\n"
                  + "    shp.shift_id AS shpshift_id\n"
                  + "FROM general.shiftpayment shp\n"
                  + "    INNER JOIN system.currency crr ON(crr.id=shp.currency_id)\n"
                  + "WHERE shp.deleted=False AND shp.shift_id IN (?, \n"
                  + "(SELECT shf.id FROM general.shift shf \n"
                  + "WHERE shf.deleted=FALSE AND shf.branch_id=? AND ? >shf.begindate AND ? > shf.id ORDER BY shf.begindate DESC LIMIT 1))\n"
                  + "\n"
                  + "GROUP BY crr.code,\n"
                  + "         shp.shift_id";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId(),
            shift.getBeginDate(), shift.getId()};

        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public int updateShiftPaymentForFinancingDoc(int type, MarketShiftPayment shiftPayment, FinancingDocument obj, int inmovementId, int outmovementId) {
        String sql = "SELECT r_shiftpayment_id FROM general.process_shiftpayment (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] param = new Object[]{type, shiftPayment.getId(), shiftPayment.isIs_check(), obj.getFinancingType().getId(), obj.getIncomeExpense().getId() == 0 ? null : obj.getIncomeExpense().getId(),
            obj.getPrice(), obj.getCurrency().getId(), obj.getExchangeRate(), obj.getDocumentDate(), obj.getDescription(),
            outmovementId, inmovementId, sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};

        System.out.println("--paramm--" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<MarketShiftPaymentFinancingDocumentCon> findFinancingDocForShiftPayment(MarketShiftPayment shiftPayment) {
        String sql = "SELECT \n"
                  + "	 shpcon.id AS shpconid,\n"
                  + "    shpcon.financingdocument_id AS shpconfinancingdocument_id,\n"
                  + "    fdoc.type_id AS fdoctype_id,\n"
                  + "    fdoc.price AS fdocprice,\n"
                  + "    fdoc.currency_id AS fdoccurrency,\n"
                  + "    fiem.incomeexpense_id AS fiemincomeexpense_id,\n"
                  + "    fie.name AS fiename,\n"
                  + "    fie.is_income AS fieis_income\n"
                  + "FROM\n"
                  + "	general.shiftpayment_financingdocument_con shpcon \n"
                  + "    INNER JOIN finance.financingdocument fdoc ON(fdoc.id = shpcon.financingdocument_id AND fdoc.deleted=FALSE)\n"
                  + "    LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n"
                  + "    LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id AND fie.deleted=False)\n"
                  + "WHERE shpcon.deleted =FALSE AND shpcon.shiftpayment_id = ? AND shpcon.is_inherited =FALSE";

        Object[] param = new Object[]{shiftPayment.getId()};

        List<MarketShiftPaymentFinancingDocumentCon> result = getJdbcTemplate().query(sql, param, new MarketShiftPaymentFinancingDocumentConMapper());
        return result;
    }

    @Override
    public int create(MarketShiftPayment obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
