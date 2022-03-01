/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:26:41 AM
 */
package com.mepsan.marwiz.general.report.zreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ZReportMapper implements RowMapper<ZReport> {
    
    @Override
    public ZReport mapRow(ResultSet rs, int i) throws SQLException {
        ZReport zReport = new ZReport();
        
        try { // kategori name
            zReport.setDescription(rs.getString("category"));
            zReport.getBranch().setName(rs.getString("branchname"));
        } catch (Exception e) {
        }
        
        try { // kategorisizler için id 
            zReport.setCategoryId(rs.getInt("categoryid"));
        } catch (Exception e) {
        }
        
        try { // kategori bazlı rapor
            zReport.setReturnAmount(rs.getBigDecimal("returnquantity"));
        } catch (Exception e) {
        }
        
        try {
            zReport.setReturnPrice(rs.getBigDecimal("returntotalprice"));
            
        } catch (Exception e) {
        }
        try {
            zReport.setSalePrice(rs.getBigDecimal("salestotal"));
        } catch (Exception e) {
        }
        try {
            zReport.setSaleAmount(rs.getBigDecimal("salesquantity"));
            
        } catch (Exception e) {
        }
        try { // kdv bazlı tablo için kdv bilgisini set eder.
            zReport.setTaxRate(rs.getBigDecimal("slitaxrate"));
            
        } catch (Exception e) {
        }
        try {
            zReport.getType().setId(rs.getInt("slptype_id"));
            zReport.getType().setTag(rs.getString("typdname"));
        } catch (Exception e) {
        }
        
        try {
            zReport.getCurrency().setId(rs.getInt("slcurrency_id"));
        } catch (Exception e) {
        }
        
        try {
            zReport.setReturnReceiptCount(rs.getInt("salereturnreceiptcount"));
            zReport.setReceiptCount(rs.getInt("salereceiptcount"));
        } catch (Exception e) {
        }
        
        try {
            zReport.setTotalSalePrice(rs.getBigDecimal("totalsaleprice"));
            zReport.setTotalSaleMoney(rs.getBigDecimal("totalsalemoney"));
            zReport.setTotalReturnPrice(rs.getBigDecimal("totalreturnprice"));
            zReport.setTotalMoneyIncludeReturn(rs.getBigDecimal("totalmoneyincludereturn"));
        } catch (Exception e) {
        }
        
        try {
            zReport.getBranch().setId(rs.getInt("brnid"));
            zReport.getBranch().setName(rs.getString("brnname"));
        } catch (Exception e) {
        }
        
        try {
            zReport.getUserData().setName(rs.getString("usrname"));
            zReport.getUserData().setSurname(rs.getString("usrsurname"));
        } catch (Exception e) {
        }
        
        return zReport;
        
    }
    
}
