/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   16.01.2018 07:59:07
 */
package com.mepsan.marwiz.finance.financingdocument.dao;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FinancingDocumentMapper implements RowMapper<FinancingDocument> {

    @Override
    public FinancingDocument mapRow(ResultSet rs, int i) throws SQLException {
        FinancingDocument financingDocument = new FinancingDocument();
        try {
            financingDocument.setId(rs.getInt("fdocid"));
            financingDocument.setDescription(rs.getString("fdocdescription"));
            financingDocument.setDocumentDate(rs.getTimestamp("fdocdocumentdate"));
            financingDocument.setDocumentNumber(rs.getString("fdocdocumentnumber"));
            financingDocument.getIncomeExpense().setId(rs.getInt("inxid"));
            financingDocument.getIncomeExpense().setName(rs.getString("inxname"));

            financingDocument.setExchangeRate(rs.getBigDecimal("fdocexchangerate"));
            financingDocument.setPrice(rs.getBigDecimal("fdocprice"));

            financingDocument.getCurrency().setId(rs.getInt("fdoccurrency_id"));

            financingDocument.getFinancingType().setId(rs.getInt("fdoctype_id"));
            financingDocument.getFinancingType().setTag(rs.getString("typdname"));

            financingDocument.setInMovementId(rs.getInt("inmovementid"));
            financingDocument.setOutMovementId(rs.getInt("outmovementid"));

            financingDocument.getAccount().setId(rs.getInt("accid"));
            financingDocument.getAccount().setIsPerson(rs.getBoolean("accis_person"));
            financingDocument.getAccount().setName(rs.getString("accname"));
        } catch (Exception e) {
        }

        try {
            financingDocument.getAccount().setTaxOffice(rs.getString("acctaxoffice"));
            financingDocument.getAccount().setTaxNo(rs.getString("acctaxno"));
            financingDocument.getAccount().setPhone(rs.getString("accphone"));

        } catch (Exception e) {
        }
        try {
            financingDocument.getAccount().setTitle(rs.getString("acctitle"));
            financingDocument.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
        } catch (Exception e) {
        }

        try {
            financingDocument.setUserCreated(new UserData());
            financingDocument.getUserCreated().setId(rs.getInt("usid"));
            financingDocument.getUserCreated().setName(rs.getString("name"));
            financingDocument.getUserCreated().setSurname(rs.getString("surname"));

        } catch (Exception e) {

        }
        try {
            financingDocument.getBranch().setId(rs.getInt("fdocbranch_id"));
            financingDocument.getTransferBranch().setId(rs.getInt("fdoctransferbranch_id"));
        } catch (Exception e) {
        }
        try {
            financingDocument.setBankAccountCommissionId(rs.getInt("bacid"));
        } catch (Exception e) {
        }

        return financingDocument;
    }

}
