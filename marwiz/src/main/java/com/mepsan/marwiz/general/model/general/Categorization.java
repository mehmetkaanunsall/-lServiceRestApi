/**
 *
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 10:06:39
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Objects;
import javax.validation.constraints.Size;

public class Categorization extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    private Categorization parentId;
    private Item item;
    private boolean checked;
    private int depth;
    private int branchId;
    private int tagQuantity;

    public Categorization() {
        this.item = new Item();
    }

    public Categorization(int id) {
        this.item = new Item();
        this.id = id;
    }

    public Categorization(int id, String name) {
        this.item = new Item();
        this.id = id;
        this.name = name;
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

    public Categorization getParentId() {
        return parentId;
    }

    public void setParentId(Categorization parentId) {
        this.parentId = parentId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getTagQuantity() {
        return tagQuantity;
    } 

    public void setTagQuantity(int tagQuantity) {
        this.tagQuantity = tagQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Categorization other = (Categorization) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
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
