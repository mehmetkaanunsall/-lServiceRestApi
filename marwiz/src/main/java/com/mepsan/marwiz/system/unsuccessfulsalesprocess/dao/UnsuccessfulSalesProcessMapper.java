package com.mepsan.marwiz.system.unsuccessfulsalesprocess.dao;

import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author elif.mart
 */
public class UnsuccessfulSalesProcessMapper implements RowMapper<UnsuccessfulSalesProcess> {

    @Override
    public UnsuccessfulSalesProcess mapRow(ResultSet rs, int i) throws SQLException {
        UnsuccessfulSalesProcess unsuccessfulSalesProcess = new UnsuccessfulSalesProcess();

        try {
            unsuccessfulSalesProcess.setId(rs.getInt("psid"));
        } catch (Exception e) {
        }
        try {
            unsuccessfulSalesProcess.getBranch().setId(rs.getInt("psbranch_id"));
            unsuccessfulSalesProcess.getBranch().setName(rs.getString("brnname"));
            unsuccessfulSalesProcess.setProcessDate(rs.getTimestamp("pssaleprocessdate"));
            unsuccessfulSalesProcess.setErrorMessage(rs.getString("pssaleerrormessage"));

        } catch (Exception e) {
        }

        try {
            unsuccessfulSalesProcess.setResponseCode(rs.getInt("r_responsecode"));
            unsuccessfulSalesProcess.setResponseMessage(rs.getString("r_responsemessage"));
        } catch (Exception e) {
        }

        return unsuccessfulSalesProcess;
    }

}
