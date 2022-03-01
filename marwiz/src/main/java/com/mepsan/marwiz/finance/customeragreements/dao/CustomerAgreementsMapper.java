/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 03:07:26
 */
package com.mepsan.marwiz.finance.customeragreements.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CustomerAgreementsMapper implements RowMapper<CustomerAgreements> {

    @Override
    public CustomerAgreements mapRow(ResultSet rs, int i) throws SQLException {
        CustomerAgreements customerAgreements = new CustomerAgreements();

        try {
            customerAgreements.setId(rs.getInt("crdtid"));
            customerAgreements.setCreditDate(rs.getTimestamp("crdtduedate"));
        } catch (Exception e) {
        }
        try {
            customerAgreements.setPlate(rs.getString("shplate"));
            customerAgreements.getStock().setName(rs.getString("stckname"));//yeni eklendi
            customerAgreements.setLiter(rs.getDouble("shsliter"));
            customerAgreements.setUnitPrice(rs.getDouble("shprice"));

            customerAgreements.getStock().getUnit().setId(rs.getInt("unid"));//yeni eklendi
            customerAgreements.getStock().getUnit().setName(rs.getString("unname"));
            customerAgreements.getStock().getUnit().setSortName(rs.getString("unsort"));
            customerAgreements.getStock().getUnit().setUnitRounding(rs.getInt("unrounding"));

        } catch (Exception e) {
        }

        customerAgreements.setIsInvoice(rs.getBoolean("isInvoiced"));
        customerAgreements.getAccount().setId(rs.getInt("crdtaccount_id"));
        customerAgreements.getAccount().setName(rs.getString("accname"));
        customerAgreements.getAccount().setTitle(rs.getString("acctitle"));
        customerAgreements.getAccount().setAddress(rs.getString("accaddress"));
        customerAgreements.getAccount().setEmail(rs.getString("accemail"));
        customerAgreements.getAccount().setPhone(rs.getString("accphone"));
        customerAgreements.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        customerAgreements.setMoney(rs.getBigDecimal("crdtmoney"));
        customerAgreements.getCurrency().setId(rs.getInt("crdtcurrency"));

        if (rs.getString("accdueday") == null) {
            customerAgreements.getAccount().setDueDay(null);
        } else {
            customerAgreements.getAccount().setDueDay(rs.getInt("accdueday"));
        }
        customerAgreements.getBranchSetting().getBranch().setId(rs.getInt("crdtbranch_id"));
        customerAgreements.getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
        customerAgreements.getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
        customerAgreements.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
        customerAgreements.getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
        customerAgreements.getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
        customerAgreements.getCurrency().setCode(rs.getString("crcode"));
        
        
        try {
            customerAgreements.setRowNumberId(rs.getInt("rownumber"));
        } catch (Exception e) {
        }

        return customerAgreements;
    }

}
