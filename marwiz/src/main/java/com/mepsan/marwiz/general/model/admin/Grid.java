/**
 * Bu sınıf grid tablosu için yazıldı.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   02.09.2016 10:16
 */
package com.mepsan.marwiz.general.model.admin;

import java.util.List;

public class Grid {

    private int id, pageId;
    private String componentId;
    private List<GridColumn> gridColumns;

    public Grid(int id) {
        this.id = id;
    }

    public Grid() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public List<GridColumn> getGridColumns() {
        return gridColumns;
    }

    public void setGridColumns(List<GridColumn> gridColumns) {
        this.gridColumns = gridColumns;
    }

}
