/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 09:14:42
 */
package com.mepsan.marwiz.general.report.removedstockreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.dashboard.dao.ChartItemMapper;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedStockReport;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class RemovedStockReportDao extends JdbcDaoSupport implements IRemovedStockReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<RemovedStockReport> listOfMonthlyLog(Date date, String branchList) {

        String sql = " SELECT\n"
                  + "    row_number() over(ORDER BY rs.userdata_id) as id,\n"
                  + "    rs.userdata_id AS rsuserdata_id,\n"
                  + "    rs.branch_id AS rsbranch_id,\n"
                  + "    br.name AS brname,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname,\n"
                  + "    COALESCE(( SELECT COUNT(rs1.id)  FROM log.removedstock rs1   WHERE rs1.userdata_id  = rs.userdata_id AND rs1.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('0 months' as interval)),'YYYY-MM') = to_char(date( rs1.processdate::date),'YYYY-MM')  GROUP BY rs1.userdata_id, rs1.branch_id   ),0) AS january,\n"
                  + "    COALESCE(( SELECT COUNT(rs2.id)  FROM log.removedstock rs2   WHERE rs2.userdata_id  = rs.userdata_id AND rs2.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('1 months' as interval)),'YYYY-MM') = to_char(date( rs2.processdate::date),'YYYY-MM')  GROUP BY rs2.userdata_id, rs2.branch_id   ),0) AS february,\n"
                  + "    COALESCE(( SELECT COUNT(rs3.id)  FROM log.removedstock rs3   WHERE rs3.userdata_id  = rs.userdata_id AND rs3.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('2 months' as interval)),'YYYY-MM') = to_char(date( rs3.processdate::date),'YYYY-MM')  GROUP BY rs3.userdata_id, rs3.branch_id   ),0) AS march,\n"
                  + "    COALESCE(( SELECT COUNT(rs4.id)  FROM log.removedstock rs4   WHERE rs4.userdata_id  = rs.userdata_id AND rs4.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('3 months' as interval)),'YYYY-MM') = to_char(date( rs4.processdate::date),'YYYY-MM')  GROUP BY rs4.userdata_id, rs4.branch_id   ),0) AS april,\n"
                  + "    COALESCE(( SELECT COUNT(rs5.id)  FROM log.removedstock rs5   WHERE rs5.userdata_id  = rs.userdata_id AND rs5.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('4 months' as interval)),'YYYY-MM') = to_char(date( rs5.processdate::date),'YYYY-MM')  GROUP BY rs5.userdata_id, rs5.branch_id   ),0) AS may,\n"
                  + "    COALESCE(( SELECT COUNT(rs6.id)  FROM log.removedstock rs6   WHERE rs6.userdata_id  = rs.userdata_id AND rs6.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('5 months' as interval)),'YYYY-MM') = to_char(date( rs6.processdate::date),'YYYY-MM')  GROUP BY rs6.userdata_id, rs6.branch_id   ),0) AS jun,\n"
                  + "    COALESCE(( SELECT COUNT(rs7.id)  FROM log.removedstock rs7   WHERE rs7.userdata_id  = rs.userdata_id AND rs7.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('6 months' as interval)),'YYYY-MM') = to_char(date( rs7.processdate::date),'YYYY-MM')  GROUP BY rs7.userdata_id, rs7.branch_id   ),0) AS july,\n"
                  + "    COALESCE(( SELECT COUNT(rs8.id)  FROM log.removedstock rs8   WHERE rs8.userdata_id  = rs.userdata_id AND rs8.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('7 months' as interval)),'YYYY-MM') = to_char(date( rs8.processdate::date),'YYYY-MM')  GROUP BY rs8.userdata_id, rs8.branch_id   ),0) AS august,\n"
                  + "    COALESCE(( SELECT COUNT(rs9.id)  FROM log.removedstock rs9   WHERE rs9.userdata_id  = rs.userdata_id AND rs9.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('8 months' as interval)),'YYYY-MM') = to_char(date( rs9.processdate::date),'YYYY-MM')  GROUP BY rs9.userdata_id, rs9.branch_id   ),0) AS september,\n"
                  + "    COALESCE(( SELECT COUNT(rs10.id) FROM log.removedstock rs10  WHERE rs10.userdata_id = rs.userdata_id AND rs10.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('9 months' as interval)),'YYYY-MM') = to_char(date( rs10.processdate::date),'YYYY-MM') GROUP BY rs10.userdata_id, rs10.branch_id  ),0) AS october,\n"
                  + "    COALESCE(( SELECT COUNT(rs11.id) FROM log.removedstock rs11  WHERE rs11.userdata_id = rs.userdata_id AND rs11.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('10 months' as interval)),'YYYY-MM') = to_char(date( rs11.processdate::date),'YYYY-MM') GROUP BY rs11.userdata_id, rs11.branch_id  ),0) AS november,\n"
                  + "    COALESCE(( SELECT COUNT(rs12.id) FROM log.removedstock rs12  WHERE rs12.userdata_id = rs.userdata_id AND rs12.branch_id = rs.branch_id AND  to_char(date((?)::date + cast('11 months' as interval)),'YYYY-MM') = to_char(date( rs12.processdate::date),'YYYY-MM') GROUP BY rs12.userdata_id, rs12.branch_id  ),0) AS december\n"
                  + " FROM\n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.branch br ON(br.id = rs.branch_id AND br.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id)"
                  + "  WHERE to_char(date((?)::date + cast('0 months' as interval)),'YYYY') = to_char(date( rs.processdate::date),'YYYY') \n"
                  + "  AND rs.branch_id IN (" + branchList + ")" + "\n"
                  + " GROUP BY\n"
                  + " 	rs.userdata_id, rs.branch_id, br.name, us.name, us.surname ORDER BY  us.name ASC \n"
                  + "   ";

        Object[] param = new Object[]{date, date, date, date, date, date, date, date, date, date, date, date, date};
        List<RemovedStockReport> result = getJdbcTemplate().query(sql, param, new RemovedStockReportMonthlyMapper());
        return result;
    }

    @Override
    public List<RemovedStock> listOfLog(Date beginDate, Date endDate, UserData userData, Branch branch) {

        String where = " ";
        if (branch.getId() != 0) {
            where = " AND rs.branch_id =  " + branch.getId() + "\n";
        }

        String sql = " SELECT \n"
                  + "    rs.id AS rsid ,\n "
                  + "    rs.userdata_id AS rsuserdata_id,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname, \n"
                  + "    rs.shift_id AS  rsshift_id,\n"
                  + "    shf.shiftno AS shfshiftno,\n"
                  + "    rs.currency_id AS rscurrency_id,\n"
                  + "    cr.code AS crcode,\n"
                  + "    cr.name AS  crname,\n"
                  + "    rs.stock_id AS rsstock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    (SELECT general.find_category(rs.stock_id, 1, ?)) AS category,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    stck.supplier_id AS supplier_id,\n"
                  + "    acc.name AS accname,\n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    stck.unit_id AS stckunit_id,\n"
                  + "    unt.unitrounding AS untunitrounding,\n"
                  + "    unt.sortname AS untsortname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    rs.oldvalue AS rsoldvalue,\n"
                  + "    rs.newvalue AS rsnewvalue,\n"
                  + "    rs.processdate AS rsprocessdate,\n"
                  + "    rs.unitprice AS rsunitprice,\n"
                  + "    rs.removedtotalprice AS rsremovedtotalprice,\n"
                  + "    rs.removedvalue AS rsremovedvalue \n"
                  + "  FROM\n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id) \n"
                  + "    INNER JOIN system.currency cr ON (cr.id = rs.currency_id )\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = rs.stock_id AND stck.deleted = FALSE )\n"
                  + "    INNER JOIN general.unit unt ON( unt.id = stck.unit_id AND unt.deleted = FALSE ) \n "
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "  WHERE\n"
                  + "    rs.processdate BETWEEN ? AND ?  AND rs.userdata_id = ?\n"
                  + where + "\n"
                  + "    ORDER BY rs.processdate DESC  ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), beginDate, endDate, userData.getId()};
        List<RemovedStock> result = getJdbcTemplate().query(sql, param, new RemovedStockMapper());
        return result;
    }

    @Override
    public List<ChartItem> yearlyRemovedStock(Date date, String branchList) {
        String sql = " SELECT\n"
                  + "    us.name AS usname, \n"
                  + "    us.surname AS ussurname,\n"
                  + "    count(rs.id) AS totalmoney\n"
                  + "   FROM\n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id)\n"
                  + "WHERE to_char(date((?)::date + cast('0 months' as interval)),'YYYY') = to_char(date( rs.processdate::date),'YYYY')\n"
                  + "      AND rs.branch_id IN(" + branchList + ")\n"
                  + " GROUP BY\n"
                  + " 	rs.userdata_id, us.name, us.surname\n"
                  + "   ";

        Object[] param = new Object[]{date};
        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> monthlyRemovedStock(Date beginDate, Date EndDate, String branchList) {
        String sql = " SELECT\n"
                  + "    us.name AS usname, \n"
                  + "    us.surname AS ussurname,\n"
                  + "    count(rs.id) AS totalmoney\n"
                  + "   FROM \n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id)\n"
                  + "WHERE rs.processdate BETWEEN ? AND ? \n"
                  + "      AND rs.branch_id IN(" + branchList + ")\n"
                  + " GROUP BY\n"
                  + " 	rs.userdata_id, us.name, us.surname\n"
                  + "   ";

        Object[] param = new Object[]{beginDate, EndDate};
        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public List<Shift> listOfShift(Date date) {
        String sql = "SELECT\n"
                  + "    shft.shiftno AS shftshiftno, \n"
                  + "    shft.id AS shftid \n "
                  + "  FROM \n"
                  + "  	general.shift shft\n"
                  + "  WHERE\n"
                  + "  	shft.deleted = FALSE  AND shft.branch_id = ? AND to_char(date((?)::date + cast('0 months' as interval)),'YYYY') = to_char(date( shft.begindate::date),'YYYY')  \n"
                  + "     AND shft.id IN (SELECT rm.shift_id FROM log.removedstock rm GROUP BY rm.shift_id) \n "
                  + " ORDER BY\n"
                  + "  shft.id DESC";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), date};
        List<Shift> result = getJdbcTemplate().query(sql, param, new RemovedStockShiftMapper());
        return result;
    }

    @Override
    public List<ChartItem> dailyRemovedStock(String branchList) {
        String sql = " SELECT\n"
                  + "        us.name AS usname,  \n"
                  + "        us.surname AS ussurname, \n"
                  + "        count(rs.id) AS totalmoney \n"
                  + "       FROM \n"
                  + "      log.removedstock rs \n"
                  + "        INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE) \n"
                  + "        INNER JOIN general.userdata us ON(us.id = rs.userdata_id) \n"
                  + "    WHERE to_char(date((now())::date + cast('0 months' as interval)),'YYYY-MM-DD') = to_char(date( rs.processdate::date),'YYYY-MM-DD') \n"
                  + "          AND rs.branch_id IN(" + branchList + ")\n"
                  + "     GROUP BY \n"
                  + "      rs.userdata_id, us.name, us.surname\n"
                  + "   ";

        List<ChartItem> result = getJdbcTemplate().query(sql, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> weeklyRemovedStock(Date beginDate, Date EndDate, String branchList) {
        String sql = " SELECT\n"
                  + "    us.name AS usname, \n"
                  + "    us.surname AS ussurname,\n"
                  + "    count(rs.id) AS totalmoney\n"
                  + "   FROM \n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id)\n"
                  + "WHERE   rs.processdate BETWEEN ? AND ? \n"
                  + "      AND rs.branch_id IN(" + branchList + ")\n"
                  + " GROUP BY\n"
                  + " 	rs.userdata_id, us.name, us.surname\n"
                  + "   ";

        Object[] param = new Object[]{beginDate, EndDate};
        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public String exportData() {
        String sql = " SELECT \n"
                  + "    rs.id AS rsid ,\n "
                  + "    rs.userdata_id AS rsuserdata_id,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname, \n"
                  + "    rs.shift_id AS  rsshift_id,\n"
                  + "    shf.shiftno AS shfshiftno,\n"
                  + "    rs.currency_id AS rscurrency_id,\n"
                  + "    cr.code AS crcode,\n"
                  + "    cr.name AS  crname,\n"
                  + "    rs.stock_id AS rsstock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    (SELECT general.find_category(rs.stock_id, 1, ?)) AS category,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    stck.supplier_id AS supplier_id,\n"
                  + "    acc.name AS accname,\n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    stck.unit_id AS stckunit_id,\n"
                  + "    unt.unitrounding AS untunitrounding,\n"
                  + "    unt.sortname AS untsortname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    rs.oldvalue AS rsoldvalue,\n"
                  + "    rs.newvalue AS rsnewvalue,\n"
                  + "    rs.processdate AS rsprocessdate,\n"
                  + "    rs.unitprice AS rsunitprice,\n"
                  + "    rs.removedtotalprice AS rsremovedtotalprice,\n"
                  + "    rs.removedvalue AS rsremovedvalue \n"
                  + "  FROM\n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.shift shf ON(shf.id = rs.shift_id AND shf.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id) \n"
                  + "    INNER JOIN system.currency cr ON (cr.id = rs.currency_id )\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = rs.stock_id AND stck.deleted = FALSE )\n"
                  + "    INNER JOIN general.unit unt ON( unt.id = stck.unit_id AND unt.deleted = FALSE ) \n "
                  + "    LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "    LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "  WHERE\n"
                  + "    rs.processdate  BETWEEN ? AND ?   AND rs.userdata_id = ? AND rs.branch_id = ?   ORDER BY rs.processdate DESC ";

        return sql;
    }

    @Override
    public List<RemovedStock> listOfRemovedStockForMarketShift(Shift shift) {
        String sql = " SELECT \n"
                  + "    rs.id AS rsid ,\n "
                  + "    rs.userdata_id AS rsuserdata_id,\n"
                  + "    us.name AS usname,\n"
                  + "    us.surname AS ussurname, \n"
                  + "    rs.shift_id AS  rsshift_id,\n"
                  + "    rs.currency_id AS rscurrency_id,\n"
                  + "    rs.stock_id AS rsstock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.unit_id AS stckunit_id,\n"
                  + "    unt.unitrounding AS untunitrounding,\n"
                  + "    unt.sortname AS untsortname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    rs.oldvalue AS rsoldvalue,\n"
                  + "    rs.newvalue AS rsnewvalue,\n"
                  + "    rs.processdate AS rsprocessdate,\n"
                  + "    rs.unitprice AS rsunitprice,\n"
                  + "    rs.removedtotalprice AS rsremovedtotalprice,\n"
                  + "    rs.removedvalue AS rsremovedvalue \n"
                  + "  FROM\n"
                  + " 	log.removedstock rs\n"
                  + "    INNER JOIN general.userdata us ON(us.id = rs.userdata_id) \n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = rs.stock_id)\n"
                  + "    INNER JOIN general.unit unt ON( unt.id = stck.unit_id) \n "
                  + "  WHERE\n"
                  + "    rs.shift_id= ? AND rs.branch_id = ?  ORDER BY rs.processdate DESC  ";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};
        List<RemovedStock> result = getJdbcTemplate().query(sql, param, new RemovedStockMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
