/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 14.03.2018 09:02:00
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.model.inventory.StockAlternativeBarcode;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockAlternativeBarcodeMapper implements RowMapper<StockAlternativeBarcode> {

    @Override
    public StockAlternativeBarcode mapRow(ResultSet rs, int i) throws SQLException {
        StockAlternativeBarcode sab = new StockAlternativeBarcode();
        sab.setId(rs.getInt("sabid"));
        sab.setBarcode(rs.getString("sabbarcode"));
        sab.setQuantity(rs.getBigDecimal("sabquantity"));
        sab.setIsOtherBranch(rs.getBoolean("sabis_otherbranch"));
        return sab;
    }

}
