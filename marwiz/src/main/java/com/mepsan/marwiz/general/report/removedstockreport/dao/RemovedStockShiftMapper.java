/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 14:54:09
 */
package com.mepsan.marwiz.general.report.removedstockreport.dao;

import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedShift;
import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedStockReport;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RemovedStockShiftMapper implements RowMapper<Shift> {

    @Override
    public Shift mapRow(ResultSet rs, int i) throws SQLException {
        Shift shift = new Shift();
        shift.setId(rs.getInt("shftid"));
        shift.setShiftNo(rs.getString("shftshiftno"));
        return shift;
    }

}
