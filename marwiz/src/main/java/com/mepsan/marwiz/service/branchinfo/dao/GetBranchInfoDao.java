/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.07.2020 09:42:35
 */
package com.mepsan.marwiz.service.branchinfo.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GetBranchInfoDao extends JdbcDaoSupport implements IGetBranchInfoDao {

    @Override
    public int callBranchInfo(int branchId, String response) {
        String sql = "SELECT r_responsecode FROM log.set_licencecode (?, ?)";

        Object[] param = new Object[]{branchId, response};
                
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<BranchSetting> findBranchSettingsForBranchInfo() {
        String sql
                  = "SELECT\n"
                  + "	brs.id AS brsid,\n"
                  + "	brs.wsendpoint AS brswsendpoint,\n"
                  + "	brs.wsusername AS brswsusername,\n"
                  + "	brs.wspassword AS brswspassword,\n"
                  + "	br.licencecode AS brlicencecode,\n"
                  + "	br.id AS brid\n"
                  + "FROM \n"
                  + "	general.branchsetting brs\n"
                  + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE \n"
                  + "	brs.is_centralintegration = TRUE \n"
                  + "	AND brs.deleted=FALSE\n"
                  + "ORDER BY br.id \n";

        List<BranchSetting> result = getJdbcTemplate().query(sql, new BranchSettingMapper());
        return result;
    }

}
