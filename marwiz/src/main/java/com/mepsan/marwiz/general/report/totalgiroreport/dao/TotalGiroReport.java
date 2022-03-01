/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.02.2018 03:02:32
 */
package com.mepsan.marwiz.general.report.totalgiroreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.SalePayment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TotalGiroReport extends SalePayment {

    private Date beginDate;
    private Date endDate;
    private Branch branch;
    private List<BranchSetting> selectedBranchList;
    
    public TotalGiroReport(){
        this.branch=new Branch();
        this.selectedBranchList=new ArrayList<>();
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }
    

    @Override
    public String toString() {
        return this.getSales().getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
