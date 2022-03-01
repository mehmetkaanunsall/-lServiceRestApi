/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   19.10.2016 10:37:21
 */
package com.mepsan.marwiz.general.pattern;

import com.mepsan.marwiz.general.model.admin.Grid;
import com.mepsan.marwiz.general.model.admin.GridColumn;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public abstract class GeneralFilterService<T> {

    public LazyDataModel<T> searchResult;
    public List<T> searchDataList;

    public LazyDataModel<T> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(LazyDataModel<T> searchResult) {
        this.searchResult = searchResult;
    }

    public List<T> getSearchDataList() {
        return searchDataList;
    }

    public void setSearchDataList(List<T> searchDataList) {
        this.searchDataList = searchDataList;
    }

    public abstract String createWhere(String value); 
    
    public abstract String createWhereForBook(String value); 

    public void makeSearch(String value) {
        searchResult = new CentrowizLazyDataModel<T>() {
            @Override
            public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<T> result;
                String where = createWhere(value);
                int count = callDaoCount(where);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }
    
    public abstract void makeSearchForbook(String value, String type, List<Object> param);

    public abstract List<T> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where);

    public abstract int callDaoCount(String where);

}
