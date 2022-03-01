/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.03.2018 04:02:04
 */
package com.mepsan.marwiz.general.report.stocktrackingreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseMapper;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockTrackingReportDao extends JdbcDaoSupport implements IStockTrackingReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        if (sortField == null) {
            sortField = " SUM(iwi.quantity) ";
            sortOrder = "DESC";
        }

        String sql = "SELECT\n"
                + "        iwi.stock_id AS iwistock_id,\n"
                + "        stck.name AS stckname,\n"
                + "	   stck.barcode AS stckbarcode,\n"
                + "        stck.code AS stckcode,\n"
                + "        stck.centerproductcode AS stckcenterproductcode,\n"
                + "	   COALESCE(SUM(iwi.quantity),0) AS sumquantity,\n"
                ////              + "   COALESCE(pll.price,0) AS pllprice,\n"
                //                + "   COALESCE(pll.price,0)/(1+(COALESCE(ptg.rate,0)/100)) as pllpricewithouttax, \n"
                + "        COALESCE(pll.currency_id,0) AS pllcurrency_id,\n"
                + "        stck.unit_id AS stckunit_id,\n"
                + "        gunt.sortname AS guntsortname,\n"
                + "        gunt.unitrounding AS guntunitsorting,\n"
                + "        (SELECT general.find_category(iwi.stock_id, 1, ?)) AS category,\n"
                + "        stck.brand_id AS stckbrand_id,\n"
                + "        br.name AS brname,\n"
                + "        stck.supplier_id AS supplier_id,\n"
                + "        acc.name AS accname,\n"
                + "        stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "        cspp.name AS csppname,\n"
                + "        brn.id AS brnid,\n"
                + "        brn.name AS brnname,\n"
                + "        COALESCE(si.currentpurchaseprice, 0) AS sicurrentpurchaseprice,\n"
                + "   COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)) as sicurrentpurchasepricewithtax, \n"
                + "        si.currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                + " COALESCE(CASE \n"
                + "         WHEN pll.is_taxincluded = TRUE THEN COALESCE(pll.price,0)\n"
                + "         WHEN pll.is_taxincluded = FALSE THEN COALESCE(pll.price,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "         END,0) AS pllprice, \n"
                + " COALESCE(CASE \n"
                + "         WHEN pll.is_taxincluded = TRUE THEN COALESCE(pll.price,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "         WHEN pll.is_taxincluded = FALSE THEN COALESCE(pll.price,0) \n"
                + "         END,0) AS pllpricewithouttax \n"
                + "FROM inventory.warehouseitem iwi\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE AND si.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN (\n"
                + "               SELECT \n"
                + "                  	pli.*,\n"
                + "                   pl.branch_id\n"
                + "                FROM \n"
                + "                	inventory.pricelist pl\n"
                + "                INNER JOIN inventory.pricelistitem pli ON (pli.pricelist_id = pl.id AND pli.deleted = False)\n"
                + "		   WHERE\n"
                + "                pl.is_default=true \n"
                + "                AND pl.is_purchase=false  \n"
                + "		  ) pll ON (pll.stock_id = stck.id AND pll.branch_id = iw.branch_id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"    
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=False \n"
                + where + "\n"
                + "GROUP BY  iwi.stock_id,stck.name,stck.barcode,pll.price,pll.currency_id, stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.centerproductcode, stck.brand_id, br.name,"
                + " stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name, si.currentpurchaseprice, si.currentpurchasecurrency_id,ptg.rate,pll.is_taxincluded,stg.rate \n"
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

