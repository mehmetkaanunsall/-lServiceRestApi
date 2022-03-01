/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   11.02.2019 02:51:00
 */

package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class MarketShiftInvoiceMapper implements RowMapper<Invoice>{

    @Override
    public Invoice mapRow(ResultSet rs, int i) throws SQLException {
        Invoice invoice =new Invoice();
        invoice.setId(rs.getInt("invid"));
        invoice.setDocumentNumber(rs.getString("documentno"));
        
        return invoice;
    }

}