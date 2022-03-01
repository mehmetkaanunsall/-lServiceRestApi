/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.model.finance.Waybill;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WaybillMapper implements RowMapper<Waybill> {
    
    @Override
    public Waybill mapRow(ResultSet rs, int i) throws SQLException {
        Waybill waybill = new Waybill();
        
        waybill.setId(rs.getInt("wbid"));
        waybill.getAccount().setId(rs.getInt("wbaccount_id"));
        waybill.setWaybillDate(rs.getTimestamp("wbwaybilldate"));
        waybill.setDocumentNumber(rs.getString("wbdocumentnumber"));
        
        waybill.getdNumber().setId(rs.getInt("wbdocumentnumber_id"));
        waybill.setDocumentSerial(rs.getString("wbdocumentserial"));
        
        if (waybill.getdNumber().getId() > 0) {
            waybill.getdNumber().setActualNumber(rs.getInt("wbdocumentnumber"));
        }
        
        waybill.setDeliveryPerson(rs.getString("wbdeliveryperson"));
        waybill.setDispatchDate(rs.getTimestamp("wbdispatchdate"));
        waybill.setDispatchAddress(rs.getString("wbdispatchaddress"));
        waybill.setDescription(rs.getString("wbdescription"));
        waybill.setIsPurchase(rs.getBoolean("wbis_purchase"));
        waybill.getType().setId(rs.getInt("wbtype_id"));
        waybill.getType().setTag(rs.getString("typdname"));
        waybill.getStatus().setId(rs.getInt("wbstatus_id"));
        waybill.getStatus().setTag(rs.getString("sttdname"));
        waybill.getAccount().setIsPerson(rs.getBoolean("accis_person"));
        waybill.getAccount().setName(rs.getString("accname"));
        waybill.getAccount().setTitle(rs.getString("acctitle"));
        waybill.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        waybill.getAccount().setPhone(rs.getString("accphone"));
        waybill.getAccount().setEmail(rs.getString("accemail"));
        waybill.getAccount().setAddress(rs.getString("accaddress"));
        waybill.getAccount().setTaxNo(rs.getString("acctaxno"));
        waybill.getAccount().setTaxOffice(rs.getString("acctaxoffice"));
        waybill.getAccount().setBalance(rs.getBigDecimal("accbalance"));
        if (rs.getString("accdueday") == null) {
            waybill.getAccount().setDueDay(null);
        } else {
            waybill.getAccount().setDueDay(rs.getInt("accdueday"));
        }
        //waybill.getWarehouse().setId(rs.getInt("wbwarehouse_id"));
        //waybill.getWarehouseReceipt().setId(rs.getInt("wbwarehousereceipt_id"));
        waybill.setIsInvoice(rs.getBoolean("isinvoice"));
        waybill.setWarehouseIdList(rs.getString("warehouseids"));
        waybill.setWarehouseNameList(rs.getString("warehousenames"));
        waybill.setIsWaybillInvoice(rs.getBoolean("iswaybillinvoice"));
        waybill.getBranchSetting().getBranch().setId(rs.getInt("wbbranch_id"));
        waybill.getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
        waybill.getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
        waybill.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
        waybill.getBranchSetting().getBranch().setName(rs.getString("brname"));
        waybill.getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
        waybill.getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
        
        try {
            waybill.setSapLogİsSend(rs.getBoolean("spinvis_send"));
        } catch (Exception e) {
        }
        
        try {
            waybill.setIsOrderConnection(rs.getBoolean("isorderconnection"));
        } catch (Exception e) {
        }
        
        try {
            waybill.setIsFuel(rs.getBoolean("wbis_fuel"));
        } catch (Exception e) {
        }
        return waybill;
    }
    
}
