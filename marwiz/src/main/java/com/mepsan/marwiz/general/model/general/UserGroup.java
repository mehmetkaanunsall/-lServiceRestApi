/**
 * This class ...
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   12.01.2018 10:09:39
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class UserGroup extends WotLogging {

    private int id;
    private String name;
    //private Authorize authorize;

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

    public UserGroup() {
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
