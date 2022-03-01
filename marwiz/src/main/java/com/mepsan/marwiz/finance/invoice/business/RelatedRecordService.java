/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:50:07
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.finance.invoice.dao.IRelatedRecordDao;
import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.general.model.finance.Invoice;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class RelatedRecordService implements IRelatedRecordService {

    @Autowired
    private IRelatedRecordDao relatedRecordDao;

    public void setRelatedRecordDao(IRelatedRecordDao relatedRecordDao) {
        this.relatedRecordDao = relatedRecordDao;
    }

    @Override
    public List<RelatedRecord> listOfRelatedRecords(Invoice invoice) {
        return relatedRecordDao.listOfRelatedRecords(invoice);
    }

}
