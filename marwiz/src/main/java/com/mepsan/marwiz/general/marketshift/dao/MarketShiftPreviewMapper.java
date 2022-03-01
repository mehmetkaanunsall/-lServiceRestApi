/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 22.02.2019 09:24:28
 */
package com.mepsan.marwiz.general.marketshift.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftPreviewMapper implements RowMapper<MarketShiftPreview> {

    @Override
    public MarketShiftPreview mapRow(ResultSet rs, int i) throws SQLException {
        MarketShiftPreview mp = new MarketShiftPreview();
        ///SalesList
        try {
            mp.getStock().setId(rs.getInt("stckid"));
            mp.getStock().setName(rs.getString("stckname"));
            mp.getStock().setBarcode(rs.getString("stckbarcode"));
            mp.getStock().getUnit().setId(rs.getInt("guntunitrounding"));
            mp.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            mp.setSaleAmount(rs.getBigDecimal("totalsalecount"));
            mp.setReturnAmount(rs.getBigDecimal("totalreturncount"));
            mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
            mp.setReturnPrice(rs.getBigDecimal("totalreturnmoney"));
            mp.setTaxRate(rs.getBigDecimal("slitaxrate"));
            mp.setTotalTaxPrice(rs.getBigDecimal("totaltax"));
            mp.setUnitPrice(rs.getBigDecimal("sliunitprice"));
            mp.getCurrency().setId(rs.getInt("currency_id"));
        } catch (Exception exception) {
            //
        }

        //CurrencyTotal
        try {
            mp.getCurrency().setId(rs.getInt("slcurrency_id"));
            mp.getCurrency().setTag(rs.getString("crdname"));
            mp.getCurrency().setCode(rs.getString("crcode"));
            mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));

        } catch (Exception exception) {
            //
        }
        //Tax List
        try {
            mp.setTaxRate(rs.getBigDecimal("slitaxrate"));
            mp.setSaleAmount(rs.getBigDecimal("totalsalecount"));
            mp.setReturnAmount(rs.getBigDecimal("totalreturncount"));
            mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
            mp.setReturnPrice(rs.getBigDecimal("totalreturnmoney"));
            mp.setTotalTaxPrice(rs.getBigDecimal("totaltax"));
            mp.getCurrency().setId(rs.getInt("currency_id"));

        } catch (Exception exception) {
            //
        }
        //Deficit Employee
        try {
            mp.getAccount().setTitle(rs.getString("accountname") + " " + rs.getString("accountsurname"));
            mp.setInComing(rs.getBigDecimal("fazla"));
            mp.setOutGoing(rs.getBigDecimal("borc"));
        } catch (Exception exception) {
            //
        }
        //Deficit Income Expense
        try {
            mp.setDescription(rs.getString("name"));
            mp.setInComing(rs.getBigDecimal("income"));
            mp.setOutGoing(rs.getBigDecimal("expense"));
        } catch (Exception exception) {
            //
        }

        //Cash Delivery
        try {
            mp.setId(rs.getInt("shpid"));
            mp.getAccount().setId(rs.getInt("shpaccount_id"));
            mp.getAccount().setName(rs.getString("accname"));
            mp.getAccount().setTitle(rs.getString("accname") + " " + rs.getString("acctitle"));
            mp.setSalePrice(rs.getBigDecimal("accualprice"));
            mp.setExchangePrice(rs.getBigDecimal("shpaccualprice"));
            mp.getSafe().setId(rs.getInt("shpsafe_id"));
            mp.getSafe().setName(rs.getString("sfname"));
            mp.getSafe().setCode(rs.getString("sfcode"));
            mp.getSafe().getCurrency().setId(rs.getInt("shpcurrency_id"));
            mp.getSafe().getCurrency().setCode(rs.getString("crcode"));
            mp.setExchangeRate(rs.getBigDecimal("shpexchangerate"));

        } catch (Exception exception) {
            //
        }

        //Credit Card Delivery
        try {

            mp.setId(rs.getInt("shpid"));
            mp.getAccount().setId(rs.getInt("shpaccount_id"));
            mp.getAccount().setName(rs.getString("accname"));
            mp.getAccount().setTitle(rs.getString("accname") + " " + rs.getString("acctitle"));
            mp.setSalePrice(rs.getBigDecimal("accualprice"));
            mp.setExchangePrice(rs.getBigDecimal("shpaccualprice"));
            mp.getBankAccount().setId(rs.getInt("shpbankaccount_id"));
            mp.getBankAccount().setName(rs.getString("baname"));
            mp.getBankAccount().getBankBranch().getBank().setName(rs.getString("bnkname"));
            mp.getBankAccount().getBankBranch().getBank().setCode(rs.getString("bnkcode"));
            mp.getBankAccount().getCurrency().setId(rs.getInt("shpcurrency_id"));
            mp.getBankAccount().getCurrency().setCode(rs.getString("crcode"));
            mp.setExchangeRate(rs.getBigDecimal("shpexchangerate"));

        } catch (Exception exception) {
            //
        }

        //Credit Delivery
        try {
            mp.getAccount().setId(rs.getInt("id"));
            mp.getAccount().setTitle(rs.getString("name"));
            mp.setSalePrice(rs.getBigDecimal("price"));
            mp.setExchangePrice(rs.getBigDecimal("totalprice"));
            mp.setExchangeRate(rs.getBigDecimal("exchangerate"));
            mp.getCurrency().setId(rs.getInt("crdcurrency_id"));
            mp.getCurrency().setCode(rs.getString("crcode"));
        } catch (Exception exception) {
            //
        }
        //Shift General
        try {
            mp.getShift().setName(rs.getString("name"));
            mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
            mp.setReturnPrice(rs.getBigDecimal("totalreturnmoney"));
            mp.setCashPrice(rs.getBigDecimal("nakit"));
            mp.setCreditCardPrice(rs.getBigDecimal("banka"));
            mp.setCreditPrice(rs.getBigDecimal("veresiye"));
            mp.setOpenPrice(rs.getBigDecimal("acık"));
        } catch (Exception exception) {
            //
        }

        //Shift General
        try {
            mp.setSalePrice(rs.getBigDecimal("totalsalemoney"));
            mp.setReturnPrice(rs.getBigDecimal("totalreturnmoney"));
            mp.setCashPrice(rs.getBigDecimal("nakittahsilat"));
            mp.setCreditCardPrice(rs.getBigDecimal("bankatahsilat"));
            mp.setCreditPrice(rs.getBigDecimal("veresiye"));
            mp.setOpenPrice(rs.getBigDecimal("acık"));
            mp.setInComing(rs.getBigDecimal("gelir"));
            mp.setOutGoing(rs.getBigDecimal("gider"));
            mp.setEmployeInComing(rs.getBigDecimal("employeeincome"));
            mp.setEmployeOutGoing(rs.getBigDecimal("employeeexpense"));
            mp.setTotalOfInComing(rs.getBigDecimal("girentoplam"));
            mp.setTotalOfOutGoing(rs.getBigDecimal("cıkantoplam"));
        } catch (Exception exception) {
            //
        }
        //Kategori Satış Listesi
        try {
            mp.setPreviousSalePrice(rs.getBigDecimal("previoussaletotal"));
            mp.setPreviousSaleAmount(rs.getBigDecimal("previoussalequantity"));
            mp.setTotalOfInComing(rs.getBigDecimal("girismiktar"));
            mp.setTotalOfOutGoing(rs.getBigDecimal("cikismiktar"));
            mp.setSalePrice(rs.getBigDecimal("salestotal"));
            mp.setSaleAmount(rs.getBigDecimal("salesquantity"));
            mp.setDescription(rs.getString("category"));
            mp.setPreviousAmount(rs.getBigDecimal("previousamountbeforeshift"));
            mp.setPreviousPrice(rs.getBigDecimal("previouspricebeforeshift"));
            mp.setRemainingPrice(rs.getBigDecimal("remainingPrice").add(rs.getBigDecimal("previouspricebeforeshift")));
            mp.setRemainingQuantity(rs.getBigDecimal("previousamountbeforeshift").add(rs.getBigDecimal("girismiktar")).subtract(rs.getBigDecimal("cikismiktar")));

        } catch (Exception e) {
        }
        try {
            mp.setCategoryId(rs.getInt("categoryid"));
        } catch (Exception e) {
        }

        //Account Group List
        try {

            mp.getAccount().setId(rs.getInt("slaccount_id"));
            mp.getAccount().setName(rs.getString("accname"));
            mp.getAccount().setTitle(rs.getString("acctitle"));
            mp.setSalePrice(rs.getBigDecimal("sltotalmoney"));
            mp.getCurrency().setId(rs.getInt("slcurrency_id"));
            mp.getCurrency().setCode(rs.getString("crrcode"));
            
        } catch (Exception e) {
        }
        //Safe Transfer List
        try {
            mp.getSafe().setId(rs.getInt("sfid"));
            mp.getSafe().setName(rs.getString("sfname"));
            mp.getSafe().setBalance(rs.getBigDecimal("sscbalance"));
            mp.getSafe().getCurrency().setId(rs.getInt("sfcurrency_id"));
            mp.getSafe().getCurrency().setCode(rs.getString("crrcode"));
            
        } catch (Exception e) {
        }
        
        
        
        return mp;

    }

}
