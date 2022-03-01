/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 08:28:23
 */
package com.mepsan.marwiz.service.paro.dao;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.pointofsale.dao.PointOfSaleMapper;
import com.mepsan.marwiz.service.model.LogParo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ParoOfflineSalesDao extends JdbcDaoSupport implements IParoOfflineSalesDao {

    @Override
    public List<LogParo> listOfLog() {
        String sql = "SELECT * FROM log.check_offlineparosales()";

        List<LogParo> logParo = new ArrayList<>();
        try {
            logParo = getJdbcTemplate().query(sql, new ParoOfflineSalesMapper());
        } catch (Exception e) {
            Logger.getLogger(ParoOfflineSalesDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return logParo;
    }

    @Override
    public int updateSaleLog(LogParo logParo) {
        String sql
                  = "UPDATE \n"
                  + "  log.paro \n"
                  + "SET \n"
                  + "  is_success = ?,\n"
                  + "  is_send = ?,\n"
                  + "  senddata = ?,\n"
                  + "  sendenddate = ?,\n"
                  + "  errorcode =  ? ,\n"
                  + "  errormessage = ? \n,"
                  + "  sendcount = COALESCE(sendcount,0)+1,\n"
                  + "  errorcount = ?,\n"
                  + "  response = ? \n"
                  + "WHERE \n"
                  + "  id = ?;";
        Object[] param = new Object[]{logParo.isIsSuccess(), logParo.isIsSend(), logParo.getSendData(), logParo.getSendEndDate(), logParo.getErrorCode(), logParo.getErrorMessage(), (logParo.isIsSend() == true && logParo.isIsSuccess() == false ? (logParo.getErrorCount() + 1) : logParo.getErrorCount()), logParo.getResponse(), logParo.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateAllRequestLog(String transactionNo, long requestId) {
        String sql
                  = "UPDATE \n"
                  + "  log.paro \n"
                  + "SET \n"
                  + "  transactionno = ? \n"
                  + "WHERE \n"
                  + "  request_id = ?;";
        Object[] param = new Object[]{transactionNo, requestId};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public LogParo selectParoPayment(long requestId) {
        String sql = "SELECT\n"
                  + "    pr.id AS r_prid,\n"
                  + "    pr.request_id AS r_prrequest_id,\n"
                  + "    pr.transactionno AS r_prtransactionno,\n"
                  + "    pr.provisionno AS r_prprovisionno,\n "
                  + "    pr.type_id AS r_prtype_id,\n"
                  + "    pr.sale_id AS r_prsale_id,\n"
                  + "    pr.is_parocustomer AS r_pris_parocustomer ,\n"
                  + "    pr.senddata AS r_prsenddata ,\n"
                  + "    pr.is_success AS r_pris_success,\n"
                  + "    pr.errorcode AS r_prerrorcode,\n"
                  + "    pr.errormessage AS r_prerrormessage,\n"
                  + "    pr.is_send AS r_pris_send,\n"
                  + "    pr.sendbegindate AS r_prsendbegindate,\n"
                  + "    pr.sendenddate AS r_prsendenddate,\n"
                  + "    pr.sendcount AS r_prsendcount,\n"
                  + "    pr.is_qrcode  as r_pris_qrcode,\n"
                  + "    COALESCE(pr.errorcount,0) AS r_prerrorcount ,\n"
                  + "    pr.response AS r_prresponse,\n"
                  + "    pr.branch_id AS r_slbranch_id,\n"
                  + "    sl.c_id AS r_slc_id ,\n"
                  + "   (CASE WHEN sl.invoice_id IS NULL THEN false ELSE true END)  AS r_slis_invoice, \n"
                  + "    pr.pointofsale_id AS  r_slpointofsale_id, \n"
                  + "    slo.orderid AS r_saleorder_id\n"
                  + "FROM\n"
                  + "	log.paro pr\n"
                  + "LEFT JOIN general.sale sl ON(sl.id = pr.sale_id AND sl.deleted = FALSE) \n"
                  + "LEFT JOIN general.saleorder slo ON(slo.sale_id = sl.id AND slo.deleted=FALSE)\n"
                  + "WHERE\n"
                  + "pr.request_id = ? AND pr.type_id = 3 AND  pr.is_success = FALSE LIMIT 200 \n";

        List<LogParo> logParo = new ArrayList<>();
        Object[] param = {requestId};
        try {
            logParo = getJdbcTemplate().query(sql, param, new ParoOfflineSalesMapper());
        } catch (Exception e) {
            Logger.getLogger(ParoOfflineSalesDao.class.getName()).log(Level.SEVERE, null, e);
        }
        if (logParo.size() > 0) {
            return logParo.get(0);
        } else {
            return new LogParo();
        }
    }

    @Override
    public int createSaleLog(LogParo obj) {
        String sql = "INSERT INTO \n"
                  + "  log.paro\n"
                  + "(\n"
                  + "  request_id,\n"
                  + "  transactionno,\n"
                  + "  type_id,\n"
                  + "  senddata,\n"
                  + "  is_success,\n"
                  + "  errorcode,\n"
                  + "  errormessage,\n"
                  + "  is_send,\n"
                  + "  sendbegindate,\n"
                  + "  sendenddate,\n"
                  + "  response,\n"
                  + "  pointofsale_id, \n"
                  + "  provisionno, \n"
                  + "  branch_id \n "
                  + ")\n"
                  + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id ;";
        Object[] param = new Object[]{obj.getRequestId(), obj.getTransactionNo(), obj.getTypeId(), obj.getSendData(), obj.isIsSuccess(), obj.getErrorCode(),
            obj.getErrorMessage(), obj.isIsSend(), obj.getSendBeginDate(), obj.getSendEndDate(), obj.getResponse(), (obj.getPointOfSaleId() <= 0 ? null : obj.getPointOfSaleId()), obj.getProvisionNo(),
            obj.getBranchId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateSale(String transactionNo, int saleId) {
        String sql
                  = "UPDATE \n"
                  + "  general.sale \n"
                  + "SET \n"
                  + "  transactionno = ?, \n"
                  + "  u_id = ? , \n"
                  + "  u_time = now() \n"
                  + "WHERE \n"
                  + "  id = ?;";
        Object[] param = new Object[]{transactionNo, 1, saleId};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createJsonSale(LogParo logParo) {
        String sql = "SELECT insertjson_sale FROM log.insertjson_sale(?,?,?,?);";

        Object[] param = new Object[]{logParo.getSaleId(), null, logParo.getBranchId(), logParo.getCreatedId()};
        System.out.println("Arrays.toString(param);=" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public boolean isThereFinishedSale(String transactionNo) {
        String sql = "SELECT \n"
                  + "	(CASE  WHEN tt.countparo > 0  THEN 1 ELSE 0 END) AS isthereparo\n"
                  + "FROM \n"
                  + "	(SELECT COUNT (pr.id) as countparo FROM log.paro pr WHERE pr.type_id IN (3,4) AND pr.transactionno =?  LIMIT 1) tt;";

        Object[] param = new Object[]{transactionNo};
        try {
            int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
            System.out.println("===== isThereFinishedSale = " + id + " Transaction No ====" + transactionNo);
            return id > 0 ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<PointOfSale> listPointOfSale(int branch_id) {
        String sql = "SELECT \n"
                  + "pos.id AS posid,\n"
                  + "pos.name AS posname,\n"
                  + "pos.code AS poscode,\n"
                  + "pos.localipaddress AS poslocalipaddress\n"
                  + "FROM general.pointofsale pos\n"
                  + "   WHERE pos.deleted=FALSE AND pos.branch_id=? AND pos.status_id=9 AND pos.is_offline =TRUE";

        Object[] param = new Object[]{branch_id};
        List<PointOfSale> result = getJdbcTemplate().query(sql, param, new PointOfSaleMapper());
        return result;
    }

    @Override
    public int updateSaleCancelLog(LogParo logParo) {
        String sql
                  = "UPDATE \n"
                  + "  log.paro \n"
                  + "SET \n"
                  + "  is_success = ?,\n"
                  + "  is_send = ?,\n"
                  + "  senddata = ?,\n"
                  + "  sendenddate = ?,\n"
                  + "  errorcode =  ? ,\n"
                  + "  errormessage = ? \n,"
                  + "  sendcount = COALESCE(sendcount,0)+1,\n"
                  + "  errorcount = ?,\n"
                  + "  response = ?,\n"
                  + "  transactionno = ? , \n"
                  + "  provisionno = ?  \n"
                  + "WHERE \n"
                  + "  id = ?;";
        Object[] param = new Object[]{logParo.isIsSuccess(), logParo.isIsSend(), logParo.getSendData(), logParo.getSendEndDate(), logParo.getErrorCode(), logParo.getErrorMessage(), (logParo.isIsSend() == true && logParo.isIsSuccess() == false ? (logParo.getErrorCount() + 1) : logParo.getErrorCount()), logParo.getResponse(),
            logParo.getTransactionNo(), logParo.getProvisionNo(), logParo.getId()
        };
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
