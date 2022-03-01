/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 02.10.2018 13:31:57
 */
package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FuelShiftDao extends JdbcDaoSupport implements IFuelShiftDao {

    @Autowired
    SessionBean sessionBean;

    int resultBatch = 0;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getResult() {
        return resultBatch;
    }

    public void setResult(int resultBatch) {
        this.resultBatch = resultBatch;
    }

    @Override
    public List<FuelShiftReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BranchSetting branchSetting) {

        String column = "";

        if (branchSetting.getAutomationId() == 1) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 1 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 2) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 5) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else {
            column = " 0 AS ttspaymentprice\n";
        }

        String sql = "SELECT \n"
                + "                    shf.id AS shfid, \n"
                + "                    shf.begindate AS shfbegindate, \n"
                + "                    shf.enddate AS shfenddate,\n"
                + "                    shf.shiftno AS shfshiftno,\n"
                + "                    sales.salecount AS salecount,\n"
                + "                    COALESCE(sales.ssltotalamount,0) AS ssltotalamount,\n"
                + "                    COALESCE(sales.ssltotalprice,0) AS ssltotalprice,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=TRUE THEN iem.price*iem.exchangerate END),0) AS iemincomeprices,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=FALSE THEN iem.price*iem.exchangerate END),0) AS iemexpenseprices,\n"
                + "                    COALESCE(SUM(CASE WHEN fdoc.type_id=50 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN fdoc.price*fdoc.exchangerate\n"
                + "                                      WHEN fdoc.type_id=49 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN -(fdoc.price*fdoc.exchangerate) END),0) AS deficitsurplus,\n"
                + "		       COALESCE(SUM(CASE WHEN shp.bankaccount_id IS NOT NULL THEN shp.saleprice END),0) AS shpcreditcardpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.credit_id IS NOT NULL THEN shp.saleprice END),0) AS creditpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.safe_id IS NOT NULL THEN shp.saleprice END),0) AS cashpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =8 THEN shp.saleprice END),0) AS dkvpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =9 THEN shp.saleprice END),0) AS utapaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =16 THEN shp.saleprice END),0) AS presentpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =98 THEN shp.saleprice END),0) AS paropaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =7 THEN shp.saleprice END),0) AS fuelcardpaymentprice,\n"
                + column + "\n"
                + "                FROM automation.shift shf\n"
                + "                LEFT JOIN automation.shiftpayment shp ON(shp.shift_id=shf.id AND shp.deleted=FALSE AND shp.is_reversemove = FALSE)\n"
                + "                LEFT JOIN finance.financingdocument fdoc ON (fdoc.id=shp.financingdocument_id AND fdoc.deleted=FALSE)\n"
                + "                LEFT JOIN finance.incomeexpensemovement iem ON (iem.financingdocument_id=fdoc.id AND iem.deleted=FALSE)\n"
                + "                LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                + "                LEFT JOIN (\n"
                + "                	SELECT\n"
                + "                         ssl.shift_id AS sslshift_id,\n"
                + "                         COALESCE(SUM(ssl.liter), 0) AS ssltotalamount,\n"
                + "                         COALESCE(SUM(ssl.totalmoney), 0) AS ssltotalprice,\n"
                + "                         COUNT(ssl.id) AS salecount\n"
                + "                    FROM automation.shiftsale ssl\n"
                + "                    WHERE ssl.deleted=FALSE\n"
                + "                    GROUP BY ssl.shift_id\n"
                + "                ) sales ON(sales.sslshift_id = shf.id)\n"
                + "                WHERE shf.branch_id=? AND shf.deleted=FALSE \n"
                + where + "\n"
                + "                GROUP BY shf.id, \n"
                + "                 	    shf.begindate, \n"
                + "                         shf.enddate,\n"
                + "                         shf.shiftno,\n"
                + "                         sales.salecount,\n"
                + "                         sales.ssltotalamount,\n"
                + "                         sales.ssltotalprice\n"
                + "ORDER BY shf.begindate DESC\n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<FuelShiftReport> result = getJdbcTemplate().query(sql, param, new FuelShiftReportMapper());
        return result;
    }

    @Override
    public List<FuelShiftReport> totals(String where, BranchSetting branchSetting) {
        String column = "";

        if (branchSetting.getAutomationId() == 1) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 1 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 2) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 5) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else {
            column = " 0 AS ttspaymentprice\n";
        }

        String sql = "SELECT\n"
                + "   COUNT(report.shfid) AS shfid,\n"
                + "   COALESCE(SUM(report.salecount),0) AS salecount,\n"
                + "   COALESCE(SUM(report.ssltotalamount),0) AS ssltotalamount,\n"
                + "   COALESCE(SUM(report.ssltotalprice),0) AS ssltotalprice,\n"
                + "   COALESCE(SUM(report.iemincomeprices),0) AS iemincomeprices,\n"
                + "   COALESCE(SUM(report.iemexpenseprices),0) AS iemexpenseprices,\n"
                + "   COALESCE(SUM(report.deficitsurplus),0) AS deficitsurplus,\n"
                + "   COALESCE(SUM(report.shpcreditcardpaymentprice),0) AS shpcreditcardpaymentprice,\n"
                + "   COALESCE(SUM(report.creditpaymentprice),0) AS creditpaymentprice,\n"
                + "   COALESCE(SUM(report.cashpaymentprice),0) AS cashpaymentprice,\n"
                + "   COALESCE(SUM(report.ttspaymentprice),0) AS ttspaymentprice,\n"
                + "   COALESCE(SUM(report.paropaymentprice),0) AS paropaymentprice,\n"
                + "   COALESCE(SUM(report.utapaymentprice),0) AS utapaymentprice,\n"
                + "   COALESCE(SUM(report.dkvpaymentprice),0) AS dkvpaymentprice,\n"
                + "   COALESCE(SUM(report.presentpaymentprice),0) AS presentpaymentprice,\n"
                + "   COALESCE(SUM(report.fuelcardpaymentprice),0) AS fuelcardpaymentprice\n"
                + "FROM\n"
                + "(SELECT \n"
                + "                    shf.id AS shfid, \n"
                + "                    sales.salecount,\n"
                + "                    sales.ssltotalamount,\n"
                + "                    sales.ssltotalprice,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=TRUE THEN iem.price*iem.exchangerate END),0) AS iemincomeprices,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=FALSE THEN iem.price*iem.exchangerate END),0) AS iemexpenseprices,\n"
                + "                    COALESCE(SUM(CASE WHEN fdoc.type_id=50 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN fdoc.price*fdoc.exchangerate\n"
                + "                                      WHEN fdoc.type_id=49 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN -(fdoc.price*fdoc.exchangerate) END),0) AS deficitsurplus,\n"
                + "		       COALESCE(SUM(CASE WHEN shp.bankaccount_id IS NOT NULL THEN shp.saleprice END),0) AS shpcreditcardpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.credit_id IS NOT NULL THEN shp.saleprice END),0) AS creditpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.safe_id IS NOT NULL THEN shp.saleprice END),0) AS cashpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =8 THEN shp.saleprice END),0) AS dkvpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =9 THEN shp.saleprice END),0) AS utapaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =16 THEN shp.saleprice END),0) AS presentpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =98 THEN shp.saleprice END),0) AS paropaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =7 THEN shp.saleprice END),0) AS fuelcardpaymentprice,\n"
                + column + "\n"
                + " FROM automation.shift shf\n"
                + "     LEFT JOIN automation.shiftpayment shp ON(shp.shift_id=shf.id AND shp.deleted=FALSE AND shp.is_reversemove = FALSE)\n"
                + "     LEFT JOIN finance.financingdocument fdoc ON (fdoc.id=shp.financingdocument_id AND fdoc.deleted=FALSE)\n"
                + "     LEFT JOIN finance.incomeexpensemovement iem ON (iem.financingdocument_id=fdoc.id AND iem.deleted=FALSE)\n"
                + "     LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                + "     LEFT JOIN (\n"
                + "                 SELECT\n"
                + "                       ssl.shift_id AS sslshift_id,\n"
                + "                       COALESCE(SUM(ssl.liter), 0) AS ssltotalamount,\n"
                + "                       COALESCE(SUM(ssl.totalmoney), 0) AS ssltotalprice,\n"
                + "                       COUNT(ssl.id) AS salecount\n"
                + "                  FROM automation.shiftsale ssl\n"
                + "                  WHERE ssl.deleted=FALSE\n"
                + "                  GROUP BY ssl.shift_id\n"
                + "                ) sales ON(sales.sslshift_id = shf.id)\n"
                + "     WHERE shf.branch_id=? AND shf.deleted=FALSE \n"
                + where + "\n"
                + "                GROUP BY shf.id, \n"
                + "                 	    shf.begindate, \n"
                + "                         shf.enddate,\n"
                + "                         shf.shiftno,\n"
                + "                         sales.salecount,\n"
                + "                         sales.ssltotalamount,\n"
                + "                         sales.ssltotalprice\n"
                + " ) report";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<FuelShiftReport> result = getJdbcTemplate().query(sql, param, new FuelShiftReportMapper());
        return result;

    }

    @Override
    public int createShift(FuelShift fuelShift) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String exportData(String where, BranchSetting branchSetting) {

        String column = "";

        if (branchSetting.getAutomationId() == 1) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 1 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 2) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (branchSetting.getAutomationId() == 5) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else {
            column = " 0 AS ttspaymentprice\n";
        }

        String sql = "SELECT \n"
                + "                    shf.id AS shfid, \n"
                + "                    shf.begindate AS shfbegindate, \n"
                + "                    shf.enddate AS shfenddate,\n"
                + "                    shf.shiftno AS shfshiftno,\n"
                + "                    sales.salecount As salecount,\n"
                + "                    COALESCE(sales.ssltotalamount,0) AS ssltotalamount,\n"
                + "                    COALESCE(sales.ssltotalprice,0) AS ssltotalprice,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=TRUE THEN iem.price*iem.exchangerate END),0) AS iemincomeprices,\n"
                + "                    COALESCE(SUM(CASE WHEN iem.is_direction=FALSE THEN iem.price*iem.exchangerate END),0) AS iemexpenseprices,\n"
                + "                    COALESCE(SUM(CASE WHEN fdoc.type_id=50 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN fdoc.price*fdoc.exchangerate\n"
                + "                                      WHEN fdoc.type_id=49 AND (shp.fuelsaletype_id is null or  fst.typeno <>98) AND iem.id IS NULL THEN -(fdoc.price*fdoc.exchangerate) END),0) AS deficitsurplus,\n"
                + "		       COALESCE(SUM(CASE WHEN shp.bankaccount_id IS NOT NULL THEN shp.saleprice END),0) AS shpcreditcardpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.credit_id IS NOT NULL THEN shp.saleprice END),0) AS creditpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN shp.safe_id IS NOT NULL THEN shp.saleprice END),0) AS cashpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =8 THEN shp.saleprice END),0) AS dkvpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =9 THEN shp.saleprice END),0) AS utapaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =16 THEN shp.saleprice END),0) AS presentpaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =98 THEN shp.saleprice END),0) AS paropaymentprice,\n"
                + "                    COALESCE(SUM(CASE WHEN fst.typeno =7 THEN shp.saleprice END),0) AS fuelcardpaymentprice,\n"
                + column + "\n"
                + "                FROM automation.shift shf\n"
                + "                LEFT JOIN automation.shiftpayment shp ON(shp.shift_id=shf.id AND shp.deleted=FALSE AND shp.is_reversemove = FALSE)\n"
                + "                LEFT JOIN finance.financingdocument fdoc ON (fdoc.id=shp.financingdocument_id AND fdoc.deleted=FALSE)\n"
                + "                LEFT JOIN finance.incomeexpensemovement iem ON (iem.financingdocument_id=fdoc.id AND iem.deleted=FALSE)\n"
                + "                LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted=FALSE AND fst.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "                LEFT JOIN (\n"
                + "                	SELECT\n"
                + "                         ssl.shift_id AS sslshift_id,\n"
                + "                         COALESCE(SUM(ssl.liter), 0) AS ssltotalamount,\n"
                + "                         COALESCE(SUM(ssl.totalmoney), 0) AS ssltotalprice,\n"
                + "                         COUNT(ssl.id) AS salecount\n"
                + "                    FROM automation.shiftsale ssl\n"
                + "                    WHERE ssl.deleted=FALSE\n"
                + "                    GROUP BY ssl.shift_id\n"
                + "                ) sales ON(sales.sslshift_id = shf.id)\n"
                + "                WHERE shf.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND shf.deleted=FALSE \n"
                + where + "\n"
                + "                GROUP BY shf.id, \n"
                + "                 	    shf.begindate, \n"
                + "                         shf.enddate,\n"
                + "                         shf.shiftno,\n"
                + "                         sales.salecount,\n"
                + "                         sales.ssltotalamount,\n"
                + "                         sales.ssltotalprice\n"
                + "ORDER BY shf.begindate DESC\n";

        return sql;
    }

    /**
     * Bu metod txt dosyadan dosya uzantısına ve içeriğine göre veri çeker
     * listeler ve veritabanına yazar.
     *
     * @param shiftSales
     * @param shiftNo
     * @param json
     * @return
     */
    @Override
    public FuelShift insertShiftAndShiftSales(String json) {

        resultBatch = 0;

        String sql = " SELECT r_result_id, r_resultmessage FROM automation.insert_shift(?, ?, ?)";

        Object[] param = new Object[]{
            sessionBean.getUser().getLastBranch().getId(),
            json,
            sessionBean.getUser().getId()};

        List<FuelShift> result = getJdbcTemplate().query(sql, param, new FuelShiftMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new FuelShift();
        }

    }

    @Override
    public List<FuelShiftSales> findAttendantSales(FuelShift fuelShift, BranchSetting branchSetting) {

        String sql = "SELECT \n"
                + "SUM(ss.totalmoney) sstotalmoney,\n"
                + "sum(ss.liter) as sstotalquantity,\n"
                + "ss.attendantname ssattendant\n"
                + "FROM automation.shiftsale ss\n"
                + "WHERE ss.shift_id=? and ss.deleted=FALSE\n"
                + "GROUP BY ss.attendantname ORDER BY ss.attendantname";

        Object[] param = new Object[]{fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());

        return result;

    }

    @Override
    public List<FuelShiftSales> findStockNameSales(FuelShift fuelShift, BranchSetting branchSetting) {

        String sql = "SELECT \n"
                + "SUM(ss.totalmoney) sstotalmoney,\n"
                + "sum(ss.liter) as sstotalquantity,\n"
                + "ss.stockname ssstckname\n"
                + "FROM automation.shiftsale ss\n"
                + "WHERE ss.shift_id=? and ss.deleted=FALSE\n"
                + "GROUP BY ss.stockname ORDER BY ss.stockname";

        Object[] param = new Object[]{fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());

        return result;

    }

    @Override
    public List<FuelShiftSales> findSaleTypeSales(FuelShift fuelShift, BranchSetting branchSetting) {

        String sql = "SELECT \n"
                + "SUM(ss.totalmoney) sstotalmoney,\n"
                + "fst.name fstname,\n"
                + "sum(ss.liter) as sstotalquantity\n"
                + "FROM automation.shiftsale ss\n"
                + "JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.branch_id=?)\n"
                + "WHERE ss.shift_id=? and ss.deleted=FALSE\n"
                + "GROUP BY fst.name ORDER BY fst.name";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());

        return result;

    }

    @Override
    public String findIntegrationName(String json, int processType) {

        String sql = "SELECT r_result FROM automation.find_integrationnameforshift(?, ?, ?)";

        Object[] param = new Object[]{json, sessionBean.getUser().getLastBranch().getId(), processType};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
