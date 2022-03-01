/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ebubekir.buker
 */
public class FuelSalesReportDao extends JdbcDaoSupport implements IFuelSalesReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FuelSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList, FuelSalesReport fuelSalesReport) {
     
        String sql = "";

        try {
            sql = "SELECT\n"
                    + "shf.id as shfid,\n"
                    + "acc.id as accid,\n"
                    + "br.id as brid,\n"
                    + "br.name as brname,\n"
                    + "shf.processdate as shfprocessdate,\n"
                    + "ash.shiftno as ashshiftno,\n"
                    + "acc.name as accname,\n"
                    + "shf.plate as shfplate,\n"
                    + "stc.code as stccode,\n"
                    + "stc.centerproductcode as stccentralproductcode,\n"
                    + "stc.barcode as stcbarcode,\n"
                    + "stc.name as stcname,\n"
                    + "shf.attendant_id as shfattendant_id,\n"
                    + "acc2.name as attendantname,\n"
                    + "fls.name as flsname,\n"
                    + "shf.liter as shfliter,\n"
                    + "shf.saletype as shfsaletype,\n"
                    + "shf.price as shfprice,\n"
                    + "shf.discounttotal as shfdiscountotal,\n"
                    + "shf.totalmoney as shftotalmoney,\n"
                    + "crn.id as crnid,\n"
                    + "crn.code as crncode,\n"
                    + "shf.receiptno as shfreceiptno,\n"
                    + "gun.id as gunid,\n"
                    + "gun.name as gunname,\n"
                    + "gun.sortname as gunsortname,\n"
                    + "gun.unitrounding as gununitrounding\n"
                    + "FROM \n"
                    + "	automation.shiftsale shf \n"
                    + "    INNER JOIN automation.shift ash ON (ash.id = shf.shift_id and ash.deleted=false)\n"
                    + "    INNER JOIN general.branch br ON (br.id = ash.branch_id)\n"
                    + "    LEFT JOIN general.account acc ON (acc.id = shf.account_id)\n"
                    + "    INNER JOIN inventory.stock stc ON (stc.id = shf.stock_id)\n"
                    + "    LEFT JOIN  system.currency crn ON (crn.id=br.currency_id)\n"
                    + "    LEFT JOIN general.account acc2 ON (acc2.id=shf.attendant_id)\n"
                    + "    INNER  JOIN automation.fuelsaletype fls ON(shf.saletype=fls.typeno AND fls.branch_id = ash.branch_id) \n"
                    + "	INNER JOIN general.unit gun ON (gun.id=stc.unit_id)\n"
                    + "    WHERE shf.deleted=FALSE  "                   
                    + where + "\n"
                    + "    ORDER BY br.name,shf.processdate,ash.shiftno\n"
                    + " LIMIT " + pageSize + " OFFSET " + first;

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<FuelSalesReport> result = getJdbcTemplate().query(sql, new FuelSalesReportMapper());
        return result;

    }

    @Override
    public int count(String where) {

        String sql = "SELECT\n"
                + "\n"
                + "COUNT(shf.id)\n"
                + "\n"
                + "FROM \n"
                + "	automation.shiftsale shf \n"
                + "    INNER JOIN automation.shift ash ON (ash.id = shf.shift_id and ash.deleted=false)\n"
                + "    INNER JOIN general.branch br ON (br.id = ash.branch_id)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = shf.account_id)\n"
                + "    INNER JOIN inventory.stock stc ON (stc.id = shf.stock_id)\n"
                + "    LEFT JOIN  system.currency crn ON (crn.id=br.currency_id)\n"
                + "    LEFT JOIN general.account acc2 ON (acc2.id=shf.attendant_id)\n"
                + "    INNER  JOIN automation.fuelsaletype fls ON(shf.saletype=fls.typeno AND fls.branch_id = ash.branch_id)"
                + "    WHERE shf.deleted=FALSE  " 
                + where + "\n";
        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public String exportData(String where, String branchList, FuelSalesReport fuelSalesReport) {

        String sql = "SELECT\n"
                + "shf.id as shfid,\n"
                + "acc.id as accid,\n"
                + "br.id as brid,\n"
                + "br.name as brname,\n"
                + "shf.processdate as shfprocessdate,\n"
                + "ash.shiftno as ashshiftno,\n"
                + "acc.name as accname,\n"
                + "shf.plate as shfplate,\n"
                + "stc.code as stccode,\n"
                + "stc.centerproductcode as stccentralproductcode,\n"
                + "stc.barcode as stcbarcode,\n"
                + "stc.name as stcname,\n"
                + "--shf.attendantname as shfattendantname,\n"
                + "shf.attendant_id as shfattendant_id,\n"
                + "acc2.name as attendantname,\n"
                + "\n"
                + "fls.name as flsname,\n"
                + "shf.liter as shfliter,\n"
                + "shf.saletype as shfsaletype,\n"
                + "shf.price as shfprice,\n"
                + "shf.discounttotal as shfdiscountotal,\n"
                + "shf.totalmoney as shftotalmoney,\n"
                + "crn.id as crnid,\n"
                + "crn.code as crncode,\n"
                + "shf.receiptno as shfreceiptno,\n"
                + "gun.id as gunid,\n"
                + "gun.name as gunname,\n"
                + "gun.sortname as gunsortname,\n"
                + "gun.unitrounding as gununitrounding\n"
                + "\n"
                + "FROM \n"
                + "	automation.shiftsale shf \n"
                + "    INNER JOIN automation.shift ash ON (ash.id = shf.shift_id and ash.deleted=false)\n"
                + "    INNER JOIN general.branch br ON (br.id = ash.branch_id)\n"
                + "    LEFT JOIN general.account acc ON (acc.id = shf.account_id)\n"
                + "    INNER JOIN inventory.stock stc ON (stc.id = shf.stock_id)\n"
                + "    LEFT JOIN  system.currency crn ON (crn.id=br.currency_id)\n"
                + "    LEFT JOIN general.account acc2 ON (acc2.id=shf.attendant_id)\n"
                + "    INNER  JOIN automation.fuelsaletype fls ON(shf.saletype=fls.typeno AND fls.branch_id = ash.branch_id) \n"
                + "	INNER JOIN general.unit gun ON (gun.id=stc.unit_id)\n"
                + "    WHERE shf.deleted=FALSE  " 
                + where + "\n"
                + "    ORDER BY br.name,shf.processdate,ash.shiftno\n";
        return sql;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
