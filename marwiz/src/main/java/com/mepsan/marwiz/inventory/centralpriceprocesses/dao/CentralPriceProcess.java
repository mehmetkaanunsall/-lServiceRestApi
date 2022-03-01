/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.06.2020 05:22:05
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import java.util.Objects;

public class CentralPriceProcess {

    private int id;
    private PriceListItem priceListItem;
    private Branch branch;

    public CentralPriceProcess() {
        this.priceListItem = new PriceListItem();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PriceListItem getPriceListItem() {
        return priceListItem;
    }

    public void setPriceListItem(PriceListItem priceListItem) {
        this.priceListItem = priceListItem;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return this.getPriceListItem().getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
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
        final CentralPriceProcess other = (CentralPriceProcess) obj;
        if (!Objects.equals(this.getPriceListItem().getStock().getId(), other.getPriceListItem().getStock().getId())) {
            return false;
        }
        return true;
    }

}
