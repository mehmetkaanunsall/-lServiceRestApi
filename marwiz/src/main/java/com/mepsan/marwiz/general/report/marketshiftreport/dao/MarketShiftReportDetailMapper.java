/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 10:39:03
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.model.general.Sales;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftReportDetailMapper implements RowMapper<Sales> {

    @Override
    public Sales mapRow(ResultSet rs, int i) throws SQLException {
        Sales sales = new Sales();
        sales.setId(rs.getInt("slid"));
        sales.setTotalMoney(rs.getBigDecimal("sltotalmoney"));
        sales.getCurrency().setId(rs.getInt("slcurrency_id"));

        try {
            sales.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            sales.getAccount().setTitle(rs.getString("acctitle"));
        } catch (Exception e) {
        }
        try {
            sales.setProcessDate(rs.getTimestamp("slprocessdate"));
            sales.getPointOfSale().setId(rs.getInt("slpointofsale_id"));
            sales.getPointOfSale().setName(rs.getString("posname"));
            sales.setPosMacAddress(rs.getString("slposmacaddress"));
            sales.getAccount().setId(rs.getInt("slaccount_id"));
            sales.getAccount().setName(rs.getString("accname"));
            sales.getUser().setId(rs.getInt("sluserdata_id"));
            sales.getUser().setName(rs.getString("usname"));
            sales.getUser().setSurname(rs.getString("ussurname"));
            sales.setTotalPrice(rs.getBigDecimal("sltotalprice"));
            sales.setIsReturn(rs.getBoolean("slisreturn"));
            sales.getInvoice().setId(rs.getInt("slinvoice_id"));
            sales.getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));
            sales.getReceipt().setId(rs.getInt("slreceipt_id"));
            sales.getReceipt().setReceiptNo(rs.getString("rcpreceiptno"));
            sales.setTotalDiscount(rs.getBigDecimal("sltotaldiscount"));
            sales.setTotalTax(rs.getBigDecimal("sltotaltax"));
            sales.setDiscountPrice(rs.getBigDecimal("sldiscountprice"));
        } catch (Exception e) {

        }
        
        
       

        return sales;
    }
}
