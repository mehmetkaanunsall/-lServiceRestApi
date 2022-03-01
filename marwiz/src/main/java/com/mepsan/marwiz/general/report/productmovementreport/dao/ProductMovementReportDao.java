/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   08.03.2018 02:01:44
 */
package com.mepsan.marwiz.general.report.productmovementreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ProductMovementReportDao extends JdbcDaoSupport implements IProductMovementReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ProductMovementReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductMovementReport obj) {

        String whereStockInfo = "";
        String branchList = "";
        int countIntegration = 0;
        int countNotIntegration = 0;
        for (BranchSetting branchsetting : obj.getListOfBranch()) {
            branchList = branchList + "," + String.valueOf(branchsetting.getBranch().getId());
            if (branchsetting.isIsCentralIntegration()) {
                countIntegration++;
            } else {
                countNotIntegration++;
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            whereStockInfo = whereStockInfo + " AND si.branch_id IN(" + branchList + ") ";
        }

        if (obj.getListOfBranch().size() == countIntegration) {//Hepsinin merkezi entegrasyonu vardır
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchList + ") AND si1.stock_id=stck.id \n"
                      + "AND  si1.is_valid  =TRUE)\n";
        } else if (obj.getListOfBranch().size() == countNotIntegration) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        } else {
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                      + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchList + ") AND si1.stock_id=stck.id \n"
                      + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) \n";
        }

        String sql = "SELECT \n"
                  + "stck.id as stckid, \n"
                  + "stck.name as stckname, \n"
                  + "stck.code as stckcode, \n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.barcode as stckbarcode, \n"
                  + "stck.unit_id AS stckunit_id ,\n"
                  + "gunt.sortname AS guntsortname, \n"
                  + "gunt.unitrounding AS guntunitsorting, \n"
                  + "COALESCE(t.unitprice,0) AS unitprice,\n"
                  + "COALESCE(t.quantity,0) AS quantity, \n"
                  + "COALESCE(t.totalmoney,0) AS totalmoney,\n"
                  + "COALESCE(t.currency_id,0) AS currency_id,\n"
                  + "(SELECT general.find_category(stck.id, 1, ?)) AS category,\n"
                  + "stck.brand_id AS stckbrand_id,\n"
                  + "br.name AS brname,\n"
                  + "stck.supplier_id AS stcksupplier_id,\n"
                  + "acc.name AS accname, \n"
                  + "stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "cspp.name AS csppname,\n"
                  + "si.branch_id AS branch_id,\n"
                  + "brc.name AS brcname\n"
                  + "FROM inventory.stock stck \n"
                  + "INNER JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE " + whereStockInfo + ")\n"
                  + "LEFT JOIN\n"
                  + "(\n"
                  + "      SELECT \n"
                  + "      sli.stock_id,\n"
                  + "      sli.unitprice AS unitprice,\n"
                  + "      sli.currency_id AS currency_id,\n"
                  + "      sl.branch_id AS branch_id,\n"
                  + "      COALESCE(SUM(sli.quantity),0) AS quantity,"
                  + "      COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))),0) AS totalmoney\n"
                  + "      FROM general.saleitem sli\n"
                  + "      LEFT JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE ) \n"
                  + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=FALSE ) \n"
                  + "      WHERE sli.deleted=FALSE AND sl.processdate BETWEEN ? AND ?\n"
                  + "      AND sl.is_return=FALSE AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN ? AND ? THEN FALSE ELSE TRUE END)\n"
                  + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "      GROUP BY sli.stock_id, sli.unitprice, sli.currency_id, sl.branch_id\n"
                  + "      \n"
                  + ") t ON (stck.id=t.stock_id AND t.branch_id=si.branch_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id) \n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = si.branch_id AND brc.deleted =FALSE)\n"
                  + "WHERE stck.deleted=FALSE \n"
                  + where + "\n"
                  + "ORDER BY COALESCE((t.quantity),0) DESC"
                  + " LIMIT " + pageSize + " OFFSET " + first;
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getBeginDate(), obj.getEndDate(), obj.getBeginDate(), obj.getEndDate()};
        List<ProductMovementReport> result = getJdbcTemplate().query(sql, param, new ProductMovementReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, ProductMovementReport obj) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereStockInfo = "";
        String branchList = "";
        int countIntegration = 0;
        int countNotIntegration = 0;
        for (BranchSetting branchsetting : obj.getListOfBranch()) {
            branchList = branchList + "," + String.valueOf(branchsetting.getBranch().getId());
            if (branchsetting.isIsCentralIntegration()) {
                countIntegration++;
            } else {
                countNotIntegration++;
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            whereStockInfo = whereStockInfo + " AND si.branch_id IN(" + branchList + ") ";
        }

        if (obj.getListOfBranch().size() == countIntegration) {//Hepsinin merkezi entegrasyonu vardır
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "where si1.deleted=FALSE AND si1.branch_id =si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND  si1.is_valid  =TRUE)\n";
        } else if (obj.getListOfBranch().size() == countNotIntegration) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        } else {
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                      + "where si1.deleted=FALSE AND si1.branch_id  = si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) \n";
        }

        String sql = "SELECT \n"
                  + "stck.id as stckid, \n"
                  + "stck.name as stckname, \n"
                  + "stck.code as stckcode, \n"
                  + "stck.centerproductcode AS stckcenterproductcode,\n"
                  + "stck.barcode as stckbarcode, \n"
                  + "stck.unit_id AS stckunit_id ,\n"
                  + "gunt.sortname AS guntsortname, \n"
                  + "gunt.unitrounding AS guntunitsorting, \n"
                  + "COALESCE(t.unitprice,0) AS unitprice,\n"
                  + "COALESCE(t.quantity,0) AS quantity, \n"
                  + "COALESCE(t.totalmoney,0) AS totalmoney,\n"
                  + "COALESCE(t.currency_id,0) AS currency_id,\n"
                  + "si.branch_id AS branch_id,\n"
                  + "(SELECT general.find_category(stck.id, 1, " + sessionBean.getUser().getLastBranch().getId() + ")) AS category,\n"
                  + "stck.brand_id AS stckbrand_id,\n"
                  + "br.name AS brname,\n"
                  + "stck.supplier_id AS stcksupplier_id,\n"
                  + "acc.name AS accname, \n"
                  + "stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "cspp.name AS csppname,\n"
                  + "si.branch_id AS branch_id,\n"
                  + "brc.name AS brcname\n"
                  + "FROM inventory.stock stck \n"
                  + "INNER JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE " + whereStockInfo + ")\n"
                  + "LEFT JOIN\n"
                  + "(\n"
                  + "      SELECT \n"
                  + "      sli.stock_id,\n"
                  + "      sli.unitprice AS unitprice,\n"
                  + "      sli.currency_id AS currency_id,\n"
                  + "      sl.branch_id AS branch_id,\n"
                  + "      COALESCE(SUM(sli.quantity),0) AS quantity,\n"
                  + "      COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))),0) AS totalmoney\n"
                  + "      FROM general.saleitem sli\n"
                  + "      LEFT JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE ) \n"
                  + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=FALSE ) \n"
                  + "      WHERE sli.deleted=FALSE AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "'\n"
                  + "      AND sl.is_return=FALSE AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                  + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "      GROUP BY sli.stock_id, sli.unitprice, sli.currency_id, sl.branch_id\n"
                  + "      \n"
                  + ") t ON (stck.id=t.stock_id AND t.branch_id=si.branch_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id) \n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = si.branch_id AND brc.deleted =FALSE)\n"
                  + "WHERE stck.deleted=FALSE \n"
                  + where + "\n"
                  + "ORDER BY COALESCE((t.quantity),0) DESC";

        return sql;

    }

    @Override

    public int count(String where, ProductMovementReport obj) {

        String whereStockInfo = "";
        String branchList = "";
        int countIntegration = 0;
        int countNotIntegration = 0;
        for (BranchSetting branchsetting : obj.getListOfBranch()) {
            branchList = branchList + "," + String.valueOf(branchsetting.getBranch().getId());
            if (branchsetting.isIsCentralIntegration()) {
                countIntegration++;
            } else {
                countNotIntegration++;
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            whereStockInfo = whereStockInfo + " AND si.branch_id IN(" + branchList + ") ";
        }

        if (obj.getListOfBranch().size() == countIntegration) {//Hepsinin merkezi entegrasyonu vardır
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "where si1.deleted=FALSE AND si1.branch_id = si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND  si1.is_valid  =TRUE)\n";
        } else if (obj.getListOfBranch().size() == countNotIntegration) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        } else {
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                      + "where si1.deleted=FALSE AND si1.branch_id = si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) \n";
        }

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                  + "COUNT(stck.id) as stckid \n"
                  + "FROM inventory.stock stck \n"
                  + "INNER JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE " + whereStockInfo + ")\n"
                  + "LEFT JOIN\n"
                  + "(\n"
                  + "      SELECT \n"
                  + "      sli.stock_id,\n"
                  + "      sl.branch_id AS branch_id,\n"
                  + "      COALESCE(SUM(sli.quantity),0) AS quantity\n"
                  + "      FROM general.saleitem sli\n"
                  + "      LEFT JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE ) \n"
                  + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=FALSE ) \n"
                  + "      WHERE sli.deleted=FALSE AND sl.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "'\n"
                  + "      AND sl.is_return=FALSE AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' THEN FALSE ELSE TRUE END)\n"
                  + "      AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "      GROUP BY sli.stock_id, sli.unitprice, sli.currency_id, sl.branch_id\n"
                  + "      \n"
                  + ") t ON (stck.id=t.stock_id AND t.branch_id=si.branch_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id) \n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = si.branch_id AND brc.deleted =FALSE)\n"
                  + "WHERE stck.deleted=FALSE \n"
                  + where + "\n";
        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<ProductMovementReport> totals(String where, ProductMovementReport obj) {

        String whereStockInfo = "";
        String branchList = "";
        int countIntegration = 0;
        int countNotIntegration = 0;
        for (BranchSetting branchsetting : obj.getListOfBranch()) {
            branchList = branchList + "," + String.valueOf(branchsetting.getBranch().getId());
            if (branchsetting.isIsCentralIntegration()) {
                countIntegration++;
            } else {
                countNotIntegration++;
            }
        }

        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
            whereStockInfo = whereStockInfo + " AND si.branch_id IN(" + branchList + ") ";
        }
        if (obj.getListOfBranch().size() == countIntegration) {//Hepsinin merkezi entegrasyonu vardır
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "where si1.deleted=FALSE AND si1.branch_id = si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND  si1.is_valid  =TRUE)\n";
        } else if (obj.getListOfBranch().size() == countNotIntegration) {
            where = where + " AND stck.is_otherbranch = TRUE ";
        } else {
            where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                      + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                      + "where si1.deleted=FALSE AND si1.branch_id = si.branch_id AND si1.stock_id=stck.id \n"
                      + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) \n";
        }

        String sql = "SELECT \n"
                  + "COUNT(stck.id) as stckid ,\n"
                  + "t.currency_id AS currency_id,\n"
                  + "COALESCE(SUM(t.totalmoney),0) AS totalmoney\n"
                  + "FROM inventory.stock stck \n"
                  + "INNER JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE " + whereStockInfo + ")\n"
                  + "LEFT JOIN\n"
                  + "(\n"
                  + "     SELECT \n"
                  + "     sli.stock_id,\n"
                  + "     sli.currency_id AS currency_id,\n"
                  + "     sl.branch_id AS branch_id,\n"
                  + "     COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))),0) AS totalmoney,\n"
                  + "     COALESCE(SUM(sli.quantity),0) AS quantity\n"
                  + "     FROM general.saleitem sli\n"
                  + "     LEFT JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted=FALSE ) \n"
                  + "     LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=FALSE ) \n"
                  + "     WHERE sli.deleted=FALSE AND sl.processdate BETWEEN ? AND ? \n"
                  + "     AND sl.is_return=FALSE AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN ? AND ? THEN FALSE ELSE TRUE END)\n"
                  + "     AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "     GROUP BY sli.stock_id, sli.unitprice, sli.currency_id, sl.branch_id\n"
                  + ") t ON (stck.id=t.stock_id AND t.branch_id=si.branch_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id) \n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "INNER JOIN general.branch brc ON(brc.id = si.branch_id AND brc.deleted =FALSE)\n"
                  + "WHERE stck.deleted=FALSE\n"
                  + where + "\n"
                  + "GROUP BY t.currency_id";
        Object[] param = new Object[]{obj.getBeginDate(), obj.getEndDate(), obj.getBeginDate(), obj.getEndDate()};
        List<ProductMovementReport> result = getJdbcTemplate().query(sql, param, new ProductMovementReportMapper());
        return result;
    }

}
