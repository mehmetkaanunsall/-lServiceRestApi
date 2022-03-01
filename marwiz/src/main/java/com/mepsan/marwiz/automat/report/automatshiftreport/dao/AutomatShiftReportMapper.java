/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:27:58 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AutomatShiftReportMapper implements RowMapper<AutomatShiftReport> {

    @Override
    public AutomatShiftReport mapRow(ResultSet rs, int i) throws SQLException {
        AutomatShiftReport washingMachicneShiftReport = new AutomatShiftReport();

        washingMachicneShiftReport.setId(rs.getInt("shfid"));
        washingMachicneShiftReport.setShiftNo(rs.getString("shfshitfno"));
        washingMachicneShiftReport.setBeginDate(rs.getTimestamp("shfbegindate"));
        washingMachicneShiftReport.setEndDate(rs.getTimestamp("shfenddate"));
        washingMachicneShiftReport.getStatus().setId(rs.getInt("shfstatus_id"));
        return washingMachicneShiftReport;
    }

}
