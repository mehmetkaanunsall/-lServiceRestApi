package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IncomingInvoicesItem;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IIncomingEInvoicesService extends ICrud<IncomingEInvoice>, ILazyGrid<IncomingEInvoice> {

    public void listOfIncomingInvoice();

    public List<EInvoice> uListOfIncomingInvoice(Date beginDate, Date endDate);

    public List<EInvoice> listGetInvoices(int first, int pageSize, String sortField, String sortOrder, java.util.Map<String, Object> filters, Date beginDate, Date endDate);

    public List<EInvoice> uListGetInvoices(List<IncomingEInvoice> listEInvoices);

    public List<IncomingInvoicesItem> listOfİtem();

    public int update(IncomingEInvoice obj);

    public int sendApproval(EInvoice obj);

    public int sendUApproval(EInvoice obj, String eInvoiceUUID);

    public String approvalMessage();

    public List<IncomingInvoicesItem> bringItemList(Invoice obj);

    public List<IncomingInvoicesItem> uBringItemList(Invoice obj);

    public int createInvoice(EInvoice obj, String invoiceItems, String waybillItems, Integer ieInvoiceId, Integer ieInvoiceApprovalStatusId, String ieInvoiceApprovalDescription);

    public String jsonArrayInvoiceItems(List<IncomingInvoicesItem> list);

    public String jsonArrayWaybillItems(List<IncomingInvoicesItem> list);

    public List<IncomingInvoicesItem> calculater(List<IncomingInvoicesItem> list, EInvoice obj);

    public List<Unit> bringUnit(Stock stock);

    public int count(String where, Date beginDate, Date endDate);

    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem);

    public IncomingInvoicesItem calculaterItem(IncomingInvoicesItem invoiceItem);

    public List<Stock> listStock(String stockEInvoiceIntegrationCodeList, EInvoice selectedObject);

    public int updateStockIntegrationCode(IncomingInvoicesItem obj, String stockInfoIds);

    public List<Stock> findStockInfo(String stockEInvoiceIntegrationCode);

    public List<EInvoice> findInMarwizEInvoices(int first, int pageSize, String sortField, String sortOrder, java.util.Map<String, Object> filters, Date beginDate, Date endDate, String where, boolean isLazy);

    public String createWhere(List<Account> accountList, String invoiceNo, int operationType, int dateFilterType, Date beginDate, Date endDate);

    public int updateArchive(String ids, int updateType);
}
