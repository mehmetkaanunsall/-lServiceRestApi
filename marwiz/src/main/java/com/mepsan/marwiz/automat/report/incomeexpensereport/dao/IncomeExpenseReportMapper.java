/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:27:36 PM
 */
package com.mepsan.marwiz.automat.report.incomeexpensereport.dao;

import com.mepsan.marwiz.automat.report.automatsalesreport.dao.AutomatSalesReport;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class IncomeExpenseReportMapper implements RowMapper<AutomatSalesReport> {

    @Override
    public AutomatSalesReport mapRow(ResultSet rs, int i) throws SQLException {
        AutomatSalesReport automatSaleReport = new AutomatSalesReport();

        try {
            automatSaleReport.getStock().setId(rs.getInt("slstock_id"));
            automatSaleReport.getStock().setName(rs.getString("stckname"));
        } catch (Exception e) {
        }

        try { // sarfiyat grafiği için
            automatSaleReport.setWaste(rs.getBigDecimal("waste"));

        } catch (Exception e) {
        }

        try { // gelir gider grafiği için
            automatSaleReport.setIncome(rs.getBigDecimal("income"));
            automatSaleReport.setExpense(rs.getBigDecimal("expense"));
        } catch (Exception e) {
        }
        try {
            automatSaleReport.setQuantitiy(rs.getBigDecimal("quantitiy"));
            automatSaleReport.setOperationTime(rs.getInt("sloperationtime"));
            automatSaleReport.setTotalIncome(rs.getBigDecimal("income"));
            automatSaleReport.setTotalExpense(rs.getBigDecimal("expense"));

            automatSaleReport.setElectricQuantity(rs.getBigDecimal("electricquantitiy"));
            automatSaleReport.setElectricOperationTime(rs.getBigDecimal("slelectricoperationtime"));
            automatSaleReport.setElectricExpense(rs.getBigDecimal("electricexpense"));
        } catch (Exception e) {
        }
        try {
            automatSaleReport.setTotalWinnings(rs.getBigDecimal("winngins"));
        } catch (Exception e) {
        }

        try {
            automatSaleReport.getStock().getUnit().setId(rs.getInt("stckuntid"));
            automatSaleReport.getStock().getUnit().setSortName(rs.getString("untsrotname"));
        } catch (Exception e) {
        }
        try {
            automatSaleReport.setTotalElectricAmount(rs.getBigDecimal("elecquantity"));
        } catch (Exception e) {
        }
        try {

            automatSaleReport.setWaterWorkingAmount(rs.getBigDecimal("waterworkingamount"));
            automatSaleReport.setWaterWorkingTime(rs.getInt("waterworkingtime"));
            automatSaleReport.setWaterExpense(rs.getBigDecimal("waterexpense"));
            automatSaleReport.setWaterWaste(rs.getBigDecimal("waterwase"));

        } catch (Exception e) {
        }

        return automatSaleReport;
    }

}
