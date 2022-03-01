/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.dao;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author ali.kurt
 */
public class OfficialAccountingMapper implements RowMapper<OfficalAccounting> {

    int type;

    public OfficialAccountingMapper(int type) {
        this.type = type;
    }

    @Override
    public OfficalAccounting mapRow(ResultSet rs, int i) throws SQLException {
        OfficalAccounting oa = new OfficalAccounting();
        oa.setId(rs.getInt("r_id"));
        oa.setEvent(rs.getInt("r_event"));
        oa.setSendData(rs.getString("r_jsondata"));
        oa.setResponse(rs.getString("r_response"));
        oa.setSendCount(rs.getInt("r_sendcount"));
        oa.setSendDate(rs.getTimestamp("r_senddate"));
        oa.setIsSend(rs.getBoolean("r_is_send"));

        switch (type) {
            case 1:
                //account
                try {
                    Account account = new Account();
                    account.setId(rs.getInt("r_account_id"));
                    account.setName(rs.getString("r_name"));
                    account.setTaxNo(rs.getString("r_taxno"));
                    account.setCode(rs.getString("r_code"));
                    oa.setAccount(account);
                } catch (Exception e) {
                }
                break;
            case 2:
                //stock
                try {
                    Stock stock = new Stock();
                    stock.setId(rs.getInt("r_stock_id"));
                    stock.setName(rs.getString("r_name"));
                    stock.setBarcode(rs.getString("r_barcode"));
                    oa.setStock(stock);
                } catch (Exception e) {
                }
                break;
            case 3:
                //safe
                try {
                    Safe sf = new Safe();
                    sf.setId(rs.getInt("r_safe_id"));
                    sf.setName(rs.getString("r_name"));
                    sf.setCode(rs.getString("r_code"));
                    oa.setSafe(sf);
                } catch (Exception e) {
                }
                break;
            case 4:
                //bankaccount
                try {
                    BankAccount ba = new BankAccount();
                    ba.setId(rs.getInt("r_bankaccount_id"));
                    ba.setName(rs.getString("r_bankaccountname"));
                    ba.setAccountNumber(rs.getString("r_accountnumber"));
                    ba.getBankBranch().getBank().setName(rs.getString("r_bankname"));
                    oa.setBankAccount(ba);
                } catch (Exception e) {
                }
                break;

            case 5:
                //warehouse
                try {
                    Warehouse w = new Warehouse();
                    w.setId(rs.getInt("r_warehouse_id"));
                    w.setName(rs.getString("r_name"));
                    w.setCode(rs.getString("r_code"));
                    oa.setWarehouse(w);
                } catch (Exception e) {
                }
                break;
            case 6:
            case 7:
                
                try {
                    FinancingDocument document = new FinancingDocument();
                    oa.setTypeId(rs.getInt("r_processtype"));
                    document.getFinancingType().setId(rs.getInt("r_processtype"));
                    document.setDocumentNumber(rs.getString("r_documentnumber"));
                    document.setDocumentDate(rs.getTimestamp("r_processdate"));
                    document.setDescription(rs.getString("r_description"));
                    document.setPrice(rs.getBigDecimal("r_price"));
                    oa.setFinancingDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return oa;
    }

}
