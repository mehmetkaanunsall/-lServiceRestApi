/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   28.10.2016 14:17:23
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.admin.Grid;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class UserDataGridColumn extends WotLogging {

    private int id;
    private UserData userData;
    private Grid grid;
    private String columnindexes;
    private String sortColumn;
    private String sortOrder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public String getColumnindexes() {
        return columnindexes;
    }

    public void setColumnindexes(String columnindexes) {
        this.columnindexes = columnindexes;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

}
