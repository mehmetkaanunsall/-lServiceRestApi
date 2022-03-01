/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 13:33:15
 */
package com.mepsan.marwiz.inventory.stockoperations.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.springframework.jdbc.core.RowMapper;

public class StockOperationsMapper implements RowMapper<StockOperations> {

    @Override
    public StockOperations mapRow(ResultSet rs, int i) throws SQLException {
        StockOperations operations = new StockOperations();

        //öneri
        try {
            operations.setDescription(rs.getString("ntfdescription"));
            operations.setCenterstock_id(rs.getInt("centerstock_id"));
            operations.setProcessdate(rs.getString("processdate"));
            operations.setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));

        } catch (Exception e) {
        }
        //değişiklik
        try {
            operations.getOldCurrency().setId(rs.getInt("oldcurrency_id"));
            operations.setOldPrice(rs.getBigDecimal("oldprice"));
            operations.setProcessDate(rs.getTimestamp("processdate"));

        } catch (Exception e) {
        }

        ///Ortak
        operations.setId(rs.getInt("id"));
        operations.getCurrency().setId(rs.getInt("currency_id"));
        operations.setPrice(rs.getBigDecimal("price"));
        operations.getStock().setId(rs.getInt("stckid"));
        operations.getStock().setBarcode(rs.getString("stckbarcode"));
        operations.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        operations.getStock().setCode(rs.getString("stckcode"));
        operations.getStock().setName(rs.getString("stckname"));
        operations.getStock().getUnit().setId(rs.getInt("stckunit_id"));
        operations.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        operations.getStock().getCountry().setId(rs.getInt("stckcountry_id"));
        operations.getStock().getCountry().setTag(rs.getString("condname"));
        operations.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
        operations.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
        operations.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
        operations.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
        operations.getStock().getStockInfo().setLastSalePriceChangeDate(rs.getTimestamp("silastsalepricechangedate"));
        operations.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));
        operations.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
        operations.getStock().getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("sisalemandatorycurrency_id"));

        try {
            operations.getStock().getStockInfo().setWeight(rs.getBigDecimal("siweight"));
            operations.getStock().getStockInfo().getWeightUnit().setId(rs.getInt("siweightunit_id"));
            operations.getStock().getStockInfo().getWeightUnit().setName(rs.getString("wuname"));
            operations.getStock().getStockInfo().getWeightUnit().setSortName(rs.getString("wusortname"));
            operations.getStock().getStockInfo().getWeightUnit().setMainWeight(rs.getBigDecimal("wumainweight"));

            operations.getStock().getStockInfo().getWeightUnit().setMainWeightUnit(new Unit());
            operations.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setId(rs.getInt("wumainweightunit_id"));
            operations.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setName(rs.getString("mwuname"));
            operations.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setSortName(rs.getString("mwusortname"));
        } catch (Exception e) {
        }

        return operations;
    }

}
