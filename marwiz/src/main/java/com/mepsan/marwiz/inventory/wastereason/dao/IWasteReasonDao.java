package com.mepsan.marwiz.inventory.wastereason.dao;

import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IWasteReasonDao extends ICrud<WasteReason> {

    public List<WasteReason> findAll();

    public int delete(WasteReason wasteReason);

    public int testBeforeDelete(WasteReason wasteReason);

    public WasteReason findAccordingToName(WasteReason wasteReason);

    public int deleteForOtherBranch(WasteReason wasteReason);

    public List<WasteReason> selectWasteReason(int isCentral);

    public int updateAvailableWasteReason(int oldId, int newId);

}
