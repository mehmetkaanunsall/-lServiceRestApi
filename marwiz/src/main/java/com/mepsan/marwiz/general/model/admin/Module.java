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

public class Module {

    private int id;
    private String icon;
    private String name;
    private String code;
    private List<Folder> folders;
    private Map<Integer, Dictionary<Module>> nameMap;
    private int order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public Map<Integer, Dictionary<Module>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Module>> nameMap) {
        this.nameMap = nameMap;
    }

   

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

}
