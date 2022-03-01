/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2020 04:13:43
 */
package com.mepsan.marwiz.general.report.freestockreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FreeStockReportDao extends JdbcDaoSupport implements IFreeStockReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FreeStockReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        
        String whereBranch = "";
        
        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }
        
        String sql = "SELECT \n"
                  + "	 invi.id AS inviid,\n"
                  + "    inv.invoicedate AS invinvoicedate,\n"
                  + "    invi.stock_id AS invistock_id,\n"
                  + "    brn.name AS brnname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    stck.name AS stckname,\n"
                  + "    (SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                  + "    stck.supplier_id AS stcksupplier_id,\n"
                  + "    acc.name AS accname, \n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    inv.documentnumber as invdocumentnumber,\n"
                  + "    inv.documentserial as invdocumentserial,\n"
                  + "    invi.quantity AS inviquantity,\n"
                  + "    inv.is_purchase AS invis_purchase,\n"
                  + "    inv.type_id AS invtype_id,\n"
                  + "    invi.unit_id AS inviunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting\n"
                  + "FROM finance.invoiceitem invi\n"
                  + "INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted=FALSE)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id = invi.stock_id)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "WHERE invi.deleted=FALSE AND inv.status_id <> 30 AND invi.is_free = TRUE " + whereBranch + "\n"
                  + where + "\n"
                  + "ORDER BY inv.invoicedate DESC, invi.id\n"
                  + " limit " + pageSize + " offset " + first;

        List<FreeStockReport> result = getJdbcTemplate().query(sql, new FreeStockReportMapper());
        return result;
    }

    @Override
    public int count(String where, String branchList) {
        
        String whereBranch = "";
        
        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }
        
        String sql = " SELECT \n"
                  + "	COUNT(invi.id)\n"
                  + "FROM finance.invoiceitem invi\n"
                  + "INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted=FALSE)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id = invi.stock_id)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "WHERE invi.deleted=FALSE AND inv.status_id <> 30 AND invi.is_free = TRUE " + whereBranch + "\n"
                  + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where, String branchList) {
        
        String whereBranch = "";
        
        if (!branchList.equals("")) {
            whereBranch += " AND inv.branch_id IN( " + branchList + " )";
        }
        
        String sql = "SELECT \n"
                  + "	 invi.id AS inviid,\n"
                  + "    inv.invoicedate AS invinvoicedate,\n"
                  + "    invi.stock_id AS invistock_id,\n"
                  + "    brn.name AS brnname,\n"
                  + "    stck.code AS stckcode,\n"
                  + "    stck.centerproductcode AS stckcenterproductcode,\n"
                  + "    stck.barcode AS stckbarcode,\n"
                  + "    stck.name AS stckname,\n"
                  + "    (SELECT general.find_category(invi.stock_id, 1, inv.branch_id)) AS category,\n"
                  + "    stck.supplier_id AS stcksupplier_id,\n"
                  + "    acc.name AS accname, \n"
                  + "    stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                  + "    cspp.name AS csppname,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    br.name AS brname,\n"
                  + "    inv.documentnumber as invdocumentnumber,\n"
                  + "    inv.documentserial as invdocumentserial,\n"
                  + "    invi.quantity AS inviquantity,\n"
                  + "    inv.is_purchase AS invis_purchase,\n"
                  + "    inv.type_id AS invtype_id,\n"
                  + "    invi.unit_id AS inviunit_id,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitsorting\n"
                  + "FROM finance.invoiceitem invi\n"
                  + "INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted=FALSE)\n"
                  + "INNER JOIN general.branch brn ON(brn.id=inv.branch_id AND brn.deleted=FALSE)\n"
                  + "INNER JOIN inventory.stock stck ON(stck.id = invi.stock_id)\n"
                  + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                  + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                  + "LEFT JOIN general.brand br ON(br.id = stck.brand_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                  + "WHERE invi.deleted=FALSE AND inv.status_id <> 30 AND invi.is_free = TRUE " + whereBranch + "\n"
                  + where + "\n"
                  + "ORDER BY inv.invoicedate DESC, invi.id";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }
}
