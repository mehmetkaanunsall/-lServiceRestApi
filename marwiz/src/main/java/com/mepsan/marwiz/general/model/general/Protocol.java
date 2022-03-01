/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.01.2020 08:57:31
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class Protocol extends WotLogging {

    private int id;
    private String name;
    private Item item;
    private int protocolNo;

    public Protocol() {
        this.item = new Item();
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

    public int getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(int protocolNo) {
        this.protocolNo = protocolNo;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
