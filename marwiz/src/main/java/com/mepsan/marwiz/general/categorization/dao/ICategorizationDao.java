/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 10:05:02
 */
package com.mepsan.marwiz.general.categorization.dao;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ICategorizationDao extends ICrud<Categorization> {

    public List<Categorization> listCategorization(Categorization obj);

    public int testBeforeDelete(Categorization categorization);

    public int delete(Categorization categorization);
    
    public int addToItem(int itemId,Categorization categorization,String categorizations,String items);
    
    public String importItemList(String json);

}
