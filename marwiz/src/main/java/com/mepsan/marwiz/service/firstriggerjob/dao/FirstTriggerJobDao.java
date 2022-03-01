/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.07.2021 01:33:45
 */
package com.mepsan.marwiz.service.firstriggerjob.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FirstTriggerJobDao extends JdbcDaoSupport implements IFirstTriggerJobDao {

    @Override
    public String callFirstTriggerJob() {
        String sql = "SELECT * FROM system.first_trigger_job();";
        try {
            return getJdbcTemplate().queryForObject(sql, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public BranchSetting findTopCentralIntegratedBranchSetting() {
        String sql
                  = "SELECT\n"
                  + "	brs.wsusername AS brswsusername,\n"
                  + "	brs.wspassword AS brswspassword,\n"
                  + "	brs.wsendpoint AS brswsendpoint\n"
                  + "FROM \n"
                  + "	general.branchsetting brs\n"
                  + "WHERE \n"
                  + "	brs.is_usecentralintegrationdefinition = TRUE\n"
                  + "	AND brs.deleted=FALSE\n"
                  + "ORDER BY brs.id \n"
                  + "LIMIT 1\n";

        BranchSetting result = getJdbcTemplate().queryForObject(sql, new BranchSettingMapper());
        return result;
    }

    @Override
    public int updateSystemParameters() {
        String sql
                  = "UPDATE \n"
                  + "  system.parameters \n"
                  + "SET \n"
                  + "  value = true\n"
                  + "WHERE keyword = 'first_trigger_job'";

        try {
            getJdbcTemplate().update(sql);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
