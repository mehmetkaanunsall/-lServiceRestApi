/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   19.09.2016 08:52:34
 */
package com.mepsan.marwiz.general.model.admin;

import com.mepsan.marwiz.general.model.admin.Grid;

public class GridColumn {

    private int id;
    private String componentId;
    private Grid grid;
    private String columnName;
    private boolean isDetailFilter;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isIsDetailFilter() {
        return isDetailFilter;
    }

    public void setIsDetailFilter(boolean isDetailFilter) {
        this.isDetailFilter = isDetailFilter;
    }

   

}
