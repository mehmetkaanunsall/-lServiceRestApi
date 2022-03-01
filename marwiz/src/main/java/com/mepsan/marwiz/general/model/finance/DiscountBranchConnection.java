/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.04.2019 15:19:01
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class DiscountBranchConnection extends WotLogging {

    private int id;
    private Discount discount;
    private Branch branch;

    public DiscountBranchConnection() {
        this.discount = new Discount();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}
