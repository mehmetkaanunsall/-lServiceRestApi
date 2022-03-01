/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elif.mart
 */
public interface IIncomingEInvoicesDao extends ICrud<IncomingEInvoice>, ILazyGrid<IncomingEInvoice> {

    public List<IncomingEInvoice> getInvoicesData(String result);

    public List<Unit> bringUnit(Stock stock);

    public List<Integer> create(List<IncomingEInvoice> listOfInsert);

    public int update(IncomingEInvoice obj);

    public int updateRequestNumber();

    public IncomingEInvoice bringEInvoiceItem(Invoice obj);

    public int createInvoice(EInvoice obj, String invoiceItems, String waybillItems, Integer ieInvoiceId, Integer ieInvoiceApprovalStatusId, String ieInvoiceApprovalDescription);

    public Exchange bringExchangeRate(Currency currency, Currency resCurrency);

    public List<IncomingEInvoice> findall(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Date beginDate, Date endDate, boolean isLazy);

    public int count(String where, Date beginDate, Date endDate);

    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem);

    public List<Stock> listStock(String stockEInvoiceIntegrationCodeList, EInvoice selectedObject);

    public int updateStockIntegrationCode(IncomingInvoicesItem obj, String stockInfoIds);

    public List<Stock> findStockInfo(String stockEInvoiceIntegrationCode);

    public int updateArchive(String ids, int updateType);

    public List<IncomingEInvoice> findGIBIncomingInvoices(String ids);

}
