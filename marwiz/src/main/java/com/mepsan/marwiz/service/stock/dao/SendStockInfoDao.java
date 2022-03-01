/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.09.2018 11:38:14
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.log.SendStockInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SendStockInfoDao extends JdbcDaoSupport implements ISendStockInfoDao {

    @Override
    public List<SendStockInfo> getStockInfoData() {
        String sql
                = "SELECT \n"
                + "	r_branch_id as branch_id,\n"
                + "	r_licencecode as licencecode,\n"
                + "	r_senddata as senddata,\n"
                + "	r_wsendpoint as wsendpoint,\n"
                + "	r_wsusername as wsusername,\n"
                + "	r_wspassword as wspassword\n"
                + "FROM log.insertjson_stockinfo();\n";
        List<SendStockInfo> sendStockInfos = new ArrayList<>();
        try {
            sendStockInfos = getJdbcTemplate().query(sql, new SendStockInfoMapper());
        } catch (Exception e) {
            Logger.getLogger(SendStockRequestDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return sendStockInfos;
    }

    @Override
    public int insertResult(SendStockInfo obj) {
        String sql = "INSERT INTO \n"
                + "log.sendstockinfo\n"
                + "(\n"
                + "  branch_id,\n"
                + "  licencecode,\n"
                + "  senddata,\n"
                + "  is_send,\n"
                + "  sendbegindate,\n"
                + "  sendenddate,\n"
                + "  sendcount,\n"
                + "  response\n"
                + ")\n"
                + "VALUES (\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?,\n"
                + "  ?\n"
                + ");";
        Object[] param = new Object[]
        {
            obj.getBranchSetting().getBranch().getId(),
            obj.getBranchSetting().getBranch().getLicenceCode(),
            obj.getSenddata(),
            obj.isIsSend(),
            obj.getSendBeginDate(),
            obj.getSendEndDate(),
            obj.getSendCount(),
            obj.getResponse()
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
