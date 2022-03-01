/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.dao;

import com.mepsan.marwiz.general.model.log.SendOrder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class SendOrderDao extends JdbcDaoSupport implements ISendOrderDao{

    @Override
    public List<SendOrder> findAll() {
        String sql
                = "SELECT\n"
                + "	so.id as soid,\n"
                + "	so.order_id as soorderid,\n"
                + "	so.senddata as sosenddata,\n"
                + "	so.is_send as soissend,\n"
                + "	so.sendbegindate as sosendbegindate,\n"
                + "	so.sendenddate as sosendenddate,\n"
                + "	so.sendcount as sosendcount,\n"
                + "	so.c_id as socid,\n"
                + "	so.c_time as soctime,\n"
                + "	so.u_id as  souid,\n"
                + "	so.u_time as soutime,\n"
                + "	so.deleted as sodeleted,\n"
                + "	so.d_time as sodtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	so.licencecode as solicencecode\n"
                + "FROM \n"
                + "	log.sendorder so\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=so.branch_id)\n"
                + "WHERE\n"
                + "	so.deleted=FALSE\n"
                + "ORDER BY so.id\n";

        List<SendOrder> sendOrders = new ArrayList<>();
        try {
            sendOrders = getJdbcTemplate().query(sql, new SendOrderMapper());
        } catch (Exception e) {
            Logger.getLogger(SendOrder.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendOrders;
    }

    @Override
    public List<SendOrder> findNotSendedAll() {
         //5 dakika önceki kayıtlar seçilir. Çift gönderimi minimuma indirmek için
        String sql
                = "SELECT\n"
                + " 	so.id as soid,\n"
                + "	so.order_id as soorderid,\n"
                + "	so.senddata as sosenddata,\n"
                + "	so.is_send as soissend,\n"
                + "	so.sendbegindate as sosendbegindate,\n"
                + "	so.sendenddate as sosendenddate,\n"
                + "	so.sendcount as sosendcount,\n"
                + "	so.c_id as socid,\n"
                + "	so.c_time as soctime,\n"
                + "	so.u_id as  souid,\n"
                + "	so.u_time as soutime,\n"
                + "	so.deleted as sodeleted,\n"
                + "	so.d_time as sodtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	so.licencecode as solicencecode\n"
                + "FROM \n"
                + "	log.sendorder so\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=so.branch_id)\n"
                + "WHERE\n"
                + "	so.deleted=FALSE\n"
                + "	AND so.is_send=FALSE\n"
                + "	AND so.c_time<(NOW()- interval '5 minute')\n"
                + "	ORDER BY so.id LIMIT 200\n";
        
        List<SendOrder> sendOrders = new ArrayList<>();
        try {
            sendOrders = getJdbcTemplate().query(sql, new SendOrderMapper());
        } catch (Exception e) {
            Logger.getLogger(SendOrderDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendOrders;
    }

    @Override
    public SendOrder findByOrderId(int orderId) {
    String sql
                = "SELECT\n"
                + " 	so.id as soid,\n"
                + "	so.order_id as soorderid,\n"
                + "	so.senddata as sosenddata,\n"
                + "	so.is_send as soissend,\n"
                + "	so.sendbegindate as sosendbegindate,\n"
                + "	so.sendenddate as sosendenddate,\n"
                + "	so.sendcount as sosendcount,\n"
                + "	so.c_id as socid,\n"
                + "	so.c_time as soctime,\n"
                + "	so.u_id as  souid,\n"
                + "	so.u_time as soutime,\n"
                + "	so.deleted as sodeleted,\n"
                + "	so.d_time as sodtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername as brswsusername,\n"
                + "	brs.wspassword as brswspassword,\n"
                + "	so.licencecode as solicencecode\n"
                + "FROM \n"
                + "	log.sendorder so\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=so.branch_id)\n"
                + "WHERE\n"
                + "	so.order_id=?\n"
                + "	AND so.deleted=FALSE\n"
                + "ORDER BY so.id DESC\n"
                + "	LIMIT 1\n";
        Object[] param = new Object[]{orderId};
        SendOrder sendOrder = null;
        try {
            sendOrder = getJdbcTemplate().queryForObject(sql, param, new SendOrderMapper());
        } catch (Exception e) {
            Logger.getLogger(SendOrderDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendOrder;
    }

    @Override
    public int updateSendOrderResult(SendOrder sendOrder) {
    String sql
                = "UPDATE \n"
                + "	log.sendorder \n"
                + "SET \n"
                + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                + "	response = ?,\n"
                + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                + "	sendenddate = NOW(),\n"
                + "	u_time = NOW(),\n"
                + "	sendcount = sendcount+1\n"
                + "WHERE \n"
                + "	id = ?";
        Object[] param = new Object[]{sendOrder.isIssend(), sendOrder.getResponse(), sendOrder.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
    
}
