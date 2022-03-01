/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.admin;

import com.mepsan.marwiz.general.model.wot.Dictionary;
import java.util.List;
import java.util.Map;

public class Page {

    private int id;
    private String url;
    private Page parent_id;
    private String name;
    private List<Page> subPages;
    private List<Tab> tabs;
    private List<Button> buttons;
    private Map<Integer, Dictionary<Page>> nameMap;

    public Page() {
    }

    public Page(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public Page(int id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public Page getParent_id() {
        return parent_id;
    }

    public List<Page> getSubPages() {
        return subPages;
    }

    public void setSubPages(List<Page> subPages) {
        this.subPages = subPages;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public void setParent_id(Page parent_id) {
        this.parent_id = parent_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<Integer, Dictionary<Page>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Page>> nameMap) {
        this.nameMap = nameMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
