/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2017 14:09:16
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface IInvoiceItemService extends ICrudService<InvoiceItem> {

    public List<InvoiceItem> listInvoiceStocks(Invoice invoice, String type);

    public int createAll(List<InvoiceItem> list, Invoice obj);

    public int updateAll(List<InvoiceItem> list, Invoice obj);

    public InvoiceItem calculater(InvoiceItem invoiceItem, int type);

    public int delete(InvoiceItem item);

    public List<InvoiceItem> findAllSaleItemForCredit(CustomerAgreements customerAgreements, String where);

    public String jsonArrayInvoiceItems(List<InvoiceItem> list);

    public InvoiceItem findStock(String barcode, Invoice obj, boolean isAlternativeBarcode, boolean isInvoiceStockSalePriceList);

    public InvoiceItem totalQuantityForInvoice(Invoice obj, int stockId, Date begin, Date end, Branch branch);

    public List<InvoiceItem> createSampleList();

    public List<InvoiceItem> processUploadFile(InputStream inputStream, Invoice invoice);

    public String jsonArrayForExcelUpload(Invoice invoiceItem, List<InvoiceItem> list);

    public List<InvoiceItem> findInvoiceItemLastPrice(String stockList, BranchSetting branchSetting);

    public void calculateProfit(InvoiceItem obj, int type);

    public List<CheckDelete> testBeforeDelete(InvoiceItem invoiceitem);

    public String jsonArrayInvoiceItemsForWaitedInvoice(List<InvoiceItem> list, Invoice invoice);

    public int updateWaitedInvoiceJson(List<InvoiceItem> listOfItem, Invoice invoice);

    public String jsonArrayInvoiceItemsforOrder(List<InvoiceItem> list);

    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem, Invoice inv);

    public List<BranchSetting> findUserAuthorizeBranch();

    public List<InvoiceItem> processExcelUpload(List<InvoiceItem> excelList, Invoice invoice);

}
