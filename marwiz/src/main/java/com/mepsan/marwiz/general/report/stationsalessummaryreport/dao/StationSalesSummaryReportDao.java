/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:28:33 PM
 */
package com.mepsan.marwiz.general.report.stationsalessummaryreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import static java.lang.String.format;
import static java.lang.String.format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StationSalesSummaryReportDao extends JdbcDaoSupport implements IStationSaleSummaryReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StationSalesSummaryReport> findFuelSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";
        String whereStockİnfoBranch = "";
        if (!selectedBranchList.isEmpty()) {
            whereShiftBranch += "AND shf.branch_id " + createWhere;
            whereStockİnfoBranch += "AND stcki.branch_id " + createWhere;
        }

        String sql = "SELECT\n"
                  + " brn.id AS brnid,\n"
                  + " brn.name AS brnname,\n"
                  + " brn.currency_id AS brncurrency_id,\n"
                  + " stck.name as stckname, \n"
                  + " COALESCE(stck.unit_id,0) as stckunitid ,\n"
                  + " unt.sortname as untsortname , \n"
                  + " COALESCE(SUM(COALESCE(sfs.totalmoney,1))/(SUM(CASE WHEN sfs.liter=0 THEN 1 ELSE sfs.liter END)),0) as unitprice,\n"
                  + " SUM(COALESCE(sfs.liter,0)) as sfsliter,\n"
                  + " SUM(COALESCE(sfs.totalmoney,0)) as sfstotalmoney\n"
                  + "FROM automation.shiftsale sfs\n"
                  + "INNER JOIN automation.shift shf ON(shf.id =sfs.shift_id  " + whereShiftBranch + " AND shf.deleted=FALSE)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=shf.branch_id AND brn.deleted=FALSE)\n"
                  + "INNER JOIN system.currency crn ON(crn.id=brn.currency_id AND crn.deleted=FALSE)\n"
                  + "LEFT JOIN inventory.stock stck ON(stck.id=sfs.stock_id AND stck.deleted=FALSE) \n"
                  + "LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id = stck.id AND stcki.deleted=FALSE  " + whereStockİnfoBranch + ")\n"
                  + "LEFT JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                  + "WHERE sfs.deleted=FALSE  AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'\n"
                  + "GROUP BY brn.id, brn.name,stck.name,stck.unit_id,unt.sortname \n"
                  + "ORDER BY brn.id \n";

        Object[] param = new Object[]{};

        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());

    }

    @Override
    public List<StationSalesSummaryReport> findFuelCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";
        String whereFuelSaleBranch = "";
        String whereFinancingDocumentBranch = "";
        String whereIncomeExpenseMovementBranch = "";
        String whereAccountCollectionPayment = "";
        if (!selectedBranchList.isEmpty()) {
            whereFuelSaleBranch += "AND fstyp.branch_id " + createWhere;
            whereShiftBranch += "AND shf.branch_id " + createWhere;
            whereFinancingDocumentBranch += " AND (fdoc.branch_id " + createWhere + "  OR fdoc.transferbranch_id " + createWhere + " )";
            whereIncomeExpenseMovementBranch += " AND  fiem.branch_id " + createWhere;
            whereAccountCollectionPayment += " AND accm.branch_id " + createWhere;
        }

        String sql = "   SELECT 		\n"
                  + "    brn.id AS brnid,\n"
                  + "    brn.name AS brnname,\n"
                  + "    brn.currency_id AS brncurrency_id,\n"
                  + "    COALESCE(shp.fuelsaletype_id,0) as saletype_id,               \n"
                  + "    COALESCE(fstyp.name, '') as fstypename,\n"
                  + "    SUM(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1)) as collectionstotal,\n"
                  + "    SUM( CASE WHEN fiem.id IS NULL  THEN (CASE WHEN fdoc.type_id = 50 THEN 1 WHEN fdoc.type_id = 49  THEN -1 ELSE 0 END) * COALESCE(fdoc.price,0)* COALESCE(fiem.exchangerate,1) ELSE 0 END)  AS acikfazla,\n"
                  + "    SUM( CASE WHEN fiem.id IS NULL AND acc.is_employee = FALSE THEN (CASE WHEN fdoc.type_id = 50 THEN -1 WHEN fdoc.type_id = 49  THEN 1 ELSE 0 END) * COALESCE(fdoc.price,0)* COALESCE(fiem.exchangerate,1) ELSE 0 END)  AS caritahsilatödeme,\n"
                  + "    SUM((CASE WHEN fdoc.type_id = 50 THEN 1 WHEN fdoc.type_id = 49  THEN -1 ELSE 0 END)* COALESCE(fiem.price,0) * COALESCE(fiem.exchangerate,1)) AS gelirgider \n"
                  + "FROM\n"
                  + "   automation.shiftpayment shp\n"
                  + "  LEFT JOIN automation.fuelsaletype fstyp ON(shp.fuelsaletype_id=fstyp.id AND fstyp.deleted=FALSE  " + whereFuelSaleBranch + ")\n"
                  + "  LEFT JOIN automation.shift shf ON(shf.id=shp.shift_id AND shf.deleted=FALSE  " + whereShiftBranch + ")\n"
                  + "  INNER JOIN general.branch brn ON (brn.id=shf.branch_id AND brn.deleted=FALSE )\n"
                  + "  INNER JOIN system.currency crn ON(crn.id=brn.currency_id AND crn.deleted=FALSE)\n"
                  + "  LEFT JOIN finance.financingdocument fdoc ON(fdoc.id=shp.financingdocument_id AND fdoc.deleted=FALSE AND (fdoc.type_id= 49 OR fdoc.type_id=50) " + whereFinancingDocumentBranch + ")    \n"
                  + "  LEFT JOIN general.accountmovement accm ON(fdoc.id = accm.financingdocument_id AND accm.deleted = FALSE " + whereAccountCollectionPayment + ")\n"
                  + "  LEFT JOIN general.account acc ON (acc.id = accm.account_id AND acc.deleted = FALSE)\n"
                  + "  LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=FALSE " + whereIncomeExpenseMovementBranch + ")  \n"
                  + "WHERE \n"
                  + "	shp.deleted=FALSE AND shp.is_reversemove = FALSE AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'\n"
                  + "GROUP BY brn.id, brn.name, shp.fuelsaletype_id,fstyp.name\n"
                  + "ORDER BY brn.id \n";

        Object[] param = new Object[]{};

        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());
    }

    @Override
    public List<StationSalesSummaryReport> findMarketSales(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";
        String whereSaleBranch = "";
        if (!selectedBranchList.isEmpty()) {

            whereShiftBranch += "AND shf.branch_id " + createWhere;
            whereSaleBranch += "AND sl.branch_id " + createWhere;
        }

        String sql = "         \n"
                  + "          SELECT \n"
                  + "             tt.slcurrency_id as slcurrency_id,\n"
                  + "             tt.brnid as brnid,\n"
                  + "             tt.brnname as brnname,\n"
                  + "            COALESCE(SUM(tt.salequantity),0) as salequantity,\n"
                  + "             COALESCE(SUM(tt.saletotal),0) as saletotal\n"
                  + "          FROM( SELECT\n"
                  + "                brn.id AS brnid,\n"
                  + "                brn.name AS brnname,\n"
                  + "                sl.currency_id AS slcurrency_id,\n"
                  + "                sl.id,\n"
                  + "                (SELECT SUM(COALESCE(sli.quantity,0))\n"
                  + "                  from general.saleitem sli \n"
                  + "                  WHERE sli.sale_id=sl.id AND sli.deleted=FALSE\n"
                  + "                ) as salequantity,\n"
                  + "                SUM(COALESCE(sl.totalmoney,0)) as saletotal\n"
                  + "             FROM general.sale sl   \n"
                  + "             LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False)\n"
                  + "             INNER JOIN general.shift shf ON(shf.id=sl.shift_id  " + whereShiftBranch + " AND shf.deleted=FALSE)\n"
                  + "             INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "             WHERE sl.deleted=FALSE AND NOT EXISTS(SELECT sl2.id FROM general.sale sl2 WHERE sl2.deleted=FALSE AND sl2.differentsale_id = sl.id)\n"
                  + "             AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'  " + whereSaleBranch + "\n"
                  + "             AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND sl.is_return=FALSE \n"
                  + "             GROUP BY brn.id,brn.name,sl.id) tt\n"
                  + "             GROUP BY tt.brnid,tt.brnname, tt.slcurrency_id \n"
                  + "             ORDER BY tt.brnid \n";
        
        System.out.println("---İstasyon satış özet raporu---"+sql);

        Object[] param = new Object[]{};

        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());
    }

    @Override
    public List<StationSalesSummaryReport> findMarketCollections(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";

        if (!selectedBranchList.isEmpty()) {

            whereShiftBranch += "AND shf.branch_id " + createWhere;

        }

        String sql = "SELECT \n"
                  + "  brn.id AS brnid,\n"
                  + "  brn.name AS brnname,\n"
                  + "  brn.currency_id AS brncurrency_id,\n"
                  + "  shp.saletype_id as typeid,\n"
                  + "  typd.name as typdname,\n"
                  + "  SUM(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1))as marketsalecollections,\n"
                  + "  SUM((COALESCE(shp.actualprice,0)*COALESCE(shp.exchangerate,1))-(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1))) as marketacikfazla\n"
                  + "FROM general.shiftpayment shp\n"
                  + " INNER JOIN general.shift shf ON(shf.id=shp.shift_id  " + whereShiftBranch + " AND shf.deleted=FALSE)\n"
                  + " INNER JOIN general.branch brn ON(brn.id=shf.branch_id AND brn.deleted=FALSE)\n"
                  + " INNER JOIN system.currency crn ON(crn.id=brn.currency_id AND crn.deleted=FALSE)\n"
                  + " INNER JOIN system.type_dict typd ON(typd.type_id=shp.saletype_id AND typd.language_id=?)\n"
                  + "WHERE shp.deleted=FALSE AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'\n"
                  + "GROUP BY brn.id, brn.name,shp.saletype_id,typd.name \n"
                  + "ORDER BY brn.id \n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};

        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<StationSalesSummaryReport> findFuelSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";
        String whereIncomeExpenseMovementBranch = "";
        String whereAccountMovementBranch = "";
        if (!selectedBranchList.isEmpty()) {
            whereShiftBranch += "AND shf.branch_id " + createWhere;
            whereIncomeExpenseMovementBranch += " AND  fiem.branch_id " + createWhere;
            whereAccountMovementBranch += " AND accm.branch_id " + createWhere;
        }

        String sql = "  SELECT 	\n"
                  + "            brn.id AS brnid,\n"
                  + "            brn.name AS brnname,\n"
                  + "            brn.currency_id AS brncurrency_id,\n"
                  + "            shp.currency_id AS shpcurrency_id,\n"
                  + "            SUM(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1)) as collectionstotal,\n"
                  + "            SUM( CASE WHEN fiem.id IS NULL AND acc.is_employee = TRUE THEN (CASE WHEN fdoc.type_id = 50 THEN 1 WHEN fdoc.type_id = 49  THEN -1 ELSE 0 END) * COALESCE(fdoc.price,0)* COALESCE(fiem.exchangerate,1) ELSE 0 END)  AS acikfazla,\n"
                  + "            SUM( CASE WHEN fiem.id IS NULL AND acc.is_employee = FALSE  THEN (CASE WHEN fdoc.type_id = 50 THEN -1 WHEN fdoc.type_id = 49  THEN 1 ELSE 0 END) * COALESCE(fdoc.price,0)* COALESCE(fiem.exchangerate,1) ELSE 0 END)  AS caritahsilatödeme,\n"
                  + "            SUM((CASE WHEN fdoc.type_id = 50 THEN 1 WHEN fdoc.type_id = 49  THEN -1 ELSE 0 END)* COALESCE(fiem.price,0) * COALESCE(fiem.exchangerate,1)) AS gelirgider \n"
                  + "        FROM\n"
                  + "           automation.shiftpayment shp\n"
                  + "           LEFT JOIN automation.shift shf ON(shf.id=shp.shift_id AND shf.deleted=FALSE  " + whereShiftBranch + ")\n"
                  + "           INNER JOIN general.branch brn ON (brn.id=shf.branch_id AND brn.deleted=FALSE )\n"
                  + "           INNER JOIN system.currency crn ON (crn.id=brn.currency_id AND crn.deleted=FALSE)\n"
                  + "           LEFT JOIN finance.financingdocument fdoc ON(fdoc.id=shp.financingdocument_id AND fdoc.deleted=FALSE AND (fdoc.type_id= 49 OR fdoc.type_id=50))    \n"
                  + "           LEFT JOIN general.accountmovement accm ON (accm.financingdocument_id = fdoc.id AND accm.deleted = FALSE " + whereAccountMovementBranch + ")\n"
                  + "           LEFT JOIN general.account acc ON (acc.id = accm.account_id AND acc.deleted = FALSE)\n "
                  + "           LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=FALSE " + whereIncomeExpenseMovementBranch + ")  \n"
                  + "        WHERE \n"
                  + "        	shp.deleted=FALSE AND shp.is_reversemove = FALSE AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'\n"
                  + "        GROUP BY brn.id, brn.name,shp.currency_id\n";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());
    }

    @Override
    public List<StationSalesSummaryReport> findMarketSalesOutherMoney(Date beginDate, Date endDate, String createWhere, List<BranchSetting> selectedBranchList) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereShiftBranch = "";

        if (!selectedBranchList.isEmpty()) {
            whereShiftBranch += "AND shf.branch_id " + createWhere;
        }

        String sql = " SELECT \n"
                  + "  brn.id AS brnid,\n"
                  + "  brn.name AS brnname, \n "
                  + "  shp.currency_id AS shpcurrency_id ,\n"
                  + "  brn.currency_id AS brncurrency_id,\n"
                  + "  SUM(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1))as marketsalecollections, \n"
                  + "  SUM((COALESCE(shp.actualprice,0)*COALESCE(shp.exchangerate,1))-(COALESCE(shp.saleprice,0)*COALESCE(shp.exchangerate,1))) as marketacikfazla \n"
                  + "FROM general.shiftpayment shp \n"
                  + "  INNER JOIN general.shift shf ON(shf.id=shp.shift_id  " + whereShiftBranch + " AND shf.deleted=FALSE)\n"
                  + "  INNER JOIN general.branch brn ON(brn.id=shf.branch_id AND brn.deleted=FALSE) \n"
                  + "WHERE shp.deleted=FALSE  AND shf.begindate >= '" + format.format(beginDate) + "' AND shf.enddate <='" + format.format(endDate) + "'\n"
                  + "GROUP BY brn.id, brn.name,shp.currency_id \n";

        Object[] param = new Object[]{};

        return getJdbcTemplate().query(sql, param, new StationSalesSummaryReportMapper());
    }

}
