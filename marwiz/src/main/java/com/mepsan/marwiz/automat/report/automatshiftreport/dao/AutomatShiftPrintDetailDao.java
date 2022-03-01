/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:52:34 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomatShiftPrintDetailDao extends JdbcDaoSupport implements IAutomatShiftPrintDetailDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomatShiftReport> listOfPaymentType(AutomatShiftReport obj) {
        String sql = "SELECT\n"
                + "sl.paymenttype_id as slpaymenttype_id,\n"
                + "sl.stock_id as slstockid,\n"
                + "stck.name as stckname,\n"
                + "stck.unit_id as sliunit_id,\n"
                + "unt.sortname as untsortname,\n"
                + "unt.unitrounding as untunitrounding,\n"
                + "COUNT(sl.id) AS slidcount ,\n"
                + "COALESCE(SUM(COALESCE(sl.operationamount,0)),0) AS slliter,\n"
                + "SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)) AS sltotalmoney\n"
                + "FROM wms.shift shf \n"
                + "INNER JOIN wms.sale sl ON(shf.id=sl.shift_id AND sl.deleted=FALSE)\n"
                + "LEFT JOIN inventory.stock stck ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "LEFT JOIN general.unit unt ON(unt.id=stck.unit_id )\n"
                + "WHERE shf.deleted=FALSE AND shf.id=?\n"
                + "GROUP BY sl.paymenttype_id,sl.stock_id,stck.name,stck.unit_id ,unt.sortname,unt.unitrounding\n"
                + "ORDER BY sl.paymenttype_id";

        Object[] param = new Object[]{obj.getId()};

        List<AutomatShiftReport> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportPrintDetailMapper());

        return result;
    }

    @Override
    public List<AutomatShiftReport> listOfProduct(AutomatShiftReport obj) {
        String sql = "SELECT\n"
                + "sl.stock_id as slstockid,\n"
                + "stck.name as stckname,\n"
                + "COALESCE(sl.unitprice,0) as slunitprice,\n"
                + "COUNT(sl.id) AS slidcount ,\n"
                + "SUM(sl.operationamount) AS slliter,\n"
                + "SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)) as sltotalmoney,\n"
                + "stck.unit_id as sliunit_id,\n"
                + "unt.sortname as untsortname,\n"
                + "unt.unitrounding as untunitrounding,\n"
                + "sl.currency_id as slcurrency_id,\n"
                + "cryd.name as crydname\n"
                + "FROM wms.shift shf\n"
                + "INNER JOIN wms.sale sl  ON(shf.id=sl.shift_id AND sl.deleted=FALSE)\n"
                + "LEFT JOIN inventory.stock stck  ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "INNER JOIN system.currency_dict cryd  ON(cryd.currency_id=sl.currency_id AND cryd.language_id=?)\n"
                + "LEFT JOIN general.unit unt ON(stck.unit_id=unt.id)\n"
                + "WHERE shf.deleted=FALSE AND shf.id=?\n"
                + "GROUP BY sl.stock_id,stck.name,sl.unitprice,sl.currency_id,cryd.name,stck.unit_id,unt.sortname,unt.unitrounding\n"
                + "ORDER BY stck.name DESC";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), obj.getId()};
        List<AutomatShiftReport> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportPrintDetailMapper());

        return result;
    }

    @Override
    public List<AutomatShiftReport> listOfPlatform(AutomatShiftReport obj) {
        String sql = "   	SELECT\n"
                + "      				 plt.id as pltid,\n"
                + "                                      plt.platformno as pltplatformno,\n"
                + "                                      sl.stock_id as slstock_id,\n"
                + "                                      stck.name as stckname,\n"
                + "                                      stck.unit_id as sliunit_id,\n"
                + "                                      unt.sortname as untsortname,\n"
                + "                                      unt.unitrounding as untunitrounding,\n"
                + "                                      COUNT(sl.id) AS slidcount ,\n"
                + "                                      (SELECT wsh.name FROM wms.washingmachine wsh WHERE wsh.id=plt.washingmachine_id AND wsh.deleted=FALSE) as wshname,\n"
                + "                                      SUM(sl.operationamount) AS slliter,\n"
                + "                                      SUM(COALESCE(sl.totalmoney,0)*COALESCE(sl.exchangerate,1)) as sltotalmoney\n"
                + "                                     FROM wms.shift shf \n"
                + "                                     INNER JOIN wms.sale sl  ON(shf.id=sl.shift_id AND sl.deleted=FALSE)\n"
                + "                                     INNER JOIN wms.platform plt ON(plt.id=sl.platform_id AND plt.deleted=FALSE)\n"
                + "                                     LEFT JOIN inventory.stock stck  ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "                                     LEFT JOIN general.unit unt ON(unt.id=stck.unit_id)\n"
                + "                                     WHERE shf.deleted=FALSE AND shf.id=?\n"
                + "                                     GROUP BY plt.id,plt.platformno,sl.stock_id ,stck.name,stck.unit_id,unt.sortname,unt.unitrounding\n"
                + "                                     ORDER BY plt.platformno DESC";

        Object[] param = new Object[]{obj.getId()};
        List<AutomatShiftReport> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportPrintDetailMapper());

        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<AutomatShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
