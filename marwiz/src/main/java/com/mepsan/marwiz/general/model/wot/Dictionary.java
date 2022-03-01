/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Feb 1, 2018 9:00:37 AM
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.system.Language;

public class Dictionary<T> {

    private int id;
    private String name;
    private T object;
    private Language language;
    private String description;

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

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
