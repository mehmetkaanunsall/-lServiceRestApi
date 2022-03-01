/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.02.2019 03:34:47
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FuelShiftPreviewMapper implements RowMapper<FuelShiftPreview> {
    
    @Override
    public FuelShiftPreview mapRow(ResultSet rs, int i) throws SQLException {
        FuelShiftPreview fuelShiftPreview = new FuelShiftPreview();
        
        try {//Stok Miktarına Göre
            fuelShiftPreview.getStock().setId(rs.getInt("stock_id"));
            fuelShiftPreview.getStock().setName(rs.getString("stckname"));
            fuelShiftPreview.getStock().setBarcode(rs.getString("stckbarcode"));
            fuelShiftPreview.setAmount(rs.getBigDecimal("sslliter"));
            fuelShiftPreview.setTotalMoney(rs.getBigDecimal("ssltotalmoney"));
            fuelShiftPreview.setPreviousAmount(rs.getBigDecimal("previousamount"));
            fuelShiftPreview.setRemainingAmount(rs.getBigDecimal("remainingamount"));
            fuelShiftPreview.getStock().getUnit().setId(rs.getInt("stckunit_id"));
            fuelShiftPreview.getStock().getUnit().setSortName(rs.getString("guntsortname"));
            fuelShiftPreview.getStock().getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        } catch (Exception e) {
        }
        
        try {//Nakit Teslimat
            fuelShiftPreview.setPrice(rs.getBigDecimal("price"));
            fuelShiftPreview.getSafe().getCurrency().setId(rs.getInt("currency"));
            fuelShiftPreview.setExchangeRate(rs.getBigDecimal("exchangerate"));
            fuelShiftPreview.getSafe().setName(rs.getString("sfname"));
            fuelShiftPreview.getSafe().setCode(rs.getString("sfcode"));
        } catch (Exception e) {
        }
        
        try {//Kredi Kartı Teslimat
            fuelShiftPreview.setPrice(rs.getBigDecimal("price"));
            fuelShiftPreview.getBankAccount().getCurrency().setId(rs.getInt("currency"));
            fuelShiftPreview.setExchangeRate(rs.getBigDecimal("exchangerate"));
            fuelShiftPreview.getBankAccount().setName(rs.getString("bkaname"));
            
        } catch (Exception e) {
        }
        
        try {//Veresiye Teslimatı
            fuelShiftPreview.getAccount().setId(rs.getInt("accid"));
            fuelShiftPreview.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            fuelShiftPreview.getAccount().setName(rs.getString("accname"));
            fuelShiftPreview.getAccount().setTitle(rs.getString("acctitle"));
            fuelShiftPreview.getAccount().setCode(rs.getString("acccode"));
            fuelShiftPreview.setPrice(rs.getBigDecimal("ssltotalmoney"));
            fuelShiftPreview.setSaleCount(rs.getInt("salecount"));
        } catch (Exception e) {
        }
        
        try {//Cari Teslimatları
            fuelShiftPreview.setOutcomingAmount(rs.getBigDecimal("employeedebt"));
            fuelShiftPreview.setIncomingAmount(rs.getBigDecimal("givenemployee"));
            fuelShiftPreview.setDocumentNumber(rs.getString("fdocdocumnetnumber"));
            fuelShiftPreview.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            fuelShiftPreview.getAccount().setName(rs.getString("accname"));
            fuelShiftPreview.getAccount().setTitle(rs.getString("acctitle"));
            fuelShiftPreview.getAccount().setCode(rs.getString("acccode"));
            fuelShiftPreview.setDescription(rs.getString("fdocdescription"));
            fuelShiftPreview.getType().setId(rs.getInt("fdoctype_id"));
        } catch (Exception e) {
        }
        try {//Genel Toplam
            fuelShiftPreview.setCreditAmout(rs.getBigDecimal("creditamount"));
            fuelShiftPreview.setAutomationSaleAmount(rs.getBigDecimal("automationsale"));
            fuelShiftPreview.setTestAmount(rs.getBigDecimal("testamount"));
            fuelShiftPreview.setCashAmount(rs.getBigDecimal("cashamount"));
            fuelShiftPreview.setIncomeAmount(rs.getBigDecimal("incomeamount"));
            fuelShiftPreview.setExpenseAmount(rs.getBigDecimal("expenseamount"));
            fuelShiftPreview.setEmployeeDebt(rs.getBigDecimal("employeedebt"));
            fuelShiftPreview.setGivenEmployee(rs.getBigDecimal("givenemployee"));
            fuelShiftPreview.setCreditCardAmount(rs.getBigDecimal("creditcardamount"));
            fuelShiftPreview.setEntrySubTotal(rs.getBigDecimal("entrysubtotal"));
            fuelShiftPreview.setExitSubTotal(rs.getBigDecimal("exitsubtotal"));
            fuelShiftPreview.setAccountCollection(rs.getBigDecimal("accountcollection"));
            fuelShiftPreview.setAccountPayment(rs.getBigDecimal("accountpayment"));
            
        } catch (Exception e) {
        }
        
        return fuelShiftPreview;
    }
    
}
