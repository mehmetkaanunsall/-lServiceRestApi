/**
 * This interface ...
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   07.03.2018 02:20:59
 */
package com.mepsan.marwiz.general.documenttemplate.dao;

import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IDocumentTemplateDao extends ICrud<DocumentTemplate> {

    public List<DocumentTemplate> listOfDocumentTemplate();

    public DocumentTemplate bringInvoiceTemplate(int type_id);

    public int delete(DocumentTemplate documentTemplate);

    public int updateOnlyJson(DocumentTemplate obj);

}
