/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 05:20:59
 */
package com.mepsan.marwiz.inventory.warehousereceipt.business;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IWarehouseReceiptService extends ILazyGridService<WarehouseReceipt>, ICrudService<WarehouseReceipt> {

    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt);

    public int delete(WarehouseReceipt warehouseReceipt);

    public int sendWasteCenter(WarehouseReceipt warehouseReceipt);

    public int deleteWasteInfo(WarehouseReceipt warehouseReceipt);

    public int updateLogSap(WarehouseReceipt warehouseReceipt);

    public WarehouseReceipt findWarehouseReceipt(WarehouseReceipt warehouseReceipt);

}
