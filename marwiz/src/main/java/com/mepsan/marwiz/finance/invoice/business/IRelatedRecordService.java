/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:49:48 
 */

package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.general.model.finance.Invoice;
import java.util.List;


public interface IRelatedRecordService {
 List<RelatedRecord> listOfRelatedRecords(Invoice invoice);
}

