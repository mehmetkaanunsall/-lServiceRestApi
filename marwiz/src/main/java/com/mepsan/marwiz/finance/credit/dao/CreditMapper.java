/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Gozde Gursel
 */
public class CreditMapper implements RowMapper<CreditReport> {

    @Override
    public CreditReport mapRow(ResultSet rs, int i) throws SQLException {
        CreditReport credit = new CreditReport();

        try {
            credit.setId(rs.getInt("crdt_id"));
            credit.setMoney(rs.getBigDecimal("crdtmoney"));
            credit.getCurrency().setId(rs.getInt("crdtcurrency_id"));
            credit.setRemainingMoney(rs.getBigDecimal("crdtremainingmoney"));

        } catch (Exception e) {
        }
        try {

            credit.setProcessDate(rs.getTimestamp("crdtprocessdate"));
            credit.getAccount().setId(rs.getInt("crdtaccount_id"));
            credit.getAccount().setName(rs.getString("accname"));
            credit.getAccount().setTitle(rs.getString("acctitle"));
            credit.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            credit.getAccount().setEmail(rs.getString("accemail"));
            credit.getAccount().setPhone(rs.getString("accphone"));
            credit.getAccount().setAddress(rs.getString("accaddress"));
            credit.getCurrency().setCode(rs.getString("crcode"));
            credit.setDueDate(rs.getTimestamp("crdtduedate"));
            credit.setIsPaid(rs.getBoolean("crdtis_paid"));
            credit.setOverallmoney(rs.getBigDecimal("totalCollection"));
            credit.setOverallremainingmoney(rs.getBigDecimal("totalCollectionRemaining"));
            credit.setOverallPaymentMoney(rs.getBigDecimal("totalPayment"));
            credit.setOverallPaymentRemaining(rs.getBigDecimal("totalPaymentRemaining"));
            credit.setIsCancel(rs.getBoolean("crdtis_cancel"));
            credit.setIsInvoice(rs.getBoolean("crdtis_invoice"));
            credit.setIsCustomer(rs.getBoolean("crdtis_customer"));
            if (rs.getString("accdueday") == null) {
                credit.getAccount().setDueDay(null);
            } else {
                credit.getAccount().setDueDay(rs.getInt("accdueday"));
            }
            credit.getBranchSetting().getBranch().setId(rs.getInt("crdtbranch_id"));
            credit.getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
            credit.getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
            credit.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
        } catch (Exception e) {
        }

        try {
            credit.setTotalShiftCredit(rs.getBigDecimal("moneysub"));
        } catch (Exception e) {
        }

        try {
            credit.getBranchSetting().getBranch().setId(rs.getInt("brid"));
            credit.getBranchSetting().getBranch().setName(rs.getString("brname"));
            credit.getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
        } catch (Exception e) {
        }
        try {
            credit.getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
        } catch (Exception e) {
        }

        try {
            credit.setPaidMoney(rs.getBigDecimal("paidmoney"));
        } catch (Exception e) {
        }
        return credit;

    }

}
