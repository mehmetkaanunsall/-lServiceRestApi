/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 09:35:23
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.InvoiceReport;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInvoiceService extends ICrudService<Invoice>, ILazyGridService<Invoice> {

    public int createInvoiceForWaybill(Invoice obj, Waybill waybill, List<InvoiceItem> listOfItem);

    public Invoice findInvoice(Invoice invoice);

    public int sendInvoiceCenter(Invoice invoice);

    public int getInvoiceSaleId(Invoice invoice);

    public int delete(Invoice invoice);

    public int deletePeriodInvoice(Invoice invoice);

    public String createWhere(InvoiceReport invoice, List<BranchSetting> listOfBranch);

    public Invoice findDuplicateInvoice(Invoice invoice);

    public int createInvoiceForAgreement(Invoice invoice, List<InvoiceItem> listOfItem, CustomerAgreements customerAgreements, CreditReport creditReport);

    public void createExcelFile(Invoice invoice, List<InvoiceItem> listOfInvoiceItems, String totalAmount, BigDecimal subTotal, BigDecimal totalDiscount, String taxRates, List<InvoiceItem> listOfTaxs, BranchSetting branchSetting);

    public List<CheckDelete> testBeforeDelete(Invoice invoice);

    public List<Invoice> invoiceBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int invoiceBookCount(String where, String type, List<Object> param);

    public int controlTankItemAvailable(String warehouseList, Stock stock);

    public int controlAutomationWarehouse(String warehouseList);

    public boolean sendSapReverse(Invoice obj);

    public int updateLogSap(Invoice invoice);

    public int createInvoiceForOrder(Invoice invoice, List<InvoiceItem> listOfItem);

    public String jsonArrayInvoiceItems(List<InvoiceItem> list);

    public int insertHistory(Invoice invoice);

    public int findSaleForInvoice(BranchSetting branchSetting, int invoiceId, boolean isDelete);

    public int createParoSales(int saleId);

}
