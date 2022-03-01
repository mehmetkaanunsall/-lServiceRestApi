/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:27:25 PM
 */
package com.mepsan.marwiz.general.contractarticles.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IContractArticlesDao extends ICrud<ContractArticles> {

    public List<ContractArticles> findAll();

    public int testBeforeDelete(ContractArticles obj);

    public int delete(ContractArticles obj);

    public int stockControl(ContractArticles obj);
    
    public ContractArticles findStockArticles(int stockId, Branch branch);
}
