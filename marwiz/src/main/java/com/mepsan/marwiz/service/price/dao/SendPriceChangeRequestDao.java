/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:45:06
 */
package com.mepsan.marwiz.service.price.dao;

import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequestCheck;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendPriceChangeRequestDao extends JdbcDaoSupport implements ISendPriceChangeRequestDao {

    @Override
    public List<SendPriceChangeRequest> findNotSendedAll() {
        String sql
                = "SELECT\n"
                + "	spcr.id AS spcrid,\n"
                + "	spcr.branch_id AS spcrbranchid,\n"
                + "	spcr.licencecode AS spcrlicencecode,\n"
                + "	spcr.pricechangerequest_id AS spcrpricechangerequest_id,\n"
                + "	spcr.senddata AS spcrsenddata,\n"
                + "	spcr.is_send AS spcrissend,\n"
                + "	spcr.sendbegindate AS spcrsendbegindate,\n"
                + "	spcr.sendenddate AS spcrsendenddate,\n"
                + "	spcr.sendcount AS spcrsendcount,\n"
                + "	spcr.response AS spcrresponse,\n"
                + "	spcr.checkresponse AS spcrcheckresponse,\n"
                + "	spcr.c_id AS spcrcid,\n"
                + "	spcr.c_time AS spcrctime,\n"
                + "	spcr.u_id AS spcruid,\n"
                + "	spcr.u_time AS spcrutime,\n"
                + "	spcr.deleted AS spcrdeleted,\n"
                + "	spcr.d_time AS spcrdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername AS brswsusername,\n"
                + "	brs.wspassword AS brswspassword,\n"
                + "	br.licencecode AS brlicencecode\n"
                + "FROM \n"
                + "	log.sendpricechangerequest spcr\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=spcr.branch_id)\n"
                + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                + "WHERE\n"
                + "	spcr.is_send=FALSE\n"
                + "	AND spcr.deleted=FALSE\n"
                + "	LIMIT 200";
        List<SendPriceChangeRequest> sendStockRequests = new ArrayList<>();
        try {
            sendStockRequests = getJdbcTemplate().query(sql, new SendPriceChangeRequestMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPriceChangeRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockRequests;
    }

    @Override
    public SendPriceChangeRequest findByIdPriceChangeRequestId(int priceChangeRequestId) {
        String sql
                = "SELECT\n"
                + "	spcr.id AS spcrid,\n"
                + "	spcr.branch_id AS spcrbranchid,\n"
                + "	spcr.licencecode AS spcrlicencecode,\n"
                + "	spcr.pricechangerequest_id AS spcrpricechangerequest_id,\n"
                + "	spcr.senddata AS spcrsenddata,\n"
                + "	spcr.is_send AS spcrissend,\n"
                + "	spcr.sendbegindate AS spcrsendbegindate,\n"
                + "	spcr.sendenddate AS spcrsendenddate,\n"
                + "	spcr.sendcount AS spcrsendcount,\n"
                + "	spcr.response AS spcrresponse,\n"
                + "	spcr.checkresponse AS spcrcheckresponse,\n"
                + "	spcr.c_id AS spcrcid,\n"
                + "	spcr.c_time AS spcrctime,\n"
                + "	spcr.u_id AS spcruid,\n"
                + "	spcr.u_time AS spcrutime,\n"
                + "	spcr.deleted AS spcrdeleted,\n"
                + "	spcr.d_time AS spcrdtime,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	brs.wsusername AS brswsusername,\n"
                + "	brs.wspassword AS brswspassword,\n"
                + "	br.licencecode AS brlicencecode\n"
                + "FROM \n"
                + "	log.sendpricechangerequest spcr\n"
                + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=spcr.branch_id)\n"
                + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                + "WHERE\n"
                + "	spcr.pricechangerequest_id = ?";
        Object[] param = new Object[]{priceChangeRequestId};
        SendPriceChangeRequest sendStockRequest = null;
        try {
            sendStockRequest = getJdbcTemplate().queryForObject(sql, param, new SendPriceChangeRequestMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPriceChangeRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockRequest;
    }

    @Override
    public int updatePriceChangeRequestResult(SendPriceChangeRequest sendPriceChangeRequest) {
        String sql
                = "UPDATE \n"
                + "  log.sendpricechangerequest\n"
                + "SET \n"
                + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                + "	response = ?,\n"
                + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                + "	sendenddate = NOW(),\n"
                + "	u_time = NOW(),\n"
                + "	sendcount = sendcount+1\n"
                + "WHERE \n"
                + "	pricechangerequest_id = ?";
        Object[] param = new Object[]{sendPriceChangeRequest.isIsSend(), sendPriceChangeRequest.getResponse(), sendPriceChangeRequest.getPriceChangeRequestId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updatePriceChangeRequestCheckResponse(int requestid, String res) {
        String sql
                = "UPDATE \n"
                + "     log.sendpricechangerequest\n"
                + "SET \n"
                + "	checkresponse = ?,\n"
                + "	u_time = NOW()\n"
                + "WHERE \n"
                + "	pricechangerequest_id = ?";
        Object[] param = new Object[]{res, requestid};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updatePriceChangeRequest(StockPriceRequest stockPriceRequest) {
        String sql
                = "UPDATE \n"
                + " inventory.pricechangerequest \n"
                + "SET \n"
                + "     approval = ?, \n"
                + "     approvaldate = ?, \n"
                + "     u_time = NOW() \n"
                + "WHERE \n"
                + "     id = ?";

        Object[] param = new Object[]{stockPriceRequest.getApproval(), stockPriceRequest.getApprovalDate(), stockPriceRequest.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<SendPriceChangeRequestCheck> findNotAprovedAll() {
        String sql
                = "SELECT\n"
                + "    string_agg( spcr.pricechangerequest_id::text,',') as spcrpricechangerequestid,\n"
                + "    brs.wsendpoint AS brswsendpoint,\n"
                + "    brs.wsusername as brswsusername,\n"
                + "    brs.wspassword as brswspassword,\n"
                + "    br.licencecode as brlicencecode\n"
                + "FROM\n"
                + "    log.sendpricechangerequest spcr\n"
                + "    INNER JOIN inventory.pricechangerequest r ON(r.id=spcr.pricechangerequest_id)\n"
                + "    INNER JOIN general.branch br ON(br.id=spcr.branch_id)\n"
                + "    INNER JOIN general.branchsetting brs ON(brs.branch_id=br.id)\n"
                + "WHERE\n"
                + "    spcr.is_send=TRUE\n"
                + "    AND r.approval = 0\n"
                + "    AND spcr.deleted=FALSE\n"
                + "GROUP BY \n"
                + "    brs.wsendpoint,brs.wsusername,brs.wspassword,br.licencecode\n";
        List<SendPriceChangeRequestCheck> stockRequestChecks = new ArrayList<>();
        try {
            stockRequestChecks = getJdbcTemplate().query(sql, new SendPriceChangeRequestCheckMapper());
        } catch (Exception e) {
            Logger.getLogger(SendPriceChangeRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return stockRequestChecks;
    }
}
