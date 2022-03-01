/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 07.05.2018 11:08:01
 */
package com.mepsan.marwiz.service.item.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckItem;
import java.util.Date;
import java.util.List;

public interface ICheckItemDao {

    public BranchSetting findTopCentralIntegratedBranchSetting();
    
    public List<BranchSetting> findTopCentralIntegratedBranchSettings();

    public int insertCheckItem(CheckItem obj);

    public Date getMaxProcessDateByType(int type);
    
    public Integer getMaxCenterNotificaionId(int branch_id);
}
