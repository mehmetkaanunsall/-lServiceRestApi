/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.11.2018 03:21:03
 */
package com.mepsan.marwiz.finance.customeragreements.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CustomerAgreementItemMapper implements RowMapper<InvoiceItem> {

    @Override
    public InvoiceItem mapRow(ResultSet rs, int i) throws SQLException {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setId(rs.getInt("sid"));
        invoiceItem.getStock().setId(rs.getInt("slistock_id"));
        invoiceItem.getStock().setName(rs.getString("stckname"));
        invoiceItem.getStock().setIsService(rs.getBoolean("stckis_service"));
        invoiceItem.getStock().setCode(rs.getString("stckcode"));
        invoiceItem.getStock().setBarcode(rs.getString("stckbarcode"));
        invoiceItem.getUnit().setId(rs.getInt("guntid"));
        invoiceItem.getUnit().setName(rs.getString("guntname"));
        invoiceItem.getUnit().setSortName(rs.getString("guntsortname"));
        invoiceItem.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        invoiceItem.setUnitPrice(rs.getBigDecimal("sliunitprice"));
        invoiceItem.setQuantity(rs.getBigDecimal("sliquantity"));
        invoiceItem.setTotalPrice(rs.getBigDecimal("slitotalprice"));
        invoiceItem.setTaxRate(rs.getBigDecimal("slitaxrate"));
        invoiceItem.setTotalTax(rs.getBigDecimal("slitotaltax"));
        invoiceItem.setDiscountRate(rs.getBigDecimal("slidiscountrate"));
        invoiceItem.setDiscountPrice(rs.getBigDecimal("slidiscountprice"));
        invoiceItem.getCurrency().setId(rs.getInt("slicurrency"));
        invoiceItem.getCurrency().setCode(rs.getString("crrcode"));
        invoiceItem.getCurrency().setTag(rs.getString("crrdname"));
        invoiceItem.setExchangeRate(rs.getBigDecimal("sliexchangerate"));
        invoiceItem.setTotalMoney(rs.getBigDecimal("slitotalmoney"));
        invoiceItem.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("stckicurrentsaleprice"));
        invoiceItem.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("stckicurrentsalecurrency_id"));

        return invoiceItem;
    }

}
