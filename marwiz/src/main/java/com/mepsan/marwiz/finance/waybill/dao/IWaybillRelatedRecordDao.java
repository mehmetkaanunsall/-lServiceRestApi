/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:34:55
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.finance.invoice.dao.*;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.List;

public interface IWaybillRelatedRecordDao {

    List<RelatedRecord> listOfRelatedRecords(Waybill waybill);
}
