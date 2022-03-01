/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Mar 1, 2018 3:03:11 PM
 */
package com.mepsan.marwiz.general.documenttemplate.business;

import com.mepsan.marwiz.general.documenttemplate.dao.IDocumentTemplateDao;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentTemplateService implements IDocumentTemplateService {

    @Autowired
    private IDocumentTemplateDao documentTemplateDao;

    public void setDocumentTemplateDao(IDocumentTemplateDao documentTemplateDao) {
        this.documentTemplateDao = documentTemplateDao;
    }

    @Override
    public List<DocumentTemplate> listOfDocumentTemplate() {
        return documentTemplateDao.listOfDocumentTemplate();
    }

    @Override
    public int create(DocumentTemplate obj) {
        return documentTemplateDao.create(obj);
    }

    @Override
    public int update(DocumentTemplate obj) {
        return documentTemplateDao.update(obj);
    }

    @Override
    public DocumentTemplate bringInvoiceTemplate(int type_id) {
        return documentTemplateDao.bringInvoiceTemplate(type_id);
    }

    @Override
    public int delete(DocumentTemplate documentTemplate) {
        return documentTemplateDao.delete(documentTemplate);
    }

    @Override
    public int updateOnlyJson(DocumentTemplate obj) {
        return documentTemplateDao.updateOnlyJson(obj);
    }

}
