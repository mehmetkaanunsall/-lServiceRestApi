/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.03.2018 09:15:38
 */
package com.mepsan.marwiz.general.report.totalgiroreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TotalGiroReportMapper implements RowMapper<TotalGiroReport> {
    
    @Override
    public TotalGiroReport mapRow(ResultSet rs, int i) throws SQLException {
        TotalGiroReport totalGiroReport = new TotalGiroReport();
        totalGiroReport.setPrice(rs.getBigDecimal("totalmoney"));
        totalGiroReport.getType().setId(rs.getInt("slptype_id"));
        totalGiroReport.getType().setTag(rs.getString("typdname"));
        totalGiroReport.getCurrency().setId(rs.getInt("slcurrency_id"));
        
        totalGiroReport.getBranch().setId(rs.getInt("brnid"));
        totalGiroReport.getBranch().setName(rs.getString("brnname"));
        return totalGiroReport;
        
    }
    
}
