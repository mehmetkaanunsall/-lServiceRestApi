/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class OrderItemMapper implements RowMapper<OrderItem>{

    @Override
    public OrderItem mapRow(ResultSet rs, int i) throws SQLException {
        OrderItem order=new OrderItem();

        try{

            order.setId(rs.getInt("odiid"));
            order.getStock().setId(rs.getInt("odistockid"));
            order.getStock().setName(rs.getString("stckname"));
            order.getStock().setBarcode(rs.getString("stckbarcode"));
            order.getUnit().setId(rs.getInt("odiunitid"));
            order.getUnit().setName(rs.getString("guntname"));
            order.getUnit().setSortName(rs.getString("guntsortname"));
            order.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
            order.setBoxQuantity(rs.getBigDecimal("odiboxquantity"));
            order.setShelfQuantity(rs.getBigDecimal("odishelfquantity"));
            order.setRequiredWarehouseStock(rs.getBigDecimal("requiredwarehousestockquantity"));
            order.setRequiredTotalStock(rs.getBigDecimal("totalstockquantity"));
            order.setWarehouseQuantity(rs.getBigDecimal("odiwarehousequantity"));
            order.setLastTwoMonthsSales(rs.getBigDecimal("oditwomonthsale"));
            order.setAverageWeeklyOrderQuantity(rs.getBigDecimal("averageweeklyorderquantity"));
            order.setStockEnoughDay(rs.getBigDecimal("stockenoughdays"));
            order.setOrderCalculationSupplement(rs.getBigDecimal("ordercalculation"));
            order.setMaxQuantity(rs.getBigDecimal("odimaximumquantity"));
            order.setMinQuantity(rs.getBigDecimal("odiminimumquantity"));
            order.setQuantity(rs.getBigDecimal("odiquantity"));
            order.setRecommendedPrice(rs.getBigDecimal("odirecommendedprice"));
            order.getCurrency().setId(rs.getInt("odicurrency_id"));
            order.setMinFactorValue(rs.getBigDecimal("odiminfactorvalue"));
            order.setMaxFactorValue(rs.getBigDecimal("odimaxfactorvalue"));
            order.setWarehouseStockDivisorValue(rs.getBigDecimal("odiwarehousestockdivisorvalue"));
        } catch (Exception e) {
        }
        try {
            order.setTwoMonthSaleActiveDay(rs.getInt("oditwomonthsaleactiveday"));
            order.setAverageWeeklyOrderQuantityForDaysCount(rs.getInt("averageweeklyorderquantityfordayscount"));
        } catch (Exception e) {
        }

        try {
            order.getOrder().setId(rs.getInt("odid"));
            order.getOrder().getAccount().setName(rs.getString("accname"));
            order.getOrder().getAccount().setTitle(rs.getString("acctitle"));
            order.getOrder().getdNumber().setId(rs.getInt("oddocumentnumber_id"));
            order.getOrder().setDocumentNumber(rs.getString("oddocumentnumber"));
            order.getOrder().setOrderDate(rs.getTimestamp("odorderdate"));

            if (order.getOrder().getdNumber().getId() > 0) {
                order.getOrder().getdNumber().setActualNumber(rs.getInt("oddocumentnumber"));
                order.getOrder().setDocumentSerial(rs.getString("oddocumentserial"));
            } else {
                order.getOrder().setDocumentSerial(rs.getString("oddocumentserial"));
            }

        } catch (Exception e) {
        }

        try {
            UserData userdata = new UserData();

            userdata.setName(rs.getString("usname"));
            userdata.setSurname(rs.getString("ussurname"));
            userdata.setUsername(rs.getString("ususername"));
            order.getOrder().setUserCreated(userdata);
        } catch (Exception e) {
        }

        try {
            order.getOrder().setDateCreated(rs.getTimestamp("odc_time"));
        } catch (Exception e) {
        }

        try {

            order.getOrder().getAccount().setId(rs.getInt("odaccount_id"));
            order.getOrder().getAccount().setIsPerson(rs.getBoolean("accis_person"));
            order.getOrder().getAccount().setPhone(rs.getString("accphone"));
            order.getOrder().getAccount().setEmail(rs.getString("accemail"));

        } catch (Exception e) {
        }

        try {

            order.getOrder().getStatus().setId(rs.getInt("odstatus_id"));
            order.getOrder().getStatus().setTag(rs.getString("sttdname"));

        } catch (Exception e) {
        }

        try {

            order.getOrder().getType().setId(rs.getInt("odtype_id"));
            order.getOrder().setTypeNo(rs.getInt("odtypeno"));

        } catch (Exception e) {
        }

        try {
            order.getOrder().getBranchSetting().getBranch().setId(rs.getInt("odbranch_id"));
            order.getOrder().getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
            order.getOrder().getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
            order.getOrder().getBranchSetting().getBranch().setName(rs.getString("brname"));
            order.getOrder().getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
            order.getOrder().getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
            order.getOrder().getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));

        } catch (Exception e) {
        }
        try {
            order.setRemainingQuantity(rs.getBigDecimal("odiremainingquantity"));
        } catch (Exception e) {
        }

        //fonksiyondan dönen sorgu için
        try{
            order.getStock().setId(rs.getInt("stock_id"));
            order.getStock().setName(rs.getString("stockname"));
            order.getStock().setBarcode(rs.getString("barcode"));
            order.getUnit().setId(rs.getInt("unit_id"));
            order.setBoxQuantity(rs.getBigDecimal("boxquantity"));
            order.setShelfQuantity(rs.getBigDecimal("shelfquantity"));
            order.setLastTwoMonthsSales(rs.getBigDecimal("lasttwomonthssalesquantity"));
            order.setRequiredWarehouseStock(rs.getBigDecimal("requiredwarehousestockquantity"));
            order.setRequiredTotalStock(rs.getBigDecimal("totalstockquantity"));
            order.setAverageWeeklyOrderQuantity(rs.getBigDecimal("averageweeklyorderquantity"));
            order.setWarehouseQuantity(rs.getBigDecimal("balance"));
            order.setStockEnoughDay(rs.getBigDecimal("stockenoughday"));
            order.setOrderCalculationSupplement(rs.getBigDecimal("ordercalculation"));
            order.setMaxQuantity(rs.getBigDecimal("maximumquantity"));
            order.setMinQuantity(rs.getBigDecimal("minimumquantity"));
            order.setQuantity(rs.getBigDecimal("quantity"));
            order.setRecommendedPrice(rs.getBigDecimal("purchaserecommendedprice"));
            order.getCurrency().setId(rs.getInt("purchasecurrency_id"));
            order.setMaxFactorValue(rs.getBigDecimal("maxfactorvalue"));
            order.setMinFactorValue(rs.getBigDecimal("minfactorvalue"));
            order.setWarehouseStockDivisorValue(rs.getBigDecimal("warehousestockdivisorvalue"));
            order.setDescription(rs.getString("description"));
            order.setTwoMonthSaleActiveDay(rs.getInt("twomonthsaleactiveday"));
            order.setAverageWeeklyOrderQuantityForDaysCount(rs.getInt("averageweeklyorderquantityfordayscount"));
            order.setRemainingQuantity(rs.getBigDecimal("remainingquantity"));

        } catch (Exception e) {
        }

        try {
            order.setRemainingQuantity(rs.getBigDecimal("odiremainingquantity"));
        } catch (Exception e) {
        }

        try {
            order.getTaxGroup().setRate(rs.getBigDecimal("purchasekdv"));
        } catch (Exception e) {
        }

        try {
            order.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            order.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
        } catch (Exception e) {
        }

        try {
            order.getStock().setCenterProductCode(rs.getString("centerproductcode"));
        } catch (Exception e) {
        }
        try {
            order.setIsNewStockControl(rs.getBoolean("isnewstockcontrol"));
        } catch (Exception e) {
        }
        return order;

    }

}
