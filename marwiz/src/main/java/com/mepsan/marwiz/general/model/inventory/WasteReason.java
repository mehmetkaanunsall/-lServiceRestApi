package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.WotLogging;

/**
 *
 * @author elif.mart
 */
public class WasteReason extends WotLogging {

    private int id;
    private String name;
    private int centerwastereason_id;

    public WasteReason() {

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

    public int getCenterwastereason_id() {
        return centerwastereason_id;
    }

    public void setCenterwastereason_id(int centerwastereason_id) {
        this.centerwastereason_id = centerwastereason_id;
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
