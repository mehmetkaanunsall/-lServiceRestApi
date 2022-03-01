/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 01:24:07
 */
package com.mepsan.marwiz.general.documentnumber.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IDocumentNumberService extends ICrudService<DocumentNumber> {

    public List<DocumentNumber> listOfDocumentNumber();
    
    public List<DocumentNumber> listOfDocumentNumber(Item item, Branch branch);
    
    public int delete(DocumentNumber documentNumber);
    
    public int testBeforeDelete(DocumentNumber documentNumber);
}
