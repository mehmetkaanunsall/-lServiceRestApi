/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.03.2021 09:51:20
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.dao;

import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ECommerceStockMapper implements RowMapper<ECommerceStock> {

    @Override
    public ECommerceStock mapRow(ResultSet rs, int i) throws SQLException {
        ECommerceStock eCommerceStock = new ECommerceStock();
        eCommerceStock.getStock().setId(rs.getInt("r_stockid"));
        eCommerceStock.getStock().setName(rs.getString("r_stockname"));
        eCommerceStock.getStock().setBarcode(rs.getString("r_stockbarcode"));
        eCommerceStock.setHepsiburadaSku(rs.getString("r_hepsiburadasku"));
        eCommerceStock.setMerchantSku(rs.getString("r_merchantsku"));
        eCommerceStock.getStock().getUnit().setId(rs.getInt("r_unit_id"));
        eCommerceStock.getStock().getUnit().setSortName(rs.getString("r_sortname"));
        eCommerceStock.getStock().getUnit().setUnitRounding(rs.getInt("r_unitrounding"));
        eCommerceStock.setMarwizAvailableStock(rs.getBigDecimal("r_marwizavailablestock"));
        eCommerceStock.setMarwizPrice(rs.getBigDecimal("r_pricelistprice"));
        eCommerceStock.getStock().setCenterstock_id(rs.getInt("r_centerstock_id"));
        eCommerceStock.setPrice(rs.getBigDecimal("r_hbprice"));
        eCommerceStock.setAvailableStock(rs.getBigDecimal("r_hbavailablestock"));
        eCommerceStock.setDispatchTime(rs.getInt("r_dispatchtime"));
        eCommerceStock.setCargoCompany1(rs.getString("r_cargocampany1"));
        eCommerceStock.setCargoCompany2(rs.getString("r_cargocampany2"));
        eCommerceStock.setCargoCompany3(rs.getString("r_cargocampany3"));
        eCommerceStock.setMaximumPurchasableQuantity(rs.getInt("r_maximumpurchasablequantity"));
        eCommerceStock.setIsSalableHepsiburada(rs.getBoolean("r_issalabledhepsiburada"));

        return eCommerceStock;
    }

}
