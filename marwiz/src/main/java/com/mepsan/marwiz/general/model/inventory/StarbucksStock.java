package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;

/**
 *
 * @author elif.mart
 */
public class StarbucksStock extends WotLogging {

    private int id;
    private String name;
    private String code;
    private int centerStarbucksStock_id;

    public StarbucksStock() {

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCenterStarbucksStock_id() {
        return centerStarbucksStock_id;
    }

    public void setCenterStarbucksStock_id(int centerStarbucksStock_id) {
        this.centerStarbucksStock_id = centerStarbucksStock_id;
    }
    
    

}
