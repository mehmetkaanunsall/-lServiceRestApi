/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.02.2018 02:14:28
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarketShiftReportPrintDao extends JdbcDaoSupport implements IMarketShiftReportPrintDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SalePayment> listOfUser(Shift obj) {
        String sql = "SELECT \n"
                  + "	tt.sluserdata_id,\n"
                  + "    tt.usname,\n"
                  + "    tt.ussurname,\n"
                  + "    tt.slpprice,\n"
                  + "    tt.slpcurrency_id,\n"
                  + "    (SELECT \n"
                  + "    	COUNT(sl1.id) AS count\n"
                  + "    FROM general.sale sl1 \n"
                  + "    	LEFT JOIN general.sale sll1 ON(sll1.returnparent_id = sl1.id AND sll1.deleted = False AND sll1.shift_id = sl1.shift_id)\n"
                  + "           INNER JOIN general.userdata usr1 ON(usr1.id = sl1.userdata_id)\n"
                  + "    WHERE sl1.deleted=False \n"
                  + "		AND sl1.branch_id=?\n"
                  + "    	AND sl1.shift_id=? AND sl1.is_return= False AND usr1.type_id = 2\n"
                  + "		AND COALESCE(sll1.id,0) = COALESCE(sll1.returnparent_id,0)\n"
                  + "    	AND sl1.userdata_id=tt.sluserdata_id\n"
                  + "    ) AS countsale,\n"
                  + "    tt.slptype_id,\n"
                  + "    tt.typdname\n"
                  + "FROM\n"
                  + "(SELECT \n"
                  + "    sl.userdata_id AS sluserdata_id,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname,\n"
                  + "    COALESCE(SUM(CASE WHEN sl.is_return= False THEN slp.price ELSE -slp.price END),0) AS slpprice,\n"
                  + "    slp.currency_id AS slpcurrency_id,\n"
                  + "    slp.type_id AS slptype_id,\n"
                  + "    typd.name AS typdname\n"
                  + "FROM general.sale sl \n"
                  + "	LEFT JOIN general.salepayment slp ON(sl.id=slp.sale_id AND slp.deleted = False)\n"
                  + "	INNER JOIN general.userdata us ON(us.id = sl.userdata_id)\n"
                  + "	LEFT JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                  + "WHERE sl.deleted=False \n"
                  + "	AND sl.branch_id=? AND us.type_id = 2\n"
                  + "    AND sl.shift_id=? \n"
                  + "GROUP BY sl.userdata_id,us.name,us.surname, slp.type_id,typd.name,slp.currency_id\n"
                  + "\n"
                  + "UNION ALL\n"
                  + "\n"
                  + "SELECT \n"
                  + "    sl.userdata_id AS sluserdata_id,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname,\n"
                  + "    COALESCE(SUM(inv.remainingmoney),0) AS slpprice,\n"
                  + "    inv.currency_id slpcurrency_id,\n"
                  + "    0 AS slptype_id,\n"
                  + "    NULL AS typdname\n"
                  + "FROM general.sale sl \n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.shift_id = sl.shift_id)\n"
                  + "	INNER JOIN general.userdata us ON(us.id = sl.userdata_id)\n"
                  + "    LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted=False)\n"
                  + "WHERE sl.deleted=False \n"
                  + "	AND sl.branch_id=?\n"
                  + "    AND sl.shift_id=? AND sl.is_return= False AND us.type_id = 2\n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "    AND inv.remainingmoney>0 AND inv.remainingmoney IS NOT NULL\n"
                  + "GROUP BY sl.userdata_id,us.name,us.surname, inv.currency_id\n"
                  + ") as tt\n"
                  + "WHERE tt.slpprice <> 0";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getId(),
            sessionBean.getUser().getLastBranch().getId(), obj.getId()};
        List<SalePayment> result = getJdbcTemplate().query(sql, param, new MarketShiftSalePaymentMapper());

        return result;
    }

    @Override
    public int transferShiftPaymentToMainSafe(int type, Shift obj, boolean isDesc) {
        Object[] param;
        String sql = "SELECT r_shift_id FROM general.process_shift (?, ?, ?, ?,?,?);";

        if (isDesc) {
            param = new Object[]{sessionBean.getUser().getLastBranch().getId(), type, obj.getId(), sessionBean.getUser().getId(), obj.getDescription(),false};
        } else {
            param = new Object[]{sessionBean.getUser().getLastBranch().getId(), type, obj.getId(), sessionBean.getUser().getId(), obj.getName(),false};
        }

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int create(SalePayment obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(SalePayment obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
