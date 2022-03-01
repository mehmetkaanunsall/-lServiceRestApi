/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 11:52:39
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InvoiceMapper implements RowMapper<Invoice> {

    @Override
    public Invoice mapRow(ResultSet rs, int i) throws SQLException {
        Invoice invoice = new Invoice();
        
        invoice.setId(rs.getInt("invid"));

        invoice.setIsPurchase(rs.getBoolean("invis_purchase"));
        invoice.getAccount().setName(rs.getString("accname"));
        invoice.getAccount().setTitle(rs.getString("acctitle"));
        invoice.getdNumber().setId(rs.getInt("invdocumentnumber_id"));
        invoice.setDocumentNumber(rs.getString("invdocumentnumber"));
        invoice.setInvoiceDate(rs.getTimestamp("invinvoicedate"));
        invoice.setTotalMoney(rs.getBigDecimal("invtotalmoney"));
        invoice.getCurrency().setId(rs.getInt("invcurrency_id"));

        if (invoice.getdNumber().getId() > 0) {
            invoice.getdNumber().setActualNumber(rs.getInt("invdocumentnumber"));
            invoice.setDocumentSerial(rs.getString("invdocumentserial"));
        } else {
            invoice.setDocumentSerial(rs.getString("invdocumentserial"));
        }

        try {
            UserData userdata = new UserData();

            userdata.setName(rs.getString("usname"));
            userdata.setSurname(rs.getString("ussurname"));
            userdata.setUsername(rs.getString("ususername"));
            invoice.setUserCreated(userdata);
        } catch (Exception e) {
        }

        try {
            invoice.setDateCreated(rs.getTimestamp("invc_time"));
        } catch (Exception e) {
        }
        try {
            if (rs.getString("accdueday") == null) {
                invoice.getAccount().setDueDay(null);
            } else {
                invoice.getAccount().setDueDay(rs.getInt("accdueday"));
            }
        } catch (Exception e) {
        }

        try {
            invoice.setDueDate(rs.getTimestamp("invduedate"));
            invoice.setTotalPrice(rs.getBigDecimal("invtotalprice"));
            invoice.setExchangeRate(rs.getBigDecimal("invexchangerate"));

            invoice.getAccount().setId(rs.getInt("invaccount_id"));
            invoice.getAccount().setIsPerson(rs.getBoolean("accis_person"));
            invoice.getAccount().setPhone(rs.getString("accphone"));
            invoice.getAccount().setEmail(rs.getString("accemail"));

            /* Yazdır İçin Ekstra Çekildi. */
            invoice.getAccount().setAddress(rs.getString("accaddress"));
            invoice.getAccount().setTaxNo(rs.getString("acctaxno"));
            invoice.getAccount().setTaxOffice(rs.getString("acctaxoffice"));
            invoice.getAccount().setBalance(rs.getBigDecimal("accbalance"));
            invoice.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            /* Yazdır İçin Ekstra Çekildi. */

            invoice.setDescription(rs.getString("invdescription"));
            invoice.setTotalDiscount(rs.getBigDecimal("invtotaldiscount"));
            invoice.setDiscountRate(rs.getBigDecimal("invdiscountrate"));
            invoice.setDiscountPrice(rs.getBigDecimal("invdiscountprice"));
            invoice.setDispatchAddress(rs.getString("invdispatchaddress"));
            invoice.setDispatchDate(rs.getTimestamp("invdispatchdate"));

            invoice.getStatus().setId(rs.getInt("invstatus_id"));
            invoice.getStatus().setTag(rs.getString("sttdname"));
            invoice.setTotalTax(rs.getBigDecimal("invtotaltax"));

            invoice.setIsPeriodInvoice(rs.getBoolean("invis_periodinvoice"));

            invoice.setSaleId(rs.getInt("sid"));
            invoice.setPosId(rs.getInt("spointofsale_id"));
            invoice.setWarehouseIdList(rs.getString("warehouseid"));
            invoice.setDeliveryPerson(rs.getString("wbdeliveryperson"));
            invoice.setRemainingMoney(rs.getBigDecimal("invremainingmoney"));
            invoice.setRoundingPrice(rs.getBigDecimal("invroundingprice"));
            invoice.setIsDiscountRate(rs.getBoolean("invis_discountrate"));
        } catch (Exception e) {
        }

        try {
            invoice.setTaxPayerTypeId(rs.getInt("invtaxpayertype_id"));
            invoice.setDeliveryTypeId(rs.getInt("invdeliverytype_id"));
            invoice.setInvoiceScenarioId(rs.getInt("invinvoicescenario_id"));
            invoice.setIsEInvoice(rs.getBoolean("invis_einvoice"));
        } catch (Exception e) {
        }

        try {
            UserData userData = new UserData();
            userData.setId(rs.getInt("invc_id"));
            userData.setUsername(rs.getString("usdusername"));

            invoice.setUserCreated(userData);
            invoice.setDateCreated(rs.getTimestamp("invc_time"));
        } catch (Exception e) {
        }
        try {
            invoice.setTotalProfit(rs.getBigDecimal("invoiceitemprofit"));
        } catch (Exception e) {
        }

        try {
            invoice.setIsPayment(rs.getBoolean("ispayment"));
        } catch (Exception e) {
        }
        try {
            invoice.getBranchSetting().getBranch().setId(rs.getInt("invbranch_id"));
            invoice.getBranchSetting().setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
            invoice.getBranchSetting().setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
            invoice.getBranchSetting().getBranch().setName(rs.getString("brname"));
            invoice.getBranchSetting().getBranch().setIsAgency(rs.getBoolean("bris_agency"));
            invoice.getBranchSetting().getBranch().getCurrency().setId(rs.getInt("brcurrency_id"));
            invoice.getBranchSetting().setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
        } catch (Exception e) {
        }
        try {
            invoice.setPriceDifferenceInvoice(new Invoice());
            invoice.getPriceDifferenceInvoice().setId(rs.getInt("invdifferentinvoice_id"));
            invoice.getPriceDifferenceInvoice().getdNumber().setId(rs.getInt("inv1documentnumber_id"));
            invoice.setIsDifferenceDirection(rs.getBoolean("invis_differentdirection"));
            invoice.getPriceDifferenceInvoice().setDocumentNumber(rs.getString("inv1documentnumber"));

            if (invoice.getPriceDifferenceInvoice().getdNumber().getId() > 0) {
                invoice.getPriceDifferenceInvoice().getdNumber().setActualNumber(rs.getInt("inv1documentnumber"));
                invoice.getPriceDifferenceInvoice().setDocumentSerial(rs.getString("inv1documentserial"));
            } else {
                invoice.getPriceDifferenceInvoice().setDocumentSerial(rs.getString("inv1documentserial"));
            }
            invoice.setPriceDifferenceTotalMoney(rs.getBigDecimal("invoicedifferentprice"));
            invoice.setPriceDifferenceTotalPrice(rs.getBigDecimal("invoicedifferenttotalprice"));

        } catch (Exception e) {
        }
        try {
            invoice.getType().setTag(rs.getString("typdname"));
        } catch (Exception e) {
        }
        try {
            invoice.getType().setId(rs.getInt("invtype_id"));
        } catch (Exception e) {
        }
        try {
            invoice.setIsWait(rs.getBoolean("invis_wait"));
            invoice.setWaitInvoiceItemJson(rs.getString("invwaitinvoiceitemjson"));
        } catch (Exception e) {
        }

        try {
            invoice.getBranchSetting().getBranch().setLicenceCode(rs.getString("brlicencecode"));
            if (invoice.isIsPurchase()) {
                invoice.setSapLogIsSend(rs.getBoolean("spinvis_send"));

            } else {
                invoice.setSapLogIsSend(rs.getBoolean("ssinvis_send"));
            }
            invoice.setSapIsSendWaybill(rs.getBoolean("spinvis_sendwaybill"));
        } catch (Exception e) {
        }

        try {
            invoice.setIsOrderConnection(rs.getBoolean("isorderconnection"));
        } catch (Exception e) {
        }

        try {
            invoice.getAccount().setCode(rs.getString("acccode"));
        } catch (Exception e) {
        }
        try {
            invoice.setIsFuel(rs.getBoolean("invis_fuel"));
        } catch (Exception e) {
        }
        
        try {
            invoice.getBranchSetting().setParoUrl(rs.getString("brsparourl"));
            invoice.getBranchSetting().setParoAccountCode(rs.getString("brsparoaccountcode"));
            invoice.getBranchSetting().setParoBranchCode(rs.getString("brsparobranchcode"));
            invoice.getBranchSetting().setParoResponsibleCode(rs.getString("brsparoresponsiblecode"));
            
        } catch (Exception e) {
        }
        
               return invoice;
    }

}
