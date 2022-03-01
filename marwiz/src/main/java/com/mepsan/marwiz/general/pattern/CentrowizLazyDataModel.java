/**
 * Bu Sınıf ... Global Lazy Data Model
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.07.2016 18:02:07
 */
package com.mepsan.marwiz.general.pattern;

import java.util.List;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public abstract class CentrowizLazyDataModel<T> extends LazyDataModel<T> {

    /**
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return
     */
    @Override
    public abstract List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters);

    /**
     * convert order to suitable with sql server
     *
     * @param order
     * @return
     */
    public String convertSortOrder(SortOrder order) {
        System.out.println("--------order---" + order);
        if (order == null) {
            return "ASC";
        } else {
            switch (order.toString()) {
                case "ASCENDING":
                    return "ASC";
                case "DESCENDING":
                    return "DESC";
                default:
                    return null;
            }
        }
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public Object getRowKey(T object) {

        return object != null ? object.hashCode() : null;
    }

    /**
     *
     * @param rowKey
     * @return
     */
    @Override
    public T getRowData(String rowKey) {
        List<T> list = (List<T>) getWrappedData();
        if (list != null) {
            for (T groupTable : list) {
                if (groupTable.hashCode() == Integer.valueOf(rowKey)) {
                    return groupTable;
                }
            }
        }
        return null;
    }
     
}
