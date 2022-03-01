/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 02:05:53
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BranchSettingDao extends JdbcDaoSupport implements IBranchSettingDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public BranchSetting find(BranchSetting obj) {
        String sql = "SELECT \n"
                + "                 brs.id AS brsid,\n"
                + "                 brs.authpaymenttype AS brsauthpaymenttype,\n"
                + "                 brs.printpaymenttype AS brsprintpaymenttype,\n"
                + "                 brs.is_managerdiscount AS brsis_managerdiscount,\n"
                + "                 brs.is_managerreturn AS brsis_managerreturn, \n"
                + "                 brs.is_returnwithoutreceipt AS brsis_returnwithoutreceipt,\n"
                + "                 brs.is_continuecrerror AS brsis_continuecrerror, \n"
                + "                 COALESCE(brs.roundingconstraint,0) AS brsroundingconstraint, \n"
                + "                 brs.pastperiodclosingdate AS brspastperiodclosingdate,\n"
                + "                 brs.getinoperabledate AS brsgetinoperabledate,\n"
                + "                 brs.is_passiveget AS brsis_passiveget,\n"
                + "                 brs.generalorderdeliveryrate AS brsgeneralorderdeliveryrate,\n"
                + "                 brs.orderdeliveryrate AS brsorderdeliveryrate,\n"
                + "                 COALESCE(brs.automation_id,0) AS brsautomation_id,\n"
                + "                 brs.is_cashierpumpscreen AS brsis_managerpumpscreen,\n"
                + "                 brs.is_centralintegration AS brsis_centralintegration,\n"
                + "                 brs.sleeptime AS brssleeptime,\n"
                + "                 COALESCE(brs.uscipaddress,'') AS uscipaddress,\n"
                + "                 brs.uscport AS brsuscport,\n"
                + "                 brs.uscprotocol AS brsuscprotocol,\n"
                + "                 brs.wsusername AS brswsusername,\n"
                + "                 brs.wspassword AS brswspassword,\n"
                + "                 brs.integrationcategorization_id AS brsintegrationcategorization_id, \n"
                + "                 brs.localserveripaddress AS brslocalserveripaddress,\n"
                + "                 brs.wsendpoint AS brswsendpoint, \n"
                + "                 brs.is_documentcreditnow AS brsis_documentcreditnow, \n"
                + "                 brs.is_purchasecontrol AS brsis_purchasecontrol,\n"
                + "                 brs.is_shiftcontrol AS brsis_shiftcontrol,\n"
                + "                 brs.profitabilitytolerance as brsprofitabilitytolerance,\n"
                + "                 brs.is_unitpriceaffectedbydiscount  AS brsis_unitpriceaffectedbydiscount , \n"
                + "                 brs.is_foreigncurrency  AS brsisforeigncurrency , \n"
                + "                 brs.is_einvoice AS brsis_einvoice, \n"
                + "                 brs.purchaseunitpriceupdateoption_id AS brspurchaseunitpriceupdateoption_id, \n"
                + "                 brs.erpurl as brserpurl,\n"
                + "                 brs.erpintegration_id as brserpintegration_id,\n"
                + "                 brs.erpusername as brserpusername,\n"
                + "                 brs.erppassword as brserppassword,\n"
                + "                 brs.erptimeout as brserptimeout,\n"
                + "                 brs.erpintegrationcode as brserpintegrationcode,\n"
                + "                 brs.automationurl as brsautomationurl,\n"
                + "                 brs.automationusername as brsautomationusername,\n"
                + "                 brs.automationpassword as brsautomationpassword,\n"
                + "                 brs.automationtimeout as brsautomationtimeout,\n"
                + "                 brs.starbucksapikey as brsstarbucksapikey,\n"
                + "                 brs.starbucksmachinename as brsstarbucksmachinename,\n"
                + "                 brs.starbucksurl as brsstarbucksurl,\n"
                + "                 brs.paroaccountcode AS brsparoaccountcode,\n"
                + "                 brs.parobranchcode AS brsparobranchcode,\n"
                + "                 brs.paroresponsiblecode AS brsparoresponsiblecode,\n"
                + "                 brs.is_allbranch AS brsis_allbranch,\n"
                + "                 brs.parourl AS brsparourl,\n"
                + "                 brs.einvoiceintegrationtype_id AS brseinvoiceintegrationtype_id,\n"
                + "                 brs.einvoiceaccountcode AS brseinvoiceaccountcode,\n"
                + "                 brs.einvoiceurl AS brseinvoiceurl,\n"
                + "                 brs.einvoiceusername AS brseinvoiceusername,\n"
                + "                 brs.einvoicepassword AS brseinvoicepassword,\n"
                + "                 brs.einvoiceprefix AS brseinvoiceprefix,\n"
                + "                 brs.earchiveprefix AS brsearchiveprefix,\n"
                + "                 brs.einvoicetaginfo AS brseinvoicetaginfo,\n"
                + "                 brs.is_showpassiveaccount as brsis_showpassiveaccount, \n"
                + "                 brs.is_processpassiveaccount as brsis_processpassiveaccount, \n"
                + "                 brs.is_minusmainsafe as brsis_minusmainsafe , \n"
                + "                 brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "                 brs.is_productremoval AS brsis_productremoval,\n"
                + "                 brs.is_taxmandatory AS brsis_taxmandatory,\n"
                + "                 brs.applicationserverurl AS brsapplicationserverurl,\n"
                + "                 brs.is_cashierentercashshift AS brsis_cashierentercashshift,\n"
                + "                 brs.wsconnectiontimeout AS brswsconnectiontimeout,\n"
                + "                 brs.wsrequesttimeout AS brswsrequesttimeout,\n"
                + "                 brs.paroconnectiontimeout AS brsparoconnectiontimeout,\n"
                + "                 brs.parorequesttimeout AS brsparorequesttimeout,\n"
                + "                 brs.automationbankaccount_id AS brsautomationbankaccount_id,\n"
                + "                 brs.automationtestkeyword AS brsautomationtestkeyword,\n"
                + "                 brs.autofilecreateurl AS brsautofilecreateurl,\n"
                + "                 brs.autofilecreateusername AS brsautofilecreateusername,\n"
                + "                 brs.autofilecreatepassword AS brsautofilecreatepassword,\n"
                + "                 brs.autofilecreatetype_id AS brsautofilecreatetype_id,\n"
                + "                 brs.automationscoreaccount_id AS brsautomationscoreaccount_id,\n"
                + "                 brs.is_purchaseinvoiceproductsupplierupdate AS brsis_purchaseinvoiceproductsupplierupdate,\n"
                + "                 acc.name AS accname,\n"
                + "                 acc.title AS acctitle,\n"
                + "                 acc.is_person AS accis_person,\n"
                + "                  ( SELECT CASE WHEN EXISTS \n"
                + "                         		(SELECT stck.centerstock_id FROM inventory.stockinfo stcki \n"
                + "                    				INNER JOIN inventory.stock stck ON(stck.id=stcki.stock_id AND stck.deleted=FALSE AND stck.status_id=3 AND stcki.is_passive = TRUE)\n"
                + "                    				WHERE  stcki.deleted=FALSE AND stcki.branch_id=? AND stck.centerstock_id IS NOT NULL) \n"
                + "                         THEN true ELSE false END ) as isopen,\n"
                + "                 brs.is_managerautomatproduct AS brsis_managerautomatproduct,\n"
                + "                 brs.printsaletype AS brsprintsaletype,\n "
                + "                 brs.einvoicecount AS brseinvoicecount,\n "
                + "                 brs.earchivecount AS brsearchivecount, \n "
                + "                 brs.is_erpuseshift AS brsis_erpuseshift, \n"
                + "                 brs.is_cashierenterbarcode AS brsis_cashierenterbarcode,\n"
                + "                 brs.is_cashierusethesyncbutton AS brsis_cashierusethesyncbutton,\n"
                + "                 brs.is_cashierenterquantity AS brsis_cashierenterquantity,\n"
                + "                 brs.magiclickurl AS brsmagiclickurl,\n"
                + "                 brs.magiclickconsumerkey AS brsmagiclickconsumerkey,\n"
                + "                 brs.magiclickconsumersecret AS brsmagiclickconsumersecret,\n"
                + "                 brs.magiclicktimeout AS brsmagiclicktimeout,\n"
                + "                 brs.washingtype_id AS brswashingtypeid,\n"
                + "                 brs.washingurl AS  brswashingurl,\n"
                + "                 brs.washingofflineurl AS brswashingofflineurl,\n"
                + "                 brs.washingusername AS brswashingusername,\n"
                + "                 brs.washingpassword AS brswashingpassword, \n "
                + "                 brs.minstockquantity AS brsminstockquantity,\n"
                + "                 brs.wsexternalendpoint AS brswsexternalendpoint, \n"
                + "                 brs.parocenteraccountcode AS brsparocenteraccountcode,\n"
                + "                 brs.parocenterresponsiblecode AS brsparocenterresponsiblecode,\n"
                + "                 brs.is_cashierstockinventory AS brsiscashierstockinventory,\n"
                + "                 brs.shiftcurrencyrounding AS brsshiftcurrencyrounding,\n"
                + "                 brs.automationpaymentbankaccount_id AS brsautomationpaymentbankaccountid,\n"
                + "                 brs.specialitem AS brsspecialitem,\n"
                + "                 brs.is_washingsalezreport as brsis_washingsalezreport"
                + "             FROM general.branchsetting brs\n"
                + "             LEFT JOIN general.account acc ON(acc.id = brs.automationscoreaccount_id AND acc.deleted=FALSE)\n"
                + "             WHERE brs.branch_id=? AND brs.deleted=False";

        Object[] param = new Object[]{obj.getBranch().getId(), obj.getBranch().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return obj;
        }
    }

    @Override
    public int create(BranchSetting obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(BranchSetting obj) {
        String sql = "";
        Object[] param;
        sql = "UPDATE general.branchsetting "
                + "SET "
                + "authpaymenttype = ?, "
                + "printpaymenttype = ? ,"
                + "is_managerdiscount = ? ,"
                + "is_managerreturn = ? ,"
                + "automation_id = ? ,"
                + "is_cashierpumpscreen = ? ,"
                + "is_documentcreditnow = ? ,  "
                + "is_purchasecontrol = ?,"
                + "is_shiftcontrol = ? , "
                + "is_centralintegration = ? , "
                + "is_returnwithoutreceipt = ?, "
                + "is_continuecrerror = ?, "
                + "roundingconstraint = ?, "
                + "purchaseunitpriceupdateoption_id = ?, "
                + "is_unitpriceaffectedbydiscount = ? ,"
                + "is_foreigncurrency = ? ,"
                + "is_einvoice = ? ,"
                + "sleeptime = ? ,"
                + "localserveripaddress = ? ,"
                + "pastperiodclosingdate = ? ,"
                + "profitabilitytolerance = ?,"
                + "integrationcategorization_id = ?,"
                + "wsendpoint = ? ,"
                + "uscipaddress = ? ,"
                + "uscport = ? ,"
                + "uscprotocol = ? ,"
                + "wsusername = ?,"
                + (obj.getWebServicePassword() != null ? (" wspassword = '" + obj.getWebServicePassword() + "',") : "\n")
                + "erpintegration_id= ?,"
                + "erpurl= ?,"
                + "erpusername= ?,"
                + (obj.getErpPassword() != null ? (" erppassword = '" + obj.getErpPassword() + "',") : "")
                + "erptimeout= ?,"
                + "automationurl = ?,"
                + "automationusername= ?,"
                + (obj.getAutomationPassword() != null ? (" automationpassword = '" + obj.getAutomationPassword() + "',") : "\n")
                + "automationtimeout= ?,"
                + "erpintegrationcode= ?,"
                + "starbucksapikey= ?,"
                + "starbucksmachinename= ?,"
                + "starbucksurl= ?,"
                + "paroaccountcode = ? ,\n"
                + "parobranchcode = ? ,\n"
                + "paroresponsiblecode = ? ,\n"
                + "parourl = ? ,\n"
                + "einvoiceintegrationtype_id = ? ,\n"
                + "einvoiceaccountcode = ? ,\n"
                + "einvoiceurl = ? ,\n"
                + "einvoiceusername = ? ,\n"
                + (obj.geteInvoicePassword() != null ? (" einvoicepassword = '" + obj.geteInvoicePassword() + "',") : "\n")
                + "einvoiceprefix = ? ,\n"
                + "earchiveprefix = ? ,\n"
                + "einvoicetaginfo = ? ,\n"
                + "is_minusmainsafe = ? ,\n"
                + "is_processpassiveaccount = ? ,\n"
                + "is_showpassiveaccount = ? ,\n"
                + "is_invoicestocksalepricelist = ? ,\n"
                + "is_productremoval = ? ,\n"
                + "is_taxmandatory = ? ,\n"
                + "applicationserverurl = ? ,\n"
                + "is_cashierentercashshift = ? ,\n"
                + "wsconnectiontimeout = ? ,\n"
                + "wsrequesttimeout = ? ,\n"
                + "paroconnectiontimeout = ? ,\n"
                + "parorequesttimeout = ? ,\n"
                + "is_managerautomatproduct = ?,\n"
                + "printsaletype = ?, \n"
                + "is_allbranch= ?,\n"
                + "automationbankaccount_id = ?,\n"
                + "automationtestkeyword = ?,\n"
                + "autofilecreateurl = ? ,\n"
                + "autofilecreateusername = ? ,\n"
                + (obj.getAutoFileCreatePassword() != null ? (" autofilecreatepassword = '" + obj.getAutoFileCreatePassword() + "',") : "\n")
                + "autofilecreatetype_id = ?,\n"
                + "einvoicecount = ?, \n"
                + "earchivecount = ?, \n"
                + "automationscoreaccount_id = ?,\n"
                + "is_purchaseinvoiceproductsupplierupdate = ?,\n"
                + "is_erpuseshift = ?,\n"
                + "is_cashierenterbarcode = ?,\n"
                + "is_cashierenterquantity = ?,\n"
                + "magiclickurl = ?,\n"
                + "magiclickconsumerkey = ?,\n"
                + "magiclickconsumersecret = ?,\n"
                + "magiclicktimeout = ?,\n"
                + "is_cashierusethesyncbutton = ?,\n"
                + "u_id= ?,\n "
                + "washingtype_id = ?,\n"
                + "washingurl = ?,\n"
                + "washingofflineurl = ?,\n"
                + "washingusername = ?,\n"
                + "washingpassword = ?,\n"
                + "minstockquantity =?,\n"
                + "automationpaymentbankaccount_id =?,\n"
                + "is_cashierstockinventory=?   ,\n"
                + "shiftcurrencyrounding =? ,\n"
                + "is_washingsalezreport =? , \n"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";

        param = new Object[]{obj.getAuthPaymentType(), obj.getPrintPaymentType(), obj.isIsManagerDiscount(),
            obj.isIsManagerReturn(), obj.getAutomationId(), obj.isIsManagerPumpScreen(), obj.isIsDocumentCreditNow(), obj.isIsPurchaseControl(), obj.isIsShiftControl(), obj.isIsCentralIntegration(), obj.isIsReturnWithoutReceipt(),
            obj.isIsContinueCrError(), obj.getRoundingConstraint(), obj.getPurchaseUnitPriceUpdateOptionId(), obj.isIsUnitPriceAffectedByDiscount(), obj.isIsForeignCurrency(), obj.isIsEInvoice(), obj.getSleepTime(),
            obj.getLocalServerIpAddress(), obj.getPastPeriodClosingDate(), obj.getProfitabilityTolerance(), obj.getIntegrationCategorization().getId() == 0 ? null : obj.getIntegrationCategorization().getId(), obj.getwSendPoint(), obj.getUscIpAddress(), obj.getUscPort(),
            obj.getUscProtocol(), obj.getWebServiceUserName(), obj.getErpIntegrationId() == 0 ? null : obj.getErpIntegrationId(), obj.getErpUrl(), obj.getErpUsername(),
            obj.getErpTimeout(), obj.getAutomationUrl(), obj.getAutomationUserName(), obj.getAutomationTimeOut(), obj.getErpEntegrationCode(),
            obj.getStarbucksApiKey(), obj.getStarbucksMachicneName(), obj.getStarbucksWebServiceUrl(),
            obj.getParoAccountCode(), obj.getParoBranchCode(), obj.getParoResponsibleCode(), obj.getParoUrl(), obj.geteInvoiceIntegrationTypeId(), obj.geteInvoiceAccountCode(), obj.geteInvoiceUrl(), obj.geteInvoiceUserName(), obj.geteInvoicePrefix(), obj.geteArchivePrefix(),
            obj.geteInvoiceTagInfo(), obj.isIsMinusMainSafe(), obj.isIsProcessPassiveAccount(), obj.isIsShowPassiveAccount(), obj.isIsInvoiceStockSalePriceList(), obj.isIsProductRemoval(), obj.isIsTaxMandatory(),
            obj.getApplicationServerUrl(), obj.getIsCashierEnterCashShift(), obj.getWsConnectionTimeOut(), obj.getWsRequestTimeOut(), obj.getParoConnectionTimeOut(), obj.getParoRequestTimeOut(),
            obj.isIsManagerAutomatProduct(), obj.getPrintSaleType(), obj.isIsAllBranch(), obj.getAutomationBankAccount().getId() == 0 ? null : obj.getAutomationBankAccount().getId(), obj.getAutomationTestKeyword(),
            obj.getAutoFileCreateUrl(), obj.getAutoFileCreateUserName(), obj.getAutoFileCreateType() == 0 ? null : obj.getAutoFileCreateType(), obj.geteInvoiceCount(), obj.geteArchiveCount(),
            obj.getAutomationScoreAccount().getId() == 0 ? null : obj.getAutomationScoreAccount().getId(), obj.isIsPurchaseInvoiceProductSupplierUpdate(), obj.isIsErpUseShift(),
            obj.isIsCashierEnterBarcode(), obj.isIsCashierEnterQuantity(), obj.getMagiclickUrl(), obj.getMagiclickConsumerKey(), obj.getMagiclickConsumerSecret(), obj.getMagiclickTimeOut(), obj.isIsCashierUseTheSyncButton(),
            sessionBean.getUser().getId(), obj.getWashingTypeId(), obj.getWashingMachicneUrl(), obj.getWashingMachicneOfflineUrl(), obj.getWashingMachicneUsername(), obj.getWashingMachicnePassword(), obj.getMinStockQuantity(),
            obj.getAutomationPaymentBankAccount().getId() == 0 ? null : obj.getAutomationPaymentBankAccount().getId(), obj.isIsCashierStockInventory(), obj.getShiftCurrencyRounding(),obj.isIsWashingSaleZReport() , obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public BranchSetting findCentralIntegration() {
        String sql = "SELECT \n"
                + "    brs.is_centralintegration AS brsis_centralintegration\n"
                + "FROM general.branchsetting brs\n"
                + "WHERE brs.branch_id=? AND brs.deleted=False";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public BranchSetting findAutomationSetting(Branch obj) {
        String sql = "SELECT \n"
                + "   brs.id AS brsid,\n"
                + "   brs.automationpassword AS brsautomationpassword,\n"
                + "   brs.automationtimeout AS brsautomationtimeout,\n"
                + "   brs.automationurl AS brsautomationurl,\n"
                + "   brs.automationusername AS brsautomationusername,\n"
                + "   br.id AS brid,\n"
                + "   br.licencecode AS brlicencecode\n"
                + "FROM general.branchsetting brs\n"
                + "INNER JOIN general.branch br ON(br.id = brs.branch_id)"
                + "WHERE brs.branch_id=? AND brs.deleted=False";

        Object[] param = new Object[]{obj.getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BranchSetting();
        }
    }

    @Override
    public BranchSetting findLicanseCode() {
        String sql = "SELECT\n"
                + "      brn.id as brid,\n"
                + "      brn.name as brname,\n"
                + "      brn.licencecode as brlicencecode,\n"
                + "      brns.localserveripaddress as brslocalserveripaddress\n"
                + "      FROM general.branch brn\n"
                + "      INNER JOIN general.branchsetting brns ON(brns.branch_id=brn.id AND brns.deleted=FALSE) \n"
                + "      WHERE brn.deleted=FALSE AND brn.id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BranchSetting();
        }
    }

    @Override
    public BranchSetting findStarbucksMachicne() {
        String sql = "SELECT\n"
                + "                 brs.starbucksapikey as  brsstarbucksapikey,\n"
                + "                 brs.starbucksmachinename as  brsstarbucksmachinename,\n"
                + "                 brs.starbucksurl as brsstarbucksurl\n"
                + "                 FROM general.branchsetting brs\n"
                + "                 WHERE brs.branch_id=? AND brs.deleted=False";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BranchSetting();
        }
    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranch() {
        String sql = "SELECT \n"
                + "   brn.id as brid,\n"
                + "   brn.name as brname,\n"
                + "   brns.is_centralintegration as brsis_centralintegration,\n"
                + "   brns.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "   brn.currency_id AS brncurrency_id,\n"
                + "   brn.is_central AS brnis_central,\n"
                + "   brn.is_agency AS brnis_agency,\n"
                + "   brn.concepttype_id AS brnconcepttype_id, \n"
                + "   brns.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount\n"
                + "FROM general.userdata usr\n"
                + "INNER JOIN general.userdata_authorize_con usda ON(usr.id=usda.userdata_id AND usda.deleted=FALSE)\n"
                + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=aut.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.branchsetting brns ON(brns.branch_id=brn.id AND brns.deleted=FALSE)\n"
                + "WHERE usr.deleted=FALSE AND usr.id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());

        return result;

    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranchForInvoiceAuth() {
        String sql = "SELECT \n"
                + "   brn.id as brid,\n"
                + "   brn.name as brname,\n"
                + "   brns.is_centralintegration as brsis_centralintegration,\n"
                + "   brns.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "   brn.currency_id AS brncurrency_id,\n"
                + "   brn.is_central AS brnis_central,\n"
                + "   brns.purchaseunitpriceupdateoption_id AS brspurchaseunitpriceupdateoption_id\n"
                + "FROM general.userdata usr\n"
                + "INNER JOIN general.userdata_authorize_con usda ON(usr.id=usda.userdata_id AND usda.deleted=FALSE)\n"
                + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=aut.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.branchsetting brns ON(brns.branch_id=brn.id AND brns.deleted=FALSE)\n"
                + "WHERE usr.deleted=FALSE AND usr.id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());

        return result;

    }

    @Override
    public BranchSetting findBranchSetting(Branch branch) {
        String sql = "SELECT \n"
                + "   brs.id AS brsid,\n"
                + "   brs.is_centralintegration as brsis_centralintegration,\n"
                + "   brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                + "   brn.currency_id AS brncurrency_id,\n"
                + "   brn.name AS brnname,\n"
                + "   brn.address AS brnaddress,\n"
                + "   brn.phone AS brnphone,\n"
                + "   brn.email AS brnemail,\n"
                + "   brn.taxno AS brntaxno,\n"
                + "   brn.taxoffice AS brntaxoffice,\n"
                + "   brs.is_einvoice AS brsis_einvoice\n"
                + "FROM general.branchsetting brs\n"
                + "INNER JOIN general.branch brn ON(brn.id=brs.branch_id AND brn.deleted=FALSE)\n"
                + "WHERE brs.deleted=FALSE AND brs.branch_id=?";

        Object[] param = new Object[]{branch.getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BranchSetting();
        }

    }

    @Override
    public int updateParoInformation(BranchSetting obj, List<String> pointOfSaleIntegrationList) {
        String sql = "";
        Object[] param;
        String pcIDString = "";

        for (String pc : pointOfSaleIntegrationList) {
            pcIDString = pcIDString + "," + String.valueOf(pc);
        }

        if (!pcIDString.equals("")) {
            pcIDString = pcIDString.substring(1, pcIDString.length());

            sql = "UPDATE general.branchsetting\n"
                    + "SET\n"
                    + "paroaccountcode = ? ,\n"
                    + "parobranchcode = ? ,\n"
                    + "paroresponsiblecode = ? ,\n"
                    + "parourl = ? ,\n"
                    + "paroconnectiontimeout = ? ,\n"
                    + "parorequesttimeout = ? ,\n"
                    + "u_id= ? ,\n"
                    + "u_time= now()\n"
                    + "WHERE id = ? AND deleted = false;\n"
                    + "UPDATE\n"
                    + "   general.pointofsale pp\n"
                    + "SET\n"
                    + "   integrationcode 	= jj.integrationcode,\n"
                    + "   u_id		= ?,\n"
                    + "   u_time		= NOW()\n"
                    + "FROM \n"
                    + "	(SELECT \n"
                    + "          rr.id,\n"
                    + "          tt.integrationcode\n"
                    + "      FROM\n"
                    + "          (\n"
                    + "          SELECT \n"
                    + "              ROW_NUMBER () OVER (ORDER BY pos.id) AS row_id,\n"
                    + "              pos.*\n"
                    + "           FROM\n"
                    + "              general.pointofsale pos\n"
                    + "           WHERE  \n"
                    + "              pos.deleted=FALSE AND pos.branch_id=? AND pos.status_id = 9\n"
                    + "           ORDER BY pos.integrationcode ASC\n"
                    + "          ) rr\n"
                    + "          INNER JOIN (\n"
                    + "          SELECT \n"
                    + "              ROW_NUMBER () OVER (ORDER BY yy.integrationcode) AS row_id,\n"
                    + "              yy.integrationcode \n"
                    + "           FROM\n"
                    + "              (SELECT unnest(string_to_array('" + pcIDString + "', ',')) AS integrationcode) yy\n"
                    + "          ) tt ON (tt.row_id = rr.row_id)\n"
                    + "	) jj \n"
                    + "WHERE \n"
                    + "	jj.id = pp.id";

            param = new Object[]{obj.getParoAccountCode(), obj.getParoBranchCode(), obj.getParoResponsibleCode(), obj.getParoUrl(),
                obj.getParoConnectionTimeOut(), obj.getParoRequestTimeOut(),
                sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getId(), obj.getBranch().getId()};
        } else {
            sql = "UPDATE general.branchsetting\n"
                    + "SET\n"
                    + "paroaccountcode = ? ,\n"
                    + "parobranchcode = ? ,\n"
                    + "paroresponsiblecode = ? ,\n"
                    + "parourl = ? ,\n"
                    + "paroconnectiontimeout = ? ,\n"
                    + "parorequesttimeout = ? ,\n"
                    + "u_id= ? ,\n"
                    + "u_time= now()\n"
                    + "WHERE id = ? AND deleted = false;\n";

            param = new Object[]{obj.getParoAccountCode(), obj.getParoBranchCode(), obj.getParoResponsibleCode(), obj.getParoUrl(),
                obj.getParoConnectionTimeOut(), obj.getParoRequestTimeOut(),
                sessionBean.getUser().getId(), obj.getId()};
        }
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
