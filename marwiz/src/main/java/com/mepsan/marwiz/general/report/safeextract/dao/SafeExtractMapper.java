/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 17:24:15
 */
package com.mepsan.marwiz.general.report.safeextract.dao;

import com.mepsan.marwiz.general.model.finance.SafeMovement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SafeExtractMapper implements RowMapper<SafeMovement> {

    @Override
    public SafeMovement mapRow(ResultSet rs, int i) throws SQLException {
        SafeMovement sm = new SafeMovement();

        sm.getSafe().setId(rs.getInt("sid"));
        sm.getSafe().setName(rs.getString("sname"));
        sm.getSafe().getCurrency().setId(rs.getInt("scurrency_id"));
        sm.setBalance(rs.getBigDecimal("sbalance"));

        sm.setTotalIncoming(rs.getBigDecimal("sumincoming"));

        sm.setTotalOutcoming(rs.getBigDecimal("sumoutcoming"));
        
        sm.getBranch().setId(rs.getInt("brid"));
        sm.getBranch().setName(rs.getString("brname"));

        return sm;
    }

}
