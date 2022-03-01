/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:29:47 PM
 */
package com.mepsan.marwiz.general.contractarticles.business;

import com.mepsan.marwiz.general.contractarticles.dao.IContractArticlesDao;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractArticleService implements IContractArticleService {

    @Autowired
    IContractArticlesDao contractArticlesDao;

    public void setContractArticlesDao(IContractArticlesDao contractArticlesDao) {
        this.contractArticlesDao = contractArticlesDao;
    }

    @Override
    public List<ContractArticles> findAll() {
        return contractArticlesDao.findAll();
    }

    @Override
    public int testBeforeDelete(ContractArticles obj) {
        return contractArticlesDao.testBeforeDelete(obj);
    }

    @Override
    public int delete(ContractArticles obj) {
        return contractArticlesDao.delete(obj);
    }

    @Override
    public int create(ContractArticles obj) {
        return contractArticlesDao.create(obj);
    }

    @Override
    public int update(ContractArticles obj) {
        return contractArticlesDao.update(obj);
    }

    @Override
    public int stockControl(ContractArticles obj) {
        return contractArticlesDao.stockControl(obj);
    }

    @Override
    public ContractArticles findStockArticles(int stockId, Branch branch) {
        return contractArticlesDao.findStockArticles(stockId, branch);
    }

}
