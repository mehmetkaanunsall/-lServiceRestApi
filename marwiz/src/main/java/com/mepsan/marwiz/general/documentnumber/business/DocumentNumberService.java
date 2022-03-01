/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 01:24:18
 */
package com.mepsan.marwiz.general.documentnumber.business;

import com.mepsan.marwiz.general.documentnumber.dao.IDocumentNumberDao;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentNumberService implements IDocumentNumberService {

    @Autowired
    private IDocumentNumberDao documentNumberDao;

    public void setDocumentNumberDao(IDocumentNumberDao documentNumberDao) {
        this.documentNumberDao = documentNumberDao;
    }

    @Override
    public List<DocumentNumber> listOfDocumentNumber() {
        return documentNumberDao.listOfDocumentNumber();
    }

    @Override
    public int create(DocumentNumber obj) {
        return documentNumberDao.create(obj);
    }

    @Override
    public int update(DocumentNumber obj) {
        return documentNumberDao.update(obj);
    }

    @Override
    public List<DocumentNumber> listOfDocumentNumber(Item item, Branch branch) {
        return documentNumberDao.listOfDocumentNumber(item, branch);
    }

    @Override
    public int delete(DocumentNumber documentNumber) {
        return documentNumberDao.delete(documentNumber);
    }

    @Override
    public int testBeforeDelete(DocumentNumber documentNumber) {
        return documentNumberDao.testBeforeDelete(documentNumber);
    }

}
