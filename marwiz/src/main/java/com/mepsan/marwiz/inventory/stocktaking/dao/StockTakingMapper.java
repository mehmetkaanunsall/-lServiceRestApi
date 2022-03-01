/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 11:25:06
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockTakingMapper implements RowMapper<StockTaking> {

    @Override
    public StockTaking mapRow(ResultSet rs, int i) throws SQLException {
        StockTaking stockTaking = new StockTaking();

        try {
            stockTaking.setId(rs.getInt("istid"));
        } catch (Exception e) {
        }
        try {
            stockTaking.setId(rs.getInt("istid"));
            stockTaking.setName(rs.getString("istname"));
            stockTaking.setBeginDate(rs.getTimestamp("istbegindate"));
        } catch (Exception e) {
        }
        try {
            stockTaking.getWarehouse().setId(rs.getInt("istwarehouse_id"));
            stockTaking.getWarehouse().setName(rs.getString("iwname"));
            stockTaking.setEndDate(rs.getTimestamp("istenddate"));
            stockTaking.getStatus().setId(rs.getInt("iststatus_id"));
            stockTaking.getStatus().setTag(rs.getString("sttdname"));
            stockTaking.setDescription(rs.getString("istdescription"));
            stockTaking.setEndDate(rs.getTimestamp("istenddate"));
            stockTaking.getApprovalEmployee().setId(rs.getInt("istapprovalemployee_id"));
            stockTaking.getApprovalEmployee().setName(rs.getString("accapprname"));
            stockTaking.getApprovalEmployee().setTitle(rs.getString("accapprtitle"));
            stockTaking.getTakingEmployee().setId(rs.getInt("isttakingemployee_id"));
            stockTaking.getTakingEmployee().setName(rs.getString("acctakname"));
            stockTaking.getTakingEmployee().setTitle(rs.getString("acctaktitle"));
            stockTaking.setIsTaxIncluded(rs.getBoolean("istis_taxincluded"));
            stockTaking.setIsControl(rs.getBoolean("istis_control"));
            stockTaking.getPriceList().setId(rs.getInt("istpricelist_id"));
            stockTaking.getPriceList().setName(rs.getString("prlname"));
            stockTaking.setRealTakingQuantity(rs.getString("realquantity"));
            stockTaking.setRealTakingPrice(rs.getString("realquantityprice"));
            stockTaking.setSystemTakingQuantity(rs.getString("systemquantity"));
            stockTaking.setSystemTakingPrice(rs.getString("systemquantityprice"));
            stockTaking.setDiffPrice(rs.getString("difftakingprice"));
            stockTaking.setDiffQuantity(rs.getString("difftakingquantity"));
            stockTaking.setCategories(rs.getString("categories"));
            stockTaking.setIsRetrospective(rs.getBoolean("istis_retrospective"));

        } catch (Exception e) {
        }

        try {
            stockTaking.getCurrency().setId(rs.getInt("crid"));
            stockTaking.getCurrency().setTag(rs.getString("currency"));
            stockTaking.setDifferencePrice(rs.getBigDecimal("diffprice"));
        } catch (Exception e) {
        }
        
        try {
            UserData userData = new UserData();
            userData.setId(rs.getInt("usdid"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            stockTaking.setDateCreated(rs.getTimestamp("istc_time"));
            stockTaking.setUserCreated(userData);
        } catch (Exception e) {
        }

        return stockTaking;
    }

}
