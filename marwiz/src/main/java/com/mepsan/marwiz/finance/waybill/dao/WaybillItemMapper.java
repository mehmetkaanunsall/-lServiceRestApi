/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WaybillItemMapper implements RowMapper<WaybillItem> {

    private Waybill waybill;

    public WaybillItemMapper(Waybill waybill) {
        this.waybill = waybill;
    }

    public WaybillItemMapper() {
    }

    @Override
    public WaybillItem mapRow(ResultSet rs, int i) throws SQLException {
        WaybillItem waybillItem = new WaybillItem();

        if (waybill != null) {
            waybillItem.setWaybill(waybill);
        }
        waybillItem.getStock().setId(rs.getInt("wbistock_id"));
        waybillItem.getStock().setName(rs.getString("stckname"));

        try {
            waybillItem.getStock().setBarcode(rs.getString("stckbarcode"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getStock().getUnit().setId(rs.getInt("guntid"));
        } catch (Exception e) {
        }

        try {
            waybillItem.setId(rs.getInt("wbiid"));
            waybillItem.getStock().getUnit().setName(rs.getString("guntname"));
            waybillItem.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            waybillItem.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            waybillItem.setRemainingQuantity(rs.getBigDecimal("wbiremainingquantity"));
        } catch (Exception e) {
        }
        try {
            waybillItem.setQuantity(rs.getBigDecimal("wbiquantity"));
        } catch (Exception e) {
        }

        try {
            waybillItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            waybillItem.getStock().setCode(rs.getString("stckcode"));

        } catch (Exception e) {
        }

        try {
            waybillItem.setDescription(rs.getString("wbidescription"));
        } catch (Exception e) {
        }

        try {
            waybillItem.getWaybill().setWaybillDate(rs.getTimestamp("wbwaybilldate"));

            waybillItem.getWaybill().setId(rs.getInt("wbid"));
            waybillItem.getWaybill().setDocumentNumber(rs.getString("wbdocumentnumber"));
            waybillItem.getWaybill().setDocumentSerial(rs.getString("wbdocumentserial"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            waybillItem.getStock().getStockInfo().setBalance(rs.getBigDecimal("sibalance"));
            waybillItem.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            waybillItem.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
        } catch (Exception e) {
        }

        try {
            waybillItem.getStock().getStockInfo().setIsMinusStockLevel(rs.getBoolean("siis_minusstocklevel"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getTaxGroup().setRate(rs.getBigDecimal("taxrate"));
            waybillItem.getPriceListItem().setPrice(rs.getBigDecimal("pricelistprice"));
            waybillItem.getPriceListItem().getCurrency().setId(rs.getInt("pricelistcurrency"));
            waybillItem.getPriceListItem().setIs_taxIncluded(rs.getBoolean("pricelisttaxincluded"));
            waybillItem.getPriceListItem().getCurrency().setTag(rs.getString("pricelistcurrencyname"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getStock().getStockInfo().setPurchaseRecommendedPrice(rs.getBigDecimal("sipurchaserecommendedprice"));
            waybillItem.getStock().getStockInfo().getPurchaseCurrency().setId(rs.getInt("sipurchasecurrency_id"));
            waybillItem.getStock().getStockInfo().getPurchaseCurrency().setTag(rs.getString("cryd1name"));
        } catch (Exception e) {
        }

        try {
            waybillItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        } catch (Exception e) {
        }

        try {
            waybillItem.setOrderItemIds(rs.getString("orderitemids"));
            waybillItem.setOrderItemQuantitys(rs.getString("orderitemquantitys"));
            waybillItem.setFirstOrderItemIds(rs.getString("orderitemids"));
            waybillItem.setFirstOrderItemQuantitys(rs.getString("orderitemquantitys"));
            waybillItem.setControlQuantity(rs.getBigDecimal("controlquantity"));
        } catch (Exception e) {
        }

        try {
            waybillItem.getWarehouse().setId(rs.getInt("whid"));
            waybillItem.getWarehouse().setName(rs.getString("whname"));
            waybillItem.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
        } catch (Exception e) {
        }
        try {
            waybillItem.getStock().setIsService(rs.getBoolean("stckis_service"));
        } catch (Exception e) {
        }

        
        try {
            waybillItem.getStock().getStockInfo().setIsDelist(rs.getBoolean("siis_delist"));
        } catch (Exception e) {
        }
        return waybillItem;
    }

}
