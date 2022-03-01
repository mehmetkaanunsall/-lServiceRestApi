/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   18.01.2018 05:07:06
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockCategorizationConnection;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.inventory.stock.dao.IStockCategorizationDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class StockCategorizationService implements IStockCategorizationService {

    @Autowired
    private IStockCategorizationDao stockCategorizationDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockCategorizationDao(IStockCategorizationDao stockCategorizationDao) {
        this.stockCategorizationDao = stockCategorizationDao;
    }


    @Override
    public List<Categorization> listCategorization(Stock obj, Item ci) {
        return stockCategorizationDao.listCategorization(obj, ci);
    }

    @Override
    public int create(StockCategorizationConnection obj) {
        return stockCategorizationDao.create(obj);
    }

    @Override
    public int update(StockCategorizationConnection obj) {
        return stockCategorizationDao.update(obj);
    }

    /**
     * secili olan kategori listesini stringe cevirip dao daki metodu tetikler
     *
     * @param obj stock objesi
     * @param choseeCategorizations kategori con listesi
     */
    @Override
    public int allCreat(Stock obj, List<StockCategorizationConnection> choseeCategorizations, Item ci) {
        String choose = "";
        if (choseeCategorizations.size() > 0) {
            choose = String.valueOf(choseeCategorizations.get(0).getCategorization().getId());

            for (int i = 1; i < choseeCategorizations.size(); i++) {
                choose = choose + "," + choseeCategorizations.get(i).getCategorization().getId();
            }
            return stockCategorizationDao.allCreat(obj, choose, ci);
        } else {
            return stockCategorizationDao.allCreat(obj, "0", ci);
        }
    }

    /**
     * secili olan kategori listesini stringe cevirip dao daki metodu tetikler
     *
     * @param obj stock objesi
     * @param choseeCategorizations kategori con listesi
     */
    @Override
    public int allUpdate(Stock obj, List<StockCategorizationConnection> choseeCategorizations) {
        String choose = "";
        if (choseeCategorizations.size() > 0) {
            choose = String.valueOf(choseeCategorizations.get(0).getCategorization().getId());

            for (int i = 1; i < choseeCategorizations.size(); i++) {
                choose = choose + "," + choseeCategorizations.get(i).getCategorization().getId();
            }
            return stockCategorizationDao.allUpdate(obj, choose);
        }
        return 0;
    }

    @Override
    public List<Categorization> listOfCategorization() {
        return stockCategorizationDao.listOfCategorization();
    }

    }
