/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.07.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.admin;

public class Button {

    private int id;
    private String name;
    private Page page;
    private Tab tab;

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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

}
