/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:00:40 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ProdutReportSoldTogetherDao extends JdbcDaoSupport implements IProdutReportSoldTogetherDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ProductReportSoldTogether> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, ProductReportSoldTogether obj, BranchSetting selectedBranch) {
        String fields = " ";
        String orderAndGroupBy = " ";

        switch (obj.getTimeInterval()) {

            case 1:
                //saatlik
                fields += " concat(to_char(msl.processdate::timestamp,'HH24:00') ,concat(' - ',to_char(((date_trunc('hour', msl.processdate::timestamp)+ '1 hour'::interval)::timestamp),'HH24:00'))) AS hour,\n";
                orderAndGroupBy += "concat(to_char(msl.processdate::timestamp,'HH24:00') ,concat(' - ',to_char(((date_trunc('hour', msl.processdate::timestamp)+ '1 hour'::interval)::timestamp),'HH24:00'))), ";
                break;
            case 2:
                //günlük
                fields += " ( msl.processdate)::date AS day,\n";
                orderAndGroupBy += "( msl.processdate)::date , ";
                break;
            case 3:
                //haftalık
                fields += " date_trunc('week', msl.processdate::timestamp)::date AS firstWeekDay,\n"
                        + " (date_trunc('week', msl.processdate::timestamp)+ '6 days'::interval)::date AS endWeekDay,\n";
                orderAndGroupBy += " date_trunc('week', msl.processdate::timestamp)::date,"
                        + " (date_trunc('week', msl.processdate::timestamp)+ '6 days'::interval)::date,";
                break;
            case 4:
                //aylık
                fields += " date_part('month',msl.processdate) AS month,\n";
                orderAndGroupBy += "  date_part('month',msl.processdate), ";
                break;
            case 5:
                //yıllık
                fields += " date_part('year',msl.processdate) AS year,\n";
                orderAndGroupBy += "  date_part('year',msl.processdate), ";
                break;
            case 6://vardiya
                fields += " msl.shiftno AS mslshiftno,\n";
                orderAndGroupBy += " msl.shiftno,";
                break;
            default:
                break;
        }

//        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
//            where = where + " AND stck.centerstock_id IS NOT NULL  ";
//        } else {
//            where = where + " AND stck.is_otherbranch = TRUE ";
//        }

        String sql = "SELECT\n"
                + "    stck.name AS stckname,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.supplierproductcode AS stcksupplierproductcode,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    msli.stock_id AS mslistock_id,\n"
                + fields + "\n"
                + "COUNT(DISTINCT(msl.id)) AS quantity\n"
                + "FROM general.sale msl \n"
                + "INNER JOIN general.saleitem msli  ON(msl.id = msli.sale_id AND msli.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck  ON (stck.id=msli.stock_id)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = msl.id AND sll.deleted = False)\n"
                + "LEFT JOIN general.account acc  ON(acc.id=stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE msl.deleted=FALSE AND msli.is_calcincluded = FALSE AND msl.branch_id=? AND msl.is_return=FALSE\n"
                + " AND msli.stock_id <>? AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "AND ? IN (select \n"
                + "msli2.stock_id \n"
                + "from general.sale msl1 \n"
                + "INNER JOIN general.saleitem  msli2 on(msl1.id=msli2.sale_id  AND msl1.id=msl.id AND msli.deleted=FALSE)\n"
                + "where msl1.branch_id =" + selectedBranch.getBranch().getId() + " AND msl1.deleted=FALSE AND msl1.is_return=FALSE AND msli2.is_calcincluded = FALSE) \n"
                + where + "\n"
                + "GROUP BY " + orderAndGroupBy + " msli.stock_id, stck.name, stck.barcode, stck.supplierproductcode, stck.supplier_id, stck.code,acc.name, stck.centralsupplier_id, cspp.name \n"
                + "ORDER BY COUNT(DISTINCT(msl.id)) DESC OFFSET " + first + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY \n";

        Object[] param = new Object[]{selectedBranch.getBranch().getId(), obj.getStock1().getId(), obj.getStock1().getId()};
        List<ProductReportSoldTogether> result = getJdbcTemplate().query(sql, param, new ProductReportSoldTogetherMapper());
        return result;
    }

    @Override
    public int count(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch) {
        String groupBy = "";
        String join = "";

        switch (obj.getTimeInterval()) {

            case 1:
                //saatlik
                groupBy += "concat(to_char(msl.processdate::timestamp,'HH24:00') ,concat(' - ',to_char(((date_trunc('hour', msl.processdate::timestamp)+ '1 hour'::interval)::timestamp),'HH24:00'))), ";
                break;
            case 2:
                //günlük
                groupBy += "(msl.processdate)::date , ";
                break;
            case 3:
                //haftalık
                groupBy += " date_trunc('week', msl.processdate::timestamp)::date,"
                        + " (date_trunc('week', msl.processdate::timestamp)+ '6 days'::interval)::date,";
                break;
            case 4:
                //aylık
                groupBy += " date_part('month',msl.processdate), ";
                break;
            case 5:
                //yıllık
                groupBy += " date_part('year',msl.processdate), ";
                break;
            case 6://vardiya
                groupBy += " msl.shiftno,";
                break;
            default:
                break;
        }

//        if (selectedBranch.isIsCentralIntegration()) {
//            where = where + " AND stck.centerstock_id IS NOT NULL  ";
//        } else {
//            where = where + " AND stck.centerstock_id IS NULL  ";
//        }

        String sql = "SELECT\n"
                + "COUNT(t.count)\n"
                + "FROM\n"
                + "(\n"
                + "SELECT\n"
                + "COUNT(msli.stock_id) AS count\n"
                + "FROM general.sale msl \n"
                + "LEFT JOIN general.saleitem msli  ON(msl.id = msli.sale_id AND msli.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck  ON (stck.id=msli.stock_id)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = msl.id AND sll.deleted = False)\n"
                + "LEFT JOIN general.account acc  ON(acc.id=stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + join + "\n"
                + "WHERE msl.deleted=FALSE AND msl.branch_id=? AND msl.is_return=FALSE AND msli.is_calcincluded = FALSE\n"
                + " AND msli.stock_id <>? AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "AND ? IN (select \n"
                + "msli2.stock_id \n"
                + "from general.sale msl1 \n"
                + "INNER JOIN general.saleitem  msli2 on(msl1.id=msli2.sale_id  AND msl1.id=msl.id AND msli.deleted=FALSE)\n"
                + "where msl1.branch_id =" + selectedBranch.getBranch().getId() + " AND msl1.deleted=FALSE AND msl1.is_return=FALSE AND msli2.is_calcincluded = FALSE) \n"
                + where + "\n"
                + "GROUP BY " + groupBy + " msli.stock_id,stck.name, stck.barcode, stck.supplierproductcode, stck.supplier_id, stck.code,acc.name, stck.centralsupplier_id, cspp.name\n"
                + ") t";

        Object[] param = new Object[]{selectedBranch.getBranch().getId(), obj.getStock1().getId(), obj.getStock1().getId()};
        int result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return result;
    }

    @Override
    public String exportData(String where, ProductReportSoldTogether obj, BranchSetting selectedBranch) {
        String fields = " ";
        String orderAndGroupBy = " ";

        switch (obj.getTimeInterval()) {

            case 1:
                //saatlik
                fields += " concat(to_char(msl.processdate::timestamp,'HH24:00') ,concat(' - ',to_char(((date_trunc('hour', msl.processdate::timestamp)+ '1 hour'::interval)::timestamp),'HH24:00'))) AS hour,\n";
                orderAndGroupBy += "concat(to_char(msl.processdate::timestamp,'HH24:00') ,concat(' - ',to_char(((date_trunc('hour', msl.processdate::timestamp)+ '1 hour'::interval)::timestamp),'HH24:00'))), ";
                break;

            case 2:
                //günlük
                fields += " (msl.processdate)::date AS day,\n";
                orderAndGroupBy += "( msl.processdate)::date , ";
                break;
            case 3:
                //haftalık
                fields += " date_trunc('week', msl.processdate::timestamp)::date AS firstWeekDay,\n"
                        + " (date_trunc('week', msl.processdate::timestamp)+ '6 days'::interval)::date AS endWeekDay,\n";
                orderAndGroupBy += " date_trunc('week', msl.processdate::timestamp)::date,"
                        + " (date_trunc('week', msl.processdate::timestamp)+ '6 days'::interval)::date,";
                break;
            case 4:
                //aylık
                fields += " date_part('month',msl.processdate) AS month,\n";
                orderAndGroupBy += "  date_part('month',msl.processdate), ";
                break;
            case 5:
                //yıllık
                fields += " date_part('year',msl.processdate) AS year,\n";
                orderAndGroupBy += "  date_part('year',msl.processdate), ";
                break;
            case 6://vardiya
                fields += " msl.shiftno AS mslshiftno,\n";
                orderAndGroupBy += " msl.shiftno,";
                break;
            default:
                break;
        }

//        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
//            where = where + " AND stck.centerstock_id IS NOT NULL  ";
//        } else {
//            where = where + " AND stck.centerstock_id IS NULL  ";
//        }

        String sql = "SELECT\n"
                + "    stck.name AS stckname,\n"
                + "    stck.code AS stckcode,\n"
                + "    stck.barcode AS stckbarcode,\n"
                + "    stck.supplierproductcode AS stcksupplierproductcode,\n"
                + "    stck.supplier_id AS stcksupplier_id,\n"
                + "    acc.name AS accname, \n"
                + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "    cspp.name AS csppname,\n"
                + "    msli.stock_id AS mslistock_id,\n"
                + fields + "\n"
                + "COUNT(DISTINCT(msl.id)) AS quantity\n"
                + "FROM general.sale msl \n"
                + "INNER JOIN general.saleitem msli  ON(msl.id = msli.sale_id AND msli.deleted=FALSE)\n"
                + "INNER JOIN inventory.stock stck  ON (stck.id=msli.stock_id)\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = msl.id AND sll.deleted = False)\n"
                + "LEFT JOIN general.account acc  ON(acc.id=stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE msl.deleted=FALSE AND msl.branch_id=" + selectedBranch.getBranch().getId() + " AND msl.is_return=FALSE AND msli.is_calcincluded= FALSE\n"
                + " AND msli.stock_id <>" + obj.getStock1().getId() + " AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "AND " + obj.getStock1().getId() + " IN (select \n"
                + "msli2.stock_id \n"
                + "from general.sale msl1 \n"
                + "INNER JOIN general.saleitem  msli2 on(msl1.id=msli2.sale_id AND msl1.id=msl.id AND msli.deleted=FALSE)\n"
                + "where msl1.branch_id =" +selectedBranch.getBranch().getId() + " AND msl1.deleted=FALSE AND msl1.is_return=FALSE AND msli2.is_calcincluded= FALSE) \n"
                + where + "\n"
                + "GROUP BY " + orderAndGroupBy + " msli.stock_id, stck.name, stck.barcode, stck.supplierproductcode, stck.supplier_id, stck.code,acc.name, stck.centralsupplier_id, cspp.name \n"
                + "ORDER BY COUNT(DISTINCT(msl.id)) DESC \n";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
