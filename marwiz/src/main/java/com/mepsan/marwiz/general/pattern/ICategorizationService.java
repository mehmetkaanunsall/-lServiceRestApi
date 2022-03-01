/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:16:31
 */
package com.mepsan.marwiz.general.pattern;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.List;

public interface ICategorizationService<T,M> {

    public List<Categorization> listCategorization(T obj, Item ci);

    public int allCreat(T obj, List<M> choseeCategorizations, Item ci);

    public int allUpdate(T obj, List<M> choseeCategorizations);

}
