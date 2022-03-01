/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 17:27:20
 */
package com.mepsan.marwiz.service.sale.dao;

import com.mepsan.marwiz.general.model.log.SendSale;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendSaleDao extends JdbcDaoSupport implements ISendSaleDao {

    @Override
    public List<SendSale> findAll() {
        String sql
                = "SELECT\n"
                + "	ss.id as ssid,\n"
                + "	ss.sale_id as sssaleid,\n"
                + "	ss.senddata as sssenddata,\n"
                + "	ss.is_send as ssissend,\n"
                + "	ss.sendbegindate as sssendbegindate,\n"
                + "	ss.sendenddate as sssendenddate,\n"
                + "	ss.sendcount as sssendcount,\n"
                + "	ss.c_id as sscid,\n"
                + "	ss.c_time as ssctime,\n"
                + "	ss.u_id as  ssuid,\n"
                + "	ss.u_time as ssutime,\n"
                + "	ss.deleted as ssdeleted,\n"
                + "	ss.d_time as ssdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	ss.licencecode as sslicencecode\n"
                + "FROM \n"
                + "	log.sendsale ss\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=ss.branch_id)\n"
                + "WHERE\n"
                + "	ss.deleted=FALSE\n"
                + "ORDER BY ss.id\n";

        List<SendSale> sendSales = new ArrayList<>();
        try {
            sendSales = getJdbcTemplate().query(sql, new SendSaleMapper());
        } catch (Exception e) {
            Logger.getLogger(SendSaleDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendSales;
    }

    @Override
    public List<SendSale> findNotSendedAll() {
        //5 dakika önceki kayıtlar seçilir. Çift gönderimi minimuma indirmek için
        String sql
                = "SELECT\n"
                + "	ss.id as ssid,\n"
                + "	ss.sale_id as sssaleid,\n"
                + "	ss.senddata as sssenddata,\n"
                + "	ss.is_send as ssissend,\n"
                + "	ss.sendbegindate as sssendbegindate,\n"
                + "	ss.sendenddate as sssendenddate,\n"
                + "	ss.sendcount as sssendcount,\n"
                + "	ss.c_id as sscid,\n"
                + "	ss.c_time as ssctime,\n"
                + "	ss.u_id as  ssuid,\n"
                + "	ss.u_time as ssutime,\n"
                + "	ss.deleted as ssdeleted,\n"
                + "	ss.d_time as ssdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	ss.licencecode as sslicencecode\n"
                + "FROM \n"
                + "	log.sendsale ss\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=ss.branch_id)\n"
                + "WHERE\n"
                + "	ss.is_send=FALSE\n"
                + "	AND ss.deleted=FALSE\n"
                + "	AND ss.c_time<(NOW()- interval '5 minute')\n"
                + "ORDER BY ss.id LIMIT 200\n";
        List<SendSale> sendSales = new ArrayList<>();
        try {
            sendSales = getJdbcTemplate().query(sql, new SendSaleMapper());
        } catch (Exception e) {
            Logger.getLogger(SendSaleDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendSales;
    }

    @Override
    public SendSale findBySaleId(int saleId) {
        String sql
                = "SELECT \n"
                + "	ss.id as ssid,\n"
                + "	ss.sale_id as sssaleid,\n"
                + "	ss.senddata as sssenddata,\n"
                + "	ss.is_send as ssissend,\n"
                + "	ss.sendbegindate as sssendbegindate,\n"
                + "	ss.sendenddate as sssendenddate,\n"
                + "	ss.sendcount as sssendcount,\n"
                + "	ss.c_id as sscid,\n"
                + "	ss.c_time as ssctime,\n"
                + "	ss.u_id as  ssuid,\n"
                + "	ss.u_time as ssutime,\n"
                + "	ss.deleted as ssdeleted,\n"
                + "	ss.d_time as ssdtime,\n"
                + "	brs.wsendpoint as brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	ss.licencecode as sslicencecode\n"
                + "FROM \n"
                + "	log.sendsale ss\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=ss.branch_id)\n"
                + "WHERE\n"
                + "	ss.sale_id=?\n"
                + "	AND ss.deleted=FALSE\n"
                + "	ORDER BY ss.id DESC\n"
                + "	LIMIT 1\n";
        Object[] param = new Object[]{saleId};
        SendSale sendSale = null;
        try {
            sendSale = getJdbcTemplate().queryForObject(sql, param, new SendSaleMapper());
        } catch (Exception e) {
            Logger.getLogger(SendSaleDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendSale;
    }

    @Override
    public int updateSendSaleResult(SendSale sendSale) {
        String sql
                = "UPDATE \n"
                + "	log.sendsale \n"
                + "SET \n"
                + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                + "	response = ?,\n"
                + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                + "	sendenddate = NOW(),\n"
                + "	u_time = NOW(),\n"
                + "	sendcount = sendcount+1\n"
                + "WHERE \n"
                + "	id = ?";
        Object[] param = new Object[]{sendSale.isIssend(), sendSale.getResponse(), sendSale.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    

}
