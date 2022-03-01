/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 09:26:55
 */
package com.mepsan.marwiz.general.report.removedstockreport.dao;

import com.mepsan.marwiz.general.report.removedstockreport.presentation.RemovedStockReport;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RemovedStockReportMonthlyMapper implements RowMapper<RemovedStockReport> {

    @Override
    public RemovedStockReport mapRow(ResultSet rs, int i) throws SQLException {
        RemovedStockReport removedStockReport = new RemovedStockReport();
        
        removedStockReport.setId(rs.getInt("id"));
        removedStockReport.getRemovedStockReport().getUserData().setId(rs.getInt("rsuserdata_id"));
        removedStockReport.getRemovedStockReport().getUserData().setName(rs.getString("usname"));
        removedStockReport.getRemovedStockReport().getUserData().setSurname(rs.getString("ussurname"));
        removedStockReport.setJanuary(rs.getInt("january"));
        removedStockReport.setFebruary(rs.getInt("february"));
        removedStockReport.setMarch(rs.getInt("march"));
        removedStockReport.setApril(rs.getInt("april"));
        removedStockReport.setMay(rs.getInt("may"));
        removedStockReport.setJune(rs.getInt("jun"));
        removedStockReport.setJuly(rs.getInt("july"));
        removedStockReport.setAugust(rs.getInt("august"));
        removedStockReport.setSeptember(rs.getInt("september"));
        removedStockReport.setOctober(rs.getInt("october"));
        removedStockReport.setNovember(rs.getInt("november"));
        removedStockReport.setDecember(rs.getInt("december"));
        removedStockReport.getBranch().setId(rs.getInt("rsbranch_id"));
        removedStockReport.getBranch().setName(rs.getString("brname"));

        return removedStockReport;
    }

}
