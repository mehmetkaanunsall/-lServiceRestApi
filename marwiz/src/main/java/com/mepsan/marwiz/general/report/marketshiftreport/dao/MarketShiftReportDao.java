/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 03:05:01
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarketShiftReportDao extends JdbcDaoSupport implements IMarketShiftReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Shift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String sql = "SELECT\n"
                  + "     shf.id AS shfid,\n"
                  + "     shf.shiftno AS shfshiftno,\n"
                  + "     shf.begindate AS shfbegindate,\n"
                  + "     shf.enddate AS shfenddate,\n"
                  + "     shf.status_id AS shfstatus_id,\n"
                  + "     (SELECT COUNT(CASE WHEN sl.is_return = False THEN sl.id END) FROM general.shift shf1\n"
                  + "                   LEFT JOIN general.sale sl ON(sl.shift_id=shf.id AND sl.deleted=False)\n"
                  + "                   LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "                   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "                   WHERE shf1.id = shf.id AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND usr.type_id = 2\n"
                  + "     ) AS slcount\n"
                  + "FROM general.shift shf \n"
                  + "LEFT JOIN general.sale sl ON(sl.shift_id=shf.id AND sl.deleted=False)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "WHERE shf.branch_id=? AND shf.deleted=FALSE" + where + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "GROUP BY shf.id, shf.shiftno, shf.begindate,shf.enddate, shf.status_id\n"
                  + "ORDER BY shf.begindate DESC"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<Shift> result = getJdbcTemplate().query(sql, param, new MarketShiftReportMapper());
        return result;
    }

    @Override
    public String exportData(String where) {
        String sql = "SELECT\n"
                  + "     shf.id AS shfid,\n"
                  + "     shf.shiftno AS shfshiftno,\n"
                  + "     shf.begindate AS shfbegindate,\n"
                  + "     shf.enddate AS shfenddate,\n"
                  + "     shf.status_id AS shfstatus_id,\n"
                  + "     (SELECT COUNT(CASE WHEN sl.is_return = False THEN sl.id END) FROM general.shift shf1\n"
                  + "                   LEFT JOIN general.sale sl ON(sl.shift_id=shf.id AND sl.deleted=False)\n"
                  + "                   LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "                   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "                   WHERE shf1.id = shf.id AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND usr.type_id = 2\n"
                  + "     ) AS slcount\n"
                  + "FROM general.shift shf \n"
                  + "LEFT JOIN general.sale sl ON(sl.shift_id=shf.id AND sl.deleted=False)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "WHERE shf.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND shf.deleted=FALSE" + where + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "GROUP BY shf.id, shf.shiftno, shf.begindate,shf.enddate, shf.status_id\n"
                  + "ORDER BY shf.begindate DESC";
        System.err.println(sql);
        return sql;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                  + "	COUNT(shf.id) AS shfid \n"
                  + "FROM  general.shift shf  \n"
                  + "WHERE shf.branch_id=? AND shf.deleted=FALSE" + where + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<Sales> listOfSalePOS(Shift obj) {
        String sql = "SELECT \n"
                  + "pos.id AS posid,\n"
                  + "pos.name AS posname,\n"
                  + "sl.currency_id AS slcurrency_id,\n"
                  + "COALESCE (SUM(CASE WHEN sl.is_return=False THEN sl.totalprice ELSE -sl.totalprice END),0) AS totalprice,\n"
                  + "COALESCE (SUM(CASE WHEN sl.is_return=False THEN sl.totalmoney ELSE -sl.totalmoney END),0) AS totalmoney\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id)\n"
                  + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE sl.deleted=False AND sl.branch_id = ? AND usr.type_id = 2\n"
                  + "AND sl.shift_id = ?\n"
                  + "GROUP BY pos.id,pos.name,sl.currency_id";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getId()};
        return getJdbcTemplate().query(sql, param, new MarketShiftReportGraphicMapper());
    }

    @Override
    public List<Sales> listOfSaleUser(Shift obj) {
        String sql = "SELECT\n"
                  + "        us.id AS usid,\n"
                  + "        us.name AS usname,\n"
                  + "        us.surname AS ussurname,\n"
                  + "        sl.currency_id AS slcurrency_id,\n"
                  + "        COALESCE (SUM(CASE WHEN sl.is_return=False THEN sl.totalprice ELSE -sl.totalprice END),0) AS totalprice,\n"
                  + "        COALESCE (SUM(CASE WHEN sl.is_return=False THEN sl.totalmoney ELSE -sl.totalmoney END),0) AS totalmoney\n"
                  + "FROM general.sale sl\n"
                  + "INNER JOIN general.userdata us ON(us.id = sl.userdata_id)\n"
                  + "WHERE sl.deleted=False AND sl.branch_id = ? AND us.type_id = 2\n"
                  + "AND sl.shift_id = ?\n"
                  + "GROUP BY us.id,us.name,us.surname,sl.currency_id";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getId()};
        return getJdbcTemplate().query(sql, param, new MarketShiftReportGraphicMapper());
    }

    @Override
    public List<SalePayment> listOfSaleType(Shift obj) {
        String sql = "SELECT\n"
                  + "    slp.type_id AS slptype_id,\n"
                  + "    typd.name AS typdname,\n"
                  + "    COALESCE(SUM(CASE WHEN sl.is_return=False THEN slp.price ELSE -slp.price END),0) AS slpprice,\n"
                  + "    slp.currency_id AS slpcurrency_id\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.salepayment slp ON(slp.sale_id = sl.id AND slp.deleted=False)\n"
                  + "   LEFT JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id =?)\n"
                  + "   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE sl.branch_id=? AND sl.shift_id=? \n"
                  + "	AND sl.deleted=False AND usr.type_id = 2\n"
                  + "GROUP BY slp.type_id, typd.name,slp.currency_id\n"
                  + "HAVING SUM(slp.price) >0\n"
                  + "\n"
                  + "UNION ALL\n"
                  + "\n"
                  + "SELECT\n"
                  + "    0 AS slptype_id,\n"
                  + "    NULL AS typdname,\n"
                  + "    COALESCE(SUM(inv.remainingmoney),0) AS slpprice,\n"
                  + "    inv.currency_id AS slpcurrency_id\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "	LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted=False)\n"
                  + "   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE sl.branch_id=? AND sl.shift_id=? \n"
                  + "	AND sl.deleted=False AND sl.is_return=False AND usr.type_id = 2\n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "GROUP BY inv.currency_id\n"
                  + "HAVING SUM(inv.remainingmoney) >0";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getId()};
        return getJdbcTemplate().query(sql, param, new MarketShiftSalePaymentMapper());
    }

    @Override
    public Shift controlShiftPayment(Shift obj) {
        String sql = "SELECT\n"
                  + "    shf.is_confirm AS sfis_confirm, \n"
                  + "    CASE WHEN SUM(CASE WHEN sfp.is_check=TRUE THEN 1 ELSE 0 END) = COUNT(sfp.id) THEN TRUE ELSE FALSE END AS isshiftpaymentcheck,\n"
                  + "    CASE WHEN SUM(CASE WHEN sf.shiftmovementsafe_id IS NOT NULL THEN 1 ELSE 0 END) > 0 THEN TRUE ELSE FALSE END AS ismovementsafe\n"
                  + "    \n"
                  + "FROM\n"
                  + "	general.shift shf \n"
                  + "	LEFT JOIN general.shiftpayment sfp ON (sfp.shift_id = shf.id AND sfp.deleted = FALSE)   \n"
                  + "    LEFT JOIN finance.safe sf ON(sf.id=sfp.safe_id)\n"
                  + "WHERE\n"
                  + "	shf.deleted = FALSE\n"
                  + "    AND shf.id=?\n"
                  + "GROUP BY shf.is_confirm";

        Object[] param = new Object[]{obj.getId()};
        List<Shift> result = getJdbcTemplate().query(sql, param, new MarketShiftReportMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Shift();
        }

    }

    @Override
    public Shift findMarketShift(FinancingDocument financingDocument) {
        String sql = "SELECT\n"
                  + "     shf.id AS shfid,\n"
                  + "    shf.shiftno AS shfshiftno, \n"
                  + "    shf.begindate  AS shfbegindate,\n"
                  + "    shf.enddate  AS shfenddate\n"
                  + "FROM\n"
                  + "	general.shift shf \n"
                  + "	INNER JOIN general.shiftpayment sfp ON (sfp.shift_id = shf.id AND sfp.deleted = FALSE)   \n"
                  + "WHERE\n"
                  + "	shf.deleted = FALSE\n"
                  + "    AND sfp.transferfinancingdocument_id=?\n";
        Object[] param = new Object[]{financingDocument.getId()};

        List<Shift> result = getJdbcTemplate().query(sql, param, new MarketShiftReportMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new Shift();
        }
    }

    @Override
    public int create(Shift obj
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
