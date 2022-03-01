/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 03:23:07
 */
package com.mepsan.marwiz.general.report.dailysalesreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DailySalesReportMapper implements RowMapper<DailySalesReport> {

    @Override
    public DailySalesReport mapRow(ResultSet rs, int i) throws SQLException {
        DailySalesReport dailySalesReport = new DailySalesReport();
        dailySalesReport.setStringResult(rs.getString("r_result"));
        return dailySalesReport;
    }

}
