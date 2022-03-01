/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 1:01:30 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.dao;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class IncomeExpenseReportDao extends JdbcDaoSupport implements IIncomeExpenseReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int count(String where) {
        int result = 0;
        String sql = "   SELECT  count(subsale.total)\n"
                + "               FROM ( SELECT \n"
                + "                       COUNT(sl.id) as total\n"
                + "                       FROM wms.sale sl \n"
                + "                       INNER JOIN inventory.stock stck ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "                       INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "                       WHERE sl.deleted=FALSE \n"
                + "                       GROUP BY sl.stock_id,stck.name ) subsale";

        result = getJdbcTemplate().queryForObject(sql, Integer.class);
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public String exportData(String where) {
        String sql = "SELECT \n"
                + "                   sl.stock_id as slstock_id,\n"
                + "                   stck.name as stckname,\n"
                + "                   COUNT(sl.id) as quantitiy,\n"
                + "                   SUM(COALESCE(sl.operationtime,0)) as sloperationtime,\n"
                + "                   ROUND(SUM(COALESCE(sl.totalmoney,0)),2) as income,\n"
                + "                   ROUND(SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.operationamount,0))/60)*COALESCE(sl.expenseunitprice,0)),2) as expense,\n"
                + "                   ROUND(COALESCE( SUM(COALESCE(sl.totalmoney,0))-SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.operationamount,0))/60)*COALESCE(sl.expenseunitprice,0)),0),2) as winngins,\n"
                + "                   COUNT(sl.id) as electricquantitiy,\n"
                + "                   ROUND(SUM(COALESCE(sl.electricamount,0)),2) as elecquantity,\n"
                + "                   SUM(COALESCE(sl.operationtime,0)) as slelectricoperationtime,\n"
                + "                   ROUND(SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.electricamount,0))/60)*COALESCE(sl.electricunitprice,0)),2) as electricexpense,\n"
                + "                   ROUND(SUM(COALESCE(sl.operationamount,0)),2) as waste,\n"
                + "                   ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN 1 ELSE 0 END),2) as waterworkingamount,\n"
                + "       	       ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.operationtime,0) ELSE 0 END),2) as waterworkingtime,\n"
                + "                   ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.wateramount,0) ELSE 0 END),2) as waterwase,\n"
                + "                   ROUND(SUM((((CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.operationtime,0) ELSE 0 END)*COALESCE(sl.wateramount,0))/60)*COALESCE(sl.waterunitprice,0)),2) as waterexpense,\n"
                + "                   stck.unit_id as stckuntid,\n"
                + "                   unt.sortname as untsrotname\n"
                + "                   FROM wms.sale sl\n"
                + "                   INNER JOIN inventory.stock stck ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "                   INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "                   INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "                   WHERE sl.deleted=FALSE\n" + where + "\n"
                + "       GROUP BY sl.stock_id,stck.name,stck.unit_id,unt.sortname";

        return sql;
    }

    @Override
    public List<AutomatSalesReport> findAll(String where) {
        String sql = "SELECT \n"
                + "                   sl.stock_id as slstock_id,\n"
                + "                   stck.name as stckname,\n"
                + "                   COUNT(sl.id) as quantitiy,\n"
                + "                   SUM(COALESCE(sl.operationtime,0)) as sloperationtime,\n"
                + "                   ROUND(SUM(COALESCE(sl.totalmoney,0)),2) as income,\n"
                + "                   ROUND(SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.operationamount,0))/60)*COALESCE(sl.expenseunitprice,0)),2) as expense,\n"
                + "                   ROUND(COALESCE( SUM(COALESCE(sl.totalmoney,0))-SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.operationamount,0))/60)*COALESCE(sl.expenseunitprice,0)),0),2) as winngins,\n"
                + "                   COUNT(sl.id) as electricquantitiy,\n"
                + "                   ROUND(SUM(COALESCE(sl.electricamount,0)),2) as elecquantity,\n"
                + "                   SUM(COALESCE(sl.operationtime,0)) as slelectricoperationtime,\n"
                + "                   ROUND(SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.electricamount,0))/60)*COALESCE(sl.electricunitprice,0)),2) as electricexpense,\n"
                + "                   ROUND(SUM(COALESCE(sl.operationamount,0)),2) as waste,\n"
                + "                   ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN 1 ELSE 0 END),2) as waterworkingamount,\n"
                + "       	       ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.operationtime,0) ELSE 0 END),2) as waterworkingtime,\n"
                + "                   ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.wateramount,0) ELSE 0 END),2) as waterwase,\n"
                + "                   ROUND(SUM((((CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.operationtime,0) ELSE 0 END)*COALESCE(sl.wateramount,0))/60)*COALESCE(sl.waterunitprice,0)),2) as waterexpense,\n"
                + "                   stck.unit_id as stckuntid,\n"
                + "                   unt.sortname as untsrotname\n"
                + "                   FROM wms.sale sl\n"
                + "                   INNER JOIN inventory.stock stck ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                + "                   INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=?)\n"
                + "                   INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "                   WHERE sl.deleted=FALSE\n" + where
                + "       GROUP BY sl.stock_id,stck.name,stck.unit_id,unt.sortname";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};


        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new IncomeExpenseReportMapper());

        return result;
    }

    @Override
    public List<AutomatSalesReport> listOfSaleWaste(String where) {
        String sql = "SELECT\n"
                + "       sl.stock_id as slstock_id,\n"
                + "       stck.name as stckname,\n"
                + "       SUM(COALESCE(sl.operationamount,0)) as waste\n"
                + "      FROM wms.sale sl\n"
                + "      INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=?)\n"
                + "      INNER JOIN inventory.stock stck ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "      WHERE sl.deleted=FALSE\n" + where
                + "      GROUP BY sl.stock_id, stck.name";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new IncomeExpenseReportMapper());

        return result;
    }

    @Override
    public List<AutomatSalesReport> listOfIncomeExpense(String where) {
        String sql = "SELECT \n"
                + "        sl.stock_id as slstock_id,\n"
                + "        stck.name as stckname,\n"
                + "        SUM(COALESCE(sl.totalmoney,0)) as income,\n"
                + "        SUM(((COALESCE(sl.operationtime,0)*COALESCE(sl.operationamount,0))/60)*COALESCE(sl.expenseunitprice,0)) as expense\n"
                + "       FROM wms.sale sl\n"
                + "       INNER JOIN inventory.stock stck  ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "       INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=?)\n"
                + "       WHERE sl.deleted=FALSE\n" + where
                + "        GROUP BY sl.stock_id, stck.name";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new IncomeExpenseReportMapper());

        return result;
    }

    @Override
    public List<AutomatSalesReport> listOfDetail(String where) {
        String sql = "SELECT \n"
                + "       sl.saledatetime as slsaledatetime,\n"
                + "       sl.id as slid, \n"
                + "       sl.paymenttype_id as slpaymenttypeid,\n"
                + "       sl.platformno as slplatformno,\n"
                + "       sl.nozzleno as slnozzleno,\n"
                + "       sl.tankno as sltankno,\n"
                + "       sl.stock_id as slstock_id,\n"
                + "       stck.name as stckname,\n"
                + "       stck.unit_id as stckunitid,\n"
                + "       unt.sortname as untsortname,\n"
                + "       unt.unitrounding as untunitrounding,\n"
                + "       sl.unitprice as slunitprice,\n"
                + "       sl.shiftno as slshiftno,\n"
                + "       sl.operationamount as sloperationamount,\n"
                + "       sl.totalmoney as sltotalmoney\n"
                + "       FROM wms.sale sl\n"
                + "       INNER JOIN inventory.stock stck ON(stck.id=sl.stock_id AND stck.deleted=FALSE)\n"
                + "       INNER JOIN wms.washingmachine wm ON(wm.id=sl.washingmachine_id AND wm.deleted=FALSE AND wm.branch_id=?) \n"
                + "       INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "       WHERE sl.deleted=FALSE\n" + where + "\n"
                + "       ORDER BY sl.saledatetime DESC ";


        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new IncomeExpenseReportDetailMapper());

        return result;

    }

}
