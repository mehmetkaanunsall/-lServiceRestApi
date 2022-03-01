/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Mar 6, 2018 3:43:56 PM
 */  
package com.mepsan.marwiz.general.model.wot;

public class DataTableColumn {

    private String id;
    private int width;
    private boolean visibility;
    private int index;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DataTableColumn(String id) {
        this.id = id;
        this.visibility=true;
    }

}
