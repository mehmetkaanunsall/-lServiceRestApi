/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:17:26
 */
package com.mepsan.marwiz.general.pattern;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.List;

public interface ICategorization<T> {

    public List<Categorization> listCategorization(T obj, Item ci);

    public int allCreat(T obj, String choseeCategorizations, Item ci);

    public int allUpdate(T obj, String choseeCategorizations);
}
