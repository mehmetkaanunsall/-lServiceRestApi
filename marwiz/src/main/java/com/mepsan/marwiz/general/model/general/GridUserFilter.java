/**
 * Bu sınıf gridUserFilter tablosu için yazıldı.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   02.09.2016 10:16
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.admin.Grid;
import javax.validation.constraints.Size;

public class GridUserFilter {

    public int id;
    @Size(max = 60)
    public String name;
    public String json;
    public UserData userdata;
    public Grid grid;

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public UserData getUserdata() {
        return userdata;
    }

    public void setUserdata(UserData userdata) {
        this.userdata = userdata;
    }
}
