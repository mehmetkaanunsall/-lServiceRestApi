package com.mepsan.marwiz.inventory.starbucksstock.business;

import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IStarbucksStockService extends ICrudService<StarbucksStock> {

    public List<StarbucksStock> findAll();

    public int delete(StarbucksStock obj);
    
    public StarbucksStock findAccordingToCode(StarbucksStock stock);
    
    public int deleteForOtherBranch(StarbucksStock stock);
    
    public int updateAvailableStarbucksStock(int oldId, int newId);

}