//        System.out.println("------stockdao--------" + sql);

        
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<StockTrackingReport> result = getJdbcTemplate().query(sql, param, new StockTrackingReportMapper());
        return result;
    }

    @Override
    public String exportData(String where, String branchID) {
        String sql = "SELECT\n"
                + "        iwi.stock_id AS iwistock_id,\n"
                + "        stck.name AS stckname,\n"
                + "	   stck.barcode AS stckbarcode,\n"
                + "        stck.code AS stckcode,\n"
                + "        stck.centerproductcode AS stckcenterproductcode,\n"
                + "	   COALESCE(SUM(iwi.quantity),0) AS sumquantity,\n"
                + "        COALESCE(pll.currency_id,0) AS pllcurrency_id,\n"
                + "        stck.unit_id AS stckunit_id,\n"
                + "        gunt.sortname AS guntsortname,\n"
                + "        gunt.unitrounding AS guntunitsorting,\n"
                + "        (SELECT general.find_category(iwi.stock_id, 1, " + sessionBean.getUser().getLastBranch().getId() + ")) AS category,\n"
                + "        stck.brand_id AS stckbrand_id,\n"
                + "        br.name AS brname,\n"
                + "        stck.supplier_id AS supplier_id,\n"
                + "        acc.name AS accname,\n"
                + "        stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "        cspp.name AS csppname,\n"
                + "        brn.id AS brnid,\n"
                + "        brn.name AS brnname,\n"
                + "        COALESCE(si.currentpurchaseprice, 0) AS sicurrentpurchaseprice,\n"
                + "   COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(ptg.rate,0)/100)) as sicurrentpurchasepricewithtax, \n"
                + "        si.currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                + " COALESCE(CASE \n"
                + "         WHEN pll.is_taxincluded = TRUE THEN COALESCE(pll.price,0)\n"
                + "         WHEN pll.is_taxincluded = FALSE THEN COALESCE(pll.price,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "         END,0) AS pllprice,\n"
                + " COALESCE(CASE \n"
                + "         WHEN pll.is_taxincluded = TRUE THEN COALESCE(pll.price,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "         WHEN pll.is_taxincluded = FALSE THEN COALESCE(pll.price,0) \n"
                + "         END,0) AS pllpricewithouttax \n"
                + "FROM inventory.warehouseitem iwi\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE AND si.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN (\n"
                + "               SELECT \n"
                + "                  	pli.*,\n"
                + "                   pl.branch_id\n"
                + "                FROM \n"
                + "                	inventory.pricelist pl\n"
                + "                INNER JOIN inventory.pricelistitem pli ON (pli.pricelist_id = pl.id AND pli.deleted = False)\n"
                + "		   WHERE\n"
                + "                pl.is_default=true \n"
                + "                AND pl.is_purchase=false  \n"
                + "		  ) pll ON (pll.stock_id = stck.id AND pll.branch_id = iw.branch_id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                + "LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=False \n"
                + where + "\n"
                + "GROUP BY  iwi.stock_id,stck.name,stck.barcode,pll.price,pll.currency_id, stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name, si.currentpurchaseprice, si.currentpurchasecurrency_id,ptg.rate,pll.is_taxincluded,stg.rate\n"
                + "ORDER BY SUM(iwi.quantity) desc \n";

        return sql;
    }

    @Override
    public int count(String where, String branchList) {
        String sql = "SELECT COUNT(u.iwistock_id) FROM\n"
                + "      (SELECT COUNT(iwi.stock_id) AS iwistock_id\n"
                + "FROM inventory.warehouseitem iwi\n"
                + "INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id)\n"
                + "INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                + "INNER JOIN general.branch brn ON(brn.id=iw.branch_id AND brn.deleted=FALSE)\n "
                + "LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted =FALSE AND si.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                + "LEFT JOIN (\n"
                + "               SELECT \n"
                + "                  	pli.*,\n"
                + "                   pl.branch_id\n"
                + "                FROM \n"
                + "                	inventory.pricelist pl\n"
                + "                INNER JOIN inventory.pricelistitem pli ON (pli.pricelist_id = pl.id AND pli.deleted = False)\n"
                + "		   WHERE\n"
                + "                pl.is_default=true \n"
                + "                AND pl.is_purchase=false  \n"
                + "		  ) pll ON (pll.stock_id = stck.id AND pll.branch_id = iw.branch_id)\n"
                + "LEFT JOIN general.brand br ON(br.id = stck.brand_id AND br.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "WHERE iwi.deleted=False \n"
                + where + "\n"
                + "GROUP BY  iwi.stock_id,stck.name,stck.barcode,pll.price,pll.currency_id, stck.unit_id, gunt.sortname, gunt.unitrounding, stck.code, stck.centerproductcode, stck.brand_id, br.name, stck.supplier_id, acc.name, stck.centralsupplier_id, cspp.name, brn.id, brn.name, si.currentpurchaseprice, si.currentpurchasecurrency_id) u";

        Object[] param = new Object[]{};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<Warehouse> listWarehouse(String branchList) {
        String sql = "select \n"
                + "iw.id as iwid,\n"
                + "iw.name as iwname\n"
                + "from inventory.warehouse iw\n"
                + "where iw.deleted=false and iw.branch_id IN (" + branchList + ") AND iw.is_fuel=FALSE  ";

        Object[] param = {};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new WarehouseMapper());
        return result;
    }

    @Override
    public List<StockTrackingReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
