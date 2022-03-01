/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 02.01.2019 15:09:27
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ResponseSalesReturnMapper implements RowMapper<ResponseSalesReturn> {

    @Override
    public ResponseSalesReturn mapRow(ResultSet rs, int i) throws SQLException {

        ResponseSalesReturn responseSalesReturn = new ResponseSalesReturn();
        try {//Satış İade İşlemi
            responseSalesReturn.setSaleId(rs.getInt("saleid"));
            responseSalesReturn.setReceiptId(rs.getInt("receiptId"));
            responseSalesReturn.setReceiptNo(rs.getString("receiptNo"));
            responseSalesReturn.setBalance(rs.getBigDecimal("balance"));
        } catch (Exception e) {
        }
        try {//Satış İade Kasa Kontrolü
            responseSalesReturn.setSaleId(rs.getInt("r_response"));
        } catch (Exception e) {
        }

        return responseSalesReturn;

    }

}
