/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 09:34:10
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import com.mepsan.marwiz.system.sapintegration.dao.IntegrationForSap;
import java.util.List;
import java.util.Map;

public interface IInvoiceDao extends ICrud<Invoice>, ILazyGrid<Invoice> {

    public int createInvoiceWaybillCon(Invoice obj, Waybill waybill);

    public int sendInvoiceCenter(Invoice invoice);

    public int getInvoiceSaleId(Invoice invoice);

    public int delete(Invoice invoice);

    public int deletePeriodInvoice(Invoice invoice);

    public Invoice findDuplicateInvoice(Invoice invoice);

    public int createInvoiceForAgreement(Invoice invoice, InvoiceItem invoiceItem, CustomerAgreements customerAgreements, CreditReport creditReport);

    public List<CheckDelete> testBeforeDelete(Invoice invoice);

    public List<Invoice> invoiceBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int invoiceBookCount(String where, String type, List<Object> param);

    public int controlTankItemAvailable(String warehouseList, Stock stock);

    public int controlAutomationWarehouse(String warehouseList);

    public int updateLogSap(Invoice invoice);

    public int createLogSapSaleInvoice(IntegrationForSap obj, Invoice invoice);

    public int createInvoiceForOrder(Invoice invoice, String invoiceItems);

    public int insertHistory(Invoice invoice);

    public int findSaleForInvoice(BranchSetting branchSetting, int invoiceId, boolean isDelete);

    public int createParoSales(int saleId);
}
