/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 03:56:04
 */
package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WarehouseMovementMapper implements RowMapper<WarehouseMovement> {

    @Override
    public WarehouseMovement mapRow(ResultSet rs, int i) throws SQLException {
        WarehouseMovement warehouseMovement = new WarehouseMovement();
        warehouseMovement.setId(rs.getInt("whmid"));
        warehouseMovement.getStock().setId(rs.getInt("whmstock_id"));
        warehouseMovement.getStock().setName(rs.getString("stckname"));
        warehouseMovement.getStock().setCode(rs.getString("stckcode"));
        warehouseMovement.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        warehouseMovement.getStock().setBarcode(rs.getString("stckbarcode"));
        warehouseMovement.getStock().getUnit().setName(rs.getString("guntname"));
        warehouseMovement.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        warehouseMovement.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        warehouseMovement.setQuantity(rs.getBigDecimal("whmquantity"));
        try {
            warehouseMovement.getStock().getUnit().setId(rs.getInt("guntid"));
        } catch (Exception e) {
        }

        try {
            warehouseMovement.getWasteItemInfo().setDescription(rs.getString("wsidescription"));
            warehouseMovement.getWasteItemInfo().setExpirationDate(rs.getTimestamp("wsiexpirationdate"));
            warehouseMovement.getWasteItemInfo().setCurrentPurchasePrice(rs.getBigDecimal("wsicurrentprice"));
            warehouseMovement.getWasteItemInfo().getCurrency().setId(rs.getInt("wsicurrency_id"));
            warehouseMovement.getWasteItemInfo().setTaxRate(rs.getBigDecimal("wsitaxrate"));
            warehouseMovement.getWasteItemInfo().getWasteReason().setId(rs.getInt("wastereasonid"));
            warehouseMovement.getWasteItemInfo().setTotalMoney(rs.getBigDecimal("wsitotalmoney"));
            warehouseMovement.getWasteItemInfo().getUnit().setId(rs.getInt("wsiunit_id"));
            warehouseMovement.getWasteItemInfo().getUnit().setName(rs.getString("wguntname"));
            warehouseMovement.getWasteItemInfo().getUnit().setSortName(rs.getString("wguntsortname"));
            warehouseMovement.getWasteItemInfo().getUnit().setUnitRounding(rs.getInt("wguntunitrounding"));
            warehouseMovement.getWasteItemInfo().setAlternativeUnitQuantity(rs.getBigDecimal("wsialternativeunitquantity"));
            warehouseMovement.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
            warehouseMovement.getStock().setTaxRate(rs.getBigDecimal("taxrate"));
        } catch (Exception e) {
        }
        try {
            warehouseMovement.setExitingWarehouseCurrentAmount(rs.getBigDecimal("iwicquantity"));
            warehouseMovement.setEntryingWarehouseCurrentAmount(rs.getBigDecimal("iwigquantity"));
        } catch (Exception e) {
        }
        try {
            warehouseMovement.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            warehouseMovement.getStock().getStockInfo().setIsMinusStockLevel(rs.getBoolean("siis_minusstocklevel"));
        } catch (Exception e) {
        }
        try {
            warehouseMovement.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            warehouseMovement.getStock().getStockInfo().setBalance(rs.getBigDecimal("sibalance"));

        } catch (Exception e) {
        }

        return warehouseMovement;
    }

}
