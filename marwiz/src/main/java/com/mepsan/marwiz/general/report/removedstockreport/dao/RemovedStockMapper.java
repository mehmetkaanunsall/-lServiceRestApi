/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 14:20:11
 */
package com.mepsan.marwiz.general.report.removedstockreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.log.RemovedStock;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;

public class RemovedStockMapper implements RowMapper<RemovedStock> {

    @Override
    public RemovedStock mapRow(ResultSet rs, int i) throws SQLException {
        RemovedStock removedStock = new RemovedStock();
        removedStock.setId(rs.getInt("rsid"));
        removedStock.getUserData().setId(rs.getInt("rsuserdata_id"));
        removedStock.getUserData().setName(rs.getString("usname"));
        removedStock.getUserData().setSurname(rs.getString("ussurname"));
        removedStock.getShift().setId(rs.getInt("rsshift_id"));
        removedStock.getCurrency().setId(rs.getInt("rscurrency_id"));
        removedStock.getStock().setId(rs.getInt("rsstock_id"));
        removedStock.getStock().setName(rs.getString("stckname"));
        removedStock.getStock().setBarcode(rs.getString("stckbarcode"));
        removedStock.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        removedStock.getStock().getUnit().setSortName(rs.getString("untsortname"));
        removedStock.getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
        removedStock.setOldValue(rs.getBigDecimal("rsoldvalue"));
        removedStock.setNewValue(rs.getBigDecimal("rsnewvalue"));
        removedStock.setProcessDate(rs.getTimestamp("rsprocessdate"));
        removedStock.setUnitPrice(rs.getBigDecimal("rsunitprice"));
        removedStock.setRemovedTotalPrice(rs.getBigDecimal("rsremovedtotalprice"));
        removedStock.setRemovedValue(rs.getBigDecimal("rsremovedvalue"));

        try {
            removedStock.getShift().setShiftNo(rs.getString("shfshiftno"));
            removedStock.getCurrency().setTag(rs.getString("crname"));
            removedStock.getCurrency().setCode(rs.getString("crcode"));
        } catch (Exception e) {
        }

        try {
            removedStock.getStock().setCode(rs.getString("stckcode"));
            removedStock.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            removedStock.setCategory(rs.getString("category"));
            removedStock.setCategory(StaticMethods.findCategories(removedStock.getCategory()));
            removedStock.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
            removedStock.getStock().getBrand().setName(rs.getString("brname"));
            removedStock.getStock().getSupplier().setId(rs.getInt("supplier_id"));
            removedStock.getStock().getSupplier().setName(rs.getString("accname"));
            removedStock.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            removedStock.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        } catch (Exception e) {
        }
        return removedStock;
    }

}
