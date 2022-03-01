/**
 * This interface ...
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   07.03.2018 02:16:25
 */
package com.mepsan.marwiz.general.documenttemplate.business;

import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IDocumentTemplateService extends ICrudService<DocumentTemplate> {

    public List<DocumentTemplate> listOfDocumentTemplate();

    public DocumentTemplate bringInvoiceTemplate(int type_id);

    public int delete(DocumentTemplate documentTemplate);

    public int updateOnlyJson(DocumentTemplate obj);

}
