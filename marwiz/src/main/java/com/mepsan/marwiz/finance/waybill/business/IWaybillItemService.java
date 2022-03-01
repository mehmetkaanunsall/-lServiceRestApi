/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.business;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.List;

public interface IWaybillItemService extends ICrudService<WaybillItem> {

    public List<WaybillItem> listWaybillItem(Waybill waybill);

    public List<WaybillItem> listWaybillItemForInvoice(Invoice invoice);

    public List<WaybillItem> listWaybillItemOpenStock(Waybill waybill);

    public CheckDelete testBeforeDelete(WaybillItem waybillItem);

    public int delete(WaybillItem waybillItem);

    public WaybillItem findStock(String barcode, Waybill obj, boolean isAlternativeBarcode);

    public int createAll(List<WaybillItem> list, Waybill obj);

    public String jsonArrayWaybillItems(List<WaybillItem> list);

    public List<WaybillItem> createSampleList();

    public List<WaybillItem> processUploadFile(InputStream inputStream, Waybill selectedWaybill);

    public String jsonArrayForExcelUpload(Waybill waybill, List<WaybillItem> list);

    public int checkStockSalePriceList(String barcode, boolean isPurchase, BranchSetting branchSetting);

    public String jsonArrayWaybillItemsforOrder(List<WaybillItem> list);

    public List<Warehouse> findFuelStockWarehouse(WaybillItem waybillItem, Waybill waybill);

}
