/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.02.2018 05:04:29
 */
package com.mepsan.marwiz.general.report.salessummaryreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SalesSummaryReportDao extends JdbcDaoSupport implements ISalesSummaryReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, SalesSummaryReport salesSummaryReport) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        if (sortField == null) {
            sortField = " brn.id, brn.name, sli.stock_id, (sli.unitprice*sli.exchangerate) ";
            sortOrder = "DESC";
        } else if (sortField.equals("totalCountByStock")) {
            sortField = " COALESCE(SUM(SUM(sli.quantity)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) ";
        } else if (sortField.equals("totalGiroByStock")) {
            sortField = " COALESCE(SUM(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * sli.exchangerate)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) ";
        } else if (sortField.equals("averageUnitPrice")) {
            sortField = " (CASE WHEN COALESCE(SUM(SUM(sli.quantity)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) <> 0 THEN\n"
                      + "COALESCE(SUM(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * sli.exchangerate)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) / COALESCE(SUM(SUM(sli.quantity)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0)\n"
                      + "ELSE 0 END)";
        }

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "WITH recursive ctTree AS(\n"
                  + "                      SELECT \n"
                  + "                          gct.id,\n"
                  + "                          scac.stock_id,\n"
                  + "                          gct.name, \n"
                  + "                          COALESCE(gct.parent_id,0) AS parent_id, \n"
                  + "                          1 AS depth\n"
                  + "                      FROM \n"
                  + "                          inventory.stock_categorization_con scac\n"
                  + "                          INNER JOIN general.categorization gct ON(scac.categorization_id = gct.id AND gct.deleted = FALSE)\n"
                  + "                  	   WHERE\n"
                  + "                          scac.deleted = FALSE\n"
                  + "                  	   UNION ALL\n"
                  + "                      SELECT     	\n"
                  + "                          gct.id, \n"
                  + "                          ct.stock_id,\n"
                  + "                          gct.name,\n"
                  + "                          COALESCE(gct.parent_id,0) AS parent_id, \n"
                  + "                          ct.depth+1 AS depth\n"
                  + "                      FROM \n"
                  + "                          general.categorization gct\n"
                  + "                          JOIN ctTree ct ON ct.parent_id = gct.id\n"
                  + "                      WHERE\n"
                  + "                         gct.deleted = FALSE\n"
                  + "                  )\n"
                  + "SELECT  \n"
                  + "          brn.id as brnid,\n"
                  + "          brn.name as brnname,\n"
                  + "	     sli.stock_id AS slistockid,\n"
                  + "          stck.name AS stckname,\n"
                  + "          stck.barcode AS stckbarcode,\n"
                  + "          stck.code AS stckcode,\n"
                  + "          stck.centerproductcode AS stckcenterproductcode,\n"
                  + "          (sli.unitprice*sli.exchangerate) AS sliunitprice,\n"
                  + "          COALESCE(SUM(sli.quantity),0) AS countQuantity, \n"
                  + "          COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) *sli.exchangerate),0) as giro,\n"
                  + "          COALESCE(SUM(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * sli.exchangerate)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) as totalgirobystock,\n"
                  + "          COALESCE(SUM(SUM(sli.quantity)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) as totalcountbystock,\n"
                  + "          sli.unit_id AS stckunit_id,\n"
                  + "          gunt.sortname AS guntsortname,\n"
                  + "          gunt.unitrounding AS guntunitsorting,\n"
                  + "          sl.currency_id AS slcurrency_id,\n"
                  + "          (\n"
                  + "           SELECT \n"
                  + "               xmlelement(\n"
                  + "                   name \"categories\",\n"
                  + "                     xmlagg(\n"
                  + "                        xmlelement(\n"
                  + "                            name \"category\",\n"
                  + "                                xmlforest (\n"
                  + "                                    ctr.id AS \"id\",\n"
                  + "                                    COALESCE(ctr.name, '') AS \"name\",\n"
                  + "                                    COALESCE(ctr.parent_id, 0) AS \"parent_id\",\n"
                  + "                                    ctr.depth AS \"depth\"\n"
                  + "                                            )\n"
                  + "                                    )\n"
                  + "                            )\n"
                  + "                        )\n"
                  + "          FROM ctTree ctr\n"
                  + "          WHERE \n"
                  + "           ctr.stock_id = sli.stock_id\n"
                  + "          ) AS category,\n"
                  + "          stck.brand_id AS stckbrand_id,\n"
                  + "          br.name AS brname,\n"
                  + "          stck.supplier_id AS supplier_id,\n"
                  + "          acc.name AS accname,\n"
                  + "          stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "          cspp.name AS csppname\n"
                  + "FROM general.saleitem sli \n"
                  + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesSummaryReport.getEndDate()) + "')\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "WHERE sl.is_return=False\n" 
                  + whereBranch + "\n"
                  + where + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND sli.deleted=False \n"
                  + "GROUP BY sli.stock_id, stck.name,stck.barcode, sli.unitprice,sli.exchangerate, sli.unit_id, gunt.sortname, gunt.unitrounding, sl.currency_id, stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id,brn.name\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{};
        List<SalesSummaryReport> result = getJdbcTemplate().query(sql, param, new SalesSummaryReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, String branchList, SalesSummaryReport salesSummaryReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "WITH recursive ctTree AS(\n"
                  + "                      SELECT \n"
                  + "                          gct.id,\n"
                  + "                          scac.stock_id,\n"
                  + "                          gct.name, \n"
                  + "                          COALESCE(gct.parent_id,0) AS parent_id, \n"
                  + "                          1 AS depth\n"
                  + "                      FROM \n"
                  + "                          inventory.stock_categorization_con scac\n"
                  + "                          INNER JOIN general.categorization gct ON(scac.categorization_id = gct.id AND gct.deleted = FALSE)\n"
                  + "                  	   WHERE\n"
                  + "                          scac.deleted = FALSE\n"
                  + "                  	   UNION ALL\n"
                  + "                      SELECT     	\n"
                  + "                          gct.id, \n"
                  + "                          ct.stock_id,\n"
                  + "                          gct.name,\n"
                  + "                          COALESCE(gct.parent_id,0) AS parent_id, \n"
                  + "                          ct.depth+1 AS depth\n"
                  + "                      FROM \n"
                  + "                          general.categorization gct\n"
                  + "                          JOIN ctTree ct ON ct.parent_id = gct.id\n"
                  + "                      WHERE\n"
                  + "                         gct.deleted = FALSE\n"
                  + "                  )\n"
                  + "SELECT  \n"
                  + "          brn.id as brnid,\n"
                  + "          brn.name as brnname,\n"
                  + "	     sli.stock_id AS slistockid,\n"
                  + "          stck.name AS stckname,\n"
                  + "          stck.barcode AS stckbarcode,\n"
                  + "          stck.code AS stckcode,\n"
                  + "          stck.centerproductcode AS stckcenterproductcode,\n"
                  + "          (COALESCE(sli.unitprice,0)*COALESCE(sli.exchangerate,1)) AS sliunitprice,\n"
                  + "          COALESCE(SUM(sli.quantity),0) AS countQuantity, \n"
                  + "          COALESCE(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) *sli.exchangerate),0) as giro,\n"
                  + "          COALESCE(SUM(SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * sli.exchangerate)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) as totalgirobystock,\n"
                  + "          COALESCE(SUM(SUM(sli.quantity)) OVER(PARTITION BY sli.stock_id, sl.currency_id),0) as totalcountbystock,\n"
                  + "          sli.unit_id AS stckunit_id,\n"
                  + "          gunt.sortname AS guntsortname,\n"
                  + "          gunt.unitrounding AS guntunitsorting,\n"
                  + "          sl.currency_id AS slcurrency_id,\n"
                  + "          (\n"
                  + "           SELECT \n"
                  + "               xmlelement(\n"
                  + "                   name \"categories\",\n"
                  + "                     xmlagg(\n"
                  + "                        xmlelement(\n"
                  + "                            name \"category\",\n"
                  + "                                xmlforest (\n"
                  + "                                    ctr.id AS \"id\",\n"
                  + "                                    COALESCE(ctr.name, '') AS \"name\",\n"
                  + "                                    COALESCE(ctr.parent_id, 0) AS \"parent_id\",\n"
                  + "                                    ctr.depth AS \"depth\"\n"
                  + "                                            )\n"
                  + "                                    )\n"
                  + "                            )\n"
                  + "                        )\n"
                  + "          FROM ctTree ctr\n"
                  + "          WHERE \n"
                  + "           ctr.stock_id = sli.stock_id\n"
                  + "          ) AS category,\n"
                  + "          stck.brand_id AS stckbrand_id,\n"
                  + "          br.name AS brname,\n"
                  + "          stck.supplier_id AS supplier_id,\n"
                  + "          acc.name AS accname,\n"
                  + "          stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "          cspp.name AS csppname\n"
                  + "FROM general.saleitem sli \n"
                  + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesSummaryReport.getEndDate()) + "')\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "WHERE sl.is_return=False \n"
                  + whereBranch + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND sli.deleted=False \n"
                  + where + "\n"
                  + "GROUP BY sli.stock_id, stck.name,stck.barcode, sli.unitprice,sli.exchangerate, sli.unit_id, gunt.sortname, gunt.unitrounding, sl.currency_id, stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id,brn.name\n"
                  + "ORDER BY brn.id,brn.name,sli.stock_id DESC, (sli.unitprice*sli.exchangerate) DESC\n";

        return sql;

    }

    @Override
    public int count(String where, String branchList, SalesSummaryReport salesSummaryReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT COUNT(u.slistock_id) FROM \n"
                  + "("
                  + "SELECT COUNT(sli.stock_id) AS slistock_id\n"
                  + "FROM general.saleitem sli \n"
                  + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesSummaryReport.getEndDate()) + "')\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "WHERE sl.is_return=False\n"
                  + whereBranch + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND sli.deleted=False \n"
                  + where + "\n"
                  + "GROUP BY brn.id,brn.name,sli.stock_id, stck.name,stck.barcode, sli.unitprice,sli.exchangerate, sli.unit_id, gunt.sortname, gunt.unitrounding, sl.currency_id, stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name \n"
                  + ") u";

        Object[] param = new Object[]{};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<SalesSummaryReport> totals(String where, String branchList, SalesSummaryReport salesSummaryReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereBranch = "";
        if (!branchList.isEmpty()) {
            whereBranch += " AND sl.branch_id IN( " + branchList + " )";
        }

        String sql = "SELECT \n"
                  + "COUNT(u.slistock_id) AS slistockid,\n"
                  + "SUM (u.totalgiro) AS totalgirobystock,\n"
                  + "u.slcurrency_id AS slcurrency_id,\n"
                  + "u.brnid as brnid,\n"
                  + "u.brnname as brnname\n"
                  + "FROM \n"
                  + "(\n"
                  + "SELECT \n"
                  + "COUNT(sli.stock_id) AS slistock_id,\n"
                  + "SUM((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))*COALESCE(sli.exchangerate,1)) AS totalgiro,\n"
                  + "sl.currency_id AS slcurrency_id,\n"
                  + "brn.id as brnid,\n"
                  + "brn.name as brnname\n"
                  + "FROM general.saleitem sli \n"
                  + "INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=sl.branch_id AND brn.deleted=FALSE)\n"
                  + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + dateFormat.format(salesSummaryReport.getEndDate()) + "')\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=sli.unit_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "WHERE sl.is_return=False \n"
                  + whereBranch + "\n"
                  + "AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND sli.deleted=False            \n"
                  + where + "\n"
                  + "GROUP BY brn.id,brn.name,sli.stock_id, stck.name,stck.barcode, sli.unitprice,sli.exchangerate, sli.unit_id, gunt.sortname, gunt.unitrounding, sl.currency_id, stck.code,stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name\n"
                  + ") u\n"
                  + "GROUP BY u.slcurrency_id,u.brnid,u.brnname\n"
                  + "ORDER BY u.brnid,u.brnname";

        Object[] param = new Object[]{};
        List<SalesSummaryReport> result = getJdbcTemplate().query(sql, param, new SalesSummaryReportMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<SalesSummaryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
