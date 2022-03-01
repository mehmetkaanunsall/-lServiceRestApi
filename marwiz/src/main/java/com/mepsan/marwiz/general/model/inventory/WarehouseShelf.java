/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.01.2018 11:08:24
 */

package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.wot.WotLogging;


public class WarehouseShelf extends WotLogging{

    private int id;
    private String name;
    private String code;
    private Warehouse wareHouse;

    public WarehouseShelf() {
        
        this.wareHouse=new Warehouse();
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

    public Warehouse getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(Warehouse wareHouse) {
        this.wareHouse = wareHouse;
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
