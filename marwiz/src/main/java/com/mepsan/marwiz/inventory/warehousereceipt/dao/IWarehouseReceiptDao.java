/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 05:20:13
 */
package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;

public interface IWarehouseReceiptDao extends ILazyGrid<WarehouseReceipt>, ICrud<WarehouseReceipt> {

    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt);

    public int delete(WarehouseReceipt warehouseReceipt);

    public int sendWasteCenter(WarehouseReceipt warehouseReceipt);

    public int deleteWasteInfo(WarehouseReceipt warehouseReceipt);

    public int updateLogSap(WarehouseReceipt warehouseReceipt);

}
