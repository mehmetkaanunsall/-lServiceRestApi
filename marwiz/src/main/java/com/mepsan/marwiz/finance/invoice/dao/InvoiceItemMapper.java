/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2017 14:10:58
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InvoiceItemMapper implements RowMapper<InvoiceItem> {

    @Override
    public InvoiceItem mapRow(ResultSet rs, int i) throws SQLException {
        InvoiceItem invoiceStock = new InvoiceItem();

        try {
            invoiceStock.setId(rs.getInt("iviid"));
            invoiceStock.setIsService(rs.getBoolean("stckisservice"));
            invoiceStock.setIsDiscountRate(rs.getBoolean("iviisdiscountrate"));
            invoiceStock.setTaxRate(rs.getBigDecimal("txgrate"));
            invoiceStock.getInvoice().setIsPurchase(rs.getBoolean("ivispurchase"));
            invoiceStock.setExchangeRate(rs.getBigDecimal("inviexchangerate"));

            invoiceStock.getInvoice().getCurrency().setId(rs.getInt("ivcurrency_id"));
            invoiceStock.getInvoice().getCurrency().setCode(rs.getString("crydname"));

            if (invoiceStock.getInvoice().isIsPurchase()) {
                invoiceStock.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("stckipurchaseprice"));
            } else {
                invoiceStock.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("stckirecommendedprice"));
            }
        } catch (Exception e) {
        }

        try {
            invoiceStock.setId(rs.getInt("inviid"));
            invoiceStock.getInvoice().setId(rs.getInt("inviinvoiceid"));
            invoiceStock.setIsService(rs.getBoolean("inviis_service"));

            invoiceStock.setTotalPrice(rs.getBigDecimal("invitotalprice"));
            invoiceStock.setTotalMoney(rs.getBigDecimal("invitotalmoney"));
            invoiceStock.setDescription(rs.getString("invidescription"));
            invoiceStock.setExchangeRate(rs.getBigDecimal("inviexchangerate"));
            invoiceStock.getCurrency().setId(rs.getInt("invicurrency_id"));

            invoiceStock.setTaxRate(rs.getBigDecimal("invitaxrate"));
            invoiceStock.setTotalTax(rs.getBigDecimal("invitotaltax"));

            invoiceStock.setUnitPrice(rs.getBigDecimal("inviunitprice"));
            invoiceStock.setTotalPrice(rs.getBigDecimal("invitotalprice"));

            invoiceStock.getCurrency().setCode(rs.getString("crrcode"));
            invoiceStock.getCurrency().setTag(rs.getString("crrdname"));

            invoiceStock.setDiscountRate(rs.getBigDecimal("invidiscountrate"));
            invoiceStock.setDiscountPrice(rs.getBigDecimal("invidiscountprice"));
            invoiceStock.setIsDiscountRate(rs.getBoolean("inviis_discountrate"));

            invoiceStock.getStock().getStockInfo().setIsFuel(rs.getBoolean("stckiis_fuel"));
            invoiceStock.getStock().getStockInfo().setPurchaseControlDate(rs.getTimestamp("stckipurchasecontroldate"));
            invoiceStock.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("stckicurrentsalecurrency_id"));
            invoiceStock.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("stckicurrentsaleprice"));
            invoiceStock.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("stckisalemandatoryprice"));
            invoiceStock.getStock().getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("stckisalemandatorycurrency_id"));
            invoiceStock.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("stckimaxstocklevel"));
            invoiceStock.getStock().getStockInfo().setBalance(rs.getBigDecimal("stckibalance"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.setQuantity(rs.getBigDecimal("inviquantity"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            invoiceStock.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            invoiceStock.getStock().getStockInfo().setBalance(rs.getBigDecimal("sibalance"));
        } catch (Exception e) {
        }
        try {
            invoiceStock.getStock().getStockInfo().setIsFuel(rs.getBoolean("siis_fuel"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.getStock().setId(rs.getInt("invistock_id"));
            invoiceStock.getStock().setName(rs.getString("stckname"));
            invoiceStock.getStock().setCode(rs.getString("stckcode"));
            invoiceStock.getStock().setBarcode(rs.getString("stckbarcode"));
            invoiceStock.getUnit().setId(rs.getInt("guntid"));
            invoiceStock.getUnit().setSortName(rs.getString("guntsortname"));
            invoiceStock.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            invoiceStock.getUnit().setName(rs.getString("guntname"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.getStock().setBarcode(rs.getString("stckbarcode"));
            invoiceStock.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            invoiceStock.getStock().setCenterstock_id(rs.getInt("stckcenterstock_id"));
            invoiceStock.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));
            invoiceStock.getStock().getStockInfo().getCurrency().setId(rs.getInt("sicurrency_id"));
            invoiceStock.getStock().getStockInfo().setMinProfitRate(rs.getBigDecimal("siminprofitrate"));
            invoiceStock.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
            invoiceStock.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
            invoiceStock.getStock().getStockInfo().setPurchaseControlDate(rs.getDate("sipurchasecontroldate"));
            invoiceStock.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
            invoiceStock.getUnit().setName(rs.getString("guntname"));
            PriceListItem listItem = new PriceListItem();
            listItem.setPrice(rs.getBigDecimal("pliprice"));
            listItem.getCurrency().setId(rs.getInt("plicurrrency"));
            invoiceStock.setPriceListItem(new PriceListItem());
            invoiceStock.setPriceListItem(listItem);
            invoiceStock.getUnit().setCenterunit_id(rs.getInt("guntcenterunit_id"));
            invoiceStock.setQuantity(rs.getBigDecimal("inviquantity"));
            invoiceStock.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            invoiceStock.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            invoiceStock.getStock().getStockInfo().setBalance(rs.getBigDecimal("sibalance"));

        } catch (Exception e) {
        }

        try {
            invoiceStock.setWaybillItemIds(rs.getString("wicwaybillitem_id"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.setWaybillItemQuantity(rs.getString("wiquantity"));
            invoiceStock.setWaybillItemQuantitys(rs.getString("wicwaybillitem_quantity"));
        } catch (Exception e) {
        }

        try {
            UserData userData = new UserData();
            userData.setId(rs.getInt("invic_id"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));

            invoiceStock.setUserCreated(userData);
            invoiceStock.setDateCreated(rs.getTimestamp("invic_time"));
        } catch (Exception e) {
        }

        invoiceStock.setIsTaxIncluded(false);
        if (invoiceStock.getUnitPrice() != null && invoiceStock.getUnitPrice().doubleValue() > 0
                && invoiceStock.getTaxRate() != null && invoiceStock.getTaxRate().doubleValue() > 0) {
            BigDecimal x = BigDecimal.ONE.add(invoiceStock.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
            invoiceStock.setUnitPrice(invoiceStock.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN));
        }
        try {
            invoiceStock.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("stckicurrentpurchaseprice"));
            invoiceStock.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("stckicurrentpurchasecurrency_id"));
            invoiceStock.getStock().setId(rs.getInt("stckistock_id"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("stckicurrentsaleprice"));
            invoiceStock.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("stckicurrentsalecurrency_id"));

        } catch (Exception e) {
        }

        try {
            invoiceStock.setProfitPercentage(rs.getBigDecimal("inviprofitrate"));
            invoiceStock.setProfitPrice(rs.getBigDecimal("inviprofitprice"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.setIsDiscountRate2(rs.getBoolean("inviis_discountrate2"));
            invoiceStock.setDiscountRate2(rs.getBigDecimal("invidiscountrate2"));
            invoiceStock.setDiscountPrice2(rs.getBigDecimal("invidiscountprice2"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.getStock().getStockInfo().setIsMinusStockLevel(rs.getBoolean("siis_minusstocklevel"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.setIsFree(rs.getBoolean("inviis_free"));
        } catch (Exception e) {
        }
        try {
            invoiceStock.setPriceDifferentTotalMoney(rs.getBigDecimal("invidifferenttotalmoney"));
        } catch (Exception e) {
        }
        try {
            invoiceStock.setPriceDifferentInvoiceItem(new InvoiceItem());
            invoiceStock.getPriceDifferentInvoiceItem().setId(rs.getInt("invidifferentinvoiceitem_id"));
        } catch (Exception e) {
        }
        try {
            invoiceStock.getStock().getStockInfo().setPurchaseRecommendedPrice(rs.getBigDecimal("sipurchaserecommendedprice"));
            invoiceStock.getStock().getStockInfo().getPurchaseCurrency().setId(rs.getInt("sipurchasecurrency_id"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        } catch (Exception e) {
        }

        try {
            invoiceStock.setOrderItemIds(rs.getString("orderitemids"));
            invoiceStock.setOrderItemQuantitys(rs.getString("orderitemquantitys"));
            invoiceStock.setFirstOrderItemIds(rs.getString("orderitemids"));
            invoiceStock.setFirstOrderItemQuantitys(rs.getString("orderitemquantitys"));
            invoiceStock.setControlQuantity(rs.getBigDecimal("controlquantity"));
        } catch (Exception e) {
        }
        try {
            invoiceStock.getWarehouse().setId(rs.getInt("whid"));
            invoiceStock.getWarehouse().setName(rs.getString("whname"));

        } catch (Exception e) {
        }
        try {
            invoiceStock.getStock().getStockInfo().setIsDelist(rs.getBoolean("siis_delist"));
        } catch (Exception e) {
        }

        //excel için 
        try {
            invoiceStock.getStock().setBarcode(rs.getString("barcode"));
            invoiceStock.getStock().setId(rs.getInt("stock_id"));
            invoiceStock.getStock().setAlternativeBarcodes(rs.getString("sabbarcode"));
            invoiceStock.getStock().setAlternativeQuantity(rs.getBigDecimal("sabquantity"));
            invoiceStock.getStock().getUnit().setId(rs.getInt("unit_id"));
            invoiceStock.getStock().getUnit().setSortName(rs.getString("unit_sortname"));
            invoiceStock.getStock().getUnit().setUnitRounding(rs.getInt("unitrounding"));
            invoiceStock.getStock().getStockInfo().setIsFuel(rs.getBoolean("is_fuel"));
            invoiceStock.getStock().setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            invoiceStock.getStock().getStockInfo().setMaxStockLevel(rs.getBigDecimal("maxstocklevel"));
            invoiceStock.getStock().getStockInfo().setBalance(rs.getBigDecimal("balance"));
            invoiceStock.getStock().getStockInfo().setIsMinusStockLevel(rs.getBoolean("is_minusstocklevel"));
            invoiceStock.getStock().getStockInfo().setPurchaseRecommendedPrice(rs.getBigDecimal("purchaserecommendedprice"));
            invoiceStock.getStock().getStockInfo().getPurchaseCurrency().setId(rs.getInt("purchasecurrency_id"));
            invoiceStock.getStock().getStockInfo().setIsDelist(rs.getBoolean("is_delist"));
            invoiceStock.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("currentpurchasecurrency_id"));
            invoiceStock.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("currentpurchaseprice"));
            invoiceStock.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("currentsalecurrency_id"));
            invoiceStock.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("currentsaleprice"));
            invoiceStock.getStock().setPurchaseKdv(rs.getBigDecimal("purchasekdv"));
            invoiceStock.getStock().getStockInfo().setTempCurrentSalePrice(rs.getBigDecimal("tempsicurrentsaleprice"));
            invoiceStock.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("salemandatoryprice"));
            invoiceStock.getStock().getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("salemandatorycurrency_id"));
            invoiceStock.setIsFuelWarehouse(rs.getInt("isfuelwarehouse"));
            invoiceStock.setIsFuelWarehouseItem(rs.getInt("isfuelwarehouseitem"));
            invoiceStock.getTaxGroup().setId(rs.getInt("txgid"));
            invoiceStock.getTaxGroup().setRate(rs.getBigDecimal("txgrate"));
            invoiceStock.setQuantity(rs.getBigDecimal("quantity"));
            invoiceStock.setUnitPrice(rs.getBigDecimal("unitprice"));
            invoiceStock.setIsTaxIncluded(rs.getBoolean("is_taxincluded"));
            invoiceStock.setId(rs.getInt("invoiceitemid"));
            invoiceStock.setIsService(rs.getBoolean("is_service"));
            invoiceStock.setIsDiscountRate(rs.getBoolean("is_discountrate"));
            invoiceStock.setRowId(rs.getInt("row_id"));

        } catch (Exception e) {
        }

        return invoiceStock;
    }

}
