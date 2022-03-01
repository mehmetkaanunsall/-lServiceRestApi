/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 01:38:47
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PriceListItemMapper implements RowMapper<PriceListItem> {

    @Override
    public PriceListItem mapRow(ResultSet rs, int i) throws SQLException {
        PriceListItem pli = new PriceListItem();

        try {
            pli.setId(rs.getInt("pliid"));
            pli.setPrice(rs.getBigDecimal("pliprice"));
            pli.getCurrency().setId(rs.getInt("plicurrency_id"));
            pli.setIs_taxIncluded(rs.getBoolean("pliis_taxincluded"));
        } catch (Exception e) {
        }

        try {
            pli.getCurrency().setTag(rs.getString("crrdname"));
        } catch (Exception e) {
        }

        try {

            pli.getStock().setId(rs.getInt("plistock_id"));
            pli.getStock().setName(rs.getString("stckname"));
            pli.getStock().setBarcode(rs.getString("stckbarcode"));
            pli.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));
            pli.getStock().getStockInfo().getCurrency().setId(rs.getInt("sicurrency_id"));
            pli.getStock().getStockInfo().getCurrency().setTag(rs.getString("crrd2name"));
            pli.getCurrency().setTag(rs.getString("crrdname"));
        } catch (Exception e) {
        }

        try {
            UserData userData = new UserData();

            userData.setId(rs.getInt("usdid"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            pli.setUserCreated(userData);
            pli.setDateCreated(rs.getTimestamp("plic_time"));
        } catch (Exception e) {

        }
        try {
            pli.getStock().getCountry().setId(rs.getInt("stckcountry_id"));
            pli.getStock().getCountry().setTag(rs.getString("condname"));
            pli.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            pli.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
            pli.getStock().getStockInfo().setLastSalePriceChangeDate(rs.getTimestamp("silastsalepricechangedate"));
        } catch (Exception e) {
        }

        try {
            pli.getStock().getStockInfo().setMinProfitRate(rs.getBigDecimal("siminprofitrate"));
            pli.getStock().getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
            pli.getStock().getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
            pli.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
            pli.getStock().getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("sisalemandatorycurrency_id"));

        } catch (Exception e) {
        }
        try {
            pli.getStock().getStockInfo().setWeight(rs.getBigDecimal("siweight"));
            pli.getStock().getStockInfo().getWeightUnit().setId(rs.getInt("siweightunit_id"));
            pli.getStock().getStockInfo().getWeightUnit().setName(rs.getString("wuname"));
            pli.getStock().getStockInfo().getWeightUnit().setSortName(rs.getString("wusortname"));
            pli.getStock().getStockInfo().getWeightUnit().setMainWeight(rs.getBigDecimal("wumainweight"));

            pli.getStock().getStockInfo().getWeightUnit().setMainWeightUnit(new Unit());
            pli.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setId(rs.getInt("wumainweightunit_id"));
            pli.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setName(rs.getString("mwuname"));
            pli.getStock().getStockInfo().getWeightUnit().getMainWeightUnit().setSortName(rs.getString("mwusortname"));

        } catch (Exception e) {
        }
        try {//Fiyat Listesinde fiyatı toplu olarak tesf olarak kaydetmek için
            pli.getStock().setId(rs.getInt("stckid"));
            pli.getStock().setName(rs.getString("stckname"));
            pli.getStock().setBarcode(rs.getString("stckbarcode"));
            pli.getStock().getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
            pli.getStock().getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("sisalemandatorycurrency_id"));
            pli.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
            pli.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));

            pli.getStock().getStockInfo().getCurrency().setId(rs.getInt("sicurrency_id"));
            pli.setPrice(rs.getBigDecimal("pliprice"));
            pli.getCurrency().setId(rs.getInt("plicurrency_id"));

        } catch (Exception e) {
        }
        try {
            pli.setAlternativeBarcodes(rs.getString("alternativebarcode"));
        } catch (Exception e) {
        }
        try {
            pli.setPriceWithTax(rs.getBigDecimal("pricewithtax"));
        } catch (Exception e) {
        }
        try {
            pli.setLogPrintTagId(rs.getInt("ptid"));
            pli.setPrintTagQuantity(rs.getBigDecimal("ptquantity"));
        } catch (Exception e) {
        }
        try {
            pli.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        } catch (Exception e) {
        }
        try {
            pli.setTagQuantity(rs.getInt("tagquantity"));
        } catch (Exception e) {
        }
        try {
            pli.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
        } catch (Exception e) {
        }
        try {
            pli.getWaybill().getAccount().setId(rs.getInt("wbaccount_id"));
        } catch (Exception e) {
        }
        try {
            pli.getCategorization().setId(rs.getInt("ctccategorization_id"));
        } catch (Exception e) {
        }
        return pli;
    }

}
