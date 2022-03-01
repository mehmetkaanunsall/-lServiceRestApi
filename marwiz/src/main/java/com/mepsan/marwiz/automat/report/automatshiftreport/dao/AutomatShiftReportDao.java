/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:47:28 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomatShiftReportDao extends JdbcDaoSupport implements IAutomatShiftReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public String exportData(String where) {
        String sql = "SELECT \n"
                + "       shf.id as shfid,\n"
                + "       shf.shiftno as shfshitfno,\n"
                + "       shf.begindate as shfbegindate,\n"
                + "       shf.enddate as shfenddate,\n"
                + "       shf.status_id as shfstatus_id\n"
                + "       FROM wms.shift shf \n"
                + "       LEFT JOIN system.status_dict sttd  ON(sttd.status_id=shf.status_id AND sttd.language_id=" + sessionBean.getUser().getLanguage().getId() + ")\n"
                + "       WHERE shf.deleted=FALSE AND shf.branch_id=" + sessionBean.getUser().getLastBranch().getId();

        return sql;
    }

    @Override
    public List<AutomatShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "shf.begindate";
            sortOrder = "desc";
        }

        String sql = "SELECT \n"
                + "       shf.id as shfid,\n"
                + "       shf.shiftno as shfshitfno,\n"
                + "       shf.begindate as shfbegindate,\n"
                + "       shf.enddate as shfenddate,\n"
                + "       shf.status_id as shfstatus_id\n"
                + "       FROM wms.shift shf \n"
                + "       LEFT JOIN system.status_dict sttd  ON(sttd.status_id=shf.status_id AND sttd.language_id=?)\n"
                + "       WHERE shf.deleted=FALSE AND shf.branch_id=?\n"
                + "ORDER BY shf.begindate DESC limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<AutomatShiftReport> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                + "       COUNT(shf.id) as shfid\n"
                + "       FROM wms.shift shf \n"
                + "       LEFT JOIN system.status_dict sttd  ON(sttd.status_id=shf.status_id AND sttd.language_id=?)\n"
                + "       WHERE shf.deleted=FALSE AND shf.branch_id=?;";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<AutomatSales> listOfSaleStock(AutomatShiftReport obj) {
        String sql = " SELECT\n"
                + "                           stck.id as stckid,\n"
                + "                           stck.name as stckname,\n"
                + "                           unt.unitrounding as untunitrounding,\n"
                + "                           unt.sortname as untsortname,\n"
                + "                           stck.unit_id as sliunit_id,\n"
                + "                           COALESCE(SUM(sl.operationamount),0) as liter,\n"
                + "                           COALESCE(SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)),0) as totalmoney,\n"
                + "                           sl.currency_id as slcurrency_id,\n"
                + "                           cryd.name as crydname\n"
                + "                           FROM wms.sale sl \n"
                + "                           LEFT JOIN inventory.stock stck ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "                           LEFT JOIN general.unit unt  ON(unt.id=stck.unit_id )\n"
                + "                           INNER JOIN system.currency_dict cryd  ON(cryd.currency_id=sl.currency_id AND cryd.language_id=?)\n"
                + "                           WHERE  sl.shift_id=? AND sl.deleted=FALSE\n"
                + "                           GROUP BY stck.id, stck.name ,stck.unit_id , unt.sortname,unt.unitrounding,sl.currency_id ,cryd.name";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};

        return getJdbcTemplate().query(sql, param, new AutomatGraphicMapper());
    }

    @Override
    public List<AutomatSales> listOfSalePlatform(AutomatShiftReport obj) {
        String sql = "SELECT\n"
                + "               		       sl.platformno AS slplatformno ,\n"
                + "                                     SUM(COALESCE(sl.operationamount,0)) AS liter,\n"
                + "                                      COALESCE(SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)),0) AS totalmoney,\n"
                + "                                      sl.currency_id as slcurrency_id,\n"
                + "                                      stck.unit_id as sliunit_id,\n"
                + "                                      unt.sortname as untsortname,\n"
                + "                                      unt.unitrounding as untunitrounding,\n"
                + "                                      cryd.name as crydname,\n"
                + "                                      wsh.name as wshname\n"
                + "                                      FROM wms.sale sl  \n"
                + "                                      LEFT JOIN inventory.stock stck ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "                                      LEFT JOIN general.unit unt ON(unt.id=stck.unit_id)\n"
                + "                                      LEFT JOIN wms.washingmachine wsh ON(wsh.id = sl.washingmachine_id)\n"
                + "                                      INNER JOIN system.currency_dict cryd ON(cryd.currency_id=sl.currency_id AND cryd.language_id=?)\n"
                + "                                      WHERE sl.deleted = FALSE\n"
                + "                                      AND sl.shift_id = ?\n"
                + "                                      GROUP BY wsh.name,sl.platform_id,sl.platformno,sl.currency_id,cryd.name,stck.unit_id,unt.sortname,unt.unitrounding\n"
                + "                                      ORDER BY wsh.name,sl.platformno";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};

        return getJdbcTemplate().query(sql, param, new AutomatGraphicMapper());
    }

    @Override
    public List<AutomatSales> listOfSalePaymentType(AutomatShiftReport obj) {
        String sql = "SELECT \n"
                + "          		  				 sl.paymenttype_id as  ptid,\n"
                + "                                stck.unit_id as sliunit_id,\n"
                + "                                gunt.sortname as untsortname,\n"
                + "                                gunt.unitrounding as untunitrounding,\n"
                + "                                COALESCE(SUM(sl.operationamount),0) as liter,\n"
                + "                                COALESCE(SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)),0) as totalmoney,\n"
                + "                                sl.currency_id as slcurrency_id,\n"
                + "                                cryd.name as crydname\n"
                + "                                FROM wms.sale sl\n"
                + "                                LEFT JOIN inventory.stock stck   ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "                                LEFT JOIN general.unit gunt   ON(gunt.id=stck.unit_id )\n"
                + "                                INNER JOIN system.currency_dict cryd   ON(cryd.currency_id=sl.currency_id AND cryd.language_id=?)\n"
                + "                                WHERE  sl.shift_id=? AND sl.deleted=FALSE\n"
                + "                                GROUP BY sl.paymenttype_id,stck.unit_id,gunt.sortname,gunt.unitrounding,sl.currency_id, cryd.name";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};

        return getJdbcTemplate().query(sql, param, new AutomatGraphicMapper());
    }

}
