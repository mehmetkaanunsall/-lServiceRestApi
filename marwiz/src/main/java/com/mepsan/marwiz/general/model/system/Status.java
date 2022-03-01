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

public class Status extends WotLogging{

    private int id;
    private String tag;
    private Item item;
    private Map<Integer, Dictionary<Status>> nameMap;

    public Status() {
    }

    public Status(int id) {
        this.id = id;
    }

    public Status(int id, String tag) {
        this.id = id;
        this.tag = tag;
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

    public Map<Integer, Dictionary<Status>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Status>> nameMap) {
        this.nameMap = nameMap;
    }

   

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
       return this.getTag();
    }

}
