/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:05:57 PM
 */
package com.mepsan.marwiz.general.refinerypurchase.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

public class RefineryPurchaseMapper implements RowMapper<RefineryStockPrice> {

    @Override
    public RefineryStockPrice mapRow(ResultSet rs, int i) throws SQLException {
        RefineryStockPrice refineryStockPrice = new RefineryStockPrice();

        refineryStockPrice.setId(rs.getInt("rspid"));
        refineryStockPrice.setRefineryId(rs.getInt("rsprefinreyid"));
        refineryStockPrice.setPrice(rs.getBigDecimal("rspprice"));
        refineryStockPrice.getCurrency().setId(rs.getInt("rspcurrency_id"));
        refineryStockPrice.getStock().setId(rs.getInt("rspstock_id"));
    
        
        try {
                refineryStockPrice.getStock().setName(rs.getString("stckname"));
        refineryStockPrice.getCurrency().setTag(rs.getString("crydname"));
        } catch (Exception e) {
        }
     
        return refineryStockPrice;
    }

}
