/**
 * Bu sınıf WidgetUserDataCon tablosu için yazılmıştır.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   22.08.2016 11:03
 * 
 */
package com.mepsan.marwiz.general.model.general;

public class WidgetUserDataCon {
    private int id;
    private Widget widget;
    private UserData userData;
    private int col;
    private int row;
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
    
    
}
