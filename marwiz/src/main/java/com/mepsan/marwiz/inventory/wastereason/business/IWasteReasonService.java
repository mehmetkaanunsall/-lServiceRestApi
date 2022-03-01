package com.mepsan.marwiz.inventory.wastereason.business;

import com.mepsan.marwiz.general.model.inventory.WasteReason;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IWasteReasonService extends ICrudService<WasteReason> {

    public List<WasteReason> findAll();

    public int delete(WasteReason wasteReason);

    public int testBeforeDelete(WasteReason wasteReason);

    public WasteReason findAccordingToName(WasteReason wasteReason);

    public int deleteForOtherBranch(WasteReason wasteReason);

    public List<WasteReason> selectWasteReason(int isCentral);

    public int updateAvailableWasteReason(int oldId, int newId);
}
