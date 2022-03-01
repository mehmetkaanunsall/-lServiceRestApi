/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 25.12.2018 08:33:37
 */
package com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MovementReportBetweenWarehouseTakingsMapper implements RowMapper<MovementReportBetweenWarehouseTakings> {

    @Override
    public MovementReportBetweenWarehouseTakings mapRow(ResultSet rs, int i) throws SQLException {
        MovementReportBetweenWarehouseTakings obj = new MovementReportBetweenWarehouseTakings();
        obj.getStock().setId(rs.getInt("stistock_id"));
        obj.setLastPurchasePrice(rs.getBigDecimal("lastpurchaseprice"));
        obj.setLastSalePrice(rs.getBigDecimal("lastsaleprice"));
        obj.getLastPurchaseCurrency().setId(rs.getInt("lastpurchasecurrency_id"));
        obj.getLastSaleCurrency().setId(rs.getInt("lastsalecurrency_id"));

        try {
            obj.getStock().setName(rs.getString("stckname"));
            obj.getStock().setBarcode(rs.getString("stckbarcode"));
            obj.getStock().setCode(rs.getString("stckcode"));
            obj.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            obj.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            obj.getStock().getUnit().setSortName(rs.getString("untsortname"));
            obj.getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
            obj.setStockTaking1Quantity(rs.getBigDecimal("stirealquantity"));
            obj.setStockTaking2Quantity(rs.getBigDecimal("quantity2"));
            obj.setEntryAmount(rs.getBigDecimal("entryamount"));
            obj.setExitamount(rs.getBigDecimal("exitamount"));
            obj.setResult((obj.getStockTaking2Quantity().subtract(obj.getStockTaking1Quantity())).subtract((obj.getEntryAmount().subtract(obj.getExitamount()))));
            if (obj.getResult().compareTo(BigDecimal.ZERO) > 0) {
                obj.setResultStatus(1);
            } else if (obj.getResult().compareTo(BigDecimal.ZERO) < 0) {
                obj.setResultStatus(-1);
            } else {
                obj.setResultStatus(0);
            }
            obj.setPurchaseTaxRate(rs.getInt("purchasetaxgrouprate"));
            obj.setSaleTaxRate(rs.getInt("salestaxgrouprate"));
        } catch (Exception e) {
        }

        return obj;

    }

}
