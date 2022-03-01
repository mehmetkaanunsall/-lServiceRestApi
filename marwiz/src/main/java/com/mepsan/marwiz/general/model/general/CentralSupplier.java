/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 09:08:48
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class CentralSupplier extends WotLogging {

    private int id;
    private int centralSupplierId;
    private String name;
    private int tagQuantity;

    public CentralSupplier() {
    }

    public CentralSupplier(int id) {
        this.id = id;
    }

    public int getCentralSupplierId() {
        return centralSupplierId;
    }

    public void setCentralSupplierId(int centralSupplierId) {
        this.centralSupplierId = centralSupplierId;
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

    public int getTagQuantity() {
        return tagQuantity;
    }

    public void setTagQuantity(int tagQuantity) {
        this.tagQuantity = tagQuantity;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public String toString() {
        return this.getName();
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
        final CentralSupplier other = (CentralSupplier) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
