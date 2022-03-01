/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ebubekir.buker
 */
public class DuePaymentsReportMapper implements RowMapper<DuePaymentsReport> {

    @Override
    public DuePaymentsReport mapRow(ResultSet rs, int i) throws SQLException {
        DuePaymentsReport duePaymentsReport = new DuePaymentsReport();

        duePaymentsReport.setId(rs.getInt("invid"));
        duePaymentsReport.setRemainingMoney(rs.getBigDecimal("invremainingmoney"));
        duePaymentsReport.getCurrency().setId(rs.getInt("crnid"));

        try {
            duePaymentsReport.getCurrency().setCode(rs.getString("code"));
            duePaymentsReport.getBranchSetting().getBranch().setId(rs.getInt("invbranch_id"));
            duePaymentsReport.getBranchSetting().getBranch().setName(rs.getString("brnname"));
            duePaymentsReport.setDocumentNumber(rs.getString("invdocumentnumber"));
            duePaymentsReport.setDocumentSerial(rs.getString("invdocumentserial"));
            duePaymentsReport.setIsPurchase(rs.getBoolean("invispurchase"));
            duePaymentsReport.getAccount().setId(rs.getInt("invaccount_id"));
            duePaymentsReport.getAccount().setName(rs.getString("accname"));
            duePaymentsReport.setInvoiceDate(rs.getTimestamp("invinvoicedate"));
            duePaymentsReport.setDueDate(rs.getTimestamp("invduedate"));
            duePaymentsReport.setTotalMoney(rs.getBigDecimal("invtotalmoney"));
        } catch (Exception e) {
        }

        return duePaymentsReport;
    }

}
