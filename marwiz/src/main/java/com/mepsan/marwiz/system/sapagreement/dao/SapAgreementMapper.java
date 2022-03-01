/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapagreement.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class SapAgreementMapper implements RowMapper<SapAgreement> {

    @Override
    public SapAgreement mapRow(ResultSet rs, int i) throws SQLException {
        SapAgreement sap = new SapAgreement();

        try {
            sap.getCurrency().setId(rs.getInt("crdid"));
            sap.getCurrency().setCode(rs.getString("crdcode"));
            sap.getCurrency().setInternationalCode(rs.getString("crdinternationalcode"));
        } catch (Exception e) {
        }

        try {
            sap.setTotalMoney(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }

        try {
            sap.setId(rs.getInt("sapid"));
            sap.getBranch().setId(rs.getInt("sapbranch_id"));
            sap.setProcessDate(rs.getTimestamp("sapprocessdate"));
            sap.setAutomationJson(rs.getString("sapautomationjson"));
            sap.setAutomationDiffAmount(rs.getBigDecimal("sapautomationdiffamount"));
            sap.setPosSaleJson(rs.getString("sappossalejson"));
            sap.setExpenseJson(rs.getString("sapexpensejson"));
            sap.setExchangeJson(rs.getString("sapexchangejson"));
            sap.setFuelZJson(rs.getString("sapfuelzjson"));
            sap.setMarketZJson(rs.getString("sapmarketzjson"));
            sap.setTotalJson(rs.getString("saptotaljson"));
            sap.setSafeTransferJson(rs.getString("sapsafetransfer"));
            sap.setBankTransferJson(rs.getString("sapbanktransfer"));
            sap.setSendData(rs.getString("sapsenddata"));
            sap.setIsSend(rs.getBoolean("sapis_send"));
            sap.setSendDate(rs.getTimestamp("sapsenddate"));
            sap.setResponse(rs.getString("sapresponse"));

        } catch (Exception e) {
        }
        try {
            sap.setTransferAutomationDiffAmount(rs.getBigDecimal("transferautomationdiffamount"));
            sap.setTransferMarketDiffAmount(rs.getBigDecimal("transfermarketdiffamount"));
        } catch (Exception e) {
        }

        try {
            sap.setReturnSalesTotal(rs.getBigDecimal("totalreturnsales"));
            sap.setReturnsWithSale(rs.getBigDecimal("returnsWithSale"));

        } catch (Exception e) {
        }

        try {
            sap.setSendData(rs.getString("sapsenddata"));
        } catch (Exception e) {
        }

        try {
            sap.getPaymentType().setId(rs.getInt("id"));
            sap.getPaymentType().setEntegrationname(rs.getString("name"));
            sap.getPaymentType().setEntegrationcode(rs.getString("integrationcode"));
            sap.getPaymentType().setTotalMoney(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }
        return sap;
    }

}
