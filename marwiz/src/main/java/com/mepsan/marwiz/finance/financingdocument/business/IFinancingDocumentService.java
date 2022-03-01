/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 04:30:19
 */
package com.mepsan.marwiz.finance.financingdocument.business;

import com.mepsan.marwiz.finance.financingdocument.dao.FinancingDocumentVoucher;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

public interface IFinancingDocumentService extends ILazyGridService {

    public int create(FinancingDocument obj, int inmovementId, int outmovementId);

    public int update(FinancingDocument obj, int inmovementId, int outmovementId);

    public FinancingDocument findFinancingDocument(FinancingDocument fd);

    public List<FinancingDocumentVoucher> listOfVancourDetail(FinancingDocument obj);

    public List<CheckDelete> testBeforeDelete(FinancingDocument financingDocument);

    public int delete(FinancingDocument financingDocument);

    public String createWhere(Branch branch);

}
