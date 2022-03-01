/**
 * Bu sınıf ApplicationService sınfının veritabanı işlemlerini gerçekleştirir.
 *
 *
 * @author Salem walaa Abdulhadie
 *
 * @date   19.01.2018 11:31:16
 */
package com.mepsan.marwiz.general.appllication.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ScheduledJobTrigger;
import com.mepsan.marwiz.general.model.wot.ApplicationList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ApplicationDao extends JdbcDaoSupport implements IApplicationDao {

    @Override
    public String countryListXml() {
        String sql = "select  system.countries() ";
        return getJdbcTemplate().queryForObject(sql, String.class);
    }

    @Override
    public ApplicationList appListXml() {
        String sql = "select * from system.systemconstants()  ";
        return getJdbcTemplate().queryForObject(sql, new ApplicationMapper());
    }

    @Override
    public String appListOfPages() {
        String sql = "select  system.pages()  ";
        return getJdbcTemplate().queryForObject(sql, String.class);
    }

    @Override
    public String modules() {
        String sql = "select  system.modules()  ";
        return getJdbcTemplate().queryForObject(sql, String.class);
    }

    @Override
    public Map bringBranchSettings() {
        String sql = "SELECT \n"
                  + "  id,\n"
                  + "  branch_id,\n"
                  + "  printpaymenttype,\n"
                  + "  authpaymenttype,\n"
                  + "  sleeptime,\n"
                  + "  is_managerdiscount,\n"
                  + "  is_managerreturn,\n"
                  + "  is_cashierpumpscreen,\n"
                  + "  is_documentcreditnow,\n"
                  + "  is_purchasecontrol,\n"
                  + "  is_shiftcontrol,\n"
                  + "  is_centralintegration,\n"
                  + "  is_returnwithoutreceipt,\n"
                  + "  is_continuecrerror,\n"
                  + "  is_foreigncurrency, \n"
                  + "  is_unitpriceaffectedbydiscount,\n"
                  + "  roundingconstraint,\n"
                  + "  purchaseunitpriceupdateoption_id,\n"
                  + "  wsendpoint,\n"
                  + "  wsusername,\n"
                  + "  wspassword,\n"
                  + "  automation_id,\n"
                  + "  automationpassword,\n"
                  + "  automationusername,\n"
                  + "  automationurl,\n"
                  + "  automationbankaccount_id,\n"
                  + "  automationtestkeyword,\n"
                  + "  automationscoreaccount_id,\n"
                  + "  uscipaddress,\n"
                  + "  uscport,\n"
                  + "  uscprotocol,\n"
                  + "  localserveripaddress,\n"
                  + "  pastperiodclosingdate,\n"
                  + "  profitabilitytolerance,\n"
                  + "  erpintegration_id,\n"
                  + "  erpurl,\n"
                  + "  erpusername,\n"
                  + "  erppassword,\n"
                  + "  erptimeout,\n"
                  + "  paroaccountcode ,\n"
                  + "  parobranchcode ,\n"
                  + "  paroresponsiblecode ,\n"
                  + "  is_einvoice , \n"
                  + "  parourl, \n"
                  + "  einvoiceintegrationtype_id, \n"
                  + "  einvoiceaccountcode, \n"
                  + "  einvoiceurl, \n"
                  + "  einvoiceusername, \n"
                  + "  is_minusmainsafe ,\n"
                  + "  is_processpassiveaccount  ,\n"
                  + "  is_showpassiveaccount,\n"
                  + "  is_invoicestocksalepricelist ,\n"
                  + "  einvoicepassword ,\n"
                  + "  einvoiceprefix,\n"
                  + "  earchiveprefix,\n"
                  + "  einvoicetaginfo, \n"
                  + "  shiftcurrencyrounding, \n"
                  + "  is_taxmandatory, \n"
                  + "  specialitem\n"
                  + "\n"
                  + "FROM \n"
                  + "  general.branchsetting where deleted = false ;";
        return getJdbcTemplate().query(sql, new ResultSetExtractor<Map>() {
            @Override
            public Map extractData(ResultSet rs) throws SQLException, DataAccessException {
                HashMap<Integer, BranchSetting> mapRet = new HashMap<Integer, BranchSetting>();
                while (rs.next()) {
                    BranchSetting branchSetting = new BranchSetting();
                    branchSetting.setId(rs.getInt("id"));
                    branchSetting.setBranch(new Branch(rs.getInt("id"), null));
                    branchSetting.setPrintPaymentType(rs.getString("printpaymenttype"));
                    branchSetting.setAuthPaymentType(rs.getString("authpaymenttype"));
                    branchSetting.setSleepTime(rs.getInt("sleeptime"));
                    branchSetting.setIsManagerDiscount(rs.getBoolean("is_managerdiscount"));
                    branchSetting.setIsManagerReturn(rs.getBoolean("is_managerreturn"));
                    branchSetting.setIsDocumentCreditNow(rs.getBoolean("is_documentcreditnow"));
                    branchSetting.setIsPurchaseControl(rs.getBoolean("is_purchasecontrol"));
                    branchSetting.setIsShiftControl(rs.getBoolean("is_shiftcontrol"));
                    branchSetting.setIsCentralIntegration(rs.getBoolean("is_centralintegration"));
                    branchSetting.setIsReturnWithoutReceipt(rs.getBoolean("is_returnwithoutreceipt"));
                    branchSetting.setIsContinueCrError(rs.getBoolean("is_continuecrerror"));
                    branchSetting.setIsForeignCurrency(rs.getBoolean("is_foreigncurrency"));
                    branchSetting.setRoundingConstraint(rs.getBigDecimal("roundingconstraint"));
                    branchSetting.setwSendPoint(rs.getString("wsendpoint"));
                    branchSetting.setWebServiceUserName(rs.getString("wsusername"));
                    branchSetting.setWebServicePassword(rs.getString("wspassword"));
                    branchSetting.setAutomationId(rs.getInt("automation_id"));
                    branchSetting.setAutomationPassword(rs.getString("automationpassword"));
                    branchSetting.setAutomationUserName(rs.getString("automationusername"));
                    branchSetting.setAutomationUrl(rs.getString("automationurl"));
                    branchSetting.getAutomationBankAccount().setId(rs.getInt("automationbankaccount_id"));
                    branchSetting.setAutomationTestKeyword(rs.getString("automationtestkeyword"));
                    branchSetting.getAutomationScoreAccount().setId(rs.getInt("automationscoreaccount_id"));
                    branchSetting.setUscIpAddress(rs.getString("uscipaddress"));
                    branchSetting.setUscPort(rs.getInt("uscport"));
                    branchSetting.setUscProtocol(rs.getInt("uscprotocol"));
                    branchSetting.setLocalServerIpAddress(rs.getString("localserveripaddress"));
                    branchSetting.setPastPeriodClosingDate(rs.getTimestamp("pastperiodclosingdate"));
                    branchSetting.setIsManagerPumpScreen(rs.getBoolean("is_cashierpumpscreen"));
                    branchSetting.setIsUnitPriceAffectedByDiscount(rs.getBoolean("is_unitpriceaffectedbydiscount"));
                    branchSetting.setPurchaseUnitPriceUpdateOptionId(rs.getInt("purchaseunitpriceupdateoption_id"));
                    branchSetting.setProfitabilityTolerance(rs.getBigDecimal("profitabilitytolerance"));
                    branchSetting.setErpIntegrationId(rs.getInt("erpintegration_id"));
                    branchSetting.setErpUrl(rs.getString("erpurl"));
                    branchSetting.setErpUsername(rs.getString("erpusername"));
                    branchSetting.setErpPassword(rs.getString("erppassword"));
                    branchSetting.setErpTimeout(rs.getInt("erptimeout"));
                    branchSetting.setParoAccountCode(rs.getString("paroaccountcode"));
                    branchSetting.setParoBranchCode(rs.getString("parobranchcode"));
                    branchSetting.setParoResponsibleCode(rs.getString("paroresponsiblecode"));
                    branchSetting.setParoUrl(rs.getString("parourl"));
                    branchSetting.setIsEInvoice(rs.getBoolean("is_einvoice"));
                    branchSetting.seteInvoiceIntegrationTypeId(rs.getInt("einvoiceintegrationtype_id"));
                    branchSetting.seteInvoiceAccountCode(rs.getString("einvoiceaccountcode"));
                    branchSetting.seteInvoiceUrl(rs.getString("einvoiceurl"));
                    branchSetting.seteInvoiceUserName(rs.getString("einvoiceusername"));
                    branchSetting.seteInvoicePassword(rs.getString("einvoicepassword"));
                    branchSetting.seteInvoicePrefix(rs.getString("einvoiceprefix"));
                    branchSetting.seteArchivePrefix(rs.getString("earchiveprefix"));
                    branchSetting.seteInvoiceTagInfo(rs.getString("einvoicetaginfo"));
                    branchSetting.setIsMinusMainSafe(rs.getBoolean("is_minusmainsafe"));
                    branchSetting.setIsProcessPassiveAccount(rs.getBoolean("is_processpassiveaccount"));
                    branchSetting.setIsShowPassiveAccount(rs.getBoolean("is_showpassiveaccount"));
                    branchSetting.setIsInvoiceStockSalePriceList(rs.getBoolean("is_invoicestocksalepricelist"));
                    branchSetting.setShiftCurrencyRounding(rs.getInt("shiftcurrencyrounding"));
                    branchSetting.setIsTaxMandatory(rs.getBoolean("is_taxmandatory"));
                    branchSetting.setSpecialItem(rs.getInt("specialitem"));

                    mapRet.put(rs.getInt("branch_id"), branchSetting);

                }
                return mapRet;
            }
        });
    }

    @Override
    public int controlAutomationSettingBranch(int automationId) {
        String sql = "SELECT \n"
                  + "	CASE WHEN (SELECT COUNT(br.id)) > 0 THEN 1 ELSE 0 END\n"
                  + "FROM general.branch br \n"
                  + "INNER JOIN general.branchsetting brcst ON(brcst.branch_id = br.id AND brcst.deleted =FALSE)\n"
                  + "WHERE br.deleted=FALSE AND brcst.automation_id = ?";

        Object[] param = new Object[]{automationId};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int controlStarbucksSettingBranch() {
        String sql = "SELECT \n"
                  + "	CASE WHEN (SELECT COUNT(br.id)) > 0 THEN 1 ELSE 0 END\n"
                  + "FROM general.branch br \n"
                  + "INNER JOIN general.branchsetting brcst ON(brcst.branch_id = br.id AND brcst.deleted =FALSE)\n"
                  + "WHERE br.deleted=FALSE AND brcst.starbucksurl IS NOT NULL";

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Map scheduledJob() {
        String sql = "SELECT\n"
                  + "             gsj.id,\n"
                  + "             gsj.branch_id,\n"
                  + "             gsj.type_id,\n"
                  + "             COALESCE(gsj.name,'') as name,\n"
                  + "		COALESCE(gsj.days,'') as days,\n"
                  + "             COALESCE(gsj.daysdate,'') as daysdate\n"
                  + "         FROM\n"
                  + "             general.scheduledjob gsj\n"
                  + "         WHERE\n"
                  + "             gsj.status_id=62 and gsj.type_id=99 and gsj.deleted=false;";
        return getJdbcTemplate().query(sql, new ResultSetExtractor<Map>() {
            @Override
            public Map extractData(ResultSet rs) throws SQLException, DataAccessException {
                HashMap<Integer, ScheduledJobTrigger> mapRet = new HashMap<Integer, ScheduledJobTrigger>();
                while (rs.next()) {
                    ScheduledJobTrigger scheduledJobTrigger = new ScheduledJobTrigger();
                    scheduledJobTrigger.setId(rs.getInt("id"));
                    scheduledJobTrigger.setBranch_id(rs.getInt("branch_id"));
                    scheduledJobTrigger.setType_id(rs.getInt("type_id"));
                    scheduledJobTrigger.setName(rs.getString("name"));
                    scheduledJobTrigger.setDays(rs.getString("days"));
                    scheduledJobTrigger.setDaysDate(rs.getString("daysdate"));

                    mapRet.put(rs.getInt("id"), scheduledJobTrigger);

                }
                return mapRet;
            }
        });
    }

    @Override
    public ScheduledJobTrigger findScheduledJob(int id) {
        String sql = "SELECT\n"
                  + "             gsj.id,\n"
                  + "             gsj.branch_id,\n"
                  + "             gsj.type_id,\n"
                  + "             COALESCE(gsj.name,'') as name,\n"
                  + "		COALESCE(gsj.days,'') as days,\n"
                  + "             COALESCE(gsj.daysdate,'') as daysdate\n"
                  + "         FROM\n"
                  + "             general.scheduledjob gsj\n"
                  + "         WHERE\n"
                  + "             gsj.status_id=62 and gsj.type_id=99 and gsj.deleted=false and gsj.id = " + id + ";";
        return getJdbcTemplate().query(sql, new ResultSetExtractor<ScheduledJobTrigger>() {
            @Override
            public ScheduledJobTrigger extractData(ResultSet rs) throws SQLException, DataAccessException {
                ScheduledJobTrigger scheduledJobTrigger = new ScheduledJobTrigger();
                while (rs.next()) {

                    scheduledJobTrigger.setId(rs.getInt("id"));
                    scheduledJobTrigger.setBranch_id(rs.getInt("branch_id"));
                    scheduledJobTrigger.setType_id(rs.getInt("type_id"));
                    scheduledJobTrigger.setName(rs.getString("name"));
                    scheduledJobTrigger.setDays(rs.getString("days"));
                    scheduledJobTrigger.setDaysDate(rs.getString("daysdate"));

                }
                return scheduledJobTrigger;
            }
        });
    }

    @Override
    public int controlBranchIntegration() {
        String sql = "SELECT \n"
                  + "	CASE WHEN (SELECT COUNT(br.id)) > 0 THEN 1 ELSE 0 END\n"
                  + "FROM general.branch br \n"
                  + "INNER JOIN general.branchintegration bri ON(bri.branch_id = br.id AND bri.deleted =FALSE)\n"
                  + "WHERE br.deleted=FALSE AND bri.type_id =1";

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
