/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 02:07:03
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BranchSettingMapper implements RowMapper<BranchSetting> {

    @Override
    public BranchSetting mapRow(ResultSet rs, int i) throws SQLException {
        BranchSetting branchSetting = new BranchSetting();
        try {
            branchSetting.setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setId(rs.getInt("brsid"));
            branchSetting.setAuthPaymentType(rs.getString("brsauthpaymenttype"));
            branchSetting.setPrintPaymentType(rs.getString("brsprintpaymenttype"));
            branchSetting.setIsManagerDiscount(rs.getBoolean("brsis_managerdiscount"));
            branchSetting.setIsManagerReturn(rs.getBoolean("brsis_managerreturn"));
            branchSetting.setAutomationId(rs.getInt("brsautomation_id"));
            branchSetting.setIsManagerPumpScreen(rs.getBoolean("brsis_managerpumpscreen"));

            branchSetting.setSleepTime(rs.getInt("brssleeptime"));
            branchSetting.setUscIpAddress(rs.getString("uscipaddress"));
            branchSetting.setUscPort(rs.getInt("brsuscport"));
            branchSetting.setUscProtocol(rs.getInt("brsuscprotocol"));

            branchSetting.getIntegrationCategorization().setId(rs.getInt("brsintegrationcategorization_id"));
            branchSetting.setIsReturnWithoutReceipt(rs.getBoolean("brsis_returnwithoutreceipt"));
            branchSetting.setIsContinueCrError(rs.getBoolean("brsis_continuecrerror"));
            branchSetting.setIsForeignCurrency(rs.getBoolean("brsisforeigncurrency"));
            branchSetting.setRoundingConstraint(rs.getBigDecimal("brsroundingconstraint"));
            branchSetting.setPastPeriodClosingDate(rs.getTimestamp("brspastperiodclosingdate"));

            branchSetting.setProfitabilityTolerance(rs.getBigDecimal("brsprofitabilitytolerance"));

            if (rs.getString("brsauthpaymenttype") != null) {
                branchSetting.getlAuthPaymentType().addAll(StaticMethods.stringToList(rs.getString("brsauthpaymenttype"), ","));
            }
            if (rs.getString("brsprintpaymenttype") != null) {
                branchSetting.getlPrintPaymentType().addAll(StaticMethods.stringToList(rs.getString("brsprintpaymenttype"), ","));
            }

        } catch (Exception e) {
        }

        try {
            branchSetting.setIsEInvoice(rs.getBoolean("brsis_einvoice"));

        } catch (Exception e) {
        }
        try {
            branchSetting.setLocalServerIpAddress(rs.getString("brslocalserveripaddress"));
        } catch (Exception e) {
        }
        try {
            branchSetting.getBranch().setName(rs.getString("brname"));
        } catch (Exception e) {
        }

        try {
            branchSetting.getBranch().setId(rs.getInt("brid"));
        } catch (Exception e) {
        }

        try {
            branchSetting.getBranch().setLicenceCode(rs.getString("brlicencecode"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setIsOpen(rs.getBoolean("isopen"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setIsDocumentCreditNow(rs.getBoolean("brsis_documentcreditnow"));
            branchSetting.setIsPurchaseControl(rs.getBoolean("brsis_purchasecontrol"));
            branchSetting.setIsShiftControl(rs.getBoolean("brsis_shiftcontrol"));
        } catch (Exception e) {
        }

        try { // erp ayarları sekmesi için F
            branchSetting.setErpIntegrationId(rs.getInt("brserpintegration_id"));
            branchSetting.setErpUrl(rs.getString("brserpurl"));
            branchSetting.setErpUsername(rs.getString("brserpusername"));
            branchSetting.setErpPassword(rs.getString("brserppassword"));
            branchSetting.setErpTimeout(rs.getInt("brserptimeout"));
            branchSetting.setErpEntegrationCode(rs.getString("brserpintegrationcode"));

        } catch (Exception e) {
        }

        try { // Otomasyon web servis ayarları  
            branchSetting.setAutomationUrl(rs.getString("brsautomationurl"));
            branchSetting.setAutomationUserName(rs.getString("brsautomationusername"));
            branchSetting.setAutomationPassword(rs.getString("brsautomationpassword"));
            branchSetting.setAutomationTimeOut(rs.getInt("brsautomationtimeout"));
        } catch (Exception e) {
        }

        try { // Starbucks entegrasyon ayarları 

            branchSetting.setStarbucksApiKey(rs.getString("brsstarbucksapikey"));
            branchSetting.setStarbucksMachicneName(rs.getString("brsstarbucksmachinename"));
            branchSetting.setStarbucksWebServiceUrl(rs.getString("brsstarbucksurl"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setParoAccountCode(rs.getString("brsparoaccountcode"));
            branchSetting.setParoBranchCode(rs.getString("brsparobranchcode"));
            branchSetting.setParoResponsibleCode(rs.getString("brsparoresponsiblecode"));

        } catch (Exception e) {
        }
        try {
            branchSetting.setParoUrl(rs.getString("brsparourl"));
        } catch (Exception e) {
        }
        try { // yıkama makinesi ayarları
            branchSetting.setIsAllBranch(rs.getBoolean("brsis_allbranch"));
            branchSetting.setWashingMachicneUrl(rs.getString("brswashingurl"));
            branchSetting.setWashingMachicneOfflineUrl(rs.getString("brswashingofflineurl"));
            branchSetting.setWashingMachicneUsername(rs.getString("brswashingusername"));
            branchSetting.setWashingMachicnePassword(rs.getString("brswashingpassword"));
            branchSetting.setWashingTypeId(rs.getInt("brswashingtypeid"));
            branchSetting.setIsWashingSaleZReport(rs.getBoolean("brsis_washingsalezreport"));
        } catch (Exception e) {
        }
        try { // E-Fatura Web Servis Ayarları
            branchSetting.seteInvoiceIntegrationTypeId(rs.getInt("brseinvoiceintegrationtype_id"));
            branchSetting.seteInvoiceAccountCode(rs.getString("brseinvoiceaccountcode"));
            branchSetting.seteInvoiceUrl(rs.getString("brseinvoiceurl"));
            branchSetting.seteInvoiceUserName(rs.getString("brseinvoiceusername"));
            branchSetting.seteInvoicePassword(rs.getString("brseinvoicepassword"));
            branchSetting.seteInvoicePrefix(rs.getString("brseinvoiceprefix"));
            branchSetting.seteArchivePrefix(rs.getString("brsearchiveprefix"));
            branchSetting.seteInvoiceTagInfo(rs.getString("brseinvoicetaginfo"));
        } catch (Exception e) {
        }
        try { // Sistem Ayarlarına Ek             
            branchSetting.setIsShowPassiveAccount(rs.getBoolean("brsis_showpassiveaccount"));
            branchSetting.setIsProcessPassiveAccount(rs.getBoolean("brsis_processpassiveaccount"));
            branchSetting.setIsMinusMainSafe(rs.getBoolean("brsis_minusmainsafe"));
            branchSetting.setIsTaxMandatory(rs.getBoolean("brsis_taxmandatory"));
            branchSetting.setIsCashierEnterCashShift(rs.getBoolean("brsis_cashierentercashshift"));

        } catch (Exception e) {
        }

        try {
            branchSetting.setIsProductRemoval(rs.getBoolean("brsis_productremoval"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setWsConnectionTimeOut(rs.getInt("brswsconnectiontimeout"));
            branchSetting.setWsRequestTimeOut(rs.getInt("brswsrequesttimeout"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setParoConnectionTimeOut(rs.getInt("brsparoconnectiontimeout"));
            branchSetting.setParoRequestTimeOut(rs.getInt("brsparorequesttimeout"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setApplicationServerUrl(rs.getString("brsapplicationserverurl"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setPrintSaleType(rs.getString("brsprintsaletype"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setIsManagerAutomatProduct(rs.getBoolean("brsis_managerautomatproduct"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setAutomationTestKeyword(rs.getString("brsautomationtestkeyword"));
            branchSetting.getAutomationBankAccount().setId(rs.getInt("brsautomationbankaccount_id"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setAutoFileCreateUrl(rs.getString("brsautofilecreateurl"));
            branchSetting.setAutoFileCreateUserName(rs.getString("brsautofilecreateusername"));
            branchSetting.setAutoFileCreatePassword(rs.getString("brsautofilecreatepassword"));
            branchSetting.setAutoFileCreateType(rs.getInt("brsautofilecreatetype_id"));
        } catch (Exception e) {
        }

        try {
            branchSetting.seteInvoiceCount(rs.getInt("brseinvoicecount"));
            branchSetting.seteArchiveCount(rs.getInt("brsearchivecount"));
        } catch (Exception e) {
        }

        try {
            branchSetting.getBranch().getCounty().setId(rs.getInt("brcounty_id"));
            branchSetting.getBranch().getCounty().setName(rs.getString("cntyname"));
            branchSetting.getBranch().getCity().setId(rs.getInt("brcity_id"));
            branchSetting.getBranch().getCity().setTag(rs.getString("ctydname"));
            branchSetting.getBranch().getCountry().setId(rs.getInt("brcountry_id"));
            branchSetting.getBranch().getCountry().setTag(rs.getString("ctrdname"));
            branchSetting.seteInvoiceCount(rs.getInt("brseinvoicecount"));
            branchSetting.seteArchiveCount(rs.getInt("brsearchivecount"));
            branchSetting.getBranch().setMail(rs.getString("bremail"));

        } catch (Exception e) {
        }
        try {
            branchSetting.getAutomationScoreAccount().setId(rs.getInt("brsautomationscoreaccount_id"));
            branchSetting.getAutomationScoreAccount().setIsPerson(rs.getBoolean("accis_person"));
            branchSetting.getAutomationScoreAccount().setName(rs.getString("accname"));
            branchSetting.getAutomationScoreAccount().setTitle(rs.getString("acctitle"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setIsPurchaseInvoiceProductSupplierUpdate(rs.getBoolean("brsis_purchaseinvoiceproductsupplierupdate"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setIsInvoiceStockSalePriceList(rs.getBoolean("brsis_invoicestocksalepricelist"));
        } catch (Exception e) {
        }
        try {
            branchSetting.getBranch().getCurrency().setId(rs.getInt("brncurrency_id"));
        } catch (Exception e) {
        }
        try {
            branchSetting.getBranch().setName(rs.getString("brnname"));
            branchSetting.getBranch().setAddress(rs.getString("brnaddress"));
            branchSetting.getBranch().setPhone(rs.getString("brnphone"));
            branchSetting.getBranch().setMail(rs.getString("brnemail"));
            branchSetting.getBranch().setTaxNo(rs.getString("brntaxno"));
            branchSetting.getBranch().setTaxOffice(rs.getString("brntaxoffice"));
        } catch (Exception e) {
        }
        try {
            branchSetting.getBranch().setIsCentral(rs.getBoolean("brnis_central"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setPurchaseUnitPriceUpdateOptionId(rs.getInt("brspurchaseunitpriceupdateoption_id"));
        } catch (Exception e) {
        }
        try {
            branchSetting.getBranch().setIsAgency(rs.getBoolean("brnis_agency"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setwSendPoint(rs.getString("brswsendpoint"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setWebServiceUserName(rs.getString("brswsusername"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setWebServicePassword(rs.getString("brswspassword"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setIsErpUseShift(rs.getBoolean("brsis_erpuseshift"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setIsCashierEnterBarcode(rs.getBoolean("brsis_cashierenterbarcode"));
            branchSetting.setIsCashierUseTheSyncButton(rs.getBoolean("brsis_cashierusethesyncbutton"));
        } catch (Exception e) {
        }

        try {
            branchSetting.getBranch().setConceptType(rs.getInt("brnconcepttype_id"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setIsCashierEnterQuantity(rs.getBoolean("brsis_cashierenterquantity"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setMagiclickUrl(rs.getString("brsmagiclickurl"));
            branchSetting.setMagiclickConsumerKey(rs.getString("brsmagiclickconsumerkey"));
            branchSetting.setMagiclickConsumerSecret(rs.getString("brsmagiclickconsumersecret"));
            branchSetting.setMagiclickTimeOut(rs.getInt("brsmagiclicktimeout"));
        } catch (Exception e) {

        }
        try {
            branchSetting.setIsUnitPriceAffectedByDiscount(rs.getBoolean("brsis_unitpriceaffectedbydiscount"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setMinStockQuantity(rs.getBigDecimal("brsminstockquantity"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setwSEEndPoint(rs.getString("brswsexternalendpoint"));
        } catch (Exception e) {
        }
        try {
            branchSetting.setParoCenterAccountCode(rs.getString("brsparocenteraccountcode"));
            branchSetting.setParoCenterResponsibleCode(rs.getString("brsparocenterresponsiblecode"));
        } catch (Exception e) {
        }

        try {
            branchSetting.setIsCashierStockInventory(rs.getBoolean("brsiscashierstockinventory"));
            branchSetting.getAutomationPaymentBankAccount().setId(rs.getInt("brsautomationpaymentbankaccountid"));

        } catch (Exception e) {
        }

        try {
            branchSetting.setShiftCurrencyRounding(rs.getInt("brsshiftcurrencyrounding"));

        } catch (Exception e) {
        }
        try {
            branchSetting.setSpecialItem(rs.getInt("brsspecialitem"));
        } catch (Exception e) {
        }

        try { // Araca Teslim Ayarları            
            branchSetting.setIsPassiveGet(rs.getBoolean("brsis_passiveget"));
            branchSetting.setGetInOperableDate(rs.getTimestamp("brsgetinoperabledate"));
            branchSetting.setOrderDeliveryRate(rs.getBigDecimal("brsorderdeliveryrate"));
            branchSetting.setGeneralOrderDeliveryRate(rs.getBigDecimal("brsgeneralorderdeliveryrate"));

        } catch (Exception e) {
        }
        
        

        return branchSetting;
    }
}
