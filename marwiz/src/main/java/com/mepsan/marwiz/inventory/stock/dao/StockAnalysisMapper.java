/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 17:18:11
 */
package com.mepsan.marwiz.inventory.stock.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockAnalysisMapper implements RowMapper<StockAnalysis> {

    @Override
    public StockAnalysis mapRow(ResultSet rs, int i) throws SQLException {
        StockAnalysis sa = new StockAnalysis();
        try {
            sa.setLastDay(rs.getBigDecimal("lastday"));
            sa.setLastWeek(rs.getBigDecimal("lastweek"));
            sa.setLastPurchasePrice(rs.getBigDecimal("lastpurchaseprice"));
            sa.getLastPurchaseCurrency().setId(rs.getInt("lastpurchasecurrency_id"));
            sa.setLastSalePrice(rs.getBigDecimal("lastsaleprice"));
            sa.getLastSaleCurrency().setId(rs.getInt("lastsalecurrency_id"));
        } catch (Exception e) {
        }

        try {
            sa.setDay(rs.getInt("day"));
        } catch (Exception e) {

        }
        try {
            sa.setLastMonth(rs.getBigDecimal("lastmonth"));
        } catch (Exception e) {

        }
        try {
            sa.setMonth(rs.getInt("month"));
        } catch (Exception e) {

        }
        try {
            sa.setLastSalePrice(rs.getBigDecimal("price"));
        } catch (Exception e) {

        }
        
       
        return sa;
    }

}
