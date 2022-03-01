/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 03:40:48
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.FuelShiftMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FuelShiftTransferDao extends JdbcDaoSupport implements IFuelShiftTransferDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FuelShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, boolean isCheckDeleted) {

        if (sortField == null) {
            sortField = "shf.begindate";
            sortOrder = " DESC ";
        }

        String column = "";

        if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 1) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 1 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 2) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 5) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice\n";
        } else {
            column = " 0 AS ttspaymentprice\n";
        }

        String where1 = "", where2 = "", where3 = "", where4 = "", where5 = "", where6 = "";

        if (isCheckDeleted) {//Silinenler gelsin
            where1 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= fdoc.d_time ELSE fdoc.deleted = FALSE END ";
            where2 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= fiem.d_time ELSE fiem.deleted = FALSE END ";
            where3 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= shp.d_time ELSE shp.deleted = FALSE END ";
            where4 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= ssl1.d_time ELSE ssl1.deleted = FALSE END ";
            where5 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= sales.ssld_time ELSE sales.ssldeleted = FALSE END ";
            where6 = " ";
        } else {///Silinenler gelmesin
            where1 = " AND fdoc.deleted = FALSE ";
            where2 = " AND fiem.deleted = FALSE ";
            where3 = " AND shp.deleted = FALSE ";
            where4 = " AND ssl1.deleted = FALSE ";
            where5 = " AND sales.ssldeleted = FALSE ";
            where6 = " AND shf.deleted=FALSE ";
        }

        String sql = "SELECT\n"
                  + "     shf.id AS shfid,\n"
                  + "     shf.shiftno AS shfshiftno,\n"
                  + "     shf.deleted,\n"
                  + "     shf.d_time AS shfd_time,\n"
                  + "     shf.begindate AS shfbegindate,\n"
                  + "     shf.enddate AS shfenddate,\n"
                  + "     shf.is_confirm AS shfis_confirm,\n"
                  + "     COALESCE(sales.ssltotalamount,0) AS ssltotalamount,\n"
                  + "     COALESCE(sales.ssltotalprice,0) AS sslprice,\n"
                  + "     (SELECT COALESCE(SUM(CASE WHEN COALESCE(shp.bankaccount_id,0)>0 THEN shp.saleprice\n"
                  + "        			    WHEN COALESCE(shp.safe_id,0)>0 THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)>0 AND fie.is_income THEN -shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)=0 AND fdoc.type_id=49 THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)>0 AND fie.is_income=FALSE THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)=0 AND fdoc.type_id=50 THEN -shp.saleprice   \n"
                  + "                               WHEN COALESCE(shp.credit_id,0)>0 THEN shp.saleprice ELSE 0 END),0) \n"
                  + "     FROM automation.shiftpayment shp \n"
                  + "       LEFT JOIN finance.financingdocument fdoc ON(fdoc.id=shp.financingdocument_id " + where1 + ")\n"
                  + "       LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id " + where2 + ")\n"
                  + "       LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n"
                  + "     WHERE shp.shift_id=shf.id AND shp.is_reversemove = FALSE " + where3 + "\n"
                  + "     ) AS shiftpaymenttotal,\n"
                  + "     (SELECT \n"
                  + "          xmlelement(\n"
                  + "            name \"attendantshift\",\n"
                  + "            xmlagg(\n"
                  + "              xmlelement(\n"
                  + "                  name \"attendantsaleshift\",\n"
                  + "                  xmlforest (\n"
                  + "                  a.accname AS \"name\",\n"
                  + "                  a.acctitle AS \"title\"\n"
                  + "                )\n"
                  + "              )\n"
                  + "            )\n"
                  + "          )\n"
                  + "      FROM \n"
                  + "        (SELECT \n"
                  + "    	DISTINCT acc.id,\n"
                  + "           COALESCE(acc.name, '') AS accname,\n"
                  + "           COALESCE(acc.title,'') AS acctitle\n"
                  + "        FROM automation.shiftsale ssl1\n"
                  + "           INNER JOIN general.account acc ON(acc.id=ssl1.attendant_id)\n"
                  + "        WHERE ssl1.shift_id=shf.id " + where4 + ") as a\n"
                  + ") AS shiftattendant,\n"
                  + " COALESCE(SUM(CASE WHEN shp.credit_id IS NOT NULL THEN shp.saleprice END),0) AS creditpaymentprice,\n"
                  + " COALESCE(SUM(CASE WHEN shp.bankaccount_id IS NOT NULL THEN shp.saleprice END),0) AS shpcreditcardpaymentprice,\n"
                  + "\n"
                  + column + "\n"
                  + "FROM automation.shift shf \n"
                  + "LEFT JOIN automation.shiftpayment shp ON(shp.shift_id=shf.id AND shp.deleted=FALSE AND shp.is_reversemove = FALSE)\n"
                  + "LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted=FALSE AND fst.branch_id=?) \n"
                  + "LEFT JOIN (\n"
                  + "              	SELECT\n"
                  + "                         ssl.shift_id AS sslshift_id,\n"
                  + "                         COALESCE(SUM(ssl.liter), 0) AS ssltotalamount,\n"
                  + "                         COALESCE(SUM(ssl.totalmoney), 0) AS ssltotalprice,\n"
                  + "                         COUNT(ssl.id) AS salecount,\n"
                  + "                         ssl.d_time AS ssld_time,\n"
                  + "                         ssl.deleted AS ssldeleted\n"
                  + "                    FROM automation.shiftsale ssl\n"
                  + "                    INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "                    WHERE fst.typeno <> 6\n"
                  + "                  GROUP BY ssl.shift_id,ssl.d_time,ssl.deleted\n"
                  + "                ) sales ON(sales.sslshift_id = shf.id " + where5 + ")\n"
                  + "WHERE shf.branch_id=?  " + where6 + "\n"
                  + where + "\n"
                  + "GROUP BY shf.id, shf.shiftno, shf.begindate, shf.enddate, sales.ssltotalamount, sales.ssltotalprice\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<FuelShift> result = getJdbcTemplate().query(sql, param, new FuelShiftMapper());
        return result;
    }

    @Override
    public List<FuelShift> count(String where, boolean isCheckDeleted) {
        String where1 = "", where2 = "", where3 = "", where4 = "", where5 = "", where6 = "";
        if (isCheckDeleted) {//Silinenler gelsin
            where1 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= sales.ssld_time ELSE sales.ssldeleted = FALSE END ";
            where2 = " ";
        } else {///Silinenler gelmesin
            where1 = " AND sales.ssldeleted = FALSE ";
            where2 = " AND shf.deleted=FALSE ";
        }
        String column = "";

        if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 1) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 1 THEN shp.saleprice END),0) AS ttspaymentprice,\n";
        } else if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 2) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice,\n";
        } else if (sessionBean.getUser().getLastBranchSetting().getAutomationId() == 5) {
            column = " COALESCE(SUM(CASE WHEN fst.typeno = 3 THEN shp.saleprice END),0) AS ttspaymentprice,\n";
        } else {
            column = " 0 AS ttspaymentprice,\n";
        }

        if (isCheckDeleted) {//Silinenler gelsin
            where3 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= fdoc.d_time ELSE fdoc.deleted = FALSE END ";
            where4 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= fiem.d_time ELSE fiem.deleted = FALSE END ";
            where5 = " AND CASE WHEN shf.deleted = TRUE THEN shf.d_time <= shp.d_time ELSE shp.deleted = FALSE END ";
        } else {///Silinenler gelmesin
            where3 = " AND fdoc.deleted = FALSE ";
            where4 = " AND fiem.deleted = FALSE ";
            where5 = " AND shp.deleted = FALSE ";
        }

        String sql = "  SELECT \n"
                  + "   COUNT(totals.shfid) AS shfid,\n"
                  + "   COALESCE(SUM(totals.ssltotalamount),0) AS ssltotalamount,\n"
                  + "   COALESCE(SUM(totals.sslprice),0) AS sslprice,\n"
                  + "   COALESCE(SUM(totals.shpcreditcardpaymentprice),0) AS shpcreditcardpaymentprice,\n"
                  + "   COALESCE(SUM(totals.creditpaymentprice),0) AS creditpaymentprice,\n"
                  + "   COALESCE(SUM(totals.ttspaymentprice),0) AS ttspaymentprice,\n"
                  + "   COALESCE(SUM(totals.shiftpaymenttotal),0) AS shiftpaymenttotal\n"
                  + "   FROM(\n"
                  + "   SELECT\n"
                  + "     shf.id AS shfid,\n"
                  + "     sales.ssltotalamount AS ssltotalamount,\n"
                  + "     sales.ssltotalprice AS sslprice,\n"
                  + "     (SELECT COALESCE(SUM(CASE WHEN COALESCE(shp.bankaccount_id,0)>0 THEN shp.saleprice\n"
                  + "        			    WHEN COALESCE(shp.safe_id,0)>0 THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)>0 AND fie.is_income THEN -shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)=0 AND fdoc.type_id=49 THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)>0 AND fie.is_income=FALSE THEN shp.saleprice\n"
                  + "                               WHEN COALESCE(fie.id,0)=0 AND fdoc.type_id=50 THEN -shp.saleprice   \n"
                  + "                               WHEN COALESCE(shp.credit_id,0)>0 THEN shp.saleprice ELSE 0 END),0) \n"
                  + "     FROM automation.shiftpayment shp \n"
                  + "       LEFT JOIN finance.financingdocument fdoc ON(fdoc.id=shp.financingdocument_id " + where3 + " )\n"
                  + "       LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id  " + where4 + " )\n"
                  + "       LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n"
                  + "     WHERE shp.shift_id=shf.id AND shp.is_reversemove = FALSE " + where5 + " \n"
                  + "     ) AS shiftpaymenttotal,\n"
                  + column + "\n"
                  + " COALESCE(SUM(CASE WHEN shp.credit_id IS NOT NULL THEN shp.saleprice END),0) AS creditpaymentprice,\n"
                  + " COALESCE(SUM(CASE WHEN shp.bankaccount_id IS NOT NULL THEN shp.saleprice END),0) AS shpcreditcardpaymentprice\n"
                  + "FROM automation.shift shf \n"
                  + "LEFT JOIN automation.shiftpayment shp ON(shp.shift_id=shf.id AND shp.deleted=FALSE AND shp.is_reversemove = FALSE)\n"
                  + "LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted=FALSE AND fst.branch_id=?) \n"
                  + "LEFT JOIN (\n"
                  + "              	SELECT\n"
                  + "                         ssl.shift_id AS sslshift_id,\n"
                  + "                         COALESCE(SUM(ssl.liter), 0) AS ssltotalamount,\n"
                  + "                         COALESCE(SUM(ssl.totalmoney), 0) AS ssltotalprice,\n"
                  + "                         COUNT(ssl.id) AS salecount,\n"
                  + "                         ssl.d_time AS ssld_time,\n"
                  + "                         ssl.deleted AS ssldeleted\n"
                  + "                    FROM automation.shiftsale ssl\n"
                  + "                    INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "                    WHERE fst.typeno <> 6\n"
                  + "                    GROUP BY ssl.shift_id,ssl.d_time,ssl.deleted\n"
                  + "                ) sales ON(sales.sslshift_id = shf.id  " + where1 + ")\n"
                  + "WHERE shf.branch_id=?  " + where + "" + where2 + " \n"
                  + "GROUP BY shf.id, shf.shiftno, shf.begindate, shf.enddate, sales.ssltotalamount, sales.ssltotalprice\n"
                  + "   ) totals\n";

        String sql1 = "SELECT COUNT(u.shfid) FROM\n"
                  + "   (SELECT\n"
                  + "     COUNT(shf.id) AS shfid\n"
                  + "   FROM automation.shift shf \n"
                  + "   LEFT JOIN automation.shiftsale ssl ON(ssl.shift_id=shf.id " + where1 + ")\n"
                  + "   WHERE shf.branch_id=?  " + where + "" + where2 + "\n"
                  + "   GROUP BY shf.id, shf.shiftno, shf.begindate, shf.enddate\n"
                  + "   ) u ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<FuelShift> result = getJdbcTemplate().query(sql, param, new FuelShiftMapper());
        return result;

    }

    @Override
    public List<FuelShiftSales> findAllAttendant(FuelShift fuelShift, BranchSetting branchSetting) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ssl.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ssl.deleted = FALSE ";
        }

        String where = "";
        if (branchSetting.getAutomationId() == 1) {
            where = " AND ssl.saletype NOT IN(1, 2, 5, 11, 12, 21, 22, 35, 41, 51, 55, 56, 57, 60, 61, 71, 81, 91, 101, 102, 103, 104, 106) ";
        } else if (branchSetting.getAutomationId() == 2) {
            where = " AND ssl.saletype NOT IN(2, 3, 4, 6, 7, 8, 9 ,16) ";
        } else if (branchSetting.getAutomationId() == 4) {
            where = " AND ssl.saletype NOT IN(1, 2) ";
        } else if (branchSetting.getAutomationId() == 3) {
            where = " AND ssl.saletype NOT IN(2) ";
        } else if (branchSetting.getAutomationId() == 5) {
            where = " AND ssl.saletype NOT IN(2,3) ";
        }

        String sql = "SELECT \n"
                  + "    ssl.attendantname AS ssattendant,\n"
                  + "    ssl.attendantcode AS ssattendantcode,\n"
                  + "    ssl.attendant_id AS empiaccount_id,\n"
                  + "    SUM(ssl.totalmoney) AS ssltotalmoney,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    COALESCE(acc.name,'') AS accname,\n"
                  + "    COALESCE(acc.title,'') AS acctitle\n"
                  + "FROM automation.shiftsale ssl\n"
                  + "LEFT JOIN general.account acc ON(acc.id=ssl.attendant_id)\n"
                  + "INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=? AND fst.typeno <> 6)\n"
                  + "WHERE ssl.shift_id=? " + where1 + " " + where + "\n"
                  + "GROUP BY ssl.attendantname, ssl.attendantcode, ssl.attendant_id, acc.is_employee, acc.name, acc.title\n"
                  + "ORDER BY ssl.attendantname\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftTransferMapper());
        return result;
    }

    @Override
    public List<FuelShiftSales> findAllAttendantSale(FuelShiftSales fuelShiftSales) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShiftSales.getFuelShift().isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShiftSales.getFuelShift().getDeletedTime()) + "' <= ssl.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ssl.deleted = FALSE ";
        }

        String sql = "SELECT \n"
                  + "   COALESCE(SUM(ssl.totalmoney),0) AS ssltotalmoney,\n"
                  + "   ssl.saletype AS sslsaletype,\n"
                  + "   fst.name AS fstname\n"
                  + "FROM automation.shiftsale ssl\n"
                  + "INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "WHERE ssl.shift_id=? " + where1 + " AND ssl.attendantcode=?\n"
                  + "GROUP BY ssl.saletype, fst.name\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShiftSales.getFuelShift().getId(), fuelShiftSales.getAttendantCode()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftTransferMapper());
        return result;
    }

    @Override
    public List<FuelShiftSales> findAllSaleForShift(FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ssl.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ssl.deleted = FALSE ";
        }

        String sql = "SELECT\n"
                  + "   COALESCE(SUM(ssl.totalmoney),0) AS ssltotalmoney,\n"
                  + "   ssl.saletype AS sslsaletype,\n"
                  + "   fst.name AS fstname,\n"
                  + "   ssl.attendantcode AS ssattendantcode\n"
                  + "FROM automation.shiftsale ssl\n"
                  + "INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "WHERE ssl.shift_id=? " + where1 + "\n"
                  + "GROUP BY ssl.saletype, fst.name, ssl.attendantcode";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftTransferMapper());
        return result;
    }

    @Override
    public List<FuelShiftSales> findAllSale(FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ssl.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ssl.deleted = FALSE ";
        }

        String sql = "SELECT\n"
                  + "   COALESCE(SUM(ssl.totalmoney),0) AS ssltotalmoney,\n"
                  + "   ssl.saletype AS sslsaletype,\n"
                  + "   fst.name AS fstname\n"
                  + "FROM automation.shiftsale ssl\n"
                  + "INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ssl.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "WHERE ssl.shift_id=? " + where1 + "\n"
                  + "GROUP BY ssl.saletype, fst.name";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftTransferMapper());
        return result;
    }

    @Override
    public List<ShiftPayment> findAllShiftPayment(FuelShift fuelShift, Account account, int type) {
        String accountWhere = "";

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "", where2 = "", where3 = "", where4 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= fdoc.d_time ";
            where2 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= fiem.d_time ";
            where3 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= cr.d_time ";
            where4 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= sp.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND fdoc.deleted = FALSE ";
            where2 = " AND fiem.deleted = FALSE ";
            where3 = " AND cr.deleted = FALSE ";
            where4 = " AND sp.deleted=FALSE ";
        }

        if (type == 0) {
            if (account.getId() == 0) {
                accountWhere = " AND is_automation = FALSE AND sp.attendant_id IS NULL ";
            } else {
                accountWhere = " AND is_automation = FALSE AND sp.attendant_id=" + account.getId();
            }
        } else if (type == 1) {
            accountWhere = " ";
        } else if (type == 2) {//Otomasyonsuz Satışları Getirmek İçin 
            accountWhere = " AND is_automation = TRUE ";
        }
        String sql = "SELECT \n"
                  + "      sp.id as spid,\n"
                  + "      sp.safe_id as spsafeid,\n"
                  + "      sp.bankaccount_id as spbankaccountid,\n"
                  + "      sp.attendant_id  as spaccountid,\n"
                  + "      sp.saleprice as spprice,\n"
                  + "      sp.processdate AS spprocessdate,\n"
                  + "      sp.is_automation AS spisautomation,\n"
                  + "      fdoc.id as fdocid,\n"
                  + "      fdoc.type_id as fdoctype_id,\n"
                  + "      fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                  + "      fie.name as fiename, \n"
                  + "      fie.is_income AS fieis_income,\n"
                  + "      ba.name as baname,\n"
                  + "      sf.name as sfname,\n"
                  + "      COALESCE(acc.name,'') as accname,\n"
                  + "      COALESCE(acc.title,'') as acctitle,\n"
                  + "      acc.is_employee AS accis_employee,\n"
                  + "      sp.credit_id AS spcredit_id,\n"
                  + "      acc1.id AS acc1id,\n"
                  + "      acc1.name AS acc1name,\n"
                  + "      acc1.title as acc1title,\n"
                  + "      acc1.is_employee AS acc1is_employee,\n"
                  + "      acc2.id as acc2id,\n"
                  + "      acc2.name AS acc2name,\n"
                  + "      acc2.title as acc2title,\n"
                  + "      acc2.is_employee AS acc2is_employee,\n"
                  + "      cr.duedate AS crduedate,\n"
                  + "      sp.fuelsaletype_id AS spfueltype_id,\n"
                  + "      fst.name AS fstname,\n"
                  + "      fst.typeno AS fsttypeno\n"
                  + "  FROM automation.shiftpayment sp\n"
                  + "  LEFT JOIN finance.financingdocument fdoc ON(fdoc.id=sp.financingdocument_id " + where1 + ")\n"
                  + "  LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id " + where2 + ")\n "
                  + "  LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                  + "  LEFT JOIN finance.bankaccount ba ON(ba.id =sp.bankaccount_id)\n"
                  + "  LEFT JOIN finance.safe sf ON(sf.id=sp.safe_id)\n"
                  + "  LEFT JOIN general.account acc ON(acc.id=sp.attendant_id)\n"
                  + "  LEFT JOIN finance.credit cr ON(cr.id=sp.credit_id " + where3 + ")\n"
                  + "  LEFT JOIN general.account acc1 ON(acc1.id=cr.account_id)\n"
                  + "  LEFT JOIN general.accountmovement accm ON(accm.financingdocument_id=fdoc.id)\n"
                  + "  LEFT JOIN general.account acc2 ON(acc2.id=accm.account_id)\n"
                  + "  LEFT JOIN automation.fuelsaletype fst ON(fst.id=sp.fuelsaletype_id AND fst.deleted=False AND fst.branch_id=?)\n"
                  + "  WHERE sp.shift_id=? AND sp.is_reversemove = FALSE " + accountWhere + "\n"
                  + "  " + where4 + "  ORDER BY sp.id\n";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<ShiftPayment> result = getJdbcTemplate().query(sql, param, new FuelShiftPaymentMapper());
        return result;
    }

    @Override
    public List<FuelShiftSales> findAllCreditSales(FuelShift fuelShift, FuelShiftSales fuelShiftSales, BranchSetting branchSetting, boolean isAllSales) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "", where2 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= crdt.d_time ";
            where2 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ssl.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND crdt.deleted = FALSE ";
            where2 = " AND ssl.deleted = FALSE ";
        }

        String where = "";
        if (branchSetting.getAutomationId() == 1) {
            where = " AND ssl.saletype NOT IN(1, 2, 5, 11, 12, 21, 22, 35, 41, 51, 55, 56, 57, 60, 61, 71, 81, 91, 101, 102, 103, 104, 106) ";
        } else if (branchSetting.getAutomationId() == 2) {
            where = " AND ssl.saletype NOT IN(2, 3, 4, 6, 7, 8, 9, 16, 99) ";
        } else if (branchSetting.getAutomationId() == 4) {
            where = " AND ssl.saletype NOT IN(1, 2) ";
        } else if (branchSetting.getAutomationId() == 3) {
            where = " AND ssl.saletype NOT IN(2) ";
        } else if (branchSetting.getAutomationId() == 5) {
            where = " AND ssl.saletype NOT IN(2, 3) ";
        }

        String join = "";
        if (!isAllSales) { //Sadece veresiye satışları
            join = " INNER JOIN automation.shiftcreditsale scs ON(scs.saledatetime= ssl.processdate AND CAST(scs.nozzleno AS integer) = CAST(ssl.nozzleno AS integer) AND CAST(scs.pumpno AS integer) = CAST(ssl.pumpno AS integer) AND scs.total = ssl.totalmoney AND scs.branch_id = " + sessionBean.getUser().getLastBranch().getId() + " AND scs.deleted = FALSE) \n";
        }

        String sql = "SELECT \n"
                  + "	 ssl.processdate AS sslprocessdate,\n"
                  + "    stck.name AS stckname,\n"
                  + "    ssl.price AS sslprice,\n"
                  + "    ssl.liter AS sslliter,\n"
                  + "    ssl.totalmoney AS ssltotalmoney,\n"
                  + "    ssl.id AS sslid,\n"
                  + "    acc.id AS accid,\n"
                  + "    acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    ssl.plate AS sslplate,\n"
                  + "    ssl.receiptno AS sslreceiptno\n"
                  + "FROM automation.shiftsale ssl \n"
                  + "	 LEFT JOIN inventory.stockinfo stcki ON(stcki.fuelintegrationcode = ssl.stockcode AND stcki.deleted=FALSE AND stcki.branch_id = ?)\n"
                  + "    LEFT JOIN inventory.stock stck ON(stck.id = stcki.stock_id)\n"
                  + "    LEFT JOIN finance.credit crdt ON(crdt.id = ssl.credit_id " + where1 + ")\n"
                  + "    LEFT JOIN general.account acc ON(acc.id=crdt.account_id)\n"
                  + join + "\n"
                  + "WHERE ssl.attendantcode = ?\n"
                  + "    AND ssl.shift_id = ? AND ssl.credit_id IS NULL \n"
                  + where2 + "\n"
                  + where + "\n"
                  + "ORDER BY ssl.processdate DESC";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShiftSales.getAttendantCode(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftTransferMapper());
        return result;
    }

    @Override
    public int update(FuelShift fuelShift) {
        String sql = "UPDATE automation.shift SET is_confirm= ?, u_id = ?, u_time = now() WHERE id = ? ";

        Object[] param = new Object[]{fuelShift.isIsConfirm(), sessionBean.getUser().getId(), fuelShift.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int create(FuelShift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(FuelShift fuelShift) {
        String sql = "SELECT r_shift_id FROM automation.delete_shift(?, ?)";

        Object[] param = new Object[]{fuelShift.getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList) {

        int attendantId = 0;

        if (shiftPayment.getFinancingDocument().getFinancingType().getId() == 49 || shiftPayment.getFinancingDocument().getFinancingType().getId() == 50) {
            if (shiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0) {
                attendantId = shiftPayment.getAttendantAccount().getId();
            } else {
                attendantId = 0;
            }

        } else {
            attendantId = 0;
        }

        String sql = "SELECT r_result_id FROM automation.process_shiftpayment(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{processType,
            shiftPayment.getFinancingDocument().getId(),
            shiftPayment.getShift().getId(),
            shiftPayment.getAccount().getId() == 0 ? null : shiftPayment.getAccount().getId(),
            attendantId == 0 ? null : attendantId,
            shiftPayment.getFinancingDocument().getFinancingType().getId(),
            shiftPayment.getBankAccount().getId() == 0 ? null : shiftPayment.getBankAccount().getId(),
            shiftPayment.getSafe().getId() == 0 ? null : shiftPayment.getSafe().getId(),
            shiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0 ? null : shiftPayment.getFinancingDocument().getIncomeExpense().getId(),
            shiftPayment.getFinancingDocument().getDocumentNumber(),
            shiftPayment.getFinancingDocument().getDocumentDate(),
            shiftPayment.getFinancingDocument().getPrice(),
            sessionBean.getUser().getLastBranch().getCurrency().getId(),
            shiftPayment.getFinancingDocument().getDescription(),
            accountList,
            shiftPayment.getCredit().getAccount().getId(),
            sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList) {
        int attendantId = 0;

        if (shiftPayment.getFinancingDocument().getFinancingType().getId() == 49 || shiftPayment.getFinancingDocument().getFinancingType().getId() == 50) {
            if (shiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0) {
                attendantId = shiftPayment.getAttendantAccount().getId();

            } else {

                attendantId = shiftPayment.getAccount().getId();
            }

        } else {
            attendantId = shiftPayment.getAccount().getId();

        }

        String sql = "SELECT r_result_id FROM automation.process_shiftpayment(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] param = new Object[]{processType,
            shiftPayment.getFinancingDocument().getId(),
            shiftPayment.getShift().getId(),
            shiftPayment.getFinancingDocument().getAccount().getId() == 0 ? null : shiftPayment.getFinancingDocument().getAccount().getId(),
            attendantId == 0 ? null : attendantId,
            shiftPayment.getFinancingDocument().getFinancingType().getId(),
            shiftPayment.getBankAccount().getId() == 0 ? null : shiftPayment.getBankAccount().getId(),
            shiftPayment.getSafe().getId() == 0 ? null : shiftPayment.getSafe().getId(),
            shiftPayment.getFinancingDocument().getIncomeExpense().getId() == 0 ? null : shiftPayment.getFinancingDocument().getIncomeExpense().getId(),
            shiftPayment.getFinancingDocument().getDocumentNumber(),
            shiftPayment.getFinancingDocument().getDocumentDate(),
            shiftPayment.getFinancingDocument().getPrice(),
            sessionBean.getUser().getLastBranch().getCurrency().getId(),
            shiftPayment.getFinancingDocument().getDescription(),
            accountList,
            shiftPayment.getCredit().getAccount().getId(),
            sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(ShiftPayment shiftPayment) {
        String sql = "SELECT r_payment_id FROM finance.delete_payment_financingdocument (?, ?, ?);";

        Object[] param = new Object[]{5, shiftPayment.getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String findSalesAccordingToStockForExcel(FuelShift fuelShift) {

        String sql = "SELECT \n"
                  + "	 stcki.stock_id AS stock_id,\n"
                  + "    stck.name AS stckname,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    COALESCE(SUM(ssl.liter),0) AS sslliter,\n"
                  + "    COALESCE(SUM(ssl.totalmoney),0) AS ssltotalmoney,\n"
                  + "    COALESCE(sstck.beginquantity,0) AS previousamount,\n"
                  + "    COALESCE(sstck.endquantity,0) AS remainingamount,\n"
                  + "    stck.unit_id AS stckunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting\n"
                  + "FROM automation.shiftsale ssl \n"
                  + "	 INNER JOIN inventory.stockinfo stcki ON(stcki.fuelintegrationcode = ssl.stockcode AND stcki.deleted=FALSE AND stcki.branch_id =" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = stcki.stock_id)\n"
                  + "    LEFT JOIN automation.shiftstock sstck ON(sstck.stock_id = stcki.stock_id AND sstck.deleted =FALSE AND sstck.shift_id = " + fuelShift.getId() + ")\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "WHERE ssl.deleted = FALSE \n"
                  + "	AND ssl.shift_id =" + fuelShift.getId() + "\n"
                  + "GROUP BY\n"
                  + "	 stcki.stock_id,\n"
                  + "    stck.name,\n"
                  + "    stck.barcode,\n"
                  + "    sstck.beginquantity,\n"
                  + "    sstck.endquantity,\n"
                  + "    stck.unit_id,\n"
                  + "    gunt.sortname,\n"
                  + "    gunt.unitrounding";

        return sql;
    }

    @Override
    public String shiftPaymentCashDetail(FuelShift fuelShift) {
        String sql = "SELECT \n"
                  + "	 SUM(shp.saleprice) AS price,\n"
                  + "    shp.currency_id AS currency,\n"
                  + "    shp.exchangerate AS exchangerate,\n"
                  + "    sf.name AS sfname,\n"
                  + "    sf.code AS sfcode\n"
                  + "FROM automation.shiftpayment shp \n"
                  + "   INNER JOIN finance.safe sf ON(sf.id = shp.safe_id)\n"
                  + "WHERE shp.deleted=False AND shp.is_reversemove = FALSE AND shp.shift_id = " + fuelShift.getId() + " AND COALESCE(shp.safe_id,0) >0\n"
                  + "GROUP BY "
                  + "   shp.currency_id,\n"
                  + "   shp.exchangerate,\n"
                  + "   sf.name,\n"
                  + "   sf.code";

        return sql;
    }

    @Override
    public String shiftPaymentCredit(FuelShift fuelShift) {

        String sql = "SELECT \n"
                  + "	 acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    acc.code AS acccode,\n"
                  + "    COALESCE(SUM(crdt.money),0) AS ssltotalmoney,\n"
                  + "    COUNT(ssl.id) AS salecount,\n"
                  + "    acc.id AS accid\n"
                  + "FROM\n"
                  + "automation.shiftsale ssl\n"
                  + "     INNER JOIN finance.credit crdt ON(crdt.id = ssl.credit_id AND crdt.deleted = FALSE)\n"
                  + "     INNER JOIN general.account acc ON(acc.id = crdt.account_id)\n"
                  + "WHERE \n"
                  + "    ssl.deleted =FALSE \n"
                  + "    AND ssl.shift_id = " + fuelShift.getId() + "\n"
                  + "	 AND COALESCE(ssl.credit_id,0)>0\n"
                  + "GROUP BY\n"
                  + "	  acc.name,\n"
                  + "     acc.title,\n"
                  + "     acc.is_employee,\n"
                  + "     acc.code,\n"
                  + "     acc.id";

        return sql;
    }

    @Override
    public String shiftPaymentCreditCardDetail(FuelShift fuelShift) {
        String sql = "SELECT \n"
                  + "   SUM(shp.saleprice) AS price,\n"
                  + "   shp.currency_id AS currency,\n"
                  + "   shp.exchangerate AS exchangerate,\n"
                  + "   bka.name AS bkaname\n"
                  + "FROM automation.shiftpayment shp \n"
                  + "  INNER JOIN finance.bankaccount bka ON(bka.id = shp.bankaccount_id)\n"
                  + "WHERE shp.deleted=False AND shp.is_reversemove = FALSE AND shp.shift_id = " + fuelShift.getId() + " AND COALESCE(shp.bankaccount_id,0) >0\n"
                  + "GROUP BY \n"
                  + "  shp.currency_id,\n"
                  + "  shp.exchangerate,\n"
                  + "  bka.name";

        return sql;
    }

    @Override
    public String shiftPaymentDeficitExcess(FuelShift fuelShift) {
        String sql = "SELECT \n"
                  + "    COALESCE(SUM(CASE WHEN fdoc.type_id=49 THEN shp.saleprice ELSE 0 END),0) AS employeedebt,\n"
                  + "    COALESCE(SUM(CASE WHEN fdoc.type_id=50 THEN shp.saleprice ELSE 0 END),0) AS givenemployee,\n"
                  + "    fdoc.documentnumber AS fdocdocumnetnumber,\n"
                  + "    acc.name AS accname,\n"
                  + "    acc.title AS acctitle,\n"
                  + "    acc.is_employee AS accis_employee,\n"
                  + "    acc.code AS acccode,\n"
                  + "    fdoc.description AS fdocdescription,\n"
                  + "    fdoc.type_id AS fdoctype_id,\n"
                  + "    fdoc.id \n"
                  + "FROM\n"
                  + "	automation.shiftpayment shp\n"
                  + "   INNER JOIN finance.financingdocument fdoc ON(fdoc.id = shp.financingdocument_id AND fdoc.deleted = FALSE)\n"
                  + "   LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = shp.financingdocument_id AND fiem.deleted = FALSE)\n"
                  + "   INNER JOIN general.accountmovement accm ON(accm.financingdocument_id = fdoc.id AND accm.deleted = FALSE)\n"
                  + "   INNER JOIN general.account acc ON(acc.id = accm.account_id)\n"
                  + "WHERE shp.deleted=FALSE\n"
                  + "   AND shp.is_reversemove = FALSE\n "
                  + "	AND shp.shift_id = " + fuelShift.getId() + "\n"
                  + "    AND fiem.id IS NULL AND fdoc.type_id IN (49,50)\n"
                  + "GROUP BY \n"
                  + "	fdoc.documentnumber,\n"
                  + "    acc.name,\n"
                  + "    acc.title,\n"
                  + "    acc.is_employee,\n"
                  + "    acc.code,\n"
                  + "    fdoc.description,\n"
                  + "    fdoc.type_id,\n"
                  + "    fdoc.id";
        return sql;
    }

    @Override
    public String shiftGeneralTotal(FuelShift fuelShift, BranchSetting branchSetting) {

        String column = "";
        if (branchSetting.getAutomationId() == 1) {
            column = "	 COALESCE(SUM(CASE WHEN fst.typeno = 18 THEN shp.saleprice ELSE 0 END),0) AS creditamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno IN (1, 2, 11, 12, 21, 22, 35, 41, 51, 55, 56, 57, 60, 61, 71, 81, 91, 101, 102, 103, 104, 106) THEN shp.saleprice ELSE 0 END),0) AS automationsale,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 5 THEN shp.saleprice ELSE 0 END),0) AS testamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 0 THEN shp.saleprice ELSE 0 END),0) AS cashamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 8 THEN shp.saleprice ELSE 0 END),0) AS creditcardamount,\n";
        } else if (branchSetting.getAutomationId() == 2) {
            column = "	 0 AS creditamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno IN (2, 3, 4, 7, 8, 9, 16) THEN shp.saleprice ELSE 0 END),0) AS automationsale,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 6 THEN shp.saleprice ELSE 0 END),0) AS testamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.safe_id>0 THEN shp.saleprice ELSE 0 END),0) AS cashamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.bankaccount_id>0 THEN shp.saleprice ELSE 0 END),0) AS creditcardamount,\n";
        } else if (branchSetting.getAutomationId() == 4) {
            column = "	 0 AS creditamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno IN (1, 2) THEN shp.saleprice ELSE 0 END),0) AS automationsale,\n"
                      + "    0 AS testamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 0 AND shp.safe_id>0 THEN shp.saleprice ELSE 0 END),0) AS cashamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 0 AND shp.bankaccount_id>0 THEN shp.saleprice ELSE 0 END),0) AS creditcardamount,\n";
        } else if (branchSetting.getAutomationId() == 3) {
            column = "	 0 AS creditamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno IN (2) THEN shp.saleprice ELSE 0 END),0) AS automationsale,\n"
                      + "    0 AS testamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.safe_id>0 THEN shp.saleprice ELSE 0 END),0) AS cashamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.bankaccount_id>0 THEN shp.saleprice ELSE 0 END),0) AS creditcardamount,\n";
        }else if (branchSetting.getAutomationId() == 5) {
            column = "	 0 AS creditamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno IN (2, 3) THEN shp.saleprice ELSE 0 END),0) AS automationsale,\n"
                      + "    0 AS testamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.safe_id>0 THEN shp.saleprice ELSE 0 END),0) AS cashamount,\n"
                      + "    COALESCE(SUM(CASE WHEN fst.typeno = 1 AND shp.bankaccount_id>0 THEN shp.saleprice ELSE 0 END),0) AS creditcardamount,\n";
        }

        String sql = "SELECT "
                  + "   SUM(sub.creditamount) AS creditamount,  \n"
                  + "   SUM(sub.automationsale) AS automationsale,  \n"
                  + "   SUM(sub.testamount) AS testamount,    \n"
                  + "   SUM(sub.cashamount) AS cashamount,  \n"
                  + "   SUM(sub.incomeamount) AS incomeamount,    \n"
                  + "   SUM(sub.expenseamount) AS expenseamount,    \n"
                  + "   SUM(sub.employeedebt) AS employeedebt,    \n"
                  + "   SUM(sub.givenemployee) AS givenemployee,\n "
                  + "   SUM(sub.accountcollection) AS accountcollection,    \n"
                  + "   SUM(sub.accountpayment) AS accountpayment,"
                  + "   SUM(sub.creditcardamount) AS creditcardamount,\n"
                  + "   SUM(sub.givenemployee) + SUM(sub.incomeamount) + SUM(sub.accountcollection) AS entrysubtotal,"
                  + "   SUM(sub.creditamount) + SUM(sub.automationsale) + SUM(sub.testamount) + SUM(sub.cashamount) + SUM(sub.creditcardamount) + SUM(sub.expenseamount) + SUM(sub.employeedebt) + SUM(sub.accountpayment) AS exitsubtotal\n"
                  + "FROM\n"
                  + "(SELECT \n"
                  + column + "\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NOT NULL AND fiem.is_direction = TRUE THEN shp.saleprice ELSE 0 END),0) AS incomeamount,\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NOT NULL AND fiem.is_direction = FALSE THEN shp.saleprice ELSE 0 END),0) AS expenseamount,\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NULL AND fdoc.type_id=49 AND acc.is_employee = TRUE THEN shp.saleprice ELSE 0 END),0) AS employeedebt,\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NULL AND fdoc.type_id=50 AND acc.is_employee = TRUE THEN shp.saleprice ELSE 0 END),0) AS givenemployee,\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NULL AND fdoc.type_id=49 AND acc.is_employee = FALSE THEN shp.saleprice ELSE 0 END),0) AS accountcollection,\n"
                  + "    COALESCE(SUM(CASE WHEN fiem.id IS NULL AND fdoc.type_id=50 AND acc.is_employee = FALSE THEN shp.saleprice ELSE 0 END),0) AS accountpayment\n"
                  + "FROM\n"
                  + "	automation.shiftpayment shp\n"
                  + "   LEFT JOIN automation.fuelsaletype fst ON(fst.id = shp.fuelsaletype_id AND fst.deleted =FALSE AND fst.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                  + "   LEFT JOIN finance.financingdocument fdoc ON(fdoc.id = shp.financingdocument_id AND fdoc.deleted = FALSE)\n"
                  + "   LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = shp.financingdocument_id AND fiem.deleted = FALSE)\n"
                  + "   LEFT JOIN general.accountmovement accm ON(accm.financingdocument_id = fdoc.id AND accm.deleted =FALSE)\n"
                  + "   LEFT JOIN general.account acc ON(acc.id = accm.account_id AND acc.deleted =FALSE)\n"
                  + "WHERE shp.deleted=FALSE\n"
                  + "AND shp.is_reversemove = FALSE\n"
                  + "	AND shp.shift_id = " + fuelShift.getId() + ") sub";
        return sql;
    }

    @Override
    public List<FuelShiftPreview> findSalesAccordingToStockForPreview(FuelShift fuelShift) {
        String sql = findSalesAccordingToStockForExcel(fuelShift);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;

    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCashDetailForPreview(FuelShift fuelShift) {
        String sql = shiftPaymentCashDetail(fuelShift);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCreditCardDetailForPreview(FuelShift fuelShift) {
        String sql = shiftPaymentCreditCardDetail(fuelShift);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentCreditForPreview(FuelShift fuelShift) {
        String sql = shiftPaymentCredit(fuelShift);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;
    }

    @Override
    public List<FuelShiftPreview> shiftPaymentDeficitExcessForPreview(FuelShift fuelShift) {
        String sql = shiftPaymentDeficitExcess(fuelShift);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;
    }

    @Override
    public List<FuelShiftPreview> shiftGeneralTotalForPreview(FuelShift fuelShift, BranchSetting branchSetting) {
        String sql = shiftGeneralTotal(fuelShift, branchSetting);

        List<FuelShiftPreview> result = getJdbcTemplate().query(sql, new FuelShiftPreviewMapper());
        return result;
    }

    @Override
    public List<FuelShift> nonTransferableShift() {

        String sql = " SELECT\n"
                  + "   shferrs.id AS shferrsid,\n"
                  + "   shferrs.errordata AS shferrordata,\n"
                  + "   shferrs.shiftno AS shferrsshiftno\n"
                  + "FROM \n"
                  + "	log.shifterrorsale shferrs\n"
                  + "WHERE \n"
                  + "	shferrs.branch_id = ? \n"
                  + "   AND shferrs.deleted=FALSE\n"
                  + "ORDER BY shferrs.id;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<FuelShift> result = getJdbcTemplate().query(sql, param, new FuelShiftMapper());

        return result;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public int reSendErrorShift() {
        String sql = "SELECT r_result_id FROM automation.control_shift(?);";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<FuelShiftControlFile> controlShiftNo(String shiftList) {
        String sql = " SELECT r_shiftno, r_filename FROM automation.control_shiftno(?,?);";

        Object[] param = {sessionBean.getUser().getLastBranch().getId(), shiftList};
        List<FuelShiftControlFile> result = getJdbcTemplate().query(sql, param, new FuelShiftControlFileMapper());
        return result;
    }

    @Override
    public int controlVehicleAccountCon(FuelShiftSales fuelShiftSales) {
        String sql = " SELECT\n"
                  + "  CASE WHEN EXISTS(SELECT vhc.plate FROM general.vehicle vhc WHERE vhc.deleted=FALSE AND vhc.plate = ?) THEN 1 ELSE 0 END;";

        Object[] param = new Object[]{fuelShiftSales.getPlate()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
