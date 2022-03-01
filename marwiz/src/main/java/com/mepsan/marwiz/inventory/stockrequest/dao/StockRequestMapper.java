/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   18.04.2018 12:26:27
 */
package com.mepsan.marwiz.inventory.stockrequest.dao;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockRequestMapper implements RowMapper<StockRequest> {

    @Override
    public StockRequest mapRow(ResultSet rs, int i) throws SQLException {
        StockRequest stockRequest = new StockRequest();
        stockRequest.setId(rs.getInt("msrtid"));
        stockRequest.setName(rs.getString("msrtname"));
        stockRequest.setBarcode(rs.getString("msrtbarcode"));
        stockRequest.getUnit().setId(rs.getInt("msrtunit_id"));
        stockRequest.getUnit().setName(rs.getString("msrtguntname"));
        stockRequest.setBrand(rs.getString("msrtbrand"));
        stockRequest.getCountry().setId(rs.getInt("msrtcountry_id"));
        stockRequest.setIsService(rs.getBoolean("msrtis_service"));
        stockRequest.setDescription(rs.getString("msrtdescription"));
        stockRequest.getSaleTaxGroup().setId(rs.getInt("msrtsaletaxgroup_id"));
        stockRequest.getPurchaseTaxGroup().setId(rs.getInt("msrtpurchasetaxgroup_id"));
        stockRequest.setApproval(rs.getInt("msrtapproval"));
        stockRequest.setApprovalDate(rs.getDate("msrtapprovaldate"));
        stockRequest.setPrice(rs.getBigDecimal("msrtprice"));
        stockRequest.getCurrency().setId(rs.getInt("msrtcurrency_id"));
        stockRequest.getCurrency().setTag(rs.getString("crydname"));
        stockRequest.setApprovalCenterStockId(rs.getInt("msrtapprovalcenterstock_id"));
        stockRequest.getApprovalStock().setId(rs.getInt("msrtapprovalstock_id"));
        stockRequest.setCode(rs.getString("msrtcode"));
        stockRequest.setWeight(rs.getBigDecimal("msrtweight"));
        stockRequest.getWeightUnit().setId(rs.getInt("msrtweightunit_id"));
        return stockRequest;
    }

}
