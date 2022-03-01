/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:15:25 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.dao;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class IncomeExpenseReportDetailMapper implements RowMapper<AutomatSalesReport> {

    @Override
    public AutomatSalesReport mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSalesReport automatSaleReport = new AutomatSalesReport();

        automatSaleReport.getStock().setId(rs.getInt("slstock_id"));
        automatSaleReport.getStock().setName(rs.getString("stckname"));
        automatSaleReport.setId(rs.getInt("slid"));
        automatSaleReport.setSaleDateTime(rs.getTimestamp("slsaledatetime"));
        automatSaleReport.setPlatformNo(rs.getString("slplatformno"));
        automatSaleReport.setNozzleNo(rs.getString("slnozzleno"));
        automatSaleReport.setTankNo(rs.getString("sltankno"));
        automatSaleReport.setUnitPrice(rs.getBigDecimal("slunitprice"));
        automatSaleReport.setShiftNo(rs.getString("slshiftno"));
        automatSaleReport.setOperationAmount(rs.getBigDecimal("sloperationamount"));
        automatSaleReport.setTotalMoney(rs.getBigDecimal("sltotalmoney"));
        automatSaleReport.setPaymentType(rs.getInt("slpaymenttypeid"));

        automatSaleReport.getStock().getUnit().setId(rs.getInt("stckunitid"));
        automatSaleReport.getStock().getUnit().setSortName(rs.getString("untsortname"));
        automatSaleReport.getStock().getUnit().setUnitRounding(rs.getInt("untunitrounding"));
        return automatSaleReport;
    }

}
