/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 04:30:33
 */
package com.mepsan.marwiz.finance.financingdocument.business;

import com.mepsan.marwiz.finance.financingdocument.dao.FinancingDocumentVoucher;
import com.mepsan.marwiz.finance.financingdocument.dao.IFinancingDocumentDao;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class FinancingDocumentService implements IFinancingDocumentService {

    @Autowired
    private IFinancingDocumentDao financingDocumentDao;

    public void setFinancingDocumentDao(IFinancingDocumentDao financingDocumentDao) {
        this.financingDocumentDao = financingDocumentDao;
    }

    @Override
    public List findAll(int first, int pageSize, String sortField, String sortOrder, Map filters, String where) {
        return financingDocumentDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return financingDocumentDao.count(where);
    }

    @Override
    public FinancingDocument findFinancingDocument(FinancingDocument fd) {
        return financingDocumentDao.findFinancingDocument(fd);
    }

    @Override
    public int create(FinancingDocument obj, int inmovementId, int outmovementId) {
        return financingDocumentDao.create(obj, inmovementId, outmovementId);
    }

    @Override
    public int update(FinancingDocument obj, int inmovementId, int outmovementId) {
        return financingDocumentDao.update(obj, inmovementId, outmovementId);
    }

    @Override
    public List<FinancingDocumentVoucher> listOfVancourDetail(FinancingDocument obj) {
        return financingDocumentDao.listOfVancourDetail(obj);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(FinancingDocument financingDocument) {
        return financingDocumentDao.testBeforeDelete(financingDocument);
    }

    @Override
    public int delete(FinancingDocument financingDocument) {
        return financingDocumentDao.delete(financingDocument);
    }

    @Override
    public String createWhere(Branch branch) {
        String where = "";
        if (branch.getId() != 0) {
            where = " AND (fdoc.branch_id = " + branch.getId() + " OR fdoc.transferbranch_id = " + branch.getId() + ")";
        }
        return where;
    }

}
