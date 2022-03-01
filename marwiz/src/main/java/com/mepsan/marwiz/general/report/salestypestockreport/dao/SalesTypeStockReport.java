/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   21.02.2018 03:09:44
 */
package com.mepsan.marwiz.general.report.salestypestockreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesTypeStockReport extends SaleItem {

    private Date beginDate;
    private Date endDate;
    private Type type;
    private BigDecimal totalQuantity;
    private List<BranchSetting> selectedBranchList;
    private Branch branch;

    public SalesTypeStockReport() {
        this.type = new Type();
        this.selectedBranchList = new ArrayList<>();
        this.branch = new Branch();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return this.getStock().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
