/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.01.2018 05:21:17
 */
package com.mepsan.marwiz.inventory.warehousereceipt.business;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.inventory.warehousereceipt.dao.IWarehouseReceiptDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseReceiptService implements IWarehouseReceiptService {

    @Autowired
    private IWarehouseReceiptDao warehouseReceiptDao;

    @Override
    public List<WarehouseReceipt> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return warehouseReceiptDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return warehouseReceiptDao.count(where);
    }

    @Override
    public int create(WarehouseReceipt obj) {
        return warehouseReceiptDao.create(obj);
    }

    @Override
    public int update(WarehouseReceipt obj) {
        return warehouseReceiptDao.update(obj);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt) {
        return warehouseReceiptDao.testBeforeDelete(warehouseReceipt);
    }

    @Override
    public int delete(WarehouseReceipt warehouseReceipt) {
        return warehouseReceiptDao.delete(warehouseReceipt);
    }

    @Override
    public int sendWasteCenter(WarehouseReceipt warehouseReceipt) {
        return warehouseReceiptDao.sendWasteCenter(warehouseReceipt);
    }

    @Override
    public int deleteWasteInfo(WarehouseReceipt warehouseReceipt) {
        return warehouseReceiptDao.deleteWasteInfo(warehouseReceipt);
    }

    @Override
    public int updateLogSap(WarehouseReceipt warehouseReceipt) {
        return warehouseReceiptDao.updateLogSap(warehouseReceipt);
    }

    @Override
    public WarehouseReceipt findWarehouseReceipt(WarehouseReceipt warehouseReceipt) {
        Map<String, Object> filt = new HashMap<>();
        List<WarehouseReceipt> list = warehouseReceiptDao.findAll(0, 10, "whr.id", "ASC", filt, " AND whr.id = " + warehouseReceipt.getId());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new WarehouseReceipt();
        }
    }
}
