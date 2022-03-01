/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 11:43:21
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class DocumentNumber extends WotLogging {

    private int id;
    private Item item;
    private String name;
    private String serial;
    private int beginNumber;
    private int endNumber;
    private int actualNumber;

    public DocumentNumber() {
        this.item = new Item();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getBeginNumber() {
        return beginNumber;
    }

    public void setBeginNumber(int beginNumber) {
        this.beginNumber = beginNumber;
    }

    public int getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(int endNumber) {
        this.endNumber = endNumber;
    }

    public int getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
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
