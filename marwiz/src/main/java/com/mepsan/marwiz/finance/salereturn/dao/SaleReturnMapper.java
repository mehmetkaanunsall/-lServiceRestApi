/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.07.2018 17:53:38
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;

public class SaleReturnMapper implements RowMapper<SaleReturnReport> {

    @Override
    public SaleReturnReport mapRow(ResultSet rs, int i) throws SQLException {
        SaleReturnReport saleReturn = new SaleReturnReport();
        try {
            saleReturn.getSales().getReceipt().setId(rs.getInt("rcidd"));
            saleReturn.getSales().getReceipt().setReceiptNo(rs.getString("rcreceiptno"));
            saleReturn.getSales().getReceipt().setProcessDate(rs.getTimestamp("rcprocessdate"));
            saleReturn.getSales().getReceipt().setIsReturn(rs.getBoolean("rcis_return"));
            saleReturn.getSales().getAccount().setId(rs.getInt("accid"));
            saleReturn.getSales().getAccount().setName(rs.getString("accname"));
            saleReturn.getSales().setId(rs.getInt("slid"));
            saleReturn.getSales().getCurrency().setId(rs.getInt("slcurrency_id"));
            saleReturn.getSales().setTotalMoney(rs.getBigDecimal("sltotalmoney"));
            saleReturn.getSales().setTotalPrice(rs.getBigDecimal("sltotalprice"));
            saleReturn.getSales().setTotalTax(rs.getBigDecimal("sltotaltax"));
            saleReturn.getSales().setTotalDiscount(rs.getBigDecimal("sltotaldiscount"));
            saleReturn.getSales().getUser().setName(rs.getString("us1name"));
            saleReturn.getSales().getUser().setSurname(rs.getString("us1surname"));
            saleReturn.setIsUsedStock(rs.getBoolean("isusedstock"));

        } catch (Exception e) {
        }
        try {
            saleReturn.getSales().setId(rs.getInt("slid"));
            saleReturn.getSales().getPointOfSale().setId(rs.getInt("slpointofsale_id"));
            saleReturn.getSales().getPointOfSale().getSafe().setId(rs.getInt("possafe_id"));
            saleReturn.getSales().getPointOfSale().setMacAddress(rs.getString("slposmacaddress"));
            saleReturn.getSales().getPointOfSale().getSafe().getCurrency().setId(rs.getInt("sfcurrency_id"));
            saleReturn.getSales().getReceipt().getWarehouse().setId(rs.getInt("wrwarehouse_id"));
            saleReturn.getSales().getPointOfSale().getWareHouse().setId(rs.getInt("poswarehouse_id"));
            saleReturn.getSales().getReceipt().setId(rs.getInt("rcidd"));
            saleReturn.getSales().getReceipt().setReceiptNo(rs.getString("rcreceiptno"));
            saleReturn.getSales().getReceipt().setProcessDate(rs.getTimestamp("rcprocessdate"));
            saleReturn.getSales().getReceipt().setIsReturn(rs.getBoolean("rcis_return"));
            saleReturn.getSales().getAccount().setId(rs.getInt("accid"));
            saleReturn.getSales().getAccount().setName(rs.getString("accname"));
            saleReturn.getSales().getCurrency().setId(rs.getInt("slcurrency_id"));
            saleReturn.getSales().setTotalDiscount(rs.getBigDecimal("sltotaldiscount"));
            saleReturn.getSales().setTotalMoney(rs.getBigDecimal("sltotalmoney"));
            saleReturn.getSales().setTotalPrice(rs.getBigDecimal("totalprice"));
            saleReturn.getSales().setTotalTax(rs.getBigDecimal("sltotaltax"));
            saleReturn.getSaleItem().setId(rs.getInt("sliid"));
            saleReturn.getSaleItem().getUnit().setId(rs.getInt("sliunit_id"));
            saleReturn.getSaleItem().setUnitPrice(rs.getBigDecimal("sliunitprice"));
            saleReturn.getSaleItem().setTotalTax(rs.getBigDecimal("slitotaltax"));
            saleReturn.getSaleItem().setDiscountPrice(rs.getBigDecimal("slidiscountprice"));
            saleReturn.getSaleItem().setDiscountRate(rs.getBigDecimal("slidiscountrate"));
            saleReturn.getSaleItem().getCurrency().setId(rs.getInt("slicurrency_id"));
            saleReturn.getSaleItem().setExchangeRate(rs.getBigDecimal("sliexchangerate"));
            saleReturn.getSaleItem().setTotalPrice(rs.getBigDecimal("slitotalprice"));
            saleReturn.getSaleItem().setTotalMoney(rs.getBigDecimal("slitotalmoney"));
            saleReturn.getSaleItem().setIsManagerDiscount(rs.getBoolean("sliis_managerdiscount"));
            saleReturn.getSaleItem().getManagerUserData().setId(rs.getInt("slimanageruserdata_id"));
            saleReturn.getSaleItem().getStock().setId(rs.getInt("slistock_id"));
            saleReturn.getSaleItem().getStock().setName(rs.getString("stckname"));
            saleReturn.getSaleItem().getStock().getUnit().setId(rs.getInt("stckunit_id"));
            saleReturn.getSaleItem().getStock().getUnit().setName(rs.getString("untname"));
            saleReturn.getSaleItem().getStock().getUnit().setSortName(rs.getString("untsortname"));
            saleReturn.getSaleItem().getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
            saleReturn.getSaleItem().setRecommendedPrice(rs.getBigDecimal("slirecommendedprice"));
            saleReturn.getSaleItem().setProcessDate(rs.getTimestamp("sliprocessdate"));
            saleReturn.getSaleItem().setQuantity(rs.getBigDecimal("sliquantity"));
            saleReturn.getSaleItem().setTaxRate(rs.getBigDecimal("slitaxrate"));
            saleReturn.getSaleItem().setTotalMoney(rs.getBigDecimal("slitotalmoney"));
            saleReturn.getSaleItem().setIsManagerDiscount(rs.getBoolean("sliis_managerdiscount"));
            saleReturn.getSaleItem().getManagerUserData().setId(rs.getInt("slimanageruserdata_id"));
            saleReturn.getSaleItem().getManagerUserData().setName(rs.getString("us1name"));
            saleReturn.getSaleItem().getManagerUserData().setSurname(rs.getString("us1surname"));
            saleReturn.getSaleItem().setTotalPrice(rs.getBigDecimal("slitotalprice"));
            saleReturn.setIsUsedStock(rs.getBoolean("isusedstock"));

        } catch (Exception ex) {
        }
        try {
            saleReturn.getType().setId(rs.getInt("slptype_idd"));
            saleReturn.getType().setTag(rs.getString("typdname"));
            saleReturn.setPrice(rs.getBigDecimal("slpprice"));
            saleReturn.setExchangeRate(rs.getBigDecimal("slpexchangerate"));
            saleReturn.getCurrency().setId(rs.getInt("slpcurrency_id"));
            saleReturn.setTotalCreditPayment(rs.getBigDecimal("totalcreditpaymentprice"));
            saleReturn.getBankAccount().setId(rs.getInt("baid"));
            saleReturn.getBankAccount().setName(rs.getString("baname"));

        } catch (Exception ex) {
        }

        try {
            saleReturn.getCreditPayment().getCredit().setId(rs.getInt("slpcredit_idd"));
            saleReturn.getCreditPayment().getType().setId(rs.getInt("crptype_id"));
            saleReturn.getCreditPayment().getType().setTag(rs.getString("typdname"));
            saleReturn.getCreditPayment().setPrice(rs.getBigDecimal("crpprice"));
            saleReturn.getCreditPayment().getCurrency().setId(rs.getInt("crpcurrency_id"));
            saleReturn.getCurrency().setId(rs.getInt("slpcurrency_id"));
            saleReturn.getCreditPayment().getCredit().setMoney(rs.getBigDecimal("crmoney"));
            saleReturn.getCreditPayment().getCredit().setRemainingMoney(rs.getBigDecimal("crremainingmoney"));
            saleReturn.getCreditPayment().getCredit().setIsPaid(rs.getBoolean("cris_paid"));
            saleReturn.getCreditPayment().getCredit().getAccount().setId(rs.getInt("accid"));
            saleReturn.getCreditPayment().getCredit().getAccount().setName(rs.getString("accname"));
            saleReturn.getCreditPayment().getBankAccount().setId(rs.getInt("baid"));
            saleReturn.getCreditPayment().getBankAccount().setName(rs.getString("baname"));
            saleReturn.getCreditPayment().getCredit().setDueDate(rs.getTimestamp("crduedate"));

        } catch (Exception ex) {
        }
        try {
            saleReturn.getCreditPayment().setPrice(rs.getBigDecimal("crppricee"));
            saleReturn.getCreditPayment().setExchangeRate(rs.getBigDecimal("crpexchangerate"));
            saleReturn.getCreditPayment().getType().setId(rs.getInt("crptype_id"));
            saleReturn.getCreditPayment().getType().setTag(rs.getString("typdname"));
            saleReturn.getCreditPayment().getCurrency().setId(rs.getInt("crpcurrency_id"));
            saleReturn.getCreditPayment().getBankAccount().setId(rs.getInt("baid"));
            saleReturn.getCreditPayment().getBankAccount().setName(rs.getString("baname"));

        } catch (Exception ex) {
        }
                return saleReturn;
    }

}
