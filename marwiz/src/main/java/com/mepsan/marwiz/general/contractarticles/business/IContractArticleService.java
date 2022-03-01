/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:28:46 PM
 */
package com.mepsan.marwiz.general.contractarticles.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IContractArticleService extends ICrudService<ContractArticles> {

    public List<ContractArticles> findAll();

    public int testBeforeDelete(ContractArticles obj);

    public int delete(ContractArticles obj);

    public int stockControl(ContractArticles obj);

    public ContractArticles findStockArticles(int stockId, Branch branch);

}
