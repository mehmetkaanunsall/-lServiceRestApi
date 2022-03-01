package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import com.mepsan.marwiz.system.branch.dao.BranchMapper;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class EInvoiceIntegrationDao extends JdbcDaoSupport implements IEInvoiceIntegrationDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<EInvoice> listOfEInvoices(String where, int operationType) {
        String whereIsArchive = "";

        if (operationType == 3) {
            whereIsArchive = whereIsArchive + " AND (lsei.is_archive = TRUE and lsei.id IS NOT NULL) ";
        } else {
            whereIsArchive = whereIsArchive + "  AND ((lsei.is_archive = FALSE and lsei.id IS NOT NULL)  or lsei.id IS NULL) ";
        }

        String sql = " SELECT\n"
                + "                                      inv.id AS invid,\n"
                + "                                      inv.is_purchase AS invis_purchase,\n"
                + "                                      inv.account_id AS invaccount_id,\n"
                + "                                      acc.name AS accname,\n"
                + "                                      acc.title AS acctitle,\n"
                + "                                      acc.is_person AS accis_person,\n"
                + "                                      acc.is_employee AS accis_employee,\n"
                + "                                      acc.phone AS accphone,\n"
                + "                                      acc.email AS accemail,\n"
                + "                                      acc.address AS accaddress,\n"
                + "                                      acc.taxno AS acctaxno,\n"
                + "                                      acc.taxoffice AS acctaxoffice,\n"
                + "                                      acc.balance AS accbalance,\n"
                + "                                      acc.city_id AS acccity_id,\n"
                + "                                      ctyd.name AS ctydname,\n"
                + "                                      acc.country_id AS acccountry_id,\n"
                + "                                      ctrd.name AS ctrdname,\n"
                + "                                      acc.county_id AS acccounty_id,\n"
                + "                                      cnty.name AS cntyname,\n"
                + "                                      acc.taginfo AS acctaginfo,\n"
                + "                                      inv.documentnumber_id AS invdocumentnumber_id,\n"
                + "                                      inv.documentserial AS invdocumentserial,\n"
                + "                                      inv.documentnumber AS invdocumentnumber,\n"
                + "                                      inv.invoicedate AS invinvoicedate,\n"
                + "                                      inv.duedate AS invduedate,\n"
                + "                                      inv.dispatchdate AS invdispatchdate,\n"
                + "                                      inv.dispatchaddress AS invdispatchaddress,\n"
                + "                                      inv.description AS invdescription,\n"
                + "                                      inv.type_id AS invtype_id,\n"
                + "                                      inv.is_periodinvoice AS invis_periodinvoice,\n"
                + "                                      typd.name AS typdname,\n"
                + "                                      inv.status_id AS invstatus_id,\n"
                + "                                      sttd.name AS sttdname,\n"
                + "                                      inv.is_discountrate AS invis_discountrate,\n"
                + "                                      COALESCE(inv.discountrate,0) AS invdiscountrate,\n"
                + "                                      COALESCE(inv.discountprice,0) AS invdiscountprice,\n"
                + "                                      COALESCE(inv.totaldiscount,0) AS invtotaldiscount,\n"
                + "                                      COALESCE(inv.remainingmoney,0) AS invremainingmoney,\n"
                + "                                      COALESCE(inv.totaltax,0) AS invtotaltax,\n"
                + "                                      COALESCE(inv.totalprice,0) AS invtotalprice,\n"
                + "                                      COALESCE(inv.totalmoney,0) AS invtotalmoney,\n"
                + "                                      COALESCE(inv.roundingprice,0) AS invroundingprice,\n"
                + "                                      inv.currency_id AS invcurrency_id,\n"
                + "                                      crr.id AS crrid,\n"
                + "                                      crr.code AS crrcode,\n"
                + "                                      crr.internationalcode AS crrinternationalcode,\n"
                + "                                      crrd.name AS crrdname, \n"
                + "                                      inv.exchangerate AS invexchangerate,\n"
                + "                                      inv.taxpayertype_id AS invtaxpayertype_id, \n"
                + "                                      inv.deliverytype_id AS invdeliverytype_id, \n"
                + "                                      inv.invoicescenario_id AS invinvoicescenario_id,\n"
                + "                                     (SELECT \n"
                + "                                       xmlelement(\n"
                + "                                         name \"invoiceitem\",\n"
                + "                                         xmlagg(\n"
                + "                                        xmlelement(\n"
                + "                                           name \"item\",\n"
                + "                                           xmlforest (\n"
                + "                                           t.inviid AS \"inviid\",\n"
                + "                                           t.inviinvoice_id AS \"inviinvoice_id\",\n"
                + "                                           t.invistock_id AS \"invistock_id\",\n"
                + "                                           t.inviquantity AS \"inviquantity\",\n"
                + "                                           t.inviunitprice AS \"inviunitprice\",\n"
                + "                                           t.invitotalprice AS \"invitotalprice\",\n"
                + "                                           t.invidiscountrate AS \"invidiscountrate\",\n"
                + "                                           t.invidiscountprice AS \"invidiscountprice\",\n"
                + "                                           t.invitaxrate AS \"invitaxrate\",\n"
                + "                                           t.invitotaltax AS \"invitotaltax\",\n"
                + "                                           t.inviexchangerate AS \"inviexchangerate\",\n"
                + "                                           t.stckname AS \"stckname\",\n"
                + "                                           t.untid AS \"untid\",\n"
                + "                                           t.untname AS \"untname\",\n"
                + "                                           t.untinternationalcode AS \"untinternationalcode\",\n"
                + "                                           t.crrid AS \"crrid\",\n"
                + "                                           t.crrcode AS \"crrcode\",\n"
                + "                                           t.crrinternationalcode AS \"crrinternationalcode\",\n"
                + "                                           t.crrdname AS \"crrdname\"\n"
                + "                                          )\n"
                + "                                        )\n"
                + "                                      )\n"
                + "                                     )\n"
                + "                                   FROM (\n"
                + "                                     SELECT\n"
                + "                      		  \n"
                + "                                      invi.id AS inviid,\n"
                + "                                      invi.invoice_id AS inviinvoice_id,\n"
                + "                                      invi.stock_id AS invistock_id,\n"
                + "                                      invi.quantity AS inviquantity,\n"
                + "                                      invi.unitprice AS inviunitprice,\n"
                + "                                      invi.totalprice AS invitotalprice,\n"
                + "                                      invi.discountrate AS invidiscountrate,\n"
                + "                                      invi.discountprice AS invidiscountprice,\n"
                + "                                      invi.taxrate AS invitaxrate,\n"
                + "                                      invi.totaltax AS invitotaltax,\n"
                + "                                      invi.exchangerate AS inviexchangerate,\n"
                + "                                      stck.name AS stckname,\n"
                + "                                      unt.id AS untid,\n"
                + "                                      unt.name AS untname, \n"
                + "                                      unt.internationalcode As untinternationalcode,\n"
                + "                                      crr.id AS crrid,\n"
                + "                                      crr.code AS crrcode,\n"
                + "                                      crr.internationalcode AS crrinternationalcode,\n"
                + "                                      crrd.name AS crrdname \n"
                + "                      	    FROM finance.invoiceitem invi\n"
                + "                                           \n"
                + "                                    LEFT JOIN inventory.stock stck ON(stck.id=invi.stock_id AND stck.deleted=FALSE) \n"
                + "                                    LEFT JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                + "                                    INNER JOIN system.currency crr   ON (crr.id=invi.currency_id AND crr.deleted=FALSE)\n"
                + "                                    INNER JOIN system.currency_dict crrd ON(crrd.currency_id=invi.currency_id AND crrd.language_id = ?)\n"
                + "                                 WHERE invi.deleted=FALSE AND invi.invoice_id=inv.id\n"
                + "                                   ) as t\n"
                + "                                ) AS invoiceitem,\n"
                + "                                 \n"
                + "                                      lsei.id AS lseiid,\n"
                + "                                      lsei.senddata AS lseisenddata,\n"
                + "                                      lsei.invoicestatus AS lseiinvoicestatus,\n"
                + "                                      lsei.invoice_id AS lseiinvoiceid,\n"
                + "                                      lsei.sendbegindate AS lseisendbegindate,\n"
                + "                                      lsei.sendenddate AS lseisendenddate,\n"
                + "                                      lsei.responsecode AS lseiresponsecode,\n"
                + "                                      lsei.gibinvoice AS lseigibinvoice,\n"
                + "                                       lsei.responsedescription AS lseiresponsedescription,\n"
                + "                                       lsei.is_send AS lseiis_send,\n"
                + "                                       lsei.integrationinvoice AS lseiintegrationinvoice  \n"
                + "                          FROM  finance.invoice inv \n"
                + "                                    INNER JOIN general.account acc   ON (acc.id=inv.account_id)\n"
                + "                                    INNER JOIN system.type_dict typd  ON (typd.type_id = inv.type_id AND typd.language_id = ?) \n"
                + "                                    INNER JOIN system.status_dict sttd  ON (sttd.status_id = inv.status_id AND sttd.language_id = ?) \n"
                + "                                    LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=acc.city_id AND ctyd.language_id= ?)\n"
                + "                                    LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=acc.country_id AND ctrd.language_id= ?)\n"
                + "                                    LEFT JOIN system.county cnty ON(cnty.id=acc.county_id) \n"
                + "                                    INNER JOIN system.currency crr   ON (crr.id=inv.currency_id AND crr.deleted=FALSE)\n"
                + "                                    INNER JOIN system.currency_dict crrd ON(crrd.currency_id=inv.currency_id AND crrd.language_id = ?)\n"
                + "                                    LEFT JOIN general.userdata usd ON(usd.id=inv.c_id)\n"
                + "                                    LEFT JOIN log.sendeinvoice lsei ON(inv.id=lsei.invoice_id AND lsei.deleted=false ) \n"
                + "                                    WHERE inv.deleted = false " + whereIsArchive + " AND inv.branch_id = ? AND ((inv.is_purchase = false AND inv.type_id <> 27 AND inv.type_id <> 26) OR (inv.is_purchase=true AND inv.type_id=27))" + where
                + "                                    \n "
                + "                                    ORDER BY  inv.invoicedate DESC ";

        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, params, new EInvoiceIntegrationMapper());
    }

    @Override
    public void insertOrUpdateLog(List<SendEInvoice> integrations, Boolean isStatus) {

        String sql = "SELECT log.process_einvoice (?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try {
            getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    SendEInvoice sei = integrations.get(i);
                    ps.setInt(1, sei.getInvoiceId());
                    ps.setString(2, sei.getSendData());
                    ps.setBoolean(3, sei.isIsSend());
                    ps.setTimestamp(4, new java.sql.Timestamp(sei.getSendBeginDate().getTime()));
                    ps.setTimestamp(5, new java.sql.Timestamp(sei.getSendEndDate().getTime()));
                    ps.setString(6, sei.getResponseCode());
                    ps.setString(7, sei.getResponseDescription());
                    ps.setString(8, sei.getGibInvoice());
                    ps.setString(9, sei.getIntegrationInvoice());
                    ps.setInt(10, sei.getInvoiceStatus());
                    ps.setBoolean(11, isStatus);
                    ps.setInt(12, sessionBean.getUser().getId());
                    ps.setInt(13, sessionBean.getUser().getLastBranch().getId());

                }

                @Override
                public int getBatchSize() {
                    return integrations.size();
                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public List<SendEInvoice> listSendEInvocie() {
        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            where = "where lsei.invoicestatus != 3";
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            where = "where lsei.invoicestatus NOT IN (1000,1200,1300,1400,2000,10)";
        }
        String sql = "SELECT   \n"
                + "                lsei.id AS lseiid,\n"
                + "                lsei.invoice_id AS lseiinvoice_id,\n"
                + "                lsei.senddata AS lseisenddata,\n"
                + "                lsei.sendbegindate AS lseisendbegindate,\n"
                + "                lsei.sendenddate AS lseisendenddate,\n"
                + "                lsei.sendcount AS lseisendcount,\n"
                + "                lsei.responsedescription AS lseiresponsedescription,\n"
                + "                lsei.responsecode AS lseiresponsecode,\n"
                + "                lsei.is_send AS lseiissend,\n"
                + "                lsei.integrationinvoice AS lseiintegrationinvoice,\n"
                + "                lsei.gibinvoice AS lseigibinvoice,\n"
                + "                lsei.invoicestatus AS lseinvoicestatus,\n"
                + "                lsei.branch_id AS lseibranch_id\n"
                + "                      FROM  log.sendeinvoice lsei \n"
                + where + " AND lsei.deleted=false AND lsei.branch_id = ?";

        Object[] params = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, params, new SendEInvoiceMapper());

    }

    @Override
    public BranchSetting bringBranchAdress() {

        String sql = "Select \n"
                + "       br.county_id AS brcounty_id,\n"
                + "       cnty.name AS cntyname,\n"
                + "       br.city_id AS brcity_id,\n"
                + "       ctyd.name AS ctydname,\n"
                + "       br.email AS bremail,\n"
                + "       br.country_id AS brcountry_id,\n"
                + "       ctrd.name AS ctrdname,\n"
                + "       brs.einvoicecount AS brseinvoicecount,\n"
                + "       brs.earchivecount AS brsearchivecount\n"
                + " FROM general.branchsetting brs\n"
                + "                INNER JOIN general.branch br ON(br.id = brs.branch_id)\n"
                + "                LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=br.city_id AND ctyd.language_id= ?)\n"
                + "                LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=br.country_id AND ctrd.language_id= ?)\n"
                + "                LEFT JOIN system.county cnty ON(cnty.id=br.county_id) \n"
                + "                WHERE brs.branch_id=? AND brs.deleted=FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            BranchSetting br = new BranchSetting();
            return br;
        }
    }

    @Override
    public int updateArchive(String ids, int updateType) {
        String sql = "UPDATE log.sendeinvoice SET is_archive=?, u_id=?,u_time=NOW() WHERE id IN( " + ids + ")";
        Object[] param = new Object[]{updateType == 0 ? false : true, sessionBean.getUser().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createLogForArchive(List<EInvoice> listOfInsert) {

        String sql = " INSERT INTO log.sendeinvoice \n"
                + "   (\n"
                + "     invoice_id,\n"
                + "     senddata,\n"
                + "     is_send,\n"
                + "     sendbegindate,\n"
                + "     sendenddate,\n"
                + "     sendcount,\n"
                + "     responsecode,\n"
                + "     responsedescription,\n"
                + "     gibinvoice,\n"
                + "     integrationinvoice,\n"
                + "     invoicestatus,\n"
                + "     branch_id,\n"
                + "     is_archive, \n"
                + "     c_id, \n"
                + "     u_id \n"
                + "   )\n"
                + "   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try {
            getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    EInvoice einvoice = listOfInsert.get(i);

                    if (einvoice.getId() == 0) {
                        ps.setNull(1, java.sql.Types.INTEGER);

                    } else {
                        ps.setInt(1, einvoice.getId());
                    }
                    ps.setString(2, einvoice.getSendEInvoice().getSendData());
                    ps.setBoolean(3, einvoice.getSendEInvoice().isIsSend());
                    ps.setTimestamp(4, null);
                    ps.setTimestamp(5, null);
                    ps.setInt(6, 0);
                    ps.setString(7, einvoice.getSendEInvoice().getResponseCode());
                    ps.setString(8, einvoice.getSendEInvoice().getResponseDescription());
                    ps.setString(9, einvoice.getSendEInvoice().getGibInvoice());
                    ps.setString(10, einvoice.getSendEInvoice().getIntegrationInvoice());
                    ps.setInt(11, einvoice.getSendEInvoice().getInvoiceStatus());
                    ps.setInt(12, sessionBean.getUser().getLastBranch().getId());
                    ps.setBoolean(13, true);
                    ps.setInt(14, sessionBean.getUser().getId());
                    ps.setInt(15, sessionBean.getUser().getId());

                }

                @Override
                public int getBatchSize() {
                    return listOfInsert.size();
                }
            });
        } catch (DataAccessException e) {

            return -((SQLException) e.getCause()).getErrorCode();

        }
        return 1;
    }

}
