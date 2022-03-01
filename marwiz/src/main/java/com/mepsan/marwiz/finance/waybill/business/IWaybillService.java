/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 05.04.2017 11:48:48
 */
package com.mepsan.marwiz.finance.waybill.business;

import com.mepsan.marwiz.finance.waybill.presentation.WaybillBean.WaybillParam;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.finance.WaybillItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IWaybillService extends ICrudService<Waybill>, ILazyGridService<Waybill> {

    public Waybill find(int waybillId);

    public CheckDelete testBeforeDelete(Waybill waybill);

    public int delete(Waybill waybill);

    public String jsonArrayWarehouses(List<Warehouse> warehouses);

    public void createExcelFile(Waybill waybill, List<WaybillItem> listOfIWaybillItems);

    public String createWhere(WaybillParam searchObject, List<Branch> listOfBranch);

    public int updateLogSap(Waybill waybill);

    public Waybill findWaybill(Waybill waybill);
    
    public int createWaybillForOrder(Waybill waybill, List<WaybillItem> listOfItem);
    
    public String jsonArrayWaybillItems(List<WaybillItem> list);

}
