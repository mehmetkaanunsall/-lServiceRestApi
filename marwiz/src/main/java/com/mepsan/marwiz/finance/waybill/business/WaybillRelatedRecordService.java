/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:50:07
 */
package com.mepsan.marwiz.finance.waybill.business;

import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.finance.waybill.dao.IWaybillRelatedRecordDao;

public class WaybillRelatedRecordService implements IWaybillRelatedRecordService {

    @Autowired
    private IWaybillRelatedRecordDao waybillRelatedRecordDao;

    public void setWaybillRelatedRecordDao(IWaybillRelatedRecordDao waybillRelatedRecordDao) {
        this.waybillRelatedRecordDao = waybillRelatedRecordDao;
    }

    @Override
    public List<RelatedRecord> listOfRelatedRecords(Waybill waybill) {
        return waybillRelatedRecordDao.listOfRelatedRecords(waybill);
    }

}
