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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Folder {

    private int id;
    private int type;
    private String name;
    private Module moduleId;
    private List<Page> pages;
    private int order;
    // private Map<Integer, Page> mapPage;
    private Map<Integer, Dictionary<Folder>> nameMap;

    public Folder() {

        pages=new ArrayList<>();
    }

    public Folder(String tag, int id) {
        this.name = tag;
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Module getModuleId() {
        return moduleId;
    }

    public void setModuleId(Module moduleId) {
        this.moduleId = moduleId;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Map<Integer, Dictionary<Folder>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Folder>> nameMap) {
        this.nameMap = nameMap;
    }



}
