/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 14:01:04
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IInvoiceItemDao extends ICrud<InvoiceItem> {

    public List<InvoiceItem> listInvoiceStocks(Invoice invoice, String type);

    public int delete(InvoiceItem item);

    public List<InvoiceItem> findAllSaleItemForCredit(CustomerAgreements customerAgreements, String where);

    public InvoiceItem findStock(String barcode, Invoice obj, boolean isAlternativeBarcode, boolean isInvoiceStockSalePriceList);

    public InvoiceItem totalQuantityForInvoice(Invoice obj, int stockId, Date begin, Date end, Branch branch);

    public String excelItemInsert(Invoice waybill, String json);

    public List<InvoiceItem> findInvoiceItemLastPrice(String stockList, BranchSetting branchSetting);

    public List<CheckDelete> testBeforeDelete(InvoiceItem invoiceitem);

    public int updateWaitedInvoiceJson(String jsonItem, Invoice invoice);

    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem, Invoice inv);

    public List<BranchSetting> findUserAuthorizeBranch();
    
     public List<InvoiceItem> processUploadExcelItemsControl(String jsonExcelItems, Invoice inv);

}
