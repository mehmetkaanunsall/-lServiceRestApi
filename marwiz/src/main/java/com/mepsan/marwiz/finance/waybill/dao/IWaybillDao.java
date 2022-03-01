/**
 *
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 23.01.2018 11:03:16
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;

public interface IWaybillDao extends ICrud<Waybill>, ILazyGrid<Waybill> {

    public CheckDelete testBeforeDelete(Waybill waybill);

    public int delete(Waybill waybill);

    public int updateLogSap(Waybill waybill);
    
    public int createWaybillForOrder(Waybill waybill, String waybillItems);

}
