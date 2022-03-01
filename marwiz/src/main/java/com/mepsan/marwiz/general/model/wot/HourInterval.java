/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 1:27:16 PM
 */
package com.mepsan.marwiz.general.model.wot;

public class HourInterval {

    private String id;
    private String name;

    public HourInterval() {

    }

    public HourInterval(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }  @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.getId());
    }

}
