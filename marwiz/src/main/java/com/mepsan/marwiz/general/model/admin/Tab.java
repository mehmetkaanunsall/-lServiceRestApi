/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.admin;

import java.util.List;
import java.util.Map;

public class Tab {

    private int id;
    private String name;
    private Page page;
    private List<Button> listOfButtons;
    private Map<Integer, TabDictionary> nameMap;

    public Tab(int id) {
        this.id = id;
    }

    public Tab() {
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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Button> getListOfButtons() {
        return listOfButtons;
    }

    public void setListOfButtons(List<Button> listOfButtons) {
        this.listOfButtons = listOfButtons;
    }

    public Map<Integer, TabDictionary> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, TabDictionary> nameMap) {
        this.nameMap = nameMap;
    }

}
