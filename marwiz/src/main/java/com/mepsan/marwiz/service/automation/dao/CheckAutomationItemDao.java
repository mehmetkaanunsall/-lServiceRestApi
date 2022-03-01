/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 11:38:54
 */
package com.mepsan.marwiz.service.automation.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckAutomationItem;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CheckAutomationItemDao extends JdbcDaoSupport implements ICheckAutomationItemDao {

    @Override
    public List<BranchSetting> findAutomationIntegratedBranchSettings() {
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
                  + "	br.licencecode AS brlicencecode,\n"
                  + "   brs.automationpassword AS brsautomationpassword,\n"
                  + "   brs.automationtimeout AS brsautomationtimeout,\n"
                  + "   brs.automationurl AS brsautomationurl,\n"
                  + "   brs.automationusername AS brsautomationusername\n"
                  + "FROM \n"
                  + "	general.branchsetting brs\n"
                  + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE \n"
                  + "   brs.deleted=FALSE\n"
                  + "   AND brs.automation_id = 1\n"
                  + "ORDER BY br.id \n";

        List<BranchSetting> result = getJdbcTemplate().query(sql, new BranchSettingMapper());
        return result;
    }

    @Override
    public int insertAutomationItem(CheckAutomationItem obj, BranchSetting branchSetting) {
        String sql
                  = "INSERT INTO \n"
                  + "log.checkautomationitem\n"
                  + "(\n"
                  + "   branch_id,\n"
                  + "   licencecode,\n"
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
                  + "	?,\n"
                  + "	?,\n"
                  + "	? \n"
                  + ")RETURNING id;";

        Object[] param = new Object[]{branchSetting.getBranch().getId(), branchSetting.getBranch().getLicenceCode(), obj.getType(), obj.getProcessDate(), obj.isIsSuccess(), obj.getResponse()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Date getMaxProcessDateByType(int type, BranchSetting branchSetting) {
        Date result = null;
        String sql
                  = "SELECT\n"
                  + "	MAX(processdate)\n"
                  + "FROM \n"
                  + "	log.checkautomationitem \n"
                  + "WHERE\n"
                  + "	type=?\n"
                  + "	AND is_success=TRUE\n"
                  + "   AND branch_id = ?";
        Object[] param = new Object[]{type, branchSetting.getBranch().getId()};
        try {
            result = getJdbcTemplate().queryForObject(sql, param, Date.class);
        } catch (Exception e) {

        }
        return result;
    }

}
