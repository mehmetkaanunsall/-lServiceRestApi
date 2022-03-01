/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:45:06
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendStockRequestDao extends JdbcDaoSupport implements ISendStockRequestDao {

    @Override
    public List<SendStockRequest> findNotSendedAll() {
        String sql
                  = "SELECT \n"
                  + "	sr.id as srid,\n"
                  + "	sr.branch_id AS srbranchid,\n"
                  + "	sr.licencecode AS srlicencecode,\n"
                  + "	sr.stockrequest_id as srstockrequestid,\n"
                  + "	sr.senddata as srsenddata,\n"
                  + "	sr.is_send as srissend,\n"
                  + "	sr.sendbegindate as srsendbegindate,\n"
                  + "	sr.sendenddate as srsendenddate,\n"
                  + "	sr.sendcount as srsendcount,\n"
                  + "	sr.response as srresponse,\n"
                  + "	sr.c_id as srcid,\n"
                  + "	sr.c_time as srctime,\n"
                  + "	sr.u_id as sruid,\n"
                  + "	sr.u_time as srutime,\n"
                  + "	sr.deleted as srdeleted,\n"
                  + "	sr.d_time as srdtime,\n"
                  + "	brs.wsendpoint AS brswsendpoint,\n"
                  + "	brs.wsusername as brswsusername,\n"
                  + "	brs.wspassword as brswspassword,\n"
                  + "	br.licencecode as brlicencecode\n"
                  + "FROM\n"
                  + "	log.sendstockrequest sr\n"
                  + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=sr.branch_id)\n"
                  + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE\n"
                  + "	sr.is_send=FALSE\n"
                  + "	AND sr.deleted=FALSE\n"
                  + "	LIMIT 200\n";
        List<SendStockRequest> sendStockRequests = new ArrayList<>();
        try {
            sendStockRequests = getJdbcTemplate().query(sql, new SendStockRequestMapper());
        } catch (Exception e) {
            Logger.getLogger(SendStockRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockRequests;
    }

    @Override
    public SendStockRequest findByIdStockRequestId(int stockRequestId) {
        String sql
                  = "SELECT \n"
                  + "	sr.id as srid,\n"
                  + "	sr.branch_id AS srbranchid,\n"
                  + "	sr.licencecode AS srlicencecode,\n"
                  + "	sr.stockrequest_id as srstockrequestid,\n"
                  + "	sr.senddata as srsenddata,\n"
                  + "	sr.is_send as srissend,\n"
                  + "	sr.sendbegindate as srsendbegindate,\n"
                  + "	sr.sendenddate as srsendenddate,\n"
                  + "	sr.sendcount as srsendcount,\n"
                  + "	sr.response as srresponse,\n"
                  + "	sr.c_id as srcid,\n"
                  + "	sr.c_time as srctime,\n"
                  + "	sr.u_id as sruid,\n"
                  + "	sr.u_time as srutime,\n"
                  + "	sr.deleted as srdeleted,\n"
                  + "	sr.d_time as srdtime,\n"
                  + "	brs.wsendpoint AS brswsendpoint,\n"
                  + "	brs.wsusername as brswsusername,\n"
                  + "	brs.wspassword as brswspassword,\n"
                  + "	br.licencecode as brlicencecode\n"
                  + "FROM\n"
                  + "	log.sendstockrequest sr \n"
                  + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=sr.branch_id)\n"
                  + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE\n"
                  + "	sr.deleted=FALSE AND sr.stockrequest_id = ?\n"
                  + "ORDER BY sr.id DESC LIMIT 1";
        Object[] param = new Object[]{stockRequestId};
        SendStockRequest sendStockRequest = null;
        try {
            sendStockRequest = getJdbcTemplate().queryForObject(sql, param, new SendStockRequestMapper());
        } catch (Exception e) {
            Logger.getLogger(SendStockRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockRequest;
    }

    @Override
    public int updateStockRequestResult(SendStockRequest sendStockRequest) {
        String sql
                  = "UPDATE \n"
                  + "  log.sendstockrequest \n"
                  + "SET \n"
                  + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                  + "	response = ?,\n"
                  + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                  + "	sendenddate = NOW(),\n"
                  + "	u_time = NOW(),\n"
                  + "	sendcount = sendcount+1\n"
                  + "WHERE \n"
                  + "	deleted = FALSE AND stockrequest_id = ?";
        Object[] param = new Object[]{sendStockRequest.isIsSend(), sendStockRequest.getResponse(), sendStockRequest.getStockrequestId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateStockRequestCheckResponse(int requestid, String res) {
        String sql
                  = "UPDATE \n"
                  + "  log.sendstockrequest \n"
                  + "SET \n"
                  + "	checkresponse = ?,\n"
                  + "	u_time = NOW()\n"
                  + "WHERE \n"
                  + "	deleted=FALSE AND stockrequest_id = ?";
        Object[] param = new Object[]{res, requestid};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateStockRequest(StockRequest stockRequest) {
        String sql
                  = "UPDATE \n"
                  + " inventory.stockrequest \n"
                  + "SET \n"
                  + "     approval = ?, \n"
                  + "     approvaldate = ?, \n"
                  + "     approvalcenterstock_id = ?, \n"
                  + "     u_time = NOW() \n"
                  + "WHERE \n"
                  + "     id = ?";

        Object[] param = new Object[]{stockRequest.getApproval(), stockRequest.getApprovalDate(), stockRequest.getApprovalCenterStockId() == 0 ? null : stockRequest.getApprovalCenterStockId(), stockRequest.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<SendStockRequestCheck> findNotAprovedAll() {
        String sql
                  = "SELECT \n"
                  + "	string_agg( sr.stockrequest_id::text,',') as srstockrequestids,\n"
                  + "	brs.wsendpoint AS brswsendpoint,\n"
                  + "	brs.wsusername as brswsusername,\n"
                  + "	brs.wspassword as brswspassword,\n"
                  + "	br.licencecode as brlicencecode\n"
                  + "FROM\n"
                  + "	log.sendstockrequest sr\n"
                  + "	INNER JOIN inventory.stockrequest r ON(r.id=sr.stockrequest_id)\n"
                  + "	INNER JOIN general.branch br ON(br.id=sr.branch_id)\n"
                  + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=br.id)\n"
                  + "WHERE\n"
                  + "	sr.is_send=TRUE\n"
                  + "	AND r.approval = 0\n"//beklemede olankları tekrar dene
                  + "	AND sr.deleted=FALSE\n"
                  + "GROUP BY \n"
                  + "	brs.wsendpoint,brs.wsusername,brs.wspassword,br.licencecode\n";
        List<SendStockRequestCheck> stockRequestChecks = new ArrayList<>();
        try {
            stockRequestChecks = getJdbcTemplate().query(sql, new SendStockRequestCheckMapper());
        } catch (Exception e) {
            Logger.getLogger(SendStockRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return stockRequestChecks;
    }

    @Override
    public List<SendStockRequestCheck> findStockRequest(StockRequest stockRequest) {
        String sql
                  = "SELECT \n"
                  + "	string_agg( sr.stockrequest_id::text,',') as srstockrequestids,\n"
                  + "	brs.wsendpoint AS brswsendpoint,\n"
                  + "	brs.wsusername as brswsusername,\n"
                  + "	brs.wspassword as brswspassword,\n"
                  + "	br.licencecode as brlicencecode\n"
                  + "FROM\n"
                  + "	log.sendstockrequest sr\n"
                  + "	INNER JOIN inventory.stockrequest r ON(r.id=sr.stockrequest_id)\n"
                  + "	INNER JOIN general.branch br ON(br.id=sr.branch_id)\n"
                  + "	INNER JOIN general.branchsetting brs ON(brs.branch_id=br.id)\n"
                  + "WHERE\n"
                  + "    sr.deleted=FALSE AND sr.stockrequest_id = ?\n"
                  + "GROUP BY \n"
                  + "	brs.wsendpoint,brs.wsusername,brs.wspassword,br.licencecode\n";
        List<SendStockRequestCheck> stockRequestChecks = new ArrayList<>();

        Object[] param = new Object[]{stockRequest.getId()};

        try {
            stockRequestChecks = getJdbcTemplate().query(sql, param, new SendStockRequestCheckMapper());
        } catch (Exception e) {
            Logger.getLogger(SendStockRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return stockRequestChecks;
    }
}
