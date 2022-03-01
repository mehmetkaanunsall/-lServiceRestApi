/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.inventory.StockEInvoiceUnitCon;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class StockEInvoiceUnitConMapper implements RowMapper<StockEInvoiceUnitCon> {

    @Override
    public StockEInvoiceUnitCon mapRow(ResultSet rs, int i) throws SQLException {
        StockEInvoiceUnitCon stockEInvoiceUnitCon = new StockEInvoiceUnitCon();

        stockEInvoiceUnitCon.setId(rs.getInt("seiuid"));
        stockEInvoiceUnitCon.setStockId(rs.getInt("seiustockid"));
        stockEInvoiceUnitCon.setStockIntegrationCode(rs.getString("seiustockintegrationcode"));
        stockEInvoiceUnitCon.setQuantity(rs.getBigDecimal("seiuquantity"));

        return stockEInvoiceUnitCon;

    }

}
