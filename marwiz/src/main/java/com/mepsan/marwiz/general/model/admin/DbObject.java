/**
 * @author Mehmet ERGÜLCÜ
 * @date 01.03.2017 05:09:41
 */
package com.mepsan.marwiz.general.model.admin;

public class DbObject {
    
    private int id;
    private String name;
    private String description;
    private Integer type;
    private String tag;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
}
