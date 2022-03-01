/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   26.09.2016 08:55:27
 */
package com.mepsan.marwiz.general.pattern;

import java.util.List;
import java.util.Map;

public interface ILazyGridService<T> {

    public List<T> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where);

    public int count(String where);

}
