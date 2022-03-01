/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 27.04.2018 11:52:50
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;

public class SendStockRequestCheck {
    private String stockRequestIds;
    private BranchSetting branchSetting;

    public SendStockRequestCheck() {
        branchSetting = new BranchSetting();
    }

    public String getStockRequestIds() {
        return stockRequestIds;
    }

    public void setStockRequestIds(String stockRequestIds) {
        this.stockRequestIds = stockRequestIds;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

}
