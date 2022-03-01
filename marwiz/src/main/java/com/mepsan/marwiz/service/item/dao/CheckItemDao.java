/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 07.05.2018 11:10:23
 */
package com.mepsan.marwiz.service.item.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckItem;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CheckItemDao extends JdbcDaoSupport implements ICheckItemDao {

    @Override
    public BranchSetting findTopCentralIntegratedBranchSetting() {
        String sql
                = "SELECT\n"
                + "	brs.id AS brsid,\n"
                + "	brs.authpaymenttype AS brsauthpaymenttype,\n"
                + "	brs.printpaymenttype AS brsprintpaymenttype,\n"
                + "	brs.is_managerdiscount AS brsis_managerdiscount,\n"
                + "	brs.is_managerreturn AS brsis_managerreturn,\n"
                + "	brs.automation_id AS brsautomation_id,\n"
                + "	brs.is_cashierpumpscreen AS brsis_managerpumpscreen,\n"
                + "	brs.is_centralintegration AS brsis_centralintegration,\n"
                + "	brs.sleeptime AS brssleeptime,\n"
                + "	brs.uscipaddress AS uscipaddress,\n"
                + "	brs.uscport AS brsuscport,\n"
                + "	brs.uscprotocol AS brsuscprotocol,\n"
                + "	brs.wsusername AS brswsusername,\n"
                + "	brs.wspassword AS brswspassword,\n"
                + "	brs.localserveripaddress AS brslocalserveripaddress,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	br.id AS brid,\n"
                + "	br.licencecode AS brlicencecode\n"
                + "FROM \n"
                + "	general.branchsetting brs\n"
                + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                + "WHERE \n"
                + "	brs.is_usecentralintegrationdefinition = TRUE\n"
                + "	AND brs.deleted=FALSE\n"
                + "ORDER BY br.id \n"
                + "LIMIT 1\n";

        BranchSetting result = getJdbcTemplate().queryForObject(sql, new BranchSettingMapper());
        return result;
    }

    @Override
    public List<BranchSetting> findTopCentralIntegratedBranchSettings() {
        String sql
                = "SELECT\n"
                + "	brs.id AS brsid,\n"
                + "	brs.authpaymenttype AS brsauthpaymenttype,\n"
                + "	brs.printpaymenttype AS brsprintpaymenttype,\n"
                + "	brs.is_managerdiscount AS brsis_managerdiscount,\n"
                + "	brs.is_managerreturn AS brsis_managerreturn,\n"
                + "	brs.automation_id AS brsautomation_id,\n"
                + "	brs.is_cashierpumpscreen AS brsis_managerpumpscreen,\n"
                + "	brs.is_centralintegration AS brsis_centralintegration,\n"
                + "	brs.sleeptime AS brssleeptime,\n"
                + "	brs.uscipaddress AS uscipaddress,\n"
                + "	brs.uscport AS brsuscport,\n"
                + "	brs.uscprotocol AS brsuscprotocol,\n"
                + "	brs.wsusername AS brswsusername,\n"
                + "	brs.wspassword AS brswspassword,\n"
                + "	brs.localserveripaddress AS brslocalserveripaddress,\n"
                + "	brs.wsendpoint AS brswsendpoint,\n"
                + "	br.id AS brid,\n"
                + "	br.licencecode AS brlicencecode\n"
                + "FROM \n"
                + "	general.branchsetting brs\n"
                + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                + "WHERE \n"
                + "	brs.is_centralintegration=TRUE \n"
                + "	AND brs.deleted=FALSE\n"
                + "ORDER BY br.id \n";

        List<BranchSetting> result = getJdbcTemplate().query(sql, new BranchSettingMapper());
        return result;
    }

    @Override
    public Date getMaxProcessDateByType(int type) {
        Date result = null;
        String sql
                = "SELECT\n"
                + "	MAX(processdate)\n"
                + "FROM \n"
                + "	log.checkitem \n"
                + "WHERE\n"
                + "	type=?\n"
                + "	AND is_success=TRUE\n";
        Object[] param = new Object[]{type};
        try {
            result = getJdbcTemplate().queryForObject(sql, param, Date.class);
        } catch (Exception e) {
            //Logger.getLogger(CheckItemDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public Integer getMaxCenterNotificaionId(int branch_id) {
        Integer result = null;
        String sql
                = "SELECT \n"
                + "	MAX(centernotification_id)\n"
                + "FROM \n"
                + "	general.notification \n"
                + "WHERE\n"
                + "	deleted=FALSE\n"
                + "	AND branch_id=?\n";
        Object[] param = new Object[]{branch_id};
        try {
            result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (Exception e) {
            //Logger.getLogger(CheckItemDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    @Override
    public int insertCheckItem(CheckItem obj) {
        System.out.println("--insertCheckItem--Dao-");
        String sql
                = "INSERT INTO \n"
                + "log.checkitem\n"
                + "(\n"
                + "	type,\n"
                + "	processdate,\n"
                + "	is_success,\n"
                + "	response\n"
                + ")\n"
                + "VALUES "
                + "(\n"
                + "	?,\n"
                + "	?,\n"
                + "	?,\n"
                + "	? \n"
                + ")RETURNING id;";

        Object[] param = new Object[]{obj.getType(), obj.getProcessDate(), obj.isIsSuccess(), obj.getResponse()};
        
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            System.out.println("--insertCheckItem--DAO Catch--" + e.getMessage());
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
