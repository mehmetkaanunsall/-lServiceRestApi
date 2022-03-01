/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   20.02.2018 11:36:58
 */
package com.mepsan.marwiz.general.report.salesreturnreport.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.SaleItem;
import java.util.Date;

public class ReceiptReturnReport extends SaleItem {

    private Date beginDate;
    private Date endDate;
    private Branch branch;

    public ReceiptReturnReport() {
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
