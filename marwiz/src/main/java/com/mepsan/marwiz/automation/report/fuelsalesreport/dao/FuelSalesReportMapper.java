/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.report.fuelsalesreport.dao;

import com.mepsan.marwiz.general.model.automation.FuelSalesReport;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ebubekir.buker
 */
public class FuelSalesReportMapper implements RowMapper<FuelSalesReport> {

    @Override
    public FuelSalesReport mapRow(ResultSet rs, int i) throws SQLException {
        FuelSalesReport fs = new FuelSalesReport();

        try {

            fs.setId(rs.getInt("shfid"));
            fs.getBranch().setId(rs.getInt("brid"));

            fs.getBranch().setName(rs.getString("brname"));
            fs.setProcessDate(rs.getTimestamp("shfprocessdate"));
            fs.setShiftNo(rs.getString("ashshiftno"));
            fs.getAccount().setName(rs.getString("accname"));
            fs.setPlate(rs.getString("shfplate"));
            fs.setStockCode(rs.getString("stccode"));
            fs.setCentralProductCode(rs.getString("stccentralproductcode"));
            fs.setStockBarcode(rs.getString("stcbarcode"));
            fs.setStockName(rs.getString("stcname"));
            fs.setReceiptNo(rs.getString("shfreceiptno"));
            fs.setAttendant(rs.getString("attendantname"));
            fs.getFuelSaleType().setName(rs.getString("flsname"));
            fs.getStock().setAvailableQuantity(rs.getBigDecimal("shfliter"));
            fs.setPrice(rs.getBigDecimal("shfprice"));
            fs.setDiscountTotal(rs.getBigDecimal("shfdiscountotal"));
            fs.setTotalMoney(rs.getBigDecimal("shftotalmoney"));
            fs.getCurrency().setId(rs.getInt("crnid"));
            fs.getCurrency().setCode(rs.getString("crncode"));

            fs.getStock().getUnit().setId(rs.getInt("gunid"));
            fs.getStock().getUnit().setName(rs.getString("gunname"));
            fs.getStock().getUnit().setSortName(rs.getString("gunsortname"));
            fs.getStock().getUnit().setUnitRounding(rs.getInt("gununitrounding"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fs;
    }

}
