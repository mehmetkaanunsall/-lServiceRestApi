/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.02.2019 08:59:13
 */
package com.mepsan.marwiz.general.model.log;

import com.mepsan.marwiz.general.model.general.BranchSetting;

public class SendPriceChangeRequestCheck {
    private String priceChangeRequestIds;
    private BranchSetting branchSetting;

    public SendPriceChangeRequestCheck() {
        branchSetting = new BranchSetting();
    }

    public String getPriceChangeRequestIds() {
        return priceChangeRequestIds;
    }

    public void setPriceChangeRequestIds(String priceChangeRequestIds) {
        this.priceChangeRequestIds = priceChangeRequestIds;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

}
