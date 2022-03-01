/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.06.2019 04:14:04
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPaymentFinancingDocumentCon;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftPaymentFinancingDocumentConMapper implements RowMapper<MarketShiftPaymentFinancingDocumentCon> {

    @Override
    public MarketShiftPaymentFinancingDocumentCon mapRow(ResultSet rs, int i) throws SQLException {
        MarketShiftPaymentFinancingDocumentCon paymentFinancingDocumentCon = new MarketShiftPaymentFinancingDocumentCon();
        paymentFinancingDocumentCon.setId(rs.getInt("shpconid"));
        paymentFinancingDocumentCon.getFinancingDocument().setId(rs.getInt("shpconfinancingdocument_id"));
        paymentFinancingDocumentCon.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
        paymentFinancingDocumentCon.getFinancingDocument().setPrice(rs.getBigDecimal("fdocprice"));
        paymentFinancingDocumentCon.getFinancingDocument().getCurrency().setId(rs.getInt("fdoccurrency"));
        paymentFinancingDocumentCon.getFinancingDocument().getIncomeExpense().setId(rs.getInt("fiemincomeexpense_id"));
        paymentFinancingDocumentCon.getFinancingDocument().getIncomeExpense().setName(rs.getString("fiename"));
        paymentFinancingDocumentCon.getFinancingDocument().getIncomeExpense().setIsIncome(rs.getBoolean("fieis_income"));

        return paymentFinancingDocumentCon;
    }

}