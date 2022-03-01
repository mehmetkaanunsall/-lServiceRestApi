/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.10.2018 13:37:26
 */
package com.mepsan.marwiz.finance.financingdocument.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FinancingDocumentVoucherMapper implements RowMapper<FinancingDocumentVoucher> {

    @Override
    public FinancingDocumentVoucher mapRow(ResultSet rs, int i) throws SQLException {
        FinancingDocumentVoucher documentVoucher = new FinancingDocumentVoucher();
        documentVoucher.setId(rs.getInt("fdocid"));
        documentVoucher.setDescription(rs.getString("fdocdescription"));
        documentVoucher.setDocumentDate(rs.getDate("fdocdocumentdate"));
        documentVoucher.setDocumentNumber(rs.getString("fdocdocumentnumber"));
        documentVoucher.setExchangeRate(rs.getBigDecimal("fdocexchangerate"));
        documentVoucher.getCurrency().setId(rs.getInt("fdoccurrency_id"));

        documentVoucher.getFinancingType().setId(rs.getInt("fdoctype_id"));
        // documentVoucher.getFinancingType().setTag(rs.getString("typdname"));
        documentVoucher.getAccount().setId(rs.getInt("accid"));
        documentVoucher.getAccount().setIsPerson(rs.getBoolean("accis_person"));
        documentVoucher.getAccount().setName(rs.getString("accname"));
        documentVoucher.getAccount().setTaxOffice(rs.getString("acctaxoffice"));
        documentVoucher.getAccount().setTaxNo(rs.getString("acctaxno"));
        documentVoucher.getAccount().setPhone(rs.getString("accphone"));
        documentVoucher.setBankAccount(new BankAccount());
        documentVoucher.getBankAccount().setId(rs.getInt("bambankaccount_id"));
        documentVoucher.getBankAccount().setName(rs.getString("baname"));
        documentVoucher.getBankAccount().setAccountNumber(rs.getString("baaccountnumber"));
        documentVoucher.getBankAccount().getBankBranch().setId(rs.getInt("babankbranch_id"));
        documentVoucher.getBankAccount().getBankBranch().setName(rs.getString("bbname"));
        documentVoucher.getBankAccount().getBankBranch().getBank().setId(rs.getInt("bankabank_id"));
        documentVoucher.getBankAccount().getBankBranch().getBank().setName(rs.getString("bankaname"));
        documentVoucher.getChequeBill().setId(rs.getInt("cbid"));
        documentVoucher.getChequeBill().setPortfolioNumber(rs.getString("cbportfolionumber"));
        documentVoucher.getChequeBill().setExpiryDate(rs.getTimestamp("cbexpirydate"));
        documentVoucher.getChequeBill().setAccountNumber(rs.getString("cbaccountnumber"));
        documentVoucher.getChequeBill().setDocumentSerial(rs.getString("documentserial"));
        documentVoucher.getChequeBill().setDocumentNo(rs.getString("cbdocumentnumber"));
        documentVoucher.setPrice(rs.getBigDecimal("cpprice"));

        documentVoucher.getRecipientPerson().setId(rs.getInt("usid"));
        documentVoucher.getRecipientPerson().setName(rs.getString("name"));
        documentVoucher.getRecipientPerson().setSurname(rs.getString("surname"));

        documentVoucher.getDeliveryPerson().setId(rs.getInt("accid"));
        documentVoucher.getDeliveryPerson().setName(rs.getString("accname"));
        documentVoucher.getBranch().setId(rs.getInt("fdocbranch_id"));
        documentVoucher.getTransferBranch().setId(rs.getInt("fdoctransferbranch_id"));
        documentVoucher.setBankAccountCommissionId(rs.getInt("bacid"));
        return documentVoucher;
    }

}
