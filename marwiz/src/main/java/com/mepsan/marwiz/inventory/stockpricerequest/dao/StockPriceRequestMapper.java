/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stockpricerequest.dao;

import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class StockPriceRequestMapper implements RowMapper<StockPriceRequest>{

    @Override
    public StockPriceRequest mapRow(ResultSet rs, int i) throws SQLException {
        StockPriceRequest stockPriceRequest=new StockPriceRequest();
        stockPriceRequest.setId(rs.getInt("pcrid"));
        stockPriceRequest.getStock().setId(rs.getInt("pcrstock_id"));
        stockPriceRequest.getStock().setCenterstock_id(rs.getInt("pcrcenterstock_id"));
        stockPriceRequest.getStock().setName(rs.getString("stckname"));
        stockPriceRequest.getStock().setBarcode(rs.getString("stckbarcode"));
        stockPriceRequest.getStock().getStockInfo().setCurrentSalePrice(rs.getBigDecimal("pcravaibleprice"));
        stockPriceRequest.getStock().getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("pcravailablecurrency_id"));
        stockPriceRequest.getStock().getStockInfo().setRecommendedPrice(rs.getBigDecimal("pcrrecommendedprice"));
        stockPriceRequest.getStock().getStockInfo().getCurrency().setId(rs.getInt("pcrrecommendedcurrency_id"));
        stockPriceRequest.setRequestPrice(rs.getBigDecimal("pcrrequestprice"));
        stockPriceRequest.getRequestCurrency().setId(rs.getInt("pcrrequestcurrency_id"));
        stockPriceRequest.setDescription(rs.getString("pcrdescription"));
        stockPriceRequest.setApproval(rs.getInt("pcrapproval"));
        stockPriceRequest.setApprovalDate(rs.getDate("pcrapprovaldate"));
        
        return stockPriceRequest;
    }
    
}
