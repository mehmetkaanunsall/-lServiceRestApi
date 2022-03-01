/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:49:48
 */
package com.mepsan.marwiz.finance.waybill.business;

import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.List;

public interface IWaybillRelatedRecordService {

    List<RelatedRecord> listOfRelatedRecords(Waybill waybill);
}
