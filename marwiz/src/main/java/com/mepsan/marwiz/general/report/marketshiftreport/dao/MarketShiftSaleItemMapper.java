/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 04:52:29
 */
package com.mepsan.marwiz.general.report.marketshiftreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.general.SaleItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MarketShiftSaleItemMapper implements RowMapper<SaleItem> {

    @Override
    public SaleItem mapRow(ResultSet rs, int i) throws SQLException {
        SaleItem saleItem = new SaleItem();
        saleItem.setId(rs.getInt("sliid"));
        saleItem.setProcessDate(rs.getTimestamp("sliprocessdate"));
        saleItem.getUnit().setId(rs.getInt("sliunit_id"));
        saleItem.getUnit().setSortName(rs.getString("guntsortname"));
        saleItem.getUnit().setUnitRounding(rs.getInt("guntunitsorting"));
        saleItem.setQuantity(rs.getBigDecimal("sliquantity"));
        saleItem.setUnitPrice(rs.getBigDecimal("sliunitprice"));
        saleItem.setTotalPrice(rs.getBigDecimal("slitotalprice"));
        saleItem.setTotalMoney(rs.getBigDecimal("slitotalmoney"));
        saleItem.getCurrency().setId(rs.getInt("slicurrency_id"));
        saleItem.getStock().setId(rs.getInt("slistock_id"));
        saleItem.getStock().setName(rs.getString("stckname"));
        saleItem.getStock().setCode(rs.getString("stckcode"));
        saleItem.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        saleItem.getStock().setBarcode(rs.getString("stckbarcode"));
        saleItem.setIsManagerDiscount(rs.getBoolean("sliis_managerdiscount"));
        saleItem.getManagerUserData().setId(rs.getInt("slimanageruserdata_id"));
        saleItem.getManagerUserData().setName(rs.getString("us1name"));
        saleItem.getManagerUserData().setSurname(rs.getString("us1surname"));
        try {
            saleItem.setCategory(rs.getString("category"));
            saleItem.setCategory(StaticMethods.findCategories(saleItem.getCategory()));
            saleItem.getStock().getBrand().setId(rs.getInt("brid"));
            saleItem.getStock().getBrand().setName(rs.getString("brname"));
            saleItem.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
            saleItem.getStock().getSupplier().setName(rs.getString("acc1name"));
            saleItem.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            saleItem.getStock().getCentralSupplier().setName(rs.getString("csppname"));
            saleItem.getSales().getAccount().setName(rs.getString("accname"));
            saleItem.getSales().getReceipt().setId(rs.getInt("slreceipt_id"));
            saleItem.getSales().getReceipt().setReceiptNo(rs.getString("receiptno"));

            saleItem.getSales().getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));
            saleItem.getSales().getAccount().setTitle(rs.getString("acctitle"));
            saleItem.getSales().getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            saleItem.getSales().setIsReturn(rs.getBoolean("slisreturn"));

        } catch (Exception e) {
        }

        return saleItem;
    }

}
