/**
 * Bu Sınıf ... 
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.system;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class Item extends WotLogging{
    private int id;
    private String tag;

    public Item(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public Item(){
    }
    
    public Item(int id) {
        this.id=id;
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
    
    
}
