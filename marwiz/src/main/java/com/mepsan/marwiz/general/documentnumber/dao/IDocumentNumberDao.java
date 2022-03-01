/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 01:23:14
 */
package com.mepsan.marwiz.general.documentnumber.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IDocumentNumberDao extends ICrud<DocumentNumber> {

    public List<DocumentNumber> listOfDocumentNumber();
    
    public List<DocumentNumber> listOfDocumentNumber(Item item, Branch branch);
    
    public int delete(DocumentNumber documentNumber);
    
    public int testBeforeDelete(DocumentNumber documentNumber);

}
