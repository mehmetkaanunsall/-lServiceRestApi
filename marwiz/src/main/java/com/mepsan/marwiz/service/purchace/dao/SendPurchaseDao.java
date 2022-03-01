/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 08:48:40
 */
package com.mepsan.marwiz.service.purchace.dao;

import com.mepsan.marwiz.general.model.log.SendPurchase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendPurchaseDao extends JdbcDaoSupport implements ISendPurchaseDao {

    @Override
    public List<SendPurchase> findAll() {
        String sql
                = "SELECT\n"
                + "	sp.id as spid,\n"
                + "	sp.invoice_id as spinvoiceid,\n"
                + "	sp.senddata as spsenddata,\n"
                + "	sp.is_send as spissend,\n"
                + "	sp.sendbegindate as spsendbegindate,\n"
                + "	sp.sendenddate as spsendenddate,\n"
                + "	sp.sendcount as spsendcount,\n"
                + "	sp.c_id as spcid,\n"
                + "	sp.c_time as spctime,\n"
                + "	sp.u_id as  spuid,\n"
                + "	sp.u_time as sputime,\n"
                + "	sp.deleted as spdeleted,\n"
                + "	sp.d_time as spdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	sp.licencecode as splicencecode\n"
                + "FROM \n"
                + "	log.sendpurchase sp\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=sp.branch_id)\n"
                + "WHERE\n"
                + "	sp.deleted=FALSE\n"
                + "ORDER BY sp.id\n";

        List<SendPurchase> sendPurchases = new ArrayList<>();
        try {
            sendPurchases = getJdbcTemplate().query(sql, new SendPurchaseMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPurchase.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendPurchases;
    }

    @Override
    public List<SendPurchase> findNotSendedAll() {
        //5 dakika önceki kayıtlar seçilir. Çift gönderimi minimuma indirmek için
        String sql
                = "SELECT\n"
                + " 	sp.id as spid,\n"
                + "	sp.invoice_id as spinvoiceid,\n"
                + "	sp.senddata as spsenddata,\n"
                + "	sp.is_send as spissend,\n"
                + "	sp.sendbegindate as spsendbegindate,\n"
                + "	sp.sendenddate as spsendenddate,\n"
                + "	sp.sendcount as spsendcount,\n"
                + "	sp.c_id as spcid,\n"
                + "	sp.c_time as spctime,\n"
                + "	sp.u_id as  spuid,\n"
                + "	sp.u_time as sputime,\n"
                + "	sp.deleted as spdeleted,\n"
                + "	sp.d_time as spdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	sp.licencecode as splicencecode\n"
                + "FROM \n"
                + "	log.sendpurchase sp\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=sp.branch_id)\n"
                + "WHERE\n"
                + "	sp.deleted=FALSE\n"
                + "	AND sp.is_send=FALSE\n"
                + "	AND sp.c_time<(NOW()- interval '5 minute')\n"
                + "	ORDER BY sp.id LIMIT 200\n";
        List<SendPurchase> sendPurchases = new ArrayList<>();
        try {
            sendPurchases = getJdbcTemplate().query(sql, new SendPurchaseMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPurchaseDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendPurchases;
    }

    @Override
    public SendPurchase findByInvoiceId(int invoiceId) {
        String sql
                = "SELECT\n"
                + " 	sp.id as spid,\n"
                + "	sp.invoice_id as spinvoiceid,\n"
                + "	sp.senddata as spsenddata,\n"
                + "	sp.is_send as spissend,\n"
                + "	sp.sendbegindate as spsendbegindate,\n"
                + "	sp.sendenddate as spsendenddate,\n"
                + "	sp.sendcount as spsendcount,\n"
                + "	sp.c_id as spcid,\n"
                + "	sp.c_time as spctime,\n"
                + "	sp.u_id as  spuid,\n"
                + "	sp.u_time as sputime,\n"
                + "	sp.deleted as spdeleted,\n"
                + "	sp.d_time as spdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	sp.licencecode as splicencecode\n"
                + "FROM \n"
                + "	log.sendpurchase sp\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=sp.branch_id)\n"
                + "WHERE\n"
                + "	sp.invoice_id=?\n"
                + "	AND sp.deleted=FALSE\n"
                + "ORDER BY sp.id DESC\n"
                + "	LIMIT 1\n";
        Object[] param = new Object[]{invoiceId};
        SendPurchase sendPurchase = null;
        try {
            sendPurchase = getJdbcTemplate().queryForObject(sql, param, new SendPurchaseMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPurchaseDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendPurchase;
    }

    @Override
    public int updateSendPurchaseResult(SendPurchase sendPurchase) {
        String sql
                = "UPDATE \n"
                + "	log.sendpurchase \n"
                + "SET \n"
                + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                + "	response = ?,\n"
                + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                + "	sendenddate = NOW(),\n"
                + "	u_time = NOW(),\n"
                + "	sendcount = sendcount+1\n"
                + "WHERE \n"
                + "	id = ?";
        Object[] param = new Object[]{sendPurchase.isIssend(), sendPurchase.getResponse(), sendPurchase.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }  

}
