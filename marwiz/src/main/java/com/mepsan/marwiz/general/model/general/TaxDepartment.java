/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:53:35
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class TaxDepartment extends WotLogging {

    private int id;
    private int departmentNo;
    private String name;
    private TaxGroup taxGroup;

    public TaxDepartment() {
        this.taxGroup = new TaxGroup();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDepartmentNo() {
        return departmentNo;
    }

    public void setDepartmentNo(int departmentNo) {
        this.departmentNo = departmentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaxGroup getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
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
