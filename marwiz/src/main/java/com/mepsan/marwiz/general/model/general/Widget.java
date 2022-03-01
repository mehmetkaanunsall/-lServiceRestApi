/**
 * Bu sınıf Widget tablosu için yazılmıştır.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   22.08.2016 09:53
 *
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Type;

public class Widget {

    private int id;
    private String name;

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

    @Override
    public String toString() {
        return "Widget{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object obj) {
        Widget widget = (Widget) obj;
        return (widget.getId() == id);
    }

}
