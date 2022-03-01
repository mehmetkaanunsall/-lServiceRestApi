package com.mepsan.marwiz.inventory.starbucksstock.dao;

import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IStarbucksStockDao extends ICrud<StarbucksStock> {

    public List<StarbucksStock> findAll();

    public int delete(StarbucksStock obj);

    public StarbucksStock findAccordingToCode(StarbucksStock stock);

    public int deleteForOtherBranch(StarbucksStock stock);

    public int updateAvailableStarbucksStock(int oldId, int newId);

}
