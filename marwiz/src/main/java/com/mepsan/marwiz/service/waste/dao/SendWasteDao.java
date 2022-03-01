/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.07.2019 02:27:00
 */
package com.mepsan.marwiz.service.waste.dao;

import com.mepsan.marwiz.general.model.log.SendWaste;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendWasteDao extends JdbcDaoSupport implements ISendWasteDao {

    @Override
    public List<SendWaste> findNotSendedAll() {
        String sql
                  = "SELECT \n"
                  + "	 sw.id AS swid,\n"
                  + "    sw.warehousereceipt_id AS swwarehousereceipt_id,\n"
                  + "    sw.senddata AS swsenddate,\n"
                  + "    sw.is_send AS swissend,\n"
                  + "    sw.sendbegindate AS swsendbegindate,\n"
                  + "    sw.sendenddate AS swsendenddate,\n"
                  + "    sw.sendcount AS swsendcount,\n"
                  + "    sw.response AS swresponse,\n"
                  + "    brs.wsendpoint AS brswsendpoint,\n"
                  + "    brs.wsusername as brswsusername,\n"
                  + "    brs.wspassword as brswspassword,\n"
                  + "    br.licencecode as brlicencecode\n"
                  + "FROM\n"
                  + "log.sendwaste sw\n"
                  + "	 INNER JOIN general.branchsetting brs ON(brs.branch_id=sw.branch_id)\n"
                  + "    INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE sw.deleted =FALSE\n"
                  + "      AND sw.is_send=FALSE\n"
                  + "      LIMIT 200 ";
        List<SendWaste> sendStockRequests = new ArrayList<>();
        try {
            sendStockRequests = getJdbcTemplate().query(sql, new SendWasteMapper());
        } catch (Exception e) {
            Logger.getLogger(SendWasteDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockRequests;
    }

    @Override
    public int updateSendWasteResult(SendWaste sendWaste) {
        String sql
                  = "UPDATE \n"
                  + "	log.sendwaste \n"
                  + "SET \n"
                  + "	is_send = CASE WHEN is_send = false THEN ? ELSE is_send END,\n"
                  + "	response = ?,\n"
                  + "	sendbegindate = CASE WHEN sendbegindate IS NULL THEN NOW() ELSE sendbegindate END,\n"
                  + "	sendenddate = NOW(),\n"
                  + "	u_time = NOW(),\n"
                  + "	sendcount = sendcount+1\n"
                  + "WHERE \n"
                  + "	id = ?";
        Object[] param = new Object[]{sendWaste.isIsSend(), sendWaste.getResponse(), sendWaste.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
