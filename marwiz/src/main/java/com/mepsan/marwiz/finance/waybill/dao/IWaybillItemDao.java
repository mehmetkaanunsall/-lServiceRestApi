/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWaybillItemDao extends ICrud<WaybillItem> {

    public List<WaybillItem> listWaybillItem(Waybill waybill, String where);

    public List<WaybillItem> listWaybillItemForInvoice(Invoice invoice);

    public CheckDelete testBeforeDelete(WaybillItem waybillItem);

    public int delete(WaybillItem waybillItem);

    public WaybillItem findStock(String barcode, Waybill obj, boolean isAlternativeBarcode);

    public String excelItemInsert(Waybill waybill, String json);

    public String findWaybillForInvoice(int stockId, int invoiceId);

    public int checkStockSalePriceList(String barcode, boolean isPurchase, BranchSetting branchSetting);

    public List<Warehouse> findFuelStockWarehouse(WaybillItem waybillItem, Waybill waybill);

}
