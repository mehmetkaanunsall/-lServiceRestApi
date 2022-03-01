/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 02:23:17
 */
package com.mepsan.marwiz.general.report.dailysalesreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Sales;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailySalesReport extends Sales {

    private Date beginDate;
    private Date endDate;
    private String stringResult;
    List<SubPivot> subList;
    private String overallTotalDiscount;
    private String overallTotalGiro;
    private BranchSetting branchSetting;

    public DailySalesReport() {
        this.subList = new ArrayList<>();
        this.branchSetting = new BranchSetting();
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

    public String getStringResult() {
        return stringResult;
    }

    public void setStringResult(String stringResult) {
        this.stringResult = stringResult;
    }

    public List<SubPivot> getSubList() {
        return subList;
    }

    public void setSubList(List<SubPivot> subList) {
        this.subList = subList;
    }

    public String getOverallTotalDiscount() {
        return overallTotalDiscount;
    }

    public void setOverallTotalDiscount(String overallTotalDiscount) {
        this.overallTotalDiscount = overallTotalDiscount;
    }

    public String getOverallTotalGiro() {
        return overallTotalGiro;
    }

    public void setOverallTotalGiro(String overallTotalGiro) {
        this.overallTotalGiro = overallTotalGiro;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

   

}
