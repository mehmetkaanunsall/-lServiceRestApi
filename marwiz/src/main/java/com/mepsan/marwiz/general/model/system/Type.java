/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.system;

import com.mepsan.marwiz.general.model.wot.Dictionary;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Map;

public class Type extends WotLogging {

    private int id;
    private String tag;
    private Item item;
    private Map<Integer, Dictionary<Type>> nameMap;

    public Type() {
    }

    public Type(int id) {
        this.id = id;

    }

    public Type(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public Type(int id, String tag, Item item) {
        this.id = id;
        this.tag = tag;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Map<Integer, Dictionary<Type>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Type>> nameMap) {
        this.nameMap = nameMap;
    }

   

    @Override
    public String toString() {
        return tag;
    }

}
