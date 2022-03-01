/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.08.2018 01:46:20
 */
package com.mepsan.marwiz.general.report.fulltakingreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FullTakingReportDao extends JdbcDaoSupport implements IFullTakingReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FullTakingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FullTakingReport productInventoryReport) {

        String sql = "SELECT\n"
                + "      sti.stock_id as iwistock_id,\n"
                + "      stck.name as stckname,\n"
                + "      stck.code as stckcode,\n"
                + "      stck.centerproductcode AS stckcenterproductcode,\n"
                + "      stck.barcode AS stckbarcode,\n"
                + "      gunt.sortname as guntsortname,\n"
                + "      gunt.unitrounding as guntunitrounding,\n"
                + "      COALESCE(sti.realquantity, 0) as quantity,\n"
                + "      COALESCE(sti.systemquantity,0) AS systemquantity,\n"
                + "      COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = TRUE LIMIT 1 ),0) AS purchasetaxgrouprate,\n"
                + "      COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = FALSE  LIMIT 1 ),0) AS salestaxgrouprate,\n"
                + "      abs((COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0))) as different,\n"
                + "      CASE WHEN   COALESCE(sti.systemquantity,0) = COALESCE(sti.realquantity, 0) THEN 0 WHEN COALESCE(sti.systemquantity,0) > COALESCE(sti.realquantity, 0) THEN -1 ELSE 1 END as tef,\n"
                + "      CASE WHEN st.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti.currentpurchaseprice,0) END AS lastpurchaseprice,\n"
                + "      CASE WHEN st.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "      CASE WHEN st.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti.currentsaleprice,0) END AS lastsaleprice,\n"
                + "      CASE WHEN st.status_id = 15 THEN si.currentsalecurrency_id ELSE sti.currentsalecurrency_id END AS lastsalecurrency_id,\n"
                + "      (SELECT array_to_string(array(SELECT ct.name \n"
                + "       FROM inventory.stock_categorization_con stcc \n"
                + "       INNER JOIN general.categorization ct ON(ct.id = stcc.categorization_id AND ct.deleted=  FALSE) \n"
                + "       WHERE stcc.deleted = FALSE  AND ct.parent_id IS NOT NULL AND stcc.stock_id = stck.id\n"
                + "       ), ',')) AS subcategories,\n"
                + "     (SELECT array_to_string(array(\n"
                + "       SELECT category FROM(\n"
                + "           WITH RECURSIVE category (id, parent_id, name) AS (\n"
                + "                     SELECT  id, parent_id, name\n"
                + "                     FROM    general.categorization \n"
                + "                     WHERE   parent_id is null AND deleted = FALSE AND item_id = 2  \n"
                + "                 \n"
                + "                     UNION ALL\n"
                + "                 \n"
                + "                     SELECT  p.id, COALESCE(t0.parent_id,p.parent_id), t0.name\n"
                + "                     FROM general.categorization p \n"
                + "                     INNER JOIN category t0 ON (t0.id = p.parent_id)\n"
                + "                     WHERE p.deleted = FALSE AND p.item_id = 2   \n"
                + "                 )\n"
                + "                 SELECT DISTINCT (CASE WHEN parent_id IS NULL THEN id ELSE parent_id END ) as categorization_id, name AS category\n"
                + "                 FROM  category\n"
                + "                 WHERE id IN ( \n"
                + "                 SELECT ct.id \n"
                + "           FROM inventory.stock_categorization_con stcc \n"
                + "           INNER JOIN general.categorization ct ON(ct.id = stcc.categorization_id AND ct.deleted=  FALSE) \n"
                + "           WHERE stcc.deleted = FALSE  AND stcc.stock_id = sti.stock_id  )\n"
                + "           ) recursivetable\n"
                + "     ), ',')) AS parentcategories, \n"
                + "CASE \n"
                + "WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)\n"
                + "WHEN prli.id IS NULL THEN 0\n"
                + "WHEN st.is_taxincluded = TRUE THEN \n"
                + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                + "     END\n"
                + "ELSE\n"
                + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                + "     END  \n"
                + "END AS price,\n"
                + "(CASE WHEN st.status_id = 16 THEN sti.currentpricelistcurrency_id ELSE prli.currency_id END) AS prlicurrency_id\n"
                + "FROM \n"
                + "    inventory.stocktakingitem sti \n"
                + "    INNER JOIN inventory.stocktaking st ON (st.id = sti.stocktaking_id )\n"
                + "    INNER JOIN inventory.stock stck ON (sti.stock_id=stck.id)\n"
                + "    LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                + "    LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id)\n"
                + "    LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "   LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "WHERE \n"
                + "   sti.stocktaking_id= ?  AND sti.deleted=false   " + where + "\n"
                + "order by sti.id \n"
                + "limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{productInventoryReport.getStockTaking().getId()};
        List<FullTakingReport> result = getJdbcTemplate().query(sql, param, new FullTakingReportMapper());

        return result;
    }

    @Override
    public int count(String where, FullTakingReport productInventoryReport) {

        String sql = "SELECT \n"
                + "	COUNT(sti.stock_id) AS iwistock_id\n"
                + "FROM \n"
                + "    inventory.stocktakingitem sti \n"
                + "     INNER JOIN inventory.stocktaking st ON (st.id = sti.stocktaking_id )"
                + "    INNER JOIN inventory.stock stck ON (sti.stock_id=stck.id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id)\n"
                + "WHERE \n"
                + "   sti.stocktaking_id= ? AND sti.deleted=false  " + where;

        Object[] param = new Object[]{productInventoryReport.getStockTaking().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where) {

        String sql = "SELECT\n"
                + "      sti.stock_id as iwistock_id,\n"
                + "      stck.name as stckname,\n"
                + "      stck.code as stckcode,\n"
                + "      stck.centerproductcode AS stckcenterproductcode,\n"
                + "      stck.barcode AS stckbarcode,\n"
                + "      gunt.sortname as guntsortname,\n"
                + "      gunt.unitrounding as guntunitrounding,\n"
                + "      COALESCE(sti.realquantity, 0) as quantity,\n"
                + "      COALESCE(sti.systemquantity,0) AS systemquantity,\n"
                + "      COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = TRUE LIMIT 1 ),0) AS purchasetaxgrouprate,\n"
                + "      COALESCE((SELECT tg.rate FROM inventory.stock_taxgroup_con stcc INNER JOIN inventory.taxgroup tg ON (tg.id = stcc.taxgroup_id AND tg.type_id = 10 AND tg.deleted = FALSE )WHERE stcc.stock_id = stck.id AND stcc.deleted = FALSE AND stcc.is_purchase = FALSE  LIMIT 1 ),0) AS salestaxgrouprate,\n"
                + "      abs((COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0))) as different,\n"
                + "      CASE WHEN   COALESCE(sti.systemquantity,0) = COALESCE(sti.realquantity, 0) THEN 0 WHEN COALESCE(sti.systemquantity,0) >  COALESCE(sti.realquantity, 0) THEN -1 ELSE 1 END as tef,\n"
                + "      CASE WHEN st.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti.currentpurchaseprice,0) END AS lastpurchaseprice,\n"
                + "      CASE WHEN st.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "      CASE WHEN st.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti.currentsaleprice,0) END AS lastsaleprice,\n"
                + "      CASE WHEN st.status_id = 15 THEN si.currentsalecurrency_id ELSE sti.currentsalecurrency_id END AS lastsalecurrency_id,\n"
                + "      (SELECT array_to_string(array(SELECT ct.name \n"
                + "       FROM inventory.stock_categorization_con stcc \n"
                + "       INNER JOIN general.categorization ct ON(ct.id = stcc.categorization_id AND ct.deleted=  FALSE) \n"
                + "       WHERE stcc.deleted = FALSE  AND ct.parent_id IS NOT NULL AND stcc.stock_id = stck.id\n"
                + "       ), ',')) AS subcategories,\n"
                + "     (SELECT array_to_string(array(\n"
                + "       SELECT category FROM(\n"
                + "           WITH RECURSIVE category (id, parent_id, name) AS (\n"
                + "                     SELECT  id, parent_id, name\n"
                + "                     FROM    general.categorization \n"
                + "                     WHERE   parent_id is null AND deleted = FALSE AND item_id = 2 \n"
                + "                 \n"
                + "                     UNION ALL\n"
                + "                 \n"
                + "                     SELECT  p.id, COALESCE(t0.parent_id,p.parent_id), t0.name\n"
                + "                     FROM general.categorization p \n"
                + "                     INNER JOIN category t0 ON (t0.id = p.parent_id)\n"
                + "                     WHERE p.deleted = FALSE AND p.item_id = 2  \n"
                + "                 )\n"
                + "                 SELECT DISTINCT (CASE WHEN parent_id IS NULL THEN id ELSE parent_id END ) as categorization_id, name AS category\n"
                + "                 FROM  category\n"
                + "                 WHERE id IN ( \n"
                + "                 SELECT ct.id \n"
                + "           FROM inventory.stock_categorization_con stcc \n"
                + "           INNER JOIN general.categorization ct ON(ct.id = stcc.categorization_id AND ct.deleted=  FALSE) \n"
                + "           WHERE stcc.deleted = FALSE  AND stcc.stock_id = sti.stock_id  )\n"
                + "           ) recursivetable\n"
                + "     ), ',')) AS parentcategories, \n"
                + "CASE \n"
                + "WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)\n"
                + "WHEN prli.id IS NULL THEN 0\n"
                + "WHEN st.is_taxincluded = TRUE THEN \n"
                + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                + "     END\n"
                + "ELSE\n"
                + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                + "     END  \n"
                + "END AS price,\n"
                + "(CASE WHEN st.status_id = 16 THEN sti.currentpricelistcurrency_id ELSE prli.currency_id END) AS prlicurrency_id\n"
                + "FROM \n"
                + "    inventory.stocktakingitem sti \n"
                + "     INNER JOIN inventory.stocktaking st ON (st.id = sti.stocktaking_id )"
                + "    INNER JOIN inventory.stock stck ON (sti.stock_id=stck.id)\n"
                + "    LEFT JOIN inventory.pricelist prl ON(prl.id = st.pricelist_id AND prl.deleted = FALSE) \n"
                + "    LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id)\n"
                + "    LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "   LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "WHERE \n"
                + "   sti.stocktaking_id= ?  AND sti.deleted=false  " + where + " \n"
                + "order by sti.id \n";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<FullTakingReport> totals(String where, FullTakingReport fullTakingReport) {

        String sql = "SELECT \n"
                + "	COUNT(sti.stock_id) AS iwistock_id,\n"
                + "          SUM(CASE WHEN st.status_id = 15 THEN COALESCE(si.currentpurchaseprice,0) ELSE COALESCE(sti.currentpurchaseprice,0) END * abs((COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0)))* CASE WHEN   COALESCE(sti.systemquantity,0) = COALESCE(sti.realquantity, 0) THEN 0 WHEN COALESCE(sti.systemquantity,0) >  COALESCE(sti.realquantity, 0) THEN -1 ELSE 1 END\n"
                + "    		)AS lastpurchaseprice,\n"
                + "     CASE WHEN st.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti.currentpurchasecurrency_id END AS lastpurchasecurrency_id,\n"
                + "     SUM(CASE WHEN st.status_id = 15 THEN COALESCE(si.currentsaleprice,0) ELSE COALESCE(sti.currentsaleprice,0) END* abs((COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0)))* CASE WHEN   COALESCE(sti.systemquantity,0) = COALESCE(sti.realquantity, 0) THEN 0 WHEN COALESCE(sti.systemquantity,0) > COALESCE(sti.realquantity, 0) THEN -1 ELSE 1 END\n"
                + "    	) AS lastsaleprice, \n"
                + "     CASE WHEN st.status_id = 15 THEN si.currentsalecurrency_id ELSE sti.currentsalecurrency_id END AS lastsalecurrency_id\n"
                + "FROM \n"
                + "    inventory.stocktakingitem sti \n"
                + "     INNER JOIN inventory.stocktaking st ON (st.id = sti.stocktaking_id )"
                + "    INNER JOIN inventory.stock stck ON (sti.stock_id=stck.id)\n"
                + "    LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=st.branch_id) \n"
                + "    LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id)\n"
                + "WHERE \n"
                + "   sti.stocktaking_id= ? AND sti.deleted=false " + where + "\n"
                + "GROUP BY CASE WHEN st.status_id = 15 THEN si.currentpurchasecurrency_id ELSE sti.currentpurchasecurrency_id END,CASE WHEN st.status_id = 15 THEN si.currentsalecurrency_id ELSE sti.currentsalecurrency_id END";

        Object[] param = new Object[]{fullTakingReport.getStockTaking().getId()};
        List<FullTakingReport> result = getJdbcTemplate().query(sql, param, new FullTakingReportMapper());

        return result;
    }

}
